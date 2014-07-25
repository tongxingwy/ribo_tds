/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import javax.transaction.xa.Xid;
/*     */ 
/*     */ public class SybXid
/*     */   implements Xid
/*     */ {
/*     */   private final int _formatID;
/*     */   private final byte[] _gtrid;
/*     */   private final byte[] _bqual;
/*     */ 
/*     */   public SybXid(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*     */   {
/*  95 */     this._formatID = paramInt;
/*     */ 
/* 101 */     this._gtrid = new byte[Math.min(paramArrayOfByte1.length, 64)];
/* 102 */     System.arraycopy(paramArrayOfByte1, 0, this._gtrid, 0, this._gtrid.length);
/*     */ 
/* 108 */     this._bqual = new byte[Math.min(paramArrayOfByte2.length, 64)];
/* 109 */     System.arraycopy(paramArrayOfByte2, 0, this._bqual, 0, this._bqual.length);
/*     */   }
/*     */ 
/*     */   public int getFormatId()
/*     */   {
/* 128 */     return this._formatID;
/*     */   }
/*     */ 
/*     */   public byte[] getGlobalTransactionId()
/*     */   {
/* 142 */     byte[] arrayOfByte = new byte[this._gtrid.length];
/* 143 */     System.arraycopy(this._gtrid, 0, arrayOfByte, 0, this._gtrid.length);
/*     */ 
/* 145 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public byte[] getBranchQualifier()
/*     */   {
/* 160 */     byte[] arrayOfByte = new byte[this._bqual.length];
/* 161 */     System.arraycopy(this._bqual, 0, arrayOfByte, 0, this._bqual.length);
/*     */ 
/* 163 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 185 */     if (!paramObject instanceof Xid)
/*     */     {
/* 187 */       return false;
/*     */     }
/*     */ 
/* 190 */     Xid localXid = (Xid)paramObject;
/*     */ 
/* 193 */     if (localXid.getFormatId() != this._formatID)
/*     */     {
/* 197 */       return false;
/*     */     }
/*     */ 
/* 201 */     byte[] arrayOfByte1 = localXid.getGlobalTransactionId();
/* 202 */     if (arrayOfByte1.length != this._gtrid.length)
/*     */     {
/* 206 */       return false;
/*     */     }
/*     */ 
/* 210 */     byte[] arrayOfByte2 = localXid.getBranchQualifier();
/* 211 */     if (arrayOfByte2.length != this._bqual.length)
/*     */     {
/* 215 */       return false;
/*     */     }
/*     */ 
/* 222 */     for (int i = 0; i < this._gtrid.length; ++i)
/*     */     {
/* 224 */       if (arrayOfByte1[i] != this._gtrid[i])
/*     */       {
/* 228 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 233 */     for (i = 0; i < this._bqual.length; ++i)
/*     */     {
/* 235 */       if (arrayOfByte2[i] != this._bqual[i])
/*     */       {
/* 239 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 244 */     return true;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybXid
 * JD-Core Version:    0.5.4
 */