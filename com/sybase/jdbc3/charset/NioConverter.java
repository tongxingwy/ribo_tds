/*    */ package com.sybase.jdbc3.charset;
/*    */ 
/*    */ import com.sybase.jdbcx.CharsetConverter;
/*    */ import java.io.CharConversionException;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.IllegalCharsetNameException;
/*    */ import java.nio.charset.UnsupportedCharsetException;
/*    */ 
/*    */ public class NioConverter
/*    */   implements CharsetConverter
/*    */ {
/*    */   private String _encoding;
/*    */   private Charset _charset;
/*    */ 
/*    */   public NioConverter()
/*    */     throws UnsupportedEncodingException
/*    */   {
/* 49 */     this._encoding = null;
/*    */   }
/*    */ 
/*    */   public void setEncoding(String paramString)
/*    */     throws UnsupportedCharsetException, IllegalCharsetNameException
/*    */   {
/* 59 */     this._encoding = paramString;
/*    */ 
/* 61 */     if (this._encoding.equals("x-SybUTF8"))
/*    */     {
/* 63 */       this._charset = SybUTF8Charset.getInstance();
/* 64 */       return;
/*    */     }
/* 66 */     this._charset = Charset.forName(paramString);
/*    */   }
/*    */ 
/*    */   public byte[] fromUnicode(String paramString)
/*    */     throws CharConversionException
/*    */   {
/* 74 */     return CharsetUtil.fromUnicode(paramString, this._encoding, this._charset);
/*    */   }
/*    */ 
/*    */   public String toUnicode(byte[] paramArrayOfByte)
/*    */     throws CharConversionException
/*    */   {
/* 83 */     return CharsetUtil.toUnicode(paramArrayOfByte, this._encoding, this._charset);
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.charset.NioConverter
 * JD-Core Version:    0.5.4
 */