package com.sybase.jdbc3.utils;

import java.io.IOException;
import java.io.InputStream;

public class AsciiInput
{
  public static String readLine(InputStream paramInputStream)
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    char[] arrayOfChar = new char[1];
    while (true)
    {
      arrayOfChar[0] = (char)(paramInputStream.read() & 0xFF);
      if (arrayOfChar[0] == '\n')
      {
        if ((localStringBuffer.length() == 0) || ((localStringBuffer.length() == 1) && (localStringBuffer.charAt(0) == '\r')))
        {
          return null;
        }
        return localStringBuffer.toString();
      }
      localStringBuffer.append(arrayOfChar);
    }
  }
}

/* Location:           C:\Users\zhangtx\Desktop\ribo\ribo.jar
 * Qualified Name:     com.sybase.jdbc3.utils.AsciiInput
 * JD-Core Version:    0.5.4
 */