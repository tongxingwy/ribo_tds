/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvErrorToken extends Token
/*     */   implements Dumpable
/*     */ {
/*     */   protected int _totalLen;
/*     */   protected int _msgNumber;
/*     */   protected int _state;
/*     */   protected int _class;
/*     */   protected int _msgLen;
/*     */   protected String _msg;
/*     */   protected int _serverNameLen;
/*     */   protected String _serverName;
/*     */   protected int _procNameLen;
/*     */   protected String _procName;
/*     */   protected int _lineNum;
/*     */ 
/*     */   public SrvErrorToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  55 */     this._totalLen = paramTdsInputStream.readShort();
/*  56 */     this._msgNumber = paramTdsInputStream.readInt();
/*  57 */     this._state = paramTdsInputStream.readUnsignedByte();
/*  58 */     this._class = paramTdsInputStream.readUnsignedByte();
/*  59 */     this._msgLen = paramTdsInputStream.readShort();
/*  60 */     this._msg = paramTdsInputStream.readString(this._msgLen);
/*  61 */     this._serverNameLen = paramTdsInputStream.readUnsignedByte();
/*  62 */     this._serverName = paramTdsInputStream.readString(this._serverNameLen);
/*  63 */     this._procNameLen = paramTdsInputStream.readUnsignedByte();
/*  64 */     this._procName = paramTdsInputStream.readString(this._procNameLen);
/*  65 */     this._lineNum = paramTdsInputStream.readShort();
/*     */   }
/*     */ 
/*     */   public SrvErrorToken(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, int paramInt4)
/*     */   {
/*  88 */     this._msgNumber = paramInt1;
/*  89 */     this._state = paramInt2;
/*  90 */     this._class = paramInt3;
/*     */ 
/*  92 */     if (paramString1 == null)
/*     */     {
/*  94 */       this._msg = new String();
/*     */     }
/*     */     else
/*     */     {
/*  98 */       this._msg = paramString1;
/*     */     }
/* 100 */     this._msgLen = this._msg.length();
/* 101 */     if (paramString2 == null)
/*     */     {
/* 103 */       this._serverName = new String();
/*     */     }
/*     */     else
/*     */     {
/* 107 */       this._serverName = paramString2;
/*     */     }
/* 109 */     this._serverNameLen = this._serverName.length();
/* 110 */     if (paramString3 == null)
/*     */     {
/* 112 */       this._procName = new String();
/*     */     }
/*     */     else
/*     */     {
/* 116 */       this._procName = paramString3;
/*     */     }
/* 118 */     this._procNameLen = this._procName.length();
/* 119 */     this._lineNum = paramInt4;
/* 120 */     this._totalLen = (8 + this._msgLen + 1 + this._serverNameLen + 1 + this._procNameLen + 2);
/*     */   }
/*     */ 
/*     */   public SrvErrorToken(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2)
/*     */   {
/* 142 */     this(paramInt1, 0, 11, paramString1, paramString2, paramString3, paramInt2);
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 151 */     paramTdsOutputStream.writeByte(170);
/*     */ 
/* 154 */     byte[] arrayOfByte1 = paramTdsOutputStream.stringToByte(this._msg);
/* 155 */     byte[] arrayOfByte2 = paramTdsOutputStream.stringToByte(this._serverName);
/* 156 */     byte[] arrayOfByte3 = paramTdsOutputStream.stringToByte(this._procName);
/*     */ 
/* 158 */     paramTdsOutputStream.writeShort(this._totalLen);
/* 159 */     paramTdsOutputStream.writeInt(this._msgNumber);
/* 160 */     paramTdsOutputStream.writeByte(this._state);
/* 161 */     paramTdsOutputStream.writeByte(this._class);
/* 162 */     paramTdsOutputStream.writeShort(arrayOfByte1.length);
/* 163 */     paramTdsOutputStream.write(arrayOfByte1);
/* 164 */     if (arrayOfByte2.length == 0)
/*     */     {
/* 166 */       paramTdsOutputStream.writeByte(0);
/*     */     }
/*     */     else
/*     */     {
/* 170 */       paramTdsOutputStream.writeByte(arrayOfByte2.length);
/* 171 */       paramTdsOutputStream.write(arrayOfByte2);
/*     */     }
/*     */ 
/* 174 */     if (arrayOfByte3.length == 0)
/*     */     {
/* 176 */       paramTdsOutputStream.writeByte(0);
/*     */     }
/*     */     else
/*     */     {
/* 180 */       paramTdsOutputStream.writeByte(arrayOfByte3.length);
/* 181 */       paramTdsOutputStream.write(arrayOfByte3);
/*     */     }
/* 183 */     paramTdsOutputStream.writeShort(this._lineNum);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 192 */     StringBuffer localStringBuffer = new StringBuffer(super.toString());
/* 193 */     localStringBuffer.append(": ");
/* 194 */     localStringBuffer.append(this._msgNumber);
/*     */ 
/* 210 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 224 */     DumpInfo localDumpInfo = null;
/* 225 */     if (paramDumpFilter.includesToken(170))
/*     */     {
/* 227 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 228 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 230 */         localDumpInfo.addInfo("Token", 1, "ERROR Token (0x" + HexConverts.hexConvert(170, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 236 */         localDumpInfo.addInfo("Token", 1, "ERROR Token");
/*     */       }
/*     */ 
/* 239 */       localDumpInfo.addInfo(dumpDetails(paramDumpFilter));
/*     */     }
/* 241 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 249 */     return 170;
/*     */   }
/*     */ 
/*     */   protected DumpInfo dumpDetails(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 260 */     DumpInfo localDumpInfo = null;
/* 261 */     if (paramDumpFilter.includesDetail(1))
/*     */     {
/* 263 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 264 */       localDumpInfo.addInt("Length", 2, this._totalLen);
/*     */     }
/*     */ 
/* 267 */     if (paramDumpFilter.includesDetail(3))
/*     */     {
/* 269 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 270 */       localDumpInfo.addInt("Message Number", 4, this._msgNumber);
/* 271 */       localDumpInfo.addInt("Message State", 1, this._state);
/* 272 */       localDumpInfo.addInt("Message Class", 1, this._class);
/* 273 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 275 */         localDumpInfo.addInt("Message Length", 2, this._msgLen);
/*     */       }
/* 277 */       localDumpInfo.addText("Message Text", this._msgLen, this._msg);
/* 278 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 280 */         localDumpInfo.addInt("Server Name Length", 1, this._serverNameLen);
/*     */       }
/* 282 */       if (this._serverNameLen > 0)
/*     */       {
/* 284 */         localDumpInfo.addText("Server Name", this._serverNameLen, this._serverName);
/*     */       }
/* 286 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 288 */         localDumpInfo.addInt("Stored Proc. Name Length", 1, this._procNameLen);
/*     */       }
/* 290 */       if (this._procNameLen > 0)
/*     */       {
/* 292 */         localDumpInfo.addText("Stored Proc. Name", this._procNameLen, this._procName);
/*     */       }
/* 294 */       localDumpInfo.addInt("Line Number", 2, this._lineNum);
/*     */     }
/* 296 */     return localDumpInfo;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvErrorToken
 * JD-Core Version:    0.5.4
 */