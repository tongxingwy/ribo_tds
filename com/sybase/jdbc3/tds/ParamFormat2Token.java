/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.jdbc.Param;
/*    */ import com.sybase.jdbc3.jdbc.ParamManager;
/*    */ import com.sybase.jdbc3.jdbc.Protocol;
/*    */ import java.io.IOException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class ParamFormat2Token extends ParamFormatToken
/*    */ {
/*    */   public ParamFormat2Token()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ParamFormat2Token(ParamManager paramParamManager, boolean paramBoolean)
/*    */     throws IOException, SQLException
/*    */   {
/* 45 */     super(paramParamManager, paramBoolean);
/*    */   }
/*    */ 
/*    */   public ParamFormat2Token(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/* 51 */     super(paramTdsInputStream);
/*    */   }
/*    */ 
/*    */   public ParamFormat2Token(ParamManager paramParamManager, Param[] paramArrayOfParam, Protocol paramProtocol, byte paramByte, boolean paramBoolean)
/*    */     throws IOException, SQLException
/*    */   {
/* 57 */     super(paramParamManager, paramArrayOfParam, paramProtocol, paramByte, paramBoolean);
/*    */   }
/*    */ 
/*    */   protected DataFormat dataFormatFactory(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/* 67 */     return new ParamDataFormat2(paramTdsInputStream);
/*    */   }
/*    */ 
/*    */   protected long readLength(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/* 76 */     long l = paramTdsInputStream.readUnsignedIntAsLong();
/* 77 */     return l;
/*    */   }
/*    */ 
/*    */   public void send(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/* 88 */     paramTdsOutputStream.writeByte(32);
/* 89 */     paramTdsOutputStream.writeLongAsUnsignedInt(getLength());
/* 90 */     paramTdsOutputStream.writeShort(getFormatCount());
/* 91 */     sendFormat(paramTdsOutputStream);
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.ParamFormat2Token
 * JD-Core Version:    0.5.4
 */