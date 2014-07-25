/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class Token
/*    */ {
/*    */   protected static final void readSQE(IOException paramIOException)
/*    */     throws IOException
/*    */   {
/* 52 */     throw paramIOException;
/*    */   }
/*    */ 
/*    */   protected static final void writeSQE(IOException paramIOException)
/*    */     throws IOException
/*    */   {
/* 61 */     throw paramIOException;
/*    */   }
/*    */ 
/*    */   protected void send(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.Token
 * JD-Core Version:    0.5.4
 */