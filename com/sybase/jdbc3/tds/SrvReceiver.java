/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.utils.SybVersion;
/*      */ import java.io.EOFException;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.net.Socket;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.net.URLClassLoader;
/*      */ import java.security.GeneralSecurityException;
/*      */ import java.security.KeyPair;
/*      */ import java.security.KeyPairGenerator;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.Provider;
/*      */ import java.security.SecureRandom;
/*      */ import java.security.Security;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Vector;
/*      */ import javax.crypto.Cipher;
/*      */ 
/*      */ public abstract class SrvReceiver
/*      */   implements SrvRequestListener, Runnable
/*      */ {
/*      */   public static final int ENCRYPT_MODE3 = 3;
/*      */   public static final int ENCRYPT_MODE2 = 2;
/*      */   public static final int ENCRYPT_MODE1 = 1;
/*      */   public static final int ENCRYPT_MODE0 = 0;
/*      */   private static final int NONCE_LENGTH = 32;
/*      */   private static final int ENCRYPT2_KEYSIZE = 1024;
/*      */   private static final int ENCRYPT3_KEYSIZE = 1024;
/*  121 */   protected Hashtable _sessions = new Hashtable();
/*      */ 
/*  124 */   protected Hashtable _regProcs = new Hashtable();
/*      */ 
/*  126 */   protected Vector _acceptors = new Vector();
/*  127 */   private SrvNotifier _notifier = null;
/*      */ 
/*  129 */   protected static int _nextCursorID = 0;
/*      */ 
/*  134 */   protected String _server = null;
/*      */ 
/*  137 */   private boolean _running = false;
/*      */ 
/*  143 */   private SecureRandom _nonceGenerator = new SecureRandom();
/*      */ 
/*  148 */   MessageDigest _messageDigest = null;
/*      */ 
/*  151 */   private KeyPairGenerator _encrypt2KeyPairGenerator = null;
/*      */ 
/*  154 */   private KeyPair _encrypt2Keys = null;
/*      */ 
/*  157 */   private KeyPairGenerator _encrypt3KeyPairGenerator = null;
/*      */ 
/*  160 */   private KeyPair _encrypt3Keys = null;
/*      */ 
/*  163 */   private Object _keyLock = new Object();
/*      */ 
/*  169 */   private Cipher _cipherSuite = null;
/*      */ 
/*  171 */   public String _encryptionProvider = null;
/*      */   private static final String DEFAULT_CRYPTO_PROVIDER = "com.certicom.ecc.jcae.Certicom";
/*  184 */   private boolean _uniqueKeyPair = true;
/*      */ 
/*  198 */   private int _encryptMode = 0;
/*      */ 
/*  204 */   private byte[] _progVersion = { (byte)(SybVersion.MAJOR_VERSION & 0xFF), (byte)(SybVersion.MINOR_VERSION & 0xFF), (byte)(SybVersion.POINT_MINOR_VERSION & 0xFF), (byte)(SybVersion.SP_NUMBER / 10) };
/*      */ 
/*      */   public SrvReceiver(String paramString)
/*      */   {
/*  216 */     this._server = paramString;
/*      */ 
/*  218 */     if (!initProvider()) {
/*      */       return;
/*      */     }
/*  221 */     initKeyGenerators();
/*      */ 
/*  224 */     createCipherSuite();
/*      */   }
/*      */ 
/*      */   private boolean initProvider()
/*      */   {
/*  231 */     Provider localProvider = null;
/*      */     try
/*      */     {
/*  234 */       if (this._encryptionProvider != null)
/*      */       {
/*  236 */         localProvider = (Provider)Class.forName(this._encryptionProvider).newInstance();
/*      */       }
/*      */       else
/*      */       {
/*  240 */         localProvider = initDefaultProvider();
/*      */       }
/*      */ 
/*  244 */       Security.addProvider(localProvider);
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*  248 */       return false;
/*      */     }
/*      */ 
/*  252 */     return true;
/*      */   }
/*      */ 
/*      */   private Provider initDefaultProvider()
/*      */     throws Exception
/*      */   {
/*  259 */     String str1 = "/";
/*      */ 
/*  262 */     ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
/*  263 */     URL[] arrayOfURL1 = ((URLClassLoader)localClassLoader).getURLs();
/*      */ 
/*  265 */     URL localURL = null;
/*  266 */     for (int i = 0; i < arrayOfURL1.length; ++i)
/*      */     {
/*  268 */       if (!arrayOfURL1[i].getPath().matches(".*jconn.?.jar"))
/*      */         continue;
/*  270 */       localURL = arrayOfURL1[i];
/*  271 */       break;
/*      */     }
/*      */ 
/*  276 */     i = localURL.getPath().lastIndexOf(str1);
/*  277 */     String str2 = localURL.getPath().substring(0, i);
/*      */ 
/*  280 */     URL[] arrayOfURL2 = new URL[2];
/*  281 */     arrayOfURL2[0] = new File(str2, "EccpressoFIPS.jar").toURI().toURL();
/*  282 */     arrayOfURL2[1] = new File(str2, "EccpressoFIPSJca.jar").toURI().toURL();
/*  283 */     URLClassLoader localURLClassLoader = new URLClassLoader(arrayOfURL2);
/*      */ 
/*  286 */     Provider localProvider = (Provider)Class.forName("com.certicom.ecc.jcae.Certicom", true, localURLClassLoader).newInstance();
/*      */ 
/*  288 */     return localProvider;
/*      */   }
/*      */ 
/*      */   public void initKeyGenerators()
/*      */   {
/*      */     try
/*      */     {
/*  300 */       this._encrypt2KeyPairGenerator = KeyPairGenerator.getInstance("RSA");
/*      */ 
/*  303 */       this._encrypt2KeyPairGenerator.initialize(1024, new SecureRandom());
/*      */ 
/*  306 */       this._encrypt3KeyPairGenerator = KeyPairGenerator.getInstance("RSA");
/*      */ 
/*  309 */       this._encrypt3KeyPairGenerator.initialize(1024, new SecureRandom());
/*      */ 
/*  317 */       generateKeys();
/*      */     }
/*      */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*      */     {
/*  321 */       throw new RuntimeException(localNoSuchAlgorithmException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateKeys()
/*      */   {
/*  330 */     synchronized (this._keyLock)
/*      */     {
/*  332 */       this._encrypt2Keys = this._encrypt2KeyPairGenerator.generateKeyPair();
/*  333 */       this._encrypt3Keys = this._encrypt3KeyPairGenerator.generateKeyPair();
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getEncryptMode()
/*      */   {
/*  345 */     return this._encryptMode;
/*      */   }
/*      */ 
/*      */   public void setEncryptMode(int paramInt)
/*      */   {
/*  356 */     this._encryptMode = paramInt;
/*      */   }
/*      */ 
/*      */   public void setUniqueKeyPair(boolean paramBoolean)
/*      */   {
/*  367 */     this._uniqueKeyPair = paramBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getUniqueKeyPair()
/*      */   {
/*  378 */     return this._uniqueKeyPair;
/*      */   }
/*      */ 
/*      */   public KeyPair getEncrypt2Keys()
/*      */   {
/*  388 */     KeyPair localKeyPair = null;
/*  389 */     synchronized (this._keyLock)
/*      */     {
/*  391 */       localKeyPair = this._encrypt2Keys;
/*  392 */       if (this._uniqueKeyPair)
/*      */       {
/*  394 */         localKeyPair = this._encrypt2KeyPairGenerator.generateKeyPair();
/*      */       }
/*      */     }
/*      */ 
/*  398 */     return localKeyPair;
/*      */   }
/*      */ 
/*      */   public KeyPair getEncrypt3Keys()
/*      */   {
/*  408 */     synchronized (this._keyLock)
/*      */     {
/*  410 */       return this._encrypt3Keys;
/*      */     }
/*      */   }
/*      */ 
/*      */   public SrvSession createSession(Socket paramSocket)
/*      */     throws IOException
/*      */   {
/*  422 */     return startSession(new SrvSession(paramSocket, paramSocket.getInputStream(), paramSocket.getOutputStream(), false, false));
/*      */   }
/*      */ 
/*      */   public void registerAcceptor(SrvAcceptor paramSrvAcceptor)
/*      */   {
/*  430 */     this._acceptors.addElement(paramSrvAcceptor);
/*      */   }
/*      */ 
/*      */   public SrvSession createSession(InputStream paramInputStream, OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/*  442 */     return startSession(new SrvSession(paramInputStream, paramOutputStream, false, false));
/*      */   }
/*      */ 
/*      */   public SrvSession createSession(Socket paramSocket, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws IOException
/*      */   {
/*  455 */     return startSession(new SrvSession(paramSocket, paramSocket.getInputStream(), paramSocket.getOutputStream(), paramBoolean1, paramBoolean2));
/*      */   }
/*      */ 
/*      */   public void connect(SrvSession paramSrvSession, SrvLoginToken paramSrvLoginToken)
/*      */   {
/*  470 */     if (this._cipherSuite != null)
/*      */     {
/*  473 */       SrvSecLoginContext localSrvSecLoginContext = new SrvSecLoginContext(this, this._cipherSuite);
/*  474 */       String str = localSrvSecLoginContext.doLogin(paramSrvSession, paramSrvLoginToken);
/*      */ 
/*  477 */       handleLogin(paramSrvSession, paramSrvLoginToken.getUser(), str, paramSrvLoginToken.getHost(), paramSrvLoginToken.getLocale(), paramSrvLoginToken.getPacketSize());
/*      */     }
/*      */     else
/*      */     {
/*  486 */       handleLogin(paramSrvSession, paramSrvLoginToken.getUser(), paramSrvLoginToken.getPassword(), paramSrvLoginToken.getHost(), paramSrvLoginToken.getLocale(), paramSrvLoginToken.getPacketSize());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void disconnect(SrvSession paramSrvSession, SrvLogoutToken paramSrvLogoutToken)
/*      */   {
/*  505 */     handleDisconnect(paramSrvSession, paramSrvLogoutToken);
/*  506 */     if (paramSrvLogoutToken != null)
/*      */     {
/*      */       try
/*      */       {
/*  510 */         paramSrvSession.send(new SrvDoneToken(0, 0, 0));
/*      */       }
/*      */       catch (Exception localException1)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  521 */     Socket localSocket = paramSrvSession.getSocket();
/*  522 */     paramSrvSession.close();
/*  523 */     removeSession(paramSrvSession);
/*      */     try
/*      */     {
/*  528 */       if (localSocket != null)
/*      */       {
/*  530 */         localSocket.close();
/*      */       }
/*      */     }
/*      */     catch (Exception localException2)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void language(SrvSession paramSrvSession, SrvLanguageToken paramSrvLanguageToken)
/*      */   {
/*  546 */     ParamFormatToken localParamFormatToken = null;
/*  547 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = null;
/*  548 */     SrvParamsToken localSrvParamsToken = null;
/*  549 */     Object[] arrayOfObject = new Object[0];
/*  550 */     if (paramSrvLanguageToken.hasParams())
/*      */     {
/*      */       try
/*      */       {
/*  556 */         localParamFormatToken = (ParamFormatToken)paramSrvSession.receive();
/*  557 */         localSrvParamsToken = (SrvParamsToken)paramSrvSession.receive();
/*      */       }
/*      */       catch (ClassCastException localClassCastException)
/*      */       {
/*  561 */         error(paramSrvSession, new SrvProtocolException("Did not get PARAMFMT, PARAMFMT2, PARAMS tokens for LANGUAGE request"));
/*      */ 
/*  563 */         return;
/*      */       }
/*      */       catch (IOException localIOException1)
/*      */       {
/*  567 */         error(paramSrvSession, localIOException1);
/*  568 */         return;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  574 */         localSrvJavaTypeFormatter = new SrvJavaTypeFormatter((SrvFormatToken)localParamFormatToken, paramSrvSession.getClientCapability());
/*  575 */         arrayOfObject = localSrvJavaTypeFormatter.convertData(localSrvParamsToken);
/*      */       }
/*      */       catch (IOException localIOException2)
/*      */       {
/*  579 */         error(paramSrvSession, localIOException2);
/*  580 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  585 */     if (paramSrvLanguageToken.getLanguage() == null)
/*      */     {
/*      */       try
/*      */       {
/*  590 */         sendMessage(paramSrvSession, 32001, "Null command was issued. Please try again.", null, 0);
/*      */ 
/*  592 */         sendDone(paramSrvSession, -1, true, true, false);
/*      */       }
/*      */       catch (IOException localIOException3)
/*      */       {
/*  596 */         error(paramSrvSession, localIOException3);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*  601 */       handleLanguage(paramSrvSession, paramSrvLanguageToken.getLanguage().trim(), arrayOfObject);
/*      */   }
/*      */ 
/*      */   public void rpc(SrvSession paramSrvSession, SrvDbrpcToken paramSrvDbrpcToken)
/*      */   {
/*  615 */     SrvFormatToken localSrvFormatToken = null;
/*  616 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = null;
/*  617 */     SrvParamsToken localSrvParamsToken = null;
/*  618 */     Object[] arrayOfObject = new Object[0];
/*      */ 
/*  620 */     if (paramSrvDbrpcToken.hasParams())
/*      */     {
/*      */       try
/*      */       {
/*  626 */         localSrvFormatToken = (SrvFormatToken)paramSrvSession.receive();
/*      */ 
/*  633 */         paramSrvDbrpcToken._hasWidetableParams = localSrvFormatToken instanceof SrvParamFormat2Token;
/*      */ 
/*  635 */         localSrvParamsToken = (SrvParamsToken)paramSrvSession.receive();
/*      */       }
/*      */       catch (ClassCastException localClassCastException)
/*      */       {
/*  639 */         error(paramSrvSession, new SrvProtocolException("Did not get PARAMFMT,PARAMFMT2,PARAMS tokens for RPC request"));
/*  640 */         return;
/*      */       }
/*      */       catch (IOException localIOException1)
/*      */       {
/*  644 */         error(paramSrvSession, localIOException1);
/*  645 */         return;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  651 */         localSrvJavaTypeFormatter = new SrvJavaTypeFormatter(localSrvFormatToken, paramSrvSession.getClientCapability());
/*  652 */         arrayOfObject = localSrvJavaTypeFormatter.convertData(localSrvParamsToken);
/*      */       }
/*      */       catch (IOException localIOException2)
/*      */       {
/*  656 */         error(paramSrvSession, localIOException2);
/*  657 */         return;
/*      */       }
/*      */     }
/*  660 */     String str = paramSrvDbrpcToken.getName();
/*  661 */     if (str.equals("sp_regwatch"))
/*      */     {
/*  666 */       addRegprocListener(paramSrvSession, (String)arrayOfObject[0], ((Short)arrayOfObject[1]).intValue());
/*      */       try
/*      */       {
/*  670 */         sendRPCParams(paramSrvSession, 0, null, null, true);
/*      */       }
/*      */       catch (Exception localException1)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*  678 */     else if (str.equals("sp_regnowatch"))
/*      */     {
/*  682 */       boolean bool = removeRegprocListener(paramSrvSession, (String)arrayOfObject[0]);
/*      */       try
/*      */       {
/*  686 */         if (!bool)
/*      */         {
/*  689 */           paramSrvSession.send(createMessage(paramSrvSession, 16126, "This connection was not registered for procedure " + arrayOfObject[0], "Unknown", "sp_regnowatch", 0));
/*      */         }
/*      */ 
/*  693 */         sendRPCParams(paramSrvSession, 0, null, null, false);
/*  694 */         sendDone(paramSrvSession, 0, bool != true, true, true);
/*      */       }
/*      */       catch (Exception localException2)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  704 */       handleRPC(paramSrvSession, paramSrvDbrpcToken, localSrvFormatToken, arrayOfObject);
/*  705 */       notifyRegprocListener(str, arrayOfObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void bulk(SrvSession paramSrvSession, SrvDataInputStream paramSrvDataInputStream)
/*      */   {
/*  717 */     handleBulk(paramSrvSession, paramSrvDataInputStream);
/*      */   }
/*      */ 
/*      */   public void attention(SrvSession paramSrvSession)
/*      */   {
/*  728 */     handleAttention(paramSrvSession);
/*      */   }
/*      */ 
/*      */   public void error(SrvSession paramSrvSession, IOException paramIOException)
/*      */   {
/*  738 */     handleError(paramSrvSession, paramIOException);
/*      */   }
/*      */ 
/*      */   public abstract void handleLogin(SrvSession paramSrvSession, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt);
/*      */ 
/*      */   public abstract void handleLanguage(SrvSession paramSrvSession, String paramString, Object[] paramArrayOfObject);
/*      */ 
/*      */   protected void handleError(SrvSession paramSrvSession, IOException paramIOException)
/*      */   {
/*      */     try
/*      */     {
/*  784 */       if ((paramIOException instanceof EOFException) || (paramIOException instanceof SrvProtocolException))
/*      */       {
/*  787 */         paramSrvSession.close();
/*  788 */         removeSession(paramSrvSession);
/*      */       }
/*  790 */       else if (paramIOException instanceof SrvTypeException)
/*      */       {
/*  794 */         paramSrvSession.send(createMessage(paramSrvSession, 32000, "Server error: " + paramIOException, "Unknown", "", 0));
/*  795 */         paramSrvSession.close();
/*  796 */         removeSession(paramSrvSession);
/*      */       }
/*  798 */       else if (paramIOException instanceof IOException)
/*      */       {
/*  800 */         paramSrvSession.close();
/*  801 */         removeSession(paramSrvSession);
/*      */       }
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void declareCursor(SrvSession paramSrvSession, SrvCurDeclareToken paramSrvCurDeclareToken, boolean paramBoolean)
/*      */   {
/*  812 */     SrvFormatToken localSrvFormatToken = null;
/*      */ 
/*  817 */     SrvCursor localSrvCursor = new SrvCursor(paramSrvCurDeclareToken instanceof SrvCurDeclare3Token);
/*  818 */     localSrvCursor.setName(paramSrvCurDeclareToken._cursorName);
/*  819 */     localSrvCursor.setOptions(paramSrvCurDeclareToken._options);
/*  820 */     localSrvCursor.setStatement(paramSrvCurDeclareToken._query);
/*      */ 
/*  823 */     if (paramSrvCurDeclareToken._status == 1)
/*      */     {
/*      */       try
/*      */       {
/*  828 */         localSrvFormatToken = (SrvFormatToken)paramSrvSession.receive();
/*      */       }
/*      */       catch (ClassCastException localClassCastException)
/*      */       {
/*  832 */         error(paramSrvSession, new SrvProtocolException("Did not get PARAMFMT,PARAMFMT2,PARAMS tokens for RPC request"));
/*  833 */         return;
/*      */       }
/*      */       catch (IOException localIOException1)
/*      */       {
/*  837 */         error(paramSrvSession, localIOException1);
/*  838 */         return;
/*      */       }
/*      */ 
/*  845 */       if ((paramBoolean) && (!paramSrvSession.inputStreamAvailable()))
/*      */       {
/*  847 */         paramBoolean = false;
/*      */       }
/*      */     }
/*      */ 
/*  851 */     synchronized (this)
/*      */     {
/*  853 */       _nextCursorID += 1;
/*  854 */       localSrvCursor.setID(_nextCursorID);
/*      */     }
/*      */ 
/*  858 */     paramSrvSession._cursors.put(new Integer(localSrvCursor.getID()), localSrvCursor);
/*      */ 
/*  863 */     boolean bool1 = handleCursorDeclare(paramSrvSession, localSrvCursor);
/*      */     try
/*      */     {
/*      */       boolean bool2;
/*  867 */       if (bool1 == true)
/*      */       {
/*  871 */         localSrvCursor.clearCurInfoStatus();
/*  872 */         localSrvCursor.buildYourOwnCurInfoStatus(-3);
/*      */         Object localObject1;
/*  873 */         if (localSrvCursor._declare3)
/*      */         {
/*  875 */           localObject1 = new SrvCurInfo3Token();
/*      */         }
/*      */         else
/*      */         {
/*  879 */           localObject1 = new SrvCurInfoToken();
/*      */         }
/*      */ 
/*  883 */         ((SrvCurInfoToken)localObject1).setCursor(localSrvCursor);
/*  884 */         ((SrvCurInfoToken)localObject1).setCommand(3);
/*  885 */         paramSrvSession.send((Token)localObject1);
/*  886 */         bool2 = false;
/*      */       }
/*      */       else
/*      */       {
/*  891 */         paramSrvSession._cursors.remove(new Integer(localSrvCursor.getID()));
/*  892 */         bool2 = true;
/*      */       }
/*      */ 
/*  897 */       if (!paramBoolean)
/*      */       {
/*  899 */         paramSrvSession.sendDone(-1, bool2, true, false);
/*      */       }
/*      */       else
/*      */       {
/*  903 */         paramSrvSession.sendDone(-1, bool2, false, false);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException2)
/*      */     {
/*  910 */       error(paramSrvSession, localIOException2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void processCurInfo(SrvSession paramSrvSession, SrvCurInfoToken paramSrvCurInfoToken, boolean paramBoolean)
/*      */   {
/*      */     SrvCursor localSrvCursor;
/*  923 */     if (paramSrvCurInfoToken._command == 4)
/*      */     {
/*  926 */       Enumeration localEnumeration = paramSrvSession._cursors.elements();
/*  927 */       while (localEnumeration.hasMoreElements())
/*      */       {
/*  929 */         localSrvCursor = (SrvCursor)localEnumeration.nextElement();
/*  930 */         reportCursorStatus(paramSrvSession, localSrvCursor, paramSrvCurInfoToken._command, true);
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  937 */         if (!paramBoolean)
/*      */         {
/*  939 */           paramSrvSession.sendDone(-1, false, true, false);
/*      */         }
/*      */         else
/*      */         {
/*  943 */           paramSrvSession.sendDone(-1, false, false, false);
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  948 */         error(paramSrvSession, localIOException);
/*      */       }
/*      */ 
/*  951 */       return;
/*      */     }
/*      */ 
/*  960 */     if (paramSrvCurInfoToken._curId == 0)
/*      */     {
/*  962 */       localSrvCursor = retrieveCursor(paramSrvSession, paramSrvCurInfoToken._cursorName);
/*      */     }
/*      */     else
/*      */     {
/*  966 */       localSrvCursor = (SrvCursor)paramSrvSession._cursors.get(new Integer(paramSrvCurInfoToken._curId));
/*      */     }
/*      */ 
/*  969 */     if (localSrvCursor == null)
/*      */     {
/*  971 */       return;
/*      */     }
/*      */ 
/*  974 */     switch (paramSrvCurInfoToken._command)
/*      */     {
/*      */     case 1:
/*  979 */       if (paramSrvCurInfoToken._fetchSize <= 0)
/*      */         return;
/*  981 */       localSrvCursor.setFetchSize(paramSrvCurInfoToken._fetchSize);
/*  982 */       reportCursorStatus(paramSrvSession, localSrvCursor, paramSrvCurInfoToken._command, paramBoolean); break;
/*      */     case 2:
/*  987 */       reportCursorStatus(paramSrvSession, localSrvCursor, paramSrvCurInfoToken._command, paramBoolean);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void reportCursorStatus(SrvSession paramSrvSession, SrvCursor paramSrvCursor, int paramInt, boolean paramBoolean)
/*      */   {
/*      */     try
/*      */     {
/* 1004 */       paramSrvCursor.buildYourOwnCurInfoStatus(paramInt);
/*      */       Object localObject;
/* 1007 */       if (paramSrvCursor._declare3)
/*      */       {
/* 1009 */         localObject = new SrvCurInfo3Token();
/*      */       }
/*      */       else
/*      */       {
/* 1013 */         localObject = new SrvCurInfoToken();
/*      */       }
/*      */ 
/* 1016 */       ((SrvCurInfoToken)localObject).setCursor(paramSrvCursor);
/* 1017 */       ((SrvCurInfoToken)localObject).setCommand(3);
/* 1018 */       paramSrvSession.send((Token)localObject);
/*      */ 
/* 1020 */       if (paramInt != 4)
/*      */       {
/* 1024 */         if (!paramBoolean)
/*      */         {
/* 1026 */           paramSrvSession.sendDone(-1, false, true, false);
/*      */         }
/*      */         else
/*      */         {
/* 1030 */           paramSrvSession.sendDone(-1, false, false, false);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1038 */       error(paramSrvSession, localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void openCursor(SrvSession paramSrvSession, SrvCurOpenToken paramSrvCurOpenToken, boolean paramBoolean)
/*      */   {
/* 1046 */     SrvFormatToken localSrvFormatToken = null;
/* 1047 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = null;
/* 1048 */     SrvParamsToken localSrvParamsToken = null;
/* 1049 */     Object[] arrayOfObject = new Object[0];
/* 1050 */     boolean bool = false;
/*      */ 
/* 1053 */     if (paramSrvCurOpenToken.hasParams())
/*      */     {
/*      */       try
/*      */       {
/* 1059 */         localSrvFormatToken = (SrvFormatToken)paramSrvSession.receive();
/* 1060 */         localSrvParamsToken = (SrvParamsToken)paramSrvSession.receive();
/* 1061 */         paramBoolean = false;
/*      */       }
/*      */       catch (ClassCastException localClassCastException)
/*      */       {
/* 1065 */         error(paramSrvSession, new SrvProtocolException("Did not get PARAMFMT,PARAMFMT2,PARAMS tokens for RPC request"));
/* 1066 */         return;
/*      */       }
/*      */       catch (IOException localIOException1)
/*      */       {
/* 1070 */         error(paramSrvSession, localIOException1);
/* 1071 */         return;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1077 */         localSrvJavaTypeFormatter = new SrvJavaTypeFormatter(localSrvFormatToken, paramSrvSession.getClientCapability());
/* 1078 */         arrayOfObject = localSrvJavaTypeFormatter.convertData(localSrvParamsToken);
/*      */       }
/*      */       catch (IOException localIOException2)
/*      */       {
/* 1082 */         error(paramSrvSession, localIOException2);
/* 1083 */         return;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*      */       SrvCursor localSrvCursor;
/* 1099 */       if (paramSrvCurOpenToken._curId == 0)
/*      */       {
/* 1101 */         localSrvCursor = retrieveCursor(paramSrvSession, paramSrvCurOpenToken._cursorName);
/*      */       }
/*      */       else
/*      */       {
/* 1105 */         localSrvCursor = (SrvCursor)paramSrvSession._cursors.get(new Integer(paramSrvCurOpenToken._curId));
/*      */       }
/*      */ 
/* 1109 */       if (localSrvCursor == null)
/*      */       {
/* 1111 */         return;
/*      */       }
/*      */ 
/* 1116 */       buildCursorResultSet(paramSrvSession, localSrvCursor, arrayOfObject);
/*      */ 
/* 1119 */       if ((localSrvCursor.getRowFormat() != null) && (localSrvCursor.getCursorResultSet() != null))
/*      */       {
/* 1121 */         bool = false;
/*      */       }
/*      */       else
/*      */       {
/* 1125 */         bool = true;
/*      */       }
/*      */       Object localObject;
/* 1130 */       if (localSrvCursor._declare3)
/*      */       {
/* 1132 */         localObject = new SrvCurInfo3Token();
/*      */       }
/*      */       else
/*      */       {
/* 1136 */         localObject = new SrvCurInfoToken();
/*      */       }
/*      */ 
/* 1139 */       ((SrvCurInfoToken)localObject).setCursor(localSrvCursor);
/*      */ 
/* 1141 */       if (!bool)
/*      */       {
/* 1145 */         paramSrvSession.send(localSrvCursor.getRowFormat());
/* 1146 */         localSrvCursor.setState(1);
/*      */       }
/*      */       else
/*      */       {
/* 1152 */         localSrvCursor.setState(2);
/*      */       }
/* 1154 */       localSrvCursor.buildYourOwnCurInfoStatus(2);
/* 1155 */       ((SrvCurInfoToken)localObject).setCommand(3);
/* 1156 */       paramSrvSession.send((Token)localObject);
/*      */ 
/* 1161 */       if (!paramBoolean)
/*      */       {
/* 1163 */         paramSrvSession.sendDone(-1, bool, true, false);
/*      */       }
/*      */       else
/*      */       {
/* 1167 */         paramSrvSession.sendDone(-1, bool, false, false);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException3)
/*      */     {
/* 1174 */       error(paramSrvSession, localIOException3);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void closeCursor(SrvSession paramSrvSession, SrvCurCloseToken paramSrvCurCloseToken)
/*      */   {
/* 1188 */     boolean bool = (paramSrvCurCloseToken._options & 0x1) != 0;
/*      */     SrvCursor localSrvCursor;
/* 1191 */     if (paramSrvCurCloseToken._curId == 0)
/*      */     {
/* 1193 */       localSrvCursor = retrieveCursor(paramSrvSession, paramSrvCurCloseToken._cursorName);
/*      */     }
/*      */     else
/*      */     {
/* 1197 */       localSrvCursor = (SrvCursor)paramSrvSession._cursors.get(new Integer(paramSrvCurCloseToken._curId));
/*      */     }
/*      */ 
/* 1200 */     if (localSrvCursor == null)
/*      */     {
/* 1202 */       return;
/*      */     }
/*      */ 
/* 1208 */     handleCursorClose(paramSrvSession, localSrvCursor, bool);
/*      */ 
/* 1210 */     localSrvCursor.setState(2);
/*      */     try
/*      */     {
/* 1216 */       localSrvCursor.buildYourOwnCurInfoStatus(2);
/*      */       Object localObject;
/* 1218 */       if (localSrvCursor._declare3)
/*      */       {
/* 1221 */         localObject = new SrvCurInfo3Token();
/*      */       }
/*      */       else
/*      */       {
/* 1225 */         localObject = new SrvCurInfoToken();
/*      */       }
/*      */ 
/* 1228 */       ((SrvCurInfoToken)localObject).setCursor(localSrvCursor);
/* 1229 */       ((SrvCurInfoToken)localObject).setCommand(3);
/* 1230 */       paramSrvSession.send((Token)localObject);
/*      */ 
/* 1233 */       if (bool)
/*      */       {
/* 1238 */         paramSrvSession._cursors.remove(new Integer(localSrvCursor.getID()));
/*      */ 
/* 1240 */         localSrvCursor.setState(3);
/* 1241 */         localSrvCursor.buildYourOwnCurInfoStatus(2);
/* 1242 */         ((SrvCurInfoToken)localObject).setCursor(localSrvCursor);
/* 1243 */         ((SrvCurInfoToken)localObject).setCommand(3);
/* 1244 */         paramSrvSession.send((Token)localObject);
/*      */       }
/*      */ 
/* 1247 */       paramSrvSession.sendDone(0, false, true, false);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1253 */       error(paramSrvSession, localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void cursorFetch(SrvSession paramSrvSession, SrvCurFetchToken paramSrvCurFetchToken)
/*      */   {
/*      */     SrvCursor localSrvCursor;
/* 1262 */     if (paramSrvCurFetchToken._curId == 0)
/*      */     {
/* 1264 */       localSrvCursor = retrieveCursor(paramSrvSession, paramSrvCurFetchToken._cursorName);
/*      */     }
/*      */     else
/*      */     {
/* 1268 */       localSrvCursor = (SrvCursor)paramSrvSession._cursors.get(new Integer(paramSrvCurFetchToken._curId));
/*      */     }
/*      */ 
/* 1271 */     if (localSrvCursor == null)
/*      */     {
/* 1273 */       return;
/*      */     }
/*      */ 
/* 1278 */     localSrvCursor.fetchRows(paramSrvSession, paramSrvCurFetchToken);
/*      */   }
/*      */ 
/*      */   public void cursorUpdate(SrvSession paramSrvSession, SrvCurUpdateToken paramSrvCurUpdateToken)
/*      */   {
/* 1285 */     SrvFormatToken localSrvFormatToken = null;
/* 1286 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = null;
/* 1287 */     SrvParamsToken localSrvParamsToken = null;
/* 1288 */     Object[] arrayOfObject = new Object[0];
/*      */     SrvCursor localSrvCursor;
/* 1291 */     if (paramSrvCurUpdateToken._curId == 0)
/*      */     {
/* 1293 */       localSrvCursor = retrieveCursor(paramSrvSession, paramSrvCurUpdateToken._cursorName);
/*      */     }
/*      */     else
/*      */     {
/* 1297 */       localSrvCursor = (SrvCursor)paramSrvSession._cursors.get(new Integer(paramSrvCurUpdateToken._curId));
/*      */     }
/*      */ 
/* 1300 */     if (localSrvCursor == null)
/*      */     {
/* 1302 */       return;
/*      */     }
/*      */ 
/* 1305 */     if (paramSrvCurUpdateToken._status == 1)
/*      */     {
/*      */       try
/*      */       {
/* 1310 */         Token localToken = paramSrvSession.receive();
/*      */ 
/* 1312 */         if (localToken instanceof SrvKeyToken)
/*      */         {
/* 1314 */           localSrvFormatToken = (SrvFormatToken)paramSrvSession.receive();
/*      */         }
/*      */         else
/*      */         {
/* 1318 */           localSrvFormatToken = (SrvFormatToken)localToken;
/*      */         }
/* 1320 */         localSrvParamsToken = (SrvParamsToken)paramSrvSession.receive();
/* 1321 */         localSrvJavaTypeFormatter = new SrvJavaTypeFormatter(localSrvFormatToken, paramSrvSession.getClientCapability());
/* 1322 */         arrayOfObject = localSrvJavaTypeFormatter.convertData(localSrvParamsToken);
/*      */       }
/*      */       catch (ClassCastException localClassCastException)
/*      */       {
/* 1326 */         error(paramSrvSession, new SrvProtocolException("Did not get PARAMFMT,PARAMFMT2,PARAMS tokens for RPC request"));
/* 1327 */         return;
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1331 */         error(paramSrvSession, localIOException);
/* 1332 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1336 */     localSrvCursor.updateCurrentPosition(paramSrvSession, paramSrvCurUpdateToken._tableName, paramSrvCurUpdateToken._stmt, arrayOfObject);
/*      */   }
/*      */ 
/*      */   public void cursorDelete(SrvSession paramSrvSession, SrvCurDeleteToken paramSrvCurDeleteToken)
/*      */   {
/*      */     SrvCursor localSrvCursor;
/* 1346 */     if (paramSrvCurDeleteToken._curId == 0)
/*      */     {
/* 1348 */       localSrvCursor = retrieveCursor(paramSrvSession, paramSrvCurDeleteToken._cursorName);
/*      */     }
/*      */     else
/*      */     {
/* 1352 */       localSrvCursor = (SrvCursor)paramSrvSession._cursors.get(new Integer(paramSrvCurDeleteToken._curId));
/*      */     }
/*      */ 
/* 1355 */     if (localSrvCursor == null)
/*      */     {
/* 1357 */       return;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1363 */       Token localToken = paramSrvSession.receive();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1367 */       error(paramSrvSession, localIOException);
/* 1368 */       return;
/*      */     }
/*      */ 
/* 1371 */     localSrvCursor.deleteCurrentPosition(paramSrvSession);
/*      */   }
/*      */ 
/*      */   protected SrvCursor retrieveCursor(SrvSession paramSrvSession, String paramString)
/*      */   {
/* 1377 */     SrvCursor localSrvCursor = null;
/* 1378 */     int i = 0;
/*      */ 
/* 1380 */     Object localObject = paramSrvSession._cursors.keys();
/*      */     do { if (!((Enumeration)localObject).hasMoreElements())
/*      */         break label66;
/* 1382 */       Integer localInteger = (Integer)((Enumeration)localObject).nextElement();
/* 1383 */       localSrvCursor = (SrvCursor)paramSrvSession._cursors.get(localInteger); }
/* 1384 */     while (!paramString.equals(localSrvCursor.getName()));
/*      */ 
/* 1386 */     i = 1;
/*      */ 
/* 1391 */     if (i == 0)
/*      */     {
/*      */       try
/*      */       {
/* 1395 */         label66: localObject = "No cursor found with name " + paramString;
/* 1396 */         sendMessage(paramSrvSession, 32000, (String)localObject, null, 1);
/* 1397 */         sendDone(paramSrvSession, -1, true, true, true);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1403 */         error(paramSrvSession, localIOException);
/*      */       }
/*      */     }
/* 1406 */     return (SrvCursor)localSrvCursor;
/*      */   }
/*      */ 
/*      */   public void dynamicRequest(SrvSession paramSrvSession, DynamicToken paramDynamicToken)
/*      */   {
/* 1419 */     Object[] arrayOfObject = new Object[0];
/*      */ 
/* 1428 */     if ((paramDynamicToken._status & 0x1) != 0)
/*      */     {
/*      */       try
/*      */       {
/* 1432 */         SrvFormatToken localSrvFormatToken = (SrvFormatToken)paramSrvSession.receive();
/* 1433 */         SrvParamsToken localSrvParamsToken = (SrvParamsToken)paramSrvSession.receive();
/* 1434 */         SrvJavaTypeFormatter localSrvJavaTypeFormatter = new SrvJavaTypeFormatter(localSrvFormatToken, paramSrvSession.getClientCapability());
/* 1435 */         arrayOfObject = localSrvJavaTypeFormatter.convertData(localSrvParamsToken);
/*      */       }
/*      */       catch (ClassCastException localClassCastException)
/*      */       {
/* 1439 */         error(paramSrvSession, new SrvProtocolException("Did not get PARAMFMT,PARAMFMT2,PARAMS tokens for dynamic request"));
/* 1440 */         return;
/*      */       }
/*      */       catch (IOException localIOException1)
/*      */       {
/* 1444 */         error(paramSrvSession, localIOException1);
/* 1445 */         return;
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*      */       DynamicToken localDynamicToken;
/*      */       try
/*      */       {
/* 1454 */         localDynamicToken = new DynamicToken(32, paramDynamicToken._name, null, false, false, false, false);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/* 1458 */         error(paramSrvSession, new SrvProtocolException("Error creating dynamic acknowledgement"));
/* 1459 */         return;
/*      */       }
/*      */ 
/* 1462 */       paramSrvSession.send(localDynamicToken);
/*      */ 
/* 1467 */       switch (paramDynamicToken._type)
/*      */       {
/*      */       case 1:
/* 1470 */         handleDynamicPrepare(paramSrvSession, paramDynamicToken._name, paramDynamicToken._body);
/* 1471 */         break;
/*      */       case 2:
/* 1474 */         handleDynamicExecute(paramSrvSession, paramDynamicToken._name, arrayOfObject);
/* 1475 */         break;
/*      */       case 4:
/* 1478 */         handleDynamicDeallocate(paramSrvSession, paramDynamicToken._name);
/* 1479 */         break;
/*      */       case 3:
/*      */       default:
/* 1482 */         error(paramSrvSession, new SrvProtocolException("Dynamic request not implemented: " + paramDynamicToken._type));
/* 1483 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException2)
/*      */     {
/* 1489 */       error(paramSrvSession, localIOException2);
/* 1490 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleDynamicPrepare(SrvSession paramSrvSession, String paramString1, String paramString2)
/*      */   {
/*      */     try
/*      */     {
/* 1504 */       sendDone(paramSrvSession, -1, true, true, false);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1508 */       error(paramSrvSession, localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleDynamicExecute(SrvSession paramSrvSession, String paramString, Object[] paramArrayOfObject)
/*      */   {
/*      */     try
/*      */     {
/* 1521 */       sendDone(paramSrvSession, -1, true, true, false);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1525 */       error(paramSrvSession, localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleDynamicDeallocate(SrvSession paramSrvSession, String paramString)
/*      */   {
/*      */     try
/*      */     {
/* 1538 */       sendDone(paramSrvSession, -1, true, true, false);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1542 */       error(paramSrvSession, localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean handleCursorDeclare(SrvSession paramSrvSession, SrvCursor paramSrvCursor)
/*      */   {
/* 1557 */     return false;
/*      */   }
/*      */ 
/*      */   public void buildCursorResultSet(SrvSession paramSrvSession, SrvCursor paramSrvCursor, Object[] paramArrayOfObject)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void handleCursorClose(SrvSession paramSrvSession, SrvCursor paramSrvCursor, boolean paramBoolean)
/*      */   {
/*      */   }
/*      */ 
/*      */   public abstract void handleRPC(SrvSession paramSrvSession, SrvDbrpcToken paramSrvDbrpcToken, Object[] paramArrayOfObject);
/*      */ 
/*      */   public void handleRPC(SrvSession paramSrvSession, SrvDbrpcToken paramSrvDbrpcToken, SrvFormatToken paramSrvFormatToken, Object[] paramArrayOfObject)
/*      */   {
/* 1614 */     handleRPC(paramSrvSession, paramSrvDbrpcToken, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public abstract void handleDisconnect(SrvSession paramSrvSession, SrvLogoutToken paramSrvLogoutToken);
/*      */ 
/*      */   public abstract void handleBulk(SrvSession paramSrvSession, SrvDataInputStream paramSrvDataInputStream);
/*      */ 
/*      */   public abstract void handleAttention(SrvSession paramSrvSession);
/*      */ 
/*      */   protected void sendLogin(SrvSession paramSrvSession, SrvCapabilityToken paramSrvCapabilityToken, String paramString, boolean paramBoolean, int paramInt)
/*      */     throws IOException
/*      */   {
/* 1658 */     if (paramSrvCapabilityToken == null)
/*      */     {
/* 1660 */       paramSrvCapabilityToken = paramSrvSession.getClientCapability();
/*      */     }
/*      */ 
/* 1663 */     int i = 0;
/*      */ 
/* 1666 */     if (paramBoolean)
/*      */     {
/* 1669 */       SrvEnvChangeToken localSrvEnvChangeToken = makeEnvChangeToken(paramInt);
/* 1670 */       paramSrvSession.send(localSrvEnvChangeToken);
/* 1671 */       if (paramSrvSession._encodingChanged)
/*      */       {
/* 1674 */         localSrvEnvChangeToken = new SrvEnvChangeToken();
/* 1675 */         localSrvEnvChangeToken.addVariable(3, "", paramSrvSession.getCharset());
/* 1676 */         paramSrvSession.send(localSrvEnvChangeToken);
/*      */       }
/* 1678 */       paramSrvSession.send(new SrvLoginAckToken(5, paramString, this._progVersion));
/*      */ 
/* 1681 */       paramSrvSession.send(paramSrvCapabilityToken);
/*      */     }
/*      */     else
/*      */     {
/* 1685 */       paramSrvSession.send(new SrvLoginAckToken(6, paramString, this._progVersion));
/*      */ 
/* 1688 */       i = 2;
/*      */     }
/* 1690 */     paramSrvSession.send(new SrvDoneToken(i, 0, 0));
/*      */   }
/*      */ 
/*      */   protected void sendLogNegotiateAck(SrvSession paramSrvSession)
/*      */     throws IOException
/*      */   {
/* 1704 */     paramSrvSession.send(new SrvLoginAckToken(7, this._server, this._progVersion));
/*      */   }
/*      */ 
/*      */   public DataFormat describeColumn(SrvSession paramSrvSession, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3, int paramInt4, String paramString6)
/*      */   {
/* 1728 */     int i = 0;
/*      */ 
/* 1730 */     SrvCapabilityToken localSrvCapabilityToken = paramSrvSession.getClientCapability();
/*      */ 
/* 1733 */     if (paramBoolean1)
/*      */     {
/* 1735 */       i |= 32;
/*      */     }
/*      */ 
/* 1738 */     if (paramBoolean2)
/*      */     {
/* 1740 */       i |= 16;
/*      */     }
/*      */     Object localObject;
/* 1743 */     if (!localSrvCapabilityToken._respCaps.get(45))
/*      */     {
/* 1745 */       localObject = new SrvRowDataFormat2(paramString1, paramString2, paramString3, paramString4, paramString5, paramInt1, i, paramInt2, paramInt3, paramInt4, paramString6);
/*      */     }
/*      */     else
/*      */     {
/* 1751 */       localObject = new SrvDataFormat(paramString1, paramInt1, i, paramInt2, paramInt3, paramInt4, paramString4, paramString6);
/*      */     }
/*      */ 
/* 1755 */     return (DataFormat)localObject;
/*      */   }
/*      */ 
/*      */   public SrvJavaTypeFormatter sendRowFormats(SrvSession paramSrvSession, DataFormat[] paramArrayOfDataFormat)
/*      */     throws IOException, SrvTypeException, SQLException
/*      */   {
/* 1770 */     Object localObject = null;
/* 1771 */     SrvCapabilityToken localSrvCapabilityToken = paramSrvSession.getClientCapability();
/*      */ 
/* 1775 */     if (!localSrvCapabilityToken._respCaps.get(45))
/*      */     {
/* 1777 */       if (paramArrayOfDataFormat[0] instanceof SrvRowDataFormat2)
/*      */       {
/* 1779 */         localObject = new SrvRowFormat2Token();
/*      */       }
/*      */       else
/*      */       {
/* 1783 */         localObject = new SrvRowFormatToken();
/*      */       }
/*      */ 
/*      */     }
/*      */     else {
/* 1788 */       localObject = new SrvRowFormatToken();
/*      */     }
/*      */ 
/* 1792 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = new SrvJavaTypeFormatter((SrvFormatToken)localObject, localSrvCapabilityToken);
/* 1793 */     for (int i = 0; i < paramArrayOfDataFormat.length; ++i)
/*      */     {
/* 1795 */       localSrvJavaTypeFormatter.addFormat(paramArrayOfDataFormat[i]);
/*      */     }
/*      */ 
/* 1799 */     paramSrvSession.send((Token)localObject);
/*      */ 
/* 1805 */     return (SrvJavaTypeFormatter)localSrvJavaTypeFormatter;
/*      */   }
/*      */ 
/*      */   public void sendRow(SrvSession paramSrvSession, SrvJavaTypeFormatter paramSrvJavaTypeFormatter, Object[] paramArrayOfObject)
/*      */     throws IOException, SrvTypeException, SQLException
/*      */   {
/* 1825 */     SrvRowToken localSrvRowToken = new SrvRowToken();
/* 1826 */     paramSrvJavaTypeFormatter.convertData(localSrvRowToken, paramArrayOfObject);
/* 1827 */     paramSrvSession.send(localSrvRowToken);
/*      */   }
/*      */ 
/*      */   public int sendResults(SrvSession paramSrvSession, String[] paramArrayOfString, Object[][] paramArrayOfObject)
/*      */     throws IOException, SrvTypeException, SQLException
/*      */   {
/* 1842 */     return sendResults(paramSrvSession, paramArrayOfString, paramArrayOfObject, null);
/*      */   }
/*      */ 
/*      */   public int sendResults(SrvSession paramSrvSession, String[] paramArrayOfString1, Object[][] paramArrayOfObject, String[] paramArrayOfString2)
/*      */     throws IOException, SrvTypeException, SQLException
/*      */   {
/* 1871 */     Object localObject = null;
/* 1872 */     SrvCapabilityToken localSrvCapabilityToken = paramSrvSession.getClientCapability();
/* 1873 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = null;
/*      */ 
/* 1877 */     if (!localSrvCapabilityToken._respCaps.get(45))
/*      */     {
/* 1879 */       localObject = new SrvRowFormat2Token();
/* 1880 */       localSrvJavaTypeFormatter = new SrvJavaTypeFormatter((SrvRowFormat2Token)localObject, localSrvCapabilityToken);
/*      */     }
/*      */     else
/*      */     {
/* 1885 */       localObject = new SrvRowFormatToken();
/* 1886 */       localSrvJavaTypeFormatter = new SrvJavaTypeFormatter((SrvRowFormatToken)localObject, localSrvCapabilityToken);
/*      */     }
/*      */ 
/* 1892 */     if ((paramArrayOfString1 != null) && (paramArrayOfString1.length != paramArrayOfObject[0].length))
/*      */     {
/* 1894 */       throw new SrvTypeException("Number of names do not match number of data elements in results send");
/*      */     }
/* 1896 */     if ((paramArrayOfString2 != null) && (paramArrayOfString2.length != paramArrayOfObject[0].length))
/*      */     {
/* 1898 */       throw new SrvTypeException("Number of locales do not match number of data elements in results send");
/*      */     }
/*      */ 
/* 1901 */     for (int i = 0; i < paramArrayOfObject[0].length; ++i)
/*      */     {
/* 1903 */       j = 0;
/*      */ 
/* 1905 */       if (paramArrayOfObject[0][i] instanceof String)
/*      */       {
/* 1907 */         j = getMaxCharLength(paramArrayOfObject, i);
/*      */       }
/*      */ 
/* 1910 */       localSrvJavaTypeFormatter.addFormat(paramArrayOfObject[0][i], checkNullValue(paramArrayOfString1, i), 0, j, checkNullValue(paramArrayOfString2, i));
/*      */     }
/*      */ 
/* 1917 */     paramSrvSession.send((Token)localObject);
/* 1918 */     SrvRowToken localSrvRowToken = new SrvRowToken();
/* 1919 */     for (int j = 0; j < paramArrayOfObject.length; ++j)
/*      */     {
/* 1921 */       localSrvJavaTypeFormatter.convertData(localSrvRowToken, paramArrayOfObject[j]);
/* 1922 */       paramSrvSession.send(localSrvRowToken);
/*      */     }
/* 1924 */     return paramArrayOfObject.length;
/*      */   }
/*      */ 
/*      */   public int sendResults(SrvSession paramSrvSession, SrvDataFormat[] paramArrayOfSrvDataFormat, Object[][] paramArrayOfObject)
/*      */     throws IOException, SrvTypeException, SQLException
/*      */   {
/* 1950 */     SrvRowFormatToken localSrvRowFormatToken = null;
/* 1951 */     SrvCapabilityToken localSrvCapabilityToken = paramSrvSession.getClientCapability();
/* 1952 */     localSrvRowFormatToken = new SrvRowFormatToken();
/*      */ 
/* 1954 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = new SrvJavaTypeFormatter(localSrvRowFormatToken, localSrvCapabilityToken);
/*      */ 
/* 1959 */     if (paramArrayOfSrvDataFormat.length != paramArrayOfObject[0].length) {
/* 1960 */       throw new SrvTypeException("Number of SrvDataFormat do not match number of data elements in results send");
/*      */     }
/*      */ 
/* 1963 */     for (int i = 0; i < paramArrayOfObject[0].length; ++i)
/*      */     {
/* 1965 */       localSrvJavaTypeFormatter.addFormat(paramArrayOfSrvDataFormat[i]);
/*      */     }
/*      */ 
/* 1969 */     paramSrvSession.send((Token)localSrvRowFormatToken);
/* 1970 */     SrvRowToken localSrvRowToken = new SrvRowToken();
/* 1971 */     for (int j = 0; j < paramArrayOfObject.length; ++j)
/*      */     {
/* 1973 */       localSrvJavaTypeFormatter.convertData(localSrvRowToken, paramArrayOfObject[j]);
/* 1974 */       paramSrvSession.send(localSrvRowToken);
/*      */     }
/* 1976 */     return paramArrayOfObject.length;
/*      */   }
/*      */ 
/*      */   public int sendResults(SrvSession paramSrvSession, DataFormat[] paramArrayOfDataFormat, Object[][] paramArrayOfObject)
/*      */     throws IOException, SrvTypeException, SQLException
/*      */   {
/* 2007 */     Object localObject = null;
/* 2008 */     SrvCapabilityToken localSrvCapabilityToken = paramSrvSession.getClientCapability();
/*      */ 
/* 2012 */     if (!localSrvCapabilityToken._respCaps.get(45))
/*      */     {
/* 2018 */       if (paramArrayOfDataFormat[0] instanceof SrvRowDataFormat2)
/*      */       {
/* 2020 */         localObject = new SrvRowFormat2Token();
/*      */       }
/*      */       else
/*      */       {
/* 2024 */         localObject = new SrvRowFormatToken();
/*      */       }
/*      */ 
/*      */     }
/*      */     else {
/* 2029 */       localObject = new SrvRowFormatToken();
/*      */     }
/* 2031 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = new SrvJavaTypeFormatter((SrvFormatToken)localObject, localSrvCapabilityToken);
/*      */ 
/* 2036 */     if (paramArrayOfDataFormat.length != paramArrayOfObject[0].length) {
/* 2037 */       throw new SrvTypeException("Number of SrvDataFormat do not match number of data elements in results send");
/*      */     }
/*      */ 
/* 2040 */     for (int i = 0; i < paramArrayOfObject[0].length; ++i)
/*      */     {
/* 2042 */       localSrvJavaTypeFormatter.addFormat(paramArrayOfDataFormat[i]);
/*      */     }
/*      */ 
/* 2046 */     paramSrvSession.send((Token)localObject);
/* 2047 */     SrvRowToken localSrvRowToken = new SrvRowToken();
/* 2048 */     for (int j = 0; j < paramArrayOfObject.length; ++j)
/*      */     {
/* 2050 */       localSrvJavaTypeFormatter.convertData(localSrvRowToken, paramArrayOfObject[j]);
/* 2051 */       paramSrvSession.send(localSrvRowToken);
/*      */     }
/* 2053 */     return paramArrayOfObject.length;
/*      */   }
/*      */ 
/*      */   public int sendResults(SrvSession paramSrvSession, ResultSet paramResultSet)
/*      */     throws IOException, SrvTypeException, SQLException
/*      */   {
/* 2067 */     return sendResults(paramSrvSession, buildDataFormat(paramSrvSession, paramResultSet), paramResultSet);
/*      */   }
/*      */ 
/*      */   public int sendResults(SrvSession paramSrvSession, SrvDataFormat[] paramArrayOfSrvDataFormat, ResultSet paramResultSet)
/*      */     throws IOException, SrvTypeException, SQLException
/*      */   {
/* 2086 */     SrvCapabilityToken localSrvCapabilityToken = paramSrvSession.getClientCapability();
/* 2087 */     ResultSetMetaData localResultSetMetaData = paramResultSet.getMetaData();
/*      */     Object localObject1;
/*      */     Object localObject2;
/* 2092 */     if ((localResultSetMetaData instanceof RowFormat2Token) && (!localSrvCapabilityToken._respCaps.get(45)))
/*      */     {
/* 2095 */       localObject1 = new SrvRowFormat2Token();
/* 2096 */       localObject2 = new SrvSQLTypeFormatter2((SrvFormatToken)localObject1, paramSrvSession.getClientCapability());
/*      */     }
/*      */     else
/*      */     {
/* 2101 */       localObject1 = new SrvRowFormatToken();
/* 2102 */       localObject2 = new SrvSQLTypeFormatter((SrvFormatToken)localObject1, paramSrvSession.getClientCapability());
/*      */     }
/*      */ 
/* 2107 */     for (int i = 0; i < paramArrayOfSrvDataFormat.length; ++i)
/*      */     {
/* 2109 */       ((SrvSQLTypeFormatter)localObject2).addFormat(paramArrayOfSrvDataFormat[i]);
/*      */     }
/*      */ 
/* 2112 */     return sendResults(paramSrvSession, (SrvSQLTypeFormatter)localObject2, (RowFormatToken)localObject1, paramResultSet);
/*      */   }
/*      */ 
/*      */   public int sendResults(SrvSession paramSrvSession, DataFormat[] paramArrayOfDataFormat, ResultSet paramResultSet)
/*      */     throws IOException, SrvTypeException, SQLException
/*      */   {
/* 2134 */     SrvCapabilityToken localSrvCapabilityToken = paramSrvSession.getClientCapability();
/* 2135 */     ResultSetMetaData localResultSetMetaData = paramResultSet.getMetaData();
/*      */     Object localObject1;
/*      */     Object localObject2;
/* 2140 */     if ((localResultSetMetaData instanceof RowFormat2Token) && (!localSrvCapabilityToken._respCaps.get(45)))
/*      */     {
/* 2143 */       localObject1 = new SrvRowFormat2Token();
/* 2144 */       localObject2 = new SrvSQLTypeFormatter2((SrvFormatToken)localObject1, paramSrvSession.getClientCapability());
/*      */     }
/*      */     else
/*      */     {
/* 2149 */       localObject1 = new SrvRowFormatToken();
/* 2150 */       localObject2 = new SrvSQLTypeFormatter((SrvFormatToken)localObject1, paramSrvSession.getClientCapability());
/*      */     }
/*      */ 
/* 2155 */     for (int i = 0; i < paramArrayOfDataFormat.length; ++i)
/*      */     {
/* 2157 */       ((SrvSQLTypeFormatter)localObject2).addFormat(paramArrayOfDataFormat[i]);
/*      */     }
/*      */ 
/* 2160 */     return sendResults(paramSrvSession, (SrvSQLTypeFormatter)localObject2, (RowFormatToken)localObject1, paramResultSet);
/*      */   }
/*      */ 
/*      */   public int sendResults(SrvSession paramSrvSession, SrvFormatToken paramSrvFormatToken, Object[][] paramArrayOfObject)
/*      */     throws IOException, SrvTypeException, SQLException
/*      */   {
/* 2188 */     Object localObject = null;
/* 2189 */     SrvCapabilityToken localSrvCapabilityToken = paramSrvSession.getClientCapability();
/* 2190 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = null;
/*      */ 
/* 2195 */     if ((localSrvCapabilityToken._respCaps.get(45)) && 
/* 2197 */       (paramSrvFormatToken instanceof SrvRowFormat2Token))
/*      */     {
/* 2199 */       throw new SrvTypeException("Attempt to send a SrvRowFormat2 token when widetable has been disabled.");
/*      */     }
/*      */ 
/* 2204 */     if (paramSrvFormatToken instanceof SrvRowFormat2Token)
/*      */     {
/* 2206 */       localSrvJavaTypeFormatter = new SrvJavaTypeFormatter((SrvRowFormat2Token)paramSrvFormatToken, localSrvCapabilityToken);
/*      */     }
/*      */     else
/*      */     {
/* 2211 */       localSrvJavaTypeFormatter = new SrvJavaTypeFormatter((SrvRowFormatToken)paramSrvFormatToken, localSrvCapabilityToken);
/*      */     }
/*      */ 
/* 2221 */     if ((paramSrvFormatToken != null) && (paramSrvFormatToken.getFormatCount() != paramArrayOfObject[0].length))
/*      */     {
/* 2223 */       throw new SrvTypeException("Number of names do not match number of data elements in results send");
/*      */     }
/*      */ 
/* 2230 */     paramSrvSession.send((Token)paramSrvFormatToken);
/* 2231 */     SrvRowToken localSrvRowToken = new SrvRowToken();
/* 2232 */     for (int i = 0; i < paramArrayOfObject.length; ++i)
/*      */     {
/* 2234 */       localSrvJavaTypeFormatter.convertData(localSrvRowToken, paramArrayOfObject[i]);
/* 2235 */       paramSrvSession.send(localSrvRowToken);
/*      */     }
/* 2237 */     return paramArrayOfObject.length;
/*      */   }
/*      */ 
/*      */   public void sendParams(SrvSession paramSrvSession, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, int paramInt)
/*      */     throws IOException
/*      */   {
/* 2259 */     paramSrvSession.sendParams(paramArrayOfObject1, paramArrayOfObject2, paramInt);
/*      */   }
/*      */ 
/*      */   public void sendRPCParams(SrvSession paramSrvSession, int paramInt, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 2290 */     paramSrvSession.send(new SrvReturnStatusToken(paramInt));
/* 2291 */     if (paramArrayOfObject2 != null)
/*      */     {
/* 2293 */       sendParams(paramSrvSession, paramArrayOfObject1, paramArrayOfObject2, 1);
/* 2294 */       sendDone(paramSrvSession, 1, false, false, true);
/*      */     }
/* 2296 */     sendDone(paramSrvSession, 0, false, paramBoolean, true);
/*      */   }
/*      */ 
/*      */   public void sendMessage(SrvSession paramSrvSession, int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3)
/*      */     throws IOException
/*      */   {
/* 2319 */     paramSrvSession.send(createMessage(paramSrvSession, paramInt1, paramString1, this._server, paramString2, paramInt2, paramInt3));
/*      */   }
/*      */ 
/*      */   public void sendMessage(SrvSession paramSrvSession, int paramInt1, String paramString1, String paramString2, int paramInt2)
/*      */     throws IOException
/*      */   {
/* 2339 */     paramSrvSession.send(createMessage(paramSrvSession, paramInt1, paramString1, this._server, paramString2, paramInt2));
/*      */   }
/*      */ 
/*      */   public Token createMessage(SrvSession paramSrvSession, int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2)
/*      */   {
/* 2361 */     if (paramSrvSession.getClientCapability()._respCaps.get(2) == true) {
/* 2362 */       return new SrvErrorToken(paramInt1, 0, 20, paramString1, paramString2, paramString3, 1);
/*      */     }
/* 2364 */     return new SrvEedToken(paramInt1, 0, 20, "UNKWN", 0, paramString1, paramString2, paramString3, 1);
/*      */   }
/*      */ 
/*      */   public Token createMessage(SrvSession paramSrvSession, int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2, int paramInt3)
/*      */   {
/* 2386 */     if (paramSrvSession.getClientCapability()._respCaps.get(2) == true) {
/* 2387 */       return new SrvErrorToken(paramInt1, 0, paramInt3, paramString1, paramString2, paramString3, 1);
/*      */     }
/* 2389 */     return new SrvEedToken(paramInt1, 0, paramInt3, "UNKWN", 0, paramString1, paramString2, paramString3, 1);
/*      */   }
/*      */ 
/*      */   public DataFormat[] buildDataFormat(SrvSession paramSrvSession, ResultSet paramResultSet)
/*      */     throws SrvTypeException, SQLException, IOException
/*      */   {
/* 2405 */     SrvCapabilityToken localSrvCapabilityToken = paramSrvSession.getClientCapability();
/*      */ 
/* 2413 */     ResultSetMetaData localResultSetMetaData = paramResultSet.getMetaData();
/*      */     Object localObject1;
/*      */     Object localObject2;
/* 2414 */     if ((localResultSetMetaData instanceof RowFormat2Token) && (!localSrvCapabilityToken._respCaps.get(45)))
/*      */     {
/* 2417 */       localObject1 = new SrvRowFormat2Token();
/* 2418 */       localObject2 = new SrvSQLTypeFormatter2((SrvFormatToken)localObject1, paramSrvSession.getClientCapability());
/*      */     }
/*      */     else
/*      */     {
/* 2424 */       localObject1 = new SrvRowFormatToken();
/* 2425 */       localObject2 = new SrvSQLTypeFormatter((SrvFormatToken)localObject1, paramSrvSession.getClientCapability());
/*      */     }
/*      */ 
/* 2428 */     return (DataFormat)(DataFormat)((SrvSQLTypeFormatter)localObject2).buildDataFormat(paramResultSet);
/*      */   }
/*      */ 
/*      */   protected int getMaxCharLength(Object[][] paramArrayOfObject, int paramInt)
/*      */   {
/* 2441 */     int i = 0;
/* 2442 */     for (int j = 0; j < paramArrayOfObject.length; ++j)
/*      */     {
/* 2444 */       String str = (String)paramArrayOfObject[j][paramInt];
/* 2445 */       if (i >= str.length())
/*      */         continue;
/* 2447 */       i = str.length();
/*      */     }
/*      */ 
/* 2450 */     return i + 1;
/*      */   }
/*      */ 
/*      */   protected void sendDone(SrvSession paramSrvSession, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */     throws IOException
/*      */   {
/* 2470 */     paramSrvSession.sendDone(paramInt, paramBoolean1, paramBoolean2, paramBoolean3);
/*      */   }
/*      */ 
/*      */   protected SrvSession startSession(SrvSession paramSrvSession)
/*      */   {
/* 2481 */     paramSrvSession.addSrvRequestListener(this);
/* 2482 */     Thread localThread = new Thread(paramSrvSession);
/* 2483 */     Object localObject = this._sessions.put(paramSrvSession, localThread);
/* 2484 */     localThread.start();
/* 2485 */     return paramSrvSession;
/*      */   }
/*      */ 
/*      */   protected Thread getSession(SrvSession paramSrvSession)
/*      */   {
/* 2495 */     return (Thread)this._sessions.get(paramSrvSession);
/*      */   }
/*      */ 
/*      */   protected void removeSession(SrvSession paramSrvSession)
/*      */   {
/* 2506 */     paramSrvSession.removeSrvRequestListener(this);
/* 2507 */     paramSrvSession.close();
/* 2508 */     Thread localThread = getSession(paramSrvSession);
/* 2509 */     Object localObject = this._sessions.remove(paramSrvSession);
/*      */   }
/*      */ 
/*      */   protected void addRegprocListener(SrvSession paramSrvSession, String paramString, int paramInt)
/*      */   {
/* 2526 */     Hashtable localHashtable = null;
/* 2527 */     synchronized (this._regProcs)
/*      */     {
/* 2529 */       if (this._notifier == null)
/*      */       {
/* 2533 */         this._notifier = new SrvNotifier();
/* 2534 */         this._notifier.start();
/*      */       }
/*      */ 
/* 2538 */       localHashtable = (Hashtable)this._regProcs.get(paramString);
/* 2539 */       if (localHashtable == null)
/*      */       {
/* 2541 */         localHashtable = new Hashtable();
/* 2542 */         this._regProcs.put(paramString, localHashtable);
/*      */       }
/*      */     }
/*      */ 
/* 2546 */     ??? = (SrvEventListener)localHashtable.get(paramSrvSession);
/* 2547 */     if (??? != null)
/*      */     {
/* 2550 */       ((SrvEventListener)???)._options = paramInt;
/* 2551 */       return;
/*      */     }
/* 2553 */     ??? = new SrvEventListener(paramSrvSession, paramInt, localHashtable);
/*      */   }
/*      */ 
/*      */   protected boolean removeRegprocListener(SrvSession paramSrvSession, String paramString)
/*      */   {
/* 2562 */     Hashtable localHashtable = (Hashtable)this._regProcs.get(paramString);
/* 2563 */     if (localHashtable == null)
/*      */     {
/* 2565 */       return false;
/*      */     }
/* 2567 */     SrvEventListener localSrvEventListener = (SrvEventListener)localHashtable.get(paramSrvSession);
/* 2568 */     if (localSrvEventListener != null)
/*      */     {
/* 2570 */       localSrvEventListener.close();
/* 2571 */       return true;
/*      */     }
/* 2573 */     return false;
/*      */   }
/*      */ 
/*      */   protected void notifyRegprocListener(String paramString, Object[] paramArrayOfObject)
/*      */   {
/* 2579 */     Hashtable localHashtable = (Hashtable)this._regProcs.get(paramString);
/* 2580 */     if ((localHashtable == null) || (localHashtable.isEmpty()))
/*      */     {
/* 2582 */       return;
/*      */     }
/*      */ 
/* 2586 */     this._notifier.queueNotifications(localHashtable, paramString, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   protected void passthruResults(SrvSession paramSrvSession, Statement paramStatement, String paramString)
/*      */     throws IOException, SQLException
/*      */   {
/*      */     boolean bool;
/* 2603 */     if (paramString == null)
/*      */     {
/* 2605 */       bool = ((PreparedStatement)paramStatement).execute();
/*      */     }
/*      */     else
/*      */     {
/* 2609 */       bool = paramStatement.execute(paramString);
/*      */     }
/* 2611 */     int i = 0;
/* 2612 */     int j = 0;
/* 2613 */     int k = -2;
/*      */     do
/*      */     {
/* 2616 */       if (bool)
/*      */       {
/* 2618 */         sendDone(paramSrvSession, sendResults(paramSrvSession, paramStatement.getResultSet()), false, false, true);
/*      */ 
/* 2620 */         k = j;
/*      */       }
/*      */       else
/*      */       {
/* 2624 */         i = paramStatement.getUpdateCount();
/*      */ 
/* 2628 */         if (j == k + 1)
/*      */         {
/* 2630 */           sendDone(paramSrvSession, -1, false, false, true);
/*      */         }
/*      */         else
/*      */         {
/* 2634 */           sendDone(paramSrvSession, i, false, false, true);
/*      */         }
/*      */       }
/* 2637 */       bool = paramStatement.getMoreResults();
/* 2638 */       ++j;
/*      */     }
/* 2640 */     while ((bool) || (i != -1));
/* 2641 */     sendDone(paramSrvSession, -1, false, true, false);
/*      */   }
/*      */ 
/*      */   private int sendResults(SrvSession paramSrvSession, SrvSQLTypeFormatter paramSrvSQLTypeFormatter, RowFormatToken paramRowFormatToken, ResultSet paramResultSet)
/*      */     throws IOException, SQLException
/*      */   {
/* 2659 */     int i = paramResultSet.getMetaData().getColumnCount();
/*      */ 
/* 2661 */     if (i != ((SrvFormatToken)paramRowFormatToken).getFormatCount())
/*      */     {
/* 2663 */       throw new SrvTypeException("Number of SrvDataFormat do not match number of data elements in results send");
/*      */     }
/*      */ 
/* 2669 */     paramSrvSession.send(paramRowFormatToken);
/*      */ 
/* 2672 */     int j = 0;
/*      */     try
/*      */     {
/* 2675 */       while (paramResultSet.next())
/*      */       {
/* 2677 */         SrvRowToken localSrvRowToken = new SrvRowToken();
/* 2678 */         paramSrvSQLTypeFormatter.convertData(localSrvRowToken, paramResultSet);
/* 2679 */         paramSrvSession.send(localSrvRowToken);
/* 2680 */         ++j;
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 2685 */       throw new SrvProtocolException("SQL error on next row fetch: " + localSQLException);
/*      */     }
/*      */ 
/* 2688 */     return j;
/*      */   }
/*      */ 
/*      */   private SrvEnvChangeToken makeEnvChangeToken(int paramInt)
/*      */   {
/* 2696 */     String str = new Integer(paramInt).toString();
/* 2697 */     SrvEnvChangeToken localSrvEnvChangeToken = new SrvEnvChangeToken();
/* 2698 */     localSrvEnvChangeToken.addVariable(4, "", str);
/* 2699 */     return localSrvEnvChangeToken;
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/* 2710 */     this._running = true;
/* 2711 */     while (this._running)
/*      */     {
/*      */       try
/*      */       {
/* 2715 */         synchronized (this)
/*      */         {
/* 2717 */           super.wait();
/*      */         }
/*      */       }
/*      */       catch (InterruptedException localInterruptedException)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void shutdown()
/*      */   {
/* 2733 */     for (Enumeration localEnumeration = this._acceptors.elements(); localEnumeration.hasMoreElements(); )
/*      */     {
/* 2735 */       localObject = (SrvAcceptor)localEnumeration.nextElement();
/* 2736 */       ((SrvAcceptor)localObject).shutdown();
/*      */       try
/*      */       {
/* 2739 */         ((SrvAcceptor)localObject).join();
/*      */       }
/*      */       catch (InterruptedException localInterruptedException1)
/*      */       {
/*      */       }
/*      */     }
/*      */     Object localObject;
/* 2746 */     for (localEnumeration = this._sessions.keys(); localEnumeration.hasMoreElements(); )
/*      */     {
/* 2748 */       localObject = (SrvSession)localEnumeration.nextElement();
/* 2749 */       Thread localThread = (Thread)this._sessions.get(localObject);
/* 2750 */       ((SrvSession)localObject).close();
/*      */       try
/*      */       {
/* 2753 */         localThread.join();
/*      */       }
/*      */       catch (InterruptedException localInterruptedException2)
/*      */       {
/*      */       }
/*      */     }
/* 2759 */     this._running = false;
/* 2760 */     super.notifyAll();
/*      */   }
/*      */ 
/*      */   private String checkNullValue(String[] paramArrayOfString, int paramInt)
/*      */   {
/* 2765 */     if (paramArrayOfString == null)
/*      */     {
/* 2767 */       return null;
/*      */     }
/* 2769 */     return paramArrayOfString[paramInt];
/*      */   }
/*      */ 
/*      */   public void passthroughLanguage(SrvSession paramSrvSession, SrvPassthroughLanguageToken paramSrvPassthroughLanguageToken)
/*      */   {
/*      */   }
/*      */ 
/*      */   public byte[] nextNonce()
/*      */   {
/* 2789 */     byte[] arrayOfByte = new byte[32];
/* 2790 */     this._nonceGenerator.nextBytes(arrayOfByte);
/*      */     try
/*      */     {
/* 2794 */       if (this._messageDigest == null)
/* 2795 */         this._messageDigest = MessageDigest.getInstance("SHA-256");
/* 2796 */       arrayOfByte = this._messageDigest.digest(arrayOfByte);
/*      */     }
/*      */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*      */     {
/* 2802 */       throw new RuntimeException(localNoSuchAlgorithmException);
/*      */     }
/*      */ 
/* 2805 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   private void createCipherSuite()
/*      */   {
/* 2815 */     if (this._cipherSuite != null)
/*      */     {
/*      */       return;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 2827 */       this._cipherSuite = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding");
/*      */     }
/*      */     catch (GeneralSecurityException localGeneralSecurityException1)
/*      */     {
/*      */       try
/*      */       {
/* 2836 */         this._cipherSuite = Cipher.getInstance("RSA/SHA1/OAEP");
/*      */       }
/*      */       catch (GeneralSecurityException localGeneralSecurityException2)
/*      */       {
/* 2840 */         String str = "Failed to instantiate Cipher object. Transformation 'RSA/NONE/OAEPWithSHA1AndMGF1Padding'+or RSA/SHA1/OAEP is is not implemented by any of the loaded JCE providers.";
/*      */ 
/* 2844 */         throw new RuntimeException(str);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvReceiver
 * JD-Core Version:    0.5.4
 */