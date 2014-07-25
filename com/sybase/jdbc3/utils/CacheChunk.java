/*    */ package com.sybase.jdbc3.utils;
/*    */ 
/*    */ public class CacheChunk
/*    */ {
/*    */   protected byte[] _buf;
/*    */   protected CacheChunk _next;
/*    */   protected int _length;
/*    */ 
/*    */   protected CacheChunk(byte[] paramArrayOfByte)
/*    */   {
/* 35 */     this._buf = paramArrayOfByte;
/* 36 */     this._next = null;
/* 37 */     this._length = 0;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.CacheChunk
 * JD-Core Version:    0.5.4
 */