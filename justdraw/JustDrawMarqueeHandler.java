/*
 * @(#)JustDrawMarqueeHandler.java 0.1 14-JUN-04
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

import java.beans.PropertyVetoException;

import java.lang.Math;

public class JustDrawMarqueeHandler extends BasicMarqueeHandler
{
  protected JGraph graph;
  // ** Need a better abstraction for the toolbar maybe? **
  protected JustDraw parent;
  private PortView startPort;
  private Point2D startLocation;
  private PortView endPort;
  private Point2D endLocation;

  public JustDrawMarqueeHandler(JGraph graph, JustDraw parent)
  {
    super();
    this.graph = graph;
    this.parent = parent;
  }


  public boolean isForceMarqueeEvent(MouseEvent e)
  {
    // When do we run these handlers
    // We need to switch graph back on any click
    if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isRightMouseButton(e))
    {
      parent.document.changeGraph(graph);
    }
    if (SwingUtilities.isLeftMouseButton(e) && parent.editMode.equals("insertClaim")) return true;
    if (SwingUtilities.isRightMouseButton(e)) return true;
    if (e.getClickCount() > 1) return true;
    super.isForceMarqueeEvent(e);
    return false;
  }
 
  public void mousePressed(MouseEvent e)
  {
    graph.requestFocus();
    parent.document.changeGraph(graph);

    if (SwingUtilities.isRightMouseButton(e))
    {
			Point2D loc = graph.fromScreen((Point2D) e.getPoint().clone());
		  // Find Cell in Model Coordinates
		  Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());

  		JPopupMenu menu = createPopupMenu(e.getPoint(), cell, graph);
			// Display PopupMenu
			menu.show(graph, e.getX(), e.getY());
    }
    else if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)
    {
     	Point2D loc = graph.fromScreen((Point2D) e.getPoint().clone());
	// Find Cell in Model Coordinate
	Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());
        parent.makePropertiesDialogue(cell);
    }
    // Insert a decision tree node
    else if (SwingUtilities.isLeftMouseButton(e) && parent.editMode.equals("insertDecision"))
    {
      Point2D point = graph.fromScreen(graph.snap((Point2D) new Point(e.getX(), e.getY())));
      JustDrawCellFactory.insertDecisionTreeNode(graph, point);
      parent.toMoveMode();
    }
    // Insert a decision tree conclusion node 
    else if (SwingUtilities.isLeftMouseButton(e) && parent.editMode.equals("insertConclusion"))
    {
      Point2D point = graph.fromScreen(graph.snap((Point2D) new Point(e.getX(), e.getY())));
      JustDrawCellFactory.insertConclusionNode(graph, point);
      parent.toMoveMode();
    }
    else if (SwingUtilities.isLeftMouseButton(e) && parent.editMode.equals("insertText"))
    {
      Point2D point = graph.fromScreen(graph.snap((Point2D) new Point(e.getX(), e.getY())));
      JustDrawCellFactory.insertText(graph, point, "");
      parent.toMoveMode();
    }
    
    else if (SwingUtilities.isLeftMouseButton(e) && parent.editMode.equals("insertArgument"))
    {
      Point2D point = graph.fromScreen(graph.snap((Point2D) new Point(e.getX(), e.getY())));
      JustDrawCellFactory.insertArgumentTreeNode(graph, point);
      parent.toMoveMode();
    }
    else if (graph.isPortsVisible() && SwingUtilities.isLeftMouseButton(e) && parent.editMode.equals("insertClaim"))
    {
      PortView port = getSourcePortAt(e.getPoint());
			// Remember Start Location
      if (port != null)
      {
		  	startLocation = graph.toScreen(port.getLocation(null));
		  	// Remember First Port
		  	startPort = port;
      }
		}
    else if (SwingUtilities.isLeftMouseButton(e) && parent.editMode.equals("move"))
    {
       super.mousePressed(e);
    }

	}

  public void mouseReleased(MouseEvent e)
  { 
    if (startPort != null && endPort != null && parent.editMode.equals("insertClaim"))
    {
      // Connect ... but not to self
      if (startPort != endPort)
      {
        connect ((Port)startPort.getCell(), (Port)endPort.getCell());
      }
      startLocation = null;
      startPort = null;
      endLocation = null;
      endPort = null;
      parent.toMoveMode();
    }
    else if (startPort != null && endPort == null && parent.editMode.equals("insertClaim"))
    {
			paintConnector(Color.black, graph.getBackground(), graph.getGraphics());
      startLocation = null;
      startPort = null;
      endLocation = null;
      endPort = null;
    }
    else if (parent.editMode.equals("move") && SwingUtilities.isLeftMouseButton(e))
    {
      super.mouseReleased(e);
    }
  }  

  public void mouseDragged(MouseEvent e)
  {

  	// If remembered Start Point is Valid
		if (startLocation != null && parent.editMode.equals("insertClaim"))
    {
      PortView port;
		  // Fetch Graphics from Graph
			Graphics g = graph.getGraphics();
			// Xor-Paint the old Connector (Hide old Connector)
			paintConnector(Color.black, graph.getBackground(), g);
			// Reset Remembered Port
			port =  getTargetPortAt(e.getPoint());
			// If Port was found then Point to Port Location
			if (port != null)
      {
				endLocation = graph.toScreen(port.getLocation(null));
        endPort = port;
      }
			// Else If no Port was found then Point to Mouse Location
			else
      {
				endLocation = graph.snap(e.getPoint());
        endPort = null;
      }
			// Xor-Paint the new Connector
			paintConnector(graph.getBackground(), Color.black, g);
		}
    else if (parent.editMode.equals("move") && SwingUtilities.isLeftMouseButton(e))
    {
      super.mouseDragged(e);
    }
	}

  // Create context sensitive pop-up menu
	protected JPopupMenu createPopupMenu(final Point pt, final Object cell, final JGraph g)
  {
	  JPopupMenu menu = new JPopupMenu();
		if (cell != null)
    {
       if (cell instanceof DecisionTreeNode)
       {
         if(parent.document.arguments.get(cell) != null)
	 {	  // Remove argument
    	   menu.add(new AbstractAction("Remove Argument")
      	   {
  	     public void actionPerformed(ActionEvent ev)
             {
	       // Remove argument Nodes for this frame
	       parent.document.arguments.remove(cell);
	       ((JInternalFrame)parent.document.frames.get(cell)).dispose();
	       parent.document.frames.remove(cell);
	       ((DecisionTreeNode)cell).removeArgument();
	       Object blah[] = new Object[]{cell};
               graph.getModel().toFront(blah);	       
	     }
           });
	   menu.add(new AbstractAction("View Argument")
      	   {
  	     public void actionPerformed(ActionEvent ev)
             {
	       try
	       {
	         ((JInternalFrame)parent.document.frames.get(cell)).setIcon(false);
	       }
	       catch (PropertyVetoException ex)
	       {
	       }
	       ((JInternalFrame)parent.document.frames.get(cell)).moveToFront();
	       ((JInternalFrame)parent.document.frames.get(cell)).requestFocus();
	     }
           });
	 }
	 else
	 {	  // Add argument
    	   menu.add(new AbstractAction("Add Argument")
      	   {
  	     public void actionPerformed(ActionEvent ev)
             {
	       ((DecisionTreeNode)cell).addArgument();
               Map editEvent = new Hashtable();
               AttributeMap attr =  ((DecisionTreeNode)cell).getAttributes();
               editEvent.put(cell, attr); // ** This edit event exists but isn't handled!
               parent.document.graph.getModel().edit(editEvent, null, null, null);
               parent.document.arguments.put(cell, new JustDrawArgument(parent.document, (DecisionTreeNode)cell));
	       // ** Must add frame to hashtable
	       parent.document.frames.put(cell, ((JustDrawArgument)parent.document.arguments.get(cell)).getFrame());
	       // Add parent of graph to this tree	  
               parent.desktop.add((JInternalFrame)parent.document.frames.get(cell));
	       parent.document.changeGraph(((JustDrawArgument)parent.document.arguments.get(cell)).graph);
	       // ** clean this up
	       ((JInternalFrame)parent.document.frames.get(cell)).moveToFront();
               ((JInternalFrame)parent.document.frames.get(cell)).requestFocus();
	       try
	       {	  
	         ((JInternalFrame)parent.document.frames.get(cell)).setIcon(false);
	       }
	       catch (Exception ex)
	       {
     	    
	       }
	     }
           });

	 }	 
                  
         if (((DecisionTreeNode)cell).isRoot())
	 {
	   menu.add(new AbstractAction("Unset as Root")
	   {
	     public void actionPerformed(ActionEvent e)
	     {
	       ((DecisionTreeNode)cell).setRoot(false);
	     }
	   });
	   menu.addSeparator();
	 }
	 else
	 {
           menu.add(new AbstractAction("Set as Root")
	   {
	     public void actionPerformed(ActionEvent e)
	     {
	       ((DecisionTreeNode)cell).setRoot(true);
	     }
	   });
	   menu.addSeparator();
	 }
       }
       if (cell instanceof ConclusionNode)
       {
         menu.add(new AbstractAction("Mutate to DT Node")
         {
	   public void actionPerformed(ActionEvent e)
           {
	     DecisionTreeNode dtClone;
             // Create DT Node with same attributes
	     if (((ConclusionNode)cell).getPrefix().equals("") && ((ConclusionNode)cell).getPrefix().equals(""))
	     {
	       dtClone = JustDrawCellFactory.insertDecisionTreeNode( graph,
	                                                   new Point2D.Double(graph.getGraphLayoutCache().getMapping(cell, false).getBounds().getX(),graph.getGraphLayoutCache().getMapping(cell, false).getBounds().getY()),
							   ((ConclusionNode)cell).getId(), 
							   ((ConclusionNode)cell).getTitle(),
							   ((ConclusionNode)cell).getPrefix(),
							   ((ConclusionNode)cell).getSuffix());
	       
	     }
	     else
	     {
	       dtClone = JustDrawCellFactory.insertDecisionTreeNode( graph,
	                                                   new Point2D.Double(graph.getGraphLayoutCache().getMapping(cell, false).getBounds().getX(),graph.getGraphLayoutCache().getMapping(cell, false).getBounds().getY()),
							   ((ConclusionNode)cell).getId(), 
							   ((ConclusionNode)cell).getPrefix() + ((ConclusionNode)cell).getSuffix(),
							   ((ConclusionNode)cell).getPrefix(),
							   ((ConclusionNode)cell).getSuffix());	     
	     }
	     // Connect Ports to created DT Node
	     Iterator i;
	     
	     Iterator edges;
	     Object port;
	     Object targetPort = null;
	     Edge currentEdge;
	     
	     i = dtClone.getChildren().iterator();
	     while(i.hasNext())
	     {
	       port = i.next();
	       if (port instanceof Port)
	       {
	         targetPort = port;
	       }
	     }
	     port = null;
	     
	     i = ((ConclusionNode)cell).getChildren().iterator();
	     while (i.hasNext())
	     {
	       port = i.next();
	       if (port instanceof Port && targetPort != null)
	       {
	        edges = ((Port)port).edges();
	        while (edges.hasNext())
	        {
	          currentEdge = (Edge)edges.next();
		  if (currentEdge.getSource().equals(cell))
		  {
		    // Theoretically should never happen
		    currentEdge.setSource(targetPort);	    
                  }
		  else
		  {
		    currentEdge.setTarget(targetPort);
		  }
		}
	       }       
	     }
	     
	     // Remove Conclusion Node
            Object[] cells = new Object[1];
            cells[0] = cell;
	    if (parent.document.arguments.get(cell) != null)
	    {
	      // Remove argument Nodes for this frame
	      parent.document.arguments.remove(cell);
	      ((JInternalFrame)parent.document.frames.get(cell)).dispose();
	      parent.document.frames.remove(cell);
	    }
            cells = g.getDescendants(cells);
            g.getModel().remove(cells);
	   }
         });
         menu.addSeparator();
       }
		  // Remove
      menu.add(new AbstractAction("Remove")
      {
				public void actionPerformed(ActionEvent e)
        {
          Object[] cells = new Object[1];
          cells[0] = cell;
	  if (parent.document.arguments.get(cell) != null)
	  {
	    // Remove argument Nodes for this frame
	    parent.document.arguments.remove(cell);
	    ((JInternalFrame)parent.document.frames.get(cell)).dispose();
	    parent.document.frames.remove(cell);
	  }
          cells = g.getDescendants(cells);
          g.getModel().remove(cells);
				}
			});


  		// Properties
		  menu.add(new AbstractAction("Properties")
      {
		  	public void actionPerformed(ActionEvent ev)
        {
          parent.makePropertiesDialogue(cell);
			  }
		  });

      menu.addSeparator();
    }

 	  // Graph Properties
    menu.add(new AbstractAction("Graph Properties")
      {
  	  public void actionPerformed(ActionEvent ev)
      {
  		  parent.makePropertiesDialogue(parent.document);
	    }
    });
		return menu;
	}



		  // Remove
  protected PortView getSourcePortAt(Point2D point)
  {
	  // Scale from Screen to Model
		Point2D tmp = graph.fromScreen((Point2D) point.clone());
		// Find a Port View in Model Coordinates and Remember
		return graph.getPortViewAt(tmp.getX(), tmp.getY());
	}

	protected PortView getTargetPortAt(Point2D point)
  {
			// Find Cell at point (No scaling needed here)
			Object cell = graph.getFirstCellForLocation(point.getX(), point.getY());
			// Loop Children to find PortView
			for (int i = 0; i < graph.getModel().getChildCount(cell); i++)
      {
				// Get Child from Model
				Object tmp = graph.getModel().getChild(cell, i);
				// Get View for Child using the Graph's View as a Cell Mapper
				tmp = graph.getGraphLayoutCache().getMapping(tmp, false);
				// If Child View is a Port View and not equal to First Port
				if (tmp instanceof PortView && tmp != startPort)
					// Return as PortView
					return (PortView) tmp;
	    }
			return getSourcePortAt(point);
	}

		// Use Xor-Mode on Graphics to Paint Connector
		protected void paintConnector(Color fg, Color bg, Graphics g)
    {
			// Set Foreground
			g.setColor(fg);
			// Set Xor-Mode Color
			g.setXORMode(bg);
			// Highlight the Current Port
			paintPort(graph.getGraphics());
			// If Valid First Port, Start and Current Point
			if (startPort != null && startLocation != null && endLocation != null)
				// Then Draw A Line From Start to Current Point
				g.drawLine((int) startLocation.getX(),
						   (int) startLocation.getY(),
						   (int) endLocation.getX(),
						   (int) endLocation.getY());
		}

		protected void paintPort(Graphics g)
    {
			// If Current Port is Valid
			if (endPort != null)
      {
				// If Not Floating Port...
				boolean o = (GraphConstants.getOffset(endPort.getAttributes()) != null);
				// ...Then use Parent's Bounds
				Rectangle2D r =	(o) ? endPort.getBounds() : endPort.getParentView().getBounds();
				// Scale from Model to Screen
				r = graph.toScreen((Rectangle2D) r.clone());
				// Add Space For the Highlight Border
				r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6);
				// Paint Port in Preview (=Highlight) Mode
				graph.getUI().paintCell(g, endPort, r, true);
			}
		}

	// Insert a new Edge between source and target
	protected void connect(Port source, Port target)
  	{
	if (parent.connectArgumentButton.isSelected())
	  {
	    JustDrawCellFactory.connectArguments(graph, source, target);
  	  }
	  else
	  {
  	    JustDrawCellFactory.connect(graph, source, target);
	  }
	}
}
