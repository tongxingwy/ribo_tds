/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvDynamic2Token extends Dynamic2Token
/*     */   implements Dumpable
/*     */ {
/*     */   public SrvDynamic2Token(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  35 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  51 */     DumpInfo localDumpInfo = null;
/*  52 */     if (paramDumpFilter.includesToken(231))
/*     */     {
/*  54 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  55 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  57 */         localDumpInfo.addInfo("Token", 1, "DYNAMIC2 Token (0x" + HexConverts.hexConvert(98, 1) + "); variable length");
/*     */       }
/*     */       else
/*     */       {
/*  63 */         localDumpInfo.addInfo("Token", 1, "DYNAMIC2 Token");
/*     */       }
/*  65 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/*  67 */         localDumpInfo.addInt("Length", 4, this._totalLen);
/*     */       }
/*     */ 
/*  71 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/*  73 */         String[] arrayOfString1 = { "<unrecognized>", "DYN_PREPARE", "DYN_EXEC", "DYN_DEALLOC", "DYN_EXEC_IMMED", "DYN_PROCNAME", "DYN_ACK", "DYN_DESCIN", "DYN_DESCOUT" };
/*     */ 
/*  80 */         localDumpInfo.addBitfield("Type", 1, this._type, arrayOfString1);
/*     */ 
/*  82 */         String[] arrayOfString2 = { "UNUSED", "DYNAMIC_HASARGS", "DYNAMIC_SUPPRESS_FMT", "DYNAMIC_BATCH_PARAMS", "DYNAMIC_SUPPRESS_PARAMFMT" };
/*     */ 
/*  91 */         localDumpInfo.addBitfield("Status", 1, this._status, arrayOfString2);
/*     */ 
/*  93 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/*  95 */           localDumpInfo.addInt("Name Length", 1, this._nameLen);
/*     */         }
/*  97 */         localDumpInfo.addText("Name", this._nameLen, this._name);
/*     */ 
/*  99 */         if (this._bodyLen > 0L)
/*     */         {
/* 101 */           if (paramDumpFilter.includesDetail(1))
/*     */           {
/* 103 */             localDumpInfo.addInt("Statement Length", 4, this._bodyLen);
/*     */           }
/* 105 */           localDumpInfo.addText("Statement", (int)this._bodyLen, this._body);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 110 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 118 */     return 98;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDynamic2Token
 * JD-Core Version:    0.5.4
 */