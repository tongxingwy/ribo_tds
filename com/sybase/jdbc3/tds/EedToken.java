/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.jdbc.SybResultSet;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class EedToken extends Token
/*    */ {
/*    */   protected int _msgNumber;
/*    */   protected int _state;
/*    */   protected int _class;
/*    */   protected String _sqlState;
/*    */   protected int _status;
/*    */   protected int _tranState;
/*    */   protected String _msg;
/*    */   protected String _serverName;
/*    */   protected String _procName;
/*    */   protected int _lineNum;
/*    */   protected SybResultSet _params;
/*    */ 
/*    */   protected EedToken()
/*    */   {
/*    */   }
/*    */ 
/*    */   public EedToken(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 57 */       int i = paramTdsInputStream.readShort();
/* 58 */       this._msgNumber = paramTdsInputStream.readInt();
/* 59 */       this._state = paramTdsInputStream.readUnsignedByte();
/* 60 */       this._class = paramTdsInputStream.readUnsignedByte();
/* 61 */       int j = paramTdsInputStream.readUnsignedByte();
/* 62 */       this._sqlState = paramTdsInputStream.readString(j);
/* 63 */       this._status = paramTdsInputStream.readUnsignedByte();
/* 64 */       this._tranState = paramTdsInputStream.readShort();
/* 65 */       int k = paramTdsInputStream.readShort();
/* 66 */       this._msg = paramTdsInputStream.readString(k);
/* 67 */       int l = paramTdsInputStream.readUnsignedByte();
/* 68 */       this._serverName = paramTdsInputStream.readString(l);
/* 69 */       int i1 = paramTdsInputStream.readUnsignedByte();
/* 70 */       this._procName = paramTdsInputStream.readString(i1);
/* 71 */       this._lineNum = paramTdsInputStream.readShort();
/* 72 */       this._params = null;
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 77 */       readSQE(localIOException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.EedToken
 * JD-Core Version:    0.5.4
 */