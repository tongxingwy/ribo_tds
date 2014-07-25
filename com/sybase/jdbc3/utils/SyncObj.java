/*    */ package com.sybase.jdbc3.utils;
/*    */ 
/*    */ public class SyncObj
/*    */ {
/*    */   protected int _state;
/*    */   private SyncQueue _mgr;
/*    */ 
/*    */   public SyncObj(SyncQueue paramSyncQueue)
/*    */   {
/* 32 */     this._state = 10;
/* 33 */     this._mgr = paramSyncQueue;
/*    */   }
/*    */ 
/*    */   public void release()
/*    */   {
/* 42 */     this._mgr.release(this);
/*    */   }
/*    */ 
/*    */   public int giveToMe(long paramLong)
/*    */   {
/* 51 */     return this._mgr.take(paramLong, this);
/*    */   }
/*    */ 
/*    */   public void giveToNext()
/*    */   {
/* 60 */     this._mgr.releaseNext(this);
/*    */   }
/*    */ 
/*    */   public SyncObj getOwner()
/*    */   {
/* 65 */     return this._mgr.getOwner();
/*    */   }
/*    */ 
/*    */   public String whoAmI() {
/* 69 */     return this + ", _state= " + this._state;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.SyncObj
 * JD-Core Version:    0.5.4
 */