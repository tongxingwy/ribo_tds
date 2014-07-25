/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.utils.Debug;
/*     */ import com.sybase.jdbc3.utils.Misc;
/*     */ import java.io.CharConversionException;
/*     */ import java.io.DataOutput;
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ public abstract class TdsOutputStream extends FilterOutputStream
/*     */   implements DataOutput
/*     */ {
/*  48 */   protected byte[] _numbuf = { 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */   protected static final int LOW_BYTE = 255;
/*     */   protected static final long D_LOW_BYTE = 255L;
/*  64 */   private boolean _byteswap = false;
/*     */ 
/*     */   public TdsOutputStream(OutputStream paramOutputStream) throws IOException
/*     */   {
/*  68 */     super(paramOutputStream);
/*     */   }
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  78 */     this.out.write(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public void write(int paramInt)
/*     */     throws IOException
/*     */   {
/*  85 */     this.out.write(paramInt);
/*     */   }
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/*  92 */     this.out.write(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public final void writeBoolean(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  99 */     throw new IOException("Not a supported TDS type");
/*     */   }
/*     */ 
/*     */   public final void writeUTF(String paramString) throws IOException {
/* 103 */     throw new IOException("Not a supported TDS type");
/*     */   }
/*     */ 
/*     */   public final void writeChar(int paramInt) throws IOException {
/* 107 */     throw new IOException("Not a supported TDS type");
/*     */   }
/*     */ 
/*     */   public final void writeBytes(String paramString) throws IOException {
/* 111 */     throw new IOException("Not a supported TDS type");
/*     */   }
/*     */ 
/*     */   public final void writeChars(String paramString) throws IOException {
/* 115 */     throw new IOException("Not a supported TDS type");
/*     */   }
/*     */ 
/*     */   public final void writeByte(int paramInt)
/*     */     throws IOException
/*     */   {
/* 123 */     write(paramInt);
/*     */   }
/*     */ 
/*     */   public void setBigEndian(boolean paramBoolean)
/*     */   {
/* 130 */     this._byteswap = (!paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean getBigEndian()
/*     */   {
/* 135 */     return !this._byteswap;
/*     */   }
/*     */ 
/*     */   public final void writeShort(int paramInt)
/*     */     throws IOException
/*     */   {
/* 144 */     if (this._byteswap)
/*     */     {
/* 146 */       this._numbuf[1] = (byte)(paramInt >>> 8 & 0xFF);
/* 147 */       this._numbuf[0] = (byte)(paramInt >>> 0 & 0xFF);
/*     */     }
/*     */     else
/*     */     {
/* 151 */       this._numbuf[0] = (byte)(paramInt >>> 8 & 0xFF);
/* 152 */       this._numbuf[1] = (byte)(paramInt >>> 0 & 0xFF);
/*     */     }
/* 154 */     write(this._numbuf, 0, 2);
/*     */   }
/*     */ 
/*     */   public final void writeInt(int paramInt)
/*     */     throws IOException
/*     */   {
/* 160 */     if (this._byteswap)
/*     */     {
/* 162 */       this._numbuf[3] = (byte)(paramInt >>> 24 & 0xFF);
/* 163 */       this._numbuf[2] = (byte)(paramInt >>> 16 & 0xFF);
/* 164 */       this._numbuf[1] = (byte)(paramInt >>> 8 & 0xFF);
/* 165 */       this._numbuf[0] = (byte)(paramInt >>> 0 & 0xFF);
/*     */     }
/*     */     else
/*     */     {
/* 169 */       this._numbuf[0] = (byte)(paramInt >>> 24 & 0xFF);
/* 170 */       this._numbuf[1] = (byte)(paramInt >>> 16 & 0xFF);
/* 171 */       this._numbuf[2] = (byte)(paramInt >>> 8 & 0xFF);
/* 172 */       this._numbuf[3] = (byte)(paramInt >>> 0 & 0xFF);
/*     */     }
/* 174 */     write(this._numbuf, 0, 4);
/*     */   }
/*     */ 
/*     */   public final void writeLongAsUnsignedInt(long paramLong) throws IOException
/*     */   {
/* 179 */     if (this._byteswap)
/*     */     {
/* 181 */       this._numbuf[3] = (byte)(int)(paramLong >>> 24 & 0xFF);
/* 182 */       this._numbuf[2] = (byte)(int)(paramLong >>> 16 & 0xFF);
/* 183 */       this._numbuf[1] = (byte)(int)(paramLong >>> 8 & 0xFF);
/* 184 */       this._numbuf[0] = (byte)(int)(paramLong >>> 0 & 0xFF);
/*     */     }
/*     */     else
/*     */     {
/* 188 */       this._numbuf[0] = (byte)(int)(paramLong >>> 24 & 0xFF);
/* 189 */       this._numbuf[1] = (byte)(int)(paramLong >>> 16 & 0xFF);
/* 190 */       this._numbuf[2] = (byte)(int)(paramLong >>> 8 & 0xFF);
/* 191 */       this._numbuf[3] = (byte)(int)(paramLong >>> 0 & 0xFF);
/*     */     }
/* 193 */     write(this._numbuf, 0, 4);
/*     */   }
/*     */ 
/*     */   public final void writeLong(long paramLong)
/*     */     throws IOException
/*     */   {
/* 200 */     if (this._byteswap)
/*     */     {
/* 202 */       this._numbuf[7] = (byte)((int)(paramLong >>> 56) & 0xFF);
/* 203 */       this._numbuf[6] = (byte)((int)(paramLong >>> 48) & 0xFF);
/* 204 */       this._numbuf[5] = (byte)((int)(paramLong >>> 40) & 0xFF);
/* 205 */       this._numbuf[4] = (byte)((int)(paramLong >>> 32) & 0xFF);
/* 206 */       this._numbuf[3] = (byte)((int)(paramLong >>> 24) & 0xFF);
/* 207 */       this._numbuf[2] = (byte)((int)(paramLong >>> 16) & 0xFF);
/* 208 */       this._numbuf[1] = (byte)((int)(paramLong >>> 8) & 0xFF);
/* 209 */       this._numbuf[0] = (byte)((int)(paramLong >>> 0) & 0xFF);
/*     */     }
/*     */     else
/*     */     {
/* 213 */       this._numbuf[0] = (byte)((int)(paramLong >>> 56) & 0xFF);
/* 214 */       this._numbuf[1] = (byte)((int)(paramLong >>> 48) & 0xFF);
/* 215 */       this._numbuf[2] = (byte)((int)(paramLong >>> 40) & 0xFF);
/* 216 */       this._numbuf[3] = (byte)((int)(paramLong >>> 32) & 0xFF);
/* 217 */       this._numbuf[4] = (byte)((int)(paramLong >>> 24) & 0xFF);
/* 218 */       this._numbuf[5] = (byte)((int)(paramLong >>> 16) & 0xFF);
/* 219 */       this._numbuf[6] = (byte)((int)(paramLong >>> 8) & 0xFF);
/* 220 */       this._numbuf[7] = (byte)((int)(paramLong >>> 0) & 0xFF);
/*     */     }
/* 222 */     write(this._numbuf, 0, 8);
/*     */   }
/*     */ 
/*     */   public final void writeFloat(float paramFloat)
/*     */     throws IOException
/*     */   {
/* 228 */     writeInt(Float.floatToIntBits(paramFloat));
/*     */   }
/*     */ 
/*     */   public final void writeDouble(double paramDouble) throws IOException
/*     */   {
/* 233 */     writeLong(Double.doubleToLongBits(paramDouble));
/*     */   }
/*     */ 
/*     */   public final int size()
/*     */   {
/* 238 */     return -1;
/*     */   }
/*     */ 
/*     */   public void writeString(String paramString)
/*     */     throws IOException
/*     */   {
/* 254 */     throw new IOException("Not a supported TDS type");
/*     */   }
/*     */ 
/*     */   public void writeStringLen(String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/* 269 */     int i = 0;
/* 270 */     if (paramString != null)
/*     */     {
/* 272 */       byte[] arrayOfByte = toBytes(paramString);
/* 273 */       i = arrayOfByte.length;
/* 274 */       if (i > paramInt)
/*     */       {
/* 276 */         ErrorMessage.raiseIOException("JZ0TS", paramString);
/*     */       }
/* 278 */       write(arrayOfByte);
/*     */     }
/*     */ 
/* 281 */     for (int j = i; j < paramInt; ++j)
/*     */     {
/* 283 */       write(0);
/*     */     }
/*     */ 
/* 286 */     write(i);
/*     */   }
/*     */ 
/*     */   public byte[] stringToByte(String paramString)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 297 */       return toBytes(paramString);
/*     */     }
/*     */     catch (CharConversionException localCharConversionException)
/*     */     {
/* 301 */       ErrorMessage.raiseIOException("JZ0I6", localCharConversionException.toString(), localCharConversionException);
/*     */ 
/* 305 */       Debug.asrt(null, false);
/* 306 */     }return null;
/*     */   }
/*     */ 
/*     */   public final byte[] intToBytes(int paramInt)
/*     */   {
/* 311 */     if (this._byteswap)
/*     */     {
/* 313 */       this._numbuf[3] = (byte)(paramInt >>> 24 & 0xFF);
/* 314 */       this._numbuf[2] = (byte)(paramInt >>> 16 & 0xFF);
/* 315 */       this._numbuf[1] = (byte)(paramInt >>> 8 & 0xFF);
/* 316 */       this._numbuf[0] = (byte)(paramInt >>> 0 & 0xFF);
/*     */     }
/*     */     else
/*     */     {
/* 320 */       this._numbuf[0] = (byte)(paramInt >>> 24 & 0xFF);
/* 321 */       this._numbuf[1] = (byte)(paramInt >>> 16 & 0xFF);
/* 322 */       this._numbuf[2] = (byte)(paramInt >>> 8 & 0xFF);
/* 323 */       this._numbuf[3] = (byte)(paramInt >>> 0 & 0xFF);
/*     */     }
/* 325 */     return this._numbuf;
/*     */   }
/*     */ 
/*     */   protected abstract byte[] toBytes(String paramString)
/*     */     throws CharConversionException;
/*     */ 
/*     */   protected int getStringByteLen(String paramString)
/*     */     throws IOException
/*     */   {
/* 338 */     return (paramString == null) ? 0 : toBytes(paramString).length;
/*     */   }
/*     */ 
/*     */   public abstract String getCharset();
/*     */ 
/*     */   public final void writeBigDecimalAsUnsignedBigInt(BigDecimal paramBigDecimal)
/*     */     throws IOException
/*     */   {
/* 353 */     BigDecimal localBigDecimal1 = paramBigDecimal;
/* 354 */     BigDecimal localBigDecimal2 = new BigDecimal("256");
/*     */     BigDecimal[] arrayOfBigDecimal;
/* 355 */     if (this._byteswap)
/*     */     {
/* 357 */       for (i = 0; ; ++i) { if (i >= 8)
/*     */           break label108;
/* 359 */         arrayOfBigDecimal = Misc.divideAndRemainder(localBigDecimal1, localBigDecimal2);
/* 360 */         localBigDecimal1 = arrayOfBigDecimal[0];
/* 361 */         this._numbuf[i] = arrayOfBigDecimal[1].toBigInteger().byteValue(); }
/*     */ 
/*     */ 
/*     */     }
/*     */ 
/* 366 */     for (int i = 7; i >= 0; --i)
/*     */     {
/* 368 */       arrayOfBigDecimal = Misc.divideAndRemainder(localBigDecimal1, localBigDecimal2);
/* 369 */       localBigDecimal1 = arrayOfBigDecimal[0];
/* 370 */       this._numbuf[i] = arrayOfBigDecimal[1].toBigInteger().byteValue();
/*     */     }
/*     */ 
/* 373 */     label108: write(this._numbuf, 0, 8);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsOutputStream
 * JD-Core Version:    0.5.4
 */