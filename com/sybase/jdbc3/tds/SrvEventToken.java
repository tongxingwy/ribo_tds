/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvEventToken extends EventToken
/*     */   implements Dumpable
/*     */ {
/*     */   protected int _totalLen;
/*     */   protected int _nameLen;
/*     */ 
/*     */   public SrvEventToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  48 */       this._totalLen = paramTdsInputStream.readUnsignedShort();
/*  49 */       this._nameLen = paramTdsInputStream.readUnsignedByte();
/*  50 */       this._name = paramTdsInputStream.readString(this._nameLen);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*  56 */       readSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SrvEventToken(String paramString)
/*     */   {
/*  68 */     super(paramString);
/*  69 */     this._nameLen = paramString.length();
/*  70 */     this._totalLen = (this._nameLen + 1);
/*     */   }
/*     */ 
/*     */   protected void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*  82 */     paramTdsOutputStream.writeByte(162);
/*  83 */     byte[] arrayOfByte = paramTdsOutputStream.stringToByte(this._name);
/*  84 */     paramTdsOutputStream.writeShort(arrayOfByte.length + 1);
/*  85 */     paramTdsOutputStream.writeByte(arrayOfByte.length);
/*  86 */     paramTdsOutputStream.write(arrayOfByte);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 100 */     DumpInfo localDumpInfo = null;
/* 101 */     if (paramDumpFilter.includesToken(162))
/*     */     {
/* 103 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 104 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 106 */         localDumpInfo.addInfo("Token", 1, "EVENT_NOTICE Token (0x" + HexConverts.hexConvert(162, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 112 */         localDumpInfo.addInfo("Token", 1, "EVENT_NOTICE Token");
/*     */       }
/*     */ 
/* 115 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 117 */         localDumpInfo.addInt("Length", 2, this._totalLen);
/*     */       }
/*     */ 
/* 120 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 122 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 124 */           localDumpInfo.addInt("Name Length", 2, this._nameLen);
/*     */         }
/* 126 */         localDumpInfo.addText("Name", this._nameLen, this._name);
/*     */       }
/*     */     }
/* 129 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 137 */     return 162;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvEventToken
 * JD-Core Version:    0.5.4
 */