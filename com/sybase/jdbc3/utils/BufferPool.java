/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import com.sybase.jdbc3.timedio.InStreamMgr;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class BufferPool
/*     */ {
/*     */   public static final int BUFF_SIZE = 4096;
/*     */   private BufferInterval _head;
/*     */   private int _bufSize;
/*     */   private int _packetSize;
/*     */   private InStreamMgr _inMgr;
/*     */   private boolean _noLimit;
/*     */   protected static final boolean DEBUG = false;
/*  40 */   private volatile long _inMemoryBICount = 0L;
/*     */ 
/*     */   public BufferPool(InStreamMgr paramInStreamMgr, SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/*  45 */     this._inMgr = paramInStreamMgr;
/*  46 */     this._noLimit = (paramSybProperty.getInteger(13) <= 0);
/*  47 */     this._packetSize = paramSybProperty.getInteger(12);
/*  48 */     this._bufSize = paramSybProperty.getBufferSize();
/*  49 */     this._head = makeBuffer();
/*     */   }
/*     */ 
/*     */   public void setNetBufSize(int paramInt)
/*     */   {
/*  61 */     if (this._packetSize == paramInt)
/*     */     {
/*  63 */       return;
/*     */     }
/*  65 */     this._packetSize = paramInt;
/*  66 */     if ((this._bufSize < 2 * paramInt) && (!this._noLimit))
/*     */     {
/*  68 */       synchronized (this)
/*     */       {
/*  74 */         while (this._head != null)
/*     */         {
/*  76 */           BufferInterval localBufferInterval1 = this._head;
/*  77 */           if (localBufferInterval1._length != this._bufSize) {
/*     */             break;
/*     */           }
/*     */ 
/*  81 */           BufferInterval localBufferInterval2 = this._head._next;
/*  82 */           this._head.detach();
/*  83 */           this._head = localBufferInterval2;
/*     */         }
/*     */ 
/*  91 */         this._bufSize = (2 * paramInt);
/*  92 */         put(makeBuffer());
/*     */       }
/*     */     } else {
/*  95 */       if (this._bufSize >= 2 * paramInt)
/*     */         return;
/*  97 */       synchronized (this)
/*     */       {
/*  99 */         this._bufSize = (2 * paramInt);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean put(BufferInterval paramBufferInterval)
/*     */   {
/* 111 */     if (this._noLimit)
/*     */     {
/* 114 */       return true;
/*     */     }
/* 116 */     int i = 0;
/*     */ 
/* 121 */     if (i == 1)
/*     */     {
/* 123 */       checkMgr();
/* 124 */       return false;
/*     */     }
/* 126 */     i = 1;
/*     */ 
/* 128 */     synchronized (this)
/*     */     {
/* 131 */       if (paramBufferInterval == null)
/*     */       {
/* 133 */         return false;
/*     */       }
/*     */ 
/* 136 */       if (this._head == null)
/*     */       {
/* 138 */         this._head = paramBufferInterval;
/*     */       }
/*     */ 
/* 142 */       boolean bool = false;
/* 143 */       for (Object localObject1 = this._head; localObject1 != null; localObject1 = ((BufferInterval)localObject1)._next)
/*     */       {
/* 147 */         if (!((BufferInterval)localObject1).merge(paramBufferInterval, bool)) {
/*     */           continue;
/*     */         }
/* 150 */         if (bool)
/*     */         {
/* 153 */           ((BufferInterval)localObject1).detach();
/*     */ 
/* 156 */           break;
/*     */         }
/*     */ 
/* 159 */         paramBufferInterval = (BufferInterval)localObject1;
/* 160 */         bool = true;
/*     */       }
/*     */ 
/* 166 */       if (bool)
/*     */       {
/* 170 */         if ((paramBufferInterval._prev != null) && (paramBufferInterval._prev._length < paramBufferInterval._length));
/* 177 */         paramBufferInterval.detach();
/*     */       }
/*     */ 
/* 182 */       localObject1 = null;
/* 183 */       for (BufferInterval localBufferInterval = this._head; localBufferInterval != null; localBufferInterval = localBufferInterval._next)
/*     */       {
/* 185 */         if (paramBufferInterval._length >= localBufferInterval._length)
/*     */         {
/* 187 */           if (localBufferInterval == this._head)
/*     */           {
/* 189 */             this._head = paramBufferInterval;
/*     */           }
/* 191 */           paramBufferInterval.attachBefore(localBufferInterval);
/*     */         }
/*     */ 
/* 194 */         localObject1 = localBufferInterval;
/*     */       }
/* 196 */       paramBufferInterval.attachAfter((BufferInterval)localObject1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkMgr()
/*     */   {
/* 205 */     synchronized (this._inMgr)
/*     */     {
/* 207 */       if ((this._head != null) && (this._head._length >= this._packetSize))
/*     */       {
/* 209 */         this._inMgr.notify();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized BufferInterval getBI(boolean paramBoolean)
/*     */   {
/* 229 */     if ((this._head == null) || (this._head._length < this._packetSize))
/*     */     {
/* 231 */       if ((paramBoolean) || (this._noLimit))
/*     */       {
/* 233 */         return makeBuffer();
/*     */       }
/* 235 */       return null;
/*     */     }
/* 237 */     BufferInterval localBufferInterval1 = this._head;
/* 238 */     BufferInterval localBufferInterval2 = this._head._next;
/* 239 */     this._head.detach();
/* 240 */     this._head = localBufferInterval2;
/*     */ 
/* 243 */     return localBufferInterval1;
/*     */   }
/*     */ 
/*     */   private BufferInterval makeBuffer()
/*     */   {
/* 248 */     byte[] arrayOfByte = new byte[this._bufSize];
/* 249 */     BufferInterval localBufferInterval = new BufferInterval(arrayOfByte, 0, arrayOfByte.length, this);
/* 250 */     incrementInMemoryBICount();
/* 251 */     return localBufferInterval;
/*     */   }
/*     */ 
/*     */   public synchronized void incrementInMemoryBICount()
/*     */   {
/* 256 */     this._inMemoryBICount += 1L;
/*     */   }
/*     */ 
/*     */   public synchronized void decrementInMemoryBICount()
/*     */   {
/* 261 */     if (this._inMemoryBICount <= 0L)
/*     */       return;
/* 263 */     this._inMemoryBICount -= 1L;
/*     */   }
/*     */ 
/*     */   public long getInMemoryBICount()
/*     */   {
/* 269 */     return this._inMemoryBICount;
/*     */   }
/*     */ 
/*     */   public synchronized void resetInMemoryBICount()
/*     */   {
/* 274 */     this._inMemoryBICount = 0L;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.BufferPool
 * JD-Core Version:    0.5.4
 */