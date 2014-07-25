/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.tds.TdsDataInputStream;
/*     */ import com.sybase.jdbc3.utils.CacheManager;
/*     */ import com.sybase.jdbc3.utils.CacheStream;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class RawInputStream extends FilterInputStream
/*     */ {
/*     */   private static final int HIBIT = -2147483648;
/*     */   private static final int LOBITS = 2147483647;
/*     */   private static final int NOLIMIT = -1;
/*     */   private int _visibleLength;
/*     */   private boolean _noLimit;
/*     */   private int _actualLength;
/*     */   private int _resetVisibleLength;
/*     */   private int _resetActualLength;
/*     */   private boolean _resetLastChunk;
/*     */   private int _bytesRead;
/*     */   private int _jmark;
/*     */   private boolean _markSupported;
/*     */   CacheManager _cm;
/*     */   TdsDataInputStream _tdis;
/*  76 */   private boolean _dead = false;
/*  77 */   protected boolean _isCached = false;
/*  78 */   private boolean _needReset = false;
/*     */ 
/*  81 */   private boolean _lastChunk = true;
/*     */ 
/*     */   public RawInputStream(InputStream paramInputStream, int paramInt1, int paramInt2, CacheManager paramCacheManager)
/*     */     throws IOException
/*     */   {
/* 100 */     super(paramInputStream);
/*     */ 
/* 103 */     this._resetVisibleLength = paramInt2;
/* 104 */     this._visibleLength = paramInt2;
/* 105 */     this._noLimit = (paramInt2 == -1);
/* 106 */     this._resetActualLength = paramInt1;
/* 107 */     this._actualLength = paramInt1;
/* 108 */     this._bytesRead = 0;
/* 109 */     this._jmark = -1;
/* 110 */     this._markSupported = false;
/* 111 */     this._cm = paramCacheManager;
/* 112 */     if (paramInputStream instanceof TdsDataInputStream)
/*     */     {
/* 114 */       this._tdis = ((TdsDataInputStream)paramInputStream);
/*     */     }
/*     */     else
/*     */     {
/* 118 */       this._tdis = null;
/*     */     }
/* 120 */     if (paramInt1 >= 0) {
/*     */       return;
/*     */     }
/*     */ 
/* 124 */     this._lastChunk = false;
/*     */   }
/*     */ 
/*     */   public void setCached(boolean paramBoolean)
/*     */   {
/* 138 */     this._isCached = paramBoolean;
/*     */   }
/*     */ 
/*     */   public boolean cache(CacheStream paramCacheStream)
/*     */     throws IOException
/*     */   {
/* 152 */     if (this._jmark != -1)
/*     */     {
/* 155 */       return true;
/*     */     }
/* 157 */     this._jmark = this._bytesRead;
/* 158 */     this._needReset = true;
/* 159 */     this._resetActualLength = this._actualLength;
/* 160 */     this._resetVisibleLength = this._visibleLength;
/* 161 */     this._resetLastChunk = this._lastChunk;
/* 162 */     if (((this._actualLength == 0) && (this._lastChunk)) || (this._dead))
/*     */     {
/* 165 */       this._isCached = true;
/* 166 */       return true;
/*     */     }
/*     */ 
/* 178 */     if (this._isCached)
/*     */     {
/* 180 */       return true;
/*     */     }
/* 182 */     this.in = paramCacheStream;
/*     */     try
/*     */     {
/* 189 */       byte[] arrayOfByte = new byte[512];
/*     */ 
/* 191 */       for (; (this._actualLength > 0) || (!this._lastChunk); nextChunk())
/*     */       {
/* 194 */         while (this._actualLength > 0)
/*     */         {
/* 196 */           int i = (this._actualLength < 512) ? this._actualLength : 512;
/* 197 */           i = this.in.read(arrayOfByte, 0, i);
/*     */ 
/* 199 */           if (i > 0)
/*     */           {
/* 201 */             this._actualLength -= i;
/* 202 */             this._bytesRead += i;
/*     */           }
/*     */ 
/* 206 */           ErrorMessage.raiseIOException("JZ0EM");
/*     */         }
/*     */       }
/*     */ 
/* 210 */       this._isCached = true;
/*     */ 
/* 213 */       doneReading();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 217 */       this._dead = true;
/* 218 */       throw localIOException;
/*     */     }
/* 220 */     return this._isCached;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/* 230 */     checkMe();
/* 231 */     if (this._visibleLength == 0)
/*     */     {
/* 233 */       return -1;
/*     */     }
/* 235 */     if (!this._noLimit) this._visibleLength -= 1;
/* 236 */     int i = 0;
/* 237 */     if (this._actualLength > 0)
/*     */     {
/* 239 */       this._actualLength -= 1;
/* 240 */       this._bytesRead += 1;
/* 241 */       i = this.in.read();
/*     */     }
/*     */ 
/* 244 */     checkDone();
/* 245 */     return i;
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 256 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 268 */     checkMe();
/* 269 */     if (paramInt2 == 0) return 0;
/* 270 */     int i = paramInt2;
/* 271 */     if ((!this._noLimit) && (this._visibleLength < paramInt2))
/*     */     {
/* 273 */       i = this._visibleLength;
/*     */     }
/*     */ 
/* 276 */     if (i == 0)
/*     */     {
/* 279 */       return -1;
/*     */     }
/* 281 */     paramInt2 = i;
/*     */     int j;
/* 282 */     while ((paramInt2 > 0) && (((this._actualLength > 0) || (!this._lastChunk))))
/*     */     {
/* 284 */       j = (paramInt2 < this._actualLength) ? paramInt2 : this._actualLength;
/* 285 */       if (j > 0)
/*     */       {
/* 290 */         int k = this.in.read(paramArrayOfByte, paramInt1, j);
/*     */ 
/* 292 */         paramInt1 += k;
/* 293 */         paramInt2 -= k;
/* 294 */         if (this._actualLength > 0)
/*     */         {
/* 296 */           this._actualLength -= k;
/*     */         }
/* 298 */         this._bytesRead += k;
/*     */       }
/* 300 */       if (this._actualLength != 0)
/*     */         continue;
/* 302 */       nextChunk();
/*     */     }
/*     */ 
/* 305 */     if (this._noLimit)
/*     */     {
/* 307 */       if (paramInt2 == i)
/*     */       {
/* 310 */         return -1;
/*     */       }
/* 312 */       if (paramInt2 > 0)
/*     */       {
/* 315 */         i -= paramInt2;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 320 */       this._visibleLength -= i;
/*     */ 
/* 323 */       if (paramInt2 > 0)
/*     */       {
/* 325 */         for (j = 0; j < paramInt2; ++j)
/*     */         {
/* 327 */           paramArrayOfByte[(paramInt1 + j)] = 0;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 332 */     checkDone();
/* 333 */     return i;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 344 */     checkMe();
/*     */ 
/* 346 */     if (this.in == null)
/*     */     {
/* 349 */       return 0;
/*     */     }
/* 351 */     int i = 0;
/* 352 */     if (this._actualLength > 0)
/*     */     {
/* 354 */       i = this.in.available();
/*     */     }
/* 356 */     if ((i < this._actualLength) && 
/* 358 */       (i < this._visibleLength) && (!this._noLimit))
/*     */     {
/* 360 */       return i;
/*     */     }
/*     */ 
/* 365 */     return (this._noLimit) ? this._actualLength : this._visibleLength;
/*     */   }
/*     */ 
/*     */   public long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 377 */     checkMe();
/* 378 */     if (this._visibleLength == 0) return 0L;
/* 379 */     long l1 = paramLong;
/*     */ 
/* 381 */     if (!this._noLimit)
/*     */     {
/* 383 */       if (this._visibleLength < paramLong) l1 = this._visibleLength;
/* 384 */       this._visibleLength = (int)(this._visibleLength - l1);
/*     */     }
/* 386 */     long l2 = 0L;
/* 387 */     while ((l1 > 0L) && (((this._actualLength > 0) || (!this._lastChunk))))
/*     */     {
/* 389 */       long l3 = this._actualLength;
/*     */ 
/* 393 */       if (l3 > 0L)
/*     */       {
/* 395 */         this.in.skip(l3);
/* 396 */         this._actualLength = (int)(this._actualLength - l3);
/* 397 */         this._bytesRead = (int)(this._bytesRead + l3);
/* 398 */         l1 -= l3;
/* 399 */         l2 += l3;
/*     */       }
/* 401 */       if (l1 <= 0L)
/*     */         continue;
/* 403 */       nextChunk();
/*     */     }
/*     */ 
/* 407 */     checkDone();
/* 408 */     return l2;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 419 */     if (this._dead) return;
/*     */ 
/* 421 */     if (this._dead) return;
/* 422 */     this._needReset = false;
/* 423 */     checkMe();
/*     */     try
/*     */     {
/* 426 */       if (!this._isCached)
/*     */       {
/* 428 */         if (this._noLimit) {
/*     */           while (true) {
/* 430 */             if ((this._actualLength <= 0) && (this._lastChunk))
/*     */               break label100;
/* 432 */             skip(this._actualLength);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 453 */         read(new byte[this._actualLength], 0, this._actualLength);
/*     */ 
/* 455 */         this._visibleLength = this._actualLength;
/* 456 */         skip(this._visibleLength);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 463 */       label100: throw localIOException;
/*     */     }
/*     */     finally
/*     */     {
/* 467 */       checkDone();
/* 468 */       this._dead = true;
/*     */ 
/* 470 */       this.in = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean markSupported()
/*     */   {
/* 477 */     return this._markSupported;
/*     */   }
/*     */ 
/*     */   private void checkMe()
/*     */     throws IOException
/*     */   {
/* 486 */     if (this._dead)
/*     */     {
/* 488 */       ErrorMessage.raiseIOException("JZ0I9");
/*     */     }
/* 490 */     if ((this._needReset) && (this._tdis != null))
/*     */     {
/* 496 */       this._tdis.reset();
/* 497 */       this._needReset = false;
/*     */ 
/* 499 */       this.in.reset();
/* 500 */       this._visibleLength = this._resetVisibleLength;
/* 501 */       this._actualLength = this._resetActualLength;
/* 502 */       this._lastChunk = this._resetLastChunk;
/* 503 */       this._bytesRead = 0;
/* 504 */       this.in.skip(this._jmark);
/* 505 */       this._bytesRead = this._jmark;
/* 506 */       this._jmark = -1;
/*     */     }
/* 508 */     if ((this._actualLength > 0) || (this._lastChunk))
/*     */       return;
/* 510 */     nextChunk();
/*     */   }
/*     */ 
/*     */   private void checkDone()
/*     */     throws IOException
/*     */   {
/* 517 */     if ((this.in == null) || ((this._visibleLength != 0) && (((!this._noLimit) || (this._actualLength != 0) || (!this._lastChunk)))))
/*     */     {
/*     */       return;
/*     */     }
/*     */ 
/* 522 */     doneReading();
/*     */ 
/* 524 */     this._isCached = true;
/*     */   }
/*     */ 
/*     */   private void doneReading()
/*     */     throws IOException
/*     */   {
/* 531 */     if (!this._isCached)
/*     */     {
/* 535 */       if (!this._noLimit)
/*     */       {
/* 539 */         if (this._resetActualLength > this._resetVisibleLength)
/*     */         {
/* 549 */           this.in.read(new byte[this._actualLength]);
/*     */         }
/*     */         else
/*     */         {
/* 555 */           this.in.skip(this._actualLength);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 561 */         while ((this.in != null) && (((this._actualLength > 0) || (!this._lastChunk))))
/*     */         {
/* 563 */           skip(this._actualLength);
/*     */         }
/*     */       }
/*     */     }
/* 567 */     if (this._cm == null)
/*     */       return;
/* 569 */     this._cm.doneReading();
/* 570 */     this._cm = null;
/*     */   }
/*     */ 
/*     */   private void nextChunk()
/*     */     throws IOException
/*     */   {
/* 578 */     if (this._lastChunk) return;
/*     */ 
/* 581 */     int i = this._tdis.readInt(this.in);
/* 582 */     this._bytesRead += 4;
/*     */ 
/* 586 */     if ((i & 0x80000000) == 0)
/*     */     {
/* 588 */       this._lastChunk = true;
/*     */     }
/* 590 */     this._actualLength = (i & 0x7FFFFFFF);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.RawInputStream
 * JD-Core Version:    0.5.4
 */