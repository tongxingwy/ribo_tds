/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class LoginAckToken extends Token
/*     */ {
/*     */   protected int _status;
/*  31 */   protected byte[] _tdsVers = null;
/*  32 */   protected String _progName = null;
/*  33 */   protected byte[] _progVers = null;
/*     */ 
/*     */   protected LoginAckToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public LoginAckToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  51 */     int i = paramTdsInputStream.readShort();
/*     */ 
/*  53 */     this._status = paramTdsInputStream.readUnsignedByte();
/*     */ 
/*  55 */     this._tdsVers = new byte[4];
/*  56 */     paramTdsInputStream.read(this._tdsVers);
/*     */ 
/*  58 */     i = paramTdsInputStream.readUnsignedByte();
/*  59 */     this._progName = paramTdsInputStream.readString(i);
/*     */ 
/*  61 */     this._progVers = new byte[4];
/*  62 */     paramTdsInputStream.read(this._progVers);
/*     */   }
/*     */ 
/*     */   protected boolean tdsVersionOK()
/*     */   {
/*  77 */     return (this._tdsVers != null) && (this._tdsVers[0] >= 5);
/*     */   }
/*     */ 
/*     */   protected String getTdsVersionString()
/*     */   {
/*  85 */     return makeVersionString(this._tdsVers);
/*     */   }
/*     */ 
/*     */   protected int getLoginStatus()
/*     */   {
/*  94 */     return this._status;
/*     */   }
/*     */ 
/*     */   protected boolean loginOK()
/*     */   {
/* 103 */     int i = 0;
/* 104 */     String str = "UNRECOGNIZED STATUS";
/* 105 */     switch (this._status)
/*     */     {
/*     */     case 5:
/*     */     case 133:
/* 109 */       str = "SUCCEED";
/* 110 */       i = 1;
/* 111 */       break;
/*     */     case 7:
/*     */     case 135:
/* 114 */       str = "NEGOTIATE";
/* 115 */       i = 1;
/* 116 */       break;
/*     */     case 6:
/*     */     case 134:
/* 119 */       str = "LOG_FAIL";
/*     */     }
/*     */ 
/* 125 */     return i;
/*     */   }
/*     */ 
/*     */   public static String makeVersionString(byte[] paramArrayOfByte)
/*     */   {
/* 136 */     if ((paramArrayOfByte == null) || (paramArrayOfByte.length < 4))
/*     */     {
/* 138 */       return null;
/*     */     }
/* 140 */     return paramArrayOfByte[0] + "." + paramArrayOfByte[1] + "." + paramArrayOfByte[2] + "." + paramArrayOfByte[3];
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.LoginAckToken
 * JD-Core Version:    0.5.4
 */