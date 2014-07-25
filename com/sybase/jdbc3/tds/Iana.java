/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class Iana
/*     */ {
/*     */   protected static final String UNSUPPORTED_CHARSET = "UnsupportedCharset";
/*  35 */   private static final Hashtable _IANA_MAP = new Hashtable();
/*     */ 
/*     */   public static String lookupIana(String paramString)
/*     */   {
/* 142 */     Object localObject = _IANA_MAP.get(paramString);
/*     */ 
/* 148 */     if (localObject == null)
/*     */     {
/* 150 */       return "UnsupportedCharset";
/*     */     }
/* 152 */     return localObject.toString();
/*     */   }
/*     */ 
/*     */   public static String reverseLookupIana(String paramString)
/*     */   {
/* 159 */     if (_IANA_MAP.contains(paramString))
/*     */     {
/* 162 */       Enumeration localEnumeration = _IANA_MAP.keys();
/* 163 */       String str1 = null;
/* 164 */       while (localEnumeration.hasMoreElements())
/*     */       {
/* 166 */         str1 = localEnumeration.nextElement().toString();
/* 167 */         String str2 = _IANA_MAP.get(str1).toString();
/* 168 */         if (str2.equals(paramString))
/*     */         {
/* 175 */           return str1;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 184 */     return "UnsupportedCharset";
/*     */   }
/*     */ 
/*     */   public static boolean truncationConversionOK(String paramString)
/*     */   {
/* 192 */     String str = lookupIana(paramString);
/*     */ 
/* 194 */     if (paramString.equals(str))
/*     */     {
/* 196 */       return true;
/*     */     }
/*     */ 
/* 200 */     return !"cp037".equals(str);
/*     */   }
/*     */ 
/*     */   public static Hashtable getConversionMap()
/*     */   {
/* 208 */     return _IANA_MAP;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  38 */     _IANA_MAP.put("iso_1", "ISO8859_1");
/*  39 */     _IANA_MAP.put("ascii_7", "ASCII");
/*  40 */     _IANA_MAP.put("cp1252", "Cp1252");
/*  41 */     _IANA_MAP.put("cp850", "Cp850");
/*  42 */     _IANA_MAP.put("cp437", "Cp437");
/*  43 */     _IANA_MAP.put("mac", "MacRoman");
/*  44 */     _IANA_MAP.put("roman8", "hp-roman8");
/*     */ 
/*  49 */     _IANA_MAP.put("unknown", "UnsupportedCharset");
/*     */ 
/*  54 */     _IANA_MAP.put("cp037", "Cp037");
/*  55 */     _IANA_MAP.put("big5", "Big5");
/*     */ 
/*  68 */     _IANA_MAP.put("big5hk", "Big5_HKSCS");
/*  69 */     _IANA_MAP.put("iso88592", "ISO8859_2");
/*  70 */     _IANA_MAP.put("iso88595", "ISO8859_5");
/*  71 */     _IANA_MAP.put("iso88596", "ISO8859_6");
/*  72 */     _IANA_MAP.put("iso88597", "ISO8859_7");
/*  73 */     _IANA_MAP.put("iso88598", "ISO8859_8");
/*  74 */     _IANA_MAP.put("iso88599", "ISO8859_9");
/*  75 */     _IANA_MAP.put("iso15", "ISO8859_15_FDIS");
/*  76 */     _IANA_MAP.put("cp852", "Cp852");
/*  77 */     _IANA_MAP.put("ibm420", "Cp420");
/*  78 */     _IANA_MAP.put("cp500", "Cp500");
/*  79 */     _IANA_MAP.put("cp855", "Cp855");
/*  80 */     _IANA_MAP.put("cp857", "Cp857");
/*  81 */     _IANA_MAP.put("cp860", "Cp860");
/*  82 */     _IANA_MAP.put("cp863", "Cp863");
/*  83 */     _IANA_MAP.put("cp864", "Cp864");
/*  84 */     _IANA_MAP.put("cp869", "Cp869");
/*  85 */     _IANA_MAP.put("ibm918", "Cp918");
/*  86 */     _IANA_MAP.put("koi8", "KOI8_R");
/*  87 */     _IANA_MAP.put("cp866", "Cp866");
/*  88 */     _IANA_MAP.put("cp1250", "Cp1250");
/*  89 */     _IANA_MAP.put("cp1251", "Cp1251");
/*  90 */     _IANA_MAP.put("cp1253", "Cp1253");
/*  91 */     _IANA_MAP.put("cp1254", "Cp1254");
/*  92 */     _IANA_MAP.put("cp1255", "Cp1255");
/*  93 */     _IANA_MAP.put("cp1256", "Cp1256");
/*  94 */     _IANA_MAP.put("cp1257", "Cp1257");
/*  95 */     _IANA_MAP.put("cp1258", "Cp1258");
/*  96 */     _IANA_MAP.put("cp874", "Cp874");
/*  97 */     _IANA_MAP.put("tis620", "MS874");
/*  98 */     _IANA_MAP.put("mac_cyr", "MacCyrillic");
/*  99 */     _IANA_MAP.put("mac_ee", "MacCentralEurope");
/* 100 */     _IANA_MAP.put("macgreek", "MacGreek");
/* 101 */     _IANA_MAP.put("macturk", "MacTurkish");
/*     */ 
/* 110 */     _IANA_MAP.put("sjis", "MS932");
/*     */ 
/* 112 */     _IANA_MAP.put("eucjis", "EUC_JP");
/* 113 */     _IANA_MAP.put("deckanji", "EUC_JP");
/* 114 */     _IANA_MAP.put("utf8", "UTF8");
/*     */ 
/* 118 */     _IANA_MAP.put("cp932", "MS932");
/* 119 */     _IANA_MAP.put("cp936", "GBK");
/* 120 */     _IANA_MAP.put("eucgb", "EUC_CN");
/* 121 */     _IANA_MAP.put("eucksc", "EUC_KR");
/* 122 */     _IANA_MAP.put("cp950", "Cp950");
/* 123 */     _IANA_MAP.put("cp949", "Cp949");
/* 124 */     _IANA_MAP.put("gb18030", "GB18030");
/*     */ 
/* 127 */     _IANA_MAP.put("euccns", "EUC_TW");
/*     */ 
/* 130 */     _IANA_MAP.put("ucs2", "Unicode");
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.Iana
 * JD-Core Version:    0.5.4
 */