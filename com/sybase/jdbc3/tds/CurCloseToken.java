/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class CurCloseToken extends Token
/*    */ {
/*    */   private TdsCursor _cursor;
/*    */   private int _dealloc;
/*    */ 
/*    */   protected CurCloseToken()
/*    */   {
/*    */   }
/*    */ 
/*    */   public CurCloseToken(TdsCursor paramTdsCursor, boolean paramBoolean)
/*    */   {
/* 45 */     this._cursor = paramTdsCursor;
/* 46 */     this._dealloc = ((paramBoolean) ? 1 : 0);
/*    */   }
/*    */ 
/*    */   public void send(TdsDataOutputStream paramTdsDataOutputStream)
/*    */     throws IOException
/*    */   {
/* 56 */     int i = 5;
/* 57 */     byte[] arrayOfByte = null;
/* 58 */     int j = 0;
/*    */ 
/* 60 */     if (this._cursor._id == 0)
/*    */     {
/* 62 */       arrayOfByte = paramTdsDataOutputStream.stringToByte(this._cursor.getName());
/* 63 */       j = arrayOfByte.length;
/* 64 */       i += 1 + j;
/*    */     }
/*    */     try
/*    */     {
/* 68 */       paramTdsDataOutputStream.writeByte(128);
/* 69 */       paramTdsDataOutputStream.writeShort(i);
/* 70 */       paramTdsDataOutputStream.writeInt(this._cursor._id);
/* 71 */       if (this._cursor._id == 0)
/*    */       {
/* 73 */         paramTdsDataOutputStream.writeByte(j);
/* 74 */         paramTdsDataOutputStream.write(arrayOfByte);
/*    */       }
/*    */ 
/* 77 */       paramTdsDataOutputStream.writeByte(this._dealloc);
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 81 */       writeSQE(localIOException);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 88 */     StringBuffer localStringBuffer = new StringBuffer("CurOpenToken: ");
/* 89 */     localStringBuffer.append("name= " + this._cursor.getName());
/* 90 */     localStringBuffer.append(", option = " + this._dealloc);
/* 91 */     return localStringBuffer.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CurCloseToken
 * JD-Core Version:    0.5.4
 */