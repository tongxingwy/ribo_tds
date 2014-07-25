/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class MsgToken extends Token
/*    */ {
/* 35 */   protected byte _length = 3;
/*    */ 
/* 38 */   protected byte _status = 0;
/*    */ 
/* 41 */   protected short _msgID = 0;
/*    */ 
/*    */   protected MsgToken(byte paramByte, short paramShort)
/*    */   {
/* 53 */     this._status = paramByte;
/* 54 */     this._msgID = paramShort;
/*    */   }
/*    */ 
/*    */   protected MsgToken(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/* 68 */     this._length = paramTdsInputStream.readByte();
/* 69 */     this._status = paramTdsInputStream.readByte();
/* 70 */     this._msgID = paramTdsInputStream.readShort();
/*    */   }
/*    */ 
/*    */   public int getMessageID()
/*    */   {
/* 84 */     return this._msgID;
/*    */   }
/*    */ 
/*    */   protected void send(TdsOutputStream paramTdsOutputStream)
/*    */     throws IOException
/*    */   {
/* 96 */     paramTdsOutputStream.writeByte(101);
/* 97 */     paramTdsOutputStream.writeByte(this._length);
/* 98 */     paramTdsOutputStream.writeByte(this._status);
/* 99 */     paramTdsOutputStream.writeShort(this._msgID);
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.MsgToken
 * JD-Core Version:    0.5.4
 */