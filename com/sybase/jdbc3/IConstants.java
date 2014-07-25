package com.sybase.jdbc3;

public abstract interface IConstants
{
  public static final byte STATEMENT_EXECUTION_MODE_DYNAMIC = 0;
  public static final byte STATEMENT_EXECUTION_MODE_PARAMETERIZED = 1;
  public static final byte STATEMENT_EXECUTION_MODE_LANGUAGE = 2;
  public static final byte STATEMENT_EXECUTION_MODE_DBRPC = 3;
  public static final byte STATEMENT_EXECUTION_MODE_UNDEFINED = 4;
  public static final byte CONNECTION_STATE_NEW = 0;
  public static final byte CONNECTION_STATE_IDLE = 1;
  public static final byte CONNECTION_STATE_DEAD = 2;
  public static final byte CONNECTION_STATE_BUSY = 3;
  public static final int COLUMN_CHUNK_SIZE = 256;
  public static final String SYBASEFUNCTION = "SYBASEFUNCTION";
  public static final char BACKSLASH = '\\';
  public static final String COPYRIGHT_STRING = "Confidential property of Sybase, Inc.\nCopyright 1997, 2011\nSybase, Inc.  All rights reserved.\nUnpublished rights reserved under U.S. copyright laws.\nThis software contains confidential and trade secret information of Sybase,\nInc.  Use, duplication or disclosure of the software and documentation by\nthe U.S. Government is subject to restrictions set forth in a license\nagreement between the Government and Sybase, Inc. or other written\nagreement specifying the Government's rights to use the software and any\napplicable FAR provisions, for example, FAR 52.227-19.\n\nSybase, Inc. One Sybase Drive, Dublin, CA 94568\n";
  public static final boolean ENABLE_DEBUGGING = false;
  public static final boolean CONTEXT_TRACE = false;
  public static final boolean DEBUG = false;
  public static final boolean ASSERT = false;
  public static final String VERSION_STRING = "jConnect (TM) for JDBC(TM)/7.07 SP100 (Build 26965)/P/EBF20990/JDK 1.4.2/jdbc3v707/OPT/Wed Apr 24 14:16:23 PDT 2013";
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.IConstants
 * JD-Core Version:    0.5.4
 */