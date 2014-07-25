/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class SybConnectionDeadException extends IOException
/*    */ {
/* 36 */   private IOException _origIOException = null;
/*    */ 
/*    */   public SybConnectionDeadException(IOException paramIOException)
/*    */   {
/* 41 */     super(paramIOException.getMessage());
/* 42 */     this._origIOException = paramIOException;
/*    */   }
/*    */ 
/*    */   public IOException getOriginalIOException()
/*    */   {
/* 48 */     return this._origIOException;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybConnectionDeadException
 * JD-Core Version:    0.5.4
 */