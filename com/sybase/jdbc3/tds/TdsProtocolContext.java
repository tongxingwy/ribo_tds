/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*      */ import com.sybase.jdbc3.jdbc.Protocol;
/*      */ import com.sybase.jdbc3.jdbc.ProtocolResultSet;
/*      */ import com.sybase.jdbc3.jdbc.SybResultSet;
/*      */ import com.sybase.jdbc3.timedio.InStreamMgr;
/*      */ import com.sybase.jdbc3.timedio.OutStreamMgr;
/*      */ import com.sybase.jdbc3.timedio.ResponseQueue;
/*      */ import com.sybase.jdbc3.timedio.StreamContext;
/*      */ import com.sybase.jdbc3.utils.BufferInterval;
/*      */ import com.sybase.jdbc3.utils.CacheManager;
/*      */ import com.sybase.jdbc3.utils.SyncObj;
/*      */ import java.io.IOException;
/*      */ import java.sql.SQLException;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ 
/*      */ public class TdsProtocolContext extends StreamContext
/*      */ {
/*   34 */   private static volatile long _logIdCounter = 0L;
/*   35 */   private String _logId = null;
/*      */   protected static final int COLUMN_CHUNK_SIZE = 256;
/*      */   protected static final int LOW_BYTE = 255;
/*      */   protected static final int DONE_PACKET_SIZE = 17;
/*      */   protected static final int PDU_TAIL_SIZE = 8;
/*      */   protected Token _tdsToken;
/*      */   protected boolean _haveDone;
/*      */   protected int _lastResult;
/*      */   protected int _ungotResult;
/*      */   protected int _maxRows;
/*      */   protected int _lastTds;
/*      */   protected RowFormatToken _paramFmts;
/*      */   protected RowFormatToken _paramFmtsForEed;
/*      */   protected RowFormatToken _paramFmtsForEvent;
/*      */   protected RowFormatToken _dynamicFmts;
/*      */   protected boolean _rereadable;
/*      */   protected TdsResultSet _trs;
/*      */   protected TdsResultSet _trsForEed;
/*      */   protected TdsResultSet _trsForEvent;
/*      */   protected TdsResultSet _trsForMsg;
/*      */   protected CacheManager _cm;
/*      */   protected PduInputFormatter _inFormat;
/*      */   protected TdsDataInputStream _in;
/*      */   public SQLException _chainedSqe;
/*   76 */   protected boolean _eed = false;
/*      */ 
/*   80 */   protected boolean _event = false;
/*      */ 
/*   82 */   protected boolean _msg = false;
/*      */   protected TdsCursor _cursor;
/*   88 */   boolean _bigEndian = true;
/*      */ 
/*   95 */   private byte[] _endOfLastPDU = null;
/*   96 */   protected List _listRowFmtTok = new LinkedList();
/*   97 */   protected List _listResultSet = new LinkedList();
/*   98 */   protected int _rowFmtIndex = 0;
/*   99 */   protected int _previousCount = 0;
/*  100 */   protected int _crcCount = 0;
/*      */   protected List _altFormatTokens;
/*      */   protected AltRowToken _currentAltRow;
/*  110 */   private boolean _isSelectSql = false;
/*      */ 
/*      */   public TdsProtocolContext(String paramString, Protocol paramProtocol, InStreamMgr paramInStreamMgr, OutStreamMgr paramOutStreamMgr)
/*      */   {
/*  115 */     super(paramString, paramProtocol, paramInStreamMgr, paramOutStreamMgr);
/*      */ 
/*  119 */     this._logId = ("Pc" + _logIdCounter++);
/*      */ 
/*  122 */     this._inFormat = new PduInputFormatter(this);
/*  123 */     Tds localTds = (Tds)paramProtocol;
/*      */     try
/*      */     {
/*  126 */       this._in = new TdsDataInputStream(localTds, this._inFormat);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */ 
/*  133 */     this._bigEndian = localTds._bigEndian;
/*  134 */     this._in.setBigEndian(this._bigEndian);
/*      */ 
/*  136 */     this._is = this._in;
/*  137 */     clear(true);
/*  138 */     this._pduState = 0;
/*      */   }
/*      */ 
/*      */   public void drop()
/*      */   {
/*  147 */     close(true);
/*      */ 
/*  153 */     ((Tds)this._protocol).removeProtocolContext(this);
/*      */   }
/*      */ 
/*      */   protected synchronized boolean isCancelNeeded()
/*      */   {
/*  167 */     if (this._state == 2)
/*      */     {
/*  169 */       return true;
/*      */     }
/*  171 */     cancelled();
/*  172 */     return false;
/*      */   }
/*      */ 
/*      */   protected synchronized void cancelled()
/*      */   {
/*  182 */     switch (this._state)
/*      */     {
/*      */     case 1:
/*      */     case 4:
/*  187 */       break;
/*      */     case 2:
/*      */     case 3:
/*  190 */       setState(5);
/*      */     case 5:
/*      */     case 6:
/*  195 */       this._responseQue.release();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void beginRequest()
/*      */   {
/*  211 */     close(true);
/*  212 */     this._pduState = 5;
/*  213 */     if (this._state != 4)
/*      */     {
/*  216 */       setState(2);
/*      */     }
/*  218 */     if (this._timeout > 0)
/*      */     {
/*  220 */       this._requestStartTime = System.currentTimeMillis();
/*      */     }
/*      */ 
/*  223 */     this._rowFmtIndex = 0;
/*      */ 
/*  225 */     this._paramFmts = this._dynamicFmts;
/*      */   }
/*      */ 
/*      */   protected void close(boolean paramBoolean)
/*      */   {
/*  237 */     SQLException localSQLException = this._chainedSqe;
/*  238 */     this._inFormat.close();
/*  239 */     clear(paramBoolean);
/*  240 */     this._chainedSqe = localSQLException;
/*      */   }
/*      */ 
/*      */   protected BufferInterval queueData(BufferInterval paramBufferInterval)
/*      */   {
/*  254 */     this._pduState = 1;
/*      */ 
/*  256 */     int i = paramBufferInterval._offset;
/*  257 */     int j = paramBufferInterval._length;
/*  258 */     byte[] arrayOfByte = paramBufferInterval._buf;
/*      */ 
/*  260 */     int k = 0;
/*  261 */     int l = 0;
/*  262 */     int i1 = 0;
/*      */ 
/*  264 */     if (j > 0)
/*      */     {
/*  266 */       int i2 = i;
/*  267 */       if (this._state == 5)
/*      */       {
/*  272 */         this._pduState = 0;
/*      */ 
/*  275 */         this._responseQue.release();
/*      */       }
/*  278 */       else if (j < 8)
/*      */       {
/*  280 */         this._pduState = 2;
/*      */       }
/*      */       else
/*      */       {
/*  286 */         i2 = i;
/*  287 */         k = arrayOfByte[(i2++)] & 0xFF;
/*  288 */         l = arrayOfByte[(i2++)] & 0xFF;
/*  289 */         i1 = ((arrayOfByte[(i2++)] & 0xFF) << 8) + (0xFF & arrayOfByte[(i2++)]);
/*  290 */         if (j >= i1)
/*      */         {
/*  292 */           j -= i1;
/*  293 */           i += i1;
/*      */         }
/*      */         else
/*      */         {
/*  297 */           this._pduState = 2;
/*      */ 
/*  299 */           break label1006:
/*      */         }
/*      */ 
/*  306 */         if (!checkBufType(k)) if (!checkBufStat(l))
/*      */           {
/*  314 */             int i3 = ((l & 0x1) != 0) ? 1 : 0;
/*  315 */             boolean bool = false;
/*      */ 
/*  324 */             int i4 = 0;
/*  325 */             synchronized (this)
/*      */             {
/*  327 */               if ((l & 0x2) != 0)
/*      */               {
/*  334 */                 i3 = 1;
/*  335 */                 i4 = 1;
/*  336 */                 if ((this._state != 5) && (this._state != 4))
/*      */                 {
/*  345 */                   cancelled();
/*      */                 }
/*      */               }
/*      */ 
/*  349 */               int i5 = i4;
/*  350 */               if (i3 != 0)
/*      */               {
/*  355 */                 int i6 = DoneToken.getStatusOffset(this._bigEndian);
/*      */ 
/*  360 */                 if ((i1 >= 9 - i6) && (i5 == 0))
/*      */                 {
/*  363 */                   i5 = ((arrayOfByte[(i - 9 + i6)] & 0x20) != 0) ? 1 : 0;
/*      */                 }
/*      */ 
/*  370 */                 Tds localTds = (Tds)this._protocol;
/*      */                 BufferInterval localBufferInterval1;
/*  372 */                 if (this._state == 4)
/*      */                 {
/*  374 */                   if ((i1 < 17) && (i4 == 0))
/*      */                   {
/*  378 */                     if (this._endOfLastPDU == null)
/*      */                     {
/*  386 */                       bool = true;
/*      */                     }
/*      */                     else
/*      */                     {
/*  399 */                       int i7 = 8 - (9 - (i1 - 8));
/*      */ 
/*  402 */                       if ((i7 + i6 < 8) && (i4 == 0))
/*      */                       {
/*  405 */                         i5 = ((this._endOfLastPDU[(i7 + i6)] & 0x20) != 0) ? 1 : 0;
/*      */                       }
/*      */ 
/*  415 */                       if (i5 != 0)
/*      */                       {
/*  418 */                         BufferInterval localBufferInterval2 = this._inMgr.getBI();
/*      */ 
/*  420 */                         i2 = localBufferInterval2._offset;
/*  421 */                         localBufferInterval2._buf[(i2++)] = (byte)(k & 0xFF);
/*  422 */                         localBufferInterval2._buf[(i2++)] = (byte)(l & 0xFF);
/*  423 */                         localBufferInterval2._buf[(i2++)] = 0;
/*  424 */                         localBufferInterval2._buf[(i2++)] = 17;
/*  425 */                         localBufferInterval2._buf[(i2++)] = 0;
/*  426 */                         localBufferInterval2._buf[(i2++)] = 0;
/*  427 */                         localBufferInterval2._buf[(i2++)] = 0;
/*  428 */                         localBufferInterval2._buf[(i2++)] = 0;
/*  429 */                         int i8 = 8 - i7;
/*  430 */                         System.arraycopy(this._endOfLastPDU, i7, localBufferInterval2._buf, i2, i8);
/*      */ 
/*  432 */                         i2 += i8;
/*  433 */                         System.arraycopy(paramBufferInterval._buf, i - (9 - i8), localBufferInterval2._buf, i2, 9 - i8);
/*      */ 
/*  437 */                         localBufferInterval2._length = 17;
/*  438 */                         if (paramBufferInterval.free())
/*      */                         {
/*  440 */                           paramBufferInterval._buf = null;
/*      */                         }
/*  442 */                         paramBufferInterval = localBufferInterval2;
/*  443 */                         i1 = 17;
/*  444 */                         this._endOfLastPDU = null;
/*      */                       }
/*      */                     }
/*      */                   }
/*  448 */                   else if ((i1 > 17) && 
/*  450 */                     (i5 != 0))
/*      */                   {
/*  455 */                     i2 = i - 17;
/*  456 */                     arrayOfByte[(i2++)] = (byte)(k & 0xFF);
/*  457 */                     arrayOfByte[(i2++)] = (byte)(l & 0xFF);
/*  458 */                     arrayOfByte[(i2++)] = 0;
/*  459 */                     arrayOfByte[(i2++)] = 17;
/*      */ 
/*  463 */                     localBufferInterval1 = paramBufferInterval.divide(i1 - 17);
/*  464 */                     if (localBufferInterval1.free())
/*      */                     {
/*  466 */                       localBufferInterval1 = null;
/*      */                     }
/*  468 */                     i1 = 17;
/*      */                   }
/*      */ 
/*  471 */                   if (i5 == 0)
/*      */                   {
/*  474 */                     bool = true;
/*      */                   }
/*      */                 }
/*  477 */                 else if (this._state == 6)
/*      */                 {
/*  483 */                   setState(5);
/*  484 */                   bool = true;
/*      */                 }
/*      */                 else
/*      */                 {
/*  488 */                   if ((i5 != 0) && (localTds._gotCancelAck))
/*      */                   {
/*  491 */                     localBufferInterval1 = null;
/*  492 */                     if (i1 < paramBufferInterval._length)
/*      */                     {
/*  494 */                       localBufferInterval1 = paramBufferInterval.divide(i1);
/*      */                     }
/*      */                     else
/*      */                     {
/*  498 */                       localBufferInterval1 = paramBufferInterval;
/*  499 */                       paramBufferInterval = null;
/*      */                     }
/*  501 */                     if (localBufferInterval1.free())
/*      */                     {
/*  503 */                       localBufferInterval1 = null;
/*      */                     }
/*  505 */                     break label1006:
/*      */                   }
/*  507 */                   setState(3);
/*      */                 }
/*      */ 
/*  510 */                 if (!this instanceof TdsEventContext)
/*      */                 {
/*  512 */                   localTds._gotCancelAck = i5;
/*      */                 }
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*  518 */                 if (this._state == 4)
/*      */                 {
/*  521 */                   if (this._endOfLastPDU == null)
/*      */                   {
/*  523 */                     this._endOfLastPDU = new byte[8];
/*      */                   }
/*      */ 
/*  527 */                   System.arraycopy(paramBufferInterval._buf, paramBufferInterval._offset + i1 - 8, this._endOfLastPDU, 0, 8);
/*      */                 }
/*      */ 
/*  532 */                 bool = (this._state == 6) || (this._state == 4);
/*      */               }
/*  534 */               if ((i3 != 0) && (((this._state != 4) || (i5 != 0))))
/*      */               {
/*  539 */                 this._pduState = 0;
/*  540 */                 if ((!this instanceof TdsEventContext) && (!this instanceof TdsMigrateContext))
/*      */                 {
/*  546 */                   this._inMgr.setCurrentContextNull();
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*  551 */               paramBufferInterval = makeChunk(paramBufferInterval, i1, bool);
/*  552 */               if (i3 != 0)
/*      */               {
/*  554 */                 if ((this._state == 4) && (i5 != 0))
/*      */                 {
/*  556 */                   this._responseQue.release();
/*      */                 }
/*  558 */                 else if (i5 != 0)
/*      */                 {
/*  561 */                   this._pduState = 0;
/*  562 */                   this._responseQue.release();
/*      */                 }
/*  564 */                 break label1006:
/*      */               }
/*      */             }
/*      */           } 
/*      */       }
/*      */     }
/*  568 */     label1006: return paramBufferInterval;
/*      */   }
/*      */ 
/*      */   public BufferInterval getChunk()
/*      */     throws IOException
/*      */   {
/*  579 */     switch (this._state)
/*      */     {
/*      */     case 1:
/*  583 */       break;
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */       while (true)
/*      */       {
/*  591 */         BufferInterval localBufferInterval = null;
/*  592 */         synchronized (this)
/*      */         {
/*  594 */           localBufferInterval = this._responseQue.pop();
/*      */ 
/*  596 */           if (localBufferInterval == null)
/*      */           {
/*  601 */             if ((this._state == 5) || (this._state == 6))
/*      */             {
/*  603 */               ErrorMessage.raiseIOException("JZ0PA");
/*      */             }
/*  605 */             if (this._state == 3)
/*      */             {
/*  608 */               ErrorMessage.raiseIOException("JZ0EM");
/*      */             }
/*      */ 
/*      */           }
/*  613 */           else if ((this._state != 5) && (this._state != 6))
/*      */           {
/*  615 */             return localBufferInterval;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  620 */         if (localBufferInterval == null)
/*      */         {
/*  623 */           this._inMgr.doRead(this);
/*      */         }
/*      */ 
/*  635 */         if (localBufferInterval.free())
/*      */         {
/*  637 */           localBufferInterval = null;
/*      */         }
/*  639 */         ErrorMessage.raiseIOException("JZ0PA");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  644 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isProcDone()
/*      */   {
/*  649 */     return (((DoneToken)this._tdsToken)._status & 0x8) == 8;
/*      */   }
/*      */ 
/*      */   protected boolean checkBufStat(int paramInt)
/*      */   {
/*  656 */     if ((paramInt & 0x8) != 0)
/*      */     {
/*  658 */       this._pduState = 3;
/*      */ 
/*  660 */       return true;
/*      */     }
/*  662 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean checkBufType(int paramInt)
/*      */   {
/*  668 */     if (paramInt == 17)
/*      */     {
/*  670 */       this._pduState = 7;
/*  671 */       return true;
/*      */     }
/*  673 */     return false;
/*      */   }
/*      */ 
/*      */   protected int available()
/*      */   {
/*  682 */     return this._responseQue.available(8);
/*      */   }
/*      */ 
/*      */   protected void chainException(SQLException paramSQLException)
/*      */   {
/*  697 */     if (this._chainedSqe == null)
/*      */     {
/*  701 */       this._chainedSqe = paramSQLException;
/*      */     }
/*  707 */     else if ("JZ0F2".equals(paramSQLException.getSQLState()))
/*      */     {
/*  711 */       paramSQLException.setNextException(this._chainedSqe);
/*  712 */       this._chainedSqe = paramSQLException;
/*      */     }
/*      */     else
/*      */     {
/*  716 */       this._chainedSqe.setNextException(paramSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void clear(boolean paramBoolean)
/*      */   {
/*  733 */     this._chainedSqe = null;
/*  734 */     this._lastResult = -1;
/*  735 */     this._ungotResult = -1;
/*  736 */     this._tdsToken = null;
/*  737 */     this._haveDone = false;
/*  738 */     this._eed = false;
/*  739 */     this._paramFmts = null;
/*  740 */     this._paramFmtsForEed = null;
/*  741 */     this._paramFmtsForEvent = null;
/*  742 */     this._trs = null;
/*  743 */     this._trsForEed = null;
/*  744 */     this._trsForEvent = null;
/*  745 */     this._trsForMsg = null;
/*      */ 
/*  750 */     if (this._state != 4)
/*      */     {
/*  754 */       if (paramBoolean)
/*      */       {
/*      */         while (true)
/*      */         {
/*  758 */           BufferInterval localBufferInterval = this._responseQue.pop();
/*  759 */           if (localBufferInterval == null) {
/*      */             break;
/*      */           }
/*      */ 
/*  763 */           if (!localBufferInterval.free())
/*      */             continue;
/*  765 */           localBufferInterval = null;
/*      */         }
/*      */       }
/*      */ 
/*  769 */       setState(1);
/*      */     }
/*  771 */     if (this._cm == null)
/*      */       return;
/*  773 */     this._cm.allDead();
/*      */   }
/*      */ 
/*      */   protected SybResultSet getParams()
/*      */   {
/*  781 */     SybResultSet localSybResultSet = null;
/*  782 */     int i = 0;
/*      */ 
/*  794 */     SQLException localSQLException1 = this._chainedSqe;
/*  795 */     this._chainedSqe = null;
/*      */     while (true)
/*      */       try
/*      */       {
/*      */         while (true)
/*      */         {
/*  801 */           i = this._protocol.nextResult(this);
/*  802 */           switch (i)
/*      */           {
/*      */           case 3:
/*  805 */             switch (this._lastTds)
/*      */             {
/*      */             case 32:
/*      */             case 236:
/*  820 */               if (this._eed)
/*      */               {
/*  822 */                 this._trsForEed = new TdsParamSet(this, false, 2);
/*      */ 
/*  824 */                 localSybResultSet = new SybResultSet(this._logId, this._trsForEed, this);
/*      */               }
/*  827 */               else if (this._event)
/*      */               {
/*  829 */                 this._trsForEvent = new TdsParamSet(this, false, 3);
/*      */ 
/*  831 */                 localSybResultSet = new SybResultSet(this._logId, this._trsForEvent, this);
/*      */               }
/*  834 */               else if (this._msg)
/*      */               {
/*  836 */                 this._trsForMsg = new TdsParamSet(this, false, 4);
/*      */ 
/*  838 */                 localSybResultSet = new SybResultSet(this._logId, this._trsForMsg, this);
/*      */               }
/*      */ 
/*  843 */               localSybResultSet.setHoldsParams(true);
/*      */ 
/*  847 */               this._lastResult = -1;
/*      */             case 215:
/*      */             }
/*      */ 
/*      */           case 0:
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  852 */         this._lastResult = -1;
/*  853 */         jsr 79; break label345:
/*      */ 
/*  858 */         this._protocol.ungetResult(this, this._lastTds);
/*  859 */         jsr 59; break label345:
/*      */ 
/*  872 */         this._protocol.ungetResult(this, i);
/*  873 */         jsr 42; break label345:
/*      */ 
/*  877 */         this._protocol.ungetResult(this, i);
/*      */       }
/*      */       catch (SQLException localSQLException2)
/*      */       {
/*  883 */         chainException(localSQLException2);
/*      */       }
/*      */       finally
/*      */       {
/*  887 */         jsr 6;
/*      */       } if (this._chainedSqe != null)
/*      */     {
/*  892 */       if (localSQLException1 != null)
/*      */       {
/*  894 */         localSQLException1.setNextException(this._chainedSqe);
/*      */       }
/*      */       else
/*      */       {
/*  902 */         localSQLException1 = this._chainedSqe;
/*      */       }
/*      */     }
/*  905 */     this._chainedSqe = localSQLException1; ret;
/*      */ 
/*  907 */     label345: return localSybResultSet;
/*      */   }
/*      */ 
/*      */   protected SybResultSet makeEmptyParams()
/*      */   {
/*  914 */     SybResultSet localSybResultSet = null;
/*      */     try
/*      */     {
/*  917 */       if (this._eed)
/*      */       {
/*  919 */         this._paramFmtsForEed = new RowFormatToken();
/*  920 */         this._trsForEed = new TdsParamSet(this, true, 2);
/*      */ 
/*  922 */         localSybResultSet = new SybResultSet(this._logId, this._trsForEed, this);
/*      */       }
/*  924 */       else if (this._event)
/*      */       {
/*  926 */         this._paramFmtsForEvent = new RowFormatToken();
/*  927 */         this._trsForEvent = new TdsParamSet(this, true, 3);
/*      */ 
/*  929 */         localSybResultSet = new SybResultSet(this._logId, this._trsForEvent, this);
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*      */     }
/*  935 */     return localSybResultSet;
/*      */   }
/*      */ 
/*      */   protected void setRereadable()
/*      */   {
/*  940 */     this._cm = new CacheManager(this._in);
/*  941 */     this._cm.setReReadable(true);
/*  942 */     this._cm.setCacheSize(-1);
/*  943 */     this._cm.setChunkSize(256);
/*  944 */     this._cm.setAbortOnCacheOverflow(true);
/*      */   }
/*      */ 
/*      */   protected void setState(int paramInt)
/*      */   {
/*  958 */     super.setState(paramInt);
/*      */   }
/*      */ 
/*      */   protected void refreshYourself(Protocol paramProtocol, InStreamMgr paramInStreamMgr, OutStreamMgr paramOutStreamMgr)
/*      */   {
/*  970 */     super.refreshYourself(paramProtocol, paramInStreamMgr, paramOutStreamMgr);
/*  971 */     this._inFormat = new PduInputFormatter(this);
/*  972 */     Tds localTds = (Tds)paramProtocol;
/*      */     try
/*      */     {
/*  975 */       this._in = new TdsDataInputStream(localTds, this._inFormat);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */ 
/*  982 */     this._bigEndian = localTds._bigEndian;
/*  983 */     this._in.setBigEndian(this._bigEndian);
/*      */ 
/*  985 */     clear(true);
/*  986 */     this._pduState = 0;
/*      */   }
/*      */ 
/*      */   protected RowFormatToken getFormat()
/*      */   {
/*  991 */     if (this._eed)
/*      */     {
/*  993 */       return this._paramFmtsForEed;
/*      */     }
/*  995 */     if (this._event)
/*      */     {
/*  997 */       return this._paramFmtsForEvent;
/*      */     }
/*      */ 
/* 1001 */     return this._paramFmts;
/*      */   }
/*      */ 
/*      */   public int getTimeUntilTimeout()
/*      */     throws IOException
/*      */   {
/* 1007 */     if (this._timeout == 0)
/*      */     {
/* 1009 */       return this._timeout;
/*      */     }
/*      */ 
/* 1012 */     int i = getTimeLeft();
/* 1013 */     if (i <= 0)
/*      */     {
/* 1015 */       ErrorMessage.raiseIOException("JZ0T3");
/*      */     }
/* 1017 */     return i;
/*      */   }
/*      */ 
/*      */   public int getTimeLeft()
/*      */   {
/* 1028 */     long l = this._timeout - (System.currentTimeMillis() - this._requestStartTime);
/*      */ 
/* 1030 */     if (l == 0L)
/*      */     {
/* 1032 */       l = -1L;
/*      */     }
/* 1034 */     return (int)l;
/*      */   }
/*      */ 
/*      */   protected void dump(SyncObj paramSyncObj1, SyncObj paramSyncObj2)
/*      */   {
/*      */   }
/*      */ 
/*      */   public int getPreviousCount()
/*      */   {
/* 1116 */     return this._previousCount;
/*      */   }
/*      */ 
/*      */   public void resetRowFmt()
/*      */   {
/* 1125 */     this._listRowFmtTok.clear();
/* 1126 */     this._listResultSet.clear();
/*      */   }
/*      */ 
/*      */   void newRowFmt(TdsInputStream paramTdsInputStream, int paramInt)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/* 1137 */       long l = 0L;
/* 1138 */       switch (paramInt)
/*      */       {
/*      */       case 238:
/* 1141 */         l = paramTdsInputStream.readShort();
/* 1142 */         RowFormatToken localRowFormatToken = null;
/* 1143 */         if (this._listRowFmtTok.size() > this._rowFmtIndex)
/*      */         {
/* 1145 */           localRowFormatToken = (RowFormatToken)this._listRowFmtTok.get(this._rowFmtIndex);
/*      */         }
/* 1147 */         if ((localRowFormatToken == null) || (l != localRowFormatToken.getRowFmtLength()))
/*      */         {
/* 1149 */           this._paramFmts = new RowFormatToken(paramTdsInputStream, l);
/* 1150 */           this._listRowFmtTok.add(this._rowFmtIndex, this._paramFmts);
/*      */         }
/*      */         else
/*      */         {
/* 1154 */           this._paramFmts = localRowFormatToken;
/* 1155 */           paramTdsInputStream.skip(l);
/*      */         }
/* 1157 */         break;
/*      */       case 97:
/* 1159 */         l = paramTdsInputStream.readUnsignedIntAsLong();
/* 1160 */         RowFormat2Token localRowFormat2Token = null;
/* 1161 */         if (this._listRowFmtTok.size() > this._rowFmtIndex)
/*      */         {
/* 1163 */           localRowFormat2Token = (RowFormat2Token)this._listRowFmtTok.get(this._rowFmtIndex);
/*      */         }
/* 1165 */         if ((localRowFormat2Token == null) || (l != localRowFormat2Token.getRowFmtLength()))
/*      */         {
/* 1167 */           this._paramFmts = new RowFormat2Token(paramTdsInputStream, l);
/* 1168 */           this._listRowFmtTok.add(this._rowFmtIndex, this._paramFmts);
/*      */         }
/*      */         else
/*      */         {
/* 1172 */           this._paramFmts = localRowFormat2Token;
/* 1173 */           paramTdsInputStream.skip(l);
/*      */         }
/*      */       }
/*      */ 
/* 1177 */       this._rowFmtIndex += 1;
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1181 */       throw localIOException;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected ProtocolResultSet newResultSet() throws SQLException
/*      */   {
/* 1187 */     int i = 0;
/* 1188 */     if (this._rowFmtIndex > 0)
/* 1189 */       i = this._rowFmtIndex - 1;
/* 1190 */     if (this._listResultSet.size() > i)
/*      */     {
/* 1192 */       this._trs = ((TdsResultSet)this._listResultSet.get(i));
/* 1193 */       if (this._trs != null)
/*      */       {
/* 1195 */         this._trs.reset();
/* 1196 */         this._lastResult = -1;
/* 1197 */         return this._trs;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1203 */     if (this._cursor != null)
/*      */     {
/* 1205 */       if ((this._cursor._type & 0x120) != 0)
/*      */       {
/* 1208 */         this._trs = new TdsScrollResultSet(this);
/*      */       }
/*      */       else
/*      */       {
/* 1212 */         this._trs = new TdsResultSet(this);
/*      */       }
/*      */ 
/*      */     }
/*      */     else {
/* 1217 */       this._trs = new TdsResultSet(this);
/*      */     }
/* 1219 */     this._lastResult = -1;
/* 1220 */     this._listResultSet.add(i, this._trs);
/* 1221 */     return this._trs;
/*      */   }
/*      */ 
/*      */   public void copyColumnCache(TdsProtocolContext paramTdsProtocolContext)
/*      */   {
/* 1226 */     this._listRowFmtTok = paramTdsProtocolContext._listRowFmtTok;
/* 1227 */     this._listResultSet.clear();
/*      */   }
/*      */ 
/*      */   public void addAltFmtToken(TdsDataInputStream paramTdsDataInputStream)
/*      */     throws IOException, SQLException
/*      */   {
/* 1236 */     AltFormatToken localAltFormatToken = new AltFormatToken(paramTdsDataInputStream);
/* 1237 */     if (this._altFormatTokens == null)
/*      */     {
/* 1239 */       this._altFormatTokens = new LinkedList();
/*      */     }
/* 1241 */     this._altFormatTokens.add(localAltFormatToken.getId() - 1, localAltFormatToken);
/*      */   }
/*      */ 
/*      */   public AltFormatToken getAltFmt(int paramInt)
/*      */   {
/* 1250 */     return (AltFormatToken)this._altFormatTokens.get(paramInt - 1);
/*      */   }
/*      */ 
/*      */   public void setCurrentAltRow(TdsDataInputStream paramTdsDataInputStream)
/*      */     throws IOException
/*      */   {
/* 1260 */     this._currentAltRow = new AltRowToken(paramTdsDataInputStream);
/*      */   }
/*      */ 
/*      */   public AltRowToken getCurrentAltRow()
/*      */   {
/* 1269 */     return this._currentAltRow;
/*      */   }
/*      */ 
/*      */   public boolean isSelectSql()
/*      */   {
/* 1277 */     return this._isSelectSql;
/*      */   }
/*      */ 
/*      */   public void setIsSelectSql(boolean paramBoolean)
/*      */   {
/* 1286 */     this._isSelectSql = paramBoolean;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsProtocolContext
 * JD-Core Version:    0.5.4
 */