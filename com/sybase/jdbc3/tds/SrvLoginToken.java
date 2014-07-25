/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvLoginToken extends LoginToken
/*     */   implements Dumpable
/*     */ {
/*     */   protected int _hostNameLen;
/*     */   protected int _userLen;
/*     */   protected int _passwordLen;
/*     */   protected int _hostProcLen;
/*     */   protected int _ldate;
/*     */   protected int _lusedb;
/*     */   protected int _linterfacespare;
/*     */   protected int _ltype;
/*     */   protected byte[] _spare;
/*     */   protected int _appNameLen;
/*     */   protected int _serviceNameLen;
/*     */   protected int _remPwLen;
/*     */   protected byte[] _tdsversion;
/*     */   protected int _progNameLen;
/*     */   protected int _lnoshort;
/*     */   protected int _ldate4;
/*     */   protected int _languageLen;
/*     */   protected int _lsetlang;
/*     */   protected byte[] _oldsecure;
/*     */   protected int _lseclogin;
/*     */   protected int _lsecbulk;
/*     */   protected byte[] _secspare;
/*     */   protected int _charsetLen;
/*     */   protected int _lsetcharset;
/*     */   protected int _lchar;
/*     */   protected int _ldmpld;
/*  60 */   int _tdsPacketSize = 0;
/*     */ 
/*  62 */   private byte _packetSizeLength = 3;
/*     */ 
/*  64 */   private String _progName = new String();
/*     */ 
/*     */   public String getHost()
/*     */   {
/*  71 */     return this._hostName;
/*     */   }
/*     */ 
/*     */   public String getUser()
/*     */   {
/*  79 */     return this._user;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/*  88 */     return this._password;
/*     */   }
/*     */ 
/*     */   public String getApplicationName()
/*     */   {
/*  97 */     return this._appName;
/*     */   }
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/* 106 */     return this._serviceName;
/*     */   }
/*     */ 
/*     */   public String getProgramName()
/*     */   {
/* 115 */     return this._progName;
/*     */   }
/*     */ 
/*     */   public String getCharset()
/*     */   {
/* 124 */     return this._charset;
/*     */   }
/*     */ 
/*     */   public String getLocale()
/*     */   {
/* 133 */     return this._language;
/*     */   }
/*     */ 
/*     */   public int getPacketSize()
/*     */   {
/* 142 */     return this._tdsPacketSize;
/*     */   }
/*     */ 
/*     */   public int getHALogin()
/*     */   {
/* 148 */     return this._lhalogin;
/*     */   }
/*     */ 
/*     */   public byte[] getHASessionID()
/*     */   {
/* 154 */     return this._lhasessionid;
/*     */   }
/*     */ 
/*     */   public boolean getBigEndian()
/*     */     throws IOException
/*     */   {
/* 167 */     if ((this._lint2 == 2) && (this._lint4 == 0) && (this._lflt == 4) && (this._lflt4 == 12)) {
/* 168 */       return true;
/*     */     }
/* 170 */     if ((this._lint2 == 3) && (this._lint4 == 1) && (this._lflt == 10) && (this._lflt4 == 13)) {
/* 171 */       return false;
/*     */     }
/* 173 */     throw new IOException("Cannot handle TDS endian order requested by client:" + this._lint2 + "," + this._lint4 + "," + this._lflt + "," + this._lflt4);
/*     */   }
/*     */ 
/*     */   public SrvLoginToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/* 195 */     this._lint2 = -1;
/* 196 */     this._lint4 = -1;
/* 197 */     this._lflt = -1;
/* 198 */     this._lflt4 = -1;
/* 199 */     this._hostName = paramTdsInputStream.readString(30);
/* 200 */     this._hostNameLen = paramTdsInputStream.readUnsignedByte();
/* 201 */     this._hostName = this._hostName.substring(0, this._hostNameLen);
/* 202 */     this._user = paramTdsInputStream.readString(30);
/* 203 */     this._userLen = paramTdsInputStream.readUnsignedByte();
/* 204 */     this._user = this._user.substring(0, this._userLen);
/* 205 */     this._password = paramTdsInputStream.readString(30);
/* 206 */     this._passwordLen = paramTdsInputStream.readUnsignedByte();
/* 207 */     this._password = this._password.substring(0, this._passwordLen);
/* 208 */     this._hostProc = paramTdsInputStream.readString(30);
/* 209 */     this._hostProcLen = paramTdsInputStream.readUnsignedByte();
/* 210 */     this._hostProc = this._hostProc.substring(0, this._hostProcLen);
/*     */ 
/* 212 */     this._lint2 = paramTdsInputStream.readUnsignedByte();
/* 213 */     this._lint4 = paramTdsInputStream.readUnsignedByte();
/* 214 */     this._lchar = paramTdsInputStream.readUnsignedByte();
/* 215 */     this._lflt = paramTdsInputStream.readUnsignedByte();
/* 216 */     this._ldate = paramTdsInputStream.readUnsignedByte();
/* 217 */     this._lusedb = paramTdsInputStream.readUnsignedByte();
/* 218 */     this._ldmpld = paramTdsInputStream.readUnsignedByte();
/* 219 */     this._linterfacespare = paramTdsInputStream.readUnsignedByte();
/* 220 */     this._ltype = paramTdsInputStream.readUnsignedByte();
/* 221 */     this._bufSize = paramTdsInputStream.readInt();
/*     */ 
/* 223 */     this._spare = new byte[SPARE.length];
/* 224 */     paramTdsInputStream.read(this._spare);
/*     */ 
/* 227 */     this._appName = paramTdsInputStream.readString(30);
/* 228 */     this._appNameLen = paramTdsInputStream.readUnsignedByte();
/* 229 */     this._appName = this._appName.substring(0, this._appNameLen);
/* 230 */     this._serviceName = paramTdsInputStream.readString(30);
/* 231 */     this._serviceNameLen = paramTdsInputStream.readUnsignedByte();
/* 232 */     this._serviceName = this._serviceName.substring(0, this._serviceNameLen);
/* 233 */     byte[] arrayOfByte1 = new byte['ÿ'];
/* 234 */     paramTdsInputStream.read(arrayOfByte1);
/* 235 */     this._remPw = new String(arrayOfByte1);
/* 236 */     this._remPwLen = paramTdsInputStream.readUnsignedByte();
/*     */ 
/* 238 */     this._tdsversion = new byte[LoginToken.TDSVERSION.length];
/* 239 */     paramTdsInputStream.read(this._tdsversion);
/* 240 */     if (this._tdsversion[0] < 5)
/*     */     {
/* 242 */       throw new IOException("TDS versions prior to 5.0 are not supported.");
/*     */     }
/* 244 */     this._progName = paramTdsInputStream.readString(10);
/* 245 */     this._progNameLen = paramTdsInputStream.readUnsignedByte();
/* 246 */     this._progName = this._progName.substring(0, this._progNameLen);
/* 247 */     paramTdsInputStream.read(this._progVers);
/*     */ 
/* 249 */     this._lnoshort = paramTdsInputStream.readUnsignedByte();
/* 250 */     this._lflt4 = paramTdsInputStream.readUnsignedByte();
/* 251 */     this._ldate4 = paramTdsInputStream.readUnsignedByte();
/*     */ 
/* 255 */     this._language = paramTdsInputStream.readString(30);
/* 256 */     this._languageLen = paramTdsInputStream.readUnsignedByte();
/* 257 */     this._language = this._language.substring(0, this._languageLen);
/* 258 */     this._lsetlang = paramTdsInputStream.readUnsignedByte();
/*     */ 
/* 261 */     this._oldsecure = new byte[OLDSECURE.length];
/* 262 */     paramTdsInputStream.read(this._oldsecure);
/* 263 */     this._lseclogin = paramTdsInputStream.readUnsignedByte();
/* 264 */     this._lsecbulk = paramTdsInputStream.readUnsignedByte();
/*     */ 
/* 267 */     this._lhalogin = paramTdsInputStream.readUnsignedByte();
/* 268 */     paramTdsInputStream.read(this._lhasessionid);
/*     */ 
/* 270 */     this._secspare = new byte[SECSPARE.length];
/* 271 */     paramTdsInputStream.read(this._secspare);
/*     */ 
/* 274 */     this._charset = paramTdsInputStream.readString(30);
/* 275 */     this._charsetLen = paramTdsInputStream.readUnsignedByte();
/* 276 */     this._charset = this._charset.substring(0, this._charsetLen);
/* 277 */     this._lsetcharset = paramTdsInputStream.readUnsignedByte();
/* 278 */     byte[] arrayOfByte2 = new byte[6];
/* 279 */     paramTdsInputStream.read(arrayOfByte2);
/* 280 */     this._packetSizeLength = (byte)paramTdsInputStream.readUnsignedByte();
/* 281 */     this._tdsPacketSize = new Integer(new String(arrayOfByte2, 0, this._packetSizeLength)).intValue();
/*     */ 
/* 285 */     byte[] arrayOfByte3 = new byte[DUMMY.length];
/* 286 */     paramTdsInputStream.read(arrayOfByte3);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 300 */     DumpInfo localDumpInfo = null;
/* 301 */     if (paramDumpFilter.includesToken(258))
/*     */     {
/* 303 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 304 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 306 */         localDumpInfo.addInfo("Token", 1, "Login Record; fixed length.");
/*     */       }
/*     */       else
/*     */       {
/* 310 */         localDumpInfo.addInfo("Token", 1, "Login Record");
/*     */       }
/*     */ 
/* 313 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 316 */         localDumpInfo.addText("Host Name", 30, this._hostName.substring(0, this._hostNameLen));
/*     */ 
/* 318 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 320 */           localDumpInfo.addInt("Host Name Length", 1, this._hostNameLen);
/*     */         }
/* 322 */         localDumpInfo.addText("User Name", 30, this._user.substring(0, this._userLen));
/*     */ 
/* 324 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 326 */           localDumpInfo.addInt("User Name Length", 1, this._userLen);
/*     */         }
/* 328 */         if (paramDumpFilter.includesDetail(5))
/*     */         {
/* 332 */           localDumpInfo.addText("Password", 30, "<masked>");
/* 333 */           if (paramDumpFilter.includesDetail(1))
/*     */           {
/* 336 */             arrayOfString = new String[] { "<masked>" };
/* 337 */             localDumpInfo.addField("Password Length", 1, 0, arrayOfString);
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 343 */           localDumpInfo.addText("Password", 30, "<masked>");
/* 344 */           if (paramDumpFilter.includesDetail(1))
/*     */           {
/* 347 */             arrayOfString = new String[] { "<masked>" };
/* 348 */             localDumpInfo.addField("Password Length", 1, 0, arrayOfString);
/*     */           }
/*     */         }
/* 351 */         localDumpInfo.addText("Host Process", 30, this._hostProc.substring(0, this._hostProcLen));
/*     */ 
/* 353 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 355 */           localDumpInfo.addInt("Host Process Length", 1, this._hostProcLen);
/*     */         }
/* 357 */         localDumpInfo.addInt("Byte Ordering - int2", 1, this._lint2);
/* 358 */         localDumpInfo.addInt("Byte Ordering - int4", 1, this._lint4);
/* 359 */         localDumpInfo.addInt("Character Encoding", 1, this._lchar);
/* 360 */         localDumpInfo.addInt("Float Format", 1, this._lflt);
/* 361 */         localDumpInfo.addInt("Date Format", 1, this._ldate);
/* 362 */         localDumpInfo.addHex("lusedb", 1, this._lusedb);
/* 363 */         localDumpInfo.addInt("ldmpld", 1, this._ldmpld);
/* 364 */         localDumpInfo.addHex("linterfacespare", 1, this._linterfacespare);
/* 365 */         localDumpInfo.addInt("Dialog Type", 1, this._ltype);
/* 366 */         localDumpInfo.addInt("lbufsize", 1, this._bufSize);
/* 367 */         localDumpInfo.addHex("spare", 3, this._spare);
/* 368 */         localDumpInfo.addText("Application Name", 30, this._appName.substring(0, this._appNameLen));
/*     */ 
/* 370 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 372 */           localDumpInfo.addInt("Application Name Length", 1, this._appNameLen);
/*     */         }
/* 374 */         localDumpInfo.addText("Service Name", 30, this._serviceName.substring(0, this._serviceNameLen));
/*     */ 
/* 376 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 378 */           localDumpInfo.addInt("Service Name Length", 1, this._serviceNameLen);
/*     */         }
/*     */ 
/* 381 */         localDumpInfo.addText("Remote Passwords", 255, "<masked>");
/* 382 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 385 */           arrayOfString = new String[] { "<masked>" };
/* 386 */           localDumpInfo.addField("Password Length", 1, 0, arrayOfString);
/*     */         }
/* 388 */         localDumpInfo.addInfo("TDS Version", 4, LoginAckToken.makeVersionString(this._tdsversion));
/* 389 */         localDumpInfo.addText("Prog Name", 30, this._progName.substring(0, this._progNameLen));
/*     */ 
/* 391 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 393 */           localDumpInfo.addInt("Prog Name Length", 1, this._progNameLen);
/*     */         }
/* 395 */         localDumpInfo.addInfo("Prog Version", 4, LoginAckToken.makeVersionString(this._progVers));
/*     */ 
/* 397 */         localDumpInfo.addInt("Convert Shorts", 1, this._lnoshort);
/* 398 */         localDumpInfo.addInt("4-byte Float Format", 1, this._lflt4);
/* 399 */         localDumpInfo.addInt("4-byte Date Format", 1, this._ldate4);
/* 400 */         localDumpInfo.addText("Language", 30, this._language.substring(0, this._languageLen));
/*     */ 
/* 402 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 404 */           localDumpInfo.addInt("Language Length", 1, this._languageLen);
/*     */         }
/* 406 */         localDumpInfo.addInt("Notify when Changed", 1, this._lsetlang);
/* 407 */         localDumpInfo.addHex("Old Secure Info", OLDSECURE.length, this._oldsecure);
/* 408 */         String[] arrayOfString = { "UNUSED", "SEC_LOG_ENCRYPT", "SEC_LOG_CHALLENGE", "SEC_LOG_LABELS", "SEC_LOG_APPDEFINED", "SEC_LOG_SECSESSION", "SEC_LOG_ENCRYPT2", "NEG_LOG_LOGPARAMS", "SEC_LOG_ENCRYPT3" };
/*     */ 
/* 416 */         localDumpInfo.addBitfield("Secure Login Flags", 1, this._lseclogin, arrayOfString);
/* 417 */         localDumpInfo.addInt("Bulk Copy", 1, this._lsecbulk);
/*     */ 
/* 419 */         localDumpInfo.addHex("HA Login Flags", 1, this._lhalogin);
/* 420 */         localDumpInfo.addHex("HA Session ID", 6, this._lhasessionid);
/*     */ 
/* 422 */         localDumpInfo.addHex("Spare", SECSPARE.length, this._secspare);
/* 423 */         localDumpInfo.addText("Character Set", 30, this._charset.substring(0, this._charsetLen));
/*     */ 
/* 425 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 427 */           localDumpInfo.addInt("Character Set Length", 1, this._charsetLen);
/*     */         }
/* 429 */         localDumpInfo.addInt("Notify when Changed", 1, this._lsetcharset);
/* 430 */         localDumpInfo.addInt("Packet Size", 6, this._tdsPacketSize);
/* 431 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 433 */           localDumpInfo.addInt("Packet Size Length", 1, this._packetSizeLength);
/*     */         }
/*     */       }
/*     */     }
/* 437 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 445 */     return 258;
/*     */   }
/*     */ 
/*     */   protected static String getPasswordsString(String paramString, int paramInt)
/*     */   {
/* 464 */     byte[] arrayOfByte = paramString.getBytes();
/*     */ 
/* 466 */     String str = new String();
/* 467 */     for (int i = 0; i < paramInt; )
/*     */     {
/* 469 */       if (str.length() > 0)
/*     */       {
/* 471 */         str = str + ", ";
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 476 */         int j = arrayOfByte[i];
/* 477 */         ++i;
/* 478 */         if (j == 0)
/*     */         {
/* 480 */           str = str + "<universal>";
/*     */         }
/*     */         else
/*     */         {
/* 484 */           str = str + "\"" + paramString.substring(i, i + j) + "\"";
/* 485 */           i += j;
/*     */         }
/*     */ 
/* 488 */         str = str + "/";
/*     */ 
/* 490 */         int k = arrayOfByte[i];
/* 491 */         ++i;
/* 492 */         if (k == 0)
/*     */         {
/* 494 */           str = str + "<null>";
/*     */         }
/*     */         else
/*     */         {
/* 498 */           str = str + "\"" + paramString.substring(i, i + k) + "\"";
/* 499 */           i += k;
/*     */         }
/*     */       }
/*     */       catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
/*     */       {
/* 504 */         str = new String("<unrecognized> ");
/* 505 */         str = str + HexConverts.hexConvert(paramString.getBytes());
/*     */       }
/*     */     }
/*     */ 
/* 509 */     return str;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvLoginToken
 * JD-Core Version:    0.5.4
 */