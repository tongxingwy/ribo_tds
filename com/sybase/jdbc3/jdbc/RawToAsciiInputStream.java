/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import com.sybase.jdbc3.utils.CacheManager;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class RawToAsciiInputStream extends PadByteInputStream
/*    */ {
/*    */   public RawToAsciiInputStream(InputStream paramInputStream, int paramInt1, int paramInt2, CacheManager paramCacheManager)
/*    */     throws IOException
/*    */   {
/* 55 */     super(paramInputStream, paramInt1, paramInt2, paramCacheManager);
/*    */   }
/*    */ 
/*    */   public int read()
/*    */     throws IOException
/*    */   {
/* 65 */     if (this._padByteLengthRemaining == 0)
/*    */     {
/* 67 */       return -1;
/*    */     }
/* 69 */     if (this._even)
/*    */     {
/* 71 */       int i = super.read();
/* 72 */       if (-1 == i)
/*    */       {
/* 76 */         return -1;
/*    */       }
/* 78 */       this._bytes[0] = com.sybase.jdbc3.utils.HexConverts.HEX_INTS[((i & 0xF0) >> 4)];
/* 79 */       this._bytes[1] = com.sybase.jdbc3.utils.HexConverts.HEX_INTS[(i & 0xF)];
/*    */     }
/* 81 */     this._padByteLengthRemaining -= 1;
/* 82 */     this._even = (!this._even);
/* 83 */     return (this._even) ? this._bytes[1] : this._bytes[0];
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.RawToAsciiInputStream
 * JD-Core Version:    0.5.4
 */