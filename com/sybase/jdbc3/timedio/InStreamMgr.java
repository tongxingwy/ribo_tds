/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.Capture;
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.SybConnection;
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import com.sybase.jdbc3.tds.Tds;
/*     */ import com.sybase.jdbc3.utils.BufferInterval;
/*     */ import com.sybase.jdbc3.utils.BufferPool;
/*     */ import com.sybase.jdbc3.utils.SyncObj;
/*     */ import com.sybase.jdbc3.utils.SyncQueue;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.util.EmptyStackException;
/*     */ 
/*     */ public class InStreamMgr
/*     */ {
/*     */   public static final int RESPONSE_DONE = 0;
/*     */   public static final int RESPONSE_MORE = 1;
/*     */   public static final int PARTIAL_PACKET = 2;
/*     */   public static final int EVENT = 3;
/*     */   public static final int ERROR = 4;
/*     */   public static final int RESPONSE_OPEN = 5;
/*     */   public static final int EVENT_DONE = 6;
/*     */   public static final int MIGRATE = 7;
/*  63 */   private Dbio _dbio = null;
/*     */   private OutStreamMgr _outMgr;
/*     */   private BufferPool _pool;
/*     */   private StreamContext _currentCtx;
/*     */   private StreamContext _eventCtx;
/*     */   private StreamContext _migrateCtx;
/*     */   private boolean _closing;
/*     */   public boolean _migrating;
/*     */   private int _cacheSize;
/*  72 */   private boolean _cancelling = false;
/*  73 */   private boolean _serialize = false;
/*     */ 
/*  76 */   BufferInterval _partialBI = null;
/*     */   private SyncQueue _readList;
/*     */   private SyncObj _readerThd;
/*  81 */   private boolean _readAhead = false;
/*     */   private SQLWarning _capSqw;
/*     */   public Tds _tds;
/*  86 */   public boolean _tracing = false;
/*     */ 
/*     */   public InStreamMgr(String paramString, int paramInt1, SybProperty paramSybProperty, int paramInt2)
/*     */     throws IOException, SQLException
/*     */   {
/*  98 */     paramInt2 *= 1000;
/*     */ 
/* 101 */     this._readList = new SyncQueue(6, 6);
/* 102 */     this._readerThd = new SyncObj(this._readList);
/*     */ 
/* 105 */     this._cacheSize = paramSybProperty.getInteger(13);
/* 106 */     this._serialize = paramSybProperty.getBoolean(29);
/* 107 */     this._pool = new BufferPool(this, paramSybProperty);
/*     */ 
/* 109 */     this._dbio = Dbio.connect(paramString, paramInt1, this, paramSybProperty);
/*     */ 
/* 111 */     this._dbio.doConnect(paramInt2);
/* 112 */     this._closing = false;
/*     */     try
/*     */     {
/* 116 */       this._dbio.createCapture(paramSybProperty);
/*     */     }
/*     */     catch (SQLWarning localSQLWarning)
/*     */     {
/* 120 */       this._capSqw = localSQLWarning;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SQLWarning getCaptureWarnings()
/*     */   {
/* 130 */     return this._capSqw;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */   {
/* 139 */     return this._dbio.getOutputStream();
/*     */   }
/*     */ 
/*     */   public void setNetBufSize(int paramInt)
/*     */   {
/* 147 */     this._pool.setNetBufSize(paramInt);
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */   {
/* 156 */     return this._dbio.getInputStream();
/*     */   }
/*     */ 
/*     */   public void closing()
/*     */   {
/* 164 */     this._dbio.closing();
/*     */   }
/*     */ 
/*     */   public void markDead()
/*     */     throws IOException
/*     */   {
/* 171 */     StreamContext localStreamContext = getCurrentContext();
/*     */ 
/* 175 */     if (localStreamContext != null)
/*     */     {
/* 177 */       localStreamContext._conn.markDeadTryHA();
/*     */     }
/*     */     else
/*     */     {
/* 181 */       this._eventCtx._conn.markDeadTryHA();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getSessionID()
/*     */   {
/* 189 */     return this._dbio.getSessionID();
/*     */   }
/*     */ 
/*     */   public void setEventContext(StreamContext paramStreamContext)
/*     */   {
/* 194 */     this._eventCtx = paramStreamContext;
/*     */   }
/*     */ 
/*     */   public void setMigrateContext(StreamContext paramStreamContext)
/*     */   {
/* 199 */     this._migrateCtx = paramStreamContext;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 209 */     if (this._closing)
/*     */       return;
/* 211 */     this._closing = true;
/* 212 */     this._dbio.stopAsync();
/* 213 */     if (this._eventCtx != null)
/*     */     {
/* 215 */       this._eventCtx.drop();
/*     */     }
/* 217 */     if (this._migrateCtx != null)
/*     */     {
/* 219 */       this._migrateCtx.drop();
/*     */     }
/* 221 */     this._dbio.close();
/*     */   }
/*     */ 
/*     */   protected StreamContext currentContext()
/*     */   {
/* 227 */     return this._currentCtx;
/*     */   }
/*     */ 
/*     */   public void migrateDbio(InStreamMgr paramInStreamMgr)
/*     */   {
/* 233 */     this._dbio.close();
/* 234 */     this._dbio = paramInStreamMgr.getDbio();
/* 235 */     this._dbio._ioMgr = this;
/*     */   }
/*     */ 
/*     */   protected Dbio getDbio()
/*     */   {
/* 240 */     return this._dbio;
/*     */   }
/*     */ 
/*     */   public void cancelling(boolean paramBoolean)
/*     */   {
/* 250 */     synchronized (this)
/*     */     {
/* 252 */       this._cancelling = paramBoolean;
/*     */     }
/* 254 */     if (this._cancelling)
/*     */     {
/* 259 */       ??? = getCurrentContext();
/* 260 */       if (??? == null) {
/*     */         return;
/*     */       }
/* 263 */       ((StreamContext)???).cancelled();
/*     */     }
/*     */     else
/*     */     {
/* 270 */       setCurrentContextNull();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean startAsync()
/*     */   {
/* 279 */     if (this._readAhead)
/*     */     {
/* 282 */       return true;
/*     */     }
/*     */ 
/* 285 */     if (13 == this._readerThd.giveToMe(0L))
/*     */     {
/* 287 */       this._readAhead = this._dbio.startAsync();
/* 288 */       if (!this._readAhead)
/*     */       {
/* 291 */         this._readerThd.giveToNext();
/*     */       }
/*     */     }
/* 294 */     return this._readAhead;
/*     */   }
/*     */ 
/*     */   public boolean asyncStarted()
/*     */   {
/* 299 */     return this._readAhead;
/*     */   }
/*     */ 
/*     */   public Capture getCapture()
/*     */   {
/* 304 */     return this._dbio.getCapture();
/*     */   }
/*     */ 
/*     */   protected SyncQueue getReadQueue()
/*     */   {
/* 309 */     return this._readList;
/*     */   }
/*     */ 
/*     */   public void doRead(StreamContext paramStreamContext)
/*     */     throws IOException
/*     */   {
/* 321 */     readIfOwner(takeIfNoReadAhead(paramStreamContext), paramStreamContext);
/*     */   }
/*     */ 
/*     */   public void readInboundData(boolean paramBoolean) throws IOException
/*     */   {
/* 326 */     if ((!paramBoolean) && (((this._readAhead) || (this._currentCtx != null) || (!this._readList.isEmpty()) || (this._dbio instanceof URLDbio) || (this._dbio.getInputStream().available() <= 0)))) {
/*     */       return;
/*     */     }
/*     */ 
/* 330 */     this._dbio.doRead(0L);
/*     */   }
/*     */ 
/*     */   protected SyncObj dump()
/*     */   {
/* 339 */     Object localObject = null;
/*     */ 
/* 354 */     return localObject;
/*     */   }
/*     */ 
/*     */   public void setSerialize()
/*     */   {
/* 362 */     this._serialize = true;
/* 363 */     this._outMgr.setSerialize(this._serialize);
/*     */   }
/*     */ 
/*     */   public BufferInterval getBI()
/*     */   {
/* 369 */     return this._pool.getBI(true);
/*     */   }
/*     */ 
/*     */   protected void setOutStreamMgr(OutStreamMgr paramOutStreamMgr)
/*     */   {
/* 374 */     this._outMgr = paramOutStreamMgr;
/* 375 */     this._outMgr.setSerialize(this._serialize);
/*     */   }
/*     */ 
/*     */   protected void moreData(BufferInterval paramBufferInterval, int paramInt)
/*     */   {
/* 385 */     int i = paramBufferInterval._length - paramInt;
/*     */ 
/* 388 */     if (this._closing)
/*     */     {
/* 390 */       return;
/*     */     }
/*     */ 
/* 393 */     BufferInterval localBufferInterval = null;
/* 394 */     if (i > 0)
/*     */     {
/* 396 */       localBufferInterval = paramBufferInterval.divide(paramInt);
/* 397 */       this._pool.put(paramBufferInterval);
/*     */     }
/*     */     else
/*     */     {
/* 401 */       localBufferInterval = paramBufferInterval;
/*     */     }
/* 403 */     paramBufferInterval = null;
/* 404 */     int j = 0;
/*     */     do
/*     */     {
/* 408 */       StreamContext localStreamContext = getCurrentContext();
/* 409 */       if (localStreamContext != null)
/*     */       {
/* 416 */         localBufferInterval = localStreamContext.queueData(localBufferInterval);
/*     */ 
/* 420 */         j = localStreamContext.responseState();
/*     */       }
/*     */       else
/*     */       {
/* 425 */         localBufferInterval = this._eventCtx.queueData(localBufferInterval);
/* 426 */         j = this._eventCtx.responseState();
/*     */       }
/* 428 */       switch (j)
/*     */       {
/*     */       case 0:
/* 434 */         if (localStreamContext._batchReadAhead)
/*     */         {
/* 436 */           localStreamContext._batchReadAhead = false;
/* 437 */           this._readerThd.giveToNext();
/* 438 */           this._outMgr.endRequest(localStreamContext);
/* 440 */         }setCurrentContextNull();
/* 441 */         break;
/*     */       case 2:
/* 443 */         partialPacket(localBufferInterval);
/* 444 */         return;
/*     */       case 3:
/* 446 */         localBufferInterval = this._eventCtx.queueData(localBufferInterval);
/* 447 */         j = this._eventCtx.responseState();
/* 448 */         break;
/*     */       case 7:
/* 450 */         localBufferInterval = this._migrateCtx.queueData(localBufferInterval);
/* 451 */         j = this._migrateCtx.responseState();
/*     */       case 1:
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/* 455 */       }if (j != 4) {
/*     */         continue;
/*     */       }
/*     */ 
/* 459 */       this._pool.put(localBufferInterval);
/* 460 */       localBufferInterval = null;
/*     */ 
/* 463 */       this._dbio._lastEx = null;
/* 464 */       reportError("JZ0T8");
/* 465 */       return;
/*     */     }
/* 467 */     while (localBufferInterval != null);
/*     */   }
/*     */ 
/*     */   private void partialPacket(BufferInterval paramBufferInterval)
/*     */   {
/* 489 */     this._partialBI = paramBufferInterval;
/*     */   }
/*     */ 
/*     */   protected void setBuffer(long paramLong)
/*     */     throws IOException
/*     */   {
/* 497 */     BufferInterval localBufferInterval = null;
/* 498 */     synchronized (this)
/*     */     {
/* 500 */       localBufferInterval = this._pool.getBI(this._cancelling);
/* 501 */       if (localBufferInterval == null)
/*     */       {
/*     */         try
/*     */         {
/* 507 */           super.wait((int)paramLong);
/*     */         }
/*     */         catch (InterruptedException localInterruptedException)
/*     */         {
/*     */         }
/*     */ 
/* 514 */         localBufferInterval = this._pool.getBI(this._cacheSize == -1);
/*     */       }
/*     */     }
/* 517 */     if (localBufferInterval == null)
/*     */     {
/* 519 */       ErrorMessage.raiseIOException("JZ0T5");
/*     */     }
/*     */ 
/* 522 */     int i = 0;
/* 523 */     if (this._partialBI != null)
/*     */     {
/* 526 */       System.arraycopy(this._partialBI._buf, this._partialBI._offset, localBufferInterval._buf, localBufferInterval._offset, this._partialBI._length);
/*     */ 
/* 528 */       i = this._partialBI._length;
/*     */ 
/* 530 */       this._pool.put(this._partialBI);
/* 531 */       this._partialBI = null;
/*     */     }
/* 533 */     this._dbio.setBufferInfo(localBufferInterval, i);
/*     */   }
/*     */ 
/*     */   public void reportError(String paramString)
/*     */   {
/* 544 */     StreamContext localStreamContext = getCurrentContext();
/* 545 */     if (localStreamContext == null)
/*     */     {
/*     */       try
/*     */       {
/* 549 */         localStreamContext = this._outMgr.peekNextContext();
/*     */       }
/*     */       catch (EmptyStackException localEmptyStackException)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 557 */     SQLException localSQLException = ErrorMessage.makeIOReportableException(paramString, this._dbio._lastEx);
/*     */ 
/* 559 */     if (localStreamContext != null)
/*     */     {
/* 561 */       localStreamContext.chainException(localSQLException);
/*     */     }
/*     */     else
/*     */     {
/* 565 */       this._eventCtx._conn.chainWarnings(localSQLException);
/*     */     }
/* 567 */     if (null == this._dbio._lastEx)
/*     */       return;
/* 569 */     this._dbio._lastEx = null;
/* 570 */     close();
/*     */   }
/*     */ 
/*     */   private void readIfOwner(int paramInt, StreamContext paramStreamContext)
/*     */     throws IOException
/*     */   {
/* 580 */     switch (paramInt)
/*     */     {
/*     */     case 13:
/*     */       try
/*     */       {
/* 585 */         this._dbio.doRead(paramStreamContext.getTimeUntilTimeout());
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */       finally
/*     */       {
/* 593 */         paramStreamContext._responseQue.giveToNext();
/*     */       }
/*     */     case 12:
/* 598 */       break;
/*     */     case 14:
/* 629 */       ErrorMessage.raiseIOException("JZ0T3");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void cacheResponses(StreamContext paramStreamContext)
/*     */     throws IOException
/*     */   {
/* 643 */     while ((localStreamContext = getCurrentContext()) != null)
/*     */     {
/*     */       int i;
/*     */       do
/*     */       {
/* 649 */         if (!this._readAhead)
/*     */         {
/* 651 */           paramStreamContext._responseQue.setNeed();
/*     */         }
/* 653 */         i = takeIfNoReadAhead(paramStreamContext);
/*     */       }
/* 655 */       while (i != 13);
/*     */ 
/* 657 */       StreamContext localStreamContext = getCurrentContext();
/* 658 */       if (localStreamContext != null)
/*     */       {
/* 663 */         readIfOwner(i, localStreamContext);
/*     */       }
/* 665 */       paramStreamContext._responseQue.giveToNext();
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized StreamContext getCurrentContext()
/*     */   {
/* 674 */     if (this._currentCtx == null)
/*     */     {
/* 676 */       this._currentCtx = this._outMgr.getNextContext();
/* 677 */       if (this._currentCtx != null);
/*     */     }
/*     */ 
/* 685 */     return this._currentCtx;
/*     */   }
/*     */ 
/*     */   public synchronized void setCurrentContextNull() {
/* 689 */     this._currentCtx = null;
/*     */   }
/*     */ 
/*     */   private int takeIfNoReadAhead(StreamContext paramStreamContext)
/*     */   {
/* 694 */     if (this._readAhead)
/*     */     {
/* 696 */       synchronized (paramStreamContext._responseQue)
/*     */       {
/* 698 */         if (paramStreamContext._responseQue.available(0) > 0)
/*     */         {
/* 700 */           return 12;
/*     */         }
/*     */         try
/*     */         {
/* 704 */           paramStreamContext._responseQue.wait(paramStreamContext._timeout);
/*     */         }
/*     */         catch (InterruptedException localInterruptedException)
/*     */         {
/*     */         }
/*     */       }
/*     */ 
/* 711 */       if (paramStreamContext._responseQue.available(0) > 0)
/*     */       {
/* 714 */         return 12;
/*     */       }
/*     */ 
/* 719 */       return 14;
/*     */     }
/*     */ 
/* 724 */     return paramStreamContext._responseQue.giveToMe(paramStreamContext._timeout);
/*     */   }
/*     */ 
/*     */   public BufferPool getBufferPool()
/*     */   {
/* 730 */     return this._pool;
/*     */   }
/*     */ 
/*     */   public boolean isRequestQueueEmpty()
/*     */   {
/* 735 */     return this._outMgr.isRequestQueueEmpty();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.InStreamMgr
 * JD-Core Version:    0.5.4
 */