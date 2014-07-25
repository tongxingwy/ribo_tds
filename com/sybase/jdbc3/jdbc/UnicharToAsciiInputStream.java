/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.CacheManager;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class UnicharToAsciiInputStream extends RawInputStream
/*     */ {
/*     */   private int _bytesRemainingInStream;
/*     */   private int[] _bytes;
/*     */   private int _returnVal;
/*     */   private boolean _isBigEndian;
/*     */ 
/*     */   public UnicharToAsciiInputStream(InputStream paramInputStream, int paramInt1, int paramInt2, boolean paramBoolean, CacheManager paramCacheManager)
/*     */     throws IOException
/*     */   {
/*  68 */     super(paramInputStream, paramInt1, paramInt2, paramCacheManager);
/*  69 */     this._bytesRemainingInStream = paramInt2;
/*  70 */     this._bytes = new int[2];
/*  71 */     this._isBigEndian = paramBoolean;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  82 */     if (this._bytesRemainingInStream == 0)
/*     */     {
/*  84 */       return -1;
/*     */     }
/*     */ 
/*  91 */     if (this._isBigEndian)
/*     */     {
/*  93 */       this._bytes[0] = super.read();
/*     */ 
/*  96 */       this._bytes[1] = super.read();
/*     */ 
/*  99 */       this._returnVal = this._bytes[1];
/*     */     }
/*     */     else
/*     */     {
/* 103 */       this._bytes[0] = super.read();
/*     */ 
/* 106 */       this._bytes[1] = super.read();
/*     */ 
/* 109 */       this._returnVal = this._bytes[0];
/*     */     }
/*     */ 
/* 112 */     this._bytesRemainingInStream -= 2;
/* 113 */     return this._returnVal;
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 124 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 137 */     if (this._bytesRemainingInStream == 0)
/*     */     {
/* 139 */       return -1;
/*     */     }
/* 141 */     paramInt2 = (paramInt2 < this._bytesRemainingInStream / 2) ? paramInt2 : this._bytesRemainingInStream / 2;
/*     */ 
/* 143 */     for (int i = paramInt1; i < paramInt2; ++i)
/*     */     {
/* 145 */       paramArrayOfByte[i] = (byte)read();
/*     */     }
/* 147 */     return paramInt2;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 163 */     int i = super.available() / 2;
/* 164 */     return i;
/*     */   }
/*     */ 
/*     */   public long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 176 */     paramLong = this._bytesRemainingInStream / 2;
/*     */ 
/* 179 */     for (int i = 0; i < paramLong; ++i)
/*     */     {
/* 181 */       read();
/*     */     }
/* 183 */     return paramLong;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.UnicharToAsciiInputStream
 * JD-Core Version:    0.5.4
 */