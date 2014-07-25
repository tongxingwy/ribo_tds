/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class LobClientOutputStream extends LobOutputStream
/*    */ {
/*    */   public LobClientOutputStream(SybLob paramSybLob, long paramLong)
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
/* 47 */       while (paramInt2 > 0)
/*    */       {
/* 49 */         int i = (paramInt2 > 16384) ? 16384 : paramInt2;
/*    */ 
/* 51 */         byte[] arrayOfByte = new byte[i];
/* 52 */         System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, i);
/* 53 */         int j = 0;
/* 54 */         if (SybCharClientLob.class.isInstance(this._lob))
/*    */         {
/* 56 */           j = ((SybCharClientLob)this._lob).setString(this._nextWritePos, new String(arrayOfByte));
/*    */         }
/*    */         else
/*    */         {
/* 61 */           j = ((SybBinaryClientLob)this._lob).setBytes(this._nextWritePos, arrayOfByte);
/*    */         }
/*    */ 
/* 64 */         this._nextWritePos += j;
/* 65 */         paramInt1 += j;
/* 66 */         paramInt2 -= j;
/*    */       }
/*    */     }
/*    */     catch (SQLException localSQLException)
/*    */     {
/* 71 */       ErrorMessage.raiseIOException("JZ041", "Write", "OuputStream", localSQLException);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.LobClientOutputStream
 * JD-Core Version:    0.5.4
 */