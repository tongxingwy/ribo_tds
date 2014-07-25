/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.BufferInterval;
/*     */ import com.sybase.jdbc3.utils.Queue;
/*     */ import com.sybase.jdbc3.utils.SyncObj;
/*     */ import com.sybase.jdbc3.utils.SyncQueue;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class ResponseQueue extends SyncObj
/*     */ {
/*     */   Queue _responseQ;
/*     */ 
/*     */   public ResponseQueue(SyncQueue paramSyncQueue, int paramInt)
/*     */   {
/*  34 */     super(paramSyncQueue);
/*  35 */     this._responseQ = new Queue(paramInt, paramInt);
/*     */   }
/*     */ 
/*     */   public synchronized BufferInterval pop()
/*     */   {
/*  40 */     BufferInterval localBufferInterval = null;
/*  41 */     localBufferInterval = (BufferInterval)this._responseQ.popNoEx();
/*  42 */     if ((localBufferInterval == null) && 
/*  45 */       (this._state != 13))
/*     */     {
/*  47 */       this._state = 10;
/*     */     }
/*     */ 
/*  50 */     return localBufferInterval;
/*     */   }
/*     */ 
/*     */   public synchronized void push(BufferInterval paramBufferInterval)
/*     */   {
/*  55 */     this._responseQ.push(paramBufferInterval);
/*     */ 
/*  58 */     super.notifyAll();
/*  59 */     release();
/*     */   }
/*     */ 
/*     */   public synchronized int available(int paramInt)
/*     */   {
/*  64 */     int i = 0;
/*     */ 
/*  66 */     this._responseQ.reset();
/*     */     while (true)
/*     */     {
/*     */       try
/*     */       {
/*  71 */         BufferInterval localBufferInterval = (BufferInterval)this._responseQ.next();
/*  72 */         i += localBufferInterval._length - paramInt;
/*     */       }
/*     */       catch (NoSuchElementException localNoSuchElementException)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  81 */     return i;
/*     */   }
/*     */ 
/*     */   public void dump()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void setNeed()
/*     */   {
/* 120 */     this._state = 10;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.ResponseQueue
 * JD-Core Version:    0.5.4
 */