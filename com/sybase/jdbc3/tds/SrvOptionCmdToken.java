/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvOptionCmdToken extends Token
/*     */   implements Dumpable
/*     */ {
/*     */   private static final int TDS_CMD_SET = 1;
/*     */   private static final int TDS_OPT_UNUSED = 0;
/*     */   private static final int TDS_OPT_DATEFIRST = 1;
/*     */   private static final int TDS_OPT_TEXTSIZE = 2;
/*     */   private static final int TDS_OPT_STAT_TIME = 3;
/*     */   private static final int TDS_OPT_STAT_IO = 4;
/*     */   private static final int TDS_OPT_ROWCOUNT = 5;
/*     */   private static final int TDS_OPT_NATLANG = 6;
/*     */   private static final int TDS_OPT_DATEFORMAT = 7;
/*     */   private static final int TDS_OPT_ISOLATION = 8;
/*     */   private static final int TDS_OPT_AUTHON = 9;
/*     */   private static final int TDS_OPT_CHARSET = 10;
/*     */   private static final int TDS_OPT_SHOWPLAN = 13;
/*     */   private static final int TDS_OPT_NOEXEC = 14;
/*     */   private static final int TDS_OPT_ARITHIGNOREON = 15;
/*     */   private static final int TDS_OPT_ARITHABORTON = 17;
/*     */   private static final int TDS_OPT_PARSEONLY = 18;
/*     */   private static final int TDS_OPT_GETDATA = 20;
/*     */   private static final int TDS_OPT_NOCOUNT = 21;
/*     */   private static final int TDS_OPT_FORCEPLAN = 23;
/*     */   private static final int TDS_OPT_FORMATONLY = 24;
/*     */   private static final int TDS_OPT_CHAINXACTS = 25;
/*     */   private static final int TDS_OPT_CURCLOSEONXACT = 26;
/*     */   private static final int TDS_OPT_FIPSFLAG = 27;
/*     */   private static final int TDS_OPT_RESTREES = 28;
/*     */   private static final int TDS_OPT_IDENTITYON = 29;
/*     */   private static final int TDS_OPT_CURREAD = 30;
/*     */   private static final int TDS_OPT_CURWRITE = 31;
/*     */   private static final int TDS_OPT_IDENTITYOFF = 32;
/*     */   private static final int TDS_OPT_AUTHOFF = 33;
/*     */   private static final int TDS_OPT_ANSINULL = 34;
/*     */   private static final int TDS_OPT_QUOTED_IDENT = 35;
/*     */   private static final int TDS_OPT_ARITHIGNOREOFF = 36;
/*     */   private static final int TDS_OPT_ARITHABORTOFF = 37;
/*     */   private static final int TDS_OPT_TRUNCABORT = 38;
/*     */   private static final int TDS_OPT_LOB_LOCATOR = 49;
/*     */   private static final int TDS_OPT_LOB_LOCATORFETCHSIZE = 50;
/*     */   private int _length;
/*     */   private int _command;
/*     */   private int _option;
/*     */   private int _argLength;
/*     */   private int _intArg;
/*     */   private String _stringArg;
/*     */ 
/*     */   public SrvOptionCmdToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  81 */     this._length = paramTdsInputStream.readShort();
/*  82 */     this._command = paramTdsInputStream.readUnsignedByte();
/*  83 */     this._option = paramTdsInputStream.readUnsignedByte();
/*  84 */     this._argLength = paramTdsInputStream.readUnsignedByte();
/*  85 */     readOptionArg(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   private void readOptionArg(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  93 */     switch (this._option)
/*     */     {
/*     */     case 1:
/*     */     case 7:
/*     */     case 8:
/*  99 */       if (!tokenHasArg(1, paramTdsInputStream))
/*     */         return;
/* 101 */       this._intArg = paramTdsInputStream.readUnsignedByte(); break;
/*     */     case 3:
/*     */     case 4:
/*     */     case 13:
/*     */     case 14:
/*     */     case 18:
/*     */     case 20:
/*     */     case 21:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/*     */     case 26:
/*     */     case 27:
/*     */     case 28:
/*     */     case 34:
/*     */     case 35:
/*     */     case 38:
/*     */     case 49:
/* 123 */       if (!tokenHasArg(1, paramTdsInputStream))
/*     */         return;
/* 125 */       this._intArg = paramTdsInputStream.readUnsignedByte(); break;
/*     */     case 2:
/*     */     case 5:
/*     */     case 15:
/*     */     case 17:
/*     */     case 36:
/*     */     case 37:
/*     */     case 50:
/* 137 */       if (!tokenHasArg(4, paramTdsInputStream))
/*     */         return;
/* 139 */       this._intArg = paramTdsInputStream.readInt(); break;
/*     */     case 6:
/*     */     case 9:
/*     */     case 10:
/*     */     case 29:
/*     */     case 30:
/*     */     case 31:
/*     */     case 32:
/*     */     case 33:
/* 152 */       if (!tokenHasArg(this._argLength, paramTdsInputStream)) return; this._stringArg = paramTdsInputStream.readString(this._argLength);
/*     */     case 11:
/*     */     case 12:
/*     */     case 16:
/*     */     case 19:
/*     */     case 22:
/*     */     case 39:
/*     */     case 40:
/*     */     case 41:
/*     */     case 42:
/*     */     case 43:
/*     */     case 44:
/*     */     case 45:
/*     */     case 46:
/*     */     case 47:
/*     */     case 48: } 
/*     */   }
/* 168 */   private boolean tokenHasArg(int paramInt, TdsInputStream paramTdsInputStream) throws IOException { int i = this._length - 3;
/*     */ 
/* 170 */     if (i < paramInt)
/*     */     {
/* 173 */       for (int j = 0; j < i; ++j)
/*     */       {
/* 176 */         paramTdsInputStream.read();
/*     */       }
/* 178 */       return false;
/*     */     }
/* 180 */     return true; }
/*     */ 
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 195 */     DumpInfo localDumpInfo = null;
/* 196 */     if (paramDumpFilter.includesToken(166))
/*     */     {
/* 198 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 199 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 201 */         localDumpInfo.addInfo("Token", 1, "OPTIONCMD Token (0x" + HexConverts.hexConvert(166, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 207 */         localDumpInfo.addInfo("Token", 1, "OPTIONCMD Token");
/*     */       }
/* 209 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 211 */         localDumpInfo.addInt("Length", 1, this._length);
/*     */       }
/*     */ 
/* 214 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 216 */         String[] arrayOfString1 = { "<unrecognized>", "TDS_OPT_SET", "TDS_OPT_DEFAULT", "TDS_OPT_LIST", "TDS_OPT_INFO" };
/*     */ 
/* 222 */         localDumpInfo.addField("Command", 1, this._command, arrayOfString1);
/*     */ 
/* 224 */         String[] arrayOfString2 = { "TDS_OPT_UNUSED", "TDS_OPT_DATEFIRST", "TDS_OPT_TEXTSIZE", "TDS_OPT_STAT_TIME", "TDS_OPT_STAT_IO", "TDS_OPT_ROWCOUNT", "TDS_OPT_NATLANG", "TDS_OPT_DATEFORMAT", "TDS_OPT_ISOLATION", "TDS_OPT_AUTHON", "TDS_OPT_CHARSET", "<unrecognized>", "<unrecognized>", "TDS_OPT_SHOWPLAN", "TDS_OPT_NOEXEC", "TDS_OPT_ARITHIGNOREON", "<unrecognized>", "TDS_OPT_ARITHABORTON", "TDS_OPT_PARSEONLY", "<unrecognized>", "TDS_OPT_GETDATA", "TDS_OPT_NOCOUNT", "<unrecognized>", "TDS_OPT_FORCEPLAN", "TDS_OPT_FORMATONLY", "TDS_OPT_CHAINXACTS", "TDS_OPT_CURCLOSEONXACT", "TDS_OPT_FIPSFLAG", "TDS_OPT_RESTREES", "TDS_OPT_IDENTITYON", "TDS_OPT_CURREAD", "TDS_OPT_CURWRITE", "TDS_OPT_IDENTITYOF", "TDS_OPT_AUTHOFF", "TDS_OPT_ANSINULL", "TDS_OPT_QUOTED_IDENT", "TDS_OPT_ARITHIGNOREOFF", "TDS_OPT_ARITHABORTOFF", "TDS_OPT_TRUNCABORT", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "TDS_OPT_LOB_LOCATOR", "TDS_OPT_LOBLOCATORFETCHSIZE" };
/*     */ 
/* 279 */         localDumpInfo.addField("Option", 1, this._option, arrayOfString2);
/* 280 */         localDumpInfo.addInt("Arg Length", 1, this._argLength);
/* 281 */         dumpOptionArg(localDumpInfo);
/*     */       }
/*     */     }
/* 284 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 292 */     return 166;
/*     */   }
/*     */ 
/*     */   private void dumpOptionArg(DumpInfo paramDumpInfo)
/*     */   {
/* 301 */     switch (this._option)
/*     */     {
/*     */     case 1:
/* 305 */       String[] arrayOfString1 = { "<unrecognized>", "TDS_OPT_MONDAY", "TDS_OPT_TUESDAY", "TDS_OPT_WEDNESDAY", "TDS_OPT_THURSDAY", "TDS_OPT_FRIDAY", "TDS_OPT_SATURDAY", "TDS_OPT_SUNDAY" };
/*     */ 
/* 313 */       paramDumpInfo.addField("Option Arg", 1, this._intArg, arrayOfString1);
/* 314 */       break;
/*     */     case 7:
/* 317 */       String[] arrayOfString2 = { "<unrecognized>", "TDS_OPT_FMTMDY", "TDS_OPT_FMTDMY", "TDS_OPT_FMTYMD", "TDS_OPT_FMTYDM", "TDS_OPT_FMTMYD", "TDS_OPT_FMTDYM" };
/*     */ 
/* 325 */       paramDumpInfo.addField("Option Arg", 1, this._intArg, arrayOfString2);
/* 326 */       break;
/*     */     case 8:
/* 329 */       paramDumpInfo.addInt("Option Arg", 1, this._intArg);
/* 330 */       break;
/*     */     case 3:
/*     */     case 4:
/*     */     case 13:
/*     */     case 14:
/*     */     case 18:
/*     */     case 20:
/*     */     case 21:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/*     */     case 26:
/*     */     case 27:
/*     */     case 28:
/*     */     case 34:
/*     */     case 35:
/*     */     case 38:
/*     */     case 49:
/* 350 */       if (this._intArg == 1)
/*     */       {
/* 352 */         paramDumpInfo.addText("Option Arg", 4, "true"); return;
/*     */       }
/*     */ 
/* 356 */       paramDumpInfo.addText("Option Arg", 5, "false");
/*     */ 
/* 358 */       break;
/*     */     case 2:
/*     */     case 5:
/*     */     case 50:
/* 364 */       paramDumpInfo.addInt("Option Arg", 4, this._intArg);
/* 365 */       break;
/*     */     case 15:
/*     */     case 17:
/*     */     case 36:
/*     */     case 37:
/* 371 */       String[] arrayOfString3 = { "<unrecognized>", "TDS_OPT_ARITHOVERFLOW", "TDS_OPT_NUMERICTRUNC" };
/*     */ 
/* 377 */       paramDumpInfo.addField("Option Arg", 4, this._intArg, arrayOfString3);
/* 378 */       break;
/*     */     case 6:
/*     */     case 9:
/*     */     case 10:
/*     */     case 29:
/*     */     case 30:
/*     */     case 31:
/*     */     case 32:
/*     */     case 33:
/* 388 */       paramDumpInfo.addText("Option Arg", 1, this._stringArg);
/*     */     case 11:
/*     */     case 12:
/*     */     case 16:
/*     */     case 19:
/*     */     case 22:
/*     */     case 39:
/*     */     case 40:
/*     */     case 41:
/*     */     case 42:
/*     */     case 43:
/*     */     case 44:
/*     */     case 45:
/*     */     case 46:
/*     */     case 47:
/*     */     case 48:
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvOptionCmdToken
 * JD-Core Version:    0.5.4
 */