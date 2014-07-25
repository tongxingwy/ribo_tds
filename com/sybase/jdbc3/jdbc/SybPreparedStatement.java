/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import I;
/*      */ import com.sybase.jdbc3.tds.SybBigDecimal;
/*      */ import com.sybase.jdbc3.tds.TdsProtocolContext;
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import com.sybase.jdbc3.utils.LogUtil;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.LinkedList;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ public class SybPreparedStatement extends SybStatement
/*      */   implements com.sybase.jdbcx.SybPreparedStatement, ParameterMetaData
/*      */ {
/*   61 */   private static Logger LOG = Logger.getLogger(SybPreparedStatement.class.getName());
/*   62 */   private static volatile long _logIdCounter = 0L;
/*      */   private static final int MAX_PARAMETERS = 1000;
/*      */   protected static final int BASE_MAX_BATCH_PARAMETERS = 255;
/*      */   private static final int NOT_SET = 0;
/*      */   private static final int IN_PARENTHESES = 1;
/*      */   private static final int IN_VALUES_BLOCK = 2;
/*      */   private static final int IN_SET_BLOCK = 4;
/*      */   private static final int IN_WHERE_BLOCK = 8;
/*      */   private static final int IN_SINGLE_QUOTES = 16;
/*      */   private static final int IN_DOUBLE_QUOTES = 32;
/*      */   private static final int IN_CURLIES = 64;
/*      */   private static final int IN_CALL_BLOCK = 128;
/*   78 */   String _dynStmtName = null;
/*      */ 
/*   82 */   private int _enableBCP = 0;
/*      */   protected LinkedList _bulkTypes;
/*      */   protected LinkedList _bulkObject;
/*      */   protected LinkedList _bulkScale;
/*      */   protected LinkedList _bulkCal;
/*   93 */   private int _rowPosition = 0;
/*      */ 
/*   95 */   private SybBCP _sybBCP = null;
/*   96 */   int _paramCount = 0;
/*   97 */   ArrayList _batchParams = null;
/*      */ 
/*  100 */   protected String _sqlStr = null;
/*      */ 
/*  103 */   protected String _paramColids = null;
/*  104 */   protected String _paramNames = null;
/*      */ 
/*  107 */   private String _whereBlock = null;
/*      */   protected int[] _types;
/*      */   protected Object[] _object;
/*      */   protected int[] _scale;
/*      */   protected Calendar[] _cal;
/*  113 */   private int _batchOffset = 0;
/*  114 */   private int _numRows = 0;
/*      */   private int[] _batchFirstRowParamType;
/*  117 */   private boolean _isDynamic = false;
/*      */ 
/*  119 */   private boolean _homoGeneousBatch = false;
/*  120 */   protected boolean _sendBatchParamsImmediate = false;
/*      */ 
/*  125 */   boolean _batchInitialized = false;
/*      */ 
/*  128 */   private boolean _batchLock = false;
/*      */ 
/*  132 */   private boolean _lobSetterCalled = false;
/*      */ 
/*  136 */   private boolean _firstExecute = true;
/*      */ 
/*  236 */   protected String _query = null;
/*      */   protected ParamManager _paramMgr;
/*      */ 
/*      */   SybPreparedStatement(String paramString1, ProtocolContext paramProtocolContext, String paramString2, int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  143 */     super(paramString1, paramProtocolContext);
/*  144 */     this._logId = (paramString1 + "_Ps" + _logIdCounter++);
/*      */ 
/*  155 */     this._dynStmtName = "placeholder";
/*      */ 
/*  157 */     paramString2 = doEscapeProcessing(paramString2);
/*  158 */     paramString2 = processGenKeysRequest(paramInt2, paramString2);
/*      */ 
/*  160 */     this._paramMgr = countParams(paramString2);
/*  161 */     this._paramCount = this._paramMgr._params.length;
/*      */ 
/*  163 */     String str = "dyn" + paramInt1;
/*  164 */     ((TdsProtocolContext)this._context).setIsSelectSql((this._type & 0x2) == 2);
/*  165 */     this._protocol.dynamicPrepare(this._context, str, paramString2, this._paramMgr);
/*      */ 
/*  167 */     this._dynStmtName = str;
/*      */ 
/*  169 */     if ((this._paramCount > 0) && (isInsertInSQL(paramString2)))
/*      */     {
/*  171 */       prepareBCP(paramProtocolContext);
/*      */     }
/*      */     else
/*      */     {
/*  175 */       initializeParamArrays(this._paramCount);
/*      */     }
/*  177 */     this._isDynamic = ((this._dynStmtName != null) && (!checkBatch()));
/*  178 */     this._executionMode = ((this._isDynamic) ? 0 : 1);
/*      */ 
/*  180 */     this._sendBatchParamsImmediate = ((this._context._conn._props.getBoolean(83)) && (!this._context._conn._props.getBoolean(15)));
/*      */ 
/*  183 */     this._homoGeneousBatch = ((this._context._conn._props.getBoolean(84)) && (!this._context._conn._props.getBoolean(15)));
/*      */   }
/*      */ 
/*      */   SybPreparedStatement(String paramString1, ProtocolContext paramProtocolContext, String paramString2, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  193 */     super(paramString1, paramProtocolContext);
/*  194 */     this._logId = (paramString1 + "_Ps" + _logIdCounter++);
/*  195 */     initializeParamArrays(this._paramCount);
/*      */ 
/*  197 */     this._executionMode = 1;
/*  198 */     paramString2 = doEscapeProcessing(paramString2);
/*  199 */     this._sqlStr = processGenKeysRequest(paramInt, paramString2);
/*      */ 
/*  201 */     this._paramMgr = countParams(this._sqlStr);
/*  202 */     this._paramCount = this._paramMgr._params.length;
/*      */ 
/*  204 */     if ((this._paramCount > 0) && (isInsertInSQL(this._sqlStr)))
/*      */     {
/*  206 */       prepareBCP(paramProtocolContext);
/*      */     }
/*  208 */     this._sendBatchParamsImmediate = ((this._context._conn._props.getBoolean(83)) && (!this._context._conn._props.getBoolean(15)));
/*      */ 
/*  211 */     this._homoGeneousBatch = ((this._context._conn._props.getBoolean(84)) && (!this._context._conn._props.getBoolean(15)));
/*      */   }
/*      */ 
/*      */   SybPreparedStatement(String paramString, ProtocolContext paramProtocolContext, SybPreparedStatement paramSybPreparedStatement)
/*      */     throws SQLException
/*      */   {
/*  222 */     super(paramString, paramProtocolContext);
/*  223 */     this._logId = (paramString + "_Ps" + _logIdCounter++);
/*  224 */     this._query = paramSybPreparedStatement._query;
/*      */ 
/*  226 */     this._paramMgr = new ParamManager(paramSybPreparedStatement._paramMgr, this);
/*  227 */     this._paramCount = this._paramMgr._params.length;
/*  228 */     if (!this._context._conn._props.getBoolean(58))
/*      */       return;
/*  230 */     ((TdsProtocolContext)paramProtocolContext).copyColumnCache((TdsProtocolContext)paramSybPreparedStatement._context);
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/*  245 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  247 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  249 */       LOG.fine(this._logId + " executeQuery()");
/*      */     }
/*      */ 
/*  255 */     return super.executeQuery(this._query, this._paramMgr);
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery(String paramString)
/*      */     throws SQLException
/*      */   {
/*  261 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  263 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  265 */         LOG.finer(this._logId + " executeQuery(String = [" + paramString + "])");
/*      */       }
/*  267 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  269 */         LOG.fine(this._logId + " executeQuery(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  274 */     ErrorMessage.raiseError("JZ0S3", "executeQuery(String)");
/*      */ 
/*  276 */     return null;
/*      */   }
/*      */ 
/*      */   public int executeUpdate()
/*      */     throws SQLException
/*      */   {
/*  284 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  286 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  288 */       LOG.fine(this._logId + " executeUpdate()");
/*      */     }
/*      */ 
/*  294 */     return super.executeUpdate(this._query, this._paramMgr);
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString)
/*      */     throws SQLException
/*      */   {
/*  300 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  302 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  304 */         LOG.finer(this._logId + " executeUpdate(int = [" + paramString + "])");
/*      */       }
/*  306 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  308 */         LOG.fine(this._logId + " executeUpdate(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  313 */     ErrorMessage.raiseError("JZ0S3", "executeUpdate(String)");
/*      */ 
/*  315 */     return 0;
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  321 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  323 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  325 */         LOG.finer(this._logId + " executeUpdate(String = [" + paramString + "], int = [" + paramInt + "])");
/*      */       }
/*  328 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  330 */         LOG.fine(this._logId + " executeUpdate(String, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  335 */     ErrorMessage.raiseError("JZ0S3", "executeUpdate(String, int)");
/*      */ 
/*  337 */     return 0;
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/*  343 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  345 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  347 */         LOG.finest(LogUtil.logMethod(false, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/*  350 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  352 */         LOG.finer(LogUtil.logMethod(true, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/*  355 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  357 */         LOG.fine(this._logId + " executeUpdate(String, int[])");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  362 */     ErrorMessage.raiseError("JZ0S3", "executeUpdate(String, int[])");
/*      */ 
/*  364 */     return 0;
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString, String[] paramArrayOfString)
/*      */     throws SQLException
/*      */   {
/*  370 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  372 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  374 */         LOG.finest(LogUtil.logMethod(false, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/*  377 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  379 */         LOG.finer(LogUtil.logMethod(true, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/*  382 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  384 */         LOG.fine(this._logId + " executeUpdate(String, String[])");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  389 */     ErrorMessage.raiseError("JZ0S3", "executeUpdate(String, String[])");
/*      */ 
/*  391 */     return 0;
/*      */   }
/*      */ 
/*      */   public void setNull(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  399 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  401 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  403 */         LOG.finer(this._logId + " setNull(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */       }
/*  406 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  408 */         LOG.fine(this._logId + " setNull(int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  413 */     checkDead();
/*  414 */     Object localObject = null;
/*      */ 
/*  417 */     if (paramInt2 == -7)
/*      */     {
/*  419 */       ErrorMessage.raiseError("JZNNA");
/*      */     }
/*  421 */     if (paramInt2 == 2005)
/*      */     {
/*  423 */       localObject = this._context._conn._nullClob;
/*      */     }
/*  429 */     else if (paramInt2 == 2004)
/*      */     {
/*  431 */       localObject = this._context._conn._nullBlob;
/*      */     }
/*  433 */     setParam(paramInt2, paramInt1, localObject);
/*      */   }
/*      */ 
/*      */   public void setNull(int paramInt1, int paramInt2, String paramString)
/*      */     throws SQLException
/*      */   {
/*  444 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  446 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  448 */         LOG.finer(this._logId + " setNull(int = [" + paramInt1 + "], int = [" + paramInt2 + "] , String = [" + paramString + "])");
/*      */       }
/*  452 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  454 */         LOG.fine(this._logId + " setNull(int, int, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  459 */     Debug.notImplemented(this, "setNull(int, int, String)");
/*      */   }
/*      */ 
/*      */   public void setBoolean(int paramInt, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  468 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  470 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  472 */         LOG.finer(this._logId + " setBoolean(int = [" + paramInt + "], boolean = [" + paramBoolean + "])");
/*      */       }
/*  475 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  477 */         LOG.fine(this._logId + " setBoolean(int, boolean)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  482 */     checkDead();
/*  483 */     checkIfMixedWithLob(paramInt, -7);
/*  484 */     Boolean localBoolean = new Boolean(paramBoolean);
/*  485 */     setParam(-7, paramInt, localBoolean);
/*      */   }
/*      */ 
/*      */   public void setByte(int paramInt, byte paramByte)
/*      */     throws SQLException
/*      */   {
/*  493 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  495 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  497 */         LOG.finer(this._logId + " setByte(int = [" + paramInt + "], byte = [" + paramByte + "])");
/*      */       }
/*  500 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  502 */         LOG.fine(this._logId + " setByte(int, byte)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  507 */     checkDead();
/*  508 */     checkIfMixedWithLob(paramInt, 5);
/*  509 */     Integer localInteger = new Integer(paramByte);
/*  510 */     if (localInteger.intValue() < 0)
/*      */     {
/*  516 */       setParam(5, paramInt, localInteger);
/*      */     }
/*      */     else
/*      */     {
/*  520 */       setParam(-6, paramInt, localInteger);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setShort(int paramInt, short paramShort)
/*      */     throws SQLException
/*      */   {
/*  529 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  531 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  533 */         LOG.finer(this._logId + " setShort(int = [" + paramInt + "], short = [" + paramShort + "])");
/*      */       }
/*  536 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  538 */         LOG.fine(this._logId + " setShort(int, short)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  543 */     checkDead();
/*  544 */     checkIfMixedWithLob(paramInt, 5);
/*  545 */     Integer localInteger = new Integer(paramShort);
/*  546 */     setParam(5, paramInt, localInteger);
/*      */   }
/*      */ 
/*      */   public void setInt(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  554 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  556 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  558 */         LOG.finer(this._logId + " setInt(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */       }
/*  561 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  563 */         LOG.fine(this._logId + " setInt(int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  569 */     if (this._enableBCP == 0)
/*      */     {
/*  571 */       checkDead();
/*      */     }
/*  573 */     checkIfMixedWithLob(paramInt1, 4);
/*  574 */     setParam(4, paramInt1, new Integer(paramInt2));
/*      */   }
/*      */ 
/*      */   public void setLong(int paramInt, long paramLong)
/*      */     throws SQLException
/*      */   {
/*  582 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  584 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  586 */         LOG.finer(this._logId + " setLong(int = [" + paramInt + "], long = [" + paramLong + "])");
/*      */       }
/*  589 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  591 */         LOG.fine(this._logId + " setLong(int, long)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  596 */     checkDead();
/*  597 */     checkIfMixedWithLob(paramInt, -5);
/*  598 */     Long localLong = new Long(paramLong);
/*  599 */     setParam(-5, paramInt, localLong);
/*      */   }
/*      */ 
/*      */   public void setFloat(int paramInt, float paramFloat)
/*      */     throws SQLException
/*      */   {
/*  607 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  609 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  611 */         LOG.finer(this._logId + " setFloat(int = [" + paramInt + "], float = [" + paramFloat + "])");
/*      */       }
/*  614 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  616 */         LOG.fine(this._logId + " setFloat(int, float)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  621 */     checkDead();
/*  622 */     checkIfMixedWithLob(paramInt, 7);
/*  623 */     Float localFloat = new Float(paramFloat);
/*  624 */     setParam(7, paramInt, localFloat);
/*      */   }
/*      */ 
/*      */   public void setDouble(int paramInt, double paramDouble)
/*      */     throws SQLException
/*      */   {
/*  632 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  634 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  636 */         LOG.finer(this._logId + " setDouble(int = [" + paramInt + "], double = [" + paramDouble + "])");
/*      */       }
/*  639 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  641 */         LOG.fine(this._logId + " setDouble(int, double)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  646 */     checkDead();
/*  647 */     checkIfMixedWithLob(paramInt, 8);
/*  648 */     Double localDouble = new Double(paramDouble);
/*  649 */     setParam(8, paramInt, localDouble);
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(int paramInt, BigDecimal paramBigDecimal)
/*      */     throws SQLException
/*      */   {
/*  658 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  660 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  662 */         LOG.finer(this._logId + " setBigDecimal(int = [" + paramInt + "], BigDecimal = [" + paramBigDecimal + "])");
/*      */       }
/*  665 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  667 */         LOG.fine(this._logId + " setBigDecimal(int, BigDecimal)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  672 */     checkDead();
/*  673 */     checkIfMixedWithLob(paramInt, 2);
/*      */ 
/*  676 */     if (paramBigDecimal == null)
/*      */     {
/*  678 */       setNull(paramInt, 2);
/*      */     }
/*      */     else
/*      */     {
/*  682 */       setParam(2, paramInt, paramBigDecimal, paramBigDecimal.scale());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkIfMixedWithLob(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  694 */     if ((this._homoGeneousBatch != true) || (this._batchFirstRowParamType == null) || (this._batchFirstRowParamType.length <= 0)) {
/*      */       return;
/*      */     }
/*  697 */     int i = this._batchFirstRowParamType[(paramInt1 - 1)];
/*  698 */     if (i == -999)
/*      */     {
/*  700 */       this._batchFirstRowParamType[(paramInt1 - 1)] = paramInt2;
/*      */     } else {
/*  702 */       if ((i != 2005) && (i != 2004)) {
/*      */         return;
/*      */       }
/*      */ 
/*  706 */       ErrorMessage.raiseError("JZ042", String.valueOf(paramInt2), String.valueOf(i));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(int paramInt1, BigDecimal paramBigDecimal, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*  721 */     checkDead();
/*  722 */     checkIfMixedWithLob(paramInt1, 2);
/*      */ 
/*  724 */     if (paramBigDecimal == null)
/*      */     {
/*  729 */       paramBigDecimal = new BigDecimal("0");
/*      */     }
/*  731 */     SybBigDecimal localSybBigDecimal = new SybBigDecimal(paramBigDecimal, paramInt2, paramInt3);
/*  732 */     setParam(2, paramInt1, localSybBigDecimal);
/*      */   }
/*      */ 
/*      */   public void setString(int paramInt, String paramString)
/*      */     throws SQLException
/*      */   {
/*  740 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  742 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  744 */         LOG.finer(this._logId + " setString(int = [" + paramInt + "], String = [" + paramString + "])");
/*      */       }
/*  747 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  749 */         LOG.fine(this._logId + " setString(int, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  754 */     checkDead();
/*  755 */     checkIfMixedWithLob(paramInt, 12);
/*      */ 
/*  758 */     setParam(12, paramInt, paramString);
/*      */   }
/*      */ 
/*      */   public void setBytes(int paramInt, byte[] paramArrayOfByte)
/*      */     throws SQLException
/*      */   {
/*  766 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  768 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  770 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBytes", new Object[] { new Integer(paramInt), paramArrayOfByte }));
/*      */       }
/*  773 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  775 */         LOG.fine(this._logId + " setBytes(int, byte)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  780 */     checkDead();
/*  781 */     checkIfMixedWithLob(paramInt, -4);
/*  782 */     int i = 0;
/*  783 */     if (paramArrayOfByte != null)
/*      */     {
/*  785 */       i = paramArrayOfByte.length;
/*      */     }
/*  787 */     if (i > 255)
/*      */     {
/*  789 */       setParam(-4, paramInt, paramArrayOfByte);
/*      */     }
/*      */     else
/*      */     {
/*  793 */       setParam(-3, paramInt, paramArrayOfByte);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(int paramInt, Date paramDate)
/*      */     throws SQLException
/*      */   {
/*  803 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  805 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  807 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setDate", new Object[] { new Integer(paramInt), paramDate }));
/*      */       }
/*  810 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  812 */         LOG.fine(this._logId + " setDate(int, java.sql.Date)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  817 */     checkDead();
/*  818 */     checkIfMixedWithLob(paramInt, 91);
/*  819 */     if ((paramDate == null) || (this._isDynamic) || (this._enableBCP > 0))
/*      */     {
/*  821 */       setParam(91, paramInt, paramDate);
/*      */     }
/*      */     else
/*      */     {
/*  825 */       setParam(91, paramInt, new DateObject(paramDate, 91));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(int paramInt, Time paramTime)
/*      */     throws SQLException
/*      */   {
/*  835 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  837 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  839 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setTime", new Object[] { new Integer(paramInt), paramTime }));
/*      */       }
/*  842 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  844 */         LOG.fine(this._logId + " setTime(int, java.sql.Time)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  849 */     checkDead();
/*  850 */     checkIfMixedWithLob(paramInt, 92);
/*  851 */     if ((paramTime == null) || (this._isDynamic) || (this._enableBCP > 0))
/*      */     {
/*  853 */       setParam(92, paramInt, paramTime);
/*      */     }
/*      */     else
/*      */     {
/*  857 */       setParam(92, paramInt, new DateObject(paramTime, 92));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int paramInt, Timestamp paramTimestamp)
/*      */     throws SQLException
/*      */   {
/*  867 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  869 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  871 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setTimestamp", new Object[] { new Integer(paramInt), paramTimestamp }));
/*      */       }
/*  874 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  876 */         LOG.fine(this._logId + " setTimestamp(int, java.sql.Timestamp)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  881 */     checkDead();
/*  882 */     checkIfMixedWithLob(paramInt, 93);
/*  883 */     if ((paramTimestamp == null) || (this._isDynamic) || (this._enableBCP > 0))
/*      */     {
/*  885 */       setParam(93, paramInt, paramTimestamp);
/*      */     }
/*      */     else
/*      */     {
/*  889 */       setParam(93, paramInt, new DateObject(paramTimestamp, 93));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  900 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  902 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  904 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/*  907 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  909 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/*  912 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  914 */         LOG.fine(this._logId + " setAsciiStream(int, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  920 */     checkDead();
/*  921 */     checkIfMixedWithLob(paramInt1, -1);
/*  922 */     if (paramInputStream == null)
/*      */     {
/*  924 */       setNull(paramInt1, -1);
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/*  938 */         InputStreamReader localInputStreamReader = new InputStreamReader(new LimiterInputStream(paramInputStream, paramInt2), "ISO8859_1");
/*      */ 
/*  941 */         String str = this._paramMgr.drainReader(localInputStreamReader, paramInt2);
/*  942 */         if (str != null)
/*      */         {
/*  944 */           setParam(12, paramInt1, str, 0);
/*      */         }
/*      */         else
/*      */         {
/*  948 */           setParam(-1, paramInt1, localInputStreamReader, paramInt2);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  954 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setUnicodeStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*      */     throws SQLException
/*      */   {
/*  964 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  966 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  968 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setUnicodeStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/*  972 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  974 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setUnicodeStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/*  977 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  979 */         LOG.fine(this._logId + " setUnicodeStream(int, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  985 */     checkDead();
/*  986 */     checkIfMixedWithLob(paramInt1, -1);
/*  987 */     if (paramInputStream == null)
/*      */     {
/*  989 */       setNull(paramInt1, -1);
/*      */     }
/*      */     else
/*      */     {
/*  993 */       byte[] arrayOfByte = this._paramMgr.drainStreams(paramInputStream, paramInt2);
/*  994 */       if (arrayOfByte != null)
/*      */       {
/*  996 */         setBytes(paramInt1, arrayOfByte);
/*      */       }
/*      */       else
/*      */       {
/* 1000 */         setParam(-1, paramInt1, paramInputStream, paramInt2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1011 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1013 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1015 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/* 1018 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1020 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt1), paramInputStream, new Integer(paramInt2) }));
/*      */       }
/* 1023 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1025 */         LOG.fine(this._logId + " setBinaryStream(int, java.io.InputStream, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1031 */     checkDead();
/* 1032 */     checkIfMixedWithLob(paramInt1, -4);
/*      */ 
/* 1034 */     byte[] arrayOfByte = this._paramMgr.drainStreams(paramInputStream, paramInt2);
/* 1035 */     if (arrayOfByte != null)
/*      */     {
/* 1037 */       setBytes(paramInt1, arrayOfByte);
/*      */     }
/*      */     else
/*      */     {
/* 1041 */       setParam(-4, paramInt1, paramInputStream, paramInt2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearParameters()
/*      */     throws SQLException
/*      */   {
/* 1050 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1052 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1054 */       LOG.fine(this._logId + " clearParameters()");
/*      */     }
/*      */ 
/* 1059 */     checkDead();
/*      */     try
/*      */     {
/* 1062 */       if ((this._isDynamic) && (this._enableBCP < 2))
/*      */       {
/* 1064 */         for (int i = this._batchOffset; ; ++i) { if (i >= this._batchOffset + this._paramCount)
/*      */             break label130;
/* 1066 */           this._types[i] = -999;
/* 1067 */           this._scale[i] = 0;
/* 1068 */           this._cal[i] = null;
/* 1069 */           this._object[i] = null; }
/*      */ 
/*      */ 
/*      */       }
/*      */ 
/* 1075 */       label130: this._paramMgr.clearParams(true);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1080 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
/*      */     throws SQLException
/*      */   {
/* 1091 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1093 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1095 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setObject", new Object[] { new Integer(paramInt1), paramObject, new Integer(paramInt2), new Integer(paramInt3) }));
/*      */       }
/* 1100 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1102 */         LOG.fine(this._logId + " setObject(int, Object, int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1107 */     checkDead();
/* 1108 */     if (paramObject == null)
/*      */     {
/* 1110 */       setParam(paramInt2, paramInt1, null, paramInt3);
/*      */     }
/* 1112 */     switch (paramInt2)
/*      */     {
/*      */     case -7:
/* 1115 */       setParam(paramInt2, paramInt1, Convert.objectToBoolean(paramObject), paramInt3);
/*      */ 
/* 1117 */       break;
/*      */     case -6:
/*      */     case 4:
/*      */     case 5:
/* 1121 */       setParam(paramInt2, paramInt1, Convert.objectToInt(paramObject), paramInt3);
/*      */ 
/* 1123 */       break;
/*      */     case -5:
/* 1125 */       setParam(paramInt2, paramInt1, Convert.objectToLong(paramObject), paramInt3);
/*      */ 
/* 1127 */       break;
/*      */     case 1:
/*      */     case 12:
/* 1132 */       setString(paramInt1, Convert.objectToString(paramObject));
/* 1133 */       break;
/*      */     case -1:
/* 1139 */       setParam(-1, paramInt1, Convert.objectToString(paramObject));
/*      */ 
/* 1141 */       break;
/*      */     case 2:
/*      */     case 3:
/* 1144 */       if (paramInt3 < 0)
/*      */       {
/* 1147 */         ErrorMessage.raiseError("JZ00I");
/*      */       }
/* 1149 */       setParam(paramInt2, paramInt1, Convert.objectToBigDecimal(paramObject), paramInt3);
/*      */ 
/* 1151 */       break;
/*      */     case 7:
/* 1153 */       setParam(paramInt2, paramInt1, Convert.objectToFloat(paramObject), paramInt3);
/*      */ 
/* 1155 */       break;
/*      */     case 6:
/*      */     case 8:
/* 1158 */       setParam(paramInt2, paramInt1, Convert.objectToDouble(paramObject), paramInt3);
/*      */ 
/* 1160 */       break;
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/* 1164 */       setBytes(paramInt1, Convert.objectToBytes(paramObject));
/* 1165 */       break;
/*      */     case 2004:
/* 1167 */       setBlob(paramInt1, (Blob)Convert.objectToLob(paramObject, 0));
/* 1168 */       break;
/*      */     case 2005:
/* 1170 */       setClob(paramInt1, (Clob)Convert.objectToLob(paramObject, 1));
/* 1171 */       break;
/*      */     case 91:
/*      */     case 92:
/*      */     case 93:
/* 1178 */       setParam(paramInt2, paramInt1, Convert.objectToDateObject(paramObject, paramInt2, null), paramInt3);
/*      */ 
/* 1181 */       break;
/*      */     case 1111:
/*      */     case 2000:
/* 1186 */       if ((!paramObject instanceof Serializable) && (paramObject != null)) {
/*      */         break label545;
/*      */       }
/* 1189 */       setParam(2000, paramInt1, paramObject);
/* 1190 */       break;
/*      */     default:
/* 1194 */       label545: ErrorMessage.raiseError("JZ0SE");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(int paramInt1, Object paramObject, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1204 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1206 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1208 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setObject", new Object[] { new Integer(paramInt1), paramObject, new Integer(paramInt2) }));
/*      */       }
/* 1211 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1213 */         LOG.fine(this._logId + " setObject(int, Object, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1218 */     checkDead();
/* 1219 */     setObject(paramInt1, paramObject, paramInt2, 0);
/*      */   }
/*      */ 
/*      */   public void setObject(int paramInt, Object paramObject)
/*      */     throws SQLException
/*      */   {
/* 1227 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1229 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1231 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setObject", new Object[] { new Integer(paramInt), paramObject }));
/*      */       }
/* 1234 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1236 */         LOG.fine(this._logId + " setObject(int, Object)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1241 */     checkDead();
/* 1242 */     if (paramObject == null)
/*      */     {
/* 1244 */       ErrorMessage.raiseError("JZ0SE");
/*      */     }
/*      */ 
/* 1247 */     if (paramObject instanceof Short)
/*      */     {
/* 1249 */       setShort(paramInt, ((Short)paramObject).shortValue());
/*      */     }
/* 1251 */     else if (paramObject instanceof Byte)
/*      */     {
/* 1253 */       setByte(paramInt, ((Byte)paramObject).byteValue());
/*      */     }
/* 1255 */     else if (paramObject instanceof String)
/*      */     {
/* 1257 */       setString(paramInt, (String)paramObject);
/*      */     }
/* 1259 */     else if (paramObject instanceof BigDecimal)
/*      */     {
/* 1261 */       setBigDecimal(paramInt, (BigDecimal)paramObject);
/*      */     }
/* 1263 */     else if (paramObject instanceof Boolean)
/*      */     {
/* 1265 */       setParam(-7, paramInt, paramObject);
/*      */     }
/* 1267 */     else if (paramObject instanceof Integer)
/*      */     {
/* 1269 */       setParam(4, paramInt, paramObject);
/*      */     }
/* 1271 */     else if (paramObject instanceof Long)
/*      */     {
/* 1273 */       setParam(-5, paramInt, paramObject);
/*      */     }
/* 1275 */     else if (paramObject instanceof Float)
/*      */     {
/* 1277 */       setParam(7, paramInt, paramObject);
/*      */     }
/* 1279 */     else if (paramObject instanceof Double)
/*      */     {
/* 1281 */       setParam(8, paramInt, paramObject);
/*      */     }
/* 1283 */     else if (paramObject instanceof byte[])
/*      */     {
/* 1285 */       setBytes(paramInt, (byte[])paramObject);
/*      */     }
/* 1287 */     else if (paramObject instanceof Date)
/*      */     {
/* 1289 */       setDate(paramInt, (Date)paramObject);
/*      */     }
/* 1291 */     else if (paramObject instanceof Time)
/*      */     {
/* 1293 */       setTime(paramInt, (Time)paramObject);
/*      */     }
/* 1295 */     else if (paramObject instanceof Timestamp)
/*      */     {
/* 1297 */       setTimestamp(paramInt, (Timestamp)paramObject);
/*      */     }
/* 1299 */     else if (paramObject instanceof Clob)
/*      */     {
/* 1301 */       setClob(paramInt, (Clob)paramObject);
/*      */     }
/* 1307 */     else if (paramObject instanceof Blob)
/*      */     {
/* 1309 */       setBlob(paramInt, (Blob)paramObject);
/*      */     }
/* 1311 */     else if (paramObject instanceof Serializable)
/*      */     {
/* 1314 */       setParam(2000, paramInt, paramObject);
/*      */     }
/*      */     else
/*      */     {
/* 1318 */       ErrorMessage.raiseError("JZ0SE");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setURL(int paramInt, URL paramURL)
/*      */   {
/* 1328 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1330 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1332 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setURL", new Object[] { new Integer(paramInt), paramURL }));
/*      */       }
/* 1335 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1337 */         LOG.fine(this._logId + " setURL(int, URL)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1342 */     Debug.notImplemented(this, "setURL(int, URL)");
/*      */   }
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/* 1350 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1352 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1354 */       LOG.fine(this._logId + " execute()");
/*      */     }
/*      */ 
/* 1360 */     return super.execute(this._query, this._paramMgr);
/*      */   }
/*      */ 
/*      */   private void addBulkBatch()
/*      */     throws SQLException
/*      */   {
/* 1369 */     this._rowPosition += 1;
/*      */ 
/* 1372 */     if (this._enableBCP >= 2)
/*      */     {
/*      */       try
/*      */       {
/* 1376 */         this._sybBCP.bcpSendRow(this._object, this._types, this._scale, this._cal);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1380 */         this._rowPosition = 0;
/* 1381 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */       }
/* 1383 */       return;
/*      */     }
/*      */ 
/* 1387 */     this._bulkObject.addLast(this._object.clone());
/* 1388 */     this._bulkTypes.addLast(this._types.clone());
/* 1389 */     this._bulkScale.addLast(this._scale.clone());
/* 1390 */     this._bulkCal.addLast(this._cal.clone());
/*      */   }
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/* 1402 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1404 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1406 */       LOG.fine(this._logId + " addBatch()");
/*      */     }
/*      */ 
/* 1410 */     if (this._enableBCP > 0)
/*      */     {
/* 1414 */       if (this._lobSetterCalled)
/*      */       {
/* 1416 */         this._lobSetterCalled = false;
/* 1417 */         ErrorMessage.raiseError("JZ043");
/*      */ 
/* 1419 */         return;
/*      */       }
/*      */ 
/* 1422 */       addBulkBatch();
/* 1423 */       return;
/*      */     }
/* 1425 */     if (this._isDynamic)
/*      */     {
/* 1427 */       this._numRows += 1;
/*      */     }
/* 1431 */     else if (this._batchParams == null)
/*      */     {
/* 1433 */       this._batchParams = new ArrayList(1000);
/*      */     }
/*      */     int i;
/* 1437 */     if (this._sendAsRpc)
/*      */     {
/* 1440 */       this._sendAsRpc = false;
/* 1441 */       this._executionMode = 1;
/*      */ 
/* 1443 */       this._doneinproc = true;
/*      */ 
/* 1445 */       this._query = ("execute " + this._query);
/*      */ 
/* 1447 */       localObject1 = this._paramMgr.getParams();
/* 1448 */       for (i = 0; i < localObject1.length; ++i)
/*      */       {
/* 1450 */         localObject1[i]._paramMarkerOffset += "execute ".length();
/*      */       }
/*      */ 
/* 1453 */       super.addBatch(this._query);
/*      */     }
/*      */     else
/*      */     {
/* 1461 */       localObject1 = "execute";
/* 1462 */       if ((!this._doneinproc) && (this._query.toLowerCase().startsWith((String)localObject1)))
/*      */       {
/* 1466 */         this._doneinproc = true;
/*      */       }
/* 1468 */       super.addBatch(this._query);
/*      */     }
/*      */ 
/* 1471 */     Object localObject1 = null;
/* 1472 */     if ((this._sendBatchParamsImmediate) && (!this._batchInitialized))
/*      */     {
/* 1474 */       this._sendBatchParamsImmediate = (!checkBatch());
/* 1475 */       if (this._sendBatchParamsImmediate)
/*      */       {
/* 1477 */         checkStatement(true);
/* 1478 */         this._batchLock = this._protocol.initCommandExecSession(this._context);
/*      */       }
/*      */     }
/*      */ 
/* 1482 */     if (this._isDynamic)
/*      */     {
/* 1484 */       this._batchOffset += this._paramCount;
/* 1485 */       if (this._enableBCP < 2)
/*      */       {
/* 1487 */         if (this._batchOffset >= this._types.length)
/*      */         {
/* 1489 */           i = this._types.length;
/* 1490 */           int k = 2 * i;
/*      */ 
/* 1492 */           int[] arrayOfInt1 = new int[k];
/* 1493 */           System.arraycopy(this._types, 0, arrayOfInt1, 0, i);
/* 1494 */           this._types = arrayOfInt1;
/*      */ 
/* 1496 */           int[] arrayOfInt2 = new int[k];
/* 1497 */           System.arraycopy(this._scale, 0, arrayOfInt2, 0, i);
/* 1498 */           this._scale = arrayOfInt2;
/*      */ 
/* 1500 */           Calendar[] arrayOfCalendar = new Calendar[k];
/* 1501 */           System.arraycopy(this._cal, 0, arrayOfCalendar, 0, i);
/* 1502 */           this._cal = arrayOfCalendar;
/*      */ 
/* 1504 */           Object[] arrayOfObject = new Object[k];
/* 1505 */           System.arraycopy(this._object, 0, arrayOfObject, 0, i);
/* 1506 */           this._object = arrayOfObject;
/*      */         }
/*      */ 
/* 1509 */         i = this._batchOffset - this._paramCount;
/* 1510 */         System.arraycopy(this._types, i, this._types, this._batchOffset, this._paramCount);
/* 1511 */         System.arraycopy(this._object, i, this._object, this._batchOffset, this._paramCount);
/* 1512 */         System.arraycopy(this._scale, i, this._scale, this._batchOffset, this._paramCount);
/* 1513 */         System.arraycopy(this._cal, i, this._cal, this._batchOffset, this._paramCount);
/*      */       }
/*      */ 
/* 1540 */       if (!this._sendBatchParamsImmediate)
/*      */         return;
/*      */       try
/*      */       {
/* 1544 */         this._protocol.sendDynamicExecuteParams(this._context, this._dynStmtName, this._paramMgr, this._numRows, this._object, this._types, this._cal, this._scale, this._homoGeneousBatch, !this._batchInitialized, this._firstExecute);
/*      */ 
/* 1547 */         this._context._conn._batchSBPIInitialized = (this._batchInitialized = 1);
/* 1548 */         this._firstExecute = false;
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/* 1552 */         this._protocol.cancel(this._context, true);
/* 1553 */         this._protocol.abortCommandSession(this._context);
/* 1554 */         this._context._conn._batchSBPIInitialized = (this._batchInitialized = 0);
/*      */       }
/*      */       finally
/*      */       {
/* 1558 */         this._batchOffset = 0;
/* 1559 */         this._numRows = 0;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1565 */       for (int j = 0; j < this._paramCount; ++j)
/*      */       {
/* 1567 */         localObject1 = this._paramMgr._params[j].cloneMe();
/* 1568 */         this._batchParams.add(localObject1);
/*      */       }
/* 1570 */       if (!this._sendBatchParamsImmediate)
/*      */         return;
/* 1572 */       this._protocol.sendLanguageParams(this._context, this._query, this._paramMgr, this._batchParams, this._homoGeneousBatch, 1, !this._batchInitialized);
/*      */ 
/* 1574 */       this._context._conn._batchSBPIInitialized = (this._batchInitialized = 1);
/*      */ 
/* 1576 */       this._batchParams.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addBatch(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1612 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1614 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1616 */         LOG.finer(this._logId + " addBatch(String = [" + paramString + "])");
/*      */       }
/* 1618 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1620 */         LOG.fine(this._logId + " addBatch(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1625 */     ErrorMessage.raiseError("JZ0S3", "addBatch(String)");
/*      */   }
/*      */ 
/*      */   private void clearBulkBatch()
/*      */   {
/* 1632 */     this._bulkObject.clear();
/* 1633 */     this._bulkCal.clear();
/* 1634 */     this._bulkScale.clear();
/* 1635 */     this._bulkTypes.clear();
/*      */   }
/*      */ 
/*      */   public void clearBatch()
/*      */     throws SQLException
/*      */   {
/* 1643 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1645 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1647 */       LOG.fine(this._logId + " clearBatch()");
/*      */     }
/*      */ 
/* 1651 */     if (this._enableBCP >= 2)
/*      */     {
/* 1653 */       this._sybBCP.resetBCPInit();
/* 1654 */       this._protocol.cancel(this._context, true);
/*      */     }
/* 1656 */     else if (this._enableBCP == 1)
/*      */     {
/* 1658 */       clearBulkBatch();
/*      */     }
/*      */ 
/* 1661 */     if (this._batchInitialized)
/*      */     {
/* 1663 */       this._protocol.cancel(this._context, true);
/* 1664 */       this._protocol.abortCommandSession(this._context);
/* 1665 */       this._context._conn._batchSBPIInitialized = (this._batchInitialized = 0);
/*      */     }
/*      */ 
/* 1668 */     this._rowPosition = 0;
/* 1669 */     super.clearBatch();
/* 1670 */     this._batchParams = null;
/* 1671 */     if (!this._isDynamic)
/*      */       return;
/* 1673 */     this._batchOffset = 0;
/* 1674 */     this._numRows = 0;
/*      */   }
/*      */ 
/*      */   private int[] executeBulkBatch()
/*      */     throws SQLException
/*      */   {
/* 1681 */     int[] arrayOfInt = new int[1];
/* 1682 */     if ((this._enableBCP == 3) && (!this._context._conn.getAutoCommit()))
/*      */     {
/* 1684 */       ErrorMessage.raiseError("JZBK5");
/*      */     }
/*      */ 
/* 1687 */     checkDead();
/* 1688 */     if ((this._enableBCP > 0) && (this._rowPosition > 0))
/*      */     {
/*      */       try
/*      */       {
/* 1696 */         if (this._enableBCP >= 2)
/*      */         {
/* 1698 */           arrayOfInt[0] = this._sybBCP.bcpDone();
/*      */         }
/*      */         else
/*      */         {
/* 1702 */           arrayOfInt[0] = this._sybBCP.bcpSendRow(this._bulkObject, this._bulkTypes, this._bulkScale, this._bulkCal);
/* 1703 */           this._sybBCP.bcpDone();
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1708 */         ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */       }
/*      */       finally
/*      */       {
/* 1712 */         this._sybBCP.resetBCPInit();
/* 1713 */         this._rowPosition = 0;
/*      */       }
/* 1715 */       return arrayOfInt;
/*      */     }
/* 1717 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   public int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/* 1730 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1732 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1734 */       LOG.fine(this._logId + " executeBatch()");
/*      */     }
/*      */ 
/* 1738 */     if (this._enableBCP > 0)
/*      */     {
/* 1740 */       return executeBulkBatch();
/*      */     }
/*      */ 
/* 1749 */     if (this._batchCmds == null)
/*      */     {
/* 1755 */       this._context._conn._batchSBPIInitialized = (this._batchInitialized = 0);
/* 1756 */       this._batchLock = false;
/* 1757 */       return new int[0];
/* 1760 */     }
/*      */ if (this._batchInitialized);
/*      */     int[] arrayOfInt1;
/*      */     Object localObject3;
/*      */     int k;
/*      */     ParamManager localParamManager2;
/*      */     try { this._protocol.finishCommandExecSession(this._context, this._batchLock);
/* 1765 */       return batchLoop(this._paramMgr); }
/*      */     finally
/*      */     {
/* 1769 */       this._context._batch = false;
/* 1770 */       this._context._conn._batchSBPIInitialized = (this._batchInitialized = 0);
/* 1771 */       this._batchLock = false;
/* 1772 */       this._batchParams = null;
/* 1773 */       this._batchCmds = null; ret;
/*      */ 
/* 1777 */       arrayOfInt1 = new int[this._batchCmds.size()];
/*      */ 
/* 1780 */       if (checkBatch())
/*      */       {
/* 1786 */         int i = 0;
/* 1787 */         for (j = 0; j < this._batchCmds.size(); ++j)
/*      */         {
/* 1791 */           ParamManager localParamManager1 = countParams((String)this._batchCmds.get(j));
/*      */ 
/* 1793 */           int l = localParamManager1._params.length;
/*      */ 
/* 1796 */           for (int i2 = 0; i2 < l; ++i2)
/*      */           {
/* 1798 */             localObject3 = (Param)this._batchParams.get(i);
/* 1799 */             localParamManager1.setParam(i2 + 1, ((Param)localObject3)._sqlType, ((Param)localObject3)._inValue, ((Param)localObject3)._scale);
/*      */ 
/* 1804 */             ++i;
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/* 1810 */             int[] arrayOfInt5 = super.executeBatch(localParamManager1);
/* 1811 */             arrayOfInt1[j] = arrayOfInt5[0];
/*      */           }
/*      */           catch (SQLException localSQLException2)
/*      */           {
/* 1815 */             handleBatchException(localSQLException2, arrayOfInt1, j);
/*      */           }
/*      */         }
/* 1818 */         clearBatch();
/*      */ 
/* 1820 */         return arrayOfInt1;
/*      */       }
/*      */ 
/* 1823 */       if (this._isDynamic)
/*      */       {
/* 1827 */         int[] arrayOfInt2 = super.executeBatch(this._paramMgr);
/* 1828 */         this._firstExecute = true;
/* 1829 */         return arrayOfInt2;
/*      */       }
/*      */ 
/* 1838 */       boolean bool1 = this._context._conn._props.getBoolean(15);
/*      */ 
/* 1849 */       int j = 0;
/*      */ 
/* 1851 */       if (!bool1)
/*      */       {
/* 1858 */         if (this._dbmda == null)
/*      */         {
/* 1860 */           j = 255;
/*      */         }
/*      */         else
/*      */         {
/* 1864 */           j = this._dbmda.getMaxParamsPerBatch();
/*      */         }
/*      */       }
/*      */ 
/* 1868 */       if ((!bool1) && (!this._homoGeneousBatch) && (this._batchCmdsCount * this._paramCount > j) && (this._paramCount <= j))
/*      */       {
/* 1880 */         k = j / this._paramCount;
/* 1881 */         int[] arrayOfInt3 = null;
/* 1882 */         String str = (String)this._batchCmds.firstElement();
/*      */ 
/* 1884 */         localObject3 = new Vector();
/* 1885 */         for (int i4 = 0; i4 < k; ++i4)
/*      */         {
/* 1887 */           ((Vector)localObject3).add(str);
/*      */         }
/*      */ 
/* 1890 */         this._batchCmds = ((Vector)localObject3);
/*      */ 
/* 1892 */         int i5 = this._batchCmdsCount;
/* 1893 */         int i7 = 0;
/* 1894 */         int i8 = 0;
/* 1895 */         int i9 = k;
/* 1896 */         boolean bool2 = false;
/*      */ 
/* 1898 */         while (i5 > 0)
/*      */         {
/* 1900 */           if (i5 <= k)
/*      */           {
/* 1904 */             bool2 = true;
/*      */           }
/*      */ 
/* 1907 */           if (i5 < k)
/*      */           {
/* 1912 */             ((Vector)localObject3).clear();
/* 1913 */             for (int i10 = 0; i10 < i5; ++i10)
/*      */             {
/* 1915 */               ((Vector)localObject3).add(str);
/*      */             }
/* 1917 */             this._batchCmds = ((Vector)localObject3);
/* 1918 */             i9 = i5;
/*      */           }
/*      */ 
/* 1921 */           this._batchCmdsCount = i9;
/* 1922 */           ParamManager localParamManager3 = countParams(batchToString());
/*      */ 
/* 1924 */           for (int i11 = 0; i11 < this._paramCount * i9; ++i11)
/*      */           {
/* 1926 */             Param localParam = (Param)this._batchParams.get(i7);
/* 1927 */             localParamManager3.setParam(i11 + 1, localParam._sqlType, localParam._inValue, localParam._scale);
/* 1928 */             ++i7;
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/* 1935 */             arrayOfInt3 = super.executeBatch(localParamManager3, bool2);
/*      */           }
/*      */           catch (SQLException localSQLException3)
/*      */           {
/* 1940 */             handleBatchException(localSQLException3, arrayOfInt1, i8);
/*      */           }
/*      */ 
/* 1945 */           for (int i12 = 0; i12 < i9; ++i12)
/*      */           {
/* 1947 */             arrayOfInt1[i8] = arrayOfInt3[i12];
/*      */ 
/* 1949 */             ++i8;
/*      */           }
/*      */           int i6;
/* 1951 */           i5 -= i9;
/*      */         }
/* 1953 */         return arrayOfInt1;
/*      */       }
/*      */ 
/* 1958 */       if ((this._homoGeneousBatch) && (!bool1))
/*      */       {
/* 1960 */         k = 1;
/* 1961 */         doEscapeProcessing(this._query);
/*      */         try
/*      */         {
/* 1965 */           getParameterMetaData();
/*      */         }
/*      */         catch (SQLException localSQLException1)
/*      */         {
/* 1974 */           chainWarning(ErrorMessage.createWarning("01S12"));
/*      */ 
/* 1976 */           k = 0;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2019 */     return (I)super.executeBatch(localParamManager2);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 2027 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2029 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2031 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setCharacterStream", new Object[] { new Integer(paramInt1), paramReader, new Integer(paramInt2) }));
/*      */       }
/* 2035 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2037 */         LOG.fine(this._logId + " setCharacterStream(int, java.io.Reader, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2043 */     checkDead();
/* 2044 */     checkIfMixedWithLob(paramInt1, -1);
/* 2045 */     String str = this._paramMgr.drainReader(paramReader, paramInt2);
/* 2046 */     if (str != null)
/*      */     {
/* 2048 */       setParam(12, paramInt1, str, 0);
/*      */     }
/*      */     else
/*      */     {
/* 2052 */       setParam(-1, paramInt1, paramReader, paramInt2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(int paramInt, Blob paramBlob)
/*      */     throws SQLException
/*      */   {
/* 2061 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2063 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2065 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramBlob }));
/*      */       }
/* 2068 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2070 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramBlob }));
/*      */       }
/* 2073 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2075 */         LOG.fine(this._logId + " setBlob(int, Blob)");
/*      */       }
/*      */     }
/*      */ 
/* 2079 */     checkDead();
/* 2080 */     this._lobSetterCalled = true;
/*      */ 
/* 2087 */     if ((this._homoGeneousBatch == true) && (paramBlob instanceof SybBinaryLob) && (this._batchFirstRowParamType != null) && (this._batchFirstRowParamType.length > 0))
/*      */     {
/* 2091 */       int i = this._batchFirstRowParamType[(paramInt - 1)];
/* 2092 */       if (i == -999)
/*      */       {
/* 2094 */         this._batchFirstRowParamType[(paramInt - 1)] = 2004;
/*      */       }
/* 2096 */       else if (i != 2004)
/*      */       {
/* 2098 */         ErrorMessage.raiseError("JZ042", String.valueOf(2004), String.valueOf(i));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2104 */     if (paramBlob == null)
/*      */     {
/* 2106 */       paramBlob = this._context._conn._nullBlob;
/*      */     }
/* 2108 */     setParam(2004, paramInt, paramBlob);
/*      */   }
/*      */ 
/*      */   public void setClob(int paramInt, Clob paramClob)
/*      */     throws SQLException
/*      */   {
/* 2116 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2118 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2120 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setClob", new Object[] { new Integer(paramInt), paramClob }));
/*      */       }
/* 2123 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2125 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setClob", new Object[] { new Integer(paramInt), paramClob }));
/*      */       }
/* 2128 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2130 */         LOG.fine(this._logId + " setClob(int, Clob)");
/*      */       }
/*      */     }
/*      */ 
/* 2134 */     checkDead();
/* 2135 */     this._lobSetterCalled = true;
/*      */ 
/* 2142 */     if ((this._homoGeneousBatch == true) && (paramClob instanceof SybCharLob) && (this._batchFirstRowParamType != null) && (this._batchFirstRowParamType.length > 0))
/*      */     {
/* 2146 */       int i = this._batchFirstRowParamType[(paramInt - 1)];
/* 2147 */       if (i == -999)
/*      */       {
/* 2149 */         this._batchFirstRowParamType[(paramInt - 1)] = 2005;
/*      */       }
/* 2151 */       else if (i != 2005)
/*      */       {
/* 2153 */         ErrorMessage.raiseError("JZ042", String.valueOf(2005), String.valueOf(i));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2159 */     if (paramClob == null)
/*      */     {
/* 2161 */       paramClob = this._context._conn._nullClob;
/*      */     }
/* 2163 */     setParam(2005, paramInt, paramClob);
/*      */   }
/*      */ 
/*      */   public void setRef(int paramInt, Ref paramRef)
/*      */     throws SQLException
/*      */   {
/* 2180 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2182 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2184 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setRef", new Object[] { new Integer(paramInt), paramRef }));
/*      */       }
/* 2187 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2189 */         LOG.fine(this._logId + " setRef(int, Ref)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2194 */     Debug.notSupported(this, "setRef(int, Ref)");
/*      */   }
/*      */ 
/*      */   public void setArray(int paramInt, Array paramArray)
/*      */     throws SQLException
/*      */   {
/* 2200 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2202 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2204 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setArray", new Object[] { new Integer(paramInt), paramArray }));
/*      */       }
/* 2207 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2209 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setArray", new Object[] { new Integer(paramInt), paramArray }));
/*      */       }
/* 2212 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2214 */         LOG.fine(this._logId + " setArray(int, Array)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2219 */     Debug.notSupported(this, "setArray(int, Array)");
/*      */   }
/*      */ 
/*      */   public ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 2225 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2227 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2229 */       LOG.fine(this._logId + " getMetaData()");
/*      */     }
/*      */ 
/* 2233 */     checkDead();
/*      */ 
/* 2240 */     ResultSetMetaData localResultSetMetaData = null;
/* 2241 */     if (this._dynStmtName != null)
/*      */     {
/* 2243 */       localResultSetMetaData = this._protocol.dynamicMetaData(this._context);
/*      */     }
/* 2245 */     if ((localResultSetMetaData == null) && 
/* 2247 */       (this._currentRS != null))
/*      */     {
/* 2249 */       localResultSetMetaData = this._currentRS.getMetaData();
/*      */     }
/*      */ 
/* 2255 */     if ((localResultSetMetaData == null) && (this._context._conn._props.getBoolean(64)))
/*      */     {
/* 2257 */       ErrorMessage.raiseError("JZ0MD");
/*      */     }
/*      */ 
/* 2261 */     return localResultSetMetaData;
/*      */   }
/*      */ 
/*      */   public void setDate(int paramInt, Date paramDate, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 2270 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2272 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2274 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setDate", new Object[] { new Integer(paramInt), paramDate, paramCalendar }));
/*      */       }
/* 2277 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2279 */         LOG.fine(this._logId + " setDate(int, java.sql.Date, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 2283 */     checkDead();
/* 2284 */     checkIfMixedWithLob(paramInt, 91);
/* 2285 */     if (this._isDynamic)
/*      */     {
/* 2287 */       setParam(91, paramInt, paramDate);
/* 2288 */       addCalendar(paramInt, paramCalendar);
/*      */     }
/* 2290 */     else if ((paramDate == null) || (this._enableBCP > 0))
/*      */     {
/* 2292 */       setParam(91, paramInt, paramDate);
/*      */     }
/*      */     else
/*      */     {
/* 2296 */       setParam(91, paramInt, new DateObject(paramDate, paramCalendar, 91));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(int paramInt, Time paramTime, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 2307 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2309 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2311 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setTime", new Object[] { new Integer(paramInt), paramTime, paramCalendar }));
/*      */       }
/* 2314 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2316 */         LOG.fine(this._logId + " setTime(int, java.sql.Time, Calendar)");
/*      */       }
/*      */     }
/*      */ 
/* 2320 */     checkDead();
/* 2321 */     checkIfMixedWithLob(paramInt, 92);
/* 2322 */     if (this._isDynamic)
/*      */     {
/* 2324 */       setParam(92, paramInt, paramTime);
/* 2325 */       addCalendar(paramInt, paramCalendar);
/*      */     }
/* 2327 */     else if ((paramTime == null) || (this._enableBCP > 0))
/*      */     {
/* 2329 */       setParam(92, paramInt, paramTime);
/*      */     }
/*      */     else
/*      */     {
/* 2333 */       setParam(92, paramInt, new DateObject(paramTime, paramCalendar, 92));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar)
/*      */     throws SQLException
/*      */   {
/* 2344 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2346 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2348 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setTimestamp", new Object[] { new Integer(paramInt), paramTimestamp, paramCalendar }));
/*      */       }
/* 2351 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2353 */         LOG.fine(this._logId + " setTimestamp(int, java.sql.Timestamp, Calendar)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2358 */     checkDead();
/* 2359 */     checkIfMixedWithLob(paramInt, 93);
/* 2360 */     if (this._isDynamic)
/*      */     {
/* 2362 */       setParam(93, paramInt, paramTimestamp);
/* 2363 */       addCalendar(paramInt, paramCalendar);
/*      */     }
/* 2365 */     else if ((paramTimestamp == null) || (this._enableBCP > 0))
/*      */     {
/* 2367 */       setParam(93, paramInt, paramTimestamp);
/*      */     }
/*      */     else
/*      */     {
/* 2371 */       setParam(93, paramInt, new DateObject(paramTimestamp, paramCalendar, 93));
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2378 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2380 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2382 */         LOG.finer(this._logId + " execute(String = [" + paramString + "])");
/*      */       }
/* 2384 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2386 */         LOG.fine(this._logId + " execute(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2391 */     ErrorMessage.raiseError("JZ0S3", "execute(String)");
/*      */ 
/* 2393 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2399 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2401 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2403 */         LOG.finer(this._logId + " execute(String = [" + paramString + "], int = [" + paramInt + "])");
/*      */       }
/* 2406 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2408 */         LOG.fine(this._logId + " execute(String, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2413 */     ErrorMessage.raiseError("JZ0S3", "execute(String, int)");
/*      */ 
/* 2415 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 2421 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2423 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2425 */         LOG.finest(LogUtil.logMethod(false, this._logId, " execute", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/* 2428 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2430 */         LOG.finer(LogUtil.logMethod(true, this._logId, " execute", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/* 2433 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2435 */         LOG.fine(this._logId + " execute(String, int[])");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2440 */     ErrorMessage.raiseError("JZ0S3", "execute(String, int[])");
/*      */ 
/* 2442 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString, String[] paramArrayOfString)
/*      */     throws SQLException
/*      */   {
/* 2448 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2450 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2452 */         LOG.finest(LogUtil.logMethod(false, this._logId, " execute", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/* 2455 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2457 */         LOG.finer(LogUtil.logMethod(true, this._logId, " execute", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/* 2460 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2462 */         LOG.fine(this._logId + " execute(String, String[])");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2467 */     ErrorMessage.raiseError("JZ0S3", "execute(String, String[])");
/*      */ 
/* 2469 */     return false;
/*      */   }
/*      */ 
/*      */   private ParamManager countParams(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2478 */     int i = 0;
/* 2479 */     int j = 0;
/* 2480 */     Object localObject = new int[1000];
/* 2481 */     int k = 0;
/* 2482 */     StringBuffer localStringBuffer1 = null;
/* 2483 */     StringBuffer localStringBuffer2 = null;
/* 2484 */     StringBuffer localStringBuffer3 = null;
/* 2485 */     if ((this instanceof SybCallableStatement) && (this._hasReturn))
/*      */     {
/* 2488 */       localStringBuffer1 = new StringBuffer("0,");
/* 2489 */       ++i;
/*      */     }
/*      */ 
/* 2492 */     int[] arrayOfInt1 = paramString.length();
/* 2493 */     for (int[] arrayOfInt2 = 0; arrayOfInt2 < arrayOfInt1; ++arrayOfInt2)
/*      */     {
/*      */       int[] arrayOfInt3;
/* 2495 */       switch (paramString.charAt(arrayOfInt2))
/*      */       {
/*      */       case '?':
/* 2499 */         if (j == 0) {
/* 2500 */           localObject[(i++)] = arrayOfInt2;
/*      */ 
/* 2502 */           if (this instanceof SybCallableStatement)
/*      */           {
/* 2505 */             if (k == 0)
/*      */             {
/* 2507 */               if (localStringBuffer1 == null)
/*      */               {
/* 2509 */                 localStringBuffer1 = new StringBuffer();
/*      */               }
/*      */ 
/* 2512 */               if (this._hasReturn)
/*      */               {
/* 2514 */                 localStringBuffer1.append(i - 1).append(",");
/*      */               }
/*      */               else
/*      */               {
/* 2518 */                 localStringBuffer1.append(i).append(",");
/*      */               }
/*      */ 
/*      */             }
/* 2525 */             else if (localStringBuffer3 != null)
/*      */             {
/* 2527 */               if (localStringBuffer2 == null)
/*      */               {
/* 2529 */                 localStringBuffer2 = new StringBuffer();
/*      */               }
/*      */ 
/* 2532 */               localStringBuffer2.append("'");
/* 2533 */               localStringBuffer2.append(localStringBuffer3.toString().trim());
/* 2534 */               localStringBuffer2.append("',");
/* 2535 */               localStringBuffer3 = null;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2541 */           int l = localObject.length;
/* 2542 */           if (i < l)
/*      */             continue;
/* 2544 */           l += 1000;
/*      */ 
/* 2547 */           arrayOfInt3 = new int[l];
/* 2548 */           System.arraycopy(localObject, 0, arrayOfInt3, 0, localObject.length);
/*      */ 
/* 2551 */           localObject = arrayOfInt3; } break;
/*      */       case '\'':
/* 2555 */         switch (j)
/*      */         {
/*      */         case 39:
/* 2559 */           j = 0;
/*      */ 
/* 2566 */           break;
/*      */         case 34:
/* 2569 */           break;
/*      */         default:
/* 2572 */           j = 39;
/* 2573 */         }break;
/*      */       case '"':
/* 2577 */         switch (j)
/*      */         {
/*      */         case 34:
/* 2581 */           j = 0;
/* 2582 */           break;
/*      */         case 39:
/* 2585 */           break;
/*      */         default:
/* 2588 */           j = 34;
/* 2589 */         }break;
/*      */       case '@':
/* 2593 */         if (!this instanceof SybCallableStatement)
/*      */           continue;
/* 2595 */         k = 1;
/* 2596 */         localStringBuffer3 = new StringBuffer();
/*      */ 
/* 2598 */         for (arrayOfInt3 = arrayOfInt2; arrayOfInt3 < arrayOfInt1; ++arrayOfInt3)
/*      */         {
/* 2600 */           char c = paramString.charAt(arrayOfInt3);
/* 2601 */           if (c != '=')
/*      */           {
/* 2603 */             localStringBuffer3.append(c);
/*      */           }
/*      */           else
/*      */           {
/* 2607 */             arrayOfInt2 = arrayOfInt3;
/* 2608 */             break;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2616 */     if (localStringBuffer1 != null)
/*      */     {
/* 2618 */       this._paramColids = localStringBuffer1.substring(0, localStringBuffer1.length() - 1);
/*      */     }
/* 2620 */     if (localStringBuffer2 != null)
/*      */     {
/* 2622 */       this._paramNames = localStringBuffer2.substring(0, localStringBuffer2.length() - 1);
/*      */     }
/*      */ 
/* 2625 */     if (this._batchCmds == null)
/*      */     {
/* 2627 */       this._query = paramString;
/*      */     }
/*      */ 
/* 2630 */     return (ParamManager)new ParamManager(i, localObject, this);
/*      */   }
/*      */ 
/*      */   private void setParam(int paramInt1, int paramInt2, Object paramObject)
/*      */     throws SQLException
/*      */   {
/* 2638 */     setParam(paramInt1, paramInt2, paramObject, 0);
/*      */   }
/*      */ 
/*      */   private void setParam(int paramInt1, int paramInt2, Object paramObject, int paramInt3)
/*      */     throws SQLException
/*      */   {
/*      */     int i;
/* 2645 */     if (this._enableBCP > 0)
/*      */     {
/* 2647 */       i = this._sybBCP.getId(paramInt2);
/* 2648 */       if ((this._sybBCP._columnDefination.size() == 0) && 
/* 2650 */         (this._sybBCP.hasDroppedColumns()))
/*      */       {
/* 2652 */         int k = this._sybBCP.getDroppedColumnCountBeforeColumnIndex(i);
/* 2653 */         i += k;
/*      */       }
/*      */ 
/* 2656 */       this._object[(i - 1)] = paramObject;
/* 2657 */       this._types[(i - 1)] = paramInt1;
/* 2658 */       this._scale[(i - 1)] = paramInt3;
/* 2659 */       if (this._sybBCP._columnDefination.size() > 0)
/* 2660 */         this._paramMgr.setParam(paramInt2, i - 1, paramInt1, paramObject, paramInt3);
/*      */       else {
/* 2662 */         this._paramMgr.setParam(paramInt2, paramInt1, paramObject, paramInt3);
/*      */       }
/*      */ 
/*      */     }
/* 2666 */     else if (this._isDynamic)
/*      */     {
/* 2668 */       paramInt2 += this._batchOffset;
/*      */ 
/* 2673 */       if ((((paramInt1 == 2) || (paramInt1 == 3))) && (paramObject instanceof BigDecimal) && ((
/* 2678 */         (this._protocol.isSuppressParamFormatSupportedAndSet()) || (this._protocol.isDynamicHomogenousBatchSupportedAndSet()))))
/*      */       {
/* 2681 */         i = getScale(paramInt2 - this._batchOffset);
/* 2682 */         if (paramInt3 != i)
/*      */         {
/* 2684 */           paramInt3 = i;
/* 2685 */           paramObject = setScale((BigDecimal)paramObject, paramInt3);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2690 */       addObject(paramInt2, paramObject);
/* 2691 */       addType(paramInt2, paramInt1);
/* 2692 */       addScale(paramInt2, paramInt3);
/*      */     }
/*      */     else
/*      */     {
/* 2706 */       if ((((paramInt1 == 2) || (paramInt1 == 3))) && (paramObject instanceof BigDecimal) && ((
/* 2711 */         (this._protocol.isSuppressParamFormatSupportedAndSet()) || (this._protocol.isLanguageHomogenousBatchSupportedAndSet()))))
/*      */       {
/* 2719 */         if (this._batchParams == null)
/*      */         {
/*      */           try
/*      */           {
/* 2723 */             getParameterMetaData();
/* 2724 */             paramInt3 = getScale(paramInt2);
/* 2725 */             paramObject = setScale((BigDecimal)paramObject, paramInt3);
/*      */           }
/*      */           catch (SQLException localSQLException)
/*      */           {
/* 2734 */             chainWarning(ErrorMessage.createWarning("01S12"));
/*      */ 
/* 2736 */             this._homoGeneousBatch = false;
/*      */           }
/*      */         }
/* 2739 */         else if ((this._paramMgr._paramMdList != null) && (this._paramMgr._paramMdList.size() == this._paramCount))
/*      */         {
/* 2742 */           int j = getScale(paramInt2);
/*      */ 
/* 2744 */           if (paramInt3 != j)
/*      */           {
/* 2746 */             paramInt3 = j;
/* 2747 */             paramObject = setScale((BigDecimal)paramObject, paramInt3);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2753 */       this._paramMgr.setParam(paramInt2, paramInt1, paramObject, paramInt3);
/*      */     }
/*      */   }
/*      */ 
/*      */   private BigDecimal setScale(BigDecimal paramBigDecimal, int paramInt)
/*      */   {
/* 2760 */     if (paramBigDecimal.signum() > 0)
/*      */     {
/* 2762 */       paramBigDecimal = paramBigDecimal.setScale(paramInt, 1);
/*      */     }
/*      */     else
/*      */     {
/* 2766 */       paramBigDecimal = paramBigDecimal.setScale(paramInt, 0);
/*      */     }
/* 2768 */     return paramBigDecimal;
/*      */   }
/*      */ 
/*      */   public boolean sendQuery(String paramString, ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 2781 */     checkDead();
/*      */ 
/* 2790 */     if (this._isDynamic)
/*      */     {
/*      */       try
/*      */       {
/* 2794 */         if (this._batchInitialized)
/*      */         {
/* 2796 */           this._protocol.finishCommandExecSession(this._context, this._batchLock);
/*      */         }
/*      */         else
/*      */         {
/* 2800 */           int i = 1;
/*      */ 
/* 2807 */           if (this._numRows == 0)
/*      */           {
/* 2809 */             this._numRows = 1;
/* 2810 */             i = 0;
/*      */           }
/* 2812 */           this._protocol.dynamicExecute(this._context, this._dynStmtName, paramParamManager, this._numRows, this._object, this._types, this._cal, this._scale, (this._homoGeneousBatch) && (i != 0), this._firstExecute);
/*      */ 
/* 2815 */           this._firstExecute = false;
/*      */         }
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 2820 */         handleSQLE(localSQLException);
/*      */       }
/*      */       finally
/*      */       {
/* 2824 */         System.arraycopy(this._object, this._batchOffset, this._object, 0, this._paramCount);
/* 2825 */         System.arraycopy(this._types, this._batchOffset, this._types, 0, this._paramCount);
/* 2826 */         System.arraycopy(this._scale, this._batchOffset, this._scale, 0, this._paramCount);
/* 2827 */         System.arraycopy(this._cal, this._batchOffset, this._cal, 0, this._paramCount);
/*      */ 
/* 2829 */         this._batchOffset = 0;
/* 2830 */         this._numRows = 0;
/* 2831 */         this._batchLock = false;
/* 2832 */         this._batchInitialized = false;
/* 2833 */         this._batchCmds = null;
/*      */       }
/* 2835 */       this._state = 3;
/* 2836 */       return false;
/*      */     }
/*      */ 
/* 2840 */     if (this._batchCmds != null)
/*      */     {
/* 2842 */       return super.sendQuery(paramString, paramParamManager);
/*      */     }
/* 2844 */     return super.sendQuery(this._query, this._paramMgr);
/*      */   }
/*      */ 
/*      */   public void switchContext(ProtocolContext paramProtocolContext)
/*      */   {
/* 2851 */     this._context = paramProtocolContext;
/*      */   }
/*      */ 
/*      */   protected void deallocateDynamic()
/*      */     throws SQLException
/*      */   {
/* 2857 */     if ((this._enableBCP != 0) || 
/* 2859 */       (this._dynStmtName == null))
/*      */       return;
/* 2861 */     this._protocol.dynamicDeallocate(this._context, this._dynStmtName);
/*      */   }
/*      */ 
/*      */   protected Vector createDynamicExecuteBatchParams()
/*      */     throws SQLException
/*      */   {
/* 2872 */     Vector localVector = new Vector();
/*      */ 
/* 2879 */     int i = 0;
/*      */ 
/* 2882 */     for (int j = 0; j < this._batchCmds.size(); ++j)
/*      */     {
/* 2884 */       ParamManager localParamManager = countParams((String)this._batchCmds.get(0));
/*      */ 
/* 2886 */       int k = localParamManager._params.length;
/*      */ 
/* 2890 */       for (int l = 0; l < k; ++l)
/*      */       {
/* 2892 */         Param localParam = (Param)this._batchParams.get(i);
/* 2893 */         localParamManager.setParam(l + 1, localParam._sqlType, localParam._inValue, localParam._scale);
/*      */ 
/* 2897 */         ++i;
/*      */       }
/*      */ 
/* 2900 */       localVector.add(localParamManager);
/*      */     }
/* 2902 */     return localVector;
/*      */   }
/*      */ 
/*      */   protected boolean checkBatch() throws SQLException
/*      */   {
/* 2907 */     boolean bool1 = super.checkBatch();
/* 2908 */     boolean bool2 = false;
/*      */ 
/* 2914 */     if (this._dbmda != null)
/*      */     {
/* 2916 */       bool2 = this._dbmda.execParameterizedBatchUpdatesInLoop();
/*      */ 
/* 2920 */       if ((bool2) && (this._context._conn._props.getBoolean(15)))
/*      */       {
/* 2923 */         bool2 = false;
/*      */       }
/* 2925 */       bool1 |= bool2;
/*      */     }
/* 2927 */     return bool1;
/*      */   }
/*      */ 
/*      */   protected void extractParams() throws SQLException
/*      */   {
/* 2932 */     String str1 = "?,()={}";
/* 2933 */     int i = 0;
/* 2934 */     Vector localVector = new Vector();
/* 2935 */     int j = 0;
/*      */ 
/* 2937 */     int l = 0;
/*      */ 
/* 2941 */     for (int k = 0; k < this._sqlStr.length(); ++k)
/*      */     {
/* 2943 */       char c = this._sqlStr.charAt(k);
/*      */ 
/* 2945 */       if (Character.isWhitespace(c))
/*      */       {
/* 2947 */         if (j == 0)
/*      */           continue;
/* 2949 */         localVector.addElement(this._sqlStr.substring(i, k));
/* 2950 */         j = 0;
/*      */       }
/* 2953 */       else if ((str1.indexOf(c) != -1) && (l == 0))
/*      */       {
/* 2956 */         if (j != 0)
/*      */         {
/* 2958 */           localVector.addElement(this._sqlStr.substring(i, k));
/* 2959 */           j = 0;
/*      */         }
/* 2961 */         localVector.addElement(String.valueOf(c));
/*      */       }
/*      */       else
/*      */       {
/* 2965 */         if (j == 0)
/*      */         {
/* 2967 */           i = k;
/* 2968 */           j = 1;
/*      */         }
/* 2970 */         if (c == '\'')
/*      */         {
/* 2972 */           switch (l)
/*      */           {
/*      */           case 16:
/* 2975 */             l &= -17;
/* 2976 */             break;
/*      */           case 32:
/* 2978 */             break;
/*      */           default:
/* 2980 */             l |= 16;
/*      */           }
/*      */         }
/*      */ 
/* 2984 */         if (c != '"')
/*      */           continue;
/* 2986 */         switch (l)
/*      */         {
/*      */         case 32:
/* 2989 */           l &= -33;
/* 2990 */           break;
/*      */         case 16:
/* 2992 */           break;
/*      */         default:
/* 2994 */           l |= 32;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3001 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */ 
/* 3003 */     l = 0;
/* 3004 */     int i1 = 0;
/* 3005 */     int i2 = 0;
/* 3006 */     int i3 = 1;
/*      */ 
/* 3008 */     this._paramNameList = new Vector();
/* 3009 */     this._rowFmtIdxList = new Vector();
/*      */ 
/* 3011 */     for (k = 0; k < localVector.size(); ++k)
/*      */     {
/* 3013 */       String str2 = (String)localVector.get(k);
/* 3014 */       if ((((str2.equals("(")) || (str2.equals(")")))) && ((l & 0x8) == 0))
/*      */       {
/* 3019 */         if (str2.equals("("))
/*      */         {
/* 3021 */           ++i1;
/*      */         }
/*      */         else
/*      */         {
/* 3025 */           --i1;
/*      */         }
/* 3027 */         if (i1 == 1)
/*      */         {
/* 3029 */           l |= 1;
/*      */         }
/*      */         else
/*      */         {
/* 3033 */           l &= -2;
/*      */         }
/*      */       }
/* 3036 */       else if ((str2.equals("{")) || (str2.equals("}")))
/*      */       {
/* 3038 */         if (str2.equals("{"))
/*      */         {
/* 3040 */           ++i2;
/*      */         }
/*      */         else
/*      */         {
/* 3044 */           ++i2;
/*      */         }
/* 3046 */         if (i2 == 1)
/*      */         {
/* 3048 */           l |= 64;
/*      */         }
/*      */         else
/*      */         {
/* 3052 */           l &= -65;
/*      */         }
/*      */       }
/* 3055 */       else if ((str2.equalsIgnoreCase("values")) && (l == 0))
/*      */       {
/* 3058 */         l |= 2;
/*      */       }
/* 3060 */       else if ((str2.equalsIgnoreCase("set")) && (l == 0))
/*      */       {
/* 3063 */         l |= 4;
/*      */       }
/* 3065 */       else if ((str2.equalsIgnoreCase("where")) && (l == 4))
/*      */       {
/* 3070 */         l = 0;
/* 3071 */         l |= 8;
/* 3072 */         localStringBuffer.append(str2);
/* 3073 */         localStringBuffer.append(' ');
/*      */       }
/* 3075 */       else if (str2.equalsIgnoreCase("call"))
/*      */       {
/* 3077 */         l |= 128;
/*      */       }
/* 3080 */       else if ((l == 1) && (this._type == 16))
/*      */       {
/* 3083 */         if (str2.equals(","))
/*      */           continue;
/* 3085 */         this._paramNameList.addElement(str2);
/*      */       }
/* 3088 */       else if (((l & 0x3) == 3) || ((l & 0xC0) == 192))
/*      */       {
/* 3093 */         if (str2.equals("?"))
/*      */         {
/* 3095 */           this._rowFmtIdxList.addElement(new Integer(i3));
/*      */         } else {
/* 3097 */           if (!str2.equals(","))
/*      */             continue;
/* 3099 */           ++i3;
/*      */         }
/*      */       }
/* 3102 */       else if (l == 4)
/*      */       {
/* 3104 */         if ((!str2.equals("=")) || (!((String)localVector.get(k + 1)).equals("?"))) {
/*      */           continue;
/*      */         }
/* 3107 */         this._paramNameList.addElement((String)localVector.get(k - 1));
/* 3108 */         this._rowFmtIdxList.addElement(new Integer(i3++));
/*      */       }
/*      */       else {
/* 3111 */         if (l != 8)
/*      */           continue;
/* 3113 */         localStringBuffer.append(str2);
/* 3114 */         localStringBuffer.append(' ');
/*      */       }
/*      */     }
/*      */ 
/* 3118 */     if (localStringBuffer.length() <= 0)
/*      */       return;
/* 3120 */     this._whereBlock = localStringBuffer.toString();
/* 3121 */     this._whereBlock.trim();
/*      */   }
/*      */ 
/*      */   public ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/* 3130 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3132 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3134 */       LOG.fine(this._logId + " getParameterMetaData()");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 3140 */       if (this._dbmda == null)
/*      */       {
/* 3142 */         this._dbmda = ((SybDatabaseMetaData)this._context._conn.getMetaData());
/*      */       }
/* 3144 */       if (this._dbmda.getDatabaseProductName().indexOf("Anywhere") != -1)
/*      */       {
/* 3147 */         return null;
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException1)
/*      */     {
/* 3153 */       return null;
/*      */     }
/*      */ 
/* 3159 */     if (this._dynStmtName == null)
/*      */     {
/*      */       Object localObject;
/*      */       try
/*      */       {
/* 3164 */         int i = this._context._conn.getDynStmtNum();
/* 3165 */         localObject = "dyn" + i;
/*      */ 
/* 3167 */         this._context._conn.setDynStmtNum(++i);
/*      */ 
/* 3172 */         this._protocol.dynamicPrepare(this._context, (String)localObject, this._sqlStr, this._paramMgr);
/* 3173 */         this._dynStmtName = ((String)localObject);
/*      */       }
/*      */       catch (SQLException localSQLException2)
/*      */       {
/* 3179 */         if ((this._type != 16) && (this._type != 8))
/*      */         {
/* 3181 */           throw localSQLException2;
/*      */         }
/* 3183 */         extractParams();
/* 3184 */         localObject = new StringBuffer("select ");
/* 3185 */         if ((this._paramNameList == null) || (this._paramNameList.isEmpty()))
/*      */         {
/* 3187 */           ((StringBuffer)localObject).append('*');
/*      */         }
/*      */         else
/*      */         {
/* 3191 */           for (j = 0; j < this._paramNameList.size(); ++j)
/*      */           {
/* 3193 */             ((StringBuffer)localObject).append((String)this._paramNameList.get(j));
/* 3194 */             if (j == this._paramNameList.size() - 1)
/*      */               continue;
/* 3196 */             ((StringBuffer)localObject).append(',');
/*      */           }
/*      */         }
/*      */ 
/* 3200 */         ((StringBuffer)localObject).append(" from " + this._table);
/* 3201 */         if ((this._whereBlock != null) && (this._whereBlock.length() > 0))
/*      */         {
/* 3203 */           ((StringBuffer)localObject).append(" " + this._whereBlock);
/*      */         }
/*      */ 
/* 3207 */         int j = this._context._conn.getDynStmtNum();
/* 3208 */         String str = "dyn" + j;
/*      */ 
/* 3210 */         this._context._conn.setDynStmtNum(++j);
/*      */ 
/* 3215 */         this._protocol.dynamicPrepare(this._context, str, ((StringBuffer)localObject).toString(), this._paramMgr);
/* 3216 */         this._dynStmtName = str;
/*      */       }
/*      */     }
/* 3219 */     return (ParameterMetaData)this;
/*      */   }
/*      */ 
/*      */   public String getParameterClassName(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3228 */     Debug.notImplemented(this, "getParameterClassName(int)");
/* 3229 */     return null;
/*      */   }
/*      */ 
/*      */   public int getParameterCount()
/*      */     throws SQLException
/*      */   {
/* 3237 */     return this._paramMgr._paramMdList.size();
/*      */   }
/*      */ 
/*      */   public int getParameterMode(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3245 */     checkIndex(paramInt);
/* 3246 */     --paramInt;
/* 3247 */     return ((Param)this._paramMgr._paramMdList.get(paramInt))._regType;
/*      */   }
/*      */ 
/*      */   public int getParameterType(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3255 */     checkIndex(paramInt);
/* 3256 */     --paramInt;
/* 3257 */     return ((Param)this._paramMgr._paramMdList.get(paramInt))._sqlType;
/*      */   }
/*      */ 
/*      */   public String getParameterTypeName(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3265 */     checkIndex(paramInt);
/* 3266 */     --paramInt;
/* 3267 */     return ((Param)this._paramMgr._paramMdList.get(paramInt))._sqlTypeName;
/*      */   }
/*      */ 
/*      */   public int getPrecision(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3275 */     checkIndex(paramInt);
/* 3276 */     --paramInt;
/* 3277 */     return ((Param)this._paramMgr._paramMdList.get(paramInt))._precision;
/*      */   }
/*      */ 
/*      */   public int getScale(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3285 */     checkIndex(paramInt);
/* 3286 */     --paramInt;
/* 3287 */     return ((Param)this._paramMgr._paramMdList.get(paramInt))._scale;
/*      */   }
/*      */ 
/*      */   public int isNullable(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3295 */     checkIndex(paramInt);
/* 3296 */     --paramInt;
/* 3297 */     return ((Param)this._paramMgr._paramMdList.get(paramInt))._isNullable;
/*      */   }
/*      */ 
/*      */   public boolean isSigned(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3305 */     checkIndex(paramInt);
/* 3306 */     --paramInt;
/* 3307 */     return ((Param)this._paramMgr._paramMdList.get(paramInt))._isSigned;
/*      */   }
/*      */ 
/*      */   private void checkIndex(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3313 */     if ((paramInt >= 1) && (paramInt <= this._paramMgr._paramMdList.size()))
/*      */       return;
/* 3315 */     ErrorMessage.raiseError("JZ0SB", "" + paramInt);
/*      */   }
/*      */ 
/*      */   public int getNumCols()
/*      */   {
/* 3321 */     return this._paramCount;
/*      */   }
/*      */ 
/*      */   public Protocol getProtocol()
/*      */   {
/* 3326 */     return this._protocol;
/*      */   }
/*      */ 
/*      */   public String getDbName() throws SQLException
/*      */   {
/* 3331 */     return this._context._conn.getCatalog();
/*      */   }
/*      */ 
/*      */   private void setEnableBCP(String paramString) throws SQLException
/*      */   {
/* 3336 */     if ((paramString == null) || (paramString.equalsIgnoreCase("NONE")) || (paramString.equalsIgnoreCase("FALSE")))
/*      */     {
/* 3339 */       this._enableBCP = 0;
/*      */     }
/* 3341 */     else if ((paramString.equalsIgnoreCase("TRUE")) || (paramString.equalsIgnoreCase("ARRAYINSERT_WITH_MIXED_STATEMENTS")))
/*      */     {
/* 3344 */       this._enableBCP = 1;
/*      */     }
/* 3346 */     else if (paramString.equalsIgnoreCase("ARRAYINSERT"))
/*      */     {
/* 3348 */       this._enableBCP = 2;
/*      */     }
/* 3350 */     else if (paramString.equalsIgnoreCase("BCP"))
/*      */     {
/* 3352 */       this._enableBCP = 3;
/*      */     }
/* 3354 */     else if (paramString.equalsIgnoreCase("LOG_BCP"))
/*      */     {
/* 3356 */       if (this._dbmda != null)
/*      */       {
/* 3358 */         if (this._dbmda.isFastLoggedBCPSupported())
/*      */         {
/* 3360 */           this._protocol.setOption(null, 14, true);
/*      */         }
/*      */         else
/*      */         {
/* 3369 */           chainWarning(ErrorMessage.createWarning("01S13"));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3378 */       this._enableBCP = 3;
/*      */     }
/*      */     else
/*      */     {
/* 3382 */       ErrorMessage.raiseError("JZBKI");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void prepareBCP(ProtocolContext paramProtocolContext) throws SQLException
/*      */   {
/* 3388 */     setEnableBCP(paramProtocolContext._conn._props.getString(68));
/* 3389 */     if (this._enableBCP > 0)
/*      */     {
/*      */       try
/*      */       {
/* 3393 */         this._sybBCP = new SybBCP(this, this._enableBCP);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 3397 */         if (localSQLException.getErrorCode() == 2812)
/*      */         {
/* 3399 */           ErrorMessage.raiseError("JZBK1");
/*      */         }
/* 3406 */         else if (localSQLException.getSQLState().equalsIgnoreCase("JZ0R2"))
/*      */         {
/* 3408 */           ErrorMessage.raiseError("JZBK3");
/*      */         }
/*      */         else
/*      */         {
/* 3412 */           handleSQLE(localSQLException);
/*      */         }
/*      */       }
/* 3415 */       int i = this._sybBCP.getColumnCount();
/* 3416 */       if (i == 0)
/*      */       {
/* 3418 */         ErrorMessage.raiseError("JZBK3");
/*      */       }
/*      */ 
/* 3422 */       initializeParamArrays(i);
/*      */ 
/* 3425 */       if ((this._enableBCP == 1) && 
/* 3427 */         (this._bulkObject == null))
/*      */       {
/* 3429 */         this._bulkObject = new LinkedList();
/* 3430 */         this._bulkTypes = new LinkedList();
/* 3431 */         this._bulkScale = new LinkedList();
/* 3432 */         this._bulkCal = new LinkedList();
/*      */       }
/*      */ 
/* 3436 */       boolean bool = false;
/* 3437 */       if (this._sybBCP._columnDefination.size() > 0)
/*      */       {
/* 3439 */         bool = true;
/*      */       }
/* 3441 */       for (int j = 0; ; ++j) { if (j >= i)
/*      */           return;
/* 3443 */         this._sybBCP.getColumnMetaDataAll(15 * j, 2 * j);
/*      */ 
/* 3445 */         this._sybBCP.setColumnIdList(j, bool);
/* 3446 */         if (this._sybBCP.getColumnDefault() != null)
/*      */         {
/* 3448 */           this._sybBCP.findDefaults(this._paramMgr._params[0], j);
/*      */         }
/* 3450 */         this._sybBCP.setEncrypted(); }
/*      */ 
/*      */ 
/*      */     }
/*      */ 
/* 3456 */     initializeParamArrays(this._paramCount);
/*      */   }
/*      */ 
/*      */   private boolean isInsertInSQL(String paramString)
/*      */   {
/* 3462 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
/* 3463 */     int i = 0;
/* 3464 */     int j = localStringTokenizer.nextToken().compareToIgnoreCase("insert");
/* 3465 */     if (j == 0)
/*      */     {
/* 3467 */       i = 1;
/*      */     }
/* 3469 */     return i;
/*      */   }
/*      */ 
/*      */   private void initializeParamArrays(int paramInt)
/*      */   {
/* 3474 */     this._types = new int[paramInt];
/* 3475 */     this._scale = new int[paramInt];
/* 3476 */     this._cal = new Calendar[paramInt];
/* 3477 */     this._object = new Object[paramInt];
/* 3478 */     this._batchFirstRowParamType = new int[paramInt];
/*      */ 
/* 3480 */     for (int i = 0; i < paramInt; ++i)
/*      */     {
/* 3482 */       this._types[i] = -999; this._batchFirstRowParamType[i] = -999;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void addCalendar(int paramInt, Calendar paramCalendar)
/*      */   {
/* 3488 */     if (paramCalendar != null)
/*      */     {
/* 3490 */       this._cal[(paramInt - 1)] = ((Calendar)paramCalendar.clone());
/*      */     }
/*      */     else
/*      */     {
/* 3494 */       this._cal[(paramInt - 1)] = paramCalendar;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addObject(int paramInt, Object paramObject)
/*      */   {
/* 3500 */     this._object[(paramInt - 1)] = paramObject;
/*      */   }
/*      */ 
/*      */   private void addScale(int paramInt1, int paramInt2)
/*      */   {
/* 3505 */     this._scale[(paramInt1 - 1)] = paramInt2;
/*      */   }
/*      */ 
/*      */   private void addType(int paramInt1, int paramInt2)
/*      */   {
/* 3510 */     this._types[(paramInt1 - 1)] = paramInt2;
/*      */   }
/*      */ 
/*      */   protected ParamManager setParamsFromArrays()
/*      */     throws SQLException
/*      */   {
/* 3520 */     for (int i = 0; i < this._paramMgr._params.length; ++i)
/*      */     {
/* 3522 */       Object localObject = this._object[i];
/* 3523 */       switch (this._types[i])
/*      */       {
/*      */       case 91:
/*      */       case 92:
/*      */       case 93:
/* 3528 */         if (this._cal[i] != null)
/*      */         {
/* 3530 */           localObject = new DateObject(localObject, this._cal[i], this._types[i]);
/*      */         }
/*      */         else
/*      */         {
/* 3534 */           localObject = new DateObject(localObject, this._types[i]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3539 */       this._paramMgr.setParam(i + 1, this._types[i], localObject, this._scale[i]);
/*      */     }
/* 3541 */     return (ParamManager)this._paramMgr;
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 3562 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3564 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3566 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 3569 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3571 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 3574 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3576 */         LOG.fine(this._logId + " setAsciiStream(int, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 3580 */     checkDead();
/* 3581 */     checkIfMixedWithLob(paramInt, -1);
/* 3582 */     setAsciiStream(paramInt, paramInputStream, -1);
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3591 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3593 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3595 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 3598 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3600 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setAsciiStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 3603 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3605 */         LOG.fine(this._logId + " setAsciiStream(int, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 3609 */     checkDead();
/* 3610 */     checkIfMixedWithLob(paramInt, -1);
/* 3611 */     if (paramInputStream == null)
/*      */     {
/* 3613 */       setNull(paramInt, -1);
/*      */     }
/*      */     else
/*      */     {
/* 3617 */       int i = checkLongLength(paramLong);
/* 3618 */       setAsciiStream(paramInt, paramInputStream, i);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 3627 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3629 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3631 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 3634 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3636 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 3639 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3641 */         LOG.fine(this._logId + " setBinaryStream(int, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 3645 */     checkDead();
/* 3646 */     checkIfMixedWithLob(paramInt, -4);
/* 3647 */     setBinaryStream(paramInt, paramInputStream, -1);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3656 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3658 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3660 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 3663 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3665 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBinaryStream", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 3668 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3670 */         LOG.fine(this._logId + " setBinaryStream(int, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 3674 */     checkDead();
/* 3675 */     checkIfMixedWithLob(paramInt, -4);
/* 3676 */     int i = checkLongLength(paramLong);
/* 3677 */     setBinaryStream(paramInt, paramInputStream, i);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int paramInt, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 3685 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3687 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3689 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setCharacterStream", new Object[] { new Integer(paramInt), paramReader }));
/*      */       }
/* 3693 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3695 */         LOG.fine(this._logId + " setCharacterStream(int, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 3699 */     checkDead();
/* 3700 */     checkIfMixedWithLob(paramInt, -1);
/* 3701 */     setCharacterStream(paramInt, paramReader, -1);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int paramInt, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3710 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3712 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3714 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setCharacterStream", new Object[] { new Integer(paramInt), paramReader, new Long(paramLong) }));
/*      */       }
/* 3718 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3720 */         LOG.fine(this._logId + " setCharacterStream(int, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 3724 */     checkDead();
/* 3725 */     checkIfMixedWithLob(paramInt, -1);
/* 3726 */     int i = checkLongLength(paramLong);
/* 3727 */     setCharacterStream(paramInt, paramReader, i);
/*      */   }
/*      */ 
/*      */   protected int checkLongLength(long paramLong)
/*      */   {
/*      */     int i;
/* 3777 */     if (paramLong > 2147483647L)
/*      */     {
/* 3779 */       i = 2147483647;
/* 3780 */       this._context._conn.chainWarnings(ErrorMessage.createWarning("01S11"));
/*      */     }
/*      */     else
/*      */     {
/* 3786 */       i = (int)paramLong;
/*      */     }
/*      */ 
/* 3789 */     return i;
/*      */   }
/*      */ 
/*      */   protected String batchToString()
/*      */   {
/* 3794 */     return (this._isDynamic) ? this._dynStmtName : super.batchToString();
/*      */   }
/*      */ 
/*      */   public void setClob(int paramInt, Reader paramReader)
/*      */     throws SQLException
/*      */   {
/* 3802 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3804 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3806 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setClob", new Object[] { new Integer(paramInt), paramReader }));
/*      */       }
/* 3809 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3811 */         LOG.fine(this._logId + " setClob(int, Reader)");
/*      */       }
/*      */     }
/*      */ 
/* 3815 */     checkDead();
/* 3816 */     checkIfMixedWithLob(paramInt, -1);
/* 3817 */     setParam(-1, paramInt, paramReader, -1);
/*      */   }
/*      */ 
/*      */   public void setClob(int paramInt, Reader paramReader, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3825 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3827 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3829 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setClob", new Object[] { new Integer(paramInt), paramReader, new Long(paramLong) }));
/*      */       }
/* 3832 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3834 */         LOG.fine(this._logId + " setClob(int, Reader, long)");
/*      */       }
/*      */     }
/*      */ 
/* 3838 */     if (paramLong < -1L)
/*      */     {
/* 3840 */       ErrorMessage.raiseError("JZ039");
/*      */     }
/*      */ 
/* 3843 */     checkDead();
/* 3844 */     checkIfMixedWithLob(paramInt, -1);
/* 3845 */     int i = checkLongLength(paramLong);
/* 3846 */     setParam(-1, paramInt, paramReader, i);
/*      */   }
/*      */ 
/*      */   public void setBlob(int paramInt, InputStream paramInputStream)
/*      */     throws SQLException
/*      */   {
/* 3909 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3911 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3913 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 3916 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3918 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramInputStream }));
/*      */       }
/* 3921 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3923 */         LOG.fine(this._logId + " setBlob(int, InputStream)");
/*      */       }
/*      */     }
/*      */ 
/* 3927 */     checkDead();
/* 3928 */     checkIfMixedWithLob(paramInt, -4);
/* 3929 */     setParam(-4, paramInt, paramInputStream, -1);
/*      */   }
/*      */ 
/*      */   public void setBlob(int paramInt, InputStream paramInputStream, long paramLong)
/*      */     throws SQLException
/*      */   {
/* 3938 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3940 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3942 */         LOG.finest(LogUtil.logMethod(false, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 3945 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3947 */         LOG.finer(LogUtil.logMethod(true, this._logId, " setBlob", new Object[] { new Integer(paramInt), paramInputStream, new Long(paramLong) }));
/*      */       }
/* 3950 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3952 */         LOG.fine(this._logId + " setBlob(int, InputStream, long)");
/*      */       }
/*      */     }
/*      */ 
/* 3956 */     if (paramLong < -1L)
/*      */     {
/* 3958 */       ErrorMessage.raiseError("JZ039");
/*      */     }
/*      */ 
/* 3961 */     checkDead();
/* 3962 */     checkIfMixedWithLob(paramInt, -4);
/* 3963 */     int i = checkLongLength(paramLong);
/* 3964 */     setParam(-4, paramInt, paramInputStream, i);
/*      */   }
/*      */ 
/*      */   protected boolean isBatchingInitialized()
/*      */   {
/* 4023 */     return this._batchInitialized;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybPreparedStatement
 * JD-Core Version:    0.5.4
 */