/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.Cursor;
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.Param;
/*     */ import com.sybase.jdbc3.jdbc.ParamManager;
/*     */ import com.sybase.jdbc3.jdbc.ProtocolContext;
/*     */ import com.sybase.jdbc3.jdbc.ProtocolResultSet;
/*     */ import com.sybase.jdbc3.jdbc.SybConnection;
/*     */ import com.sybase.jdbc3.jdbc.SybPreparedStatement;
/*     */ import com.sybase.jdbc3.timedio.OutStreamMgr;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class TdsCursor
/*     */   implements Cursor
/*     */ {
/*     */   private boolean _langCur;
/*     */   protected int _hasArgs;
/*     */   protected int _id;
/*  35 */   private static int _cursorNum = 0;
/*     */   protected String _name;
/*     */   protected int _state;
/*     */   protected int _type;
/*  40 */   private int _fetchSize = 1;
/*  41 */   private int _savedFetchSize = 1;
/*  42 */   protected String _table = null;
/*  43 */   protected String[] _columns = null;
/*  44 */   protected int _totalRowCount = -1;
/*  45 */   protected int _rowNum = -1;
/*     */   private Tds _tds;
/*     */   private TdsProtocolContext _tpc;
/*     */   private TdsProtocolContext _curPC;
/*     */   private SybPreparedStatement _utilStmt;
/*     */ 
/*     */   public TdsCursor(Tds paramTds, ProtocolContext paramProtocolContext1, boolean paramBoolean, ProtocolContext paramProtocolContext2)
/*     */   {
/*  61 */     this._state = 0;
/*  62 */     this._type = 0;
/*  63 */     this._tds = paramTds;
/*  64 */     this._tpc = ((TdsProtocolContext)paramProtocolContext1);
/*  65 */     this._tpc._cursor = this;
/*  66 */     this._langCur = paramBoolean;
/*  67 */     this._curPC = ((TdsProtocolContext)paramProtocolContext2);
/*  68 */     this._curPC._cursor = this;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  74 */     if (this._name == null)
/*     */     {
/*  76 */       synchronized (this)
/*     */       {
/*  78 */         _cursorNum += 1;
/*  79 */         this._name = ("jconnect_implicit_" + _cursorNum);
/*     */       }
/*     */     }
/*  82 */     return this._name;
/*     */   }
/*     */ 
/*     */   public void setName(String paramString) throws SQLException
/*     */   {
/*  87 */     checkState();
/*  88 */     this._name = paramString;
/*     */   }
/*     */ 
/*     */   public void setDynamic(boolean paramBoolean)
/*     */   {
/*  98 */     if (!paramBoolean)
/*     */       return;
/* 100 */     this._type |= 8;
/*     */   }
/*     */ 
/*     */   public void setTypeAndConcurrency(int paramInt1, int paramInt2)
/*     */     throws SQLException
/*     */   {
/* 118 */     this._type = 0;
/*     */ 
/* 120 */     switch (paramInt1)
/*     */     {
/*     */     case 1003:
/* 124 */       break;
/*     */     case 1004:
/* 129 */       if (this._tds.useInsensitiveScrollableCursor())
/*     */       {
/* 131 */         this._type |= 288; } break;
/*     */     default:
/* 136 */       ErrorMessage.raiseError("JZ0BD");
/*     */     }
/*     */ 
/* 139 */     switch (paramInt2)
/*     */     {
/*     */     case 1007:
/* 143 */       this._type |= 1;
/* 144 */       break;
/*     */     case 1008:
/* 146 */       this._type &= -2;
/* 147 */       this._type |= 2;
/* 148 */       break;
/*     */     default:
/* 150 */       ErrorMessage.raiseError("JZ0BD");
/*     */     }
/* 152 */     if (!this._tds.shouldReleaseLockOnCursorClose())
/*     */       return;
/* 154 */     this._type |= 512;
/*     */   }
/*     */ 
/*     */   public void setType(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 170 */     switch (paramInt)
/*     */     {
/*     */     case 1:
/* 173 */       this._type |= 1;
/* 174 */       this._type &= -3;
/* 175 */       break;
/*     */     case 2:
/* 177 */       this._type &= -2;
/* 178 */       this._type |= 2;
/* 179 */       break;
/*     */     case 8:
/* 181 */       this._type |= 8;
/* 182 */       break;
/*     */     case 256:
/* 184 */       this._type |= 256;
/* 185 */       break;
/*     */     case 32:
/* 187 */       this._type |= 32;
/* 188 */       break;
/*     */     case 64:
/* 190 */       this._type |= 64;
/* 191 */       break;
/*     */     case 4:
/* 193 */       this._type |= 4;
/* 194 */       break;
/*     */     case 128:
/* 196 */       this._type |= 128;
/* 197 */       break;
/*     */     default:
/* 199 */       ErrorMessage.raiseError("JZ0BD");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clearType()
/*     */   {
/* 205 */     this._type = 0;
/*     */   }
/*     */ 
/*     */   public int getConcurrency() throws SQLException
/*     */   {
/* 210 */     if ((!this._langCur) && (this._state == 1))
/*     */     {
/* 212 */       doCurInfo(2, 131);
/*     */     }
/*     */ 
/* 215 */     if ((this._type & 0x1) != 0)
/*     */     {
/* 217 */       return 1007;
/*     */     }
/* 219 */     return 1008;
/*     */   }
/*     */ 
/*     */   public boolean scrollingAtServer()
/*     */   {
/* 224 */     return (this._type & 0x100) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isLanguageCursor()
/*     */   {
/* 229 */     return this._langCur;
/*     */   }
/*     */ 
/*     */   public void setFetchSize(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 235 */     if (paramInt == 0) {
/* 236 */       return;
/*     */     }
/* 238 */     if (paramInt < 0)
/*     */     {
/* 240 */       ErrorMessage.raiseError("JZ0BI");
/*     */     }
/*     */ 
/* 243 */     int i = this._fetchSize;
/* 244 */     this._fetchSize = paramInt;
/*     */ 
/* 246 */     if ((this._state == 1) && (i != paramInt))
/*     */     {
/* 248 */       doCurInfo(1, 131);
/*     */     }
/* 250 */     this._savedFetchSize = this._fetchSize;
/*     */   }
/*     */ 
/*     */   public int getFetchSize() {
/* 254 */     if (this._savedFetchSize != this._fetchSize)
/*     */     {
/* 256 */       return this._savedFetchSize;
/*     */     }
/*     */ 
/* 260 */     return this._fetchSize;
/*     */   }
/*     */ 
/*     */   protected int getFetchSizeForLastFetch()
/*     */   {
/* 271 */     return this._fetchSize;
/*     */   }
/*     */ 
/*     */   public ProtocolResultSet open(String paramString, ParamManager paramParamManager, boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/* 281 */     if (this._name == null)
/*     */     {
/* 285 */       getName();
/*     */     }
/*     */ 
/* 290 */     if (this._state == 1)
/*     */     {
/* 292 */       ErrorMessage.raiseError("JZ00E");
/*     */     }
/* 294 */     this._tds._outStreamMgr.getSendLock(this._tpc);
/* 295 */     this._tpc.setSponsor(this._tpc);
/* 296 */     if (paramParamManager != null)
/*     */     {
/* 298 */       this._hasArgs = ((paramParamManager.hasInParams()) ? 1 : 0);
/*     */     }
/* 300 */     ProtocolResultSet localProtocolResultSet = null;
/*     */     try
/*     */     {
/* 303 */       if (paramBoolean)
/*     */       {
/* 305 */         paramString = new String("EXECUTE " + paramString);
/*     */       }
/* 307 */       if ((this._type & 0x8) != 0)
/*     */       {
/* 309 */         paramBoolean = true;
/*     */       }
/* 311 */       if ((!this._langCur) || ((this._type & 0x8) != 0))
/*     */       {
/* 315 */         tdsCursor(paramString, paramParamManager, paramBoolean);
/* 316 */         this._langCur = false;
/*     */       }
/*     */       else
/*     */       {
/* 320 */         languageCursor(paramString, paramParamManager);
/*     */       }
/* 322 */       if (this._tds.getResultSetResult(this._tpc, !this._langCur))
/*     */       {
/* 324 */         localProtocolResultSet = this._tds.resultSet(this._tpc);
/*     */       }
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 329 */       this._tds.cancel(this._tpc, false);
/* 330 */       throw localSQLException;
/*     */     }
/*     */     finally
/*     */     {
/* 335 */       this._tpc.setSponsor(null);
/* 336 */       this._tds._outStreamMgr.endRequest(this._tpc);
/*     */     }
/*     */ 
/* 339 */     this._state = 1;
/* 340 */     return localProtocolResultSet;
/*     */   }
/*     */ 
/*     */   public ProtocolResultSet fetch() throws SQLException
/*     */   {
/* 345 */     return fetch(1, 0, false);
/*     */   }
/*     */ 
/*     */   public ProtocolResultSet fetch(int paramInt1, int paramInt2, boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/* 357 */     clearAllPositionStateInfo();
/* 358 */     if (this._tpc._maxRows > 0)
/*     */     {
/* 360 */       int i = this._tpc._maxRows - this._tpc._trs._totalCursorRows;
/* 361 */       if (i <= 0)
/*     */       {
/* 363 */         return null;
/*     */       }
/* 365 */       if (i < this._fetchSize)
/*     */       {
/* 367 */         setFetchSize(i);
/*     */       }
/*     */     }
/*     */ 
/* 371 */     this._tds._outStreamMgr.getSendLock(this._tpc);
/* 372 */     TdsResultSet localTdsResultSet = null;
/*     */     try
/*     */     {
/* 375 */       if (!this._langCur)
/*     */       {
/* 379 */         localTdsResultSet = this._tpc._trs;
/* 380 */         this._tds._outStreamMgr.beginRequest(this._tpc);
/*     */         try
/*     */         {
/* 383 */           if (paramBoolean)
/*     */           {
/* 385 */             if (this._fetchSize != 1)
/*     */             {
/* 387 */               this._savedFetchSize = this._fetchSize;
/* 388 */               this._fetchSize = 1;
/* 389 */               localObject1 = new CurInfoToken(this);
/* 390 */               ((CurInfoToken)localObject1).send(this._tds._out, 1, this._fetchSize);
/*     */             }
/*     */ 
/*     */           }
/* 396 */           else if (this._fetchSize != this._savedFetchSize)
/*     */           {
/* 398 */             this._fetchSize = this._savedFetchSize;
/* 399 */             localObject1 = new CurInfoToken(this);
/* 400 */             ((CurInfoToken)localObject1).send(this._tds._out, 1, this._fetchSize);
/*     */           }
/*     */ 
/* 404 */           Object localObject1 = new CurFetchToken(this, paramInt1, paramInt2);
/*     */ 
/* 407 */           ((CurFetchToken)localObject1).send(this._tds._out);
/* 408 */           this._tds._out.flush();
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/* 412 */           this._tds._outStreamMgr.abortRequest(this._tpc);
/* 413 */           handleIOE(localIOException);
/*     */         }
/*     */ 
/* 416 */         int j = this._tds.nextResult(this._tpc);
/*     */ 
/* 424 */         localTdsResultSet.prepareForNextFetch();
/*     */ 
/* 426 */         this._tpc._trs = localTdsResultSet;
/* 427 */         this._tds.ungetResult(this._tpc, j);
/*     */       }
/*     */       else
/*     */       {
/* 431 */         this._utilStmt.sendQuery(null, null);
/* 432 */         if (this._tds.getResultSetResult(this._tpc, false))
/*     */         {
/* 434 */           localTdsResultSet = (TdsResultSet)this._tds.resultSet(this._tpc);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 445 */       this._tds._outStreamMgr.endRequest(this._tpc);
/*     */     }
/* 447 */     return (ProtocolResultSet)localTdsResultSet;
/*     */   }
/*     */ 
/*     */   public int delete(ProtocolResultSet paramProtocolResultSet)
/*     */     throws SQLException
/*     */   {
/* 454 */     TdsResultSet localTdsResultSet = (TdsResultSet)paramProtocolResultSet;
/* 455 */     this._tds._outStreamMgr.getSendLock(this._curPC);
/*     */     try
/*     */     {
/* 458 */       if (!this._langCur)
/*     */       {
/* 460 */         this._tds._outStreamMgr.beginRequest(this._curPC);
/*     */         try
/*     */         {
/* 463 */           CurDeleteToken localCurDeleteToken = new CurDeleteToken(this, localTdsResultSet);
/* 464 */           localCurDeleteToken.send(this._tds._out);
/*     */ 
/* 466 */           KeyToken localKeyToken = new KeyToken(localTdsResultSet);
/* 467 */           localKeyToken.send(this._tds._out);
/* 468 */           this._tds._out.flush();
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/* 472 */           this._tds._outStreamMgr.abortRequest(this._curPC);
/* 473 */           handleIOE(localIOException);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 478 */         this._tds.language(this._curPC, "DELETE " + this._table + " WHERE CURRENT OF " + this._name, null);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 488 */       this._tds._outStreamMgr.endRequest(this._curPC);
/*     */     }
/* 490 */     return this._tds.getDoneResult(this._curPC);
/*     */   }
/*     */ 
/*     */   public int insert(ProtocolResultSet paramProtocolResultSet, ParamManager paramParamManager, String paramString)
/*     */     throws SQLException
/*     */   {
/* 499 */     this._tds._outStreamMgr.getSendLock(this._curPC);
/*     */     try
/*     */     {
/* 503 */       this._tds.language(this._curPC, paramString, paramParamManager);
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 511 */       this._tds._outStreamMgr.endRequest(this._curPC);
/*     */     }
/* 513 */     return this._tds.getDoneResult(this._curPC);
/*     */   }
/*     */ 
/*     */   public int update(ProtocolResultSet paramProtocolResultSet, ParamManager paramParamManager, String paramString)
/*     */     throws SQLException
/*     */   {
/* 523 */     boolean bool = false;
/* 524 */     TdsResultSet localTdsResultSet = (TdsResultSet)paramProtocolResultSet;
/* 525 */     this._tds._outStreamMgr.getSendLock(this._curPC);
/*     */     try
/*     */     {
/* 528 */       if (!this._langCur)
/*     */       {
/*     */         Object localObject1;
/* 530 */         if (paramParamManager != null)
/*     */         {
/* 532 */           paramParamManager.parseParamsAgain();
/* 533 */           paramParamManager.checkParams(this._tds, false, false, -1);
/* 534 */           localObject1 = null;
/* 535 */           localObject1 = paramParamManager.processParamMarkers(paramString);
/* 536 */           if (localObject1 != null)
/*     */           {
/* 538 */             paramString = (String)localObject1;
/*     */           }
/* 540 */           bool = paramParamManager.hasInParams();
/*     */         }
/*     */ 
/* 543 */         this._tds._outStreamMgr.beginRequest(this._curPC);
/*     */         try
/*     */         {
/* 546 */           localObject1 = new CurUpdateToken(this, localTdsResultSet, paramString, bool);
/*     */ 
/* 548 */           ((CurUpdateToken)localObject1).send(this._tds._out);
/*     */ 
/* 550 */           KeyToken localKeyToken = new KeyToken(localTdsResultSet);
/* 551 */           localKeyToken.send(this._tds._out);
/* 552 */           this._tds.sendParamStream(paramParamManager, this._tds._out);
/* 553 */           this._tds._out.flush();
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/* 557 */           this._tds._outStreamMgr.abortRequest(this._curPC);
/* 558 */           handleIOE(localIOException);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 563 */         this._tds.language(this._curPC, paramString + " WHERE CURRENT OF " + this._name, paramParamManager);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 573 */       this._tds._outStreamMgr.endRequest(this._curPC);
/*     */     }
/* 575 */     return this._tds.getDoneResult(this._curPC);
/*     */   }
/*     */ 
/*     */   public void close(boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/* 583 */     if (paramBoolean)
/*     */     {
/* 585 */       if ((this._state == 0) || (this._state == 3))
/*     */       {
/* 587 */         return;
/*     */       }
/*     */     }
/* 590 */     else if (this._state != 1)
/*     */     {
/* 592 */       return;
/*     */     }
/* 594 */     int i = this._id;
/*     */ 
/* 596 */     this._tpc._conn.removeCursorResultSet(this._name);
/* 597 */     this._tds._outStreamMgr.getSendLock(this._tpc);
/*     */     try
/*     */     {
/* 600 */       if (!this._langCur)
/*     */       {
/* 602 */         this._tds._outStreamMgr.beginRequest(this._tpc);
/*     */         try
/*     */         {
/* 605 */           CurCloseToken localCurCloseToken = new CurCloseToken(this, paramBoolean);
/* 606 */           localCurCloseToken.send(this._tds._out);
/* 607 */           this._tds._out.flush();
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/* 611 */           this._tds._outStreamMgr.abortRequest(this._tpc);
/* 612 */           handleIOE(localIOException);
/*     */         }
/* 614 */         if (paramBoolean)
/*     */         {
/* 616 */           this._state = 3;
/* 617 */           this._id = 0;
/*     */         }
/*     */         else
/*     */         {
/* 621 */           this._state = 2;
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 629 */         this._tds.language(this._tpc, "DEALLOCATE CURSOR " + this._name, null);
/* 630 */         this._state = 3;
/*     */ 
/* 634 */         this._utilStmt = null;
/*     */       }
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 639 */       throw localSQLException;
/*     */     }
/*     */     finally
/*     */     {
/* 643 */       this._tds._outStreamMgr.endRequest(this._tpc);
/*     */ 
/* 647 */       this._curPC.drop();
/*     */ 
/* 655 */       this._tds._cursors.remove(new Integer(i));
/*     */     }
/* 657 */     this._tds.getDoneResult(this._tpc);
/*     */   }
/*     */ 
/*     */   public void setTable(String paramString)
/*     */   {
/* 662 */     if (paramString == null)
/*     */       return;
/* 664 */     this._table = paramString;
/*     */   }
/*     */ 
/*     */   public String getTable()
/*     */   {
/* 670 */     return this._table;
/*     */   }
/*     */ 
/*     */   protected void clearAllPositionStateInfo()
/*     */   {
/* 675 */     this._totalRowCount = -1;
/* 676 */     this._rowNum = -1;
/*     */   }
/*     */ 
/*     */   protected void setRowNum(int paramInt)
/*     */   {
/* 682 */     this._rowNum = paramInt;
/*     */   }
/*     */ 
/*     */   public int getRowNum()
/*     */   {
/* 687 */     return this._rowNum;
/*     */   }
/*     */ 
/*     */   public void setTotalRowCount(int paramInt)
/*     */   {
/* 693 */     this._totalRowCount = paramInt;
/*     */   }
/*     */ 
/*     */   public int getTotalRowCount()
/*     */   {
/* 698 */     return this._totalRowCount;
/*     */   }
/*     */ 
/*     */   private void checkState()
/*     */     throws SQLException
/*     */   {
/* 705 */     if ((this._state != 1) && (this._state != 2))
/*     */       return;
/* 707 */     ErrorMessage.raiseError("JZ00F");
/*     */   }
/*     */ 
/*     */   private void tdsCursor(String paramString, ParamManager paramParamManager, boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/* 715 */     this._tds._outStreamMgr.beginRequest(this._tpc);
/*     */     try
/*     */     {
/*     */       Object localObject2;
/* 718 */       if ((this._state == 0) || (this._state == 3))
/*     */       {
/* 720 */         this._id = 0;
/* 721 */         localObject1 = null;
/* 722 */         if (paramParamManager != null)
/*     */         {
/* 724 */           paramParamManager.checkParams(this._tds, false, false, -1);
/* 725 */           if (!paramBoolean)
/*     */           {
/* 727 */             localObject1 = paramParamManager.processParamMarkers(paramString);
/* 728 */             if (localObject1 != null)
/*     */             {
/* 730 */               paramString = (String)localObject1;
/*     */             }
/*     */           }
/*     */         }
/* 734 */         localObject2 = null;
/* 735 */         int i = 0;
/* 736 */         int j = 0;
/*     */ 
/* 746 */         if (this._tds.sendCurDeclare3())
/*     */         {
/* 748 */           j = 1;
/*     */         }
/*     */         else
/*     */         {
/* 766 */           long l = 65529 - getName().length();
/* 767 */           if (this._columns != null)
/*     */           {
/* 769 */             for (int k = 0; k < this._columns.length; ++k)
/*     */             {
/* 771 */               l -= 1L;
/* 772 */               l -= this._columns[k].length();
/*     */             }
/*     */           }
/* 775 */           if (paramString.length() > l)
/*     */           {
/* 777 */             i = 1;
/*     */           }
/*     */         }
/* 780 */         if (j != 0)
/*     */         {
/* 782 */           localObject2 = new CurDeclare3Token(this, paramString);
/*     */         }
/* 784 */         else if (i != 0)
/*     */         {
/* 789 */           if (!this._tds.isWidetableEnabled())
/*     */           {
/* 792 */             ErrorMessage.raiseError("JZ0PE");
/*     */           }
/*     */           else
/*     */           {
/* 797 */             localObject2 = new CurDeclare2Token(this, paramString);
/*     */           }
/*     */ 
/*     */         }
/*     */         else {
/* 802 */           localObject2 = new CurDeclareToken(this, paramString);
/*     */         }
/* 804 */         ((CurDeclareToken)localObject2).send(this._tds._out);
/*     */ 
/* 808 */         if ((!paramBoolean) && (this._hasArgs == 1))
/*     */         {
/* 811 */           buildParamfmtToken(paramParamManager);
/*     */         }
/*     */       }
/*     */ 
/* 815 */       if (this._fetchSize > 1)
/*     */       {
/* 817 */         localObject1 = new CurInfoToken(this);
/* 818 */         ((CurInfoToken)localObject1).send(this._tds._out, 1, this._fetchSize);
/*     */       }
/* 820 */       Object localObject1 = new CurOpenToken(this);
/* 821 */       ((CurOpenToken)localObject1).send(this._tds._out);
/*     */ 
/* 823 */       if (this._hasArgs == 1)
/*     */       {
/* 826 */         buildParamfmtToken(paramParamManager);
/* 827 */         localObject2 = new ParamsToken();
/* 828 */         ((ParamsToken)localObject2).send(this._tds._out);
/* 829 */         paramParamManager.send(this._tds._out);
/*     */       }
/* 831 */       this._tds._out.flush();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 835 */       this._tds._outStreamMgr.abortRequest(this._tpc);
/* 836 */       handleIOE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void buildParamfmtToken(ParamManager paramParamManager)
/*     */     throws IOException, SQLException
/*     */   {
/* 845 */     Object localObject = null;
/* 846 */     Param[] arrayOfParam = paramParamManager.getParams();
/*     */ 
/* 850 */     if (arrayOfParam[0] instanceof TdsParam2)
/*     */     {
/* 856 */       localObject = new ParamFormat2Token(paramParamManager, false);
/*     */     }
/*     */     else
/*     */     {
/* 868 */       localObject = new ParamFormatToken(paramParamManager, false);
/*     */     }
/* 870 */     ((ParamFormatToken)localObject).send(this._tds._out);
/*     */   }
/*     */ 
/*     */   private void languageCursor(String paramString, ParamManager paramParamManager)
/*     */     throws SQLException
/*     */   {
/* 876 */     String str = new String("DECLARE " + this._name + " CURSOR FOR ");
/*     */ 
/* 878 */     if ((paramParamManager != null) && (this._state == 0))
/*     */     {
/* 880 */       paramParamManager.adjustOffsets(str.length());
/*     */     }
/* 882 */     if ((this._state == 0) || (this._state == 3))
/*     */     {
/* 884 */       this._tds.language(this._tpc, str + paramString, paramParamManager);
/* 885 */       this._tds.getDoneResult(this._tpc);
/*     */     }
/*     */ 
/* 888 */     if (this._fetchSize > 1)
/*     */     {
/* 890 */       this._tds.language(this._tpc, "SET CURSOR ROWS " + this._fetchSize + " FOR " + this._name, null);
/*     */ 
/* 892 */       this._tds.getDoneResult(this._tpc);
/*     */     }
/*     */ 
/* 895 */     this._tds.language(this._tpc, "OPEN " + this._name, null);
/* 896 */     this._tds.getDoneResult(this._tpc);
/*     */ 
/* 898 */     if (this._utilStmt == null)
/*     */     {
/* 901 */       this._utilStmt = ((SybPreparedStatement)this._tpc._conn.prepareInternalStatement("FETCH " + getName(), false));
/*     */ 
/* 905 */       this._utilStmt.switchContext(this._tpc);
/*     */     }
/*     */ 
/* 908 */     this._utilStmt.sendQuery(null, null);
/*     */   }
/*     */ 
/*     */   protected void doCurInfo(int paramInt1, int paramInt2)
/*     */     throws SQLException
/*     */   {
/* 914 */     this._tds._outStreamMgr.getSendLock(this._curPC);
/*     */     try
/*     */     {
/* 917 */       if (!this._langCur)
/*     */       {
/*     */         try
/*     */         {
/* 921 */           this._tds._outStreamMgr.beginRequest(this._curPC);
/* 922 */           Object localObject1 = null;
/* 923 */           if (paramInt2 == 131)
/*     */           {
/* 925 */             localObject1 = new CurInfoToken(this);
/*     */           }
/*     */           else
/*     */           {
/* 929 */             localObject1 = new CurInfo3Token(this);
/*     */           }
/* 931 */           ((CurInfoToken)localObject1).send(this._tds._out, paramInt1, this._fetchSize);
/* 932 */           this._tds._out.flush();
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/* 936 */           this._tds._outStreamMgr.abortRequest(this._curPC);
/* 937 */           handleIOE(localIOException);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 944 */         this._tds.language(this._curPC, "SET CURSOR ROWS " + this._fetchSize + "FOR " + this._name, null);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 954 */       this._tds._outStreamMgr.endRequest(this._curPC);
/*     */     }
/* 956 */     this._tds.getDoneResult(this._curPC);
/*     */   }
/*     */ 
/*     */   private void handleIOE(IOException paramIOException)
/*     */     throws SQLException
/*     */   {
/* 963 */     ErrorMessage.raiseErrorCheckDead(paramIOException);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsCursor
 * JD-Core Version:    0.5.4
 */