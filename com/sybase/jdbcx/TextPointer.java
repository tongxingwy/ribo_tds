package com.sybase.jdbcx;

import java.io.InputStream;
import java.sql.SQLException;

public abstract interface TextPointer
{
  public abstract void sendData(InputStream paramInputStream, boolean paramBoolean)
    throws SQLException;

  public abstract void sendData(InputStream paramInputStream, int paramInt, boolean paramBoolean)
    throws SQLException;

  public abstract void sendData(InputStream paramInputStream, int paramInt1, int paramInt2, boolean paramBoolean)
    throws SQLException;

  public abstract void sendData(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws SQLException;

  public abstract void writePage(String paramString, int paramInt, byte[] paramArrayOfByte)
    throws SQLException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.TextPointer
 * JD-Core Version:    0.5.4
 */