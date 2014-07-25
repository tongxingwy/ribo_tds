/*      */ package com.sybase.jdbc3.jdbc;
/*      */ 
/*      */ import com.sybase.jdbc3.utils.HexConverts;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Vector;
/*      */ import javax.transaction.xa.XAException;
/*      */ import javax.transaction.xa.XAResource;
/*      */ import javax.transaction.xa.Xid;
/*      */ 
/*      */ public class SybXAResource
/*      */   implements XAResource
/*      */ {
/*      */   private static final int MAX_SAFE_RM_NAME_LENGTH = 74;
/*      */   protected static final String RPC_PREFIX = "{?= call ";
/*      */   private static final String RPC_POSTFIX = " (?, ?, ?)}";
/*      */   private static final String COMMIT_RPC = "{?= call $commitSybDtmXact (?, ?, ?)}";
/*      */   private static final String ROLLBACK_RPC = "{?= call $rollbackSybDtmXact (?, ?, ?)}";
/*      */   private static final String FORGET_RPC = "{?= call $forgetSybDtmXact (?, ?, ?)}";
/*      */   private static final String PREPARE_RPC = "{?= call $prepareSybDtmXact (?, ?, ?)}";
/*      */   private static final String BEGIN_RPC = "{?= call $beginSybDtmXact (?, ?, ?, ?)}";
/*      */   private static final String END_RPC = "{?= call $endSybDtmXact (?, ?, ?)}";
/*      */   private static final String STATUS_RPC = "{?= call $statusSybDtmXact (?, ?, ?)}";
/*      */   private static final String ATTACH_RPC = "{?= call $attachSybDtmXact (?, ?, ?)}";
/*      */   private static final String DETACH_RPC = "{?= call $detachSybDtmXact (?, ?, ?)}";
/*      */   private static final String TRANSACTION_STATUS = "{?= call sp_transactions (?)}";
/*      */   private static final String ENCODING_MAP = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#";
/*      */   private static final String ENCODED_XID_DELIMITER = "_";
/*      */   private static final String ENCODED_RM_NAME_DELIMITER = ":";
/*  143 */   protected int _transProtocolType = 196608;
/*      */ 
/*  146 */   private int _timeout = 0;
/*      */   private final SybXADataSource _xaDataSource;
/*      */   protected final SybXAConnection _xaConn;
/*      */   protected final String _resourceManagerID;
/*  185 */   protected boolean _localTransactionOK = true;
/*      */ 
/*      */   protected SybXAResource(String paramString, SybXAConnection paramSybXAConnection, SybXADataSource paramSybXADataSource, SybUrlProvider paramSybUrlProvider)
/*      */   {
/*  192 */     this._xaConn = paramSybXAConnection;
/*  193 */     this._xaDataSource = paramSybXADataSource;
/*  194 */     this._resourceManagerID = paramString;
/*      */   }
/*      */ 
/*      */   public static SybXAResource createSybXAResource(int paramInt, String paramString, SybXAConnection paramSybXAConnection, SybXADataSource paramSybXADataSource, SybUrlProvider paramSybUrlProvider)
/*      */   {
/*  248 */     Object localObject = null;
/*      */ 
/*  253 */     switch (paramInt)
/*      */     {
/*      */     case 2:
/*  256 */       localObject = new SybXAResource(paramString, paramSybXAConnection, paramSybXADataSource, paramSybUrlProvider);
/*  257 */       break;
/*      */     case 1:
/*  259 */       localObject = new SybXAResource11(paramString, paramSybXAConnection, paramSybXADataSource, paramSybUrlProvider);
/*      */     }
/*      */ 
/*  266 */     return (SybXAResource)localObject;
/*      */   }
/*      */ 
/*      */   public void commit(Xid paramXid, boolean paramBoolean)
/*      */     throws XAException
/*      */   {
/*  283 */     int i = 0;
/*      */ 
/*  285 */     if (paramBoolean)
/*      */     {
/*  292 */       sendRPC("{?= call $attachSybDtmXact (?, ?, ?)}", paramXid, 1, 2);
/*      */ 
/*  295 */       sendRPC("{?= call $endSybDtmXact (?, ?, ?)}", paramXid, 2);
/*      */ 
/*  297 */       i = 32;
/*      */     }
/*      */ 
/*  301 */     sendRPC("{?= call $commitSybDtmXact (?, ?, ?)}", paramXid, 100, i);
/*  302 */     this._localTransactionOK = true;
/*      */   }
/*      */ 
/*      */   public void start(Xid paramXid, int paramInt)
/*      */     throws XAException
/*      */   {
/*  313 */     if (isLocalTransactionOK())
/*      */     {
/*      */       try
/*      */       {
/*  317 */         this._xaConn.endLocalTransaction();
/*  318 */         if (this._localTransactionOK)
/*      */         {
/*  320 */           ErrorMessage.raiseWarning("01S08");
/*      */         }
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/*  325 */         this._xaConn.chainWarnings(localSQLException);
/*      */       }
/*  327 */       this._localTransactionOK = false;
/*      */     }
/*      */ 
/*  331 */     if ((paramInt & 0x8200000) == 0)
/*      */     {
/*  338 */       sendRPC("{?= call $beginSybDtmXact (?, ?, ?, ?)}", paramXid, 1, 16);
/*      */     }
/*      */     else
/*      */     {
/*  344 */       int i = sendRPC("{?= call $attachSybDtmXact (?, ?, ?)}", paramXid, 1);
/*      */ 
/*  349 */       if (((((paramInt & 0x8000000) == 0) || (i == 3))) && ((((paramInt & 0x200000) == 0) || (i != 101))))
/*      */       {
/*      */         return;
/*      */       }
/*      */ 
/*  363 */       sendRPC("{?= call $detachSybDtmXact (?, ?, ?)}", paramXid, i);
/*  364 */       throw new XAException(-6);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void end(Xid paramXid, int paramInt)
/*      */     throws XAException
/*      */   {
/*  379 */     int i = 2;
/*      */ 
/*  382 */     if ((paramInt & 0x2000000) != 0)
/*      */     {
/*  385 */       i = 3;
/*      */     }
/*  388 */     else if ((paramInt & 0x20000000) != 0)
/*      */     {
/*  392 */       i = 101;
/*      */     }
/*      */ 
/*  395 */     sendRPC("{?= call $detachSybDtmXact (?, ?, ?)}", paramXid, i);
/*  396 */     this._localTransactionOK = true;
/*      */   }
/*      */ 
/*      */   public void forget(Xid paramXid)
/*      */     throws XAException
/*      */   {
/*  409 */     sendRPC("{?= call $forgetSybDtmXact (?, ?, ?)}", paramXid, 102);
/*  410 */     this._localTransactionOK = true;
/*      */   }
/*      */ 
/*      */   public int prepare(Xid paramXid)
/*      */     throws XAException
/*      */   {
/*  421 */     this._localTransactionOK = false;
/*      */ 
/*  427 */     sendRPC("{?= call $attachSybDtmXact (?, ?, ?)}", paramXid, 1, 2);
/*      */ 
/*  430 */     sendRPC("{?= call $detachSybDtmXact (?, ?, ?)}", paramXid, 2, 4);
/*      */ 
/*  433 */     int i = sendRPC("{?= call $prepareSybDtmXact (?, ?, ?)}", paramXid, 7);
/*      */ 
/*  435 */     if (i == -256)
/*      */     {
/*  437 */       this._localTransactionOK = true;
/*      */     }
/*      */ 
/*  442 */     return (i == -256) ? 3 : 0;
/*      */   }
/*      */ 
/*      */   public Xid[] recover(int paramInt)
/*      */     throws XAException
/*      */   {
/*  476 */     Vector localVector = new Vector();
/*  477 */     CallableStatement localCallableStatement = null;
/*  478 */     ResultSet localResultSet = null;
/*      */ 
/*  481 */     if (((paramInt & 0x1800000) == 0) && (paramInt != 0))
/*      */     {
/*  489 */       throw new XAException(-5);
/*      */     }
/*      */ 
/*  492 */     int i = 0;
/*  493 */     if ((paramInt & 0x1000000) != 0)
/*      */     {
/*  498 */       i = 1;
/*      */     }
/*      */ 
/*  507 */     if (i != 0)
/*      */     {
/*      */       try
/*      */       {
/*  511 */         localCallableStatement = this._xaConn.prepareInternalCall("{?= call sp_transactions (?)}");
/*      */ 
/*  513 */         localCallableStatement.setString(2, "xa_recover");
/*      */ 
/*  515 */         localResultSet = localCallableStatement.executeQuery();
/*      */ 
/*  519 */         while (localResultSet.next())
/*      */         {
/*  521 */           String str = localResultSet.getString("xactname");
/*      */ 
/*  524 */           localVector.add(dtmDecode(str));
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException localSQLException1)
/*      */       {
/*  536 */         throw new XAException(-3);
/*      */       }
/*      */       finally
/*      */       {
/*      */         try
/*      */         {
/*  542 */           if (localResultSet != null)
/*      */           {
/*  544 */             localResultSet.close();
/*      */           }
/*  546 */           if (localCallableStatement != null)
/*      */           {
/*  548 */             localCallableStatement.close();
/*      */           }
/*      */         }
/*      */         catch (SQLException localSQLException2)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  559 */     Xid[] arrayOfXid = new Xid[localVector.size()];
/*  560 */     localVector.copyInto(arrayOfXid);
/*      */ 
/*  562 */     return arrayOfXid;
/*      */   }
/*      */ 
/*      */   public void rollback(Xid paramXid)
/*      */     throws XAException
/*      */   {
/*  574 */     sendRPC("{?= call $rollbackSybDtmXact (?, ?, ?)}", paramXid, 101);
/*  575 */     this._localTransactionOK = true;
/*      */   }
/*      */ 
/*      */   public boolean isSameRM(XAResource paramXAResource)
/*      */     throws XAException
/*      */   {
/*  592 */     boolean bool = false;
/*      */ 
/*  596 */     if (paramXAResource instanceof SybXAResource)
/*      */     {
/*  598 */       bool = this._resourceManagerID.equals(((SybXAResource)paramXAResource)._resourceManagerID);
/*      */     }
/*      */ 
/*  601 */     return bool;
/*      */   }
/*      */ 
/*      */   public int getTransactionTimeout()
/*      */     throws XAException
/*      */   {
/*  613 */     return this._timeout;
/*      */   }
/*      */ 
/*      */   public boolean setTransactionTimeout(int paramInt)
/*      */     throws XAException
/*      */   {
/*  624 */     if (paramInt < 0)
/*      */     {
/*  626 */       throw new XAException(-5);
/*      */     }
/*  628 */     this._timeout = paramInt;
/*      */ 
/*  631 */     return false;
/*      */   }
/*      */ 
/*      */   protected void close()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected boolean isLocalTransactionOK()
/*      */   {
/*  655 */     return this._localTransactionOK;
/*      */   }
/*      */ 
/*      */   private int sendRPC(String paramString, Xid paramXid, int paramInt)
/*      */     throws XAException
/*      */   {
/*  666 */     return sendRPC(paramString, paramXid, this._transProtocolType, paramInt, 0);
/*      */   }
/*      */ 
/*      */   private final int sendRPC(String paramString, Xid paramXid, int paramInt1, int paramInt2)
/*      */     throws XAException
/*      */   {
/*  678 */     return sendRPC(paramString, paramXid, this._transProtocolType, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   private final int sendRPC(String paramString, Xid paramXid, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws XAException
/*      */   {
/*  713 */     int i = 0;
/*      */ 
/*  715 */     SybCallableStatement localSybCallableStatement = null;
/*  716 */     Object localObject1 = null;
/*      */     try
/*      */     {
/*  724 */       localSybCallableStatement = (SybCallableStatement)this._xaConn.prepareInternalCall(paramString);
/*      */ 
/*  727 */       localSybCallableStatement.registerOutParameter(1, 4);
/*      */ 
/*  731 */       localSybCallableStatement.setString(2, dtmEncode(paramXid));
/*  732 */       localSybCallableStatement.setParameterName(2, "@name");
/*      */ 
/*  737 */       localSybCallableStatement.setInt(3, paramInt2);
/*  738 */       localSybCallableStatement.setParameterName(3, "@status");
/*      */ 
/*  741 */       localSybCallableStatement.setInt(4, paramInt3);
/*  742 */       localSybCallableStatement.setParameterName(4, "@flags");
/*      */ 
/*  747 */       if (paramString == "{?= call $beginSybDtmXact (?, ?, ?, ?)}")
/*      */       {
/*  749 */         localSybCallableStatement.setInt(5, paramInt1);
/*  750 */         localSybCallableStatement.setParameterName(5, "@mode");
/*      */       }
/*      */ 
/*  754 */       localSybCallableStatement.execute();
/*  755 */       i = localSybCallableStatement.getInt(1);
/*      */     }
/*      */     catch (SQLException localSQLException1)
/*      */     {
/*  765 */       localObject1 = localSQLException1;
/*      */     }
/*      */     finally
/*      */     {
/*      */       try
/*      */       {
/*  771 */         if (localSybCallableStatement != null);
/*  787 */         throw new XAException(-3);
/*      */       }
/*      */       catch (SQLException localSQLException2)
/*      */       {
/*      */       }
/*      */       finally
/*      */       {
/*  783 */         if (localObject1 != null)
/*      */         {
/*  787 */           throw new XAException(-3);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  794 */     verifyReturnCode(i, paramInt2);
/*      */ 
/*  798 */     return i;
/*      */   }
/*      */ 
/*      */   private final void verifyReturnCode(int paramInt1, int paramInt2)
/*      */     throws XAException
/*      */   {
/*  820 */     if (paramInt1 >= 0)
/*      */       return;
/*  822 */     int i = 0;
/*  823 */     String str = null;
/*      */ 
/*  827 */     int j = 1;
/*      */ 
/*  829 */     switch (paramInt1)
/*      */     {
/*      */     case -1:
/*  833 */       i = -3; break;
/*      */     case -2:
/*  837 */       i = -4; break;
/*      */     case -3:
/*  841 */       i = -8; break;
/*      */     case -4:
/*  845 */       i = -7; break;
/*      */     case -5:
/*  849 */       i = -5; break;
/*      */     case -6:
/*  854 */       i = -6; break;
/*      */     case -128:
/*  862 */       if (paramInt2 == 101)
/*      */       {
/*  864 */         i = -4;
/*      */       }
/*      */       else
/*      */       {
/*  868 */         i = -6; } break;
/*      */     case -256:
/*  875 */       j = 0; break;
/*      */     default:
/*  879 */       str = "Unrecognized return code from server: " + paramInt1;
/*      */     }
/*      */ 
/*  884 */     if (j == 0)
/*      */       return;
/*  886 */     if (str != null)
/*      */     {
/*  888 */       throw new XAException(str);
/*      */     }
/*      */ 
/*  892 */     throw new XAException(i);
/*      */   }
/*      */ 
/*      */   protected final String dtmEncode(Xid paramXid)
/*      */     throws XAException
/*      */   {
/*  918 */     byte[] arrayOfByte1 = paramXid.getGlobalTransactionId();
/*  919 */     byte[] arrayOfByte2 = paramXid.getBranchQualifier();
/*      */ 
/*  921 */     if ((arrayOfByte1.length > 64) || (arrayOfByte2.length > 64))
/*      */     {
/*  925 */       throw new XAException(-4);
/*      */     }
/*      */ 
/*  928 */     String str = "";
/*      */ 
/*  931 */     str = str + HexConverts.hexConvert(paramXid.getFormatId(), 4) + "_";
/*      */ 
/*  935 */     str = str + jjEncode(arrayOfByte1) + "_";
/*      */ 
/*  938 */     str = str + jjEncode(arrayOfByte2);
/*      */ 
/*  946 */     return str;
/*      */   }
/*      */ 
/*      */   protected final SybXid dtmDecode(String paramString)
/*      */   {
/*  966 */     int i = paramString.indexOf("_");
/*  967 */     int j = paramString.indexOf("_", i + 1);
/*  968 */     int k = paramString.indexOf(":", j);
/*      */ 
/*  970 */     SybXid localSybXid = new SybXid(Integer.parseInt(paramString.substring(0, i), 16), jjDecode(paramString.substring(i + 1, j)), jjDecode(paramString.substring(j + 1, paramString.length())));
/*      */ 
/*  974 */     return localSybXid;
/*      */   }
/*      */ 
/*      */   private static final String jjEncode(byte[] paramArrayOfByte)
/*      */   {
/* 1011 */     int i = paramArrayOfByte.length * 4 / 3 + ((paramArrayOfByte.length % 3 == 0) ? 0 : 1);
/*      */ 
/* 1014 */     char[] arrayOfChar = new char[i];
/*      */ 
/* 1027 */     int j = 0;
/*      */     int l;
/* 1027 */     for (int k = 0; j < paramArrayOfByte.length - 2; j += 3)
/*      */     {
/* 1029 */       l = paramArrayOfByte[j] >> 2 & 0x3F;
/* 1030 */       arrayOfChar[(k++)] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".charAt(l);
/*      */ 
/* 1032 */       l = (paramArrayOfByte[j] << 4 | paramArrayOfByte[(j + 1)] >> 4 & 0xF) & 0x3F;
/* 1033 */       arrayOfChar[(k++)] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".charAt(l);
/*      */ 
/* 1035 */       l = (paramArrayOfByte[(j + 1)] << 2 | paramArrayOfByte[(j + 2)] >> 6 & 0x3) & 0x3F;
/* 1036 */       arrayOfChar[(k++)] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".charAt(l);
/*      */ 
/* 1038 */       l = paramArrayOfByte[(j + 2)] & 0x3F;
/* 1039 */       arrayOfChar[(k++)] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".charAt(l);
/*      */     }
/*      */ 
/* 1049 */     switch (paramArrayOfByte.length - j)
/*      */     {
/*      */     case 2:
/* 1052 */       l = paramArrayOfByte[j] >> 2 & 0x3F;
/* 1053 */       arrayOfChar[(k++)] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".charAt(l);
/* 1054 */       l = (paramArrayOfByte[j] << 4 | paramArrayOfByte[(j + 1)] >> 4 & 0xF) & 0x3F;
/* 1055 */       arrayOfChar[(k++)] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".charAt(l);
/* 1056 */       l = paramArrayOfByte[(j + 1)] << 2 & 0x3F;
/* 1057 */       arrayOfChar[k] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".charAt(l);
/* 1058 */       break;
/*      */     case 1:
/* 1061 */       l = paramArrayOfByte[j] >> 2 & 0x3F;
/* 1062 */       arrayOfChar[(k++)] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".charAt(l);
/* 1063 */       l = paramArrayOfByte[j] << 4 & 0x3F;
/* 1064 */       arrayOfChar[k] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".charAt(l);
/* 1065 */       break;
/*      */     case 0:
/* 1072 */       --k;
/*      */     }
/*      */ 
/* 1080 */     return new String(arrayOfChar);
/*      */   }
/*      */ 
/*      */   private static final byte[] jjDecode(String paramString)
/*      */   {
/* 1096 */     char[] arrayOfChar = paramString.toCharArray();
/*      */ 
/* 1104 */     int i = arrayOfChar.length * 3 / 4;
/*      */ 
/* 1108 */     byte[] arrayOfByte = new byte[i];
/*      */ 
/* 1119 */     int j = 0;
/*      */     int l;
/*      */     int i1;
/* 1119 */     for (int k = 0; j < arrayOfChar.length - 3; j += 4)
/*      */     {
/* 1122 */       l = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[j]) << 2;
/* 1123 */       i1 = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[(j + 1)]) & 0x30) >> 4;
/*      */ 
/* 1125 */       arrayOfByte[(k++)] = (byte)(l | i1);
/*      */ 
/* 1127 */       l = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[(j + 1)]) & 0xF) << 4;
/* 1128 */       i1 = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[(j + 2)]) & 0x3E) >> 2;
/*      */ 
/* 1130 */       arrayOfByte[(k++)] = (byte)(l | i1);
/*      */ 
/* 1132 */       l = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[(j + 2)]) & 0x3) << 6;
/* 1133 */       i1 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[(j + 3)]) & 0x3F;
/*      */ 
/* 1135 */       arrayOfByte[(k++)] = (byte)(l | i1);
/*      */     }
/*      */ 
/* 1143 */     switch (arrayOfChar.length - j)
/*      */     {
/*      */     case 3:
/* 1146 */       l = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[j]) & 0x3F) << 2;
/* 1147 */       i1 = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[(j + 1)]) & 0x30) >> 4;
/* 1148 */       arrayOfByte[(k++)] = (byte)(l | i1);
/*      */ 
/* 1150 */       l = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[(j + 1)]) & 0xF) << 4;
/* 1151 */       i1 = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[(j + 2)]) & 0x3E) >> 2;
/* 1152 */       arrayOfByte[k] = (byte)(l | i1);
/*      */ 
/* 1154 */       break;
/*      */     case 2:
/* 1157 */       l = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[j]) & 0x3F) << 2;
/* 1158 */       i1 = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#".indexOf(arrayOfChar[(j + 1)]) & 0x30) >> 4;
/* 1159 */       arrayOfByte[k] = (byte)(l | i1);
/*      */ 
/* 1161 */       break;
/*      */     case 1:
/* 1168 */       break;
/*      */     case 0:
/* 1175 */       --k;
/*      */     }
/*      */ 
/* 1184 */     return arrayOfByte;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.jdbc.SybXAResource
 * JD-Core Version:    0.5.4
 */