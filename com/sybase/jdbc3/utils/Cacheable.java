package com.sybase.jdbc3.utils;

import java.io.IOException;
import java.io.InputStream;

public abstract interface Cacheable
{
  public static final int IDLE = 0;
  public static final int READING = 1;
  public static final int CACHING = 2;
  public static final int CACHED = 3;
  public static final int READASBYTES = 4;

  public abstract void open(boolean paramBoolean)
    throws IOException;

  public abstract void clear()
    throws IOException;

  public abstract void resetInputStream(InputStream paramInputStream);

  public abstract void setManager(CacheManager paramCacheManager);

  public abstract void reset()
    throws IOException;

  public abstract void cache()
    throws IOException;

  public abstract int getState();
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.Cacheable
 * JD-Core Version:    0.5.4
 */