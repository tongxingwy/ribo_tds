package com.sybase.jdbc3.utils;

import java.io.Serializable;

public abstract interface DumpFilter extends Serializable
{
  public static final long serialVersionUID = 18549804L;
  public static final int PDU_HEADERS = 256;
  public static final int TDS_STREAM_HEADER = 257;
  public static final int LOGIN = 258;
  public static final int UNFORMATTED_PDUS = 259;
  public static final int TOKEN_DETAILS = 0;
  public static final int LENGTH_DETAILS = 1;
  public static final int SQL_TEXT_DETAILS = 2;
  public static final int DATA_DETAILS = 3;
  public static final int VERBOSE_CAP_DETAILS = 4;
  public static final int PASSWORD_DETAILS = 5;
  public static final int ROW_DETAILS = 6;
  public static final int FORMAT_DETAILS = 7;
  public static final int RPC_DETAILS = 8;
  public static final int CURSOR_DETAILS = 9;
  public static final int EED_DETAILS = 10;
  public static final int ALL_DETAILS = 11;

  public abstract boolean includesToken(int paramInt);

  public abstract boolean includesDetail(int paramInt);

  public abstract DumpInfo getDumpInfo();

  public abstract String getFileName();

  public abstract void setFileName(String paramString);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.DumpFilter
 * JD-Core Version:    0.5.4
 */