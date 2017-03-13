/*
 * Created on Oct 11, 2004
 */
package com.uob.utility;


/**
 * @author camfoale
 */
public class StringArrayDisplay {
  
  public static String formatStringArray(String[] sa)
  {
    String sRet = "";
    for(int i = 0; i < sa.length; i++)
    {
      sRet += sa[i] + ", ";
    }
    
    sRet += "[ " + Integer.toString(sa.length) + " elements ]";
    
    return sRet;
  }

}
