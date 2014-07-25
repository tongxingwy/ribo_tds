/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.SybConnection;
/*     */ import com.sybase.jdbc3.jdbc.SybResultSet;
/*     */ import com.sybase.jdbcx.SybEventHandler;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ 
/*     */ public class EventThread
/*     */   implements Runnable
/*     */ {
/*     */   protected boolean _threadState;
/*     */   private TdsEventContext _tec;
/*     */   private SybEventHandler _hdlr;
/*     */   private String _procName;
/*     */   protected int _eventCount;
/*     */   private SybResultSet _params;
/*  35 */   private volatile boolean _running = true;
/*     */ 
/*     */   protected EventThread(TdsEventContext paramTdsEventContext)
/*     */   {
/*  40 */     this._tec = paramTdsEventContext;
/*  41 */     this._eventCount = 0;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  46 */     if (!this._running)
/*     */       label0: return;
/*  48 */     synchronized (this)
/*     */     {
/*  50 */       if (this._eventCount == 0)
/*     */       {
/*  52 */         this._threadState = true;
/*     */         try
/*     */         {
/*  57 */           super.wait();
/*     */         }
/*     */         catch (InterruptedException localInterruptedException) {
/*     */         }
/*  61 */         break label0:
/*     */ 
/*  63 */         this._threadState = false;
/*     */       }
/*     */     }
/*     */ 
/*  67 */     this._procName = null;
/*  68 */     this._params = null;
/*  69 */     this._hdlr = null;
/*     */     try
/*     */     {
/*  72 */       this._tec.beginRequest();
/*  73 */       this._procName = this._tec.getProcName();
/*  74 */       this._params = this._tec.getParams();
/*  75 */       this._hdlr = this._tec.getHandler(this._procName);
/*     */     }
/*     */     catch (SQLException )
/*     */     {
/*  79 */       this._tec.chainException(???);
/*     */     }
/*  81 */     if (this._hdlr == null)
/*     */     {
/*  84 */       this._tec._conn.chainWarnings(this._tec._chainedSqe);
/*  85 */       this._tec._chainedSqe = null;
/*     */ 
/*  87 */       synchronized (this)
/*     */       {
/*  89 */         this._eventCount -= 1;
/*     */       }
/*     */     }
/*     */ 
/*  93 */     if (this._params == null)
/*     */     {
/*  95 */       this._params = this._tec.makeEmptyParams();
/*     */     }
/*     */ 
/*  99 */     SQLWarning localSQLWarning = SybConnection.convertToWarnings(this._tec._chainedSqe);
/* 100 */     if (this._tec._trsForEvent._warning == null)
/*     */     {
/* 102 */       this._tec._trsForEvent._warning = localSQLWarning;
/*     */     }
/*     */     else
/*     */     {
/* 106 */       this._tec._trsForEvent._warning.setNextWarning(localSQLWarning);
/*     */     }
/*     */ 
/* 109 */     this._hdlr.event(this._procName, this._params);
/*     */     try
/*     */     {
/* 113 */       this._params.close();
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */ 
/* 119 */     synchronized (this)
/*     */     {
/* 121 */       this._eventCount -= 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 128 */     this._running = false;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.EventThread
 * JD-Core Version:    0.5.4
 */