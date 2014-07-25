package com.sybase.jdbcx;

import java.sql.ResultSet;

public abstract interface SybEventHandler
{
  public static final int NOTIFY_ALWAYS = 4;
  public static final int NOTIFY_ONCE = 2;

  public abstract void event(String paramString, ResultSet paramResultSet);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybEventHandler
 * JD-Core Version:    0.5.4
 */