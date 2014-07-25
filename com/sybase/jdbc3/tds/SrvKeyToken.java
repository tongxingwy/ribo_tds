/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvKeyToken extends KeyToken
/*     */   implements SrvDataToken, Dumpable
/*     */ {
/*  28 */   SrvTypeFormatter _formatter = null;
/*     */ 
/*  33 */   TdsInputStream _in = null;
/*     */ 
/*     */   public SrvKeyToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  44 */     super(paramTdsInputStream);
/*  45 */     this._in = paramTdsInputStream;
/*     */   }
/*     */ 
/*     */   public void setFormatter(SrvTypeFormatter paramSrvTypeFormatter)
/*     */   {
/*  54 */     this._formatter = paramSrvTypeFormatter;
/*     */   }
/*     */ 
/*     */   public TdsInputStream getStream()
/*     */   {
/*  62 */     return this._in;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  70 */     DumpInfo localDumpInfo = null;
/*     */ 
/*  73 */     if (paramDumpFilter.includesToken(202))
/*     */     {
/*  75 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  76 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  78 */         localDumpInfo.addInfo("Token", 1, "KEY Token (0x" + HexConverts.hexConvert(202, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/*  84 */         localDumpInfo.addInfo("Token", 1, "KEY Token");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  90 */     if (this._formatter != null)
/*     */     {
/*  93 */       SrvJavaTypeFormatter localSrvJavaTypeFormatter = (SrvJavaTypeFormatter)this._formatter;
/*  94 */       Object[] arrayOfObject = localSrvJavaTypeFormatter.convertData(this);
/*     */ 
/*  96 */       if ((paramDumpFilter.includesToken(202)) && ((
/*  98 */         (paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(6)))))
/*     */       {
/* 101 */         int[] arrayOfInt1 = localSrvJavaTypeFormatter.getDataLengths();
/* 102 */         int[] arrayOfInt2 = localSrvJavaTypeFormatter.getLengthSizes();
/* 103 */         for (int i = 0; i < arrayOfObject.length; ++i)
/*     */         {
/* 105 */           localDumpInfo.addInfo("TDSFmt", 0, "Column " + (i + 1));
/* 106 */           if (paramDumpFilter.includesDetail(1))
/*     */           {
/* 109 */             localDumpInfo.addInt("Length", arrayOfInt2[i], arrayOfInt1[i]);
/*     */           }
/* 111 */           localDumpInfo.addValue("Key data", arrayOfInt1[i], arrayOfObject[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 116 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 124 */     return 202;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvKeyToken
 * JD-Core Version:    0.5.4
 */