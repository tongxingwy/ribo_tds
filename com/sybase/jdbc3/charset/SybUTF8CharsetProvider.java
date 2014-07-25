/*    */ package com.sybase.jdbc3.charset;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.spi.CharsetProvider;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ public class SybUTF8CharsetProvider extends CharsetProvider
/*    */ {
/*    */   public Charset charsetForName(String paramString)
/*    */   {
/* 39 */     if (paramString.equalsIgnoreCase("x-SybUTF8"))
/*    */     {
/* 41 */       return SybUTF8Charset.getInstance();
/*    */     }
/* 43 */     return null;
/*    */   }
/*    */ 
/*    */   public Iterator charsets()
/*    */   {
/* 53 */     HashSet localHashSet = new HashSet();
/* 54 */     localHashSet.add(SybUTF8Charset.getInstance());
/* 55 */     return localHashSet.iterator();
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.charset.SybUTF8CharsetProvider
 * JD-Core Version:    0.5.4
 */