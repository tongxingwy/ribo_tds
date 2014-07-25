/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.Debug;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ import javax.naming.InvalidNameException;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.NameParser;
/*     */ import javax.naming.NamingEnumeration;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.directory.Attribute;
/*     */ import javax.naming.directory.Attributes;
/*     */ import javax.naming.directory.DirContext;
/*     */ import javax.naming.directory.InitialDirContext;
/*     */ 
/*     */ public class SybJndiProvider extends SybUrlProviderImplBase
/*     */ {
/*  50 */   private static final String[] SERVER_ATTRIBUTES = { "1.3.6.1.4.1.897.4.2.5", "1.3.6.1.4.1.897.4.2.15", "1.3.6.1.4.1.897.4.2.9", "1.3.6.1.4.1.897.4.2.10", "1.3.6.1.4.1.897.4.2.11", "1.3.6.1.4.1.897.4.2.16", "1.3.6.1.4.1.897.4.2.17", "1.3.6.1.4.1.897.4.2.18", "sybaseAddress", "sybaseHAservername", "sybaseJconnectProtocol", "sybaseJconnectProperty", "sybaseDatabaseName", "sybaseResourceManagerName", "sybaseResourceManagerType", "sybaseJdbcDataSourceInterface" };
/*     */   private static final int ADDRESS = 0;
/*     */   private static final int HASERVER = 1;
/*     */   private static final int PROTOCOL = 2;
/*     */   private static final int PROPERTY = 3;
/*     */   private static final int DBNAME = 4;
/*     */   private static final int RMNAME = 5;
/*     */   private static final int RMTYPE = 6;
/*     */   private static final int DATASOURCE = 7;
/*  80 */   private static final String[][] ATTRIBUTE_TABLE = { { "1.3.6.1.4.1.897.4.2.5", "sybaseAddress" }, { "1.3.6.1.4.1.897.4.2.15", "sybaseHAservername" }, { "1.3.6.1.4.1.897.4.2.9", "sybaseJconnectProtocol" }, { "1.3.6.1.4.1.897.4.2.10", "sybaseJconnectProperty" }, { "1.3.6.1.4.1.897.4.2.11", "sybaseDatabaseName" }, { "1.3.6.1.4.1.897.4.2.16", "sybaseResourceManagerName" }, { "1.3.6.1.4.1.897.4.2.17", "sybaseResourceManagerType" }, { "1.3.6.1.4.1.897.4.2.18", "sybaseJdbcDataSourceInterface" } };
/*     */   private static final String SYBASE_ADDRESS_PREFIX = "TCP#1#";
/*     */   private static final char SYBASE_ADDRESS_DELIMITER = ' ';
/*     */   private static final String PLACEHOLDER = "placeholder";
/*     */   private static final char HOSTPORT_DELIMITER = ':';
/*     */ 
/*     */   public SybJndiProvider()
/*     */   {
/* 133 */     this._hostportList = new Vector();
/* 134 */     this._secondaryHostportList = new Vector();
/*     */   }
/*     */ 
/*     */   public SybJndiProvider(DirContext paramDirContext, Name paramName, SybProperty paramSybProperty)
/*     */     throws SQLException, NamingException
/*     */   {
/* 149 */     this._sybProps = paramSybProperty;
/* 150 */     processAttrs(paramDirContext.getNameInNamespace(), paramDirContext.getAttributes(paramName, SERVER_ATTRIBUTES), paramDirContext);
/*     */   }
/*     */ 
/*     */   public void init(String paramString1, String paramString2, Properties paramProperties, SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/* 159 */     this._sybProps = paramSybProperty;
/*     */     try
/*     */     {
/* 178 */       InitialDirContext localInitialDirContext = new InitialDirContext(paramProperties);
/*     */ 
/* 182 */       processAttrs(paramString2, localInitialDirContext.getAttributes(paramString2, SERVER_ATTRIBUTES), localInitialDirContext);
/*     */     }
/*     */     catch (NamingException localNamingException)
/*     */     {
/* 188 */       ErrorMessage.raiseError("JZ013", paramString2, localNamingException.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processAttrs(String paramString, Attributes paramAttributes, DirContext paramDirContext)
/*     */     throws SQLException
/*     */   {
/* 197 */     String str1 = null;
/* 198 */     Object localObject1 = paramString;
/*     */     try
/*     */     {
/* 203 */       NamingEnumeration localNamingEnumeration1 = paramAttributes.getAll();
/* 204 */       while ((localNamingEnumeration1 != null) && (localNamingEnumeration1.hasMoreElements()))
/*     */       {
/* 206 */         Attribute localAttribute = (Attribute)localNamingEnumeration1.next();
/*     */ 
/* 208 */         NamingEnumeration localNamingEnumeration2 = localAttribute.getAll();
/* 209 */         String str2 = localAttribute.getID();
/*     */         String str3;
/* 212 */         switch (mapAttribute(str2))
/*     */         {
/*     */         case 0:
/* 218 */           populateAddressList(this._hostportList, localAttribute);
/* 219 */           break;
/*     */         case 1:
/*     */           while (true)
/*     */           {
/* 222 */             if (localNamingEnumeration2.hasMoreElements());
/* 224 */             str3 = localNamingEnumeration2.nextElement().toString();
/* 225 */             Attributes localAttributes = null;
/*     */             try
/*     */             {
/* 248 */               localObject1 = str3;
/*     */ 
/* 250 */               URI localURI = null;
/*     */               try
/*     */               {
/* 261 */                 localURI = new URI(null, str3, null);
/*     */               }
/*     */               catch (URISyntaxException localURISyntaxException)
/*     */               {
/*     */               }
/*     */ 
/* 271 */               localAttributes = paramDirContext.getAttributes(localURI.toString(), SERVER_ATTRIBUTES);
/*     */             }
/*     */             catch (NamingException localNamingException2)
/*     */             {
/* 280 */               NameParser localNameParser = null;
/*     */               Object localObject2;
/*     */               try
/*     */               {
/* 285 */                 localNameParser = paramDirContext.getNameParser(paramString);
/* 286 */                 Name localName1 = localNameParser.parse(paramString);
/*     */ 
/* 289 */                 localName1.remove(localName1.size() - 1);
/* 290 */                 localObject2 = localNameParser.parse(str3);
/*     */ 
/* 293 */                 removeDups((Name)localObject2, localName1);
/* 294 */                 localName1.addAll((Name)localObject2);
/*     */                 try
/*     */                 {
/* 300 */                   localObject1 = localName1.toString();
/*     */ 
/* 303 */                   localAttributes = paramDirContext.getAttributes(localName1, SERVER_ATTRIBUTES);
/*     */                 }
/*     */                 catch (NamingException localNamingException3)
/*     */                 {
/* 312 */                   localAttributes = paramDirContext.getAttributes(localName1.toString(), SERVER_ATTRIBUTES);
/*     */                 }
/*     */ 
/*     */               }
/*     */               catch (Exception localException)
/*     */               {
/* 325 */                 localObject2 = paramDirContext.getNameInNamespace();
/*     */ 
/* 327 */                 Name localName2 = localNameParser.parse((String)localObject2);
/* 328 */                 Name localName3 = localNameParser.parse(str3);
/* 329 */                 removeDups(localName3, localName2);
/*     */                 try
/*     */                 {
/* 335 */                   localObject1 = localName3.toString();
/*     */ 
/* 338 */                   localAttributes = paramDirContext.getAttributes(localName3, SERVER_ATTRIBUTES);
/*     */                 }
/*     */                 catch (NamingException localNamingException4)
/*     */                 {
/* 347 */                   localAttributes = paramDirContext.getAttributes(localName3.toString(), SERVER_ATTRIBUTES);
/*     */                 }
/*     */ 
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 361 */             if (localAttributes == null)
/*     */             {
/*     */               continue;
/*     */             }
/*     */ 
/* 368 */             if (localAttributes.get("1.3.6.1.4.1.897.4.2.5") != null)
/*     */             {
/* 373 */               populateAddressList(this._secondaryHostportList, localAttributes.get("1.3.6.1.4.1.897.4.2.5"));
/*     */             }
/*     */ 
/* 381 */             populateAddressList(this._secondaryHostportList, localAttributes.get("sybaseAddress"));
/*     */           }
/*     */ 
/*     */         case 2:
/* 390 */           str1 = localNamingEnumeration2.nextElement().toString();
/*     */ 
/* 393 */           break;
/*     */         case 3:
/*     */           while (true)
/*     */           {
/* 399 */             if (localNamingEnumeration2.hasMoreElements());
/* 401 */             str3 = localNamingEnumeration2.nextElement().toString();
/* 402 */             this._sybProps.parsePropertyString(str3);
/*     */           }
/*     */         case 4:
/* 410 */           this._dbName = localNamingEnumeration2.nextElement().toString();
/*     */ 
/* 413 */           break;
/*     */         case 5:
/* 416 */           str3 = localNamingEnumeration2.nextElement().toString();
/*     */           try
/*     */           {
/* 419 */             this._sybProps.setConnProperty("RMNAME", str3, true, true);
/*     */           }
/*     */           catch (SQLException localSQLException)
/*     */           {
/*     */           }
/*     */ 
/* 434 */           this._rmName = this._sybProps.getString(42);
/*     */ 
/* 439 */           break;
/*     */         case 6:
/* 442 */           this._rmType = Integer.parseInt(localNamingEnumeration2.nextElement().toString());
/*     */ 
/* 445 */           break;
/*     */         case 7:
/* 448 */           this._dataSourceInterface = localNamingEnumeration2.nextElement().toString();
/*     */ 
/* 452 */           continue;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (NamingException localNamingException1)
/*     */     {
/* 465 */       ErrorMessage.raiseError("JZ013", (String)localObject1, localNamingException1.toString());
/*     */     }
/*     */ 
/* 471 */     if (str1 == null)
/*     */     {
/* 473 */       str1 = "Tds";
/*     */     }
/*     */ 
/* 482 */     this._protocol = SybUrlManager.loadProtocol(str1);
/*     */   }
/*     */ 
/*     */   private void removeDups(Name paramName1, Name paramName2)
/*     */     throws InvalidNameException
/*     */   {
/* 490 */     for (int i = paramName1.size() - 1; i >= 0; --i)
/*     */     {
/* 492 */       String str = paramName1.get(i);
/* 493 */       for (int j = 0; j < paramName2.size(); ++j)
/*     */       {
/* 495 */         if (!str.equals(paramName2.get(j)))
/*     */           continue;
/* 497 */         Debug.println(this, "ignoring duplicate DN part: " + str);
/*     */ 
/* 499 */         paramName1.remove(i);
/* 500 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void populateAddressList(Vector paramVector, Attribute paramAttribute)
/*     */     throws NamingException
/*     */   {
/* 509 */     NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
/*     */ 
/* 511 */     while (localNamingEnumeration.hasMoreElements())
/*     */     {
/* 513 */       String str = localNamingEnumeration.nextElement().toString();
/*     */ 
/* 517 */       if (!str.startsWith("TCP#1#")) continue; if (str.indexOf(' ') == -1)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 528 */       paramVector.addElement(str.substring("TCP#1#".length()).replace(' ', ':'));
/*     */     }
/*     */   }
/*     */ 
/*     */   private int mapAttribute(String paramString)
/*     */   {
/* 540 */     for (int i = 0; i < ATTRIBUTE_TABLE.length; ++i)
/*     */     {
/* 542 */       if ((paramString.equals(ATTRIBUTE_TABLE[i][0])) || (paramString.equalsIgnoreCase(ATTRIBUTE_TABLE[i][1])))
/*     */       {
/* 545 */         return i;
/*     */       }
/*     */     }
/* 548 */     return -1;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybJndiProvider
 * JD-Core Version:    0.5.4
 */