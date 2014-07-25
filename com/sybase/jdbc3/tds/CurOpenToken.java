/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class CurOpenToken extends Token
/*    */ {
/*    */   private TdsCursor _cursor;
/*    */ 
/*    */   protected CurOpenToken()
/*    */   {
/*    */   }
/*    */ 
/*    */   public CurOpenToken(TdsCursor paramTdsCursor)
/*    */   {
/* 45 */     this._cursor = paramTdsCursor;
/*    */   }
/*    */ 
/*    */   public void send(TdsDataOutputStream paramTdsDataOutputStream)
/*    */     throws IOException
/*    */   {
/* 55 */     int i = 5;
/* 56 */     byte[] arrayOfByte = null;
/* 57 */     int j = 0;
/*    */ 
/* 59 */     if (this._cursor._id == 0)
/*    */     {
/* 61 */       arrayOfByte = paramTdsDataOutputStream.stringToByte(this._cursor.getName());
/* 62 */       j = arrayOfByte.length;
/* 63 */       i += 1 + j;
/*    */     }
/*    */     try
/*    */     {
/* 67 */       paramTdsDataOutputStream.writeByte(132);
/* 68 */       paramTdsDataOutputStream.writeShort(i);
/* 69 */       paramTdsDataOutputStream.writeInt(this._cursor._id);
/* 70 */       if (this._cursor._id == 0)
/*    */       {
/* 72 */         paramTdsDataOutputStream.writeByte(j);
/* 73 */         paramTdsDataOutputStream.write(arrayOfByte);
/*    */       }
/* 75 */       paramTdsDataOutputStream.writeByte(this._cursor._hasArgs);
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 79 */       writeSQE(localIOException);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 86 */     StringBuffer localStringBuffer = new StringBuffer("CurOpenToken: ");
/* 87 */     localStringBuffer.append("name= " + this._cursor.getName());
/* 88 */     localStringBuffer.append(", status= " + this._cursor._hasArgs);
/* 89 */     return localStringBuffer.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CurOpenToken
 * JD-Core Version:    0.5.4
 */