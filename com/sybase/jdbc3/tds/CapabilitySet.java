/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import java.util.BitSet;
/*    */ 
/*    */ class CapabilitySet
/*    */ {
/*    */   protected BitSet _caps;
/*    */   protected int _type;
/*    */   protected int _bits;
/*    */   protected int _maskLen;
/*    */ 
/*    */   public CapabilitySet(int paramInt1, int paramInt2)
/*    */   {
/* 42 */     this._caps = new BitSet();
/* 43 */     this._type = paramInt1;
/* 44 */     this._maskLen = paramInt2;
/* 45 */     this._bits = (paramInt2 * 8);
/*    */   }
/*    */ 
/*    */   public boolean get(int paramInt)
/*    */   {
/* 54 */     boolean bool = false;
/* 55 */     if ((paramInt > 0) && (paramInt < this._bits))
/*    */     {
/* 57 */       bool = this._caps.get(paramInt);
/*    */     }
/* 59 */     return bool;
/*    */   }
/*    */ 
/*    */   public void set(int paramInt)
/*    */   {
/* 67 */     if ((paramInt <= 0) || (paramInt >= this._bits))
/*    */       return;
/* 69 */     this._caps.set(paramInt);
/*    */   }
/*    */ 
/*    */   public void clear(int paramInt)
/*    */   {
/* 78 */     if ((paramInt <= 0) || (paramInt >= this._bits))
/*    */       return;
/* 80 */     this._caps.clear(paramInt);
/*    */   }
/*    */ 
/*    */   public void setMaskSize(int paramInt)
/*    */   {
/* 88 */     this._maskLen = paramInt;
/* 89 */     this._bits = (paramInt * 8);
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CapabilitySet
 * JD-Core Version:    0.5.4
 */