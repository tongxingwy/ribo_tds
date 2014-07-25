/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvOffsetToken extends Token
/*     */   implements Dumpable
/*     */ {
/*     */   public static final int TDS_OFF_SELECT = 365;
/*     */   public static final int TDS_OFF_FROM = 335;
/*     */   public static final int TDS_OFF_ORDER = 357;
/*     */   public static final int TDS_OFF_COMPUTE = 313;
/*     */   public static final int TDS_OFF_TABLE = 371;
/*     */   public static final int TDS_OFF_PROC = 362;
/*     */   public static final int TDS_OFF_STMT = 459;
/*     */   public static final int TDS_OFF_PARAM = 452;
/*     */   private int _keyword;
/*     */   private int _offset;
/*     */ 
/*     */   public SrvOffsetToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  43 */     this._keyword = paramTdsInputStream.readShort();
/*  44 */     this._offset = paramTdsInputStream.readShort();
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  58 */     DumpInfo localDumpInfo = null;
/*  59 */     if (paramDumpFilter.includesToken(120))
/*     */     {
/*  61 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  62 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  64 */         localDumpInfo.addInfo("Token", 1, "OFFSET Token (0x" + HexConverts.hexConvert(120, 1) + "); fixed length.");
/*     */       }
/*     */       else
/*     */       {
/*  70 */         localDumpInfo.addInfo("Token", 1, "OFFSET Token");
/*     */       }
/*     */ 
/*  73 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/*  75 */         localDumpInfo.addInfo("Keyword", 2, getKeywordString(this._keyword));
/*  76 */         localDumpInfo.addInt("Offset", 2, this._offset);
/*     */       }
/*     */     }
/*  79 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/*  87 */     return 120;
/*     */   }
/*     */ 
/*     */   private String getKeywordString(int paramInt)
/*     */   {
/*     */     String str;
/*  96 */     switch (paramInt)
/*     */     {
/*     */     case 365:
/*  99 */       str = "TDS_OFF_SELECT";
/* 100 */       break;
/*     */     case 335:
/* 103 */       str = "TDS_OFF_FROM";
/* 104 */       break;
/*     */     case 357:
/* 107 */       str = "TDS_OFF_ORDER";
/* 108 */       break;
/*     */     case 313:
/* 111 */       str = "TDS_OFF_COMPUTE";
/* 112 */       break;
/*     */     case 371:
/* 115 */       str = "TDS_OFF_TABLE";
/* 116 */       break;
/*     */     case 362:
/* 119 */       str = "TDS_OFF_PROC";
/* 120 */       break;
/*     */     case 459:
/* 123 */       str = "TDS_OFF_STMT";
/* 124 */       break;
/*     */     case 452:
/* 127 */       str = "TDS_OFF_PARAM";
/* 128 */       break;
/*     */     default:
/* 131 */       str = "<unrecognized>";
/*     */     }
/*     */ 
/* 134 */     return str;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvOffsetToken
 * JD-Core Version:    0.5.4
 */