package com.sybase.jdbc3.tds;

import java.io.IOException;

public abstract interface SrvFormatToken
{
  public abstract void addFormat(DataFormat paramDataFormat);

  public abstract int getFormatCount();

  public abstract DataFormat formatAt(int paramInt);

  public abstract void sendFormat(TdsOutputStream paramTdsOutputStream)
    throws IOException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvFormatToken
 * JD-Core Version:    0.5.4
 */