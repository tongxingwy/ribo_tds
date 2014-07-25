/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvControlToken extends Token
/*     */   implements Dumpable
/*     */ {
/*     */   protected int _totalLen;
/*     */   protected Vector _formats;
/*     */ 
/*     */   public SrvControlToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  40 */     this._totalLen = paramTdsInputStream.readUnsignedShort();
/*  41 */     this._formats = new Vector();
/*     */ 
/*  43 */     int i = this._totalLen;
/*  44 */     while (i > 0) {
/*  45 */       ControlInfo localControlInfo = new ControlInfo(paramTdsInputStream);
/*  46 */       this._formats.addElement(localControlInfo);
/*  47 */       i -= localControlInfo.getLength();
/*     */     }
/*  49 */     if (i < 0)
/*  50 */       throw new IOException("Malformed Control token lengths");
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  66 */     DumpInfo localDumpInfo = null;
/*  67 */     if (paramDumpFilter.includesToken(174)) {
/*  68 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  69 */       if (paramDumpFilter.includesDetail(0)) {
/*  70 */         localDumpInfo.addInfo("Token", 1, "CONTROL Token (0x" + HexConverts.hexConvert(174, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/*  74 */         localDumpInfo.addInfo("Token", 1, "CONTROL Token");
/*     */       }
/*     */ 
/*  77 */       if (paramDumpFilter.includesDetail(1)) {
/*  78 */         localDumpInfo.addInt("Length", 2, this._totalLen);
/*     */       }
/*     */ 
/*  81 */       if (paramDumpFilter.includesDetail(3)) {
/*  82 */         Enumeration localEnumeration = this._formats.elements();
/*  83 */         while (localEnumeration.hasMoreElements()) {
/*  84 */           ControlInfo localControlInfo = (ControlInfo)localEnumeration.nextElement();
/*  85 */           localDumpInfo.addInfo(localControlInfo.dump(paramDumpFilter));
/*     */         }
/*     */       }
/*     */     }
/*  89 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/*  96 */     return 174;
/*     */   }
/*     */ 
/*     */   private class ControlInfo
/*     */     implements Dumpable
/*     */   {
/*     */     private int _length;
/*     */     private byte[] _format;
/*     */ 
/*     */     protected ControlInfo(TdsInputStream arg2)
/*     */       throws IOException
/*     */     {
/*     */       Object localObject;
/* 113 */       this._length = localObject.readUnsignedByte();
/* 114 */       if (this._length != 0) {
/* 115 */         this._format = new byte[this._length];
/* 116 */         localObject.read(this._format);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected int getLength()
/*     */     {
/* 122 */       return this._length + 1;
/*     */     }
/*     */ 
/*     */     public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */       throws IOException
/*     */     {
/* 131 */       DumpInfo localDumpInfo = paramDumpFilter.getDumpInfo();
/* 132 */       if (paramDumpFilter.includesDetail(1)) {
/* 133 */         localDumpInfo.addInt("Control Info Length", 1, this._length);
/*     */       }
/* 135 */       if (this._length != 0) {
/* 136 */         localDumpInfo.addHex("Control Info", this._length, this._format);
/*     */       }
/* 138 */       return localDumpInfo;
/*     */     }
/*     */ 
/*     */     public int getTokenType()
/*     */     {
/* 145 */       return -1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvControlToken
 * JD-Core Version:    0.5.4
 */