package com.sybase.jdbcx;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public abstract interface SybDriver extends Driver
{
  public static final int VERSION_2 = 2;
  public static final int VERSION_3 = 3;
  public static final int VERSION_4 = 4;
  public static final int VERSION_5 = 5;
  public static final int VERSION_6 = 6;
  public static final float VERSION_6_05 = 6.05F;
  public static final int VERSION_7 = 7;
  public static final int VERSION_LATEST = 0;

  public abstract void setVersion(int paramInt)
    throws SQLException;

  public abstract void setSybMessageHandler(SybMessageHandler paramSybMessageHandler);

  public abstract SybMessageHandler getSybMessageHandler();

  public abstract Debug getDebug();

  public abstract void setRemotePassword(String paramString1, String paramString2, Properties paramProperties);

  public abstract DynamicClassLoader getClassLoader(String paramString, Properties paramProperties);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybDriver
 * JD-Core Version:    0.5.4
 */