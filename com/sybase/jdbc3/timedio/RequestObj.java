/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.SyncObj;
/*     */ import com.sybase.jdbc3.utils.SyncQueue;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class RequestObj extends SyncObj
/*     */ {
/*     */   protected SyncObj _guestOf;
/*     */ 
/*     */   public RequestObj(SyncQueue paramSyncQueue)
/*     */   {
/*  36 */     super(paramSyncQueue);
/*  37 */     this._guestOf = null;
/*     */   }
/*     */ 
/*     */   public int giveToMe(long paramLong)
/*     */   {
/*  43 */     RequestObj localRequestObj = (RequestObj)getOwner();
/*  44 */     int i = 10;
/*     */ 
/*  57 */     if ((localRequestObj != null) && (ownerIsMeOrASponsor(localRequestObj)))
/*     */     {
/*  60 */       i = 12;
/*     */     }
/*     */     else
/*     */     {
/*  65 */       i = super.giveToMe(paramLong);
/*     */     }
/*  67 */     return i;
/*     */   }
/*     */ 
/*     */   public void giveToNext()
/*     */   {
/*  78 */     this._guestOf = null;
/*  79 */     super.giveToNext();
/*     */   }
/*     */ 
/*     */   public boolean validate() throws SQLException
/*     */   {
/*  84 */     RequestObj localRequestObj = (RequestObj)getOwner();
/*  85 */     return ownerIsMeOrASponsor(localRequestObj);
/*     */   }
/*     */ 
/*     */   public String whoAmI()
/*     */   {
/*  90 */     return super.whoAmI() + ", guestOf= " + this._guestOf;
/*     */   }
/*     */ 
/*     */   private boolean ownerIsMeOrASponsor(RequestObj paramRequestObj)
/*     */   {
/*  99 */     if (paramRequestObj == this)
/*     */     {
/* 101 */       return true;
/*     */     }
/* 103 */     if ((this._guestOf != null) && (this._guestOf != this))
/*     */     {
/* 105 */       return ((RequestObj)this._guestOf).ownerIsMeOrASponsor(paramRequestObj);
/*     */     }
/* 107 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.RequestObj
 * JD-Core Version:    0.5.4
 */