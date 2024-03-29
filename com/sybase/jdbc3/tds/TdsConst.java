package com.sybase.jdbc3.tds;

public abstract interface TdsConst
{
  public static final int BUF_LANG = 1;
  public static final int BUF_LOGIN = 2;
  public static final int BUF_RPC = 3;
  public static final int BUF_RESPONSE = 4;
  public static final int BUF_UNFMT = 5;
  public static final int BUF_ATTN = 6;
  public static final int BUF_BULK = 7;
  public static final int BUF_SETUP = 8;
  public static final int BUF_CLOSE = 9;
  public static final int BUF_ERROR = 10;
  public static final int BUF_PROTACK = 11;
  public static final int BUF_ECHO = 12;
  public static final int BUF_LOGOUT = 13;
  public static final int BUF_ENDPARAM = 14;
  public static final int BUF_NORMAL = 15;
  public static final int BUF_URGENT = 16;
  public static final int BUF_MIGRATE = 17;
  public static final int BUF_CMDSEQ_NORMAL = 24;
  public static final int BUF_CMDSEQ_LOGIN = 25;
  public static final int BUF_CMDSEQ_LIVENESS = 26;
  public static final int BUF_CMDSEQ_RESERVED1 = 27;
  public static final int BUF_CMDSEQ_RESERVED2 = 28;
  public static final int BUFSTAT_BEGIN = 0;
  public static final int BUFSTAT_EOM = 1;
  public static final int BUFSTAT_ATTNACK = 2;
  public static final int BUFSTAT_ATTN = 4;
  public static final int BUFSTAT_EVENT = 8;
  public static final int BUFSTAT_SEALED = 16;
  public static final int NETHDRSIZE = 8;
  public static final int TIMESTAMPSIZE = 8;
  public static final int MIN_DATETIME_YEAR = 1753;
  public static final int MAX_DATETIME_YEAR = 9999;
  public static final int MIN_DATE_YEAR = 1;
  public static final int MAX_DATE_YEAR = 9999;
  public static final int EED = 229;
  public static final int LOGINACK = 173;
  public static final int LOGOUT = 113;
  public static final int ENVCHANGE = 227;
  public static final int ERROR = 170;
  public static final int INFO = 171;
  public static final int LANGUAGE = 33;
  public static final int RPC = 224;
  public static final int DBRPC = 230;
  public static final int DYNAMIC = 231;
  public static final int DYNAMIC2 = 98;
  public static final int MSG = 101;
  public static final int EVENT_NOTICE = 162;
  public static final int ROW = 209;
  public static final int ROWFMT = 238;
  public static final int ROWFMT2 = 97;
  public static final int ALTFMT = 168;
  public static final int PARAMS = 215;
  public static final int PARAMFMT = 236;
  public static final int PARAMFMT2 = 32;
  public static final int RETURN_VALUE = 172;
  public static final int RETURN_STATUS = 121;
  public static final int CAPABILITY = 226;
  public static final int CONTROL = 174;
  public static final int DONE = 253;
  public static final int DONEPROC = 254;
  public static final int DONEINPROC = 255;
  public static final int CURCLOSE = 128;
  public static final int CURDELETE = 129;
  public static final int CURFETCH = 130;
  public static final int CURINFO = 131;
  public static final int CURINFO3 = 136;
  public static final int CUROPEN = 132;
  public static final int CURUPDATE = 133;
  public static final int CURDECLARE = 134;
  public static final int CURDECLARE2 = 35;
  public static final int CURDECLARE3 = 16;
  public static final int KEY = 202;
  public static final int DEBUG_CMD = 96;
  public static final int ORDERBY = 169;
  public static final int ORDERBY2 = 34;
  public static final int ALTNAME = 167;
  public static final int ALTROW = 211;
  public static final int COLINFO = 165;
  public static final int OFFSET = 120;
  public static final int OPTIONCMD = 166;
  public static final int TABNAME = 164;
  public static final int PACKETSIZE = 512;
  public static final int ALT_AVG = 79;
  public static final int ALT_COUNT = 75;
  public static final int ALT_MAX = 82;
  public static final int ALT_MIN = 81;
  public static final int ALT_SUM = 77;
  public static final int ALT_COUNTBG = 97;
  public static final int BINARY = 45;
  public static final int BIT = 50;
  public static final int CHAR = 47;
  public static final int DATETIME = 61;
  public static final int SHORTDATE = 58;
  public static final int DATETIMN = 111;
  public static final int DATE = 49;
  public static final int DATEN = 123;
  public static final int TIME = 51;
  public static final int TIMEN = 147;
  public static final int INTERVAL = 46;
  public static final int DECN = 106;
  public static final int DECML = 55;
  public static final int NUME = 63;
  public static final int FLT4 = 59;
  public static final int FLT8 = 62;
  public static final int FLTN = 109;
  public static final int IMAGE = 34;
  public static final int INT1 = 48;
  public static final int INT2 = 52;
  public static final int INT4 = 56;
  public static final int INTN = 38;
  public static final int LONGBINARY = 225;
  public static final int LONGCHAR = 175;
  public static final int MONEY = 60;
  public static final int SHORTMONEY = 122;
  public static final int MONEYN = 110;
  public static final int NUMN = 108;
  public static final int TEXT = 35;
  public static final int UNITEXT = 174;
  public static final int VARBINARY = 37;
  public static final int SENSITIVITY = 103;
  public static final int BOUNDARY = 104;
  public static final int INT8 = 191;
  public static final int UINT2 = 65;
  public static final int UINT4 = 66;
  public static final int UINT8 = 67;
  public static final int UINTN = 68;
  public static final int BIGDATETIMEN = 187;
  public static final int BIGTIMEN = 188;
  public static final int VARCHAR = 39;
  public static final int BLOB = 36;
  public static final int VOID = 31;
  public static final int UNKNOWN = 255;
  public static final int JAVA_OBJECT1 = 1;
  public static final int JAVA_OBJECT2 = 2;
  public static final int BLOB_VARCHAR = 3;
  public static final int BLOB_VARBINARY = 4;
  public static final int BLOB_UTF16 = 5;
  public static final int LOCATOR_TEXT = 6;
  public static final int LOCATOR_IMAGE = 7;
  public static final int LOCATOR_UNITEXT = 8;
  public static final int JAVAOBJECT = 9217;
  public static final int STREAMCHAR = 9219;
  public static final int STREAMCHAR_UTF16 = 9221;
  public static final int STREAMBIN = 9220;
  public static final int NATIVE = 0;
  public static final int JAVA_SERIALIZATION = 1;
  public static final int UNICHAR = 34;
  public static final int UNIVARCHAR = 35;
  public static final int NCHAR = 24;
  public static final int NVARCHAR = 25;
  public static final int TDSBIT = 16;
  public static final int USERTYPE_TEXT = 19;
  public static final int USERTYPE_IMAGE = 20;
  public static final int USERTYPE_UNITEXT = 36;
  public static final int OPT_SET = 1;
  public static final int OPT_TEXTSIZE = 2;
  public static final int OPT_ROWCOUNT = 5;
  public static final int OPT_ISOLATION = 8;
  public static final int OPT_CHAINXACTS = 25;
  public static final int OPT_QUOTED_IDENT = 35;
  public static final int OPT_LOB_LOCATOR = 49;
  public static final int DONE_FINAL = 0;
  public static final int DONE_MORE = 1;
  public static final int DONE_ERROR = 2;
  public static final int DONE_INXACT = 4;
  public static final int DONE_PROC = 8;
  public static final int DONE_COUNT = 16;
  public static final int DONE_ATTN = 32;
  public static final int DONE_EVENT = 64;
  public static final int TRAN_PROGRESS = 2;
  public static final int NOTIFY_NOWAIT = 64;
  public static final int PARAM_NULLALLOWED = 32;
  public static final int PARAM_COLUMNSTATUS = 8;
  public static final int PARAM_RETURN = 1;
  public static final int NO_EED = 0;
  public static final int EED_FOLLOWS = 1;
  public static final int EED_INFO = 2;
  public static final int REDIRECT = 1;
  public static final int EED_IMMEDIATE_REDIRECT = 1;
  public static final int EED_SET_REDIRECT = 2;
  public static final int HADR_FAILOVER = 2376;
  public static final int HADR_DEACTIVATED = 2379;
  public static final int ROW_HIDDEN = 1;
  public static final int ROW_KEY = 2;
  public static final int ROW_VERSION = 4;
  public static final int ROW_COLUMNSTATUS = 8;
  public static final int ROW_UPDATABLE = 16;
  public static final int ROW_NULLALLOWED = 32;
  public static final int ROW_IDENTITY = 64;
  public static final int DATA_NORMAL = 0;
  public static final int DATA_NULL = 1;
  public static final int DATA_ZERO_LENGTH_TEXT_IMAGE = 2;
  public static final int MSG_HASARGS = 1;
  public static final int MSG_SEC_ENCRYPT = 1;
  public static final int MSG_SEC_LOGPWD = 2;
  public static final int MSG_SEC_REMPWD = 3;
  public static final int MSG_SEC_CHALLENGE = 4;
  public static final int MSG_SEC_RESPONSE = 5;
  public static final int MSG_SEC_GETLABEL = 6;
  public static final int MSG_SEC_LABEL = 7;
  public static final int MSG_SEC_OPAQUE = 11;
  public static final int MSG_HAFAILOVER = 12;
  public static final int MSG_EMPTY = 13;
  public static final int MSG_SEC_ENCRYPT2 = 14;
  public static final int MSG_SEC_LOGPWD2 = 15;
  public static final int MSG_SEC_SUP_CIPHER = 16;
  public static final int MSG_SEC_REMPWD2 = 22;
  public static final int MSG_SEC_ENCRYPT3 = 30;
  public static final int MSG_SEC_LOGPWD3 = 31;
  public static final int MSG_SEC_REMPWD3 = 32;
  public static final int MSG_MIG_REQ = 17;
  public static final int MSG_MIG_SYNC = 18;
  public static final int MSG_MIG_CONT = 19;
  public static final int MSG_MIG_IGN = 20;
  public static final int MSG_MIG_FAIL = 21;
  public static final int MSG_MIG_RESUME = 23;
  public static final int MSG_DR_MAP = 33;
  public static final int SEC_SECSESS = 1;
  public static final int SEC_FORWARD = 2;
  public static final int SEC_SIGN = 3;
  public static final int SEC_OTHER = 4;
  public static final int SEC_LOG_ENCRYPT = 1;
  public static final int SEC_LOG_CHALLENGE = 2;
  public static final int SEC_LOG_LABELS = 4;
  public static final int SEC_LOG_APPDEFINED = 8;
  public static final int SEC_LOG_SECSESSION = 16;
  public static final int SEC_LOG_ENCRYPT2 = 32;
  public static final int SEC_LOG_ENCRYPT3 = 128;
  public static final int TDS_SEC_VERSION = 50;
  public static final int COMMAND_STATUS_UNUSED = 0;
  public static final int DYN_HASARGS = 1;
  public static final int DYN_SUPPRESS_ROWFMT = 2;
  public static final int DYN_BATCH_PARAMS = 4;
  public static final int DYN_SUPPRESS_PARAMFMT = 8;
  public static final int LANG_HASARGS = 1;
  public static final int LANG_BATCH_PARAMS = 4;
  public static final int CUR_RDONLY = 1;
  public static final int CUR_UPDATABLE = 2;
  public static final int CUR_DYNAMIC = 8;
  public static final int CUR_SENSITIVE = 4;
  public static final int CUR_INSENSITIVE = 32;
  public static final int CUR_SEMISENSITIVE = 64;
  public static final int CUR_KEYSETDRIVEN = 128;
  public static final int CUR_SCROLLABLE = 256;
  public static final int CUR_RELLOCKSONCLOSE = 512;
  public static final int CUR_UNUSED = 0;
  public static final int CUR_HASARGS = 1;
  public static final int CUR_DEALLOC = 1;
  public static final int CUR_NEXT = 1;
  public static final int CUR_PREV = 2;
  public static final int CUR_FIRST = 3;
  public static final int CUR_LAST = 4;
  public static final int CUR_ABS = 5;
  public static final int CUR_REL = 6;
  public static final int CUR_SETCURROWS = 1;
  public static final int CUR_INQUIRE = 2;
  public static final int CUR_INFORM = 3;
  public static final int CUR_LISTALL = 4;
  public static final int CUR_IS_DECLARED = 1;
  public static final int CUR_IS_OPEN = 2;
  public static final int CUR_IS_CLOSED = 4;
  public static final int CUR_IS_RDONLY = 8;
  public static final int CUR_IS_UPDATABLE = 16;
  public static final int CUR_IS_ROWCNT = 32;
  public static final int CUR_IS_DALLOC = 64;
  public static final int CUR_IS_SCROLLABLE = 128;
  public static final int CUR_IS_IMPLICIT = 256;
  public static final int CUR_IS_SENSITIVE = 512;
  public static final int CUR_IS_INSENSITIVE = 1024;
  public static final int CUR_IS_SEMISENSITIVE = 2048;
  public static final int CUR_IS_KEYSETDRIVEN = 4096;
  public static final int RPC_UNUSED = 0;
  public static final int RPC_RECOMPILE = 1;
  public static final int RPC_PARAMS = 2;
  public static final int CAP_REQUEST = 1;
  public static final int CAP_RESPONSE = 2;
  public static final int REQ_LANG = 1;
  public static final int REQ_RPC = 2;
  public static final int REQ_EVT = 3;
  public static final int REQ_MSTMT = 4;
  public static final int REQ_BCP = 5;
  public static final int REQ_CURSOR = 6;
  public static final int REQ_DYNF = 7;
  public static final int REQ_MSG = 8;
  public static final int REQ_PARAM = 9;
  public static final int DATA_INT1 = 10;
  public static final int DATA_INT2 = 11;
  public static final int DATA_INT4 = 12;
  public static final int DATA_BIT = 13;
  public static final int DATA_CHAR = 14;
  public static final int DATA_VCHAR = 15;
  public static final int DATA_BIN = 16;
  public static final int DATA_VBIN = 17;
  public static final int DATA_MNY8 = 18;
  public static final int DATA_MNY4 = 19;
  public static final int DATA_DATE8 = 20;
  public static final int DATA_DATE4 = 21;
  public static final int DATA_FLT4 = 22;
  public static final int DATA_FLT8 = 23;
  public static final int DATA_NUM = 24;
  public static final int DATA_TEXT = 25;
  public static final int DATA_IMAGE = 26;
  public static final int DATA_DEC = 27;
  public static final int DATA_LCHAR = 28;
  public static final int DATA_LBIN = 29;
  public static final int DATA_INTN = 30;
  public static final int DATA_DATETIMEN = 31;
  public static final int DATA_MONEYN = 32;
  public static final int CSR_PREV = 33;
  public static final int CSR_FIRST = 34;
  public static final int CSR_LAST = 35;
  public static final int CSR_ABS = 36;
  public static final int CSR_REL = 37;
  public static final int CSR_MULTI = 38;
  public static final int CON_OOB = 39;
  public static final int CON_INBAND = 40;
  public static final int CON_LOGICAL = 41;
  public static final int PROTO_TEXT = 42;
  public static final int PROTO_BULK = 43;
  public static final int REQ_URGEVT = 44;
  public static final int DATA_SENSITIVITY = 45;
  public static final int DATA_BOUNDARY = 46;
  public static final int PROTO_DYNAMIC = 47;
  public static final int PROTO_DYNPROC = 48;
  public static final int DATA_FLTN = 49;
  public static final int DATA_BITN = 50;
  public static final int DATA_INT8 = 51;
  public static final int DATA_VOID = 52;
  public static final int DOL_BULK = 53;
  public static final int OBJECT_JAVA1 = 54;
  public static final int OBJECT_CHAR = 55;
  public static final int REQ_RESERVED1 = 56;
  public static final int OBJECT_BINARY = 57;
  public static final int DATA_COLUMNSTATUS = 58;
  public static final int WIDETABLE = 59;
  public static final int REQ_RESERVED2 = 60;
  public static final int DATA_UINT2 = 61;
  public static final int DATA_UINT4 = 62;
  public static final int DATA_UINT8 = 63;
  public static final int DATA_UINTN = 64;
  public static final int CUR_IMPLICIT = 65;
  public static final int DATA_NLBIN = 66;
  public static final int IMAGE_NCHAR = 67;
  public static final int BLOB_NCHAR_16 = 68;
  public static final int BLOB_NCHAR_8 = 69;
  public static final int BLOB_NCHAR_SCSU = 70;
  public static final int DATA_DATE = 71;
  public static final int DATA_TIME = 72;
  public static final int DATA_INTERVAL = 73;
  public static final int CSR_SCROLL = 74;
  public static final int CSR_SENSITIVE = 75;
  public static final int CSR_INSENSITIVE = 76;
  public static final int CSR_SEMISENSITIVE = 77;
  public static final int CSR_KEYSETDRIVEN = 78;
  public static final int REQ_SRVPKTSIZE = 79;
  public static final int DATA_UNITEXT = 80;
  public static final int CAP_CLUSTERFAILOVER = 81;
  public static final int DATA_SINT1 = 82;
  public static final int REQ_LARGEIDENT = 83;
  public static final int REQ_BLOB_NCHAR_16 = 84;
  public static final int DATA_XML = 85;
  public static final int REQ_CURINFO3 = 86;
  public static final int REQ_DBRPC2 = 87;
  public static final int REQ_UNUSED = 88;
  public static final int REQ_MIGRATE = 89;
  public static final int MULTI_REQUESTS = 90;
  public static final int REQ_RESERVED_91 = 91;
  public static final int REQ_RESERVED_92 = 92;
  public static final int DATA_BIGDATETIME = 93;
  public static final int DATA_USECS = 94;
  public static final int RPCPARAM_LOB = 95;
  public static final int REQ_INSTID = 96;
  public static final int REQ_GRID = 97;
  public static final int REQ_DYN_BATCH = 98;
  public static final int REQ_LANG_BATCH = 99;
  public static final int REQ_RPC_BATCH = 100;
  public static final int DATA_LOBLOCATOR = 101;
  public static final int ROWCOUNT_FOR_SELECT = 102;
  public static final int REQ_LOGPARAMS = 103;
  public static final int REQ_DYNAMIC_SUPPRESS_PARAMFMT = 104;
  public static final int REQ_READONLY = 105;
  public static final int MIN_REQ_CAP = 1;
  public static final int MAX_REQ_CAP = 105;
  public static final int MIN_REQ_CAP_V605 = 1;
  public static final int MAX_REQ_CAP_V605 = 90;
  public static final int MIN_REQ_CAP_V6 = 1;
  public static final int MAX_REQ_CAP_V6 = 73;
  public static final int MAX_OLD_REQ_CAP = 52;
  public static final int MAX_MEDIUM_REQ_CAP = 57;
  public static final int RES_NOMSG = 1;
  public static final int RES_NOEED = 2;
  public static final int RES_NOPARAM = 3;
  public static final int DATA_NOINT1 = 4;
  public static final int DATA_NOINT2 = 5;
  public static final int DATA_NOINT4 = 6;
  public static final int DATA_NOBIT = 7;
  public static final int DATA_NOCHAR = 8;
  public static final int DATA_NOVCHAR = 9;
  public static final int DATA_NOBIN = 10;
  public static final int DATA_NOVBIN = 11;
  public static final int DATA_NOMNY8 = 12;
  public static final int DATA_NOMNY4 = 13;
  public static final int DATA_NODATE8 = 14;
  public static final int DATA_NODATE4 = 15;
  public static final int DATA_NOFLT4 = 16;
  public static final int DATA_NOFLT8 = 17;
  public static final int DATA_NONUM = 18;
  public static final int DATA_NOTEXT = 19;
  public static final int DATA_NOIMAGE = 20;
  public static final int DATA_NODEC = 21;
  public static final int DATA_NOLCHAR = 22;
  public static final int DATA_NOLBIN = 23;
  public static final int DATA_NOINTN = 24;
  public static final int DATA_NODATETIMEN = 25;
  public static final int DATA_NOMONEYN = 26;
  public static final int CON_NOOOB = 27;
  public static final int CON_NOINBAND = 28;
  public static final int PROTO_NOTEXT = 29;
  public static final int PROTO_NOBULK = 30;
  public static final int DATA_NOSENSITIVITY = 31;
  public static final int DATA_NOBOUNDARY = 32;
  public static final int RES_NOTDSDEBUG = 33;
  public static final int RES_NOSTRIPBLANKS = 34;
  public static final int DATA_NOINT8 = 35;
  public static final int OBJECT_NOJAVA1 = 36;
  public static final int OBJECT_NOCHAR = 37;
  public static final int DATA_NOCOLUMNSTATUS = 38;
  public static final int OBJECT_NOBINARY = 39;
  public static final int RES_RESERVED1 = 40;
  public static final int DATA_NOUINT2 = 41;
  public static final int DATA_NOUINT4 = 42;
  public static final int DATA_NOUINT8 = 43;
  public static final int DATA_NOUINTN = 44;
  public static final int NO_WIDETABLES = 45;
  public static final int DATA_NONLBIN = 46;
  public static final int IMAGE_NONCHAR = 47;
  public static final int BLOB_NONCHAR_16 = 48;
  public static final int BLOB_NONCHAR_8 = 49;
  public static final int BLOB_NONCHAR_SCSU = 50;
  public static final int DATA_NODATE = 51;
  public static final int DATA_NOTIME = 52;
  public static final int DATA_NOINTERVAL = 53;
  public static final int DATA_NOUNITEXT = 54;
  public static final int DATA_NOSINT1 = 55;
  public static final int RES_NOLARGEIDENT = 56;
  public static final int RES_NOBLOB_NCHAR_16 = 57;
  public static final int NO_SRVPKTSIZE = 58;
  public static final int RES_NODATA_XML = 59;
  public static final int NONINT_RETURN_VALUE = 60;
  public static final int RES_NOXNLDATA = 61;
  public static final int RES_SUPPRESS_FMT = 62;
  public static final int RES_SUPPRESS_DONEINPROC = 63;
  public static final int RES_FORCE_ROWFMT2 = 64;
  public static final int DATA_NOBIGDATETIME = 65;
  public static final int DATA_NOUSECS = 66;
  public static final int RES_NO_TDSCONTROL = 67;
  public static final int RPCPARAM_NOLOB = 68;
  public static final int DATA_NOLOBLOCATOR = 69;
  public static final int NOROWCOUNT_FOR_SELECT = 70;
  public static final int RES_CUMULATIVE_DONE = 71;
  public static final int RES_LIST_DR_MAP = 72;
  public static final int RES_DR_NOKILL = 73;
  public static final int MIN_RES_CAP = 1;
  public static final int MAX_RES_CAP = 73;
  public static final int MIN_RES_CAP_V605 = 1;
  public static final int MAX_RES_CAP_V605 = 59;
  public static final int MIN_RES_CAP_V6 = 1;
  public static final int MAX_RES_CAP_V6 = 52;
  public static final int MAX_OLD_RES_CAP = 35;
  public static final int MAX_MEDIUM_RES_CAP = 39;
  public static final int INT4_LSB_HI = 0;
  public static final int INT4_LSB_LO = 1;
  public static final int INT2_LSB_HI = 2;
  public static final int INT2_LSB_LO = 3;
  public static final int FLT_IEEE_HI = 4;
  public static final int FLT_IEEE_LO = 10;
  public static final int FLT4_IEEE_HI = 12;
  public static final int FLT4_IEEE_LO = 13;
  public static final int TWO_I4_LSB_HI = 8;
  public static final int TWO_I4_LSB_LO = 9;
  public static final int TWO_I2_LSB_HI = 16;
  public static final int TWO_I2_LSB_LO = 17;
  public static final int LOG_SUCCEED = 5;
  public static final int LOG_FAIL = 6;
  public static final int LOG_NEGOTIATE = 7;
  public static final int HA_LOG_SESSION = 1;
  public static final int HA_LOG_RESUME = 2;
  public static final int HA_LOG_FAILOVERSRV = 4;
  public static final int HA_LOG_REDIRECT = 8;
  public static final int HA_LOG_MIGRATE = 16;
  public static final int HA_SESSION_LENGTH = 6;
  public static final int LOG_SECSESS_ACK = 128;
  public static final int VERSIZE = 4;
  public static final int ENV_DB = 1;
  public static final int ENV_LANG = 2;
  public static final int ENV_CHARSET = 3;
  public static final int ENV_PACKETSIZE = 4;
  public static final int CANCEL_TIMEOUT = 20000;
  public static final int NONE = -1;
  public static final int LOW_BYTE = 255;
  public static final String OPEN_SERVER_PROGRAM_NAME = "OpenServer";
  public static final String SQL_SERVER_PROGRAM_NAME = "sql server";
  public static final String ASE_SERVER_PROGRAM_NAME = "ASE";
  public static final String SA_SERVER_PROGRAM_NAME = "SQL Anywhere";
  public static final String IQ_SERVER_PROGRAM_NAME = "Sybase IQ";
  public static final int DTMNOCOOR = 0;
  public static final int DTMSYB2PC = 1;
  public static final int DTMASTC = 2;
  public static final int DTMXA = 3;
  public static final int DTMDTC = 4;
  public static final int DTMOTS = 5;
  public static final int DTM001 = 196608;
  public static final int SYB2PCV0 = 65536;
  public static final int USER_ALLOWED_XA = 1;
  public static final int TXN_ALLOWED_MIGRATE = 2;
  public static final int MULT_CONN_TXN = 4;
  public static final int XACTRV_RM_ERR = -1;
  public static final int XACTRV_NOXID_ERR = -2;
  public static final int XACTRV_DUPXID_ERR = -3;
  public static final int XACTRV_BUSY_ERR = -4;
  public static final int XACTRV_INVLDPRM_ERR = -5;
  public static final int XACTRV_NOPERM_ERR = -6;
  public static final int XACTRV_PROTO_ERR = -128;
  public static final int XACTRV_COMMITTED_READONLY = -256;
  public static final int XACTFL_NOFLAG = 0;
  public static final int XACTFL_ABORT = 1;
  public static final int XACTFL_NOWAIT = 2;
  public static final int XACTFL_DONE = 4;
  public static final int XACTFL_WITHATTACH = 16;
  public static final int XACTFL_ONE_PHASE_COMMIT = 32;
  public static final int XACTFL_WITHDETACH = 8192;
  public static final int XACTST_NOSTATUS = 0;
  public static final int XA_DTM_STAT_ATTACH = 1;
  public static final int XA_DTM_STAT_DETACH = 2;
  public static final int XA_DTM_STAT_SUSPEND = 3;
  public static final int XA_DTM_STAT_PREPARED = 7;
  public static final int XA_DTM_STAT_COMMIT = 100;
  public static final int XA_DTM_STAT_ABORT = 101;
  public static final int XA_DTM_STAT_FORGET = 102;
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.tds.TdsConst
 * JD-Core Version:    0.5.4
 */