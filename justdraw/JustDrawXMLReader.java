/*
 * @(#)JustDrawXMLReader.java 0.1 14-JUN-04
 * 
 * Copyright (c) 2004, John Avery All rights reserved.
 * Portions Copyright (c) 2001-2004, Gaudenz Alder All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. - Resdistributions in
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


import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JProgressBar;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

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
//import org.jgraph.event.*;
import org.jgraph.graph.*;

public class JustDrawXMLReader
{
  DocumentBuilderFactory dbf;
  HashMap hmDecisionNodes;
  HashMap hmArgumentNodes;
  Document xmldoc;
  HashMap idToNodeMap;
  String idOfRoot;
  String docPrefix;
  
  public JustDrawXMLReader()
  {
    dbf = DocumentBuilderFactory.newInstance();
    hmDecisionNodes = new HashMap();
    hmArgumentNodes = new HashMap();
    xmldoc = null;
    idToNodeMap = new HashMap();
    idOfRoot = "";
  }
  
  public HashMap getRootNodes(String sFileName)
  {
    HashMap rootNodeTable = new HashMap();   
    
    JProgressBar progress = new JProgressBar();
    JFrame progressFrame = new JFrame("Loading please wait"); 
    progressFrame.getContentPane().setLayout(new BorderLayout());   
    progressFrame.getContentPane().add(progress, BorderLayout.CENTER);
    progressFrame.setSize(250, 50);        
    progressFrame.show();

    dbf.setValidating(false);
    progress.setIndeterminate(true);
    try
    {
      DocumentBuilder db = dbf.newDocumentBuilder();
      System.out.println("XmlKBLoader::loadKnowledgeBase: using " + sFileName + " as KB source");
      
      if(sFileName.indexOf('!') >= 0)
      {
        System.out.println("XmlKBLoader::loadKnowledgeBase: Invalid file path!");
        return null;
      }
      
      xmldoc = db.parse(new BufferedInputStream(new FileInputStream(new java.io.File(sFileName))));
      System.out.println("XmlKBLoader::loadKnowledgeBase: Parsed");
      Element knowledgeBaseElement = xmldoc.getDocumentElement();


      NodeList docPrefixNodes = knowledgeBaseElement.getElementsByTagName("docPrefix");
      for( int xx = 0; xx < docPrefixNodes.getLength(); xx++){
    	 Element docPrefixEl = (Element) docPrefixNodes.item(xx);
    	 if( docPrefixEl.getElementsByTagName("docprefix").item(0).getFirstChild() != null ){
    	     docPrefix =  docPrefixEl.getElementsByTagName("docprefix").item(0).getFirstChild().getNodeValue();
        	 System.out.print( "docPrefix: --> " + docPrefix +"\n" );
    	 }
    	 else
    		 docPrefix = "";
      }
      
      NodeList rootNodes = knowledgeBaseElement.getElementsByTagName("rootNode");
      System.out.println("XmlKBLoader::loadKnowledgeBase: got " + Integer.toString(rootNodes.getLength()) + " root nodes"); 
      progress.setMaximum(rootNodes.getLength());
      progress.setIndeterminate(false);
      progress.setStringPainted(true);
      for (int i = 0; i < rootNodes.getLength(); i++)
      {
        if(rootNodes.item(i) instanceof Element )
        {
          Element rootNodeElement = (Element) rootNodes.item(i);	  
	  rootNodeTable.put(rootNodeElement.getAttribute("refid"), new String(rootNodeElement.getAttribute("name")));
	  // This takes a long time using the DOM (** should use SAX) so use a progress bar
	  progress.setValue(i);
	  progressFrame.update(progressFrame.getGraphics());
	}
      }
    }
    catch (SAXParseException e)
    {
      e.printStackTrace();
      rootNodeTable = null;
      // ** Put a dialog box here 
    }
    catch (Exception e)
    {
      e.printStackTrace();

    }
    
    progressFrame.dispose();
    return rootNodeTable;
  }

  public JustDrawDocument buildTree(final JustDraw parent, String rootId, String name)
  {
    JustDrawDocument document = new JustDrawDocument(parent, name, docPrefix );
    idOfRoot = rootId;   
    try
    {
      document = addChildren(rootId, null, document, null, 0.0, 1, 0.0, "", null);
    }
    catch (Exception e)
    {
     // ** Display could not load dialog
      e.printStackTrace();
    }
    return document;
  }
  
  public JustDrawDocument addChildren(String rootId, Port parentPort, JustDrawDocument document, String claim, double weight, int position, double threshold, String summary, NodeList actions)
  {
    try
    {
      Element root = xmldoc.getElementById(rootId);
      // add this node
      String prefix;
      String postfix;
      String title;
      String relevance;
      String graph_id;
      String display_type;
      
      int xpos, ypos;
      DefaultGraphCell currentCell;
      Port currentPort = null;
      
      NodeList answers  = root.getElementsByTagName("decisionAnswers").item(0).getChildNodes();
      NodeList notsures = root.getElementsByTagName("notSureNodes").item(0).getChildNodes();
     
      
      if (root.getElementsByTagName("prefix").item(0).getFirstChild() != null)
      {
        prefix = root.getElementsByTagName("prefix").item(0).getFirstChild().getNodeValue();
      }
      else
      {
        prefix = "";
      }
      if (root.getElementsByTagName("postfix").item(0).getFirstChild() != null)
      {
        postfix = root.getElementsByTagName("postfix").item(0).getFirstChild().getNodeValue();
        System.out.print( "postfix: " + postfix +"\n" );
      }
      else
      {
        postfix = "";
      }
      if (root.getElementsByTagName("title").item(0).getFirstChild() != null)
      {
        title = root.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
        System.out.print( "Title: " + title +"\n" );
      }
      else
      {
        title = "";
        System.out.print( "Title: Not Title set \n" );
      }      
      if (root.getElementsByTagName("relevance").item(0).getFirstChild() != null)
      {
        relevance = root.getElementsByTagName("relevance").item(0).getFirstChild().getNodeValue();
        //System.out.print( "Title: " + title + "Relevance: " + relevance + "\n" );
      }
      else
      {
        relevance = "";
        //System.out.print( "Title: "+ title + "relevance not found\n" );
      }

      if (root.getElementsByTagName("graphID").item(0).getFirstChild() != null)
      {
        graph_id = root.getElementsByTagName("graphID").item(0).getFirstChild().getNodeValue();
      }
      else
      {
        graph_id = "";
      }
      
      if (root.getElementsByTagName("displayType").item(0).getFirstChild() != null)
      {
        display_type = root.getElementsByTagName("displayType").item(0).getFirstChild().getNodeValue();
      }
      else
      {
        display_type = "";
      }
      
      
      if (root.getAttribute("xpos") != "")
      {
        xpos = new Double(root.getAttribute("xpos")).intValue();
      }
      else
      {
        xpos = 10;
      }
      if (root.getAttribute("ypos") != "")
      {
        ypos = new Double(root.getAttribute("ypos")).intValue();
      }
      else
      {
        ypos = 10;
      }
      Point2D pos = new Point(xpos, ypos);
      
      if (answers.getLength() > 1)
      {
        if (idToNodeMap.containsKey(rootId))
        {
        	currentCell = (DefaultGraphCell)idToNodeMap.get(rootId);
        }
        else
        {
          currentCell = (DefaultGraphCell) JustDrawCellFactory.insertDecisionTreeNode(document.graph, pos, rootId, title, prefix, postfix, relevance, graph_id, display_type);
          if (rootId.equals(idOfRoot) && rootId != "")
          {
        	  if (currentCell instanceof DecisionTreeNode)
        	  {
        		  ((DecisionTreeNode)currentCell).setRoot(true);
        	  }
          }

        }
	
      }
      else
      {
        if (idToNodeMap.containsKey(rootId))
	{
	  currentCell = (DefaultGraphCell)idToNodeMap.get(rootId);
	}
	else
	{
          currentCell = (DefaultGraphCell) JustDrawCellFactory.insertConclusionNode(document.graph, pos, rootId, title, prefix, postfix, graph_id, relevance, display_type  );

	}
      }
      
      // Find port for vertex
      Iterator children = currentCell.getChildren().iterator();
      Object child;
      while (children.hasNext())
      {
        child = children.next();
        if (child instanceof Port){
        	currentPort = (Port)child;
        }
      }
      
      // Build actions
      Vector claimActions = new Vector();
      double newWeight;
      int newPosition;
      String newSummary;
      String newClaim;
      if (actions !=null)
      {
        for (int i = 0; i < actions.getLength(); i++)
        { 
          
        	// add each action
        	Node thisActionNode  = actions.item(i);  
        	if (thisActionNode instanceof Element)
        	{
        		Element actionElement = (Element)thisActionNode;
        		// check each node, compare to some known ones
        		if(actionElement.getTagName().equalsIgnoreCase("setVariable"))
        		{
        			String varName, varValue;
        			// check if this is an old-school justdraw (filterName,filterParam) setVariable
        			if(actionElement.hasAttribute("filterName"))
        			{
        				// old-skool
        				varName = actionElement.getAttribute("filterName");
        				varValue = actionElement.getAttribute("filterParam");          
        			} else {
        				// nu skool
        				varName = actionElement.getAttribute("variableName");
        				varValue = actionElement.getAttribute("variableValue");          
        			}
        			claimActions.add(new TreeArcAction(actionElement.getTagName(),varName,varValue));
       
        		} 
         		else if(actionElement.getTagName().equalsIgnoreCase("setAction"))
        		{
        			String varName, varValue;
        			varName = actionElement.getAttribute("actionType");
        			varValue = actionElement.getAttribute("actionValue");          
        			
        			claimActions.add(new TreeArcAction(actionElement.getTagName(),varName,varValue));
       
        		}
         		else if(actionElement.getTagName().equalsIgnoreCase("__badAction__"))
        		{
        			String varName, varValue;
        			varName = actionElement.getAttribute("actionType");
        			varValue = actionElement.getAttribute("actionValue");          
        			
        			claimActions.add(new TreeArcAction(actionElement.getTagName(),varName,varValue));
       
        		}
        		 		
        		else {
        			claimActions.add(new TreeArcAction(actionElement.getTagName(),actionElement.getAttribute("filterName"),actionElement.getAttribute("filterParam")));
        		} // TODO: Handle Script tags
        	}
         }
      }
      // connect to parent
      if (parentPort != null && currentPort != null)
      {
        DecisionTreeArc arc = JustDrawCellFactory.connect(document.graph, parentPort, currentPort, claim, threshold, weight, summary, claimActions);
        arc.setPosition(position);
      }
      
      // for each child call this method again
      if (!idToNodeMap.containsKey(rootId))
      {
    	  for (int i = 0; i < answers.getLength(); i++)
    	  {
    		  Node thisAnswerNode = answers.item(i);
    		  if( thisAnswerNode instanceof Element )
    		  {
    			  Element answerElement = (Element) thisAnswerNode;
    			  if (!answerElement.getAttribute("weight").equals(""))
    			  {
    				  newWeight = Double.parseDouble(answerElement.getAttribute("weight"));	    
    			  }
    			  else
    			  {
    				  newWeight = 0.0;
    			  } 
    			  if (!answerElement.getAttribute("position").equals(""))
    			  {
    				  newPosition = Integer.parseInt(answerElement.getAttribute("position"));	    
    			  }
    			  else
    			  {
    				  newPosition = 0;
    			  } 
    			  if (!answerElement.getAttribute("summary").equals(""))
    			  {
    				  newSummary = answerElement.getAttribute("summary");	    
    			  }
    			  else
    			  {
    				  newSummary = "";
    			  } 
	  	  	  
    			  if (((Element)answerElement.getElementsByTagName("text").item(0)).getFirstChild() != null)
    			  {
    				  newClaim = ((Element)answerElement.getElementsByTagName("text").item(0)).getFirstChild().getNodeValue();	    
    			  }
    			  else
    			  {
    				  newClaim = "";
    			  } 
	  	  
    			  NodeList newActions = answerElement.getElementsByTagName("actions").item(0).getChildNodes();
    			  document = addChildren(((Element)answerElement.getElementsByTagName("destination").item(0)).getAttribute("refid"), 
	                         currentPort,
	                         document,
	                         newClaim,
	                         newWeight,
	                         newPosition,
	                         Double.parseDouble(((Element)answerElement.getElementsByTagName("threshold").item(0)).getFirstChild().getNodeValue()),
	                         newSummary,
	                         newActions);
    		  }
    	  }

    	   // For each notsure build an argument tree      
    	  for (int i = 0; i < notsures.getLength(); i++)
    	  {
    		  Node thisNotSureNode = notsures.item(i);
        
    		  // error here
     
    		  if( thisNotSureNode instanceof Element ){
    			  System.out.print("Adding Argument \n");
    			  document = addArgumentNode(((Element)thisNotSureNode).getAttribute("refid"), document, (JustDrawArgument)document.arguments.get(currentCell), (DecisionTreeNode)currentCell, (ArgumentTreeNode)null);
    			  
    		  }
    	  }
	}
     idToNodeMap.put(rootId, currentCell);	  
    }
    catch (Exception e)
    {
     JustDraw.showErrorDialog("Could not load XML File");
      e.printStackTrace();
    }
    return document;
  }
  
    public JustDrawDocument addArgumentNode(String rootId, JustDrawDocument document, JustDrawArgument argument, DecisionTreeNode parentCell, ArgumentTreeNode parentNode)
    {
    	System.out.print("Adding Argument \n");
       Element root = xmldoc.getElementById(rootId);
      Vector claims = new Vector();
      String prefix, postfix, title, relevance;
      int xpos, ypos;
      ArgumentTreeNode createdNode;
      Port parentPort = null, childPort = null;

      NodeList answers = root.getElementsByTagName("argumentAnswers").item(0).getChildNodes();
      NodeList notsures = root.getElementsByTagName("notSureNodes").item(0).getChildNodes();
      System.out.print( "num of arguments: " + notsures.getLength() + "\n");
      
      if (argument == null)
      {
        argument = new JustDrawArgument(document, parentCell);
	    parentCell.hasArgument = true;
	    
        document.arguments.put(parentCell, argument);
        document.frames.put(parentCell, argument.getFrame());   
        document.parent.desktop.add((JInternalFrame)argument.getFrame());

      }
 
      
      if (parentNode == null)
       parentNode = argument.getRootNode();
      if (root.getElementsByTagName("title").item(0).getFirstChild() != null)
      {
        title = root.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
      }
      else
      {
        title = "";
      }
      
      if (root.getElementsByTagName("relevance").item(0).getFirstChild() != null)
      {
         relevance = root.getElementsByTagName("relevance").item(0).getFirstChild().getNodeValue();
         System.out.print("relevance-->: " + relevance +"\n");
      }
      else
      {
    	 relevance = " ";
      }       
      if (root.getElementsByTagName("prefix").item(0).getFirstChild() != null)
      {
        prefix = root.getElementsByTagName("prefix").item(0).getFirstChild().getNodeValue();
      }
      else
      {
        prefix = "";
      }
      if (root.getElementsByTagName("postfix").item(0).getFirstChild() != null)
      {
        postfix = root.getElementsByTagName("postfix").item(0).getFirstChild().getNodeValue();
      }
      else
      {
        postfix = "";
      }
      
      if (root.getAttribute("xpos") != "")
      {
        xpos = new Double(root.getAttribute("xpos")).intValue();
      }
      else
      {
        xpos = 10;
      }
      if (root.getAttribute("ypos") != "")
      {
        ypos = new Double(root.getAttribute("ypos")).intValue();
      }
      else
      {
        ypos = 10;
      }
      Point2D pos = new Point(xpos, ypos);
      double newWeight;
      String newClaim;
      for (int i = 0; i < answers.getLength(); i++)
      {
        Node thisAnswerNode = answers.item(i);
        if( thisAnswerNode instanceof Element )
        {
          Element answerElement = (Element) thisAnswerNode;
          if (!answerElement.getAttribute("weight").equals(""))
          {
            newWeight = Double.parseDouble(answerElement.getAttribute("weight"));	    
          }
          else
          {
            newWeight = 0.0;
          } 
	  
          if (((Element)answerElement.getElementsByTagName("text").item(0)).getFirstChild() != null)
          {
	    newClaim = ((Element)answerElement.getElementsByTagName("text").item(0)).getFirstChild().getNodeValue();	    
          }
          else
          {
            newClaim = "";
          } 
	  claims.add(new ArgumentClaim(newClaim, new Double(newWeight), new Double(((Element)answerElement.getElementsByTagName("threshold").item(0)).getFirstChild().getNodeValue()))); 
//	  NodeList newActions = answerElement.getElementsByTagName("actions").item(0).getChildNodes();
  /*        document = addChildren( ((Element)answerElement.getElementsByTagName("destination").item(0)).getAttribute("refid"), 
        		 //currentPort,
        		 childPort, 
				 document,
				 newClaim,
				 newWeight,
				 0,
				 Double.parseDouble(((Element)answerElement.getElementsByTagName("threshold").item(0)).getFirstChild().getNodeValue()),
				   "null", newActions );
	*/			   
		/*System.out.print(((Element)answerElement.getElementsByTagName("destination").item(0)).getAttribute("refid") + " " + 
        		 //currentPort,
        		 childPort + " " +
				 document + " " +
				 newClaim  + " " +
				 newWeight + " " +
				 0 + " " +
				 Double.parseDouble(((Element)answerElement.getElementsByTagName("threshold").item(0)).getFirstChild().getNodeValue()) + " " +
				   "null" + " " + newActions);
				   */		 
	}	
      }
      
      
      
      createdNode = JustDrawCellFactory.insertArgumentTreeNode(argument.graph, pos, rootId, title, relevance, prefix, postfix, claims);
    
        System.out.println("parent isnt null");
        // Find port for vertex
        Iterator children = createdNode.getChildren().iterator();
        Object child;
        while (children.hasNext())
        {
          child = children.next();
          if (child instanceof Port)
	  {
  	    childPort = (Port)child;
            System.out.println("child has port");	    
 	  }
        }

	children = parentNode.getChildren().iterator();
        while (children.hasNext())
        {
          child = children.next();
          if (child instanceof Port)
	  {
  	    parentPort = (Port)child;
            System.out.println("parent has port");	    	    
 	  }
        }

	if (parentPort != null && childPort != null)
	{
	  JustDrawCellFactory.connectArguments(argument.graph, childPort, parentPort);
	}
      

            
      for (int i = 0; i < notsures.getLength(); i++)
      {
        Node thisNotSureNode = notsures.item(i);
        if( thisNotSureNode instanceof Element && (rootId != ((Element)thisNotSureNode).getAttribute("refid")))
        {
	            System.out.println("creating child with parent " + createdNode);	
	            // needs a port var
          document = addArgumentNode(((Element)thisNotSureNode).getAttribute("refid"), document, argument, (DecisionTreeNode)null, createdNode);
	}
	else
	{
	
	}
      }      
      return document;
    }
}
