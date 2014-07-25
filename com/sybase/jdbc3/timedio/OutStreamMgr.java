/*     */ package com.sybase.jdbc3.timedio;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.tds.Tds;
/*     */ import com.sybase.jdbc3.utils.Queue;
/*     */ import com.sybase.jdbc3.utils.SyncObj;
/*     */ import com.sybase.jdbc3.utils.SyncQueue;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.EmptyStackException;
/*     */ 
/*     */ public class OutStreamMgr
/*     */ {
/*     */   private Queue _requestList;
/*     */   private SyncQueue _waitList;
/*     */   private InStreamMgr _inMgr;
/*     */   private StreamContext _cancelCtx;
/*     */   private StreamContext _cancelledCtx;
/*     */   private StreamContext _currentCtx;
/*  46 */   private boolean _serialize = false;
/*     */   public Tds _tds;
/*  52 */   public boolean _tracing = false;
/*     */ 
/*     */   public OutStreamMgr(InStreamMgr paramInStreamMgr)
/*     */   {
/*  57 */     this._requestList = new Queue(6, 6);
/*  58 */     this._waitList = new SyncQueue(6, 6);
/*  59 */     this._inMgr = paramInStreamMgr;
/*  60 */     this._inMgr.setOutStreamMgr(this);
/*  61 */     this._cancelCtx = null;
/*  62 */     this._cancelledCtx = null;
/*     */   }
/*     */ 
/*     */   protected SyncQueue getWaitQueue()
/*     */   {
/*  67 */     return this._waitList;
/*     */   }
/*     */ 
/*     */   public boolean getSendLock(StreamContext paramStreamContext)
/*     */     throws SQLException
/*     */   {
/* 113 */     int i = 0;
/* 114 */     switch (paramStreamContext._request.giveToMe(paramStreamContext._timeout))
/*     */     {
/*     */     case 13:
/* 117 */       i = 1;
/* 118 */       this._currentCtx = paramStreamContext;
/*     */     case 12:
/* 123 */       if (!this._requestList.empty())
/*     */         break label100;
/*     */       try
/*     */       {
/* 127 */         this._inMgr.readInboundData(false);
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 131 */         localIOException.printStackTrace();
/* 132 */       }break;
/*     */     case 14:
/* 162 */       ErrorMessage.raiseError("JZ0T4", Integer.toString(paramStreamContext._timeout));
/*     */     default:
/* 166 */       ErrorMessage.raiseError("JZ0T4", Integer.toString(paramStreamContext._timeout));
/*     */     }
/*     */ 
/* 169 */     label100: return i;
/*     */   }
/*     */ 
/*     */   protected SyncObj dump()
/*     */   {
/* 176 */     Object localObject = null;
/*     */ 
/* 210 */     return localObject;
/*     */   }
/*     */ 
/*     */   public void beginRequestForMigration(StreamContext paramStreamContext)
/*     */     throws SQLException
/*     */   {
/* 216 */     boolean bool = this._serialize;
/*     */     try
/*     */     {
/* 219 */       this._serialize = false;
/* 220 */       beginRequest(paramStreamContext);
/*     */     }
/*     */     finally
/*     */     {
/* 224 */       this._serialize = bool;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void beginRequest(StreamContext paramStreamContext)
/*     */     throws SQLException
/*     */   {
/* 241 */     if (this._serialize)
/*     */     {
/*     */       try
/*     */       {
/* 246 */         this._inMgr.cacheResponses(paramStreamContext);
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 250 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 255 */     paramStreamContext.beginRequest();
/*     */ 
/* 267 */     queueRequest(paramStreamContext); } 
/*     */   // ERROR //
/*     */   public boolean doCancelRequest(StreamContext paramStreamContext1, StreamContext paramStreamContext2, boolean paramBoolean) throws SQLException { // Byte code:
/*     */     //   0: aload_2
/*     */     //   1: iconst_4
/*     */     //   2: invokevirtual 30	com/sybase/jdbc3/timedio/StreamContext:setState	(I)V
/*     */     //   5: aload_0
/*     */     //   6: dup
/*     */     //   7: astore 4
/*     */     //   9: monitorenter
/*     */     //   10: aload_0
/*     */     //   11: getfield 2	com/sybase/jdbc3/timedio/OutStreamMgr:_serialize	Z
/*     */     //   14: istore 5
/*     */     //   16: aload_0
/*     */     //   17: iconst_0
/*     */     //   18: putfield 2	com/sybase/jdbc3/timedio/OutStreamMgr:_serialize	Z
/*     */     //   21: aload_0
/*     */     //   22: aload_2
/*     */     //   23: invokevirtual 31	com/sybase/jdbc3/timedio/OutStreamMgr:getSendLock	(Lcom/sybase/jdbc3/timedio/StreamContext;)Z
/*     */     //   26: pop
/*     */     //   27: aload_0
/*     */     //   28: aload_2
/*     */     //   29: invokevirtual 25	com/sybase/jdbc3/timedio/OutStreamMgr:beginRequest	(Lcom/sybase/jdbc3/timedio/StreamContext;)V
/*     */     //   32: aload_0
/*     */     //   33: iload 5
/*     */     //   35: putfield 2	com/sybase/jdbc3/timedio/OutStreamMgr:_serialize	Z
/*     */     //   38: goto +19 -> 57
/*     */     //   41: astore 6
/*     */     //   43: aload 6
/*     */     //   45: athrow
/*     */     //   46: astore 7
/*     */     //   48: aload_0
/*     */     //   49: iload 5
/*     */     //   51: putfield 2	com/sybase/jdbc3/timedio/OutStreamMgr:_serialize	Z
/*     */     //   54: aload 7
/*     */     //   56: athrow
/*     */     //   57: aload 4
/*     */     //   59: monitorexit
/*     */     //   60: goto +11 -> 71
/*     */     //   63: astore 8
/*     */     //   65: aload 4
/*     */     //   67: monitorexit
/*     */     //   68: aload 8
/*     */     //   70: athrow
/*     */     //   71: iload_3
/*     */     //   72: ifeq +102 -> 174
/*     */     //   75: aload_0
/*     */     //   76: getfield 6	com/sybase/jdbc3/timedio/OutStreamMgr:_requestList	Lcom/sybase/jdbc3/utils/Queue;
/*     */     //   79: dup
/*     */     //   80: astore 4
/*     */     //   82: monitorenter
/*     */     //   83: aload_0
/*     */     //   84: getfield 10	com/sybase/jdbc3/timedio/OutStreamMgr:_inMgr	Lcom/sybase/jdbc3/timedio/InStreamMgr;
/*     */     //   87: invokevirtual 33	com/sybase/jdbc3/timedio/InStreamMgr:currentContext	()Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   90: astore 5
/*     */     //   92: aload 5
/*     */     //   94: ifnull +8 -> 102
/*     */     //   97: aload 5
/*     */     //   99: invokevirtual 34	com/sybase/jdbc3/timedio/StreamContext:cancelled	()V
/*     */     //   102: aload_0
/*     */     //   103: aload_2
/*     */     //   104: putfield 12	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   107: aload_0
/*     */     //   108: aconst_null
/*     */     //   109: putfield 13	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelledCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   112: aload_0
/*     */     //   113: getfield 6	com/sybase/jdbc3/timedio/OutStreamMgr:_requestList	Lcom/sybase/jdbc3/utils/Queue;
/*     */     //   116: invokevirtual 35	com/sybase/jdbc3/utils/Queue:reset	()V
/*     */     //   119: aload_0
/*     */     //   120: getfield 6	com/sybase/jdbc3/timedio/OutStreamMgr:_requestList	Lcom/sybase/jdbc3/utils/Queue;
/*     */     //   123: invokevirtual 36	com/sybase/jdbc3/utils/Queue:next	()Ljava/lang/Object;
/*     */     //   126: checkcast 37	com/sybase/jdbc3/timedio/StreamContext
/*     */     //   129: astore 5
/*     */     //   131: aload 5
/*     */     //   133: aload_2
/*     */     //   134: if_acmpeq +18 -> 152
/*     */     //   137: aload 5
/*     */     //   139: invokevirtual 34	com/sybase/jdbc3/timedio/StreamContext:cancelled	()V
/*     */     //   142: aload_0
/*     */     //   143: getfield 6	com/sybase/jdbc3/timedio/OutStreamMgr:_requestList	Lcom/sybase/jdbc3/utils/Queue;
/*     */     //   146: aload 5
/*     */     //   148: invokevirtual 38	com/sybase/jdbc3/utils/Queue:removeElement	(Ljava/lang/Object;)Z
/*     */     //   151: pop
/*     */     //   152: goto -33 -> 119
/*     */     //   155: astore 6
/*     */     //   157: goto +3 -> 160
/*     */     //   160: aload 4
/*     */     //   162: monitorexit
/*     */     //   163: goto +76 -> 239
/*     */     //   166: astore 9
/*     */     //   168: aload 4
/*     */     //   170: monitorexit
/*     */     //   171: aload 9
/*     */     //   173: athrow
/*     */     //   174: aload_0
/*     */     //   175: aload_1
/*     */     //   176: aload_2
/*     */     //   177: invokespecial 40	com/sybase/jdbc3/timedio/OutStreamMgr:cancelBySpec	(Lcom/sybase/jdbc3/timedio/StreamContext;Lcom/sybase/jdbc3/timedio/StreamContext;)Z
/*     */     //   180: ifne +59 -> 239
/*     */     //   183: aload_1
/*     */     //   184: getfield 41	com/sybase/jdbc3/timedio/StreamContext:_state	I
/*     */     //   187: bipush 6
/*     */     //   189: if_icmpne +48 -> 237
/*     */     //   192: aload_0
/*     */     //   193: dup
/*     */     //   194: astore 4
/*     */     //   196: monitorenter
/*     */     //   197: aload_0
/*     */     //   198: getfield 13	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelledCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   201: ifnull +17 -> 218
/*     */     //   204: aload_0
/*     */     //   205: aload_2
/*     */     //   206: getfield 15	com/sybase/jdbc3/timedio/StreamContext:_timeout	I
/*     */     //   209: i2l
/*     */     //   210: invokevirtual 42	java/lang/Object:wait	(J)V
/*     */     //   213: goto +5 -> 218
/*     */     //   216: astore 5
/*     */     //   218: aload 4
/*     */     //   220: monitorexit
/*     */     //   221: goto +11 -> 232
/*     */     //   224: astore 10
/*     */     //   226: aload 4
/*     */     //   228: monitorexit
/*     */     //   229: aload 10
/*     */     //   231: athrow
/*     */     //   232: aload_0
/*     */     //   233: aconst_null
/*     */     //   234: putfield 13	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelledCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   237: iconst_0
/*     */     //   238: ireturn
/*     */     //   239: iconst_1
/*     */     //   240: ireturn
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   16	32	41	java/sql/SQLException
/*     */     //   16	32	46	finally
/*     */     //   41	48	46	finally
/*     */     //   10	60	63	finally
/*     */     //   63	68	63	finally
/*     */     //   119	152	155	java/util/NoSuchElementException
/*     */     //   83	163	166	finally
/*     */     //   166	171	166	finally
/*     */     //   204	213	216	java/lang/InterruptedException
/*     */     //   197	221	224	finally
/*     */     //   224	229	224	finally } 
/*     */   // ERROR //
/*     */   private boolean cancelBySpec(StreamContext paramStreamContext1, StreamContext paramStreamContext2) throws SQLException { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 6	com/sybase/jdbc3/timedio/OutStreamMgr:_requestList	Lcom/sybase/jdbc3/utils/Queue;
/*     */     //   4: dup
/*     */     //   5: astore_3
/*     */     //   6: monitorenter
/*     */     //   7: iconst_0
/*     */     //   8: istore 4
/*     */     //   10: iconst_m1
/*     */     //   11: istore 5
/*     */     //   13: aload_0
/*     */     //   14: getfield 6	com/sybase/jdbc3/timedio/OutStreamMgr:_requestList	Lcom/sybase/jdbc3/utils/Queue;
/*     */     //   17: invokevirtual 35	com/sybase/jdbc3/utils/Queue:reset	()V
/*     */     //   20: aload_0
/*     */     //   21: getfield 6	com/sybase/jdbc3/timedio/OutStreamMgr:_requestList	Lcom/sybase/jdbc3/utils/Queue;
/*     */     //   24: invokevirtual 36	com/sybase/jdbc3/utils/Queue:next	()Ljava/lang/Object;
/*     */     //   27: checkcast 37	com/sybase/jdbc3/timedio/StreamContext
/*     */     //   30: astore 6
/*     */     //   32: aload 6
/*     */     //   34: aload_2
/*     */     //   35: if_acmpne +6 -> 41
/*     */     //   38: goto -18 -> 20
/*     */     //   41: iload 5
/*     */     //   43: iflt +22 -> 65
/*     */     //   46: aload_0
/*     */     //   47: getfield 6	com/sybase/jdbc3/timedio/OutStreamMgr:_requestList	Lcom/sybase/jdbc3/utils/Queue;
/*     */     //   50: aload_2
/*     */     //   51: invokevirtual 38	com/sybase/jdbc3/utils/Queue:removeElement	(Ljava/lang/Object;)Z
/*     */     //   54: pop
/*     */     //   55: aload_1
/*     */     //   56: bipush 6
/*     */     //   58: invokevirtual 30	com/sybase/jdbc3/timedio/StreamContext:setState	(I)V
/*     */     //   61: iconst_0
/*     */     //   62: aload_3
/*     */     //   63: monitorexit
/*     */     //   64: ireturn
/*     */     //   65: aload 6
/*     */     //   67: aload_1
/*     */     //   68: if_acmpne +16 -> 84
/*     */     //   71: iload 4
/*     */     //   73: istore 5
/*     */     //   75: aload_1
/*     */     //   76: invokevirtual 34	com/sybase/jdbc3/timedio/StreamContext:cancelled	()V
/*     */     //   79: aload_0
/*     */     //   80: aload_1
/*     */     //   81: putfield 13	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelledCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   84: iinc 4 1
/*     */     //   87: goto -67 -> 20
/*     */     //   90: astore 6
/*     */     //   92: goto +3 -> 95
/*     */     //   95: iload 5
/*     */     //   97: ifne +12 -> 109
/*     */     //   100: aload_0
/*     */     //   101: aload_2
/*     */     //   102: putfield 12	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   105: iconst_1
/*     */     //   106: aload_3
/*     */     //   107: monitorexit
/*     */     //   108: ireturn
/*     */     //   109: iload 5
/*     */     //   111: ifge +55 -> 166
/*     */     //   114: aload_0
/*     */     //   115: getfield 10	com/sybase/jdbc3/timedio/OutStreamMgr:_inMgr	Lcom/sybase/jdbc3/timedio/InStreamMgr;
/*     */     //   118: invokevirtual 33	com/sybase/jdbc3/timedio/InStreamMgr:currentContext	()Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   121: aload_1
/*     */     //   122: if_acmpne +26 -> 148
/*     */     //   125: iload 4
/*     */     //   127: ifne +21 -> 148
/*     */     //   130: aload_0
/*     */     //   131: aload_2
/*     */     //   132: putfield 12	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   135: aload_1
/*     */     //   136: invokevirtual 34	com/sybase/jdbc3/timedio/StreamContext:cancelled	()V
/*     */     //   139: aload_0
/*     */     //   140: aconst_null
/*     */     //   141: putfield 13	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelledCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   144: iconst_1
/*     */     //   145: aload_3
/*     */     //   146: monitorexit
/*     */     //   147: ireturn
/*     */     //   148: aload_0
/*     */     //   149: getfield 6	com/sybase/jdbc3/timedio/OutStreamMgr:_requestList	Lcom/sybase/jdbc3/utils/Queue;
/*     */     //   152: aload_2
/*     */     //   153: invokevirtual 38	com/sybase/jdbc3/utils/Queue:removeElement	(Ljava/lang/Object;)Z
/*     */     //   156: pop
/*     */     //   157: aload_0
/*     */     //   158: aconst_null
/*     */     //   159: putfield 13	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelledCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   162: iconst_0
/*     */     //   163: aload_3
/*     */     //   164: monitorexit
/*     */     //   165: ireturn
/*     */     //   166: aload_3
/*     */     //   167: monitorexit
/*     */     //   168: goto +10 -> 178
/*     */     //   171: astore 7
/*     */     //   173: aload_3
/*     */     //   174: monitorexit
/*     */     //   175: aload 7
/*     */     //   177: athrow
/*     */     //   178: aload_0
/*     */     //   179: aload_2
/*     */     //   180: putfield 12	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   183: aload_0
/*     */     //   184: aload_1
/*     */     //   185: putfield 13	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelledCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   188: aload_0
/*     */     //   189: dup
/*     */     //   190: astore_3
/*     */     //   191: monitorenter
/*     */     //   192: aload_0
/*     */     //   193: aload_0
/*     */     //   194: getfield 12	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   197: getfield 15	com/sybase/jdbc3/timedio/StreamContext:_timeout	I
/*     */     //   200: i2l
/*     */     //   201: invokevirtual 42	java/lang/Object:wait	(J)V
/*     */     //   204: goto +5 -> 209
/*     */     //   207: astore 4
/*     */     //   209: aload_3
/*     */     //   210: monitorexit
/*     */     //   211: goto +10 -> 221
/*     */     //   214: astore 8
/*     */     //   216: aload_3
/*     */     //   217: monitorexit
/*     */     //   218: aload 8
/*     */     //   220: athrow
/*     */     //   221: aload_0
/*     */     //   222: aconst_null
/*     */     //   223: putfield 13	com/sybase/jdbc3/timedio/OutStreamMgr:_cancelledCtx	Lcom/sybase/jdbc3/timedio/StreamContext;
/*     */     //   226: iconst_1
/*     */     //   227: ireturn
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   20	38	90	java/util/NoSuchElementException
/*     */     //   41	62	90	java/util/NoSuchElementException
/*     */     //   65	87	90	java/util/NoSuchElementException
/*     */     //   7	64	171	finally
/*     */     //   65	108	171	finally
/*     */     //   109	147	171	finally
/*     */     //   148	165	171	finally
/*     */     //   166	168	171	finally
/*     */     //   171	175	171	finally
/*     */     //   192	204	207	java/lang/InterruptedException
/*     */     //   192	211	214	finally
/*     */     //   214	218	214	finally } 
/* 478 */   public void queueRequest(StreamContext paramStreamContext) { this._requestList.push(paramStreamContext); }
/*     */ 
/*     */ 
/*     */   public void endRequest(StreamContext paramStreamContext)
/*     */   {
/* 487 */     if (this._cancelCtx != null)
/*     */     {
/* 492 */       this._cancelCtx = null;
/* 493 */       this._cancelledCtx = null;
/*     */     }
/* 495 */     if (paramStreamContext._request._guestOf == null)
/*     */     {
/* 497 */       this._currentCtx = null;
/*     */     }
/* 499 */     paramStreamContext._request.giveToNext();
/*     */   }
/*     */ 
/*     */   public void abortRequest(StreamContext paramStreamContext)
/*     */   {
/* 508 */     if (paramStreamContext._request._guestOf == null)
/*     */     {
/* 510 */       this._currentCtx = null;
/*     */     }
/*     */ 
/* 513 */     paramStreamContext._request.giveToNext();
/*     */ 
/* 520 */     this._requestList.removeElement(paramStreamContext);
/*     */   }
/*     */ 
/*     */   protected StreamContext getNextContext()
/*     */   {
/* 531 */     StreamContext localStreamContext = (StreamContext)this._requestList.popNoEx();
/* 532 */     if (this._cancelledCtx == localStreamContext)
/*     */     {
/* 534 */       synchronized (this)
/*     */       {
/* 538 */         super.notify();
/* 539 */         this._cancelledCtx = null;
/*     */       }
/*     */     }
/* 542 */     return localStreamContext;
/*     */   }
/*     */ 
/*     */   protected StreamContext peekNextContext()
/*     */     throws EmptyStackException
/*     */   {
/* 552 */     return (StreamContext)this._requestList.peek();
/*     */   }
/*     */ 
/*     */   protected void setSerialize(boolean paramBoolean)
/*     */   {
/* 561 */     this._serialize = paramBoolean;
/*     */   }
/*     */ 
/*     */   public StreamContext currentContext()
/*     */   {
/* 570 */     return this._currentCtx;
/*     */   }
/*     */ 
/*     */   public boolean isRequestQueueEmpty()
/*     */   {
/* 575 */     return this._requestList.empty();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.timedio.OutStreamMgr
 * JD-Core Version:    0.5.4
 */