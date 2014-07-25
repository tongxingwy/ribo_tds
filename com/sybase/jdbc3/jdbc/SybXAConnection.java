/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.SecureRandom;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.transaction.xa.XAException;
/*     */ import javax.transaction.xa.XAResource;
/*     */ import javax.transaction.xa.Xid;
/*     */ 
/*     */ public class SybXAConnection extends SybPooledConnection
/*     */   implements XAConnection
/*     */ {
/*     */   private final SybXAResource _xaRes;
/*     */ 
/*     */   protected SybXAConnection(SybXADataSource paramSybXADataSource, SybUrlProvider paramSybUrlProvider)
/*     */     throws SQLException
/*     */   {
/*  93 */     super(paramSybUrlProvider, paramSybXADataSource.getLoginTimeout());
/*     */ 
/*  95 */     String str1 = null;
/*  96 */     Object localObject1 = null;
/*     */ 
/*  98 */     int i = paramSybXADataSource.getResourceManagerType();
/*  99 */     if (i == 1)
/*     */     {
/* 131 */       localObject2 = null;
/* 132 */       if (paramSybUrlProvider.getResourceManagerName() != null)
/*     */       {
/* 136 */         localObject2 = paramSybUrlProvider.getResourceManagerName();
/*     */       }
/*     */       else
/*     */       {
/* 140 */         localObject2 = ((SybUrlProviderImplBase)paramSybUrlProvider)._rmNameDefault;
/*     */       }
/*     */ 
/* 147 */       this._xaRes = SybXAResource.createSybXAResource(i, (String)localObject2, this, paramSybXADataSource, paramSybUrlProvider);
/*     */       try
/*     */       {
/* 159 */         SecureRandom localSecureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
/*     */ 
/* 161 */         localObject3 = new byte[16];
/* 162 */         localSecureRandom.nextBytes(localObject3);
/* 163 */         byte[] arrayOfByte = new byte[8];
/* 164 */         localSecureRandom.nextBytes(arrayOfByte);
/*     */ 
/* 166 */         localObject4 = new SybXid(99, localObject3, arrayOfByte);
/*     */ 
/* 168 */         this._xaRes.start((Xid)localObject4, 0);
/*     */ 
/* 171 */         super.init();
/*     */ 
/* 173 */         this._xaRes.end((Xid)localObject4, 67108864);
/* 174 */         k = this._xaRes.prepare((Xid)localObject4);
/*     */ 
/* 177 */         if (k == 0)
/*     */         {
/* 179 */           this._xaRes.commit((Xid)localObject4, true);
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (XAException localXAException)
/*     */       {
/* 191 */         close();
/*     */ 
/* 193 */         ErrorMessage.raiseError("JZ0XS");
/*     */       }
/*     */       catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*     */       {
/*     */       }
/*     */       catch (NoSuchProviderException localNoSuchProviderException)
/*     */       {
/*     */       }
/*     */ 
/* 203 */       return;
/*     */     }
/*     */ 
/* 210 */     super.init();
/*     */ 
/* 213 */     Object localObject2 = (SybDatabaseMetaData)getMetaData();
/* 214 */     ResultSet localResultSet = ((SybDatabaseMetaData)localObject2).getXACoordinatorType();
/* 215 */     Object localObject3 = localResultSet.getMetaData();
/* 216 */     int j = ((ResultSetMetaData)localObject3).getColumnCount();
/* 217 */     localResultSet.next();
/* 218 */     i = localResultSet.getInt(1);
/* 219 */     Object localObject4 = localResultSet.getString(2);
/* 220 */     int k = localResultSet.getInt(3);
/* 221 */     Object localObject5 = null;
/* 222 */     String str2 = null;
/*     */ 
/* 224 */     if (((ResultSetMetaData)localObject3).getColumnCount() > 3)
/*     */     {
/* 226 */       str2 = localResultSet.getString(4);
/*     */     }
/*     */ 
/* 229 */     localResultSet.close();
/*     */ 
/* 231 */     if (paramSybUrlProvider.getResourceManagerName() != null)
/*     */     {
/* 235 */       localObject5 = paramSybUrlProvider.getResourceManagerName();
/*     */     }
/* 237 */     else if (str2 != null)
/*     */     {
/* 239 */       localObject5 = str2;
/*     */     }
/*     */     else
/*     */     {
/* 243 */       localObject5 = ((SybUrlProviderImplBase)paramSybUrlProvider)._rmNameDefault;
/*     */     }
/*     */ 
/* 254 */     if (i == 0)
/*     */     {
/* 256 */       this._xaRes = null;
/*     */ 
/* 260 */       str1 = "JZ0XS";
/*     */     }
/* 262 */     else if ((k & 0x1) == 0)
/*     */     {
/* 264 */       this._xaRes = null;
/*     */ 
/* 267 */       str1 = "JZ0XU";
/*     */ 
/* 270 */       localObject1 = localObject4;
/*     */     }
/*     */     else
/*     */     {
/* 277 */       this._xaRes = SybXAResource.createSybXAResource(i, (String)localObject5, this, paramSybXADataSource, paramSybUrlProvider);
/*     */ 
/* 279 */       if (this._xaRes == null)
/*     */       {
/* 282 */         str1 = "JZ0XC";
/* 283 */         localObject1 = String.valueOf(i);
/*     */       }
/*     */     }
/*     */ 
/* 287 */     if (str1 == null) {
/*     */       return;
/*     */     }
/* 290 */     close();
/*     */ 
/* 292 */     if (localObject1 != null)
/*     */     {
/* 294 */       ErrorMessage.raiseError(str1, (String)localObject1);
/*     */     }
/*     */     else
/*     */     {
/* 298 */       ErrorMessage.raiseError(str1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public XAResource getXAResource()
/*     */   {
/* 320 */     return this._xaRes;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 337 */     if (this._xaRes != null)
/*     */     {
/* 339 */       this._xaRes.close();
/*     */     }
/*     */ 
/* 343 */     super.close();
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void endLocalTransaction()
/*     */     throws SQLException
/*     */   {
/* 373 */     checkConnection();
/* 374 */     boolean bool = false;
/*     */ 
/* 376 */     bool = this._props.getBoolean(53);
/*     */     try
/*     */     {
/* 381 */       if (getAutoCommit())
/*     */       {
/* 383 */         this._xaRes._localTransactionOK = false;
/*     */       }
/* 387 */       else if (bool)
/*     */       {
/* 389 */         rollback();
/* 390 */         setAutoCommit(true);
/*     */       }
/* 394 */       else if (!this._protocol.getInTransaction())
/*     */       {
/* 396 */         this._xaRes._localTransactionOK = false;
/*     */       }
/*     */       else
/*     */       {
/* 400 */         rollback();
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/* 408 */       handleSQLE(localSQLException);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybXAConnection
 * JD-Core Version:    0.5.4
 */