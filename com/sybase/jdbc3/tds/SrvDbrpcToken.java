/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvDbrpcToken extends DbrpcToken
/*     */   implements Dumpable
/*     */ {
/*     */   protected int _totalLen;
/*     */   protected int _rpcnameLen;
/*     */   protected boolean _hasWidetableParams;
/*     */ 
/*     */   public SrvDbrpcToken(String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/*  45 */     this._rpcname = paramString;
/*  46 */     this._rpcnameLen = paramString.length();
/*  47 */     this._options = paramInt;
/*  48 */     this._totalLen = (this._rpcnameLen + 3);
/*     */   }
/*     */ 
/*     */   public SrvDbrpcToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  61 */     this._totalLen = paramTdsInputStream.readShort();
/*  62 */     this._rpcnameLen = paramTdsInputStream.readByte();
/*  63 */     this._rpcname = paramTdsInputStream.readString(this._rpcnameLen).trim();
/*  64 */     this._options = paramTdsInputStream.readShort();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  73 */     return this._rpcname;
/*     */   }
/*     */ 
/*     */   public boolean hasParams()
/*     */   {
/*  82 */     return (this._options & 0x2) != 0;
/*     */   }
/*     */ 
/*     */   public boolean includesWidetableParams()
/*     */   {
/*  92 */     return this._hasWidetableParams;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 106 */     DumpInfo localDumpInfo = null;
/* 107 */     if (paramDumpFilter.includesToken(230))
/*     */     {
/* 109 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 110 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 112 */         localDumpInfo.addInfo("Token", 1, "DBRPC Token (0x" + HexConverts.hexConvert(230, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 118 */         localDumpInfo.addInfo("Token", 1, "DBRPC Token");
/*     */       }
/*     */ 
/* 121 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 123 */         localDumpInfo.addInt("Length", 2, this._totalLen);
/*     */       }
/*     */ 
/* 126 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(8)))
/*     */       {
/* 129 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 131 */           localDumpInfo.addInt("Name Length", 1, this._rpcnameLen);
/*     */         }
/* 133 */         localDumpInfo.addText("Name", this._rpcnameLen, this._rpcname);
/* 134 */         String[] arrayOfString = { "RPC_UNUSED", "RPC_RECOMPILE", "RPC_PARAMS" };
/*     */ 
/* 139 */         localDumpInfo.addField("Options", 2, this._options, arrayOfString);
/*     */       }
/*     */     }
/* 142 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 149 */     return 230;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDbrpcToken
 * JD-Core Version:    0.5.4
 */