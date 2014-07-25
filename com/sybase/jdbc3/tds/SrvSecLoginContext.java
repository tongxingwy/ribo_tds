/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.security.asn1.DERObject;
/*     */ import com.sybase.jdbc3.security.asn1.DEROutputStream;
/*     */ import com.sybase.jdbc3.security.asn1.x509.RSAPublicKeyStructure;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.KeyPair;
/*     */ import java.security.interfaces.RSAPrivateKey;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.util.Arrays;
/*     */ import java.util.Vector;
/*     */ import javax.crypto.Cipher;
/*     */ import sun.misc.BASE64Encoder;
/*     */ 
/*     */ public class SrvSecLoginContext
/*     */ {
/*  48 */   private static int LOG_MSG_PARM_ID = 0;
/*     */ 
/*  51 */   private static int REM_MSG_PARM_ID = 1;
/*     */ 
/*  54 */   private static int CIPHER_SUITE = 1;
/*     */ 
/*  57 */   private String[] ENCRYPT_MSGS = { "Client determines encryption.", "Encrypt2 is required.", "Encrypt3 or Encrypt2 is required.", "Encrypt3 is required." };
/*     */   private SrvReceiver _srvReceiver;
/*  66 */   private byte[] _nonce = new byte[32];
/*     */ 
/*  72 */   private Cipher _cipherSuite = null;
/*     */ 
/*  75 */   RSAPrivateKey _privateKey = null;
/*     */ 
/*  78 */   RSAPublicKey _publicKey = null;
/*     */ 
/*     */   public SrvSecLoginContext(SrvReceiver paramSrvReceiver, Cipher paramCipher)
/*     */   {
/*  85 */     this._srvReceiver = paramSrvReceiver;
/*  86 */     this._cipherSuite = paramCipher;
/*     */   }
/*     */ 
/*     */   String doLogin(SrvSession paramSrvSession, SrvLoginToken paramSrvLoginToken)
/*     */   {
/* 117 */     int i = paramSrvSession.getLogin()._lseclogin;
/* 118 */     switch (this._srvReceiver.getEncryptMode())
/*     */     {
/*     */     case 3:
/* 122 */       validateEncryptLevel(paramSrvSession, i, 128);
/* 123 */       break;
/*     */     case 2:
/* 126 */       validateEncryptLevel(paramSrvSession, i, 160);
/* 127 */       break;
/*     */     case 1:
/* 130 */       validateEncryptLevel(paramSrvSession, i, 32);
/* 131 */       i &= 32;
/*     */     }
/*     */ 
/* 139 */     String str = paramSrvLoginToken.getPassword();
/* 140 */     if ((i & 0x80) != 0)
/*     */     {
/* 143 */       str = doEncrypt3(paramSrvSession, paramSrvLoginToken);
/*     */     }
/* 145 */     else if ((i & 0x20) != 0)
/*     */     {
/* 148 */       str = doEncrypt2(paramSrvSession, paramSrvLoginToken);
/*     */     }
/*     */ 
/* 151 */     return str;
/*     */   }
/*     */ 
/*     */   private void validateEncryptLevel(SrvSession paramSrvSession, int paramInt1, int paramInt2)
/*     */   {
/* 167 */     if ((paramInt1 & paramInt2) != 0)
/*     */       return;
/* 169 */     String str = "Received insufficient encryption level from client. " + this.ENCRYPT_MSGS[this._srvReceiver.getEncryptMode()];
/*     */     try
/*     */     {
/* 173 */       this._srvReceiver.sendLogin(paramSrvSession, paramSrvSession.getClientCapability(), this._srvReceiver._server, false, paramSrvSession.getLogin().getPacketSize());
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */ 
/* 179 */     throw new RuntimeException(str);
/*     */   }
/*     */ 
/*     */   private String doEncrypt3(SrvSession paramSrvSession, SrvLoginToken paramSrvLoginToken)
/*     */   {
/* 199 */     String str = paramSrvLoginToken.getPassword();
/*     */     try
/*     */     {
/* 203 */       this._nonce = this._srvReceiver.nextNonce();
/* 204 */       KeyPair localKeyPair = this._srvReceiver.getEncrypt3Keys();
/* 205 */       this._privateKey = ((RSAPrivateKey)localKeyPair.getPrivate());
/* 206 */       this._publicKey = ((RSAPublicKey)localKeyPair.getPublic());
/*     */ 
/* 209 */       this._srvReceiver.sendLogNegotiateAck(paramSrvSession);
/*     */ 
/* 212 */       sendKey(paramSrvSession, 30);
/*     */ 
/* 215 */       byte[] arrayOfByte1 = getEncryptedMsg(paramSrvSession, 31, LOG_MSG_PARM_ID);
/*     */       byte[] arrayOfByte2;
/*     */       Object localObject;
/* 216 */       if (arrayOfByte1 != null)
/*     */       {
/* 218 */         arrayOfByte2 = decryptMessage(arrayOfByte1);
/*     */ 
/* 221 */         localObject = new byte[this._nonce.length];
/* 222 */         System.arraycopy(arrayOfByte2, 0, localObject, 0, localObject.length);
/*     */ 
/* 226 */         if (!Arrays.equals(localObject, this._nonce))
/*     */         {
/* 229 */           this._srvReceiver.sendLogin(paramSrvSession, paramSrvSession.getClientCapability(), this._srvReceiver._server, false, paramSrvLoginToken.getPacketSize());
/* 230 */           throw new SrvProtocolException("Invalid nonce received from client");
/*     */         }
/*     */ 
/* 234 */         str = new String(arrayOfByte2, this._nonce.length, arrayOfByte2.length - localObject.length);
/*     */       }
/*     */ 
/* 246 */       arrayOfByte1 = getEncryptedMsg(paramSrvSession, 32, REM_MSG_PARM_ID);
/* 247 */       if (arrayOfByte1 != null)
/*     */       {
/* 249 */         arrayOfByte2 = decryptMessage(arrayOfByte1);
/*     */ 
/* 252 */         localObject = new String(arrayOfByte2);
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 257 */       throw new RuntimeException(localIOException);
/*     */     }
/*     */ 
/* 260 */     return (String)str;
/*     */   }
/*     */ 
/*     */   private String doEncrypt2(SrvSession paramSrvSession, SrvLoginToken paramSrvLoginToken)
/*     */   {
/* 275 */     String str1 = paramSrvLoginToken.getPassword();
/*     */     try
/*     */     {
/* 279 */       this._nonce = new byte[0];
/* 280 */       KeyPair localKeyPair = this._srvReceiver.getEncrypt2Keys();
/* 281 */       this._privateKey = ((RSAPrivateKey)localKeyPair.getPrivate());
/* 282 */       this._publicKey = ((RSAPublicKey)localKeyPair.getPublic());
/*     */ 
/* 285 */       this._srvReceiver.sendLogNegotiateAck(paramSrvSession);
/*     */ 
/* 288 */       sendKey(paramSrvSession, 14);
/*     */ 
/* 291 */       byte[] arrayOfByte1 = getEncryptedMsg(paramSrvSession, 15, LOG_MSG_PARM_ID);
/*     */       byte[] arrayOfByte2;
/* 292 */       if (arrayOfByte1 != null)
/*     */       {
/* 294 */         arrayOfByte2 = decryptMessage(arrayOfByte1);
/*     */ 
/* 297 */         str1 = new String(arrayOfByte2);
/*     */       }
/*     */ 
/* 309 */       arrayOfByte1 = getEncryptedMsg(paramSrvSession, 22, REM_MSG_PARM_ID);
/* 310 */       if (arrayOfByte1 != null)
/*     */       {
/* 312 */         arrayOfByte2 = decryptMessage(arrayOfByte1);
/*     */ 
/* 315 */         String str2 = new String(arrayOfByte2);
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 320 */       throw new RuntimeException(localIOException);
/*     */     }
/*     */ 
/* 323 */     return str1;
/*     */   }
/*     */ 
/*     */   private void sendKey(SrvSession paramSrvSession, short paramShort)
/*     */   {
/*     */     try
/*     */     {
/* 340 */       MsgToken localMsgToken = new MsgToken(1, paramShort);
/* 341 */       localMsgToken.send(paramSrvSession.getOutputStream());
/*     */ 
/* 344 */       String str = createPEMKey(this._publicKey);
/* 345 */       Vector localVector = new Vector();
/* 346 */       localVector.add(new Integer(CIPHER_SUITE));
/* 347 */       localVector.add(str.getBytes());
/* 348 */       if ((this._nonce != null) && (this._nonce.length > 0))
/* 349 */         localVector.add(this._nonce);
/* 350 */       paramSrvSession.sendParams(null, localVector.toArray(), 1);
/* 351 */       paramSrvSession.sendDone(-1, false, true, false);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 355 */       throw new RuntimeException(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private byte[] getEncryptedMsg(SrvSession paramSrvSession, int paramInt1, int paramInt2)
/*     */     throws SrvProtocolException
/*     */   {
/* 378 */     SrvMsgToken localSrvMsgToken = getMessage(paramSrvSession);
/* 379 */     if ((localSrvMsgToken != null) && (localSrvMsgToken.getMessageID() != paramInt1))
/*     */     {
/* 381 */       throw new SrvProtocolException("Expected message type: " + paramInt1 + ", received: " + localSrvMsgToken.getMessageID());
/*     */     }
/*     */ 
/* 385 */     Object[] arrayOfObject = null;
/* 386 */     byte[] arrayOfByte = null;
/* 387 */     if (localSrvMsgToken.hasParameters())
/*     */     {
/* 390 */       arrayOfObject = getParameters(paramSrvSession);
/* 391 */       if (arrayOfObject != null) {
/* 392 */         arrayOfByte = (byte[])arrayOfObject[paramInt2];
/*     */       }
/*     */     }
/* 395 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   private Object[] getParameters(SrvSession paramSrvSession)
/*     */   {
/* 407 */     Object[] arrayOfObject = null;
/*     */     try
/*     */     {
/* 411 */       SrvParamFormatToken localSrvParamFormatToken = (SrvParamFormatToken)paramSrvSession.receive();
/*     */ 
/* 414 */       SrvParamsToken localSrvParamsToken = (SrvParamsToken)paramSrvSession.receive();
/* 415 */       if ((localSrvParamFormatToken != null) && (localSrvParamsToken != null))
/*     */       {
/* 417 */         SrvJavaTypeFormatter localSrvJavaTypeFormatter = new SrvJavaTypeFormatter(localSrvParamFormatToken, paramSrvSession.getClientCapability());
/* 418 */         arrayOfObject = localSrvJavaTypeFormatter.convertData(localSrvParamsToken);
/*     */       }
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 423 */       throw new RuntimeException(localException);
/*     */     }
/*     */ 
/* 426 */     return arrayOfObject;
/*     */   }
/*     */ 
/*     */   private byte[] decryptMessage(byte[] paramArrayOfByte)
/*     */   {
/* 438 */     byte[] arrayOfByte = null;
/*     */     try
/*     */     {
/* 441 */       this._cipherSuite.init(2, this._privateKey);
/* 442 */       arrayOfByte = this._cipherSuite.doFinal(paramArrayOfByte);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 446 */       throw new RuntimeException(localException);
/*     */     }
/*     */ 
/* 449 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   private SrvMsgToken getMessage(SrvSession paramSrvSession)
/*     */   {
/* 461 */     SrvMsgToken localSrvMsgToken = null;
/*     */     try
/*     */     {
/* 464 */       localSrvMsgToken = (SrvMsgToken)paramSrvSession.receive();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 468 */       throw new RuntimeException(localException);
/*     */     }
/* 470 */     return localSrvMsgToken;
/*     */   }
/*     */ 
/*     */   private String createPEMKey(RSAPublicKey paramRSAPublicKey)
/*     */     throws IOException
/*     */   {
/* 486 */     RSAPublicKeyStructure localRSAPublicKeyStructure = new RSAPublicKeyStructure(paramRSAPublicKey.getModulus(), paramRSAPublicKey.getPublicExponent());
/* 487 */     DERObject localDERObject = localRSAPublicKeyStructure.toASN1Object();
/* 488 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 489 */     DEROutputStream localDEROutputStream = new DEROutputStream(localByteArrayOutputStream);
/* 490 */     localDEROutputStream.writeObject(localDERObject);
/* 491 */     return encodePEMKey(localByteArrayOutputStream);
/*     */   }
/*     */ 
/*     */   private String encodePEMKey(ByteArrayOutputStream paramByteArrayOutputStream)
/*     */   {
/* 503 */     BASE64Encoder localBASE64Encoder = new BASE64Encoder();
/* 504 */     StringBuffer localStringBuffer = new StringBuffer();
/* 505 */     localStringBuffer.append("-----BEGIN RSA PUBLIC KEY-----").append("\n").append(localBASE64Encoder.encode(paramByteArrayOutputStream.toByteArray())).append("\n").append("-----END RSA PUBLIC KEY-----").append("\n");
/*     */ 
/* 511 */     return localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvSecLoginContext
 * JD-Core Version:    0.5.4
 */