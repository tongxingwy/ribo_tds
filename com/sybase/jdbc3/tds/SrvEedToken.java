/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvEedToken extends EedToken
/*     */   implements Dumpable
/*     */ {
/*     */   protected int _totalLen;
/*     */   protected int _sqlStateLen;
/*     */   protected int _msgLen;
/*     */   protected int _serverNameLen;
/*     */   protected int _procNameLen;
/*     */ 
/*     */   public SrvEedToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  52 */       this._totalLen = paramTdsInputStream.readShort();
/*  53 */       this._msgNumber = paramTdsInputStream.readInt();
/*  54 */       this._state = paramTdsInputStream.readUnsignedByte();
/*  55 */       this._class = paramTdsInputStream.readUnsignedByte();
/*  56 */       this._sqlStateLen = paramTdsInputStream.readUnsignedByte();
/*  57 */       this._sqlState = paramTdsInputStream.readString(this._sqlStateLen);
/*  58 */       this._status = paramTdsInputStream.readUnsignedByte();
/*  59 */       this._tranState = paramTdsInputStream.readShort();
/*  60 */       this._msgLen = paramTdsInputStream.readShort();
/*  61 */       this._msg = paramTdsInputStream.readString(this._msgLen);
/*  62 */       this._serverNameLen = paramTdsInputStream.readUnsignedByte();
/*  63 */       this._serverName = paramTdsInputStream.readString(this._serverNameLen);
/*  64 */       this._procNameLen = paramTdsInputStream.readUnsignedByte();
/*  65 */       this._procName = paramTdsInputStream.readString(this._procNameLen);
/*  66 */       this._lineNum = paramTdsInputStream.readShort();
/*  67 */       this._params = null;
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*  71 */       readSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SrvEedToken(int paramInt1, int paramInt2, int paramInt3, String paramString1, int paramInt4, String paramString2, String paramString3, String paramString4, int paramInt5)
/*     */   {
/*  95 */     this._msgNumber = paramInt1;
/*  96 */     this._state = paramInt2;
/*  97 */     this._class = paramInt3;
/*  98 */     if (paramString1 == null)
/*     */     {
/* 100 */       this._sqlState = new String();
/*     */     }
/*     */     else
/*     */     {
/* 104 */       this._sqlState = paramString1;
/*     */     }
/* 106 */     this._sqlStateLen = this._sqlState.length();
/* 107 */     this._status = 0;
/* 108 */     this._tranState = paramInt4;
/*     */ 
/* 110 */     if (paramString2 == null)
/*     */     {
/* 112 */       this._msg = new String();
/*     */     }
/*     */     else
/*     */     {
/* 116 */       this._msg = paramString2;
/*     */     }
/* 118 */     this._msgLen = this._msg.length();
/* 119 */     if (paramString3 == null)
/*     */     {
/* 121 */       this._serverName = new String();
/*     */     }
/*     */     else
/*     */     {
/* 125 */       this._serverName = paramString3;
/*     */     }
/* 127 */     this._serverNameLen = this._serverName.length();
/* 128 */     if (paramString4 == null)
/*     */     {
/* 130 */       this._procName = new String();
/*     */     }
/*     */     else
/*     */     {
/* 134 */       this._procName = paramString4;
/*     */     }
/* 136 */     this._procNameLen = this._procName.length();
/* 137 */     this._lineNum = paramInt5;
/* 138 */     this._params = null;
/* 139 */     this._totalLen = (7 + this._sqlStateLen + 1 + 2 + 2 + this._msgLen + 1 + this._serverNameLen + 1 + this._procNameLen + 2);
/*     */   }
/*     */ 
/*     */   public SrvEedToken(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2)
/*     */   {
/* 163 */     this(paramInt1, 0, 11, new String("UNKWN"), 0, paramString1, paramString2, paramString3, paramInt2);
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 173 */     paramTdsOutputStream.writeByte(229);
/*     */ 
/* 176 */     byte[] arrayOfByte1 = paramTdsOutputStream.stringToByte(this._sqlState);
/* 177 */     byte[] arrayOfByte2 = paramTdsOutputStream.stringToByte(this._msg);
/* 178 */     byte[] arrayOfByte3 = paramTdsOutputStream.stringToByte(this._serverName);
/* 179 */     byte[] arrayOfByte4 = paramTdsOutputStream.stringToByte(this._procName);
/*     */ 
/* 181 */     paramTdsOutputStream.writeShort(7 + arrayOfByte1.length + 1 + 2 + 2 + arrayOfByte2.length + 1 + arrayOfByte3.length + 1 + arrayOfByte4.length + 2);
/*     */ 
/* 191 */     paramTdsOutputStream.writeInt(this._msgNumber);
/* 192 */     paramTdsOutputStream.writeByte(this._state);
/* 193 */     paramTdsOutputStream.writeByte(this._class);
/*     */ 
/* 195 */     paramTdsOutputStream.writeByte(arrayOfByte1.length);
/* 196 */     paramTdsOutputStream.write(arrayOfByte1);
/*     */ 
/* 198 */     paramTdsOutputStream.writeByte(this._status);
/* 199 */     paramTdsOutputStream.writeShort(this._tranState);
/*     */ 
/* 201 */     paramTdsOutputStream.writeShort(arrayOfByte2.length);
/* 202 */     paramTdsOutputStream.write(arrayOfByte2);
/*     */ 
/* 205 */     if (arrayOfByte3.length == 0)
/*     */     {
/* 207 */       paramTdsOutputStream.writeByte(0);
/*     */     }
/*     */     else
/*     */     {
/* 211 */       paramTdsOutputStream.writeByte(arrayOfByte3.length);
/* 212 */       paramTdsOutputStream.write(arrayOfByte3);
/*     */     }
/*     */ 
/* 215 */     if (arrayOfByte4.length == 0)
/*     */     {
/* 217 */       paramTdsOutputStream.writeByte(0);
/*     */     }
/*     */     else
/*     */     {
/* 221 */       paramTdsOutputStream.writeByte(arrayOfByte4.length);
/* 222 */       paramTdsOutputStream.write(arrayOfByte4);
/*     */     }
/* 224 */     paramTdsOutputStream.writeShort(this._lineNum);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 238 */     DumpInfo localDumpInfo = null;
/* 239 */     if (paramDumpFilter.includesToken(229))
/*     */     {
/* 241 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 242 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 244 */         localDumpInfo.addInfo("Token", 1, "EED Token (0x" + HexConverts.hexConvert(229, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 250 */         localDumpInfo.addInfo("Token", 1, "EED Token");
/*     */       }
/*     */ 
/* 253 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 255 */         localDumpInfo.addInt("Length", 2, this._totalLen);
/*     */       }
/*     */ 
/* 258 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(10)))
/*     */       {
/* 262 */         localDumpInfo.addInt("Message Number", 4, this._msgNumber);
/* 263 */         localDumpInfo.addInt("Message State", 1, this._state);
/* 264 */         localDumpInfo.addInt("Message Class", 1, this._class);
/* 265 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 267 */           localDumpInfo.addInt("SQL State Length", 1, this._sqlStateLen);
/*     */         }
/* 269 */         if (this._sqlStateLen > 0)
/*     */         {
/* 271 */           localDumpInfo.addText("SQL State", this._sqlStateLen, this._sqlState);
/*     */         }
/* 273 */         String[] arrayOfString = { "NO_EED", "EED_FOLLOWS", "EED_INFO" };
/*     */ 
/* 278 */         localDumpInfo.addBitfield("Status", 1, this._status, arrayOfString);
/* 279 */         localDumpInfo.addField("Transaction State", 2, this._tranState, SrvDoneToken.TRANSTATE_NAMES);
/*     */ 
/* 281 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 283 */           localDumpInfo.addInt("Message Length", 2, this._msgLen);
/*     */         }
/*     */ 
/* 286 */         if (this._msgLen > 0)
/*     */         {
/* 288 */           localDumpInfo.addText("Message Text", this._msgLen, this._msg);
/*     */         }
/*     */ 
/* 291 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 293 */           localDumpInfo.addInt("Server Name Length", 1, this._serverNameLen);
/*     */         }
/* 295 */         if (this._serverNameLen > 0)
/*     */         {
/* 297 */           localDumpInfo.addText("Server Name", this._serverNameLen, this._serverName);
/*     */         }
/* 299 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 301 */           localDumpInfo.addInt("Stored Proc. Name Length", 1, this._procNameLen);
/*     */         }
/* 303 */         if (this._procNameLen > 0)
/*     */         {
/* 305 */           localDumpInfo.addText("Stored Proc. Name", this._procNameLen, this._procName);
/*     */         }
/* 307 */         localDumpInfo.addInt("Line Number", 2, this._lineNum);
/*     */       }
/*     */     }
/* 310 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 318 */     return 229;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvEedToken
 * JD-Core Version:    0.5.4
 */