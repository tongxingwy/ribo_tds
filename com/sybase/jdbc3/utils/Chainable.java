package com.sybase.jdbc3.utils;

public abstract interface Chainable
{
  public abstract void setNext(Chainable paramChainable);

  public abstract Chainable getNext();

  public abstract void setPrevious(Chainable paramChainable);

  public abstract Chainable getPrevious();
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.Chainable
 * JD-Core Version:    0.5.4
 */