/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvOrderByToken extends Token
/*     */   implements Dumpable
/*     */ {
/*     */   protected long _length;
/*     */   protected int _noColumns;
/*     */   protected int[] _columnNos;
/*     */ 
/*     */   public SrvOrderByToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  43 */       this._length = readLength(paramTdsInputStream);
/*  44 */       this._noColumns = paramTdsInputStream.readUnsignedShort();
/*  45 */       this._columnNos = new int[this._noColumns];
/*  46 */       for (int i = 0; i < this._noColumns; ++i)
/*     */       {
/*  48 */         this._columnNos[i] = readColumnNum(paramTdsInputStream);
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*  53 */       readSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected long readLength(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  62 */     return 0L;
/*     */   }
/*     */ 
/*     */   protected int readColumnNum(TdsInputStream paramTdsInputStream) throws IOException
/*     */   {
/*  67 */     return paramTdsInputStream.readUnsignedByte();
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  82 */     DumpInfo localDumpInfo = null;
/*  83 */     if (paramDumpFilter.includesToken(169))
/*     */     {
/*  85 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  86 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  88 */         localDumpInfo.addInfo("Token", 1, "ORDERBY Token (0x" + HexConverts.hexConvert(169, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/*  95 */         localDumpInfo.addInfo("Token", 1, "ORDERBY Token");
/*     */       }
/*     */ 
/*  98 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 100 */         localDumpInfo.addInt("No. Columns", 2, this._noColumns);
/* 101 */         for (int i = 0; i < this._noColumns; ++i) {
/* 102 */           localDumpInfo.addInt("Column", 1, this._columnNos[i]);
/*     */         }
/*     */       }
/*     */     }
/* 106 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 113 */     return 169;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvOrderByToken
 * JD-Core Version:    0.5.4
 */