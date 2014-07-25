/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.ASAUDPUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.StreamTokenizer;
/*     */ import java.io.StringReader;
/*     */ import java.net.URLDecoder;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SybUrlManager extends SybUrlProviderImplBase
/*     */ {
/*     */   public static final String KEYWORD_JDBC = "jdbc";
/*     */   public static final String KEYWORD_SYBASE = "sybase";
/*     */   public static final String KEYWORD_LDAP = "ldap";
/*     */   public static final String KEYWORD_JNDI = "jndi";
/*     */   public static final String KEYWORD_SQLINI = "file";
/*     */   public static final String PACKAGE_PREFIX = "com.sybase.jdbc3.";
/*     */   private static final String JNDI_PROVIDER = "jdbc.SybJndiProvider";
/*     */   private static final String SQLINI_PROVIDER = "jdbc.SybSqlIniProvider";
/*  51 */   private Properties _userProps = null;
/*     */ 
/*  54 */   private String _url = null;
/*     */ 
/*  60 */   private String _providerName = null;
/*     */ 
/*  66 */   private String _urlRest = null;
/*     */ 
/*     */   public SybUrlManager(String paramString, Properties paramProperties, SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/*  71 */     this._url = paramString;
/*  72 */     this._sybProps = paramSybProperty;
/*  73 */     this._userProps = paramProperties;
/*  74 */     this._hostportList = new Vector();
/*     */ 
/*  76 */     validateUrl();
/*     */   }
/*     */ 
/*     */   public SybUrlProvider getUrlProvider()
/*     */     throws SQLException
/*     */   {
/*  88 */     String str1 = "com.sybase.jdbc3.";
/*  89 */     Object localObject = null;
/*  90 */     String str2 = null;
/*     */ 
/*  93 */     if (this._providerName == null)
/*     */     {
/*  95 */       return null;
/*     */     }
/*     */ 
/*  98 */     if (this._providerName.equals("jndi"))
/*     */     {
/* 100 */       int i = this._urlRest.indexOf(":");
/* 101 */       if (i == -1)
/*     */       {
/* 103 */         str2 = "ldap";
/*     */       }
/*     */       else
/*     */       {
/* 107 */         str2 = this._urlRest.substring(0, i);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 112 */       str2 = this._providerName;
/*     */     }
/* 114 */     if (str2.equals("ldap"))
/*     */     {
/* 118 */       str1 = str1 + "jdbc.SybJndiProvider";
/* 119 */       localObject = loadProvider(str1);
/*     */     }
/* 122 */     else if (str2.equals("file"))
/*     */     {
/* 126 */       this._providerName = "Tds";
/* 127 */       str1 = str1 + "jdbc.SybSqlIniProvider";
/* 128 */       localObject = loadProvider(str1);
/*     */     }
/*     */     else
/*     */     {
/* 134 */       localObject = this;
/*     */     }
/*     */ 
/* 148 */     if ((this._userProps == null) && (this._sybProps == null))
/*     */     {
/* 151 */       if ((str2.equals("ldap")) || (str2.equals("file")))
/*     */       {
/* 154 */         return localObject;
/*     */       }
/*     */ 
/* 158 */       ((SybUrlProvider)localObject).init(this._providerName, this._urlRest, this._userProps, this._sybProps);
/*     */     }
/*     */     else
/*     */     {
/* 165 */       ((SybUrlProvider)localObject).init(this._providerName, this._urlRest, this._userProps, this._sybProps);
/*     */     }
/*     */ 
/* 170 */     return (SybUrlProvider)localObject;
/*     */   }
/*     */ 
/*     */   private SybUrlProvider loadProvider(String paramString) throws SQLException
/*     */   {
/* 175 */     SybUrlProvider localSybUrlProvider = null;
/*     */     try
/*     */     {
/* 180 */       localSybUrlProvider = (SybUrlProvider)Class.forName(paramString).newInstance();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 186 */       ErrorMessage.raiseError("JZ0D7", paramString, localException.toString());
/*     */     }
/*     */ 
/* 189 */     return localSybUrlProvider;
/*     */   }
/*     */ 
/*     */   public void init(String paramString1, String paramString2, Properties paramProperties, SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/* 204 */     String str1 = null;
/* 205 */     String str2 = null;
/*     */ 
/* 209 */     if (paramString1.equals("shm"))
/*     */     {
/* 213 */       if (paramSybProperty != null)
/*     */       {
/* 215 */         paramSybProperty.setProperty(31, "com.sybase.shmem.ShmemSocketFactory");
/*     */       }
/*     */ 
/* 220 */       paramString1 = "Tds";
/*     */     }
/*     */ 
/* 223 */     this._protocol = loadProtocol(paramString1);
/*     */ 
/* 227 */     if (paramString2 != null)
/*     */     {
/* 229 */       int i = paramString2.indexOf('/');
/* 230 */       int j = paramString2.indexOf('?');
/* 231 */       int k = paramString2.indexOf(',');
/* 232 */       if ((i > -1) && (i < j))
/*     */       {
/* 234 */         str2 = paramString2.substring(0, i);
/* 235 */         this._dbName = paramString2.substring(i + 1, j);
/* 236 */         str1 = URLDecoder.decode(paramString2.substring(j + 1));
/*     */       }
/* 239 */       else if ((j > -1) && (((i > j) || (i == -1))))
/*     */       {
/* 244 */         str2 = paramString2.substring(0, j);
/* 245 */         str1 = URLDecoder.decode(paramString2.substring(j + 1));
/*     */       }
/* 248 */       else if ((i > -1) && (j == -1))
/*     */       {
/* 250 */         str2 = paramString2.substring(0, i);
/* 251 */         this._dbName = paramString2.substring(i + 1);
/*     */       }
/*     */       else
/*     */       {
/* 255 */         str2 = paramString2;
/*     */       }
/*     */ 
/* 259 */       if ((k > -1) && (((k < j) || (k < i) || ((i == -1) && (j == -1)))))
/*     */       {
/* 262 */         str2 = setHostPort(str2, k);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 270 */     if ((str1 != null) && (paramSybProperty != null))
/*     */     {
/* 272 */       this._sybProps.parsePropertyString(str1);
/*     */     }
/*     */ 
/* 276 */     this._hostportList.addElement(str2);
/* 277 */     if (this._sybProps == null)
/*     */       return;
/* 279 */     String str3 = this._sybProps.getString(73);
/* 280 */     if (str3 == null)
/*     */       return;
/* 282 */     ASAUDPUtil localASAUDPUtil = new ASAUDPUtil();
/* 283 */     String str4 = localASAUDPUtil.lookupServer(str3);
/* 284 */     this._hostportList.clear();
/* 285 */     this._hostportList.addElement(str4);
/*     */   }
/*     */ 
/*     */   private String setHostPort(String paramString, int paramInt)
/*     */   {
/* 298 */     String str = null;
/* 299 */     while (paramInt > -1)
/*     */     {
/* 301 */       str = paramString.substring(0, paramInt);
/* 302 */       this._hostportList.addElement(str);
/* 303 */       paramString = paramString.substring(paramInt + 1, paramString.length());
/* 304 */       paramInt = paramString.indexOf(',');
/* 305 */       if (paramInt == -1)
/*     */       {
/* 307 */         return paramString;
/*     */       }
/*     */     }
/* 310 */     return str;
/*     */   }
/*     */ 
/*     */   public static Protocol loadProtocol(String paramString)
/*     */     throws SQLException
/*     */   {
/* 321 */     String str = "com.sybase.jdbc3." + paramString.toLowerCase() + "." + paramString;
/*     */ 
/* 325 */     return ProtocolManager.getProtocol(str);
/*     */   }
/*     */ 
/*     */   private void validateUrl()
/*     */     throws SQLException
/*     */   {
/* 341 */     StreamTokenizer localStreamTokenizer = new StreamTokenizer(new StringReader(this._url));
/*     */ 
/* 344 */     localStreamTokenizer.wordChars(0, 255);
/* 345 */     localStreamTokenizer.whitespaceChars(58, 58);
/*     */     try
/*     */     {
/* 349 */       if (localStreamTokenizer.nextToken() != -3)
/*     */       {
/* 352 */         return;
/*     */       }
/* 354 */       if (localStreamTokenizer.sval.compareTo("jdbc") != 0)
/*     */       {
/* 357 */         return;
/*     */       }
/* 359 */       if (localStreamTokenizer.nextToken() != -3)
/*     */       {
/* 362 */         return;
/*     */       }
/* 364 */       if (localStreamTokenizer.sval.compareTo("sybase") != 0)
/*     */       {
/* 367 */         return;
/*     */       }
/* 369 */       if (localStreamTokenizer.nextToken() != -3)
/*     */       {
/* 371 */         ErrorMessage.raiseError("JZ0D4", this._url);
/*     */       }
/*     */ 
/* 374 */       this._providerName = localStreamTokenizer.sval;
/*     */       try
/*     */       {
/* 379 */         localStreamTokenizer.whitespaceChars(0, 0);
/* 380 */         localStreamTokenizer.resetSyntax();
/* 381 */         localStreamTokenizer.wordChars(0, 255);
/* 382 */         if (localStreamTokenizer.nextToken() == -3)
/*     */         {
/* 385 */           this._urlRest = localStreamTokenizer.sval.substring(1);
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (IOException localIOException1)
/*     */       {
/* 396 */         this._urlRest = null;
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException2)
/*     */     {
/* 401 */       SQLException localSQLException = new SQLException(localIOException2.toString());
/* 402 */       throw localSQLException;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybUrlManager
 * JD-Core Version:    0.5.4
 */