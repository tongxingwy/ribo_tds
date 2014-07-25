/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*     */ import com.sybase.jdbc3.jdbc.SybProperty;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class CapabilityToken extends Token
/*     */ {
/*     */   protected CapabilitySet _reqCaps;
/*     */   protected CapabilitySet _respCaps;
/*     */   protected Vector _allCapSets;
/*     */   protected int _totalLen;
/*  38 */   protected int _capsTry = 0;
/*  39 */   protected boolean _wideTableOn = false;
/*     */ 
/* 481 */   protected static final String[] REQUEST_LABELS = { "NONE", "REQ_LANG", "REQ_RPC", "REQ_EVT", "REQ_MSTMT", "REQ_BCP", "REQ_CURSOR", "REQ_DYNF", "REQ_MSG", "REQ_PARAM", "DATA_INT1", "DATA_INT2", "DATA_INT4", "DATA_BIT", "DATA_CHAR", "DATA_VCHAR", "DATA_BIN", "DATA_VBIN", "DATA_MNY8", "DATA_MNY4", "DATA_DATE8", "DATA_DATE4", "DATA_FLT4", "DATA_FLT8", "DATA_NUM", "DATA_TEXT", "DATA_IMAGE", "DATA_DEC", "DATA_LCHAR", "DATA_LBIN", "DATA_INTN", "DATA_DATETIMEN", "DATA_MONEYN", "CSR_PREV", "CSR_FIRST", "CSR_LAST", "CSR_ABS", "CSR_REL", "CSR_MULTI", "CON_OOB", "CON_INBAND", "CON_LOGICAL", "PROTO_TEXT", "PROTO_BULK", "REQ_URGEVT", "DATA_SENSITIVITY", "DATA_BOUNDARY", "PROTO_DYNAMIC", "PROTO_DYNPROC", "DATA_FLTN", "DATA_BITN", "DATA_INT8", "DATA_VOID", "DOL_BULK", "OBJECT_JAVA1", "OBJECT_CHAR", "REQ_RESERVED1", "OBJECT_BINARY", "DATA_COLUMNSTATUS", "WIDETABLE", "REQ_RESERVED2", "DATA_UINT2", "DATA_UINT4", "DATA_UINT8", "DATA_UINTN", "CUR_IMPLICIT", "DATA_NLBIN", "IMAGE_NCHAR", "BLOB_NCHAR_16", "BLOB_NCHAR_8", "BLOB_NCHAR_SCSU", "DATA_DATE", "DATA_TIME", "DATA_INTERVAL", "CSR_SCROLL", "CSR_SENSITIVE", "CSR_INSENSITIVE", "CSR_SEMISENSITIVE", "CSR_KEYSETDRIVEN", "REQ_SRVPKTSIZE", "DATA_UNITEXT", "CAP_CLUSTERFAILOVER", "DATA_SINT1", "REQ_LARGEIDENT", "REQ_BLOB_NCHAR_16", "DATA_XML", "REQ_CURINFO3", "REQ_DBRPC2", "REQ_UNUSED", "REQ_MIGRATE", "MULTI_REQUESTS", "REQ_UNUSED", "REQ_UNUSED", "DATA_BIGDATETIME", "DATA_USECS", "RPCPARAM_LOB", "REQ_INSTID", "REQ_GRID", "REQ_DYN_BATCH", "REQ_LANG_BATCH", "REQ_RPC_BATCH", "DATA_LOBLOCATOR", "REQ_UNUSED", "REQ_UNUSED" };
/*     */ 
/* 590 */   protected static final String[] RESPONSE_LABELS = { "NONE", "RES_NOMSG", "RES_NOEED", "RES_NOPARAM", "DATA_NOINT1", "DATA_NOINT2", "DATA_NOINT4", "DATA_NOBIT", "DATA_NOCHAR", "DATA_NOVCHAR", "DATA_NOBIN", "DATA_NOVBIN", "DATA_NOMNY8", "DATA_NOMNY4", "DATA_NODATE8", "DATA_NODATE4", "DATA_NOFLT4", "DATA_NOFLT8", "DATA_NONUM", "DATA_NOTEXT", "DATA_NOIMAGE", "DATA_NODEC", "DATA_NOLCHAR", "DATA_NOLBIN", "DATA_NOINTN", "DATA_NODATETIMEN", "DATA_NOMONEYN", "CON_NOOOB", "CON_NOINBAND", "PROTO_NOTEXT", "PROTO_NOBULK", "DATA_NOSENSITIVITY", "DATA_NOBOUNDARY", "RES_NOTDSDEBUG", "RES_NOSTRIPBLANKS", "DATA_NOINT8", "OBJECT_NOJAVA1", "OBJECT_NOCHAR", "DATA_NOCOLUMNSTATUS", "OBJECT_NOBINARY", "RES_RESERVED1", "DATA_NOUINT2", "DATA_NOUINT4", "DATA_NOUINT8", "DATA_NOUINTN", "NO_WIDETABLES", "DATA_NONLBIN", "IMAGE_NONCHAR", "BLOB_NONCHAR_16", "BLOB_NONCHAR_8", "BLOB_NONCHAR_SCSU", "DATA_NODATE", "DATA_NOTIME", "DATA_NOINTERVAL", "DATA_NOUNITEXT", "DATA_NOSINT1", "RES_NOLARGEIDENT", "RES_NOBLOB_NCHAR_16", "NO_SRVPKTSIZE", "RES_NODATA_XML", "NONINT_RETURN_VALUE", "RES_NOXNLDATA", "RES_SUPPRESS_FMT", "RES_SUPPRESS_DONEINPROC", "RES_FORCE_ROWFMT2", "DATA_NOBIGDATETIME", "DATA_NOUSECS", "RES_NO_TDSCONTROL", "RPCPARAM_NOLOB", "RES_UNUSED", "DATA_NOLOBLOCATOR", "RES_UNUSED" };
/*     */ 
/*     */   public CapabilityToken()
/*     */   {
/*  49 */     this._allCapSets = new Vector();
/*  50 */     int i = 14;
/*  51 */     CapabilitySet localCapabilitySet = new CapabilitySet(1, i);
/*  52 */     this._reqCaps = localCapabilitySet;
/*  53 */     this._allCapSets.addElement(localCapabilitySet);
/*  54 */     i = 10;
/*  55 */     localCapabilitySet = new CapabilitySet(2, i);
/*  56 */     this._respCaps = localCapabilitySet;
/*  57 */     this._allCapSets.addElement(localCapabilitySet);
/*     */ 
/*  59 */     this._totalLen = (this._reqCaps._maskLen + this._respCaps._maskLen + 4);
/*     */   }
/*     */ 
/*     */   public CapabilityToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException, SQLException
/*     */   {
/*  76 */     this._allCapSets = new Vector();
/*     */     try
/*     */     {
/*  79 */       int k = paramTdsInputStream.readShort();
/*  80 */       this._totalLen = k;
/*  81 */       while (k > 0)
/*     */       {
/*  83 */         int i = paramTdsInputStream.readUnsignedByte();
/*  84 */         int j = paramTdsInputStream.readUnsignedByte();
/*  85 */         if (j == 0)
/*     */         {
/*  90 */           ErrorMessage.raiseWarning("010SM");
/*     */         }
/*     */ 
/*  95 */         CapabilitySet localCapabilitySet = new CapabilitySet(i, j);
/*  96 */         this._allCapSets.addElement(localCapabilitySet);
/*     */ 
/*  99 */         for (int l = 1; l <= j; ++l)
/*     */         {
/* 101 */           i1 = paramTdsInputStream.readUnsignedByte();
/* 102 */           for (int i2 = 7; i2 >= 0; --i2)
/*     */           {
/* 104 */             if ((i1 & 1 << i2) == 0)
/*     */             {
/* 106 */               localCapabilitySet.clear(8 * (j - l) + i2);
/*     */             }
/*     */             else
/*     */             {
/* 110 */               localCapabilitySet.set(8 * (j - l) + i2);
/*     */             }
/*     */           }
/*     */         }
/* 114 */         l = 1;
/* 115 */         switch (i)
/*     */         {
/*     */         case 1:
/* 118 */           l += 105;
/* 119 */           this._reqCaps = localCapabilitySet;
/* 120 */           break;
/*     */         case 2:
/* 122 */           l += 73;
/* 123 */           this._respCaps = localCapabilitySet;
/*     */         }
/*     */ 
/* 128 */         for (int i1 = l; i1 < 8 * j; ++i1)
/*     */         {
/* 130 */           localCapabilitySet.clear(i1);
/*     */         }
/* 132 */         k -= 2 + j;
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 138 */       readSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 158 */       paramTdsOutputStream.writeByte(226);
/*     */ 
/* 161 */       paramTdsOutputStream.writeShort(this._totalLen);
/* 162 */       for (int i = 0; i < this._allCapSets.size(); ++i)
/*     */       {
/* 164 */         CapabilitySet localCapabilitySet = (CapabilitySet)this._allCapSets.elementAt(i);
/* 165 */         paramTdsOutputStream.writeByte(localCapabilitySet._type);
/* 166 */         paramTdsOutputStream.writeByte(localCapabilitySet._maskLen);
/* 167 */         int j = 0;
/* 168 */         for (int k = localCapabilitySet._bits - 1; k >= 0; --k)
/*     */         {
/* 170 */           if (localCapabilitySet.get(k))
/*     */           {
/* 172 */             j |= 1 << k % 8;
/*     */           }
/* 174 */           if (k % 8 != 0)
/*     */             continue;
/* 176 */           paramTdsOutputStream.writeByte(j);
/* 177 */           j = 0;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 184 */       writeSQE(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setCapabilities(SybProperty paramSybProperty)
/*     */     throws SQLException
/*     */   {
/* 192 */     this._capsTry += 1;
/* 193 */     switch (this._capsTry)
/*     */     {
/*     */     case 1:
/* 203 */       float f = paramSybProperty.getFloat(22);
/*     */       int i;
/*     */       int j;
/* 207 */       if (f >= 7.0F)
/*     */       {
/* 209 */         i = 105;
/* 210 */         j = 73;
/*     */       }
/* 212 */       else if (f >= 6.05F)
/*     */       {
/* 214 */         i = 90;
/* 215 */         j = 59;
/*     */ 
/* 219 */         k = i / 8 + 1;
/* 220 */         this._reqCaps.setMaskSize(k);
/* 221 */         this._respCaps.setMaskSize(k);
/* 222 */         this._totalLen = (2 * k + 4);
/*     */       }
/* 224 */       else if (f >= 6.0F)
/*     */       {
/* 226 */         i = 73;
/* 227 */         j = 52;
/*     */ 
/* 231 */         k = i / 8 + 1;
/* 232 */         this._reqCaps.setMaskSize(k);
/* 233 */         this._respCaps.setMaskSize(k);
/* 234 */         this._totalLen = (2 * k + 4);
/*     */       }
/* 236 */       else if (f >= 4.0F)
/*     */       {
/* 238 */         i = 57;
/* 239 */         j = 39;
/*     */ 
/* 244 */         k = i / 8 + 1;
/* 245 */         this._reqCaps.setMaskSize(k);
/* 246 */         this._respCaps.setMaskSize(k);
/* 247 */         this._totalLen = (2 * k + 4);
/*     */       }
/*     */       else
/*     */       {
/* 263 */         i = 57;
/* 264 */         j = 39;
/*     */       }
/*     */ 
/* 269 */       for (int k = 1; k <= i; ++k)
/*     */       {
/* 271 */         this._reqCaps.set(k);
/*     */       }
/*     */ 
/* 276 */       this._reqCaps.clear(0);
/* 277 */       this._reqCaps.clear(3);
/*     */ 
/* 279 */       String str1 = paramSybProperty.getString(68);
/* 280 */       if ((str1 == null) || (str1.equalsIgnoreCase("NONE")) || (str1.equalsIgnoreCase("FALSE")))
/*     */       {
/* 283 */         this._reqCaps.clear(5);
/*     */       }
/*     */ 
/* 294 */       this._reqCaps.clear(39);
/*     */ 
/* 296 */       this._reqCaps.clear(41);
/* 297 */       this._reqCaps.clear(43);
/* 298 */       this._reqCaps.clear(44);
/* 299 */       this._reqCaps.clear(52);
/* 300 */       this._reqCaps.clear(56);
/*     */ 
/* 302 */       this._reqCaps.clear(60);
/*     */ 
/* 305 */       this._reqCaps.clear(65);
/*     */ 
/* 307 */       this._reqCaps.clear(67);
/* 308 */       this._reqCaps.clear(69);
/* 309 */       this._reqCaps.clear(70);
/* 310 */       if (!paramSybProperty.getBoolean(54)) {
/* 311 */         this._reqCaps.clear(79);
/*     */       }
/*     */ 
/* 321 */       this._reqCaps.clear(68);
/*     */ 
/* 324 */       this._reqCaps.clear(47);
/*     */ 
/* 326 */       if (!paramSybProperty.getBoolean(34))
/*     */       {
/* 328 */         this._reqCaps.clear(81);
/*     */       }
/* 330 */       if (!paramSybProperty.getBoolean(72))
/*     */       {
/* 332 */         this._reqCaps.clear(89);
/*     */       }
/* 334 */       if (!paramSybProperty.getBoolean(81))
/*     */       {
/* 336 */         this._reqCaps.clear(102);
/*     */       }
/* 338 */       this._reqCaps.clear(85);
/* 339 */       if (!paramSybProperty.getBoolean(90))
/*     */       {
/* 341 */         this._reqCaps.clear(104);
/*     */       }
/* 343 */       if (!paramSybProperty.getBoolean(97))
/*     */       {
/* 345 */         this._reqCaps.clear(105);
/*     */       }
/*     */ 
/* 349 */       for (int l = 0; l <= j; ++l)
/*     */       {
/* 351 */         this._respCaps.clear(l);
/*     */       }
/* 353 */       this._respCaps.set(27);
/* 354 */       this._respCaps.set(30);
/* 355 */       this._respCaps.set(33);
/* 356 */       if (!paramSybProperty.getBoolean(92))
/*     */       {
/* 358 */         this._respCaps.set(34);
/*     */       }
/*     */ 
/* 376 */       if (paramSybProperty.getFloat(22) < 6.0F)
/*     */       {
/* 379 */         this._respCaps.set(3);
/*     */       }
/*     */ 
/* 384 */       this._respCaps.set(47);
/* 385 */       this._respCaps.set(49);
/* 386 */       this._respCaps.set(50);
/*     */ 
/* 404 */       if (!paramSybProperty.getBoolean(54))
/*     */       {
/* 406 */         this._respCaps.set(58);
/*     */       }
/*     */ 
/* 410 */       if (!paramSybProperty.getBoolean(59))
/*     */       {
/* 412 */         this._reqCaps.clear(59);
/* 413 */         this._respCaps.clear(45);
/*     */       }
/*     */ 
/* 417 */       if (paramSybProperty.getBoolean(87))
/*     */       {
/* 419 */         this._respCaps.set(67);
/*     */       }
/*     */ 
/* 424 */       if (paramSybProperty.getBoolean(88))
/*     */       {
/* 426 */         this._respCaps.set(61);
/*     */       }
/*     */ 
/* 430 */       if (paramSybProperty.getBoolean(89))
/*     */       {
/* 432 */         this._respCaps.set(62);
/*     */       }
/*     */ 
/* 435 */       String str2 = paramSybProperty.getString(96);
/* 436 */       if (str2 == null)
/*     */         return;
/* 438 */       if (str2.equalsIgnoreCase("HADR_NOKILL"))
/*     */       {
/* 443 */         this._respCaps.set(73); return;
/*     */       }
/* 445 */       if (str2.equalsIgnoreCase("HADR_MAP"))
/*     */       {
/* 447 */         this._respCaps.set(72); return;
/*     */       }
/* 449 */       if ((!str2.equalsIgnoreCase("HADR_NOKILL_WITH_MAP")) && (!str2.equalsIgnoreCase("HADR_RECONNECT"))) {
/*     */         return;
/*     */       }
/* 452 */       this._respCaps.set(73);
/*     */ 
/* 456 */       this._respCaps.set(72); break;
/*     */     case 2:
/* 470 */       int i1 = 7;
/* 471 */       this._reqCaps.setMaskSize(i1);
/* 472 */       this._respCaps.setMaskSize(i1);
/* 473 */       this._totalLen = (2 * i1 + 4);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 669 */     StringBuffer localStringBuffer = new StringBuffer();
/* 670 */     localStringBuffer.append("Request capabilities:\n");
/* 671 */     for (int i = 1; i < REQUEST_LABELS.length; ++i)
/*     */     {
/* 673 */       localStringBuffer.append(REQUEST_LABELS[i] + ": " + this._reqCaps.get(i) + "\n");
/*     */     }
/* 675 */     localStringBuffer.append("\nResponse capabilities:\n");
/* 676 */     for (i = 1; i < RESPONSE_LABELS.length; ++i)
/*     */     {
/* 678 */       localStringBuffer.append(RESPONSE_LABELS[i] + ": " + this._respCaps.get(i) + "\n");
/*     */     }
/* 680 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   protected void printMask()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected String maskToVerboseString(CapabilitySet paramCapabilitySet)
/*     */   {
/* 709 */     String str1 = "\n";
/* 710 */     int i = (paramCapabilitySet._type == 1) ? 1 : 0;
/* 711 */     String[] arrayOfString = (i != 0) ? REQUEST_LABELS : RESPONSE_LABELS;
/* 712 */     String str2 = "";
/* 713 */     int j = 0;
/*     */ 
/* 715 */     for (int l = arrayOfString.length - 1; l >= 0; --l)
/*     */     {
/* 717 */       if (l < arrayOfString.length)
/*     */       {
/* 719 */         if (str2.length() > 0)
/*     */         {
/* 721 */           str2 = str2 + ", ";
/*     */         }
/* 723 */         if (!paramCapabilitySet.get(l))
/*     */         {
/* 725 */           str2 = str2 + "(";
/*     */         }
/*     */ 
/* 728 */         str2 = str2 + arrayOfString[l];
/*     */ 
/* 730 */         if (!paramCapabilitySet.get(l))
/*     */         {
/* 732 */           str2 = str2 + ")";
/*     */         }
/*     */       }
/*     */ 
/* 736 */       if (paramCapabilitySet.get(l))
/*     */       {
/* 738 */         j |= 1 << l % 8;
/*     */       }
/* 740 */       if (l % 8 != 0)
/*     */         continue;
/* 742 */       str1 = str1 + "0x" + HexConverts.hexConvert(j, 1) + " (" + HexConverts.binaryConvert(j, 1) + "): " + str2 + " \n";
/*     */ 
/* 745 */       str2 = "";
/* 746 */       int k = 0;
/*     */     }
/*     */ 
/* 750 */     return str1;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.CapabilityToken
 * JD-Core Version:    0.5.4
 */