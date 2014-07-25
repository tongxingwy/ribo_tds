package com.sybase.jdbcx;

import java.sql.SQLException;

public abstract interface DynamicClassLoader
{
  public abstract Class findClass(String paramString);

  public abstract void preloadJars(String[] paramArrayOfString)
    throws SQLException;

  public abstract void openConnection()
    throws SQLException;

  public abstract void closeConnection()
    throws SQLException;

  public abstract void setKeepConnectionAlive(boolean paramBoolean);

  public abstract boolean getKeepConnectionAlive();

  public abstract boolean hasClassBeenLoaded(String paramString);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.DynamicClassLoader
 * JD-Core Version:    0.5.4
 */