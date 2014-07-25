/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvAltFmtToken extends Token
/*     */   implements SrvFormatToken, Dumpable
/*     */ {
/*     */   protected int _totalLen;
/*     */   protected int _id;
/*     */   protected int _noOperators;
/*     */   protected Vector _operators;
/*     */   protected int _noByColumns;
/*     */   protected Vector _columns;
/*     */ 
/*     */   public SrvAltFmtToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  62 */     this._totalLen = paramTdsInputStream.readShort();
/*  63 */     this._id = paramTdsInputStream.readShort();
/*  64 */     this._noOperators = paramTdsInputStream.readUnsignedByte();
/*  65 */     this._operators = new Vector();
/*  66 */     for (int i = 0; i < this._noOperators; ++i)
/*     */     {
/*  68 */       OperatorInfo localOperatorInfo = new OperatorInfo(paramTdsInputStream);
/*  69 */       this._operators.addElement(localOperatorInfo);
/*     */     }
/*  71 */     this._columns = new Vector();
/*  72 */     this._noByColumns = paramTdsInputStream.readUnsignedByte();
/*  73 */     for (i = 0; i < this._noByColumns; ++i)
/*     */     {
/*  75 */       this._columns.addElement(new Integer(paramTdsInputStream.readUnsignedByte()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getId()
/*     */   {
/*  84 */     return this._id;
/*     */   }
/*     */ 
/*     */   public void addFormat(DataFormat paramDataFormat)
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/*  99 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getFormatCount()
/*     */   {
/* 107 */     return this._noOperators;
/*     */   }
/*     */ 
/*     */   public DataFormat formatAt(int paramInt)
/*     */   {
/* 115 */     return (SrvDataFormat)this._operators.elementAt(paramInt);
/*     */   }
/*     */ 
/*     */   public void sendFormat(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 139 */     DumpInfo localDumpInfo = null;
/* 140 */     if (paramDumpFilter.includesToken(168))
/*     */     {
/* 142 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 143 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 145 */         localDumpInfo.addInfo("Token", 1, "ALTFMT Token (0x" + HexConverts.hexConvert(168, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 150 */         localDumpInfo.addInfo("Token", 1, "ALTFMT Token");
/*     */       }
/* 152 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 154 */         localDumpInfo.addInt("Length", 2, this._totalLen);
/*     */       }
/* 156 */       if ((paramDumpFilter.includesDetail(3)) || (paramDumpFilter.includesDetail(7)))
/*     */       {
/* 159 */         localDumpInfo.addInt("ID", 2, this._id);
/* 160 */         localDumpInfo.addInt("Number of Operators", 1, this._noOperators);
/* 161 */         Enumeration localEnumeration = this._operators.elements();
/* 162 */         int i = 1;
/*     */         Object localObject;
/* 163 */         while (localEnumeration.hasMoreElements())
/*     */         {
/* 165 */           localDumpInfo.addInfo("TDSFmt", 0, "Operator " + i++);
/* 166 */           localObject = (OperatorInfo)localEnumeration.nextElement();
/* 167 */           localDumpInfo.addInfo(((OperatorInfo)localObject).dump(paramDumpFilter));
/*     */         }
/* 169 */         localDumpInfo.addInt("Number of By Columns", 1, this._noByColumns);
/* 170 */         localEnumeration = this._columns.elements();
/* 171 */         i = 1;
/* 172 */         while (localEnumeration.hasMoreElements())
/*     */         {
/* 174 */           localDumpInfo.addInfo("TDSFmt", 0, "Column " + i++);
/* 175 */           localObject = (Integer)localEnumeration.nextElement();
/* 176 */           localDumpInfo.addInt("", 1, ((Integer)localObject).intValue());
/*     */         }
/*     */       }
/*     */     }
/* 180 */     return (DumpInfo)localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 188 */     return 168;
/*     */   }
/*     */ 
/*     */   class OperatorInfo extends SrvDataFormat
/*     */     implements Dumpable
/*     */   {
/*     */     private int _opType;
/*     */     private int _opColNo;
/*     */ 
/*     */     protected OperatorInfo(TdsInputStream arg2)
/*     */       throws IOException
/*     */     {
/*     */       Object localObject;
/* 205 */       this._opType = localObject.readUnsignedByte();
/* 206 */       this._opColNo = localObject.readUnsignedByte();
/* 207 */       this._usertype = localObject.readInt();
/* 208 */       this._datatype = localObject.readUnsignedByte();
/*     */ 
/* 210 */       switch (DataFormat.lengthSize(this._datatype))
/*     */       {
/*     */       case 1:
/* 213 */         this._length = localObject.readUnsignedByte();
/* 214 */         break;
/*     */       case 2:
/* 217 */         this._length = localObject.readShort();
/* 218 */         break;
/*     */       case 4:
/* 221 */         this._length = localObject.readInt();
/*     */       case 3:
/*     */       }
/*     */ 
/* 224 */       this._localeLen = localObject.readUnsignedByte();
/* 225 */       if (this._localeLen == 0)
/*     */         return;
/* 227 */       this._locale = localObject.readString(this._localeLen);
/*     */     }
/*     */ 
/*     */     public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */       throws IOException
/*     */     {
/* 241 */       DumpInfo localDumpInfo = paramDumpFilter.getDumpInfo();
/* 242 */       localDumpInfo.addInfo("Operator Type", 1, getOperatorTypeString(this._opType));
/* 243 */       localDumpInfo.addInt("Column Number", 1, this._opColNo);
/* 244 */       localDumpInfo.addHex("User Type", 4, this._usertype);
/* 245 */       localDumpInfo.addInfo("Data Type", 1, DataFormat.getDataTypeString(this._datatype));
/*     */ 
/* 247 */       if ((paramDumpFilter.includesDetail(1)) && (DataFormat.lengthSize(this._datatype) > 0))
/*     */       {
/* 250 */         localDumpInfo.addInt("Length", DataFormat.lengthSize(this._datatype), this._length);
/*     */       }
/*     */ 
/* 253 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 255 */         localDumpInfo.addInt("Locale Length", 1, this._localeLen);
/*     */       }
/* 257 */       if (this._localeLen > 0)
/*     */       {
/* 259 */         localDumpInfo.addText("Locale", this._localeLen, this._locale);
/*     */       }
/* 261 */       return localDumpInfo;
/*     */     }
/*     */ 
/*     */     public int getTokenType()
/*     */     {
/* 269 */       return -1;
/*     */     }
/*     */ 
/*     */     private String getOperatorTypeString(int paramInt)
/*     */     {
/*     */       String str;
/* 279 */       switch (paramInt)
/*     */       {
/*     */       case 79:
/* 282 */         str = "ALT_AVG";
/* 283 */         break;
/*     */       case 75:
/* 286 */         str = "ALT_COUNT";
/* 287 */         break;
/*     */       case 82:
/* 290 */         str = "ALT_MAX";
/* 291 */         break;
/*     */       case 81:
/* 294 */         str = "ALT_MIN";
/* 295 */         break;
/*     */       case 77:
/* 298 */         str = "ALT_SUM";
/* 299 */         break;
/*     */       case 76:
/*     */       case 78:
/*     */       case 80:
/*     */       default:
/* 302 */         str = "<unrecognized>";
/*     */       }
/*     */ 
/* 305 */       return str;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvAltFmtToken
 * JD-Core Version:    0.5.4
 */