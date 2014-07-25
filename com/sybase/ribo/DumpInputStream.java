/*    */ package com.sybase.ribo;
/*    */ 
/*    */ import com.sybase.jdbc3.tds.SrvDataInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class DumpInputStream extends SrvDataInputStream
/*    */ {
/*    */   public DumpInputStream(InputStream is)
/*    */     throws IOException
/*    */   {
/* 30 */     super(is);
/*    */ 
/* 33 */     setBigEndian(true);
/*    */   }
/*    */ 
/*    */   public int read()
/*    */     throws IOException
/*    */   {
/* 41 */     int b = this.in.read();
/* 42 */     return b;
/*    */   }
/*    */ 
/*    */   public int read(byte[] b)
/*    */     throws IOException
/*    */   {
/* 50 */     int count = this.in.read(b, 0, b.length);
/* 51 */     return count;
/*    */   }
/*    */ 
/*    */   public int read(byte[] b, int off, int len)
/*    */     throws IOException
/*    */   {
/* 59 */     int count = this.in.read(b, off, len);
/* 60 */     return count;
/*    */   }
/*    */ 
/*    */   protected boolean isBigEndian()
/*    */   {
/* 70 */     return getBigEndian();
/*    */   }
/*    */ 
/*    */   protected byte[] skipRest()
/*    */   {
/* 78 */     byte[] answer = null;
/*    */     try
/*    */     {
/* 81 */       int count = this.in.available();
/* 82 */       answer = new byte[count];
/* 83 */       read(answer);
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 88 */       e.printStackTrace();
/*    */     }
/* 90 */     return answer;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.DumpInputStream
 * JD-Core Version:    0.5.4
 */