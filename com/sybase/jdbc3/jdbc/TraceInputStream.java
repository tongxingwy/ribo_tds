/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import com.sybase.jdbc3.utils.Debug;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class TraceInputStream extends InputStream
/*    */ {
/*    */   private InputStream _in;
/*    */   private Capture _cap;
/*    */   private int _state;
/*    */ 
/*    */   TraceInputStream(Capture paramCapture, InputStream paramInputStream, int paramInt)
/*    */   {
/* 54 */     this._cap = paramCapture;
/* 55 */     this._in = paramInputStream;
/* 56 */     Debug.asrt(this, (paramInt == 2) || (paramInt == 1));
/*    */ 
/* 58 */     this._state = paramInt;
/*    */   }
/*    */ 
/*    */   public int read()
/*    */     throws IOException
/*    */   {
/* 67 */     byte[] arrayOfByte = new byte[1];
/* 68 */     int i = read(arrayOfByte, 0, 1);
/* 69 */     return (i == -1) ? i : arrayOfByte[0];
/*    */   }
/*    */ 
/*    */   public int read(byte[] paramArrayOfByte)
/*    */     throws IOException
/*    */   {
/* 75 */     return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*    */   }
/*    */ 
/*    */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*    */     throws IOException
/*    */   {
/* 82 */     int i = this._in.read(paramArrayOfByte, paramInt1, paramInt2);
/* 83 */     if ((i != -1) && (this._state == 2))
/*    */     {
/* 86 */       this._cap.writeBuffer(2, paramArrayOfByte, paramInt1, i);
/*    */     }
/* 88 */     return i;
/*    */   }
/*    */ 
/*    */   protected void setState(int paramInt)
/*    */   {
/* 96 */     this._state = paramInt;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.TraceInputStream
 * JD-Core Version:    0.5.4
 */