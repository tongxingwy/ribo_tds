/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvInfoToken extends SrvErrorToken
/*     */   implements Dumpable
/*     */ {
/*     */   public SrvInfoToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  40 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public SrvInfoToken(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, int paramInt4)
/*     */   {
/*  57 */     super(paramInt1, paramInt2, paramInt3, paramString1, paramString2, paramString3, paramInt4);
/*     */   }
/*     */ 
/*     */   public SrvInfoToken(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2)
/*     */   {
/*  73 */     this(paramInt1, 0, 1, paramString1, paramString2, paramString3, paramInt2);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  89 */     DumpInfo localDumpInfo = null;
/*  90 */     if (paramDumpFilter.includesToken(171))
/*     */     {
/*  92 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  93 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  95 */         localDumpInfo.addInfo("Token", 1, "INFO Token (0x" + HexConverts.hexConvert(171, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 101 */         localDumpInfo.addInfo("Token", 1, "INFO Token");
/*     */       }
/*     */ 
/* 105 */       localDumpInfo.addInfo(dumpDetails(paramDumpFilter));
/*     */     }
/* 107 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 115 */     return 171;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvInfoToken
 * JD-Core Version:    0.5.4
 */