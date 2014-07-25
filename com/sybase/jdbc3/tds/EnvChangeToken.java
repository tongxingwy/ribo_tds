/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class EnvChangeToken extends Token
/*    */ {
/* 26 */   protected String _newValue = null;
/* 27 */   protected String _oldValue = null;
/* 28 */   protected int _envType = -1;
/*    */ 
/*    */   protected EnvChangeToken()
/*    */   {
/*    */   }
/*    */ 
/*    */   public EnvChangeToken(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 49 */       int i = paramTdsInputStream.readShort();
/* 50 */       while (i > 0)
/*    */       {
/* 53 */         this._envType = paramTdsInputStream.readUnsignedByte();
/*    */ 
/* 56 */         int j = paramTdsInputStream.readUnsignedByte();
/*    */ 
/* 59 */         String str = null;
/* 60 */         if (j > 0)
/*    */         {
/* 62 */           str = paramTdsInputStream.readString(j);
/*    */         }
/*    */ 
/* 66 */         this._newValue = str;
/*    */ 
/* 69 */         i -= 3 + j;
/*    */ 
/* 72 */         j = paramTdsInputStream.readUnsignedByte();
/*    */ 
/* 75 */         if (j > 0)
/*    */         {
/* 77 */           this._oldValue = paramTdsInputStream.readString(j);
/*    */         }
/*    */ 
/* 81 */         i -= 3 + j;
/*    */       }
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 86 */       readSQE(localIOException);
/*    */     }
/*    */   }
/*    */ 
/*    */   public int getEnvType()
/*    */   {
/* 92 */     return this._envType;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.EnvChangeToken
 * JD-Core Version:    0.5.4
 */