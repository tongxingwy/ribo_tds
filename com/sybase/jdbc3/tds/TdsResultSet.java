/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.JdbcDataObject;
/*     */ import com.sybase.jdbc3.jdbc.Protocol;
/*     */ import com.sybase.jdbc3.jdbc.ProtocolResultSet;
/*     */ import com.sybase.jdbc3.jdbc.SybConnection;
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import com.sybase.jdbc3.utils.Chainable;
/*     */ import java.io.IOException;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class TdsResultSet
/*     */   implements ProtocolResultSet
/*     */ {
/*     */   protected static final int TYPE_TDSRESULTSET = 1;
/*     */   protected static final int TYPE_TDSPARAMSET_EED = 2;
/*     */   protected static final int TYPE_TDSPARAMSET_EVENT = 3;
/*     */   protected static final int TYPE_TDSPARAMSET_MSG = 4;
/*  57 */   protected TdsProtocolContext _tpc = null;
/*     */ 
/*  61 */   protected int _rowCount = 0;
/*     */ 
/*  64 */   protected boolean _needNext = true;
/*     */   protected SQLWarning _warning;
/*     */   protected TdsDataObject[] _columns;
/*  79 */   protected int _tdsResultSetType = 1;
/*     */   protected RowFormatToken _rowfmt;
/*  85 */   protected int _totalColumns = -1;
/*     */ 
/*  88 */   protected Vector _cachedRows = null;
/*     */ 
/*  91 */   protected int _rowIndex = 0;
/*     */ 
/*  95 */   protected boolean _dead = false;
/*     */ 
/*  98 */   protected int _type = 1003;
/*     */ 
/* 104 */   protected boolean _scrolling = false;
/*     */ 
/* 114 */   protected boolean _serverSideScrolling = false;
/*     */ 
/* 120 */   protected TdsDataObject[] _savedCols = null;
/*     */ 
/* 126 */   protected int _lastFetchSize = 1;
/*     */ 
/* 128 */   protected boolean _isAltResult = false;
/* 129 */   protected boolean _altFetchFinished = false;
/*     */ 
/* 132 */   protected int _totalCursorRows = 0;
/*     */ 
/*     */   protected TdsResultSet(TdsProtocolContext paramTdsProtocolContext)
/*     */     throws SQLException
/*     */   {
/* 139 */     this._tpc = paramTdsProtocolContext;
/*     */ 
/* 145 */     if (paramTdsProtocolContext._lastResult == 6)
/*     */     {
/* 147 */       int i = paramTdsProtocolContext.getCurrentAltRow().getId();
/* 148 */       this._rowfmt = paramTdsProtocolContext.getAltFmt(i);
/* 149 */       this._rowfmt.setPc(paramTdsProtocolContext);
/* 150 */       this._totalColumns = ((AltFormatToken)this._rowfmt).getFormatCount();
/* 151 */       this._isAltResult = true;
/*     */     }
/*     */     else
/*     */     {
/* 155 */       this._rowfmt = paramTdsProtocolContext.getFormat();
/* 156 */       this._rowfmt.setPc(paramTdsProtocolContext);
/* 157 */       this._totalColumns = this._rowfmt._numColumns;
/*     */     }
/*     */ 
/* 160 */     this._columns = new TdsDataObject[this._totalColumns];
/* 161 */     Object localObject = null;
/*     */     try
/*     */     {
/* 166 */       for (int j = 0; j < this._totalColumns; ++j)
/*     */       {
/* 168 */         DataFormat localDataFormat = this._rowfmt.getDataFormat(j);
/* 169 */         switch (localDataFormat._datatype)
/*     */         {
/*     */         case 38:
/*     */         case 48:
/*     */         case 52:
/*     */         case 56:
/*     */         case 65:
/*     */         case 66:
/*     */         case 191:
/* 178 */           this._columns[j] = new TdsInt(paramTdsProtocolContext);
/* 179 */           break;
/*     */         case 59:
/*     */         case 62:
/*     */         case 109:
/* 183 */           this._columns[j] = new TdsReal(paramTdsProtocolContext);
/* 184 */           break;
/*     */         case 68:
/* 186 */           if (localDataFormat._length < 8)
/*     */           {
/* 188 */             this._columns[j] = new TdsInt(paramTdsProtocolContext);
/*     */           }
/*     */           else
/*     */           {
/* 192 */             this._columns[j] = new TdsJdbcInputStream(null, paramTdsProtocolContext, (Tds)paramTdsProtocolContext._protocol);
/*     */           }
/*     */ 
/* 195 */           break;
/*     */         default:
/* 197 */           this._columns[j] = new TdsJdbcInputStream(null, paramTdsProtocolContext, (Tds)paramTdsProtocolContext._protocol);
/*     */         }
/*     */ 
/* 200 */         this._columns[j]._dataFmt = localDataFormat;
/*     */ 
/* 202 */         this._columns[j].setPrevious((Chainable)localObject);
/* 203 */         if (localObject != null)
/*     */         {
/* 205 */           ((TdsDataObject)localObject).setNext(this._columns[j]);
/*     */         }
/* 207 */         localObject = this._columns[j];
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 213 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected RowFormatToken getFormat()
/*     */   {
/* 219 */     return this._tpc._paramFmts;
/*     */   }
/*     */ 
/*     */   public void setType(int paramInt)
/*     */   {
/* 230 */     this._type = paramInt;
/* 231 */     if (this._type != 1004)
/*     */       return;
/* 233 */     this._cachedRows = new Vector();
/* 234 */     this._scrolling = true;
/*     */   }
/*     */ 
/*     */   public int getType()
/*     */   {
/* 243 */     return this._type;
/*     */   }
/*     */ 
/*     */   public void setLastFetchSize(int paramInt)
/*     */   {
/* 249 */     this._lastFetchSize = paramInt;
/*     */   }
/*     */ 
/*     */   public int getLastFetchSize()
/*     */   {
/* 254 */     return this._lastFetchSize;
/*     */   }
/*     */ 
/*     */   public boolean previous()
/*     */     throws SQLException
/*     */   {
/* 277 */     this._rowIndex -= 1;
/*     */ 
/* 280 */     if (this._rowIndex != 0)
/*     */     {
/* 282 */       this._columns = ((TdsDataObject[])this._cachedRows.get(this._rowIndex - 1));
/*     */     }
/*     */ 
/* 287 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean absolute(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 312 */     if ((this._rowIndex != paramInt) && (paramInt != 0))
/*     */     {
/* 314 */       this._rowIndex = paramInt;
/* 315 */       this._columns = ((TdsDataObject[])this._cachedRows.get(this._rowIndex - 1));
/*     */     }
/*     */ 
/* 320 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean relative(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 329 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isBeforeFirst()
/*     */     throws SQLException
/*     */   {
/* 338 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean first()
/*     */     throws SQLException
/*     */   {
/* 347 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isFirst()
/*     */     throws SQLException
/*     */   {
/* 356 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean last()
/*     */     throws SQLException
/*     */   {
/* 365 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isLast()
/*     */     throws SQLException
/*     */   {
/* 374 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isAfterLast()
/*     */     throws SQLException
/*     */   {
/* 383 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isResultSetEmpty()
/*     */     throws SQLException
/*     */   {
/* 392 */     return false;
/*     */   }
/*     */ 
/*     */   public int getRowNumber()
/*     */     throws SQLException
/*     */   {
/* 401 */     return -1;
/*     */   }
/*     */ 
/*     */   public int getNumRowsCached()
/*     */   {
/* 410 */     return (this._scrolling) ? this._cachedRows.size() : 0;
/*     */   }
/*     */ 
/*     */   public boolean next()
/*     */     throws SQLException
/*     */   {
/* 428 */     if (this._scrolling)
/*     */     {
/* 436 */       if (this._rowIndex < this._cachedRows.size())
/*     */       {
/* 438 */         this._rowIndex += 1;
/*     */ 
/* 444 */         this._columns = ((TdsDataObject[])this._cachedRows.get(this._rowIndex - 1));
/*     */ 
/* 446 */         return true;
/*     */       }
/*     */ 
/* 450 */       if (this._dead)
/*     */       {
/* 453 */         this._rowIndex += 1;
/* 454 */         return false;
/*     */       }
/*     */ 
/* 462 */       if ((this._rowIndex > 0) && (this._cachedRows.size() == 0))
/*     */       {
/* 464 */         this._cachedRows.setSize(this._rowIndex - 1);
/* 465 */         cacheCurrentRow();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 471 */     if (!this._needNext)
/*     */     {
/* 473 */       if (this._tpc._lastResult != 211)
/*     */       {
/* 475 */         this._tpc._lastResult = -1;
/*     */       }
/*     */       try
/*     */       {
/* 479 */         if (this._scrolling)
/*     */         {
/* 481 */           this._columns = this._savedCols;
/*     */         }
/*     */ 
/* 486 */         for (int i = 0; i < this._totalColumns; ++i)
/*     */         {
/* 488 */           this._columns[i].clear();
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 495 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*     */       }
/*     */     }
/*     */ 
/* 499 */     if (this._altFetchFinished)
/*     */     {
/* 501 */       markDead();
/* 502 */       this._rowIndex += 1;
/* 503 */       return false;
/*     */     }
/*     */ 
/* 506 */     int j = nextResult();
/*     */ 
/* 508 */     switch (j)
/*     */     {
/*     */     case 211:
/* 511 */       this._altFetchFinished = true;
/*     */     case 209:
/* 515 */       this._rowIndex += 1;
/* 516 */       this._rowCount += 1;
/* 517 */       if (this._scrolling)
/*     */       {
/* 520 */         cacheCurrentRow();
/*     */       }
/* 522 */       this._needNext = false;
/* 523 */       return true;
/*     */     case 5:
/* 527 */       this._rowCount = this._tpc._protocol.count(this._tpc);
/* 528 */       if (this._tpc._cursor != null)
/*     */       {
/* 530 */         this._totalCursorRows += this._rowCount;
/*     */       }
/*     */ 
/* 534 */       this._rowIndex += 1;
/* 535 */       markDead();
/* 536 */       return false;
/*     */     case 6:
/* 538 */       this._tpc._protocol.ungetResult(this._tpc, j);
/* 539 */       markDead();
/* 540 */       this._rowIndex += 1;
/* 541 */       return false;
/*     */     }
/*     */ 
/* 544 */     this._tpc._protocol.ungetResult(this._tpc, j);
/* 545 */     markDead();
/* 546 */     return false;
/*     */   }
/*     */ 
/*     */   public SQLWarning getWarnings()
/*     */     throws SQLException
/*     */   {
/* 557 */     return this._warning;
/*     */   }
/*     */ 
/*     */   public void clearWarnings()
/*     */     throws SQLException
/*     */   {
/* 568 */     this._warning = null;
/*     */   }
/*     */ 
/*     */   public ResultSetMetaData getMetaData()
/*     */     throws SQLException
/*     */   {
/* 576 */     return this._rowfmt;
/*     */   }
/*     */ 
/*     */   protected synchronized void markDead()
/*     */   {
/* 587 */     this._dead = true;
/*     */   }
/*     */ 
/*     */   public void close(boolean paramBoolean)
/*     */     throws SQLException
/*     */   {
/* 612 */     if (this._dead)
/*     */     {
/* 614 */       if (this._tpc._cursor == null)
/*     */       {
/* 616 */         switch (this._tdsResultSetType)
/*     */         {
/*     */         case 1:
/* 619 */           this._tpc._trs = null;
/* 620 */           break;
/*     */         case 2:
/* 623 */           this._tpc._trsForEed = null;
/* 624 */           break;
/*     */         case 3:
/* 627 */           this._tpc._trsForEvent = null;
/* 628 */           break;
/*     */         case 4:
/* 631 */           this._tpc._trsForMsg = null;
/*     */         }
/*     */       }
/*     */ 
/* 635 */       return;
/*     */     }
/*     */ 
/* 641 */     if (this._scrolling)
/*     */     {
/* 643 */       this._scrolling = false;
/* 644 */       if (this._savedCols != null)
/*     */       {
/* 646 */         this._columns = this._savedCols;
/*     */       }
/*     */     }
/*     */ 
/* 650 */     while ((!paramBoolean) && 
/* 653 */       (next()));
/* 658 */     if (this._tpc._cursor == null)
/*     */     {
/* 660 */       switch (this._tdsResultSetType)
/*     */       {
/*     */       case 1:
/* 663 */         this._tpc._trs = null;
/* 664 */         break;
/*     */       case 2:
/* 667 */         this._tpc._trsForEed = null;
/* 668 */         break;
/*     */       case 3:
/* 671 */         this._tpc._trsForEvent = null;
/* 672 */         break;
/*     */       case 4:
/* 675 */         this._tpc._trsForMsg = null;
/*     */       }
/*     */     }
/*     */ 
/* 679 */     markDead();
/*     */   }
/*     */ 
/*     */   public int getCount() throws SQLException
/*     */   {
/* 684 */     return this._rowCount;
/*     */   }
/*     */ 
/*     */   public JdbcDataObject getColumn(int paramInt)
/*     */     throws SQLException
/*     */   {
/* 693 */     int i = (this._isAltResult) ? paramInt - 1 : this._rowfmt.mapColumn(paramInt);
/* 694 */     return this._columns[i];
/*     */   }
/*     */ 
/*     */   public int findColumn(String paramString) throws SQLException
/*     */   {
/* 699 */     int i = 0;
/* 700 */     for (int j = 0; j < this._rowfmt._numColumns; ++j)
/*     */     {
/* 702 */       if ((this._rowfmt.getStatus(j) & 0x1) != 0)
/*     */         continue;
/* 704 */       ++i;
/*     */ 
/* 707 */       if (this._rowfmt.getName(j).equalsIgnoreCase(paramString))
/*     */       {
/* 709 */         return i;
/*     */       }
/*     */     }
/*     */ 
/* 713 */     ErrorMessage.raiseError("S0022", paramString);
/*     */ 
/* 715 */     return -1;
/*     */   }
/*     */ 
/*     */   public int findColumnByLabel(String paramString)
/*     */     throws SQLException
/*     */   {
/* 725 */     int i = 0;
/* 726 */     for (int j = 0; j < this._rowfmt._numColumns; ++j)
/*     */     {
/* 728 */       if ((this._rowfmt.getStatus(j) & 0x1) != 0)
/*     */         continue;
/* 730 */       ++i;
/*     */ 
/* 734 */       if (this._rowfmt.getLabel(j).equalsIgnoreCase(paramString))
/*     */       {
/* 736 */         return i;
/*     */       }
/*     */     }
/*     */ 
/* 740 */     ErrorMessage.raiseError("S0022", paramString);
/*     */ 
/* 742 */     return -1;
/*     */   }
/*     */ 
/*     */   protected void handleSQLE(SQLException paramSQLException)
/*     */     throws SQLException
/*     */   {
/* 749 */     boolean bool = SybConnection.thisChainHasAnException(paramSQLException);
/* 750 */     SQLWarning localSQLWarning = null;
/* 751 */     if (!bool)
/*     */     {
/* 753 */       localSQLWarning = (SQLWarning)paramSQLException;
/*     */     }
/* 757 */     else if (((Tds)this._tpc._protocol).getSybProperty().getBoolean(76))
/*     */     {
/* 760 */       paramSQLException = SybConnection.getAllExceptions(paramSQLException);
/*     */     }
/*     */     else
/*     */     {
/* 765 */       localSQLWarning = SybConnection.getAllTheWarnings(paramSQLException);
/*     */     }
/*     */ 
/* 768 */     if (localSQLWarning != null)
/*     */     {
/* 773 */       if (this._warning == null)
/*     */       {
/* 775 */         this._warning = localSQLWarning;
/*     */       }
/*     */       else
/*     */       {
/* 779 */         this._warning.setNextWarning(localSQLWarning);
/*     */       }
/*     */     }
/* 782 */     if (!bool)
/*     */       return;
/* 784 */     throw paramSQLException;
/*     */   }
/*     */ 
/*     */   protected int nextResult()
/*     */     throws SQLException
/*     */   {
/*     */     int i;
/*     */     while (true) {
/*     */       try
/*     */       {
/* 798 */         i = this._tpc._protocol.nextResult(this._tpc);
/*     */       }
/*     */       catch (SQLException localSQLException)
/*     */       {
/* 803 */         handleSQLE(localSQLException);
/*     */       }
/*     */     }
/*     */ 
/* 807 */     return i;
/*     */   }
/*     */ 
/*     */   protected void prepareForNextFetch()
/*     */   {
/* 821 */     this._needNext = true;
/* 822 */     if (!this._scrolling) {
/*     */       return;
/*     */     }
/*     */ 
/* 826 */     if (this._rowIndex != 0)
/*     */     {
/* 828 */       this._rowIndex -= 1;
/*     */     }
/* 830 */     this._dead = false;
/*     */   }
/*     */ 
/*     */   protected void cacheCurrentRow()
/*     */     throws SQLException
/*     */   {
/* 843 */     TdsDataObject[] arrayOfTdsDataObject = new TdsDataObject[this._totalColumns];
/*     */     try
/*     */     {
/* 849 */       for (int i = 0; i < this._totalColumns; ++i)
/*     */       {
/* 851 */         arrayOfTdsDataObject[i] = this._columns[i].createCachedCopy();
/*     */       }
/*     */ 
/* 857 */       this._cachedRows.insertElementAt(arrayOfTdsDataObject, this._rowIndex - 1);
/*     */ 
/* 859 */       this._savedCols = this._columns;
/*     */ 
/* 862 */       this._columns = arrayOfTdsDataObject;
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 869 */       ErrorMessage.raiseError("JZ006", localIOException.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void dump()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 894 */     this._rowCount = 0;
/* 895 */     this._needNext = true;
/* 896 */     this._warning = null;
/* 897 */     if (this._cachedRows != null)
/*     */     {
/* 899 */       this._cachedRows.clear();
/*     */     }
/* 901 */     this._rowIndex = 0;
/* 902 */     this._dead = false;
/* 903 */     this._lastFetchSize = 1;
/*     */ 
/* 905 */     for (int i = 0; i < this._totalColumns; ++i)
/*     */     {
/* 907 */       this._columns[i].initialize();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsResultSet
 * JD-Core Version:    0.5.4
 */