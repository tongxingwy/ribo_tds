/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.CacheManager;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public abstract class PadByteInputStream extends RawInputStream
/*     */ {
/*     */   protected int _padByteLengthRemaining;
/*     */   protected int[] _bytes;
/*     */   protected boolean _even;
/*     */ 
/*     */   public PadByteInputStream(InputStream paramInputStream, int paramInt1, int paramInt2, CacheManager paramCacheManager)
/*     */     throws IOException
/*     */   {
/*  71 */     super(paramInputStream, paramInt1, paramInt2, paramCacheManager);
/*  72 */     this._bytes = new int[2];
/*  73 */     this._padByteLengthRemaining = (paramInt2 * 2);
/*  74 */     this._even = true;
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/*  86 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  98 */     if (this._padByteLengthRemaining == 0)
/*     */     {
/* 100 */       return -1;
/*     */     }
/* 102 */     paramInt2 = (paramInt2 < this._padByteLengthRemaining) ? paramInt2 : this._padByteLengthRemaining;
/*     */ 
/* 104 */     for (int i = paramInt1; i < paramInt2; ++i)
/*     */     {
/* 106 */       paramArrayOfByte[i] = (byte)read();
/*     */     }
/* 108 */     return paramInt2;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 119 */     int i = super.available() * 2;
/* 120 */     return (this._even) ? i : ++i;
/*     */   }
/*     */ 
/*     */   public long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 132 */     paramLong = this._padByteLengthRemaining;
/*     */ 
/* 134 */     for (int i = 0; i < paramLong; ++i)
/*     */     {
/* 136 */       read();
/*     */     }
/* 138 */     return paramLong;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.PadByteInputStream
 * JD-Core Version:    0.5.4
 */