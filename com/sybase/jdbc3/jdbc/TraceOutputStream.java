/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import com.sybase.jdbc3.utils.Debug;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class TraceOutputStream extends OutputStream
/*    */ {
/*    */   private OutputStream _out;
/*    */   private Capture _cap;
/*    */   private int _state;
/*    */ 
/*    */   TraceOutputStream(Capture paramCapture, OutputStream paramOutputStream, int paramInt)
/*    */   {
/* 54 */     this._cap = paramCapture;
/* 55 */     this._out = paramOutputStream;
/* 56 */     Debug.asrt(this, (paramInt == 2) || (paramInt == 1));
/*    */ 
/* 58 */     this._state = paramInt;
/*    */   }
/*    */ 
/*    */   public void write(int paramInt)
/*    */     throws IOException
/*    */   {
/* 67 */     byte[] arrayOfByte = new byte[1];
/* 68 */     arrayOfByte[0] = (byte)paramInt;
/* 69 */     write(arrayOfByte, 0, 1);
/*    */   }
/*    */ 
/*    */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*    */     throws IOException
/*    */   {
/* 76 */     this._out.write(paramArrayOfByte, paramInt1, paramInt2);
/* 77 */     if (this._state != 2)
/*    */       return;
/* 79 */     this._cap.writeBuffer(1, paramArrayOfByte, paramInt1, paramInt2);
/*    */   }
/*    */ 
/*    */   protected void setState(int paramInt)
/*    */   {
/* 87 */     this._state = paramInt;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.TraceOutputStream
 * JD-Core Version:    0.5.4
 */