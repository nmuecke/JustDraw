/*
 * Created on Feb 22, 2005
 */
package com.uob.utility;

import java.util.regex.Pattern;


/**
 * @author camfoale
 */
public class AscoFormatter {
  
  private static Pattern p = null;
  
  private static Pattern getAscoPattern()
  {
    if(p == null)
    {
      p = Pattern.compile("\\d{6}");
    }
    
    return p;
  }
  
  public static String format(String sAsco)
  {
    String sRet;
    
    
    sRet = ("000000" + sAsco.replaceAll("[-_]",""));
    sRet = sRet.substring(sRet.length() - 6);
    
    if( getAscoPattern().matcher(sRet).matches() )
    {
      
      sRet = sRet.substring(0,4) + "-" + sRet.substring(4);
    } else {
      // come up with something that looks nothing like an asco
      sRet = "NOASCO";
    }
    
    return sRet;
  }
  

}
