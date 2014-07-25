/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvLoginAckToken extends LoginAckToken
/*     */   implements Dumpable
/*     */ {
/*     */   private static final int PROGNAME_MAXLEN = 255;
/*     */   protected int _totalLen;
/*     */   protected int _progNameLen;
/*  40 */   private static final byte[] PROGVERSION = { 1, 1, 0, 0 };
/*     */ 
/*     */   public SrvLoginAckToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  53 */     this._totalLen = paramTdsInputStream.readShort();
/*     */ 
/*  56 */     this._status = paramTdsInputStream.readUnsignedByte();
/*     */ 
/*  59 */     this._tdsVers = new byte[4];
/*  60 */     paramTdsInputStream.read(this._tdsVers);
/*     */ 
/*  63 */     this._progNameLen = paramTdsInputStream.readUnsignedByte();
/*  64 */     this._progName = paramTdsInputStream.readString(this._progNameLen);
/*     */ 
/*  67 */     this._progVers = new byte[4];
/*  68 */     paramTdsInputStream.read(this._progVers);
/*     */   }
/*     */ 
/*     */   public SrvLoginAckToken(int paramInt, String paramString, byte[] paramArrayOfByte)
/*     */   {
/*  85 */     this._status = paramInt;
/*     */ 
/*  88 */     int i = paramString.length();
/*  89 */     if (i > 255)
/*     */     {
/*  91 */       i = 255;
/*     */     }
/*     */ 
/*  94 */     this._progName = paramString.substring(0, i);
/*  95 */     this._progNameLen = i;
/*     */ 
/*  97 */     this._progVers = paramArrayOfByte;
/*  98 */     this._totalLen = (6 + this._progNameLen + 4);
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 109 */     paramTdsOutputStream.writeByte(173);
/*     */ 
/* 112 */     byte[] arrayOfByte = paramTdsOutputStream.stringToByte(this._progName);
/* 113 */     int i = arrayOfByte.length;
/*     */ 
/* 118 */     if (arrayOfByte.length > 255)
/*     */     {
/* 120 */       i = 255;
/*     */     }
/*     */ 
/* 123 */     paramTdsOutputStream.writeShort(1 + LoginToken.TDSVERSION.length + 1 + i + PROGVERSION.length);
/*     */ 
/* 129 */     paramTdsOutputStream.writeByte(this._status);
/* 130 */     paramTdsOutputStream.write(LoginToken.TDSVERSION);
/* 131 */     paramTdsOutputStream.writeByte(i);
/* 132 */     paramTdsOutputStream.write(arrayOfByte);
/* 133 */     paramTdsOutputStream.write(PROGVERSION);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 148 */     DumpInfo localDumpInfo = null;
/* 149 */     if (paramDumpFilter.includesToken(173))
/*     */     {
/* 151 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 152 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 154 */         localDumpInfo.addInfo("Token", 1, "LOGINACK Token (0x" + HexConverts.hexConvert(173, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 160 */         localDumpInfo.addInfo("Token", 1, "LOGINACK Token");
/*     */       }
/*     */ 
/* 163 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 165 */         localDumpInfo.addInt("Length", 2, this._totalLen);
/*     */       }
/*     */ 
/* 168 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 170 */         String[] arrayOfString = { "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "<unrecognized>", "LOG_SUCCEED", "LOG_FAIL", "LOG_NEGOTIATE" };
/*     */ 
/* 177 */         if ((this._status & 0x80) != 0)
/*     */         {
/* 179 */           this._status ^= 128;
/* 180 */           for (int i = 5; i <= 7; ++i)
/*     */           {
/* 182 */             arrayOfString[i] = ("LOG_SECSESS_ACK (0x80) + " + arrayOfString[i]);
/*     */           }
/*     */         }
/* 185 */         localDumpInfo.addField("Status", 1, this._status, arrayOfString);
/* 186 */         localDumpInfo.addInfo("TDS Version", 4, makeVersionString(this._tdsVers));
/* 187 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 189 */           localDumpInfo.addInt("Program Name Length", 1, this._progNameLen);
/*     */         }
/* 191 */         localDumpInfo.addText("Program Name", this._progName.length(), this._progName);
/* 192 */         localDumpInfo.addInfo("Program Version", 4, makeVersionString(this._progVers));
/*     */       }
/*     */     }
/* 195 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 203 */     return 173;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvLoginAckToken
 * JD-Core Version:    0.5.4
 */