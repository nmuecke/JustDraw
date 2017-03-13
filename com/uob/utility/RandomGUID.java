/*
 * Created on Apr 5, 2005
 */
package com.uob.utility;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;


/**
 * @author camfoale
 */
public class RandomGUID {
  
  private static int seq = 0;
  
  private static Random rand;
  private static SecureRandom secrand;
  
  private static String machineid;
  
  static {
    secrand = new SecureRandom();
    long secureInitializer = secrand.nextLong();
    rand = new Random(secureInitializer);
    try {
      machineid = InetAddress.getLocalHost().toString();
    } catch (UnknownHostException e) {
        e.printStackTrace();
    }
  }
  
  private String seedString = null;
  private String md5hash = null;
  
  public String generate()
  {
    StringBuffer seedBuilder = new StringBuffer();
    
    // build the seed string
    seedBuilder.append(machineid);
    seedBuilder.append("#");
    seedBuilder.append(System.currentTimeMillis());
    seedBuilder.append("!");
    seedBuilder.append(seq++);
    seedBuilder.append(":");
    seedBuilder.append(rand.nextLong());
    seedBuilder.append("?");
    seedBuilder.append(rand.nextLong());
    
    seedString = seedBuilder.toString();
    md5hash = seedString;
    
    System.out.println(seedString);
    
    try
    {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(seedString.getBytes());

      byte[] array = md5.digest();
      StringBuffer sb = new StringBuffer();
      for (int j = 0; j < array.length; ++j) {
          int b = array[j] & 0xFF;
          if (b < 0x10) sb.append('0');
          sb.append(Integer.toHexString(b));
      }

      md5hash = sb.toString();
      
    } catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
    md5hash = formatGUID(md5hash);
    System.out.println(md5hash);
    return md5hash;
    
  }
  
  private String formatGUID(String guid)
  {
    String raw = guid.toUpperCase();
    StringBuffer sb = new StringBuffer();
    sb.append(raw.substring(0, 8));
    sb.append("-");
    sb.append(raw.substring(8, 12));
    sb.append("-");
    sb.append(raw.substring(12, 16));
    sb.append("-");
    sb.append(raw.substring(16, 20));
    sb.append("-");
    sb.append(raw.substring(20, 32));

    return sb.toString();
  }
  

}
