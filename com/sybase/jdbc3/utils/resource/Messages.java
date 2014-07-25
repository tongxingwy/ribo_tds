/*    */ package com.sybase.jdbc3.utils.resource;
/*    */ 
/*    */ import java.util.ListResourceBundle;
/*    */ 
/*    */ public class Messages extends ListResourceBundle
/*    */ {
/* 28 */   private static final Object[][] CONTENTS = { { "IO_CACHE_EXHAUSED", "Cache room exhausted." }, { "IO_NOT_RESETABLE", "This IOStream is not resetable.  This is an internal product error.  Please report it to Sybase technical support." }, { "IO_CLOSED", "This InputStream was closed." }, { "IO_NOT_OPEN", "This CacheableInputStream is not open.  This is an internal product error.  Please report it to Sybase technical support." } };
/*    */ 
/*    */   public Object[][] getContents()
/*    */   {
/* 55 */     return CONTENTS;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.resource.Messages
 * JD-Core Version:    0.5.4
 */