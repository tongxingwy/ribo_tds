/*     */ package com.sybase.jdbc3.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Vector;
/*     */ import javax.transaction.xa.XAException;
/*     */ import javax.transaction.xa.Xid;
/*     */ 
/*     */ public class SybXAResource11 extends SybXAResource
/*     */ {
/*     */   private static final int MAX_XIDS_TO_RETURN = 10;
/*     */   private static final String ISO_ENCODING = "ISO8859_1";
/*     */   private static final String RPC_POSTFIX = " ( ?, ?, ?, ?, ?, ?)}";
/*     */   private static final String COMMIT_RPC = "{?= call xaos_commit ( ?, ?, ?, ?, ?, ?)}";
/*     */   private static final String ROLLBACK_RPC = "{?= call xaos_rollback ( ?, ?, ?, ?, ?, ?)}";
/*     */   private static final String FORGET_RPC = "{?= call xaos_forget ( ?, ?, ?, ?, ?, ?)}";
/*     */   private static final String PREPARE_RPC = "{?= call xaos_prepare ( ?, ?, ?, ?, ?, ?)}";
/*     */   private static final String RECOVER_RPC = "{?= call xaos_recover ( ?, ?, ?)}";
/*     */   private static final String OPEN_RPC = "{?= call xaos_open ( ?, ?, ?)}";
/*     */   private static final String CLOSE_RPC = "{?= call xaos_close ( ?, ?, ?, ?)}";
/*     */   private static final String START_RPC = "{?= call xaos_start ( ?, ?, ?, ?, ?, ?)}";
/*     */   private static final String END_RPC = "{?= call xaos_end ( ?, ?, ?, ?, ?, ?)}";
/*  75 */   private static int _rmidGenerator = 1;
/*     */ 
/*  82 */   private final int _rmid = _rmidGenerator++;
/*     */ 
/*     */   protected SybXAResource11(String paramString, SybXAConnection paramSybXAConnection, SybXADataSource paramSybXADataSource, SybUrlProvider paramSybUrlProvider)
/*     */   {
/* 100 */     super(paramString, paramSybXAConnection, paramSybXADataSource, paramSybUrlProvider);
/*     */ 
/* 104 */     this._transProtocolType = 65536;
/*     */ 
/* 111 */     SybProperty localSybProperty = paramSybUrlProvider.getSybProperty();
/*     */ 
/* 113 */     SybCallableStatement localSybCallableStatement = null;
/*     */     try
/*     */     {
/* 117 */       localSybCallableStatement = (SybCallableStatement)this._xaConn.prepareInternalCall("{?= call xaos_open ( ?, ?, ?)}");
/*     */ 
/* 120 */       localSybCallableStatement.registerOutParameter(1, 4);
/*     */ 
/* 122 */       localSybCallableStatement.setInt(2, this._rmid);
/* 123 */       localSybCallableStatement.setParameterName(2, "@rmid");
/*     */ 
/* 126 */       localSybCallableStatement.setInt(3, 0);
/* 127 */       localSybCallableStatement.setParameterName(3, "@flags");
/*     */ 
/* 129 */       String str1 = localSybProperty.getString(3);
/* 130 */       String str2 = localSybProperty.getString(4);
/*     */ 
/* 132 */       localSybCallableStatement.setString(4, "-N" + this._resourceManagerID + " -U" + str1 + " -P" + str2);
/* 133 */       localSybCallableStatement.setParameterName(4, "@info");
/*     */ 
/* 135 */       localSybCallableStatement.execute();
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */ 
/* 147 */     this._localTransactionOK = false;
/*     */   }
/*     */ 
/*     */   public void start(Xid paramXid, int paramInt)
/*     */     throws XAException
/*     */   {
/* 161 */     sendRPC("{?= call xaos_start ( ?, ?, ?, ?, ?, ?)}", paramXid, paramInt);
/*     */   }
/*     */ 
/*     */   public void commit(Xid paramXid, boolean paramBoolean)
/*     */     throws XAException
/*     */   {
/* 173 */     int i = (paramBoolean) ? 1073741824 : 0;
/*     */ 
/* 175 */     sendRPC("{?= call xaos_commit ( ?, ?, ?, ?, ?, ?)}", paramXid, i);
/*     */   }
/*     */ 
/*     */   public void end(Xid paramXid, int paramInt)
/*     */     throws XAException
/*     */   {
/* 187 */     sendRPC("{?= call xaos_end ( ?, ?, ?, ?, ?, ?)}", paramXid, paramInt);
/*     */   }
/*     */ 
/*     */   public void forget(Xid paramXid)
/*     */     throws XAException
/*     */   {
/* 199 */     sendRPC("{?= call xaos_forget ( ?, ?, ?, ?, ?, ?)}", paramXid);
/*     */   }
/*     */ 
/*     */   public int prepare(Xid paramXid)
/*     */     throws XAException
/*     */   {
/* 211 */     return sendRPC("{?= call xaos_prepare ( ?, ?, ?, ?, ?, ?)}", paramXid);
/*     */   }
/*     */ 
/*     */   public void rollback(Xid paramXid)
/*     */     throws XAException
/*     */   {
/* 223 */     sendRPC("{?= call xaos_rollback ( ?, ?, ?, ?, ?, ?)}", paramXid);
/*     */   }
/*     */ 
/*     */   public Xid[] recover(int paramInt)
/*     */     throws XAException
/*     */   {
/* 235 */     Vector localVector = new Vector();
/* 236 */     SybCallableStatement localSybCallableStatement = null;
/* 237 */     ResultSet localResultSet = null;
/*     */     try
/*     */     {
/* 241 */       localSybCallableStatement = (SybCallableStatement)this._xaConn.prepareInternalCall("{?= call xaos_recover ( ?, ?, ?)}");
/*     */ 
/* 243 */       localSybCallableStatement.registerOutParameter(1, 4);
/*     */ 
/* 246 */       localSybCallableStatement.setInt(2, 10);
/* 247 */       localSybCallableStatement.setParameterName(2, "@count");
/*     */ 
/* 249 */       localSybCallableStatement.setInt(3, this._rmid);
/* 250 */       localSybCallableStatement.setParameterName(3, "@rmid");
/*     */ 
/* 252 */       localSybCallableStatement.setInt(4, paramInt);
/* 253 */       localSybCallableStatement.setParameterName(4, "@flags");
/*     */ 
/* 255 */       int i = 0;
/*     */ 
/* 259 */       localResultSet = localSybCallableStatement.executeQuery();
/*     */ 
/* 263 */       while (localResultSet.next())
/*     */       {
/* 265 */         localVector.add(createXid(localResultSet.getInt(1), localResultSet.getInt(2), localResultSet.getInt(3), localResultSet.getString(4)));
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SQLException localSQLException1)
/*     */     {
/* 277 */       if (!localSQLException1.getSQLState().equals("JZ0R2"))
/*     */       {
/* 289 */         throw new XAException(-6);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 296 */         if (localResultSet != null)
/*     */         {
/* 298 */           localResultSet.close();
/*     */         }
/* 300 */         if (localSybCallableStatement != null)
/*     */         {
/* 302 */           localSybCallableStatement.close();
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (SQLException localSQLException2)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 314 */     Xid[] arrayOfXid = new Xid[localVector.size()];
/* 315 */     localVector.copyInto(arrayOfXid);
/*     */ 
/* 317 */     return arrayOfXid;
/*     */   }
/*     */ 
/*     */   protected void close()
/*     */   {
/* 335 */     SybCallableStatement localSybCallableStatement = null;
/*     */     try
/*     */     {
/* 339 */       localSybCallableStatement = (SybCallableStatement)this._xaConn.prepareInternalCall("{?= call xaos_close ( ?, ?, ?, ?)}");
/*     */ 
/* 342 */       localSybCallableStatement.registerOutParameter(1, 4);
/*     */ 
/* 344 */       localSybCallableStatement.setInt(2, this._rmid);
/* 345 */       localSybCallableStatement.setParameterName(2, "@rmid");
/*     */ 
/* 348 */       localSybCallableStatement.setInt(3, 0);
/* 349 */       localSybCallableStatement.setParameterName(3, "@flags");
/*     */ 
/* 351 */       localSybCallableStatement.setString(4, null);
/* 352 */       localSybCallableStatement.setParameterName(4, "@open_info");
/*     */ 
/* 354 */       localSybCallableStatement.setString(5, null);
/* 355 */       localSybCallableStatement.setParameterName(5, "@close_info");
/*     */ 
/* 357 */       localSybCallableStatement.execute();
/*     */     }
/*     */     catch (SQLException localSQLException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private final int sendRPC(String paramString, Xid paramXid)
/*     */     throws XAException
/*     */   {
/* 377 */     return sendRPC(paramString, paramXid, 0);
/*     */   }
/*     */ 
/*     */   private final int sendRPC(String paramString, Xid paramXid, int paramInt)
/*     */     throws XAException
/*     */   {
/* 398 */     int i = 0;
/*     */ 
/* 400 */     SybCallableStatement localSybCallableStatement = null;
/* 401 */     Object localObject1 = null;
/*     */     try
/*     */     {
/* 407 */       localSybCallableStatement = (SybCallableStatement)this._xaConn.prepareInternalCall(paramString);
/*     */ 
/* 410 */       localSybCallableStatement.registerOutParameter(1, 4);
/*     */ 
/* 413 */       localSybCallableStatement.setInt(2, paramXid.getFormatId());
/* 414 */       localSybCallableStatement.setParameterName(2, "@xid_format");
/*     */ 
/* 417 */       byte[] arrayOfByte1 = paramXid.getGlobalTransactionId();
/* 418 */       localSybCallableStatement.setInt(3, arrayOfByte1.length);
/* 419 */       localSybCallableStatement.setParameterName(3, "@xid_gtrid");
/*     */ 
/* 422 */       byte[] arrayOfByte2 = paramXid.getBranchQualifier();
/* 423 */       localSybCallableStatement.setInt(4, arrayOfByte2.length);
/* 424 */       localSybCallableStatement.setParameterName(4, "@xid_bqual");
/*     */ 
/* 428 */       byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
/* 429 */       System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
/* 430 */       System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
/*     */ 
/* 436 */       String str = null;
/*     */       try
/*     */       {
/* 439 */         str = new String(arrayOfByte3, "ISO8859_1");
/*     */       }
/*     */       catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */       {
/* 445 */         throw new XAException(-4);
/*     */       }
/*     */ 
/* 448 */       localSybCallableStatement.setString(5, str);
/* 449 */       localSybCallableStatement.setParameterName(5, "@xid_data");
/*     */ 
/* 452 */       localSybCallableStatement.setInt(6, this._rmid);
/* 453 */       localSybCallableStatement.setParameterName(6, "@rmid");
/*     */ 
/* 456 */       localSybCallableStatement.setInt(7, paramInt);
/* 457 */       localSybCallableStatement.setParameterName(7, "@flags");
/*     */ 
/* 460 */       localSybCallableStatement.execute();
/* 461 */       i = localSybCallableStatement.getInt(1);
/*     */     }
/*     */     catch (SQLException localSQLException1)
/*     */     {
/* 471 */       localObject1 = localSQLException1;
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 477 */         if (localSybCallableStatement != null)
/*     */         {
/* 479 */           localSybCallableStatement.close();
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (SQLException localSQLException2)
/*     */       {
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 499 */         if (localObject1 != null)
/*     */         {
/* 504 */           throw new XAException(-6);
/*     */         }
/*     */       }
/*     */ 
/* 508 */       if (localObject1 != null)
/*     */       {
/* 512 */         throw new XAException(-6);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 518 */     verifyReturnCode(i);
/*     */ 
/* 524 */     return i;
/*     */   }
/*     */ 
/*     */   protected void verifyReturnCode(int paramInt)
/*     */     throws XAException
/*     */   {
/* 531 */     if (paramInt == 0)
/*     */       return;
/* 533 */     throw new XAException(paramInt);
/*     */   }
/*     */ 
/*     */   private static final Xid createXid(int paramInt1, int paramInt2, int paramInt3, String paramString)
/*     */     throws XAException
/*     */   {
/* 561 */     byte[] arrayOfByte1 = null;
/* 562 */     byte[] arrayOfByte2 = null;
/*     */     try
/*     */     {
/* 571 */       arrayOfByte1 = paramString.substring(0, paramInt2).getBytes("ISO8859_1");
/* 572 */       arrayOfByte2 = paramString.substring(paramInt2, paramInt2 + paramInt3).getBytes("ISO8859_1");
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */     {
/* 577 */       throw new XAException(-4);
/*     */     }
/*     */ 
/* 590 */     return new SybXid(paramInt1, arrayOfByte1, arrayOfByte2);
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybXAResource11
 * JD-Core Version:    0.5.4
 */