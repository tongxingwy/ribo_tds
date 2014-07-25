package com.sybase.jdbcx;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;

public abstract interface CharsetConverter
{
  public abstract void setEncoding(String paramString)
    throws UnsupportedEncodingException;

  public abstract byte[] fromUnicode(String paramString)
    throws CharConversionException;

  public abstract String toUnicode(byte[] paramArrayOfByte)
    throws CharConversionException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.CharsetConverter
 * JD-Core Version:    0.5.4
 */