/*      */ package com.sybase.jdbc3.tds;
/*      */ 
/*      */ import com.sybase.jdbc3.jdbc.ErrorMessage;
/*      */ import com.sybase.jdbc3.utils.Debug;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketException;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Vector;
/*      */ import javax.security.auth.Subject;
/*      */ 
/*      */ public class SrvSession
/*      */   implements Runnable
/*      */ {
/*   67 */   private OutputStream _rawOut = null;
/*   68 */   private SrvDataInputStream _tdsin = null;
/*   69 */   private SrvDataOutputStream _tdsout = null;
/*   70 */   private Socket _srvSocket = null;
/*      */ 
/*   72 */   private SrvDataOutputStream _asyncNotif = null;
/*      */ 
/*   75 */   private Vector _listeners = new Vector();
/*      */ 
/*   77 */   protected Vector _events = new Vector();
/*      */ 
/*   79 */   protected SrvCapabilityToken _cap = null;
/*      */ 
/*   81 */   private SrvLoginToken _login = null;
/*   82 */   private volatile boolean _running = false;
/*      */ 
/*   88 */   private String _encoding = System.getProperty("file.encoding");
/*      */ 
/*   93 */   protected boolean _encodingChanged = false;
/*      */ 
/*   96 */   private Hashtable _sessionData = new Hashtable();
/*      */ 
/*   99 */   protected Hashtable _cursors = new Hashtable();
/*      */ 
/*  104 */   public Subject _subject = null;
/*      */ 
/*      */   public SrvSession(InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  124 */       if (paramBoolean2)
/*      */       {
/*  126 */         this._rawOut = new SrvDumpOutputStream(paramOutputStream);
/*      */       }
/*      */       else
/*      */       {
/*  130 */         this._rawOut = paramOutputStream;
/*      */       }
/*  132 */       this._tdsout = new SrvDataOutputStream(this._rawOut);
/*  133 */       if (paramBoolean1)
/*      */       {
/*  135 */         this._tdsin = new SrvDataInputStream(new SrvDumpInputStream(paramInputStream));
/*      */       }
/*      */       else
/*      */       {
/*  139 */         this._tdsin = new SrvDataInputStream(paramInputStream);
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  144 */       throw localIOException;
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*  149 */       throw new IOException(localException.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   public SrvSession(Socket paramSocket, InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws IOException
/*      */   {
/*  160 */     this(paramInputStream, paramOutputStream, paramBoolean1, paramBoolean2);
/*  161 */     this._srvSocket = paramSocket;
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/*      */     try
/*      */     {
/*  177 */       login();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  182 */       fireEvent(localIOException, null);
/*  183 */       return;
/*      */     }
/*  185 */     if (this._subject != null) {
/*  186 */       Subject.doAs(this._subject, new SrvSession.1(this));
/*      */     }
/*      */     else
/*      */     {
/*  198 */       processRequests();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void processRequests()
/*      */   {
/*  205 */     this._running = true;
/*  206 */     while (this._running)
/*      */     {
/*      */       try
/*      */       {
/*  210 */         fireEvent(receive(), null);
/*      */       }
/*      */       catch (SrvAttentionException localSrvAttentionException)
/*      */       {
/*  214 */         fireEvent(null, null);
/*      */       }
/*      */       catch (SrvBulkException localSrvBulkException)
/*      */       {
/*  218 */         fireEvent(localSrvBulkException, null);
/*      */         try
/*      */         {
/*  224 */           if (this._tdsin.read() == -1);
/*  227 */           this._tdsin.bulkDone();
/*      */         }
/*      */         catch (IOException localIOException2)
/*      */         {
/*  232 */           fireEvent(localIOException2, null);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SocketException localSocketException)
/*      */       {
/*  240 */         this._listeners.removeElement(this);
/*  241 */         close();
/*  242 */         return;
/*      */       }
/*      */       catch (IOException localIOException1)
/*      */       {
/*  247 */         fireEvent(localIOException1, null);
/*      */       }
/*  249 */       Thread.yield();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Token receive()
/*      */     throws IOException, SrvAttentionException, SrvBulkException
/*      */   {
/*  266 */     int i = 0;
/*      */     try
/*      */     {
/*  270 */       i = this._tdsin.readUnsignedByte();
/*  271 */       if (this._tdsin.getCurrentPDUType() == 1) {
/*  272 */         SrvPassthroughLanguageToken localSrvPassthroughLanguageToken = new SrvPassthroughLanguageToken(i, this._tdsin);
/*  273 */         return localSrvPassthroughLanguageToken;
/*      */       }
/*      */     }
/*      */     catch (SrvAttentionException localSrvAttentionException)
/*      */     {
/*  278 */       this._tdsin.flush();
/*  279 */       throw localSrvAttentionException;
/*      */     }
/*      */ 
/*  282 */     Object localObject = null;
/*  283 */     switch (i)
/*      */     {
/*      */     case 101:
/*  286 */       localObject = new SrvMsgToken(this._tdsin);
/*  287 */       break;
/*      */     case 33:
/*  291 */       localObject = new SrvLanguageToken(this._tdsin);
/*  292 */       break;
/*      */     case 113:
/*  295 */       localObject = new SrvLogoutToken(this._tdsin);
/*  296 */       break;
/*      */     case 226:
/*      */       try
/*      */       {
/*  302 */         localObject = new SrvCapabilityToken(this._tdsin);
/*      */       }
/*      */       catch (SQLException localSQLException)
/*      */       {
/*  306 */         ErrorMessage.raiseIOException(localSQLException.getSQLState());
/*      */       }
/*  308 */       break;
/*      */     case 230:
/*  311 */       localObject = new SrvDbrpcToken(this._tdsin);
/*  312 */       break;
/*      */     case 236:
/*  315 */       localObject = new SrvParamFormatToken(this._tdsin);
/*  316 */       break;
/*      */     case 32:
/*  319 */       localObject = new SrvParamFormat2Token(this._tdsin);
/*  320 */       break;
/*      */     case 215:
/*  323 */       localObject = new SrvParamsToken(this._tdsin);
/*  324 */       break;
/*      */     case 128:
/*  327 */       localObject = new SrvCurCloseToken(this._tdsin);
/*  328 */       break;
/*      */     case 35:
/*  331 */       localObject = new SrvCurDeclare2Token(this._tdsin);
/*  332 */       break;
/*      */     case 16:
/*  335 */       localObject = new SrvCurDeclare3Token(this._tdsin);
/*  336 */       break;
/*      */     case 131:
/*  339 */       localObject = new SrvCurInfoToken(this._tdsin);
/*  340 */       break;
/*      */     case 136:
/*  343 */       localObject = new SrvCurInfo3Token(this._tdsin);
/*  344 */       break;
/*      */     case 132:
/*  347 */       localObject = new SrvCurOpenToken(this._tdsin);
/*  348 */       break;
/*      */     case 130:
/*  351 */       localObject = new SrvCurFetchToken(this._tdsin);
/*  352 */       break;
/*      */     case 133:
/*  355 */       localObject = new SrvCurUpdateToken(this._tdsin);
/*  356 */       break;
/*      */     case 129:
/*  359 */       localObject = new SrvCurDeleteToken(this._tdsin);
/*  360 */       break;
/*      */     case 202:
/*  363 */       localObject = new SrvKeyToken(this._tdsin);
/*  364 */       break;
/*      */     case 98:
/*  367 */       localObject = new SrvDynamic2Token(this._tdsin);
/*  368 */       break;
/*      */     default:
/*  371 */       Slurp localSlurp = new Slurp(this._tdsin, i);
/*  372 */       throw new SrvUnknownPacketException("Received unknown or unhandled TDS token type: 0x" + Integer.toHexString(i));
/*      */     }
/*      */ 
/*  376 */     return (Token)localObject;
/*      */   }
/*      */ 
/*      */   public void addSrvRequestListener(SrvRequestListener paramSrvRequestListener)
/*      */   {
/*  382 */     this._listeners.addElement(paramSrvRequestListener);
/*      */   }
/*      */ 
/*      */   public void removeSrvRequestListener(SrvRequestListener paramSrvRequestListener)
/*      */   {
/*  388 */     this._listeners.removeElement(paramSrvRequestListener);
/*      */   }
/*      */ 
/*      */   public void send(Token paramToken)
/*      */     throws IOException
/*      */   {
/*  400 */     send(this._tdsout, paramToken);
/*      */   }
/*      */ 
/*      */   public void send(Token paramToken, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  415 */     paramToken.send(this._tdsout);
/*  416 */     if (!paramBoolean)
/*      */       return;
/*  418 */     this._tdsout.flush(true);
/*      */   }
/*      */ 
/*      */   public void send(SrvDataOutputStream paramSrvDataOutputStream, Token paramToken)
/*      */     throws IOException
/*      */   {
/*  433 */     paramToken.send(paramSrvDataOutputStream);
/*      */ 
/*  436 */     if ((!paramToken instanceof SrvDoneToken) || (!((SrvDoneToken)paramToken).getFinal())) {
/*      */       return;
/*      */     }
/*  439 */     paramSrvDataOutputStream.flush(true);
/*      */   }
/*      */ 
/*      */   public boolean inputStreamAvailable()
/*      */   {
/*      */     try
/*      */     {
/*  454 */       if (this._tdsin.available() > 0)
/*      */       {
/*  456 */         return true;
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */ 
/*  464 */     return false;
/*      */   }
/*      */ 
/*      */   public void sendAttention()
/*      */     throws IOException
/*      */   {
/*  475 */     this._tdsout.setAttention();
/*  476 */     this._tdsout.flush(true);
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */   {
/*      */     try
/*      */     {
/*  484 */       this._running = false;
/*  485 */       if (this._tdsout != null)
/*      */       {
/*  487 */         this._tdsout.flush();
/*  488 */         this._tdsout.close();
/*  489 */         this._tdsout = null;
/*      */       }
/*  491 */       if (this._tdsin != null)
/*      */       {
/*  493 */         this._tdsin.flush();
/*  494 */         this._tdsin.close();
/*  495 */         this._tdsin = null;
/*      */       }
/*      */     }
/*      */     catch (Exception localException1)
/*      */     {
/*      */     }
/*      */ 
/*      */     while (true)
/*      */       try
/*      */       {
/*  505 */         SrvEventListener localSrvEventListener = (SrvEventListener)this._events.elementAt(0);
/*      */ 
/*  507 */         localSrvEventListener.close();
/*  508 */         this._events.removeElementAt(0);
/*      */       }
/*      */       catch (Exception localException2)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   public SrvCapabilityToken getClientCapability()
/*      */   {
/*  521 */     return this._cap;
/*      */   }
/*      */ 
/*      */   public SrvLoginToken getLogin()
/*      */   {
/*  528 */     return this._login;
/*      */   }
/*      */ 
/*      */   public void login()
/*      */     throws IOException
/*      */   {
/*  540 */     this._login = new SrvLoginToken(this._tdsin);
/*      */ 
/*  558 */     this._tdsin.setBigEndian(this._login.getBigEndian());
/*  559 */     this._tdsout.setBigEndian(this._login.getBigEndian());
/*  560 */     if ((this._login.getCharset() == null) || (this._login.getCharset().equals("")))
/*      */     {
/*  562 */       this._encodingChanged = true;
/*  563 */       this._login._charset = Iana.reverseLookupIana(this._encoding);
/*  564 */       if (this._login._charset.equals("UnsupportedCharset"))
/*      */       {
/*  568 */         this._login._charset = "iso_1";
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  575 */     int i = 0;
/*      */     try
/*      */     {
/*  578 */       String str = Iana.lookupIana(this._login.getCharset());
/*  579 */       this._tdsin.setEncoding(str);
/*  580 */       this._tdsout.setEncoding(str);
/*      */ 
/*  582 */       this._encoding = str;
/*      */     }
/*      */     catch (Exception localException1)
/*      */     {
/*  589 */       this._encodingChanged = true;
/*      */       try
/*      */       {
/*  592 */         this._tdsin.setEncoding(this._encoding);
/*  593 */         this._tdsout.setEncoding(this._encoding);
/*      */       }
/*      */       catch (Exception localException2)
/*      */       {
/*  597 */         throw new IOException("Problems negotiating PDU protocol: " + localException2);
/*      */       }
/*  599 */       i = 1;
/*      */     }
/*      */ 
/*  603 */     receiveClientCapabilities();
/*      */ 
/*  607 */     this._tdsin.setPacketSize(this._login.getPacketSize());
/*  608 */     this._tdsout.setPacketSize(this._login.getPacketSize());
/*  609 */     if (i != 0)
/*      */     {
/*  612 */       SrvEedToken localSrvEedToken = new SrvEedToken(2409, 0, 0, "01ZZZ", 0, " Cannot find the requested character set in Syscharsets:  name = '" + this._login.getCharset() + "'. No conversions will be done.", "Unknown", null, 0);
/*      */ 
/*  617 */       send(localSrvEedToken);
/*      */     }
/*  619 */     fireEvent(this._login, null);
/*      */   }
/*      */ 
/*      */   protected void receiveClientCapabilities()
/*      */     throws IOException, SrvAttentionException, SrvBulkException
/*      */   {
/*      */     try
/*      */     {
/*  638 */       this._cap = ((SrvCapabilityToken)receive());
/*  639 */       this._cap.setDefaultServerCapabilities();
/*      */     }
/*      */     catch (ClassCastException localClassCastException)
/*      */     {
/*  643 */       throw new SrvProtocolException("Did not get SrvCapabilityToken for connection request");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void fireEvent(Object paramObject1, Object paramObject2)
/*      */   {
/*  659 */     for (int i = 0; i < this._listeners.size(); ++i)
/*      */     {
/*  661 */       SrvRequestListener localSrvRequestListener = (SrvRequestListener)this._listeners.elementAt(i);
/*  662 */       if (paramObject1 == null)
/*      */       {
/*  664 */         localSrvRequestListener.attention(this);
/*      */       }
/*  666 */       else if (paramObject1 instanceof SrvPassthroughLanguageToken) {
/*  667 */         localSrvRequestListener.passthroughLanguage(this, (SrvPassthroughLanguageToken)paramObject1);
/*      */       }
/*  669 */       else if (paramObject1 instanceof SrvLoginToken)
/*      */       {
/*  671 */         localSrvRequestListener.connect(this, (SrvLoginToken)paramObject1);
/*      */       }
/*  673 */       else if (paramObject1 instanceof SrvLanguageToken)
/*      */       {
/*  675 */         localSrvRequestListener.language(this, (SrvLanguageToken)paramObject1);
/*      */       }
/*  677 */       else if (paramObject1 instanceof SrvDbrpcToken)
/*      */       {
/*  679 */         localSrvRequestListener.rpc(this, (SrvDbrpcToken)paramObject1);
/*      */       }
/*  681 */       else if (paramObject1 instanceof SrvCurCloseToken)
/*      */       {
/*  683 */         localSrvRequestListener.closeCursor(this, (SrvCurCloseToken)paramObject1);
/*      */       }
/*  685 */       else if (paramObject1 instanceof SrvCurDeclareToken)
/*      */       {
/*      */         try
/*      */         {
/*  692 */           if (this._tdsin.available() != 0)
/*      */           {
/*  694 */             localSrvRequestListener.declareCursor(this, (SrvCurDeclareToken)paramObject1, true);
/*      */           }
/*      */           else
/*      */           {
/*  698 */             localSrvRequestListener.declareCursor(this, (SrvCurDeclareToken)paramObject1, false);
/*      */           }
/*      */         }
/*      */         catch (IOException localIOException1)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*  706 */       else if ((paramObject1 instanceof SrvCurInfoToken) || (paramObject1 instanceof SrvCurInfo3Token))
/*      */       {
/*      */         try
/*      */         {
/*  713 */           if (this._tdsin.available() != 0)
/*      */           {
/*  715 */             localSrvRequestListener.processCurInfo(this, (SrvCurInfoToken)paramObject1, true);
/*      */           }
/*      */           else
/*      */           {
/*  719 */             localSrvRequestListener.processCurInfo(this, (SrvCurInfoToken)paramObject1, false);
/*      */           }
/*      */         }
/*      */         catch (IOException localIOException2)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*  727 */       else if (paramObject1 instanceof SrvCurOpenToken)
/*      */       {
/*      */         try
/*      */         {
/*  734 */           if (this._tdsin.available() != 0)
/*      */           {
/*  736 */             localSrvRequestListener.openCursor(this, (SrvCurOpenToken)paramObject1, true);
/*      */           }
/*      */           else
/*      */           {
/*  740 */             localSrvRequestListener.openCursor(this, (SrvCurOpenToken)paramObject1, false);
/*      */           }
/*      */         }
/*      */         catch (IOException localIOException3)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*  748 */       else if (paramObject1 instanceof SrvCurFetchToken)
/*      */       {
/*  750 */         localSrvRequestListener.cursorFetch(this, (SrvCurFetchToken)paramObject1);
/*      */       }
/*  752 */       else if (paramObject1 instanceof SrvCurUpdateToken)
/*      */       {
/*  754 */         localSrvRequestListener.cursorUpdate(this, (SrvCurUpdateToken)paramObject1);
/*      */       }
/*  756 */       else if (paramObject1 instanceof SrvCurDeleteToken)
/*      */       {
/*  758 */         localSrvRequestListener.cursorDelete(this, (SrvCurDeleteToken)paramObject1);
/*      */       } else {
/*  760 */         if (paramObject1 instanceof SrvKeyToken)
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  765 */         if (paramObject1 instanceof SrvDynamic2Token)
/*      */         {
/*  767 */           localSrvRequestListener.dynamicRequest(this, (SrvDynamic2Token)paramObject1);
/*      */         }
/*  769 */         else if (paramObject1 instanceof EOFException)
/*      */         {
/*  772 */           localSrvRequestListener.disconnect(this, null);
/*      */         }
/*  774 */         else if (paramObject1 instanceof SrvLogoutToken)
/*      */         {
/*  776 */           localSrvRequestListener.disconnect(this, (SrvLogoutToken)paramObject1);
/*      */         }
/*  778 */         else if (paramObject1 instanceof SrvBulkException)
/*      */         {
/*  780 */           localSrvRequestListener.bulk(this, this._tdsin);
/*      */         }
/*  782 */         else if (paramObject1 instanceof IOException)
/*      */         {
/*  784 */           localSrvRequestListener.error(this, (IOException)paramObject1);
/*      */         }
/*      */         else
/*      */         {
/*  788 */           Debug.asrt(false, "Invalid request token " + paramObject1.getClass().getName() + ", hence can't fire event");
/*      */ 
/*  791 */           localSrvRequestListener.error(this, new SrvProtocolException("Invalid request token " + paramObject1.getClass().getName() + ", hence can't fire event"));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void sendNotify(String paramString, Object[] paramArrayOfObject)
/*      */     throws IOException, SrvTypeException
/*      */   {
/*  810 */     SrvDataOutputStream localSrvDataOutputStream = getAsyncOut();
/*  811 */     synchronized (localSrvDataOutputStream)
/*      */     {
/*  813 */       send(localSrvDataOutputStream, new SrvEventToken(paramString));
/*  814 */       if ((paramArrayOfObject != null) && (paramArrayOfObject.length > 0))
/*      */       {
/*  816 */         sendParams(localSrvDataOutputStream, null, paramArrayOfObject, 0);
/*      */       }
/*  818 */       sendDone(localSrvDataOutputStream, 0, false, true, false, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected synchronized SrvDataOutputStream getAsyncOut()
/*      */     throws IOException
/*      */   {
/*  826 */     if (this._asyncNotif == null)
/*      */     {
/*      */       try
/*      */       {
/*  833 */         this._asyncNotif = new SrvDataOutputStream(this._rawOut);
/*  834 */         this._asyncNotif.setBigEndian(this._tdsout.getBigEndian());
/*  835 */         this._asyncNotif.setPacketSize(this._tdsout.getPacketSize());
/*  836 */         this._asyncNotif.setEncoding(this._tdsout.getCharset());
/*  837 */         this._asyncNotif.setNotify();
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*  843 */         throw new IOException(localException.getMessage());
/*      */       }
/*      */     }
/*  846 */     return this._asyncNotif;
/*      */   }
/*      */ 
/*      */   public void sendParams(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, int paramInt)
/*      */     throws IOException
/*      */   {
/*  866 */     sendParams(this._tdsout, paramArrayOfObject1, paramArrayOfObject2, paramInt);
/*      */   }
/*      */ 
/*      */   private void sendParams(SrvDataOutputStream paramSrvDataOutputStream, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, int paramInt)
/*      */     throws IOException
/*      */   {
/*  887 */     if ((paramArrayOfObject1 != null) && (paramArrayOfObject1.length != paramArrayOfObject2.length))
/*      */     {
/*  889 */       throw new SrvTypeException("Number of names do not match number of data elements in results send");
/*      */     }
/*  891 */     Object localObject = null;
/*      */ 
/*  894 */     Object[] arrayOfObject = null;
/*  895 */     if (paramArrayOfObject1 != null)
/*      */     {
/*  897 */       arrayOfObject = paramArrayOfObject1;
/*      */     }
/*      */     else
/*      */     {
/*  901 */       arrayOfObject = paramArrayOfObject2;
/*      */     }
/*      */ 
/*  907 */     if ((arrayOfObject.length * 136 > 65535) && 
/*  913 */       (isWidetableEnabled()))
/*      */     {
/*  915 */       localObject = new SrvParamFormat2Token();
/*      */     }
/*      */ 
/*  918 */     if (localObject == null)
/*      */     {
/*  929 */       localObject = new SrvParamFormatToken();
/*      */     }
/*  931 */     SrvJavaTypeFormatter localSrvJavaTypeFormatter = new SrvJavaTypeFormatter((SrvFormatToken)localObject, getClientCapability());
/*      */ 
/*  935 */     for (int i = 0; i < arrayOfObject.length; ++i)
/*      */     {
/*  937 */       int j = 0;
/*      */ 
/*  939 */       if (arrayOfObject[i] instanceof String)
/*      */       {
/*  941 */         j = ((String)arrayOfObject[i]).length();
/*      */       }
/*      */ 
/*  944 */       localSrvJavaTypeFormatter.addFormat(arrayOfObject[i], null, paramInt, j);
/*      */     }
/*      */ 
/*  947 */     send(paramSrvDataOutputStream, (Token)localObject);
/*  948 */     SrvParamsToken localSrvParamsToken = new SrvParamsToken();
/*  949 */     localSrvJavaTypeFormatter.convertData(localSrvParamsToken, arrayOfObject);
/*  950 */     send(paramSrvDataOutputStream, localSrvParamsToken);
/*      */   }
/*      */ 
/*      */   public void sendDone(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */     throws IOException
/*      */   {
/*  969 */     sendDone(this._tdsout, paramInt, paramBoolean1, paramBoolean2, paramBoolean3, false);
/*      */   }
/*      */ 
/*      */   private void sendDone(SrvDataOutputStream paramSrvDataOutputStream, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
/*      */     throws IOException
/*      */   {
/*  985 */     int i = 0;
/*  986 */     if (paramBoolean1)
/*      */     {
/*  988 */       i |= 2;
/*      */     }
/*  990 */     if (paramBoolean4)
/*      */     {
/*  992 */       i |= 64;
/*      */     }
/*      */ 
/*  995 */     if (paramBoolean2)
/*      */     {
/*  997 */       i |= 0;
/*      */     }
/*      */     else
/*      */     {
/* 1001 */       i |= 1;
/*      */     }
/*      */ 
/* 1004 */     if (paramInt >= 0)
/*      */     {
/* 1006 */       i |= 16;
/*      */     }
/*      */     else
/*      */     {
/* 1010 */       paramInt = 0;
/*      */     }
/* 1012 */     if (paramBoolean3)
/*      */     {
/* 1014 */       if (paramBoolean2)
/*      */       {
/* 1016 */         send(paramSrvDataOutputStream, new SrvDoneToken(i, 0, paramInt));
/*      */       }
/*      */       else
/*      */       {
/* 1022 */         send(paramSrvDataOutputStream, new SrvDoneInProcToken(i, 0, paramInt));
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1029 */       send(paramSrvDataOutputStream, new SrvDoneToken(i, 0, paramInt));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String getCharset()
/*      */   {
/* 1039 */     return Iana.reverseLookupIana(this._encoding);
/*      */   }
/*      */ 
/*      */   public boolean isWidetableEnabled() {
/* 1043 */     return getClientCapability()._reqCaps.get(59) & !getClientCapability()._respCaps.get(45);
/*      */   }
/*      */ 
/*      */   public Object getSessionData(Object paramObject)
/*      */   {
/* 1048 */     return this._sessionData.get(paramObject);
/*      */   }
/*      */ 
/*      */   public void setSessionData(Object paramObject1, Object paramObject2) {
/* 1052 */     this._sessionData.put(paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   protected Socket getSocket() {
/* 1056 */     return this._srvSocket;
/*      */   }
/*      */ 
/*      */   protected TdsOutputStream getOutputStream()
/*      */   {
/* 1066 */     return this._tdsout;
/*      */   }
/*      */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.SrvSession
 * JD-Core Version:    0.5.4
 */