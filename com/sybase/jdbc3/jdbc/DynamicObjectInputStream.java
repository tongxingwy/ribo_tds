/*    */ package com.sybase.jdbc3.jdbc;
/*    */ 
/*    */ import com.sybase.jdbcx.DynamicClassLoader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectStreamClass;
/*    */ import java.io.StreamCorruptedException;
/*    */ 
/*    */ public class DynamicObjectInputStream extends ObjectInputStream
/*    */ {
/*    */   private ClassLoader _classLoader;
/*    */ 
/*    */   private DynamicObjectInputStream(InputStream paramInputStream, ClassLoader paramClassLoader)
/*    */     throws IOException, StreamCorruptedException
/*    */   {
/* 39 */     super(paramInputStream);
/* 40 */     this._classLoader = paramClassLoader;
/*    */   }
/*    */ 
/*    */   public DynamicObjectInputStream(InputStream paramInputStream, DynamicClassLoader paramDynamicClassLoader)
/*    */     throws IOException, StreamCorruptedException
/*    */   {
/* 51 */     this(paramInputStream, (ClassLoader)paramDynamicClassLoader);
/*    */   }
/*    */ 
/*    */   protected Class resolveClass(ObjectStreamClass paramObjectStreamClass)
/*    */     throws IOException, ClassNotFoundException
/*    */   {
/* 70 */     return this._classLoader.loadClass(paramObjectStreamClass.getName());
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.DynamicObjectInputStream
 * JD-Core Version:    0.5.4
 */