/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PipedInputStream;
/*     */ import java.io.PipedOutputStream;
/*     */ 
/*     */ public class DualPipedOutputStream extends PipedOutputStream
/*     */ {
/*  23 */   private OutputStream _fos = null;
/*     */ 
/*     */   public DualPipedOutputStream(PipedInputStream snk, OutputStream fos)
/*     */     throws IOException
/*     */   {
/*  33 */     super(snk);
/*  34 */     this._fos = fos;
/*     */   }
/*     */ 
/*     */   public DualPipedOutputStream()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void write(int b)
/*     */     throws IOException
/*     */   {
/*  51 */     if (this._fos != null)
/*     */     {
/*  53 */       this._fos.write(b);
/*     */     }
/*  55 */     super.write(b);
/*     */   }
/*     */ 
/*     */   public void write(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/*  64 */     if (b == null)
/*     */     {
/*  66 */       throw new NullPointerException();
/*     */     }
/*  68 */     if (len == 0)
/*     */     {
/*  70 */       return;
/*     */     }
/*  72 */     byte[] bCopy = new byte[b.length];
/*  73 */     System.arraycopy(b, 0, bCopy, 0, b.length);
/*  74 */     if (this._fos != null)
/*     */     {
/*  76 */       this._fos.write(bCopy, off, len);
/*     */     }
/*  78 */     super.write(b, off, len);
/*     */   }
/*     */ 
/*     */   public synchronized void flush()
/*     */     throws IOException
/*     */   {
/*  87 */     if (this._fos != null)
/*     */     {
/*  89 */       this._fos.flush();
/*     */     }
/*  91 */     super.flush();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 100 */     if (this._fos != null)
/*     */     {
/* 102 */       this._fos.close();
/*     */     }
/* 104 */     super.close();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.DualPipedOutputStream
 * JD-Core Version:    0.5.4
 */