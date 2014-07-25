/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ class MetaDataAccessor
/*    */ {
/*    */   protected static final int RPC = 1;
/*    */   protected static final int LANGUAGE = 2;
/*    */   protected static final int NOT_SUPPORTED = 3;
/*    */   protected static final int LITERAL_BOOLEAN = 4;
/*    */   protected static final int LITERAL_INTEGER = 5;
/*    */   protected static final int LITERAL_STRING = 6;
/*    */   protected static final int LITERAL_STRING_NO_TOKEN = 7;
/*    */   protected int _queryType;
/*    */   protected String _query;
/*    */ 
/*    */   protected MetaDataAccessor(int paramInt, String paramString)
/*    */   {
/* 57 */     this._queryType = paramInt;
/*    */ 
/* 74 */     this._query = paramString;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.MetaDataAccessor
 * JD-Core Version:    0.5.4
 */