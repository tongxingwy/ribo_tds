/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class SrvDumpInputStream extends FilterInputStream
/*     */ {
/*  31 */   private int _aCount = 0;
/*  32 */   private int _bCount = 0;
/*  33 */   private int _totalByteCount = 0;
/*     */ 
/*  35 */   private byte[] _bray = new byte[24];
/*  36 */   PrintStream _out = System.err;
/*     */ 
/*     */   SrvDumpInputStream(InputStream paramInputStream)
/*     */   {
/*  44 */     super(paramInputStream);
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  52 */     int i = this.in.read();
/*  53 */     if (i > -1)
/*  54 */       dumpByte((byte)i);
/*  55 */     return i;
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/*  63 */     int i = this.in.read(paramArrayOfByte);
/*  64 */     dumpBytes(paramArrayOfByte, 0, i);
/*  65 */     return i;
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  73 */     int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
/*  74 */     dumpBytes(paramArrayOfByte, paramInt1, i);
/*  75 */     return i;
/*     */   }
/*     */ 
/*     */   private void dumpBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/*  86 */     for (int i = 0; i < paramInt2; ++i)
/*  87 */       dumpByte(paramArrayOfByte[(i + paramInt1)]);
/*     */   }
/*     */ 
/*     */   static void printByte(byte paramByte)
/*     */   {
/*  96 */     byte[] arrayOfByte = Integer.toHexString(new Byte(paramByte).intValue()).getBytes();
/*  97 */     PrintStream localPrintStream = System.out;
/*  98 */     if (arrayOfByte.length > 2)
/*     */     {
/* 100 */       localPrintStream.write(arrayOfByte, arrayOfByte.length - 2, 2);
/*     */     }
/* 102 */     else if (arrayOfByte.length == 1)
/*     */     {
/* 104 */       localPrintStream.print("0");
/* 105 */       localPrintStream.write(arrayOfByte[0]);
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 111 */         localPrintStream.write(arrayOfByte);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void dumpByte(byte paramByte)
/*     */   {
/* 127 */     byte[] arrayOfByte = Integer.toHexString(new Byte(paramByte).intValue()).getBytes();
/* 128 */     if (arrayOfByte.length > 2)
/*     */     {
/* 130 */       this._out.write(arrayOfByte, arrayOfByte.length - 2, 2);
/*     */     }
/* 132 */     else if (arrayOfByte.length == 1)
/*     */     {
/* 134 */       this._out.print("0");
/* 135 */       this._out.write(arrayOfByte[0]);
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 141 */         this._out.write(arrayOfByte);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 149 */     this._aCount += 1;
/* 150 */     if (this._aCount == 4)
/*     */     {
/* 152 */       this._aCount = 0;
/* 153 */       this._out.print(" ");
/*     */     }
/*     */ 
/* 157 */     this._bray[this._bCount] = paramByte;
/* 158 */     this._bCount += 1;
/* 159 */     if (this._bCount != 24)
/*     */       return;
/* 161 */     this._bCount = 0;
/* 162 */     int i = 0;
/* 163 */     for (int j = 0; j < this._bray.length; ++j)
/*     */     {
/* 165 */       if (Character.isLetterOrDigit((char)this._bray[j]))
/* 166 */         this._out.write(this._bray[j]);
/*     */       else {
/* 168 */         this._out.print(".");
/*     */       }
/*     */ 
/* 171 */       ++i;
/* 172 */       if (i != 8)
/*     */         continue;
/* 174 */       i = 0;
/* 175 */       this._out.print(" ");
/*     */     }
/*     */ 
/* 179 */     this._totalByteCount += 24;
/* 180 */     this._out.println(" " + this._totalByteCount);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDumpInputStream
 * JD-Core Version:    0.5.4
 */