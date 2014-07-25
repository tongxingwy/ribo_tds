package com.sybase.jdbcx;

import java.sql.ResultSet;

public abstract interface EedInfo
{
  public abstract int getState();

  public abstract int getSeverity();

  public abstract String getServerName();

  public abstract String getProcedureName();

  public abstract int getLineNumber();

  public abstract ResultSet getEedParams();

  public abstract int getTranState();

  public abstract int getStatus();
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.EedInfo
 * JD-Core Version:    0.5.4
 */