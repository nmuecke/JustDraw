/*
 * @(#)JustDraw.java 0.1 14-JUN-04
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

public class JustDrawDocument 
{
  public String name;
  public String docPrefix;
  protected JInternalFrame frame;  // Current Frame
  protected JGraph graph;          // Current Graph
  protected JGraph rootGraph;
  protected Hashtable arguments;   // Map between nodes and child graphs (arguments)
  protected Hashtable frames;      // Map between nodes and child frames (root node mapped to main frame ** but not yet)
  protected final JustDraw parent;
  protected GraphUndoManager undoManager;

  public JustDrawDocument(final JustDraw parent)
  {    
    this(parent, "New Decision Tree", "");
  }
  
  public JustDrawDocument(final JustDraw parent, String name, String docPrefix)
  {
    this.parent = parent;
    this.name = name;
    this.docPrefix = docPrefix;
    
    frame = new JInternalFrame(name, true, true, true, true);
    //frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
    frame.addInternalFrameListener(new InternalFrameAdapter()
    {
      public void internalFrameClosed(InternalFrameEvent e)
      {
        // ** Saving dialog also needed here
	System.out.println("Main Frame closed");
        Iterator i = frames.values().iterator();
	while (i.hasNext())
	{
	  ((JInternalFrame)i.next()).dispose();
	}
	    parent.exportCSVMenuItem.setEnabled(false);
	    parent.exportXMLMenuItem.setEnabled(false);
	    parent.exportMySQLMenuItem.setEnabled(false);	
        parent.importXMLMenuItem.setEnabled(true);	
  //      parent.importMySQLMenuItem.setEnabled(true);
        parent.newMenuItem.setEnabled(true);		
      }
      public void internalFrameActivated(InternalFrameEvent e)
      {
        changeGraph(rootGraph);
      }
    });
    arguments = new Hashtable();
    frames = new Hashtable();
    graph = new JGraph(new DefaultGraphModel())
    {
      protected VertexView createVertexView(JGraph graph,CellMapper cm, Object v )
      {
        if (v instanceof ConclusionNode)
        {
          return new ConclusionNodeView(v, this, cm);
        }
        if (v instanceof ArgumentTreeNode)
        {
          return new ArgumentNodeView(v, this, cm);
        }

        else if (v instanceof DecisionTreeNode)
        {
          return new DecisionNodeView(v, this, cm);
        }	
        return super.createVertexView(graph, cm, v);
      }
      
      protected EdgeView createEdgeView(JGraph graph,CellMapper cm, Object v)
      {
	if (v instanceof DecisionTreeArc)
        {
          return new TreeArcView(v, this, cm);
        }	
        return super.createEdgeView(graph, cm, v);
      }    
    };

    // Use a Custom Marquee Handler
    graph.setMarqueeHandler(new JustDrawMarqueeHandler(graph, parent));
    // Tell the Graph to Select new Cells upon Insertion
    graph.setScale(0.75);
    graph.setSelectNewCells(true);
    graph.setPortsVisible(true);
    graph.setGridEnabled(true);
    graph.setGridSize(20);
    graph.setTolerance(15);
    graph.setInvokesStopCellEditing(true);
    graph.setAntiAliased(false);
    graph.setSizeable(true);
    graph.setBendable(true);
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
        if ((e.getCells().length - removecount) == 1 && parent.properties.isVisible())
        {
          parent.makePropertiesDialogue(e.getCell());
        }
        else if ((e.getCells().length - removecount) == 0 && parent.properties.isVisible())
        {
          parent.makePropertiesDialogue("No cells selected.");
        }
        else if (parent.properties.isVisible())
        {
          parent.makePropertiesDialogue("Multiple cells selected.");
        }
      }
    });

    // Undo manager
    undoManager = new GraphUndoManager()
    {
      // Override Superclass
      public void undoableEditHappened(UndoableEditEvent e)
      {
        // First Invoke Superclass
        super.undoableEditHappened(e);
        // Then Update Undo/Redo Buttons
         updateHistoryButtons();
     };
    };
    graph.getModel().addUndoableEditListener(undoManager);
    rootGraph = graph;

    frame.getContentPane().add(new JScrollPane(graph), BorderLayout.CENTER);
    frame.pack();
    frame.setSize(800,600);
    frame.show();
  }

  // Undo the last Change to the Model or the View
  public void undo()
  {
    try
    {
      undoManager.undo(graph.getGraphLayoutCache());
    }
    catch (Exception ex)
    {
      System.err.println(ex);
    }
    finally
    {
       updateHistoryButtons();
    }
  }

  // Redo the last Change to the Model or the View
  public void redo()
  {
    try
    {
      undoManager.redo(graph.getGraphLayoutCache());
    } 
    catch (Exception ex)
    {
      System.err.println(ex);
    }
    finally
    {
      updateHistoryButtons();
    }
  }

  // Update Undo/Redo Button State based on Undo Manager
  protected void updateHistoryButtons()
  {
    // The View Argument Defines the Context
    parent.undo.setEnabled(undoManager.canUndo(graph.getGraphLayoutCache()));
    parent.redo.setEnabled(undoManager.canRedo(graph.getGraphLayoutCache()));
  }

  // A new grah has become the current graph, so unselect cells from old graph and remake properties dialogue
  protected void changeGraph(JGraph newGraph)
  {
    if (!newGraph.equals(graph)) 
    {

      graph.getSelectionModel().clearSelection();
      graph = newGraph;
      Object cells[] = newGraph.getSelectionCells();
      graph.requestFocus();
      if (parent.properties.isVisible())
      {
        if (cells.length > 1)
        {
          parent.makePropertiesDialogue("Multiple cells selected.");
        }
        else if (cells.length < 1)
        {
          parent.makePropertiesDialogue("No cells selected.");
        }
        else
        {
          parent.makePropertiesDialogue(cells[0]);
        }
      }
      // ** Will have to do do this better when dt's can be part or arguments
      if (newGraph != rootGraph)
      {
        parent.argumentSelected();
      }
      else
      {
        parent.dtSelected();
      }
    }
  }

  public String getName()
  {
    return name;
  }
  
  public String getDocPrefix()
  {
    return docPrefix;
  }
  
  public JInternalFrame getFrame()
  {
    return frame;
  }

  public JGraph getGraph()
  {
    return graph;
  }

  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setDocPrefix(String prefix)
  {
    this.docPrefix = prefix;
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
