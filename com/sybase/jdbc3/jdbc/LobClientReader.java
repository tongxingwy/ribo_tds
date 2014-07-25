/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class LobClientReader extends LobReader
/*    */ {
/*    */   public LobClientReader(SybCharClientLob paramSybCharClientLob, long paramLong1, long paramLong2)
/*    */   {
/* 24 */     super(paramSybCharClientLob, paramLong1, paramLong2);
/*    */   }
/*    */ 
/*    */   public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException
/*    */   {
/* 29 */     checkIfClosed();
/* 30 */     if (paramArrayOfChar == null)
/*    */     {
/* 32 */       throw new NullPointerException();
/*    */     }
/* 34 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfChar.length - paramInt1))
/*    */     {
/* 36 */       throw new IndexOutOfBoundsException();
/*    */     }
/* 38 */     if (paramInt2 == 0)
/*    */     {
/* 40 */       return -1;
/*    */     }
/*    */ 
/* 43 */     if (this._nextReadPos > this._readLimit)
/*    */     {
/* 45 */       return -1;
/*    */     }
/*    */ 
/* 48 */     if (this._nextReadPos + paramInt2 > this._readLimit)
/*    */     {
/* 52 */       paramInt2 = (int)(this._readLimit - this._nextReadPos + 1L);
/*    */     }
/*    */ 
/* 55 */     StringBuffer localStringBuffer = ((SybCharClientLob)this._lob).getDataRef();
/* 56 */     if (localStringBuffer != null)
/*    */     {
/* 58 */       if (paramInt2 + paramInt1 + this._nextReadPos - 1L > localStringBuffer.length())
/*    */       {
/* 60 */         return -1;
/*    */       }
/* 62 */       localStringBuffer.getChars((int)this._nextReadPos - 1, (int)this._nextReadPos + paramInt2 - 1, paramArrayOfChar, paramInt1);
/*    */     }
/*    */ 
/* 65 */     this._nextReadPos += paramInt2;
/* 66 */     return paramInt2;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.LobClientReader
 * JD-Core Version:    0.5.4
 */