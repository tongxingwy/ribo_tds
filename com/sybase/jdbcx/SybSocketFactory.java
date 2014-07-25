package com.sybase.jdbcx;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public abstract interface SybSocketFactory
{
  public abstract Socket createSocket(String paramString, int paramInt, Properties paramProperties)
    throws IOException, UnknownHostException;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbcx.SybSocketFactory
 * JD-Core Version:    0.5.4
 */