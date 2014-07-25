/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.utils.LogUtil;
/*      */ import com.sybase.jdbc3.utils.SybVersion;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ public class SybDatabaseMetaData
/*      */   implements DatabaseMetaData
/*      */ {
/*   44 */   private static Logger LOG = Logger.getLogger(SybDatabaseMetaData.class.getName());
/*   45 */   private static volatile long _logIdCounter = 0L;
/*      */ 
/*   47 */   private String _logId = null;
/*      */   private SybConnection _conn;
/*      */   private MdaManager _mda;
/*      */   private int _outerJoinEscapeSupport;
/*      */   private int _isCaseSensitive;
/*   53 */   private int _dbMajorVersion = -1;
/*   54 */   private int _dbMinorVersion = -1;
/*   55 */   private final int _jdbcMajorVersion = 3;
/*   56 */   private int _jdbcMinorVersion = -1;
/*      */   protected static final int UNKNOWN = -1;
/*      */   protected static final int NO = 0;
/*      */   protected static final int YES = 1;
/*      */   private static final String JAVA_OBJECT_NAME = "JAVA_OBJECT";
/*      */   private static final String STRUCT_NAME = "STRUCT";
/*      */   private static final String DISTINCT_NAME = "DISTINCT";
/*      */   private static final String TABLES = "TABLES";
/*      */   private static final String COLUMNS = "COLUMNS";
/*      */   private static final String ALLPROCSCALLABLE = "ALLPROCSCALLABLE";
/*      */   private static final String ALLTABLESSELECTABLE = "ALLTABLESSELECTABLE";
/*      */   private static final String ISREADONLY = "ISREADONLY";
/*      */   private static final String NULLSORTING = "NULLSORTING";
/*      */   private static final String USERNAME = "USERNAME";
/*      */   private static final String PRODUCTNAME = "PRODUCTNAME";
/*      */   private static final String GET_IDENTITY = "GET_IDENTITY";
/*      */   private static final String PRODUCTVERSION = "PRODUCTVERSION";
/*      */   private static final String FILEUSAGE = "FILEUSAGE";
/*      */   private static final String IDENTIFIERCASES = "IDENTIFIERCASES";
/*      */   private static final String IDENTIFIERQUOTE = "IDENTIFIERQUOTE";
/*      */   private static final String SQLKEYWORDS = "SQLKEYWORDS";
/*      */   private static final String NUMERICFUNCTIONLIST = "NUMERICFUNCTIONLIST";
/*      */   private static final String STRINGFUNCTIONLIST = "STRINGFUNCTIONLIST";
/*      */   private static final String SYSTEMFUNCTIONLIST = "SYSTEMFUNCTIONLIST";
/*      */   private static final String TIMEDATEFUNCTIONLIST = "TIMEDATEFUNCTIONLIST";
/*      */   private static final String SEARCHSTRING = "SEARCHSTRING";
/*      */   private static final String EXTRANAMECHARS = "EXTRANAMECHARS";
/*      */   private static final String ALTERTABLESUPPORT = "ALTERTABLESUPPORT";
/*      */   private static final String COLUMNALIASING = "COLUMNALIASING";
/*      */   private static final String NULLPLUSNONNULL = "NULLPLUSNONNULL";
/*      */   private static final String CONVERTSUPPORT = "CONVERTSUPPORT";
/*      */   private static final String CONVERTMAP = "CONVERTMAP";
/*      */   private static final String CORRELATIONNAMES = "CORRELATIONNAMES";
/*      */   private static final String ORDERBYSUPPORT = "ORDERBYSUPPORT";
/*      */   private static final String GROUPBYSUPPORT = "GROUPBYSUPPORT";
/*      */   private static final String LIKEESCAPECLAUSE = "LIKEESCAPECLAUSE";
/*      */   private static final String MULTIPLERESULTSETS = "MULTIPLERESULTSETS";
/*      */   private static final String MULTIPLETRANSACTIONS = "MULTIPLETRANSACTIONS";
/*      */   private static final String NONNULLABLECOLUMNS = "NONNULLABLECOLUMNS";
/*      */   private static final String SQLGRAMMAR = "SQLGRAMMAR";
/*      */   private static final String ANSI92LEVEL = "ANSI92LEVEL";
/*      */   private static final String INTEGRITYENHANCEMENT = "INTEGRITYENHANCEMENT";
/*      */   private static final String OUTERJOINS = "OUTERJOINS";
/*      */   private static final String SCHEMATERM = "SCHEMATERM";
/*      */   private static final String PROCEDURETERM = "PROCEDURETERM";
/*      */   private static final String CATALOGTERM = "CATALOGTERM";
/*      */   private static final String CATALOGSEPARATOR = "CATALOGSEPARATOR";
/*      */   private static final String CATALOGATSTART = "CATALOGATSTART";
/*      */   private static final String SCHEMASUPPORT = "SCHEMASUPPORT";
/*      */   private static final String CATALOGSUPPORT = "CATALOGSUPPORT";
/*      */   private static final String POSITIONEDDELETE = "POSITIONEDDELETE";
/*      */   private static final String POSITIONEDUPDATE = "POSITIONEDUPDATE";
/*      */   private static final String SELECTFORUPDATE = "SELECTFORUPDATE";
/*      */   private static final String STOREDPROCEDURES = "STOREDPROCEDURES";
/*      */   private static final String SUBQUERIES = "SUBQUERIES";
/*      */   private static final String UNIONSUPPORT = "UNIONSUPPORT";
/*      */   private static final String CURSORTRANSACTIONS = "CURSORTRANSACTIONS";
/*      */   private static final String STATEMENTTRANSACTIONS = "STATEMENTTRANSACTIONS";
/*      */   private static final String MAXBINARYLITERALLENGTH = "MAXBINARYLITERALLENGTH";
/*      */   private static final String MAXCHARLITERALLENGTH = "MAXCHARLITERALLENGTH";
/*      */   private static final String COLUMNINFO = "COLUMNINFO";
/*      */   private static final String MAXCONNECTIONS = "MAXCONNECTIONS";
/*      */   private static final String MAXINDEXLENGTH = "MAXINDEXLENGTH";
/*      */   private static final String MAXNAMELENGTHS = "MAXNAMELENGTHS";
/*      */   private static final String ROWINFO = "ROWINFO";
/*      */   private static final String STATEMENTINFO = "STATEMENTINFO";
/*      */   private static final String TABLEINFO = "TABLEINFO";
/*      */   private static final String TRANSACTIONSUPPORT = "TRANSACTIONSUPPORT";
/*      */   private static final String TRANSACTIONLEVELDEFAULT = "TRANSACTIONLEVELDEFAULT";
/*      */   private static final String TRANSACTIONLEVELS = "TRANSACTIONLEVELS";
/*      */   private static final String TRANSACTIONDATADEFINFO = "TRANSACTIONDATADEFINFO";
/*      */   private static final String PROCEDURES = "PROCEDURES";
/*      */   private static final String FUNCTIONS = "FUNCTIONS";
/*      */   private static final String PROCEDURECOLUMNS = "PROCEDURECOLUMNS";
/*      */   private static final String FUNCTIONCOLUMNS = "FUNCTIONCOLUMNS";
/*      */   private static final String SCHEMAS = "SCHEMAS";
/*      */   private static final String SCHEMAS_CTS = "SCHEMAS_CTS";
/*      */   private static final String CATALOGS = "CATALOGS";
/*      */   private static final String CATALOGS_CTS = "CATALOGS_CTS";
/*      */   private static final String TABLETYPES = "TABLETYPES";
/*      */   private static final String COLUMNPRIVILEGES = "COLUMNPRIVILEGES";
/*      */   private static final String TABLEPRIVILEGES = "TABLEPRIVILEGES";
/*      */   private static final String ROWIDENTIFIERS = "ROWIDENTIFIERS";
/*      */   private static final String VERSIONCOLUMNS = "VERSIONCOLUMNS";
/*      */   private static final String PRIMARYKEYS = "PRIMARYKEYS";
/*      */   private static final String IMPORTEDKEYS = "IMPORTEDKEYS";
/*      */   private static final String EXPORTEDKEYS = "EXPORTEDKEYS";
/*      */   private static final String KEYCROSSREFERENCE = "KEYCROSSREFERENCE";
/*      */   private static final String TYPEINFO = "TYPEINFO";
/*      */   private static final String TYPEINFO_CTS = "TYPEINFO_CTS";
/*      */   private static final String INDEXINFO = "INDEXINFO";
/*      */   private static final String OWNUPDATESAREVISIBLE = "OWNUPDATESAREVISIBLE";
/*      */   private static final String OWNDELETESAREVISIBLE = "OWNDELETESAREVISIBLE";
/*      */   private static final String OWNINSERTSAREVISIBLE = "OWNINSERTSAREVISIBLE";
/*      */   private static final String OTHERSUPDATESAREVISIBLE = "OTHERSUPDATESAREVISIBLE";
/*      */   private static final String OTHERSDELETESAREVISIBLE = "OTHERSDELETESAREVISIBLE";
/*      */   private static final String OTHERSINSERTSAREVISIBLE = "OTHERSINSERTSAREVISIBLE";
/*      */   private static final String UPDATESAREDETECTED = "UPDATESAREDETECTED";
/*      */   private static final String DELETESAREDETECTED = "DELETESAREDETECTED";
/*      */   private static final String INSERTSAREDETECTED = "INSERTSAREDETECTED";
/*      */   private static final String SUPPORTSBATCHUPDATES = "SUPPORTSBATCHUPDATES";
/*      */   private static final String SUPPORTSRESULTSETTYPE = "SUPPORTSRESULTSETTYPE";
/*      */   private static final String READONLYCONCURRENCY = "READONLYCONCURRENCY";
/*      */   private static final String UPDATABLECONCURRENCY = "UPDATABLECONCURRENCY";
/*      */   private static final String UDTS = "UDTS";
/*      */   private static final String SUPERTYPES = "SUPERTYPES";
/*      */   private static final String SUPERTABLES = "SUPERTABLES";
/*      */   private static final String ATTRIBUTES = "ATTRIBUTES";
/*      */   private static final String EXECBATCHUPDATESINLOOP = "EXECBATCHUPDATESINLOOP";
/*      */   private static final String EXECPARAMETERIZEDBATCHINLOOP = "EXECPARAMETERIZEDBATCHINLOOP";
/*      */   private static final String MAXBATCHPARAMS = "MAXBATCHPARAMS";
/*      */   private static final String ISCASESENSITIVE = "ISCASESENSITIVE";
/*      */   private static final String CLASSFORNAME = "CLASSFORNAME";
/*      */   private static final String JARFORCLASS = "JARFORCLASS";
/*      */   private static final String JARBYNAME = "JARBYNAME";
/*      */   private static final String CLASSESINJAR = "CLASSESINJAR";
/*      */   private static final String CANRETURNJARS = "CANRETURNJARS";
/*      */   private static final String XACOORDINATORTYPE = "XACOORDINATORTYPE";
/*      */   private static final String SAVEPOINTSUPPORT = "SAVEPOINTSUPPORT";
/*      */   private static final String JDBCMAJORVERSION = "JDBCMAJORVERSION";
/*      */   private static final String JDBCMINORVERSION = "JDBCMINORVERSION";
/*      */   public static final String IS_LOGGED_BCP_SUPPORTED = "IS_LOGGED_BCP_SUPPORTED";
/*      */   public static final String GETCLIENTINFOPROPERTIES = "GETCLIENTINFOPROPERTIES";
/*  193 */   private int _execBatchInLoop = -1;
/*      */ 
/*  196 */   private int _execParameterizedBatchInLoop = -1;
/*      */ 
/*  199 */   private int _batchSupport = -1;
/*      */ 
/*  202 */   private int _maxParamsPerBatch = -1;
/*      */ 
/*  204 */   private Boolean _isFastLoggedBCPSupported = null;
/*      */ 
/*      */   SybDatabaseMetaData(String paramString, SybConnection paramSybConnection)
/*      */     throws SQLException
/*      */   {
/*  218 */     this._logId = (paramString + "_Db" + _logIdCounter++);
/*  219 */     this._conn = paramSybConnection;
/*  220 */     this._mda = paramSybConnection.getMDA(null);
/*  221 */     this._outerJoinEscapeSupport = -1;
/*  222 */     this._isCaseSensitive = -1;
/*      */   }
/*      */ 
/*      */   public boolean allProceduresAreCallable()
/*      */     throws SQLException
/*      */   {
/*  233 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  235 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  237 */       LOG.fine(this._logId + " allProceduresAreCallable()");
/*      */     }
/*      */ 
/*  243 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ALLPROCSCALLABLE");
/*  244 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean allTablesAreSelectable()
/*      */     throws SQLException
/*      */   {
/*  252 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  254 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  256 */       LOG.fine(this._logId + " allTablesAreSelectable()");
/*      */     }
/*      */ 
/*  262 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ALLTABLESSELECTABLE");
/*  263 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public String getURL()
/*      */     throws SQLException
/*      */   {
/*  271 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  273 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  275 */       LOG.fine(this._logId + " getURL()");
/*      */     }
/*      */ 
/*  281 */     return this._conn._url;
/*      */   }
/*      */ 
/*      */   public String getUserName()
/*      */     throws SQLException
/*      */   {
/*  289 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  291 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  293 */       LOG.fine(this._logId + " getUserName()");
/*      */     }
/*      */ 
/*  299 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("USERNAME");
/*  300 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/*  308 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  310 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  312 */       LOG.fine(this._logId + " isReadOnly()");
/*      */     }
/*      */ 
/*  318 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ISREADONLY");
/*  319 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedHigh()
/*      */     throws SQLException
/*      */   {
/*  329 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  331 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  333 */       LOG.fine(this._logId + " nullsAreSortedHigh()");
/*      */     }
/*      */ 
/*  339 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("NULLSORTING");
/*  340 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedLow()
/*      */     throws SQLException
/*      */   {
/*  348 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  350 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  352 */       LOG.fine(this._logId + " nullsAreSortedLow()");
/*      */     }
/*      */ 
/*  358 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("NULLSORTING");
/*  359 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedAtStart()
/*      */     throws SQLException
/*      */   {
/*  367 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  369 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  371 */       LOG.fine(this._logId + " nullsAreSortedAtStart()");
/*      */     }
/*      */ 
/*  377 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("NULLSORTING");
/*  378 */     return returnBoolean(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedAtEnd()
/*      */     throws SQLException
/*      */   {
/*  386 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  388 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  390 */       LOG.fine(this._logId + " nullsAreSortedAtEnd()");
/*      */     }
/*      */ 
/*  396 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("NULLSORTING");
/*  397 */     return returnBoolean(localPreparedStatement, 4);
/*      */   }
/*      */ 
/*      */   public String getDatabaseProductName()
/*      */     throws SQLException
/*      */   {
/*  405 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  407 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  409 */       LOG.fine(this._logId + " getDatabaseProductName()");
/*      */     }
/*      */ 
/*  416 */     if (this._conn.getDatabaseProductName() == null)
/*      */     {
/*  418 */       PreparedStatement localPreparedStatement = getMetaDataAccessor("PRODUCTNAME");
/*  419 */       this._conn.setDatabaseProductName(returnString(localPreparedStatement));
/*      */     }
/*  421 */     return this._conn.getDatabaseProductName();
/*      */   }
/*      */ 
/*      */   public String getIdentityQuery()
/*      */     throws SQLException
/*      */   {
/*  431 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("GET_IDENTITY");
/*  432 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public String getDatabaseProductVersion()
/*      */     throws SQLException
/*      */   {
/*  440 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  442 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  444 */       LOG.fine(this._logId + " getDatabaseProductVersion()");
/*      */     }
/*      */ 
/*  451 */     if (this._conn.getDatabaseProductVersion() == null)
/*      */     {
/*  453 */       PreparedStatement localPreparedStatement = getMetaDataAccessor("PRODUCTVERSION");
/*  454 */       this._conn.setDatabaseProductVersion(returnString(localPreparedStatement));
/*      */     }
/*  456 */     return this._conn.getDatabaseProductVersion();
/*      */   }
/*      */ 
/*      */   public String getDriverName()
/*      */     throws SQLException
/*      */   {
/*  464 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  466 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  468 */       LOG.fine(this._logId + " getDriverName()");
/*      */     }
/*      */ 
/*  473 */     return "jConnect (TM) for JDBC (TM)";
/*      */   }
/*      */ 
/*      */   public String getDriverVersion()
/*      */     throws SQLException
/*      */   {
/*  481 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  483 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  485 */       LOG.fine(this._logId + " getDriverVersion()");
/*      */     }
/*      */ 
/*  490 */     return SybVersion.VERSION_STRING;
/*      */   }
/*      */ 
/*      */   public int getDriverMajorVersion()
/*      */   {
/*  498 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  500 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  502 */       LOG.fine(this._logId + " getDriverMajorVersion()");
/*      */     }
/*      */ 
/*  507 */     return SybVersion.MAJOR_VERSION;
/*      */   }
/*      */ 
/*      */   public boolean execBatchUpdatesInLoop()
/*      */     throws SQLException
/*      */   {
/*  514 */     if (this._execBatchInLoop == -1)
/*      */     {
/*      */       try
/*      */       {
/*  518 */         PreparedStatement localPreparedStatement = getMetaDataAccessor("EXECBATCHUPDATESINLOOP");
/*  519 */         this._execBatchInLoop = ((returnBoolean(localPreparedStatement, 1)) ? 1 : 0);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/*  529 */         if (!"JZ0SJ".equals(localSQLException.getSQLState()))
/*      */         {
/*  531 */           throw localSQLException;
/*      */         }
/*      */ 
/*  539 */         this._execBatchInLoop = 0;
/*      */       }
/*      */     }
/*      */ 
/*  543 */     return this._execBatchInLoop == 1;
/*      */   }
/*      */ 
/*      */   public boolean execParameterizedBatchUpdatesInLoop()
/*      */     throws SQLException
/*      */   {
/*  552 */     if (this._execParameterizedBatchInLoop == -1)
/*      */     {
/*      */       try
/*      */       {
/*  556 */         PreparedStatement localPreparedStatement = getMetaDataAccessor("EXECPARAMETERIZEDBATCHINLOOP");
/*      */ 
/*  558 */         this._execParameterizedBatchInLoop = ((returnBoolean(localPreparedStatement, 1)) ? 1 : 0);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/*  567 */         if (!"JZ0SJ".equals(localSQLException.getSQLState()))
/*      */         {
/*  569 */           throw localSQLException;
/*      */         }
/*      */ 
/*  577 */         this._execParameterizedBatchInLoop = 0;
/*      */       }
/*      */     }
/*      */ 
/*  581 */     return this._execParameterizedBatchInLoop == 1;
/*      */   }
/*      */ 
/*      */   protected boolean isFastLoggedBCPSupported()
/*      */     throws SQLException
/*      */   {
/*  590 */     if (this._isFastLoggedBCPSupported == null)
/*      */     {
/*      */       try
/*      */       {
/*  594 */         PreparedStatement localPreparedStatement = getMetaDataAccessor("IS_LOGGED_BCP_SUPPORTED");
/*  595 */         this._isFastLoggedBCPSupported = Boolean.valueOf(returnBoolean(localPreparedStatement, 1));
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/*  604 */         if (!"JZ0SJ".equals(localSQLException.getSQLState()))
/*      */         {
/*  606 */           throw localSQLException;
/*      */         }
/*      */ 
/*  614 */         this._isFastLoggedBCPSupported = new Boolean(false);
/*      */       }
/*      */     }
/*      */ 
/*  618 */     return this._isFastLoggedBCPSupported.booleanValue();
/*      */   }
/*      */ 
/*      */   public int getMaxParamsPerBatch()
/*      */     throws SQLException
/*      */   {
/*  626 */     if (this._maxParamsPerBatch == -1)
/*      */     {
/*      */       try
/*      */       {
/*  630 */         PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXBATCHPARAMS");
/*  631 */         this._maxParamsPerBatch = returnInt(localPreparedStatement, 1);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/*  640 */         if (!"JZ0SJ".equals(localSQLException.getSQLState()))
/*      */         {
/*  642 */           throw localSQLException;
/*      */         }
/*      */ 
/*  649 */         this._maxParamsPerBatch = 255;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  654 */     return this._maxParamsPerBatch;
/*      */   }
/*      */ 
/*      */   public int getDriverMinorVersion()
/*      */   {
/*  662 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  664 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  666 */       LOG.fine(this._logId + " getDriverMinorVersion()");
/*      */     }
/*      */ 
/*  671 */     return SybVersion.MINOR_VERSION;
/*      */   }
/*      */ 
/*      */   public boolean usesLocalFiles()
/*      */     throws SQLException
/*      */   {
/*  681 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  683 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  685 */       LOG.fine(this._logId + " usesLocalFiles()");
/*      */     }
/*      */ 
/*  691 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("FILEUSAGE");
/*  692 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean usesLocalFilePerTable()
/*      */     throws SQLException
/*      */   {
/*  700 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  702 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  704 */       LOG.fine(this._logId + " usesLocalFilePerTable()");
/*      */     }
/*      */ 
/*  711 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("FILEUSAGE");
/*  712 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  722 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  724 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  726 */       LOG.fine(this._logId + " supportsMixedCaseIdentifiers()");
/*      */     }
/*      */ 
/*  733 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("IDENTIFIERCASES");
/*  734 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean storesUpperCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  741 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  743 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  745 */       LOG.fine(this._logId + " storesUpperCaseIdentifiers()");
/*      */     }
/*      */ 
/*  751 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("IDENTIFIERCASES");
/*  752 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  760 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  762 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  764 */       LOG.fine(this._logId + " storesLowerCaseIdentifiers()");
/*      */     }
/*      */ 
/*  771 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("IDENTIFIERCASES");
/*  772 */     return returnBoolean(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public boolean storesMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  780 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  782 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  784 */       LOG.fine(this._logId + " storesMixedCaseIdentifiers()");
/*      */     }
/*      */ 
/*  791 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("IDENTIFIERCASES");
/*  792 */     return returnBoolean(localPreparedStatement, 4);
/*      */   }
/*      */ 
/*      */   public boolean supportsMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  800 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  802 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  804 */       LOG.fine(this._logId + " supportsMixedCaseQuotedIdentifiers()");
/*      */     }
/*      */ 
/*  811 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("IDENTIFIERCASES");
/*  812 */     return returnBoolean(localPreparedStatement, 5);
/*      */   }
/*      */ 
/*      */   public boolean storesUpperCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  820 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  822 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  824 */       LOG.fine(this._logId + " storesUpperCaseQuotedIdentifiers()");
/*      */     }
/*      */ 
/*  831 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("IDENTIFIERCASES");
/*  832 */     return returnBoolean(localPreparedStatement, 6);
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  840 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  842 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  844 */       LOG.fine(this._logId + " storesLowerCaseQuotedIdentifiers()");
/*      */     }
/*      */ 
/*  851 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("IDENTIFIERCASES");
/*  852 */     return returnBoolean(localPreparedStatement, 7);
/*      */   }
/*      */ 
/*      */   public boolean storesMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  863 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("IDENTIFIERCASES");
/*  864 */     return returnBoolean(localPreparedStatement, 8);
/*      */   }
/*      */ 
/*      */   public String getIdentifierQuoteString()
/*      */     throws SQLException
/*      */   {
/*  872 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  874 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  876 */       LOG.fine(this._logId + " getIdentifierQuoteString()");
/*      */     }
/*      */ 
/*  883 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("IDENTIFIERQUOTE");
/*  884 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public String getSQLKeywords()
/*      */     throws SQLException
/*      */   {
/*  892 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  894 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  896 */       LOG.fine(this._logId + " getSQLKeywords()");
/*      */     }
/*      */ 
/*  902 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SQLKEYWORDS");
/*  903 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public String getNumericFunctions()
/*      */     throws SQLException
/*      */   {
/*  911 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  913 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  915 */       LOG.fine(this._logId + " getNumericFunctions()");
/*      */     }
/*      */ 
/*  921 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("NUMERICFUNCTIONLIST");
/*  922 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public String getStringFunctions()
/*      */     throws SQLException
/*      */   {
/*  930 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  932 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  934 */       LOG.fine(this._logId + " getStringFunctions()");
/*      */     }
/*      */ 
/*  940 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("STRINGFUNCTIONLIST");
/*  941 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public String getSystemFunctions()
/*      */     throws SQLException
/*      */   {
/*  949 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  951 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  953 */       LOG.fine(this._logId + " getSystemFunctions()");
/*      */     }
/*      */ 
/*  959 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SYSTEMFUNCTIONLIST");
/*  960 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public String getTimeDateFunctions()
/*      */     throws SQLException
/*      */   {
/*  968 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  970 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  972 */       LOG.fine(this._logId + " getTimeDateFunctions()");
/*      */     }
/*      */ 
/*  978 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TIMEDATEFUNCTIONLIST");
/*  979 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public String getSearchStringEscape()
/*      */     throws SQLException
/*      */   {
/*  987 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/*  989 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/*  991 */       LOG.fine(this._logId + " getSearchStringEscape()");
/*      */     }
/*      */ 
/*  997 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SEARCHSTRING");
/*  998 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public String getExtraNameCharacters()
/*      */     throws SQLException
/*      */   {
/* 1006 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1008 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1010 */       LOG.fine(this._logId + " getExtraNameCharacters()");
/*      */     }
/*      */ 
/* 1016 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("EXTRANAMECHARS");
/* 1017 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public boolean supportsAlterTableWithAddColumn()
/*      */     throws SQLException
/*      */   {
/* 1030 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1032 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1034 */       LOG.fine(this._logId + " supportsAlterTableWithAddColumn()");
/*      */     }
/*      */ 
/* 1040 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ALTERTABLESUPPORT");
/* 1041 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsAlterTableWithDropColumn()
/*      */     throws SQLException
/*      */   {
/* 1049 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1051 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1053 */       LOG.fine(this._logId + " supportsAlterTableWithDropColumn()");
/*      */     }
/*      */ 
/* 1059 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ALTERTABLESUPPORT");
/* 1060 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsColumnAliasing()
/*      */     throws SQLException
/*      */   {
/* 1068 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1070 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1072 */       LOG.fine(this._logId + " supportsColumnAliasing()");
/*      */     }
/*      */ 
/* 1078 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("COLUMNALIASING");
/* 1079 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean nullPlusNonNullIsNull()
/*      */     throws SQLException
/*      */   {
/* 1087 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1089 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1091 */       LOG.fine(this._logId + " nullPlusNonNullsNull()");
/*      */     }
/*      */ 
/* 1097 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("NULLPLUSNONNULL");
/* 1098 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsConvert()
/*      */     throws SQLException
/*      */   {
/* 1106 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1108 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1110 */       LOG.fine(this._logId + " supportsConvert()");
/*      */     }
/*      */ 
/* 1116 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CONVERTSUPPORT");
/* 1117 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsConvert(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 1128 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 1130 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 1132 */         LOG.finer(this._logId + " supportsConvert(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */       }
/* 1135 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 1137 */         LOG.fine(this._logId + " supportsConvert(int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1143 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CONVERTMAP");
/* 1144 */     localPreparedStatement.setInt(1, paramInt1);
/* 1145 */     localPreparedStatement.setInt(2, paramInt2);
/* 1146 */     ResultSet localResultSet = localPreparedStatement.executeQuery();
/* 1147 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/* 1157 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1159 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1161 */       LOG.fine(this._logId + " supportsTableCorrelationNames()");
/*      */     }
/*      */ 
/* 1168 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CORRELATIONNAMES");
/* 1169 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsDifferentTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/* 1178 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1180 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1182 */       LOG.fine(this._logId + " supportsDifferentTableCorrelationNames()");
/*      */     }
/*      */ 
/* 1189 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CORRELATIONNAMES");
/* 1190 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsExpressionsInOrderBy()
/*      */     throws SQLException
/*      */   {
/* 1200 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1202 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1204 */       LOG.fine(this._logId + " supportsExpressionsInOrderBy()");
/*      */     }
/*      */ 
/* 1211 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ORDERBYSUPPORT");
/* 1212 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsOrderByUnrelated()
/*      */     throws SQLException
/*      */   {
/* 1220 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1222 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1224 */       LOG.fine(this._logId + " supportsOrderByUnrelated()");
/*      */     }
/*      */ 
/* 1231 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ORDERBYSUPPORT");
/* 1232 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupBy()
/*      */     throws SQLException
/*      */   {
/* 1242 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1244 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1246 */       LOG.fine(this._logId + " supportsGroupBy()");
/*      */     }
/*      */ 
/* 1252 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("GROUPBYSUPPORT");
/* 1253 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupByUnrelated()
/*      */     throws SQLException
/*      */   {
/* 1261 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1263 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1265 */       LOG.fine(this._logId + " supportsGroupByUnrelated()");
/*      */     }
/*      */ 
/* 1272 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("GROUPBYSUPPORT");
/* 1273 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupByBeyondSelect()
/*      */     throws SQLException
/*      */   {
/* 1281 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1283 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1285 */       LOG.fine(this._logId + " supportsGroupByBeyondSelect()");
/*      */     }
/*      */ 
/* 1292 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("GROUPBYSUPPORT");
/* 1293 */     return returnBoolean(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public boolean supportsLikeEscapeClause()
/*      */     throws SQLException
/*      */   {
/* 1301 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1303 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1305 */       LOG.fine(this._logId + " supportsLikeEscapeClause()");
/*      */     }
/*      */ 
/* 1312 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("LIKEESCAPECLAUSE");
/* 1313 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleResultSets()
/*      */     throws SQLException
/*      */   {
/* 1321 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1323 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1325 */       LOG.fine(this._logId + " supportsMultipleResultSets()");
/*      */     }
/*      */ 
/* 1332 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MULTIPLERESULTSETS");
/* 1333 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleTransactions()
/*      */     throws SQLException
/*      */   {
/* 1341 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1343 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1345 */       LOG.fine(this._logId + " supportsMultipleTransactions()");
/*      */     }
/*      */ 
/* 1352 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MULTIPLETRANSACTIONS");
/* 1353 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsNonNullableColumns()
/*      */     throws SQLException
/*      */   {
/* 1361 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1363 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1365 */       LOG.fine(this._logId + " supportsNonNullableColumns()");
/*      */     }
/*      */ 
/* 1372 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("NONNULLABLECOLUMNS");
/* 1373 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsMinimumSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 1383 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1385 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1387 */       LOG.fine(this._logId + " supportsMinimumSQLGrammar()");
/*      */     }
/*      */ 
/* 1394 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SQLGRAMMAR");
/* 1395 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsCoreSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 1403 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1405 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1407 */       LOG.fine(this._logId + " supportsCoreSQLGrammar()");
/*      */     }
/*      */ 
/* 1414 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SQLGRAMMAR");
/* 1415 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsExtendedSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 1423 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1425 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1427 */       LOG.fine(this._logId + " supportsExtendedSQLGrammar()");
/*      */     }
/*      */ 
/* 1434 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SQLGRAMMAR");
/* 1435 */     return returnBoolean(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92EntryLevelSQL()
/*      */     throws SQLException
/*      */   {
/* 1445 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1447 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1449 */       LOG.fine(this._logId + " supportsANSI92EntryLevelSQL()");
/*      */     }
/*      */ 
/* 1456 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ANSI92LEVEL");
/* 1457 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92IntermediateSQL()
/*      */     throws SQLException
/*      */   {
/* 1465 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1467 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1469 */       LOG.fine(this._logId + " supportsANSI92IntermediateSQL()");
/*      */     }
/*      */ 
/* 1476 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ANSI92LEVEL");
/* 1477 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92FullSQL()
/*      */     throws SQLException
/*      */   {
/* 1485 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1487 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1489 */       LOG.fine(this._logId + " supportsANSI92FullSQL()");
/*      */     }
/*      */ 
/* 1496 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ANSI92LEVEL");
/* 1497 */     return returnBoolean(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public boolean supportsIntegrityEnhancementFacility()
/*      */     throws SQLException
/*      */   {
/* 1505 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1507 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1509 */       LOG.fine(this._logId + " supportsIntegrityEnhancementFacility()");
/*      */     }
/*      */ 
/* 1516 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("INTEGRITYENHANCEMENT");
/* 1517 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 1527 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1529 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1531 */       LOG.fine(this._logId + " supportsOuterJoins()");
/*      */     }
/*      */ 
/* 1538 */     if (this._outerJoinEscapeSupport == -1)
/*      */     {
/* 1540 */       PreparedStatement localPreparedStatement = getMetaDataAccessor("OUTERJOINS");
/* 1541 */       this._outerJoinEscapeSupport = ((returnBoolean(localPreparedStatement, 1)) ? 1 : 0);
/*      */     }
/* 1543 */     return this._outerJoinEscapeSupport == 1;
/*      */   }
/*      */ 
/*      */   public boolean supportsFullOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 1551 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1553 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1555 */       LOG.fine(this._logId + " supportsFullOuterJoins()");
/*      */     }
/*      */ 
/* 1562 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("OUTERJOINS");
/* 1563 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsLimitedOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 1571 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1573 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1575 */       LOG.fine(this._logId + " supportsLimitedOuterJoins()");
/*      */     }
/*      */ 
/* 1582 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("OUTERJOINS");
/* 1583 */     return returnBoolean(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   protected boolean supportsOuterJoinEscapeSyntax()
/*      */     throws SQLException
/*      */   {
/* 1598 */     if (this._mda._version < 1)
/*      */     {
/* 1607 */       return getDatabaseProductName().equals("Sybase SQL Anywhere");
/*      */     }
/*      */ 
/* 1610 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("OUTERJOINS");
/* 1611 */     return returnBoolean(localPreparedStatement, 4);
/*      */   }
/*      */ 
/*      */   public String getSchemaTerm()
/*      */     throws SQLException
/*      */   {
/* 1619 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1621 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1623 */       LOG.fine(this._logId + " getSchemaTerm()");
/*      */     }
/*      */ 
/* 1629 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SCHEMATERM");
/* 1630 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public String getProcedureTerm()
/*      */     throws SQLException
/*      */   {
/* 1638 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1640 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1642 */       LOG.fine(this._logId + " getProcedureTerm()");
/*      */     }
/*      */ 
/* 1648 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("PROCEDURETERM");
/* 1649 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public String getCatalogTerm()
/*      */     throws SQLException
/*      */   {
/* 1657 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1659 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1661 */       LOG.fine(this._logId + " getCatalogTerm()");
/*      */     }
/*      */ 
/* 1667 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CATALOGTERM");
/* 1668 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public boolean isCatalogAtStart()
/*      */     throws SQLException
/*      */   {
/* 1676 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1678 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1680 */       LOG.fine(this._logId + " isCatalogAtStart()");
/*      */     }
/*      */ 
/* 1686 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CATALOGATSTART");
/* 1687 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public String getCatalogSeparator()
/*      */     throws SQLException
/*      */   {
/* 1695 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1697 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1699 */       LOG.fine(this._logId + " getCatalogSeparator()");
/*      */     }
/*      */ 
/* 1705 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CATALOGSEPARATOR");
/* 1706 */     return returnString(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInDataManipulation()
/*      */     throws SQLException
/*      */   {
/* 1716 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1718 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1720 */       LOG.fine(this._logId + " supportsSchemasInDataManipulation()");
/*      */     }
/*      */ 
/* 1727 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SCHEMASUPPORT");
/* 1728 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/* 1736 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1738 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1740 */       LOG.fine(this._logId + " supportsSchemasInProcedureCalls()");
/*      */     }
/*      */ 
/* 1747 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SCHEMASUPPORT");
/* 1748 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/* 1756 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1758 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1760 */       LOG.fine(this._logId + " supportsSchemasInTableDefinitions()");
/*      */     }
/*      */ 
/* 1767 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SCHEMASUPPORT");
/* 1768 */     return returnBoolean(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/* 1776 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1778 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1780 */       LOG.fine(this._logId + " supportsSchemasInIndexDefinitions()");
/*      */     }
/*      */ 
/* 1787 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SCHEMASUPPORT");
/* 1788 */     return returnBoolean(localPreparedStatement, 4);
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/* 1796 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1798 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1800 */       LOG.fine(this._logId + " supportsSchemasInPrivilegeDefinitions()");
/*      */     }
/*      */ 
/* 1807 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SCHEMASUPPORT");
/* 1808 */     return returnBoolean(localPreparedStatement, 5);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInDataManipulation()
/*      */     throws SQLException
/*      */   {
/* 1818 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1820 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1822 */       LOG.fine(this._logId + " supportsCatalogsInDataManipulation()");
/*      */     }
/*      */ 
/* 1829 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CATALOGSUPPORT");
/* 1830 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/* 1838 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1840 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1842 */       LOG.fine(this._logId + " supportsCatalogsInProcedureCalls()");
/*      */     }
/*      */ 
/* 1849 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CATALOGSUPPORT");
/* 1850 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/* 1858 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1860 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1862 */       LOG.fine(this._logId + " supportsCatalogsInTableDefinitions()");
/*      */     }
/*      */ 
/* 1869 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CATALOGSUPPORT");
/* 1870 */     return returnBoolean(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/* 1878 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1880 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1882 */       LOG.fine(this._logId + " supportsCatalogsInIndexDefinitions()");
/*      */     }
/*      */ 
/* 1889 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CATALOGSUPPORT");
/* 1890 */     return returnBoolean(localPreparedStatement, 4);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/* 1898 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1900 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1902 */       LOG.fine(this._logId + " supportsCatalogsInPrivilegeDefinitions()");
/*      */     }
/*      */ 
/* 1909 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CATALOGSUPPORT");
/* 1910 */     return returnBoolean(localPreparedStatement, 5);
/*      */   }
/*      */ 
/*      */   public boolean supportsPositionedDelete()
/*      */     throws SQLException
/*      */   {
/* 1918 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1920 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1922 */       LOG.fine(this._logId + " supportsPositionedDelete()");
/*      */     }
/*      */ 
/* 1929 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("POSITIONEDDELETE");
/* 1930 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsPositionedUpdate()
/*      */     throws SQLException
/*      */   {
/* 1938 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1940 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1942 */       LOG.fine(this._logId + " supportsPositionedUpdate()");
/*      */     }
/*      */ 
/* 1949 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("POSITIONEDUPDATE");
/* 1950 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsSelectForUpdate()
/*      */     throws SQLException
/*      */   {
/* 1958 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1960 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1962 */       LOG.fine(this._logId + " supportsSelectForUpdate()");
/*      */     }
/*      */ 
/* 1969 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SELECTFORUPDATE");
/* 1970 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsStoredProcedures()
/*      */     throws SQLException
/*      */   {
/* 1978 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 1980 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 1982 */       LOG.fine(this._logId + " supportsStoredProcedures()");
/*      */     }
/*      */ 
/* 1989 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("STOREDPROCEDURES");
/* 1990 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInComparisons()
/*      */     throws SQLException
/*      */   {
/* 2000 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2002 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2004 */       LOG.fine(this._logId + " supportsSubqueriesInComparisons()");
/*      */     }
/*      */ 
/* 2011 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SUBQUERIES");
/* 2012 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInExists()
/*      */     throws SQLException
/*      */   {
/* 2020 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2022 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2024 */       LOG.fine(this._logId + " supportsSunqueriesInExists()");
/*      */     }
/*      */ 
/* 2031 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SUBQUERIES");
/* 2032 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInIns()
/*      */     throws SQLException
/*      */   {
/* 2040 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2042 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2044 */       LOG.fine(this._logId + " supportsSubqueriesInIns()");
/*      */     }
/*      */ 
/* 2051 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SUBQUERIES");
/* 2052 */     return returnBoolean(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInQuantifieds()
/*      */     throws SQLException
/*      */   {
/* 2060 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2062 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2064 */       LOG.fine(this._logId + " supportsSubqueriesInQuantifieds()");
/*      */     }
/*      */ 
/* 2071 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SUBQUERIES");
/* 2072 */     return returnBoolean(localPreparedStatement, 4);
/*      */   }
/*      */ 
/*      */   public boolean supportsCorrelatedSubqueries()
/*      */     throws SQLException
/*      */   {
/* 2080 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2082 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2084 */       LOG.fine(this._logId + " supportsCorrelatedSubqueries()");
/*      */     }
/*      */ 
/* 2091 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SUBQUERIES");
/* 2092 */     return returnBoolean(localPreparedStatement, 5);
/*      */   }
/*      */ 
/*      */   public boolean supportsUnion()
/*      */     throws SQLException
/*      */   {
/* 2102 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2104 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2106 */       LOG.fine(this._logId + " supportsUnion()");
/*      */     }
/*      */ 
/* 2113 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("UNIONSUPPORT");
/* 2114 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsUnionAll()
/*      */     throws SQLException
/*      */   {
/* 2122 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2124 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2126 */       LOG.fine(this._logId + " supportsUnionAll()");
/*      */     }
/*      */ 
/* 2133 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("UNIONSUPPORT");
/* 2134 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/* 2144 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2146 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2148 */       LOG.fine(this._logId + " supportsOpenCursorsAcrossCommit()");
/*      */     }
/*      */ 
/* 2155 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CURSORTRANSACTIONS");
/* 2156 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/* 2164 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2166 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2168 */       LOG.fine(this._logId + " supportsOpenCursorsAcrossRollback()");
/*      */     }
/*      */ 
/* 2175 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CURSORTRANSACTIONS");
/* 2176 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/* 2186 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2188 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2190 */       LOG.fine(this._logId + " supportsOpenStatementsAcrossCommit()");
/*      */     }
/*      */ 
/* 2197 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("STATEMENTTRANSACTIONS");
/* 2198 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/* 2207 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2209 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2211 */       LOG.fine(this._logId + " supportsOpenStatementsAcrossRollback()");
/*      */     }
/*      */ 
/* 2218 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("STATEMENTTRANSACTIONS");
/* 2219 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public int getMaxBinaryLiteralLength()
/*      */     throws SQLException
/*      */   {
/* 2233 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2235 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2237 */       LOG.fine(this._logId + " getMaxBinaryLiteralLength()");
/*      */     }
/*      */ 
/* 2244 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXBINARYLITERALLENGTH");
/* 2245 */     return returnInt(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public int getMaxCharLiteralLength()
/*      */     throws SQLException
/*      */   {
/* 2253 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2255 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2257 */       LOG.fine(this._logId + " getMaxCharLiteralLength()");
/*      */     }
/*      */ 
/* 2264 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXCHARLITERALLENGTH");
/* 2265 */     return returnInt(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public int getMaxColumnNameLength()
/*      */     throws SQLException
/*      */   {
/* 2275 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2277 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2279 */       LOG.fine(this._logId + " getMaxColumnNameLength()");
/*      */     }
/*      */ 
/* 2286 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("COLUMNINFO");
/* 2287 */     return returnInt(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInGroupBy()
/*      */     throws SQLException
/*      */   {
/* 2295 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2297 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2299 */       LOG.fine(this._logId + " getMaxColumnsInGroupBy()");
/*      */     }
/*      */ 
/* 2306 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("COLUMNINFO");
/* 2307 */     return returnInt(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInIndex()
/*      */     throws SQLException
/*      */   {
/* 2315 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2317 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2319 */       LOG.fine(this._logId + " getMaxColumnsInIndex()");
/*      */     }
/*      */ 
/* 2326 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("COLUMNINFO");
/* 2327 */     return returnInt(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInOrderBy()
/*      */     throws SQLException
/*      */   {
/* 2335 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2337 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2339 */       LOG.fine(this._logId + " getMaxColumnsInOrderBy()");
/*      */     }
/*      */ 
/* 2346 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("COLUMNINFO");
/* 2347 */     return returnInt(localPreparedStatement, 4);
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInSelect()
/*      */     throws SQLException
/*      */   {
/* 2355 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2357 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2359 */       LOG.fine(this._logId + " getMaxColumnsInSelect()");
/*      */     }
/*      */ 
/* 2366 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("COLUMNINFO");
/* 2367 */     return returnInt(localPreparedStatement, 5);
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInTable()
/*      */     throws SQLException
/*      */   {
/* 2375 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2377 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2379 */       LOG.fine(this._logId + " getMaxColumnsInTable()");
/*      */     }
/*      */ 
/* 2386 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("COLUMNINFO");
/* 2387 */     return returnInt(localPreparedStatement, 6);
/*      */   }
/*      */ 
/*      */   public int getMaxConnections()
/*      */     throws SQLException
/*      */   {
/* 2395 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2397 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2399 */       LOG.fine(this._logId + " getMaxConnections()");
/*      */     }
/*      */ 
/* 2406 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXCONNECTIONS");
/* 2407 */     return returnInt(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public int getMaxIndexLength()
/*      */     throws SQLException
/*      */   {
/* 2415 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2417 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2419 */       LOG.fine(this._logId + " getMaxIndexLength()");
/*      */     }
/*      */ 
/* 2426 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXINDEXLENGTH");
/* 2427 */     return returnInt(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public int getMaxCursorNameLength()
/*      */     throws SQLException
/*      */   {
/* 2437 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2439 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2441 */       LOG.fine(this._logId + " getMaxCursorNameLength()");
/*      */     }
/*      */ 
/* 2448 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXNAMELENGTHS");
/* 2449 */     return returnInt(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public int getMaxUserNameLength()
/*      */     throws SQLException
/*      */   {
/* 2457 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2459 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2461 */       LOG.fine(this._logId + " getMaxUserNameLength()");
/*      */     }
/*      */ 
/* 2468 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXNAMELENGTHS");
/* 2469 */     return returnInt(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public int getMaxSchemaNameLength()
/*      */     throws SQLException
/*      */   {
/* 2477 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2479 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2481 */       LOG.fine(this._logId + " getMaxSchemaNameLength()");
/*      */     }
/*      */ 
/* 2488 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXNAMELENGTHS");
/* 2489 */     return returnInt(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public int getMaxProcedureNameLength()
/*      */     throws SQLException
/*      */   {
/* 2497 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2499 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2501 */       LOG.fine(this._logId + " getMaxProcedureNameLength()");
/*      */     }
/*      */ 
/* 2508 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXNAMELENGTHS");
/* 2509 */     return returnInt(localPreparedStatement, 4);
/*      */   }
/*      */ 
/*      */   public int getMaxCatalogNameLength()
/*      */     throws SQLException
/*      */   {
/* 2517 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2519 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2521 */       LOG.fine(this._logId + " getMaxCatalogNameLength()");
/*      */     }
/*      */ 
/* 2528 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("MAXNAMELENGTHS");
/* 2529 */     return returnInt(localPreparedStatement, 5);
/*      */   }
/*      */ 
/*      */   public int getMaxRowSize()
/*      */     throws SQLException
/*      */   {
/* 2539 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2541 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2543 */       LOG.fine(this._logId + " getMaxRowSize()");
/*      */     }
/*      */ 
/* 2550 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ROWINFO");
/* 2551 */     return returnInt(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean doesMaxRowSizeIncludeBlobs()
/*      */     throws SQLException
/*      */   {
/* 2559 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2561 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2563 */       LOG.fine(this._logId + " doesMaxRowSizeIncludeBlobs()");
/*      */     }
/*      */ 
/* 2570 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ROWINFO");
/* 2571 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public int getMaxStatementLength()
/*      */     throws SQLException
/*      */   {
/* 2581 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2583 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2585 */       LOG.fine(this._logId + " getMaxStatementLength()");
/*      */     }
/*      */ 
/* 2592 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("STATEMENTINFO");
/* 2593 */     return returnInt(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public int getMaxStatements()
/*      */     throws SQLException
/*      */   {
/* 2601 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2603 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2605 */       LOG.fine(this._logId + " getMaxStatements()");
/*      */     }
/*      */ 
/* 2612 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("STATEMENTINFO");
/* 2613 */     return returnInt(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public int getMaxTableNameLength()
/*      */     throws SQLException
/*      */   {
/* 2623 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2625 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2627 */       LOG.fine(this._logId + " getMaxTableNameLength()");
/*      */     }
/*      */ 
/* 2634 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TABLEINFO");
/* 2635 */     return returnInt(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public int getMaxTablesInSelect()
/*      */     throws SQLException
/*      */   {
/* 2643 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2645 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2647 */       LOG.fine(this._logId + " getMaxTablesInSelect()");
/*      */     }
/*      */ 
/* 2654 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TABLEINFO");
/* 2655 */     return returnInt(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactions()
/*      */     throws SQLException
/*      */   {
/* 2665 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2667 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2669 */       LOG.fine(this._logId + " supportsTransactions()");
/*      */     }
/*      */ 
/* 2676 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TRANSACTIONSUPPORT");
/* 2677 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public int getDefaultTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/* 2685 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2687 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2689 */       LOG.fine(this._logId + " getDefaultTransactionIsolation()");
/*      */     }
/*      */ 
/* 2696 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TRANSACTIONLEVELDEFAULT");
/* 2697 */     return returnInt(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactionIsolationLevel(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2706 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2708 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2710 */         LOG.finer(this._logId + " supportsTransactionIsolationLevel(int = [" + paramInt + "])");
/*      */       }
/* 2713 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2715 */         LOG.fine(this._logId + " supportsTransactionIsolationLevel(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2722 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TRANSACTIONLEVELS");
/* 2723 */     ResultSet localResultSet = localPreparedStatement.executeQuery();
/* 2724 */     int i = 0;
/* 2725 */     Object localObject1 = null;
/*      */     try
/*      */     {
/* 2728 */       if (localResultSet.next())
/*      */       {
/* 2730 */         int j = localResultSet.getMetaData().getColumnCount();
/* 2731 */         for (int k = 1; k <= j; ++k)
/*      */         {
/* 2733 */           if (localResultSet.getInt(k) == paramInt)
/* 2734 */             i = 1;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2740 */       localObject1 = localSQLException;
/*      */     }
/*      */     finally
/*      */     {
/* 2744 */       localResultSet.close();
/* 2745 */       localPreparedStatement.close();
/* 2746 */       if (localObject1 != null)
/*      */       {
/* 2748 */         throw localObject1;
/*      */       }
/*      */     }
/* 2751 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean supportsDataDefinitionAndDataManipulationTransactions()
/*      */     throws SQLException
/*      */   {
/* 2762 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2764 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2766 */       LOG.fine(this._logId + " supportsDataDefinitionAndDataManipulationTransactions()");
/*      */     }
/*      */ 
/* 2775 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TRANSACTIONDATADEFINFO");
/* 2776 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsDataManipulationTransactionsOnly()
/*      */     throws SQLException
/*      */   {
/* 2784 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2786 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2788 */       LOG.fine(this._logId + " supportsDataManipulationTransactionsOnly()");
/*      */     }
/*      */ 
/* 2796 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TRANSACTIONDATADEFINFO");
/* 2797 */     return returnBoolean(localPreparedStatement, 2);
/*      */   }
/*      */ 
/*      */   public boolean dataDefinitionCausesTransactionCommit()
/*      */     throws SQLException
/*      */   {
/* 2805 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2807 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2809 */       LOG.fine(this._logId + " defaultDefinitionCausesTransactionCommit()");
/*      */     }
/*      */ 
/* 2817 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TRANSACTIONDATADEFINFO");
/* 2818 */     return returnBoolean(localPreparedStatement, 3);
/*      */   }
/*      */ 
/*      */   public boolean dataDefinitionIgnoredInTransactions()
/*      */     throws SQLException
/*      */   {
/* 2826 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 2828 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 2830 */       LOG.fine(this._logId + " dataDefinitionIgnoredInTransactions()");
/*      */     }
/*      */ 
/* 2837 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TRANSACTIONDATADEFINFO");
/* 2838 */     return returnBoolean(localPreparedStatement, 4);
/*      */   }
/*      */ 
/*      */   public ResultSet getProcedures(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 2847 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2849 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2851 */         LOG.finer(this._logId + " getProcedures(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "])");
/*      */       }
/* 2855 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2857 */         LOG.fine(this._logId + " getProcedures(String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2863 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("PROCEDURES", paramString1);
/* 2864 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 2865 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 2866 */     setStringParam(localPreparedStatement, paramString3, 3);
/*      */ 
/* 2868 */     if (((SybPreparedStatement)localPreparedStatement)._paramCount >= 4)
/*      */     {
/* 2870 */       localPreparedStatement.setNull(4, 4);
/*      */     }
/* 2872 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getProcedureColumns(String paramString1, String paramString2, String paramString3, String paramString4)
/*      */     throws SQLException
/*      */   {
/* 2883 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2885 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2887 */         LOG.finer(this._logId + " getProcedureColumns(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "], String = [" + paramString4 + "])");
/*      */       }
/* 2892 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2894 */         LOG.fine(this._logId + " getProcedureColumns(String, String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2901 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("PROCEDURECOLUMNS", paramString1);
/* 2902 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 2903 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 2904 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 2905 */     setStringParam(localPreparedStatement, paramString4, 4);
/*      */ 
/* 2907 */     if (((SybPreparedStatement)localPreparedStatement)._paramCount >= 6)
/*      */     {
/* 2909 */       localPreparedStatement.setInt(5, 0);
/* 2910 */       localPreparedStatement.setNull(6, 4);
/*      */     }
/* 2912 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getTables(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
/*      */     throws SQLException
/*      */   {
/* 2921 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 2923 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 2925 */         LOG.finest(LogUtil.logMethod(false, this._logId, "getTables", new Object[] { paramString1, paramString2, paramString3, paramArrayOfString }));
/*      */       }
/* 2929 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 2931 */         LOG.finer(LogUtil.logMethod(true, this._logId, "getTables", new Object[] { paramString1, paramString2, paramString3, paramArrayOfString }));
/*      */       }
/* 2935 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 2937 */         LOG.fine(this._logId + " getTables(String, String, String, String[])");
/*      */       }
/*      */     }
/*      */     StringBuffer localStringBuffer;
/* 2950 */     if (paramArrayOfString == null)
/*      */     {
/* 2952 */       localStringBuffer = null;
/*      */     }
/*      */     else
/*      */     {
/* 2958 */       localStringBuffer = new StringBuffer("\"");
/* 2959 */       for (int i = 0; (i < paramArrayOfString.length) && 
/* 2961 */         (paramArrayOfString[i] != null); ++i)
/*      */       {
/* 2961 */         if (paramArrayOfString[i].equals("null")) break;
/* 2962 */         localStringBuffer.append("'" + paramArrayOfString[i] + "'");
/*      */       }
/* 2964 */       localStringBuffer = localStringBuffer.append("\"");
/*      */     }
/*      */ 
/* 2967 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TABLES", paramString1);
/* 2968 */     setStringParam(localPreparedStatement, paramString3, 1);
/* 2969 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 2970 */     setStringParam(localPreparedStatement, paramString1, 3);
/* 2971 */     setStringParam(localPreparedStatement, (localStringBuffer == null) ? null : localStringBuffer.toString(), 4);
/* 2972 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getClassForName(String paramString)
/*      */     throws SQLException
/*      */   {
/* 2986 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CLASSFORNAME");
/* 2987 */     setStringParam(localPreparedStatement, paramString, 1);
/* 2988 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getJarForClass(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3005 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("JARFORCLASS");
/* 3006 */     setStringParam(localPreparedStatement, paramString, 1);
/* 3007 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getJarByName(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3023 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("JARBYNAME");
/* 3024 */     setStringParam(localPreparedStatement, paramString, 1);
/* 3025 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getClassesInJar(String paramString)
/*      */     throws SQLException
/*      */   {
/* 3041 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CLASSESINJAR");
/* 3042 */     setStringParam(localPreparedStatement, paramString, 1);
/* 3043 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public boolean canReturnJars()
/*      */     throws SQLException
/*      */   {
/* 3055 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("CANRETURNJARS");
/* 3056 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public ResultSet getSchemas()
/*      */     throws SQLException
/*      */   {
/* 3064 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3066 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3068 */       LOG.fine(this._logId + " getSchemas()");
/*      */     }
/*      */ 
/* 3073 */     PreparedStatement localPreparedStatement = null;
/*      */ 
/* 3075 */     if (this._conn._props.getBoolean(64))
/*      */     {
/* 3077 */       localPreparedStatement = getMetaDataAccessor("SCHEMAS_CTS");
/* 3078 */       return returnResults(localPreparedStatement);
/*      */     }
/* 3080 */     return getSchemas(null, null);
/*      */   }
/*      */ 
/*      */   public ResultSet getCatalogs()
/*      */     throws SQLException
/*      */   {
/* 3088 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3090 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3092 */       LOG.fine(this._logId + " getCatalogs()");
/*      */     }
/*      */ 
/* 3097 */     PreparedStatement localPreparedStatement = null;
/*      */ 
/* 3099 */     if (this._conn._props.getBoolean(64))
/*      */     {
/* 3101 */       localPreparedStatement = getMetaDataAccessor("CATALOGS_CTS");
/*      */     }
/*      */     else
/*      */     {
/* 3105 */       localPreparedStatement = getMetaDataAccessor("CATALOGS");
/*      */     }
/* 3107 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getTableTypes()
/*      */     throws SQLException
/*      */   {
/* 3115 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3117 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3119 */       LOG.fine(this._logId + " getTableTypes()");
/*      */     }
/*      */ 
/* 3125 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TABLETYPES");
/* 3126 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getColumns(String paramString1, String paramString2, String paramString3, String paramString4)
/*      */     throws SQLException
/*      */   {
/* 3136 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3138 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3140 */         LOG.finer(this._logId + " getColumns(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "], String = [" + paramString4 + "])");
/*      */       }
/* 3145 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3147 */         LOG.fine(this._logId + " getColumns(String, String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3160 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("COLUMNS", paramString1);
/* 3161 */     setStringParam(localPreparedStatement, paramString3, 1);
/* 3162 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3163 */     setStringParam(localPreparedStatement, paramString1, 3);
/* 3164 */     setStringParam(localPreparedStatement, paramString4, 4);
/*      */ 
/* 3166 */     if (((SybPreparedStatement)localPreparedStatement)._paramCount >= 5)
/*      */     {
/* 3168 */       localPreparedStatement.setNull(5, 4);
/*      */     }
/* 3170 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getColumnPrivileges(String paramString1, String paramString2, String paramString3, String paramString4)
/*      */     throws SQLException
/*      */   {
/* 3179 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3181 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3183 */         LOG.finer(this._logId + " getColumnPrivileges(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "], String = [" + paramString4 + "])");
/*      */       }
/* 3187 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3189 */         LOG.fine(this._logId + " getColumnPrivileges(String, String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3196 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("COLUMNPRIVILEGES", paramString1);
/* 3197 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3198 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3199 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3200 */     setStringParam(localPreparedStatement, paramString4, 4);
/* 3201 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getTablePrivileges(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 3210 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3212 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3214 */         LOG.finer(this._logId + " getTablePrivileges(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "])");
/*      */       }
/* 3218 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3220 */         LOG.fine(this._logId + " getTablePrivileges(String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3227 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("TABLEPRIVILEGES", paramString1);
/* 3228 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3229 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3230 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3231 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getBestRowIdentifier(String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean)
/*      */     throws SQLException
/*      */   {
/* 3240 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3242 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3244 */         LOG.finer(this._logId + " getBestRowIdentifier(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "], int = [" + paramInt + "], boolean = [" + paramBoolean + "])");
/*      */       }
/* 3249 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3251 */         LOG.fine(this._logId + " getBestRowIdentifier(String, String, String, int, boolean)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3258 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ROWIDENTIFIERS", paramString1);
/* 3259 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3260 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3261 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3262 */     localPreparedStatement.setInt(4, paramInt);
/* 3263 */     localPreparedStatement.setBoolean(5, paramBoolean);
/* 3264 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getVersionColumns(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 3273 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3275 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3277 */         LOG.finer(this._logId + " getVersionColumns(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "])");
/*      */       }
/* 3281 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3283 */         LOG.fine(this._logId + " getVersionColumns(String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3289 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("VERSIONCOLUMNS", paramString1);
/* 3290 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3291 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3292 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3293 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getPrimaryKeys(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 3302 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3304 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3306 */         LOG.finer(this._logId + " getPrimaryKeys(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "])");
/*      */       }
/* 3310 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3312 */         LOG.fine(this._logId + " getPrimaryKeys(String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3318 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("PRIMARYKEYS", paramString1);
/* 3319 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3320 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3321 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3322 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getImportedKeys(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 3331 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3333 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3335 */         LOG.finer(this._logId + " getImportedKeys(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "])");
/*      */       }
/* 3339 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3341 */         LOG.fine(this._logId + " getImportedKeys(String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3347 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("IMPORTEDKEYS", paramString1);
/* 3348 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3349 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3350 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3351 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getExportedKeys(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 3360 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3362 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3364 */         LOG.finer(this._logId + " getExportedKeys(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "])");
/*      */       }
/* 3368 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3370 */         LOG.fine(this._logId + " getExportedKeys(String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3376 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("EXPORTEDKEYS", paramString1);
/* 3377 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3378 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3379 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3380 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getCrossReference(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
/*      */     throws SQLException
/*      */   {
/* 3391 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3393 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3395 */         LOG.finer(this._logId + " getCrossReference(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "], String = [" + paramString4 + "], String = [" + paramString5 + "], String = [" + paramString6 + "])");
/*      */       }
/* 3401 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3403 */         LOG.fine(this._logId + " getCrossReference(String, String, String," + " String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3410 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("KEYCROSSREFERENCE", paramString1);
/* 3411 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3412 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3413 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3414 */     setStringParam(localPreparedStatement, paramString4, 4);
/* 3415 */     setStringParam(localPreparedStatement, paramString5, 5);
/* 3416 */     setStringParam(localPreparedStatement, paramString6, 6);
/* 3417 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getTypeInfo()
/*      */     throws SQLException
/*      */   {
/* 3425 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3427 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3429 */       LOG.fine(this._logId + " getTypeInfo()");
/*      */     }
/*      */ 
/* 3435 */     PreparedStatement localPreparedStatement = null;
/* 3436 */     if (this._conn._props.getBoolean(64))
/*      */     {
/* 3438 */       localPreparedStatement = getMetaDataAccessor("TYPEINFO_CTS");
/*      */     }
/*      */     else
/*      */     {
/* 3442 */       localPreparedStatement = getMetaDataAccessor("TYPEINFO");
/*      */     }
/* 3444 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getIndexInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws SQLException
/*      */   {
/* 3454 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3456 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3458 */         LOG.finer(this._logId + " getIndexInfo(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "], boolean = [" + paramBoolean1 + "], boolean = [" + paramBoolean2 + "])");
/*      */       }
/* 3463 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3465 */         LOG.fine(this._logId + " getIndexInfo(String, String, String, boolean, boolean)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3472 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("INDEXINFO", paramString1);
/* 3473 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3474 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3475 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3476 */     localPreparedStatement.setBoolean(4, paramBoolean1);
/* 3477 */     localPreparedStatement.setBoolean(5, paramBoolean2);
/* 3478 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public boolean isCaseSensitive()
/*      */     throws SQLException
/*      */   {
/* 3489 */     if (this._isCaseSensitive == -1)
/*      */     {
/* 3491 */       PreparedStatement localPreparedStatement = getMetaDataAccessor("ISCASESENSITIVE");
/*      */ 
/* 3493 */       this._isCaseSensitive = ((returnBoolean(localPreparedStatement, 1)) ? 1 : 0);
/*      */     }
/* 3495 */     return this._isCaseSensitive == 1;
/*      */   }
/*      */ 
/*      */   public boolean supportsSavepoints()
/*      */     throws SQLException
/*      */   {
/* 3503 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3505 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3507 */       LOG.fine(this._logId + " supportSavepoints()");
/*      */     }
/*      */ 
/* 3512 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SAVEPOINTSUPPORT");
/* 3513 */     return returnBoolean(localPreparedStatement, 1);
/*      */   }
/*      */ 
/*      */   public boolean supportsNamedParameters()
/*      */     throws SQLException
/*      */   {
/* 3521 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3523 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3525 */       LOG.fine(this._logId + " supportsNamedParameters()");
/*      */     }
/*      */ 
/* 3530 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleOpenResults()
/*      */     throws SQLException
/*      */   {
/* 3538 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3540 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3542 */       LOG.fine(this._logId + " supportsMultipleOpenResults()");
/*      */     }
/*      */ 
/* 3547 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsGetGeneratedKeys()
/*      */     throws SQLException
/*      */   {
/* 3555 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3557 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3559 */       LOG.fine(this._logId + " supportsGetGeneratedKeys()");
/*      */     }
/*      */ 
/* 3564 */     return true;
/*      */   }
/*      */ 
/*      */   public ResultSet getSuperTypes(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 3573 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3575 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3577 */         LOG.finer(this._logId + " getSuperTypes(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "])");
/*      */       }
/* 3581 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3583 */         LOG.fine(this._logId + " getSuperTypes(String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3588 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SUPERTYPES", paramString1);
/* 3589 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3590 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3591 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3592 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getSuperTables(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 3601 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3603 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3605 */         LOG.finer(this._logId + " getSuperTables(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "])");
/*      */       }
/* 3609 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3611 */         LOG.fine(this._logId + " getSuperTables(String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3617 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("SUPERTABLES", paramString1);
/* 3618 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3619 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3620 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3621 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getAttributes(String paramString1, String paramString2, String paramString3, String paramString4)
/*      */     throws SQLException
/*      */   {
/* 3630 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3632 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3634 */         LOG.finer(this._logId + " getAttributes(String = [" + paramString1 + "], String = [" + paramString2 + ", String = " + paramString3 + "], String = [" + paramString4 + "])");
/*      */       }
/* 3639 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3641 */         LOG.fine(this._logId + " getAttributes(String, String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3646 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("ATTRIBUTES", paramString1);
/* 3647 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 3648 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 3649 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 3650 */     setStringParam(localPreparedStatement, paramString4, 4);
/* 3651 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 3659 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3661 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3663 */       LOG.fine(this._logId + " getResultHoldability()");
/*      */     }
/*      */ 
/* 3669 */     return 1;
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetHoldability(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 3678 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3680 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3682 */         LOG.finer(this._logId + " supportsResultSetHoldability(int = [" + paramInt + "])");
/*      */       }
/* 3685 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3687 */         LOG.fine(this._logId + " supportsResultSetHoldability(int)");
/*      */       }
/*      */     }
/*      */ 
/* 3691 */     if ((paramInt != 1) && (paramInt != 2))
/*      */     {
/* 3694 */       ErrorMessage.raiseError("JZ0SW", String.valueOf(paramInt));
/*      */     }
/*      */ 
/* 3698 */     return true;
/*      */   }
/*      */ 
/*      */   public int getSQLStateType()
/*      */     throws SQLException
/*      */   {
/* 3706 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3708 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3710 */       LOG.fine(this._logId + " getSQLStateType()");
/*      */     }
/*      */ 
/* 3716 */     return 2;
/*      */   }
/*      */ 
/*      */   public int getDatabaseMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 3724 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3726 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3728 */       LOG.fine(this._logId + " getDatabaseMajorVersion()");
/*      */     }
/*      */ 
/* 3735 */     if (this._dbMajorVersion == -1)
/*      */     {
/* 3738 */       String str1 = getDatabaseProductVersion();
/* 3739 */       int i = str1.indexOf("/");
/*      */       String str2;
/*      */       int j;
/* 3740 */       if (i == -1)
/*      */       {
/* 3743 */         str2 = str1;
/* 3744 */         str1 = str1.substring(0, str1.indexOf("."));
/* 3745 */         str2 = str2.substring(str2.indexOf(".") + 1, str2.length());
/*      */ 
/* 3747 */         j = str2.indexOf(".");
/* 3748 */         if (j != -1)
/*      */         {
/* 3750 */           str2 = str2.substring(0, j);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 3760 */         str2 = str1 = str1.substring(str1.indexOf("/") + 1, str1.length());
/*      */ 
/* 3762 */         str1 = str1.substring(0, str1.indexOf("/")).substring(0, str1.indexOf("."));
/*      */ 
/* 3764 */         str2 = str2.substring(str2.indexOf(".") + 1, str2.indexOf("/"));
/*      */ 
/* 3767 */         j = str2.indexOf(".");
/* 3768 */         if (j >= 0)
/*      */         {
/* 3770 */           str2 = str2.substring(0, j);
/*      */         }
/*      */       }
/* 3773 */       this._dbMajorVersion = Integer.valueOf(str1).intValue();
/* 3774 */       this._dbMinorVersion = Integer.valueOf(str2).intValue();
/*      */     }
/* 3776 */     return this._dbMajorVersion;
/*      */   }
/*      */ 
/*      */   public int getDatabaseMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 3784 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3786 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3788 */       LOG.fine(this._logId + " getDatabaseMinorVersion()");
/*      */     }
/*      */ 
/* 3794 */     if (this._dbMinorVersion == -1)
/*      */     {
/* 3796 */       getDatabaseMajorVersion();
/*      */     }
/* 3798 */     return this._dbMinorVersion;
/*      */   }
/*      */ 
/*      */   public int getJDBCMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 3806 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3808 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3810 */       LOG.fine(this._logId + " getJDBCMajorVersion()");
/*      */     }
/*      */ 
/* 3816 */     return 3;
/*      */   }
/*      */ 
/*      */   public int getJDBCMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 3824 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3826 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3828 */       LOG.fine(this._logId + " getJDBCMinorVersion()");
/*      */     }
/*      */ 
/* 3834 */     if (this._jdbcMinorVersion == -1)
/*      */     {
/* 3836 */       PreparedStatement localPreparedStatement = getMetaDataAccessor("JDBCMINORVERSION");
/* 3837 */       this._jdbcMinorVersion = returnInt(localPreparedStatement, 1);
/*      */     }
/* 3839 */     return this._jdbcMinorVersion;
/*      */   }
/*      */ 
/*      */   public boolean locatorsUpdateCopy()
/*      */     throws SQLException
/*      */   {
/* 3847 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3849 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3851 */       LOG.fine(this._logId + " locatorsUpdateCopy()");
/*      */     }
/*      */ 
/* 3855 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsStatementPooling()
/*      */     throws SQLException
/*      */   {
/* 3863 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 3865 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 3867 */       LOG.fine(this._logId + " supportsStatementPooling()");
/*      */     }
/*      */ 
/* 3871 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetType(int paramInt) throws SQLException
/*      */   {
/* 3876 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3878 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3880 */         LOG.finer(this._logId + " supportsResultSetType(int = [" + paramInt + "])");
/*      */       }
/* 3883 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3885 */         LOG.fine(this._logId + " supportsResultSetType(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3890 */     return supportForResultSetType("SUPPORTSRESULTSETTYPE", paramInt);
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetConcurrency(int paramInt1, int paramInt2)
/*      */     throws SQLException
/*      */   {
/* 3896 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3898 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3900 */         LOG.finer(this._logId + " supportsResultSetConcurrency(int = [" + paramInt1 + "], int = [" + paramInt2 + "])");
/*      */       }
/* 3903 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3905 */         LOG.fine(this._logId + " supportsResultSetConcurrency(int, int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3910 */     switch (paramInt2)
/*      */     {
/*      */     case 1007:
/* 3913 */       return supportForResultSetType("READONLYCONCURRENCY", paramInt1);
/*      */     case 1008:
/* 3915 */       return supportForResultSetType("UPDATABLECONCURRENCY", paramInt1);
/*      */     }
/* 3917 */     ErrorMessage.raiseError("JZ0SO", "" + paramInt2);
/*      */ 
/* 3921 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean ownUpdatesAreVisible(int paramInt) throws SQLException
/*      */   {
/* 3926 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3928 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3930 */         LOG.finer(this._logId + " ownUpdatesAreVisible(int = [" + paramInt + "])");
/*      */       }
/* 3933 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3935 */         LOG.fine(this._logId + " ownUpdatesAreVisible(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3940 */     return supportForResultSetType("OWNUPDATESAREVISIBLE", paramInt);
/*      */   }
/*      */ 
/*      */   public boolean ownDeletesAreVisible(int paramInt) throws SQLException
/*      */   {
/* 3945 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3947 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3949 */         LOG.finer(this._logId + " ownDeletesAreVisible(int = [" + paramInt + "])");
/*      */       }
/* 3952 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3954 */         LOG.fine(this._logId + " ownDeletesAreVisible(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3959 */     return supportForResultSetType("OWNDELETESAREVISIBLE", paramInt);
/*      */   }
/*      */ 
/*      */   public boolean ownInsertsAreVisible(int paramInt) throws SQLException
/*      */   {
/* 3964 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3966 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3968 */         LOG.finer(this._logId + " ownInsertsAreVisible(int = [" + paramInt + "])");
/*      */       }
/* 3971 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3973 */         LOG.fine(this._logId + " ownInsertsAreVisible(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3978 */     return supportForResultSetType("OWNINSERTSAREVISIBLE", paramInt);
/*      */   }
/*      */ 
/*      */   public boolean othersUpdatesAreVisible(int paramInt) throws SQLException
/*      */   {
/* 3983 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 3985 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 3987 */         LOG.finer(this._logId + " othersUpdatesAreVisible(int = [" + paramInt + "])");
/*      */       }
/* 3990 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 3992 */         LOG.fine(this._logId + " othersUpdatesAreVisible(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3997 */     return supportForResultSetType("OTHERSUPDATESAREVISIBLE", paramInt);
/*      */   }
/*      */ 
/*      */   public boolean othersDeletesAreVisible(int paramInt) throws SQLException
/*      */   {
/* 4002 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4004 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4006 */         LOG.finer(this._logId + " othersDeletesAreVisible(int = [" + paramInt + "])");
/*      */       }
/* 4009 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4011 */         LOG.fine(this._logId + " othersDeletesAreVisible(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4016 */     return supportForResultSetType("OTHERSDELETESAREVISIBLE", paramInt);
/*      */   }
/*      */ 
/*      */   public boolean othersInsertsAreVisible(int paramInt) throws SQLException
/*      */   {
/* 4021 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4023 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4025 */         LOG.finer(this._logId + " othersInsertsAreVisible(int = [" + paramInt + "])");
/*      */       }
/* 4028 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4030 */         LOG.fine(this._logId + " othersInsertsAreVisible(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4035 */     return supportForResultSetType("OTHERSINSERTSAREVISIBLE", paramInt);
/*      */   }
/*      */ 
/*      */   public boolean updatesAreDetected(int paramInt) throws SQLException
/*      */   {
/* 4040 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4042 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4044 */         LOG.finer(this._logId + " updatesAreDetected(int = [" + paramInt + "])");
/*      */       }
/* 4046 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4048 */         LOG.fine(this._logId + " updatesAreDetected(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4053 */     return supportForResultSetType("UPDATESAREDETECTED", paramInt);
/*      */   }
/*      */ 
/*      */   public boolean deletesAreDetected(int paramInt) throws SQLException
/*      */   {
/* 4058 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4060 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4062 */         LOG.finer(this._logId + " deletesAreDetected(int = [" + paramInt + "])");
/*      */       }
/* 4064 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4066 */         LOG.fine(this._logId + " deletesAreDetected(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4071 */     return supportForResultSetType("DELETESAREDETECTED", paramInt);
/*      */   }
/*      */ 
/*      */   public boolean insertsAreDetected(int paramInt) throws SQLException
/*      */   {
/* 4076 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4078 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4080 */         LOG.finer(this._logId + " insertsAreDetected(int = [" + paramInt + "])");
/*      */       }
/* 4082 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4084 */         LOG.fine(this._logId + " insertsAreDetected(int)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4089 */     return supportForResultSetType("INSERTSAREDETECTED", paramInt);
/*      */   }
/*      */ 
/*      */   public boolean supportsBatchUpdates() throws SQLException
/*      */   {
/* 4094 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 4096 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 4098 */       LOG.fine(this._logId + " supportsBatchUpdates()");
/*      */     }
/*      */ 
/* 4104 */     if (this._batchSupport != 1)
/*      */     {
/* 4106 */       PreparedStatement localPreparedStatement = getMetaDataAccessor("SUPPORTSBATCHUPDATES");
/* 4107 */       boolean bool = returnBoolean(localPreparedStatement, 1);
/* 4108 */       if (!bool)
/*      */       {
/* 4110 */         this._batchSupport = 0;
/* 4111 */         ErrorMessage.raiseError("JZ0BS");
/*      */       }
/*      */ 
/* 4115 */       this._batchSupport = 1;
/*      */     }
/* 4117 */     return this._batchSupport == 1;
/*      */   }
/*      */ 
/*      */   public ResultSet getUDTs(String paramString1, String paramString2, String paramString3, int[] paramArrayOfInt)
/*      */     throws SQLException
/*      */   {
/* 4123 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4125 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4127 */         LOG.finest(LogUtil.logMethod(false, this._logId, " getUDTs", new Object[] { paramString1, paramString2, paramString3, paramArrayOfInt }));
/*      */       }
/* 4131 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4133 */         LOG.finer(LogUtil.logMethod(true, this._logId, " getUDTs", new Object[] { paramString1, paramString2, paramString3, paramArrayOfInt }));
/*      */       }
/* 4137 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4139 */         LOG.fine(this._logId + " getUDTs(String, String, String, int[])");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4144 */     String str = null;
/* 4145 */     if (paramArrayOfInt != null)
/*      */     {
/* 4147 */       str = "";
/* 4148 */       for (int i = 0; i < paramArrayOfInt.length; ++i)
/*      */       {
/* 4151 */         if (i > 0)
/*      */         {
/* 4153 */           str = str + ",";
/*      */         }
/* 4155 */         switch (paramArrayOfInt[i])
/*      */         {
/*      */         case 2000:
/* 4158 */           str = str + "JAVA_OBJECT";
/* 4159 */           break;
/*      */         case 2002:
/* 4161 */           str = str + "STRUCT";
/* 4162 */           break;
/*      */         case 2001:
/* 4164 */           str = str + "DISTINCT";
/* 4165 */           break;
/*      */         default:
/* 4167 */           ErrorMessage.raiseError("JZ0SQ", "" + paramArrayOfInt[i]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4174 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("UDTS", paramString1);
/* 4175 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 4176 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 4177 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 4178 */     setStringParam(localPreparedStatement, str, 4);
/* 4179 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getXACoordinatorType()
/*      */     throws SQLException
/*      */   {
/* 4186 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("XACOORDINATORTYPE");
/* 4187 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public Connection getConnection() throws SQLException
/*      */   {
/* 4192 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 4194 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 4196 */       LOG.fine(this._logId + " getConnection()");
/*      */     }
/*      */ 
/* 4201 */     return this._conn;
/*      */   }
/*      */ 
/*      */   public PreparedStatement getMetaDataAccessor(String paramString)
/*      */     throws SQLException
/*      */   {
/* 4213 */     return getMetaDataAccessor(paramString, "", null);
/*      */   }
/*      */ 
/*      */   public PreparedStatement getMetaDataAccessor(String paramString1, String paramString2)
/*      */     throws SQLException
/*      */   {
/* 4220 */     return getMetaDataAccessor(paramString1, "", paramString2);
/*      */   }
/*      */ 
/*      */   public PreparedStatement getMetaDataAccessor(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 4228 */     return this._mda.getMetaDataAccessor(paramString1, paramString2, paramString3, null);
/*      */   }
/*      */ 
/*      */   private boolean supportForResultSetType(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 4237 */     boolean bool = false;
/* 4238 */     PreparedStatement localPreparedStatement = getMetaDataAccessor(paramString);
/* 4239 */     switch (paramInt)
/*      */     {
/*      */     case 1003:
/* 4242 */       bool = returnBoolean(localPreparedStatement, 1);
/* 4243 */       break;
/*      */     case 1004:
/* 4245 */       bool = returnBoolean(localPreparedStatement, 2);
/* 4246 */       break;
/*      */     case 1005:
/* 4248 */       bool = returnBoolean(localPreparedStatement, 3);
/* 4249 */       break;
/*      */     default:
/* 4251 */       ErrorMessage.raiseError("JZ0SP", "" + paramInt);
/*      */     }
/*      */ 
/* 4254 */     return bool;
/*      */   }
/*      */ 
/*      */   private void setStringParam(PreparedStatement paramPreparedStatement, String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 4261 */     if (paramString == null)
/*      */     {
/* 4263 */       paramPreparedStatement.setNull(paramInt, 12);
/*      */     }
/*      */     else
/*      */     {
/* 4267 */       paramPreparedStatement.setString(paramInt, paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */   private ResultSet returnResults(PreparedStatement paramPreparedStatement)
/*      */     throws SQLException
/*      */   {
/* 4276 */     ResultSet localResultSet = null;
/* 4277 */     Object localObject1 = null;
/* 4278 */     SQLWarning localSQLWarning = null;
/*      */     try
/*      */     {
/* 4299 */       throw localObject1;
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 4285 */       localObject1 = localSQLException;
/* 4286 */       localSQLWarning = paramPreparedStatement.getWarnings();
/* 4287 */       if (localSQLWarning != null)
/*      */       {
/* 4291 */         this._conn.handleSQLE(localSQLWarning);
/*      */       }
/*      */ 
/* 4299 */       throw localObject1;
/*      */     }
/*      */     finally
/*      */     {
/* 4297 */       if (localObject1 != null)
/*      */       {
/* 4299 */         throw localObject1;
/*      */       }
/*      */     }
/* 4302 */     return localResultSet;
/*      */   }
/*      */ 
/*      */   private boolean returnBoolean(PreparedStatement paramPreparedStatement, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 4310 */     ResultSet localResultSet = null;
/* 4311 */     boolean bool = false;
/* 4312 */     Object localObject1 = null;
/*      */     try
/*      */     {
/* 4315 */       localResultSet = paramPreparedStatement.executeQuery();
/* 4316 */       localResultSet.next();
/* 4317 */       bool = localResultSet.getBoolean(paramInt);
/* 4318 */       localResultSet.close();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 4323 */       localObject1 = localSQLException;
/*      */     }
/*      */     finally
/*      */     {
/* 4327 */       paramPreparedStatement.close();
/* 4328 */       if (localObject1 != null)
/*      */       {
/* 4330 */         throw localObject1;
/*      */       }
/*      */     }
/* 4333 */     return bool;
/*      */   }
/*      */ 
/*      */   private int returnInt(PreparedStatement paramPreparedStatement, int paramInt)
/*      */     throws SQLException
/*      */   {
/* 4341 */     ResultSet localResultSet = null;
/* 4342 */     int i = -1;
/* 4343 */     Object localObject1 = null;
/*      */     try
/*      */     {
/* 4346 */       localResultSet = paramPreparedStatement.executeQuery();
/* 4347 */       localResultSet.next();
/* 4348 */       i = localResultSet.getInt(paramInt);
/* 4349 */       localResultSet.close();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 4354 */       localObject1 = localSQLException;
/*      */     }
/*      */     finally
/*      */     {
/* 4358 */       paramPreparedStatement.close();
/* 4359 */       if (localObject1 != null)
/*      */       {
/* 4361 */         throw localObject1;
/*      */       }
/*      */     }
/* 4364 */     return i;
/*      */   }
/*      */ 
/*      */   private String returnString(PreparedStatement paramPreparedStatement)
/*      */     throws SQLException
/*      */   {
/* 4372 */     ResultSet localResultSet = null;
/* 4373 */     String str = null;
/* 4374 */     Object localObject1 = null;
/*      */     try
/*      */     {
/* 4377 */       localResultSet = paramPreparedStatement.executeQuery();
/* 4378 */       localResultSet.next();
/* 4379 */       str = localResultSet.getString(1);
/* 4380 */       localResultSet.close();
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 4385 */       localObject1 = localSQLException;
/*      */     }
/*      */     finally
/*      */     {
/* 4389 */       paramPreparedStatement.close();
/* 4390 */       if (localObject1 != null)
/*      */       {
/* 4392 */         throw localObject1;
/*      */       }
/*      */     }
/* 4395 */     return str;
/*      */   }
/*      */ 
/*      */   public boolean autoCommitFailureClosesAllResultSets()
/*      */     throws SQLException
/*      */   {
/* 4413 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 4415 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 4417 */       LOG.fine(this._logId + " autoCommitFailureClosesAllResultSets()");
/*      */     }
/*      */ 
/* 4421 */     return false;
/*      */   }
/*      */ 
/*      */   public ResultSet getFunctions(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/* 4427 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4429 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4431 */         LOG.finer(this._logId + " getFunctions(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "])");
/*      */       }
/* 4435 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4437 */         LOG.fine(this._logId + " getFunctions(String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4443 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("FUNCTIONS", paramString1);
/* 4444 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 4445 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 4446 */     setStringParam(localPreparedStatement, paramString3, 3);
/*      */ 
/* 4448 */     if (((SybPreparedStatement)localPreparedStatement)._paramCount >= 5)
/*      */     {
/* 4450 */       localPreparedStatement.setNull(4, 4);
/* 4451 */       localPreparedStatement.setInt(5, 1);
/*      */     }
/* 4453 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getFunctionColumns(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException
/*      */   {
/* 4458 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4460 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4462 */         LOG.finer(this._logId + " getFunctionColumns(String = [" + paramString1 + "], String = [" + paramString2 + "], String = [" + paramString3 + "], String = [" + paramString4 + "])");
/*      */       }
/* 4467 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4469 */         LOG.fine(this._logId + " getFunctionColumns(String, String, String, String)");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4476 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("FUNCTIONCOLUMNS", paramString1);
/* 4477 */     setStringParam(localPreparedStatement, paramString1, 1);
/* 4478 */     setStringParam(localPreparedStatement, paramString2, 2);
/* 4479 */     setStringParam(localPreparedStatement, paramString3, 3);
/* 4480 */     setStringParam(localPreparedStatement, paramString4, 4);
/* 4481 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public ResultSet getSchemas(String paramString1, String paramString2)
/*      */     throws SQLException
/*      */   {
/* 4488 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4490 */       if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4492 */         LOG.finer(this._logId + " getSchemas(String = [" + paramString1 + "], String = [" + paramString2 + "])");
/*      */       }
/* 4495 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4497 */         LOG.fine(this._logId + " getSchemas(String, String)");
/*      */       }
/*      */     }
/*      */ 
/* 4501 */     PreparedStatement localPreparedStatement = null;
/*      */ 
/* 4503 */     localPreparedStatement = getMetaDataAccessor("SCHEMAS");
/*      */ 
/* 4506 */     if (((SybPreparedStatement)localPreparedStatement)._paramCount >= 2)
/*      */     {
/* 4508 */       setStringParam(localPreparedStatement, paramString1, 1);
/* 4509 */       setStringParam(localPreparedStatement, paramString2, 2);
/*      */     }
/* 4511 */     return returnResults(localPreparedStatement);
/*      */   }
/*      */ 
/*      */   public boolean supportsStoredFunctionsUsingCallSyntax()
/*      */     throws SQLException
/*      */   {
/* 4517 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 4519 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 4521 */       LOG.fine(this._logId + " supportsStoredFunctionsUsingCallSyntax()");
/*      */     }
/*      */ 
/* 4525 */     return false;
/*      */   }
/*      */ 
/*      */   public ResultSet getClientInfoProperties()
/*      */     throws SQLException
/*      */   {
/* 4531 */     if ((LogUtil.isLoggingEnabled(LOG)) && 
/* 4533 */       (LOG.isLoggable(Level.FINE)))
/*      */     {
/* 4535 */       LOG.fine(this._logId + " getClientInfoProperties()");
/*      */     }
/*      */ 
/* 4539 */     PreparedStatement localPreparedStatement = getMetaDataAccessor("GETCLIENTINFOPROPERTIES");
/* 4540 */     localPreparedStatement.execute();
/* 4541 */     ResultSet localResultSet = localPreparedStatement.getResultSet();
/* 4542 */     return localResultSet;
/*      */   }
/*      */ 
/*      */   public boolean isWrapperFor(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 4550 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4552 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4554 */         LOG.finest(LogUtil.logMethod(false, this._logId, " isWrapperFor", new Object[] { paramClass }));
/*      */       }
/* 4557 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4559 */         LOG.finer(LogUtil.logMethod(true, this._logId, " isWrapperFor", new Object[] { paramClass }));
/*      */       }
/* 4562 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4564 */         LOG.fine(this._logId + " isWrapperFor(Class<?>)");
/*      */       }
/*      */     }
/*      */ 
/* 4568 */     return paramClass.isInstance(this);
/*      */   }
/*      */ 
/*      */   public Object unwrap(Class paramClass)
/*      */     throws SQLException
/*      */   {
/* 4576 */     if (LogUtil.isLoggingEnabled(LOG))
/*      */     {
/* 4578 */       if (LOG.isLoggable(Level.FINEST))
/*      */       {
/* 4580 */         LOG.finest(LogUtil.logMethod(false, this._logId, " unwrap", new Object[] { paramClass }));
/*      */       }
/* 4583 */       else if (LOG.isLoggable(Level.FINER))
/*      */       {
/* 4585 */         LOG.finer(LogUtil.logMethod(true, this._logId, " unwrap", new Object[] { paramClass }));
/*      */       }
/* 4588 */       else if (LOG.isLoggable(Level.FINE))
/*      */       {
/* 4590 */         LOG.fine(this._logId + " unwrap(Class<T>)");
/*      */       }
/*      */     }
/* 4593 */     SybDatabaseMetaData localSybDatabaseMetaData = null;
/*      */     try
/*      */     {
/* 4596 */       localSybDatabaseMetaData = this;
/*      */     }
/*      */     catch (ClassCastException localClassCastException)
/*      */     {
/* 4600 */       ErrorMessage.raiseError("JZ031", paramClass.getName());
/*      */     }
/*      */ 
/* 4603 */     return localSybDatabaseMetaData;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybDatabaseMetaData
 * JD-Core Version:    0.5.4
 */