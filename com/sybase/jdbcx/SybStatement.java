package com.sybase.jdbcx;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract interface SybStatement extends Statement
{
  public abstract Connection getConnection()
    throws SQLException;

  public abstract int getFetchSize()
    throws SQLException;

  public abstract SybMessageHandler getSybMessageHandler();

  public abstract int getResultSetConcurrency()
    throws SQLException;

  public abstract void setCursorName(String paramString)
    throws SQLException;

  public abstract void setFetchSize(int paramInt)
    throws SQLException;

  public abstract void setSybMessageHandler(SybMessageHandler paramSybMessageHandler);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybStatement
 * JD-Core Version:    0.5.4
 */