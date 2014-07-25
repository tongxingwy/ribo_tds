package com.sybase.jdbcx;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public abstract interface SybCursorResultSet extends SybResultSet
{
  public abstract String getCursorName()
    throws SQLException;

  public abstract boolean isLanguageCursor();

  public abstract void setFetchSize(int paramInt)
    throws SQLException;

  public abstract int getFetchSize()
    throws SQLException;

  public abstract int getType()
    throws SQLException;

  public abstract int getConcurrency()
    throws SQLException;

  public abstract boolean rowUpdated()
    throws SQLException;

  public abstract boolean rowDeleted()
    throws SQLException;

  public abstract void updateNull(int paramInt)
    throws SQLException;

  public abstract void updateBoolean(int paramInt, boolean paramBoolean)
    throws SQLException;

  public abstract void updateByte(int paramInt, byte paramByte)
    throws SQLException;

  public abstract void updateShort(int paramInt, short paramShort)
    throws SQLException;

  public abstract void updateInt(int paramInt1, int paramInt2)
    throws SQLException;

  public abstract void updateLong(int paramInt, long paramLong)
    throws SQLException;

  public abstract void updateFloat(int paramInt, float paramFloat)
    throws SQLException;

  public abstract void updateDouble(int paramInt, double paramDouble)
    throws SQLException;

  public abstract void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException;

  public abstract void updateString(int paramInt, String paramString)
    throws SQLException;

  public abstract void updateBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException;

  public abstract void updateDate(int paramInt, Date paramDate)
    throws SQLException;

  public abstract void updateTime(int paramInt, Time paramTime)
    throws SQLException;

  public abstract void updateTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException;

  public abstract void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException;

  public abstract void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException;

  public abstract void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException;

  public abstract void updateObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException;

  public abstract void updateObject(int paramInt, Object paramObject)
    throws SQLException;

  public abstract void updateNull(String paramString)
    throws SQLException;

  public abstract void updateBoolean(String paramString, boolean paramBoolean)
    throws SQLException;

  public abstract void updateByte(String paramString, byte paramByte)
    throws SQLException;

  public abstract void updateShort(String paramString, short paramShort)
    throws SQLException;

  public abstract void updateInt(String paramString, int paramInt)
    throws SQLException;

  public abstract void updateLong(String paramString, long paramLong)
    throws SQLException;

  public abstract void updateFloat(String paramString, float paramFloat)
    throws SQLException;

  public abstract void updateDouble(String paramString, double paramDouble)
    throws SQLException;

  public abstract void updateBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException;

  public abstract void updateString(String paramString1, String paramString2)
    throws SQLException;

  public abstract void updateBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException;

  public abstract void updateDate(String paramString, Date paramDate)
    throws SQLException;

  public abstract void updateTime(String paramString, Time paramTime)
    throws SQLException;

  public abstract void updateTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException;

  public abstract void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException;

  public abstract void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException;

  public abstract void updateCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException;

  public abstract void updateObject(String paramString, Object paramObject, int paramInt)
    throws SQLException;

  public abstract void updateObject(String paramString, Object paramObject)
    throws SQLException;

  public abstract void cancelRowUpdates()
    throws SQLException;

  public abstract void deleteRow()
    throws SQLException;

  public abstract void deleteRow(String paramString)
    throws SQLException;

  public abstract void updateRow()
    throws SQLException;

  public abstract void updateRow(String paramString)
    throws SQLException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybCursorResultSet
 * JD-Core Version:    0.5.4
 */