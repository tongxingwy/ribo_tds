/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import com.sybase.jdbc3.utils.Misc;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.BitSet;
/*     */ 
/*     */ public class DumpFilterImpl
/*     */   implements DumpFilter
/*     */ {
/*     */   private static final long serialVersionUID = -6054625272426879817L;
/*  61 */   public static final int[] DONE_TOKENS = { 253, 255, 254 };
/*     */ 
/*  70 */   public static final int[] DIALOG_TOKENS = { 258, 253, 173, 113, 101 };
/*     */ 
/*  81 */   public static final int[] LANGUAGE_TOKENS = { 33 };
/*     */ 
/*  88 */   public static final int[] CURSOR_TOKENS = { 128, 134, 35, 16, 129, 130, 131, 136, 132, 133 };
/*     */ 
/* 104 */   public static final int[] RPC_TOKENS = { 224, 121, 172, 230 };
/*     */ 
/* 114 */   public static final int[] DYNAMIC_SQL_TOKENS = { 231, 98 };
/*     */ 
/* 122 */   public static final int[] MESSAGE_TOKENS = { 101, 171, 170, 229 };
/*     */ 
/* 132 */   public static final int[] RESULT_TOKENS = { 209, 238, 97, 215, 202, 211, 168, 167, 169, 34 };
/*     */ 
/* 149 */   public static final int[] FORMAT_TOKENS = { 236, 32, 238, 97, 168 };
/*     */ 
/* 161 */   public static final int[] ERROR_TOKENS = { 170 };
/*     */ 
/* 168 */   public static final int[] ALL_TOKENS = { 256, 257, 258, 259, 229, 173, 113, 227, 170, 171, 33, 224, 230, 231, 98, 101, 162, 209, 238, 97, 168, 211, 167, 215, 236, 32, 172, 121, 226, 174, 253, 254, 255, 128, 129, 130, 131, 136, 132, 133, 134, 35, 16, 202, 169, 34, 96 };
/*     */   private BitSet _includedTokens;
/*     */   private BitSet _includedDetails;
/*     */   private transient String _fileName;
/*     */ 
/*     */   public static DumpFilter newUserFilter()
/*     */   {
/* 240 */     DumpFilterImpl answer = new DumpFilterImpl();
/* 241 */     answer.includeAllTokens();
/* 242 */     answer.includeAllDetails();
/* 243 */     return answer;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 255 */       DumpFilterImpl f = new DumpFilterImpl();
/* 256 */       f.includeAllTokens();
/* 257 */       f.includeAllDetails();
/* 258 */       String filterName = "devFilter";
/* 259 */       f.saveFilter(filterName);
/*     */ 
/* 261 */       System.out.println(filterName + " has been created in the current directory");
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 266 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static DumpFilter loadFilter(String aString)
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 278 */     FileInputStream fileStream = new FileInputStream(aString);
/* 279 */     ObjectInputStream stream = new ObjectInputStream(fileStream);
/* 280 */     DumpFilter answer = (DumpFilter)stream.readObject();
/* 281 */     stream.close();
/*     */ 
/* 283 */     ((DumpFilterImpl)answer)._fileName = aString;
/*     */ 
/* 285 */     return answer;
/*     */   }
/*     */ 
/*     */   public DumpFilterImpl()
/*     */   {
/* 294 */     this._includedTokens = new BitSet();
/* 295 */     this._includedDetails = new BitSet();
/* 296 */     this._fileName = "";
/*     */   }
/*     */ 
/*     */   public DumpInfo getDumpInfo()
/*     */   {
/* 305 */     DumpInfoImpl answer = new DumpInfoImpl();
/* 306 */     return answer;
/*     */   }
/*     */ 
/*     */   public void saveFilter(String aString)
/*     */     throws IOException
/*     */   {
/* 315 */     Misc.checkOutputFilePath(aString);
/* 316 */     FileOutputStream fileStream = new FileOutputStream(aString);
/* 317 */     ObjectOutputStream stream = new ObjectOutputStream(fileStream);
/* 318 */     stream.writeObject(this);
/* 319 */     stream.flush();
/* 320 */     stream.close();
/* 321 */     this._fileName = aString;
/*     */   }
/*     */ 
/*     */   public boolean includesToken(int tokenType)
/*     */   {
/* 334 */     return this._includedTokens.get(tokenType);
/*     */   }
/*     */ 
/*     */   public boolean includesDetail(int detailGroupType)
/*     */   {
/* 344 */     return this._includedDetails.get(detailGroupType);
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 352 */     return this._fileName;
/*     */   }
/*     */ 
/*     */   public void setFileName(String name)
/*     */   {
/* 360 */     this._fileName = name;
/*     */   }
/*     */ 
/*     */   public void excludeToken(int tokenType)
/*     */   {
/* 376 */     this._includedTokens.clear(tokenType);
/*     */   }
/*     */ 
/*     */   public void includeToken(int tokenType)
/*     */   {
/* 387 */     this._includedTokens.set(tokenType);
/*     */   }
/*     */ 
/*     */   public void includeGroup(int[] group)
/*     */   {
/* 401 */     if (group == ALL_TOKENS)
/*     */     {
/* 403 */       includeAllTokens();
/*     */     }
/*     */     else
/*     */     {
/* 407 */       int len = group.length;
/* 408 */       for (int i = 0; i < len; ++i)
/*     */       {
/* 410 */         this._includedTokens.set(group[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void excludeGroup(int[] group)
/*     */   {
/* 422 */     if (group == ALL_TOKENS)
/*     */     {
/* 424 */       excludeAllTokens();
/*     */     }
/*     */     else
/*     */     {
/* 428 */       int len = group.length;
/* 429 */       for (int i = 0; i < len; ++i)
/*     */       {
/* 431 */         this._includedTokens.clear(group[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void includeAllTokens()
/*     */   {
/* 442 */     for (int i = 0; i < 512; ++i)
/*     */     {
/* 444 */       this._includedTokens.set(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void excludeAllTokens()
/*     */   {
/* 454 */     this._includedTokens = new BitSet();
/*     */   }
/*     */ 
/*     */   public void includeDetails(int group)
/*     */   {
/* 467 */     if (group == 11)
/*     */     {
/* 469 */       includeAllDetails();
/*     */     }
/*     */     else
/*     */     {
/* 473 */       this._includedDetails.set(group);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void excludeDetails(int group)
/*     */   {
/* 484 */     if (group == 11)
/*     */     {
/* 486 */       excludeAllDetails();
/*     */     }
/*     */     else
/*     */     {
/* 490 */       this._includedDetails.clear(group);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void includeAllDetails()
/*     */   {
/* 499 */     for (int i = 0; i < 11; ++i)
/*     */     {
/* 501 */       this._includedDetails.set(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void excludeAllDetails()
/*     */   {
/* 510 */     this._includedDetails = new BitSet();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 520 */     String answer = "";
/*     */ 
/* 522 */     for (int i = 0; i < ALL_TOKENS.length; ++i)
/*     */     {
/* 524 */       if (!includesToken(ALL_TOKENS[i]))
/*     */         continue;
/* 526 */       answer = answer + ", " + HexConverts.hexConvert(ALL_TOKENS[i], 3);
/*     */     }
/*     */ 
/* 529 */     return answer;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.DumpFilterImpl
 * JD-Core Version:    0.5.4
 */