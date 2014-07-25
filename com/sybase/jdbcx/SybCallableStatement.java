package com.sybase.jdbcx;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.SQLException;

public abstract interface SybCallableStatement extends CallableStatement, SybPreparedStatement
{
  public abstract void setParameterName(int paramInt, String paramString);

  public abstract BigDecimal getBigDecimal(int paramInt)
    throws SQLException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybCallableStatement
 * JD-Core Version:    0.5.4
 */