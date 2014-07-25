/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.directory.DirContext;
/*     */ import javax.naming.spi.ObjectFactory;
/*     */ 
/*     */ public class SybObjectFactory
/*     */   implements ObjectFactory
/*     */ {
/*  69 */   static final String SYB_DATA_SOURCE_CLASSNAME = SybDataSource.class.getName();
/*     */ 
/*  73 */   static final String SYB_POOLED_DATA_SOURCE_CLASSNAME = SybConnectionPoolDataSource.class.getName();
/*     */ 
/*  77 */   static final String SYB_XA_DATA_SOURCE_CLASSNAME = SybXADataSource.class.getName();
/*     */   static final String DATASOURCE_NAME = "DataSource";
/*     */   static final String CONNECTIONPOOLDATASOURCE_NAME = "ConnectionPoolDataSource";
/*     */   static final String XADATASOURCE_NAME = "XADataSource";
/*     */ 
/*     */   public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable paramHashtable)
/*     */   {
/* 117 */     Object localObject = null;
/*     */ 
/* 130 */     if (paramObject != null)
/*     */     {
/* 140 */       if ((paramObject instanceof DirContext) && (paramContext instanceof DirContext))
/*     */       {
/* 142 */         localObject = getObjectUsingDirContext(paramName, (DirContext)paramContext);
/*     */       }
/* 147 */       else if (paramObject instanceof Reference)
/*     */       {
/* 149 */         localObject = getObjectUsingReference((Reference)paramObject, paramName, paramContext, paramHashtable);
/*     */       }
/*     */     }
/*     */ 
/* 153 */     return localObject;
/*     */   }
/*     */ 
/*     */   protected Object getObjectUsingReference(Reference paramReference, Name paramName, Context paramContext, Hashtable paramHashtable)
/*     */   {
/* 172 */     Object localObject1 = null;
/*     */     try
/*     */     {
/* 176 */       Object localObject2 = null;
/* 177 */       if (paramReference.getClassName().equals(SYB_DATA_SOURCE_CLASSNAME))
/*     */       {
/* 180 */         localObject2 = new SybDataSource();
/*     */       }
/* 182 */       else if (paramReference.getClassName().equals(SYB_POOLED_DATA_SOURCE_CLASSNAME))
/*     */       {
/* 185 */         localObject2 = new SybConnectionPoolDataSource();
/*     */       }
/* 187 */       else if (paramReference.getClassName().equals(SYB_XA_DATA_SOURCE_CLASSNAME))
/*     */       {
/* 190 */         localObject2 = new SybXADataSource();
/*     */       }
/*     */ 
/* 193 */       if (localObject2 != null)
/*     */       {
/* 197 */         ((SybDataSource)localObject2).setDatabaseName((String)paramReference.get("databaseName").getContent());
/*     */ 
/* 200 */         ((SybDataSource)localObject2).setDataSourceName((String)paramReference.get("dataSourceName").getContent());
/*     */ 
/* 203 */         ((SybDataSource)localObject2).setDescription((String)paramReference.get("description").getContent());
/*     */ 
/* 206 */         ((SybDataSource)localObject2).setNetworkProtocol((String)paramReference.get("networkProtocol").getContent());
/*     */ 
/* 209 */         ((SybDataSource)localObject2).setPortNumber((String)paramReference.get("portNumber").getContent());
/*     */ 
/* 212 */         ((SybDataSource)localObject2).setServerName((String)paramReference.get("serverName").getContent());
/*     */ 
/* 215 */         ((SybDataSource)localObject2).setUser((String)paramReference.get("user").getContent());
/*     */ 
/* 218 */         ((SybDataSource)localObject2).setPassword((String)paramReference.get("password").getContent());
/*     */ 
/* 223 */         ((SybDataSource)localObject2).setResourceManagerName((String)paramReference.get("resourceManagerName").getContent());
/*     */ 
/* 226 */         ((SybDataSource)localObject2).setResourceManagerType(Integer.parseInt((String)paramReference.get("resourceManagerType").getContent()));
/*     */ 
/* 229 */         ((SybDataSource)localObject2).setAddressList((String)paramReference.get("addressList").getContent());
/*     */ 
/* 235 */         ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream((byte[])paramReference.get("sybProperty").getContent());
/*     */ 
/* 237 */         ObjectInputStream localObjectInputStream = new ObjectInputStream(localByteArrayInputStream);
/*     */ 
/* 239 */         ((SybDataSource)localObject2).setSybProperty((SybProperty)localObjectInputStream.readObject());
/*     */ 
/* 243 */         localObject1 = localObject2;
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */ 
/* 251 */     return localObject1;
/*     */   }
/*     */ 
/*     */   protected Object getObjectUsingDirContext(Name paramName, DirContext paramDirContext)
/*     */   {
/* 274 */     Object localObject1 = null;
/*     */     try
/*     */     {
/* 281 */       SybProperty localSybProperty = new SybDataSource().getSybProperty();
/*     */ 
/* 287 */       SybJndiProvider localSybJndiProvider = new SybJndiProvider(paramDirContext, paramName, localSybProperty);
/*     */ 
/* 289 */       Object localObject2 = null;
/*     */ 
/* 294 */       String str = localSybJndiProvider.getDataSourceInterface();
/* 295 */       if (str.equals("DataSource"))
/*     */       {
/* 297 */         localObject2 = new SybDataSource();
/*     */       }
/* 299 */       else if (str.equalsIgnoreCase("ConnectionPoolDataSource"))
/*     */       {
/* 301 */         localObject2 = new SybConnectionPoolDataSource();
/*     */       }
/* 303 */       else if (str.equalsIgnoreCase("XADataSource"))
/*     */       {
/* 305 */         localObject2 = new SybXADataSource();
/*     */       }
/*     */ 
/* 311 */       ((SybDataSource)localObject2).setAddressList(localSybJndiProvider.getHostPortList());
/* 312 */       ((SybDataSource)localObject2).setDatabaseName(localSybJndiProvider.getDatabaseName());
/* 313 */       ((SybDataSource)localObject2).setSybProperty(localSybJndiProvider.getSybProperty());
/* 314 */       ((SybDataSource)localObject2).setResourceManagerName(localSybJndiProvider.getResourceManagerName());
/* 315 */       ((SybDataSource)localObject2).setResourceManagerType(localSybJndiProvider.getResourceManagerType());
/*     */ 
/* 319 */       localObject1 = localObject2;
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */ 
/* 330 */     return localObject1;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybObjectFactory
 * JD-Core Version:    0.5.4
 */