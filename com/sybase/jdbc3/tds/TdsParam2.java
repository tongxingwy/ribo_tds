/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.jdbc.Protocol;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class TdsParam2 extends TdsParam
/*    */ {
/*    */   public TdsParam2(TdsDataOutputStream paramTdsDataOutputStream)
/*    */   {
/* 43 */     this._tdos = paramTdsDataOutputStream;
/*    */   }
/*    */ 
/*    */   protected boolean makeFormat(Protocol paramProtocol, byte paramByte)
/*    */     throws IOException
/*    */   {
/* 54 */     if ((this._sqlType != -998) && (!this._sendAsLiteral))
/*    */     {
/* 56 */       this._inDataFmt = new ParamDataFormat2(this, ((Tds)paramProtocol)._out, paramByte);
/* 57 */       return true;
/*    */     }
/* 59 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsParam2
 * JD-Core Version:    0.5.4
 */