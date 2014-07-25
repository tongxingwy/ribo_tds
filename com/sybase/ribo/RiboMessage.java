/*     */ package com.sybase.ribo;
/*     */ 
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ public class RiboMessage
/*     */ {
/*     */   public static final String RIBO_USAGE = "RiboUsage";
/*     */   public static final String RIBO_USAGE_UNEXPECTED_PARM = "RiboUsageUnexpected";
/*     */   public static final String RIBO_USAGE_EXPECTED_PARM_MISSING = "RiboUsageExpectedParmMissing";
/*     */   public static final String RIBO_USAGE_INVALID_FLAG = "RiboUsageInvalidFlag";
/*     */   public static final String RIBO_VERSION = "RiboVersion";
/*     */   public static final String ERR_BAD_COOKIE = "BadCookie";
/*     */   public static final String ERR_BAD_COOKIE_MESSAGE = "BadCookieMessage";
/*     */   public static final String INVALID_SESSION_SETTING = "InvalidSessionSetting";
/*     */   public static final String CAPTURE_INPUT_FILE_DOES_NOT_EXIST = "CaptureInputFileDoesNotExist";
/*     */   public static final String CAPTURE_INPUT_FILE_IS_NOT_READABLE = "CaptureInputFileIsNotReadable";
/*     */   public static final String IO_EXCEPTION = "IOException";
/*     */   public static final String EXCEPTION = "Exception";
/*     */   public static final String LISTENING_ON_PORT = "ListeningOnPort";
/*     */   public static final String PRESS_TO_QUIT = "PressToQuit";
/*     */   public static final String ACCEPTING_CONNECTION_FROM = "AcceptingConnectionFrom";
/*     */   public static final String SENDING_TRAFFIC_TO = "SendingTrafficTo";
/*     */   public static final String STOPPING_ACCEPT_THREAD = "StoppingAcceptThread";
/*     */   public static final String ERR_SHUTTING_DOWN_SERVER_CONNECTION = "ShuttingDownServerConnection";
/*     */   public static final String ERR_SHUTTING_DOWN_CLIENT_CONNECTION = "ShuttingDownClientConnection";
/*     */   public static final String ERR_SHUTTING_DOWN_ANALYZER = "ShuttingDownAnalyzer";
/*     */   public static final String CLOSING_DUMP_FILE = "ClosingDumpFile";
/*     */   public static final String ABOUT_RIBO = "AboutRibo";
/*     */   public static final String PARTIAL_READ_FAILED = "PartialReadFailed";
/*     */   public static final String INVALID_RECORD_HEADER = "InvalidRecordHeader";
/*     */   public static final String INVALID_CAPTURE_FILE = "InvalidCaptureFile";
/*     */   public static final String LISTEN_PORT = "ListenPort";
/*     */   public static final String SERVER_HOST = "ServerHost";
/*     */   public static final String SERVER_PORT = "ServerPort";
/*     */   public static final String START_CAPTURE = "StartCapture";
/*     */   public static final String STOP_CAPTURE = "StopCapture";
/*     */   public static final String FILE = "File";
/*     */   public static final String PREFERENCES = "Preferences";
/*     */   public static final String EDITFILTER = "EditFilter";
/*     */   public static final String EXIT = "Exit";
/*     */   public static final String HELP = "Help";
/*     */   public static final String ABOUT = "About";
/*     */   public static final String OK = "OK";
/*     */   public static final String CANCEL = "Cancel";
/*     */   public static final String CLOSE = "Close";
/*     */   public static final String CAPTURE_FILE_PREFIX = "CaptureFilePrefix";
/*     */   public static final String TRANSLATE_FILE_PREFIX = "TranlateFilePrefix";
/*     */   public static final String TRANSLATION_FILTER = "TranslationFilter";
/*     */   public static final String TRANSLATE_FILE = "TranslateFile";
/*     */   public static final String DISPLAY_TRANSLATION = "DisplayTranslation";
/*     */   public static final String QUIT_KEY = "QuitKey";
/*     */   public static final String WRONG_CLASS_TYPE_SESSION_SETTING = "InvalidClassTypeSessionSetting";
/*     */   public static final String EDITFILTER_LABEL = "EditFilterLabel";
/*     */   public static final String SAVE_CHANGES = "SaveChanges";
/*     */   public static final String DUMP_TOKEN_EXCEPTION = "DumpTokenException";
/*     */   public static final String READ_TOKEN_EXCEPTION = "ReadTokenException";
/*     */   public static final String DUMP_HEADER_EXCEPTION = "DumpHeaderException";
/*     */   public static final String UNTITLED = "Untitled";
/*     */   public static final String TOKENS = "Tokens";
/*     */   public static final String DETAILS = "Details";
/*     */   public static final String DATA_CONTINUED_IN_NEXT_FILE = "DataContinuedInNextFile";
/*     */   public static final String TOKEN_CONTINUED_IN_NEXT_FILE = "TokenContinuedInNextFile";
/*     */   public static final String NO_FILTER_SPECIFIED = "NoFilterSpecified";
/*     */   public static final String PDU_PACKET_NOT_DUMPED = "PduPacketNotDumped";
/*     */   public static final String CMDSEQ_PROTOCOL_PACKET_NOT_DUMPED = "CmdSeqProtocolPacketNotDumped";
/*     */   public static final String UNRECOGNIZED_PACKET_TYPE = "UnrecognizedPacketType";
/*     */   public static final String ERROR_PROCESSING_TDS = "ErrorProcessingTds";
/*     */   public static final String END = "End";
/*     */   public static final String TOKEN_CHARACTER_SET_INFO_USED = "TokenCharacterSetInfoUsed";
/*     */   public static final String NO_ENCODING_INFO_AVAILABLE = "NoEncodingInfoAvailable";
/*     */   public static final String TOKEN_DOESNT_KNOW_ITS_TYPE = "TokenDoesntKnowItsType";
/*     */   private static ResourceBundle _messages;
/*     */ 
/*     */   protected static String getMessage(String key)
/*     */   {
/* 116 */     if (_messages == null)
/*     */     {
/*     */       try
/*     */       {
/* 120 */         _messages = ResourceBundle.getBundle("com.sybase.ribo.resource.Messages");
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 125 */         e.printStackTrace();
/* 126 */         return null;
/*     */       }
/*     */     }
/*     */     String message;
/*     */     String message;
/*     */     try {
/* 132 */       message = _messages.getString(key);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 137 */       message = "Internal error, missing message for: " + key + " exception: " + e.toString();
/*     */     }
/*     */ 
/* 140 */     return message;
/*     */   }
/*     */ 
/*     */   public static void raiseError(String error)
/*     */     throws RiboException
/*     */   {
/* 149 */     RiboException re = new RiboException(getMessage(error));
/*     */ 
/* 151 */     throw re;
/*     */   }
/*     */ 
/*     */   public static void raiseError(String error, String arg1)
/*     */     throws RiboException
/*     */   {
/* 160 */     RiboException re = new RiboException(makeMessage(error, arg1));
/*     */ 
/* 162 */     throw re;
/*     */   }
/*     */ 
/*     */   public static void raiseError(String error, String arg1, String arg2)
/*     */     throws RiboException
/*     */   {
/* 171 */     RiboException re = new RiboException(makeMessage(error, arg1, arg2));
/*     */ 
/* 173 */     throw re;
/*     */   }
/*     */ 
/*     */   public static void raiseError(String error, String arg1, String arg2, String arg3)
/*     */     throws RiboException
/*     */   {
/* 182 */     RiboException re = new RiboException(makeMessage(error, arg1, arg2, arg3));
/*     */ 
/* 184 */     throw re;
/*     */   }
/*     */ 
/*     */   public static void raiseError(String error, String arg1, String arg2, String arg3, String arg4)
/*     */     throws RiboException
/*     */   {
/* 193 */     RiboException re = new RiboException(makeMessage(error, arg1, arg2, arg3, arg4));
/*     */ 
/* 195 */     throw re;
/*     */   }
/*     */ 
/*     */   public static void raiseError(String error, String arg1, String arg2, String arg3, String arg4, String arg5)
/*     */     throws RiboException
/*     */   {
/* 205 */     RiboException re = new RiboException(makeMessage(error, arg1, arg2, arg3, arg4, arg5));
/*     */ 
/* 207 */     throw re;
/*     */   }
/*     */ 
/*     */   protected static String makeMessage(String key, String arg1)
/*     */   {
/* 217 */     String message = getMessage(key);
/* 218 */     message = cookieReplace(key, message, 1, arg1);
/* 219 */     return message;
/*     */   }
/*     */ 
/*     */   protected static String makeMessage(String key, String arg1, String arg2)
/*     */   {
/* 228 */     String message = getMessage(key);
/* 229 */     message = cookieReplace(key, message, 1, arg1);
/* 230 */     message = cookieReplace(key, message, 2, arg2);
/* 231 */     return message;
/*     */   }
/*     */ 
/*     */   protected static String makeMessage(String key, String arg1, String arg2, String arg3)
/*     */   {
/* 241 */     String message = getMessage(key);
/* 242 */     message = cookieReplace(key, message, 1, arg1);
/* 243 */     message = cookieReplace(key, message, 2, arg2);
/* 244 */     message = cookieReplace(key, message, 3, arg3);
/* 245 */     return message;
/*     */   }
/*     */ 
/*     */   protected static String makeMessage(String key, String arg1, String arg2, String arg3, String arg4)
/*     */   {
/* 255 */     String message = getMessage(key);
/* 256 */     message = cookieReplace(key, message, 1, arg1);
/* 257 */     message = cookieReplace(key, message, 2, arg2);
/* 258 */     message = cookieReplace(key, message, 3, arg3);
/* 259 */     message = cookieReplace(key, message, 4, arg4);
/* 260 */     return message;
/*     */   }
/*     */ 
/*     */   protected static String makeMessage(String key, String arg1, String arg2, String arg3, String arg4, String arg5)
/*     */   {
/* 270 */     String message = getMessage(key);
/* 271 */     message = cookieReplace(key, message, 1, arg1);
/* 272 */     message = cookieReplace(key, message, 2, arg2);
/* 273 */     message = cookieReplace(key, message, 3, arg3);
/* 274 */     message = cookieReplace(key, message, 4, arg4);
/* 275 */     message = cookieReplace(key, message, 5, arg5);
/* 276 */     return message;
/*     */   }
/*     */ 
/*     */   private static String cookieReplace(String key, String message, int argNumber, String argument)
/*     */   {
/* 285 */     String cookie = "%" + argNumber + "s";
/* 286 */     int startIndex = message.indexOf(cookie);
/* 287 */     String newMessage = message;
/* 288 */     if (startIndex == -1)
/*     */     {
/* 292 */       if (newMessage.indexOf(getMessage("BadCookieMessage")) == -1)
/*     */       {
/* 296 */         newMessage = newMessage.concat(getMessage("BadCookieMessage"));
/*     */       }
/*     */ 
/* 299 */       newMessage = newMessage.concat(makeMessage("BadCookie", "" + argNumber, argument));
/* 300 */       return newMessage;
/*     */     }
/* 302 */     int endIndex = startIndex + cookie.length();
/* 303 */     newMessage = message.substring(0, startIndex) + argument + message.substring(endIndex);
/*     */ 
/* 305 */     return newMessage;
/*     */   }
/*     */ }

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.ribo.RiboMessage
 * JD-Core Version:    0.5.4
 */