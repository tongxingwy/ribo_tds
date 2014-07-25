/*     */ package com.sybase.jdbc3.tds;
/*     */ 
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class SrvDoneToken extends DoneToken
/*     */   implements Dumpable
/*     */ {
/*     */   public static final int TDS_NOT_IN_TRAN = 0;
/*     */   public static final int TDS_TRAN_SUCCEED = 1;
/*     */   public static final int TDS_TRAN_PROGRESS = 2;
/*     */   public static final int TDS_STMT_ABORT = 3;
/*     */   public static final int TDS_TRAN_ABORT = 4;
/*  39 */   protected static final String[] TRANSTATE_NAMES = { "TDS_NOT_IN_TRAN", "TDS_TRAN_SUCCEED", "TDS_TRAN_PROGRESS", "TDS_STMT_ABORT", "TDS_TRAN_ABORT" };
/*     */ 
/*     */   public SrvDoneToken(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*  56 */     super(paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */ 
/*     */   public SrvDoneToken(TdsInputStream paramTdsInputStream)
/*     */     throws IOException
/*     */   {
/*  63 */     super(paramTdsInputStream);
/*     */   }
/*     */ 
/*     */   public boolean getFinal()
/*     */   {
/*  73 */     return (0x1 & this._status) <= 0;
/*     */   }
/*     */ 
/*     */   public void send(TdsOutputStream paramTdsOutputStream)
/*     */     throws IOException
/*     */   {
/*  83 */     send(paramTdsOutputStream, 253);
/*     */   }
/*     */ 
/*     */   protected void send(TdsOutputStream paramTdsOutputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/*  94 */     paramTdsOutputStream.writeByte(paramInt);
/*  95 */     paramTdsOutputStream.writeShort(this._status);
/*  96 */     paramTdsOutputStream.writeShort(this._tranState);
/*  97 */     paramTdsOutputStream.writeInt(this._count);
/*     */   }
/*     */ 
/*     */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*     */     throws IOException
/*     */   {
/* 111 */     DumpInfo localDumpInfo = null;
/* 112 */     if (paramDumpFilter.includesToken(getTokenType()))
/*     */     {
/* 114 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 115 */       String str = null;
/* 116 */       switch (getTokenType())
/*     */       {
/*     */       case 253:
/* 119 */         str = "DONE";
/* 120 */         break;
/*     */       case 255:
/* 122 */         str = "DONEINPROC";
/* 123 */         break;
/*     */       case 254:
/* 125 */         str = "DONEPROC";
/*     */       }
/*     */ 
/* 128 */       if (paramDumpFilter.includesDetail(0))
/*     */       {
/* 130 */         localDumpInfo.addInfo("Token", 1, str + " Token (0x" + HexConverts.hexConvert(getTokenType(), 1) + "); fixed length.");
/*     */       }
/*     */       else
/*     */       {
/* 136 */         localDumpInfo.addInfo("Token", 1, str + " Token");
/*     */       }
/*     */ 
/* 139 */       if (paramDumpFilter.includesDetail(1))
/*     */       {
/* 141 */         localDumpInfo.addInt("Length", 0, 8L);
/*     */       }
/*     */ 
/* 148 */       if (paramDumpFilter.includesDetail(3))
/*     */       {
/* 150 */         String[] arrayOfString = { "DONE_FINAL", "DONE_MORE", "DONE_ERROR", "DONE_INXACT", "DONE_PROC", "DONE_COUNT", "DONE_ATTN", "DONE_EVENT" };
/*     */ 
/* 156 */         localDumpInfo.addBitfield("Status", 2, this._status, arrayOfString);
/* 157 */         localDumpInfo.addField("TranState", 2, this._tranState, TRANSTATE_NAMES);
/*     */ 
/* 159 */         if ((this._status & 0x10) != 0)
/*     */         {
/* 161 */           localDumpInfo.addInt("Count", 4, this._count);
/*     */         }
/*     */         else
/*     */         {
/* 165 */           localDumpInfo.addInt("Count (unused)", 4, this._count);
/*     */         }
/*     */       }
/*     */     }
/* 169 */     return localDumpInfo;
/*     */   }
/*     */ 
/*     */   public int getTokenType()
/*     */   {
/* 178 */     return 253;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvDoneToken
 * JD-Core Version:    0.5.4
 */