/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public abstract class CacheableInputStream extends InputStream
/*     */   implements Cacheable
/*     */ {
/*     */   protected InputStream _is;
/*     */   protected CacheManager _cm;
/*  36 */   protected int _state = 0;
/*     */ 
/*     */   public void setManager(CacheManager paramCacheManager)
/*     */   {
/*  52 */     this._cm = paramCacheManager;
/*     */   }
/*     */ 
/*     */   public void open(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  60 */     if (paramBoolean)
/*     */     {
/*  62 */       this._state = 2;
/*     */     }
/*     */     else
/*     */     {
/*  66 */       this._state = 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  74 */     checkRead();
/*  75 */     return this._is.read();
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/*  81 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  90 */     checkRead();
/*  91 */     int i = 0;
/*  92 */     while (paramInt2 > 0)
/*     */     {
/*  94 */       int j = this._is.read(paramArrayOfByte, paramInt1 + i, paramInt2);
/*  95 */       if (j < 0)
/*     */       {
/*  98 */         if (i != 0) {
/*     */           break;
/*     */         }
/* 101 */         return -1;
/*     */       }
/*     */ 
/* 105 */       i += j;
/* 106 */       paramInt2 -= j;
/*     */     }
/* 108 */     return i;
/*     */   }
/*     */ 
/*     */   public long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 115 */     checkRead();
/* 116 */     return this._is.skip(paramLong);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 190 */     this._state = 0;
/* 191 */     this._cm.dead(this);
/* 192 */     this._cm = null;
/*     */   }
/*     */ 
/*     */   public void resetInputStream(InputStream paramInputStream)
/*     */   {
/* 200 */     this._is = paramInputStream;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */     throws IOException
/*     */   {
/* 207 */     if (this._state == 2)
/*     */     {
/* 210 */       cache();
/*     */     }
/* 212 */     ((CacheStream)this._is).reset();
/*     */   }
/*     */ 
/*     */   public void checkRead()
/*     */     throws IOException
/*     */   {
/* 218 */     if (this._state != 0)
/*     */       return;
/* 220 */     CacheManager.raiseIOException("IO_NOT_OPEN");
/*     */   }
/*     */ 
/*     */   public int getState()
/*     */   {
/* 226 */     return this._state;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.CacheableInputStream
 * JD-Core Version:    0.5.4
 */