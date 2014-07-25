package com.sybase.jdbc3.jdbc;

import java.sql.SQLException;

public abstract interface Cursor
{
  public static final int NEW = 0;
  public static final int OPEN = 1;
  public static final int CLOSED = 2;
  public static final int DEALLOCATED = 3;
  public static final int BEFORE_FIRST = 0;
  public static final int UNKNOWN = -1;
  public static final int AFTER_LAST = -2;

  public abstract String getName();

  public abstract void setName(String paramString)
    throws SQLException;

  public abstract int getConcurrency()
    throws SQLException;

  public abstract void setTypeAndConcurrency(int paramInt1, int paramInt2)
    throws SQLException;

  public abstract boolean scrollingAtServer();

  public abstract void setFetchSize(int paramInt)
    throws SQLException;

  public abstract int getFetchSize();

  public abstract ProtocolResultSet open(String paramString, ParamManager paramParamManager, boolean paramBoolean)
    throws SQLException;

  public abstract ProtocolResultSet fetch()
    throws SQLException;

  public abstract int delete(ProtocolResultSet paramProtocolResultSet)
    throws SQLException;

  public abstract void setTable(String paramString);

  public abstract String getTable();

  public abstract int getTotalRowCount();

  public abstract boolean isLanguageCursor();

  public abstract int update(ProtocolResultSet paramProtocolResultSet, ParamManager paramParamManager, String paramString)
    throws SQLException;

  public abstract int insert(ProtocolResultSet paramProtocolResultSet, ParamManager paramParamManager, String paramString)
    throws SQLException;

  public abstract void close(boolean paramBoolean)
    throws SQLException;

  public abstract void setDynamic(boolean paramBoolean);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.Cursor
 * JD-Core Version:    0.5.4
 */