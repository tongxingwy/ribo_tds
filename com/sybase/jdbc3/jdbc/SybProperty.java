/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.tds.Iana;
/*      */ import com.sybase.jdbc3.utils.EncryptedValue;
/*      */ import com.sybase.jdbc3.utils.SybVersion;
/*      */ import com.sybase.jdbcx.DynamicClassLoader;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Serializable;
/*      */ import java.security.Provider;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Properties;
/*      */ import java.util.PropertyResourceBundle;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.StringTokenizer;
/*      */ import org.ietf.jgss.GSSManager;
/*      */ 
/*      */ public class SybProperty
/*      */   implements Serializable, Cloneable
/*      */ {
/*      */   static final long serialVersionUID = -2663952745103804805L;
/*      */   private static final String OFF = "false";
/*      */   private static final String ON = "true";
/*      */   public static final int BLK_NONE = 0;
/*      */   public static final int BLK_ARRINS_MIX_STMT = 1;
/*      */   public static final int BLK_ARRINS_NO_MIX_STMT = 2;
/*      */   public static final int BLK_BCP = 3;
/*      */   protected static final String IS_CLOSED_TEST_INTERNAL_VALUE = "INTERNAL";
/*      */   public static final int SERVICENAME = 0;
/*      */   public static final int HOSTNAME = 1;
/*      */   public static final int HOSTPROC = 2;
/*      */   public static final int USER = 3;
/*      */   public static final int PASSWORD = 4;
/*      */   public static final int APPLICATIONNAME = 5;
/*      */   public static final int USE_METADATA = 6;
/*      */   public static final int LANGUAGE = 7;
/*      */   public static final int CHARSET = 8;
/*      */   public static final int REMOTEPWD = 9;
/*      */   public static final int VERSIONSTRING = 10;
/*      */   public static final int EXPIRESTRING = 11;
/*      */   public static final int PACKETSIZE = 12;
/*      */   public static final int STREAM_CACHE_SIZE = 13;
/*      */   public static final int REPEAT_READ = 14;
/*      */   public static final int LITERAL_PARAMS = 15;
/*      */   public static final int CURSOR_ROWS = 16;
/*      */   public static final int PROXY = 17;
/*      */   public static final int SQL_INIT_STRING = 18;
/*      */   public static final int SESSION_TIMEOUT = 19;
/*      */   public static final int SESSION_ID = 20;
/*      */   public static final int CHARSET_CONVERTER_CLASS = 21;
/*      */   public static final int JCONNECT_VERSION = 22;
/*      */   public static final int CANCEL_ALL = 23;
/*      */   public static final int PROTOCOL_CAPTURE = 24;
/*      */   public static final int DYNAMIC_PREPARE = 25;
/*      */   public static final int CONNECTION_FAILOVER = 26;
/*      */   public static final int LANGUAGE_CURSOR = 27;
/*      */   public static final int SEND_LONG_PARAMS_REGARDLESS_OF_CAPABILITIES = 28;
/*      */   public static final int SERIALIZE_REQUESTS = 29;
/*      */   public static final int LSB_BYTE_ORDER = 30;
/*      */   public static final int SYBSOCKET_FACTORY = 31;
/*      */   public static final int IGNORE_DONE_IN_PROC = 32;
/*      */   public static final int SELECT_OPENS_CURSOR = 33;
/*      */   public static final int REQUEST_HA_SESSION = 34;
/*      */   public static final int ELIMINATE_010SM = 35;
/*      */   public static final int IS_CLOSED_TEST = 36;
/*      */   public static final int CLASS_LOADER = 37;
/*      */   public static final int PRELOAD_JARS = 38;
/*      */   public static final int FAKE_METADATA = 39;
/*      */   public static final int GET_BY_NAME_USES_COLUMN_LABEL = 40;
/*      */   public static final int BE_AS_JDBC_COMPLIANT_AS_POSSIBLE = 41;
/*      */   public static final int RMNAME = 42;
/*      */   public static final int DISABLE_UNPROCESSED_PARAM_WARNINGS = 43;
/*      */   public static final int DISABLE_UNICHAR_SENDING = 44;
/*      */   public static final int SECONDARY_SERVER_HOSTPORT = 45;
/*      */   public static final int ESCAPE_PROCESSING_DEFAULT = 46;
/*      */   public static final int IMPLICIT_CURSOR_FETCH_SIZE = 47;
/*      */   public static final int REQUEST_KERBEROS_SESSION = 48;
/*      */   public static final int SERVICE_PRINCIPAL_NAME = 49;
/*      */   public static final int GSSMANAGER_CLASS = 50;
/*      */   public static final int QUERY_TIMEOUT_CANCELS_ALL = 51;
/*      */   public static final int CAPABILITY_TIME = 52;
/*      */   public static final int SERVER_INITIATED_TRANSACTIONS = 53;
/*      */   public static final int ENABLE_SERVER_PACKETSIZE = 54;
/*      */   public static final int ENCRYPT_PASSWORD = 55;
/*      */   public static final int TEXTSIZE = 56;
/*      */   public static final int SERVERTYPE = 57;
/*      */   public static final int CACHE_COLUMN_METADATA = 58;
/*      */   public static final int CAPABILITY_WIDETABLE = 59;
/*      */   public static final int DATABASE = 60;
/*      */   public static final int INTERNAL_QUERY_TIMEOUT = 61;
/*      */   public static final int DEFAULT_QUERY_TIMEOUT = 62;
/*      */   public static final int CRC = 63;
/*      */   public static final int J2EE_TCK_COMPLIANT = 64;
/*      */   public static final int GET_COLUMN_LABEL_FOR_NAME = 65;
/*      */   public static final int JCE_PROVIDER_CLASS = 66;
/*      */   public static final int RETRY_WITH_NO_ENCRYPTION = 67;
/*      */   public static final int ENABLE_BULK_LOAD = 68;
/*      */   public static final int NEWPASSWORD = 69;
/*      */   public static final int PROMPT_FOR_NEWPASSWORD = 70;
/*      */   public static final int ALLOW_LOADBALANCING = 71;
/*      */   public static final int ALLOW_CONTEXT_MIGRATION = 72;
/*      */   public static final int ALTERNATE_SERVER_NAME = 73;
/*      */   public static final int IGNORE_WARNINGS = 74;
/*      */   public static final int OPTIMIZE_FOR_PERFORMANCE = 75;
/*      */   public static final int DELETE_WARNINGS_FROM_EXCEPTION_CHAIN = 76;
/*      */   public static final int JAVA_CHARSET_MAPPING = 77;
/*      */   public static final int ENABLE_RAWBULK_INTERFACE = 78;
/*      */   public static final int ENABLE_LOB_LOCATOR = 79;
/*      */   public static final int EXECUTE_BATCH_PAST_ERRORS = 80;
/*      */   public static final int SETMAXROWS_AFFECTS_SELECT_ONLY = 81;
/*      */   public static final int RELEASE_LOCKS_ON_CURSOR_CLOSE = 82;
/*      */   public static final int SEND_BATCHPARAMS_IMMEDIATE = 83;
/*      */   public static final int HOMOGENEOUS_BATCH = 84;
/*      */   public static final int EARLY_BATCH_READ_THRESHOLD = 85;
/*      */   public static final int OPTIMIZE_STRING_CONVERSIONS = 86;
/*      */   public static final int SUPPRESS_CONTROL_TOKEN = 87;
/*      */   public static final int SUPPRESS_ROW_FORMAT2 = 88;
/*      */   public static final int SUPPRESS_ROW_FORMAT = 89;
/*      */   public static final int SUPPRESS_PARAM_FORMAT = 90;
/*      */   public static final int ENABLE_FUNCTIONALITY_GROUP = 91;
/*      */   public static final int STRIP_BLANKS = 92;
/*      */   public static final int INTERNAL_READ_BUFFER_LIMIT = 93;
/*      */   public static final int CONNECTION_RETRY_COUNT = 94;
/*      */   public static final int CONNECTION_RETRY_DELAY = 95;
/*      */   public static final int HADR_MODE = 96;
/*      */   public static final int CONNECT_READONLY = 97;
/*      */   public static final int ENABLE_REDIRECTION = 98;
/*      */   public static final int SET_ENCRYPTED_COLUMN_VALUES_AS_CIPHERTEXT = 99;
/*      */   public static final int ENABLE_SSL = 100;
/*      */   public static final int SSL_HOSTNAME_IN_CERT = 101;
/*      */   public static final int SSL_TRUST_ALL_CERTS = 102;
/*      */   public static final int MAX_PROPS = 103;
/*  909 */   protected static final String[] PROPNAME = { "SERVICENAME", "HOSTNAME", "HOSTPROC", "user", "password", "APPLICATIONNAME", "USE_METADATA", "LANGUAGE", "CHARSET", "REMOTEPWD", "VERSIONSTRING", "EXPIRESTRING", "PACKETSIZE", "STREAM_CACHE_SIZE", "REPEAT_READ", "LITERAL_PARAMS", "CURSOR_ROWS", "proxy", "SQLInitString", "SESSION_TIMEOUT", "SESSION_ID", "CHARSET_CONVERTER_CLASS", "JCONNECT_VERSION", "CANCEL_ALL", "PROTOCOL_CAPTURE", "DYNAMIC_PREPARE", "CONNECTION_FAILOVER", "LANGUAGE_CURSOR", "SEND_LONG_PARAMS_REGARDLESS_OF_CAPABILITIES", "SERIALIZE_REQUESTS", "LSB_BYTE_ORDER", "SYBSOCKET_FACTORY", "IGNORE_DONE_IN_PROC", "SELECT_OPENS_CURSOR", "REQUEST_HA_SESSION", "ELIMINATE_010SM", "IS_CLOSED_TEST", "CLASS_LOADER", "PRELOAD_JARS", "FAKE_METADATA", "GET_BY_NAME_USES_COLUMN_LABEL", "BE_AS_JDBC_COMPLIANT_AS_POSSIBLE", "RMNAME", "DISABLE_UNPROCESSED_PARAM_WARNINGS", "DISABLE_UNICHAR_SENDING", "SECONDARY_SERVER_HOSTPORT", "ESCAPE_PROCESSING_DEFAULT", "IMPLICIT_CURSOR_FETCH_SIZE", "REQUEST_KERBEROS_SESSION", "SERVICE_PRINCIPAL_NAME", "GSSMANAGER_CLASS", "QUERY_TIMEOUT_CANCELS_ALL", "CAPABILITY_TIME", "SERVER_INITIATED_TRANSACTIONS", "ENABLE_SERVER_PACKETSIZE", "ENCRYPT_PASSWORD", "TEXTSIZE", "SERVERTYPE", "CACHE_COLUMN_METADATA", "CAPABILITY_WIDETABLE", "DATABASE", "INTERNAL_QUERY_TIMEOUT", "DEFAULT_QUERY_TIMEOUT", "CRC", "J2EE_TCK_COMPLIANT", "GET_COLUMN_LABEL_FOR_NAME", "JCE_PROVIDER_CLASS", "RETRY_WITH_NO_ENCRYPTION", "ENABLE_BULK_LOAD", "NEWPASSWORD", "PROMPT_FOR_NEWPASSWORD", "ALLOW_LOADBALANCING", "ALLOW_CONTEXT_MIGRATION", "ALTERNATE_SERVER_NAME", "IGNORE_WARNINGS", "OPTIMIZE_FOR_PERFORMANCE", "DELETE_WARNINGS_FROM_EXCEPTION_CHAIN", "JAVA_CHARSET_MAPPING", "ENABLE_RAWBULK_INTERFACE", "ENABLE_LOB_LOCATOR", "EXECUTE_BATCH_PAST_ERRORS", "SETMAXROWS_AFFECTS_SELECT_ONLY", "RELEASE_LOCKS_ON_CURSOR_CLOSE", "SEND_BATCHPARAMS_IMMEDIATE", "HOMOGENEOUS_BATCH", "EARLY_BATCH_READ_THRESHOLD", "OPTIMIZE_STRING_CONVERSIONS", "SUPPRESS_CONTROL_TOKEN", "SUPPRESS_ROW_FORMAT2", "SUPPRESS_ROW_FORMAT", "SUPPRESS_PARAM_FORMAT", "ENABLE_FUNCTIONALITY_GROUP", "STRIP_BLANKS", "INTERNAL_READ_BUFFER_LIMIT", "CONNECTION_RETRY_COUNT", "CONNECTION_RETRY_DELAY", "HADR_MODE", "CONNECT_READONLY", "ENABLE_REDIRECTION", "SET_ENCRYPTED_COLUMN_VALUES_AS_CIPHERTEXT", "ENABLE_SSL", "SSL_HOSTNAME_IN_CERT", "SSL_TRUST_ALL_CERTS" };
/*      */ 
/* 1017 */   private static final String[] DEF_PROP_VALUE = { null, null, null, null, null, null, "true", null, null, null, SybVersion.VERSION_STRING, SybVersion.EXPIRES_STRING, String.valueOf(512), String.valueOf(-1), "true", "false", "1", null, null, "-1", null, "com.sybase.jdbc3.charset.PureConverter", "7.0", "true", null, "true", "true", "false", "false", "false", "false", null, "false", "false", "false", "false", null, null, null, "false", "true", "false", null, "false", "true", null, "true", Integer.toString(-2147483648), "false", null, null, "false", "false", "true", "true", "false", "0", "", "false", "true", "", "0", "0", "false", "false", "false", null, "false", null, null, "false", "true", "true", null, "false", "false", "true", null, "false", "false", "false", "true", "false", "false", "true", "-1", "0", "true", "false", "true", "false", "0", "false", "-1", "0", "0", null, "false", "true", "false", "false", null, "true" };
/*      */   private float _version;
/* 1135 */   private Properties _info = null;
/*      */ 
/* 1138 */   protected Object[] _propValue = new Object[103];
/*      */ 
/* 1143 */   protected boolean[] _hasBeenSet = new boolean[103];
/*      */   private SQLWarning _warnings;
/* 1151 */   private static ResourceBundle _propertyDescriptions = null;
/*      */ 
/* 1443 */   String[][] properties_name_value = { { "DYNAMIC_PREPARE", "true" }, { "HOMOGENEOUS_BATCH", "false" }, { "SQL_INIT_STRING", "SET ANSINULL ON SET STRING_RTRUNCATION ON" }, { "EARLY_BATCH_READ_THRESHOLD", "50" }, { "EXECUTE_BATCH_PAST_ERRORS", "true" }, { "ENCRYPT_PASSWORD", "true" } };
/*      */ 
/* 1451 */   int[] properties_key = { 25, 84, 18, 85, 80, 55 };
/*      */ 
/* 1455 */   int total_enable_functionality_group_properties = 6;
/*      */ 
/*      */   public SybProperty(int paramInt)
/*      */   {
/* 1172 */     this._version = paramInt;
/*      */ 
/* 1175 */     setPropertyDefaults();
/*      */ 
/* 1179 */     setVersionDefaults();
/*      */   }
/*      */ 
/*      */   public SybProperty(float paramFloat)
/*      */   {
/* 1185 */     this._version = paramFloat;
/*      */ 
/* 1188 */     setPropertyDefaults();
/*      */ 
/* 1192 */     setVersionDefaults();
/*      */   }
/*      */ 
/*      */   public SybProperty(Properties paramProperties, int paramInt)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1210 */       SybPropertyInit(paramProperties, paramInt);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1214 */       stashWarning(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public SybProperty(Properties paramProperties, float paramFloat) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1222 */       SybPropertyInit(paramProperties, paramFloat);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1226 */       stashWarning(localSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void SybPropertyInit(Properties paramProperties, float paramFloat) throws SQLException
/*      */   {
/* 1232 */     this._version = paramFloat;
/* 1233 */     this._info = paramProperties;
/*      */ 
/* 1237 */     if (paramProperties != null)
/*      */     {
/* 1240 */       Enumeration localEnumeration = paramProperties.propertyNames();
/* 1241 */       while (localEnumeration.hasMoreElements())
/*      */       {
/* 1243 */         String str = (String)localEnumeration.nextElement();
/*      */         try
/*      */         {
/* 1256 */           Object localObject1 = null;
/*      */           try
/*      */           {
/* 1260 */             localObject1 = paramProperties.getProperty(str);
/*      */ 
/* 1266 */             if (localObject1 == null)
/*      */             {
/* 1268 */               Object localObject2 = paramProperties.get(str);
/* 1269 */               if (localObject2 != null)
/*      */               {
/* 1271 */                 localObject1 = localObject2;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */           catch (ClassCastException localClassCastException)
/*      */           {
/* 1280 */             localObject1 = paramProperties.get(str);
/*      */           }
/*      */ 
/* 1283 */           setConnProperty(str, localObject1, true, true);
/*      */         }
/*      */         catch (SQLException localSQLException)
/*      */         {
/* 1287 */           stashWarning(localSQLException);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1293 */     setPropertyDefaults();
/*      */ 
/* 1296 */     setVersionDefaults();
/*      */   }
/*      */ 
/*      */   public Object clone()
/*      */   {
/* 1305 */     SybProperty localSybProperty = null;
/*      */     try
/*      */     {
/* 1308 */       localSybProperty = new SybProperty(this._info, this._version);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1313 */       return localSybProperty;
/*      */     }
/*      */ 
/* 1331 */     for (int i = 0; i < 103; ++i)
/*      */     {
/* 1333 */       if ((this._hasBeenSet[i] == 0) || (localSybProperty._hasBeenSet[i] != 0)) {
/*      */         continue;
/*      */       }
/* 1336 */       if (this._propValue[i] instanceof String)
/*      */       {
/* 1338 */         localSybProperty._propValue[i] = new String((String)this._propValue[i]);
/*      */       }
/* 1341 */       else if (this._propValue[i] instanceof Boolean)
/*      */       {
/* 1343 */         localSybProperty._propValue[i] = new Boolean(((Boolean)this._propValue[i]).booleanValue());
/*      */       }
/* 1346 */       else if (this._propValue[i] instanceof Integer)
/*      */       {
/* 1348 */         localSybProperty._propValue[i] = new Integer(((Integer)this._propValue[i]).intValue());
/*      */       }
/*      */       else
/*      */       {
/* 1356 */         localSybProperty._propValue[i] = this._propValue[i];
/*      */       }
/* 1358 */       localSybProperty._hasBeenSet[i] = true;
/*      */     }
/*      */ 
/* 1364 */     return localSybProperty;
/*      */   }
/*      */ 
/*      */   public void parsePropertyString(String paramString)
/*      */     throws SQLException
/*      */   {
/* 1375 */     ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramString.getBytes());
/* 1376 */     char[] arrayOfChar1 = new char['ÿ'];
/* 1377 */     char[] arrayOfChar2 = new char['ÿ'];
/* 1378 */     int i = 0;
/* 1379 */     int j = 0;
/* 1380 */     int k = 0;
/* 1381 */     int l = 0;
/*      */ 
/* 1383 */     int i1 = 0;
/* 1384 */     while ((i1 > -1) && (localByteArrayInputStream.available() > 0)) {
/*      */       do {
/* 1386 */         if ((i1 = localByteArrayInputStream.read()) <= -1)
/*      */           break;
/* 1388 */         i = (char)i1;
/* 1389 */         if (i == 61)
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/* 1395 */         arrayOfChar1[(l++)] = i;
/* 1396 */       }while (localByteArrayInputStream.available() != 0);
/*      */ 
/* 1401 */       j = l--;
/* 1402 */       l = 0;
/*      */       do {
/* 1404 */         if ((i1 = localByteArrayInputStream.read()) <= -1)
/*      */           break;
/* 1406 */         i = (char)i1;
/* 1407 */         if (i == 38)
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/* 1412 */         if (i == 92)
/*      */         {
/* 1414 */           localByteArrayInputStream.skip(1L);
/* 1415 */           arrayOfChar2[(l++)] = '&';
/*      */         }
/*      */         else
/*      */         {
/* 1419 */           arrayOfChar2[(l++)] = i;
/*      */         }
/*      */       }
/* 1421 */       while (localByteArrayInputStream.available() != 0);
/*      */ 
/* 1426 */       k = l--;
/*      */ 
/* 1428 */       String str1 = new String(arrayOfChar1, 0, j);
/* 1429 */       String str2 = new String(arrayOfChar2, 0, k);
/*      */       try
/*      */       {
/* 1432 */         setConnProperty(str1, str2, false, true);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 1436 */         stashWarning(localSQLException);
/*      */       }
/* 1438 */       l = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setEnableFunctionalityGroup()
/*      */     throws SQLException
/*      */   {
/* 1458 */     int i = 1;
/* 1459 */     if (getInteger(91) == 0)
/*      */     {
/* 1461 */       i = 0;
/*      */     }
/* 1463 */     for (int j = 0; j < this.total_enable_functionality_group_properties; ++j)
/*      */     {
/* 1465 */       String str1 = this.properties_name_value[j][0];
/* 1466 */       String str2 = (i != 0) ? this.properties_name_value[j][1] : getDefaultValue(this.properties_key[j]);
/*      */ 
/* 1470 */       if ((str2 == null) || (str2.equalsIgnoreCase("")) || (str2.equalsIgnoreCase("null")))
/*      */       {
/* 1472 */         setConnProperty(str1, null, true, false);
/*      */       }
/*      */       else
/*      */       {
/* 1476 */         setConnProperty(str1, str2, true, false);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   Object getConnProperty(String paramString)
/*      */   {
/* 1495 */     Object localObject = null;
/*      */ 
/* 1497 */     for (int i = 0; i < 103; ++i)
/*      */     {
/* 1499 */       if (!PROPNAME[i].equalsIgnoreCase(paramString))
/*      */         continue;
/* 1501 */       localObject = this._propValue[i];
/* 1502 */       break;
/*      */     }
/*      */ 
/* 1508 */     return localObject;
/*      */   }
/*      */ 
/*      */   void setConnProperty(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws SQLException
/*      */   {
/* 1530 */     if (paramString.equalsIgnoreCase("NO_REPEAT_READ"))
/*      */     {
/* 1532 */       if ((paramBoolean2) && (this._hasBeenSet[14] != 0) && (this._hasBeenSet[91] == 0))
/*      */       {
/* 1534 */         ErrorMessage.raiseWarning("010DP", paramString);
/*      */ 
/* 1536 */         return;
/*      */       }
/* 1538 */       if (paramObject != null)
/*      */       {
/* 1540 */         this._propValue[14] = new Boolean(false);
/* 1541 */         this._hasBeenSet[14] = true;
/*      */       }
/* 1543 */       return;
/*      */     }
/*      */ 
/* 1546 */     int i = -1;
/* 1547 */     int j = 0;
/* 1548 */     while ((j < 103) && (i == -1))
/*      */     {
/* 1550 */       if (PROPNAME[j].equalsIgnoreCase(paramString))
/* 1551 */         i = j;
/* 1552 */       ++j;
/*      */     }
/*      */ 
/* 1555 */     if (i == -1)
/*      */     {
/* 1557 */       if (!paramBoolean1)
/*      */       {
/* 1559 */         ErrorMessage.raiseWarning("010UP", paramString);
/*      */       }
/*      */ 
/* 1562 */       return;
/*      */     }
/* 1564 */     if ((paramBoolean2) && (this._hasBeenSet[i] != 0) && (this._hasBeenSet[91] == 0))
/*      */     {
/* 1566 */       ErrorMessage.raiseWarning("010DP", paramString);
/*      */ 
/* 1568 */       return;
/*      */     }
/* 1570 */     this._hasBeenSet[i] = true;
/*      */     boolean bool;
/* 1571 */     switch (i)
/*      */     {
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 17:
/*      */     case 18:
/*      */     case 20:
/*      */     case 21:
/*      */     case 24:
/*      */     case 31:
/*      */     case 36:
/*      */     case 42:
/*      */     case 45:
/*      */     case 49:
/*      */     case 57:
/*      */     case 60:
/*      */     case 68:
/*      */     case 69:
/*      */     case 73:
/*      */     case 77:
/*      */     case 96:
/*      */     case 101:
/* 1604 */       if ((paramObject == null) || ((paramObject instanceof String) && (((String)paramObject).length() == 0)))
/*      */       {
/* 1607 */         paramObject = DEF_PROP_VALUE[i];
/*      */       }
/* 1609 */       if ((i == 9) && (!paramObject instanceof EncryptedValue))
/*      */       {
/* 1611 */         paramObject = new EncryptedValue((String)paramObject);
/*      */       }
/* 1613 */       setProperty(i, paramObject);
/* 1614 */       break;
/*      */     case 6:
/*      */     case 14:
/*      */     case 15:
/*      */     case 23:
/*      */     case 25:
/*      */     case 26:
/*      */     case 27:
/*      */     case 28:
/*      */     case 29:
/*      */     case 30:
/*      */     case 32:
/*      */     case 33:
/*      */     case 34:
/*      */     case 35:
/*      */     case 39:
/*      */     case 40:
/*      */     case 43:
/*      */     case 44:
/*      */     case 46:
/*      */     case 48:
/*      */     case 51:
/*      */     case 52:
/*      */     case 53:
/*      */     case 54:
/*      */     case 55:
/*      */     case 58:
/*      */     case 59:
/*      */     case 63:
/*      */     case 64:
/*      */     case 65:
/*      */     case 67:
/*      */     case 70:
/*      */     case 71:
/*      */     case 72:
/*      */     case 74:
/*      */     case 75:
/*      */     case 76:
/*      */     case 78:
/*      */     case 79:
/*      */     case 80:
/*      */     case 81:
/*      */     case 82:
/*      */     case 83:
/*      */     case 84:
/*      */     case 87:
/*      */     case 88:
/*      */     case 89:
/*      */     case 90:
/*      */     case 92:
/*      */     case 97:
/*      */     case 98:
/*      */     case 99:
/*      */     case 100:
/*      */     case 102:
/* 1671 */       if (paramObject == null)
/*      */       {
/* 1674 */         paramObject = "true";
/*      */       }
/* 1676 */       else if (((paramObject instanceof String) && (!((String)paramObject).equalsIgnoreCase("false")) && (!((String)paramObject).equalsIgnoreCase("true"))) || (!paramObject instanceof String))
/*      */       {
/* 1682 */         ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */       }
/*      */ 
/* 1685 */       setProperty(i, Boolean.valueOf((String)paramObject));
/* 1686 */       if (i == 75)
/*      */       {
/* 1688 */         bool = Boolean.valueOf((String)paramObject).booleanValue();
/* 1689 */         if (bool)
/*      */         {
/* 1691 */           setPerformanceProps();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1696 */       if (i == 98)
/*      */       {
/* 1698 */         setProperty(71, paramObject); } break;
/*      */     case 41:
/* 1703 */       if (paramObject == null)
/*      */       {
/* 1706 */         paramObject = "true";
/*      */       }
/* 1708 */       else if (((paramObject instanceof String) && (!((String)paramObject).equalsIgnoreCase("false")) && (!((String)paramObject).equalsIgnoreCase("true"))) || (!paramObject instanceof String))
/*      */       {
/* 1714 */         ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */       }
/*      */ 
/* 1717 */       bool = Boolean.valueOf((String)paramObject).booleanValue();
/*      */ 
/* 1719 */       setProperty(i, Boolean.valueOf((String)paramObject));
/*      */ 
/* 1724 */       if (bool)
/*      */       {
/* 1726 */         setJDBCComplianceProps();
/*      */       }
/*      */ 
/* 1726 */       break;
/*      */     case 12:
/*      */     case 13:
/*      */     case 16:
/*      */     case 19:
/*      */     case 56:
/*      */     case 61:
/*      */     case 62:
/*      */     case 85:
/*      */     case 94:
/*      */     case 95:
/*      */       try
/*      */       {
/* 1743 */         Integer.valueOf(paramObject.toString());
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException1)
/*      */       {
/* 1747 */         ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */       }
/*      */ 
/* 1750 */       setProperty(i, paramObject);
/* 1751 */       break;
/*      */     case 93:
/* 1755 */       validateInternalReadBufferLimit(paramObject);
/* 1756 */       setProperty(i, paramObject);
/* 1757 */       break;
/*      */     case 91:
/*      */       try
/*      */       {
/* 1762 */         Integer.valueOf(paramObject.toString());
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException2)
/*      */       {
/* 1766 */         ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */       }
/*      */ 
/* 1769 */       setProperty(i, paramObject);
/* 1770 */       if (getInteger(91) == 1)
/*      */       {
/* 1772 */         setEnableFunctionalityGroup();
/* 1772 */       }
/*      */ break;
/*      */     case 47:
/*      */       try
/*      */       {
/* 1778 */         Integer.valueOf(paramObject.toString()).intValue();
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException3)
/*      */       {
/* 1782 */         ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */       }
/*      */ 
/* 1785 */       if ((Integer.valueOf(paramObject.toString()).intValue() <= 0) && (Integer.valueOf(paramObject.toString()).intValue() != -2147483648))
/*      */       {
/* 1789 */         ErrorMessage.raiseError("JZ0BJ");
/*      */       }
/*      */ 
/* 1792 */       setProperty(i, paramObject);
/* 1793 */       break;
/*      */     case 22:
/* 1796 */       float f = 99.0F;
/*      */       try
/*      */       {
/* 1799 */         f = Float.valueOf(paramObject.toString()).floatValue();
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException4)
/*      */       {
/* 1803 */         ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */       }
/*      */ 
/* 1806 */       if ((f == 2.0F) || (f == 3.0F) || (f == 4.0F) || (f == 5.0F) || (f == 6.0F) || (f == 6.05F))
/*      */       {
/* 1813 */         paramObject = new Integer((int)f);
/*      */       }
/* 1815 */       else if ((f == 0.0F) || (f == 99.0F) || (f == 100.0F))
/*      */       {
/* 1819 */         f = 7.0F;
/* 1820 */         paramObject = new Float(f);
/*      */       }
/* 1822 */       else if (f == 7.0F)
/*      */       {
/* 1824 */         paramObject = new Float(f);
/*      */       }
/*      */       else
/*      */       {
/* 1828 */         ErrorMessage.raiseError("JZ0D6", "" + f);
/*      */       }
/*      */ 
/* 1831 */       setProperty(i, paramObject);
/* 1832 */       this._version = f;
/* 1833 */       break;
/*      */     case 37:
/* 1836 */       if ((paramObject != null) && (!paramObject instanceof DynamicClassLoader))
/*      */       {
/* 1839 */         ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */       }
/*      */ 
/* 1842 */       setProperty(i, paramObject);
/* 1843 */       break;
/*      */     case 38:
/* 1846 */       if ((paramObject != null) && (!paramObject instanceof String))
/*      */       {
/* 1848 */         ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */       }
/*      */ 
/* 1854 */       if (paramObject != null)
/*      */       {
/* 1856 */         setProperty(i, tokenizeString((String)paramObject, ","));
/*      */       }
/*      */       else
/*      */       {
/* 1860 */         setProperty(i, null);
/*      */       }
/* 1862 */       break;
/*      */     case 50:
/* 1865 */       if ((paramObject != null) && (!paramObject instanceof String) && (!paramObject instanceof GSSManager))
/*      */       {
/* 1869 */         ErrorMessage.raiseError("JZ015");
/*      */       }
/*      */ 
/* 1872 */       setProperty(i, paramObject);
/* 1873 */       break;
/*      */     case 66:
/* 1876 */       if ((paramObject != null) && (!paramObject instanceof String) && (!paramObject instanceof Provider))
/*      */       {
/* 1880 */         ErrorMessage.raiseError("JZ028");
/*      */       }
/*      */ 
/* 1883 */       setProperty(i, paramObject);
/* 1884 */       break;
/*      */     case 86:
/*      */       try
/*      */       {
/* 1889 */         Integer.valueOf(paramObject.toString());
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException5)
/*      */       {
/* 1893 */         if ((paramObject != null) && (paramObject instanceof String))
/*      */         {
/* 1895 */           if (((String)paramObject).equalsIgnoreCase("false"))
/*      */           {
/* 1897 */             setProperty(i, "0");
/* 1898 */             break label1489:
/*      */           }
/* 1900 */           if (((String)paramObject).equalsIgnoreCase("true"))
/*      */           {
/* 1902 */             setProperty(i, "1");
/* 1903 */             break label1489:
/*      */           }
/*      */         }
/* 1906 */         ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */       }
/*      */ 
/* 1909 */       setProperty(i, paramObject);
/* 1910 */       break;
/*      */     default:
/* 1914 */       ErrorMessage.raiseError("JZ012", PROPNAME[i]);
/*      */     }
/*      */ 
/* 1920 */     label1489: setVersionDefaults();
/*      */   }
/*      */ 
/*      */   public String getString(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1928 */     switch (paramInt)
/*      */     {
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 17:
/*      */     case 18:
/*      */     case 20:
/*      */     case 21:
/*      */     case 24:
/*      */     case 31:
/*      */     case 36:
/*      */     case 42:
/*      */     case 45:
/*      */     case 49:
/*      */     case 57:
/*      */     case 60:
/*      */     case 68:
/*      */     case 69:
/*      */     case 73:
/*      */     case 77:
/*      */     case 96:
/*      */     case 101:
/* 1960 */       if ((paramInt == 9) && (this._propValue[paramInt] instanceof EncryptedValue))
/*      */       {
/* 1962 */         return ((EncryptedValue)this._propValue[paramInt]).getValue(); } return (this._propValue[paramInt] == null) ? null : this._propValue[paramInt].toString();
/*      */     case 6:
/*      */     case 12:
/*      */     case 13:
/*      */     case 14:
/*      */     case 15:
/*      */     case 16:
/*      */     case 19:
/*      */     case 22:
/*      */     case 23:
/*      */     case 25:
/*      */     case 26:
/*      */     case 27:
/*      */     case 28:
/*      */     case 29:
/*      */     case 30:
/*      */     case 32:
/*      */     case 33:
/*      */     case 34:
/*      */     case 35:
/*      */     case 37:
/*      */     case 38:
/*      */     case 39:
/*      */     case 40:
/*      */     case 41:
/*      */     case 43:
/*      */     case 44:
/*      */     case 46:
/*      */     case 47:
/*      */     case 48:
/*      */     case 50:
/*      */     case 51:
/*      */     case 52:
/*      */     case 53:
/*      */     case 54:
/*      */     case 55:
/*      */     case 56:
/*      */     case 58:
/*      */     case 59:
/*      */     case 61:
/*      */     case 62:
/*      */     case 63:
/*      */     case 64:
/*      */     case 65:
/*      */     case 66:
/*      */     case 67:
/*      */     case 70:
/*      */     case 71:
/*      */     case 72:
/*      */     case 74:
/*      */     case 75:
/*      */     case 76:
/*      */     case 78:
/*      */     case 79:
/*      */     case 80:
/*      */     case 81:
/*      */     case 82:
/*      */     case 83:
/*      */     case 84:
/*      */     case 85:
/*      */     case 86:
/*      */     case 87:
/*      */     case 88:
/*      */     case 89:
/*      */     case 90:
/*      */     case 91:
/*      */     case 92:
/*      */     case 93:
/*      */     case 94:
/*      */     case 95:
/*      */     case 97:
/*      */     case 98:
/*      */     case 99:
/*      */     case 100: } ErrorMessage.raiseError("JZ012", PROPNAME[paramInt]);
/*      */ 
/* 1970 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 1978 */     switch (paramInt) { case 6:
/*      */     case 14:
/*      */     case 15:
/*      */     case 23:
/*      */     case 25:
/*      */     case 26:
/*      */     case 27:
/*      */     case 28:
/*      */     case 29:
/*      */     case 30:
/*      */     case 32:
/*      */     case 33:
/*      */     case 34:
/*      */     case 35:
/*      */     case 39:
/*      */     case 40:
/*      */     case 41:
/*      */     case 43:
/*      */     case 44:
/*      */     case 46:
/*      */     case 48:
/*      */     case 51:
/*      */     case 52:
/*      */     case 53:
/*      */     case 54:
/*      */     case 55:
/*      */     case 58:
/*      */     case 59:
/*      */     case 63:
/*      */     case 64:
/*      */     case 65:
/*      */     case 67:
/*      */     case 70:
/*      */     case 71:
/*      */     case 72:
/*      */     case 74:
/*      */     case 75:
/*      */     case 76:
/*      */     case 78:
/*      */     case 79:
/*      */     case 80:
/*      */     case 81:
/*      */     case 82:
/*      */     case 83:
/*      */     case 84:
/*      */     case 87:
/*      */     case 88:
/*      */     case 89:
/*      */     case 90:
/*      */     case 92:
/*      */     case 97:
/*      */     case 98:
/*      */     case 99:
/*      */     case 100:
/*      */     case 102:
/* 2036 */       return Boolean.valueOf(this._propValue[paramInt].toString()).booleanValue();
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 16:
/*      */     case 17:
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/*      */     case 21:
/*      */     case 22:
/*      */     case 24:
/*      */     case 31:
/*      */     case 36:
/*      */     case 37:
/*      */     case 38:
/*      */     case 42:
/*      */     case 45:
/*      */     case 47:
/*      */     case 49:
/*      */     case 50:
/*      */     case 56:
/*      */     case 57:
/*      */     case 60:
/*      */     case 61:
/*      */     case 62:
/*      */     case 66:
/*      */     case 68:
/*      */     case 69:
/*      */     case 73:
/*      */     case 77:
/*      */     case 85:
/*      */     case 86:
/*      */     case 91:
/*      */     case 93:
/*      */     case 94:
/*      */     case 95:
/*      */     case 96:
/*      */     case 101: } ErrorMessage.raiseError("JZ012", PROPNAME[paramInt]);
/*      */ 
/* 2042 */     return false;
/*      */   }
/*      */ 
/*      */   public int getInteger(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2050 */     switch (paramInt)
/*      */     {
/*      */     case 12:
/*      */     case 13:
/*      */     case 16:
/*      */     case 19:
/*      */     case 47:
/*      */     case 56:
/*      */     case 61:
/*      */     case 62:
/*      */     case 85:
/*      */     case 86:
/*      */     case 91:
/*      */     case 94:
/*      */     case 95:
/* 2066 */       return Integer.valueOf(this._propValue[paramInt].toString()).intValue();
/*      */     case 22:
/* 2072 */       return Integer.valueOf(this._propValue[paramInt].toString().substring(0, 1)).intValue();
/*      */     }
/*      */ 
/* 2075 */     ErrorMessage.raiseError("JZ012", PROPNAME[paramInt]);
/*      */ 
/* 2077 */     return 0;
/*      */   }
/*      */ 
/*      */   public long getMaxBICount() throws SQLException
/*      */   {
/* 2082 */     long l = getLong(93);
/* 2083 */     return (l == -1L) ? -1L : l / getBufferSize();
/*      */   }
/*      */ 
/*      */   public int getBufferSize() throws SQLException
/*      */   {
/* 2088 */     int i = 0;
/* 2089 */     int j = 0;
/* 2090 */     int k = 4096;
/*      */ 
/* 2092 */     if (isPropertySet(12))
/*      */     {
/* 2094 */       i = getInteger(12);
/*      */     }
/*      */     else
/*      */     {
/* 2098 */       i = Integer.parseInt(getDefaultValue(12));
/*      */     }
/* 2100 */     i *= 2;
/* 2101 */     if (isPropertySet(13))
/*      */     {
/* 2103 */       j = getInteger(13);
/*      */     }
/*      */     else
/*      */     {
/* 2107 */       j = Integer.parseInt(getDefaultValue(13));
/*      */     }
/*      */ 
/* 2111 */     k = (j < i) ? i : (j <= 0) ? 4096 : (4096 < i) ? i : j;
/*      */ 
/* 2113 */     return k;
/*      */   }
/*      */ 
/*      */   private String getMinBIValue() throws SQLException
/*      */   {
/* 2118 */     return Math.round(getBufferSize() / 1024) + "";
/*      */   }
/*      */ 
/*      */   private String getMaxBIValue() throws SQLException
/*      */   {
/* 2123 */     return Math.round((float)(Runtime.getRuntime().maxMemory() / 1024L)) + "";
/*      */   }
/*      */ 
/*      */   public long getLong(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2132 */     switch (paramInt)
/*      */     {
/*      */     case 93:
/* 2136 */       if (isPropertySet(paramInt))
/*      */       {
/* 2138 */         String str1 = this._propValue[paramInt].toString();
/*      */ 
/* 2141 */         str1 = str1.replaceAll("\\s", "");
/* 2142 */         str1 = str1.toLowerCase();
/*      */ 
/* 2145 */         String str2 = str1.split("[kKmMgG%][Bb]?")[0];
/* 2146 */         long l1 = Long.parseLong(str2);
/* 2147 */         long l2 = 0L;
/*      */ 
/* 2150 */         if (str1.indexOf('%') != -1)
/*      */         {
/* 2152 */           l2 = Runtime.getRuntime().maxMemory() / 100L * l1;
/*      */         }
/*      */ 
/* 2156 */         if (str1.indexOf('k') != -1)
/*      */         {
/* 2158 */           l2 = l1 * 1024L;
/*      */         }
/* 2160 */         if (str1.indexOf('m') != -1)
/*      */         {
/* 2162 */           l2 = l1 * 1048576L;
/*      */         }
/* 2164 */         if (str1.indexOf('g') != -1)
/*      */         {
/* 2166 */           l2 = l1 * 1073741824L;
/*      */         }
/*      */ 
/* 2169 */         if (l2 > 0L)
/*      */         {
/* 2176 */           if (getBufferSize() > l2)
/*      */           {
/* 2178 */             ErrorMessage.raiseError("JZ046", getMinBIValue(), getMaxBIValue());
/*      */           }
/*      */ 
/* 2182 */           return l2;
/*      */         }
/*      */ 
/* 2188 */         ErrorMessage.raiseError("JZ046", getMinBIValue(), getMaxBIValue());
/*      */ 
/* 2194 */         ErrorMessage.raiseError("JZ046", getMinBIValue(), getMaxBIValue());
/*      */       }
/*      */       else
/*      */       {
/* 2200 */         return -1L;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2206 */     ErrorMessage.raiseError("JZ012", PROPNAME[paramInt]);
/*      */ 
/* 2208 */     return -1L;
/*      */   }
/*      */ 
/*      */   public float getFloat(int paramInt) throws SQLException
/*      */   {
/* 2213 */     switch (paramInt)
/*      */     {
/*      */     case 22:
/* 2216 */       return Float.valueOf(this._propValue[paramInt].toString()).floatValue();
/*      */     }
/*      */ 
/* 2219 */     ErrorMessage.raiseError("JZ012", PROPNAME[paramInt]);
/*      */ 
/* 2221 */     return 0.0F;
/*      */   }
/*      */ 
/*      */   public Object getObject(int paramInt)
/*      */     throws SQLException
/*      */   {
/* 2228 */     switch (paramInt)
/*      */     {
/*      */     case 37:
/*      */     case 38:
/*      */     case 50:
/*      */     case 66:
/* 2234 */       return this._propValue[paramInt];
/*      */     }
/*      */ 
/* 2238 */     ErrorMessage.raiseError("JZ012", PROPNAME[paramInt]);
/*      */ 
/* 2240 */     return null;
/*      */   }
/*      */ 
/*      */   public Properties getProperties()
/*      */   {
/* 2249 */     return this._info;
/*      */   }
/*      */ 
/*      */   String getDefaultValue(int paramInt)
/*      */   {
/* 2257 */     return DEF_PROP_VALUE[paramInt];
/*      */   }
/*      */ 
/*      */   int getKey(String paramString)
/*      */   {
/* 2266 */     if ((paramString.equalsIgnoreCase("ENABLE_FUNCTIONALITY_GROUP")) || (paramString.equalsIgnoreCase("STRIP_BLANKS")))
/*      */     {
/* 2268 */       return -1;
/*      */     }
/* 2270 */     for (int i = 0; i < 103; ++i)
/*      */     {
/* 2272 */       if (paramString.equalsIgnoreCase(PROPNAME[i]))
/*      */       {
/* 2274 */         return i;
/*      */       }
/*      */     }
/* 2277 */     return -1;
/*      */   }
/*      */ 
/*      */   Properties getAllProperties()
/*      */   {
/* 2285 */     Properties localProperties = new Properties();
/* 2286 */     for (int i = 0; i < 103; ++i)
/*      */     {
/* 2288 */       if (PROPNAME[i].equalsIgnoreCase("ENABLE_FUNCTIONALITY_GROUP")) continue; if (PROPNAME[i].equalsIgnoreCase("STRIP_BLANKS")) {
/*      */         continue;
/*      */       }
/*      */ 
/* 2292 */       if ((this._propValue != null) && (this._propValue[i] != null))
/*      */       {
/* 2294 */         localProperties.put(PROPNAME[i], this._propValue[i]);
/*      */       }
/* 2296 */       if ((this._propValue == null) || (this._propValue[i] != null))
/*      */         continue;
/* 2298 */       localProperties.put(PROPNAME[i], "");
/*      */     }
/*      */ 
/* 2301 */     return localProperties;
/*      */   }
/*      */ 
/*      */   public void setProperty(int paramInt, Object paramObject)
/*      */   {
/* 2310 */     this._propValue[paramInt] = paramObject;
/*      */   }
/*      */ 
/*      */   protected void setProperty(int paramInt, Object paramObject, boolean paramBoolean)
/*      */   {
/* 2321 */     setProperty(paramInt, paramObject);
/* 2322 */     this._hasBeenSet[paramInt] = paramBoolean;
/*      */   }
/*      */ 
/*      */   protected void verifyProps()
/*      */     throws SQLException
/*      */   {
/* 2332 */     for (int i = 0; i < 103; ++i)
/*      */     {
/* 2334 */       Object localObject = this._propValue[i];
/* 2335 */       switch (i)
/*      */       {
/*      */       case 0:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 17:
/*      */       case 18:
/*      */       case 20:
/*      */       case 21:
/*      */       case 24:
/*      */       case 31:
/*      */       case 36:
/*      */       case 42:
/*      */       case 45:
/*      */       case 49:
/*      */       case 57:
/*      */       case 60:
/*      */       case 68:
/*      */       case 69:
/*      */       case 73:
/*      */       case 77:
/*      */       case 96:
/*      */       case 101:
/* 2368 */         if ((localObject == null) || ((localObject instanceof String) && (((String)localObject).length() == 0)))
/*      */         {
/* 2371 */           localObject = DEF_PROP_VALUE[i];
/*      */         }
/* 2373 */         setProperty(i, localObject);
/* 2374 */         break;
/*      */       case 6:
/*      */       case 14:
/*      */       case 15:
/*      */       case 23:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/*      */       case 35:
/*      */       case 39:
/*      */       case 40:
/*      */       case 41:
/*      */       case 43:
/*      */       case 44:
/*      */       case 46:
/*      */       case 48:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 58:
/*      */       case 59:
/*      */       case 63:
/*      */       case 64:
/*      */       case 65:
/*      */       case 67:
/*      */       case 70:
/*      */       case 71:
/*      */       case 72:
/*      */       case 74:
/*      */       case 75:
/*      */       case 76:
/*      */       case 78:
/*      */       case 79:
/*      */       case 80:
/*      */       case 81:
/*      */       case 82:
/*      */       case 83:
/*      */       case 84:
/*      */       case 87:
/*      */       case 88:
/*      */       case 89:
/*      */       case 90:
/*      */       case 92:
/*      */       case 97:
/*      */       case 98:
/*      */       case 99:
/*      */       case 100:
/*      */       case 102:
/* 2432 */         int j = 0;
/* 2433 */         if (localObject == null)
/*      */         {
/* 2436 */           localObject = "true";
/* 2437 */           j = 1;
/*      */         }
/* 2439 */         else if ((localObject instanceof String) && (!((String)localObject).equalsIgnoreCase("false")) && (!((String)localObject).equalsIgnoreCase("true")) && (!localObject instanceof Boolean))
/*      */         {
/* 2445 */           ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */         }
/*      */ 
/* 2448 */         if (j == 0)
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/* 2450 */         setProperty(i, Boolean.valueOf((String)localObject)); break;
/*      */       case 12:
/*      */       case 13:
/*      */       case 16:
/*      */       case 19:
/*      */       case 56:
/*      */       case 61:
/*      */       case 62:
/*      */       case 85:
/*      */       case 86:
/*      */       case 91:
/*      */       case 94:
/*      */       case 95:
/*      */         try
/*      */         {
/* 2469 */           Integer.valueOf(localObject.toString());
/*      */         }
/*      */         catch (NumberFormatException localNumberFormatException1)
/*      */         {
/* 2473 */           ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */         }
/*      */ 
/* 2476 */         setProperty(i, localObject);
/* 2477 */         break;
/*      */       case 93:
/* 2481 */         validateInternalReadBufferLimit(localObject);
/* 2482 */         break;
/*      */       case 47:
/*      */         try
/*      */         {
/* 2487 */           Integer.valueOf(localObject.toString()).intValue();
/*      */         }
/*      */         catch (NumberFormatException localNumberFormatException2)
/*      */         {
/* 2491 */           ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */         }
/*      */ 
/* 2494 */         if ((Integer.valueOf(localObject.toString()).intValue() <= 0) && (Integer.valueOf(localObject.toString()).intValue() != -2147483648))
/*      */         {
/* 2498 */           ErrorMessage.raiseError("JZ0BJ");
/*      */         }
/*      */ 
/* 2501 */         setProperty(i, localObject);
/* 2502 */         break;
/*      */       case 22:
/* 2505 */         float f = 99.0F;
/*      */         try
/*      */         {
/* 2508 */           f = Float.valueOf(localObject.toString()).floatValue();
/*      */         }
/*      */         catch (NumberFormatException localNumberFormatException3)
/*      */         {
/* 2512 */           ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */         }
/*      */ 
/* 2515 */         if ((f == 2.0F) || (f == 3.0F) || (f == 4.0F) || (f == 5.0F) || (f == 5.0F) || (f == 6.0F) || (f == 6.05F))
/*      */         {
/* 2523 */           localObject = new Integer((int)f);
/*      */         }
/* 2525 */         else if ((f == 0.0F) || (f == 99.0F) || (f == 100.0F))
/*      */         {
/* 2529 */           f = 7.0F;
/* 2530 */           localObject = new Float(f);
/*      */         }
/* 2532 */         else if (f == 7.0F)
/*      */         {
/* 2534 */           localObject = new Float(f);
/*      */         }
/*      */         else
/*      */         {
/* 2538 */           ErrorMessage.raiseError("JZ0D6", "" + f);
/*      */         }
/*      */ 
/* 2541 */         setProperty(i, localObject);
/* 2542 */         this._version = f;
/* 2543 */         break;
/*      */       case 37:
/* 2546 */         if ((localObject != null) && (!localObject instanceof DynamicClassLoader))
/*      */         {
/* 2549 */           ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */         }
/*      */ 
/* 2552 */         setProperty(i, localObject);
/* 2553 */         break;
/*      */       case 38:
/* 2556 */         if ((localObject != null) && (!localObject instanceof String))
/*      */         {
/* 2558 */           ErrorMessage.raiseError("JZ011", PROPNAME[i]);
/*      */         }
/*      */ 
/* 2567 */         if ((localObject == null) || (localObject instanceof String[]))
/*      */           continue;
/* 2569 */         setProperty(i, tokenizeString((String)localObject, ",")); break;
/*      */       case 50:
/* 2574 */         if ((localObject != null) && (!localObject instanceof String) && (!localObject instanceof GSSManager))
/*      */         {
/* 2578 */           ErrorMessage.raiseError("JZ015");
/*      */         }
/*      */ 
/* 2581 */         setProperty(i, localObject);
/* 2582 */         break;
/*      */       case 66:
/* 2585 */         if ((localObject != null) && (!localObject instanceof String) && (!localObject instanceof Provider))
/*      */         {
/* 2589 */           ErrorMessage.raiseError("JZ028");
/*      */         }
/*      */ 
/* 2592 */         setProperty(i, localObject);
/* 2593 */         break;
/*      */       default:
/* 2597 */         ErrorMessage.raiseError("JZ012", PROPNAME[i]);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2602 */     setVersionDefaults();
/*      */   }
/*      */ 
/*      */   private void validateInternalReadBufferLimit(Object paramObject)
/*      */     throws SQLException
/*      */   {
/* 2608 */     if ((paramObject == null) || ((paramObject instanceof String) && (((String)paramObject).length() == 0)) || (((String)paramObject).trim().equals("-1")))
/*      */     {
/* 2612 */       paramObject = DEF_PROP_VALUE[93];
/* 2613 */       this._hasBeenSet[93] = false;
/* 2614 */       return;
/*      */     }
/*      */ 
/* 2623 */     if (!paramObject.toString().matches("\\s*\\b(\\d+?\\s*[kKmMgG%][Bb]?)\\s*"))
/*      */     {
/* 2625 */       ErrorMessage.raiseError("JZ046", getMinBIValue(), getMaxBIValue());
/*      */     }
/*      */ 
/* 2631 */     String str1 = ((String)paramObject).replaceAll("\\s", "");
/*      */ 
/* 2633 */     String str2 = str1.split("[kKmMgG%][Bb]?")[0];
/*      */     try
/*      */     {
/* 2636 */       long l = Long.parseLong(str2);
/*      */ 
/* 2638 */       if (l == 0L)
/*      */       {
/* 2640 */         ErrorMessage.raiseError("JZ046", getMinBIValue(), getMaxBIValue());
/*      */       }
/*      */ 
/* 2645 */       if ((str1.indexOf('%') != -1) && 
/* 2647 */         (l > 99L))
/*      */       {
/* 2649 */         ErrorMessage.raiseError("JZ046", getMinBIValue(), getMaxBIValue());
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/* 2658 */       ErrorMessage.raiseError("JZ046", getMinBIValue(), getMaxBIValue());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected SQLWarning getWarnings()
/*      */   {
/* 2669 */     return this._warnings;
/*      */   }
/*      */ 
/*      */   private void stashWarning(SQLException paramSQLException)
/*      */     throws SQLException
/*      */   {
/* 2677 */     if (!paramSQLException instanceof SQLWarning)
/*      */     {
/* 2679 */       throw paramSQLException;
/*      */     }
/* 2681 */     if (this._warnings == null)
/*      */     {
/* 2683 */       this._warnings = ((SQLWarning)paramSQLException);
/*      */     }
/*      */     else
/*      */     {
/* 2687 */       this._warnings.setNextWarning((SQLWarning)paramSQLException);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setPropertyDefaults()
/*      */   {
/* 2699 */     for (int i = 0; i < 103; ++i)
/*      */     {
/* 2701 */       if (this._hasBeenSet[i] != 0)
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/* 2707 */       this._propValue[i] = DEF_PROP_VALUE[i];
/*      */     }
/*      */ 
/* 2711 */     if (this._hasBeenSet[22] == 0)
/*      */     {
/* 2713 */       setProperty(22, new Float(this._version));
/*      */     }
/*      */ 
/* 2718 */     if ((!Boolean.valueOf(this._propValue[15].toString()).booleanValue()) || (this._hasBeenSet[25] != 0)) {
/*      */       return;
/*      */     }
/* 2721 */     this._propValue[25] = "false";
/*      */   }
/*      */ 
/*      */   private void setVersionDefaults()
/*      */   {
/* 2733 */     if (this._hasBeenSet[8] == 0)
/*      */     {
/* 2735 */       if (this._version == 2.0F)
/*      */       {
/* 2738 */         setProperty(8, "iso_1");
/*      */       }
/*      */       else
/*      */       {
/* 2743 */         setProperty(8, DEF_PROP_VALUE[8]);
/*      */       }
/*      */     }
/*      */ 
/* 2747 */     if (this._hasBeenSet[21] == 0)
/*      */     {
/* 2749 */       if (this._version == 2.0F)
/*      */       {
/* 2752 */         String str = (this._propValue[8] == null) ? "iso_1" : this._propValue[8].toString();
/*      */ 
/* 2754 */         if (Iana.truncationConversionOK(str))
/*      */         {
/* 2758 */           setProperty(21, "com.sybase.jdbc3.charset.TruncationConverter");
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2765 */         setProperty(21, DEF_PROP_VALUE[21]);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2770 */     if (this._hasBeenSet[7] == 0)
/*      */     {
/* 2772 */       if ((this._version == 2.0F) || (this._version == 3.0F))
/*      */       {
/* 2776 */         setProperty(7, "us_english");
/*      */       }
/*      */       else
/*      */       {
/* 2781 */         setProperty(7, DEF_PROP_VALUE[7]);
/*      */       }
/*      */     }
/*      */ 
/* 2785 */     if (this._hasBeenSet[23] == 0)
/*      */     {
/* 2787 */       if (this._version > 3.0F)
/*      */       {
/* 2789 */         setProperty(23, new Boolean(false));
/*      */       }
/*      */       else
/*      */       {
/* 2794 */         setProperty(23, DEF_PROP_VALUE[23]);
/*      */       }
/*      */     }
/*      */ 
/* 2798 */     if (this._hasBeenSet[35] == 0)
/*      */     {
/* 2800 */       if (this._version < 4.0F)
/*      */       {
/* 2802 */         setProperty(35, new Boolean(true));
/*      */       }
/*      */       else
/*      */       {
/* 2807 */         setProperty(35, DEF_PROP_VALUE[35]);
/*      */       }
/*      */     }
/*      */ 
/* 2811 */     if ((this._hasBeenSet[44] == 0) && 
/* 2813 */       (this._version >= 6.05F))
/*      */     {
/* 2818 */       setProperty(44, new Boolean(false));
/*      */     }
/*      */ 
/* 2822 */     if ((this._hasBeenSet[25] == 0) && 
/* 2824 */       (this._version < 7.0F))
/*      */     {
/* 2829 */       setProperty(25, new Boolean(false));
/*      */     }
/*      */ 
/* 2833 */     if ((this._hasBeenSet[76] != 0) || 
/* 2835 */       (this._version >= 7.0F))
/*      */     {
/*      */       return;
/*      */     }
/*      */ 
/* 2840 */     setProperty(76, new Boolean(false));
/*      */   }
/*      */ 
/*      */   private void setPerformanceProps()
/*      */   {
/* 2853 */     setProperty(74, new Boolean("true"), true);
/*      */   }
/*      */ 
/*      */   private void setJDBCComplianceProps()
/*      */   {
/* 2865 */     setProperty(23, new Boolean("false"), true);
/*      */ 
/* 2870 */     setProperty(27, new Boolean("false"), true);
/*      */ 
/* 2872 */     setProperty(33, new Boolean("true"), true);
/*      */ 
/* 2874 */     setProperty(39, new Boolean("true"), true);
/*      */ 
/* 2876 */     setProperty(40, new Boolean("false"), true);
/*      */   }
/*      */ 
/*      */   private static String[] tokenizeString(String paramString1, String paramString2)
/*      */   {
/* 2886 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString1, paramString2, false);
/* 2887 */     String[] arrayOfString = null;
/* 2888 */     if (localStringTokenizer != null)
/*      */     {
/* 2890 */       int i = localStringTokenizer.countTokens();
/* 2891 */       arrayOfString = new String[i];
/* 2892 */       for (int j = 0; j < i; ++j)
/*      */       {
/* 2894 */         arrayOfString[j] = localStringTokenizer.nextToken();
/*      */       }
/*      */     }
/* 2897 */     return arrayOfString;
/*      */   }
/*      */ 
/*      */   public String getPropertyDescription(String paramString)
/*      */   {
/* 2902 */     String str = null;
/* 2903 */     if (_propertyDescriptions == null)
/*      */     {
/*      */       try
/*      */       {
/* 2908 */         _propertyDescriptions = PropertyResourceBundle.getBundle("com.sybase.jdbc3.jdbc.resource.DriverProperties");
/*      */       }
/*      */       catch (Exception localException1)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2923 */     if (_propertyDescriptions == null)
/*      */     {
/* 2927 */       str = "";
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/* 2933 */         str = _propertyDescriptions.getString(paramString);
/*      */       }
/*      */       catch (Exception localException2)
/*      */       {
/* 2943 */         str = "";
/*      */       }
/*      */     }
/* 2946 */     return str;
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws ClassNotFoundException, IOException
/*      */   {
/* 2960 */     paramObjectInputStream.defaultReadObject();
/* 2961 */     int i = this._propValue.length;
/* 2962 */     if (i >= 103)
/*      */       return;
/* 2964 */     Object[] arrayOfObject = new Object[103];
/* 2965 */     System.arraycopy(this._propValue, 0, arrayOfObject, 0, this._propValue.length);
/* 2966 */     this._propValue = arrayOfObject;
/*      */ 
/* 2968 */     boolean[] arrayOfBoolean = new boolean[103];
/* 2969 */     System.arraycopy(this._hasBeenSet, 0, arrayOfBoolean, 0, this._hasBeenSet.length);
/* 2970 */     this._hasBeenSet = arrayOfBoolean;
/*      */ 
/* 2972 */     for (int j = i; j < 103; ++j)
/*      */     {
/*      */       try
/*      */       {
/* 2976 */         setConnProperty(PROPNAME[j], DEF_PROP_VALUE[j], true, true);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isPropertySet(int paramInt)
/*      */   {
/* 2991 */     return (this._hasBeenSet[paramInt] != 0) && (!DEF_PROP_VALUE[paramInt].equals(this._propValue[paramInt].toString()));
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybProperty
 * JD-Core Version:    0.5.4
 */