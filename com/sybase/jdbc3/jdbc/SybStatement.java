/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.tds.TdsProtocolContext;
/*      */ import com.sybase.jdbc3.utils.CacheManager;
/*      */ import com.sybase.jdbc3.utils.LogUtil;
/*      */ import com.sybase.jdbcx.SybMessageHandler;
/*      */ import java.io.IOException;
/*      */ import java.io.StringWriter;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Connection;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.util.Enumeration;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ public class SybStatement
/*      */   implements com.sybase.jdbcx.SybStatement
/*      */ {
/*   53 */   private static Logger LOG = Logger.getLogger(SybStatement.class.getName());
/*   54 */   private static volatile long _logIdCounter = 0L;
/*   55 */   protected String _logId = null;
/*      */ 
/*   57 */   private SQLWarning _warning = null;
/*      */ 
/*   60 */   protected int _rsType = 1003;
/*      */ 
/*   63 */   protected int _rsConcur = -9;
/*      */   private int _rsHold;
/*   69 */   protected int _rsFetchDir = 1000;
/*      */   protected ProtocolContext _context;
/*      */   protected Protocol _protocol;
/*      */   protected CacheManager _statementManager;
/*   79 */   protected boolean _doEscapes = true;
/*      */ 
/*   82 */   protected String _rpcName = null;
/*      */ 
/*   85 */   protected boolean _sendAsRpc = false;
/*      */ 
/*   88 */   protected boolean _hasReturn = false;
/*      */ 
/*   93 */   protected boolean _allowsOutputParms = false;
/*      */ 
/*   98 */   protected boolean _returnHasBeenRegistered = false;
/*      */ 
/*  100 */   protected boolean _closing = false;
/*      */ 
/*  105 */   protected SybResultSet _currentRS = null;
/*      */   protected ResultGetter _resultGetter;
/*  110 */   protected Cursor _cursor = null;
/*      */ 
/*  113 */   protected byte _state = 1;
/*      */ 
/*  115 */   private int _rowcount = -1;
/*      */ 
/*  118 */   private boolean _validRowcount = false;
/*      */   private int _currentResult;
/*      */   protected static final int NORESULTS = 1;
/*      */   protected static final int ONERESULT = 2;
/*      */   protected static final int MANYRESULTS = 3;
/*      */   protected static final String CALL_DELIMS = "(), \t\n\r";
/*      */   protected static final int OTHER = 0;
/*      */   protected static final int CALL = 1;
/*      */   protected static final int SELECT = 2;
/*      */   protected static final int DELETE = 4;
/*      */   protected static final int UPDATE = 8;
/*      */   protected static final int INSERT = 16;
/*      */   protected static final int VALID = 4096;
/*      */   protected int _type;
/*      */   protected int _setStart;
/*      */   protected int _setEnd;
/*      */   protected String _table;
/*  161 */   protected Vector _batchCmds = null;
/*  162 */   protected int _batchCmdsCount = 0;
/*  163 */   protected boolean _doneinproc = false;
/*      */ 
/*  165 */   protected SybDatabaseMetaData _dbmda = null;
/*      */ 
/*  168 */   protected Vector _rowFmtIdxList = null;
/*  169 */   protected Vector _paramNameList = null;
/*      */ 
/*  172 */   protected boolean _retGeneratedKeys = false;
/*  173 */   protected SybResultSet _genKeysRS = null;
/*      */ 
/*  175 */   protected byte _executionMode = 2;
/*      */ 
/*  177 */   protected Vector _currentOpenRS = null;
/*      */ 
/*  179 */   private SQLException _storedBatchSQE = null;
/*      */ 
/*  185 */   private long _maxBICounter = -1L;
/*      */ 
/*      */   protected SybStatement(String paramString, ProtocolContext paramProtocolContext)
/*      */   {
/*  192 */     this._logId = (paramString + "_St" + _logIdCounter++);
/*      */ 
/*  194 */     this._context = paramProtocolContext;
/*  195 */     this._context._batch = false;
/*  196 */     this._protocol = paramProtocolContext._protocol;
/*  197 */     this._statementManager = new CacheManager(paramProtocolContext._is);
/*  198 */     this._statementManager.setSetable(false);
/*  199 */     this._resultGetter = new ResultGetter(this);
/*  200 */     this._resultGetter.setManager(this._statementManager);
/*  201 */     this._statementManager.register(this._resultGetter);
/*      */ 
/*  204 */     this._rsHold = this._context._conn._rsHoldability;
/*  205 */     this._currentOpenRS = new Vector();
/*  206 */     setDoEscapes();
/*      */     try
/*      */     {
/*  209 */       this._maxBICounter = this._context._conn._props.getMaxBICount();
/*  210 */       this._maxBICounter = ((this._maxBICounter == -1L) ? 2147483647L : this._maxBICounter * this._context._conn._props.getBufferSize() / 1024L);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setDoEscapes()
/*      */   {
/*      */     try
/*      */     {
/*  223 */       this._doEscapes = this._context._conn._props.getBoolean(46);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int nextResult()
/*      */     throws SQLException
/*      */   {
/*  257 */     return nextResult((ParamManager)null);
/*      */   }
/*      */ 
/*      */   protected int nextResult(ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/*  266 */     if (!this._retGeneratedKeys)
/*      */     {
/*  268 */       resetRowCount();
/*      */     }
/*      */ 
/*      */     while (true)
/*      */     {
/*      */       try
/*      */       {
/*  275 */         this._currentResult = this._resultGetter.nextResult();
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/*  280 */         handleSQLE(localSQLException);
/*      */       }
/*      */     }
/*  283 */     return processResults(paramParamManager);
/*      */   }
/*      */ 
/*      */   protected int processResults(ParamManager paramParamManager) throws SQLException
/*      */   {
/*  288 */     switch (this._currentResult)
/*      */     {
/*      */     case 1:
/*      */     case 6:
/*  293 */       ProtocolResultSet localProtocolResultSet = this._protocol.resultSet(this._context);
/*  294 */       if (localProtocolResultSet != null)
/*      */       {
/*  297 */         localProtocolResultSet.setType((this._retGeneratedKeys) ? 1004 : this._rsType);
/*      */       }
/*      */ 
/*  300 */       SybResultSet localSybResultSet = new SybResultSet(this._logId, this, localProtocolResultSet);
/*      */       try
/*      */       {
/*  303 */         localSybResultSet.setManager(this._statementManager);
/*  304 */         this._statementManager.register(localSybResultSet);
/*  305 */         this._statementManager.open(localSybResultSet);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*      */       }
/*      */ 
/*  312 */       if (this._retGeneratedKeys)
/*      */       {
/*  314 */         this._genKeysRS = localSybResultSet;
/*      */       }
/*      */       else
/*      */       {
/*  318 */         this._currentRS = localSybResultSet;
/*      */       }
/*      */ 
/*  320 */       break;
/*      */     case 5:
/*  323 */       setRowCount(this._protocol.count(this._context));
/*      */ 
/*  326 */       break;
/*      */     case 0:
/*  331 */       setRowCount(-1);
/*  332 */       this._state = 1;
/*      */ 
/*  334 */       break;
/*      */     case 3:
/*  337 */       handleParam(paramParamManager);
/*      */     case 2:
/*      */     case 4:
/*      */     }
/*      */ 
/*  344 */     this._validRowcount = true;
/*      */ 
/*  346 */     return this._currentResult;
/*      */   }
/*      */ 
/*      */   protected String processEscapes(String paramString)
/*      */     throws SQLException
/*      */   {
/*  356 */     if (paramString.length() == 0)
/*      */     {
/*  358 */       return paramString;
/*      */     }
/*      */ 
/*  363 */     EscapeTokenizer localEscapeTokenizer = new EscapeTokenizer(this, paramString);
/*  364 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */ 
/*  367 */     for (Object localObject = localEscapeTokenizer.next(); localObject != null; localObject = localEscapeTokenizer.next())
/*      */     {
/*  371 */       if (localObject instanceof String)
/*      */       {
/*  373 */         localStringBuffer.append((String)localObject);
/*      */       }
/*      */       else
/*      */       {
/*  377 */         Escape localEscape = (Escape)localObject;
/*      */ 
/*  379 */         switch (localEscape.getType())
/*      */         {
/*      */         case 4:
/*  382 */           String str1 = localEscape.getBody();
/*  383 */           if (this._context._conn._props.getBoolean(52))
/*      */           {
/*  385 */             localStringBuffer.append(str1);
/*      */           }
/*      */           else
/*      */           {
/*  389 */             localStringBuffer.append("'1970-1-1 ");
/*      */ 
/*  391 */             localStringBuffer.append(str1.substring(1, str1.length()));
/*      */           }
/*  393 */           break;
/*      */         case 3:
/*  396 */           String str2 = localEscape.getBody();
/*  397 */           localStringBuffer.append(str2.substring(0, str2.length() - 1));
/*  398 */           localStringBuffer.append(" 00:00:00'");
/*  399 */           break;
/*      */         case 5:
/*  401 */           String str3 = localEscape.getBody();
/*  402 */           int i = str3.indexOf(".");
/*  403 */           if ((i > 0) && (str3.length() - i > 4))
/*      */           {
/*  406 */             localStringBuffer.append(str3.substring(0, i + 4) + "'");
/*      */           }
/*      */           else
/*      */           {
/*  411 */             localStringBuffer.append(str3);
/*      */           }
/*  413 */           break;
/*      */         case 2:
/*  415 */           this._hasReturn = true;
/*  416 */           localStringBuffer = new StringBuffer(handleCallBody(localEscape.getBody()));
/*      */ 
/*  418 */           break;
/*      */         case 1:
/*  420 */           this._hasReturn = false;
/*  421 */           localStringBuffer = new StringBuffer(handleCallBody(localEscape.getBody()));
/*      */ 
/*  423 */           break;
/*      */         case 7:
/*  425 */           localStringBuffer.append(" ESCAPE " + localEscape.getBody());
/*  426 */           break;
/*      */         case 6:
/*  428 */           String str4 = localEscape.functionName();
/*  429 */           if ((str4.equalsIgnoreCase("extract")) || (str4.equalsIgnoreCase("position")))
/*      */           {
/*  432 */             str4 = getAlternateFunction(str4, localEscape.getBody());
/*      */           }
/*      */ 
/*  435 */           String str5 = this._protocol.getStringOption(this._context, 10, str4);
/*      */ 
/*  438 */           if (str5 == null)
/*      */           {
/*  440 */             ErrorMessage.raiseError("JZ0SI", localEscape.functionName());
/*      */           }
/*      */ 
/*  444 */           localStringBuffer.append(localEscape.doMap(str5));
/*  445 */           break;
/*      */         case 8:
/*  447 */           if (((SybDatabaseMetaData)this._context._conn.getMetaData()).supportsOuterJoinEscapeSyntax())
/*      */           {
/*  451 */             localStringBuffer.append(localEscape.getBody());
/*      */           }
/*      */           else
/*      */           {
/*  455 */             ErrorMessage.raiseError("JZ0SK");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  468 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private String getAlternateFunction(String paramString1, String paramString2) throws SQLException
/*      */   {
/*  473 */     if (paramString1.equalsIgnoreCase("extract"))
/*      */     {
/*  475 */       String[] arrayOfString = paramString2.split("[()]|[fF]+[rR]+[oO]+[mM]");
/*  476 */       if (arrayOfString.length == 3)
/*      */       {
/*  479 */         if (arrayOfString[1].trim().equalsIgnoreCase("day"))
/*      */         {
/*  481 */           return "dayofmonth";
/*      */         }
/*  483 */         return arrayOfString[1].trim();
/*      */       }
/*      */ 
/*  487 */       ErrorMessage.raiseError("JZ0S8", paramString2);
/*      */     }
/*  490 */     else if (paramString1.equalsIgnoreCase("position"))
/*      */     {
/*  492 */       return "locate";
/*      */     }
/*  494 */     return paramString1;
/*      */   }
/*      */ 
/*      */   protected String doEscapeProcessing(String paramString)
/*      */     throws SQLException
/*      */   {
/*  504 */     if ((paramString == null) || (paramString.length() == 0))
/*      */     {
/*  506 */       ErrorMessage.raiseError("JZ0S4");
/*      */     }
/*  508 */     if (this._doEscapes)
/*      */     {
/*  510 */       return processEscapes(paramString);
/*      */     }
/*  512 */     return paramString;
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery(String paramString)
/*      */     throws SQLException
/*      */   {
/*  520 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  522 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  524 */         LOG.finer(this._logId + " executeQuery(String = [" + paramString + "])");
/*      */       }
/*  526 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  528 */         LOG.fine(this._logId + " executeQuery(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  534 */     paramString = doEscapeProcessing(paramString);
/*  535 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)this._context;
/*      */ 
/*  537 */     localTdsProtocolContext.resetRowFmt();
/*  538 */     return executeQuery(paramString, null);
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString)
/*      */     throws SQLException
/*      */   {
/*  546 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  548 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  550 */         LOG.finer(this._logId + " executeUpdate(String = [" + paramString + "])");
/*      */       }
/*  552 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  554 */         LOG.fine(this._logId + " executeUpdate(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  560 */     paramString = doEscapeProcessing(paramString);
/*      */ 
/*  562 */     return executeUpdate(paramString, (ParamManager)null);
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  571 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  573 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  575 */         LOG.finer(this._logId + " executeUpdate(String = [" + paramString + "] , int = [" + paramInt + "])");
/*      */       }
/*  578 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  580 */         LOG.fine(this._logId + " executeUpdate(String, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  586 */     paramString = doEscapeProcessing(paramString);
/*  587 */     paramString = processGenKeysRequest(paramInt, paramString);
/*  588 */     int i = executeUpdate(paramString, (ParamManager)null);
/*  589 */     this._retGeneratedKeys = false;
/*  590 */     return i;
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/*  599 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  601 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  603 */         LOG.finest(LogUtil.logMethod(false, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/*  606 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  608 */         LOG.finer(LogUtil.logMethod(true, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/*  611 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  613 */         LOG.fine(this._logId + " executeUpdate(String, int[])");
/*      */       }
/*      */     }
/*      */ 
/*  617 */     if ((paramArrayOfInt == null) || (paramArrayOfInt.length != 1))
/*      */     {
/*  619 */       ErrorMessage.raiseError("JZ0GK", "columnIndexes");
/*      */     }
/*      */ 
/*  622 */     return executeUpdate(paramString, 1);
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String paramString, String[] paramArrayOfString)
/*      */     throws SQLException
/*      */   {
/*  631 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  633 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/*  635 */         LOG.finest(LogUtil.logMethod(false, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/*  638 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  640 */         LOG.finer(LogUtil.logMethod(true, this._logId, " executeUpdate", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/*  643 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  645 */         LOG.fine(this._logId + " executeUpdate(String, String[])");
/*      */       }
/*      */     }
/*      */ 
/*  649 */     if ((paramArrayOfString == null) || (paramArrayOfString.length != 1))
/*      */     {
/*  651 */       ErrorMessage.raiseError("JZ0GK", "columnNames");
/*      */     }
/*      */ 
/*  654 */     return executeUpdate(paramString, 1);
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  662 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  664 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  666 */       LOG.fine(this._logId + " close()");
/*      */     }
/*      */ 
/*  670 */     close(true);
/*      */   }
/*      */ 
/*      */   protected void close(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  683 */     if (this._state == 2)
/*      */     {
/*  685 */       return;
/*      */     }
/*  687 */     this._context._conn.checkConnection();
/*      */ 
/*  689 */     this._context._conn._stmtList.removeElement(this);
/*  690 */     this._closing = true;
/*      */ 
/*  692 */     doCancel(true, false);
/*      */     try
/*      */     {
/*  696 */       deallocateDynamic();
/*  697 */       this._statementManager.clear();
/*      */ 
/*  699 */       if (this._cursor != null)
/*      */       {
/*  701 */         this._cursor.close(true);
/*  702 */         this._cursor = null;
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  707 */       ErrorMessage.raiseErrorCheckDead(localIOException);
/*      */     }
/*  709 */     this._statementManager = null;
/*  710 */     if (paramBoolean)
/*      */     {
/*  712 */       this._context.drop();
/*      */     }
/*  714 */     this._state = 2;
/*      */   }
/*      */ 
/*      */   public int getMaxFieldSize()
/*      */     throws SQLException
/*      */   {
/*  722 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  724 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  726 */       LOG.fine(this._logId + " getMaxFielsSize()");
/*      */     }
/*      */ 
/*  731 */     checkDead();
/*  732 */     return this._context._maxFieldSize;
/*      */   }
/*      */ 
/*      */   public void setMaxFieldSize(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  740 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  742 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  744 */         LOG.finer(this._logId + " setMaxFieldSize(int = [" + paramInt + "])");
/*      */       }
/*  746 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  748 */         LOG.fine(this._logId + " setMaxFieldSize(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  753 */     if (paramInt < 0)
/*      */     {
/*  755 */       ErrorMessage.raiseError("JZ0SN");
/*      */     }
/*  757 */     checkStatement(true);
/*  758 */     this._context._maxFieldSize = paramInt;
/*      */   }
/*      */ 
/*      */   public int getMaxRows()
/*      */     throws SQLException
/*      */   {
/*  766 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  768 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  770 */       LOG.fine(this._logId + " getMaxRows()");
/*      */     }
/*      */ 
/*  775 */     checkDead();
/*  776 */     return this._protocol.getIntOption(this._context, 4);
/*      */   }
/*      */ 
/*      */   public void setMaxRows(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  784 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  786 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  788 */         LOG.finer(this._logId + " setMaxRows(int = [" + paramInt + "])");
/*      */       }
/*  790 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  792 */         LOG.fine(this._logId + " setMaxRows(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  798 */     if (paramInt < 0)
/*      */     {
/*  800 */       ErrorMessage.raiseError("JZ0SR");
/*      */     }
/*  802 */     checkDead();
/*      */     try
/*      */     {
/*  805 */       this._protocol.setOption(this._context, 4, paramInt);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  809 */       handleSQLE(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setEscapeProcessing(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/*  818 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  820 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  822 */         LOG.finer(this._logId + " setEscapeProcessing(boolean = [" + paramBoolean + "])");
/*      */       }
/*  825 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  827 */         LOG.fine(this._logId + " setEscapeProcessing(boolean)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  832 */     checkDead();
/*  833 */     this._doEscapes = paramBoolean;
/*      */   }
/*      */ 
/*      */   public int getQueryTimeout()
/*      */     throws SQLException
/*      */   {
/*  841 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  843 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  845 */       LOG.fine(this._logId + " getQueryTimeout()");
/*      */     }
/*      */ 
/*  850 */     checkDead();
/*  851 */     return this._context._timeout / 1000;
/*      */   }
/*      */ 
/*      */   public void setQueryTimeout(int paramInt)
/*      */     throws SQLException
/*      */   {
/*  859 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/*  861 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/*  863 */         LOG.finer(this._logId + " setQueryTimeout(int = [" + paramInt + "])");
/*      */       }
/*  865 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/*  867 */         LOG.fine(this._logId + " setQueryTimeout(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  873 */     if (paramInt < 0)
/*      */     {
/*  875 */       ErrorMessage.raiseError("JZ0SS");
/*      */     }
/*  877 */     checkDead();
/*  878 */     this._context._timeout = (paramInt * 1000);
/*      */   }
/*      */ 
/*      */   public void cancel()
/*      */     throws SQLException
/*      */   {
/*  886 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  888 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  890 */       LOG.fine(this._logId + " cancel()");
/*      */     }
/*      */ 
/*  896 */     checkDead();
/*  897 */     doCancel(true, false);
/*      */   }
/*      */ 
/*      */   private void doCancel(boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws SQLException
/*      */   {
/*  912 */     if ((!paramBoolean2) && (this._context._conn.getHAState() != 8))
/*      */     {
/*  914 */       this._protocol.cancel(this._context, false);
/*      */     }
/*  916 */     if (this._cursor != null)
/*      */     {
/*  918 */       this._cursor.close(paramBoolean1);
/*  919 */       if (paramBoolean1) this._cursor = null;
/*      */     }
/*  921 */     if (this._currentRS != null)
/*      */     {
/*  923 */       if (this._context._conn.getHAState() == 8)
/*      */       {
/*  925 */         this._currentRS.close(false);
/*      */       }
/*      */       else
/*      */       {
/*  929 */         this._currentRS.close(true);
/*      */       }
/*  931 */       this._currentRS = null;
/*      */     }
/*  933 */     if (this._genKeysRS != null)
/*      */     {
/*  935 */       this._genKeysRS.close();
/*  936 */       this._genKeysRS = null;
/*      */     }
/*  938 */     if (!this._currentOpenRS.isEmpty())
/*      */     {
/*  940 */       Enumeration localEnumeration = this._currentOpenRS.elements();
/*  941 */       while (localEnumeration.hasMoreElements())
/*      */       {
/*  943 */         ((SybResultSet)localEnumeration.nextElement()).close();
/*      */       }
/*  945 */       this._currentOpenRS.clear();
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  950 */       this._statementManager.clear();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */ 
/*  956 */     resetRowCount();
/*  957 */     this._state = 1;
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/*  966 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  968 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  970 */       LOG.fine(this._logId + " getWarnings()");
/*      */     }
/*      */ 
/*  982 */     if (this._state == 2)
/*      */     {
/*  984 */       ErrorMessage.raiseError("JZ0S2");
/*      */     }
/*      */ 
/*  987 */     return this._warning;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  995 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  997 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  999 */       LOG.fine(this._logId + " clearWarnings()");
/*      */     }
/*      */ 
/* 1005 */     checkDead();
/* 1006 */     this._warning = null;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setCursorName(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1024 */     checkDead();
/*      */     try
/*      */     {
/* 1028 */       checkCursor(true, 1008);
/* 1029 */       this._cursor.setName(paramString);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1033 */       handleSQLE(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setFetchSize(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1064 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1066 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1068 */         LOG.finer(this._logId + " setFetchSize(int = [" + paramInt + "])");
/*      */       }
/* 1070 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1072 */         LOG.fine(this._logId + " setFetchSize(int)");
/*      */       }
/*      */     }
/*      */ 
/* 1076 */     if (paramInt == 0)
/*      */     {
/* 1078 */       return;
/*      */     }
/*      */ 
/* 1081 */     int i = getMaxRows();
/*      */ 
/* 1084 */     if ((paramInt < 0) || ((i > 0) && (paramInt > i)))
/*      */     {
/* 1087 */       ErrorMessage.raiseError("JZ0BI");
/*      */     }
/*      */ 
/* 1090 */     checkCursor(false, 1007);
/* 1091 */     this._cursor.setFetchSize(paramInt);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public int getFetchSize()
/*      */     throws SQLException
/*      */   {
/* 1117 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1119 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1121 */       LOG.fine(this._logId + " getFetchSize()");
/*      */     }
/*      */ 
/* 1125 */     if (this._cursor != null)
/*      */     {
/* 1127 */       return this._cursor.getFetchSize();
/*      */     }
/* 1129 */     return 0;
/*      */   }
/*      */ 
/*      */   public ResultSet getGeneratedKeys() throws SQLException
/*      */   {
/* 1134 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1136 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1138 */       LOG.fine(this._logId + " getGeneratedKeys()");
/*      */     }
/*      */ 
/* 1142 */     checkDead();
/* 1143 */     if ((this._genKeysRS == null) || ((this._genKeysRS.next()) && (this._genKeysRS.getBigDecimal(1).longValue() == 0L)))
/*      */     {
/* 1146 */       ErrorMessage.raiseError("JZ0NK");
/*      */     }
/*      */ 
/* 1149 */     if (!this._genKeysRS.isBeforeFirst())
/*      */     {
/* 1151 */       this._genKeysRS.beforeFirst();
/*      */     }
/* 1153 */     return this._genKeysRS;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public int getResultSetConcurrency()
/*      */     throws SQLException
/*      */   {
/* 1179 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1181 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1183 */       LOG.fine(this._logId + " getResultSetConcurrency()");
/*      */     }
/*      */ 
/* 1187 */     if (this._currentRS != null)
/*      */     {
/* 1189 */       return this._currentRS.getConcurrency();
/*      */     }
/*      */ 
/* 1195 */     if (this._rsConcur == -9)
/*      */     {
/* 1197 */       return 1007;
/*      */     }
/*      */ 
/* 1201 */     return this._rsConcur;
/*      */   }
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 1207 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1209 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1211 */       LOG.fine(this._logId + " getResultSetHoldability()");
/*      */     }
/*      */ 
/* 1215 */     return this._rsHold;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Connection getConnection()
/*      */     throws SQLException
/*      */   {
/* 1237 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1239 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1241 */       LOG.fine(this._logId + " getConnection()");
/*      */     }
/*      */ 
/* 1246 */     if (this._context._conn instanceof SybPooledConnection)
/*      */     {
/* 1248 */       return ((SybPooledConnection)this._context._conn).getConnectionProxy();
/*      */     }
/* 1250 */     return this._context._conn;
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1260 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1262 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1264 */         LOG.finer(this._logId + " execute(String = [" + paramString + "])");
/*      */       }
/* 1266 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1268 */         LOG.fine(this._logId + " execute(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1274 */     paramString = doEscapeProcessing(paramString);
/* 1275 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)this._context;
/*      */ 
/* 1277 */     localTdsProtocolContext.resetRowFmt();
/* 1278 */     return execute(paramString, (ParamManager)null);
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1287 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1289 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1291 */         LOG.finer(this._logId + " execute(String = [" + paramString + "] , int = [" + paramInt + "])");
/*      */       }
/* 1294 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1296 */         LOG.fine(this._logId + " execute(String, int)");
/*      */       }
/*      */     }
/*      */ 
/* 1300 */     paramString = doEscapeProcessing(paramString);
/* 1301 */     paramString = processGenKeysRequest(paramInt, paramString);
/* 1302 */     TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)this._context;
/*      */ 
/* 1304 */     localTdsProtocolContext.resetRowFmt();
/* 1305 */     boolean bool = execute(paramString, (ParamManager)null);
/* 1306 */     if (this._retGeneratedKeys)
/*      */     {
/* 1308 */       nextResult();
/*      */     }
/* 1310 */     this._retGeneratedKeys = false;
/* 1311 */     return bool;
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 1320 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1322 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1324 */         LOG.finest(LogUtil.logMethod(false, this._logId, " execute", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/* 1327 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1329 */         LOG.finer(LogUtil.logMethod(true, this._logId, " execute", new Object[] { paramString, paramArrayOfInt }));
/*      */       }
/* 1332 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1334 */         LOG.fine(this._logId + " execute(String, int[])");
/*      */       }
/*      */     }
/*      */ 
/* 1338 */     if ((paramArrayOfInt == null) || (paramArrayOfInt.length != 1))
/*      */     {
/* 1340 */       ErrorMessage.raiseError("JZ0GK", "columnIndexes");
/*      */     }
/*      */ 
/* 1343 */     return execute(paramString, 1);
/*      */   }
/*      */ 
/*      */   public boolean execute(String paramString, String[] paramArrayOfString)
/*      */     throws SQLException
/*      */   {
/* 1352 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1354 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 1356 */         LOG.finest(LogUtil.logMethod(false, this._logId, " execute", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/* 1359 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1361 */         LOG.finer(LogUtil.logMethod(true, this._logId, " execute", new Object[] { paramString, paramArrayOfString }));
/*      */       }
/* 1364 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1366 */         LOG.fine(this._logId + " execute(String, String[])");
/*      */       }
/*      */     }
/*      */ 
/* 1370 */     if ((paramArrayOfString == null) || (paramArrayOfString.length != 1))
/*      */     {
/* 1372 */       ErrorMessage.raiseError("JZ0GK", "columnNames");
/*      */     }
/*      */ 
/* 1375 */     return execute(paramString, 1);
/*      */   }
/*      */ 
/*      */   public ResultSet getResultSet()
/*      */     throws SQLException
/*      */   {
/* 1383 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1385 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1387 */       LOG.fine(this._logId + " getResultSet()");
/*      */     }
/*      */ 
/* 1392 */     checkDead();
/* 1393 */     if (this._currentResult != 5)
/*      */     {
/* 1395 */       setRowCount(-1);
/*      */     }
/*      */ 
/* 1398 */     if (this._context.getState() == 5)
/*      */     {
/* 1400 */       ErrorMessage.raiseError("JZ0PA");
/*      */     }
/* 1402 */     return this._currentRS;
/*      */   }
/*      */ 
/*      */   public int getUpdateCount()
/*      */     throws SQLException
/*      */   {
/* 1410 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1412 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1414 */       LOG.fine(this._logId + " getUpdateCount()");
/*      */     }
/*      */ 
/* 1419 */     checkDead();
/*      */ 
/* 1421 */     if (this._context.getState() == 5)
/*      */     {
/* 1423 */       ErrorMessage.raiseError("JZ0PA");
/*      */     }
/* 1425 */     if (!this._validRowcount)
/*      */     {
/* 1427 */       return -1;
/*      */     }
/* 1429 */     int i = this._rowcount;
/* 1430 */     resetRowCount();
/* 1431 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean getMoreResults()
/*      */     throws SQLException
/*      */   {
/* 1439 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1441 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1443 */       LOG.fine(this._logId + " getMoreResults()");
/*      */     }
/*      */ 
/* 1448 */     return getMoreResults(1);
/*      */   }
/*      */ 
/*      */   public boolean getMoreResults(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1456 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1458 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1460 */         LOG.finer(this._logId + " getMoreResults(int = [" + paramInt + "])");
/*      */       }
/* 1462 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1464 */         LOG.fine(this._logId + " getMoreResults(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1470 */     if ((paramInt != 1) && (paramInt != 2) && (paramInt != 3))
/*      */     {
/* 1473 */       ErrorMessage.raiseError("JZ0FP", "current");
/*      */     }
/* 1475 */     checkDead();
/* 1476 */     if (this._state == 1)
/*      */     {
/* 1479 */       this._validRowcount = true;
/* 1480 */       return false;
/*      */     }
/* 1482 */     if ((this._currentResult == 1) && (this._currentRS != null) && (this instanceof CallableStatement))
/*      */     {
/* 1485 */       this._currentRS._currentStatus = paramInt;
/*      */     }
/*      */ 
/* 1488 */     int i = nextResult();
/* 1489 */     switch (i)
/*      */     {
/*      */     case 1:
/*      */     case 6:
/* 1493 */       return true;
/*      */     }
/* 1495 */     return false;
/*      */   }
/*      */ 
/*      */   public void setFetchDirection(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1505 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1507 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1509 */         LOG.finer(this._logId + " setFetchDirection(int = [" + paramInt + "])");
/*      */       }
/* 1512 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1514 */         LOG.fine(this._logId + " setFetchDirection(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1525 */     switch (paramInt)
/*      */     {
/*      */     case 1000:
/*      */     case 1001:
/*      */     case 1002:
/* 1530 */       this._rsFetchDir = paramInt;
/* 1531 */       break;
/*      */     default:
/* 1533 */       ErrorMessage.raiseError("JZ0I3", String.valueOf(paramInt), "setFetchDirection(int)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getFetchDirection()
/*      */     throws SQLException
/*      */   {
/* 1540 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1542 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1544 */       LOG.fine(this._logId + " getFetchDirection()");
/*      */     }
/*      */ 
/* 1552 */     return this._rsFetchDir;
/*      */   }
/*      */ 
/*      */   public int getResultSetType()
/*      */     throws SQLException
/*      */   {
/* 1559 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1561 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1563 */       LOG.fine(this._logId + " getResultSetType()");
/*      */     }
/*      */ 
/* 1567 */     if (this._currentRS != null)
/*      */     {
/* 1569 */       return this._currentRS.getType();
/*      */     }
/*      */ 
/* 1573 */     return this._rsType;
/*      */   }
/*      */ 
/*      */   public void addBatch(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1589 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1591 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1593 */         LOG.finer(this._logId + " addBatch(String = [" + paramString + "])");
/*      */       }
/* 1595 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1597 */         LOG.fine(this._logId + " addBatch(String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1605 */     checkBatch();
/* 1606 */     if (this._batchCmds == null)
/*      */     {
/* 1609 */       this._batchCmds = new Vector();
/* 1610 */       this._batchCmdsCount = 0;
/*      */     }
/*      */ 
/* 1615 */     if (this._batchCmdsCount == this._maxBICounter)
/*      */     {
/* 1617 */       ErrorMessage.raiseError("JZ045", "" + (this._batchCmdsCount + 1), "" + this._maxBICounter);
/*      */     }
/*      */ 
/* 1622 */     this._batchCmds.add(paramString);
/* 1623 */     this._batchCmdsCount += 1;
/*      */   }
/*      */ 
/*      */   public void clearBatch()
/*      */     throws SQLException
/*      */   {
/* 1640 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1642 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1644 */       LOG.fine(this._logId + " clearBatch()");
/*      */     }
/*      */ 
/* 1653 */     checkBatch();
/* 1654 */     this._batchCmds = null;
/* 1655 */     this._batchCmdsCount = 0;
/*      */   }
/*      */ 
/*      */   public int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/* 1668 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1670 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1672 */       LOG.fine(this._logId + " executeBatch()");
/*      */     }
/*      */ 
/* 1676 */     return executeBatch(null, true);
/*      */   }
/*      */ 
/*      */   protected void setResultSetParams(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1711 */     int i = 0;
/*      */ 
/* 1713 */     switch (paramInt1)
/*      */     {
/*      */     case 1003:
/*      */     case 1004:
/* 1717 */       break;
/*      */     case 1005:
/* 1721 */       i = 1;
/* 1722 */       paramInt1 = 1004;
/* 1723 */       break;
/*      */     default:
/* 1726 */       ErrorMessage.raiseError("JZ0SP", String.valueOf(paramInt1));
/*      */     }
/*      */ 
/* 1731 */     switch (paramInt2)
/*      */     {
/*      */     case 1007:
/* 1734 */       break;
/*      */     case 1008:
/* 1737 */       if (paramInt1 == 1004)
/*      */       {
/* 1740 */         paramInt2 = 1007;
/* 1741 */         i = 1; } break;
/*      */     default:
/* 1745 */       ErrorMessage.raiseError("JZ0SO", String.valueOf(paramInt2));
/*      */     }
/*      */ 
/* 1751 */     this._rsType = paramInt1;
/* 1752 */     this._rsConcur = paramInt2;
/*      */ 
/* 1758 */     if (i == 0)
/*      */       return;
/* 1760 */     this._context._conn.chainWarnings(ErrorMessage.createWarning("010RC"));
/*      */   }
/*      */ 
/*      */   protected void setResultSetHoldability(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1769 */     if ((paramInt != 1) && (paramInt != 2))
/*      */     {
/* 1772 */       ErrorMessage.raiseError("JZ0SW", String.valueOf(paramInt));
/*      */     }
/*      */ 
/* 1776 */     if (paramInt == 2)
/*      */     {
/* 1778 */       this._context._conn._stmtList.addElement(this);
/*      */     }
/* 1780 */     this._rsHold = paramInt;
/*      */   }
/*      */ 
/*      */   protected int[] executeBatch(ParamManager paramParamManager) throws SQLException
/*      */   {
/* 1785 */     return executeBatch(paramParamManager, true);
/*      */   }
/*      */ 
/*      */   protected int[] executeBatch(ParamManager paramParamManager, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 1815 */     if (this._batchCmds == null)
/*      */     {
/* 1823 */       return new int[0];
/*      */     }
/* 1825 */     int[] arrayOfInt1 = new int[this._batchCmds.size()];
/*      */     try
/*      */     {
/* 1829 */       if (checkBatch())
/*      */       {
/* 1839 */         if (paramParamManager != null)
/*      */         {
/* 1844 */           return sendBatch((String)this._batchCmds.elementAt(0), paramParamManager);
/*      */         }
/*      */ 
/* 1848 */         for (int i = 0; ; ++i) { if (i >= this._batchCmds.size())
/*      */             break label128;
/*      */           try
/*      */           {
/* 1852 */             int[] arrayOfInt3 = sendBatch((String)this._batchCmds.elementAt(i), paramParamManager);
/*      */ 
/* 1854 */             arrayOfInt1[i] = arrayOfInt3[0];
/*      */           }
/*      */           catch (SQLException localSQLException2)
/*      */           {
/* 1860 */             handleBatchException(localSQLException2, arrayOfInt1, i);
/*      */           }
/*      */  }
/*      */ 
/*      */ 
/*      */       }
/*      */ 
/* 1867 */       label128: arrayOfInt1 = sendBatch(batchToString(), paramParamManager);
/*      */     }
/*      */     catch (SQLException localSQLException1)
/*      */     {
/* 1872 */       if (!checkBatch())
/*      */       {
/* 1874 */         clearBatch();
/*      */       }
/* 1876 */       this._context._batch = false;
/* 1877 */       throw localSQLException1;
/*      */     }
/*      */     finally
/*      */     {
/* 1881 */       if (paramBoolean)
/*      */       {
/* 1883 */         if (!checkBatch())
/*      */         {
/* 1885 */           clearBatch();
/*      */         }
/* 1887 */         this._context._batch = false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1893 */     return arrayOfInt1;
/*      */   }
/*      */ 
/*      */   protected int[] sendBatch(String paramString, ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 1905 */     String str = doEscapeProcessing(paramString);
/* 1906 */     this._context._batch = true;
/* 1907 */     checkStatement(true);
/* 1908 */     sendQuery(str, paramParamManager);
/* 1909 */     return batchLoop(paramParamManager);
/*      */   }
/*      */ 
/*      */   protected int[] batchLoop(ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 1925 */     int i = 0;
/*      */ 
/* 1927 */     this._context._batch = true;
/*      */ 
/* 1930 */     int[] arrayOfInt1 = new int[this._batchCmdsCount];
/* 1931 */     for (int k = 0; k < arrayOfInt1.length; ++k)
/*      */     {
/* 1938 */       if ((paramParamManager != null) && (paramParamManager._rowUpdatedStatus != null))
/*      */       {
/* 1940 */         arrayOfInt1[k] = paramParamManager._rowUpdatedStatus[k];
/*      */       }
/*      */       else
/*      */       {
/* 1944 */         arrayOfInt1[k] = 0;
/*      */       }
/*      */     }
/*      */ 
/* 1948 */     k = 0;
/*      */ 
/* 1951 */     int l = 5;
/* 1952 */     boolean bool = this._context._conn._props.getBoolean(80);
/*      */     do
/*      */     {
/*      */       do
/*      */       {
/* 1957 */         if (l != 3)
/*      */         {
/* 1959 */           l = 5;
/* 1960 */           i = 0;
/*      */         }
/*      */         label380: 
/*      */         while (true) {
/*      */           try
/*      */           {
/*      */             while (true) {
/* 1967 */               int j = nextResult(paramParamManager);
/* 1968 */               switch (j)
/*      */               {
/*      */               case 5:
/* 1978 */                 if (this._doneinproc)
/*      */                 {
/* 1980 */                   TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)this._context;
/*      */ 
/* 1983 */                   if (localTdsProtocolContext.isProcDone())
/*      */                   {
/* 1985 */                     i += this._rowcount;
/* 1986 */                     l = 5;
/* 1987 */                     break label380:
/*      */                   }
/*      */ 
/* 1990 */                   i += this._rowcount;
/*      */                 }
/*      */ 
/* 2004 */                 i += this._rowcount;
/* 2005 */                 break;
/*      */               case 0:
/* 2012 */                 l = 0;
/* 2013 */                 break;
/*      */               case 3:
/* 2015 */                 if (this._doneinproc)
/*      */                 {
/* 2017 */                   l = 3;
/*      */ 
/* 2025 */                   break label380:
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 2040 */             ErrorMessage.raiseError("JZ0P1");
/*      */           }
/*      */           catch (SQLException localSQLException1)
/*      */           {
/* 2072 */             if ("JZ0F2".equals(localSQLException1.getSQLState()))
/*      */             {
/* 2074 */               doCancel(false, true);
/*      */ 
/* 2076 */               throw localSQLException1;
/*      */             }
/* 2078 */             if (bool)
/*      */             {
/* 2080 */               k = skipEarlyRowFailures(k, paramParamManager);
/*      */ 
/* 2082 */               if (this._storedBatchSQE != null)
/*      */               {
/* 2084 */                 localSQLException1.setNextException(this._storedBatchSQE);
/*      */               }
/* 2086 */               this._storedBatchSQE = localSQLException1;
/*      */ 
/* 2094 */               arrayOfInt1[k] = -3;
/*      */             }
/*      */ 
/*      */             try
/*      */             {
/* 2101 */               doCancel(false, false);
/*      */             }
/*      */             catch (SQLException localSQLException3)
/*      */             {
/* 2108 */               if (localSQLException3.getSQLState().equals("JZ0F2"))
/*      */               {
/* 2110 */                 localSQLException3.setNextException(localSQLException1);
/*      */               }
/* 2112 */               throw localSQLException3;
/*      */             }
/* 2114 */             int[] arrayOfInt2 = new int[k];
/* 2115 */             for (int i1 = 0; i1 < k; ++i1)
/*      */             {
/* 2117 */               arrayOfInt2[i1] = arrayOfInt1[i1];
/*      */             }
/* 2119 */             ErrorMessage.raiseBatchUpdateException(localSQLException1, arrayOfInt2);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2125 */       while (l == 3);
/*      */ 
/* 2127 */       if (bool)
/*      */       {
/* 2129 */         k = skipEarlyRowFailures(k, paramParamManager);
/*      */       }
/* 2131 */       if (k >= arrayOfInt1.length)
/*      */         continue;
/* 2133 */       if (arrayOfInt1[k] != -3)
/*      */       {
/* 2135 */         arrayOfInt1[k] = i;
/*      */       }
/* 2137 */       ++k;
/*      */     }
/*      */ 
/* 2144 */     while (l != 0);
/*      */ 
/* 2152 */     if (this._storedBatchSQE != null)
/*      */     {
/* 2154 */       while (k < arrayOfInt1.length)
/*      */       {
/* 2156 */         arrayOfInt1[(k++)] = -3;
/*      */       }
/*      */ 
/* 2160 */       if (paramParamManager != null)
/*      */       {
/* 2162 */         paramParamManager._rowException = null;
/* 2163 */         paramParamManager._rowUpdatedStatus = null;
/*      */       }
/* 2165 */       SQLException localSQLException2 = this._storedBatchSQE;
/* 2166 */       this._storedBatchSQE = null;
/* 2167 */       ErrorMessage.raiseBatchUpdateException(localSQLException2, arrayOfInt1);
/*      */     }
/*      */ 
/* 2170 */     return arrayOfInt1;
/*      */   }
/*      */ 
/*      */   private int skipEarlyRowFailures(int paramInt, ParamManager paramParamManager)
/*      */   {
/* 2182 */     while ((paramParamManager != null) && (paramParamManager._rowUpdatedStatus != null) && 
/* 2185 */       (paramInt < paramParamManager._rowUpdatedStatus.length) && (paramParamManager._rowUpdatedStatus[paramInt] == -3))
/*      */     {
/* 2187 */       if (this._storedBatchSQE == null)
/*      */       {
/* 2189 */         this._storedBatchSQE = paramParamManager._rowException[paramInt];
/*      */       }
/*      */       else
/*      */       {
/* 2193 */         this._storedBatchSQE.setNextException(paramParamManager._rowException[paramInt]);
/*      */       }
/* 2195 */       ++paramInt;
/*      */     }
/*      */ 
/* 2198 */     return paramInt;
/*      */   }
/*      */ 
/*      */   protected String batchToString()
/*      */   {
/* 2213 */     int i = this._batchCmds.size() * (((String)this._batchCmds.elementAt(this._batchCmds.size() / 2)).length() + 1);
/*      */ 
/* 2220 */     StringWriter localStringWriter = new StringWriter(i);
/*      */ 
/* 2222 */     localStringWriter.write((String)this._batchCmds.elementAt(0));
/*      */ 
/* 2225 */     for (int j = 1; j < this._batchCmds.size(); ++j)
/*      */     {
/* 2227 */       localStringWriter.write(" ");
/* 2228 */       localStringWriter.write((String)this._batchCmds.elementAt(j));
/*      */     }
/* 2230 */     String str = localStringWriter.toString();
/*      */ 
/* 2233 */     return str;
/*      */   }
/*      */ 
/*      */   protected boolean sendQuery(String paramString, ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 2248 */     if ((this._type == 4100) || (this._type == 4104))
/*      */     {
/* 2252 */       paramString.trim();
/* 2253 */       int i = paramString.lastIndexOf(' ');
/* 2254 */       String str = paramString.substring(i + 1);
/*      */ 
/* 2257 */       SybCursorResultSet localSybCursorResultSet = (SybCursorResultSet)this._context._conn.getCursorResultSet(str);
/*      */ 
/* 2263 */       if ((localSybCursorResultSet != null) && (!localSybCursorResultSet._cursor.isLanguageCursor()))
/*      */       {
/* 2265 */         localSybCursorResultSet._cursor.setTable(this._table);
/* 2266 */         if (this._type == 4100)
/*      */         {
/* 2268 */           localSybCursorResultSet.deleteRow();
/*      */         }
/*      */         else
/*      */         {
/* 2274 */           localSybCursorResultSet._cursor.update(localSybCursorResultSet._prs, paramParamManager, paramString.substring(0, this._setEnd));
/*      */         }
/*      */ 
/* 2281 */         return true;
/*      */       }
/*      */ 
/* 2287 */       if (paramParamManager != null)
/*      */       {
/* 2289 */         paramParamManager.literalizeAll();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 2296 */       this._protocol.language(this._context, paramString, paramParamManager);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2300 */       handleSQLE(localSQLException);
/*      */     }
/* 2302 */     this._state = 3;
/* 2303 */     return false;
/*      */   }
/*      */ 
/*      */   protected ResultSet executeQuery(String paramString, ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 2319 */     checkStatement(true);
/*      */ 
/* 2323 */     if (this._rsConcur == -9)
/*      */     {
/* 2325 */       this._rsConcur = 1007;
/*      */     }
/*      */ 
/* 2332 */     if ((this._cursor == null) && (this._rsConcur == 1008))
/*      */     {
/* 2336 */       checkCursor(false, 1008);
/*      */     }
/*      */ 
/* 2340 */     checkForImplicitCursor();
/*      */ 
/* 2342 */     if (this._cursor != null)
/*      */     {
/* 2344 */       this._cursor.setTable(this._table);
/* 2345 */       ProtocolResultSet localProtocolResultSet = null;
/* 2346 */       if ((this instanceof SybPreparedStatement) && (((SybPreparedStatement)this)._dynStmtName != null))
/*      */       {
/* 2349 */         this._cursor.setDynamic(true);
/* 2350 */         paramParamManager = ((SybPreparedStatement)this).setParamsFromArrays();
/* 2351 */         localProtocolResultSet = this._cursor.open(((SybPreparedStatement)this)._dynStmtName, paramParamManager, false);
/*      */       }
/*      */       else
/*      */       {
/* 2356 */         localProtocolResultSet = this._cursor.open(paramString, paramParamManager, false);
/*      */       }
/* 2358 */       if (localProtocolResultSet == null)
/*      */       {
/* 2360 */         ErrorMessage.raiseError("JZ0R2");
/*      */       }
/*      */ 
/* 2363 */       localProtocolResultSet.setType(this._rsType);
/*      */ 
/* 2365 */       if (this._cursor.scrollingAtServer())
/*      */       {
/* 2367 */         this._currentRS = new SybScrollCursorResultSet(this._logId, this, localProtocolResultSet);
/*      */       }
/*      */       else
/*      */       {
/* 2371 */         this._currentRS = new SybCursorResultSet(this._logId, this, localProtocolResultSet);
/*      */       }
/* 2373 */       this._context._conn.addCursorResultSet(this._cursor.getName(), this._currentRS);
/*      */ 
/* 2375 */       this._state = 3;
/*      */     }
/*      */     else
/*      */     {
/* 2379 */       sendQuery(paramString, paramParamManager);
/* 2380 */       queryLoop();
/*      */     }
/* 2382 */     return this._currentRS;
/*      */   }
/*      */ 
/*      */   protected ResultSet queryLoop()
/*      */     throws SQLException
/*      */   {
/* 2389 */     int i = 1;
/*      */     while (true)
/*      */       try
/*      */       {
/*      */         while (true) {
/* 2394 */           int j = nextResult();
/* 2395 */           switch (j)
/*      */           {
/*      */           case 1:
/* 2399 */             return this._currentRS;
/*      */           case 5:
/* 2401 */             break;
/*      */           case 0:
/* 2408 */             i = 0;
/*      */ 
/* 2410 */             ErrorMessage.raiseError("JZ0R2");
/*      */           case 3:
/*      */           case 2:
/*      */           case 4:
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2417 */         ErrorMessage.raiseError("JZ0P1");
/*      */       }
/*      */       catch (SQLException localSQLException1)
/*      */       {
/* 2425 */         if ((i != 0) && (!"JZ0T4".equals(localSQLException1.getSQLState())) && (!this._context._conn.isDead()))
/*      */         {
/*      */           try
/*      */           {
/* 2441 */             if ("JZ0F2".equals(localSQLException1.getSQLState()))
/*      */             {
/* 2443 */               doCancel(false, true);
/*      */             }
/*      */             else
/*      */             {
/* 2447 */               doCancel(false, false);
/*      */             }
/*      */ 
/*      */           }
/*      */           catch (SQLException localSQLException2)
/*      */           {
/* 2455 */             if (localSQLException2.getSQLState().equals("JZ0F2"))
/*      */             {
/* 2457 */               localSQLException2.setNextException(localSQLException1);
/* 2458 */               throw localSQLException2;
/*      */             }
/* 2460 */             localSQLException1.setNextException(localSQLException2);
/*      */           }
/*      */         }
/* 2463 */         throw localSQLException1;
/*      */       }
/*      */   }
/*      */ 
/*      */   protected int executeUpdate(String paramString, ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 2479 */     checkStatement(true);
/* 2480 */     if (sendQuery(paramString, paramParamManager))
/*      */     {
/* 2482 */       return 1;
/*      */     }
/* 2484 */     return updateLoop();
/*      */   }
/*      */ 
/*      */   protected int updateLoop()
/*      */     throws SQLException
/*      */   {
/* 2493 */     int i = 0;
/*      */     try
/*      */     {
/*      */       while (true)
/*      */       {
/* 2500 */         int j = nextResult();
/* 2501 */         switch (j)
/*      */         {
/*      */         case 5:
/* 2504 */           i = this._rowcount;
/* 2505 */           break;
/*      */         case 0:
/* 2507 */           this._rowcount = i;
/* 2508 */           break;
/*      */         case 3:
/* 2510 */           if (!this instanceof CallableStatement)
/*      */             continue;
/* 2512 */           TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)this._context;
/* 2513 */           setRowCount(localTdsProtocolContext.getPreviousCount());
/* 2514 */           i = this._rowcount;
/*      */ 
/* 2516 */           break;
/*      */         case 1:
/* 2525 */           if (this._retGeneratedKeys)
/*      */             break;
/*      */         case 2:
/*      */         case 4:
/*      */         default:
/* 2531 */           ErrorMessage.raiseError("JZ0P1");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2542 */       boolean bool = false;
/* 2543 */       if ("JZ0F2".equals(localSQLException.getSQLState()))
/*      */       {
/* 2545 */         bool = true;
/*      */       }
/* 2547 */       doCancel(false, bool);
/* 2548 */       throw localSQLException;
/*      */     }
/* 2550 */     return i;
/*      */   }
/*      */ 
/*      */   protected boolean execute(String paramString, ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 2564 */     checkStatement(true);
/* 2565 */     sendQuery(paramString, paramParamManager);
/* 2566 */     boolean bool = executeLoop();
/* 2567 */     if ((this._retGeneratedKeys) && (this._executionMode != 2))
/*      */     {
/* 2569 */       nextResult();
/* 2570 */       this._retGeneratedKeys = false;
/*      */     }
/* 2572 */     return bool;
/*      */   }
/*      */ 
/*      */   protected boolean executeLoop()
/*      */     throws SQLException
/*      */   {
/* 2579 */     int i = nextResult();
/* 2580 */     switch (i)
/*      */     {
/*      */     case 0:
/*      */     case 1:
/*      */     case 5:
/* 2585 */       break;
/*      */     case 3:
/* 2587 */       TdsProtocolContext localTdsProtocolContext = (TdsProtocolContext)this._context;
/* 2588 */       setRowCount(localTdsProtocolContext.getPreviousCount());
/* 2589 */       break;
/*      */     case 2:
/*      */     case 4:
/*      */     default:
/* 2591 */       ErrorMessage.raiseError("JZ0P1");
/*      */     }
/*      */ 
/* 2594 */     return this._currentRS != null;
/*      */   }
/*      */ 
/*      */   public void setSybMessageHandler(SybMessageHandler paramSybMessageHandler)
/*      */   {
/* 2607 */     this._context.setMessageHandler(paramSybMessageHandler);
/*      */   }
/*      */ 
/*      */   public SybMessageHandler getSybMessageHandler()
/*      */   {
/* 2618 */     return this._context.getMessageHandler();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public SybMessageHandler getMessageHandler()
/*      */   {
/* 2631 */     return getSybMessageHandler();
/*      */   }
/*      */ 
/*      */   protected void handleSQLE(SQLException paramSQLException)
/*      */     throws SQLException
/*      */   {
/* 2638 */     boolean bool = SybConnection.thisChainHasAnException(paramSQLException);
/* 2639 */     SQLWarning localSQLWarning = null;
/* 2640 */     if (!bool)
/*      */     {
/* 2642 */       localSQLWarning = (SQLWarning)paramSQLException;
/*      */     }
/* 2646 */     else if (this._context._conn._props.getBoolean(76))
/*      */     {
/* 2649 */       paramSQLException = SybConnection.getAllExceptions(paramSQLException);
/*      */     }
/*      */     else
/*      */     {
/* 2654 */       localSQLWarning = SybConnection.getAllTheWarnings(paramSQLException);
/*      */     }
/*      */ 
/* 2657 */     if (localSQLWarning != null)
/*      */     {
/* 2662 */       if (this._warning == null)
/*      */       {
/* 2664 */         this._warning = localSQLWarning;
/*      */       }
/*      */       else
/*      */       {
/* 2668 */         this._warning.setNextWarning(localSQLWarning);
/*      */       }
/*      */     }
/* 2671 */     if (!bool)
/*      */       return;
/* 2673 */     throw paramSQLException;
/*      */   }
/*      */ 
/*      */   protected void chainWarning(SQLWarning paramSQLWarning)
/*      */   {
/* 2681 */     if (paramSQLWarning == null) {
/*      */       return;
/*      */     }
/* 2684 */     if (this._warning == null)
/*      */     {
/* 2686 */       this._warning = paramSQLWarning;
/*      */     }
/*      */     else
/*      */     {
/* 2690 */       this._warning.setNextWarning(paramSQLWarning);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void handleBatchException(SQLException paramSQLException, int[] paramArrayOfInt, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2699 */     if (paramSQLException instanceof BatchUpdateException)
/*      */     {
/* 2710 */       int[] arrayOfInt1 = ((BatchUpdateException)paramSQLException).getUpdateCounts();
/*      */ 
/* 2712 */       for (int i = 0; i < arrayOfInt1.length; ++i)
/*      */       {
/* 2714 */         paramArrayOfInt[paramInt] = arrayOfInt1[i];
/* 2715 */         ++paramInt;
/*      */       }
/*      */ 
/* 2723 */       int[] arrayOfInt2 = new int[paramInt];
/* 2724 */       System.arraycopy(paramArrayOfInt, 0, arrayOfInt2, 0, paramInt);
/*      */ 
/* 2727 */       if (paramSQLException instanceof SybBatchUpdateException)
/*      */       {
/* 2733 */         SQLException localSQLException = ((SybBatchUpdateException)paramSQLException).getOrigSQE();
/*      */ 
/* 2735 */         ErrorMessage.raiseBatchUpdateException(localSQLException, arrayOfInt2);
/*      */       }
/*      */       else
/*      */       {
/* 2748 */         ErrorMessage.raiseBatchUpdateException(paramSQLException, arrayOfInt2);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 2754 */       throw paramSQLException;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkStatement(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 2761 */     checkDead();
/* 2762 */     if ((this._cursor != null) && (this._currentRS != null) && (this._currentRS._state == 3) && 
/* 2765 */       (this._cursor.isLanguageCursor()))
/*      */     {
/* 2767 */       ErrorMessage.raiseError("JZ00E");
/*      */     }
/*      */ 
/* 2770 */     if (this._closing) return;
/* 2771 */     if (paramBoolean)
/*      */     {
/* 2774 */       this._warning = null;
/* 2775 */       doCancel(false, false);
/*      */ 
/* 2777 */       this._state = 3;
/*      */     } else {
/* 2779 */       if (this._state == 3) {
/*      */         return;
/*      */       }
/* 2782 */       ErrorMessage.raiseError("JZ0S1");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkDead()
/*      */     throws SQLException
/*      */   {
/* 2789 */     if (this._state == 2)
/*      */     {
/* 2791 */       ErrorMessage.raiseError("JZ0S2");
/*      */     }
/* 2793 */     this._context._conn.checkConnection();
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/* 2800 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2802 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2804 */       LOG.fine(this._logId + " isClosed()");
/*      */     }
/*      */ 
/* 2808 */     return this._closing;
/*      */   }
/*      */ 
/*      */   protected void handleParam(ParamManager paramParamManager)
/*      */     throws SQLException
/*      */   {
/* 2818 */     if (this._rowcount == -1)
/*      */     {
/* 2823 */       setRowCount(0);
/*      */     }
/*      */ 
/* 2826 */     if (!this._retGeneratedKeys)
/*      */     {
/* 2828 */       resetRowCount();
/*      */     }
/*      */ 
/*      */     do
/*      */     {
/*      */       try
/*      */       {
/* 2835 */         this._currentResult = this._resultGetter.nextResult();
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 2839 */         handleSQLE(localSQLException);
/*      */       }
/*      */     }
/* 2841 */     while (this._currentResult == 3);
/*      */ 
/* 2843 */     processResults((ParamManager)null);
/*      */   }
/*      */ 
/*      */   protected void setRowCount(int paramInt)
/*      */   {
/* 2851 */     this._rowcount = paramInt;
/*      */   }
/*      */ 
/*      */   private void resetRowCount()
/*      */   {
/* 2856 */     this._validRowcount = false;
/* 2857 */     this._rowcount = -1;
/*      */   }
/*      */ 
/*      */   protected String handleCallBody(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2867 */     StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString);
/* 2868 */     String str1 = paramString;
/* 2869 */     int i = 0;
/* 2870 */     String str2 = null;
/*      */ 
/* 2876 */     this._allowsOutputParms = true;
/*      */ 
/* 2880 */     this._sendAsRpc = true;
/*      */     try
/*      */     {
/* 2884 */       this._rpcName = localStringTokenizer1.nextToken("(), \t\n\r");
/* 2885 */       int j = this._rpcName.length();
/* 2886 */       if (this._rpcName.toLowerCase().equals("exec"))
/*      */       {
/* 2888 */         i = 1;
/*      */ 
/* 2892 */         this._rpcName = localStringTokenizer1.nextToken("(), \t\n\r");
/* 2893 */         int k = paramString.indexOf(this._rpcName);
/* 2894 */         str2 = paramString.substring(0, k);
/* 2895 */         str1 = paramString.substring(k);
/* 2896 */         j += this._rpcName.length();
/*      */       }
/*      */ 
/* 2902 */       StringTokenizer localStringTokenizer2 = new StringTokenizer(paramString.substring(paramString.indexOf(this._rpcName) + this._rpcName.length()));
/*      */ 
/* 2904 */       String str3 = localStringTokenizer2.nextToken(" ");
/* 2905 */       int l = paramString.lastIndexOf(')');
/*      */ 
/* 2908 */       if ((str3.startsWith("(")) && (l > 0))
/*      */       {
/* 2910 */         str1 = this._rpcName + " " + paramString.substring(paramString.indexOf('(') + 1, l);
/*      */       }
/*      */ 
/*      */       String str4;
/*      */       do
/*      */       {
/* 2919 */         str4 = localStringTokenizer1.nextToken("(), \t\n\r");
/* 2920 */       }while (str4.charAt(0) == '?');
/*      */ 
/* 2924 */       this._sendAsRpc = false;
/*      */     }
/*      */     catch (NoSuchElementException localNoSuchElementException)
/*      */     {
/*      */     }
/*      */ 
/* 2934 */     if ((i != 0) && 
/* 2939 */       (!this._protocol.getBoolOption(this._context, 12)))
/*      */     {
/* 2943 */       str1 = str2 + str1;
/*      */     }
/*      */ 
/* 2947 */     this._executionMode = ((this._sendAsRpc) ? 3 : 1);
/* 2948 */     return str1;
/*      */   }
/*      */ 
/*      */   protected void deallocateDynamic()
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void checkCursor(boolean paramBoolean, int paramInt)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2971 */       if (this._cursor == null)
/*      */       {
/* 2973 */         this._cursor = this._protocol.getCursor(this._context, paramBoolean);
/* 2974 */         this._cursor.setFetchSize(this._context._conn._props.getInteger(16));
/*      */       }
/*      */ 
/* 2989 */       if ((paramInt != 1007) || (this._rsConcur != 1008))
/*      */       {
/* 2997 */         this._rsConcur = paramInt;
/*      */       }
/* 2999 */       this._cursor.setTypeAndConcurrency(this._rsType, this._rsConcur);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 3003 */       handleSQLE(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean checkBatch()
/*      */     throws SQLException
/*      */   {
/* 3022 */     boolean bool1 = false;
/* 3023 */     boolean bool2 = false;
/*      */     try
/*      */     {
/* 3027 */       if (this._dbmda == null)
/*      */       {
/* 3029 */         this._dbmda = ((SybDatabaseMetaData)this._context._conn.getMetaData());
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 3036 */       return false;
/*      */     }
/*      */ 
/* 3039 */     bool2 = this._dbmda.supportsBatchUpdates();
/* 3040 */     if (bool2)
/*      */     {
/* 3042 */       bool1 = this._dbmda.execBatchUpdatesInLoop();
/*      */     }
/*      */ 
/* 3049 */     return bool1;
/*      */   }
/*      */ 
/*      */   protected void checkForImplicitCursor()
/*      */     throws SQLException
/*      */   {
/* 3090 */     if (this._cursor != null)
/*      */     {
/* 3095 */       return;
/*      */     }
/*      */ 
/* 3098 */     if ((!this._context._conn._props.getBoolean(33)) || (this._context._conn._props.getBoolean(27)))
/*      */     {
/*      */       return;
/*      */     }
/*      */ 
/* 3105 */     int i = this._context._conn._props.getInteger(47);
/*      */ 
/* 3107 */     if (i <= 0)
/*      */       return;
/* 3109 */     int j = getMaxRows();
/*      */ 
/* 3112 */     if ((j > 0) && (i > j))
/*      */     {
/* 3117 */       i = j;
/*      */     }
/*      */ 
/* 3127 */     setFetchSize(i);
/*      */   }
/*      */ 
/*      */   protected String processGenKeysRequest(int paramInt, String paramString)
/*      */     throws SQLException
/*      */   {
/* 3135 */     if ((paramInt != 1) && (paramInt != 2))
/*      */     {
/* 3138 */       ErrorMessage.raiseError("JZ0FP", "autoGeneratedKeys");
/*      */     }
/*      */ 
/* 3141 */     this._retGeneratedKeys = ((this._type == 16) && (paramInt == 1));
/*      */ 
/* 3143 */     if (this._retGeneratedKeys)
/*      */     {
/* 3145 */       if (this._dbmda == null)
/*      */       {
/* 3147 */         this._dbmda = ((SybDatabaseMetaData)this._context._conn.getMetaData());
/*      */       }
/* 3149 */       paramString = paramString + " " + this._dbmda.getIdentityQuery();
/*      */     }
/* 3151 */     return paramString;
/*      */   }
/*      */ 
/*      */   public boolean isWrapperFor(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 3159 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3161 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3163 */         LOG.finest(LogUtil.logMethod(false, this._logId, " isWrapperFor", new Object[] { paramClass }));
/*      */       }
/* 3166 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3168 */         LOG.finer(LogUtil.logMethod(true, this._logId, " isWrapperFor", new Object[] { paramClass }));
/*      */       }
/* 3171 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3173 */         LOG.fine(this._logId + " isWrapperFor(Class<?>)");
/*      */       }
/*      */     }
/*      */ 
/* 3177 */     return paramClass.isInstance(this);
/*      */   }
/*      */ 
/*      */   public Object unwrap(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 3185 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3187 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 3189 */         LOG.finest(LogUtil.logMethod(false, this._logId, " unwrap", new Object[] { paramClass }));
/*      */       }
/* 3192 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3194 */         LOG.finer(LogUtil.logMethod(true, this._logId, " unWrap", new Object[] { paramClass }));
/*      */       }
/* 3197 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3199 */         LOG.fine(this._logId + " unWrap(Class<T>)");
/*      */       }
/*      */     }
/* 3202 */     SybStatement localSybStatement = null;
/*      */     try
/*      */     {
/* 3205 */       localSybStatement = this;
/*      */     }
/*      */     catch (ClassCastException localClassCastException)
/*      */     {
/* 3209 */       ErrorMessage.raiseError("JZ031", paramClass.getName());
/*      */     }
/*      */ 
/* 3212 */     return localSybStatement;
/*      */   }
/*      */ 
/*      */   public void setPoolable(boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 3221 */     if (!LogUtil.isLoggingEnabled(LOG))
/*      */       return;
/* 3223 */     if (LOG.isLoggable(Level.FINER))
/*      */     {
/* 3225 */       LOG.finer(this._logId + " setPoolable(boolean = [" + paramBoolean + "])");
/*      */     }
/*      */     else {
/* 3228 */       if (!LOG.isLoggable(Level.FINE))
/*      */         return;
/* 3230 */       LOG.fine(this._logId + " setPoolable(boolean)");
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isPoolable()
/*      */     throws SQLException
/*      */   {
/* 3241 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3243 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3245 */       LOG.fine(this._logId + " isPoolable()");
/*      */     }
/*      */ 
/* 3249 */     return false;
/*      */   }
/*      */ 
/*      */   protected final byte getExecutionMode()
/*      */   {
/* 3257 */     return this._executionMode;
/*      */   }
/*      */ 
/*      */   protected final void setExecutionMode(byte paramByte)
/*      */   {
/* 3265 */     this._executionMode = paramByte;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybStatement
 * JD-Core Version:    0.5.4
 */