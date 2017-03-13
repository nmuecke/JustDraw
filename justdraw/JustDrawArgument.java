/*
 * @(#)JustDrawArgument.java 0.1 14-JUN-04
 * 
 * Copyright (c) 2004, John Avery All rights reserved.
 * Portions Copyright (c) 2001-2004, Gaudenz Alder All rights reserved.
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

public class JustDrawArgument
{
  public String name;
  protected JInternalFrame frame;
  protected JGraph graph;          // Current Graph
  protected final JustDrawDocument parent;
  private ArgumentTreeNode rootNode;

  public JustDrawArgument(final JustDrawDocument parent, DecisionTreeNode parentCell)
  {    
    if (parentCell.getId().equals("")) name = "annoymous DT node";
    this.parent = parent;
    this.name = "Argument Tree for " + parentCell.getId() + " in " + parent.getName();
    frame = new JInternalFrame(this.name, true, true, true, true);
    frame.addInternalFrameListener(new InternalFrameAdapter(){
      public void internalFrameActivated(InternalFrameEvent e)
      {
        parent.changeGraph(graph);
      }
    });
    frame.setClosable(false);
    graph = new JGraph(new DefaultGraphModel())
    {
      protected VertexView createVertexView(JGraph graph,CellMapper cm, Object v )
      {
        if (v instanceof DecisionTreeNode)
        {
          return new DecisionNodeView(v, this, cm);
        }
        if (v instanceof ArgumentTreeNode)
        {
          return new ArgumentNodeView(v, this, cm);
        }

        return super.createVertexView(graph, cm, v);
      }
    };

    // Use a Custom Marquee Handler
    graph.setMarqueeHandler(new JustDrawMarqueeHandler(graph, parent.parent));
    // Tell the Graph to Select new Cells upon Insertion
    graph.setScale(0.75);
    graph.setSelectNewCells(true);
    graph.setPortsVisible(true);
    graph.setGridEnabled(true);
    graph.setGridSize(1);
    graph.setTolerance(15);
    graph.setInvokesStopCellEditing(true);
    graph.setAntiAliased(false);
    graph.setSizeable(false);
    graph.setBendable(false);
    graph.setInvokesStopCellEditing(true);
    graph.addGraphSelectionListener(new GraphSelectionListener()
    {
      int removecount = 0;
      public void valueChanged(GraphSelectionEvent e) 
      {
        removecount = 0;
        // Determine how many of the selectinEvent nodes are removed
        for (int i = 0; i < e.getCells().length; i++)
          if (!e.isAddedCell(i)) removecount++;

        System.out.println(e.getCells().length + " - " + removecount);

        // If there is only one cell selected that isn't a removed cell we can show properties 
        if ((e.getCells().length - removecount) == 1 && parent.parent.properties.isVisible())
        {
          parent.parent.makePropertiesDialogue(e.getCell());
        }
        else if ((e.getCells().length - removecount) == 0 && parent.parent.properties.isVisible())
        {
          parent.parent.makePropertiesDialogue("No cells selected.");
        }
        else if (parent.parent.properties.isVisible())
        {
          System.out.println("Multiple selected");
          parent.parent.makePropertiesDialogue("Multiple cells selected.");
        }
      }
    });
    Vector claims = new Vector();
    // Find port for vertex
    Iterator children = parentCell.getChildren().iterator();
    Object child;
    Port currentPort = null;
    while (children.hasNext())
    {
      child = children.next();
      if (child instanceof Port)
      {
        currentPort = (Port)child;
      }
    }
    
    DecisionTreeArc claim;
    if (currentPort != null)
    {
      children = currentPort.edges();
      while (children.hasNext())
      {
        claim = (DecisionTreeArc)children.next();
	if (((DefaultPort)claim.getSource()).getParent() == parentCell)
        {
	  claims.add(new ArgumentClaim(claim.getClaim(),
	   	     new Double(claim.getWeight()),
		     new Double (claim.getThreshold())));  
	}	
      }
    }
    
    rootNode = JustDrawCellFactory.insertArgumentTreeNode(graph, new Point(250,10), parentCell.getId(), parentCell.getTitle() ,parentCell.getRelevance() ,parentCell.getPrefix() , parentCell.getSuffix() , claims);
    rootNode.setRoot(true);
    rootNode.setPrefix(rootNode.getPrefix()); // force a redraw of title
    // Use decision trees undo manager
    graph.getModel().addUndoableEditListener(parent.undoManager);
    frame.getContentPane().add(new JScrollPane(graph), BorderLayout.CENTER);
    frame.pack();
    frame.setSize(500,400);
    frame.show();
  }

  // Update Undo/Redo Button State based on Undo Manager
  protected void updateHistoryButtons()
  {
    // The View Argument Defines the Context
    parent.parent.undo.setEnabled(parent.undoManager.canUndo(graph.getGraphLayoutCache()));
    parent.parent.redo.setEnabled(parent.undoManager.canRedo(graph.getGraphLayoutCache()));
  }

  public String getName()
  {
    return name;
  }

  public JInternalFrame getFrame()
  {
    return frame;
  }

  public JGraph getGraph()
  {
    return graph;
  }

  public ArgumentTreeNode getRootNode()
  {
    return rootNode;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setFrame(JInternalFrame frame)
  {
    this.frame = frame;
  }

  public void setGraph(JGraph graph)
  {
    this.graph = graph;
  }
}
