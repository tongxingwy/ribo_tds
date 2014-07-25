/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class DoneToken extends Token
/*    */ {
/*    */   protected static final int DONE_TOKEN_SIZE = 9;
/*    */   protected static final int DONE_STATUS_OFFSET = 2;
/*    */   protected int _status;
/*    */   protected int _tranState;
/*    */   protected int _count;
/*    */ 
/*    */   public DoneToken(int paramInt1, int paramInt2, int paramInt3)
/*    */   {
/* 49 */     this._status = paramInt1;
/* 50 */     this._tranState = paramInt2;
/* 51 */     this._count = paramInt3;
/*    */   }
/*    */ 
/*    */   public DoneToken(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 64 */       this._status = paramTdsInputStream.readUnsignedShort();
/* 65 */       this._tranState = paramTdsInputStream.readUnsignedShort();
/* 66 */       this._count = paramTdsInputStream.readInt();
/*    */     }
/*    */     catch (IOException localIOException)
/*    */     {
/* 72 */       readSQE(localIOException);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected static int getStatusOffset(boolean paramBoolean)
/*    */   {
/* 83 */     return (paramBoolean) ? 2 : 1;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.DoneToken
 * JD-Core Version:    0.5.4
 */