/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.utils.DumpFilter;
/*    */ import com.sybase.jdbc3.utils.DumpInfo;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class SrvPassthroughLanguageToken extends Token
/*    */   implements Dumpable
/*    */ {
/* 30 */   protected byte[] rawBuffer = null;
/*    */ 
/* 48 */   protected boolean lastBuffer = false;
/*    */ 
/*    */   public byte[] getRawBuffer()
/*    */   {
/* 37 */     return this.rawBuffer;
/*    */   }
/*    */ 
/*    */   public boolean isLastBuffer()
/*    */   {
/* 45 */     return this.lastBuffer;
/*    */   }
/*    */ 
/*    */   public SrvPassthroughLanguageToken(int paramInt, SrvDataInputStream paramSrvDataInputStream)
/*    */     throws IOException
/*    */   {
/* 51 */     int i = paramSrvDataInputStream.getCurrentPDULength() - 8;
/* 52 */     int j = ((paramSrvDataInputStream.getCurrentPDUStatus() & 0x1) == 1) ? 1 : 0;
/* 53 */     byte[] arrayOfByte = new byte[i];
/*    */ 
/* 55 */     arrayOfByte[0] = (byte)paramInt;
/* 56 */     paramSrvDataInputStream.read(arrayOfByte, 1, i - 1);
/*    */ 
/* 58 */     this.rawBuffer = arrayOfByte;
/* 59 */     this.lastBuffer = j;
/*    */   }
/*    */ 
/*    */   public DumpInfo dump(DumpFilter paramDumpFilter) throws IOException {
/* 63 */     return null;
/*    */   }
/*    */ 
/*    */   public int getTokenType() {
/* 67 */     return 1;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvPassthroughLanguageToken
 * JD-Core Version:    0.5.4
 */