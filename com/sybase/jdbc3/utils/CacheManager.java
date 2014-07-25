/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class CacheManager
/*     */ {
/*     */   public static final boolean DEBUG = false;
/*     */   public static final boolean ASSERT = false;
/*     */   public static final int INFINITE_CACHING = -1;
/*     */   public static final int NO_CACHING = 0;
/*     */   public static final int DEFAULT_CHUNK_SIZE = 0;
/*     */   public static final String IO_CACHE_EXHAUSED = "IO_CACHE_EXHAUSED";
/*     */   public static final String IO_NOT_RESETABLE = "IO_NOT_RESETABLE";
/*     */   public static final String IO_CLOSED = "IO_CLOSED";
/*     */   public static final String IO_NOT_OPEN = "IO_NOT_OPEN";
/*     */   private static ResourceBundle _messages;
/*     */   protected Vector _colleagues;
/*     */   protected InputStream _is;
/* 135 */   protected int _maxCacheSize = -1;
/* 136 */   protected int _currentCacheSize = 0;
/*     */ 
/* 139 */   protected int _chunkSize = 0;
/* 140 */   protected int _cacheTimeout = 0;
/* 141 */   protected boolean _reReadable = false;
/*     */ 
/* 143 */   protected boolean _setable = true;
/* 144 */   protected boolean _abortOnCacheOverflow = false;
/* 145 */   protected Cacheable _current = null;
/* 146 */   protected CacheChunk _free = null;
/*     */ 
/* 148 */   private boolean _resettingChunkSize = false;
/*     */ 
/*     */   public CacheManager(InputStream paramInputStream)
/*     */   {
/* 161 */     this._is = paramInputStream;
/* 162 */     this._colleagues = new Vector();
/*     */   }
/*     */ 
/*     */   public synchronized void setCacheSize(int paramInt)
/*     */   {
/* 176 */     this._maxCacheSize = paramInt;
/*     */   }
/*     */ 
/*     */   public synchronized void setChunkSize(int paramInt)
/*     */   {
/* 191 */     this._chunkSize = paramInt;
/*     */ 
/* 196 */     CacheChunk localCacheChunk = this._free;
/* 197 */     this._free = null;
/* 198 */     this._resettingChunkSize = true;
/* 199 */     putChunks(localCacheChunk);
/* 200 */     this._resettingChunkSize = false;
/*     */   }
/*     */ 
/*     */   public synchronized void setCacheTimeout(int paramInt)
/*     */   {
/* 215 */     this._cacheTimeout = paramInt;
/*     */   }
/*     */ 
/*     */   public synchronized void setReReadable(boolean paramBoolean)
/*     */   {
/* 227 */     this._reReadable = paramBoolean;
/*     */   }
/*     */ 
/*     */   public synchronized void setSetable(boolean paramBoolean)
/*     */   {
/* 242 */     this._setable = paramBoolean;
/*     */   }
/*     */ 
/*     */   public synchronized void setAbortOnCacheOverflow(boolean paramBoolean)
/*     */   {
/* 259 */     this._abortOnCacheOverflow = paramBoolean;
/*     */   }
/*     */ 
/*     */   public synchronized void register(Cacheable paramCacheable)
/*     */   {
/* 273 */     paramCacheable.setManager(this);
/* 274 */     if (this._colleagues.contains(paramCacheable))
/*     */       return;
/* 276 */     this._colleagues.addElement(paramCacheable);
/*     */   }
/*     */ 
/*     */   public void open(Cacheable paramCacheable)
/*     */     throws IOException
/*     */   {
/* 298 */     if (this._setable)
/*     */     {
/* 300 */       Object localObject = this._is;
/* 301 */       if (this._reReadable)
/*     */       {
/* 303 */         localObject = new CacheStream(this, (InputStream)localObject, this._cacheTimeout);
/*     */       }
/* 305 */       paramCacheable.resetInputStream((InputStream)localObject);
/*     */     }
/*     */ 
/* 308 */     beginReading(paramCacheable);
/*     */   }
/*     */ 
/*     */   public synchronized void clear()
/*     */     throws IOException
/*     */   {
/* 324 */     if (this._colleagues == null)
/*     */       return;
/* 326 */     for (int i = 0; i < this._colleagues.size(); ++i)
/*     */     {
/* 328 */       Cacheable localCacheable = (Cacheable)this._colleagues.elementAt(i);
/* 329 */       localCacheable.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void beginReading(Cacheable paramCacheable)
/*     */     throws IOException
/*     */   {
/*     */     Cacheable localCacheable;
/* 383 */     synchronized (this)
/*     */     {
/* 385 */       if ((this._current == null) || (this._current == paramCacheable))
/*     */       {
/* 387 */         paramCacheable.open(this._reReadable);
/* 388 */         this._current = paramCacheable;
/* 389 */         return;
/*     */       }
/*     */ 
/* 395 */       localCacheable = this._current;
/*     */     }
/* 397 */     synchronized (localCacheable)
/*     */     {
/* 399 */       if (localCacheable == this._current);
/* 407 */       int i = localCacheable.getState();
/* 408 */       if ((i == 1) && (this._setable))
/*     */       {
/* 410 */         localCacheable.resetInputStream(new CacheStream(this, this._is, this._cacheTimeout));
/*     */       }
/*     */ 
/* 413 */       if (i != 0)
/*     */       {
/* 415 */         localCacheable.cache();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void doneReading()
/*     */   {
/* 434 */     this._current = null;
/*     */   }
/*     */ 
/*     */   public synchronized void dead(Cacheable paramCacheable)
/*     */   {
/* 446 */     if (this._current == paramCacheable) this._current = null;
/* 447 */     this._colleagues.removeElement(paramCacheable);
/*     */   }
/*     */ 
/*     */   public synchronized void allDead()
/*     */   {
/* 460 */     this._current = null;
/* 461 */     this._colleagues.removeAllElements();
/*     */   }
/*     */ 
/*     */   protected synchronized CacheChunk getChunk(int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 483 */     int i = (paramInt1 < this._chunkSize) ? this._chunkSize : paramInt1;
/*     */ 
/* 485 */     if ((this._maxCacheSize >= 0) && (this._maxCacheSize - this._currentCacheSize < i))
/*     */     {
/* 489 */       if (this._abortOnCacheOverflow)
/*     */       {
/* 492 */         raiseIOException("IO_CACHE_EXHAUSED");
/*     */       }
/*     */       else
/*     */       {
/* 499 */         long l1 = System.currentTimeMillis();
/* 500 */         int j = (paramInt2 == 0) ? 1 : 0;
/*     */         while (true)
/*     */         {
/*     */           try
/*     */           {
/* 507 */             super.wait(paramInt2);
/*     */           }
/*     */           catch (InterruptedException localInterruptedException)
/*     */           {
/*     */           }
/*     */ 
/* 514 */           if (this._maxCacheSize - this._currentCacheSize >= i) {
/*     */             break;
/*     */           }
/*     */ 
/* 518 */           long l2 = System.currentTimeMillis();
/* 519 */           if (j == 0)
/*     */           {
/* 521 */             paramInt2 -= (int)(l2 - l1);
/* 522 */             l1 = l2;
/*     */           }
/* 524 */           if (paramInt2 >= 0) {
/*     */             continue;
/*     */           }
/*     */ 
/* 528 */           raiseIOException("IO_CACHE_EXHAUSED");
/*     */         }
/*     */       }
/*     */     }
/*     */     CacheChunk localCacheChunk;
/* 533 */     if ((i == this._chunkSize) && (this._free != null))
/*     */     {
/* 536 */       localCacheChunk = this._free;
/* 537 */       this._free = this._free._next;
/* 538 */       localCacheChunk._length = 0;
/* 539 */       localCacheChunk._next = null;
/*     */     }
/*     */     else
/*     */     {
/* 544 */       byte[] arrayOfByte = new byte[i];
/* 545 */       localCacheChunk = new CacheChunk(arrayOfByte);
/*     */     }
/* 547 */     this._currentCacheSize += i;
/* 548 */     return localCacheChunk;
/*     */   }
/*     */ 
/*     */   protected synchronized void putChunks(CacheChunk paramCacheChunk)
/*     */   {
/* 559 */     CacheChunk localCacheChunk1 = null;
/* 560 */     for (CacheChunk localCacheChunk2 = paramCacheChunk; localCacheChunk2 != null; localCacheChunk2 = localCacheChunk1)
/*     */     {
/* 562 */       localCacheChunk1 = localCacheChunk2._next;
/* 563 */       if (!this._resettingChunkSize)
/*     */       {
/* 566 */         this._currentCacheSize -= localCacheChunk2._buf.length;
/* 567 */         super.notify();
/*     */       }
/* 569 */       if (localCacheChunk2._buf.length == this._chunkSize)
/*     */       {
/* 574 */         localCacheChunk2._next = this._free;
/* 575 */         this._free = localCacheChunk2;
/*     */       }
/*     */       else
/*     */       {
/* 580 */         localCacheChunk2._next = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static void raiseIOException(String paramString)
/*     */     throws IOException
/*     */   {
/*     */     String str;
/*     */     try
/*     */     {
/* 595 */       str = _messages.getString(paramString);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 602 */       str = "Internal error, missing message for: " + paramString + " exception: " + localException.toString();
/*     */     }
/*     */ 
/* 605 */     throw new IOException(str);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 119 */       _messages = ResourceBundle.getBundle("com.sybase.jdbc3.utils.resource.Messages");
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.CacheManager
 * JD-Core Version:    0.5.4
 */