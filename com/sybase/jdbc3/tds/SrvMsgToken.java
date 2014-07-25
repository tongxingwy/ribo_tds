/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvMsgToken extends MsgToken
/*     */   implements Dumpable
/*     */ {
/*     */   public SrvMsgToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  35 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public boolean hasParameters()
/*     */   {
/*  45 */     return (this._status & 0x1) != 0;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  59 */     DumpInfo localDumpInfo = null;
/*  60 */     if (paramDumpFilter.includesToken(101))
/*     */     {
/*  62 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  63 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  65 */         localDumpInfo.addInfo("Token", 1, "MSG Token (0x" + HexConverts.hexConvert(101, 1) + ");");
/*     */       }
/*     */       else
/*     */       {
/*  70 */         localDumpInfo.addInfo("Token", 1, "MSG Token");
/*     */       }
/*  72 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/*  74 */         localDumpInfo.addInt("Length", 1, this._length);
/*     */       }
/*  76 */       String[] arrayOfString1 = { "NO_ARGS", "MSG_HASARGS" };
/*     */ 
/*  81 */       localDumpInfo.addBitfield("Status", 1, this._status, arrayOfString1);
/*  82 */       String[] arrayOfString2 = { "<unrecognized>", "MSG_SEC_ENCRYPT", "MSG_SEC_LOGPWD", "MSG_SEC_REMPWD", "MSG_SEC_CHALLENGE", "MSG_SEC_RESPONSE", "MSG_SEC_GETLABEL", "MSG_SEC_LABEL", "MSG_SQL_TBLNAME", "MSG_GW_RESERVED", "MSG_OMNI_CAPABILITIES", "MSG_SEC_OPAQUE", "MSG_HAFAILOVER", "MSG_EMPTY", "MSG_SEC_ENCRYPT2", "MSG_SEC_LOGPWD2", "MSG_SEC_SUP_CIPHER", "MSG_MIG_REQ", "MSG_MIG_SYNC", "MSG_MIG_CONT", "MSG_MIG_IGN", "MSG_MIG_FAIL", "MSG_SEC_REMPWD2", "MSG_MIG_RESUME", "MSG_HELLO", "MSG_LOGINPARAMS", "MSG_GRID_MIGREQ", "MSG_GRID_QUIESCE", "MSG_GRID_UNQUIESCE", "MSG_GRID_EVENT", "MSG_SEC_ENCRYPT3", "MSG_SEC_LOGPWD3", "MSG_SEC_REMPWD3" };
/*     */ 
/*  97 */       localDumpInfo.addField("MsgId", 2, this._msgID, arrayOfString2);
/*     */     }
/*  99 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 107 */     return 101;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvMsgToken
 * JD-Core Version:    0.5.4
 */