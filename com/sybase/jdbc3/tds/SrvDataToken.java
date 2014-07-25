package com.sybase.jdbc3.tds;

public abstract interface SrvDataToken
{
  public abstract TdsInputStream getStream();

  public abstract void setFormatter(SrvTypeFormatter paramSrvTypeFormatter);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDataToken
 * JD-Core Version:    0.5.4
 */