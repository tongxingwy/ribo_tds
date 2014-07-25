package com.sybase.ribo;

import java.util.EventListener;
import java.util.EventObject;

public abstract interface HeaderListener extends EventListener
{
  public abstract void newHeader(EventObject paramEventObject);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.HeaderListener
 * JD-Core Version:    0.5.4
 */