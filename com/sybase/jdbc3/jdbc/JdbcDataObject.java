package com.sybase.jdbc3.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Calendar;

public abstract interface JdbcDataObject
{
  public abstract boolean isNull()
    throws SQLException;

  public abstract InputStream getAsciiStream()
    throws SQLException;

  public abstract Reader getCharacterStream()
    throws SQLException;

  public abstract BigDecimal getBigDecimal(int paramInt)
    throws SQLException;

  public abstract InputStream getBinaryStream()
    throws SQLException;

  public abstract boolean getBoolean()
    throws SQLException;

  public abstract byte getByte()
    throws SQLException;

  public abstract byte[] getBytes()
    throws SQLException;

  public abstract TextPointer getTextPtr()
    throws SQLException;

  public abstract DateObject getDateObject(int paramInt, Calendar paramCalendar)
    throws SQLException;

  public abstract double getDouble()
    throws SQLException;

  public abstract float getFloat()
    throws SQLException;

  public abstract int getInt()
    throws SQLException;

  public abstract long getLong()
    throws SQLException;

  public abstract Object getObject()
    throws SQLException;

  public abstract short getShort()
    throws SQLException;

  public abstract String getString()
    throws SQLException;

  public abstract InputStream getUnicodeStream()
    throws SQLException;

  public abstract Blob getBlob()
    throws SQLException;

  public abstract Clob getClob()
    throws SQLException;

  public abstract Clob getInitializedClob()
    throws SQLException;

  public abstract Blob getInitializedBlob()
    throws SQLException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.JdbcDataObject
 * JD-Core Version:    0.5.4
 */