/*
 * Created on Apr 20, 2005
 */
package com.uob.utility;


/**
 * @author camfoale
 */
public class SafeKey {
  
  public static String safe(String key)
  {
    return key.trim().toLowerCase();
  }

}
