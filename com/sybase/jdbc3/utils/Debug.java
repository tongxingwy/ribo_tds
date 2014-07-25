/*     */ package com.sybase.jdbc3.utils;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.DriverManager;
/*     */ import java.util.Hashtable;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class Debug
/*     */ {
/*     */   private static final String MESSAGE = "Use jConnect devclasses for debugging.";
/*     */   private static final int MAX_JDB_OUTPUT = 128;
/*  63 */   private static boolean _printedMessage = false;
/*  64 */   private static boolean _debugAll = false;
/*  65 */   protected static String _debugClassList = null;
/*  66 */   private static byte[] _debugListBytes = null;
/*     */   protected static PrintStream _debugOutputStream;
/*  68 */   protected static PrintWriter _debugOutputWriter = null;
/*  69 */   private static Hashtable _times = new Hashtable(8);
/*  70 */   private static long _staticTO = 0L;
/*  71 */   private static boolean _disableAssertTrace = false;
/*     */ 
/*     */   public static void startTimer(Object paramObject)
/*     */   {
/*  98 */     long l = System.currentTimeMillis();
/*  99 */     if ((!_debugAll) && (!isDebugObject(paramObject)))
/*     */       return;
/* 101 */     if (null != paramObject)
/*     */     {
/* 103 */       _times.put(paramObject, new Long(System.currentTimeMillis()));
/*     */     }
/*     */     else
/*     */     {
/* 107 */       _staticTO = System.currentTimeMillis();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void stopTimer(Object paramObject, String paramString)
/*     */   {
/* 120 */     long l = System.currentTimeMillis();
/* 121 */     if ((!_debugAll) && (!isDebugObject(paramObject)))
/*     */       return;
/* 123 */     if (null != paramObject)
/*     */     {
/* 125 */       Long localLong = (Long)_times.get(paramObject);
/* 126 */       if (localLong == null)
/*     */       {
/* 128 */         println(formatString(paramObject, "No start time found for " + paramString));
/* 129 */         return;
/*     */       }
/*     */ 
/* 133 */       l -= localLong.longValue();
/* 134 */       _times.remove(paramObject);
/*     */     }
/*     */     else
/*     */     {
/* 139 */       l -= _staticTO;
/*     */     }
/* 141 */     println(formatString(paramObject, paramString + "; elapsed time = " + l + "ms."));
/*     */   }
/*     */ 
/*     */   public static void debug(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 155 */     debug(paramBoolean, "ALL");
/*     */   }
/*     */ 
/*     */   public static void debug(boolean paramBoolean, String paramString)
/*     */     throws IOException
/*     */   {
/* 172 */     debug(paramBoolean, paramString, _debugOutputStream);
/*     */   }
/*     */ 
/*     */   public static void debug(boolean paramBoolean, String paramString, PrintStream paramPrintStream)
/*     */     throws IOException
/*     */   {
/* 187 */     printMessage();
/*     */ 
/* 190 */     if (paramBoolean)
/*     */     {
/* 192 */       if (paramString == null)
/*     */       {
/* 194 */         paramString = "ALL";
/*     */       }
/* 196 */       _debugClassList = paramString;
/* 197 */       _debugOutputStream = paramPrintStream;
/* 198 */       if (null == _debugClassList)
/*     */         return;
/* 200 */       _debugListBytes = new byte[_debugClassList.length()];
/* 201 */       _debugListBytes = _debugClassList.getBytes();
/*     */ 
/* 203 */       _debugAll = isDebugOn();
/*     */     }
/*     */     else
/*     */     {
/* 208 */       _debugClassList = null;
/* 209 */       _debugListBytes = null;
/* 210 */       _debugOutputStream = null;
/* 211 */       _debugAll = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static PrintStream getOutputStream()
/*     */   {
/* 222 */     return _debugOutputStream;
/*     */   }
/*     */ 
/*     */   public static void setOutputStream(PrintStream paramPrintStream)
/*     */   {
/* 232 */     _debugOutputStream = paramPrintStream;
/*     */   }
/*     */ 
/*     */   public static PrintWriter getOutputWriter()
/*     */   {
/* 243 */     return _debugOutputWriter;
/*     */   }
/*     */ 
/*     */   public static void setOutputWriter(PrintWriter paramPrintWriter)
/*     */   {
/* 255 */     _debugOutputWriter = paramPrintWriter;
/*     */   }
/*     */ 
/*     */   public static void println(String paramString)
/*     */   {
/* 275 */     if ((_debugOutputStream == null) && (_debugOutputWriter == null))
/*     */     {
/* 277 */       return;
/*     */     }
/* 279 */     int i = paramString.length();
/* 280 */     int j = 0;
/* 281 */     int k = 0;
/* 282 */     while (k < i)
/*     */     {
/* 284 */       k = j + 128;
/* 285 */       if (k >= i)
/*     */       {
/* 287 */         if (_debugOutputWriter != null)
/*     */         {
/* 289 */           _debugOutputWriter.println(paramString.substring(j, i));
/*     */         }
/*     */         else
/*     */         {
/* 293 */           _debugOutputStream.println(paramString.substring(j, i));
/*     */         }
/*     */ 
/*     */       }
/* 298 */       else if (_debugOutputWriter != null)
/*     */       {
/* 300 */         _debugOutputWriter.println(paramString.substring(j, i));
/*     */       }
/*     */       else
/*     */       {
/* 304 */         _debugOutputStream.print(paramString.substring(j, k));
/*     */       }
/*     */ 
/* 307 */       j = k;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void println(Object paramObject, String paramString)
/*     */   {
/* 324 */     if ((!_debugAll) && (!isDebugObject(paramObject)))
/*     */       return;
/* 326 */     println(formatString(paramObject, paramString));
/*     */   }
/*     */ 
/*     */   public static void printStackTrace(Object paramObject, Exception paramException)
/*     */   {
/* 338 */     if ((!_debugAll) && (!isDebugObject(paramObject)))
/*     */       return;
/* 340 */     println(formatString(paramObject, paramException.toString()));
/*     */ 
/* 342 */     if ((_disableAssertTrace) || 
/* 344 */       (_debugOutputStream == null))
/*     */       return;
/* 346 */     paramException.printStackTrace(_debugOutputStream);
/*     */   }
/*     */ 
/*     */   public static void notSupported(Object paramObject, String paramString)
/*     */     throws UnsupportedOperationException
/*     */   {
/* 370 */     String[] arrayOfString = { className(paramObject) + "." + paramString };
/*     */ 
/* 376 */     ErrorMessage.raiseRuntimeException("JZ0NS", arrayOfString);
/*     */   }
/*     */ 
/*     */   public static void notImplemented(Object paramObject, String paramString)
/*     */     throws UnimplementedOperationException
/*     */   {
/* 392 */     String[] arrayOfString = { className(paramObject) + "." + paramString };
/*     */ 
/* 397 */     ErrorMessage.raiseRuntimeException("ZZ00A", arrayOfString);
/*     */   }
/*     */ 
/*     */   public static void checkObj(Object paramObject1, String paramString, Object paramObject2)
/*     */   {
/*     */   }
/*     */ 
/*     */   public static void asrt(Object paramObject, boolean paramBoolean, String paramString)
/*     */     throws RuntimeException
/*     */   {
/* 435 */     if (paramBoolean)
/*     */       return;
/*     */   }
/*     */ 
/*     */   public static void asrt(Object paramObject, boolean paramBoolean)
/*     */   {
/* 473 */     asrt(paramObject, paramBoolean, null);
/*     */   }
/*     */ 
/*     */   public static void disableAssertTrace(boolean paramBoolean)
/*     */   {
/* 483 */     _disableAssertTrace = paramBoolean;
/*     */   }
/*     */ 
/*     */   public static final void asrt(boolean paramBoolean, String paramString)
/*     */   {
/* 492 */     asrt(null, paramBoolean, paramString);
/*     */   }
/*     */ 
/*     */   private static String className(Object paramObject)
/*     */   {
/* 506 */     if (paramObject != null)
/*     */     {
/* 508 */       return paramObject.getClass().getName();
/*     */     }
/*     */ 
/* 512 */     return "";
/*     */   }
/*     */ 
/*     */   private static String formatString(Object paramObject, String paramString)
/*     */   {
/* 522 */     return className(paramObject) + "(" + Thread.currentThread() + "): " + paramString;
/*     */   }
/*     */ 
/*     */   private static void printMessage()
/*     */   {
/* 533 */     if (_printedMessage) return;
/* 534 */     _printedMessage = true;
/* 535 */     println("Use jConnect devclasses for debugging.");
/*     */   }
/*     */ 
/*     */   private static boolean isDebugObject(Object paramObject)
/*     */   {
/*     */     String str;
/* 549 */     if (paramObject == null)
/*     */     {
/* 552 */       str = "STATIC";
/*     */     }
/*     */     else
/*     */     {
/* 556 */       str = stripPrefix(className(paramObject));
/*     */     }
/* 558 */     if (null == _debugListBytes)
/*     */     {
/* 560 */       return false;
/*     */     }
/*     */ 
/* 564 */     return (str != null) && (parseList(str));
/*     */   }
/*     */ 
/*     */   private static boolean isDebugOn()
/*     */   {
/* 572 */     return parseList("ALL");
/*     */   }
/*     */ 
/*     */   private static String stripPrefix(String paramString)
/*     */   {
/* 582 */     String str = paramString;
/*     */ 
/* 584 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
/* 585 */     int i = localStringTokenizer.countTokens();
/*     */     try
/*     */     {
/* 589 */       while (i-- > 1)
/*     */       {
/* 591 */         localStringTokenizer.nextToken();
/*     */       }
/* 593 */       str = localStringTokenizer.nextToken();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 597 */       println("parsing class name exception for: " + paramString);
/* 598 */       return null;
/*     */     }
/* 600 */     return str;
/*     */   }
/*     */ 
/*     */   private static boolean parseList(String paramString)
/*     */   {
/* 611 */     StringTokenizer localStringTokenizer = new StringTokenizer(_debugListBytes.toString());
/*     */ 
/* 613 */     int i = localStringTokenizer.countTokens();
/*     */     try {
/*     */       do
/* 616 */         if (i-- <= 0)
/*     */           break label40;
/* 618 */       while (localStringTokenizer.nextToken().compareTo(paramString) != 0);
/*     */ 
/* 620 */       label40: return true;
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 626 */       println("parsing debugClassList exception for: " + _debugClassList);
/* 627 */       return false;
/*     */     }
/* 629 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  75 */     _debugOutputStream = DriverManager.getLogStream();
/*  76 */     if (_debugOutputStream != null)
/*     */       return;
/*  78 */     _debugOutputStream = System.out;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.Debug
 * JD-Core Version:    0.5.4
 */