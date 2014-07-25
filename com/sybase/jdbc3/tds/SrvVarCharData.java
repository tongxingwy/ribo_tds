/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.UnsupportedEncodingException;
/*    */ 
/*    */ public class SrvVarCharData
/*    */ {
/*    */   private byte[] _bytes;
/*    */   private String _encoder;
/*    */ 
/*    */   public SrvVarCharData(byte[] paramArrayOfByte, String paramString)
/*    */   {
/* 39 */     this._bytes = paramArrayOfByte;
/* 40 */     this._encoder = paramString;
/*    */   }
/*    */ 
/*    */   public String getString()
/*    */     throws UnsupportedEncodingException
/*    */   {
/* 46 */     String str = new String(this._bytes, this._encoder);
/* 47 */     return str;
/*    */   }
/*    */ 
/*    */   public byte[] getBytes()
/*    */   {
/* 53 */     return this._bytes;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvVarCharData
 * JD-Core Version:    0.5.4
 */