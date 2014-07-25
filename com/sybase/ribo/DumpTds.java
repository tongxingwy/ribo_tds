/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import com.sybase.jdbc3.tds.Dumpable;
/*     */ import com.sybase.jdbc3.tds.Iana;
/*     */ import com.sybase.jdbc3.tds.SrvAltFmtToken;
/*     */ import com.sybase.jdbc3.tds.SrvAltNameToken;
/*     */ import com.sybase.jdbc3.tds.SrvAltRowToken;
/*     */ import com.sybase.jdbc3.tds.SrvCapabilityToken;
/*     */ import com.sybase.jdbc3.tds.SrvColInfoToken;
/*     */ import com.sybase.jdbc3.tds.SrvControlToken;
/*     */ import com.sybase.jdbc3.tds.SrvCurCloseToken;
/*     */ import com.sybase.jdbc3.tds.SrvCurDeclare2Token;
/*     */ import com.sybase.jdbc3.tds.SrvCurDeclare3Token;
/*     */ import com.sybase.jdbc3.tds.SrvCurDeclareToken;
/*     */ import com.sybase.jdbc3.tds.SrvCurDeleteToken;
/*     */ import com.sybase.jdbc3.tds.SrvCurFetchToken;
/*     */ import com.sybase.jdbc3.tds.SrvCurInfo3Token;
/*     */ import com.sybase.jdbc3.tds.SrvCurInfoToken;
/*     */ import com.sybase.jdbc3.tds.SrvCurOpenToken;
/*     */ import com.sybase.jdbc3.tds.SrvCurUpdateToken;
/*     */ import com.sybase.jdbc3.tds.SrvDbrpcToken;
/*     */ import com.sybase.jdbc3.tds.SrvDoneInProcToken;
/*     */ import com.sybase.jdbc3.tds.SrvDoneProcToken;
/*     */ import com.sybase.jdbc3.tds.SrvDoneToken;
/*     */ import com.sybase.jdbc3.tds.SrvDynamic2Token;
/*     */ import com.sybase.jdbc3.tds.SrvDynamicToken;
/*     */ import com.sybase.jdbc3.tds.SrvEedToken;
/*     */ import com.sybase.jdbc3.tds.SrvEnvChangeToken;
/*     */ import com.sybase.jdbc3.tds.SrvErrorToken;
/*     */ import com.sybase.jdbc3.tds.SrvEventToken;
/*     */ import com.sybase.jdbc3.tds.SrvFormatToken;
/*     */ import com.sybase.jdbc3.tds.SrvInfoToken;
/*     */ import com.sybase.jdbc3.tds.SrvJavaTypeFormatter;
/*     */ import com.sybase.jdbc3.tds.SrvKeyToken;
/*     */ import com.sybase.jdbc3.tds.SrvLanguageToken;
/*     */ import com.sybase.jdbc3.tds.SrvLoginAckToken;
/*     */ import com.sybase.jdbc3.tds.SrvLoginToken;
/*     */ import com.sybase.jdbc3.tds.SrvLogoutToken;
/*     */ import com.sybase.jdbc3.tds.SrvMsgToken;
/*     */ import com.sybase.jdbc3.tds.SrvOffsetToken;
/*     */ import com.sybase.jdbc3.tds.SrvOptionCmdToken;
/*     */ import com.sybase.jdbc3.tds.SrvOrderBy2Token;
/*     */ import com.sybase.jdbc3.tds.SrvOrderByToken;
/*     */ import com.sybase.jdbc3.tds.SrvParamFormat2Token;
/*     */ import com.sybase.jdbc3.tds.SrvParamFormatToken;
/*     */ import com.sybase.jdbc3.tds.SrvParamsToken;
/*     */ import com.sybase.jdbc3.tds.SrvRPCToken;
/*     */ import com.sybase.jdbc3.tds.SrvReturnStatusToken;
/*     */ import com.sybase.jdbc3.tds.SrvReturnValueToken;
/*     */ import com.sybase.jdbc3.tds.SrvRowFormat2Token;
/*     */ import com.sybase.jdbc3.tds.SrvRowFormatToken;
/*     */ import com.sybase.jdbc3.tds.SrvRowToken;
/*     */ import com.sybase.jdbc3.tds.SrvTabNameToken;
/*     */ import com.sybase.jdbc3.tds.Token;
/*     */ import com.sybase.jdbc3.utils.DumpFilter;
/*     */ import com.sybase.jdbc3.utils.DumpInfo;
/*     */ import com.sybase.jdbc3.utils.HexConverts;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Writer;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.EventObject;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class DumpTds
/*     */   implements HeaderListener
/*     */ {
/*  36 */   private InputStream _inputStream = null;
/*     */ 
/*  42 */   private Writer _writer = null;
/*     */   private FormatTable _cursorFormats;
/*     */   private Hashtable _altFormats;
/*     */   private DumpFilter _filter;
/*  54 */   private SrvFormatToken _formatToken = null;
/*     */   private int _cursorId;
/*     */   private Vector _listeners;
/*     */   private RiboMgr _supervisor;
/*     */   private String _encoding;
/*  77 */   private boolean _processedLoginToken = false;
/*     */   private int _chunksWritten;
/*  82 */   private String _formatterName = null;
/*     */ 
/*  85 */   private Hashtable _paramFormats = null;
/*     */ 
/*  88 */   private Hashtable _rowFormats = null;
/*     */ 
/*  91 */   private Hashtable _eedParamFormats = null;
/*     */ 
/*  94 */   private boolean _gotEEDToken = false;
/*     */ 
/*     */   protected DumpTds(InputStream is, Writer writer)
/*     */   {
/* 101 */     this(RiboMgr.getInstance(), RiboMgr.getPrefs()._filter, RiboMgr.getPrefs()._encoding, is, writer);
/*     */   }
/*     */ 
/*     */   public DumpTds(DumpFilter filter, InputStream is)
/*     */   {
/* 113 */     this(null, filter, "", is, null);
/*     */   }
/*     */ 
/*     */   private DumpTds(RiboMgr supervisor, DumpFilter aFilter, String encoding, InputStream is, Writer writer)
/*     */   {
/* 128 */     if (aFilter == null)
/*     */     {
/* 130 */       throw new IllegalArgumentException(RiboMessage.getMessage("NoFilterSpecified"));
/*     */     }
/* 132 */     this._filter = aFilter;
/* 133 */     this._encoding = encoding;
/* 134 */     this._cursorFormats = new FormatTable(null);
/* 135 */     this._paramFormats = new Hashtable();
/* 136 */     this._rowFormats = new Hashtable();
/* 137 */     this._eedParamFormats = new Hashtable();
/* 138 */     this._altFormats = new Hashtable();
/* 139 */     this._listeners = new Vector();
/* 140 */     this._supervisor = supervisor;
/*     */ 
/* 142 */     this._inputStream = is;
/* 143 */     this._writer = writer;
/*     */   }
/*     */ 
/*     */   public void processTds()
/*     */     throws IOException
/*     */   {
/* 157 */     CaptureInputStream cis = new CaptureInputStream(this._inputStream);
/* 158 */     cis.addHeaderListener(this);
/*     */ 
/* 162 */     PduInputStream pis = new PduInputStream(cis);
/* 163 */     pis.addHeaderListener(this);
/*     */ 
/* 166 */     int source = cis.getSource();
/* 167 */     int pduType = pis.getPDUType();
/* 168 */     for (boolean done = false; !done; )
/*     */     {
/*     */       try
/*     */       {
/* 173 */         if (this._supervisor != null)
/*     */         {
/* 175 */           this._supervisor.stillWorking(2);
/*     */         }
/*     */ 
/* 180 */         if (pis.endOfLogicalPDU())
/*     */         {
/* 182 */           source = cis.switchSource();
/* 183 */           if (source < 0)
/*     */           {
/* 185 */             done = true;
/* 186 */             break label416:
/*     */           }
/* 188 */           pduType = pis.getPDUType();
/*     */         }
/*     */ 
/* 192 */         switch (pduType)
/*     */         {
/*     */         case 2:
/* 201 */           if (!this._processedLoginToken)
/*     */           {
/* 203 */             this._processedLoginToken = true;
/* 204 */             SrvLoginToken token = new SrvLoginToken(pis);
/* 205 */             dumpObject(token);
/*     */ 
/* 209 */             pis.setBigEndian(token.getBigEndian());
/* 210 */             determineEncoding(token.getCharset());
/* 211 */             if (this._encoding.length() > 0)
/*     */             {
/* 213 */               pis.setEncoding(Iana.lookupIana(this._encoding));
/*     */             }
/* 215 */             if (!pis.endOfLogicalPDU())
/*     */             {
/* 219 */               done = dumpToken(pis);
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 224 */             done = dumpToken(pis);
/*     */           }
/* 226 */           break;
/*     */         case 5:
/*     */         case 7:
/* 231 */           dumpLogicalPDU(pis, pduType);
/* 232 */           break;
/*     */         case 8:
/*     */         case 9:
/*     */         case 10:
/*     */         case 11:
/*     */         case 13:
/* 241 */           break;
/*     */         case 1:
/*     */         case 3:
/*     */         case 6:
/*     */         case 12:
/*     */         case 14:
/* 250 */           dumpln(RiboMessage.getMessage("PduPacketNotDumped"));
/* 251 */           pis.skipRestOfLogicalPDU();
/* 252 */           break;
/*     */         case 4:
/*     */         case 15:
/*     */         case 16:
/*     */         case 17:
/* 258 */           if (!pis.endOfLogicalPDU())
/*     */           {
/* 260 */             done = dumpToken(pis); } break;
/*     */         case 24:
/*     */         case 25:
/*     */         case 26:
/*     */         case 27:
/*     */         case 28:
/* 271 */           dumpln(RiboMessage.getMessage("CmdSeqProtocolPacketNotDumped"));
/* 272 */           pis.skipRestOfLogicalPDU();
/* 273 */           break;
/*     */         case 18:
/*     */         case 19:
/*     */         case 20:
/*     */         case 21:
/*     */         case 22:
/*     */         case 23:
/*     */         default:
/* 277 */           throw new IOException(RiboMessage.makeMessage("UnrecognizedPacketType", HexConverts.hexConvert(pduType, 1)));
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/* 283 */         dumpln(RiboMessage.getMessage("ErrorProcessingTds"));
/* 284 */         pis.skipRestOfLogicalPDU();
/*     */       }
/*     */     }
/*     */ 
/* 288 */     label416: dumpln(RiboMessage.getMessage("End"));
/*     */ 
/* 291 */     cis.close();
/*     */   }
/*     */ 
/*     */   private void dumpLogicalPDU(PduInputStream pis, int pduType)
/*     */     throws IOException
/*     */   {
/* 300 */     DumpInfo info = null;
/* 301 */     if (this._filter.includesToken(259))
/*     */     {
/* 303 */       info = this._filter.getDumpInfo();
/* 304 */       info.addInfo("Token", 0, "Raw PDU Data");
/* 305 */       if (this._filter.includesDetail(3))
/*     */       {
/* 307 */         byte[] data = pis.readRestOfLogicalPDU();
/* 308 */         info.addHex("Unformatted Contents", data.length, data);
/*     */       }
/*     */       else
/*     */       {
/* 312 */         pis.skipRestOfLogicalPDU();
/*     */       }
/*     */ 
/* 315 */       dumpln(info.toString());
/*     */     }
/*     */     else
/*     */     {
/* 319 */       dumpln(RiboMessage.getMessage("PduPacketNotDumped"));
/* 320 */       pis.skipRestOfLogicalPDU();
/*     */     }
/*     */ 
/* 324 */     Object anObject = new Dumpable()
/*     */     {
/*     */       public DumpInfo dump(DumpFilter aFilter) throws IOException
/*     */       {
/* 328 */         return null;
/*     */       }
/*     */ 
/*     */       public int getTokenType()
/*     */       {
/* 333 */         return 259;
/*     */       }
/*     */     };
/* 337 */     postInfoEvent(new InfoEvent(anObject, info));
/*     */   }
/*     */ 
/*     */   private boolean dumpToken(PduInputStream pis)
/*     */     throws IOException
/*     */   {
/* 350 */     Token token = null;
/* 351 */     String activity = "";
/*     */     try
/*     */     {
/* 355 */       activity = "ReadTokenException";
/* 356 */       token = readToken(pis);
/*     */ 
/* 358 */       activity = "DumpTokenException";
/* 359 */       dumpObject(token);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 363 */       writeErrorMessage(e, activity);
/* 364 */       pis.skipRestOfLogicalPDU();
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/* 368 */       writeErrorMessage(e, activity);
/* 369 */       pis.skipRestOfLogicalPDU();
/*     */     }
/*     */ 
/* 372 */     return token instanceof SrvLogoutToken;
/*     */   }
/*     */ 
/*     */   private Token readToken(PduInputStream pis)
/*     */     throws IOException, SQLException
/*     */   {
/* 386 */     int token = 0;
/*     */ 
/* 388 */     token = pis.readUnsignedByte();
/* 389 */     Token answer = null;
/*     */ 
/* 392 */     switch (token)
/*     */     {
/*     */     case 229:
/* 395 */       answer = new SrvEedToken(pis);
/* 396 */       this._gotEEDToken = true;
/* 397 */       break;
/*     */     case 173:
/* 399 */       answer = new SrvLoginAckToken(pis);
/* 400 */       break;
/*     */     case 113:
/* 402 */       answer = new SrvLogoutToken(pis);
/* 403 */       break;
/*     */     case 227:
/* 405 */       answer = new SrvEnvChangeToken(pis);
/* 406 */       determineEncoding(((SrvEnvChangeToken)answer).getCharset());
/* 407 */       if (this._encoding.length() > 0)
/*     */       {
/* 409 */         pis.setEncoding(Iana.lookupIana(this._encoding)); } break;
/*     */     case 170:
/* 413 */       answer = new SrvErrorToken(pis);
/* 414 */       break;
/*     */     case 171:
/* 416 */       answer = new SrvInfoToken(pis);
/* 417 */       break;
/*     */     case 33:
/* 419 */       answer = new SrvLanguageToken(pis);
/* 420 */       break;
/*     */     case 230:
/* 422 */       answer = new SrvDbrpcToken(pis);
/* 423 */       this._formatterName = null;
/* 424 */       break;
/*     */     case 231:
/* 426 */       SrvDynamicToken dynamicToken = new SrvDynamicToken(pis);
/* 427 */       this._formatterName = dynamicToken.getName();
/* 428 */       answer = dynamicToken;
/* 429 */       break;
/*     */     case 98:
/* 431 */       SrvDynamic2Token dynamic2Token = new SrvDynamic2Token(pis);
/* 432 */       this._formatterName = dynamic2Token.getName();
/* 433 */       answer = dynamic2Token;
/* 434 */       break;
/*     */     case 209:
/* 436 */       answer = new SrvRowToken(pis);
/*     */       SrvFormatToken fmtToken;
/*     */       SrvFormatToken fmtToken;
/* 439 */       if (this._cursorFormats.contains(this._cursorId))
/*     */       {
/* 441 */         fmtToken = this._cursorFormats.get(this._cursorId);
/*     */       }
/*     */       else
/*     */       {
/* 445 */         if (this._formatterName != null)
/*     */         {
/* 447 */           this._formatToken = ((SrvFormatToken)this._rowFormats.get(this._formatterName));
/*     */         }
/* 449 */         fmtToken = this._formatToken;
/*     */       }
/* 451 */       ((SrvRowToken)answer).setFormatter(new SrvJavaTypeFormatter(fmtToken, null, false));
/*     */ 
/* 453 */       break;
/*     */     case 238:
/* 455 */       answer = new SrvRowFormatToken(pis);
/*     */ 
/* 458 */       this._formatToken = ((SrvFormatToken)answer);
/* 459 */       if ((this._cursorId != 0) && (this._cursorFormats.get(this._cursorId) == null))
/*     */       {
/* 461 */         this._cursorFormats.put(this._cursorId, this._formatToken);
/*     */       }
/*     */       else
/*     */       {
/* 465 */         this._cursorId = 0;
/*     */       }
/* 467 */       if (this._formatterName != null)
/*     */       {
/* 469 */         this._rowFormats.put(this._formatterName, this._formatToken); } break;
/*     */     case 97:
/* 473 */       answer = new SrvRowFormat2Token(pis);
/*     */ 
/* 476 */       this._formatToken = ((SrvFormatToken)answer);
/* 477 */       if ((this._cursorId != 0) && (this._cursorFormats.get(this._cursorId) == null))
/*     */       {
/* 479 */         this._cursorFormats.put(this._cursorId, this._formatToken);
/*     */       }
/*     */       else
/*     */       {
/* 483 */         this._cursorId = 0;
/*     */       }
/* 485 */       if (this._formatterName != null)
/*     */       {
/* 487 */         this._rowFormats.put(this._formatterName, this._formatToken); } break;
/*     */     case 215:
/* 491 */       SrvParamsToken srvParamsToken = new SrvParamsToken(pis);
/* 492 */       if (this._formatterName != null)
/*     */       {
/* 494 */         if (this._gotEEDToken)
/*     */         {
/* 496 */           this._formatToken = ((SrvFormatToken)this._eedParamFormats.get(this._formatterName));
/*     */         }
/*     */         else
/*     */         {
/* 500 */           this._formatToken = ((SrvFormatToken)this._paramFormats.get(this._formatterName));
/*     */         }
/*     */       }
/* 503 */       srvParamsToken.setFormatter(new SrvJavaTypeFormatter(this._formatToken, null, false));
/* 504 */       answer = srvParamsToken;
/* 505 */       break;
/*     */     case 236:
/* 507 */       answer = new SrvParamFormatToken(pis);
/* 508 */       this._formatToken = ((SrvFormatToken)answer);
/* 509 */       if (this._formatterName != null)
/*     */       {
/* 511 */         if (this._gotEEDToken)
/*     */         {
/* 513 */           this._eedParamFormats.put(this._formatterName, this._formatToken);
/*     */         }
/*     */         else
/*     */         {
/* 517 */           this._paramFormats.put(this._formatterName, this._formatToken); } 
/* 517 */       }break;
/*     */     case 32:
/* 522 */       answer = new SrvParamFormat2Token(pis);
/* 523 */       this._formatToken = ((SrvFormatToken)answer);
/* 524 */       if (this._formatterName != null)
/*     */       {
/* 526 */         if (this._gotEEDToken)
/*     */         {
/* 528 */           this._eedParamFormats.put(this._formatterName, this._formatToken);
/*     */         }
/*     */         else
/*     */         {
/* 532 */           this._paramFormats.put(this._formatterName, this._formatToken); } 
/* 532 */       }break;
/*     */     case 121:
/* 537 */       answer = new SrvReturnStatusToken(pis);
/* 538 */       break;
/*     */     case 226:
/* 540 */       answer = new SrvCapabilityToken(pis);
/* 541 */       break;
/*     */     case 174:
/* 543 */       answer = new SrvControlToken(pis);
/* 544 */       break;
/*     */     case 253:
/* 546 */       this._gotEEDToken = false;
/* 547 */       answer = new SrvDoneToken(pis);
/* 548 */       break;
/*     */     case 254:
/* 550 */       this._gotEEDToken = false;
/* 551 */       answer = new SrvDoneProcToken(pis);
/* 552 */       break;
/*     */     case 255:
/* 554 */       answer = new SrvDoneInProcToken(pis);
/* 555 */       break;
/*     */     case 128:
/* 557 */       answer = new SrvCurCloseToken(pis);
/* 558 */       this._cursorId = 0;
/* 559 */       break;
/*     */     case 134:
/* 561 */       answer = new SrvCurDeclareToken(pis);
/* 562 */       break;
/*     */     case 35:
/* 564 */       answer = new SrvCurDeclare2Token(pis);
/* 565 */       break;
/*     */     case 16:
/* 567 */       answer = new SrvCurDeclare3Token(pis);
/* 568 */       break;
/*     */     case 129:
/* 570 */       answer = new SrvCurDeleteToken(pis);
/* 571 */       break;
/*     */     case 130:
/* 573 */       answer = new SrvCurFetchToken(pis);
/* 574 */       this._cursorId = ((SrvCurFetchToken)answer)._curId;
/* 575 */       break;
/*     */     case 131:
/* 577 */       answer = new SrvCurInfoToken(pis);
/* 578 */       this._cursorId = ((SrvCurInfoToken)answer).curId();
/* 579 */       break;
/*     */     case 136:
/* 581 */       answer = new SrvCurInfo3Token(pis);
/* 582 */       this._cursorId = ((SrvCurInfo3Token)answer).curId();
/* 583 */       break;
/*     */     case 132:
/* 585 */       answer = new SrvCurOpenToken(pis);
/* 586 */       break;
/*     */     case 133:
/* 588 */       answer = new SrvCurUpdateToken(pis);
/* 589 */       this._cursorId = ((SrvCurUpdateToken)answer)._curId;
/* 590 */       break;
/*     */     case 202:
/* 592 */       answer = new SrvKeyToken(pis);
/*     */       SrvFormatToken fmtToken;
/*     */       SrvFormatToken fmtToken;
/* 595 */       if (this._cursorFormats.contains(this._cursorId))
/*     */       {
/* 597 */         fmtToken = this._cursorFormats.get(this._cursorId);
/*     */       }
/*     */       else
/*     */       {
/* 601 */         fmtToken = this._formatToken;
/*     */       }
/* 603 */       if (fmtToken instanceof SrvRowFormat2Token)
/*     */       {
/* 605 */         if (((SrvRowFormat2Token)fmtToken).hasKeyColumns())
/*     */         {
/* 607 */           ((SrvKeyToken)answer).setFormatter(new SrvJavaTypeFormatter(fmtToken, null, false));
/*     */         }
/*     */ 
/*     */       }
/* 613 */       else if (((SrvRowFormatToken)fmtToken).hasKeyColumns())
/*     */       {
/* 615 */         ((SrvKeyToken)answer).setFormatter(new SrvJavaTypeFormatter(fmtToken, null, false)); } break;
/*     */     case 169:
/* 621 */       answer = new SrvOrderByToken(pis);
/* 622 */       break;
/*     */     case 34:
/* 624 */       answer = new SrvOrderBy2Token(pis);
/* 625 */       break;
/*     */     case 167:
/* 628 */       answer = new SrvAltNameToken(pis);
/* 629 */       break;
/*     */     case 168:
/* 631 */       answer = new SrvAltFmtToken(pis);
/*     */ 
/* 633 */       this._altFormats.put(new Integer(((SrvAltFmtToken)answer).getId()), answer);
/*     */ 
/* 635 */       break;
/*     */     case 211:
/* 637 */       answer = new SrvAltRowToken(pis);
/*     */ 
/* 639 */       SrvFormatToken altFormat = (SrvFormatToken)this._altFormats.get(new Integer(((SrvAltRowToken)answer).getId()));
/*     */ 
/* 641 */       ((SrvAltRowToken)answer).setFormatter(new SrvJavaTypeFormatter(altFormat, null, false));
/*     */ 
/* 643 */       break;
/*     */     case 101:
/* 645 */       answer = new SrvMsgToken(pis);
/* 646 */       break;
/*     */     case 162:
/* 648 */       answer = new SrvEventToken(pis);
/* 649 */       break;
/*     */     case 120:
/* 651 */       answer = new SrvOffsetToken(pis);
/* 652 */       break;
/*     */     case 166:
/* 654 */       answer = new SrvOptionCmdToken(pis);
/* 655 */       break;
/*     */     case 164:
/* 657 */       answer = new SrvTabNameToken(pis);
/* 658 */       break;
/*     */     case 165:
/* 660 */       answer = new SrvColInfoToken(pis);
/* 661 */       break;
/*     */     case 224:
/* 664 */       answer = new SrvRPCToken(pis);
/* 665 */       break;
/*     */     case 172:
/* 667 */       answer = new SrvReturnValueToken(pis);
/* 668 */       break;
/*     */     default:
/* 671 */       throw new IOException(RiboMessage.makeMessage("UnrecognizedPacketType", HexConverts.hexConvert(token, 1)));
/*     */     }
/*     */ 
/* 676 */     if (((Dumpable)answer).getTokenType() != token)
/*     */     {
/* 678 */       throw new IOException(RiboMessage.makeMessage("TokenDoesntKnowItsType", HexConverts.hexConvert(token, 1)));
/*     */     }
/*     */ 
/* 682 */     return answer;
/*     */   }
/*     */ 
/*     */   private void determineEncoding(String tokenEncoding)
/*     */   {
/* 696 */     if ((tokenEncoding != null) && (tokenEncoding.length() > 0))
/*     */     {
/* 698 */       this._encoding = tokenEncoding;
/* 699 */       if (this._encoding.length() <= 0) {
/*     */         return;
/*     */       }
/*     */ 
/* 703 */       dumpln(RiboMessage.getMessage("TokenCharacterSetInfoUsed"));
/*     */     }
/*     */     else
/*     */     {
/* 709 */       if (this._encoding.length() > 0) {
/*     */         return;
/*     */       }
/* 712 */       dumpln(RiboMessage.getMessage("NoEncodingInfoAvailable"));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeErrorMessage(Exception exception, String message)
/*     */   {
/* 725 */     String text = RiboMessage.makeMessage(message, exception.toString());
/* 726 */     dumpln(">>>>> " + text + "\n");
/*     */ 
/* 728 */     if (this._supervisor == null)
/*     */       return;
/* 730 */     this._supervisor.println(text);
/*     */   }
/*     */ 
/*     */   private void dumpObject(Object anObject)
/*     */     throws IOException
/*     */   {
/* 745 */     DumpInfo di = null;
/* 746 */     if (anObject instanceof Dumpable)
/*     */     {
/* 748 */       di = ((Dumpable)anObject).dump(this._filter);
/* 749 */       if (di != null)
/*     */       {
/* 751 */         String string = di.toString();
/* 752 */         dumpln(string);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 757 */       String string = anObject.toString();
/* 758 */       dumpln("--\n" + string + "\n--\n");
/*     */     }
/* 760 */     postInfoEvent(new InfoEvent(anObject, di));
/*     */   }
/*     */ 
/*     */   private void dumpln(String msg)
/*     */   {
/*     */     try
/*     */     {
/* 772 */       int chunkSize = RiboMgr.getPrefs().getChunkSize();
/*     */ 
/* 774 */       if (chunkSize != 0)
/*     */       {
/* 776 */         this._chunksWritten += msg.length();
/*     */ 
/* 779 */         chunkSize -= 80;
/*     */ 
/* 781 */         if (this._chunksWritten >= chunkSize)
/*     */         {
/* 786 */           closeWriterAfterWrite("\n" + RiboMessage.getMessage("DataContinuedInNextFile"));
/*     */ 
/* 789 */           this._writer = RiboMgr.getInstance().getNextFileWriter();
/* 790 */           int length = msg.length();
/* 791 */           while (length > chunkSize)
/*     */           {
/* 793 */             StringBuffer chunkOfToken = new StringBuffer(msg.substring(0, chunkSize));
/*     */ 
/* 795 */             msg = msg.substring(chunkSize);
/* 796 */             length -= chunkSize;
/* 797 */             closeWriterAfterWrite(chunkOfToken.toString() + "\n" + RiboMessage.getMessage("TokenContinuedInNextFile"));
/*     */ 
/* 800 */             this._writer = RiboMgr.getInstance().getNextFileWriter();
/*     */           }
/* 802 */           this._chunksWritten = msg.length();
/*     */         }
/*     */       }
/*     */ 
/* 806 */       if (this._writer != null)
/*     */       {
/* 808 */         this._writer.write(msg + "\n");
/*     */ 
/* 810 */         this._writer.flush();
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 815 */       throw new Error(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void closeWriterAfterWrite(String msg) throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 823 */       if (this._writer != null)
/*     */       {
/* 825 */         this._writer.write(msg);
/* 826 */         this._writer.flush();
/*     */       }
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/* 831 */       throw ioe;
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 837 */         if (this._writer != null)
/*     */         {
/* 839 */           this._writer.close();
/* 840 */           this._writer = null;
/*     */         }
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void newHeader(EventObject event)
/*     */   {
/* 860 */     Dumpable source = (Dumpable)event.getSource();
/*     */     try
/*     */     {
/* 863 */       dumpObject(source);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 867 */       writeErrorMessage(e, "DumpHeaderException");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addInfoListener(InfoListener listener)
/*     */   {
/* 919 */     this._listeners.addElement(listener);
/*     */   }
/*     */ 
/*     */   public void removeInfoListener(InfoListener listener)
/*     */   {
/* 929 */     this._listeners.removeElement(listener);
/*     */   }
/*     */ 
/*     */   public void postInfoEvent(InfoEvent event)
/*     */   {
/* 938 */     Enumeration sybenum = this._listeners.elements();
/* 939 */     while (sybenum.hasMoreElements())
/*     */     {
/* 941 */       InfoListener listener = (InfoListener)sybenum.nextElement();
/* 942 */       listener.newInfo(event);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Writer getWriter()
/*     */   {
/* 948 */     return this._writer;
/*     */   }
/*     */ 
/*     */   private class FormatTable
/*     */   {
/*     */     private Hashtable _hashtable;
/*     */     private final DumpTds this$0;
/*     */ 
/*     */     private FormatTable()
/*     */     {
/* 875 */       this.this$0 = this$0;
/*     */ 
/* 877 */       this._hashtable = new Hashtable();
/*     */     }
/*     */ 
/*     */     protected void put(int id, SrvFormatToken ft)
/*     */     {
/* 884 */       if (id <= 0)
/*     */         return;
/* 886 */       this._hashtable.put(new Integer(id), ft);
/*     */     }
/*     */ 
/*     */     protected SrvFormatToken get(int id)
/*     */     {
/* 896 */       return (SrvFormatToken)this._hashtable.get(new Integer(id));
/*     */     }
/*     */ 
/*     */     protected boolean contains(int id)
/*     */     {
/* 904 */       boolean answer = (id > 0) && (this._hashtable.containsKey(new Integer(id)));
/* 905 */       return answer;
/*     */     }
/*     */ 
/*     */     FormatTable(DumpTds.1 x1)
/*     */     {
/* 875 */       this(x0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.DumpTds
 * JD-Core Version:    0.5.4
 */