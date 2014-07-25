/*    */ package com.sybase.ribo;
/*    */ 
/*    */ import com.sybase.jdbc3.utils.DumpInfo;
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public class InfoEvent extends EventObject
/*    */ {
/*    */   private DumpInfo _info;
/*    */ 
/*    */   public InfoEvent(Object source, DumpInfo info)
/*    */   {
/* 30 */     super(source);
/* 31 */     this._info = info;
/*    */   }
/*    */ 
/*    */   public DumpInfo getInfo()
/*    */   {
/* 36 */     return this._info;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.InfoEvent
 * JD-Core Version:    0.5.4
 */