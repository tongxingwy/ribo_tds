/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import com.sybase.jdbcx.SybMessageHandler;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public abstract class ProtocolContext
/*    */ {
/* 43 */   public int _timeout = 0;
/*    */   public long _requestStartTime;
/* 52 */   public int _maxFieldSize = 0;
/*    */   public InputStream _is;
/*    */   public Protocol _protocol;
/*    */   public SybConnection _conn;
/* 64 */   protected SybMessageHandler _msgHandler = null;
/*    */   public boolean _batch;
/*    */   public boolean _batchReadAhead;
/*    */   public static final int IDLE = 1;
/*    */   public static final int BUSY = 2;
/*    */   public static final int CACHED = 3;
/*    */   public static final int CANCELLING = 4;
/*    */   public static final int CANCELLED = 5;
/*    */   public static final int CANCELSLURP = 6;
/*    */ 
/*    */   public void setMessageHandler(SybMessageHandler paramSybMessageHandler)
/*    */   {
/* 85 */     this._msgHandler = paramSybMessageHandler;
/*    */   }
/*    */ 
/*    */   public SybMessageHandler getMessageHandler()
/*    */   {
/* 91 */     return this._msgHandler;
/*    */   }
/*    */ 
/*    */   public ProtocolContext(Protocol paramProtocol)
/*    */   {
/* 97 */     this._protocol = paramProtocol;
/*    */   }
/*    */ 
/*    */   public abstract void drop();
/*    */ 
/*    */   public abstract int getState();
/*    */ 
/*    */   public abstract void setSponsor(ProtocolContext paramProtocolContext);
/*    */ 
/*    */   public abstract int getTimeLeft();
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.ProtocolContext
 * JD-Core Version:    0.5.4
 */