package com.sybase.jdbc3.tds;

import java.io.IOException;

public abstract interface SrvRequestListener
{
  public abstract void connect(SrvSession paramSrvSession, SrvLoginToken paramSrvLoginToken);

  public abstract void disconnect(SrvSession paramSrvSession, SrvLogoutToken paramSrvLogoutToken);

  public abstract void language(SrvSession paramSrvSession, SrvLanguageToken paramSrvLanguageToken);

  public abstract void rpc(SrvSession paramSrvSession, SrvDbrpcToken paramSrvDbrpcToken);

  public abstract void declareCursor(SrvSession paramSrvSession, SrvCurDeclareToken paramSrvCurDeclareToken, boolean paramBoolean);

  public abstract void processCurInfo(SrvSession paramSrvSession, SrvCurInfoToken paramSrvCurInfoToken, boolean paramBoolean);

  public abstract void openCursor(SrvSession paramSrvSession, SrvCurOpenToken paramSrvCurOpenToken, boolean paramBoolean);

  public abstract void closeCursor(SrvSession paramSrvSession, SrvCurCloseToken paramSrvCurCloseToken);

  public abstract void cursorFetch(SrvSession paramSrvSession, SrvCurFetchToken paramSrvCurFetchToken);

  public abstract void cursorUpdate(SrvSession paramSrvSession, SrvCurUpdateToken paramSrvCurUpdateToken);

  public abstract void cursorDelete(SrvSession paramSrvSession, SrvCurDeleteToken paramSrvCurDeleteToken);

  public abstract void buildCursorResultSet(SrvSession paramSrvSession, SrvCursor paramSrvCursor, Object[] paramArrayOfObject);

  public abstract void dynamicRequest(SrvSession paramSrvSession, DynamicToken paramDynamicToken);

  public abstract void attention(SrvSession paramSrvSession);

  public abstract void bulk(SrvSession paramSrvSession, SrvDataInputStream paramSrvDataInputStream);

  public abstract void error(SrvSession paramSrvSession, IOException paramIOException);

  public abstract void passthroughLanguage(SrvSession paramSrvSession, SrvPassthroughLanguageToken paramSrvPassthroughLanguageToken);
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvRequestListener
 * JD-Core Version:    0.5.4
 */