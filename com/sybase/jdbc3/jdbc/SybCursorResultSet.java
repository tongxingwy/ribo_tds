/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.tds.SybBigDecimal;
/*      */ import com.sybase.jdbc3.utils.CacheManager;
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import com.sybase.jdbc3.utils.LogUtil;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ public class SybCursorResultSet extends SybResultSet
/*      */   implements com.sybase.jdbcx.SybCursorResultSet
/*      */ {
/*   57 */   private static Logger LOG = Logger.getLogger(SybCursorResultSet.class.getName());
/*   58 */   private static volatile long _logIdCounter = 0L;
/*      */   protected Cursor _cursor;
/*      */   protected ParamManager _paramMgr;
/*      */   protected ParamManager _insertParamMgr;
/*   64 */   protected boolean _rowDeleted = false;
/*   65 */   protected boolean _rowUpdated = false;
/*   66 */   protected boolean _rowInserted = false;
/*      */ 
/*   70 */   private int _savedCurrentRow = -4;
/*      */ 
/*      */   protected SybCursorResultSet(String paramString, SybStatement paramSybStatement, ProtocolResultSet paramProtocolResultSet)
/*      */     throws SQLException
/*      */   {
/*   77 */     super(paramString, paramSybStatement, paramProtocolResultSet);
/*      */ 
/*   79 */     this._cursor = this._statement._cursor;
/*   80 */     this._concurType = this._cursor.getConcurrency();
/*      */ 
/*   82 */     if (this._concurType != 1007)
/*      */     {
/*   85 */       int i = paramProtocolResultSet.getMetaData().getColumnCount();
/*   86 */       this._paramMgr = new ParamManager(i, paramSybStatement);
/*   87 */       this._insertParamMgr = new ParamManager(i, paramSybStatement);
/*      */     }
/*   89 */     this._logId = (paramString + "_Cr" + _logIdCounter++);
/*      */   }
/*      */ 
/*      */   public String getCursorName()
/*      */     throws SQLException
/*      */   {
/*   97 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*   99 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  101 */       LOG.fine(this._logId + " getCursorName()");
/*      */     }
/*      */ 
/*  110 */     return this._cursor.getName();
/*      */   }
/*      */ 
/*      */   public int getConcurrency()
/*      */     throws SQLException
/*      */   {
/*  121 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  123 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  125 */       LOG.fine(this._logId + " getConcurrency()");
/*      */     }
/*      */ 
/*  129 */     if (this._cursor == null)
/*      */     {
/*  131 */       ErrorMessage.raiseError("JZ00D");
/*      */     }
/*  133 */     return this._cursor.getConcurrency();
/*      */   }
/*      */ 
/*      */   public boolean next()
/*      */     throws SQLException
/*      */   {
/*  141 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  143 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  145 */       LOG.fine(this._logId + " next()");
/*      */     }
/*      */ 
/*  152 */     checkResultSet();
/*  153 */     clearWarnings();
/*  154 */     moveToCurrentRow();
/*      */ 
/*  156 */     if (checkRowIndexBeforeProtocolNext())
/*      */     {
/*  158 */       return false;
/*      */     }
/*      */ 
/*  162 */     if (this._paramMgr != null)
/*      */     {
/*      */       try
/*      */       {
/*  167 */         this._paramMgr.clearParamArray(true);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*      */       }
/*      */     }
/*  173 */     this._rowDeleted = false;
/*  174 */     this._rowUpdated = false;
/*  175 */     this._rowInserted = false;
/*      */ 
/*  179 */     boolean bool = this._prs.next();
/*      */ 
/*  181 */     if (!bool)
/*      */     {
/*  183 */       prepareForNextFetch();
/*      */ 
/*  186 */       this._prs = this._cursor.fetch();
/*  187 */       if (this._prs == null)
/*      */       {
/*  189 */         bool = false;
/*      */       }
/*      */       else
/*      */       {
/*  193 */         bool = this._prs.next();
/*      */       }
/*      */     }
/*      */ 
/*  197 */     if (!bool)
/*      */     {
/*  205 */       if ((this._statement != null) && (this._prs != null))
/*      */       {
/*  207 */         this._statement.setRowCount(this._prs.getCount());
/*      */       }
/*      */ 
/*  210 */       adjustRowIndexesAfterProtocolNext();
/*      */     }
/*      */ 
/*  218 */     return bool;
/*      */   }
/*      */ 
/*      */   protected void prepareForNextFetch()
/*      */     throws SQLException
/*      */   {
/*  225 */     SQLWarning localSQLWarning = this._prs.getWarnings();
/*  226 */     this._prs.clearWarnings();
/*  227 */     if (this._savedWarnings != null)
/*      */     {
/*  229 */       this._savedWarnings.setNextWarning(localSQLWarning);
/*      */     }
/*      */     else
/*      */     {
/*  233 */       this._savedWarnings = localSQLWarning;
/*      */     }
/*  235 */     this._prs.close(false);
/*      */ 
/*  237 */     if (this._cm == null)
/*      */       return;
/*  239 */     this._cm.doneReading();
/*      */   }
/*      */ 
/*      */   public void moveToCurrentRow() throws SQLException
/*      */   {
/*  244 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  246 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  248 */       LOG.fine(this._logId + " moveToCurrentRow()");
/*      */     }
/*      */ 
/*  253 */     if (this._rowIndex != -3)
/*      */     {
/*      */       return;
/*      */     }
/*      */ 
/*  260 */     this._rowIndex = this._savedCurrentRow;
/*  261 */     this._rowInserted = false;
/*      */   }
/*      */ 
/*      */   public void moveToInsertRow()
/*      */     throws SQLException
/*      */   {
/*  267 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  269 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  271 */       LOG.fine(this._logId + " moveToInsertRow()");
/*      */     }
/*      */ 
/*  275 */     if (this._concurType == 1008)
/*      */     {
/*  278 */       if (this._rowIndex == -3)
/*      */       {
/*      */         return;
/*      */       }
/*      */ 
/*  286 */       this._savedCurrentRow = this._rowIndex;
/*  287 */       this._rowIndex = -3;
/*      */       try
/*      */       {
/*  293 */         this._insertParamMgr.clearParamArray(true);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  297 */         ErrorMessage.raiseError("JZ006", localIOException.getMessage());
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  304 */       ErrorMessage.raiseError("JZ0BT", "moveToInsertRow()", this._concurTypeString);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void close(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  330 */     this._cursor.close(true);
/*      */ 
/*  332 */     super.close(paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean isLanguageCursor()
/*      */   {
/*  345 */     return this._cursor.isLanguageCursor();
/*      */   }
/*      */ 
/*      */   public void setFetchSize(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  363 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  365 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  367 */         LOG.finer(this._logId + " setFetchSize(int = [" + paramInt + "])");
/*      */       }
/*  369 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  371 */         LOG.fine(this._logId + " setFetchSize(int)");
/*      */       }
/*      */     }
/*      */ 
/*  375 */     if (paramInt == 0)
/*      */     {
/*  377 */       return;
/*      */     }
/*      */ 
/*  380 */     int i = this._statement.getMaxRows();
/*      */ 
/*  383 */     if ((paramInt < 0) || ((i > 0) && (paramInt > i)))
/*      */     {
/*  386 */       ErrorMessage.raiseError("JZ0BI");
/*      */     }
/*      */ 
/*  389 */     this._cursor.setFetchSize(paramInt);
/*      */   }
/*      */ 
/*      */   public int getFetchSize()
/*      */     throws SQLException
/*      */   {
/*  402 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  404 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  406 */       LOG.fine(this._logId + " getFetchSize()");
/*      */     }
/*      */ 
/*  410 */     return this._cursor.getFetchSize();
/*      */   }
/*      */ 
/*      */   public void insertRow() throws SQLException
/*      */   {
/*  415 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  417 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  419 */       LOG.fine(this._logId + " insertRow()");
/*      */     }
/*      */ 
/*  423 */     checkUpdatability("insertRow()");
/*  424 */     if (this._rowIndex != -3)
/*      */     {
/*  426 */       ErrorMessage.raiseError("JZ0BR", "insertRow()");
/*      */     }
/*      */ 
/*  430 */     String str = "INSERT INTO " + this._cursor.getTable() + " ";
/*      */ 
/*  432 */     ResultSetMetaData localResultSetMetaData = this._prs.getMetaData();
/*  433 */     int i = localResultSetMetaData.getColumnCount();
/*  434 */     Param[] arrayOfParam = this._insertParamMgr.getParams();
/*  435 */     int j = 1;
/*      */ 
/*  440 */     for (int k = 0; k < i; ++k)
/*      */     {
/*  442 */       if (arrayOfParam[k]._sqlType == -999) {
/*      */         continue;
/*      */       }
/*  445 */       if (j == 0)
/*      */       {
/*  447 */         str = str + ", ";
/*      */       }
/*      */       else
/*      */       {
/*  453 */         str = str + "( ";
/*      */       }
/*  455 */       str = str + localResultSetMetaData.getColumnName(k + 1);
/*  456 */       j = 0;
/*      */     }
/*      */ 
/*  459 */     if (j == 0)
/*      */     {
/*  462 */       str = str + " ) ";
/*  463 */       j = 1;
/*      */     }
/*      */ 
/*  467 */     str = str + "VALUES (";
/*      */ 
/*  470 */     k = str.length();
/*      */ 
/*  475 */     for (int l = 0; l < i; ++l)
/*      */     {
/*  477 */       if (arrayOfParam[l]._sqlType == -999)
/*      */       {
/*  479 */         arrayOfParam[l]._sqlType = -998;
/*      */       }
/*      */       else
/*      */       {
/*  483 */         if (j == 0)
/*      */         {
/*  485 */           str = str + ", ";
/*  486 */           k += 2;
/*      */         }
/*  488 */         str = str + "?";
/*  489 */         j = 0;
/*  490 */         arrayOfParam[l]._paramMarkerOffset = (k++);
/*      */       }
/*      */     }
/*  493 */     str = str + ")";
/*      */ 
/*  497 */     if (1 != this._cursor.insert(this._prs, this._insertParamMgr, str))
/*      */       return;
/*  499 */     this._rowInserted = true;
/*      */   }
/*      */ 
/*      */   public boolean rowUpdated()
/*      */     throws SQLException
/*      */   {
/*  513 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  515 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  517 */       LOG.fine(this._logId + " rowUpdated()");
/*      */     }
/*      */ 
/*  521 */     return this._rowUpdated;
/*      */   }
/*      */ 
/*      */   public boolean rowDeleted()
/*      */     throws SQLException
/*      */   {
/*  534 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  536 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  538 */       LOG.fine(this._logId + " rowDeleted()");
/*      */     }
/*      */ 
/*  542 */     return this._rowDeleted;
/*      */   }
/*      */ 
/*      */   public boolean rowInserted()
/*      */     throws SQLException
/*      */   {
/*  557 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  559 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  561 */       LOG.fine(this._logId + " rowInserted()");
/*      */     }
/*      */ 
/*  565 */     return this._rowInserted;
/*      */   }
/*      */ 
/*      */   public void updateNull(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  584 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  586 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  588 */         LOG.finer(this._logId + " updateNull(int = [" + paramInt + "])");
/*      */       }
/*  591 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  593 */         LOG.fine(this._logId + " updateNull(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  598 */     checkUpdatability("updateNull(int)");
/*  599 */     Object localObject = null;
/*      */ 
/*  604 */     int i = getMetaData().getColumnType(paramInt);
/*      */ 
/*  607 */     if (i == 2005)
/*      */     {
/*  609 */       localObject = this._pc._conn._nullClob;
/*      */     }
/*  615 */     else if (i == 2004)
/*      */     {
/*  617 */       localObject = this._pc._conn._nullBlob;
/*      */     }
/*  619 */     setParam(i, paramInt, localObject);
/*      */   }
/*      */ 
/*      */   public void updateBoolean(int paramInt, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  632 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  634 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  636 */         LOG.finer(this._logId + " updatedBoolean(int = [" + paramInt + "], boolean = [" + paramBoolean + "])");
/*      */       }
/*  639 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  641 */         LOG.fine(this._logId + " updateBoolean(int, boolean)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  646 */     checkUpdatability("updateBoolean(int, boolean)");
/*  647 */     Boolean localBoolean = new Boolean(paramBoolean);
/*  648 */     setParam(-7, paramInt, localBoolean);
/*      */   }
/*      */ 
/*      */   public void updateByte(int paramInt, byte paramByte)
/*      */     throws SQLException
/*      */   {
/*  661 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  663 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  665 */         LOG.finer(this._logId + " updateByte(int = [" + paramInt + "], byte = [" + paramByte + "])");
/*      */       }
/*  668 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  670 */         LOG.fine(this._logId + " updateByte(int, byte)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  675 */     checkUpdatability("updateByte(int, byte)");
/*  676 */     Integer localInteger = new Integer(paramByte);
/*  677 */     if (localInteger.intValue() < 0)
/*      */     {
/*  683 */       setParam(5, paramInt, localInteger);
/*      */     }
/*      */     else
/*      */     {
/*  687 */       setParam(-6, paramInt, localInteger);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateShort(int paramInt, short paramShort)
/*      */     throws SQLException
/*      */   {
/*  701 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  703 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  705 */         LOG.finer(this._logId + " updateShort(int = [" + paramInt + "], short = [" + paramShort + "])");
/*      */       }
/*  708 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  710 */         LOG.fine(this._logId + " updateShort(int, short)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  715 */     checkUpdatability("updateShort(int, short)");
/*  716 */     Integer localInteger = new Integer(paramShort);
/*  717 */     setParam(5, paramInt, localInteger);
/*      */   }
/*      */ 
/*      */   public void updateInt(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  730 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  732 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  734 */         LOG.finer(this._logId + " updateInt(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */       }
/*  737 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  739 */         LOG.fine(this._logId + " updateInt(int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  744 */     checkUpdatability("updateInt(int, int)");
/*  745 */     Integer localInteger = new Integer(paramInt2);
/*  746 */     setParam(4, paramInt1, localInteger);
/*      */   }
/*      */ 
/*      */   public void updateLong(int paramInt, long paramLong)
/*      */     throws SQLException
/*      */   {
/*  759 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  761 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  763 */         LOG.finer(this._logId + " updateLong(int = [" + paramInt + "], long = [" + paramLong + "])");
/*      */       }
/*  766 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  768 */         LOG.fine(this._logId + " updateLong(int, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  773 */     checkUpdatability("updateLong(int, long)");
/*  774 */     Long localLong = new Long(paramLong);
/*  775 */     setParam(-5, paramInt, localLong);
/*      */   }
/*      */ 
/*      */   public void updateFloat(int paramInt, float paramFloat)
/*      */     throws SQLException
/*      */   {
/*  788 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  790 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  792 */         LOG.finer(this._logId + " updateFloat(int = [" + paramInt + "], float = [" + paramFloat + "])");
/*      */       }
/*  795 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  797 */         LOG.fine(this._logId + " updateFloat(int, float)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  802 */     checkUpdatability("updateFloat(int, float)");
/*  803 */     Float localFloat = new Float(paramFloat);
/*  804 */     setParam(7, paramInt, localFloat);
/*      */   }
/*      */ 
/*      */   public void updateDouble(int paramInt, double paramDouble)
/*      */     throws SQLException
/*      */   {
/*  817 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  819 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  821 */         LOG.finer(this._logId + " updateDouble(int = [" + paramInt + "], double = [" + paramDouble + "])");
/*      */       }
/*  824 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  826 */         LOG.fine(this._logId + " updateDouble(int, double)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  831 */     checkUpdatability("updateDouble(int, double)");
/*  832 */     Double localDouble = new Double(paramDouble);
/*  833 */     setParam(8, paramInt, localDouble);
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
/*      */     throws SQLException
/*      */   {
/*  847 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  849 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  851 */         LOG.finer(this._logId + " updateBigDecimal(int = [" + paramInt + "], BigDecimal = [" + paramBigDecimal + "])");
/*      */       }
/*  854 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  856 */         LOG.fine(this._logId + " updateBigDecimal(int, BigDecimal)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  861 */     checkUpdatability("updateBigDecimal(int, BigDecimal)");
/*  862 */     if (paramBigDecimal == null)
/*      */     {
/*  864 */       setParam(2, paramInt, paramBigDecimal);
/*      */     }
/*      */     else
/*      */     {
/*  869 */       setParam(2, paramInt, paramBigDecimal, paramBigDecimal.scale());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(int paramInt1, BigDecimal paramBigDecimal, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*  887 */     checkUpdatability("updateBigDecimal(int, BigDecimal, int, int)");
/*  888 */     if (paramBigDecimal == null)
/*      */     {
/*  893 */       paramBigDecimal = new BigDecimal("0");
/*      */     }
/*  895 */     SybBigDecimal localSybBigDecimal = new SybBigDecimal(paramBigDecimal, paramInt2, paramInt3);
/*  896 */     setParam(2, paramInt1, localSybBigDecimal);
/*      */   }
/*      */ 
/*      */   public void updateString(int paramInt, String paramString)
/*      */     throws SQLException
/*      */   {
/*  909 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  911 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  913 */         LOG.finer(this._logId + " updateString(int = [" + paramInt + "], String = [" + paramString + "])");
/*      */       }
/*  916 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  918 */         LOG.fine(this._logId + " updateString(int, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  923 */     checkUpdatability("updateString(int, String)");
/*  924 */     setParam(12, paramInt, paramString);
/*      */   }
/*      */ 
/*      */   public void updateBytes(int paramInt, byte[] paramArrayOfByte)
/*      */     throws SQLException
/*      */   {
/*  937 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  939 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  941 */         LOG.finest(LogUtil.logMethod(false, this._logId, "updateBytes", new Object[] { new Integer(paramInt), paramArrayOfByte }));
/*      */       }
/*  944 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  946 */         LOG.finer(LogUtil.logMethod(true, this._logId, "updateBytes", new Object[] { new Integer(paramInt), paramArrayOfByte }));
/*      */       }
/*  949 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  951 */         LOG.fine(this._logId + " updateBytes(int, byte)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  956 */     checkUpdatability("updateBytes(int, byte[])");
/*  957 */     int i = 0;
/*  958 */     if (paramArrayOfByte != null)
/*      */     {
/*  960 */       i = paramArrayOfByte.length;
/*      */     }
/*  962 */     if (i > 255)
/*      */     {
/*  964 */       setParam(-4, paramInt, paramArrayOfByte);
/*      */     }
/*      */     else
/*      */     {
/*  968 */       setParam(-3, paramInt, paramArrayOfByte);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateDate(int paramInt, Date paramDate)
/*      */     throws SQLException
/*      */   {
/*  983 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  985 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  987 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateDate", new Object[] { new Integer(paramInt), paramDate }));
/*      */       }
/*  990 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  992 */         LOG.fine(this._logId + " updateDate(int, java.sql.Date)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  997 */     checkUpdatability("updateDate(int, java.sql.Date)");
/*  998 */     setParam(91, paramInt, new DateObject(paramDate, 91));
/*      */   }
/*      */ 
/*      */   public void updateTime(int paramInt, Time paramTime)
/*      */     throws SQLException
/*      */   {
/* 1012 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1014 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1016 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateTime", new Object[] { new Integer(paramInt), paramTime }));
/*      */       }
/* 1019 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1021 */         LOG.fine(this._logId + " updateTime(int, java.sql.Time)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1026 */     checkUpdatability("updateTime(int, java.sql.Time)");
/* 1027 */     setParam(92, paramInt, new DateObject(paramTime, 92));
/*      */   }
/*      */ 
/*      */   public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
/*      */     throws SQLException
/*      */   {
/* 1042 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1044 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1046 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateTimestamp", new Object[] { new Integer(paramInt), paramTimestamp }));
/*      */       }
/* 1049 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1051 */         LOG.fine(this._logId + " updateTimestamp(int, java.sql.Timestamp)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1056 */     checkUpdatability("updateTimestamp(int, java.sql.Timestamp)");
/* 1057 */     setParam(93, paramInt, new DateObject(paramTimestamp, 93));
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1074 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1076 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1078 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/* 1082 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1084 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/* 1087 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1089 */         LOG.fine(this._logId + " updateAsciiStream(int, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1095 */     checkUpdatability("updateAsciiStream(int, java.io.InputStream, int)");
/*      */     try
/*      */     {
/* 1106 */       InputStreamReader localInputStreamReader = new InputStreamReader(new LimiterInputStream(paramInputStream, paramInt2), "ISO8859_1");
/*      */ 
/* 1109 */       String str = this._paramMgr.drainReader(localInputStreamReader, paramInt2);
/* 1110 */       if (str != null)
/*      */       {
/* 1112 */         setParam(12, paramInt1, str, 0);
/*      */       }
/*      */       else
/*      */       {
/* 1116 */         setParam(-1, paramInt1, localInputStreamReader, paramInt2);
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1121 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1144 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1146 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1148 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/* 1152 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1154 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/* 1158 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1160 */         LOG.fine(this._logId + " updateBinaryStream(int, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1166 */     checkUpdatability("updateBinaryStream(int, java.io.InputStream, int)");
/* 1167 */     byte[] arrayOfByte = this._paramMgr.drainStreams(paramInputStream, paramInt2);
/* 1168 */     if (arrayOfByte != null)
/*      */     {
/* 1170 */       updateBytes(paramInt1, arrayOfByte);
/*      */     }
/*      */     else
/*      */     {
/* 1174 */       setParam(-4, paramInt1, paramInputStream, paramInt2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1190 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1192 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1194 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { new Integer(paramInt1), paramReader, new Integer(paramInt2) }));
/*      */       }
/* 1198 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1200 */         LOG.fine(this._logId + " updateChracterStream(int, java.io.Redear, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1206 */     checkUpdatability("updateCharacterStream(int, java.io.Reader, int)");
/*      */ 
/* 1209 */     String str = this._paramMgr.drainReader(paramReader, paramInt2);
/* 1210 */     if (str != null)
/*      */     {
/* 1212 */       setParam(12, paramInt1, str, 0);
/*      */     }
/*      */     else
/*      */     {
/* 1216 */       setParam(-1, paramInt1, paramReader, paramInt2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateObject(int paramInt1, Object paramObject, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1234 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1236 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1238 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateObject", new Object[] { new Integer(paramInt1), paramObject, new Integer(paramInt2) }));
/*      */       }
/* 1241 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1243 */         LOG.fine(this._logId + " updateObject(int, Object, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1248 */     checkUpdatability("updateObject(int, Object, int)");
/* 1249 */     if (paramObject == null)
/*      */     {
/* 1255 */       updateNull(paramInt1);
/*      */     }
/* 1257 */     else if (paramObject instanceof String)
/*      */     {
/* 1259 */       updateString(paramInt1, (String)paramObject);
/*      */     }
/* 1261 */     else if (paramObject instanceof BigDecimal)
/*      */     {
/* 1263 */       setParam(2, paramInt1, paramObject, paramInt2);
/*      */     }
/* 1265 */     else if (paramObject instanceof Boolean)
/*      */     {
/* 1267 */       setParam(-7, paramInt1, paramObject, paramInt2);
/*      */     }
/* 1269 */     else if (paramObject instanceof Integer)
/*      */     {
/* 1271 */       setParam(4, paramInt1, paramObject, paramInt2);
/*      */     }
/* 1273 */     else if (paramObject instanceof Long)
/*      */     {
/* 1275 */       setParam(-5, paramInt1, paramObject, paramInt2);
/*      */     }
/* 1277 */     else if (paramObject instanceof Float)
/*      */     {
/* 1279 */       setParam(7, paramInt1, paramObject, paramInt2);
/*      */     }
/* 1281 */     else if (paramObject instanceof Double)
/*      */     {
/* 1283 */       setParam(8, paramInt1, paramObject, paramInt2);
/*      */     }
/* 1285 */     else if (paramObject instanceof byte[])
/*      */     {
/* 1287 */       updateBytes(paramInt1, (byte[])paramObject);
/*      */     }
/* 1289 */     else if (paramObject instanceof Date)
/*      */     {
/* 1291 */       updateDate(paramInt1, (Date)paramObject);
/*      */     }
/* 1293 */     else if (paramObject instanceof Time)
/*      */     {
/* 1295 */       updateTime(paramInt1, (Time)paramObject);
/*      */     }
/* 1297 */     else if (paramObject instanceof Timestamp)
/*      */     {
/* 1299 */       updateTimestamp(paramInt1, (Timestamp)paramObject);
/*      */     }
/* 1301 */     else if (paramObject instanceof Clob)
/*      */     {
/* 1303 */       updateClob(paramInt1, (Clob)paramObject);
/*      */     }
/* 1309 */     else if (paramObject instanceof Blob)
/*      */     {
/* 1311 */       updateBlob(paramInt1, (Blob)paramObject);
/*      */     }
/* 1313 */     else if (paramObject instanceof Serializable)
/*      */     {
/* 1316 */       setParam(2000, paramInt1, paramObject, paramInt2);
/*      */     }
/*      */     else
/*      */     {
/* 1320 */       ErrorMessage.raiseError("JZ0SE");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateObject(int paramInt, Object paramObject)
/*      */     throws SQLException
/*      */   {
/* 1334 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1336 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1338 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateObject", new Object[] { new Integer(paramInt), paramObject }));
/*      */       }
/* 1341 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1343 */         LOG.fine(this._logId + " updateObject(int, Object)");
/*      */       }
/*      */     }
/*      */ 
/* 1347 */     checkUpdatability("updateObject(int, Object)");
/* 1348 */     int i = 0;
/*      */ 
/* 1351 */     if ((paramObject != null) && (paramObject instanceof BigDecimal))
/*      */     {
/* 1353 */       i = ((BigDecimal)paramObject).scale();
/*      */     }
/* 1355 */     updateObject(paramInt, paramObject, i);
/*      */   }
/*      */ 
/*      */   public void updateNull(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1367 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1369 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1371 */         LOG.finer(this._logId + " updateNull(String = [" + paramString + "])");
/*      */       }
/* 1375 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1377 */         LOG.fine(this._logId + " updateNull(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1382 */     checkUpdatability("updateNull(String)");
/* 1383 */     updateNull(findColumn(paramString));
/*      */   }
/*      */ 
/*      */   public void updateBoolean(String paramString, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 1396 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1398 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1400 */         LOG.finer(this._logId + " updateBoolean(String = [" + paramString + "], boolean = [" + paramBoolean + "])");
/*      */       }
/* 1403 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1405 */         LOG.fine(this._logId + " updateBoolean(String, boolean)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1410 */     checkUpdatability("updateBoolean(String, boolean)");
/* 1411 */     updateBoolean(findColumn(paramString), paramBoolean);
/*      */   }
/*      */ 
/*      */   public void updateByte(String paramString, byte paramByte)
/*      */     throws SQLException
/*      */   {
/* 1424 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1426 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1428 */         LOG.finer(this._logId + " updateByte(String = [" + paramString + "], byte = [" + paramByte + "])");
/*      */       }
/* 1431 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1433 */         LOG.fine(this._logId + " updateByte(String, byte)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1438 */     checkUpdatability("updateByte(String, byte)");
/* 1439 */     updateByte(findColumn(paramString), paramByte);
/*      */   }
/*      */ 
/*      */   public void updateShort(String paramString, short paramShort)
/*      */     throws SQLException
/*      */   {
/* 1452 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1454 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1456 */         LOG.finer(this._logId + " updateShort(String = [" + paramString + "], short = [" + paramShort + "])");
/*      */       }
/* 1459 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1461 */         LOG.fine(this._logId + " updateShort(String, short)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1466 */     checkUpdatability("updateShort(String, short)");
/* 1467 */     updateShort(findColumn(paramString), paramShort);
/*      */   }
/*      */ 
/*      */   public void updateInt(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1480 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1482 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1484 */         LOG.finer(this._logId + " updateInt(String = [" + paramString + "], int = [" + paramInt + "])");
/*      */       }
/* 1487 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1489 */         LOG.fine(this._logId + " updateInt(String, int)");
/*      */       }
/*      */     }
/*      */ 
/* 1493 */     checkUpdatability("updateInt(String, int)");
/* 1494 */     updateInt(findColumn(paramString), paramInt);
/*      */   }
/*      */ 
/*      */   public void updateLong(String paramString, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 1507 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1509 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1511 */         LOG.finer(this._logId + " updateLong(String = [" + paramString + "], long = [" + paramLong + "])");
/*      */       }
/* 1514 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1516 */         LOG.fine(this._logId + " updateLong(String, long)");
/*      */       }
/*      */     }
/*      */ 
/* 1520 */     checkUpdatability("updateLong(String, long)");
/* 1521 */     updateLong(findColumn(paramString), paramLong);
/*      */   }
/*      */ 
/*      */   public void updateFloat(String paramString, float paramFloat)
/*      */     throws SQLException
/*      */   {
/* 1534 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1536 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1538 */         LOG.finer(this._logId + " updateFloat(String = [" + paramString + "], float = [" + paramFloat + "])");
/*      */       }
/* 1541 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1543 */         LOG.fine(this._logId + " updateFloat(String, float)");
/*      */       }
/*      */     }
/*      */ 
/* 1547 */     checkUpdatability("updateFloat(String, float)");
/* 1548 */     updateFloat(findColumn(paramString), paramFloat);
/*      */   }
/*      */ 
/*      */   public void updateDouble(String paramString, double paramDouble)
/*      */     throws SQLException
/*      */   {
/* 1561 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1563 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1565 */         LOG.finer(this._logId + " updateDouble(String = [" + paramString + "], double = [" + paramDouble + "])");
/*      */       }
/* 1568 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1570 */         LOG.fine(this._logId + " updateDouble(String, double)");
/*      */       }
/*      */     }
/*      */ 
/* 1574 */     checkUpdatability("updateDouble(String, double)");
/* 1575 */     updateDouble(findColumn(paramString), paramDouble);
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal)
/*      */     throws SQLException
/*      */   {
/* 1589 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1591 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1593 */         LOG.finer(this._logId + " updateBigDecimal(String = [" + paramString + "], BigDecimal = [" + paramBigDecimal + "])");
/*      */       }
/* 1596 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1598 */         LOG.fine(this._logId + " updateBigDecimal(String, BigDecimal)");
/*      */       }
/*      */     }
/*      */ 
/* 1602 */     checkUpdatability("updateBigDecimal(String, BigDecimal)");
/* 1603 */     updateBigDecimal(findColumn(paramString), paramBigDecimal);
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1619 */     checkUpdatability("updateBigDecimal(String, BigDecimal, int, int)");
/* 1620 */     updateBigDecimal(findColumn(paramString), paramBigDecimal, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public void updateString(String paramString1, String paramString2)
/*      */     throws SQLException
/*      */   {
/* 1633 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1635 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1637 */         LOG.finer(this._logId + " updateString(String = [" + paramString1 + "], String = [" + paramString2 + "])");
/*      */       }
/* 1640 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1642 */         LOG.fine(this._logId + " updateString(String, String)");
/*      */       }
/*      */     }
/*      */ 
/* 1646 */     checkUpdatability("updateString(String, String)");
/* 1647 */     updateString(findColumn(paramString1), paramString2);
/*      */   }
/*      */ 
/*      */   public void updateBytes(String paramString, byte[] paramArrayOfByte)
/*      */     throws SQLException
/*      */   {
/* 1660 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1662 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1664 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBytes", new Object[] { paramString, paramArrayOfByte }));
/*      */       }
/* 1667 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1669 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBytes", new Object[] { paramString, paramArrayOfByte }));
/*      */       }
/* 1672 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1674 */         LOG.fine(this._logId + " updateBytes(String, byte)");
/*      */       }
/*      */     }
/*      */ 
/* 1678 */     checkUpdatability("updateBytes(String, byte[])");
/* 1679 */     updateBytes(findColumn(paramString), paramArrayOfByte);
/*      */   }
/*      */ 
/*      */   public void updateDate(String paramString, Date paramDate)
/*      */     throws SQLException
/*      */   {
/* 1693 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1695 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1697 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateDate", new Object[] { paramString, paramDate }));
/*      */       }
/* 1700 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1702 */         LOG.fine(this._logId + " updateDate(String, java.sql.Date)");
/*      */       }
/*      */     }
/*      */ 
/* 1706 */     checkUpdatability("updateDate(String, java.sql.Date)");
/* 1707 */     updateDate(findColumn(paramString), paramDate);
/*      */   }
/*      */ 
/*      */   public void updateTime(String paramString, Time paramTime)
/*      */     throws SQLException
/*      */   {
/* 1721 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1723 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1725 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateTime", new Object[] { paramString, paramTime }));
/*      */       }
/* 1728 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1730 */         LOG.fine(this._logId + " updateTime(String, java.sql.Time)");
/*      */       }
/*      */     }
/*      */ 
/* 1734 */     checkUpdatability("updateTime(String, java.sql.Time)");
/* 1735 */     updateTime(findColumn(paramString), paramTime);
/*      */   }
/*      */ 
/*      */   public void updateTimestamp(String paramString, Timestamp paramTimestamp)
/*      */     throws SQLException
/*      */   {
/* 1749 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1751 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1753 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateTimestamp", new Object[] { paramString, paramTimestamp }));
/*      */       }
/* 1756 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1758 */         LOG.fine(this._logId + " updateTimestamp(String, java.sql.Timestamp)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1763 */     checkUpdatability("updateTimestamp(String, java.sql.Timestamp)");
/* 1764 */     updateTimestamp(findColumn(paramString), paramTimestamp);
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1779 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1781 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1783 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 1787 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1789 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 1792 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1794 */         LOG.fine(this._logId + " updateAsciiStream(String, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1799 */     checkUpdatability("updateAsciiStream(String, java.io.InputStream, int)");
/*      */ 
/* 1801 */     updateAsciiStream(findColumn(paramString), paramInputStream, paramInt);
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1816 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1818 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1820 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 1824 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1826 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream, new Integer(paramInt) }));
/*      */       }
/* 1830 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1832 */         LOG.fine(this._logId + " updateBinaryStream(String, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1837 */     checkUpdatability("updateBinaryStream(String, java.io.InputStream, int)");
/*      */ 
/* 1839 */     updateBinaryStream(findColumn(paramString), paramInputStream, paramInt);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(String paramString, Reader paramReader, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1854 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1856 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1858 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { paramString, paramReader, new Integer(paramInt) }));
/*      */       }
/* 1862 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1864 */         LOG.fine(this._logId + " updateCharacterStream(String, java.io.Reader, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1869 */     checkUpdatability("updateCharacterStream(String, java.io.Reader, int)");
/*      */ 
/* 1871 */     updateCharacterStream(findColumn(paramString), paramReader, paramInt);
/*      */   }
/*      */ 
/*      */   public void updateObject(String paramString, Object paramObject, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1889 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1891 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1893 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateObject", new Object[] { paramString, paramObject, new Integer(paramInt) }));
/*      */       }
/* 1896 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1898 */         LOG.fine(this._logId + " updateObject(String, Object, int)");
/*      */       }
/*      */     }
/*      */ 
/* 1902 */     checkUpdatability("updateObject(String, Object, int)");
/* 1903 */     int i = 0;
/*      */ 
/* 1906 */     if ((paramObject != null) && (paramObject instanceof BigDecimal))
/*      */     {
/* 1908 */       i = ((BigDecimal)paramObject).scale();
/*      */     }
/* 1910 */     updateObject(findColumn(paramString), paramObject, i);
/*      */   }
/*      */ 
/*      */   public void updateObject(String paramString, Object paramObject)
/*      */     throws SQLException
/*      */   {
/* 1924 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1926 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1928 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateObject", new Object[] { paramString, paramObject }));
/*      */       }
/* 1931 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1933 */         LOG.fine(this._logId + " updateObject(String, Object)");
/*      */       }
/*      */     }
/*      */ 
/* 1937 */     checkUpdatability("updateObject(String, Object)");
/* 1938 */     updateObject(findColumn(paramString), paramObject);
/*      */   }
/*      */ 
/*      */   public void updateNCharacterStream(String paramString, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 2034 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2036 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2038 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateNCharacterStream", new Object[] { paramString, paramReader }));
/*      */       }
/* 2042 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2044 */         LOG.fine(this._logId + " updateNCharacterStream(String, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 2048 */     checkUpdatability("updateNCharacterStream(String, java.io.Reader)");
/* 2049 */     updateNCharacterStream(findColumn(paramString), paramReader);
/*      */   }
/*      */ 
/*      */   public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2093 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2095 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2097 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateNCharacterStream", new Object[] { paramString, paramReader, new Long(paramLong) }));
/*      */       }
/* 2101 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2103 */         LOG.fine(this._logId + " updateNCharacterStream(String, Reader, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2108 */     checkUpdatability("updateNCharacterStream(String, java.io.Reader, long)");
/* 2109 */     updateNCharacterStream(findColumn(paramString), paramReader, paramLong);
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 2122 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2124 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2126 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 2129 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2131 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 2134 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2136 */         LOG.fine(this._logId + " updateAsciiStream(int, InputStream)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2141 */     checkUpdatability("updateAsciiStream(int, java.io.InputStream)");
/* 2142 */     updateAsciiStream(paramInt, paramInputStream, -1);
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 2155 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2157 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2159 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2162 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2164 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2167 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2169 */         LOG.fine(this._logId + " updateAsciiStream(String, InputStream)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2174 */     checkUpdatability("updateAsciiStream(String, java.io.InputStream)");
/* 2175 */     updateAsciiStream(findColumn(paramString), paramInputStream);
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2190 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2192 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2194 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2198 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2200 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2203 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2205 */         LOG.fine(this._logId + " updateAsciiStream(int, InputStream, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2210 */     checkUpdatability("updateAsciiStream(int, java.io.InputStream, long)");
/* 2211 */     int i = checkLongLength(paramLong);
/* 2212 */     updateAsciiStream(paramInt, paramInputStream, i);
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2227 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2229 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2231 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2235 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2237 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateAsciiStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2240 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2242 */         LOG.fine(this._logId + " updateAsciiStream(String, InputStream, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2248 */     checkUpdatability("updateAsciiStream(int, java.io.InputStream, long)");
/* 2249 */     updateAsciiStream(findColumn(paramString), paramInputStream, paramLong);
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 2263 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2265 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2267 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 2270 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2272 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 2275 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2277 */         LOG.fine(this._logId + " updateBinaryStream(int, InputStream)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2282 */     checkUpdatability("updateBinaryStream(int, java.io.InputStream)");
/* 2283 */     updateBinaryStream(paramInt, paramInputStream, -1);
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 2297 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2299 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2301 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2304 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2306 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2309 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2311 */         LOG.fine(this._logId + " updateBinaryStream(String, InputStream)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2316 */     checkUpdatability("updateBinaryStream(String, java.io.InputStream)");
/* 2317 */     updateBinaryStream(findColumn(paramString), paramInputStream, -1);
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2332 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2334 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2336 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2340 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2342 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2346 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2348 */         LOG.fine(this._logId + " updateBinaryStream(int, InputStream, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2354 */     checkUpdatability("updateBinaryStream(int, java.io.InputStream, long)");
/* 2355 */     int i = checkLongLength(paramLong);
/* 2356 */     updateBinaryStream(paramInt, paramInputStream, i);
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2371 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2373 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2375 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2379 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2381 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBinaryStream", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2385 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2387 */         LOG.fine(this._logId + " updateBinaryStream(String, InputStream, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2393 */     checkUpdatability("updateBinaryStream(int, java.io.InputStream, long)");
/* 2394 */     updateBinaryStream(findColumn(paramString), paramInputStream, paramLong);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(int paramInt, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 2408 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2410 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2412 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { new Integer(paramInt), paramReader }));
/*      */       }
/* 2416 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2418 */         LOG.fine(this._logId + " updateCharacterStream(int, Reader)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2423 */     checkUpdatability("updateCharacterStream(int, java.io.Reader)");
/* 2424 */     updateCharacterStream(paramInt, paramReader, -1);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(String paramString, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 2438 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2440 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2442 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { paramString, paramReader }));
/*      */       }
/* 2445 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2447 */         LOG.fine(this._logId + " updateCharacterStream(String, Reader)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2452 */     checkUpdatability("updateCharacterStream(int, java.io.Reader)");
/* 2453 */     updateCharacterStream(findColumn(paramString), paramReader);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2468 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2470 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2472 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { new Integer(paramInt), paramReader, new Long(paramLong) }));
/*      */       }
/* 2476 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2478 */         LOG.fine(this._logId + " updateCharacterStream(int, Reader, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2483 */     checkUpdatability("updateCharacterStream(int, java.io.Reader, long)");
/* 2484 */     int i = checkLongLength(paramLong);
/* 2485 */     updateCharacterStream(paramInt, paramReader, i);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(String paramString, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2500 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2502 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2504 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { paramString, paramReader, new Long(paramLong) }));
/*      */       }
/* 2508 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2510 */         LOG.fine(this._logId + " updateCharacterStream(String, Reader, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2516 */     checkUpdatability("updateCharacterStream(int, java.io.Reader, long)");
/* 2517 */     updateCharacterStream(findColumn(paramString), paramReader, paramLong);
/*      */   }
/*      */ 
/*      */   public void updateClob(int paramInt, Clob paramClob)
/*      */     throws SQLException
/*      */   {
/* 2525 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2527 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2529 */         LOG.finest(LogUtil.logMethod(false, this._logId, "updateClob", new Object[] { new Integer(paramInt), paramClob }));
/*      */       }
/* 2532 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2534 */         LOG.finer(LogUtil.logMethod(true, this._logId, "updateClob", new Object[] { new Integer(paramInt), paramClob }));
/*      */       }
/* 2537 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2539 */         LOG.fine(this._logId + " updateClob(int, Clob)");
/*      */       }
/*      */     }
/*      */ 
/* 2543 */     checkUpdatability("updateClob(int, Clob)");
/* 2544 */     if (paramClob == null)
/*      */     {
/* 2546 */       paramClob = this._pc._conn._nullClob;
/*      */     }
/* 2548 */     setParam(2005, paramInt, paramClob);
/*      */   }
/*      */ 
/*      */   public void updateClob(String paramString, Clob paramClob)
/*      */     throws SQLException
/*      */   {
/* 2556 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2558 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2560 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateClob", new Object[] { paramString, paramClob }));
/*      */       }
/* 2563 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2565 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateClob", new Object[] { paramString, paramClob }));
/*      */       }
/* 2568 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2570 */         LOG.fine(this._logId + " updateClob(String, Clob)");
/*      */       }
/*      */     }
/*      */ 
/* 2574 */     checkUpdatability("updateClob(String, Clob)");
/* 2575 */     updateClob(findColumn(paramString), paramClob);
/*      */   }
/*      */ 
/*      */   public void updateClob(int paramInt, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 2583 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2585 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2587 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateClob", new Object[] { new Integer(paramInt), paramReader }));
/*      */       }
/* 2590 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2592 */         LOG.fine(this._logId + " updateClob(int, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 2596 */     checkUpdatability("updateClob(int, Reader)");
/* 2597 */     setParam(-1, paramInt, paramReader, -1);
/*      */   }
/*      */ 
/*      */   public void updateClob(String paramString, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 2606 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2608 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2610 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateClob", new Object[] { paramString, paramReader }));
/*      */       }
/* 2613 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2615 */         LOG.fine(this._logId + " updateClob(String, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 2619 */     checkUpdatability("updateClob(String, Reader)");
/* 2620 */     updateClob(findColumn(paramString), paramReader);
/*      */   }
/*      */ 
/*      */   public void updateClob(int paramInt, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2629 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2631 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2633 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateClob", new Object[] { new Integer(paramInt), paramReader, new Long(paramLong) }));
/*      */       }
/* 2636 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2638 */         LOG.fine(this._logId + " updateClob(int, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 2642 */     checkUpdatability("updateClob(int, Reader, long)");
/* 2643 */     int i = checkLongLength(paramLong);
/* 2644 */     setParam(-1, paramInt, paramReader, i);
/*      */   }
/*      */ 
/*      */   public void updateClob(String paramString, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2653 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2655 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2657 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateCharacterStream", new Object[] { paramString, paramReader }));
/*      */       }
/* 2661 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2663 */         LOG.fine(this._logId + " updateCharacterStream(String, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 2667 */     checkUpdatability("updateClob(String, Reader, long)");
/* 2668 */     updateClob(findColumn(paramString), paramReader, paramLong);
/*      */   }
/*      */ 
/*      */   public void updateBlob(int paramInt, Blob paramBlob)
/*      */     throws SQLException
/*      */   {
/* 2676 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2678 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2680 */         LOG.finest(LogUtil.logMethod(false, this._logId, "updateBlob", new Object[] { new Integer(paramInt), paramBlob }));
/*      */       }
/* 2683 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2685 */         LOG.finer(LogUtil.logMethod(true, this._logId, "updateBlob", new Object[] { new Integer(paramInt), paramBlob }));
/*      */       }
/* 2688 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2690 */         LOG.fine(this._logId + " updateBlob(int, Blob)");
/*      */       }
/*      */     }
/*      */ 
/* 2694 */     checkUpdatability("updateBlob(int, Blob)");
/* 2695 */     if (paramBlob == null)
/*      */     {
/* 2697 */       paramBlob = this._pc._conn._nullBlob;
/*      */     }
/* 2699 */     setParam(2004, paramInt, paramBlob);
/*      */   }
/*      */ 
/*      */   public void updateBlob(String paramString, Blob paramBlob)
/*      */     throws SQLException
/*      */   {
/* 2707 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2709 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2711 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBlob", new Object[] { paramString, paramBlob }));
/*      */       }
/* 2714 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2716 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBlob", new Object[] { paramString, paramBlob }));
/*      */       }
/* 2719 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2721 */         LOG.fine(this._logId + " updateBlob(String, Blob)");
/*      */       }
/*      */     }
/*      */ 
/* 2725 */     checkUpdatability("updateBlob(String, Blob)");
/* 2726 */     updateBlob(findColumn(paramString), paramBlob);
/*      */   }
/*      */ 
/*      */   public void updateBlob(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 2735 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2737 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2739 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBlob", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 2742 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2744 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBlob", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 2747 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2749 */         LOG.fine(this._logId + " updateBlob(int, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 2753 */     checkUpdatability("updateBlob(int, InputStream)");
/* 2754 */     setParam(-4, paramInt, paramInputStream, -1);
/*      */   }
/*      */ 
/*      */   public void updateBlob(String paramString, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 2763 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2765 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2767 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBlob", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2770 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2772 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBlob", new Object[] { paramString, paramInputStream }));
/*      */       }
/* 2775 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2777 */         LOG.fine(this._logId + " updateBlob(String, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 2781 */     checkUpdatability("updateBlob(String, InputStream)");
/* 2782 */     updateBlob(findColumn(paramString), paramInputStream);
/*      */   }
/*      */ 
/*      */   public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2791 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2793 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2795 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBlob", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2798 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2800 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBlob", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2803 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2805 */         LOG.fine(this._logId + " updateBlob(int, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 2809 */     checkUpdatability("updateBlob(int, InputStream, long)");
/* 2810 */     int i = checkLongLength(paramLong);
/* 2811 */     setParam(-4, paramInt, paramInputStream, i);
/*      */   }
/*      */ 
/*      */   public void updateBlob(String paramString, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 2820 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2822 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2824 */         LOG.finest(LogUtil.logMethod(false, this._logId, " updateBlob", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2827 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2829 */         LOG.finer(LogUtil.logMethod(true, this._logId, " updateBlob", new Object[] { paramString, paramInputStream, new Long(paramLong) }));
/*      */       }
/* 2832 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2834 */         LOG.fine(this._logId + " updateBlob(String, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 2838 */     checkUpdatability("updateBlob(String, InputStream, long)");
/* 2839 */     int i = checkLongLength(paramLong);
/* 2840 */     updateBlob(findColumn(paramString), paramInputStream, i);
/*      */   }
/*      */ 
/*      */   public void cancelRowUpdates()
/*      */     throws SQLException
/*      */   {
/* 2915 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2917 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2919 */       LOG.fine(this._logId + " cancelRowUpdates()");
/*      */     }
/*      */ 
/* 2923 */     checkUpdatability("cancelRowUpdates()");
/* 2924 */     if ((this._rowDeleted) || (this._rowUpdated) || (this._rowInserted))
/*      */     {
/* 2926 */       ErrorMessage.raiseError("JZ0RM", "cancelRowUpdates");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 2931 */       this._paramMgr.clearParamArray(true);
/* 2932 */       this._insertParamMgr.clearParamArray(true);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 2936 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteRow()
/*      */     throws SQLException
/*      */   {
/* 2949 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2951 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2953 */       LOG.fine(this._logId + " deleteRow()");
/*      */     }
/*      */ 
/* 2957 */     checkUpdatability("deleteRow()");
/*      */ 
/* 2959 */     int i = this._cursor.delete(this._prs);
/* 2960 */     if (i <= 0)
/*      */       return;
/* 2962 */     this._rowDeleted = true;
/*      */   }
/*      */ 
/*      */   public void deleteRow(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2979 */     checkUpdatability("deleteRow(String)");
/* 2980 */     this._cursor.setTable(paramString);
/*      */ 
/* 2982 */     int i = this._cursor.delete(this._prs);
/* 2983 */     if (i <= 0)
/*      */       return;
/* 2985 */     this._rowDeleted = true;
/*      */   }
/*      */ 
/*      */   public void updateRow()
/*      */     throws SQLException
/*      */   {
/* 3003 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3005 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3007 */       LOG.fine(this._logId + " updateRow()");
/*      */     }
/*      */ 
/* 3011 */     checkUpdatability("updateRow()");
/* 3012 */     StringBuffer localStringBuffer = new StringBuffer("UPDATE " + this._cursor.getTable() + " SET ");
/*      */ 
/* 3014 */     int i = localStringBuffer.length();
/* 3015 */     ResultSetMetaData localResultSetMetaData = this._prs.getMetaData();
/* 3016 */     int j = localResultSetMetaData.getColumnCount();
/* 3017 */     Param[] arrayOfParam = this._paramMgr.getParams();
/* 3018 */     int k = 1;
/* 3019 */     for (int l = 0; l < j; ++l)
/*      */     {
/* 3021 */       if (arrayOfParam[l]._sqlType == -999)
/*      */       {
/* 3024 */         arrayOfParam[l]._sqlType = -998;
/*      */       } else {
/* 3026 */         if (arrayOfParam[l]._sqlType == -998)
/*      */           continue;
/* 3028 */         if (k == 0)
/*      */         {
/* 3030 */           localStringBuffer.append(", ");
/* 3031 */           i += 2;
/*      */         }
/* 3033 */         String str = localResultSetMetaData.getColumnName(l + 1);
/* 3034 */         i += str.length() + 3;
/* 3035 */         localStringBuffer.append(str + " = ?");
/*      */ 
/* 3037 */         arrayOfParam[l]._paramMarkerOffset = (i++);
/* 3038 */         k = 0;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3043 */     if (k != 0)
/*      */     {
/* 3045 */       ErrorMessage.raiseError("JZ00G");
/*      */     }
/* 3047 */     if (1 == this._cursor.update(this._prs, this._paramMgr, localStringBuffer.toString()))
/*      */     {
/* 3049 */       this._rowUpdated = true;
/*      */     }
/*      */ 
/* 3054 */     for (l = 0; l < j; ++l)
/*      */     {
/* 3056 */       if (arrayOfParam[l]._sqlType != -998)
/*      */         continue;
/* 3058 */       arrayOfParam[l]._sqlType = -999;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateRow(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3075 */     checkUpdatability("updateRow(String)");
/* 3076 */     this._cursor.setTable(paramString);
/* 3077 */     updateRow();
/*      */   }
/*      */ 
/*      */   private void setParam(int paramInt1, int paramInt2, Object paramObject)
/*      */     throws SQLException
/*      */   {
/* 3085 */     setParam(paramInt1, paramInt2, paramObject, 0);
/*      */   }
/*      */ 
/*      */   private void setParam(int paramInt1, int paramInt2, Object paramObject, int paramInt3)
/*      */     throws SQLException
/*      */   {
/* 3091 */     checkResultSet();
/* 3092 */     if (this._paramMgr != null)
/*      */     {
/* 3094 */       if (this._rowIndex != -3)
/*      */       {
/* 3099 */         this._paramMgr.setParam(paramInt2, paramInt1, paramObject, paramInt3);
/*      */       }
/*      */       else
/*      */       {
/* 3106 */         this._insertParamMgr.setParam(paramInt2, paramInt1, paramObject, paramInt3);
/* 3107 */         this._rowInserted = false;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/* 3112 */       ErrorMessage.raiseError("JZ00H");
/*      */   }
/*      */ 
/*      */   protected JdbcDataObject getColumn(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3122 */     checkResultSet();
/*      */     Param[] arrayOfParam;
/* 3127 */     if (this._rowIndex == -3)
/*      */     {
/* 3129 */       arrayOfParam = this._insertParamMgr.getParams();
/* 3130 */       if ((paramInt < 1) || (paramInt > arrayOfParam.length))
/*      */       {
/* 3132 */         ErrorMessage.raiseError("JZ008", "" + paramInt);
/*      */       }
/*      */ 
/* 3136 */       if ((arrayOfParam[(paramInt - 1)]._sqlType == -999) || (arrayOfParam[(paramInt - 1)]._sqlType == -998))
/*      */       {
/* 3140 */         this._currentColumn = null;
/*      */       }
/*      */       else
/*      */       {
/* 3144 */         this._currentColumn = ((JdbcDataObject)arrayOfParam[(paramInt - 1)]);
/*      */       }
/*      */ 
/*      */     }
/* 3149 */     else if (this._paramMgr != null)
/*      */     {
/* 3152 */       arrayOfParam = this._paramMgr.getParams();
/*      */ 
/* 3154 */       if ((paramInt < 1) || (paramInt > arrayOfParam.length))
/*      */       {
/* 3156 */         ErrorMessage.raiseError("JZ008", "" + paramInt);
/*      */       }
/*      */ 
/* 3159 */       checkIfReadableRow();
/* 3160 */       if ((arrayOfParam[(paramInt - 1)]._sqlType == -999) || (arrayOfParam[(paramInt - 1)]._sqlType == -998))
/*      */       {
/* 3164 */         this._currentColumn = this._prs.getColumn(paramInt);
/*      */       }
/*      */       else
/*      */       {
/* 3169 */         this._currentColumn = ((JdbcDataObject)arrayOfParam[(paramInt - 1)]);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 3176 */       checkIfReadableRow();
/* 3177 */       this._currentColumn = this._prs.getColumn(paramInt);
/*      */     }
/*      */ 
/* 3180 */     return this._currentColumn;
/*      */   }
/*      */ 
/*      */   protected void checkIfReadableRow()
/*      */     throws SQLException
/*      */   {
/* 3189 */     if (this._rowDeleted)
/*      */     {
/* 3192 */       ErrorMessage.raiseError("JZ0RD");
/*      */     }
/* 3194 */     if (this._rowIndex == -1)
/*      */     {
/* 3196 */       ErrorMessage.raiseError("JZ0R1");
/*      */     } else {
/* 3198 */       if (this._rowIndex != -2)
/*      */         return;
/* 3200 */       ErrorMessage.raiseError("JZ0R5");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkForScrollability(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3212 */     switch (this._scrollType)
/*      */     {
/*      */     case 1003:
/* 3215 */       ErrorMessage.raiseError("JZ0BT", paramString, "TYPE_FORWARD_ONLY");
/*      */ 
/* 3218 */       break;
/*      */     case 1004:
/* 3229 */       if (!this._cursor.isLanguageCursor())
/*      */         return;
/* 3231 */       ErrorMessage.raiseError("JZ0LC", paramString); break;
/*      */     case 1005:
/* 3241 */       Debug.notImplemented(this, paramString + " for TYPE_SCROLL_SENSITIVE");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkUpdatability(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3252 */     if (this._concurType != 1007)
/*      */       return;
/* 3254 */     ErrorMessage.raiseError("JZ0BT", paramString, this._concurTypeString);
/*      */   }
/*      */ 
/*      */   private int checkLongLength(long paramLong)
/*      */   {
/*      */     int i;
/* 3268 */     if (paramLong > 2147483647L)
/*      */     {
/* 3270 */       i = 2147483647;
/* 3271 */       this._statement._context._conn.chainWarnings(ErrorMessage.createWarning("01S11"));
/*      */     }
/*      */     else
/*      */     {
/* 3277 */       i = (int)paramLong;
/*      */     }
/*      */ 
/* 3280 */     return i;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybCursorResultSet
 * JD-Core Version:    0.5.4
 */