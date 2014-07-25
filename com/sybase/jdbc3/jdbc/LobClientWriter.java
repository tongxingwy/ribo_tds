/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class LobClientWriter extends LobWriter
/*    */ {
/*    */   public LobClientWriter(SybCharClientLob paramSybCharClientLob, long paramLong)
/*    */   {
/* 25 */     super(paramSybCharClientLob, paramLong);
/*    */   }
/*    */ 
/*    */   public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException
/*    */   {
/* 30 */     checkIfClosed();
/* 31 */     if (paramArrayOfChar == null)
/*    */     {
/* 33 */       throw new NullPointerException();
/*    */     }
/* 35 */     if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0))
/*    */     {
/* 38 */       throw new IndexOutOfBoundsException();
/*    */     }
/*    */ 
/* 41 */     if (paramInt2 == 0)
/*    */     {
/* 43 */       return;
/*    */     }
/*    */ 
/*    */     try
/*    */     {
/* 48 */       while (paramInt2 > 0)
/*    */       {
/* 50 */         int i = (paramInt2 > 16384) ? 16384 : paramInt2;
/*    */ 
/* 52 */         int j = ((SybCharClientLob)this._lob).setString(this._nextWritePos, new String(paramArrayOfChar, paramInt1, i));
/*    */ 
/* 54 */         this._nextWritePos += j;
/* 55 */         paramInt1 += j;
/* 56 */         paramInt2 -= j;
/*    */       }
/*    */     }
/*    */     catch (SQLException localSQLException)
/*    */     {
/* 61 */       ErrorMessage.raiseIOException("JZ041", "Write", "Writer", localSQLException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.LobClientWriter
 * JD-Core Version:    0.5.4
 */