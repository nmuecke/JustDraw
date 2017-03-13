/*
 * @(#)JustDrawCellFactory.java 0.1 14-JUN-04
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
import javax.swing.text.DefaultStyledDocument;

import org.jgraph.*;
import org.jgraph.event.*;
import org.jgraph.graph.*;

import java.lang.Math;

public class JustDrawCellFactory
{
  static public DecisionTreeNode insertDecisionTreeNode(JGraph graph, Point2D point)
  {
    return insertDecisionTreeNode(graph, point, "", "", "", "");
  }
  
  static public DecisionTreeNode insertDecisionTreeNode(JGraph graph, Point2D point, String id, String title)
  {
    return insertDecisionTreeNode(graph, point, id, title, "", "");
  }
  
  static public DecisionTreeNode insertDecisionTreeNode(JGraph graph, Point2D point, String id, String title, String prefix, String suffix)
  {
      // Correction for snapping which doesnt seem to scale correctly all the time
      if (Math.IEEEremainder(point.getX(),10.0) != 0)
      {
        point.setLocation(point.getX() - (Math.IEEEremainder(point.getX(),10.0)), point.getY());
      }
      if (Math.IEEEremainder(point.getY(),10.0) != 0)
      {
        point.setLocation(point.getX(), point.getY() - (Math.IEEEremainder(point.getY(),10.0)));
      }

      // format from screen to model (put it in the right place)
  	  DecisionTreeNode vertex = new DecisionTreeNode(id, title, prefix, suffix);
   		vertex.add(new DefaultPort());
  	  // Create a Map that holds the attributes for the Vertex
	    AttributeMap map = graph.getModel().createAttributes();  
  	  // Snap the Point to the Grid
	    map.createPoint(point);
	    // Default Size for the new Vertex
	    Dimension size = new Dimension(130, 130);
	    // Add a Bounds Attribute to the Map
	    GraphConstants.setBounds(map, map.createRect(point.getX(), point.getY(), size.width, size.height));
	    // Add a Border Color Attribute to the Map
	    GraphConstants.setBorderColor(map, Color.black);
	    // Add a White Background
	    GraphConstants.setBackground(map, Color.white);
	    // Make Vertex Opaque
	    GraphConstants.setOpaque(map, true);
	    // Construct a Map from cells to Maps (for insert)
	    Hashtable attributes = new Hashtable();
	    // Associate the Vertex with its Attributes
	    attributes.put(vertex, map);
	    // Insert the Vertex and its Attributes (can also use model)
	    graph.getGraphLayoutCache().insert(
		  new Object[] { vertex },
		  attributes,
		  null,
		  null,
		  null);
	    return vertex;

  }
 //////// edit nial
  static public DecisionTreeNode insertDecisionTreeNode(JGraph graph, Point2D point, String id, String title, String prefix, String suffix, String relevance, String graph_id, String display_type)
  {
	  //System.out.print("Factory Relevance:" + relevance + "\n" );
      // Correction for snapping which doesnt seem to scale correctly all the time
      if (Math.IEEEremainder(point.getX(),10.0) != 0)
      {
        point.setLocation(point.getX() - (Math.IEEEremainder(point.getX(),10.0)), point.getY());
      }
      if (Math.IEEEremainder(point.getY(),10.0) != 0)
      {
        point.setLocation(point.getX(), point.getY() - (Math.IEEEremainder(point.getY(),10.0)));
      }

      // format from screen to model (put it in the right place)
  	  DecisionTreeNode vertex = new DecisionTreeNode(id, title, prefix, suffix, relevance, graph_id, display_type);
   		vertex.add(new DefaultPort());
  	  // Create a Map that holds the attributes for the Vertex
	    AttributeMap map = graph.getModel().createAttributes();  
  	  // Snap the Point to the Grid
	    map.createPoint(point);
	    // Default Size for the new Vertex
	    Dimension size = new Dimension(130, 130);
	    // Add a Bounds Attribute to the Map
	    GraphConstants.setBounds(map, map.createRect(point.getX(), point.getY(), size.width, size.height));
	    // Add a Border Color Attribute to the Map
	    GraphConstants.setBorderColor(map, Color.black);
	    // Add a White Background
	    GraphConstants.setBackground(map, Color.white);
	    // Make Vertex Opaque
	    GraphConstants.setOpaque(map, true);
	    // Construct a Map from cells to Maps (for insert)
	    Hashtable attributes = new Hashtable();
	    // Associate the Vertex with its Attributes
	    attributes.put(vertex, map);
	    // Insert the Vertex and its Attributes (can also use model)
	    graph.getGraphLayoutCache().insert(
		  new Object[] { vertex },
		  attributes,
		  null,
		  null,
		  null);
	    return vertex;
  }
 ///////
    static public JTextPane insertText(JGraph graph, Point2D point, String text)
  {
      // Correction for snapping which doesnt seem to scale correctly all the time
      if (Math.IEEEremainder(point.getX(),10.0) != 0)
      {
        point.setLocation(point.getX() - (Math.IEEEremainder(point.getX(),10.0)), point.getY());
      }
      if (Math.IEEEremainder(point.getY(),10.0) != 0)
      {
        point.setLocation(point.getX(), point.getY() - (Math.IEEEremainder(point.getY(),10.0)));
      }

      // format from screen to model (put it in the right place)
  	  JTextPane textPane = new JTextPane(new DefaultStyledDocument());
	  textPane.setText(text);
   	//	textPane.add(new DefaultPort());
  	  // Create a Map that holds the attributes for the Vertex
	    AttributeMap map = graph.getModel().createAttributes();  
  	  // Snap the Point to the Grid
	    map.createPoint(point);
	    // Default Size for the new Vertex
	    Dimension size = new Dimension(130, 130);
	    // Add a Bounds Attribute to the Map
	    GraphConstants.setBounds(map, map.createRect(point.getX(), point.getY(), size.width, size.height));
	    // Add a Border Color Attribute to the Map
	    GraphConstants.setBorderColor(map, Color.black);
	    // Add a White Background
	    GraphConstants.setBackground(map, Color.white);
	    // Make Vertex Opaque
	    GraphConstants.setOpaque(map, true);
	    // Construct a Map from cells to Maps (for insert)
	    Hashtable attributes = new Hashtable();
	    // Associate the Vertex with its Attributes
	    attributes.put(textPane, map);
	    // Insert the Vertex and its Attributes (can also use model)
	    graph.getGraphLayoutCache().insert(
		  new Object[] { textPane },
		  attributes,
		  null,
		  null,
		  null);
	    return textPane;

  }
  
  static public ArgumentTreeNode insertArgumentTreeNode(JGraph graph, Point2D point)
  {
    return insertArgumentTreeNode(graph, point, "", "", "", "", "", null);
  }
  
  static public ArgumentTreeNode insertArgumentTreeNode(JGraph graph, Point2D point, String id, String title, String relevance, String prefix, String suffix, Vector claims)
  {
      // Correction for snapping which doesnt seem to scale correctly all the time
      if (Math.IEEEremainder(point.getX(),10.0) != 0)
      {
        point.setLocation(point.getX() - (Math.IEEEremainder(point.getX(),10.0)), point.getY());
      }
      if (Math.IEEEremainder(point.getY(),10.0) != 0)
      {
        point.setLocation(point.getX(), point.getY() - (Math.IEEEremainder(point.getY(),10.0)));
      }

      // format from screen to model (put it in the right place)
      
  	  ArgumentTreeNode vertex = new ArgumentTreeNode(id, prefix, suffix, title, relevance, "", "");
	  vertex.setClaims(claims);
   		vertex.add(new DefaultPort());
  	  // Create a Map that holds the attributes for the Vertex
	    AttributeMap map = graph.getModel().createAttributes();  
  	  // Snap the Point to the Grid
	    map.createPoint(point);
	    // Default Size for the new Vertex
	    Dimension size = new Dimension(230, 130);
	    // Add a Bounds Attribute to the Map
	    GraphConstants.setBounds(map, map.createRect(point.getX(), point.getY(), size.width, size.height));
	    // Add a Border Color Attribute to the Map
	    GraphConstants.setBorderColor(map, Color.black);
	    // Add a White Background
	    GraphConstants.setBackground(map, Color.white);
	    // Make Vertex Opaque
	    GraphConstants.setOpaque(map, true);
	    // Construct a Map from cells to Maps (for insert)
	    Hashtable attributes = new Hashtable();
	    // Associate the Vertex with its Attributes
	    attributes.put(vertex, map);
	    // Insert the Vertex and its Attributes (can also use model)
	    graph.getGraphLayoutCache().insert(
		  new Object[] { vertex },
		  attributes,
		  null,
		  null,
		  null);
	    return vertex;
  }
  
  static public ConclusionNode insertConclusionNode(JGraph graph, Point2D point)
  {
    return insertConclusionNode(graph, point, "", "", "", "", "", "", "radio");
  }
  
  static public ConclusionNode insertConclusionNode(JGraph graph, Point2D point, String id, String title)
  {
    return insertConclusionNode(graph, point, id, title, "", "", "", "", "radio");  
  }
  
  
  static public ConclusionNode insertConclusionNode(JGraph graph, Point2D point, String id, String title, String prefix, String suffix)
  {
    return insertConclusionNode(graph, point, id, title, prefix, suffix, "", "", "radio");  
  }
  
  static public ConclusionNode insertConclusionNode(JGraph graph, Point2D point, String id, String title, String prefix, String suffix, String relevance, String graph_id, String display_type)
  {  
      // Correction for snapping which doesnt seem to scale correctly all the time
      if (Math.IEEEremainder(point.getX(),10.0) != 0)
      {
        point.setLocation(point.getX() - (Math.IEEEremainder(point.getX(),10.0)), point.getY());
      }
      if (Math.IEEEremainder(point.getY(),10.0) != 0)
      {
        point.setLocation(point.getX(), point.getY() - (Math.IEEEremainder(point.getY(),10.0)));
      }

      // format from screen to model (put it in the right place)
  	  ConclusionNode vertex = new ConclusionNode(id, title, prefix, suffix, relevance, graph_id, display_type);
   		vertex.add(new DefaultPort());
  	  // Create a Map that holds the attributes for the Vertex
	    AttributeMap map = graph.getModel().createAttributes();  
  	  // Snap the Point to the Grid
	    map.createPoint(point);
	    // Default Size for the new Vertex
	    Dimension size = new Dimension(130, 130);
	    // Add a Bounds Attribute to the Map
	    GraphConstants.setBounds(map, map.createRect(point.getX(), point.getY(), size.width, size.height));
	    // Add a Border Color Attribute to the Map
	    GraphConstants.setBorderColor(map, Color.black);
	    // Add a White Background
	    GraphConstants.setBackground(map, Color.white);
	    // Make Vertex Opaque
	    GraphConstants.setOpaque(map, true);
	    // Construct a Map from cells to Maps (for insert)
	    Hashtable attributes = new Hashtable();
	    // Associate the Vertex with its Attributes
	    attributes.put(vertex, map);	    
	    // Insert the Vertex and its Attributes (can also use model)
	    graph.getGraphLayoutCache().insert(
		  new Object[] { vertex },
		  attributes,
		  null,
		  null,
		  null);
		  
	    return vertex;

  }
  
  	// Insert a new Edge between source and target
	static public DecisionTreeArc connect(JGraph graph, Port source, Port target)
        {
	  return connect(graph, source, target, "", 0.0, 0.0, "", null);
        }
	
	static public DecisionTreeArc connect(JGraph graph, Port source, Port target, String claim)
	{
	  return connect(graph,source, target, claim, 0.0, 0.0, "", null);
	}
	
  	static public DecisionTreeArc connect(JGraph graph, Port source, Port target, String claim, double threshold, double weight, String summary, Vector actions)
        {
	
	        // Determine if there are other connections from this port to the target
		int connectionNumber = 0;
		Iterator i = source.edges();
		Edge currentEdge;
		while (i.hasNext())
		{
		  currentEdge = (Edge)i.next();
		  if (currentEdge.getTarget() == target)
		  {
		    connectionNumber++;	    
		  }
		}
		
		
		// Connections that will be inserted into the Model
		ConnectionSet cs = new ConnectionSet();
		// Construct Edge with no label
		DecisionTreeArc edge = new DecisionTreeArc(claim, threshold, weight, summary, actions, connectionNumber);

		// Create Connection between source and target using edge
		cs.connect(edge, source, target);
		// Create a Map thath holds the attributes for the edge
		Map map = graph.getModel().createAttributes();
		// Add a Line End Attribute
		// GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_SIMPLE);
		// Add a label along edge attribute
		GraphConstants.setLabelAlongEdge(map, true);
		// Construct a Map from cells to Maps (for insert)
		Hashtable attributes = new Hashtable();
		// Associate the Edge with its Attributes
		attributes.put(edge, map);

		// Insert the Edge and its Attributes
		graph.getGraphLayoutCache().insert(
			new Object[] { edge },
			attributes,
			cs,
			null,
			null);
	  return edge;
	}

  	static public ArgumentArc connectArguments(JGraph graph, Port source, Port target)
        {
		// Connections that will be inserted into the Model
		ConnectionSet cs = new ConnectionSet();
		// Construct Edge with no label
		ArgumentArc edge = new ArgumentArc();

		// Create Connection between source and target using edge
		cs.connect(edge, source, target);
		// Create a Map thath holds the attributes for the edge
		Map map = graph.getModel().createAttributes();
		// Add a Line End Attribute
		// GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_SIMPLE);
		// Add a label along edge attribute
		GraphConstants.setLabelAlongEdge(map, true);
		// Construct a Map from cells to Maps (for insert)
		Hashtable attributes = new Hashtable();
		// Associate the Edge with its Attributes
		attributes.put(edge, map);

		// Insert the Edge and its Attributes
		graph.getGraphLayoutCache().insert(
			new Object[] { edge },
			attributes,
			cs,
			null,
			null);
	  return edge;
	}

}
