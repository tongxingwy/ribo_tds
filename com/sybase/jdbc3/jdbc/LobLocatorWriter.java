/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class LobLocatorWriter extends LobWriter
/*    */ {
/*    */   public LobLocatorWriter(SybCharLob paramSybCharLob, long paramLong)
/*    */   {
/* 25 */     super(paramSybCharLob, paramLong);
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
/* 48 */       this._lob.checkLocatorValidity();
/* 49 */       int i = 0;
/* 50 */       while (paramInt2 > 0)
/*    */       {
/* 52 */         int j = (paramInt2 > 16384) ? 16384 : paramInt2;
/*    */ 
/* 54 */         i = 0;
/*    */ 
/* 56 */         if (SybBinaryLob.class.isInstance(this._lob))
/*    */         {
/* 58 */           i = ((SybBinaryLob)this._lob).setBytes(this._nextWritePos, new String(paramArrayOfChar, paramInt1, j).getBytes());
/*    */         }
/*    */         else
/*    */         {
/* 65 */           i = this._lob.setData(this._nextWritePos, new String(paramArrayOfChar, paramInt1, j));
/*    */         }
/*    */ 
/* 68 */         this._nextWritePos += i;
/* 69 */         paramInt1 += i;
/* 70 */         paramInt2 -= i;
/*    */       }
/*    */     }
/*    */     catch (SQLException localSQLException)
/*    */     {
/* 75 */       ErrorMessage.raiseIOException("JZ041", "Write", "Writer", localSQLException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.LobLocatorWriter
 * JD-Core Version:    0.5.4
 */