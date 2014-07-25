/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbcx.CharsetConverter;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class TdsDataInputStream extends TdsInputStream
/*    */ {
/*    */   protected Tds _tds;
/*    */ 
/*    */   public TdsDataInputStream(Tds paramTds, PduInputFormatter paramPduInputFormatter)
/*    */     throws IOException
/*    */   {
/* 52 */     super(paramPduInputFormatter);
/* 53 */     this._tds = paramTds;
/*    */   }
/*    */ 
/*    */   public final String readString(int paramInt)
/*    */     throws IOException
/*    */   {
/* 67 */     if (paramInt <= 0) return null;
/* 68 */     byte[] arrayOfByte = new byte[paramInt];
/* 69 */     this.in.read(arrayOfByte, 0, paramInt);
/* 70 */     return this._tds._charsetConverter.toUnicode(arrayOfByte);
/*    */   }
/*    */ 
/*    */   public final String convertBytesToString(byte[] paramArrayOfByte)
/*    */     throws IOException
/*    */   {
/* 82 */     return this._tds._charsetConverter.toUnicode(paramArrayOfByte);
/*    */   }
/*    */ 
/*    */   public String getCharset()
/*    */   {
/* 87 */     return this._tds._charsetName;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsDataInputStream
 * JD-Core Version:    0.5.4
 */