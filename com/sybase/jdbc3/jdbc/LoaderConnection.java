/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarInputStream;
/*     */ 
/*     */ public class LoaderConnection
/*     */ {
/*     */   protected static final int MAX_BUFFER_SIZE = 1000;
/*     */   private Driver _driver;
/*     */   private Connection _connection;
/*     */   private String _url;
/*     */   private Properties _properties;
/*     */   private boolean _keepAlive;
/*     */ 
/*     */   public LoaderConnection(String paramString, Properties paramProperties)
/*     */   {
/*  60 */     this(new SybDriver(), paramString, paramProperties);
/*     */   }
/*     */ 
/*     */   public LoaderConnection(Driver paramDriver, String paramString, Properties paramProperties)
/*     */   {
/*  73 */     this._url = paramString;
/*  74 */     setProps(paramProperties);
/*  75 */     this._connection = null;
/*  76 */     this._keepAlive = true;
/*  77 */     this._driver = paramDriver;
/*     */   }
/*     */ 
/*     */   protected byte[] fetchClass(String paramString)
/*     */     throws SQLException
/*     */   {
/*  91 */     open();
/*     */ 
/*  93 */     byte[] arrayOfByte = null;
/*     */ 
/*  95 */     SybDatabaseMetaData localSybDatabaseMetaData = (SybDatabaseMetaData)this._connection.getMetaData();
/*  96 */     ResultSet localResultSet = localSybDatabaseMetaData.getClassForName(paramString);
/*     */ 
/*  98 */     if (localResultSet.next())
/*     */     {
/* 104 */       arrayOfByte = localResultSet.getBytes(1);
/*     */     }
/*     */     else
/*     */     {
/* 108 */       throw new SQLException("No results returned!");
/*     */     }
/*     */ 
/* 111 */     localResultSet.close();
/*     */ 
/* 113 */     close();
/* 114 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   protected byte[] fetchClassFromJar(String paramString1, String paramString2)
/*     */     throws SQLException
/*     */   {
/* 133 */     open();
/*     */ 
/* 135 */     byte[] arrayOfByte = null;
/*     */ 
/* 137 */     SybDatabaseMetaData localSybDatabaseMetaData = (SybDatabaseMetaData)this._connection.getMetaData();
/* 138 */     ResultSet localResultSet = localSybDatabaseMetaData.getJarForClass(paramString1);
/* 139 */     if (localResultSet.next())
/*     */     {
/* 142 */       InputStream localInputStream = localResultSet.getBinaryStream(1);
/*     */       try
/*     */       {
/* 147 */         JarInputStream localJarInputStream = new JarInputStream(localInputStream);
/* 148 */         ClassData localClassData = readJarBytes(localJarInputStream, paramString1);
/* 149 */         if (localClassData != null)
/*     */         {
/* 151 */           arrayOfByte = localClassData._data;
/*     */         }
/* 153 */         localJarInputStream.close();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */ 
/* 160 */       if (localResultSet.next())
/*     */       {
/* 162 */         throw new SQLException("More than one row was returned for class " + paramString1 + ", jar " + paramString2);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 167 */     localResultSet.close();
/*     */ 
/* 169 */     close();
/* 170 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   protected Enumeration allClassesInJar(String paramString)
/*     */     throws SQLException
/*     */   {
/* 188 */     open();
/* 189 */     SybDatabaseMetaData localSybDatabaseMetaData = (SybDatabaseMetaData)this._connection.getMetaData();
/*     */     Object localObject;
/* 190 */     if (localSybDatabaseMetaData.canReturnJars())
/*     */     {
/* 194 */       localObject = new JarDataEnumeration(paramString);
/*     */     }
/*     */     else
/*     */     {
/* 201 */       localObject = new ClassDataEnumeration(paramString);
/*     */     }
/* 203 */     close();
/*     */ 
/* 205 */     return (Enumeration)localObject;
/*     */   }
/*     */ 
/*     */   protected ClassData readJarBytes(JarInputStream paramJarInputStream, String paramString)
/*     */     throws IOException
/*     */   {
/* 218 */     ClassData localClassData = null;
/*     */     JarEntry localJarEntry;
/*     */     String str;
/*     */     do
/*     */     {
/*     */       do
/*     */       {
/* 225 */         localJarEntry = paramJarInputStream.getNextJarEntry();
/*     */ 
/* 227 */         if (localJarEntry == null) {
/*     */           break label135;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 233 */       while (localJarEntry.isDirectory());
/*     */ 
/* 236 */       str = localJarEntry.getName();
/* 237 */     }while ((paramString != null) && (!str.equals(paramString.concat(".class"))));
/*     */ 
/* 250 */     int i = 1000;
/* 251 */     if ((localJarEntry.getSize() > 0L) && (localJarEntry.getSize() < 1000L))
/*     */     {
/* 255 */       i = (int)localJarEntry.getSize();
/*     */     }
/* 257 */     localClassData = new ClassData();
/* 258 */     localClassData._data = readBytes(paramJarInputStream, i);
/* 259 */     localClassData._name = str.substring(0, str.lastIndexOf(".class")).replace('/', '.');
/*     */ 
/* 261 */     paramJarInputStream.closeEntry();
/*     */ 
/* 266 */     label135: return localClassData;
/*     */   }
/*     */ 
/*     */   protected byte[] readBytes(InputStream paramInputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/* 276 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 277 */     byte[] arrayOfByte1 = new byte[paramInt];
/*     */     while (true)
/*     */     {
/* 280 */       int i = paramInputStream.read(arrayOfByte1, 0, paramInt);
/* 281 */       if (i == -1) {
/*     */         break;
/*     */       }
/*     */ 
/* 285 */       localByteArrayOutputStream.write(arrayOfByte1, 0, i);
/*     */     }
/* 287 */     byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
/* 288 */     localByteArrayOutputStream.close();
/*     */ 
/* 290 */     return arrayOfByte2;
/*     */   }
/*     */ 
/*     */   protected void kill()
/*     */     throws SQLException
/*     */   {
/* 299 */     this._connection.close();
/* 300 */     this._connection = null;
/*     */   }
/*     */ 
/*     */   protected Connection getConnection()
/*     */   {
/* 311 */     return this._connection;
/*     */   }
/*     */ 
/*     */   protected void open()
/*     */     throws SQLException
/*     */   {
/* 319 */     if (this._connection != null)
/*     */     {
/*     */       return;
/*     */     }
/*     */ 
/* 325 */     this._connection = this._driver.connect(this._url, this._properties);
/*     */   }
/*     */ 
/*     */   protected void close()
/*     */     throws SQLException
/*     */   {
/* 334 */     if (this._keepAlive)
/*     */       return;
/* 336 */     kill();
/*     */   }
/*     */ 
/*     */   public void setKeepAlive(boolean paramBoolean)
/*     */   {
/* 346 */     this._keepAlive = paramBoolean;
/*     */   }
/*     */ 
/*     */   protected boolean getKeepAlive()
/*     */   {
/* 354 */     return this._keepAlive;
/*     */   }
/*     */ 
/*     */   private void setProps(Properties paramProperties)
/*     */   {
/* 367 */     this._properties = ((Properties)paramProperties.clone());
/* 368 */     Enumeration localEnumeration = this._properties.keys();
/* 369 */     while (localEnumeration.hasMoreElements())
/*     */     {
/* 371 */       String str = (String)localEnumeration.nextElement();
/* 372 */       if ((!str.equalsIgnoreCase("class_loader")) && (!str.equalsIgnoreCase("preload_jars"))) {
/*     */         continue;
/*     */       }
/* 375 */       this._properties.remove(str);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class ClassData
/*     */   {
/*     */     protected String _name;
/*     */     protected byte[] _data;
/*     */ 
/*     */     protected ClassData()
/*     */     {
/*     */     }
/*     */ 
/*     */     public byte[] getData()
/*     */     {
/* 564 */       return this._data;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class ClassDataEnumeration
/*     */     implements Enumeration
/*     */   {
/*     */     private Enumeration _enum;
/*     */ 
/*     */     protected ClassDataEnumeration(String arg2)
/*     */       throws SQLException
/*     */     {
/* 505 */       SybDatabaseMetaData localSybDatabaseMetaData = (SybDatabaseMetaData)LoaderConnection.this._connection.getMetaData();
/*     */       String str1;
/* 506 */       ResultSet localResultSet = localSybDatabaseMetaData.getClassesInJar(str1);
/*     */ 
/* 509 */       Vector localVector = new Vector();
/* 510 */       while (localResultSet.next())
/*     */       {
/* 512 */         String str2 = localResultSet.getString(1);
/* 513 */         localVector.addElement(str2);
/*     */       }
/* 515 */       localResultSet.close();
/*     */ 
/* 517 */       this._enum = localVector.elements();
/*     */     }
/*     */ 
/*     */     public boolean hasMoreElements()
/*     */     {
/* 524 */       return this._enum.hasMoreElements();
/*     */     }
/*     */ 
/*     */     public Object nextElement()
/*     */     {
/* 531 */       LoaderConnection.ClassData localClassData = new LoaderConnection.ClassData(LoaderConnection.this);
/*     */ 
/* 533 */       localClassData._name = ((String)this._enum.nextElement());
/*     */       try
/*     */       {
/* 536 */         localClassData._data = LoaderConnection.this.fetchClass(localClassData._name);
/*     */       }
/*     */       catch (SQLException localSQLException)
/*     */       {
/* 540 */         localClassData = null;
/*     */       }
/* 542 */       return localClassData;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class JarDataEnumeration
/*     */     implements Enumeration
/*     */   {
/*     */     ResultSet _rs;
/*     */     private JarInputStream _jarStream;
/*     */     private LoaderConnection.ClassData _nextElement;
/*     */ 
/*     */     protected JarDataEnumeration(String arg2)
/*     */       throws SQLException
/*     */     {
/* 413 */       this._nextElement = null;
/*     */ 
/* 415 */       SybDatabaseMetaData localSybDatabaseMetaData = (SybDatabaseMetaData)LoaderConnection.this._connection.getMetaData();
/*     */       String str;
/* 416 */       this._rs = localSybDatabaseMetaData.getJarByName(str);
/* 417 */       if (!this._rs.next()) {
/*     */         return;
/*     */       }
/* 420 */       InputStream localInputStream = this._rs.getBinaryStream(1);
/*     */       try
/*     */       {
/* 425 */         this._jarStream = new JarInputStream(localInputStream);
/* 426 */         this._nextElement = fetchNextElement();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 430 */         localIOException.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean hasMoreElements()
/*     */     {
/* 443 */       int i = (this._nextElement != null) ? 1 : 0;
/* 444 */       if (i == 0)
/*     */       {
/*     */         try
/*     */         {
/* 448 */           this._rs.close();
/*     */         }
/*     */         catch (SQLException localSQLException)
/*     */         {
/*     */         }
/*     */       }
/* 454 */       return i;
/*     */     }
/*     */ 
/*     */     public Object nextElement()
/*     */     {
/* 461 */       LoaderConnection.ClassData localClassData = this._nextElement;
/* 462 */       this._nextElement = fetchNextElement();
/* 463 */       return localClassData;
/*     */     }
/*     */ 
/*     */     private LoaderConnection.ClassData fetchNextElement()
/*     */     {
/*     */       LoaderConnection.ClassData localClassData;
/*     */       try
/*     */       {
/* 473 */         localClassData = LoaderConnection.this.readJarBytes(this._jarStream, null);
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 477 */         localClassData = null;
/*     */       }
/*     */ 
/* 480 */       return localClassData;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.LoaderConnection
 * JD-Core Version:    0.5.4
 */