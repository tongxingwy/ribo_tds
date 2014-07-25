/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvColInfoToken extends Token
/*     */   implements Dumpable
/*     */ {
/*     */   public static final int TDS_STAT_RENAME = 32;
/*     */   private int _totalLength;
/*     */   private Vector _columns;
/*     */ 
/*     */   public SrvColInfoToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  39 */     this._columns = new Vector();
/*  40 */     this._totalLength = paramTdsInputStream.readShort();
/*     */ 
/*  42 */     int i = this._totalLength;
/*  43 */     while (i > 0)
/*     */     {
/*  45 */       ColumnInfo localColumnInfo = new ColumnInfo(paramTdsInputStream);
/*  46 */       this._columns.addElement(localColumnInfo);
/*  47 */       i -= localColumnInfo.getLength();
/*     */     }
/*  49 */     if (i >= 0)
/*     */       return;
/*  51 */     throw new IOException("Malformed ColInfo token lengths");
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  66 */     DumpInfo localDumpInfo = null;
/*  67 */     if (paramDumpFilter.includesToken(165))
/*     */     {
/*  69 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  70 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  72 */         localDumpInfo.addInfo("Token", 1, "COLINFO Token (0x" + HexConverts.hexConvert(165, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/*  78 */         localDumpInfo.addInfo("Token", 1, "COLINFO Token");
/*     */       }
/*     */ 
/*  81 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/*  83 */         localDumpInfo.addInt("Length", 2, this._totalLength);
/*     */       }
/*     */ 
/*  86 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/*  88 */         Enumeration localEnumeration = this._columns.elements();
/*  89 */         while (localEnumeration.hasMoreElements())
/*     */         {
/*  91 */           ColumnInfo localColumnInfo = (ColumnInfo)localEnumeration.nextElement();
/*  92 */           localDumpInfo.addInfo(localColumnInfo.dump(paramDumpFilter));
/*     */         }
/*     */       }
/*     */     }
/*  96 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 105 */     return 165;
/*     */   }
/*     */ 
/*     */   private class ColumnInfo
/*     */     implements Dumpable
/*     */   {
/*     */     private int _columnNo;
/*     */     private int _tableNo;
/*     */     private int _status;
/*     */     private String _name;
/*     */ 
/*     */     protected ColumnInfo(TdsInputStream arg2)
/*     */       throws IOException
/*     */     {
/*     */       Object localObject;
/* 126 */       this._columnNo = localObject.readUnsignedByte();
/* 127 */       this._tableNo = localObject.readUnsignedByte();
/* 128 */       this._status = localObject.readUnsignedByte();
/* 129 */       if ((this._status & 0x20) == 0)
/*     */         return;
/* 131 */       int i = localObject.readUnsignedByte();
/* 132 */       this._name = localObject.readString(i);
/*     */     }
/*     */ 
/*     */     protected int getLength()
/*     */     {
/* 138 */       int i = 3;
/* 139 */       if ((this._status & 0x20) != 0)
/*     */       {
/* 141 */         i += this._name.length();
/*     */       }
/* 143 */       return i;
/*     */     }
/*     */ 
/*     */     public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */       throws IOException
/*     */     {
/* 153 */       DumpInfo localDumpInfo = paramDumpFilter.getDumpInfo();
/* 154 */       localDumpInfo.addInt("Column Number", 1, this._columnNo);
/* 155 */       localDumpInfo.addInt("Table Number", 1, this._tableNo);
/*     */ 
/* 157 */       String[] arrayOfString = { "<unrecognized>", "<unrecognized>", "TDS_STAT_EXPR", "TDS_STAT_KEY", "TDS_STAT_HIDDEN", "TDS_STAT_RENAME" };
/*     */ 
/* 164 */       localDumpInfo.addBitfield("Status", 1, this._status, arrayOfString);
/*     */ 
/* 166 */       if ((this._status & 0x20) != 0)
/*     */       {
/* 168 */         if (paramDumpFilter.includesDetail(1))
/*     */         {
/* 170 */           localDumpInfo.addInt("Length of Column Name", 1, this._name.length());
/*     */         }
/* 172 */         localDumpInfo.addText("Column Name", this._name.length(), this._name);
/*     */       }
/* 174 */       return localDumpInfo;
/*     */     }
/*     */ 
/*     */     public int getTokenType()
/*     */     {
/* 182 */       return -1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvColInfoToken
 * JD-Core Version:    0.5.4
 */