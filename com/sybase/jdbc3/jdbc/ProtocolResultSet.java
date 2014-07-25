package com.sybase.jdbc3.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;

public abstract interface ProtocolResultSet
{
  public abstract boolean absolute(int paramInt)
    throws SQLException;

  public abstract boolean previous()
    throws SQLException;

  public abstract boolean relative(int paramInt)
    throws SQLException;

  public abstract boolean first()
    throws SQLException;

  public abstract boolean last()
    throws SQLException;

  public abstract boolean isBeforeFirst()
    throws SQLException;

  public abstract boolean isFirst()
    throws SQLException;

  public abstract boolean isLast()
    throws SQLException;

  public abstract boolean isAfterLast()
    throws SQLException;

  public abstract boolean isResultSetEmpty()
    throws SQLException;

  public abstract int getRowNumber()
    throws SQLException;

  public abstract void setType(int paramInt);

  public abstract int getType();

  public abstract int getNumRowsCached();

  public abstract boolean next()
    throws SQLException;

  public abstract ResultSetMetaData getMetaData()
    throws SQLException;

  public abstract JdbcDataObject getColumn(int paramInt)
    throws SQLException;

  public abstract int findColumn(String paramString)
    throws SQLException;

  public abstract int findColumnByLabel(String paramString)
    throws SQLException;

  public abstract void close(boolean paramBoolean)
    throws SQLException;

  public abstract int getCount()
    throws SQLException;

  public abstract SQLWarning getWarnings()
    throws SQLException;

  public abstract void clearWarnings()
    throws SQLException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.ProtocolResultSet
 * JD-Core Version:    0.5.4
 */