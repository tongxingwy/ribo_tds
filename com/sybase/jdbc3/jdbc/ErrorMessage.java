/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.tds.Language;
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import com.sybase.jdbc3.utils.UnimplementedOperationException;
/*      */ import java.io.IOException;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.io.PrintStream;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.util.ResourceBundle;
/*      */ import org.ietf.jgss.GSSException;
/*      */ 
/*      */ public class ErrorMessage
/*      */ {
/*      */   private static ResourceBundle _messages;
/*      */   public static final String ERR_LOADING_SYBSOCKET_FACTORY = "JZ0NF";
/*      */   public static final String ERR_URL_AND_SYBSOCKET_FACTORY = "JZ0US";
/*      */   public static final String ERR_USERNAME_TOO_LONG = "JZ001";
/*      */   public static final String ERR_PASSWORD_TOO_LONG = "JZ002";
/*      */   public static final String ERR_URL_FORMAT_ERROR = "JZ003";
/*      */   public static final String ERR_URL_FORMAT_ERROR_WITH_NFE = "JZ0NE";
/*      */   public static final String ERR_BAD_PORT_NUMBER = "JZ0PN";
/*      */   public static final String WARN_USE_FAILED = "010UF";
/*      */   public static final String WARN_LOGIN_DATABASE_FAILED = "010DF";
/*      */   public static final String ERR_BAD_CONVERT_TYPE_COMBINATION = "JZ0TC";
/*      */   public static final String ERR_BAD_CONVERT_TYPE_COMB_VALUES = "JZ0TE";
/*      */   public static final String ERR_ILLEGAL_TYPE_CONVERSION = "JZ0TI";
/*      */   public static final String ERR_ILLEGAL_HEX_CHAR = "JZ0HC";
/*      */   public static final String IO_TRUNCATION_WITH_STRING = "JZ0TS";
/*      */   public static final String IO_THREAD_DEATH = "JZ0TD";
/*      */   public static final String ERR_USERNAME_MISSING = "JZ004";
/*      */   public static final String ERR_BAD_COOKIE = "JZ0BC";
/*      */   public static final String ERR_BAD_COOKIE_MESSAGE = "JZ0BM";
/*      */   public static final String ERR_IO_EXCEPTION = "JZ006";
/*      */   public static final String ERR_BAD_COLUMN_INDEX = "JZ008";
/*      */   public static final String ERR_WASNULL_NO_COLUMN = "JZ0NC";
/*      */   public static final String ERR_BAD_CONVERT = "JZ009";
/*      */   public static final String ERR_BAD_PREC_SCALE = "JZ00A";
/*      */   public static final String ERR_PREC_SCALE_TOO_SMALL = "JZ00C";
/*      */   public static final String ERR_SCALE_TOO_SMALL = "JZ00I";
/*      */   public static final String ERR_LOGIN = "JZ00L";
/*      */   public static final String ERR_LOGIN_TIMEOUT = "JZ00M";
/*      */   public static final String ERR_READ_TIMEOUT_SQLEX = "JZ0TO";
/*      */   public static final String WARN_HA_REQUEST_DENIED = "010HA";
/*      */   public static final String WARN_HA_FAILOVER_NOT_SUPPORTED = "010HD";
/*      */   public static final String ERR_HA_SECONDARY_MISSING = "JZ0F1";
/*      */   public static final String ERR_HA_FAILOVER = "JZ0F2";
/*      */   public static final String ERR_HADR_FAILOVER = "JZ0F3";
/*      */   public static final String WARN_KERBEROS_LOGIN_FAILED = "010KF";
/*      */   public static final String WARN_TDS_VERSION = "0100V";
/*      */   public static final String ERR_NUMERIC_OFLO = "JZ00B";
/*      */   public static final String ERR_CUR_NOT_FOUND = "JZ00D";
/*      */   public static final String ERR_CURSOR_ALREADY = "JZ00F";
/*      */   public static final String ERR_CURSOR_IN_USE = "JZ00E";
/*      */   public static final String ERR_UPDATE_NOT_SET = "JZ00G";
/*      */   public static final String ERR_NOT_UPDATABLE = "JZ00H";
/*      */   public static final String ERR_ROW_MODIFIED = "JZ0RM";
/*      */   public static final String ERR_ROW_DELETED = "JZ0RD";
/*      */   public static final String ERR_INVALID_READER = "JZ0IR";
/*      */   public static final String ERR_INVALID_STREAM = "JZ0IS";
/*      */   public static final String ERR_LANGUAGE_CURSOR_CANT_SCROLL = "JZ0LC";
/*      */   public static final String ERR_BAD_METHOD_FOR_TYPE = "JZ0BT";
/*      */   public static final String ERR_BAD_METHOD_FOR_ROW = "JZ0BR";
/*      */   public static final String WARN_RESULTSET_TYPE_CHANGE = "010RC";
/*      */   public static final String ERR_BAD_DATA = "JZ0BD";
/*      */   public static final String INFO_EXPIRES_SOON = "000D3";
/*      */   public static final String ERR_BAD_PROTOCOL = "JZ0D4";
/*      */   public static final String ERR_LOADING_PROTOCOL = "JZ0D5";
/*      */   public static final String ERR_UNKNOWN_VERSION = "JZ0D6";
/*      */   public static final String ERR_LOADING_URL_PROVIDER = "JZ0D7";
/*      */   public static final String ERR_CONNECTION_PROP = "JZ011";
/*      */   public static final String ERR_PROPERTY_ACCESS = "JZ012";
/*      */   public static final String ERR_JNDI_ENTRY = "JZ013";
/*      */   public static final String ERR_TRANS_NONE = "JZ014";
/*      */   public static final String ERR_GSSMANAGER_CONN_PROP = "JZ015";
/*      */   public static final String ERR_INVALID_SAVEPOINT = "JZ017";
/*      */   public static final String ERR_SAVEPOINT_NAME_ID = "JZ018";
/*      */   public static final String ERR_SQLINI_SERVERNAME = "JZ019";
/*      */   public static final String ERR_FILE_NOT_FOUND = "JZ021";
/*      */   public static final String ERR_FORMAT_ERROR = "JZ022";
/*      */   public static final String ERR_SQLINI_SERVER_ENTRY_NOTFOUND = "JZ024";
/*      */   public static final String ERR_SQLINI_PROTOCOL_NOTSUPPORTED = "JZ026";
/*      */   public static final String ERR_SQLINI_KERBEROS_NOTSUPPORTED = "JZ027";
/*      */   public static final String ERR_JCE_PROVIDER_CLASS = "JZ028";
/*      */   public static final String ERR_LOOKUP_ASA = "JZ029";
/*      */   public static final String WARN_DUPLICATE_PROPERTY = "010DP";
/*      */   public static final String WARN_UNKNOWN_PROPERTY = "010UP";
/*      */   public static final String WARN_HOSTNAME_TRUNCATED = "010HT";
/*      */   public static final String WARN_LITERAL_PARAM_OVERRIDE = "010PO";
/*      */   public static final String WARN_COMMIT_PENDING_STMTS = "010CP";
/*      */   public static final String ERR_CONNECTION_DEAD = "JZ0C0";
/*      */   public static final String ERR_IOE_KILLED_CONNECTION = "JZ0C1";
/*      */   public static final String ERR_STATEMENT_IDLE = "JZ0S1";
/*      */   public static final String ERR_STATEMENT_CLOSED = "JZ0S2";
/*      */   public static final String ERR_RESULTSET_DEAD = "JZ0R0";
/*      */   public static final String ERR_RESULTSET_IDLE = "JZ0R1";
/*      */   public static final String ERR_RESULTSET_NULL = "JZ0R2";
/*      */   public static final String ERR_READ_PAST_RESULTSET = "JZ0R5";
/*      */   public static final String ERR_INVALID_COLUMN_NAME = "S0022";
/*      */   public static final String ERR_COLUMN_DEAD = "JZ0R3";
/*      */   public static final String ERR_BAD_TXTPTR = "JZ0R4";
/*      */   public static final String ERR_WRONG_LENGTH = "JZ0J0";
/*      */   public static final String ERR_ILLEGAL_FETCH_SIZE = "JZ0BI";
/*      */   public static final String ERR_ILLEGAL_IMPLICIT_CURSOR_FETCH_SIZE = "JZ0BJ";
/*      */   public static final String ERR_UNEXPECTED_RESULTTYPE = "JZ0P1";
/*      */   public static final String WARN_UNPROCESSED_PARAM = "010P4";
/*      */   public static final String WARN_UNPROCESSED_ROW = "010P6";
/*      */   public static final String ERR_PROTOCOL_ERROR = "JZ0P4";
/*      */   public static final String ERR_NOT_CACHED = "JZ0P7";
/*      */   public static final String ERR_EVENT_INIT = "JZ0H0";
/*      */   public static final String ERR_EVENT_NOTFOUND = "JZ0H1";
/*      */   public static final String ERR_BAD_ARGUMENT = "JZ0I3";
/*      */   public static final String ERR_BAD_CHARSET = "JZ0I5";
/*      */   public static final String ERR_CHARSET_CONVERT = "JZ0I6";
/*      */   public static final String ERR_SERVER_CHARSET_NOT_SUPPORTED_IN_JAVA = "JZ0IB";
/*      */   public static final String IO_NO_GATEWAY_RESPONSE = "JZ0I7";
/*      */   public static final String IO_GATEWAY_REFUSED = "JZ0I8";
/*      */   public static final String IO_TRUNCATION = "JZ0IA";
/*      */   public static final String IO_INPUTSTREAM_CLOSED = "JZ0I9";
/*      */   public static final String ERR_READ_STREAM = "JZ0T2";
/*      */   public static final String ERR_READ_TIMEOUT = "JZ0T3";
/*      */   public static final String ERR_WRITE_TIMEOUT = "JZ0T4";
/*      */   public static final String IO_CACHE_FULL = "JZ0T5";
/*      */   public static final String ERR_TUNNELLED_URL = "JZ0T6";
/*      */   public static final String ERR_READ_STREAM_THREAD_DEATH = "JZ0T7";
/*      */   public static final String ERR_READ_STREAM_SYNC = "JZ0T8";
/*      */   public static final String ERR_READ_EOM = "JZ0EM";
/*      */   public static final String ERR_ESCAPE_SYNTAX = "JZ0S8";
/*      */   public static final String ERR_NO_FUNCTION_INFO = "JZ0SH";
/*      */   public static final String ERR_FUNCTION_ESCAPE_NOT_IMPL = "JZ0SI";
/*      */   public static final String ERR_METADATA_INFO = "JZ0SJ";
/*      */   public static final String WARN_METADATA_INFO_WITH_EXCEPTION = "010MX";
/*      */   public static final String WARN_METADATA_INFO = "010SJ";
/*      */   public static final String WARN_OLD_METADATA_INFO = "010SL";
/*      */   public static final String ERR_RSMD_NOT_AVAILABLE = "JZ0MD";
/*      */   public static final String ERR_OUTER_JOINS_NOT_SUPPORTED = "JZ0SK";
/*      */   public static final String WARN_OPT_NOT_AVAIL = "010SK";
/*      */   public static final String ERR_INPARAM_NOT_SET = "JZ0SA";
/*      */   public static final String ERR_BAD_PARAM_INDEX = "JZ0SB";
/*      */   public static final String ERR_BAD_INPARAM_INDEX = "JZ0SC";
/*      */   public static final String ERR_NO_OUTPARAM = "JZ0SD";
/*      */   public static final String ERR_NOT_JDBC_OBJ = "JZ0SE";
/*      */   public static final String ERR_NOT_EXPECTING_PARAM = "JZ0SF";
/*      */   public static final String ERR_MISSING_PARAMS = "JZ0SG";
/*      */   public static final String ERR_SET_PARAM_MIXED = "JZ0SV";
/*      */   public static final String ERR_BAD_DATETIME_PARAM = "JZ0SU";
/*      */   public static final String ERR_INVALID_METHOD = "JZ0S3";
/*      */   public static final String ERR_EMPTY_QUERY = "JZ0S4";
/*      */   public static final String ERR_SQL_TYPE = "JZ0SL";
/*      */   public static final String ERR_CANT_SEND_LITERAL = "JZ0SM";
/*      */   public static final String ERR_NEGATIVE_FIELD_SIZE = "JZ0SN";
/*      */   public static final String ERR_INVALID_RESULTSET_CONCUR_TYPE = "JZ0SO";
/*      */   public static final String ERR_INVALID_RESULTSET_TYPE = "JZ0SP";
/*      */   public static final String ERR_INVALID_RESULTSET_HOLD_TYPE = "JZ0SW";
/*      */   public static final String ERR_INVALID_UDT_TYPE = "JZ0SQ";
/*      */   public static final String ERR_NEGATIVE_MAXROW_SIZE = "JZ0SR";
/*      */   public static final String ERR_NEGATIVE_TIMEOUT_SIZE = "JZ0SS";
/*      */   public static final String ERR_JAVA_OBJECT_AS_LITERAL = "JZ0ST";
/*      */   public static final String ERR_COLUMNTYPE_UNKN = "JZ0P8";
/*      */   public static final String ERR_CANCELLED = "JZ0PA";
/*      */   public static final String ERR_NOT_IMPLEMENTED = "ZZ00A";
/*      */   public static final String ERR_NOT_SUPPORTED = "JZ0NS";
/*      */   public static final String ERR_ASSERT_FAILED = "JZ0AF";
/*      */   public static final String WARN_ASSERT_FAILED = "010AF";
/*      */   public static final String ERR_NO_OUTPARAMS_ALLOWED = "JZ0BP";
/*      */   public static final String ERR_BATCH_STMTS_NOTSUPPORTED = "JZ0BS";
/*      */   public static final String ERR_BATCH_UPDATE_EXCEPTION = "JZ0BE";
/*      */   public static final String WARN_CAPABILITY_MISMATCH = "010SM";
/*      */   public static final String ERR_DESERIALIZATION = "JZ010";
/*      */   public static final String WARN_WRITE_ACCESS_DENIED = "010SN";
/*      */   public static final String WARN_FILEIO_FAILED = "010SP";
/*      */   public static final String WARN_SERVER_CHARSET_USED = "010TP";
/*      */   public static final String WARN_USING_ASCII_CHARSET = "010TQ";
/*      */   public static final String ERR_UNSUPPORTED_CAPABILITY = "JZ0PB";
/*      */   public static final String ERR_NO_XA_SUPPORT = "JZ0XS";
/*      */   public static final String ERR_UNRECOGNIZED_XA_COORD = "JZ0XC";
/*      */   public static final String ERR_NOT_XA_USER = "JZ0XU";
/*      */   public static final String ERR_PARAMS_NEED_WIDETABLE = "JZ0PC";
/*      */   public static final String ERR_DYNAMIC_NEEDS_WIDETABLE = "JZ0PD";
/*      */   public static final String ERR_CURDECLARE_NEEDS_WIDETABLE = "JZ0PE";
/*      */   public static final String WARN_CONNECTION_LOGIN_REFUSED = "010SQ";
/*      */   public static final String WARN_PRELOAD_FAILED = "010PF";
/*      */   public static final String ERR_NO_CLASSLOADER_SUPPLIED = "JZ0CL";
/*      */   public static final String ERR_GSS_EXCEPTION = "JZ0GS";
/*      */   public static final String ERR_BAD_GSSMANAGER_CLASS_NAME = "JZ0GN";
/*      */   public static final String ERR_BAD_GSSMANAGER_CLASS = "JZ0GC";
/*      */   public static final String WARN_USE_HOSTNAME_FOR_SERVICE_PRINCIPAL = "010HN";
/*      */   public static final String WARN_SETNANOS_TRUNCATED = "01S07";
/*      */   public static final String WARN_LOCALTX_ROLLEDBACK = "01S08";
/*      */   public static final String WARN_GLOBAL_TRAN_IN_PROGRESS = "01S09";
/*      */   public static final String WARN_GLOBAL_PRE_12 = "01S10";
/*      */   public static final String ERR_BAD_FUNCTION_PARAM = "JZ0FP";
/*      */   public static final String ERR_BAD_GEN_KEY_COLUMNS = "JZ0GK";
/*      */   public static final String ERR_NO_GEN_KEYS_USED = "JZ0NK";
/*      */   public static final String ERR_LOADING_CIPHER = "JZ0LA";
/*      */   public static final String ERR_SYBBCP_NOT_INITIALIZED = "JZBK1";
/*      */   public static final String ERR_BULKLOAD_TABLE_DOES_NOT_EXIST = "JZBK3";
/*      */   public static final String ERR_PASSWORD_EXPIRED = "01ZZZ";
/*      */   public static final String NULL_NOT_ALLOWED = "JZNNA";
/*      */   public static final String ERR_INVALID_BULKLOAD_VALUE = "JZBKI";
/*      */   public static final String ERR_ILLEGAL_BCP_USAGE = "JZBK4";
/*      */   public static final String ERR_BCP_AUTOCOMMIT = "JZBK5";
/*      */   public static final String ERR_BCP_WIDE_DOL_NOT_SUPPORTED = "JZBK6";
/*      */   public static final String ERR_BCP_DATA_EXCEEDED_ROW_LIMITS = "JZBK7";
/*      */   public static final String ERR_SQL_FEATURE_NOT_SUPPORTED = "JZ030";
/*      */   public static final String ERR_UNWRAP_FAILURE = "JZ031";
/*      */   public static final String WARN_LENGTH_CASTED_LONG_TO_INT = "01S11";
/*      */   public static final String ERR_BAD_BLOBTYPE = "JZ033";
/*      */   public static final String ERR_LOB_INVALID = "JZ036";
/*      */   public static final String ERR_OFFSET_INVALID = "JZ037";
/*      */   public static final String ERR_LENGTH_LESS_THAN_ZERO = "JZ038";
/*      */   public static final String ERR_LENGTH_LESS_THAN_MINUS_ONE = "JZ039";
/*      */   public static final String ERR_STREAM_CLOSED = "JZ040";
/*      */   public static final String ERR_STREAM_OP_FAILED = "JZ041";
/*      */   public static final String ERR_LOB_SETTER_USED_WITH_OTHER_SETTERS = "JZ042";
/*      */   public static final String WARN_FALLING_TO_HETEROGENEOUS_BATCH = "01S12";
/*      */   public static final String ERR_LOB_SETTER_NOT_ALLOWED_IN_BULK_LOAD = "JZ043";
/*      */   public static final String ERR_LOB_CREATION_NOT_ALLOWED_WITHIN_SEND_BATCHPARAMS_IMMEDIATE = "JZ044";
/*      */   public static final String WARN_FALLING_TO_NON_LOG_BCP = "01S13";
/*      */   public static final String ERR_NO_ENOUGH_MEMORY_FOR_ADDING_BATCH = "JZ045";
/*      */   public static final String ERR_INVALID_INTERNAL_READ_BUFFER_LIMIT = "JZ046";
/*      */ 
/*      */   public static void raiseError(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLException
/*      */   {
/*  716 */     SQLException localSQLException = new SQLException(makeMessage(true, paramString1, paramString2, paramString3), paramString1);
/*      */ 
/*  718 */     throw localSQLException;
/*      */   }
/*      */ 
/*      */   public static void raiseError(String paramString1, String paramString2, String paramString3, String paramString4)
/*      */     throws SQLException
/*      */   {
/*  735 */     SQLException localSQLException = new SQLException(makeMessage(true, paramString1, paramString2, paramString3, paramString4), paramString1);
/*      */ 
/*  738 */     throw localSQLException;
/*      */   }
/*      */ 
/*      */   public static void raiseError(String paramString1, String paramString2)
/*      */     throws SQLException
/*      */   {
/*  753 */     SQLException localSQLException = new SQLException(makeMessage(true, paramString1, paramString2), paramString1);
/*      */ 
/*  755 */     throw localSQLException;
/*      */   }
/*      */ 
/*      */   public static void raiseError(String paramString)
/*      */     throws SQLException
/*      */   {
/*  766 */     SQLException localSQLException = new SQLException(makeMessage(true, paramString), paramString);
/*      */ 
/*  769 */     throw localSQLException;
/*      */   }
/*      */ 
/*      */   public static void raiseError(String paramString, SQLException paramSQLException)
/*      */     throws SQLException
/*      */   {
/*  784 */     SQLException localSQLException = new SQLException(makeMessage(true, paramString), paramString);
/*      */ 
/*  786 */     localSQLException.setNextException(paramSQLException);
/*  787 */     if (paramSQLException != null)
/*      */     {
/*  789 */       localSQLException.initCause(paramSQLException.getCause());
/*      */     }
/*      */ 
/*  792 */     throw localSQLException;
/*      */   }
/*      */ 
/*      */   public static void raiseError(String paramString, int paramInt)
/*      */     throws SQLException
/*      */   {
/*  805 */     SQLException localSQLException = new SQLException(makeMessage(true, paramString), paramString, paramInt);
/*      */ 
/*  808 */     throw localSQLException;
/*      */   }
/*      */ 
/*      */   public static void raiseError(String paramString1, String paramString2, SQLException paramSQLException)
/*      */     throws SQLException
/*      */   {
/*  827 */     SQLException localSQLException = new SQLException(makeMessage(true, paramString1, paramString2), paramString1);
/*  828 */     localSQLException.setNextException(paramSQLException);
/*  829 */     if (paramSQLException != null)
/*      */     {
/*  831 */       localSQLException.initCause(paramSQLException.getCause());
/*      */     }
/*      */ 
/*  834 */     throw localSQLException;
/*      */   }
/*      */ 
/*      */   public static void raiseWarning(String paramString1, String paramString2, String paramString3)
/*      */     throws SQLWarning
/*      */   {
/*  852 */     throw new SQLWarning(makeMessage(true, paramString1, paramString2, paramString3), paramString1);
/*      */   }
/*      */ 
/*      */   public static void raiseWarning(String paramString1, String paramString2)
/*      */     throws SQLWarning
/*      */   {
/*  869 */     throw createWarning(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public static void raiseWarning(String paramString)
/*      */     throws SQLWarning
/*      */   {
/*  881 */     throw createWarning(paramString);
/*      */   }
/*      */ 
/*      */   public static SQLWarning createWarning(String paramString)
/*      */   {
/*  916 */     return new SQLWarning(makeMessage(true, paramString), paramString);
/*      */   }
/*      */ 
/*      */   public static SQLWarning createWarning(String paramString1, String paramString2)
/*      */   {
/*  929 */     return new SQLWarning(makeMessage(true, paramString1, paramString2), paramString1);
/*      */   }
/*      */ 
/*      */   public static void raiseIOException(String paramString1, String paramString2, Throwable paramThrowable)
/*      */     throws IOException
/*      */   {
/*  944 */     IOException localIOException = new IOException(makeMessage(true, paramString1, paramString2));
/*  945 */     localIOException.initCause(paramThrowable);
/*  946 */     throw localIOException;
/*      */   }
/*      */ 
/*      */   public static void raiseIOException(String paramString1, String paramString2, String paramString3, Throwable paramThrowable)
/*      */     throws IOException
/*      */   {
/*  961 */     IOException localIOException = new IOException(makeMessage(true, paramString1, paramString2, paramString3));
/*  962 */     localIOException.initCause(paramThrowable);
/*  963 */     throw localIOException;
/*      */   }
/*      */ 
/*      */   public static void raiseIOException(String paramString1, String paramString2)
/*      */     throws IOException
/*      */   {
/*  976 */     throw new IOException(makeMessage(true, paramString1, paramString2));
/*      */   }
/*      */ 
/*      */   public static void raiseIOException(String paramString1, String paramString2, String paramString3)
/*      */     throws IOException
/*      */   {
/*  990 */     throw new IOException(makeMessage(true, paramString1, paramString2, paramString3));
/*      */   }
/*      */ 
/*      */   public static void raiseIOException(String paramString)
/*      */     throws IOException
/*      */   {
/* 1003 */     throw new IOException(makeMessage(true, paramString));
/*      */   }
/*      */ 
/*      */   public static void raiseIOECheckDead(SQLException paramSQLException)
/*      */     throws IOException
/*      */   {
/* 1021 */     String str = paramSQLException.getMessage();
/* 1022 */     int i = 0;
/* 1023 */     while (paramSQLException != null)
/*      */     {
/* 1025 */       if ("JZ0C1".equals(paramSQLException.getSQLState()))
/*      */       {
/* 1030 */         i = 1;
/* 1031 */         break;
/*      */       }
/* 1033 */       paramSQLException = paramSQLException.getNextException();
/*      */     }
/* 1035 */     if (i != 0)
/*      */     {
/* 1037 */       IOException localIOException = makeIOException("JZ006", str);
/* 1038 */       raiseSybConnectionDeadException(localIOException);
/*      */     }
/*      */     else
/*      */     {
/* 1042 */       raiseIOException("JZ006", str, paramSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static IOException makeIOException(String paramString1, String paramString2)
/*      */   {
/* 1054 */     return new IOException(makeMessage(true, paramString1, paramString2));
/*      */   }
/*      */ 
/*      */   public static IOException makeIOException(String paramString)
/*      */   {
/* 1064 */     return new IOException(makeMessage(true, paramString));
/*      */   }
/*      */ 
/*      */   public static void raiseHAException(String paramString)
/*      */     throws SybHAException
/*      */   {
/* 1076 */     throw new SybHAException(paramString, makeMessage(true, paramString));
/*      */   }
/*      */ 
/*      */   public static SQLException createIOEKilledConnEx(IOException paramIOException)
/*      */   {
/* 1086 */     SQLException localSQLException1 = new SQLException(makeMessage(true, "JZ006", paramIOException.toString()), "JZ006");
/*      */ 
/* 1089 */     SQLException localSQLException2 = new SQLException(makeMessage(true, "JZ0C1"), "JZ0C1");
/*      */ 
/* 1092 */     localSQLException1.setNextException(localSQLException2);
/* 1093 */     return localSQLException1;
/*      */   }
/*      */ 
/*      */   public static void raiseSybConnectionDeadException(IOException paramIOException)
/*      */     throws SybConnectionDeadException
/*      */   {
/* 1106 */     throw new SybConnectionDeadException(paramIOException);
/*      */   }
/*      */ 
/*      */   public static void raiseErrorCheckDead(IOException paramIOException)
/*      */     throws SQLException
/*      */   {
/*      */     SQLException localSQLException1;
/* 1120 */     if (paramIOException instanceof SybConnectionDeadException)
/*      */     {
/* 1127 */       localSQLException1 = createIOEKilledConnEx(paramIOException);
/*      */ 
/* 1129 */       throw localSQLException1;
/*      */     }
/* 1131 */     if (paramIOException instanceof InterruptedIOException)
/*      */     {
/* 1139 */       localSQLException1 = new SQLException(makeMessage(true, "JZ0TO"), "JZ0TO");
/*      */ 
/* 1142 */       localSQLException1.initCause(paramIOException);
/*      */ 
/* 1144 */       raiseError("JZ006", paramIOException.toString() + " use getCause() to see the error chain", localSQLException1);
/*      */     }
/* 1147 */     else if (paramIOException.toString().indexOf("JZ0T3") != -1)
/*      */     {
/* 1152 */       localSQLException1 = new SQLException(makeMessage(true, "JZ0T3"), "JZ0T3");
/*      */ 
/* 1159 */       SQLException localSQLException2 = new SQLException(makeMessage(true, "JZ0TO"), "JZ0TO");
/*      */ 
/* 1162 */       localSQLException1.setNextException(localSQLException2);
/* 1163 */       localSQLException1.initCause(paramIOException);
/*      */ 
/* 1165 */       raiseError("JZ006", paramIOException.toString() + " use getCause() to see the error chain", localSQLException1);
/*      */     }
/*      */     else
/*      */     {
/* 1171 */       raiseError("JZ006", paramIOException.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void raiseErrorCheckDead(IOException paramIOException, SQLException paramSQLException)
/*      */     throws SQLException
/*      */   {
/*      */     SQLException localSQLException1;
/* 1191 */     if (paramIOException instanceof SybConnectionDeadException)
/*      */     {
/* 1198 */       localSQLException1 = createIOEKilledConnEx(paramIOException);
/* 1199 */       localSQLException1.setNextException(paramSQLException);
/*      */ 
/* 1201 */       throw localSQLException1;
/*      */     }
/* 1203 */     if (paramIOException instanceof InterruptedIOException)
/*      */     {
/* 1211 */       localSQLException1 = new SQLException(makeMessage(true, "JZ0TO"), "JZ0TO");
/*      */ 
/* 1214 */       paramSQLException.setNextException(localSQLException1);
/* 1215 */       paramSQLException.initCause(paramIOException);
/*      */ 
/* 1217 */       raiseError("JZ006", paramIOException.toString() + " use getCause() to see the error chain", paramSQLException);
/*      */     }
/* 1219 */     else if (paramIOException.toString().indexOf("JZ0T3") != -1)
/*      */     {
/* 1224 */       localSQLException1 = new SQLException(makeMessage(true, "JZ0T3"), "JZ0T3");
/*      */ 
/* 1231 */       SQLException localSQLException2 = new SQLException(makeMessage(true, "JZ0TO"), "JZ0TO");
/*      */ 
/* 1234 */       localSQLException1.setNextException(localSQLException2);
/* 1235 */       localSQLException1.initCause(paramIOException);
/*      */ 
/* 1237 */       raiseError("JZ006", paramIOException.toString() + " use getCause() to see the error chain", localSQLException1);
/*      */     }
/*      */     else
/*      */     {
/* 1243 */       paramSQLException.initCause(paramIOException);
/* 1244 */       raiseError("JZ006", paramIOException.toString() + " use getCause() to see the error chain", paramSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void raiseRuntimeException(String paramString, String[] paramArrayOfString)
/*      */     throws RuntimeException
/*      */   {
/* 1277 */     String str = makeMessage(false, paramString, paramArrayOfString);
/*      */ 
/* 1280 */     if (paramString.equals("JZ0NS"))
/*      */     {
/* 1282 */       throw new UnsupportedOperationException(str);
/*      */     }
/* 1284 */     if (paramString.equals("ZZ00A"))
/*      */     {
/* 1286 */       throw new UnimplementedOperationException(str);
/*      */     }
/* 1288 */     if (paramString.equals("JZ0AF"))
/*      */     {
/* 1290 */       RuntimeException localRuntimeException = new RuntimeException(str);
/* 1291 */       Debug.printStackTrace(null, localRuntimeException);
/* 1292 */       throw localRuntimeException;
/*      */     }
/* 1294 */     if (!paramString.equals("010AF")) {
/*      */       return;
/*      */     }
/* 1297 */     Debug.println(str);
/*      */   }
/*      */ 
/*      */   public static SQLException makeIOReportableException(String paramString, Exception paramException)
/*      */   {
/* 1309 */     String str = makeMessage(true, paramString);
/* 1310 */     if (paramException != null)
/*      */     {
/* 1312 */       str = str + ": " + paramException.toString();
/*      */     }
/* 1314 */     return new SQLException(str, paramString);
/*      */   }
/*      */ 
/*      */   public static void raiseBatchUpdateException(SQLException paramSQLException, int[] paramArrayOfInt)
/*      */     throws BatchUpdateException
/*      */   {
/*      */     Object localObject;
/* 1320 */     if (paramSQLException instanceof SybSQLException)
/*      */     {
/* 1322 */       localObject = new SybBatchUpdateException((SybSQLException)paramSQLException, makeMessage(true, "JZ0BE", paramSQLException.getMessage()), "JZ0BE", 0, paramArrayOfInt);
/*      */     }
/*      */     else
/*      */     {
/* 1328 */       localObject = new BatchUpdateException(makeMessage(true, "JZ0BE", paramSQLException.getMessage()), "JZ0BE", 0, paramArrayOfInt);
/*      */     }
/*      */ 
/* 1336 */     ((BatchUpdateException)localObject).setNextException(paramSQLException);
/* 1337 */     throw ((Throwable)localObject);
/*      */   }
/*      */ 
/*      */   public static void raiseGSSError(GSSException paramGSSException)
/*      */     throws SQLException
/*      */   {
/* 1352 */     String[] arrayOfString = new String[4];
/* 1353 */     arrayOfString[1] = paramGSSException.getMajorString();
/* 1354 */     if (arrayOfString[1] == null)
/*      */     {
/* 1356 */       arrayOfString[1] = "";
/*      */     }
/* 1358 */     arrayOfString[3] = paramGSSException.getMinorString();
/* 1359 */     if (arrayOfString[3] == null)
/*      */     {
/* 1361 */       arrayOfString[3] = "";
/*      */     }
/* 1363 */     arrayOfString[0] = String.valueOf(paramGSSException.getMajor());
/* 1364 */     arrayOfString[2] = String.valueOf(paramGSSException.getMinor());
/*      */ 
/* 1366 */     SQLException localSQLException = new SQLException(makeMessage(true, "JZ0GS", arrayOfString), "JZ0GS");
/*      */ 
/* 1369 */     throw localSQLException;
/*      */   }
/*      */ 
/*      */   public static void print(String paramString1, String paramString2)
/*      */   {
/* 1380 */     System.out.println(makeMessage(false, paramString1, paramString2));
/*      */   }
/*      */ 
/*      */   private static String makeMessage(boolean paramBoolean, String paramString, String[] paramArrayOfString)
/*      */   {
/* 1392 */     String str = getMessage(paramBoolean, paramString);
/* 1393 */     if (paramArrayOfString != null)
/*      */     {
/* 1395 */       for (int i = 0; i < paramArrayOfString.length; ++i)
/*      */       {
/* 1397 */         str = cookieReplace(paramString, str, i + 1, paramArrayOfString[i]);
/*      */       }
/*      */     }
/* 1400 */     return str;
/*      */   }
/*      */ 
/*      */   private static String makeMessage(boolean paramBoolean, String paramString)
/*      */   {
/* 1406 */     String str = getMessage(paramBoolean, paramString);
/* 1407 */     return str;
/*      */   }
/*      */ 
/*      */   private static String makeMessage(boolean paramBoolean, String paramString1, String paramString2)
/*      */   {
/* 1412 */     String str = getMessage(paramBoolean, paramString1);
/* 1413 */     str = cookieReplace(paramString1, str, 1, paramString2);
/* 1414 */     return str;
/*      */   }
/*      */ 
/*      */   private static String makeMessage(boolean paramBoolean, String paramString1, String paramString2, String paramString3)
/*      */   {
/* 1420 */     String str = getMessage(paramBoolean, paramString1);
/* 1421 */     str = cookieReplace(paramString1, str, 1, paramString2);
/* 1422 */     str = cookieReplace(paramString1, str, 2, paramString3);
/* 1423 */     return str;
/*      */   }
/*      */ 
/*      */   private static String makeMessage(boolean paramBoolean, String paramString1, String paramString2, String paramString3, String paramString4)
/*      */   {
/* 1429 */     String str = getMessage(paramBoolean, paramString1);
/* 1430 */     str = cookieReplace(paramString1, str, 1, paramString2);
/* 1431 */     str = cookieReplace(paramString1, str, 2, paramString3);
/* 1432 */     str = cookieReplace(paramString1, str, 3, paramString4);
/* 1433 */     return str;
/*      */   }
/*      */ 
/*      */   private static String getMessage(boolean paramBoolean, String paramString)
/*      */   {
/*      */     String str;
/*      */     try
/*      */     {
/* 1441 */       str = _messages.getString(paramString);
/* 1442 */       if (paramBoolean)
/*      */       {
/* 1444 */         str = paramString + ": " + str;
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 1453 */       str = "Internal error, missing message for: " + paramString + " exception: " + localException.toString();
/*      */     }
/*      */ 
/* 1456 */     return str;
/*      */   }
/*      */ 
/*      */   private static String cookieReplace(String paramString1, String paramString2, int paramInt, String paramString3)
/*      */   {
/* 1461 */     String str1 = "%" + paramInt + "s";
/* 1462 */     int i = paramString2.indexOf(str1);
/* 1463 */     String str2 = paramString2;
/* 1464 */     if (i == -1)
/*      */     {
/* 1468 */       if (str2.indexOf(makeMessage(false, "JZ0BM")) == -1)
/*      */       {
/* 1474 */         str2 = str2.concat(makeMessage(false, "JZ0BM"));
/*      */       }
/*      */ 
/* 1478 */       str2 = str2.concat(makeMessage(false, "JZ0BC", "" + paramInt, paramString3));
/*      */ 
/* 1480 */       return str2;
/*      */     }
/* 1482 */     int j = i + str1.length();
/* 1483 */     str2 = paramString2.substring(0, i) + paramString3 + paramString2.substring(j);
/*      */ 
/* 1485 */     return str2;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  683 */       _messages = ResourceBundle.getBundle("com.sybase.jdbc3.jdbc.resource.Messages", Language.getLocale());
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.ErrorMessage
 * JD-Core Version:    0.5.4
 */