/*    */ package com.sybase.jdbc3.charset;
/*    */ 
/*    */ import com.sybase.jdbcx.CharsetConverter;
/*    */ import java.io.CharConversionException;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ 
/*    */ public class PureConverter
/*    */   implements CharsetConverter
/*    */ {
/*    */   protected String _encoding;
/*    */ 
/*    */   public PureConverter()
/*    */     throws UnsupportedEncodingException
/*    */   {
/* 41 */     this._encoding = null;
/*    */   }
/*    */ 
/*    */   public void setEncoding(String paramString)
/*    */     throws UnsupportedEncodingException
/*    */   {
/* 51 */     this._encoding = paramString;
/*    */ 
/* 53 */     if (this._encoding.equals("x-SybUTF8"))
/*    */       return;
/* 55 */     "Test".getBytes(paramString);
/*    */   }
/*    */ 
/*    */   public byte[] fromUnicode(String paramString)
/*    */     throws CharConversionException
/*    */   {
/*    */     try
/*    */     {
/* 67 */       return (this._encoding.equals("x-SybUTF8")) ? CharsetUtil.fromSybUTF8(paramString) : (this._encoding == null) ? paramString.getBytes() : paramString.getBytes(this._encoding);
/*    */     }
/*    */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*    */     {
/* 74 */       throw new CharConversionException(localUnsupportedEncodingException.toString());
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toUnicode(byte[] paramArrayOfByte)
/*    */     throws CharConversionException
/*    */   {
/*    */     try
/*    */     {
/* 86 */       return (this._encoding.equals("x-SybUTF8")) ? CharsetUtil.toSybUTF8(paramArrayOfByte) : (this._encoding == null) ? new String(paramArrayOfByte) : new String(paramArrayOfByte, this._encoding);
/*    */     }
/*    */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*    */     {
/* 93 */       throw new CharConversionException(localUnsupportedEncodingException.toString());
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.charset.PureConverter
 * JD-Core Version:    0.5.4
 */