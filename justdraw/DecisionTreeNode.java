/*
 * @(#)DecisionTreeNode.java 0.1 16-JUN-04
 * 
 * Copyright (c) 2004, John Avery All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. - Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. - Neither the name of JGraph nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
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

public class DecisionTreeNode extends DefaultGraphCell
{

  private String prefix;
  private String suffix;
  private String id;
  private String title;
  private String relevance; 
  private String graph_id;
  private String display_type;
  
  private boolean root;
  
  
  protected boolean hasArgument = false;

  public DecisionTreeNode()
  {
    this("","","","","", "", "radio");
  }

  public DecisionTreeNode(String id, String prefix, String suffix)
  {
    this(id, "", prefix, suffix, "", "", "radio");
  }

  public DecisionTreeNode(String id, String title, String prefix, String suffix, 
		                  String relevance, String graph_id, String display_type )
  {
    super();
    setId( id );
    this.title = title;
    setPrefix(prefix);
    setSuffix(suffix);
    this.relevance = relevance;
    this.graph_id = graph_id;
    root = false;
    this.display_type = display_type;
  }

  public DecisionTreeNode(String id, String title, String prefix, String suffix)
  {
    super();
    setId( id );
    this.title = title;
    setPrefix(prefix);
    setSuffix(suffix);
    this.relevance = "";
    this.graph_id = "";
    root = false;
    this.display_type = "radio";
  }
  
  public void addArgument()
  {
    hasArgument = true;   
    AttributeMap attributemap = getAttributes();
    attributemap.put("argument", "true");
    changeAttributes(attributemap);  
  }
  
  public void removeArgument()
  {
    hasArgument = false;   
    AttributeMap attributemap = getAttributes();
    attributemap.put("argument", "false");
    //setAttributes(attributemap);
    changeAttributes(attributemap);    
  }
  
  public String getId()
  {
    return id;
  }
 
  public String getTitle()
  {
    return title;
  }
  
  public String getPrefix()
  {
    return prefix;
  }

  public String getSuffix()
  {
    return suffix;
  }
  
  public String getRelevance()
  {
    return relevance;
  }
  
  public String getGraph_id()
  {
    return graph_id.replaceAll( " ", "_");
  }
  
  public String getDisplay_type()
  {
    return display_type;
  }
  
  public boolean isRoot()
  {
    return root;
  }

  public void setRoot(boolean root)
  {
    this.root = root;
  }
  
  public void setId(String id)
  {
    this.id = id.replaceAll( " ", "_");
  }
  
  public void setTitle(String title)
  {
    this.title = title;
  }
  
  public void setRelevance(String relevance)
  {
    this.relevance = relevance;
  }
  public void setGraph_id(String graph_id)
  {
    this.graph_id = graph_id;
  }
    

  public void setPrefix(String prefix)
  {
    this.prefix = prefix;
    AttributeMap attributemap = getAttributes();
    attributemap.put("value", prefix + " " + suffix);
    //setAttributes(attributemap);
    changeAttributes(attributemap);    
  }

  public void setSuffix(String suffix)
  {
    this.suffix = suffix;
    //this.prefix = prefix;
    AttributeMap attributemap = getAttributes();
    attributemap.put("value", prefix + " " + suffix);
    //setAttributes(attributemap);
    changeAttributes(attributemap);        
  }
}

