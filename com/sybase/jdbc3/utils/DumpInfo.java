package com.sybase.jdbc3.utils;

import java.io.IOException;

public abstract interface DumpInfo
{
  public abstract void addInfo(String paramString, int paramInt, Object paramObject);

  public abstract void addInfo(DumpInfo paramDumpInfo);

  public abstract void addValue(String paramString, int paramInt, Object paramObject)
    throws IOException;

  public abstract void addValue(String paramString1, int paramInt, Object paramObject, String paramString2)
    throws IOException;

  public abstract void addInt(String paramString, int paramInt, long paramLong);

  public abstract void addBitfield(String paramString, int paramInt1, int paramInt2, String[] paramArrayOfString);

  public abstract void addField(String paramString, int paramInt1, int paramInt2, String[] paramArrayOfString);

  public abstract void addBitfield(String paramString, int paramInt, byte[] paramArrayOfByte, String[] paramArrayOfString);

  public abstract void addText(String paramString1, int paramInt, String paramString2);

  public abstract void addHex(String paramString, int paramInt, long paramLong);

  public abstract void addHex(String paramString, int paramInt1, int paramInt2);

  public abstract void addHex(String paramString, int paramInt, byte[] paramArrayOfByte);

  public abstract String fieldToString(int paramInt1, int paramInt2, String[] paramArrayOfString);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.DumpInfo
 * JD-Core Version:    0.5.4
 */