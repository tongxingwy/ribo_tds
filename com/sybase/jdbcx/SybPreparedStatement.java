package com.sybase.jdbcx;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract interface SybPreparedStatement extends PreparedStatement, SybStatement
{
  public abstract void setBigDecimal(int paramInt1, BigDecimal paramBigDecimal, int paramInt2, int paramInt3)
    throws SQLException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybPreparedStatement
 * JD-Core Version:    0.5.4
 */