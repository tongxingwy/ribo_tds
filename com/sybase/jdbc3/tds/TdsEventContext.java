/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.Protocol;
/*     */ import com.sybase.jdbc3.jdbc.SybCallableStatement;
/*     */ import com.sybase.jdbc3.jdbc.SybConnection;
/*     */ import com.sybase.jdbc3.timedio.InStreamMgr;
/*     */ import com.sybase.jdbc3.timedio.OutStreamMgr;
/*     */ import com.sybase.jdbcx.SybEventHandler;
/*     */ import java.io.IOException;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class TdsEventContext extends TdsProtocolContext
/*     */ {
/*     */   private Thread _runner;
/*     */   private EventThread _evtThrd;
/*     */   private Hashtable _events;
/*     */ 
/*     */   public TdsEventContext(Protocol paramProtocol, InStreamMgr paramInStreamMgr, OutStreamMgr paramOutStreamMgr)
/*     */     throws SQLException
/*     */   {
/*  49 */     super(null, paramProtocol, paramInStreamMgr, paramOutStreamMgr);
/*  50 */     this._runner = null;
/*  51 */     this._evtThrd = null;
/*  52 */     this._event = true;
/*  53 */     paramInStreamMgr.setEventContext(this);
/*     */   }
/*     */ 
/*     */   protected void addHandler(String paramString, SybEventHandler paramSybEventHandler, int paramInt)
/*     */     throws SQLException
/*     */   {
/*  59 */     checkEventTable();
/*     */ 
/*  64 */     this._events.put(paramString, paramSybEventHandler);
/*     */ 
/*  67 */     SybCallableStatement localSybCallableStatement = (SybCallableStatement)this._conn.prepareInternalCall("{call sp_regwatch(?, ?)}");
/*     */ 
/*  69 */     localSybCallableStatement.setString(1, paramString);
/*     */ 
/*  71 */     short s = (short)(paramInt | 0x40);
/*  72 */     localSybCallableStatement.setShort(2, s);
/*     */ 
/*  75 */     localSybCallableStatement.executeUpdate();
/*  76 */     localSybCallableStatement.close();
/*     */   }
/*     */ 
/*     */   protected void dropHandler(String paramString)
/*     */     throws SQLException
/*     */   {
/*  82 */     checkEventTable();
/*  83 */     CallableStatement localCallableStatement = this._conn.prepareInternalCall("{call sp_regnowatch(?)}");
/*  84 */     localCallableStatement.setString(1, paramString);
/*  85 */     localCallableStatement.executeUpdate();
/*     */ 
/*  88 */     this._events.remove(paramString);
/*     */   }
/*     */ 
/*     */   private synchronized void checkEventTable() throws SQLException
/*     */   {
/*  93 */     if (this._events != null) {
/*     */       return;
/*     */     }
/*     */ 
/*  97 */     this._evtThrd = new EventThread(this);
/*  98 */     this._runner = new Thread(this._evtThrd);
/*     */     try
/*     */     {
/* 101 */       this._runner.start();
/*     */     }
/*     */     catch (IllegalThreadStateException localIllegalThreadStateException)
/*     */     {
/* 105 */       ErrorMessage.raiseError("JZ0H0", localIllegalThreadStateException.toString());
/*     */     }
/*     */ 
/* 109 */     this._events = new Hashtable();
/*     */   }
/*     */ 
/*     */   protected boolean checkBufStat(int paramInt)
/*     */   {
/* 115 */     if ((paramInt & 0x8) == 0)
/*     */     {
/* 118 */       this._pduState = 4;
/* 119 */       return true;
/*     */     }
/* 121 */     return false;
/*     */   }
/*     */ 
/*     */   protected String getProcName()
/*     */     throws SQLException
/*     */   {
/* 128 */     String str = null;
/*     */ 
/* 130 */     int i = this._protocol.nextResult(this);
/*     */ 
/* 133 */     if (this._lastTds == 162)
/*     */     {
/*     */       try
/*     */       {
/* 138 */         EventToken localEventToken = new EventToken(this._in);
/* 139 */         str = localEventToken._name;
/* 140 */         this._lastResult = -1;
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 144 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 150 */       this._protocol.ungetResult(this, this._lastTds);
/* 151 */       ErrorMessage.raiseError("JZ0P4");
/*     */     }
/* 153 */     return str;
/*     */   }
/*     */ 
/*     */   protected SybEventHandler getHandler(String paramString)
/*     */     throws SQLException
/*     */   {
/* 160 */     checkEventTable();
/* 161 */     SybEventHandler localSybEventHandler = (SybEventHandler)this._events.get(paramString);
/* 162 */     if (localSybEventHandler == null)
/*     */     {
/* 164 */       close(false);
/* 165 */       ErrorMessage.raiseError("JZ0H1", paramString);
/*     */     }
/*     */ 
/* 168 */     return localSybEventHandler;
/*     */   }
/*     */ 
/*     */   public int responseState()
/*     */   {
/* 176 */     if (this._pduState == 0)
/*     */     {
/* 178 */       if (this._evtThrd != null)
/*     */       {
/* 180 */         synchronized (this._evtThrd)
/*     */         {
/* 182 */           this._evtThrd._eventCount += 1;
/*     */ 
/* 184 */           if (this._evtThrd._threadState)
/*     */           {
/* 186 */             this._evtThrd.notify();
/*     */           }
/*     */         }
/*     */       }
/* 190 */       this._pduState = 6;
/*     */     }
/* 192 */     return this._pduState;
/*     */   }
/*     */ 
/*     */   public void drop()
/*     */   {
/* 197 */     if (this._runner != null)
/*     */     {
/* 205 */       this._evtThrd.stop();
/* 206 */       this._runner.interrupt();
/*     */       try
/*     */       {
/* 209 */         this._runner.join(1L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException)
/*     */       {
/*     */       }
/*     */     }
/* 215 */     super.drop();
/*     */   }
/*     */ 
/*     */   public void beginRequest()
/*     */   {
/* 223 */     close(false);
/* 224 */     setState(2);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsEventContext
 * JD-Core Version:    0.5.4
 */