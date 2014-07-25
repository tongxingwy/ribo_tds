package com.sybase.jdbc3.jdbc;

import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

public abstract interface SybUrlProvider
{
  public static final String DEFAULT_PROTOCOL = "Tds";
  public static final String KEYWORD_SHM = "shm";
  public static final String SYBASE_OID = "1.3.6.1.4.1.897";
  public static final String ADDRESS_OID = "1.3.6.1.4.1.897.4.2.5";
  public static final String JCONNECT_PROTOCOL_OID = "1.3.6.1.4.1.897.4.2.9";
  public static final String JCONNECT_PROPERTY_OID = "1.3.6.1.4.1.897.4.2.10";
  public static final String JCONNECT_DBNAME_OID = "1.3.6.1.4.1.897.4.2.11";
  public static final String ADDRESS_HA_OID = "1.3.6.1.4.1.897.4.2.15";
  public static final String RMNAME_OID = "1.3.6.1.4.1.897.4.2.16";
  public static final String RMTYPE_OID = "1.3.6.1.4.1.897.4.2.17";
  public static final String JDBC_DATASOURCE_OID = "1.3.6.1.4.1.897.4.2.18";
  public static final String ADDRESS_ALIAS = "sybaseAddress";
  public static final String ADDRESS_HA_ALIAS = "sybaseHAservername";
  public static final String JCONNECT_PROTOCOL_ALIAS = "sybaseJconnectProtocol";
  public static final String JCONNECT_PROPERTY_ALIAS = "sybaseJconnectProperty";
  public static final String JCONNECT_DBNAME_ALIAS = "sybaseDatabaseName";
  public static final String RMNAME_ALIAS = "sybaseResourceManagerName";
  public static final String RMTYPE_ALIAS = "sybaseResourceManagerType";
  public static final String JDBC_DATASOURCE_ALIAS = "sybaseJdbcDataSourceInterface";

  public abstract Vector getHostPortList();

  public abstract Vector getSecondaryHostPortList();

  public abstract Protocol getProtocol();

  public abstract String getDatabaseName();

  public abstract SybProperty getSybProperty();

  public abstract String getResourceManagerName();

  public abstract int getResourceManagerType();

  public abstract String getDataSourceInterface();

  public abstract void init(String paramString1, String paramString2, Properties paramProperties, SybProperty paramSybProperty)
    throws SQLException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybUrlProvider
 * JD-Core Version:    0.5.4
 */