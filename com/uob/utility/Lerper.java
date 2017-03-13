/*
 * Created on 30/05/2005
 */
package com.uob.utility;


/**
 * @author camfoale
 * Does linear interpolation maths
 */
public class Lerper {
  
  /**
   * Returns (val - min) / (max - min), clamped to 0-1 inclusive
   * @param min
   * @param max
   * @param val
   * @return
   */
  public static double getLerpRatio(double min, double max, double val)
  {
    double ret;
    if((max - min) > 0)
    {
      ret = (val - min) / (max - min);
      ret = Math.min(1.0, ret);
      ret = Math.max(0.0, ret);
    } else {
      ret = 1.0;
    }
    
    return ret;
  }
  
  // linearly interpolates 
  public static double lerp(double min, double max, double lerp)
  {
    
    return ((max - min) * lerp) + min;
    
  }

}
