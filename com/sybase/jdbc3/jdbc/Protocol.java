package com.sybase.jdbc3.jdbc;

import com.sybase.jdbc3.utils.CacheManager;
import com.sybase.jdbcx.SybEventHandler;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;

public abstract interface Protocol
{
  public static final int RESULT_NONE = 0;
  public static final int RESULT_RESULTSET = 1;
  public static final int RESULT_PARAM = 3;
  public static final int RESULT_COUNT = 5;
  public static final int ALT_RESULTSET = 6;
  public static final int OPTION_AUTOCOMMIT = 1;
  public static final int OPTION_TRANSACTION_ISOLATION_LEVEL = 2;
  public static final int OPTION_READONLY = 3;
  public static final int OPTION_ROWCOUNT = 4;
  public static final int OPTION_SESSION_ID = 5;
  public static final int OPTION_CHARSET_CONVERT = 6;
  public static final int OPTION_ENDPOINT = 7;
  public static final int OPTION_CAPTURE = 8;
  public static final int OPTION_CATALOG = 9;
  public static final int OPTION_FUNCTION_MAP = 10;
  public static final int OPTION_CONFIGURE = 11;
  public static final int OPTION_STRIP_EXEC = 12;
  public static final int OPTION_LOB_LOCATOR = 13;
  public static final int OPTION_LOG_BULK_COPY = 14;
  public static final int DEFAULT_PACKET_SIZE = 512;
  public static final String DEFAULT_LANGUAGE = "us_english";
  public static final String DEFAULT_CHARSET = "iso_1";
  public static final int NON_HA = 0;
  public static final int PRIMARY = 1;
  public static final int PRIMARY_AGAIN = 3;
  public static final int SECONDARY_INIT = 5;
  public static final int SECONDARY_FAILOVER = 7;
  public static final int CLUSTER_LOGIN_REDIRECTION = 8;
  public static final int CLUSTER_MIGRATION = 16;
  public static final int CLUSTER_MIGRATION_ENABLE_HA = 17;
  public static final int HA_STATE_UNDEFINED = -1;
  public static final int TRY_PRIMARY = 0;
  public static final int TRY_SECONDARY = 1;
  public static final int CONNECTED_PRIMARY = 2;
  public static final int CONNECTED_SECONDARY = 3;
  public static final int RETRY_PRIMARY_FROM_PRIMARY = 4;
  public static final int RETRY_PRIMARY_FROM_SECONDARY = 5;
  public static final int RECONNECT_SECONDARY = 6;
  public static final int TRY_CLUSTER_NODE_CONNECT = 7;
  public static final int CONNECTED_CLUSTER_NODE = 8;
  public static final int TRY_CLUSTER_NODE_MIGRATE = 9;
  public static final int TRY_CLUSTER_NODE_FAILOVER = 10;

  public abstract Protocol getProtocol()
    throws SQLException;

  public abstract void login(String paramString, SybProperty paramSybProperty, SybConnection paramSybConnection, boolean paramBoolean)
    throws SQLException;

  public abstract void logout()
    throws SQLException;

  public abstract void abort();

  public abstract void language(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager)
    throws SQLException;

  public abstract void rpc(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager)
    throws SQLException;

  public abstract void dynamicPrepare(ProtocolContext paramProtocolContext, String paramString1, String paramString2, ParamManager paramParamManager)
    throws SQLException;

  public abstract void languageBatch(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager, ArrayList paramArrayList, int paramInt, boolean paramBoolean)
    throws SQLException;

