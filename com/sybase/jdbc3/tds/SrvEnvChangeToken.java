/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SrvEnvChangeToken extends EnvChangeToken
/*     */   implements Dumpable
/*     */ {
/*     */   private int _totalLength;
/*     */   private Vector _variables;
/*     */ 
/*     */   public SrvEnvChangeToken()
/*     */   {
/*  45 */     this._totalLength = 0;
/*  46 */     this._variables = new Vector();
/*     */   }
/*     */ 
/*     */   public SrvEnvChangeToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  59 */     this._variables = new Vector();
/*  60 */     this._totalLength = paramTdsInputStream.readShort();
/*     */ 
/*  62 */     int i = this._totalLength;
/*  63 */     while (i > 0)
/*     */     {
/*  65 */       VariableInfo localVariableInfo = new VariableInfo(paramTdsInputStream);
/*  66 */       this._variables.addElement(localVariableInfo);
/*  67 */       i -= localVariableInfo.getLength();
/*     */ 
/*  70 */       switch (localVariableInfo.getType())
/*     */       {
/*     */       case 3:
/*     */       case 4:
/*  74 */         this._newValue = localVariableInfo.getNewVal();
/*     */       }
/*     */     }
/*     */ 
/*  78 */     if (i >= 0)
/*     */       return;
/*  80 */     throw new IOException("Malformed EnvChange token lengths");
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*  92 */     paramTdsOutputStream.writeByte(227);
/*  93 */     paramTdsOutputStream.writeShort(this._totalLength);
/*     */ 
/*  95 */     Enumeration localEnumeration = this._variables.elements();
/*  96 */     while (localEnumeration.hasMoreElements()) {
/*  97 */       VariableInfo localVariableInfo = (VariableInfo)localEnumeration.nextElement();
/*  98 */       localVariableInfo.send(paramTdsOutputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addVariable(int paramInt, String paramString1, String paramString2)
/*     */   {
/* 111 */     VariableInfo localVariableInfo = new VariableInfo(paramInt, paramString1, paramString2);
/* 112 */     this._variables.addElement(localVariableInfo);
/* 113 */     this._totalLength += localVariableInfo.getLength();
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 128 */     DumpInfo localDumpInfo = null;
/* 129 */     if (paramDumpFilter.includesToken(227)) {
/* 130 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 131 */       if (paramDumpFilter.includesDetail(0)) {
/* 132 */         localDumpInfo.addInfo("Token", 1, "ENVCHANGE Token (0x" + HexConverts.hexConvert(227, 1) + "); variable length.");
/*     */       }
/*     */       else
/*     */       {
/* 136 */         localDumpInfo.addInfo("Token", 1, "ENVCHANGE Token");
/*     */       }
/*     */ 
/* 139 */       if (paramDumpFilter.includesDetail(1)) {
/* 140 */         localDumpInfo.addInt("Length", 2, this._totalLength);
/*     */       }
/*     */ 
/* 143 */       if (paramDumpFilter.includesDetail(3)) {
/* 144 */         Enumeration localEnumeration = this._variables.elements();
/* 145 */         while (localEnumeration.hasMoreElements()) {
/* 146 */           VariableInfo localVariableInfo = (VariableInfo)localEnumeration.nextElement();
/* 147 */           localDumpInfo.addInfo(localVariableInfo.dump(paramDumpFilter));
/*     */         }
/*     */       }
/*     */     }
/* 151 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 159 */     return 227;
/*     */   }
/*     */ 
/*     */   public String getCharset()
/*     */   {
/* 165 */     return (this._envType == 3) ? this._newValue : null;
/*     */   }
/*     */ 
/*     */   public String getPacketSize()
/*     */   {
/* 171 */     return (this._envType == 4) ? this._newValue : null;
/*     */   }
/*     */ 
/*     */   private class VariableInfo
/*     */     implements Dumpable
/*     */   {
/*     */     private int _type;
/*     */     private int _newValLen;
/*     */     private String _newVal;
/*     */     private int _oldValLen;
/*     */     private String _oldVal;
/*     */ 
/*     */     protected VariableInfo(int paramString1, String paramString2, String arg4)
/*     */     {
/* 195 */       this._type = paramString1;
/* 196 */       this._oldVal = paramString2;
/*     */       Object localObject;
/* 197 */       this._newVal = localObject;
/*     */ 
/* 199 */       this._oldValLen = this._oldVal.length();
/* 200 */       this._newValLen = this._newVal.length();
/*     */     }
/*     */ 
/*     */     public void send(TdsOutputStream paramTdsOutputStream)
/*     */       throws IOException
/*     */     {
/* 212 */       paramTdsOutputStream.writeByte(this._type);
/* 213 */       byte[] arrayOfByte = paramTdsOutputStream.stringToByte(this._newVal);
/* 214 */       paramTdsOutputStream.writeByte(this._newValLen);
/* 215 */       paramTdsOutputStream.write(arrayOfByte);
/*     */ 
/* 217 */       arrayOfByte = paramTdsOutputStream.stringToByte(this._oldVal);
/* 218 */       paramTdsOutputStream.writeByte(this._oldValLen);
/* 219 */       paramTdsOutputStream.write(arrayOfByte);
/*     */     }
/*     */ 
/*     */     protected VariableInfo(TdsInputStream arg2)
/*     */       throws IOException
/*     */     {
/*     */       Object localObject;
/* 228 */       this._type = localObject.readUnsignedByte();
/* 229 */       this._newValLen = localObject.readUnsignedByte();
/* 230 */       if (this._newValLen != 0) {
/* 231 */         this._newVal = localObject.readString(this._newValLen);
/*     */       }
/* 233 */       this._oldValLen = localObject.readUnsignedByte();
/* 234 */       if (this._oldValLen != 0)
/* 235 */         this._oldVal = localObject.readString(this._oldValLen);
/*     */     }
/*     */ 
/*     */     protected int getLength()
/*     */     {
/* 242 */       return 2 + this._newValLen + 1 + this._oldValLen;
/*     */     }
/*     */ 
/*     */     public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */       throws IOException
/*     */     {
/* 251 */       DumpInfo localDumpInfo = paramDumpFilter.getDumpInfo();
/* 252 */       String[] arrayOfString = { "<unrecognized>", "ENV_DB", "ENV_LANG", "ENV_CHARSET", "ENV_PACKETSIZE" };
/*     */ 
/* 256 */       localDumpInfo.addField("Type", 1, this._type, arrayOfString);
/* 257 */       if (paramDumpFilter.includesDetail(1)) {
/* 258 */         localDumpInfo.addInt("Length of New Value", 1, this._newValLen);
/*     */       }
/* 260 */       if (this._newValLen > 0) {
/* 261 */         localDumpInfo.addText("New Value", this._newValLen, this._newVal);
/*     */       }
/* 263 */       if (paramDumpFilter.includesDetail(1)) {
/* 264 */         localDumpInfo.addInt("Length of Old Value", 1, this._oldValLen);
/*     */       }
/* 266 */       if (this._oldValLen > 0) {
/* 267 */         localDumpInfo.addText("Old Value", this._oldValLen, this._oldVal);
/*     */       }
/* 269 */       return localDumpInfo;
/*     */     }
/*     */ 
/*     */     public int getTokenType()
/*     */     {
/* 276 */       return -1;
/*     */     }
/*     */ 
/*     */     protected int getType()
/*     */     {
/* 281 */       return this._type;
/*     */     }
/*     */ 
/*     */     protected String getNewVal()
/*     */     {
/* 286 */       return this._newVal;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvEnvChangeToken
 * JD-Core Version:    0.5.4
 */