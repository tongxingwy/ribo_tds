/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.DataInput;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class SrvBlobInputStream extends InputStream
/*    */ {
/*    */   private DataInput _din;
/* 32 */   private int _available = 0;
/* 33 */   private boolean _lastChunk = false;
/*    */ 
/*    */   public SrvBlobInputStream(DataInput paramDataInput)
/*    */     throws IOException
/*    */   {
/* 42 */     this._din = paramDataInput;
/*    */   }
/*    */ 
/*    */   public int read()
/*    */     throws IOException
/*    */   {
/* 53 */     while ((!this._lastChunk) && (this._available == 0))
/*    */     {
/* 55 */       int i = this._din.readInt();
/* 56 */       this._lastChunk = ((i & 0x80000000) == 0);
/* 57 */       this._available = (i & 0x7FFFFFFF);
/*    */     }
/*    */ 
/* 61 */     if (this._available > 0)
/*    */     {
/* 63 */       this._available -= 1;
/* 64 */       return this._din.readUnsignedByte();
/*    */     }
/* 66 */     return -1;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 77 */     if (read() == -1);
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvBlobInputStream
 * JD-Core Version:    0.5.4
 */