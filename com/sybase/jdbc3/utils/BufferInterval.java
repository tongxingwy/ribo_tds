/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ public class BufferInterval
/*     */ {
/*     */   public byte[] _buf;
/*     */   public int _offset;
/*     */   public int _length;
/*     */   private BufferPool _bp;
/*     */   protected BufferInterval _prev;
/*     */   protected BufferInterval _next;
/*     */ 
/*     */   public BufferInterval(byte[] paramArrayOfByte, int paramInt1, int paramInt2, BufferPool paramBufferPool)
/*     */   {
/*  40 */     this._buf = paramArrayOfByte;
/*  41 */     this._offset = paramInt1;
/*  42 */     this._length = paramInt2;
/*  43 */     this._bp = paramBufferPool;
/*  44 */     this._next = null;
/*  45 */     this._prev = null;
/*     */   }
/*     */ 
/*     */   public boolean free()
/*     */   {
/*  56 */     boolean bool = this._bp.put(this);
/*  57 */     if (bool)
/*     */     {
/*  59 */       this._bp.decrementInMemoryBICount();
/*     */     }
/*  61 */     return bool;
/*     */   }
/*     */ 
/*     */   public synchronized BufferInterval divide(int paramInt)
/*     */     throws IndexOutOfBoundsException
/*     */   {
/*  74 */     BufferInterval localBufferInterval = null;
/*  75 */     if (paramInt > 0)
/*     */     {
/*  77 */       if (this._length == paramInt)
/*     */       {
/*  79 */         throw new IndexOutOfBoundsException("BufferInterval");
/*     */       }
/*  81 */       localBufferInterval = new BufferInterval(this._buf, this._offset, paramInt, this._bp);
/*  82 */       this._offset += paramInt;
/*  83 */       this._length -= paramInt;
/*     */     }
/*  85 */     return localBufferInterval;
/*     */   }
/*     */ 
/*     */   protected synchronized boolean merge(BufferInterval paramBufferInterval, boolean paramBoolean)
/*     */   {
/* 101 */     if (this._buf != paramBufferInterval._buf)
/*     */     {
/* 104 */       return false;
/*     */     }
/*     */ 
/* 114 */     if (this._offset + this._length == paramBufferInterval._offset)
/*     */     {
/* 116 */       if (paramBoolean)
/*     */       {
/* 118 */         paramBufferInterval._length += this._length;
/* 119 */         paramBufferInterval._offset = this._offset;
/*     */       }
/*     */       else
/*     */       {
/* 123 */         this._length += paramBufferInterval._length;
/*     */       }
/*     */ 
/* 127 */       return true;
/*     */     }
/* 129 */     if (paramBufferInterval._offset + paramBufferInterval._length == this._offset)
/*     */     {
/* 131 */       if (paramBoolean)
/*     */       {
/* 133 */         paramBufferInterval._length += this._length;
/*     */       }
/*     */       else
/*     */       {
/* 137 */         this._length += paramBufferInterval._length;
/* 138 */         this._offset = paramBufferInterval._offset;
/*     */       }
/*     */ 
/* 142 */       return true;
/*     */     }
/*     */ 
/* 145 */     return false;
/*     */   }
/*     */ 
/*     */   protected void detach()
/*     */   {
/* 155 */     if (this._prev != null)
/*     */     {
/* 157 */       this._prev._next = this._next;
/*     */     }
/* 159 */     if (this._next != null)
/*     */     {
/* 161 */       this._next._prev = this._prev;
/*     */     }
/* 163 */     this._next = null;
/* 164 */     this._prev = null;
/*     */   }
/*     */ 
/*     */   protected void attachBefore(BufferInterval paramBufferInterval)
/*     */   {
/* 175 */     this._next = paramBufferInterval;
/* 176 */     this._prev = paramBufferInterval._prev;
/* 177 */     if (paramBufferInterval._prev != null)
/*     */     {
/* 179 */       paramBufferInterval._prev._next = this;
/*     */     }
/* 181 */     paramBufferInterval._prev = this;
/*     */   }
/*     */ 
/*     */   protected void attachAfter(BufferInterval paramBufferInterval)
/*     */   {
/* 192 */     this._next = paramBufferInterval._next;
/* 193 */     this._prev = paramBufferInterval;
/* 194 */     if (paramBufferInterval._next != null)
/*     */     {
/* 196 */       paramBufferInterval._next._prev = this;
/*     */     }
/* 198 */     paramBufferInterval._next = this;
/*     */   }
/*     */ 
/*     */   public BufferPool getBufferPool()
/*     */   {
/* 203 */     return this._bp;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.BufferInterval
 * JD-Core Version:    0.5.4
 */