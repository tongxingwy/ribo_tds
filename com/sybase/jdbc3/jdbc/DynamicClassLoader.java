/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class DynamicClassLoader extends ClassLoader
/*     */   implements com.sybase.jdbcx.DynamicClassLoader
/*     */ {
/*     */   private LoaderConnection _connection;
/*     */ 
/*     */   public DynamicClassLoader(LoaderConnection paramLoaderConnection)
/*     */     throws SecurityException
/*     */   {
/*  73 */     this._connection = paramLoaderConnection;
/*     */   }
/*     */ 
/*     */   public DynamicClassLoader(String paramString, Properties paramProperties)
/*     */     throws SecurityException
/*     */   {
/*  87 */     this(new LoaderConnection(paramString, paramProperties));
/*     */   }
/*     */ 
/*     */   public Class findClass(String paramString)
/*     */   {
/* 101 */     byte[] arrayOfByte = null;
/*     */     try
/*     */     {
/* 104 */       arrayOfByte = loadClassData(paramString);
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 108 */       localSQLException.printStackTrace();
/*     */ 
/* 110 */       return null;
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 114 */       localIOException.printStackTrace();
/*     */ 
/* 116 */       return null;
/*     */     }
/*     */ 
/* 119 */     return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public synchronized void preloadJars(String[] paramArrayOfString)
/*     */     throws SQLException
/*     */   {
/* 131 */     for (int i = 0; i < paramArrayOfString.length; ++i)
/*     */     {
/* 133 */       String str = paramArrayOfString[i];
/*     */       try
/*     */       {
/* 137 */         preloadAJar(str);
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 142 */         throw new SQLException(localIOException.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void openConnection()
/*     */     throws SQLException
/*     */   {
/* 156 */     this._connection.open();
/*     */   }
/*     */ 
/*     */   public void closeConnection()
/*     */     throws SQLException
/*     */   {
/* 172 */     this._connection.kill();
/*     */   }
/*     */ 
/*     */   public void setKeepConnectionAlive(boolean paramBoolean)
/*     */   {
/* 181 */     this._connection.setKeepAlive(paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean getKeepConnectionAlive()
/*     */   {
/* 189 */     return this._connection.getKeepAlive();
/*     */   }
/*     */ 
/*     */   public Connection getConnection()
/*     */   {
/* 199 */     return this._connection.getConnection();
/*     */   }
/*     */ 
/*     */   public boolean hasClassBeenLoaded(String paramString)
/*     */   {
/* 209 */     int i = 0;
/*     */ 
/* 211 */     Class localClass = findLoadedClass(paramString);
/* 212 */     if (localClass != null)
/*     */     {
/* 214 */       i = 1;
/*     */     }
/* 216 */     return i;
/*     */   }
/*     */ 
/*     */   private void preloadAJar(String paramString)
/*     */     throws IOException, SQLException
/*     */   {
/* 231 */     int i = 0;
/*     */ 
/* 235 */     Enumeration localEnumeration = this._connection.allClassesInJar(paramString);
/* 236 */     while (localEnumeration.hasMoreElements())
/*     */     {
/* 238 */       i = 1;
/*     */ 
/* 240 */       LoaderConnection.ClassData localClassData = (LoaderConnection.ClassData)localEnumeration.nextElement();
/*     */ 
/* 242 */       if (localClassData == null)
/*     */         continue;
/* 244 */       byte[] arrayOfByte = localClassData._data;
/* 245 */       String str = localClassData._name;
/*     */ 
/* 250 */       Class localClass = findLoadedClass(str);
/* 251 */       if (localClass == null)
/*     */       {
/* 253 */         localClass = defineClass(str, arrayOfByte, 0, arrayOfByte.length);
/*     */       }
/*     */ 
/* 258 */       resolveClass(localClass);
/*     */     }
/*     */ 
/* 261 */     if (i != 0)
/*     */       return;
/* 263 */     ErrorMessage.raiseWarning("010PF");
/*     */   }
/*     */ 
/*     */   private byte[] loadClassData(String paramString)
/*     */     throws SQLException, IOException
/*     */   {
/* 283 */     byte[] arrayOfByte = this._connection.fetchClass(paramString);
/* 284 */     return arrayOfByte;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.DynamicClassLoader
 * JD-Core Version:    0.5.4
 */