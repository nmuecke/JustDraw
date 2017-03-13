package com.uob.utility;


/**
 * @author camfoale
 */
public class IndefiniteArticler {
  
  public static String getIndefiniteArticle(String ext)
  {
    if(ext != null && ext.length() > 0 && isVowel(ext.substring(0,1)))
    {
      return "an";
    }
    return "a";
  }
  
  public static boolean isVowel(String c)
  {
    return (c.matches("[aeiouAEIOU]"));
  }

}
