/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.tds.SybTimestamp;
/*      */ import com.sybase.jdbc3.tds.TdsNumeric;
/*      */ import com.sybase.jdbc3.tds.TdsParam;
/*      */ import com.sybase.jdbc3.utils.CacheManager;
/*      */ import com.sybase.jdbc3.utils.Cacheable;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataOutput;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class ParamManager
/*      */   implements Cacheable
/*      */ {
/*      */   public static final int PARAM_BY_NAME = 1;
/*      */   public static final int PARAM_BY_INDEX = 2;
/*      */   public Param[] _params;
/*      */   public int[] _prevParamTypes;
/*      */   protected int _next;
/*      */   protected int _last;
/*      */   private int _maxOutParam;
/*      */   public int[] _rowUpdatedStatus;
/*      */   public SQLException[] _rowException;
/*   75 */   protected ArrayList _paramMdList = new ArrayList();
/*      */   protected SybStatement _stmt;
/*      */   protected ProtocolContext _context;
/*      */   protected Protocol _protocol;
/*      */   protected CacheManager _stmtMgr;
/*      */   private CacheManager _mgr;
/*   82 */   private boolean _haveOutParam = false;
/*      */ 
/*   86 */   private boolean _ignoreClear = false;
/*   87 */   private boolean _clearingParams = false;
/*      */ 
/*   90 */   protected boolean _hasLiteralParam = false;
/*   91 */   protected boolean _paramMarkersHaveBeenParsed = false;
/*   92 */   protected boolean _dontProcessParamMarkers = false;
/*   93 */   protected boolean _setHasBeenCalled = false;
/*   94 */   private String _savedParsedQuery = null;
/*      */ 
/*  105 */   private boolean _templateHoldsParsedNoLiteralQuery = false;
/*  106 */   private ParamManager _copiedFrom = null;
/*      */ 
/*  109 */   protected int _paramSetType = 0;
/*      */   private static final int PARAM_MODE_IN = 1;
/*      */   private static final int PARAM_MODE_OUT = 2;
/*      */   private static final int PARAM_MODE_RETURN = 4;
/*      */   private static final String PARAM_NAME_RETURN = "RETURN_VALUE";
/*  116 */   private boolean _ignoreWarnings = false;
/*      */ 
/*      */   public ParamManager(int paramInt, int[] paramArrayOfInt, SybStatement paramSybStatement)
/*      */     throws SQLException
/*      */   {
/*  130 */     this._next = -1;
/*  131 */     this._last = -1;
/*  132 */     this._maxOutParam = -1;
/*  133 */     this._stmt = paramSybStatement;
/*  134 */     this._context = paramSybStatement._context;
/*  135 */     this._stmtMgr = paramSybStatement._statementManager;
/*  136 */     this._protocol = this._context._protocol;
/*  137 */     this._params = this._protocol.paramArray(this._context, paramInt);
/*  138 */     this._ignoreWarnings = this._context._conn.isPropertySet(74);
/*  139 */     for (int i = 0; i < paramInt; ++i)
/*      */     {
/*  141 */       this._params[i]._paramMarkerOffset = paramArrayOfInt[i];
/*      */     }
/*  143 */     this._mgr = new CacheManager(this._context._is);
/*  144 */     boolean bool = this._context._conn._props.getBoolean(14);
/*  145 */     this._mgr.setReReadable(bool);
/*  146 */     int j = this._context._conn._props.getInteger(13);
/*  147 */     if (bool)
/*      */     {
/*  149 */       j = -1;
/*      */     }
/*  151 */     this._mgr.setCacheSize(j);
/*      */ 
/*  153 */     this._mgr.setChunkSize(256);
/*  154 */     this._mgr.setAbortOnCacheOverflow(true);
/*      */   }
/*      */ 
/*      */   public ParamManager(ParamManager paramParamManager, SybStatement paramSybStatement)
/*      */     throws SQLException
/*      */   {
/*  161 */     this._next = -1;
/*  162 */     this._last = -1;
/*  163 */     this._maxOutParam = -1;
/*  164 */     this._stmt = paramSybStatement;
/*  165 */     this._context = paramSybStatement._context;
/*  166 */     this._stmtMgr = paramSybStatement._statementManager;
/*  167 */     this._protocol = this._context._protocol;
/*  168 */     int i = paramParamManager._params.length;
/*  169 */     this._params = this._protocol.paramArray(this._context, i);
/*  170 */     this._ignoreWarnings = this._context._conn._props.getBoolean(74);
/*      */ 
/*  172 */     for (int j = 0; j < i; ++j)
/*      */     {
/*  174 */       this._params[j]._paramMarkerOffset = paramParamManager._params[j]._paramMarkerOffset;
/*      */     }
/*  176 */     this._mgr = this._context._conn.getSharedCacheManager();
/*  177 */     if (this._mgr == null)
/*      */     {
/*  179 */       this._mgr = new CacheManager(this._context._is);
/*  180 */       boolean bool = this._context._conn._props.getBoolean(14);
/*  181 */       this._mgr.setReReadable(bool);
/*  182 */       int k = this._context._conn._props.getInteger(13);
/*  183 */       if (bool)
/*      */       {
/*  185 */         k = -1;
/*      */       }
/*  187 */       this._mgr.setCacheSize(k);
/*      */ 
/*  189 */       this._mgr.setChunkSize(256);
/*  190 */       this._mgr.setAbortOnCacheOverflow(true);
/*  191 */       this._context._conn.setSharedCacheManager(this._mgr);
/*      */     }
/*  193 */     if (paramParamManager._templateHoldsParsedNoLiteralQuery)
/*      */     {
/*  195 */       this._hasLiteralParam = paramParamManager._hasLiteralParam;
/*  196 */       this._savedParsedQuery = paramParamManager._savedParsedQuery;
/*  197 */       this._paramMarkersHaveBeenParsed = true;
/*      */     }
/*  199 */     this._copiedFrom = paramParamManager;
/*      */   }
/*      */ 
/*      */   public ParamManager(int paramInt, SybStatement paramSybStatement)
/*      */     throws SQLException
/*      */   {
/*  206 */     this(paramInt, paramSybStatement._context);
/*  207 */     this._stmt = paramSybStatement;
/*      */   }
/*      */ 
/*      */   public ParamManager(int paramInt, ProtocolContext paramProtocolContext)
/*      */     throws SQLException
/*      */   {
/*  213 */     this._next = -1;
/*  214 */     this._last = -1;
/*  215 */     this._maxOutParam = -1;
/*  216 */     this._context = paramProtocolContext;
/*  217 */     this._protocol = this._context._protocol;
/*  218 */     this._ignoreWarnings = this._context._conn._props.getBoolean(74);
/*      */ 
/*  221 */     this._params = this._protocol.paramArray(paramInt, this._mgr);
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */     throws IOException
/*      */   {
/*  228 */     if (this._ignoreClear) return;
/*      */ 
/*      */     try
/*      */     {
/*  232 */       if (this._haveOutParam)
/*      */       {
/*  235 */         if ((this._maxOutParam >= 0) && 
/*  237 */           (this._params[this._maxOutParam]._outValue == null))
/*      */         {
/*  239 */           getOutValueAt(this._maxOutParam + 1);
/*      */         }
/*      */ 
/*  242 */         if (!this._clearingParams)
/*      */         {
/*  244 */           clearParams(false);
/*      */         }
/*      */       }
/*  247 */       this._mgr.clear();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  251 */       ErrorMessage.raiseIOECheckDead(localSQLException);
/*      */     }
/*      */     finally
/*      */     {
/*  255 */       this._stmtMgr.dead(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setManager(CacheManager paramCacheManager)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */     throws IOException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void resetInputStream(InputStream paramInputStream)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void cache()
/*      */     throws IOException
/*      */   {
/*  278 */     if (this._ignoreClear) return;
/*      */ 
/*  281 */     this._ignoreClear = true;
/*      */ 
/*  285 */     this._mgr.register(this);
/*  286 */     this._mgr.open(this);
/*      */ 
/*  288 */     this._mgr.doneReading();
/*      */ 
/*  290 */     this._mgr.dead(this);
/*  291 */     this._ignoreClear = false;
/*  292 */     this._stmtMgr.doneReading();
/*      */   }
/*      */ 
/*      */   public void open(boolean paramBoolean)
/*      */   {
/*      */   }
/*      */ 
/*      */   public int getState()
/*      */   {
/*  302 */     return 1;
/*      */   }
/*      */ 
/*      */   public int getNext()
/*      */   {
/*  307 */     return this._next;
/*      */   }
/*      */ 
/*      */   public Param[] getParams()
/*      */   {
/*  312 */     return this._params;
/*      */   }
/*      */ 
/*      */   public ProtocolContext getContext()
/*      */   {
/*  317 */     return this._context;
/*      */   }
/*      */ 
/*      */   protected synchronized void clearParams(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  330 */     if (this._clearingParams) return;
/*  331 */     if (this._haveOutParam)
/*      */     {
/*  334 */       this._clearingParams = true;
/*      */ 
/*  336 */       this._stmtMgr.open(this);
/*  337 */       cache();
/*  338 */       this._clearingParams = false;
/*  339 */       this._haveOutParam = false;
/*      */     }
/*  341 */     clearParamArray(paramBoolean);
/*      */ 
/*  344 */     this._mgr.clear();
/*  345 */     this._mgr.allDead();
/*      */   }
/*      */ 
/*      */   protected synchronized void clearParamArray(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  352 */     int i = 0;
/*  353 */     if ((this._params.length > 0) && (this._params[0]._sqlType == -998))
/*      */     {
/*  356 */       i = 1;
/*      */ 
/*  358 */       this._params[0].clear(false);
/*      */     }
/*  360 */     for (int j = i; j < this._params.length; ++j)
/*      */     {
/*  362 */       this._params[j].clear(paramBoolean);
/*      */     }
/*  364 */     this._last = (this._next = -1);
/*      */   }
/*      */ 
/*      */   private void checkIndex(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  371 */     if ((paramInt >= 1) && (paramInt <= this._params.length))
/*      */       return;
/*  373 */     ErrorMessage.raiseError("JZ0SB", "" + paramInt);
/*      */   }
/*      */ 
/*      */   private void checkType(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  382 */     switch (paramInt)
/*      */     {
/*      */     case -7:
/*      */     case -6:
/*      */     case -5:
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 12:
/*      */     case 91:
/*      */     case 92:
/*      */     case 93:
/*      */     case 2000:
/*  407 */       break;
/*      */     case 0:
/*      */     case 1111:
/*      */     default:
/*  411 */       ErrorMessage.raiseError("JZ0SL", "" + paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setParamMd(ResultSet paramResultSet)
/*      */     throws SQLException
/*      */   {
/*  419 */     int i = 0;
/*  420 */     int j = 0;
/*  421 */     while (paramResultSet.next())
/*      */     {
/*  429 */       if (("RETURN_VALUE".equalsIgnoreCase(paramResultSet.getString("COLUMN_NAME"))) && (paramResultSet.getInt("COLUMN_TYPE") == 0))
/*      */       {
/*  432 */         if (!this._stmt._sendAsRpc)
/*      */         {
/*  434 */           ((SybCallableStatement)this._stmt).extractParams();
/*      */         }
/*  436 */         if ((i != 0) || 
/*  438 */           (this._stmt._hasReturn))
/*      */           break label142;
/*  440 */         ++i;
/*      */       }
/*      */ 
/*  445 */       if ((this._stmt._rowFmtIdxList != null) && (this._stmt._rowFmtIdxList.size() > 0))
/*      */       {
/*      */         int k;
/*      */         try
/*      */         {
/*  451 */           k = ((Integer)this._stmt._rowFmtIdxList.get(j)).intValue();
/*      */         }
/*      */         catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
/*      */         {
/*  458 */           return;
/*      */         }
/*      */ 
/*  461 */         if (k != i) {
/*      */           continue;
/*      */         }
/*      */ 
/*  465 */         ++j;
/*      */       }
/*      */ 
/*  468 */       label142: Param localParam = this._protocol.getParam();
/*  469 */       localParam._sqlType = paramResultSet.getInt("DATA_TYPE");
/*  470 */       localParam._sqlTypeName = paramResultSet.getString("TYPE_NAME");
/*      */ 
/*  472 */       switch (paramResultSet.getInt("COLUMN_TYPE"))
/*      */       {
/*      */       case 1:
/*  475 */         localParam._regType = 1;
/*  476 */         break;
/*      */       case 2:
/*  478 */         localParam._regType = 2;
/*  479 */         break;
/*      */       case 4:
/*  481 */         localParam._regType = 4;
/*  482 */         break;
/*      */       case 3:
/*      */       default:
/*  484 */         localParam._regType = 0;
/*      */       }
/*      */ 
/*  488 */       localParam._precision = paramResultSet.getInt("PRECISION");
/*  489 */       localParam._scale = paramResultSet.getInt("SCALE");
/*  490 */       switch (paramResultSet.getInt("NULLABLE"))
/*      */       {
/*      */       case 0:
/*  493 */         localParam._isNullable = 0;
/*  494 */         break;
/*      */       case 1:
/*  496 */         localParam._isNullable = 1;
/*  497 */         break;
/*      */       default:
/*  499 */         localParam._isNullable = 2;
/*      */       }
/*      */ 
/*  502 */       switch (paramResultSet.getInt("SS_DATA_TYPE"))
/*      */       {
/*      */       case 38:
/*      */       case 48:
/*      */       case 52:
/*      */       case 56:
/*      */       case 59:
/*      */       case 60:
/*      */       case 62:
/*      */       case 106:
/*      */       case 108:
/*      */       case 109:
/*      */       case 110:
/*      */       case 122:
/*      */       case 191:
/*  517 */         localParam._isSigned = true;
/*  518 */         break;
/*      */       default:
/*  520 */         localParam._isSigned = false;
/*      */       }
/*      */ 
/*  524 */       for (int l = 0; l < Param.UNICODE_SQLTYPES.length; ++l)
/*      */       {
/*  526 */         String str = Param.UNICODE_SQLTYPES[l];
/*  527 */         if (!str.equalsIgnoreCase(localParam._sqlTypeName))
/*      */           continue;
/*  529 */         localParam._isUnicodeType = true;
/*  530 */         break;
/*      */       }
/*      */ 
/*  534 */       this._paramMdList.add(localParam);
/*  535 */       ++i;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setParamMd(ResultSetMetaData paramResultSetMetaData, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  543 */     if (!paramBoolean)
/*      */     {
/*  545 */       Vector localVector = this._stmt._rowFmtIdxList;
/*  546 */       if ((localVector == null) || (localVector.size() <= 0))
/*      */         return;
/*  548 */       for (i = 0; ; ++i) { if (i >= localVector.size())
/*      */           return;
/*  550 */         int j = ((Integer)localVector.get(i)).intValue();
/*  551 */         this._paramMdList.add(i, fillParamFromRSMD(paramResultSetMetaData, j, paramBoolean)); }
/*      */ 
/*      */ 
/*      */     }
/*      */ 
/*  558 */     for (int i = 1; i <= paramResultSetMetaData.getColumnCount(); ++i)
/*      */     {
/*  560 */       this._paramMdList.add(fillParamFromRSMD(paramResultSetMetaData, i, paramBoolean));
/*      */     }
/*      */   }
/*      */ 
/*      */   private int getTargetType(int paramInt, boolean paramBoolean)
/*      */   {
/*  567 */     int i = paramInt;
/*      */ 
/*  572 */     switch (paramInt)
/*      */     {
/*      */     case -5:
/*  575 */       if (paramBoolean)
/*      */         break label74;
/*  577 */       i = 2; break;
/*      */     case 4:
/*  581 */       if (paramBoolean)
/*      */         break label74;
/*  583 */       i = -5; break;
/*      */     case 5:
/*  587 */       if (paramBoolean)
/*      */         break label74;
/*  589 */       i = 4; break;
/*      */     case 12:
/*  593 */       i = -1;
/*      */     }
/*      */ 
/*  596 */     label74: return i;
/*      */   }
/*      */ 
/*      */   private Param fillParamFromRSMD(ResultSetMetaData paramResultSetMetaData, int paramInt, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  602 */     Param localParam = this._protocol.getParam();
/*  603 */     localParam._sqlType = paramResultSetMetaData.getColumnType(paramInt);
/*  604 */     localParam._sqlTypeName = paramResultSetMetaData.getColumnTypeName(paramInt);
/*  605 */     localParam._regType = 1;
/*  606 */     localParam._precision = paramResultSetMetaData.getPrecision(paramInt);
/*  607 */     localParam._scale = paramResultSetMetaData.getScale(paramInt);
/*  608 */     localParam._isSigned = paramResultSetMetaData.isSigned(paramInt);
/*  609 */     if (paramBoolean)
/*      */     {
/*  611 */       localParam._isNullable = 2;
/*      */     }
/*      */     else
/*      */     {
/*  615 */       localParam._isNullable = ((paramResultSetMetaData.isNullable(paramInt) == 1) ? 1 : 0);
/*      */     }
/*      */ 
/*  623 */     localParam._targetType = getTargetType(localParam._sqlType, localParam._isSigned);
/*      */ 
/*  630 */     this._params[(paramInt - 1)]._targetType = localParam._targetType;
/*      */ 
/*  632 */     for (int i = 0; i < Param.UNICODE_SQLTYPES.length; ++i)
/*      */     {
/*  634 */       String str = Param.UNICODE_SQLTYPES[i];
/*  635 */       if (!str.equalsIgnoreCase(localParam._sqlTypeName))
/*      */         continue;
/*  637 */       localParam._isUnicodeType = true;
/*  638 */       break;
/*      */     }
/*      */ 
/*  642 */     return localParam;
/*      */   }
/*      */ 
/*      */   public void setParam(String paramString, int paramInt1, Object paramObject, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  650 */     useParamByName();
/*  651 */     int i = getParamIndexByName(paramString);
/*  652 */     doSetParam(i, paramInt1, paramObject, paramInt2);
/*  653 */     this._params[(i - 1)]._name = paramString;
/*      */   }
/*      */ 
/*      */   public void setParam(int paramInt1, int paramInt2, Object paramObject, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*  660 */     useParamByIndex();
/*  661 */     doSetParam(paramInt1, paramInt1 - 1, paramInt2, paramObject, paramInt3);
/*      */   }
/*      */ 
/*      */   public void setParam(int paramInt1, int paramInt2, int paramInt3, Object paramObject, int paramInt4)
/*      */     throws SQLException
/*      */   {
/*  667 */     useParamByIndex();
/*  668 */     doSetParam(paramInt1, paramInt2, paramInt3, paramObject, paramInt4);
/*      */   }
/*      */ 
/*      */   public void doSetParam(int paramInt1, int paramInt2, int paramInt3, Object paramObject, int paramInt4)
/*      */     throws SQLException
/*      */   {
/*  675 */     doSetParam(paramInt1, paramInt3, paramObject, paramInt4);
/*  676 */     checkIndex(paramInt1);
/*  677 */     Param localParam = this._params[(paramInt1 - 1)];
/*  678 */     localParam._colId = paramInt2;
/*      */   }
/*      */ 
/*      */   public void doSetParam(int paramInt1, int paramInt2, Object paramObject, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*  688 */     checkIndex(paramInt1);
/*  689 */     --paramInt1;
/*  690 */     Param localParam = this._params[paramInt1];
/*  691 */     if (localParam._sqlType == -998)
/*      */     {
/*  693 */       ErrorMessage.raiseError("JZ0SC");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  698 */       localParam.clear(false);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  702 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */ 
/*  705 */     switch (paramInt2)
/*      */     {
/*      */     case 2:
/*  708 */       if (paramObject instanceof BigDecimal)
/*      */       {
/*  710 */         TdsNumeric.checkRange((BigDecimal)paramObject, paramInt3);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  720 */     localParam._sqlType = paramInt2;
/*  721 */     localParam._inValue = paramObject;
/*  722 */     localParam._scale = paramInt3;
/*  723 */     this._setHasBeenCalled = true;
/*      */   }
/*      */ 
/*      */   public void registerParam(String paramString, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  732 */     useParamByName();
/*  733 */     int i = getParamIndexByName(paramString);
/*  734 */     doRegisterParam(i, paramInt1);
/*  735 */     this._params[(i - 1)]._name = paramString;
/*  736 */     this._params[(i - 1)]._scale = paramInt2;
/*      */   }
/*      */ 
/*      */   public void registerParam(int paramInt1, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*  744 */     registerParam(paramInt1, paramInt2);
/*  745 */     this._params[(paramInt1 - 1)]._scale = paramInt3;
/*      */   }
/*      */ 
/*      */   public void registerParam(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  751 */     useParamByName();
/*  752 */     int i = getParamIndexByName(paramString);
/*  753 */     doRegisterParam(i, paramInt);
/*  754 */     this._params[(i - 1)]._name = paramString;
/*      */   }
/*      */ 
/*      */   public void registerParam(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  760 */     useParamByIndex();
/*  761 */     doRegisterParam(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   protected void doRegisterParam(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  770 */     checkIndex(paramInt1);
/*  771 */     checkType(paramInt2);
/*  772 */     --paramInt1;
/*  773 */     if (paramInt1 > this._maxOutParam) this._maxOutParam = paramInt1;
/*  774 */     this._params[paramInt1]._regType = paramInt2;
/*  775 */     if ((!this._stmt._hasReturn) || (paramInt1 != 0))
/*      */       return;
/*  777 */     this._stmt._returnHasBeenRegistered = true;
/*      */   }
/*      */ 
/*      */   public void registerParam(String paramString1, int paramInt, String paramString2)
/*      */     throws SQLException
/*      */   {
/*  784 */     useParamByName();
/*  785 */     int i = getParamIndexByName(paramString1);
/*  786 */     doRegisterParam(i, paramInt);
/*  787 */     this._params[(i - 1)]._name = paramString1;
/*  788 */     this._params[(i - 1)]._outParamClassName = paramString2;
/*      */   }
/*      */ 
/*      */   public void registerParam(int paramInt1, int paramInt2, String paramString)
/*      */     throws SQLException
/*      */   {
/*  795 */     registerParam(paramInt1, paramInt2);
/*      */ 
/*  798 */     --paramInt1;
/*  799 */     this._params[paramInt1]._outParamClassName = paramString;
/*      */   }
/*      */ 
/*      */   private void checkReceive()
/*      */     throws SQLException
/*      */   {
/*  806 */     if (this._next >= 0)
/*      */       return;
/*  808 */     ErrorMessage.raiseError("JZ0SF");
/*      */   }
/*      */ 
/*      */   public JdbcDataObject getOutValueAt(String paramString)
/*      */     throws SQLException
/*      */   {
/*  814 */     useParamByName();
/*  815 */     int i = getParamIndexByName(paramString);
/*  816 */     return doGetOutValueAt(i);
/*      */   }
/*      */ 
/*      */   public JdbcDataObject getOutValueAt(int paramInt) throws SQLException
/*      */   {
/*  821 */     useParamByIndex();
/*  822 */     return doGetOutValueAt(paramInt);
/*      */   }
/*      */ 
/*      */   protected JdbcDataObject doGetOutValueAt(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  830 */     checkReceive();
/*  831 */     checkIndex(paramInt);
/*  832 */     --paramInt;
/*      */ 
/*  835 */     if ((this._params[paramInt]._regType == -999) || ((this._stmt._hasReturn) && (paramInt == 0) && (!this._stmt._returnHasBeenRegistered)))
/*      */     {
/*  838 */       ErrorMessage.raiseError("JZ0SB", "" + paramInt);
/*      */     }
/*      */ 
/*  841 */     if (this._params[paramInt]._outValue == null)
/*      */     {
/*  844 */       nextOutParam();
/*      */       while (true) {
/*  846 */         if (this._params[paramInt]._outValue == null);
/*  851 */         nextResult();
/*      */       }
/*      */     }
/*  854 */     this._last = paramInt;
/*      */ 
/*  857 */     return this._params[paramInt]._outValue;
/*      */   }
/*      */ 
/*      */   public Object getOutObjectAt(String paramString) throws SQLException
/*      */   {
/*  862 */     useParamByName();
/*  863 */     int i = getParamIndexByName(paramString);
/*  864 */     return doGetOutObjectAt(i);
/*      */   }
/*      */ 
/*      */   public Object getOutObjectAt(int paramInt) throws SQLException
/*      */   {
/*  869 */     useParamByIndex();
/*  870 */     return doGetOutObjectAt(paramInt);
/*      */   }
/*      */ 
/*      */   protected Object doGetOutObjectAt(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  878 */     Object localObject1 = null;
/*  879 */     JdbcDataObject localJdbcDataObject = doGetOutValueAt(paramInt);
/*  880 */     --paramInt;
/*      */ 
/*  882 */     switch (this._params[paramInt]._regType)
/*      */     {
/*      */     case -6:
/*      */     case 5:
/*  886 */       localObject1 = Convert.objectToShort(localJdbcDataObject.getObject());
/*  887 */       break;
/*      */     case -5:
/*  893 */       Object localObject2 = localJdbcDataObject.getObject();
/*  894 */       if (localObject2 == null)
/*      */       {
/*  896 */         localObject1 = null; break label316:
/*      */       }
/*  898 */       if (localObject2 instanceof Integer)
/*      */       {
/*  900 */         localObject1 = new Long(((Integer)localObject2).longValue()); break label316:
/*      */       }
/*  902 */       if (localObject2 instanceof BigDecimal)
/*      */       {
/*  904 */         localObject1 = new Long(((BigDecimal)localObject2).longValue()); break label316:
/*      */       }
/*  906 */       if (!localObject2 instanceof Long) {
/*      */         break label316;
/*      */       }
/*      */ 
/*  910 */       localObject1 = localObject2; break;
/*      */     case 2:
/*      */     case 3:
/*  922 */       if (this._params[paramInt]._scale != -999)
/*      */       {
/*  924 */         localObject1 = localJdbcDataObject.getBigDecimal(this._params[paramInt]._scale); break label316:
/*      */       }
/*      */ 
/*  942 */       localObject1 = localJdbcDataObject.getBigDecimal(-1);
/*      */ 
/*  944 */       break;
/*      */     case 6:
/*  946 */       localObject1 = Convert.objectToDouble(localJdbcDataObject.getObject());
/*  947 */       break;
/*      */     case 91:
/*  949 */       localObject1 = Convert.objectToDate(localJdbcDataObject.getDateObject(91, null));
/*      */ 
/*  951 */       break;
/*      */     case 92:
/*  953 */       localObject1 = Convert.objectToTime(localJdbcDataObject.getDateObject(92, null));
/*      */ 
/*  955 */       break;
/*      */     case 93:
/*  957 */       localObject1 = Convert.objectToTimestamp(localJdbcDataObject.getDateObject(93, null));
/*      */ 
/*  960 */       break;
/*      */     default:
/*  962 */       localObject1 = localJdbcDataObject.getObject();
/*      */     }
/*  964 */     label316: return localObject1;
/*      */   }
/*      */ 
/*      */   public boolean hasInParams()
/*      */   {
/*  976 */     if (this._params.length == 0)
/*      */     {
/*  980 */       return false;
/*      */     }
/*      */ 
/*  987 */     return (this._params.length > 1) || (this._params[0]._sqlType != -998);
/*      */   }
/*      */ 
/*      */   protected boolean wasNull()
/*      */     throws SQLException
/*      */   {
/*  998 */     checkReceive();
/*  999 */     if ((this._last < 0) || (this._params[this._last]._outValue == null))
/*      */     {
/* 1001 */       ErrorMessage.raiseError("JZ0SD");
/*      */     }
/* 1003 */     return this._params[this._last]._outValue.isNull();
/*      */   }
/*      */ 
/*      */   protected void getParameter()
/*      */     throws SQLException
/*      */   {
/* 1011 */     checkReceive();
/* 1012 */     this._protocol.param(this);
/*      */   }
/*      */ 
/*      */   public int nextOutParam(String paramString)
/*      */   {
/* 1017 */     for (int i = 0; i < this._params.length; ++i)
/*      */     {
/* 1019 */       if ((this._params[i]._name != null) && (this._params[i]._name.equalsIgnoreCase(paramString)) && (this._params[i]._regType != -999))
/*      */       {
/* 1023 */         return i;
/*      */       }
/*      */     }
/* 1026 */     return -1;
/*      */   }
/*      */ 
/*      */   public int nextOutParam()
/*      */   {
/* 1033 */     if ((this._next < this._params.length) && (this._params[this._next]._outValue != null)) this._next += 1;
/* 1034 */     while ((this._next < this._params.length) && (this._params[this._next]._regType == -999))
/*      */     {
/* 1036 */       this._next += 1;
/*      */     }
/* 1038 */     return this._next;
/*      */   }
/*      */ 
/*      */   public void registerParam(Cacheable paramCacheable)
/*      */     throws SQLException
/*      */   {
/* 1045 */     nextOutParam();
/*      */     try
/*      */     {
/* 1048 */       this._haveOutParam = true;
/*      */ 
/* 1050 */       this._mgr.register(paramCacheable);
/* 1051 */       this._mgr.open(paramCacheable);
/*      */ 
/* 1053 */       this._stmtMgr.register(this);
/* 1054 */       this._stmtMgr.open(this);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1058 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerStatus(Cacheable paramCacheable)
/*      */     throws SQLException
/*      */   {
/* 1066 */     if ((this._params.length > 0) && (this._params[0]._sqlType == -998))
/*      */     {
/* 1069 */       registerParam(paramCacheable);
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/* 1076 */         paramCacheable.clear();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1080 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int nextResult()
/*      */     throws SQLException
/*      */   {
/* 1090 */     int j = this._next;
/*      */     while (true)
/*      */     {
/*      */       try
/*      */       {
/* 1096 */         int i = this._stmt.nextResult();
/* 1097 */         switch (i)
/*      */         {
/*      */         case 3:
/* 1101 */           return i;
/*      */         case 0:
/* 1103 */           ErrorMessage.raiseError("JZ0SG");
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 1110 */         this._stmt.handleSQLE(localSQLException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean checkParams(Protocol paramProtocol, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1120 */     int i = 1;
/*      */ 
/* 1123 */     boolean bool = false;
/* 1124 */     for (int j = 0; j < this._params.length; ++j)
/*      */     {
/* 1126 */       if (!this._ignoreWarnings)
/*      */       {
/* 1129 */         checkForNanos(j);
/*      */       }
/* 1131 */       if (paramBoolean2)
/*      */       {
/*      */         try
/*      */         {
/* 1135 */           this._params[j].normalizeForSend(j);
/*      */         }
/*      */         catch (SQLException localSQLException)
/*      */         {
/* 1139 */           if (this._context._conn._props.getBoolean(80))
/*      */           {
/* 1144 */             if ((this._rowUpdatedStatus == null) && (this._rowException == null))
/*      */             {
/* 1146 */               this._rowUpdatedStatus = new int[this._stmt._batchCmds.size()];
/*      */ 
/* 1148 */               this._rowException = new SQLException[this._stmt._batchCmds.size()];
/*      */             }
/*      */ 
/* 1154 */             this._rowUpdatedStatus[paramInt] = -3;
/* 1155 */             this._rowException[paramInt] = localSQLException;
/* 1156 */             i = 0;
/*      */           }
/*      */           else
/*      */           {
/* 1162 */             throw localSQLException;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       else {
/* 1168 */         this._params[j].prepareForSend(paramProtocol, j, paramBoolean1);
/*      */       }
/* 1170 */       bool |= this._params[j]._sendAsLiteral;
/*      */     }
/*      */ 
/* 1178 */     if (this._paramMarkersHaveBeenParsed)
/*      */     {
/* 1180 */       if (!this._setHasBeenCalled)
/*      */       {
/* 1184 */         this._dontProcessParamMarkers = true;
/*      */       }
/* 1186 */       else if ((!bool) && 
/* 1188 */         (!this._hasLiteralParam))
/*      */       {
/* 1193 */         this._dontProcessParamMarkers = true;
/*      */       }
/*      */     }
/*      */ 
/* 1197 */     this._hasLiteralParam = bool;
/* 1198 */     return i;
/*      */   }
/*      */ 
/*      */   public void parseParamsAgain()
/*      */   {
/* 1209 */     this._paramMarkersHaveBeenParsed = false;
/*      */   }
/*      */ 
/*      */   public void send(OutputStream paramOutputStream)
/*      */     throws IOException, SQLException
/*      */   {
/* 1218 */     this._next = 0;
/* 1219 */     for (int i = 0; i < this._params.length; ++i)
/*      */     {
/* 1221 */       this._params[i].send(paramOutputStream, this._context._maxFieldSize);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getLength()
/*      */   {
/* 1230 */     int i = 0;
/* 1231 */     for (int j = 0; j < this._params.length; ++j)
/*      */     {
/* 1233 */       i += this._params[j].getLength();
/*      */     }
/* 1235 */     return i;
/*      */   }
/*      */ 
/*      */   public void adjustOffsets(int paramInt)
/*      */   {
/* 1244 */     for (int i = 0; i < this._params.length; ++i)
/*      */     {
/* 1246 */       this._params[i]._paramMarkerOffset += paramInt;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void literalizeAll()
/*      */   {
/* 1254 */     for (int i = 0; i < this._params.length; ++i)
/*      */     {
/* 1256 */       this._params[i]._sendAsLiteral = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String processParamMarkers(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1269 */     if (this._params.length == 0)
/*      */     {
/* 1271 */       return null;
/*      */     }
/* 1273 */     if (this._dontProcessParamMarkers)
/*      */     {
/* 1275 */       this._dontProcessParamMarkers = false;
/* 1276 */       this._setHasBeenCalled = false;
/* 1277 */       return this._savedParsedQuery;
/*      */     }
/*      */ 
/* 1280 */     int i = 0;
/*      */ 
/* 1282 */     if (this._params[i]._sqlType == -998) ++i;
/*      */ 
/* 1284 */     if (i == this._params.length) return null;
/*      */ 
/* 1287 */     StringBuffer localStringBuffer = new StringBuffer("");
/* 1288 */     int j = 0;
/*      */     try
/*      */     {
/* 1291 */       while (i < this._params.length)
/*      */       {
/* 1293 */         if (this._params[i]._sqlType != -998)
/*      */         {
/* 1298 */           localStringBuffer.append(paramString.substring(j, this._params[i]._paramMarkerOffset));
/*      */ 
/* 1300 */           localStringBuffer.append(this._params[i].literalValue(this._protocol, i, this._context._maxFieldSize));
/*      */ 
/* 1302 */           j = this._params[i]._paramMarkerOffset + 1;
/*      */         }
/* 1304 */         ++i;
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1309 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */ 
/* 1312 */     if (j < paramString.length())
/*      */     {
/* 1314 */       localStringBuffer.append(paramString.substring(j));
/*      */     }
/*      */ 
/* 1317 */     this._savedParsedQuery = localStringBuffer.toString();
/* 1318 */     if ((!this._hasLiteralParam) && (this._copiedFrom != null) && 
/* 1320 */       (!this._copiedFrom._templateHoldsParsedNoLiteralQuery))
/*      */     {
/* 1322 */       synchronized (this._copiedFrom)
/*      */       {
/* 1324 */         if (!this._copiedFrom._templateHoldsParsedNoLiteralQuery)
/*      */         {
/* 1326 */           this._copiedFrom._savedParsedQuery = this._savedParsedQuery;
/* 1327 */           this._copiedFrom._hasLiteralParam = this._hasLiteralParam;
/* 1328 */           this._copiedFrom._templateHoldsParsedNoLiteralQuery = true;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1334 */     this._dontProcessParamMarkers = false;
/* 1335 */     this._setHasBeenCalled = false;
/* 1336 */     this._paramMarkersHaveBeenParsed = true;
/* 1337 */     return (String)this._savedParsedQuery;
/*      */   }
/*      */ 
/*      */   public int makeFormats(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1346 */     int i = 0;
/* 1347 */     byte b = 4;
/* 1348 */     if ((this._stmt != null) && (this._protocol.isLOBSupportedAsParameterToSproc()))
/*      */     {
/* 1350 */       b = this._stmt.getExecutionMode();
/*      */     }
/* 1352 */     for (int j = 0; j < this._params.length; ++j)
/*      */     {
/* 1354 */       if (!this._params[j].makeFormat(this._protocol, b)) {
/*      */         continue;
/*      */       }
/*      */ 
/* 1358 */       if ((paramBoolean) && (((((TdsParam)this._params[j])._sqlType == 2) || (((TdsParam)this._params[j])._sqlType == 3))))
/*      */       {
/* 1362 */         ((TdsParam)this._params[j]).setPrecision(getParamMD(j)._precision);
/*      */       }
/* 1364 */       ++i;
/*      */     }
/*      */ 
/* 1367 */     return i;
/*      */   }
/*      */ 
/*      */   public void sendFormats(DataOutput paramDataOutput)
/*      */     throws IOException
/*      */   {
/* 1375 */     for (int i = 0; i < this._params.length; ++i)
/*      */     {
/* 1377 */       this._params[i].sendFormat(paramDataOutput);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getParamIndexByName(String paramString)
/*      */   {
/* 1385 */     for (int i = 0; i < this._params.length; ++i)
/*      */     {
/* 1387 */       if ((this._params[i]._name == null) || (this._params[i]._name.equalsIgnoreCase(paramString)))
/*      */       {
/* 1390 */         return i + 1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1395 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getParamSetType()
/*      */   {
/* 1400 */     return this._paramSetType;
/*      */   }
/*      */ 
/*      */   private void useParamByName() throws SQLException
/*      */   {
/* 1405 */     if (this._paramSetType == 2)
/*      */     {
/* 1407 */       ErrorMessage.raiseError("JZ0SV");
/*      */     }
/* 1409 */     if (this._paramSetType == 1)
/*      */       return;
/* 1411 */     this._paramSetType = 1;
/*      */   }
/*      */ 
/*      */   private void useParamByIndex()
/*      */     throws SQLException
/*      */   {
/* 1417 */     if (this._paramSetType == 1)
/*      */     {
/* 1419 */       ErrorMessage.raiseError("JZ0SV");
/*      */     } else {
/* 1421 */       if (this._paramSetType == 2)
/*      */         return;
/* 1423 */       this._paramSetType = 2;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkForNanos(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1434 */     if (this._params[paramInt]._sqlType != 93)
/*      */       return;
/*      */     try
/*      */     {
/* 1438 */       if ((this._params[paramInt]._inValue != null) && 
/* 1441 */         (!this._protocol.serverAcceptsBigDateTimeData()))
/*      */       {
/* 1443 */         SybTimestamp.checkNanos((DateObject)this._params[paramInt]._inValue);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1449 */       this._stmt.handleSQLE(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String getColumnNames()
/*      */   {
/* 1459 */     StringBuffer localStringBuffer = null;
/*      */ 
/* 1461 */     for (int i = 0; i < this._params.length; ++i)
/*      */     {
/* 1463 */       if ((this._stmt._hasReturn) && (i == 0))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/* 1468 */       if (this._params[i]._name == null)
/*      */         continue;
/* 1470 */       if (localStringBuffer == null)
/*      */       {
/* 1472 */         localStringBuffer = new StringBuffer();
/*      */       }
/* 1474 */       localStringBuffer.append("'");
/* 1475 */       localStringBuffer.append(this._params[i]._name);
/* 1476 */       localStringBuffer.append("',");
/*      */     }
/*      */ 
/* 1480 */     if (localStringBuffer != null)
/*      */     {
/* 1482 */       return localStringBuffer.substring(0, localStringBuffer.length() - 1);
/*      */     }
/*      */ 
/* 1486 */     return null;
/*      */   }
/*      */ 
/*      */   protected String drainReader(Reader paramReader, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1500 */     StringBuffer localStringBuffer = null;
/* 1501 */     boolean bool = false;
/* 1502 */     if (this._stmt instanceof SybPreparedStatement)
/*      */     {
/* 1504 */       bool = ((SybPreparedStatement)this._stmt)._batchInitialized;
/*      */     }
/*      */ 
/* 1507 */     if ((!bool) && (paramReader instanceof LobLocatorBufferedReader) && (this._context._conn.canUseLocators()))
/*      */     {
/* 1509 */       localStringBuffer = new StringBuffer();
/*      */       try
/*      */       {
/* 1512 */         if (paramInt == -1)
/*      */         {
/* 1514 */           paramInt = 2147483647;
/*      */         }
/* 1516 */         int i = -1;
/* 1517 */         while ((paramInt-- > 0) && ((i = paramReader.read()) != -1))
/*      */         {
/* 1519 */           localStringBuffer.append((char)i);
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1524 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */       }
/*      */     }
/* 1527 */     return (localStringBuffer == null) ? null : localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   protected byte[] drainStreams(InputStream paramInputStream, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1539 */     byte[] arrayOfByte = null;
/* 1540 */     boolean bool = false;
/* 1541 */     int i = -1;
/* 1542 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 1543 */     if (this._stmt instanceof SybPreparedStatement)
/*      */     {
/* 1545 */       bool = ((SybPreparedStatement)this._stmt)._batchInitialized;
/*      */     }
/*      */ 
/* 1548 */     if ((!bool) && (paramInputStream instanceof LobLocatorBufferedInputStream) && (this._context._conn.canUseLocators()))
/*      */     {
/*      */       try
/*      */       {
/* 1552 */         if (paramInt == -1)
/*      */         {
/* 1554 */           paramInt = paramInputStream.available();
/*      */         }
/* 1556 */         arrayOfByte = new byte[paramInt];
/* 1557 */         while ((i = paramInputStream.read(arrayOfByte)) != -1)
/*      */         {
/* 1559 */           localByteArrayOutputStream.write(arrayOfByte, 0, i);
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1564 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */       }
/*      */     }
/* 1567 */     return (arrayOfByte == null) ? null : localByteArrayOutputStream.toByteArray();
/*      */   }
/*      */ 
/*      */   public Param getParamMD(int paramInt)
/*      */   {
/* 1572 */     return (Param)this._paramMdList.get(paramInt);
/*      */   }
/*      */ 
/*      */   public void initParamTypesArray()
/*      */   {
/* 1578 */     this._prevParamTypes = new int[this._params.length];
/*      */   }
/*      */ 
/*      */   public void copyCurIntoPrevParamTypes()
/*      */   {
/* 1588 */     for (int i = 0; i < this._params.length; ++i)
/*      */     {
/* 1590 */       this._prevParamTypes[i] = this._params[i]._sqlType;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean hasParamsNotChanged()
/*      */   {
/* 1600 */     for (int i = 0; i < this._params.length; ++i)
/*      */     {
/* 1602 */       if (this._prevParamTypes[i] != this._params[i]._sqlType)
/*      */       {
/* 1604 */         return false;
/*      */       }
/*      */     }
/* 1607 */     return true;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.ParamManager
 * JD-Core Version:    0.5.4
 */