/*
 * @(#)TreeArcView.java	1.0 1.2 11/11/02
 *
 * Copyright (C) 2001 Gaudenz Alder
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package justdraw;

import java.awt.*;
import java.util.List;
import java.util.Vector;
import java.util.Map;

import java.lang.Math;

import java.awt.geom.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;


public class TreeArcView extends EdgeView {	

	transient public int connectionId = 0;
	
	public TreeArcView(Object cell, JGraph graph, CellMapper cm) {
		super(cell, graph, cm);
		
		Object connectionState = getAllAttributes().get("conid");
		  
                if (connectionState instanceof String)
		{
		  connectionId = Integer.parseInt((String)connectionState);
		}
		if (connectionId > 0)
		{
                AttributeMap sourceAttr = ((DefaultGraphCell)((DefaultPort)((DecisionTreeArc)cell).getSource()).getParent()).getAttributes();
		AttributeMap targetAttr = ((DefaultGraphCell)((DefaultPort)((DecisionTreeArc)cell).getTarget()).getParent()).getAttributes();
		
		org.jgraph.graph.AttributeMap attributes = ((GraphCell)cell).getAttributes();
		List ePoints = GraphConstants.getPoints(((GraphCell)cell).getAttributes());
		Vector route = new Vector();
		double startX, startY, endX, endY;
		startX = ((Rectangle2D)sourceAttr.get("bounds")).getX();
		startY = ((Rectangle2D)sourceAttr.get("bounds")).getY();	
		endX = ((Rectangle2D)targetAttr.get("bounds")).getX();
		endY = ((Rectangle2D)targetAttr.get("bounds")).getY();
		System.out.println("x1,y1 = " + startX + "," + startY);
		System.out.println("x2,y2 = " + endX + "," + endY);
		route.add(new Integer(connectionId));	
		Edge.Routing router = new TreeArcRouting();
		router.route(this, route);
		GraphConstants.setLineStyle(attributes, GraphConstants.STYLE_SPLINE);
		//GraphConstants.setLineWidth(attributes, 5);
		GraphConstants.setRouting(attributes, router);
		//((GraphCell)cell).changeAttributes(attributes);
		//((GraphCell)cell).setAttributes(attributes);
		}
	}	
	
	public class TreeArcRouting implements Edge.Routing {

		public void route(EdgeView edge, java.util.List points) {		        
			int n = points.size();
			System.out.println("conid = " + connectionId);
			//Point2D from = edge.getPoint(0);
Point2D from = new Point2D.Double(((Rectangle2D)((AttributeMap)((DefaultGraphCell)((DefaultPort)((DecisionTreeArc)edge.getCell()).getSource()).getParent()).getAttributes()).get("bounds")).getX(),((Rectangle2D)((AttributeMap)((DefaultGraphCell)((DefaultPort)((DecisionTreeArc)edge.getCell()).getSource()).getParent()).getAttributes()).get("bounds")).getY());			
			if (edge.getSource() instanceof PortView)
				from = ((PortView) edge.getSource()).getLocation(null);
			else if (edge.getSource() != null) {
				Rectangle2D b = edge.getSource().getBounds();
				from =
					edge.getAttributes().createPoint(b.getCenterX(), b.getCenterY());
			}
			Point2D to = new Point2D.Double(((Rectangle2D)((AttributeMap)((DefaultGraphCell)((DefaultPort)((DecisionTreeArc)edge.getCell()).getTarget()).getParent()).getAttributes()).get("bounds")).getX(),((Rectangle2D)((AttributeMap)((DefaultGraphCell)((DefaultPort)((DecisionTreeArc)edge.getCell()).getTarget()).getParent()).getAttributes()).get("bounds")).getY());			
			if (edge.getTarget() instanceof PortView)
				to = ((PortView) edge.getTarget()).getLocation(null);
			else if (edge.getTarget() != null) {
				Rectangle2D b = edge.getTarget().getBounds();
				to = edge.getAttributes().createPoint(b.getCenterX(), b.getCenterY());
			}
			if (from != null && to != null) {
				Point2D[] routed;
				// Handle self references
				/* if (edge.getSource() == edge.getTarget()
					&& edge.getSource() != null) {
					Rectangle2D bounds =
						edge.getSource().getParentView().getBounds();
					double height = edge.getGraph().getGridSize();
					double width = bounds.getWidth() / 3;
					routed = new Point2D[4];
					routed[0] =
						edge.getAttributes().createPoint(
							bounds.getX() + width,
							bounds.getY() + bounds.getHeight());
					routed[1] =
						edge.getAttributes().createPoint(
							bounds.getX() + width,
							bounds.getY() + bounds.getHeight() + height);
					routed[2] =
						edge.getAttributes().createPoint(
							bounds.getX() + 2 * width,
							bounds.getY() + bounds.getHeight() + height);
					routed[3] =
						edge.getAttributes().createPoint(
							bounds.getX() + 2 * width,
							bounds.getY() + bounds.getHeight());
				} 
				else */{
					double dx = from.getX() - to.getX();
					double dy = from.getY() - to.getY();
					double dxa = Math.abs(dx);
					double dya = Math.abs(dy); 
					double x2 = from.getX() + ((to.getX() - from.getX()) / 2);
					double y2 = from.getY() + ((to.getY() - from.getY()) / 2);
					routed = new Point2D[1];
					double dist = 60; // how far away is each new line
					double xmod = dist * (dx / Math.sqrt( (dx *  dx) +(dy * dy)));
					double ymod = dist * (1 - (dx / Math.sqrt( (dx*  dx) +(dy * dy))));
									
					if (dxa > dya)
					{
					  if (connectionId == 1)
					  {
					    routed[0] = edge.getAttributes().createPoint(to.getX() + (dx * 0.25), from.getY() - (dy * 0.25));
					  }
					  else if (connectionId == 2)
					  {
					    routed[0] = edge.getAttributes().createPoint(from.getX() - (dx * 0.25), to.getY() + (dy * 0.25));		  
					  }
			 		  else if (connectionId == 3)
					  {
					    routed[0] = edge.getAttributes().createPoint(to.getX(), from.getY());
					  }
					  else if (connectionId == 4)
					  {
					    routed[0] = edge.getAttributes().createPoint(from.getX(), to.getY());		  
					  }					  
					}
					else
					{
					  if (connectionId == 1)
					  {
					    routed[0] = edge.getAttributes().createPoint(from.getX() - (dx * 0.25), to.getY() + (dy * 0.25));					
					  }
					  else if (connectionId == 2)
					  {
					    routed[0] = edge.getAttributes().createPoint(to.getX() + (dx * 0.25), from.getY() - (dy * 0.25));	
					  }	  
					  else if (connectionId == 3)
					  {
					    routed[0] = edge.getAttributes().createPoint(from.getX(), to.getY());					
					  }
					  else if (connectionId == 4)
					  {
					    routed[0] = edge.getAttributes().createPoint(to.getX(), from.getY());	
					  }	  					  
					}
		
	
				}
				// Set/Add Points
				for (int i = 0; i < routed.length; i++)
					if (points.size() > i + 2)
						points.set(i + 1, routed[i]);
					else
						points.add(i + 1, routed[i]);
				// Remove spare points
				while (points.size() > routed.length + 2) {
					points.remove(points.size() - 2);
				}
				// System.out.println(points);
			}
		}

	}

	
	
	
	
	
}