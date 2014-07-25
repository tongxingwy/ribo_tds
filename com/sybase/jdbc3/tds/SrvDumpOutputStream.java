/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class SrvDumpOutputStream extends FilterOutputStream
/*     */ {
/*  30 */   private int _aCount = 0;
/*  31 */   private int _bCount = 0;
/*  32 */   private int _totalByteCount = 0;
/*     */ 
/*  34 */   private byte[] _bray = new byte[24];
/*  35 */   PrintStream _out = System.err;
/*     */ 
/*     */   SrvDumpOutputStream(OutputStream paramOutputStream)
/*     */   {
/*  43 */     super(paramOutputStream);
/*     */   }
/*     */ 
/*     */   public void write(int paramInt)
/*     */     throws IOException
/*     */   {
/*  51 */     dumpByte((byte)paramInt);
/*  52 */     this.out.write(paramInt);
/*     */   }
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/*  60 */     dumpBytes(paramArrayOfByte, 0, paramArrayOfByte.length);
/*  61 */     this.out.write(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  69 */     dumpBytes(paramArrayOfByte, paramInt1, paramInt2);
/*  70 */     this.out.write(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   private void dumpBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/*  81 */     for (int i = 0; i < paramInt2; ++i)
/*  82 */       dumpByte(paramArrayOfByte[(paramInt1 + i)]);
/*     */   }
/*     */ 
/*     */   static void printByte(byte paramByte)
/*     */   {
/*  91 */     byte[] arrayOfByte = Integer.toHexString(new Byte(paramByte).intValue()).getBytes();
/*  92 */     PrintStream localPrintStream = System.out;
/*  93 */     if (arrayOfByte.length > 2)
/*     */     {
/*  95 */       localPrintStream.write(arrayOfByte, arrayOfByte.length - 2, 2);
/*     */     }
/*  97 */     else if (arrayOfByte.length == 1)
/*     */     {
/*  99 */       localPrintStream.print("0");
/* 100 */       localPrintStream.write(arrayOfByte[0]);
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 106 */         localPrintStream.write(arrayOfByte);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void dumpByte(byte paramByte)
/*     */   {
/* 122 */     byte[] arrayOfByte = Integer.toHexString(new Byte(paramByte).intValue()).getBytes();
/* 123 */     if (arrayOfByte.length > 2)
/*     */     {
/* 125 */       this._out.write(arrayOfByte, arrayOfByte.length - 2, 2);
/*     */     }
/* 127 */     else if (arrayOfByte.length == 1)
/*     */     {
/* 129 */       this._out.print("0");
/* 130 */       this._out.write(arrayOfByte[0]);
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 136 */         this._out.write(arrayOfByte);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 144 */     this._aCount += 1;
/* 145 */     if (this._aCount == 4)
/*     */     {
/* 147 */       this._aCount = 0;
/* 148 */       this._out.print(" ");
/*     */     }
/*     */ 
/* 152 */     this._bray[this._bCount] = paramByte;
/* 153 */     this._bCount += 1;
/* 154 */     if (this._bCount != 24)
/*     */       return;
/* 156 */     this._bCount = 0;
/* 157 */     int i = 0;
/* 158 */     for (int j = 0; j < this._bray.length; ++j)
/*     */     {
/* 160 */       if (Character.isLetterOrDigit((char)this._bray[j]))
/* 161 */         this._out.write(this._bray[j]);
/*     */       else {
/* 163 */         this._out.print(".");
/*     */       }
/*     */ 
/* 166 */       ++i;
/* 167 */       if (i != 8)
/*     */         continue;
/* 169 */       i = 0;
/* 170 */       this._out.print(" ");
/*     */     }
/*     */ 
/* 174 */     this._totalByteCount += 24;
/* 175 */     this._out.println(" " + this._totalByteCount);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDumpOutputStream
 * JD-Core Version:    0.5.4
 */