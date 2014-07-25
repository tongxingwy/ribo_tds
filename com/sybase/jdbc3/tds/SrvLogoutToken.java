/*    */ package com.sybase.jdbc3.tds;
/*    */ 
/*    */ import com.sybase.jdbc3.utils.DumpFilter;
/*    */ import com.sybase.jdbc3.utils.DumpInfo;
/*    */ import com.sybase.jdbc3.utils.HexConverts;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class SrvLogoutToken extends LogoutToken
/*    */   implements Dumpable
/*    */ {
/*    */   private int _options;
/*    */ 
/*    */   public SrvLogoutToken()
/*    */   {
/* 39 */     this._options = 0;
/*    */   }
/*    */ 
/*    */   public SrvLogoutToken(TdsInputStream paramTdsInputStream)
/*    */     throws IOException
/*    */   {
/* 49 */     this._options = paramTdsInputStream.readByte();
/*    */   }
/*    */ 
/*    */   public DumpInfo dump(DumpFilter paramDumpFilter)
/*    */     throws IOException
/*    */   {
/* 63 */     DumpInfo localDumpInfo = null;
/* 64 */     if (paramDumpFilter.includesToken(113))
/*    */     {
/* 66 */       localDumpInfo = paramDumpFilter.getDumpInfo();
/* 67 */       if (paramDumpFilter.includesDetail(0))
/*    */       {
/* 69 */         localDumpInfo.addInfo("Token", 1, "LOGOUT Token (0x" + HexConverts.hexConvert(113, 1) + "); fixed length.");
/*    */       }
/*    */       else
/*    */       {
/* 75 */         localDumpInfo.addInfo("Token", 1, "LOGOUT Token");
/*    */       }
/*    */ 
/* 78 */       if (paramDumpFilter.includesDetail(3))
/*    */       {
/* 80 */         localDumpInfo.addHex("Options", 1, this._options);
/*    */       }
/*    */     }
/* 83 */     return localDumpInfo;
/*    */   }
/*    */ 
/*    */   public int getTokenType()
/*    */   {
/* 91 */     return 113;
/*    */   }
/*    */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvLogoutToken
 * JD-Core Version:    0.5.4
 */