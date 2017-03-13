/*
 * @(#)JustDraw.java 0.1 14-JUN-04
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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import java.net.URL;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.jgraph.*;
import org.jgraph.event.*;
import org.jgraph.event.*;
import org.jgraph.graph.*;


import javax.swing.undo.CompoundEdit;
import javax.swing.JProgressBar;

import org.jgraph.graph.DefaultGraphModel.*;

public class JustDraw
{
  final public JustDraw self;
  protected JFrame mainFrame;
  protected JPanel mainPanel;
  protected JDesktopPane desktop;
  protected JMenuBar menubar;
  protected JToolBar toolbar;
  protected JDialog properties;
  protected JustDrawDocument document;
  protected String editMode;
  protected PortView sourcePort;
  protected CompoundEdit currentTextEdit;
  protected int currentTextEditState = 0; // 0 = unset, 1 = inserting, 2 = removing
  // Actions which Change State
  protected Action undo,
                   redo;
  protected JToggleButton moveButton,insertDecisionButton, insertConclusionButton, insertClaimButton, insertArgumentButton, connectArgumentButton, lockedButton, insertTextButton;
  protected ButtonGroup insertsButtonGroup, argumentButtonGroup;
  
  JMenuItem newMenuItem, exitMenuItem, importXMLMenuItem, /*importMySQLMenuItem,*/ exportXMLMenuItem, exportMySQLMenuItem, exportCSVMenuItem, updateXMLMenuItem, insertXMLMenuItem, printMenuItem;	   
  // Main Method
  public static void main(String[] args)
  {
    new JustDraw(args);
  }

  protected JustDraw(String[] args)
  {
    // Set Look and Feel
    try
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception ex)
    {
    }
    self = this;
    // Set initial mode
    editMode = "move";
    sourcePort = null;

    //Set up main frame
    mainFrame = new JFrame("JustDraw");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Set up main panel
    mainPanel = new JPanel(new BorderLayout());

    // Set up menu bar
    menubar = new JMenuBar();
    makeMenubar();

    // Set up toolbar
    makeToolbar();

    // Set up properties dialogue
     makePropertiesDialogue(null);

    // Set up desktop
    desktop = new JDesktopPane();
    desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

    // Add content to main panel
    mainPanel.add(BorderLayout.CENTER, desktop);

    // Add content to mainFrame
    mainFrame.getContentPane().add(BorderLayout.NORTH, toolbar);
    mainFrame.setJMenuBar(menubar);
    mainFrame.getContentPane().add(BorderLayout.CENTER, mainPanel);

    // Pack and show frame
    mainFrame.pack();
    mainFrame.setSize(1000, 800);
    mainFrame.show();

    // Initialise documents
    //documents = new Vector();
  }

  /*
   * Populates JMenubar menubar...eventually from a text/xml file?
   */
  private void makeMenubar()
  {
    JMenu file = new JMenu("File");
    file.setMnemonic('f');

    newMenuItem = new JMenuItem();
    newMenuItem.setAction(new AbstractAction("New")
    {
      public void actionPerformed(ActionEvent e)
      {
          newDocument();
	  importXMLMenuItem.setEnabled(false);
//	  importMySQLMenuItem.setEnabled(false);
	  newMenuItem.setEnabled(false);
	  exportXMLMenuItem.setEnabled(true);
	  exportMySQLMenuItem.setEnabled(true);
	  exportCSVMenuItem.setEnabled(true);
      }
    });
    newMenuItem.setMnemonic('n');
    newMenuItem.setToolTipText("Creates a new decision tree.");

    importXMLMenuItem = new JMenuItem();
    importXMLMenuItem.setAction(new AbstractAction("Import XML")
    {
      public void actionPerformed(ActionEvent e)
      {
          if(importXML())
	  {
	    importXMLMenuItem.setEnabled(false);
//	    importMySQLMenuItem.setEnabled(false);
	    newMenuItem.setEnabled(false);
	    exportXMLMenuItem.setEnabled(true);	 
	    exportMySQLMenuItem.setEnabled(true);
	    exportCSVMenuItem.setEnabled(true);	 
	  };
      }
    });
    importXMLMenuItem.setMnemonic('i');
    importXMLMenuItem.setToolTipText("Imports a decision tree from an XML file.");
    
    /*///////////// edit
    importMySQLMenuItem = new JMenuItem();
    importMySQLMenuItem.setAction(new AbstractAction("Import CSV")
    {
      public void actionPerformed(ActionEvent e)
      {
          if(importMySQL())
	  {
	    importXMLMenuItem.setEnabled(false);
	    importMySQLMenuItem.setEnabled(false);
	    newMenuItem.setEnabled(false);
	    exportXMLMenuItem.setEnabled(true);	 
	    exportMySQLMenuItem.setEnabled(true);
	    exportCSVMenuItem.setEnabled(true);	 
	  };
      }
    });
    importMySQLMenuItem.setMnemonic('i');
    importMySQLMenuItem.setToolTipText("Imports a decision tree from an CSV file.");
     /////////////// */
    
    exportXMLMenuItem = new JMenuItem();
    exportXMLMenuItem.setAction(new AbstractAction("Export to XML")
    {
      public void actionPerformed(ActionEvent e)
      {
          exportXML();
      }
    });

    exportXMLMenuItem.setMnemonic('e');
    exportXMLMenuItem.setToolTipText("Exports a single decision tree to an XML file.");
    exportXMLMenuItem.setEnabled(false);
    
    ////////////// edit nial
    exportMySQLMenuItem = new JMenuItem();
    exportMySQLMenuItem.setAction(new AbstractAction("Export to MySQL")
    {
      public void actionPerformed(ActionEvent e)
      {
          exportMySQL();
          exportXML();
      }
    });
    exportMySQLMenuItem.setMnemonic('e');
    exportMySQLMenuItem.setToolTipText("Exports a single decision tree to an MySQL file.");
    exportMySQLMenuItem.setEnabled(false);   
    //////////////
    exportCSVMenuItem = new JMenuItem();
    exportCSVMenuItem.setAction(new AbstractAction("Export to CSV")
    {
      public void actionPerformed(ActionEvent e)
      {
          exportCSV();
          exportXML();
      }
    });
    exportCSVMenuItem.setMnemonic('e');
    exportCSVMenuItem.setToolTipText("Exports a single decision tree to an CSV file.");
    exportCSVMenuItem.setEnabled(false);   
    
    updateXMLMenuItem = new JMenuItem();
    updateXMLMenuItem.setAction(new AbstractAction("Update XML File")
    {
      public void actionPerformed(ActionEvent e)
      {
          
      }
    });
    updateXMLMenuItem.setMnemonic('u');
    updateXMLMenuItem.setToolTipText("Updates XML, generally for XML files containing multiple decision trees (only available if file loaded from an XML file).");
    updateXMLMenuItem.setEnabled(false);
    
    insertXMLMenuItem = new JMenuItem();
    insertXMLMenuItem.setAction(new AbstractAction("Insert into XML File")
    {
      public void actionPerformed(ActionEvent e)
      {
          
      }
    });
    insertXMLMenuItem.setMnemonic('n');
    insertXMLMenuItem.setToolTipText("Inserts decison tree XML into a pre-existing XML file.");
    insertXMLMenuItem.setEnabled(false);    
    

    printMenuItem = new JMenuItem();
    printMenuItem.setAction(new AbstractAction("Print")
    {
      public void actionPerformed(ActionEvent e)
      {
          document.graph.print(document.graph.getGraphics());    	  
      }
    });

            
    exitMenuItem = new JMenuItem();
    exitMenuItem.setAction(new AbstractAction("Exit")
    {
      public void actionPerformed(ActionEvent e)
      {
          System.exit(0);
      }
    });
    exitMenuItem.setMnemonic('x');

    file.add(newMenuItem);
    file.addSeparator();
    file.add(importXMLMenuItem);
 //   file.add(importMySQLMenuItem);
    file.add(exportXMLMenuItem);
    file.add(exportMySQLMenuItem);
    file.add(exportCSVMenuItem);
    file.add(updateXMLMenuItem);
    file.add(insertXMLMenuItem);
    file.addSeparator();
    file.add(printMenuItem);
    file.addSeparator();
    file.add(exitMenuItem);
      
    menubar.add(file);
  }

  // Create context sensitive properties dialogue
  protected void makePropertiesDialogue(final Object object)
  {
    // ** Slow as rebuilding everytime
    JTabbedPane pTab = new JTabbedPane();
    JPanel oTab = new JPanel(); 
    JPanel vTab = new JPanel();
    vTab.add(new JLabel("This panel has not yet been implemented. This is not a release version of JustDraw."));
    if (object == null)
    {

    }
    else if(object instanceof String)
    {
      oTab.add(new JLabel((String)object));
    }
    else if(object instanceof JustDrawDocument ){
    	//System.out.print("Graph properies: here");
    	//// edit
    	// final JustDrawDocument node = (JustDrawDocument) object;
        Box pBox = new Box(BoxLayout.Y_AXIS);
        Box docPrefixBox = new Box(BoxLayout.X_AXIS);
        oTab.add(new JLabel("Tabel Prefix "));
       
        final JTextField docPrefix = new JTextField(document.getDocPrefix(), 20);
        docPrefix.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent e)
            {
              document.setDocPrefix(docPrefix.getText());
            }
            public void insertUpdate(DocumentEvent e)
            {
              document.setDocPrefix(docPrefix.getText());
            }
            public void removeUpdate(DocumentEvent e) 
            {
              document.setDocPrefix(docPrefix.getText());
            }
        });
        docPrefixBox.add(docPrefix);
        docPrefixBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        oTab.add(docPrefix);
        //oTab.add(pBox); 
        
