/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class CacheStream extends InputStream
/*     */ {
/*     */   private static final int LOW_BYTE = 255;
/*     */   CacheManager _cm;
/*     */   InputStream _is;
/*  45 */   boolean _dead = false;
/*  46 */   boolean _rewindable = true;
/*  47 */   private boolean _reset = false;
/*     */ 
/*  50 */   CacheChunk _first = null;
/*  51 */   CacheChunk _last = null;
/*     */ 
/*  53 */   CacheChunk _current = null;
/*     */   int _timeout;
/*  55 */   int _nextByte = 0;
/*     */ 
/*     */   public CacheStream(CacheManager paramCacheManager, InputStream paramInputStream, int paramInt)
/*     */   {
/*  60 */     this._cm = paramCacheManager;
/*  61 */     this._is = paramInputStream;
/*  62 */     this._timeout = paramInt;
/*     */   }
/*     */ 
/*     */   public synchronized int read()
/*     */     throws IOException
/*     */   {
/*  76 */     checkMe();
/*  77 */     if ((this._current == null) || ((this._nextByte == this._current._length) && (this._current._next == null) && (this._current._length == this._current._buf.length)))
/*     */     {
/*  85 */       addChunk(1);
/*     */     }
/*  87 */     else if ((this._nextByte == this._current._length) && (this._current._next != null))
/*     */     {
/*  92 */       this._current = this._current._next;
/*  93 */       this._nextByte = 0;
/*     */     }
/*  95 */     if (this._nextByte == this._current._length)
/*     */     {
/* 105 */       i = this._is.read();
/* 106 */       if (i == -1)
/*     */       {
/* 111 */         return -1;
/*     */       }
/* 113 */       this._current._length += 1;
/* 114 */       this._current._buf[this._nextByte] = (byte)(i & 0xFF);
/*     */     }
/*     */ 
/* 121 */     int i = this._current._buf[(this._nextByte++)] & 0xFF;
/* 122 */     return i;
/*     */   }
/*     */ 
/*     */   public synchronized int read(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 132 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 147 */     checkMe();
/* 148 */     int i = paramInt2;
/* 149 */     while (i > 0)
/*     */     {
/* 151 */       if (this._current == null)
/*     */       {
/* 156 */         addChunk(i);
/*     */       }
/* 158 */       else if (this._nextByte == this._current._length)
/*     */       {
/* 161 */         if ((this._current._length == this._current._buf.length) && (this._current._next == null))
/*     */         {
/* 167 */           addChunk(i);
/*     */         }
/* 169 */         else if (this._current._next != null)
/*     */         {
/* 171 */           this._current = this._current._next;
/* 172 */           this._nextByte = 0;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 179 */       int j = this._current._length - this._nextByte;
/*     */ 
/* 182 */       int k = (i < j) ? i : j;
/* 183 */       if (j == 0)
/*     */       {
/* 188 */         int l = this._current._buf.length - this._current._length;
/*     */ 
/* 192 */         k = (l > i) ? i : l;
/*     */ 
/* 199 */         k = this._is.read(this._current._buf, this._current._length, k);
/* 200 */         this._current._length += k;
/*     */       }
/*     */ 
/* 205 */       System.arraycopy(this._current._buf, this._nextByte, paramArrayOfByte, paramInt1, k);
/* 206 */       paramInt1 += k;
/* 207 */       i -= k;
/* 208 */       this._nextByte += k;
/*     */     }
/* 210 */     return paramInt2;
/*     */   }
/*     */ 
/*     */   public synchronized int available()
/*     */     throws IOException
/*     */   {
/* 221 */     checkMe();
/* 222 */     if (this._current == null) return 0;
/* 223 */     CacheChunk localCacheChunk = this._current;
/* 224 */     int i = localCacheChunk._buf.length - this._nextByte;
/* 225 */     while (localCacheChunk._next != null)
/*     */     {
/* 227 */       localCacheChunk = localCacheChunk._next;
/* 228 */       i += localCacheChunk._buf.length;
/*     */     }
/*     */ 
/* 231 */     return i;
/*     */   }
/*     */ 
/*     */   public synchronized long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 246 */     checkMe();
/* 247 */     int i = 0;
/* 248 */     long l = paramLong;
/* 249 */     while (l > 0L)
/*     */     {
/* 251 */       if (this._current != null)
/*     */       {
/* 254 */         int j = this._current._length - this._nextByte;
/* 255 */         if (j <= l)
/*     */         {
/* 258 */           this._current = this._current._next;
/* 259 */           this._nextByte = 0;
/* 260 */           ++i;
/* 261 */           l -= j;
/*     */         }
/*     */ 
/* 266 */         this._nextByte = (int)(this._nextByte + l);
/* 267 */         break;
/*     */       }
/*     */ 
/* 275 */       this._rewindable = false;
/* 276 */       l -= this._is.skip(l);
/*     */     }
/*     */ 
/* 283 */     return paramLong;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */     throws IOException
/*     */   {
/* 293 */     checkMe();
/* 294 */     this._dead = true;
/* 295 */     this._cm.putChunks(this._first);
/* 296 */     this._first = null;
/* 297 */     this._current = null;
/* 298 */     this._last = null;
/* 299 */     this._is = null;
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */     throws IOException
/*     */   {
/* 311 */     checkMe();
/* 312 */     if (!this._rewindable)
/*     */     {
/* 314 */       CacheManager.raiseIOException("IO_NOT_RESETABLE");
/*     */     }
/* 316 */     this._current = this._first;
/* 317 */     this._nextByte = 0;
/*     */ 
/* 319 */     this._reset = true;
/*     */   }
/*     */ 
/*     */   public long cacheSize()
/*     */   {
/* 328 */     int i = 0;
/* 329 */     for (CacheChunk localCacheChunk = this._first; localCacheChunk != null; localCacheChunk = localCacheChunk._next)
/*     */     {
/* 331 */       i += localCacheChunk._length;
/*     */     }
/*     */ 
/* 335 */     return i;
/*     */   }
/*     */ 
/*     */   private void addChunk(int paramInt)
/*     */     throws IOException
/*     */   {
/* 343 */     CacheChunk localCacheChunk = this._cm.getChunk(paramInt, this._timeout);
/* 344 */     if (this._last == null)
/*     */     {
/* 347 */       this._first = localCacheChunk;
/*     */     }
/*     */     else
/*     */     {
/* 351 */       this._last._next = localCacheChunk;
/*     */     }
/* 353 */     this._last = localCacheChunk;
/* 354 */     this._current = localCacheChunk;
/* 355 */     this._nextByte = 0;
/*     */   }
/*     */ 
/*     */   private void checkMe()
/*     */     throws IOException
/*     */   {
/* 362 */     if (!this._dead)
/*     */       return;
/* 364 */     CacheManager.raiseIOException("IO_CLOSED");
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.CacheStream
 * JD-Core Version:    0.5.4
 */