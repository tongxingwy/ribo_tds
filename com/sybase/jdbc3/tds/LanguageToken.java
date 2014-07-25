/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class LanguageToken extends Token
/*    */ {
/*    */   protected String _query;
/*    */   protected int _status;
/*    */ 
/*    */   protected LanguageToken()
/*    */   {
/*    */   }
/*    */ 
/*    */   public LanguageToken(String paramString, boolean paramBoolean1, boolean paramBoolean2)
/*    */   {
/* 49 */     this._query = paramString;
/* 50 */     this._status = 0;
/* 51 */     if (!paramBoolean1)
/*    */       return;
/* 53 */     this._status |= 1;
/* 54 */     if (!paramBoolean2)
/*    */       return;
/* 56 */     this._status |= 4;
/*    */   }
/*    */ 
/*    */   public void send(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 70 */       paramTdsOutputStream.writeByte(33);
/* 71 */       byte[] arrayOfByte = paramTdsOutputStream.stringToByte(this._query);
/*    */ 
/* 73 */       paramTdsOutputStream.writeInt(arrayOfByte.length + 1);
/* 74 */       paramTdsOutputStream.writeByte(this._status);
/* 75 */       paramTdsOutputStream.write(arrayOfByte);
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 79 */       writeSQE(localIOException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.LanguageToken
 * JD-Core Version:    0.5.4
 */