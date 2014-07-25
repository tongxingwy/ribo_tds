/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.tds.Dumpable;
/*     */ import com.sybase.jdbc3.tds.TdsInputStream;
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.EventObject;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class CaptureInputStream extends DumpInputStream
/*     */ {
/*     */   private Vector _listeners;
/*     */   private Vector _buffers;
/*     */   private InputStreamInfo _currentInput;
/*     */   private InputStreamInfo _fileInput;
/*     */   private InputStreamInfo _bufferInput;
/*     */   private int _currentSource;
/* 100 */   private boolean _readingRecordHeader = false;
/*     */ 
/* 103 */   private boolean _headerHasBeenRead = false;
/*     */ 
/* 110 */   private boolean _partialRecordInFirstBuffer = false;
/*     */   private long _offset;
/*     */ 
/*     */   public CaptureInputStream(InputStream is)
/*     */     throws IOException
/*     */   {
/* 124 */     super(is);
/*     */ 
/* 126 */     this._listeners = new Vector();
/* 127 */     this._buffers = new Vector();
/* 128 */     this._fileInput = new InputStreamInfo(is);
/* 129 */     this._bufferInput = new InputStreamInfo(null);
/* 130 */     this._currentInput = this._fileInput;
/* 131 */     this._currentSource = -1;
/* 132 */     this._offset = -1L;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */   {
/* 144 */     return this._currentInput._length - this._currentInput._bytesRead;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/* 153 */     if (!this._readingRecordHeader)
/*     */     {
/* 155 */       if (available() < 1)
/*     */       {
/* 158 */         readNextRecord();
/*     */       }
/* 160 */       this._currentInput._bytesRead += 1;
/*     */     }
/* 162 */     int answer = super.read();
/* 163 */     this._offset += 1L;
/* 164 */     return answer;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/* 174 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 187 */     int answer = len;
/* 188 */     if (this._readingRecordHeader)
/*     */     {
/* 193 */       answer = super.read(b, off, len);
/* 194 */       this._offset += len;
/*     */     }
/*     */     else
/*     */     {
/* 200 */       int avail = available();
/* 201 */       if (len > avail)
/*     */       {
/* 205 */         if (avail > 0)
/*     */         {
/* 207 */           int count = super.read(b, off, avail);
/* 208 */           if (count > 0)
/*     */           {
/* 210 */             this._currentInput._bytesRead += count;
/* 211 */             this._offset += count;
/*     */           }
/*     */           else
/*     */           {
/* 215 */             throw new EOFException("Partial read failed");
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 220 */         readNextRecord();
/*     */ 
/* 223 */         int toRead = len - avail;
/* 224 */         if (toRead > 0)
/*     */         {
/* 226 */           int firstRead = 0;
/* 227 */           while (toRead > this._currentInput._length)
/*     */           {
/* 229 */             firstRead += super.read(b, off + avail, this._currentInput._length);
/* 230 */             if (firstRead < 0)
/*     */             {
/* 232 */               throw new EOFException();
/*     */             }
/* 234 */             toRead -= this._currentInput._length;
/*     */ 
/* 236 */             this._currentInput._bytesRead += firstRead;
/* 237 */             this._offset += firstRead;
/*     */ 
/* 239 */             readNextRecord();
/*     */           }
/* 241 */           int count = super.read(b, off + avail + firstRead, toRead);
/*     */ 
/* 243 */           if (count < 0)
/*     */           {
/* 245 */             throw new EOFException();
/*     */           }
/* 247 */           this._currentInput._bytesRead += count;
/* 248 */           this._offset += count;
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 254 */         int count = super.read(b, off, len);
/* 255 */         this._currentInput._bytesRead += count;
/* 256 */         this._offset += count;
/*     */       }
/*     */     }
/* 259 */     return answer;
/*     */   }
/*     */ 
/*     */   public int switchSource()
/*     */     throws IOException
/*     */   {
/* 279 */     if (available() <= 0)
/*     */     {
/* 284 */       for (int tries = 1; ; ++tries)
/*     */       {
/* 287 */         this._currentSource = ((this._currentSource == 1) ? 2 : 1);
/*     */ 
/* 290 */         this._currentInput = ((this._currentInput == this._fileInput) ? this._bufferInput : this._fileInput);
/*     */ 
/* 293 */         this.in = this._currentInput._stream;
/*     */         try
/*     */         {
/* 299 */           readNextRecord();
/*     */         }
/*     */         catch (EOFException e)
/*     */         {
/* 306 */           if (tries < 2)
/*     */             break label84;
/* 308 */           label84: this._currentSource = -1;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 314 */     return this._currentSource;
/*     */   }
/*     */ 
/*     */   public int getSource()
/*     */     throws IOException
/*     */   {
/* 322 */     if (this._currentSource == -1)
/*     */     {
/* 324 */       readNextRecord();
/*     */     }
/* 326 */     return this._currentSource;
/*     */   }
/*     */ 
/*     */   private void readFileHeader()
/*     */     throws IOException
/*     */   {
/* 340 */     this._readingRecordHeader = true;
/* 341 */     TDSHeader header = new TDSHeader(this);
/* 342 */     this._readingRecordHeader = false;
/*     */ 
/* 344 */     postNewHeaderEvent(new EventObject(header));
/*     */   }
/*     */ 
/*     */   private void readNextRecord()
/*     */     throws IOException
/*     */   {
/* 354 */     if (!this._headerHasBeenRead)
/*     */     {
/* 356 */       readFileHeader();
/* 357 */       this._headerHasBeenRead = true;
/*     */     }
/*     */ 
/* 360 */     if (this._currentInput == this._fileInput)
/*     */     {
/* 362 */       readRecordFromFile();
/*     */     }
/*     */     else
/*     */     {
/* 366 */       readRecordFromBuffer();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readRecordFromFile()
/*     */     throws IOException
/*     */   {
/* 378 */     this._readingRecordHeader = true;
/*     */ 
/* 380 */     if (this._currentInput != this._fileInput)
/*     */     {
/* 382 */       throw new Error("Supposed to be reading from a file!");
/*     */     }
/*     */ 
/* 385 */     if (this._currentInput._bytesRead < this._currentInput._length)
/*     */     {
/* 387 */       throw new Error("Reading a new record from file before the old one is finished.");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 393 */       int source = readInt();
/* 394 */       if ((source != 1) && (source != 2))
/*     */       {
/* 397 */         throw new IOException("Bad record header");
/*     */       }
/* 399 */       int length = readInt();
/*     */ 
/* 402 */       while ((source != this._currentSource) && (this._currentSource != -1))
/*     */       {
/* 405 */         byte[] buffer = new byte[length];
/* 406 */         read(buffer, 0, length);
/* 407 */         this._buffers.addElement(buffer);
/*     */ 
/* 410 */         source = readInt();
/* 411 */         if ((source != 1) && (source != 2))
/*     */         {
/* 414 */           throw new IOException("Bad record header");
/*     */         }
/* 416 */         length = readInt();
/*     */       }
/* 418 */       this._currentInput._length = length;
/* 419 */       this._currentInput._bytesRead = 0;
/* 420 */       this._currentSource = source;
/*     */     }
/*     */     catch (EOFException e)
/*     */     {
/* 424 */       this._readingRecordHeader = false;
/* 425 */       throw e;
/*     */     }
/*     */ 
/* 428 */     this._readingRecordHeader = false;
/*     */ 
/* 430 */     RecordHeader header = new RecordHeader(this._currentSource, this._currentInput._length);
/*     */ 
/* 432 */     postNewHeaderEvent(new EventObject(header));
/*     */   }
/*     */ 
/*     */   private void readRecordFromBuffer()
/*     */     throws IOException
/*     */   {
/* 446 */     if (this._currentInput != this._bufferInput)
/*     */     {
/* 448 */       throw new Error("Supposed to be reading from a buffer!");
/*     */     }
/*     */ 
/* 451 */     if (this._currentInput._bytesRead < this._currentInput._length)
/*     */     {
/* 453 */       throw new Error("Reading a new record from buffer before the old one is finished.");
/*     */     }
/*     */ 
/* 456 */     if (this._buffers.isEmpty())
/*     */     {
/* 459 */       this._currentInput = this._fileInput;
/* 460 */       this.in = this._currentInput._stream;
/*     */ 
/* 464 */       int remains = this._currentInput._length - this._currentInput._bytesRead;
/* 465 */       if (remains > 0)
/*     */       {
/* 467 */         byte[] buffer = new byte[remains];
/* 468 */         int count = read(buffer, 0, remains);
/* 469 */         if (count < remains)
/*     */         {
/* 471 */           throw new EOFException();
/*     */         }
/* 473 */         this._buffers.addElement(buffer);
/* 474 */         this._partialRecordInFirstBuffer = true;
/*     */ 
/* 478 */         this._currentInput._bytesRead = this._currentInput._length;
/*     */       }
/*     */ 
/* 482 */       readRecordFromFile();
/*     */     }
/*     */     else
/*     */     {
/* 486 */       byte[] buffer = (byte[])this._buffers.elementAt(0);
/* 487 */       this._buffers.removeElementAt(0);
/* 488 */       this._currentInput._stream = new ByteArrayInputStream(buffer);
/* 489 */       this._currentInput._length = buffer.length;
/* 490 */       this._currentInput._bytesRead = 0;
/*     */ 
/* 492 */       this.in = this._currentInput._stream;
/*     */ 
/* 496 */       if (!this._partialRecordInFirstBuffer)
/*     */       {
/* 498 */         RecordHeader header = new RecordHeader(this._currentSource, this._currentInput._length);
/*     */ 
/* 500 */         postNewHeaderEvent(new EventObject(header));
/*     */       }
/* 502 */       this._partialRecordInFirstBuffer = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addHeaderListener(HeaderListener listener)
/*     */   {
/* 517 */     this._listeners.addElement(listener);
/*     */   }
/*     */ 
/*     */   public void removeHeaderListener(HeaderListener listener)
/*     */   {
/* 527 */     this._listeners.removeElement(listener);
/*     */   }
/*     */ 
/*     */   public void postNewHeaderEvent(EventObject event)
/*     */   {
/* 536 */     Enumeration sybenum = this._listeners.elements();
/* 537 */     while (sybenum.hasMoreElements())
/*     */     {
/* 539 */       HeaderListener listener = (HeaderListener)sybenum.nextElement();
/* 540 */       listener.newHeader(event);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class InputStreamInfo
/*     */   {
/*     */     protected int _length;
/*     */     protected int _bytesRead;
/*     */     protected InputStream _stream;
/*     */ 
/*     */     protected InputStreamInfo(InputStream is)
/*     */     {
/* 706 */       this._stream = is;
/* 707 */       this._bytesRead = 0;
/* 708 */       this._length = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class RecordHeader
/*     */     implements Dumpable
/*     */   {
/*     */     private int _source;
/*     */     private int _length;
/*     */ 
/*     */     protected RecordHeader(int source, int length)
/*     */     {
/* 642 */       this._source = source;
/* 643 */       this._length = length;
/*     */     }
/*     */ 
/*     */     public DumpInfo dump(DumpFilter aFilter)
/*     */       throws IOException
/*     */     {
/* 653 */       DumpInfo answer = null;
/* 654 */       if (aFilter.includesToken(257))
/*     */       {
/* 656 */         answer = aFilter.getDumpInfo();
/* 657 */         String[] names = { "<unrecognized>", "REQUEST", "RESPONSE" };
/*     */ 
/* 662 */         answer.addInfo("Token", 0, "Capture Record Header");
/* 663 */         answer.addField("Source", 4, this._source, names);
/* 664 */         if (aFilter.includesDetail(1))
/*     */         {
/* 666 */           answer.addInt("Length", 4, this._length);
/*     */         }
/*     */       }
/* 669 */       return answer;
/*     */     }
/*     */ 
/*     */     public int getTokenType()
/*     */     {
/* 677 */       return 257;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TDSHeader
/*     */     implements Dumpable
/*     */   {
/*     */     private int _version;
/*     */     private int _time;
/*     */     private String _programName;
/*     */     private int _inputPortNum;
/*     */     private int _outputPortNum;
/*     */     private String _clientName;
/*     */     private String _serverName;
/*     */ 
/*     */     public TDSHeader(TdsInputStream cis)
/*     */       throws IOException
/*     */     {
/* 565 */       this._version = cis.readInt();
/*     */ 
/* 567 */       if (this._version != 1)
/*     */       {
/* 569 */         throw new IOException("The input file has an unrecognized version, or it isn't a capture file at all.");
/*     */       }
/*     */ 
/* 576 */       this._time = cis.readInt();
/* 577 */       this._programName = readCString(cis, 256);
/* 578 */       this._inputPortNum = cis.readInt();
/* 579 */       this._outputPortNum = cis.readInt();
/* 580 */       this._clientName = readCString(cis, 256);
/* 581 */       this._serverName = readCString(cis, 256);
/*     */     }
/*     */ 
/*     */     public DumpInfo dump(DumpFilter aFilter)
/*     */       throws IOException
/*     */     {
/* 591 */       DumpInfo answer = null;
/* 592 */       if (aFilter.includesToken(257))
/*     */       {
/* 594 */         answer = aFilter.getDumpInfo();
/* 595 */         answer.addInfo("Token", 0, "Capture File Header");
/* 596 */         answer.addInt("Version", 4, this._version);
/* 597 */         answer.addHex("Time", 4, this._time);
/* 598 */         answer.addText("Program Name", 256, this._programName);
/* 599 */         answer.addInt("Input Port No.", 4, this._inputPortNum);
/* 600 */         answer.addInt("Output Port No.", 4, this._outputPortNum);
/* 601 */         answer.addText("Client Name", 256, this._clientName);
/* 602 */         answer.addText("Server Name", 256, this._serverName);
/*     */       }
/* 604 */       return answer;
/*     */     }
/*     */ 
/*     */     public int getTokenType()
/*     */     {
/* 612 */       return 257;
/*     */     }
/*     */ 
/*     */     private String readCString(TdsInputStream cis, int maxLen)
/*     */       throws IOException
/*     */     {
/* 624 */       String answer = cis.readString(maxLen);
/* 625 */       int end = answer.indexOf(0);
/* 626 */       return answer.substring(0, end);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.CaptureInputStream
 * JD-Core Version:    0.5.4
 */