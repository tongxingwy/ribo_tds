/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvAltNameToken extends Token
/*     */   implements Dumpable
/*     */ {
/*     */   protected int _totalLen;
/*     */   protected int _id;
/*     */   protected Vector _names;
/*     */ 
/*     */   public SrvAltNameToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  44 */     this._totalLen = paramTdsInputStream.readShort();
/*  45 */     this._id = paramTdsInputStream.readShort();
/*     */ 
/*  47 */     this._names = new Vector();
/*  48 */     int i = this._totalLen - 2;
/*  49 */     while (i > 0)
/*     */     {
/*  51 */       NameInfo localNameInfo = new NameInfo(paramTdsInputStream);
/*  52 */       this._names.addElement(localNameInfo);
/*  53 */       i -= localNameInfo.getLength();
/*     */     }
/*  55 */     if (i >= 0)
/*     */       return;
/*  57 */     throw new IOException("Malformed AltName token lengths");
/*     */   }
/*     */ 
/*     */   public int getId()
/*     */   {
/*  66 */     return this._id;
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/*  80 */     DumpInfo localDumpInfo = null;
/*  81 */     if (paramDumpFilter.includesToken(167))
/*     */     {
/*  83 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/*  84 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/*  86 */         localDumpInfo.addInfo("Token", 1, "ALTNAME Token (0x" + HexConverts.hexConvert(167, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/*  92 */         localDumpInfo.addInfo("Token", 1, "ALTNAME Token");
/*     */       }
/*     */ 
/*  95 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/*  97 */         localDumpInfo.addInt("Length", 2, this._totalLen);
/*     */       }
/*     */ 
/* 100 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 102 */         localDumpInfo.addInt("ID", 2, this._id);
/*     */ 
/* 104 */         Enumeration localEnumeration = this._names.elements();
/* 105 */         while (localEnumeration.hasMoreElements())
/*     */         {
/* 107 */           NameInfo localNameInfo = (NameInfo)localEnumeration.nextElement();
/* 108 */           localDumpInfo.addInfo(localNameInfo.dump(paramDumpFilter));
/*     */         }
/*     */       }
/*     */     }
/* 112 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 119 */     return 167;
/*     */   }
/*     */ 
/*     */   private class NameInfo
/*     */     implements Dumpable
/*     */   {
/*     */     private int _length;
/*     */     private String _name;
/*     */ 
/*     */     protected NameInfo(TdsInputStream arg2)
/*     */       throws IOException
/*     */     {
/*     */       Object localObject;
/* 138 */       this._length = localObject.readUnsignedByte();
/* 139 */       if (this._length == 0)
/*     */         return;
/* 141 */       this._name = localObject.readString(this._length);
/*     */     }
/*     */ 
/*     */     protected int getLength()
/*     */     {
/* 148 */       return this._length + 1;
/*     */     }
/*     */ 
/*     */     public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */       throws IOException
/*     */     {
/* 157 */       DumpInfo localDumpInfo = paramDumpFilter.getDumpInfo();
/* 158 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 160 */         localDumpInfo.addInt("Name Length", 1, this._length);
/*     */       }
/* 162 */       if (this._length != 0)
/*     */       {
/* 164 */         localDumpInfo.addText("Name", this._length, this._name);
/*     */       }
/* 166 */       return localDumpInfo;
/*     */     }
/*     */ 
/*     */     public int getTokenType()
/*     */     {
/* 173 */       return -1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvAltNameToken
 * JD-Core Version:    0.5.4
 */