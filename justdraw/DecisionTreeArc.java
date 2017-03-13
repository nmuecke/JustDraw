/*
 * @(#)DecisionTreeArc.java 0.1 16-JUN-04
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

public class DecisionTreeArc extends DefaultEdge
{

  private String claim;
  private double threshold;
  private double weight;
  private Vector actions;
  private int connectionId;
  private String summary;
  private int position;

  public DecisionTreeArc()
  {
    super();
    claim = "";
    threshold = 0.0;
    weight = 0.0;
    connectionId = 0;
    summary = "";
    actions = new Vector();
    position = 1;
  }

  public DecisionTreeArc(String claim, double threshold, double weight, Vector actions)
  {
    super();
    setClaim(claim);
    this.threshold = threshold;
    this.weight = weight;
    summary = "";
    connectionId = 0;
    if (actions != null)
    {
      this.actions = actions;
    }
    else
    {
      this.actions = new Vector();
    }
    position = 1;
  }

  public DecisionTreeArc(String claim, double threshold, double weight, Vector actions, int connectionId)
  {
    super();
    setClaim(claim);
    this.threshold = threshold;
    this.weight = weight;
    setConnectionId(connectionId);
    summary = "";
    if (actions != null)
    {
      this.actions = actions;
    }
    else
    {
      this.actions = new Vector();
    }
    position = 1;
  }

    public DecisionTreeArc(String claim, double threshold, double weight, String summary, Vector actions, int connectionId)
  {
    super();
    setSummary(summary);
    setClaim(claim);
    this.threshold = threshold;
    this.weight = weight;
    setConnectionId(connectionId);
    if (actions != null)
    {
      this.actions = actions;
    }
    else
    {
      this.actions = new Vector();
    }
    position = 1;
  }

  public void addAction(String type, String variable, String value)
  {
    actions.add(new TreeArcAction(type, variable, value));
  }

  public void delAction(int index)
  {
    actions.remove(index);
  }

  public String getClaim()
  {
    return claim;
  }
 
  public double getThreshold()
  {
    return threshold;
  }

  public double getWeight()
  {
    return weight;
  }

  public int getPosition()
  {
    return position;
  }

  public Vector getActions()
  {
    return actions;
  }
  
  
  public String getSummary()
  {
    return summary;
  }
  
  public int getConnectionId()
  {
    return connectionId;
  }

  public void setClaim(String claim)
  {
    this.claim = claim;
    AttributeMap attributemap = getAttributes();
    attributemap.put("value", claim);
    setAttributes(attributemap);
  }
 
  public void setThreshold(double threshold)
  {
    this.threshold = threshold;
  }

  public void setWeight(double weight)
  {
    this.weight = weight;
  }

  public void setPosition(int position)
  {
    this.position = position;
  }

  public void setSummary(String summary)
  {
    this.summary = summary;
  }
  
  public void setActions(Vector actions)
  {
    this.actions = actions;
  }
  
  public void setConnectionId(int c)
  {
    connectionId = c;
    AttributeMap attributemap = getAttributes();
    attributemap.put("conid", (new Integer(c)).toString());
    setAttributes(attributemap);    
  }
}
