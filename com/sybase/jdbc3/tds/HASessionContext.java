/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ParamManager;
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class HASessionContext
/*     */ {
/*  66 */   private byte[] _haSessionID = new byte[6];
/*     */ 
/*  78 */   private int _haLogin = 0;
/*     */ 
/*  81 */   private boolean _haRequested = false;
/*     */ 
/*  83 */   private boolean _supportsRedirect = false;
/*     */ 
/*  86 */   private boolean _failover = false;
/*     */ 
/*  89 */   private Tds _protocol = null;
/*     */ 
/*     */   protected HASessionContext(SybProperty paramSybProperty, Tds paramTds)
/*     */     throws SQLException
/*     */   {
/* 102 */     this._haRequested = ((paramSybProperty.getBoolean(34)) || (paramSybProperty.getBoolean(72)));
/*     */ 
/* 104 */     this._supportsRedirect = paramSybProperty.getBoolean(71);
/* 105 */     this._protocol = paramTds;
/* 106 */     this._failover = false;
/*     */ 
/* 109 */     if (this._haRequested)
/*     */     {
/* 112 */       this._haLogin |= 1;
/*     */     }
/* 114 */     if (!this._supportsRedirect)
/*     */       return;
/* 116 */     this._haLogin |= 8;
/*     */   }
/*     */ 
/*     */   public boolean wasHARequested()
/*     */   {
/* 126 */     return this._haRequested;
/*     */   }
/*     */ 
/*     */   public boolean isInFailoverMode()
/*     */   {
/* 136 */     return this._failover;
/*     */   }
/*     */ 
/*     */   public byte[] getSessionID()
/*     */   {
/* 147 */     return this._haSessionID;
/*     */   }
/*     */ 
/*     */   public int getLogin()
/*     */   {
/* 159 */     return this._haLogin;
/*     */   }
/*     */ 
/*     */   protected boolean isMigrating()
/*     */   {
/* 168 */     return (this._haLogin & 0x10) != 0;
/*     */   }
/*     */ 
/*     */   protected void setFailoverMode(boolean paramBoolean)
/*     */   {
/* 178 */     this._failover = paramBoolean;
/*     */   }
/*     */ 
/*     */   protected void setHALogin(int paramInt)
/*     */   {
/* 188 */     this._haLogin = paramInt;
/*     */   }
/*     */ 
/*     */   protected void readSessionID(TdsProtocolContext paramTdsProtocolContext, MsgToken paramMsgToken)
/*     */     throws IOException, SQLException
/*     */   {
/* 218 */     int i = this._protocol.nextResult(paramTdsProtocolContext);
/*     */ 
/* 226 */     i = paramTdsProtocolContext._in.readUnsignedByte();
/*     */ 
/* 234 */     TdsJdbcInputStream localTdsJdbcInputStream = new TdsJdbcInputStream(null, paramTdsProtocolContext, this._protocol);
/* 235 */     localTdsJdbcInputStream._dataFmt = paramTdsProtocolContext._paramFmts.getDataFormat(0);
/*     */ 
/* 238 */     byte[] arrayOfByte = localTdsJdbcInputStream.getBytes();
/*     */ 
/* 245 */     if ((getLogin() != 7) || ((getLogin() == 1) && (!isInFailoverMode())))
/*     */     {
/* 254 */       System.arraycopy(arrayOfByte, 0, this._haSessionID, 0, 6);
/*     */     }
/*     */ 
/* 263 */     paramTdsProtocolContext._lastResult = -1;
/*     */ 
/* 266 */     i = this._protocol.nextResult(paramTdsProtocolContext);
/*     */   }
/*     */ 
/*     */   protected void acknowledgeSessionID(TdsDataOutputStream paramTdsDataOutputStream, TdsProtocolContext paramTdsProtocolContext)
/*     */     throws SQLException, IOException
/*     */   {
/* 294 */     MsgToken localMsgToken = new MsgToken(1, 12);
/*     */ 
/* 298 */     ParamManager localParamManager = new ParamManager(1, paramTdsProtocolContext);
/*     */ 
/* 302 */     localParamManager.setParam(1, -3, new byte[0], 0);
/*     */ 
/* 307 */     localMsgToken.send(paramTdsDataOutputStream);
/*     */ 
/* 310 */     this._protocol.sendParamStream(localParamManager, paramTdsDataOutputStream);
/*     */ 
/* 312 */     paramTdsDataOutputStream.flush();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.HASessionContext
 * JD-Core Version:    0.5.4
 */