//////////////////  /
    }

    else if (object instanceof DecisionTreeNode)
    {
      final DecisionTreeNode node = (DecisionTreeNode) object;
      Box pBox = new Box(BoxLayout.Y_AXIS);
      Box idBox = new Box(BoxLayout.X_AXIS);
      idBox.add(new JLabel("ID "));
      final JTextField id = new JTextField(node.getId(), 2);
      id.getDocument().addDocumentListener(new DocumentListener(){
        public void update(DocumentEvent e)
	{
          // Set the id and fire edit so repaint occurs
          node.setId(id.getText());
	  if (((DecisionTreeNode)object).hasArgument)
	  {
	    ((JustDrawArgument)document.arguments.get(object)).getRootNode().setId(id.getText());
	    Object blah[] = new Object[]{((JustDrawArgument)document.arguments.get(object)).getRootNode()}; 
            // ((JustDrawArgument)document.arguments.get(object)).graph.getModel().toFront(blah);	      
	  }          
	}
        public void changedUpdate(DocumentEvent e)
        {
	  update(e);
        }
        public void insertUpdate(DocumentEvent e)
        {
          update(e);
	}
        public void removeUpdate(DocumentEvent e) 
        {
	  update(e);
        }
      });
      idBox.add(id);

      idBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      Box prefixBox = new Box(BoxLayout.X_AXIS);
      prefixBox.add(new JLabel("Prefix "));
      final JTextField prefix = new JTextField(node.getPrefix(), 15);
      prefix.getDocument().addDocumentListener(new DocumentListener(){
        public void update(DocumentEvent e)
	{
          // Set the id and fire edit so repaint occurs
          node.setPrefix(prefix.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);	

	  if (((DecisionTreeNode)object).hasArgument)
	  {
	    ((JustDrawArgument)document.arguments.get(object)).getRootNode().setPrefix(prefix.getText());
	    Object blah[] = new Object[]{((JustDrawArgument)document.arguments.get(object)).getRootNode()}; 
            ((JustDrawArgument)document.arguments.get(object)).graph.getModel().toFront(blah);	      
	  }   	  
	}
      
        public void changedUpdate(DocumentEvent e)
        {
          update(e);
        }
        public void insertUpdate(DocumentEvent e)
        {
          update(e);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          update(e);
        }
      });
      prefixBox.add(prefix);
      prefixBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      Box suffixBox = new Box(BoxLayout.X_AXIS);
      suffixBox.add(new JLabel("Suffix "));
      final JTextField suffix = new JTextField(node.getSuffix(), 15);
      suffix.getDocument().addDocumentListener(new DocumentListener(){
        public void update(DocumentEvent e)
	{
          // Set the id and fire edit so repaint occurs
          node.setSuffix(suffix.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);	
	  
	  if (((DecisionTreeNode)object).hasArgument)
	  {
	    ((JustDrawArgument)document.arguments.get(object)).getRootNode().setSuffix(suffix.getText());
	    Object blah[] = new Object[]{((JustDrawArgument)document.arguments.get(object)).getRootNode()}; 
            ((JustDrawArgument)document.arguments.get(object)).graph.getModel().toFront(blah);	      
	  }   	  
	}

        public void changedUpdate(DocumentEvent e)
        {
          update(e);
        }
        public void insertUpdate(DocumentEvent e)
        {
          update(e);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          update(e);
        }
      });
      suffixBox.add(suffix);
      suffixBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      idBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      
    //////////////////// edit nial  

      Box titleBox = new Box(BoxLayout.X_AXIS);
      titleBox.add(new JLabel("Title "));
      final JTextField title = new JTextField(node.getTitle(), 20);
      title.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setTitle(title.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void insertUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setTitle(title.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          // Set the id and fire edit so repaint occurs
          node.setTitle(title.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
      });
      titleBox.add(title);

      titleBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      
    /////////////////// edit nial
      Box relevanceBox = new Box(BoxLayout.X_AXIS);
      relevanceBox.add(new JLabel("Relevance "));
      final JTextField relevance = new JTextField(node.getRelevance(), 20);
      relevance.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setRelevance(relevance.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void insertUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setRelevance(relevance.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          // Set the id and fire edit so repaint occurs
          node.setRelevance(relevance.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
      });
      relevanceBox.add(relevance);

      relevanceBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      
/////////////////// edit nial
      Box graph_idBox = new Box(BoxLayout.X_AXIS);
      graph_idBox.add(new JLabel("Graph ID "));
      final JTextField graph_id = new JTextField(node.getGraph_id(), 20);
      graph_id.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setGraph_id(graph_id.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void insertUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setGraph_id(graph_id.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          // Set the id and fire edit so repaint occurs
          node.setGraph_id(graph_id.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
      });
      graph_idBox.add(graph_id);

      graph_idBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      
    ///////////////////  
      pBox.add(idBox);
      pBox.add(titleBox);
      pBox.add(prefixBox);
      pBox.add(suffixBox);
      pBox.add(relevanceBox);
      pBox.add(graph_idBox);
      oTab.add(pBox);
    }
    else if (object instanceof ConclusionNode)
    {
      final ConclusionNode node = (ConclusionNode) object;
      Box pBox = new Box(BoxLayout.Y_AXIS);
      
      Box titleBox = new Box(BoxLayout.X_AXIS);
      titleBox.add(new JLabel("Title "));
      final JTextField title = new JTextField(node.getTitle(), 20);
      title.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setTitle(title.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
         // Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void insertUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setTitle(title.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
       //   Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          // Set the id and fire edit so repaint occurs
          node.setTitle(title.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
        //  Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
      });
      titleBox.add(title);

      titleBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      
      Box idBox = new Box(BoxLayout.X_AXIS);
      idBox.add(new JLabel("ID "));
      final JTextField id = new JTextField(node.getId(), 12);
      id.getDocument().addDocumentListener(new DocumentListener(){
      public void update(DocumentEvent e)
	    {
          // Set the id and fire edit so repaint occurs
          node.setId(id.getText());
          //final Object[] nodes = new Object[1];
          //nodes[0] = node;
          //Map nested = new Hashtable();
          //AttributeMap attr = new AttributeMap();
          //Object value = node.getAttributes().get("value");
          //attr.put("value", node.getAttributes().get("value"));
          //nested.put(node, attr);
          //document.graph.getModel().edit(nested, null, null, null);	
	      }
        public void changedUpdate(DocumentEvent e)
        {
	        update(e);
        }
        public void insertUpdate(DocumentEvent e)
        {
          update(e);
	}
        public void removeUpdate(DocumentEvent e) 
        {
	  update(e);
        }
      });
/////////////////// edit nial
      
      Box prefixBox = new Box(BoxLayout.X_AXIS);
      prefixBox.add(new JLabel("Prefix "));
      final JTextField prefix = new JTextField(node.getPrefix(), 15);
      prefix.getDocument().addDocumentListener(new DocumentListener(){
        public void update(DocumentEvent e)
	{
          // Set the id and fire edit so repaint occurs
          node.setPrefix(prefix.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
       //   Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);	
	}
      
        public void changedUpdate(DocumentEvent e)
        {
          update(e);
        }
        public void insertUpdate(DocumentEvent e)
        {
          update(e);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          update(e);
        }
      });
      prefixBox.add(prefix);
      prefixBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      Box suffixBox = new Box(BoxLayout.X_AXIS);
      suffixBox.add(new JLabel("Suffix "));
      final JTextField suffix = new JTextField(node.getSuffix(), 15);
      suffix.getDocument().addDocumentListener(new DocumentListener(){
        public void update(DocumentEvent e)
	{
          // Set the id and fire edit so repaint occurs
          node.setSuffix(suffix.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
   //       Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);	
 
	}

        public void changedUpdate(DocumentEvent e)
        {
          update(e);
        }
        public void insertUpdate(DocumentEvent e)
        {
          update(e);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          update(e);
        }
      });
      suffixBox.add(suffix);
      suffixBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      
      Box relevanceBox = new Box(BoxLayout.X_AXIS);
      relevanceBox.add(new JLabel("Relevance "));
      final JTextField relevance = new JTextField(node.getRelevance(), 20);
      relevance.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setRelevance(relevance.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
    //      Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void insertUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setRelevance(relevance.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
    //      Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          // Set the id and fire edit so repaint occurs
          node.setRelevance(relevance.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
    //      Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
      });
      relevanceBox.add(relevance);

      relevanceBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      
/////////////////// edit nial
      Box graph_idBox = new Box(BoxLayout.X_AXIS);
      graph_idBox.add(new JLabel("Graph ID "));
      final JTextField graph_id = new JTextField(node.getGraph_id(), 20);
      graph_id.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setGraph_id(graph_id.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
   //       Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void insertUpdate(DocumentEvent e)
        {
          // Set the id and fire edit so repaint occurs
          node.setGraph_id(graph_id.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
   //       Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          // Set the id and fire edit so repaint occurs
          node.setGraph_id(graph_id.getText());
          final Object[] nodes = new Object[1];
          nodes[0] = node;
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
 //         Object value = node.getAttributes().get("value");
          attr.put("value", node.getAttributes().get("value"));
          nested.put(node, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
      });
      graph_idBox.add(graph_id);

      graph_idBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      
      idBox.add(id);

      pBox.add(idBox);
      pBox.add(titleBox);
      pBox.add(prefixBox);
      pBox.add(suffixBox);
      pBox.add(relevanceBox);
      pBox.add(graph_idBox);
      oTab.add(pBox);
    }
    else if (object instanceof DecisionTreeArc)
    {
      final DecisionTreeArc arc = (DecisionTreeArc) object;
      Box pBox = new Box(BoxLayout.Y_AXIS);
      Box idBox = new Box(BoxLayout.X_AXIS);
      Box summaryBox = new Box(BoxLayout.X_AXIS);
      idBox.add(new JLabel("Claim "));
      final JTextField claim = new JTextField(arc.getClaim(), 30);
      claim.getDocument().addDocumentListener(new DocumentListener(){
        public void changed(DocumentEvent e)
        {
          // Set the claim and fire edit so repaint occurs
          arc.setClaim(claim.getText());
          // If we have an argument we need to change claims list for root node
          
        //  if (((DefaultPort)arc.getSource()).getParent() instanceof DecisionTreeArc)
          {
            DecisionTreeNode source = (DecisionTreeNode) ((DefaultPort)arc.getSource()).getParent();
            if (source.hasArgument)
            {

              Vector claims = new Vector();
              // Find port for vertex
              Iterator children = source.getChildren().iterator();
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
		  if (((DefaultPort)claim.getSource()).getParent() == source)
		  {
		    claims.add(new ArgumentClaim(claim.getClaim(), 
		               new Double(claim.getWeight()),
			       new Double(claim.getThreshold())));  
		  }
                }
              }

              ((JustDrawArgument)document.arguments.get(source)).getRootNode().setClaims(claims);

              Object blah[] = new Object[]{((JustDrawArgument)document.arguments.get(source)).getRootNode()};

              ((JustDrawArgument)document.arguments.get(source)).graph.getModel().toFront(blah);
            }
          }
          Object[] arcs = new Object[1];
          arcs[0] = arc;          
          Map nested = new Hashtable();
          AttributeMap attr = new AttributeMap();
          Object value = arc.getAttributes().get("value");
          attr.put("value", arc.getAttributes().get("value"));
          nested.put(arc, attr);
          document.graph.getModel().edit(nested, null, null, null);
        }
        public void changedUpdate(DocumentEvent e)
        {
          changed(e);
        }
        public void insertUpdate(DocumentEvent e)
        {
          changed(e);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          changed(e);
        }
      });
      idBox.add(claim);

      idBox.add(new JLabel(" Weight "));
      final JTextField weight = new JTextField(""+arc.getWeight(), 3);
      weight.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
	  try
	  {
            arc.setWeight(new Double(weight.getText()).doubleValue());
	  }
	  catch (Exception ex)
	  {
	    arc.setWeight(0.0);
	  }
        }
        public void insertUpdate(DocumentEvent e)
        {
	  try
	  {
            arc.setWeight(new Double(weight.getText()).doubleValue());
	  }
	  catch (Exception ex)
	  {
	    arc.setWeight(0.0);
	  }
        }
        public void removeUpdate(DocumentEvent e) 
        {
	  try
	  {
            arc.setWeight(new Double(weight.getText()).doubleValue());
	  }
	  catch (Exception ex)
	  {
	    arc.setWeight(0.0);
	  }
        }
      });
      idBox.add(weight);
      idBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      idBox.add(new JLabel(" Threshold "));
      final JTextField threshold = new JTextField(""+arc.getThreshold(), 3);
      threshold.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
	  try
	  {
            arc.setThreshold(new Double(threshold.getText()).doubleValue());
	  }
	  catch (Exception ex)
	  {
	    arc.setThreshold(0.0);
	  }
        }
        public void insertUpdate(DocumentEvent e)
        {
	  try
	  {
            arc.setThreshold(new Double(threshold.getText()).doubleValue());
	  }
	  catch (Exception ex)
	  {
	    arc.setThreshold(0.0);
	  }

        }
        public void removeUpdate(DocumentEvent e) 
        {
	  try
	  {
            arc.setThreshold(new Double(threshold.getText()).doubleValue());
	  }
	  catch (Exception ex)
	  {
	    arc.setThreshold(0.0);
	  }
        }
      });
      idBox.add(threshold);



      idBox.add(new JLabel(" Poisition "));
      final JTextField pos = new JTextField(""+arc.getPosition(), 3);
      pos.getDocument().addDocumentListener(new DocumentListener(){
        public void update(DocumentEvent e)
        {
           try
	         {
            arc.setPosition(new Integer(pos.getText()).intValue());
	         }
	         catch (Exception ex)
	         {	           
        	 }
        }
        public void changedUpdate(DocumentEvent e)
        {
          update(e);
        } 
        public void removeUpdate(DocumentEvent e) 
        {
          update(e);
        }
        public void insertUpdate(DocumentEvent e)
        {
          update(e);
        }
      });
      idBox.add(pos);

      // thresholdBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      summaryBox.add(new JLabel("Summary "));
      final JTextField summary = new JTextField(""+arc.getSummary(), 25);
      summary.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          arc.setSummary(summary.getText());
        }
        public void insertUpdate(DocumentEvent e)
        {
          arc.setSummary(summary.getText());
        }
        public void removeUpdate(DocumentEvent e) 
        {
          arc.setSummary(summary.getText());
        }
      });
      summaryBox.add(summary);
      summaryBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      
      Box actionsBox = new Box(BoxLayout.X_AXIS);
      TableModel dataModel = new AbstractTableModel()
      {
        public int getColumnCount()
        {
          return 3;
        }
        public int getRowCount()
        {
          return arc.getActions().size();
        }
        public Object getValueAt(int row, int col)
        {
          TreeArcAction currentAction = (TreeArcAction) arc.getActions().get(row);
          if (currentAction == null)
          {
            return null;
          }
          else
          {
            if (col == 0) return currentAction.getType();
            if (col == 1) return currentAction.getVariable();
            if (col == 2) return currentAction.getValue();
          }
          return null;
        }

        public void setValueAt(Object aValue, int rowIndex, int col)
        {
          TreeArcAction action = (TreeArcAction) arc.getActions().get(rowIndex);
          if (col == 0) action.setType((String) aValue);
          if (col == 1) action.setVariable((String) aValue);
          if (col == 2) action.setValue((String) aValue);
          arc.getActions().setElementAt(action, rowIndex);
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
          return true;
        }

        public String getColumnName(int col)
        {
          if (col == 0) return "Type";
          if (col == 1) return "Variable";
          if (col == 2) return "Value";
          return null;
        }
      };
 
      final JTable table = new JTable(dataModel);
      JScrollPane tableScrollPane = new JScrollPane(table);
      tableScrollPane.setPreferredSize(new Dimension(200,100));
      
      actionsBox.add(tableScrollPane);
      actionsBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      Box editActionBox = new Box(BoxLayout.X_AXIS);
      final JButton addAction = new JButton("Add Action");
      addAction.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
          arc.addAction("New Action", "", "");
          table.tableChanged(null);
        }
      });
      final JButton delAction = new JButton("Remove Action");
      delAction.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
          arc.delAction(table.getSelectedRow());
          table.tableChanged(null);
	  delAction.setEnabled(false);
        }
      });
      delAction.setEnabled(false);
      
      ListSelectionModel rowSM = table.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e)
	{         
              if(table.getSelectedRow() != -1)
	      {
	        delAction.setEnabled(true);
	      }
	      else
	      {
	       delAction.setEnabled(false);
	      }	   
	}
      });
      
      editActionBox.add(addAction);
      editActionBox.add(Box.createRigidArea(new Dimension(5,5)));
      editActionBox.add(delAction);
      editActionBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      
      
      pBox.add(idBox);      
      pBox.add(summaryBox);      
      pBox.add(new JLabel("Actions"));
      pBox.add(actionsBox);
      pBox.add(editActionBox);
      oTab.add(pBox);
    }

     else if (object instanceof ArgumentTreeNode)
    {
      final ArgumentTreeNode node = (ArgumentTreeNode) object;
      Box pBox = new Box(BoxLayout.Y_AXIS);
      Box idBox = new Box(BoxLayout.X_AXIS);
      idBox.add(new JLabel("Id "));
      final JTextField claim = new JTextField(node.getId(), 10);
      if (node.isRoot()) claim.setEditable(false);
      claim.getDocument().addDocumentListener(new DocumentListener(){
        public void changed(DocumentEvent e)
        {
	  node.setId(claim.getText());
        }
        public void changedUpdate(DocumentEvent e)
        {
          changed(e);
        }
        public void insertUpdate(DocumentEvent e)
        {
          changed(e);
        }
        public void removeUpdate(DocumentEvent e) 
        {
          changed(e);
        }
      });
      idBox.add(claim);
      idBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//--->
      
      Box titleBox = new Box(BoxLayout.X_AXIS);
      titleBox.add(new JLabel(" Title "));
      final JTextField title = new JTextField(""+node.getTitle(), 30);
      if (node.isRoot()) title.setEditable(false);      
      title.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          node.setTitle(title.getText());
	  document.graph.getModel().toFront(new Object[]{node});	  
        }
        public void insertUpdate(DocumentEvent e)
        {
          node.setTitle(title.getText());
	  document.graph.getModel().toFront(new Object[]{node});	  
        }
        public void removeUpdate(DocumentEvent e) 
        {          
	  node.setTitle(title.getText());
	  document.graph.getModel().toFront(new Object[]{node});
        }
      });
      titleBox.add(title);
      titleBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      Box relevanceBox = new Box(BoxLayout.X_AXIS);
      relevanceBox.add(new JLabel(" Relevance "));
      final JTextField relevance = new JTextField(""+node.getRelevance(), 30);
      if (node.isRoot()) relevance.setEditable(false);      
      relevance.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          node.setRelevance(relevance.getText());
	  document.graph.getModel().toFront(new Object[]{node});	  
        }
        public void insertUpdate(DocumentEvent e)
        {
          node.setRelevance(relevance.getText());
	  document.graph.getModel().toFront(new Object[]{node});	  
        }
        public void removeUpdate(DocumentEvent e) 
        {          
	  node.setRelevance(relevance.getText());
	  document.graph.getModel().toFront(new Object[]{node});
        }
      });
      relevanceBox.add(relevance);
      relevanceBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//--->
      Box prefBox = new Box(BoxLayout.X_AXIS);
      prefBox.add(new JLabel(" Prefix "));
      final JTextField prefix = new JTextField(""+node.getPrefix(), 30);
      if (node.isRoot()) prefix.setEditable(false);      
      prefix.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          node.setPrefix(prefix.getText());
	  document.graph.getModel().toFront(new Object[]{node});	  
        }
        public void insertUpdate(DocumentEvent e)
        {
          node.setPrefix(prefix.getText());
	  document.graph.getModel().toFront(new Object[]{node});	  
        }
        public void removeUpdate(DocumentEvent e) 
        {          
	  node.setPrefix(prefix.getText());
	  document.graph.getModel().toFront(new Object[]{node});
        }
      });
      prefBox.add(prefix);
      prefBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      prefBox.add(new JLabel(" Suffix "));
      final JTextField suffix = new JTextField(""+node.getSuffix(), 30);
      if (node.isRoot()) suffix.setEditable(false);       
      suffix.getDocument().addDocumentListener(new DocumentListener(){
        public void changedUpdate(DocumentEvent e)
        {
          node.setSuffix(suffix.getText());
	  document.graph.getModel().toFront(new Object[]{node});	  
        }
        public void insertUpdate(DocumentEvent e)
        {
          node.setSuffix(suffix.getText());
	  document.graph.getModel().toFront(new Object[]{node});	  
        }
        public void removeUpdate(DocumentEvent e) 
        {
          node.setSuffix(suffix.getText());
	  document.graph.getModel().toFront(new Object[]{node});	  
        }
      });
      prefBox.add(suffix);

      Box actionsBox = new Box(BoxLayout.X_AXIS);
      TableModel dataModel = new AbstractTableModel()
      {
        public int getColumnCount()
        {
          return 3;
        }
        public int getRowCount()
        {
          return node.getClaims().size();
        }
        public Object getValueAt(int row, int col)
        {
          ArgumentClaim currentClaim = (ArgumentClaim) node.getClaims().get(row);
          if (currentClaim == null)
          {
            return null;
          }
          else
          {
            if (col == 0) return currentClaim.getClaim();
            if (col == 1) return currentClaim.getWeight();
            if (col == 2) return currentClaim.getThreshold();
          }
          return null;
        }

        public void setValueAt(Object aValue, int rowIndex, int col)
        {
          ArgumentClaim currentClaim = (ArgumentClaim) node.getClaims().get(rowIndex);
          if (col == 0) currentClaim.setClaim((String) aValue);
          if (col == 1) currentClaim.setWeight((String) aValue);
          if (col == 2) currentClaim.setThreshold((String) aValue);
          node.getClaims().setElementAt(currentClaim, rowIndex);
	  document.graph.getModel().toFront(new Object[]{node});
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
          if (node.isRoot()) return false;
	  return true;
        }

        public String getColumnName(int col)
        {
          if (col == 0) return "Claim";
          if (col == 1) return "Weight";
          if (col == 2) return "Threshold";
          return null;
        }
      };
 
      final JTable table = new JTable(dataModel);
      JScrollPane tableScrollPane = new JScrollPane(table);
      tableScrollPane.setPreferredSize(new Dimension(200,100));
      
      actionsBox.add(tableScrollPane);
      actionsBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      Box editActionBox = new Box(BoxLayout.X_AXIS);
      final JButton addAction = new JButton("Add Claim");
      if (node.isRoot()) addAction.setEnabled(false); 
      addAction.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
          node.addClaim("New Claim", new Double(0), new Double(0));
          table.tableChanged(null);       
	  document.graph.getModel().toFront(new Object[]{node});
        }
      });
      final JButton delAction = new JButton("Remove Claim");
      delAction.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
          node.delClaim(table.getSelectedRow());
          table.tableChanged(null);
	  delAction.setEnabled(false);
	  document.graph.getModel().toFront(new Object[]{node});	  
        }
      });
      delAction.setEnabled(false);
      
      ListSelectionModel rowSM = table.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e)
	{         
              if(table.getSelectedRow() != -1)
	      {
	        if (!node.isRoot())
	          delAction.setEnabled(true);
	      }
	      else
	      {
	       delAction.setEnabled(false);
	      }	   
	}
      });
      
      editActionBox.add(addAction);
      editActionBox.add(Box.createRigidArea(new Dimension(5,5)));
      editActionBox.add(delAction);
      editActionBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      
      
      pBox.add(idBox);      
      pBox.add(titleBox);
      pBox.add(relevanceBox);
      pBox.add(prefBox);      
      pBox.add(new JLabel("Claims"));
      pBox.add(actionsBox);
      pBox.add(editActionBox);
      oTab.add(pBox);
    }

    pTab.addTab("Object", oTab);
    pTab.addTab("Visual", vTab);

    if (properties == null)
    {
      properties = new JDialog(mainFrame, "Properties", false);
      properties.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
    }

    properties.getContentPane().removeAll();
    properties.getContentPane().add(pTab);
    properties.pack();
    if (object != null) properties.show();
  }

  /*
   * Populates JToolbar toolbar...eventually from a text/xml file?
   */
  private void makeToolbar()
  {
    toolbar = new JToolBar();
    toolbar.setFloatable(true);
    insertsButtonGroup = new ButtonGroup();
    argumentButtonGroup = new ButtonGroup();
    
    // Move mode

    moveButton = new JToggleButton();
    URL moveURL = getClass().getClassLoader().getResource("justdraw/resources/move.gif");
    ImageIcon moveIcon = new ImageIcon(moveURL);
    moveButton.setName("move");
    moveButton.setEnabled(true);
    moveButton.setAction(new AbstractAction("", moveIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        changeMode(e);
      }
    });
    insertsButtonGroup.add(moveButton);
    toolbar.add(moveButton);
    
    toolbar.addSeparator();
    
    // locked button
    
    lockedButton = new LockButton();
    lockedButton.setSelected(false);

    toolbar.add(lockedButton);
    toolbar.addSeparator();
    
    // Insert DecisionBasic

    insertDecisionButton = new JToggleButton();
    URL decisionModeURL = getClass().getClassLoader().getResource("justdraw/resources/ellipse.gif");
    ImageIcon decisionModeIcon = new ImageIcon(decisionModeURL);
    insertDecisionButton.setName("insertDecision");
    insertDecisionButton.setAction(new AbstractAction("", decisionModeIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        changeMode(e);
      }
    });
    insertsButtonGroup.add(insertDecisionButton);
    toolbar.add(insertDecisionButton);

    // Insert Conclusion

    insertConclusionButton = new JToggleButton();
    URL conclusionModeURL = getClass().getClassLoader().getResource("justdraw/resources/rectangle.gif");
    ImageIcon conclusionModeIcon = new ImageIcon(conclusionModeURL);
    insertConclusionButton.setName("insertConclusion");
    insertConclusionButton.setAction(new AbstractAction("", conclusionModeIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        changeMode(e);
      }
    });
    insertsButtonGroup.add(insertConclusionButton);
    toolbar.add(insertConclusionButton);

    // Insert Claim

    insertClaimButton = new JToggleButton();
    URL claimModeURL = getClass().getClassLoader().getResource("justdraw/resources/connecton.gif");
    ImageIcon claimModeIcon = new ImageIcon(claimModeURL);
    insertClaimButton.setName("insertClaim");
    insertClaimButton.setAction(new AbstractAction("", claimModeIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        changeMode(e);
      }
    });
    insertsButtonGroup.add(insertClaimButton);
    toolbar.add(insertClaimButton);

    // Insert Text

    insertTextButton = new JToggleButton();
    URL textModeURL = getClass().getClassLoader().getResource("justdraw/resources/text.gif");
    ImageIcon textModeIcon = new ImageIcon(textModeURL);
    insertTextButton.setName("insertText");
    insertTextButton.setAction(new AbstractAction("", textModeIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        changeMode(e);
      }
    });
    insertsButtonGroup.add(insertTextButton);
    toolbar.add(insertTextButton);
    
    toolbar.addSeparator();    
    // Insert ArgumentNode

    insertArgumentButton = new JToggleButton();
    URL argumentModeURL = getClass().getClassLoader().getResource("justdraw/resources/tofront.gif");
    ImageIcon argumentModeIcon = new ImageIcon(argumentModeURL);
    insertArgumentButton.setName("insertArgument");
    insertArgumentButton.setAction(new AbstractAction("", argumentModeIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        changeMode(e);
      }
    });
    argumentButtonGroup.add(insertArgumentButton);
    toolbar.add(insertArgumentButton);

    connectArgumentButton = new JToggleButton();
    URL caModeURL = getClass().getClassLoader().getResource("justdraw/resources/edge.gif");
    ImageIcon caModeIcon = new ImageIcon(caModeURL);
    connectArgumentButton.setName("insertClaim");
    connectArgumentButton.setAction(new AbstractAction("", caModeIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        changeMode(e);
      }
    });
    argumentButtonGroup.add(connectArgumentButton);
    toolbar.add(connectArgumentButton);
    
    toolbar.addSeparator();
    
    // Zoom 1 to 1

    URL zoomOneToOneUrl = getClass().getClassLoader().getResource("justdraw/resources/zoom.gif");
    ImageIcon zoomOneToOneIcon = new ImageIcon(zoomOneToOneUrl);
    toolbar.add(new AbstractAction("", zoomOneToOneIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        document.graph.setScale(0.75);
      }
    });

    // Zoom In

    URL zoomInUrl = getClass().getClassLoader().getResource("justdraw/resources/zoomin.gif");
    ImageIcon zoomInIcon = new ImageIcon(zoomInUrl);
    toolbar.add(new AbstractAction("", zoomInIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        document.graph.setScale(document.graph.getScale() / 0.75);
      }
    });

    // Zoom Out

    URL zoomOutUrl = getClass().getClassLoader().getResource("justdraw/resources/zoomout.gif");
    ImageIcon zoomOutIcon = new ImageIcon(zoomOutUrl);
    toolbar.add(new AbstractAction("", zoomOutIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        document.graph.setScale(document.graph.getScale() * 0.75);
      }
    });

    toolbar.addSeparator();

    // Undo

    URL undoUrl = getClass().getClassLoader().getResource("justdraw/resources/undo.gif");
    ImageIcon undoIcon = new ImageIcon(undoUrl);
    undo = new AbstractAction("", undoIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        document.undo();
      }
    };
    undo.setEnabled(false);
    toolbar.add(undo);

    // Redo
    URL redoUrl = getClass().getClassLoader().getResource("justdraw/resources/redo.gif");
    ImageIcon redoIcon = new ImageIcon(redoUrl);
    redo = new AbstractAction("", redoIcon)
    {
      public void actionPerformed(ActionEvent e)
      {
        document.redo();
      }
    };
    redo.setEnabled(false);
    toolbar.add(redo);
    
    insertDecisionButton.setEnabled(false);
    insertConclusionButton.setEnabled(false);
    insertClaimButton.setEnabled(false);
    insertArgumentButton.setEnabled(false);
    connectArgumentButton.setEnabled(false);
  }

