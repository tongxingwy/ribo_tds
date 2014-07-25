/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*    */ import java.io.DataOutput;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class BlobOutputStream extends OutputStream
/*    */ {
/*    */   protected static final int HIBIT = -2147483648;
/*    */   private DataOutput _dout;
/*    */ 
/*    */   public BlobOutputStream(DataOutput paramDataOutput)
/*    */     throws IOException
/*    */   {
/* 37 */     this._dout = paramDataOutput;
/*    */   }
/*    */ 
/*    */   public void write(int paramInt)
/*    */     throws IOException
/*    */   {
/* 43 */     this._dout.writeInt(-2147483647);
/* 44 */     this._dout.write(paramInt);
/*    */   }
/*    */ 
/*    */   public void write(byte[] paramArrayOfByte) throws IOException
/*    */   {
/* 49 */     write(paramArrayOfByte, 0, paramArrayOfByte.length);
/*    */   }
/*    */ 
/*    */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*    */   {
/* 54 */     if (paramInt2 == 0)
/*    */     {
/* 65 */       return;
/*    */     }
/* 67 */     if ((paramInt2 & 0x80000000) != 0)
/*    */     {
/* 69 */       ErrorMessage.raiseIOException("JZ0IA");
/*    */     }
/* 71 */     this._dout.writeInt(paramInt2 | 0x80000000);
/* 72 */     this._dout.write(paramArrayOfByte, paramInt1, paramInt2);
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 82 */     this._dout.writeInt(0);
/* 83 */     this._dout = null;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.BlobOutputStream
 * JD-Core Version:    0.5.4
 */