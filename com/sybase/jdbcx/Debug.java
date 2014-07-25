package com.sybase.jdbcx;

import java.io.IOException;
import java.io.PrintStream;

public abstract interface Debug
{
  public abstract void debug(boolean paramBoolean, String paramString)
    throws IOException;

  public abstract void debug(boolean paramBoolean, String paramString, PrintStream paramPrintStream)
    throws IOException;

  public abstract void startTimer(Object paramObject);

  public abstract void stopTimer(Object paramObject, String paramString);

  public abstract void println(String paramString);

  public abstract void println(Object paramObject, String paramString);

  public abstract void asrt(Object paramObject, boolean paramBoolean, String paramString)
    throws RuntimeException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.Debug
 * JD-Core Version:    0.5.4
 */