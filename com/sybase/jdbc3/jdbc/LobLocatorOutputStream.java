/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class LobLocatorOutputStream extends LobOutputStream
/*    */ {
/*    */   public LobLocatorOutputStream(SybLob paramSybLob, long paramLong)
/*    */   {
/* 25 */     super(paramSybLob, paramLong);
/*    */   }
/*    */ 
/*    */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*    */   {
/* 30 */     checkIfClosed();
/* 31 */     if (paramArrayOfByte == null)
/*    */     {
/* 33 */       throw new NullPointerException();
/*    */     }
/* 35 */     if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0))
/*    */     {
/* 38 */       throw new IndexOutOfBoundsException();
/*    */     }
/* 40 */     if (paramInt2 == 0)
/*    */     {
/* 42 */       return;
/*    */     }
/*    */ 
/*    */     try
/*    */     {
/* 47 */       this._lob.checkLocatorValidity();
/*    */ 
/* 49 */       int i = 0;
/* 50 */       while (paramInt2 > 0)
/*    */       {
/* 52 */         int j = (paramInt2 > 16384) ? 16384 : paramInt2;
/*    */ 
/* 54 */         byte[] arrayOfByte = new byte[j];
/* 55 */         System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, j);
/* 56 */         i = 0;
/* 57 */         if (SybBinaryLob.class.isInstance(this._lob))
/*    */         {
/* 59 */           i = ((SybBinaryLob)this._lob).setBytes(this._nextWritePos, arrayOfByte);
/*    */         }
/*    */         else
/*    */         {
/* 64 */           i = this._lob.setData(this._nextWritePos, new String(arrayOfByte));
/*    */         }
/*    */ 
/* 68 */         this._nextWritePos += i;
/* 69 */         paramInt1 += i;
/* 70 */         paramInt2 -= i;
/*    */       }
/*    */     }
/*    */     catch (SQLException localSQLException)
/*    */     {
/* 75 */       ErrorMessage.raiseIOException("JZ041", "Write", "OuputStream", localSQLException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.LobLocatorOutputStream
 * JD-Core Version:    0.5.4
 */