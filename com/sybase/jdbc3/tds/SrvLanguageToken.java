/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvLanguageToken extends LanguageToken
/*     */   implements Dumpable
/*     */ {
/*     */   protected long _totalLen;
/*     */ 
/*     */   public SrvLanguageToken(String paramString, boolean paramBoolean)
/*     */   {
/*  40 */     super(paramString, paramBoolean, false);
/*  41 */     this._totalLen = (paramString.length() + 1);
/*     */   }
/*     */ 
/*     */   public SrvLanguageToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  51 */     this._totalLen = paramTdsInputStream.readInt();
/*  52 */     this._status = paramTdsInputStream.readUnsignedByte();
/*  53 */     this._query = paramTdsInputStream.readString((int)this._totalLen - 1);
/*     */   }
/*     */ 
/*     */   public boolean hasParams()
/*     */   {
/*  62 */     return (this._status & 0x1) != 0;
/*     */   }
/*     */ 
/*     */   public String getLanguage()
/*     */   {
/*  71 */     return this._query;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  85 */     DumpInfo localDumpInfo = null;
/*  86 */     if (paramDumpFilter.includesToken(33))
/*     */     {
/*  88 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  89 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  91 */         localDumpInfo.addInfo("Token", 1, "LANGUAGE Token (0x" + HexConverts.hexConvert(33, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/*  97 */         localDumpInfo.addInfo("Token", 1, "LANGUAGE Token");
/*     */       }
/*     */ 
/* 100 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 102 */         localDumpInfo.addInt("Length", 4, this._totalLen);
/*     */       }
/*     */ 
/* 105 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(2)))
/*     */       {
/* 108 */         String[] arrayOfString = { "UNUSED", "PARAMETERIZED", "UNUSED", "LANGUAGE_BATCH" };
/*     */ 
/* 113 */         localDumpInfo.addBitfield("Status", 1, this._status, arrayOfString);
/* 114 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 116 */           localDumpInfo.addInt("Text Length", 0, this._totalLen - 1L);
/*     */         }
/* 118 */         localDumpInfo.addText("Text", this._query.length(), this._query);
/*     */       }
/*     */     }
/* 121 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 129 */     return 33;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvLanguageToken
 * JD-Core Version:    0.5.4
 */