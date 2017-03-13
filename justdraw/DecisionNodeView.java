/*
 * @(#)DecisionNodeView.java	1.0 1.2 11/11/02
 *
 * Copyright (C) John Avery 2004 All rights reserved.
 * Portions copyright (c) 2001 Gaudenz Alder
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

import java.util.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;

import java.awt.font.*;
import java.text.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

public class DecisionNodeView extends VertexView {

	public static EllipseRenderer renderer = new EllipseRenderer();

	public DecisionNodeView(Object cell, JGraph graph, CellMapper cm)
  {
		super(cell, graph, cm);
	}
                                     
  public boolean isMultiLined()
  {
    return true;
  }

  public Point getPerimeterPoint(Point source, Point p) {
		Rectangle2D r = getBounds();
		
		double x = r.getX();
		double y = r.getY();
		double a = (r.getWidth() + 1) / 2;
		double b = (r.getHeight() + 1) / 2;
		
		// x0,y0 - center of ellipse
		double x0 = x + a;
		double y0 = y + b;

		// x1, y1 - point
		double x1 = p.x;
		double y1 = p.y;
		
		// calculate straight line equation through point and ellipse center
		// y = d * x + h
		double dx = x1 - x0;
		double dy = y1 - y0;

		if (dx == 0)
		    return new Point((int) x0, (int) (y0 + b * dy / Math.abs(dy)));

		double d = dy / dx;
		double h = y0 - d * x0;
					
		// calculate intersection
		double e = a * a * d * d + b * b;
		double f = - 2 * x0 * e;
		double g = a * a * d * d * x0 * x0 + b * b * x0 * x0 - a * a * b * b;
		
		double det = Math.sqrt(f * f - 4 * e * g);
		
		// two solutions (perimeter points)
		double xout1 = (-f + det) / (2 * e);
		double xout2 = (-f - det) / (2 * e);
		double yout1 = d * xout1 + h;
		double yout2 = d * xout2 + h;
		
		double dist1 = Math.sqrt(Math.pow((xout1 - x1), 2)
								 + Math.pow((yout1 - y1), 2));
		double dist2 = Math.sqrt(Math.pow((xout2 - x1), 2)
								 + Math.pow((yout2 - y1), 2));
		
		// correct solution
		double xout, yout;
		
		if (dist1 < dist2) {
			xout = xout1;
			yout = yout1;
		} else {
			xout = xout2;
			yout = yout2;
		}
		
		return new Point((int)xout, (int)yout);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class EllipseRenderer extends JComponent implements CellViewRenderer
  {

	/** Cache the current graph for drawing. */
	transient protected JGraph graph;

	/** Cache the current shape for drawing. */
	transient protected VertexView view;

	
	transient protected Font font;
	
	transient protected String text;
	
	/** Cached hasFocus and selected value. */
	transient protected boolean hasFocus,
		selected,
		preview,
		opaque,
		childrenSelected,
                shadow;
                
	/** Cached default foreground and default background. */
	transient protected Color defaultForeground, defaultBackground, bordercolor;

	/** Cached borderwidth. */
	transient protected int borderWidth;
	
	/** Cached value of the double buffered state */
	transient boolean isDoubleBuffered = false;

	public Component getRendererComponent(
				JGraph graph,
				CellView view,
				boolean sel,
				boolean focus,
				boolean preview)
    {
      MutableAttributeSet attr = (MutableAttributeSet) new SimpleAttributeSet();
      StyleConstants.setAlignment(attr, StyleConstants.ALIGN_CENTER);
      String label = view.getCell().toString();
      text = view.getCell().toString();
      /* 
      if (label.length() > 48)
      {
        label = label.substring(0, 45);
      }
			setText(label);

	  setParagraphAttributes(attr, true); 
      */
	  this.graph = graph;
	  this.hasFocus = focus;
	  this.childrenSelected = graph.getSelectionModel().isChildrenSelected(view.getCell());
	  this.selected = sel;
	  this.preview = preview;
	  Map attributes = view.getAllAttributes();
	  installAttributes(graph, attributes);
return this;
	}

		protected void installAttributes(JGraph graph, Map attributes)
    {
		  setOpaque(GraphConstants.isOpaque(attributes));
  		Color foreground = GraphConstants.getForeground(attributes);
	  	setForeground((foreground != null) ? foreground : graph.getForeground());
	  	Color background = GraphConstants.getBackground(attributes);
  		setBackground((background != null) ? background : graph.getBackground());
  		font = GraphConstants.getFont(attributes);
		  setFont((font != null) ? font : graph.getFont());
		  Border border = GraphConstants.getBorder(attributes);
		  bordercolor = GraphConstants.getBorderColor(attributes);
		  shadow = false;		  
		  Object shadowState = attributes.get("argument");
		  
                  if (shadowState instanceof String)
		  {
		    if (((String)shadowState).equals("true")) shadow = true;
		    
		  }
		  
		  if (border != null)
	  		setBorder(border);
	  	else if (bordercolor != null)
      {
	  		int borderWidth =	Math.max(1,Math.round(GraphConstants.getLineWidth(attributes)));
	  		setBorder(BorderFactory.createLineBorder(bordercolor,borderWidth));
	  	}
	  }

		public void paint(Graphics g)
    {
			int b = borderWidth;
			Graphics2D g2 = (Graphics2D) g;
			Dimension d = getSize();
      Rectangle bounds = getBounds();
			boolean tmp = selected;

		       LineBreakMeasurer lineMeasurer;
        int paragraphStart;
        int paragraphEnd;
        AttributedCharacterIterator paragraph;
	String plainString = text;
	AttributedString attrString = new AttributedString(plainString);

	attrString.addAttribute(TextAttribute.FONT, font, 0, plainString.length());
	paragraph = attrString.getIterator();
        FontRenderContext frc = g2.getFontRenderContext();
        paragraphStart = paragraph.getBeginIndex();
        paragraphEnd = paragraph.getEndIndex();
        lineMeasurer = new LineBreakMeasurer(paragraph, frc);
  

        float formatWidth = (float) (d.width) - 15;

        float drawPosY = 0;
	float drawPosX = 0;
	
        lineMeasurer.setPosition(paragraphStart);
        
         
    	
	int i = 20;

	g.setColor(bordercolor);
	Vector layouts = new Vector();
	while (lineMeasurer.getPosition() < paragraphEnd)
       	{
          TextLayout layout = 
		lineMeasurer.nextLayout(formatWidth);          
            drawPosX = (float)(d.width + layout.getBounds().getWidth()) / (float)2 ;

	  layouts.add(layout);
       	}
	
	/* 
        lineMeasurer.setPosition(paragraphStart);
	
	while (lineMeasurer.getPosition() < paragraphEnd)
       	{
          TextLayout layout = 
		lineMeasurer.nextLayout(formatWidth);          
            drawPosX = (float)(d.width + layout.getBounds().getWidth()) / (float)2 ;

	  layouts.add(layout);
       	}
	*/				
			
			
			
      if (shadow)
      {
                if (!selected)
		{
    			g.setColor(bordercolor);
	  		g.fillOval(b + 3, b + 3, d.width - b - 3, d.height - b - 3);
                }
		else
		{
			g.setColor(graph.getHighlightColor());
	  		g.fillOval(b + 3, b + 3, d.width - b - 3, d.height - b - 3);
		}
 	  	  g.setColor(new Color(240, 240, 255));
	  		g.fillOval(b , b , d.width - b - 6, d.height - b - 6);
	 		
	  		if (bordercolor != null && !selected)
        {
	  			g.setColor(bordercolor);
	  			g2.setStroke(new BasicStroke(b));
	  			g.drawOval(b, b , d.width - b - 6, d.height - b - 6);
	  		}
	  		else if (selected && hasFocus)
        {

	  			g2.setStroke(GraphConstants.SELECTION_STROKE);
	  			g.setColor(graph.getHighlightColor());
	  			g.drawOval(b , b , d.width - b - 6, d.height - b - 6);
	  		}
	  		else if (selected)
        {;	
	  			g2.setStroke(GraphConstants.SELECTION_STROKE);
	  			g.setColor(graph.getHighlightColor());
  				g.drawOval(b , b , d.width - b - 6, d.height - b - 6);  
  			}
  			try
        {
	  			setBorder(null);
		  		setOpaque(false);
		  		selected = false;
          super.setBorder(BorderFactory.createEmptyBorder(25,13,125,12)); 
		  		super.paint(g);
		  	}
        finally
        {
		  		selected = tmp;
			  }
      }
      else 
      {

      
	  		if (super.isOpaque())
        {
  				g.setColor(super.getBackground());
  				g.fillOval(b , b , d.width - b - 1, d.height - b - 1);
                                g.setColor(new Color(240, 240, 255));
	                        g.fillOval(b , b , d.width - b - 1, d.height - b - 1);				
  			}
  			if (bordercolor != null && !selected)
        {
  				g.setColor(bordercolor);
  				g2.setStroke(new BasicStroke(b));
  				g.drawOval(b, b , d.width - b - 1, d.height - b - 1);
  			}
  			else if (selected && hasFocus)
        {
  				g2.setStroke(GraphConstants.SELECTION_STROKE);
  				g.setColor(graph.getHighlightColor());
  				g.drawOval(b , b , d.width - b - 1, d.height - b - 1);
  			}
  			else if (selected)
        {
  				g2.setStroke(GraphConstants.SELECTION_STROKE);
  				g.setColor(graph.getHighlightColor());
	  			g.drawOval(b , b , d.width - b - 1, d.height - b - 1);
  			}
      }
	g2.setColor(bordercolor);
      	if (layouts.size() > 0)
	{
	  TextLayout layout = (TextLayout)layouts.get(0);
	  drawPosY = d.height/2 - ((layouts.size() - 1) * (layout.getAscent() + layout.getDescent() + layout.getLeading())/2) - layout.getAscent();	  
	  if (drawPosY < 10) drawPosY = 10;
	  for (Enumeration e = layouts.elements() ; e.hasMoreElements() ;)
	  {
	    layout = ((TextLayout)e.nextElement());
            drawPosX = (float)(d.width - layout.getBounds().getWidth()) / (float)2 ;	    
	    drawPosY += layout.getAscent();

            layout.draw(g2, drawPosX, drawPosY);
            drawPosY += layout.getDescent() + layout.getLeading();
          }
	}
	
			try
      {
				setBorder(null);
				setOpaque(false);
				selected = false;
        super.setBorder(BorderFactory.createEmptyBorder(25,13,125,12));
				super.paint(g);
			}
      finally
      {
				selected = tmp;
			}
		}
	}
}
