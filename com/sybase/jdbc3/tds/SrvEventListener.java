/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class SrvEventListener
/*    */ {
/*    */   private SrvSession _session;
/*    */   protected int _options;
/*    */   private Hashtable _list;
/*    */ 
/*    */   protected SrvEventListener(SrvSession paramSrvSession, int paramInt, Hashtable paramHashtable)
/*    */   {
/* 41 */     this._session = paramSrvSession;
/* 42 */     this._list = paramHashtable;
/*    */ 
/* 52 */     this._options = paramInt;
/* 53 */     this._session._events.addElement(this);
/* 54 */     this._list.put(this._session, this);
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/* 59 */     this._list.remove(this._session);
/* 60 */     this._session = null;
/* 61 */     this._list = null;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvEventListener
 * JD-Core Version:    0.5.4
 */