// Methods

  private void newDocument()
  {
    final JustDrawDocument newDocument = new JustDrawDocument(this);
    document = newDocument;
    desktop.add(newDocument.getFrame());
    exportXMLMenuItem.setEnabled(true);
    exportMySQLMenuItem.setEnabled(true);
    exportCSVMenuItem.setEnabled(true);
    insertDecisionButton.setEnabled(true);
    insertConclusionButton.setEnabled(true);
    insertClaimButton.setEnabled(true);    
  }
  
  
  private boolean importXML()
  {
    // File name dialog box
    final JustDrawXMLReader xmlReader = new JustDrawXMLReader();
    FileDialog xmlFileDialogBox = new FileDialog(mainFrame, "Selecting file to import");
    xmlFileDialogBox.setFile("*.xml");
    xmlFileDialogBox.setFilenameFilter(new FilenameFilter()
    {
      public boolean accept(File dir, String file)
      {
        if (file.endsWith("xml")) return true;
	return false;
      }
    });
    xmlFileDialogBox.show(); 
    final String filenamenopath = xmlFileDialogBox.getFile();
    if (filenamenopath == null)
    {
      return false;
    }
    String filename = xmlFileDialogBox.getDirectory() + System.getProperty("file.separator").charAt(0) + xmlFileDialogBox.getFile();
    xmlFileDialogBox.dispose();
    mainFrame.update(mainFrame.getGraphics());
    HashMap nodeMap = ((HashMap)xmlReader.getRootNodes(filename));

    if (nodeMap != null)
    {
      if (nodeMap.size() == 0)
      {
        showErrorDialog("No root node specified in graph!");
        return false;
      }
      if (nodeMap.size() == 1)
      {
          document = xmlReader.buildTree(self, (String)(nodeMap.values().toArray()[0]), filenamenopath.substring(0,filenamenopath.length() - 4));
	  desktop.add(document.getFrame());
      }
      else
      {
      final JList trees = new JList (nodeMap.values().toArray());
      
      trees.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      final JDialog decisionTreeChoser = new JDialog(mainFrame, "Select Decision Tree to Edit");
      JScrollPane treeScroll = new JScrollPane(trees);
      Box buttonBox = new Box(BoxLayout.X_AXIS);
      final JButton selectButton = new JButton("Select");
      selectButton.setEnabled(false);
      selectButton.addActionListener(new ActionListener()
      {
        public void actionPerformed (ActionEvent e)
	{    
	  document = xmlReader.buildTree(self, (String)trees.getSelectedValue(), filenamenopath.substring(0,filenamenopath.length() - 4));
	  decisionTreeChoser.dispose();  
	  desktop.add(document.getFrame());
	}
      });
      JButton cancelButton = new JButton("Cancel");

      cancelButton.addActionListener(new ActionListener()
      {
        public void actionPerformed (ActionEvent e)
        {
	  decisionTreeChoser.dispose();
	}
      });
      trees.addListSelectionListener(new ListSelectionListener()
      {
       public void valueChanged (ListSelectionEvent e)
       {
         if (trees.getSelectedIndex() != -1)
	 {
	   selectButton.setEnabled(true);
	 }
	 else
	 {
	   selectButton.setEnabled(false);
	 }
       }
      });

      buttonBox.add(Box.createVerticalStrut(30));
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(selectButton);
      buttonBox.add(Box.createHorizontalGlue());
      decisionTreeChoser.getContentPane().setLayout(new BorderLayout());
      decisionTreeChoser.getContentPane().add(treeScroll, BorderLayout.CENTER);      
      decisionTreeChoser.getContentPane().add(buttonBox, BorderLayout.SOUTH);      
      decisionTreeChoser.setSize(400, 400);
      decisionTreeChoser.show();
      }
    }
    
    // File name to XML reader
    insertDecisionButton.setEnabled(true);
    insertConclusionButton.setEnabled(true);
    insertClaimButton.setEnabled(true);
    return true;
  }
  /*
  private boolean importMySQL()
  {
    // File name dialog box
    final JustDrawMySQLReader xmlReader = new JustDrawMySQLReader();
    FileDialog xmlFileDialogBox = new FileDialog(mainFrame, "Selecting file to import");
    xmlFileDialogBox.setFile("*");
    xmlFileDialogBox.setFilenameFilter(new FilenameFilter()
    {
      public boolean accept(File dir, String file)
      {
        if (file.endsWith("")) return true;
	return false;
      }
    });
    xmlFileDialogBox.show(); 
    final String filenamenopath = xmlFileDialogBox.getFile();
    if (filenamenopath == null)
    {
      return false;
    }
    String filename = xmlFileDialogBox.getDirectory() + System.getProperty("file.separator").charAt(0) + xmlFileDialogBox.getFile();
    xmlFileDialogBox.dispose();
    mainFrame.update(mainFrame.getGraphics());
  
    HashMap nodeMap = ((HashMap)xmlReader.getRootNodes(filename));

    if (nodeMap != null)
    {
      if (nodeMap.size() == 0)
      {
        showErrorDialog("No root node specified in graph!");
        return false;
      }
      if (nodeMap.size() == 1)
      {
          document = xmlReader.buildTree(self, (String)(nodeMap.values().toArray()[0]), filenamenopath.substring(0,filenamenopath.length() - 4));
	  desktop.add(document.getFrame());
      }
      else
      {
      final JList trees = new JList (nodeMap.values().toArray());
      
      trees.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      final JDialog decisionTreeChoser = new JDialog(mainFrame, "Select Decision Tree to Edit");
      JScrollPane treeScroll = new JScrollPane(trees);
      Box buttonBox = new Box(BoxLayout.X_AXIS);
      final JButton selectButton = new JButton("Select");
      selectButton.setEnabled(false);
      selectButton.addActionListener(new ActionListener()
      {
        public void actionPerformed (ActionEvent e)
	{    
	  document = xmlReader.buildTree(self, (String)trees.getSelectedValue(), filenamenopath.substring(0,filenamenopath.length() - 4));
	  decisionTreeChoser.dispose();  
	  desktop.add(document.getFrame());
	}
      });
      JButton cancelButton = new JButton("Cancel");

      cancelButton.addActionListener(new ActionListener()
      {
        public void actionPerformed (ActionEvent e)
        {
	  decisionTreeChoser.dispose();
	}
      });
      trees.addListSelectionListener(new ListSelectionListener()
      {
       public void valueChanged (ListSelectionEvent e)
       {
         if (trees.getSelectedIndex() != -1)
	 {
	   selectButton.setEnabled(true);
	 }
	 else
	 {
	   selectButton.setEnabled(false);
	 }
       }
      });

      buttonBox.add(Box.createVerticalStrut(30));
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(selectButton);
      buttonBox.add(Box.createHorizontalGlue());
      decisionTreeChoser.getContentPane().setLayout(new BorderLayout());
      decisionTreeChoser.getContentPane().add(treeScroll, BorderLayout.CENTER);      
      decisionTreeChoser.getContentPane().add(buttonBox, BorderLayout.SOUTH);      
      decisionTreeChoser.setSize(400, 400);
      decisionTreeChoser.show();
      }
    }
    
    // File name to XML reader
    insertDecisionButton.setEnabled(true);
    insertConclusionButton.setEnabled(true);
    insertClaimButton.setEnabled(true);
    return true;
  }
  
  */
  private void exportXML()
    {
    // File name dialog box
    FileDialog xmlFileDialogBox = new FileDialog(mainFrame, "Selecting filename for export", FileDialog.SAVE);
    String defaultName;
    Object root;
    boolean hasRoot = false;
    int rootCount;
    
    rootCount = document.graph.getModel().getRootCount();    
    for (int i  = 0; i < rootCount; i++)
    {
      root = document.graph.getModel().getRootAt(i);
      if (root instanceof DecisionTreeNode)
      {
        if (((DecisionTreeNode)root).isRoot())
	{
	  hasRoot = true;
	}
      }
    }
    if (hasRoot)
    {
      if (document == null)    
      {
        defaultName = "NewDocument.xml";
      }
      else
      {
        defaultName = document.name + ".xml";
      }
      xmlFileDialogBox.setFile(defaultName);
      xmlFileDialogBox.show(); 
      String filename = xmlFileDialogBox.getDirectory() + System.getProperty("file.separator").charAt(0) + xmlFileDialogBox.getFile();
      xmlFileDialogBox.dispose();
      mainFrame.update(mainFrame.getGraphics());
    
      JustDrawXMLWriter.exportXML(filename, document);
    }
    else
    {
      JustDraw.showErrorDialog("No root specified for this graph");
    }
  }
  /////////// edi nial
  
  
  private void exportMySQL()
  {
  // File name dialog box
  FileDialog xmlFileDialogBox = new FileDialog(mainFrame, "Selecting filename for export", FileDialog.SAVE);
  String defaultName;
  Object root;
  boolean hasRoot = false;
  int rootCount;
  
  rootCount = document.graph.getModel().getRootCount();    
  for (int i  = 0; i < rootCount; i++)
  {
    root = document.graph.getModel().getRootAt(i);
    if (root instanceof DecisionTreeNode)
    {
      if (((DecisionTreeNode)root).isRoot())
	{
	  hasRoot = true;
	}
    }
  }
  if (hasRoot)
  {
    if (document == null)    
    {
      defaultName = "NewDocument.sql";
    }
    else
    {
      defaultName = document.name + ".sql";
    }
    xmlFileDialogBox.setFile(defaultName);
    xmlFileDialogBox.show(); 
    String filename = xmlFileDialogBox.getDirectory() + System.getProperty("file.separator").charAt(0) + xmlFileDialogBox.getFile();
    xmlFileDialogBox.dispose();
    mainFrame.update(mainFrame.getGraphics());
  
    JustDrawMySQLWriter.exportMySQL(filename, document);
  }
  else
  {
    JustDraw.showErrorDialog("No root specified for this graph");
  }
}

  
  /////////////
    private void exportCSV()
    {
    // File name dialog box
    FileDialog xmlFileDialogBox = new FileDialog(mainFrame, "Selecting filename for export", FileDialog.SAVE);
    String defaultName;
    Object root;
    boolean hasRoot = false;
    int rootCount;
    
    rootCount = document.graph.getModel().getRootCount();    
    for (int i  = 0; i < rootCount; i++)
    {
      root = document.graph.getModel().getRootAt(i);
      if (root instanceof DecisionTreeNode)
      {
        if (((DecisionTreeNode)root).isRoot())
	{
	  hasRoot = true;
	}
      }
    }
    if (hasRoot)
    {
      if (document == null)    
      {
        defaultName = "NewDocument.csv";
      }
      else
      {
        defaultName = document.name + ".csv";
      }
      xmlFileDialogBox.setFile(defaultName);
      xmlFileDialogBox.show(); 
      String filename = xmlFileDialogBox.getDirectory() + System.getProperty("file.separator").charAt(0) + xmlFileDialogBox.getFile();
      xmlFileDialogBox.dispose();
      mainFrame.update(mainFrame.getGraphics());
    
      JustDrawCSVWriter.exportCSV(filename, document);
    }
    else
    {
      JustDraw.showErrorDialog("No root specified for this graph");
    }
  }
  
  // Change the editing mode
  private void changeMode(ActionEvent e)
  {
    try
    {
      ((JToggleButton)e.getSource()).setEnabled(true);
      editMode = ((JToggleButton)e.getSource()).getName();
    }
    catch (Exception ex)
    {
    }
  }
  
  public void toMoveMode()
  {
    if (lockedButton.isSelected()==false)
    {
      moveButton.setEnabled(true);
      moveButton.setSelected(true);
      editMode = moveButton.getName();
    }
  }
  
  public void dtSelected()
  {
    moveButton.setEnabled(true);
    moveButton.setSelected(true);
    editMode = moveButton.getName();  
    argumentButtonGroup.remove(moveButton);
    insertsButtonGroup.add(moveButton);    
    insertDecisionButton.setEnabled(true);
    insertConclusionButton.setEnabled(true);
    insertClaimButton.setEnabled(true);
    insertArgumentButton.setEnabled(false);
    connectArgumentButton.setEnabled(false);

  }

  public void argumentSelected()
  {
    moveButton.setEnabled(true);
    moveButton.setSelected(true);
    editMode = moveButton.getName();  
    insertsButtonGroup.remove(moveButton);
    argumentButtonGroup.add(moveButton);
    insertDecisionButton.setEnabled(false);
    insertConclusionButton.setEnabled(false);
    insertClaimButton.setEnabled(false);
    insertArgumentButton.setEnabled(true);
    connectArgumentButton.setEnabled(true);
  }
  
  public static void showErrorDialog(String text)
  {
     final JDialog errorBox = new JDialog();

     Box mainBox = new Box(BoxLayout.Y_AXIS);
     Box textBox = new Box(BoxLayout.X_AXIS);
     Box buttonBox = new Box(BoxLayout.X_AXIS);
     
     errorBox.setTitle("Error");
     errorBox.setModal(true);
     errorBox.setResizable(false);
     
     JButton okButton = new JButton("OK");
     okButton.addActionListener(new ActionListener()
     {
       public void actionPerformed(ActionEvent e)
       {
        errorBox.dispose();
       }
     });
     
     buttonBox.add(Box.createHorizontalGlue());
     buttonBox.add(Box.createVerticalStrut(30));
     buttonBox.add(okButton);
     buttonBox.add(Box.createVerticalStrut(30));
     buttonBox.add(Box.createHorizontalGlue());
     
     textBox.add(Box.createHorizontalGlue());
     textBox.add(Box.createHorizontalStrut(5));
     textBox.add(new JLabel(text));
     textBox.add(Box.createHorizontalStrut(5));
     textBox.add(Box.createHorizontalGlue());
     
     mainBox.add(textBox);
     mainBox.add(buttonBox);
     
     errorBox.getContentPane().add(mainBox);
     errorBox.pack();
     errorBox.show();
  }
}
