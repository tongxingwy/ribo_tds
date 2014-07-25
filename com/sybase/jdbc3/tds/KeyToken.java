/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class KeyToken extends Token
/*    */ {
/*    */   private TdsResultSet _trs;
/*    */ 
/*    */   public KeyToken(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public KeyToken(TdsResultSet paramTdsResultSet)
/*    */   {
/* 46 */     this._trs = paramTdsResultSet;
/*    */   }
/*    */ 
/*    */   public void send(TdsDataOutputStream paramTdsDataOutputStream)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 57 */       RowFormatToken localRowFormatToken = null;
/* 58 */       paramTdsDataOutputStream.writeByte(202);
/*    */       try
/*    */       {
/* 61 */         localRowFormatToken = (RowFormatToken)this._trs.getMetaData();
/*    */       }
/*    */       catch (SQLException localSQLException1)
/*    */       {
/*    */       }
/*    */ 
/* 68 */       int i = 0;
/* 69 */       for (int j = 0; j < localRowFormatToken._numColumns; ++j)
/*    */       {
/*    */         try
/*    */         {
/* 73 */           i = localRowFormatToken.getStatus(j);
/*    */         }
/*    */         catch (SQLException localSQLException2)
/*    */         {
/*    */         }
/*    */ 
/* 80 */         if ((i & 0x6) == 0) {
/*    */           continue;
/*    */         }
/*    */ 
/*    */         try
/*    */         {
/* 86 */           paramTdsDataOutputStream.send(this._trs._columns[j]);
/*    */         }
/*    */         catch (SQLException localSQLException3)
/*    */         {
/* 90 */           ErrorMessage.raiseIOECheckDead(localSQLException3);
/*    */         }
/*    */       }
/*    */ 
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 97 */       writeSQE(localIOException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.KeyToken
 * JD-Core Version:    0.5.4
 */