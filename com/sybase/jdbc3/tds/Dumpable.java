package com.sybase.jdbc3.tds;

import com.sybase.jdbc3.utils.DumpFilter;
import com.sybase.jdbc3.utils.DumpInfo;
import java.io.IOException;

public abstract interface Dumpable
{
  public abstract DumpInfo dump(DumpFilter paramDumpFilter)
    throws IOException;

  public abstract int getTokenType();
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.Dumpable
 * JD-Core Version:    0.5.4
 */