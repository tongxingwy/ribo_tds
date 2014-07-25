/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvReturnStatusToken extends Token
/*     */   implements Dumpable
/*     */ {
/*  32 */   int _status = -1;
/*     */ 
/*     */   public SrvReturnStatusToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  41 */     this._status = paramTdsInputStream.readInt();
/*     */   }
/*     */ 
/*     */   public SrvReturnStatusToken(int paramInt)
/*     */   {
/*  51 */     this._status = paramInt;
/*     */   }
/*     */ 
/*     */   public int getStatus()
/*     */   {
/*  61 */     return this._status;
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*  72 */     paramTdsOutputStream.writeByte(121);
/*  73 */     paramTdsOutputStream.writeInt(this._status);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  87 */     DumpInfo localDumpInfo = null;
/*  88 */     if (paramDumpFilter.includesToken(121))
/*     */     {
/*  90 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  91 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  93 */         localDumpInfo.addInfo("Token", 1, "RETURNSTATUS Token (0x" + HexConverts.hexConvert(121, 1) + "); fixed length.");
/*     */       }
/*     */       else
/*     */       {
/*  99 */         localDumpInfo.addInfo("Token", 1, "RETURNSTATUS Token");
/*     */       }
/*     */ 
/* 102 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 104 */         localDumpInfo.addInt("Length", 0, 4L);
/*     */       }
/*     */ 
/* 107 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 109 */         localDumpInfo.addHex("Status", 4, this._status);
/*     */       }
/*     */     }
/* 112 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 121 */     return 121;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvReturnStatusToken
 * JD-Core Version:    0.5.4
 */