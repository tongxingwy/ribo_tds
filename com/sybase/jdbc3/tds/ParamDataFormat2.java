/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class ParamDataFormat2 extends DataFormat
/*    */ {
/*    */   protected static final int FIXED_LENGTH_PART = 11;
/*    */ 
/*    */   public ParamDataFormat2(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/* 48 */     super(paramTdsInputStream, true);
/*    */   }
/*    */ 
/*    */   public ParamDataFormat2(TdsInputStream paramTdsInputStream, boolean paramBoolean)
/*    */     throws IOException
/*    */   {
/* 62 */     super(paramTdsInputStream, paramBoolean);
/*    */   }
/*    */ 
/*    */   public ParamDataFormat2(TdsParam paramTdsParam, TdsOutputStream paramTdsOutputStream, byte paramByte)
/*    */     throws IOException
/*    */   {
/* 73 */     super(paramTdsParam, paramTdsOutputStream, paramByte);
/*    */   }
/*    */ 
/*    */   protected void readStatus(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/* 83 */     this._status = paramTdsInputStream.readInt();
/*    */   }
/*    */ 
/*    */   protected void sendStatus(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/* 93 */     paramTdsOutputStream.writeInt(this._status);
/*    */   }
/*    */ 
/*    */   protected int getFixedLengthPart()
/*    */   {
/* 98 */     return 11;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.ParamDataFormat2
 * JD-Core Version:    0.5.4
 */