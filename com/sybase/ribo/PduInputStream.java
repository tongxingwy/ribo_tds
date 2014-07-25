/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.tds.SrvAttentionException;
/*     */ import com.sybase.jdbc3.tds.SrvPdu;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.EventObject;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class PduInputStream extends DumpInputStream
/*     */ {
/*     */   private SrvPdu _pdu;
/*     */   private int _bytesRead;
/*  45 */   private boolean _readingPDUHeader = false;
/*     */   private Vector _listeners;
/*     */ 
/*     */   public PduInputStream(InputStream inputStream)
/*     */     throws IOException
/*     */   {
/*  60 */     super(inputStream);
/*  61 */     this._listeners = new Vector();
/*  62 */     this._bytesRead = 0;
/*  63 */     this._pdu = null;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */   {
/*  75 */     int answer = 0;
/*  76 */     if (this._pdu != null)
/*     */     {
/*  78 */       answer = this._pdu.dataLength() - this._bytesRead;
/*     */     }
/*  80 */     return answer;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  88 */     if (!this._readingPDUHeader)
/*     */     {
/*  90 */       if (available() < 1)
/*     */       {
/*  93 */         readNextPDU();
/*     */       }
/*  95 */       this._bytesRead += 1;
/*     */     }
/*  97 */     return super.read();
/*     */   }
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/* 105 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 118 */     int answer = len;
/* 119 */     if (this._readingPDUHeader)
/*     */     {
/* 124 */       answer = super.read(b, off, len);
/*     */     }
/*     */     else
/*     */     {
/* 129 */       int avail = available();
/* 130 */       while (len > avail)
/*     */       {
/* 134 */         if (avail > 0)
/*     */         {
/* 137 */           int count = super.read(b, off, avail);
/* 138 */           if (count < 0)
/*     */           {
/* 140 */             throw new EOFException("Partial read from PDU failed");
/*     */           }
/* 142 */           this._bytesRead += count;
/* 143 */           len -= count;
/* 144 */           off += count;
/*     */         }
/*     */ 
/* 148 */         readNextPDU();
/* 149 */         avail = available();
/*     */       }
/*     */ 
/* 153 */       if (len > 0)
/*     */       {
/* 155 */         int count = super.read(b, off, len);
/* 156 */         if (count < 0)
/*     */         {
/* 158 */           throw new EOFException();
/*     */         }
/* 160 */         this._bytesRead += count;
/*     */       }
/*     */     }
/* 163 */     return answer;
/*     */   }
/*     */ 
/*     */   public boolean endOfLogicalPDU()
/*     */   {
/* 177 */     boolean eom = (this._pdu.getStatus() & 0x1) != 0;
/* 178 */     return (eom) && (available() <= 0);
/*     */   }
/*     */ 
/*     */   public int getPDUType()
/*     */     throws IOException
/*     */   {
/* 188 */     if ((this._pdu == null) || (endOfLogicalPDU()))
/*     */     {
/* 190 */       readNextPDU();
/*     */     }
/* 192 */     return this._pdu.getType();
/*     */   }
/*     */ 
/*     */   public void skipRestOfLogicalPDU()
/*     */     throws IOException
/*     */   {
/* 201 */     while ((this._pdu.getStatus() & 0x1) == 0)
/*     */     {
/* 203 */       skipBytes(available());
/* 204 */       readNextPDU();
/*     */     }
/*     */ 
/* 208 */     skipBytes(available());
/*     */   }
/*     */ 
/*     */   public byte[] readRestOfLogicalPDU()
/*     */     throws IOException
/*     */   {
/* 216 */     byte[] answer = new byte[0];
/*     */ 
/* 219 */     while ((this._pdu.getStatus() & 0x1) == 0)
/*     */     {
/* 221 */       byte[] pdu = new byte[available()];
/* 222 */       read(pdu);
/* 223 */       answer = concat(answer, pdu);
/* 224 */       readNextPDU();
/*     */     }
/*     */ 
/* 228 */     byte[] pdu = new byte[available()];
/* 229 */     read(pdu);
/* 230 */     answer = concat(answer, pdu);
/*     */ 
/* 232 */     return answer;
/*     */   }
/*     */ 
/*     */   private byte[] concat(byte[] left, byte[] right)
/*     */   {
/* 240 */     byte[] answer = new byte[left.length + right.length];
/* 241 */     System.arraycopy(left, 0, answer, 0, left.length);
/* 242 */     System.arraycopy(right, 0, answer, left.length, right.length);
/* 243 */     return answer;
/*     */   }
/*     */ 
/*     */   public void addHeaderListener(HeaderListener listener)
/*     */   {
/* 256 */     this._listeners.addElement(listener);
/*     */   }
/*     */ 
/*     */   public void removeHeaderListener(HeaderListener listener)
/*     */   {
/* 265 */     this._listeners.removeElement(listener);
/*     */   }
/*     */ 
/*     */   public void postNewHeaderEvent(EventObject event)
/*     */   {
/* 274 */     Enumeration sybenum = this._listeners.elements();
/* 275 */     while (sybenum.hasMoreElements())
/*     */     {
/* 277 */       HeaderListener listener = (HeaderListener)sybenum.nextElement();
/* 278 */       listener.newHeader(event);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readNextPDU()
/*     */     throws IOException
/*     */   {
/* 292 */     this._readingPDUHeader = true;
/*     */ 
/* 295 */     if (available() > 0)
/*     */     {
/* 297 */       throw new Error("Reading a new PDU before the old one is finished.");
/*     */     }
/*     */ 
/* 302 */     byte[] b = new byte[8];
/* 303 */     int count = read(b);
/* 304 */     if (count < 0)
/*     */     {
/* 306 */       throw new EOFException("EOF Reading PDU header");
/*     */     }
/*     */ 
/* 310 */     this._pdu = new SrvPdu();
/*     */     try
/*     */     {
/* 313 */       this._pdu.setpdu(b);
/*     */     }
/*     */     catch (SrvAttentionException e)
/*     */     {
/*     */     }
/*     */ 
/* 319 */     postNewHeaderEvent(new EventObject(this._pdu));
/*     */ 
/* 322 */     this._bytesRead = 0;
/* 323 */     this._readingPDUHeader = false;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.PduInputStream
 * JD-Core Version:    0.5.4
 */