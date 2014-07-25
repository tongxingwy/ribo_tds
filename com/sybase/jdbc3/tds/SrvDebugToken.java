/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvDebugToken extends Token
/*     */   implements Dumpable
/*     */ {
/*     */   protected int _totalLen;
/*     */   protected int _mysteryData;
/*     */   protected byte[] _serverData;
/*     */ 
/*     */   public SrvDebugToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  45 */       this._totalLen = paramTdsInputStream.readInt();
/*  46 */       this._mysteryData = paramTdsInputStream.readInt();
/*  47 */       paramTdsInputStream.read(this._serverData, 0, 2048);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*  51 */       readSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SrvDebugToken(int paramInt, byte[] paramArrayOfByte)
/*     */   {
/*  59 */     this._mysteryData = paramInt;
/*  60 */     this._serverData = paramArrayOfByte;
/*  61 */     this._totalLen = (8 + this._serverData.length);
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*  69 */     paramTdsOutputStream.writeByte(96);
/*  70 */     paramTdsOutputStream.writeInt(this._totalLen);
/*  71 */     paramTdsOutputStream.writeInt(this._mysteryData);
/*  72 */     paramTdsOutputStream.write(this._serverData);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  87 */     DumpInfo localDumpInfo = null;
/*  88 */     if (paramDumpFilter.includesToken(96))
/*     */     {
/*  90 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  91 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  93 */         localDumpInfo.addInfo("Token", 1, "DEBUG_CMD Token (0x" + HexConverts.hexConvert(96, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 103 */         localDumpInfo.addInfo("Token", 1, "DEBUG_CMD Token");
/*     */       }
/*     */ 
/* 106 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 108 */         localDumpInfo.addInt("Length", 4, this._totalLen);
/*     */       }
/*     */ 
/* 111 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 114 */         localDumpInfo.addHex("Mystery Data", 4, this._mysteryData);
/* 115 */         localDumpInfo.addHex("Server Data", this._serverData.length, this._serverData);
/*     */       }
/*     */     }
/* 118 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 126 */     return 96;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDebugToken
 * JD-Core Version:    0.5.4
 */