/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class Language
/*     */ {
/*  29 */   private static Locale _locale = new Locale("en");
/*  30 */   private static String[][] _LANGUAGE_MAP = { { "us_english", "en" }, { "french", "fr" }, { "german", "de" }, { "japanese", "ja" }, { "chinese", "zh" }, { "spanish", "es" }, { "korean", "ko" }, { "portuguese", "pt" }, { "polish", "pl" }, { "thai", "th" } };
/*     */ 
/*     */   public static String defaultLanguage()
/*     */   {
/*  80 */     String str = Locale.getDefault().getLanguage();
/*     */ 
/*  82 */     for (int i = 0; i < _LANGUAGE_MAP.length; ++i)
/*     */     {
/*  84 */       if (_LANGUAGE_MAP[i][1].equals(str))
/*     */       {
/*  86 */         return _LANGUAGE_MAP[i][0];
/*     */       }
/*     */     }
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */   public static Locale getLocale()
/*     */   {
/*  95 */     return _locale;
/*     */   }
/*     */ 
/*     */   public static void setLocale(String paramString)
/*     */   {
/* 101 */     for (int i = 0; i < _LANGUAGE_MAP.length; ++i)
/*     */     {
/* 103 */       if (!_LANGUAGE_MAP[i][0].equals(paramString))
/*     */         continue;
/* 105 */       _locale = new Locale(_LANGUAGE_MAP[i][1]);
/* 106 */       return;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.Language
 * JD-Core Version:    0.5.4
 */