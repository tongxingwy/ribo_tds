/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ import I;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class JavaVersion
/*     */ {
/*  52 */   private static int[] _java_version = null;
/*     */ 
/*     */   private JavaVersion()
/*     */   {
/*     */   }
/*     */ 
/*     */   public JavaVersion(String paramString)
/*     */   {
/*     */     try
/*     */     {
/*  84 */       _java_version = stringToInts(paramString);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean atOrAboveVersion(String paramString)
/*     */   {
/* 102 */     if (_java_version == null)
/*     */     {
/* 105 */       return false;
/*     */     }
/* 107 */     int[] arrayOfInt = stringToInts(paramString);
/* 108 */     if (arrayOfInt == null)
/*     */     {
/* 110 */       return false;
/*     */     }
/* 112 */     for (int i = 0; (i < arrayOfInt.length) && (i < _java_version.length); ++i)
/*     */     {
/* 114 */       if (arrayOfInt[i] != _java_version[i])
/*     */       {
/* 116 */         return arrayOfInt[i] < _java_version[i];
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 126 */     if (arrayOfInt.length == _java_version.length)
/*     */     {
/* 128 */       return true;
/*     */     }
/*     */ 
/* 132 */     return _java_version.length > arrayOfInt.length;
/*     */   }
/*     */ 
/*     */   private static int[] stringToInts(String paramString)
/*     */   {
/* 141 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ".");
/* 142 */     int i = localStringTokenizer.countTokens();
/* 143 */     Object localObject = new int[i];
/*     */ 
/* 146 */     for (int j = 0; j < i; ++j)
/*     */     {
/* 148 */       String str = localStringTokenizer.nextToken();
/* 149 */       char[] arrayOfChar = str.toCharArray();
/* 150 */       int k = 0;
/*     */ 
/* 158 */       for (int l = 0; l < arrayOfChar.length; ++l)
/*     */       {
/* 160 */         if (!Character.isDigit(arrayOfChar[l]))
/*     */         {
/* 165 */           if (k != 0)
/*     */           {
/* 171 */             str = new String(arrayOfChar, 0, l);
/* 172 */             break;
/*     */           }
/*     */ 
/* 176 */           if (l + 1 == arrayOfChar.length)
/*     */           {
/* 188 */             if (j == 0)
/*     */             {
/* 194 */               return null;
/*     */             }
/*     */ 
/* 200 */             int[] arrayOfInt = new int[j];
/* 201 */             System.arraycopy(localObject, 0, arrayOfInt, 0, j);
/* 202 */             localObject = arrayOfInt;
/* 203 */             break label185:
/*     */           }
/*     */ 
/* 210 */           str = new String(arrayOfChar, l + 1, arrayOfChar.length - (l + 1));
/*     */         }
/*     */         else
/*     */         {
/* 217 */           k = 1;
/*     */         }
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 223 */         localObject[j] = Integer.parseInt(str);
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException)
/*     */       {
/* 231 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 245 */     label185: return (I)localObject;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  58 */       String str = System.getProperty("java.version");
/*     */ 
/*  60 */       _java_version = stringToInts(str);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.JavaVersion
 * JD-Core Version:    0.5.4
 */