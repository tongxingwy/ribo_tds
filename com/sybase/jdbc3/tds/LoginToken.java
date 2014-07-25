/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.SybConnection;
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import com.sybase.jdbc3.utils.Debug;
/*     */ import com.sybase.jdbc3.utils.SybVersion;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetAddress;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class LoginToken extends Token
/*     */ {
/*     */   protected static final int MAX_NAMELEN = 30;
/*     */   protected static final int MAX_REMPWDLEN = 255;
/*     */   protected static final int MAX_PROGNLEN = 10;
/*     */   protected static final int MAX_PACKETSIZELEN = 6;
/*     */   protected String _hostName;
/*     */   protected String _user;
/*     */   protected String _password;
/*     */   protected String _hostProc;
/*  42 */   protected int _bufSize = 512;
/*     */   protected String _appName;
/*     */   protected String _serviceName;
/*     */   protected String _remPw;
/*     */   protected boolean _encPwd;
/*  50 */   protected int _lint2 = 2;
/*  51 */   protected int _lint4 = 0;
/*  52 */   protected int _lflt = 4;
/*  53 */   protected int _lflt4 = 12;
/*  54 */   protected int _ldate = 8;
/*  55 */   protected int _ldate4 = 16;
/*     */ 
/*  58 */   protected static final byte[] TDSVERSION = { 5, 0, 0, 0 };
/*     */ 
/*  65 */   protected int _lseclogin = 0;
/*     */   private static final String PROGNAME = "jConnect";
/*  70 */   protected byte[] _progVers = { 1, 0, 0, 0 };
/*     */   protected String _language;
/*     */   protected String _charset;
/*     */   protected String _packetSize;
/*  85 */   protected byte[] _lhasessionid = new byte[6];
/*     */   protected int _lhalogin;
/*  90 */   protected static final byte[] SPARE = { 0, 0, 0 };
/*     */ 
/*  97 */   protected static final byte[] OLDSECURE = { 0, 0 };
/*     */ 
/* 104 */   protected static final byte[] SECSPARE = { 0, 0 };
/*     */ 
/* 110 */   protected static final byte[] DUMMY = { 0, 0, 0, 0 };
/*     */ 
/*     */   protected LoginToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public LoginToken(SybProperty paramSybProperty, TdsProtocolContext paramTdsProtocolContext, HASessionContext paramHASessionContext)
/*     */     throws SQLException
/*     */   {
/* 142 */     if (paramSybProperty.getBoolean(48))
/*     */     {
/* 147 */       this._user = "";
/* 148 */       this._password = "";
/*     */ 
/* 151 */       this._lseclogin = 16;
/*     */     }
/*     */     else
/*     */     {
/* 155 */       this._user = paramSybProperty.getString(3);
/* 156 */       if (this._user == null)
/*     */       {
/* 158 */         ErrorMessage.raiseError("JZ004");
/*     */       }
/* 160 */       if (this._user.length() > 30)
/*     */       {
/* 162 */         ErrorMessage.raiseError("JZ001", this._user);
/*     */       }
/*     */ 
/* 165 */       this._password = paramSybProperty.getString(4);
/* 166 */       if (this._password == null)
/*     */       {
/* 169 */         this._password = "";
/*     */       }
/* 171 */       if (this._password.length() > 30)
/*     */       {
/* 175 */         ErrorMessage.raiseError("JZ002", "");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 180 */     this._hostName = paramSybProperty.getString(1);
/* 181 */     if ((this._hostName != null) && (this._hostName.length() > 30))
/*     */     {
/* 183 */       paramTdsProtocolContext._conn.chainWarnings(ErrorMessage.createWarning("010HT", this._hostName));
/*     */ 
/* 185 */       this._hostName = this._hostName.substring(0, 30);
/*     */     }
/*     */ 
/* 195 */     if (this._hostName == null)
/*     */     {
/*     */       try
/*     */       {
/* 199 */         this._hostName = InetAddress.getLocalHost().getHostName();
/*     */ 
/* 203 */         if ((this._hostName != null) && (this._hostName.length() > 30))
/*     */         {
/* 205 */           paramTdsProtocolContext._conn.chainWarnings(ErrorMessage.createWarning("010HT", this._hostName));
/*     */ 
/* 207 */           this._hostName = this._hostName.substring(0, 30);
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (Throwable localThrowable)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 218 */     this._hostProc = paramSybProperty.getString(2);
/* 219 */     if ((this._hostProc == null) || (this._hostProc == ""))
/*     */     {
/* 221 */       this._hostProc = "";
/*     */       try
/*     */       {
/* 224 */         Class localClass1 = Class.forName("java.lang.management.ManagementFactory");
/* 225 */         Method localMethod1 = localClass1.getMethod("getRuntimeMXBean", null);
/* 226 */         Object localObject1 = localMethod1.invoke(null, null);
/*     */ 
/* 228 */         Class localClass2 = Class.forName("java.lang.management.RuntimeMXBean");
/* 229 */         Method localMethod2 = localClass2.getMethod("getName", null);
/* 230 */         Object localObject2 = localMethod2.invoke(localObject1, null);
/*     */ 
/* 232 */         String str = (String)localObject2;
/* 233 */         String[] arrayOfString = str.split("@");
/* 234 */         this._hostProc = arrayOfString[0];
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 242 */     this._appName = paramSybProperty.getString(5);
/*     */ 
/* 244 */     if (this._appName == null)
/*     */     {
/* 246 */       StackTraceElement[] arrayOfStackTraceElement = new Throwable().getStackTrace();
/* 247 */       this._appName = arrayOfStackTraceElement[(arrayOfStackTraceElement.length - 1)].getClassName();
/*     */     }
/*     */ 
/* 250 */     if (this._appName.length() > 30)
/*     */     {
/* 252 */       this._appName = this._appName.substring(0, 29);
/*     */     }
/*     */ 
/* 255 */     this._serviceName = paramSybProperty.getString(0);
/* 256 */     this._remPw = paramSybProperty.getString(9);
/* 257 */     this._charset = paramSybProperty.getString(8);
/* 258 */     this._language = paramSybProperty.getString(7);
/*     */ 
/* 260 */     if (this._language == null)
/*     */     {
/* 263 */       this._language = Language.defaultLanguage();
/*     */     }
/* 265 */     if (this._language != null)
/*     */     {
/* 267 */       paramSybProperty.setProperty(7, this._language);
/* 268 */       Language.setLocale(this._language);
/*     */     }
/*     */ 
/* 273 */     this._packetSize = String.valueOf(paramSybProperty.getInteger(12));
/*     */ 
/* 277 */     this._progVers[0] = SybVersion.MAJOR_VERSION;
/* 278 */     this._progVers[1] = SybVersion.MINOR_VERSION;
/* 279 */     this._progVers[2] = SybVersion.POINT_MINOR_VERSION;
/* 280 */     this._progVers[3] = (byte)(SybVersion.SP_NUMBER / 10);
/*     */ 
/* 282 */     if (paramSybProperty.getBoolean(30))
/*     */     {
/* 284 */       this._lint2 = 3;
/* 285 */       this._lint4 = 1;
/* 286 */       this._lflt = 10;
/* 287 */       this._lflt4 = 13;
/* 288 */       this._ldate = 9;
/* 289 */       this._ldate4 = 17;
/*     */     }
/*     */ 
/* 292 */     this._lhalogin = paramHASessionContext.getLogin();
/* 293 */     System.arraycopy(paramHASessionContext.getSessionID(), 0, this._lhasessionid, 0, 6);
/*     */ 
/* 298 */     if (!(this._encPwd = paramSybProperty.getBoolean(55)))
/*     */       return;
/* 300 */     this._password = "";
/* 301 */     this._lseclogin = 161;
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 332 */       paramTdsOutputStream.writeStringLen(this._hostName, 30);
/* 333 */       paramTdsOutputStream.writeStringLen(this._user, 30);
/* 334 */       paramTdsOutputStream.writeStringLen(this._password, 30);
/* 335 */       paramTdsOutputStream.writeStringLen(this._hostProc, 30);
/* 336 */       paramTdsOutputStream.write(this._lint2);
/* 337 */       paramTdsOutputStream.write(this._lint4);
/* 338 */       paramTdsOutputStream.write(6);
/* 339 */       paramTdsOutputStream.write(this._lflt);
/* 340 */       paramTdsOutputStream.write(this._ldate);
/* 341 */       paramTdsOutputStream.write(1);
/* 342 */       paramTdsOutputStream.write(0);
/* 343 */       paramTdsOutputStream.write(0);
/* 344 */       paramTdsOutputStream.write(0);
/* 345 */       paramTdsOutputStream.writeInt(this._bufSize);
/* 346 */       paramTdsOutputStream.write(SPARE);
/* 347 */       paramTdsOutputStream.writeStringLen(this._appName, 30);
/* 348 */       paramTdsOutputStream.writeStringLen(this._serviceName, 30);
/* 349 */       prepareRemPwd(paramTdsOutputStream);
/* 350 */       paramTdsOutputStream.writeStringLen(this._remPw, 255);
/* 351 */       paramTdsOutputStream.write(TDSVERSION);
/* 352 */       paramTdsOutputStream.writeStringLen("jConnect", 10);
/* 353 */       paramTdsOutputStream.write(this._progVers);
/* 354 */       paramTdsOutputStream.write(0);
/* 355 */       paramTdsOutputStream.write(this._lflt4);
/* 356 */       paramTdsOutputStream.write(this._ldate4);
/* 357 */       paramTdsOutputStream.writeStringLen(this._language, 30);
/* 358 */       paramTdsOutputStream.write(0);
/* 359 */       paramTdsOutputStream.write(OLDSECURE);
/* 360 */       paramTdsOutputStream.write(this._lseclogin);
/* 361 */       paramTdsOutputStream.write(0);
/* 362 */       paramTdsOutputStream.write(this._lhalogin);
/* 363 */       paramTdsOutputStream.write(this._lhasessionid);
/* 364 */       paramTdsOutputStream.write(SECSPARE);
/* 365 */       paramTdsOutputStream.writeStringLen(this._charset, 30);
/* 366 */       paramTdsOutputStream.write(1);
/* 367 */       paramTdsOutputStream.writeStringLen(this._packetSize, 6);
/* 368 */       paramTdsOutputStream.write(DUMMY);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 372 */       writeSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void prepareRemPwd(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/* 398 */     if (this._encPwd)
/*     */     {
/* 400 */       this._remPw = null;
/* 401 */       return;
/*     */     }
/* 403 */     StringBuffer localStringBuffer = new StringBuffer();
/*     */ 
/* 406 */     if (this._remPw == null)
/*     */     {
/* 408 */       localStringBuffer.append('\000');
/* 409 */       localStringBuffer.append((char)paramTdsOutputStream.getStringByteLen(this._password));
/* 410 */       localStringBuffer.append(this._password);
/*     */     }
/*     */     else
/*     */     {
/* 414 */       int i = 0;
/* 415 */       int j = 0;
/*     */ 
/* 418 */       char[] arrayOfChar1 = new char[256];
/*     */ 
/* 421 */       int k = this._remPw.length();
/* 422 */       char[] arrayOfChar2 = new char[k];
/* 423 */       this._remPw.getChars(0, k, arrayOfChar2, 0);
/*     */ 
/* 427 */       while ((j < k) && ((i = arrayOfChar2[(j++)]) != ','))
/*     */       {
/* 430 */         if (i != 92)
/*     */           continue;
/* 432 */         ++j;
/*     */       }
/*     */ 
/* 437 */       while (j < k)
/*     */       {
/* 439 */         int l = 0;
/*     */         do
/*     */         {
/* 442 */           if ((i = arrayOfChar2[(j++)]) == ',')
/*     */             break;
/* 444 */           if (i == 92)
/*     */           {
/* 447 */             Debug.asrt(this, j < k);
/* 448 */             i = arrayOfChar2[(j++)];
/*     */           }
/* 450 */           arrayOfChar1[(l++)] = i;
/*     */         }
/* 452 */         while (j < k);
/*     */ 
/* 466 */         if (l == 0)
/*     */         {
/* 468 */           localStringBuffer.append('\000');
/*     */         }
/*     */         else
/*     */         {
/* 475 */           String str = new String(arrayOfChar1, 0, l);
/* 476 */           localStringBuffer.append((char)paramTdsOutputStream.getStringByteLen(str));
/* 477 */           localStringBuffer.append(str);
/*     */         }
/*     */ 
/* 485 */         if ((arrayOfChar2[(j - 1)] != ',') || (j < k))
/*     */           continue;
/* 487 */         localStringBuffer.append('\000');
/*     */       }
/*     */     }
/*     */ 
/* 491 */     this._remPw = localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.LoginToken
 * JD-Core Version:    0.5.4
 */