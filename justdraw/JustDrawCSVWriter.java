/*
 * @(#)JustDrawCSVWriter.java 0.1 14-JUN-04 Copyright (c) 2004, Nila Muecke All
 * rights reserved. Portions Copyright (c) 2001-2004, Gaudenz Alder All rights
 * reserved. Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Resdistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of JGraph nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission. THIS SOFTWARE IS PROVIDED
 * BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package justdraw;

import java.awt.geom.Rectangle2D;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.TreeMap;

import javax.swing.JProgressBar;
import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXParseException;

import java.awt.Point;
import java.awt.geom.Point2D;

import org.jgraph.*;
import org.jgraph.event.*;
import org.jgraph.event.*;
import org.jgraph.graph.*;

//import com.uob.utility.HTMLConverter;

public class JustDrawCSVWriter
{
  
  public JustDrawCSVWriter()
  {
  }
  
  public static void exportCSV(String filename, JustDrawDocument document)
  {
    AutoId autoId = new AutoId("AutoId");
    File ga_file = new File(filename + "_genericArguments.csv");
    File cv_file = new File(filename + "_claimValues.csv");
    BufferedWriter ga_writer;
    BufferedWriter cv_writer;
    int rootCount;
    Object root, child, target, notsure;
    Iterator children, edges;    
    Edge currentEdge;
    Rectangle2D bounds;
    String id;
    
    try
    {
      ga_writer = new BufferedWriter( new FileWriter(ga_file));
      cv_writer = new BufferedWriter( new FileWriter(cv_file));
      
      //System.out.println("Argument ID; Title; Prefix; Suffix; Parent; Relevence; Not Sure; Type; Graph ID; Expanderble; Xpos; Ypos" );
      ga_writer.write("Argument ID; Title; Prefix; Suffix; Parent; Relevence; Not Sure; Type; Graph ID; Expanderble; Xpos; Ypos" );
      ga_writer.newLine();
      ga_writer.flush();
      
      cv_writer.write("Choice ID; Argument ID; Claim; Threshold; Weight; Default; Graph ID; Summary" );
      cv_writer.newLine();   
      //System.out.println("Choice ID; Argument ID; Claim; Threshold; Weight; Default; Graph ID; Position; Target; Summary" );
      cv_writer.flush();
      
      // Write the root nodes
      rootCount = document.graph.getModel().getRootCount();    
      for (int i  = 0; i < rootCount; i++)
      {
        root = document.graph.getModel().getRootAt(i);
        if (root instanceof DecisionTreeNode)
        {
          if (((DecisionTreeNode)root).isRoot())
          {
            id = ((DecisionTreeNode)root).getId();
            if (id.equals("") || id == null || id == "")
              ((DecisionTreeNode)root).setId(autoId.getNextId());
            
            // writer.write("<rootNode name=\""+ ((DecisionTreeNode)root).getId() +"\"
            // type=\"basic\" refid=\"" + ((DecisionTreeNode)root).getId() +"\" />");
            // writer.newLine()();
          }
        }
      }
      rootCount = document.graph.getModel().getRootCount();    
      for (int i  = 0; i < rootCount; i++)
      {
        root = document.graph.getModel().getRootAt(i);
        if (root instanceof DecisionTreeNode)
        {
          id = ((DecisionTreeNode)root).getId();	    
          if (id.equals("") || id == null || id == "")
            ((DecisionTreeNode)root).setId(autoId.getNextId());		
          bounds = document.graph.getGraphLayoutCache().getMapping(root, false).getBounds();
          
          ga_writer.write( ((DecisionTreeNode)root).getId() + ";" + 
        		           ((DecisionTreeNode)root).getTitle() +";" + 
        		           ((DecisionTreeNode)root).getPrefix() + ";" + 
        		           ((DecisionTreeNode)root).getSuffix() + ";" + "Parent" + ";" + 
        		           ((DecisionTreeNode)root).getRelevance() + ";" 
        		           );
          ga_writer.flush();

          children = ((DecisionTreeNode)root).getChildren().iterator();
          while (children.hasNext())
          {        
            child = children.next();
            if (child instanceof Port)
            {
              edges = ((Port)child).edges();
              TreeMap answerStrings = new TreeMap();
              // String[] answerStrings = new String[]{};
              
              int xx = 0;
              while (edges.hasNext())
              {
                currentEdge = (Edge)edges.next();
                if (currentEdge.getSource().equals(child))
                {
                  if (currentEdge instanceof DecisionTreeArc)
                  {
                    // out put
                    cv_writer.write( xx + ";" + ((DecisionTreeNode)root).getId() + ";" + 
                    		                    ((DecisionTreeArc)currentEdge).getClaim() + ";"  +
                    		                    ((DecisionTreeArc)currentEdge).getThreshold() +";" 
                    		                    );
                    cv_writer.flush();
                    xx++;
                    target = ((DefaultPort)currentEdge.getTarget()).getParent();
                    if (target instanceof DecisionTreeNode)
                    {
                      id = ((DecisionTreeNode)target).getId();	    
                      if (id.equals("") || id == null || id == "")
                        ((DecisionTreeNode)target).setId(autoId.getNextId());
                      // out put
                      cv_writer.write( ((DecisionTreeNode)target).getId() + ";" );
                      cv_writer.flush();
                    }
                    if (target instanceof ConclusionNode)		    
                    {
                      id = ((ConclusionNode)target).getId();
                      if (id.equals("") || id == null || id == "")
                        ((ConclusionNode)target).setId(autoId.getNextId());
                      // out put
                      cv_writer.write( ((ConclusionNode)target).getId() + ";" );
                      cv_writer.flush();
                    }
                    
                    
                    // + ((DecisionTreeArc)currentEdge).getWeight() + ";"
                    // out put
                    
                    cv_writer.write( ((DecisionTreeArc)currentEdge).getPosition() + ";" + 
                    		         ((DecisionTreeNode)root).getGraph_id() + ";" + 
                    		         ((DecisionTreeArc)currentEdge).getSummary() 
                    		         );
                    
                    cv_writer.newLine(); 
                    cv_writer.flush(); 
                    /*
                     * Iterator actions =
                     * ((Vector)((DecisionTreeArc)currentEdge).getActions()).iterator();
                     * TreeArcAction currentAction; while (actions.hasNext()) { currentAction =
                     * (TreeArcAction)actions.next(); if
                     * (currentAction.getType().toLowerCase().equals("add")) { currentString =
                     * currentString + " <add filterName=\"" +
                     * HTMLConverter.escape(currentAction.getVariable()) + "\" filterParam=\"" +
                     * HTMLConverter.escape(currentAction.getValue()) +"\"/>\n"; } else if
                     * (currentAction.getType().toLowerCase().equals("remove")) { currentString =
                     * currentString + " <remove filterName=\"" +
                     * HTMLConverter.escape(currentAction.getVariable()) + "\" filterParam=\"" +
                     * HTMLConverter.escape(currentAction.getValue()) +"\"/>\n"; } else if
                     * (currentAction.getType().toLowerCase().equals("clear")) { currentString =
                     * currentString + " <clear filterName=\"" +
                     * HTMLConverter.escape(currentAction.getVariable()) + "\" filterParam=\"" +
                     * HTMLConverter.escape(currentAction.getValue()) +"\"/>\n"; } else if
                     * (currentAction.getType().toLowerCase().equals("script")) { currentString =
                     * currentString +" <script>\n" + HTMLConverter.escape(currentAction.getValue())
                     * +"\n</script>\n"; } else if
                     * (currentAction.getType().toLowerCase().equals("setvariable") ||
                     * currentAction.getType().toLowerCase().equals("setvar") ||
                     * currentAction.getType().toLowerCase().equals("set") ||
                     * currentAction.getType().toLowerCase().equals("set variable")) { currentString =
                     * currentString +" <setVariable variableName=\"" +
                     * HTMLConverter.escape(currentAction.getVariable()) + "\" variableValue=\"" +
                     * HTMLConverter.escape(currentAction.getValue()) +"\"/>\n"; } }
                     */ 
                    
                    System.out.println("Connection from " + root + " to " + ((DefaultPort)currentEdge.getTarget()).getParent());
                  }
                } 
                // answerStrings.put(new Integer(((DecisionTreeArc)currentEdge).getPosition()), currentString);
              }
              System.out.println(answerStrings);  
              Iterator it = answerStrings.values().iterator();  
              /* while (it.hasNext())
              {
                cv_writer.write((String)it.next());
              } */
            } 
          }// end while
          
          if(((DecisionTreeNode)root).hasArgument)
          {
            
           // cv_writer.newLine()();
            notsure = ((JustDrawArgument)document.arguments.get(root)).getRootNode();	    	    
            children = ((ArgumentTreeNode)notsure).getChildren().iterator();
            while (children.hasNext())
            {        
              child = children.next();
              if (child instanceof Port)
              {
                edges = ((Port)child).edges();	      
                while (edges.hasNext())
                {
                  currentEdge = (Edge)edges.next();
                  if (currentEdge.getTarget().equals(child))
                  {
                    if (currentEdge instanceof ArgumentArc)
                    {
                      id = ((ArgumentTreeNode)((DefaultPort)currentEdge.getSource()).getParent()).getId();
                      if (id.equals("") || id == null || id == "")
                        ((ArgumentTreeNode)((DefaultPort)currentEdge.getSource()).getParent()).setId(autoId.getNextId());		    
                      // writer.write(" <notSureNode refid=\"" +
                      // ((ArgumentTreeNode)((DefaultPort)currentEdge.getSource()).getParent()).getId()
                      // + "\"/>");
                      // writer.newLine()();
                    }
                  }
                }  
              } 
            }    	    	    	    	       
            ga_writer.write( "1;" + "A;" ); // enable not sure
            ga_writer.flush();
          }
          else
          {
            // no not sures
            
            ga_writer.write( "0;" + "D;" );// disable not sure
            ga_writer.flush();
          }
          
          ga_writer.write(  ((DecisionTreeNode)root).getGraph_id() + ";" + "Expanderble" + 
        		           ";" + bounds.getX() + ";" + bounds.getY() 
        		           );
          
          ga_writer.newLine();    
          ga_writer.flush();
        }
        
        // concluseion
        if (root instanceof ConclusionNode)
        {
          id = ((ConclusionNode)root).getId();	    
          if (id.equals("") || id == null || id == "")
            ((ConclusionNode)root).setId(autoId.getNextId());
          
          bounds = document.graph.getGraphLayoutCache().getMapping(root, false).getBounds();
          
          ga_writer.write( ((ConclusionNode)root).getId() + ";" + 
        		           ((ConclusionNode)root).getTitle() + ";" + 
        		           ((ConclusionNode)root).getPrefix() + ";" + 
        		           ((ConclusionNode)root).getSuffix() + ";" + ";" + 
        		           ((ConclusionNode)root).getRelevance() + ";" + "0;" + "C;" +
        		           ((ConclusionNode)root).getGraph_id() + ";" + 
        		           "Expanderble" + ";" +bounds.getX() + ";" + bounds.getY() 
        		           );
          ga_writer.newLine();
          ga_writer.flush();
        }
      }    
      
      
      Enumeration e = document.arguments.elements();
      Enumeration arguments;
      JGraph currentGraph;
      // int rootCount;
      // Object root;
      while (e.hasMoreElements())
      {
        currentGraph = ((JustDrawArgument)e.nextElement()).graph;
        rootCount = currentGraph.getModel().getRootCount();    
        
        for (int i  = 0; i < rootCount; i++)
        {
          root = currentGraph.getModel().getRootAt(i);
          if (root instanceof ArgumentTreeNode)
          {
            if (((ArgumentTreeNode)root).isRoot())
            {
            }
            else
            {
              bounds = currentGraph.getGraphLayoutCache().getMapping(root, false).getBounds();
              // argument generic arguments
              ga_writer.write( ((ArgumentTreeNode)root).getId() + ";" + "Title" + ";" + 
            		           ((ArgumentTreeNode)root).getPrefix() + ";" + 
            		           ((ArgumentTreeNode)root).getSuffix() + ";" + 
            		           "Parent" + ";" + "Relivance" + ";" + 
            		           "Not Sure" + ";" + "A;" + 
            		           "Graph ID" + ";" + "Ecpandable" + 
            		           ";" + bounds.getX() + ";" + bounds.getY() 
            		           );
              
              ga_writer.newLine();
              ga_writer.flush();
              
              arguments = ((ArgumentTreeNode)root).getClaims().elements();
              
              int xx = 0;
              while (arguments.hasMoreElements())
              {        
                child = arguments.nextElement();
                // argurnt claim values
                cv_writer.write( xx + ((ArgumentTreeNode)root).getId() + ";" + ((ArgumentClaim)child).getClaim() + ";" + ((ArgumentClaim)child).getThreshold() + ";" + ((ArgumentClaim)child).getWeight() + ";" + "Order" + ";" + "Graph ID" + ";" + "Position" + ";" + "Target" + ";" + "Summary"  );
                cv_writer.flush();
                
                
                children = ((ArgumentTreeNode)root).getChildren().iterator();
                while (children.hasNext())
                {        
                  child = children.next();
                  if (child instanceof Port)
                  {
                    edges = ((Port)child).edges();	      
                    while (edges.hasNext())
                    {
                      currentEdge = (Edge)edges.next();
                      if (currentEdge.getTarget().equals(child))
                      {
                        if (currentEdge instanceof ArgumentArc)
                        {
                          id = ((ArgumentTreeNode)((DefaultPort)currentEdge.getSource()).getParent()).getId();
                          if (id.equals("") || id == null || id == "")
                            ((ArgumentTreeNode)((DefaultPort)currentEdge.getSource()).getParent()).setId(autoId.getNextId());		    		    
                          // writer.write(" <notSureNode refid=\"" +
                          // ((ArgumentTreeNode)((DefaultPort)currentEdge.getSource()).getParent()).getId()
                          // );
                          
                          
                        }
                      }
                    }  
                  } 
                }    
                
                cv_writer.newLine();	 
              }
            }	  
          }
        }      
        
        ga_writer.flush();
        cv_writer.flush();
        ga_writer.close();
        cv_writer.close();
      } 
    }
    catch (Exception ex)
    {
      // ** Show dialog here
      ex.printStackTrace();
    }
  }
}
