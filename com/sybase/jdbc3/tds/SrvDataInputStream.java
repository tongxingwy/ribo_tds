/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.Debug;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.MalformedInputException;
/*     */ 
/*     */ public class SrvDataInputStream extends TdsInputStream
/*     */ {
/*  64 */   private SrvPdu _pdu = new SrvPdu();
/*  65 */   private byte[] _packet = null;
/*     */ 
/*  67 */   private CharsetDecoder _btcc = null;
/*     */ 
/*     */   public SrvDataInputStream(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/*  76 */     super(paramInputStream);
/*  77 */     this._btcc = Charset.forName(this._pdu._charset).newDecoder();
/*  78 */     setPacketSize(this._pdu._packetSize);
/*     */   }
/*     */ 
/*     */   public void setEncoding(String paramString)
/*     */     throws IOException
/*     */   {
/*  84 */     this._pdu._charset = paramString;
/*  85 */     this._btcc = Charset.forName(this._pdu._charset).newDecoder();
/*     */   }
/*     */ 
/*     */   public String getCharset()
/*     */   {
/*  90 */     return this._pdu._charset;
/*     */   }
/*     */ 
/*     */   public void setPacketSize(int paramInt)
/*     */   {
/*  99 */     Debug.asrt((this._last < 0) || (this._last == this._next), "TDS input buffer in use, cannot reset packetsize");
/*     */ 
/* 101 */     this._pdu._packetSize = paramInt;
/* 102 */     this._packet = new byte[this._pdu._packetSize];
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/* 108 */     if (this._next >= this._last)
/*     */     {
/* 110 */       if ((this._pdu._inBulk) && ((this._pdu._msgStatus & 0x1) != 0))
/*     */       {
/* 118 */         return -1;
/*     */       }
/* 120 */       fill();
/*     */     }
/* 122 */     return this._packet[(this._next++)] & 0xFF;
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 131 */     int i = 0;
/* 132 */     for (; i < paramInt2; ++i)
/*     */     {
/* 134 */       int j = read();
/* 135 */       if (j == -1) {
/*     */         break;
/*     */       }
/*     */ 
/* 139 */       paramArrayOfByte[(paramInt1 + i)] = (byte)j;
/*     */     }
/* 141 */     return (i == 0) ? -1 : i;
/*     */   }
/*     */ 
/*     */   public long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 155 */     for (int i = 0; i < paramLong; ++i)
/*     */     {
/* 157 */       if (read() != -1)
/*     */         continue;
/* 159 */       throw new EOFException("TDS data input stream lost connection");
/*     */     }
/*     */ 
/* 162 */     return i;
/*     */   }
/*     */ 
/*     */   private void fill()
/*     */     throws IOException
/*     */   {
/* 174 */     int i = 0;
/*     */     do {
/* 176 */       if (this._last > this._next) {
/*     */         return;
/*     */       }
/* 179 */       i = this.in.read(this._packet, 0, SrvPdu.size());
/* 180 */       if (i < 0)
/*     */       {
/* 182 */         throw new EOFException("TDS data input stream lost connection");
/*     */       }
/*     */ 
/* 185 */       this._pdu.setpdu(this._packet);
/*     */ 
/* 188 */       int j = 0;
/* 189 */       while (j < this._pdu.dataLength())
/*     */       {
/* 191 */         i = this.in.read(this._packet, SrvPdu.size() + j, this._pdu.dataLength() - j);
/* 192 */         if (i < 0)
/*     */         {
/* 194 */           throw new EOFException("TDS data input stream lost connection");
/*     */         }
/* 196 */         j += i;
/*     */       }
/*     */ 
/* 200 */       this._next = SrvPdu.size();
/* 201 */       this._last = (j + this._next);
/*     */     }
/*     */ 
/* 210 */     while (!this._pdu.bulkOccurred());
/*     */ 
/* 213 */     throw new SrvBulkException();
/*     */   }
/*     */ 
/*     */   public int skipBytes(int paramInt)
/*     */     throws IOException
/*     */   {
/* 221 */     return (int)skip(paramInt);
/*     */   }
/*     */ 
/*     */   public final String readString(int paramInt)
/*     */     throws IOException
/*     */   {
/* 238 */     if (paramInt <= 0)
/*     */     {
/* 240 */       return null;
/*     */     }
/* 242 */     byte[] arrayOfByte = new byte[paramInt];
/* 243 */     while (paramInt > 0)
/*     */     {
/* 245 */       int i = read(arrayOfByte, arrayOfByte.length - paramInt, paramInt);
/* 246 */       if (i == -1)
/*     */       {
/* 249 */         throw new IOException("Unexpected end of data from client");
/*     */       }
/* 251 */       paramInt -= i;
/*     */     }
/* 253 */     ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
/* 254 */     String str = null;
/*     */     try
/*     */     {
/* 257 */       str = new String(this._btcc.decode(localByteBuffer).array());
/*     */     }
/*     */     catch (MalformedInputException localMalformedInputException)
/*     */     {
/* 269 */       str = "\"\n\n>>>> " + localMalformedInputException + ": Error while decoding char data in " + this._pdu._charset;
/*     */     }
/*     */ 
/* 273 */     return str;
/*     */   }
/*     */ 
/*     */   public final String readUnicodeString(int paramInt)
/*     */     throws IOException
/*     */   {
/* 281 */     if (paramInt <= 0)
/*     */     {
/* 283 */       return null;
/*     */     }
/*     */ 
/* 286 */     byte[] arrayOfByte = new byte[paramInt];
/* 287 */     int i = paramInt;
/* 288 */     while (i > 0)
/*     */     {
/* 290 */       int j = read(arrayOfByte, paramInt - i, i);
/* 291 */       if (j == -1)
/*     */       {
/* 294 */         throw new IOException("Unexpected end of data from client");
/*     */       }
/* 296 */       i -= j;
/*     */     }
/* 298 */     return convertUnicodeBytesToString(arrayOfByte);
/*     */   }
/*     */ 
/*     */   public final String convertUnicodeBytesToString(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 308 */     if (paramArrayOfByte == null)
/*     */     {
/* 310 */       return null;
/*     */     }
/*     */ 
/* 313 */     int i = paramArrayOfByte.length;
/*     */ 
/* 315 */     if (i == 0)
/*     */     {
/* 317 */       return null;
/*     */     }
/*     */ 
/* 320 */     String str1 = null;
/* 321 */     String str2 = null;
/* 322 */     if (getBigEndian())
/*     */     {
/* 324 */       str2 = "UnicodeBig";
/*     */     }
/*     */     else
/*     */     {
/* 328 */       str2 = "UnicodeLittle";
/*     */     }
/*     */     try
/*     */     {
/* 332 */       str1 = new String(paramArrayOfByte, 0, i, str2);
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */     {
/*     */     }
/*     */ 
/* 339 */     return str1;
/*     */   }
/*     */ 
/*     */   public final String convertBytesToString(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 351 */     ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
/* 352 */     String str = null;
/*     */     try
/*     */     {
/* 355 */       str = new String(this._btcc.decode(localByteBuffer).array());
/*     */     }
/*     */     catch (MalformedInputException localMalformedInputException)
/*     */     {
/* 367 */       str = "\"\n\n>>>> " + localMalformedInputException + ": Error while decoding char data in " + this._pdu._charset;
/*     */     }
/*     */ 
/* 371 */     return str;
/*     */   }
/*     */ 
/*     */   protected void bulkDone()
/*     */   {
/* 376 */     this._pdu._inBulk = false;
/*     */   }
/*     */ 
/*     */   public int getCurrentPDUType()
/*     */   {
/* 381 */     return this._pdu._msgType;
/*     */   }
/*     */ 
/*     */   public int getCurrentPDULength()
/*     */   {
/* 386 */     return this._pdu._length;
/*     */   }
/*     */ 
/*     */   public int getCurrentPDUStatus()
/*     */   {
/* 391 */     return this._pdu._msgStatus;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDataInputStream
 * JD-Core Version:    0.5.4
 */