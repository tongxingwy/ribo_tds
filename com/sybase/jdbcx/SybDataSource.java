package com.sybase.jdbcx;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.Referenceable;
import javax.sql.DataSource;

public abstract interface SybDataSource extends DataSource, Referenceable, Serializable
{
  public static final String DATABASE_NAME = "databaseName";
  public static final String DATA_SOURCE_NAME = "dataSourceName";
  public static final String DESCRIPTION = "description";
  public static final String NETWORK_PROTOCOL = "networkProtocol";
  public static final String PASSWORD = "password";
  public static final String PORT_NUMBER = "portNumber";
  public static final String SERVER_NAME = "serverName";
  public static final String USER = "user";
  public static final String CONNECTION_PROPERTY = "connectionProperty";
  public static final String RESOURCE_MANAGER_NAME = "resourceManagerName";
  public static final int RMTYPE_NO_MANAGER = 0;
  public static final int RMTYPE_XA_11 = 1;
  public static final int RMTYPE_ASE_XA_DTM = 2;

  public abstract void setVersion(int paramInt)
    throws SQLException;

  public abstract void setSybMessageHandler(SybMessageHandler paramSybMessageHandler);

  public abstract SybMessageHandler getSybMessageHandler();

  public abstract Debug getDebug();

  public abstract void setRemotePassword(String paramString1, String paramString2, Properties paramProperties);

  public abstract DynamicClassLoader getClassLoader(String paramString, Properties paramProperties);

  public abstract String getServerName();

  public abstract void setServerName(String paramString);

  public abstract String getDatabaseName();

  public abstract void setDatabaseName(String paramString);

  public abstract String getDataSourceName();

  public abstract void setDataSourceName(String paramString);

  public abstract String getDescription();

  public abstract void setDescription(String paramString);

  public abstract String getUser();

  public abstract void setUser(String paramString);

  public abstract String getPassword();

  public abstract void setPassword(String paramString);

  public abstract String getNetworkProtocol();

  public abstract void setNetworkProtocol(String paramString);

  public abstract int getPortNumber();

  public abstract void setPortNumber(int paramInt);

  public abstract Object getConnectionProperty(String paramString);

  public abstract void setConnectionProperties(Properties paramProperties)
    throws SQLException;

  public abstract void setResourceManagerName(String paramString);

  public abstract String getResourceManagerName();

  public abstract void setResourceManagerType(int paramInt);

  public abstract int getResourceManagerType();

  public abstract void setAddressList(String paramString);

  public abstract String getAddressList();
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybDataSource
 * JD-Core Version:    0.5.4
 */