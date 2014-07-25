/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ public class SyncQueue
/*     */ {
/*     */   public static final int NEED = 10;
/*     */   public static final int WAITING = 11;
/*     */   public static final int SATISFIED = 12;
/*     */   public static final int OWNER = 13;
/*     */   public static final int TIMEOUT = 14;
/*     */   private Queue _waitList;
/*  38 */   private SyncObj _owner = null;
/*     */ 
/*     */   public SyncQueue()
/*     */   {
/*  43 */     this._waitList = new Queue();
/*     */   }
/*     */ 
/*     */   public SyncQueue(int paramInt1, int paramInt2)
/*     */   {
/*  48 */     this._waitList = new Queue(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   protected int take(long paramLong, SyncObj paramSyncObj)
/*     */   {
/*  63 */     if (paramSyncObj == this._owner)
/*     */     {
/*  66 */       return paramSyncObj._state;
/*     */     }
/*  68 */     synchronized (paramSyncObj)
/*     */     {
/*  70 */       if (paramSyncObj._state != 10)
/*     */       {
/*  72 */         return paramSyncObj._state;
/*     */       }
/*     */ 
/*  75 */       synchronized (this)
/*     */       {
/*  77 */         if (this._owner == null)
/*     */         {
/*  80 */           this._owner = paramSyncObj;
/*  81 */           paramSyncObj._state = 13;
/*     */ 
/*  84 */           monitorexit; return 13;
/*     */         }
/*     */ 
/*  88 */         paramSyncObj._state = 11;
/*  89 */         this._waitList.push(paramSyncObj);
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/*  95 */         paramSyncObj.wait(paramLong);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException)
/*     */       {
/*     */       }
/*     */ 
/* 102 */       if (paramSyncObj._state == 11)
/*     */       {
/* 106 */         this._waitList.removeElement(paramSyncObj);
/* 107 */         paramSyncObj._state = 14;
/*     */       }
/*     */     }
/*     */ 
/* 111 */     return paramSyncObj._state;
/*     */   }
/*     */ 
/*     */   protected void release(SyncObj paramSyncObj)
/*     */   {
/* 121 */     synchronized (paramSyncObj)
/*     */     {
/* 123 */       switch (paramSyncObj._state)
/*     */       {
/*     */       case 10:
/* 126 */         paramSyncObj._state = 12;
/* 127 */         break;
/*     */       case 11:
/* 129 */         paramSyncObj._state = 12;
/* 130 */         this._waitList.removeElement(paramSyncObj);
/* 131 */         paramSyncObj.notify();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void releaseNext(SyncObj paramSyncObj)
/*     */   {
/* 145 */     if (paramSyncObj != this._owner)
/*     */     {
/* 152 */       return;
/*     */     }
/*     */ 
/* 155 */     this._owner = null;
/*     */ 
/* 157 */     synchronized (paramSyncObj)
/*     */     {
/* 159 */       paramSyncObj._state = 10;
/*     */     }
/*     */ 
/* 162 */     ??? = null;
/*     */ 
/* 165 */     ??? = (SyncObj)this._waitList.popNoEx();
/* 166 */     if (??? == null)
/*     */     {
/*     */       return;
/*     */     }
/*     */ 
/* 171 */     synchronized (???)
/*     */     {
/* 173 */       if (((SyncObj)???)._state == 11)
/*     */       {
/* 175 */         ((SyncObj)???)._state = 13;
/* 176 */         this._owner = ((SyncObj)???);
/* 177 */         ???.notify();
/* 178 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized SyncObj getOwner()
/*     */   {
/* 186 */     return this._owner;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 191 */     return this._waitList.empty();
/*     */   }
/*     */ 
/*     */   public SyncObj dump()
/*     */   {
/* 229 */     return this._owner;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.SyncQueue
 * JD-Core Version:    0.5.4
 */