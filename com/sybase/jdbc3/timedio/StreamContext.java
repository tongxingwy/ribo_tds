/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.Protocol;
/*     */ import com.sybase.jdbc3.jdbc.ProtocolContext;
/*     */ import com.sybase.jdbc3.utils.BufferInterval;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public abstract class StreamContext extends ProtocolContext
/*     */ {
/*     */   protected int _state;
/*     */   protected int _pduState;
/*     */   protected ResponseQueue _responseQue;
/*     */   protected RequestObj _request;
/*     */   protected InStreamMgr _inMgr;
/*     */ 
/*     */   public StreamContext(String paramString, Protocol paramProtocol, InStreamMgr paramInStreamMgr, OutStreamMgr paramOutStreamMgr)
/*     */   {
/*  40 */     super(paramProtocol);
/*  41 */     this._inMgr = paramInStreamMgr;
/*  42 */     this._state = 1;
/*  43 */     this._request = new RequestObj(paramOutStreamMgr.getWaitQueue());
/*  44 */     this._responseQue = new ResponseQueue(paramInStreamMgr.getReadQueue(), 10);
/*     */   }
/*     */ 
/*     */   protected void refreshYourself(Protocol paramProtocol, InStreamMgr paramInStreamMgr, OutStreamMgr paramOutStreamMgr)
/*     */   {
/*  53 */     this._inMgr = paramInStreamMgr;
/*  54 */     this._state = 1;
/*  55 */     this._request = new RequestObj(paramOutStreamMgr.getWaitQueue());
/*  56 */     this._responseQue = new ResponseQueue(paramInStreamMgr.getReadQueue(), 10);
/*     */   }
/*     */ 
/*     */   protected abstract void close(boolean paramBoolean);
/*     */ 
/*     */   protected abstract void chainException(SQLException paramSQLException);
/*     */ 
/*     */   protected abstract void beginRequest()
/*     */     throws SQLException;
/*     */ 
/*     */   protected abstract BufferInterval queueData(BufferInterval paramBufferInterval);
/*     */ 
/*     */   public void setSponsor(ProtocolContext paramProtocolContext)
/*     */   {
/*  89 */     if (paramProtocolContext != null)
/*     */     {
/*  91 */       StreamContext localStreamContext = (StreamContext)paramProtocolContext;
/*  92 */       this._request._guestOf = localStreamContext._request;
/*     */     }
/*     */     else
/*     */     {
/* 100 */       this._request._guestOf = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int responseState()
/*     */   {
/* 112 */     return this._pduState;
/*     */   }
/*     */ 
/*     */   protected abstract void cancelled();
/*     */ 
/*     */   protected BufferInterval makeChunk(BufferInterval paramBufferInterval, int paramInt, boolean paramBoolean)
/*     */   {
/* 125 */     if (paramInt == 0)
/*     */     {
/* 127 */       return paramBufferInterval;
/*     */     }
/* 129 */     BufferInterval localBufferInterval = null;
/* 130 */     if (paramInt < paramBufferInterval._length)
/*     */     {
/* 133 */       localBufferInterval = paramBufferInterval.divide(paramInt);
/*     */     }
/*     */     else
/*     */     {
/* 138 */       localBufferInterval = paramBufferInterval;
/* 139 */       paramBufferInterval = null;
/*     */     }
/* 141 */     if (localBufferInterval != null)
/*     */     {
/* 143 */       if (paramBoolean)
/*     */       {
/* 145 */         if (localBufferInterval.free())
/*     */         {
/* 147 */           localBufferInterval = null;
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 153 */         this._responseQue.push(localBufferInterval);
/*     */       }
/*     */     }
/* 156 */     return paramBufferInterval;
/*     */   }
/*     */ 
/*     */   protected void setState(int paramInt)
/*     */   {
/* 164 */     this._state = paramInt;
/*     */   }
/*     */ 
/*     */   public int getState()
/*     */   {
/* 169 */     return this._state;
/*     */   }
/*     */ 
/*     */   protected int getTimeUntilTimeout() throws IOException
/*     */   {
/* 174 */     return this._timeout;
/*     */   }
/*     */ 
/*     */   public int getTimeLeft()
/*     */   {
/* 179 */     return this._timeout;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.StreamContext
 * JD-Core Version:    0.5.4
 */