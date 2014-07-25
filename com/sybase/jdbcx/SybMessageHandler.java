package com.sybase.jdbcx;

import java.sql.SQLException;

public abstract interface SybMessageHandler
{
  public abstract SQLException messageHandler(SQLException paramSQLException);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybMessageHandler
 * JD-Core Version:    0.5.4
 */