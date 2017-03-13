/*
 * @(#)ArgumentClaim.java 0.1 16-JUN-04
 * 
 * Copyright (c) 2004, John Avery All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provclaimed that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. - Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provclaimed with the distribution. - Neither the name of JGraph nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVclaimED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCclaimENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *  
 */

package justdraw;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import java.net.URL;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.jgraph.*;
import org.jgraph.event.*;
import org.jgraph.graph.*;

public class ArgumentClaim
{

  private String claim;
  private Double weight;
  private Double threshold;

  public ArgumentClaim()
  {
    claim = "";
    weight = new Double(0);
    threshold = new Double(0);
  }

  public ArgumentClaim(String claim, Double weight, Double threshold)
  {
    this.claim = claim;
    this.weight = weight;
    this.threshold = threshold;
  }

  public ArgumentClaim(String claim, String weight, String threshold)
  {
    this.claim = claim;
    try
    {
      this.weight = new Double(weight);
    }
    catch (Exception ex)
    {
      this.weight = new Double(0);
    }
    try
    {
      this.threshold = new Double(threshold);
    }
    catch (Exception ex)
    {
      this.threshold = new Double(0);
    }
  }
  
  public String getClaim()
  {
    return claim;
  }

  public void setClaim(String claim)
  {
    this.claim = claim;
  }

  public Double getWeight()
  {
    return weight;
  }

  public void setWeight(Double weight)
  {
    this.weight = weight;
  }

  public void setWeight(String weight)
  {
    try
    {
      this.weight = new Double(weight);
    }
    catch (Exception ex)
    {
      this.weight = new Double(0);
    }
  }
  
  public Double getThreshold()
  {
    return threshold;
  }

  public void setThreshold(Double weight)
  {
    this.threshold = threshold;
  }
  
  public void setThreshold(String threshold)
  {
    try
    {
      this.threshold = new Double(threshold);
    }
    catch (Exception ex)
    {
      this.threshold = new Double(0);
    }    
  }
}