package com.sybase.jdbcx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public abstract interface SybConnection extends Connection
{
  public abstract void cancel()
    throws SQLException;

  public abstract Capture createCapture()
    throws SQLException;

  public abstract String getSessionID()
    throws SQLException;

  public abstract PreparedStatement prepareStatement(String paramString, boolean paramBoolean)
    throws SQLException;

  public abstract void regNoWatch(String paramString)
    throws SQLException;

  public abstract void regWatch(String paramString, SybEventHandler paramSybEventHandler, int paramInt)
    throws SQLException;

  public abstract void setSybMessageHandler(SybMessageHandler paramSybMessageHandler);

  public abstract SybMessageHandler getSybMessageHandler();

  public abstract Properties getClientInfo()
    throws SQLException;

  public abstract SybPreparedStatement copyPreparedStatement(SybPreparedStatement paramSybPreparedStatement)
    throws SQLException;

  public abstract SybCallableStatement copyCallableStatement(SybCallableStatement paramSybCallableStatement)
    throws SQLException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybConnection
 * JD-Core Version:    0.5.4
 */