  public abstract void sendDynamicExecuteParams(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager, int paramInt, Object[] paramArrayOfObject, int[] paramArrayOfInt1, Calendar[] paramArrayOfCalendar, int[] paramArrayOfInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws SQLException;

  public abstract void dynamicExecute(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager, int paramInt, Object[] paramArrayOfObject, int[] paramArrayOfInt1, Calendar[] paramArrayOfCalendar, int[] paramArrayOfInt2, boolean paramBoolean1, boolean paramBoolean2)
    throws SQLException;

  public abstract void dynamicDeallocate(ProtocolContext paramProtocolContext, String paramString)
    throws SQLException;

  public abstract ResultSetMetaData dynamicMetaData(ProtocolContext paramProtocolContext);

  public abstract Cursor getCursor(ProtocolContext paramProtocolContext, boolean paramBoolean)
    throws SQLException;

  public abstract void setOption(ProtocolContext paramProtocolContext, int paramInt, boolean paramBoolean)
    throws SQLException;

  public abstract void setOption(ProtocolContext paramProtocolContext, int paramInt, String paramString)
    throws SQLException;

  public abstract void setOption(ProtocolContext paramProtocolContext, int paramInt1, int paramInt2)
    throws SQLException;

  public abstract boolean getBoolOption(ProtocolContext paramProtocolContext, int paramInt)
    throws SQLException;

  public abstract String getStringOption(ProtocolContext paramProtocolContext, int paramInt, String paramString)
    throws SQLException;

  public abstract int getIntOption(ProtocolContext paramProtocolContext, int paramInt)
    throws SQLException;

  public abstract Object getObjectOption(ProtocolContext paramProtocolContext, int paramInt)
    throws SQLException;

  public abstract void cancel(ProtocolContext paramProtocolContext, boolean paramBoolean)
    throws SQLException;

  public abstract void cancel(ProtocolContext paramProtocolContext, boolean paramBoolean1, boolean paramBoolean2)
    throws SQLException;

  public abstract void resync(ProtocolContext paramProtocolContext)
    throws SQLException;

  public abstract void endTransaction(boolean paramBoolean)
    throws SQLException;

  public abstract void bulkWrite(TextPointer paramTextPointer, InputStream paramInputStream, int paramInt1, int paramInt2, boolean paramBoolean)
    throws SQLException;

  public abstract void writePage(TextPointer paramTextPointer, byte[] paramArrayOfByte, String paramString, int paramInt)
    throws SQLException;

  public abstract int nextResult(ProtocolContext paramProtocolContext)
    throws SQLException;

  public abstract void ungetResult(ProtocolContext paramProtocolContext, int paramInt)
    throws SQLException;

  public abstract ProtocolResultSet resultSet(ProtocolContext paramProtocolContext)
    throws SQLException;

  public abstract Param[] paramArray(ProtocolContext paramProtocolContext, int paramInt);

  public abstract Param[] paramArray(int paramInt, CacheManager paramCacheManager);

  public abstract Param getParam();

  public abstract LinkedHashMap getHADRListMap();

  public abstract void param(ParamManager paramParamManager)
    throws SQLException;

  public abstract int count(ProtocolContext paramProtocolContext)
    throws SQLException;

  public abstract ProtocolContext getProtocolContext(SybProperty paramSybProperty);

  public abstract void makeEventContext(String paramString, SybEventHandler paramSybEventHandler, int paramInt)
    throws SQLException;

  public abstract void killEventContext(String paramString)
    throws SQLException;

  public abstract boolean getSendLock(ProtocolContext paramProtocolContext)
    throws SQLException;

  public abstract void freeSendLock(ProtocolContext paramProtocolContext);

  public abstract DynamicClassLoader getClassLoader();

  public abstract int sendBulkData(Object[] paramArrayOfObject, int[] paramArrayOfInt1, int[] paramArrayOfInt2, Calendar[] paramArrayOfCalendar)
    throws SQLException, IOException;

  public abstract int sendBulkData(LinkedList paramLinkedList1, LinkedList paramLinkedList2, LinkedList paramLinkedList3, LinkedList paramLinkedList4)
    throws SQLException, IOException;

  public abstract int flushBCP(boolean paramBoolean)
    throws SQLException;

  public abstract void initBCP(SybBCP paramSybBCP, String paramString, int paramInt)
    throws SQLException, IOException;

  public abstract void setRedirectImmed(boolean paramBoolean);

  public abstract boolean getRedirectImmed();

  public abstract Vector getRedirectionHostPort();

  public abstract boolean isAse();

  public abstract boolean getInTransaction();

  public abstract boolean serverAcceptsBigDateTimeData();

  public abstract boolean isLocatorSupported();

  public abstract boolean isReceivedHadrFailover();

  public abstract void setReceivedHadrFailover(boolean paramBoolean);

  public abstract String getHadrLatestPrimaryHostPort();

  public abstract boolean isLOBSupportedAsParameterToSproc();

  public abstract boolean initCommandExecSession(ProtocolContext paramProtocolContext)
    throws SQLException;

  public abstract void finishCommandExecSession(ProtocolContext paramProtocolContext, boolean paramBoolean)
    throws SQLException;

  public abstract void sendLanguageParams(ProtocolContext paramProtocolContext, String paramString, ParamManager paramParamManager, ArrayList paramArrayList, boolean paramBoolean1, int paramInt, boolean paramBoolean2)
    throws SQLException;

  public abstract void abortCommandSession(ProtocolContext paramProtocolContext);

  public abstract boolean isSuppressParamFormatSupportedAndSet()
    throws SQLException;

  public abstract boolean isDynamicHomogenousBatchSupportedAndSet()
    throws SQLException;

  public abstract boolean isLanguageHomogenousBatchSupportedAndSet()
    throws SQLException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.Protocol
 * JD-Core Version:    0.5.4
 */