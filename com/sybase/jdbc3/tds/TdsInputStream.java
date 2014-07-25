/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.DataInput;
/*     */ import java.io.EOFException;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ public abstract class TdsInputStream extends FilterInputStream
/*     */   implements DataInput
/*     */ {
/*     */   protected static final int LOW_BYTE = 255;
/*     */   protected static final long D_LOW_BYTE = 255L;
/*  43 */   protected byte[] _buf = { 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*  55 */   protected int _next = -1;
/*     */ 
/*  60 */   protected int _last = -1;
/*     */ 
/*  68 */   private boolean _byteswap = false;
/*     */ 
/*     */   public TdsInputStream(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/*  81 */     super(paramInputStream);
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  92 */     return this.in.read();
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte) throws IOException
/*     */   {
/*  97 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */   {
/* 102 */     return this.in.read(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public int skipBytes(int paramInt) throws IOException
/*     */   {
/* 107 */     for (int i = 0; i < paramInt; i += (int)this.in.skip(paramInt - i));
/* 108 */     return paramInt;
/*     */   }
/*     */ 
/*     */   public void setBigEndian(boolean paramBoolean)
/*     */   {
/* 115 */     this._byteswap = (!paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean getBigEndian()
/*     */   {
/* 120 */     return !this._byteswap;
/*     */   }
/*     */ 
/*     */   public int readUnsignedByte() throws IOException
/*     */   {
/* 125 */     int i = read();
/* 126 */     if (i < 0)
/*     */     {
/* 128 */       throw new EOFException();
/*     */     }
/* 130 */     return i;
/*     */   }
/*     */ 
/*     */   public short readShort() throws IOException
/*     */   {
/* 135 */     int i = read(this._buf, 0, 2);
/* 136 */     if (i < 0)
/*     */     {
/* 138 */       throw new EOFException();
/*     */     }
/*     */ 
/* 141 */     return (short)((this._byteswap) ? (0xFF & this._buf[1]) << 8 | 0xFF & this._buf[0] : (0xFF & this._buf[0]) << 8 | 0xFF & this._buf[1]);
/*     */   }
/*     */ 
/*     */   public int readUnsignedShort()
/*     */     throws IOException
/*     */   {
/* 152 */     int i = read(this._buf, 0, 2);
/* 153 */     if (i < 0)
/*     */     {
/* 155 */       throw new EOFException();
/*     */     }
/*     */ 
/* 158 */     return (this._byteswap) ? (0xFF & this._buf[1]) << 8 | 0xFF & this._buf[0] : (0xFF & this._buf[0]) << 8 | 0xFF & this._buf[1];
/*     */   }
/*     */ 
/*     */   public int readInt()
/*     */     throws IOException
/*     */   {
/* 169 */     int i = read(this._buf, 0, 4);
/* 170 */     if (i < 0)
/*     */     {
/* 172 */       throw new EOFException();
/*     */     }
/* 174 */     return (this._byteswap) ? (0xFF & this._buf[3]) << 24 | (0xFF & this._buf[2]) << 16 | (0xFF & this._buf[1]) << 8 | 0xFF & this._buf[0] : (0xFF & this._buf[0]) << 24 | (0xFF & this._buf[1]) << 16 | (0xFF & this._buf[2]) << 8 | 0xFF & this._buf[3];
/*     */   }
/*     */ 
/*     */   public byte[] readBytesForAllTypes(int paramInt)
/*     */     throws IOException
/*     */   {
/* 189 */     int i = read(this._buf, 0, paramInt);
/* 190 */     if (i < paramInt)
/*     */     {
/* 192 */       throw new EOFException();
/*     */     }
/* 194 */     byte[] arrayOfByte = new byte[paramInt];
/* 195 */     for (int j = 0; j < paramInt; ++j)
/*     */     {
/* 197 */       arrayOfByte[j] = this._buf[j];
/*     */     }
/* 199 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public int readInt(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 210 */     byte[] arrayOfByte = new byte[4];
/* 211 */     int i = paramInputStream.read(arrayOfByte, 0, 4);
/* 212 */     if (i < 0)
/*     */     {
/* 214 */       throw new EOFException();
/*     */     }
/*     */ 
/* 217 */     return (this._byteswap) ? (0xFF & arrayOfByte[3]) << 24 | (0xFF & arrayOfByte[2]) << 16 | (0xFF & arrayOfByte[1]) << 8 | 0xFF & arrayOfByte[0] : (0xFF & arrayOfByte[0]) << 24 | (0xFF & arrayOfByte[1]) << 16 | (0xFF & arrayOfByte[2]) << 8 | 0xFF & arrayOfByte[3];
/*     */   }
/*     */ 
/*     */   public long readUnsignedIntAsLong()
/*     */     throws IOException
/*     */   {
/* 233 */     int i = read(this._buf, 0, 4);
/* 234 */     if (i != 4)
/*     */     {
/* 236 */       throw new EOFException();
/*     */     }
/* 238 */     return (this._byteswap) ? (0xFF & this._buf[3]) << 24 | (0xFF & this._buf[2]) << 16 | (0xFF & this._buf[1]) << 8 | 0xFF & this._buf[0] : (0xFF & this._buf[0]) << 24 | (0xFF & this._buf[1]) << 16 | (0xFF & this._buf[2]) << 8 | 0xFF & this._buf[3];
/*     */   }
/*     */ 
/*     */   public int readUnsignedShortAsInt()
/*     */     throws IOException
/*     */   {
/* 254 */     int i = read(this._buf, 0, 2);
/* 255 */     if (i != 2)
/*     */     {
/* 257 */       throw new EOFException();
/*     */     }
/*     */ 
/* 260 */     return (this._byteswap) ? (0xFF & this._buf[1]) << 8 | 0xFF & this._buf[0] : (0xFF & this._buf[0]) << 8 | 0xFF & this._buf[1];
/*     */   }
/*     */ 
/*     */   public BigDecimal readUnsignedLongAsBigDecimal()
/*     */     throws IOException
/*     */   {
/* 272 */     int i = read(this._buf, 0, 8);
/* 273 */     if (i != 8)
/*     */     {
/* 275 */       throw new EOFException();
/*     */     }
/* 277 */     byte[] arrayOfByte = new byte[8];
/* 278 */     if (this._byteswap)
/*     */     {
/* 280 */       for (j = 0; ; ++j) { if (j >= 8)
/*     */           break label87;
/* 282 */         arrayOfByte[j] = this._buf[(7 - j)]; }
/*     */ 
/*     */ 
/*     */     }
/*     */ 
/* 287 */     for (int j = 0; j < 8; ++j)
/*     */     {
/* 289 */       arrayOfByte[j] = this._buf[j];
/*     */     }
/*     */ 
/* 292 */     label87: BigDecimal localBigDecimal = new BigDecimal(new BigInteger(1, arrayOfByte), 0);
/* 293 */     return localBigDecimal;
/*     */   }
/*     */ 
/*     */   public long readLong() throws IOException
/*     */   {
/* 298 */     int i = read(this._buf, 0, 8);
/* 299 */     if (i < 0)
/*     */     {
/* 301 */       throw new EOFException();
/*     */     }
/*     */ 
/* 304 */     return (this._byteswap) ? (0xFF & this._buf[7]) << 56 | (0xFF & this._buf[6]) << 48 | (0xFF & this._buf[5]) << 40 | (0xFF & this._buf[4]) << 32 | (0xFF & this._buf[3]) << 24 | (0xFF & this._buf[2]) << 16 | (0xFF & this._buf[1]) << 8 | 0xFF & this._buf[0] : (0xFF & this._buf[0]) << 56 | (0xFF & this._buf[1]) << 48 | (0xFF & this._buf[2]) << 40 | (0xFF & this._buf[3]) << 32 | (0xFF & this._buf[4]) << 24 | (0xFF & this._buf[5]) << 16 | (0xFF & this._buf[6]) << 8 | 0xFF & this._buf[7];
/*     */   }
/*     */ 
/*     */   public float readFloat()
/*     */     throws IOException
/*     */   {
/* 327 */     return Float.intBitsToFloat(readInt());
/*     */   }
/*     */ 
/*     */   public double readDouble()
/*     */     throws IOException
/*     */   {
/* 334 */     return Double.longBitsToDouble(readLong());
/*     */   }
/*     */ 
/*     */   public String readString(int paramInt)
/*     */     throws IOException
/*     */   {
/* 347 */     return (String)null;
/*     */   }
/*     */ 
/*     */   public String readUnicodeString(int paramInt)
/*     */     throws IOException
/*     */   {
/* 361 */     return (String)null;
/*     */   }
/*     */ 
/*     */   public String convertBytesToString(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 373 */     return (String)null;
/*     */   }
/*     */ 
/*     */   public String convertUnicodeBytesToString(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 386 */     return (String)null;
/*     */   }
/*     */ 
/*     */   public byte readByte() throws IOException
/*     */   {
/* 391 */     return (byte)read();
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 399 */     return this._last - this._next + this.in.available();
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */   {
/* 410 */     this._next = -1;
/* 411 */     this._last = -1;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 417 */     this._buf = null;
/* 418 */     this.in.close();
/*     */   }
/*     */ 
/*     */   public void readFully(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 424 */     readFully(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */   {
/* 429 */     while (paramInt2 > 0)
/*     */     {
/* 431 */       int i = read(paramArrayOfByte, paramInt1, paramInt2);
/* 432 */       paramInt1 += i;
/* 433 */       paramInt2 -= i;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String readUTF() throws IOException
/*     */   {
/* 439 */     throw new IOException("Not implemented");
/*     */   }
/*     */ 
/*     */   public String readLine() throws IOException
/*     */   {
/* 444 */     throw new IOException("Not implemented");
/*     */   }
/*     */ 
/*     */   public boolean readBoolean() throws IOException
/*     */   {
/* 449 */     throw new IOException("Not implemented");
/*     */   }
/*     */ 
/*     */   public char readChar() throws IOException
/*     */   {
/* 454 */     throw new IOException("Not implemented");
/*     */   }
/*     */ 
/*     */   public abstract String getCharset();
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsInputStream
 * JD-Core Version:    0.5.4
 */