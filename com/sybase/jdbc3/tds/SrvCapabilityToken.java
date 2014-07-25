/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.BitSet;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvCapabilityToken extends CapabilityToken
/*     */   implements Dumpable
/*     */ {
/*  49 */   protected static Hashtable _datamap = new Hashtable();
/*     */ 
/*     */   public SrvCapabilityToken()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SrvCapabilityToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException, SQLException
/*     */   {
/* 255 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public boolean handlesDataType(int paramInt)
/*     */   {
/* 271 */     Object localObject = _datamap.get(new Integer(paramInt));
/* 272 */     if (localObject == null)
/*     */     {
/* 274 */       return false;
/*     */     }
/* 276 */     return !this._respCaps.get(((int[])localObject)[1]);
/*     */   }
/*     */ 
/*     */   protected void setDefaultServerCapabilities()
/*     */   {
/* 289 */     this._reqCaps.clear(3);
/* 290 */     this._reqCaps.clear(4);
/* 291 */     this._reqCaps.clear(5);
/* 292 */     this._reqCaps.clear(6);
/* 293 */     this._reqCaps.clear(7);
/* 294 */     this._reqCaps.clear(18);
/* 295 */     this._reqCaps.clear(19);
/* 296 */     this._reqCaps.clear(25);
/* 297 */     this._reqCaps.clear(26);
/* 298 */     this._reqCaps.clear(31);
/* 299 */     this._reqCaps.clear(32);
/*     */ 
/* 302 */     this._reqCaps.clear(33);
/* 303 */     this._reqCaps.clear(34);
/* 304 */     this._reqCaps.clear(35);
/* 305 */     this._reqCaps.clear(36);
/* 306 */     this._reqCaps.clear(37);
/* 307 */     this._reqCaps.clear(38);
/*     */ 
/* 311 */     this._reqCaps.clear(74);
/* 312 */     this._reqCaps.clear(75);
/* 313 */     this._reqCaps.clear(76);
/* 314 */     this._reqCaps.clear(77);
/* 315 */     this._reqCaps.clear(78);
/*     */ 
/* 318 */     this._reqCaps.clear(44);
/* 319 */     this._reqCaps.clear(45);
/* 320 */     this._reqCaps.clear(46);
/* 321 */     this._reqCaps.clear(47);
/* 322 */     this._reqCaps.clear(48);
/* 323 */     this._reqCaps.clear(49);
/* 324 */     this._reqCaps.clear(50);
/* 325 */     this._reqCaps.clear(51);
/* 326 */     this._reqCaps.clear(52);
/* 327 */     this._reqCaps.clear(53);
/*     */ 
/* 329 */     this._reqCaps.clear(55);
/* 330 */     this._reqCaps.clear(58);
/* 331 */     this._reqCaps.clear(61);
/* 332 */     this._reqCaps.clear(62);
/* 333 */     this._reqCaps.clear(63);
/* 334 */     this._reqCaps.clear(64);
/* 335 */     this._reqCaps.clear(93);
/* 336 */     this._reqCaps.clear(94);
/*     */ 
/* 339 */     this._reqCaps.clear(101);
/* 340 */     this._reqCaps.clear(66);
/* 341 */     this._reqCaps.clear(67);
/* 342 */     this._reqCaps.clear(68);
/* 343 */     this._reqCaps.clear(69);
/* 344 */     this._reqCaps.clear(70);
/*     */ 
/* 348 */     this._respCaps.set(12);
/* 349 */     this._respCaps.set(13);
/* 350 */     this._respCaps.set(14);
/* 351 */     this._respCaps.set(19);
/* 352 */     this._respCaps.set(20);
/* 353 */     this._respCaps.set(26);
/* 354 */     this._respCaps.set(29);
/* 355 */     this._respCaps.set(30);
/* 356 */     this._respCaps.set(35);
/*     */ 
/* 358 */     this._respCaps.set(37);
/* 359 */     this._respCaps.set(38);
/* 360 */     this._respCaps.set(39);
/* 361 */     this._respCaps.set(41);
/* 362 */     this._respCaps.set(42);
/* 363 */     this._respCaps.set(43);
/* 364 */     this._respCaps.set(44);
/* 365 */     this._reqCaps.clear(65);
/* 366 */     this._reqCaps.clear(66);
/*     */ 
/* 369 */     this._respCaps.set(69);
/* 370 */     this._respCaps.set(46);
/* 371 */     this._respCaps.set(47);
/* 372 */     this._respCaps.set(48);
/* 373 */     this._respCaps.set(49);
/* 374 */     this._respCaps.set(50);
/* 375 */     this._respCaps.set(58);
/*     */ 
/* 384 */     if ((this._reqCaps.get(59)) || (this._respCaps.get(45))) {
/*     */       return;
/*     */     }
/* 387 */     this._respCaps.set(45);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 403 */     DumpInfo localDumpInfo = null;
/* 404 */     if (paramDumpFilter.includesToken(226))
/*     */     {
/* 406 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 407 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 409 */         localDumpInfo.addInfo("Token", 1, "CAPABILITY Token (0x" + HexConverts.hexConvert(226, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 415 */         localDumpInfo.addInfo("Token", 1, "CAPABILITY Token");
/*     */       }
/*     */ 
/* 418 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 420 */         localDumpInfo.addInt("Length", 2, this._totalLen);
/*     */       }
/*     */ 
/* 423 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 426 */         Enumeration localEnumeration = this._allCapSets.elements();
/* 427 */         while (localEnumeration.hasMoreElements())
/*     */         {
/* 430 */           CapabilitySet localCapabilitySet = (CapabilitySet)localEnumeration.nextElement();
/* 431 */           localDumpInfo.addInfo("Type", 1, (localCapabilitySet._type == 1) ? "CAP_REQUEST" : "CAP_RESPONSE");
/*     */           String str;
/* 435 */           if (paramDumpFilter.includesDetail(4))
/*     */           {
/* 437 */             str = maskToVerboseString(localCapabilitySet);
/*     */           }
/*     */           else
/*     */           {
/* 441 */             str = maskToString(localCapabilitySet);
/*     */           }
/* 443 */           localDumpInfo.addInfo("Mask", localCapabilitySet._maskLen, str);
/*     */         }
/*     */       }
/*     */     }
/* 447 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 455 */     return 226;
/*     */   }
/*     */ 
/*     */   protected String maskToString(CapabilitySet paramCapabilitySet)
/*     */   {
/* 467 */     String str = "";
/* 468 */     for (int i = 1; i <= paramCapabilitySet._maskLen; ++i)
/*     */     {
/* 471 */       int j = 0;
/* 472 */       for (int k = 7; k >= 0; --k)
/*     */       {
/* 474 */         if (!paramCapabilitySet._caps.get(k))
/*     */           continue;
/* 476 */         j |= 1 << k;
/*     */       }
/*     */ 
/* 480 */       str = str + "0x" + Integer.toHexString(j) + " ";
/*     */     }
/* 482 */     return str;
/*     */   }
/*     */ 
/*     */   public void setRequestCap(int paramInt)
/*     */   {
/* 487 */     this._reqCaps.set(paramInt);
/*     */   }
/*     */ 
/*     */   public void clearRequestCap(int paramInt)
/*     */   {
/* 492 */     this._reqCaps.clear(paramInt);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  55 */     int[][] arrayOfInt = { { 50, 7 }, { 45, 10 }, { 37, 11 }, { 225, 23 }, { 47, 8 }, { 39, 9 }, { 175, 22 }, { 48, 4 }, { 52, 5 }, { 56, 6 }, { 191, 35 }, { 65, 41 }, { 66, 42 }, { 67, 43 }, { 68, 44 }, { 48, 4 }, { 38, 24 }, { 59, 16 }, { 62, 17 }, { 109, 17 }, { 106, 21 }, { 108, 18 }, { 122, 13 }, { 60, 12 }, { 110, 26 }, { 58, 15 }, { 61, 14 }, { 111, 25 }, { 187, 65 }, { 188, 66 }, { 49, 51 }, { 123, 51 }, { 51, 52 }, { 147, 52 }, { 35, 19 }, { 174, 54 }, { 34, 20 }, { 103, 31 }, { 9217, 36 }, { 9219, 37 }, { 9220, 39 } };
/*     */ 
/* 226 */     for (int i = 0; i < arrayOfInt.length; ++i)
/*     */     {
/* 228 */       _datamap.put(new Integer(arrayOfInt[i][0]), arrayOfInt[i]);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvCapabilityToken
 * JD-Core Version:    0.5.4
 */