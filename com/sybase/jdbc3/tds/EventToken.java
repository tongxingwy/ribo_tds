/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class EventToken extends Token
/*    */ {
/*    */   protected String _name;
/*    */ 
/*    */   public EventToken(String paramString)
/*    */   {
/* 40 */     this._name = paramString;
/*    */   }
/*    */ 
/*    */   public EventToken(TdsDataInputStream paramTdsDataInputStream)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 53 */       int i = paramTdsDataInputStream.readUnsignedShort();
/* 54 */       i = paramTdsDataInputStream.readUnsignedByte();
/* 55 */       this._name = paramTdsDataInputStream.readString(i);
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 61 */       readSQE(localIOException);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected EventToken()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.EventToken
 * JD-Core Version:    0.5.4
 */