package com.sybase.jdbcx;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface SybResultSet extends ResultSet
{
  public static final int TYPE_FORWARD_ONLY = 1003;
  public static final int CONCUR_UNKNOWN = -9;
  public static final int CONCUR_READ_ONLY = 1007;
  public static final int CONCUR_UPDATABLE = 1008;

  public abstract BigDecimal getBigDecimal(int paramInt)
    throws SQLException;

  public abstract BigDecimal getBigDecimal(String paramString)
    throws SQLException;

  public abstract void updateBigDecimal(int paramInt1, BigDecimal paramBigDecimal, int paramInt2, int paramInt3)
    throws SQLException;

  public abstract void updateBigDecimal(String paramString, BigDecimal paramBigDecimal, int paramInt1, int paramInt2)
    throws SQLException;

  public abstract TextPointer getSybTextPointer(int paramInt)
    throws SQLException;

  public abstract TextPointer getSybTextPointer(String paramString)
    throws SQLException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybResultSet
 * JD-Core Version:    0.5.4
 */