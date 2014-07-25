package com.sybase.ribo;

import java.util.EventListener;

public abstract interface InfoListener extends EventListener
{
  public abstract void newInfo(InfoEvent paramInfoEvent);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.InfoListener
 * JD-Core Version:    0.5.4
 */