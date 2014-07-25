/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvDataFormat extends DataFormat
/*     */   implements Dumpable
/*     */ {
/*     */   private int _parentTokenType;
/*     */ 
/*     */   public SrvDataFormat(String paramString, int paramInt1, int paramInt2, int paramInt3, Object paramObject)
/*     */   {
/*  55 */     this(paramString, paramInt1, paramInt2, paramInt3, paramObject, null);
/*     */   }
/*     */ 
/*     */   public SrvDataFormat(String paramString1, int paramInt1, int paramInt2, int paramInt3, Object paramObject, String paramString2)
/*     */   {
/*  76 */     this._name = paramString1;
/*  77 */     if (this._name != null)
/*     */     {
/*  79 */       this._nameLen = paramString1.length();
/*     */     }
/*     */     else
/*     */     {
/*  83 */       this._nameLen = 0;
/*     */     }
/*  85 */     this._status = paramInt2;
/*  86 */     this._usertype = 0;
/*     */ 
/*  90 */     switch (paramInt1)
/*     */     {
/*     */     case 9217:
/*  93 */       this._datatype = 36;
/*  94 */       this._blobType = 1;
/*  95 */       if (paramObject != null)
/*     */       {
/*  97 */         this._className = paramObject.getClass().getName();
/*  98 */         this._classIdLen = this._className.length();
/*     */       }
/*     */       else
/*     */       {
/* 102 */         this._classIdLen = 0;
/*     */       }
/* 104 */       break;
/*     */     case 9219:
/* 106 */       this._datatype = 36;
/* 107 */       this._blobType = 3;
/* 108 */       this._classIdLen = 0;
/* 109 */       break;
/*     */     case 9221:
/* 111 */       this._datatype = 36;
/* 112 */       this._blobType = 5;
/* 113 */       this._classIdLen = 0;
/* 114 */       break;
/*     */     case 9220:
/* 116 */       this._datatype = 36;
/* 117 */       this._blobType = 4;
/* 118 */       this._classIdLen = 0;
/* 119 */       break;
/*     */     case 34:
/*     */     case 35:
/*     */     case 174:
/* 125 */       if ((paramObject != null) && (paramObject instanceof SrvTextImageData))
/*     */       {
/* 127 */         this._tableName = ((SrvTextImageData)paramObject).getTableName();
/*     */       }
/*     */     default:
/* 131 */       this._datatype = paramInt1;
/*     */     }
/*     */ 
/* 134 */     this._length = paramInt3;
/*     */ 
/* 136 */     this._precision = 0;
/* 137 */     this._scale = 0;
/* 138 */     this._locale = paramString2;
/* 139 */     this._localeLen = ((paramString2 == null) ? 0 : paramString2.length());
/*     */   }
/*     */ 
/*     */   public SrvDataFormat(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Object paramObject)
/*     */   {
/* 164 */     this(paramString, paramInt1, paramInt2, paramInt3, paramObject);
/* 165 */     this._precision = paramInt4;
/* 166 */     this._scale = paramInt5;
/*     */   }
/*     */ 
/*     */   public SrvDataFormat(String paramString1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString2, String paramString3)
/*     */   {
/* 179 */     this(paramString1, paramInt1, paramInt2, paramInt3, null);
/* 180 */     this._precision = paramInt4;
/* 181 */     this._scale = paramInt5;
/*     */ 
/* 185 */     switch (paramInt1)
/*     */     {
/*     */     case 9217:
/* 188 */       if (paramString3 == null)
/*     */         return;
/* 190 */       this._className = paramString3;
/* 191 */       this._classIdLen = this._className.length(); break;
/*     */     case 34:
/*     */     case 35:
/*     */     case 174:
/* 198 */       this._tableName = paramString2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SrvDataFormat(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/* 214 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   protected SrvDataFormat()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void setParentTokenType(int paramInt)
/*     */   {
/* 226 */     this._parentTokenType = paramInt;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 243 */     DumpInfo localDumpInfo = paramDumpFilter.getDumpInfo();
/* 244 */     if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(7)))
/*     */     {
/* 247 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 250 */         localDumpInfo.addInt("Name Length", 1, this._nameLen);
/*     */       }
/* 252 */       if (this._nameLen > 0)
/*     */       {
/* 254 */         localDumpInfo.addText("Name", this._nameLen, getName());
/*     */       }
/* 256 */       String[] arrayOfString = null;
/*     */ 
/* 258 */       if (this._parentTokenType == 238)
/*     */       {
/* 260 */         arrayOfString = new String[] { "<unrecognized>", "ROW_HIDDEN", "ROW_KEY", "ROW_VERSION", "ROW_COLUMNSTATUS", "ROW_UPDATABLE", "ROW_NULLALLOWED", "ROW_IDENTITY ", "ROW_PADCHAR" };
/*     */       }
/*     */       else
/*     */       {
/* 269 */         arrayOfString = new String[] { "<unrecognized>", "PARAM_RETURN", "<unrecognized>", "<unrecognized>", "PARAM_COLUMNSTATUS", "<unrecognized>", "PARAM_NULLALLOWED" };
/*     */       }
/*     */ 
/* 277 */       localDumpInfo.addBitfield("Status", 1, this._status, arrayOfString);
/* 278 */       localDumpInfo.addInt("User Type", 4, this._usertype);
/* 279 */       localDumpInfo.addInfo("Data Type", 1, getDataTypeString(this._datatype));
/* 280 */       if (this._datatype == 36)
/*     */       {
/* 282 */         String str = null;
/* 283 */         switch (this._blobType)
/*     */         {
/*     */         case 1:
/* 286 */           str = "JAVA_OBJECT1";
/* 287 */           break;
/*     */         case 2:
/* 289 */           str = "JAVA_OBJECT2";
/* 290 */           break;
/*     */         case 3:
/* 292 */           str = "BLOB_VARCHAR";
/* 293 */           break;
/*     */         case 4:
/* 295 */           str = "BLOB_VARBINARY";
/* 296 */           break;
/*     */         case 5:
/* 298 */           str = "BLOB_UTF16";
/* 299 */           break;
/*     */         case 7:
/* 301 */           str = "IMAGE LOCATOR";
/* 302 */           break;
/*     */         case 6:
/* 304 */           str = "TEXT LOCATOR";
/* 305 */           break;
/*     */         case 8:
/* 307 */           str = "UNITEXT LOCATOR";
/*     */         }
/*     */ 
/* 312 */         localDumpInfo.addInfo("Blob Type", 1, str + " (" + this._blobType + ")");
/*     */ 
/* 315 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 317 */           localDumpInfo.addInt("Class ID Length", 2, this._classIdLen);
/*     */         }
/* 319 */         if (this._classIdLen > 0)
/*     */         {
/* 321 */           localDumpInfo.addText("ClassID", this._classIdLen, this._className);
/*     */         }
/*     */       }
/* 324 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 326 */         localDumpInfo.addInt("Length", lengthSize(this._datatype), this._length);
/*     */       }
/* 328 */       if ((this._datatype == 106) || (this._datatype == 108))
/*     */       {
/* 330 */         localDumpInfo.addInt("Precision", 1, this._precision);
/* 331 */         localDumpInfo.addInt("Scale", 1, this._scale);
/*     */       }
/* 333 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 335 */         localDumpInfo.addInt("Locale Length", 1, this._localeLen);
/*     */       }
/* 337 */       if (this._localeLen > 0)
/*     */       {
/* 339 */         localDumpInfo.addText("Locale", this._localeLen, this._locale);
/*     */       }
/*     */     }
/* 342 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 350 */     return -1;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDataFormat
 * JD-Core Version:    0.5.4
 */