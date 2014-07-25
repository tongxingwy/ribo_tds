/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.utils.DumpFilter;
/*    */ import com.sybase.jdbc3.utils.DumpInfo;
/*    */ import com.sybase.jdbc3.utils.HexConverts;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class SrvOrderBy2Token extends SrvOrderByToken
/*    */ {
/*    */   public SrvOrderBy2Token(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/* 31 */     super(paramTdsInputStream);
/*    */   }
/*    */ 
/*    */   protected long readLength(TdsInputStream paramTdsInputStream) throws IOException
/*    */   {
/* 36 */     long l = paramTdsInputStream.readUnsignedIntAsLong();
/* 37 */     return l;
/*    */   }
/*    */ 
/*    */   protected int readColumnNum(TdsInputStream paramTdsInputStream) throws IOException
/*    */   {
/* 42 */     return paramTdsInputStream.readUnsignedShort();
/*    */   }
/*    */ 
/*    */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*    */     throws IOException
/*    */   {
/* 58 */     DumpInfo localDumpInfo = null;
/* 59 */     if (paramDumpFilter.includesToken(169))
/*    */     {
/* 61 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 62 */       if (paramDumpFilter.includesDetail(0))
/*    */       {
/* 64 */         localDumpInfo.addInfo("Token", 1, "ORDERBY2 Token (0x" + HexConverts.hexConvert(34, 1) + "); variable length.");
/*    */       }
/*    */       else
/*    */       {
/* 71 */         localDumpInfo.addInfo("Token", 1, "ORDERBY2 Token");
/*    */       }
/*    */ 
/* 74 */       if (paramDumpFilter.includesDetail(1))
/*    */       {
/* 76 */         localDumpInfo.addInt("Length", 4, (int)this._length);
/*    */       }
/*    */ 
/* 79 */       if (paramDumpFilter.includesDetail(3))
/*    */       {
/* 81 */         localDumpInfo.addInt("No. Columns", 2, this._noColumns);
/* 82 */         for (int i = 0; i < this._noColumns; ++i)
/*    */         {
/* 84 */           localDumpInfo.addInt("Column", 1, this._columnNos[i]);
/*    */         }
/*    */       }
/*    */     }
/* 88 */     return localDumpInfo;
/*    */   }
/*    */ 
/*    */   public int getTokenType()
/*    */   {
/* 96 */     return 34;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvOrderBy2Token
 * JD-Core Version:    0.5.4
 */