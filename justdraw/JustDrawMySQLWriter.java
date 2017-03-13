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

import java.lang.Object;

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

import com.uob.utility.HTMLConverter;

// import com.uob.utility.HTMLConverter;

public class JustDrawMySQLWriter {

	public JustDrawMySQLWriter() {
	}

	public static String srt_replace( String toReplace ) {
		System.out.print( toReplace );
		toReplace = toReplace.replace( '[', '<' );
		toReplace = toReplace.replace( ']', '>' );
		System.out.print( toReplace );
		return toReplace;
	}
		

	public static void exportMySQL(String filename, JustDrawDocument document) {
		
		AutoId autoId = new AutoId("AutoId");
		File file = new File(filename);
		BufferedWriter writer;
		int rootCount;
		Object root, child, target, notsure;
		Iterator children, edges;
		Edge currentEdge;
		Rectangle2D bounds;
		String id;

		try {
			String spacer = "", spacer2 = "";
			writer = new BufferedWriter(new FileWriter(file), 16384);

			for (int xx = 0; xx < document.getDocPrefix().length(); xx++) {
				spacer += " ";
				spacer2 += "-";
			}

			String file_header = "-- ------------------------------------"
					+ spacer2 + " --\n"
					+ "--                                     " + spacer
					+ " --\n" + "-- MySQL Generated with JustDraw       "
					+ spacer + " --\n"
					+ "--                                     " + spacer
					+ " --\n" + "-- tablePrefix=\"" + document.getDocPrefix()
					+ "\"                       --\n"
					+ "-- ------------------------------------" + spacer2
					+ " --\n\n";
			String drop_header = "DROP TABLE IF EXISTS`"
					+ document.getDocPrefix() + "claimvalues` , " + "`"
					+ document.getDocPrefix() + "genericarguments` , " + "`"
					+ document.getDocPrefix() + "actions` , " + "`"
					+ document.getDocPrefix() + "rootnodes` ;\n\n";
			// generic arguments header
			String ga_header = "CREATE TABLE `" + document.getDocPrefix()
					+ "genericarguments` ( \n"
					+ "   `id` varchar(36) NOT NULL default '0',\n"
					+ "   `title` varchar(100) NOT NULL default '', \n"
					+ "   `prefix` MEDIUMTEXT, \n"
					+ "   `suffix` MEDIUMTEXT, \n"
					+ "   `parent` varchar(36) NOT NULL default '', \n"
					+ "   `relevance` MEDIUMTEXT, \n"
					+ "   `not_sure` varchar(36) default NULL, \n"
					+ "   `type` char(1) NOT NULL default '', \n"
					+ "   `display_type` varchar(25) NOT NULL default 'r', \n"
					+ "   `graph_id` varchar(20) default NULL, \n"
					+ "   `action` MEDIUMTEXT, \n"
					+ "   `Xpos` decimal(6) NOT NULL default '0', \n"
					+ "   `Ypos` decimal(6) NOT NULL default '0', \n"
					+ "   PRIMARY KEY  (`id`) \n"
					+ "  ) ENGINE=MyISAM DEFAULT CHARSET=latin1;\n\n";
			  
		    // claim values header
			String cv_header = "CREATE TABLE `" + document.getDocPrefix()
					+ "claimvalues` (\n"
					+ "   `choice_id` tinyint(4) NOT NULL default '0', \n"
					+ "   `argument_id` varchar(36) NOT NULL default '0',\n"
					+ "   `claim` MEDIUMTEXT, \n"
					+ "   `threshold` tinyint(4) default NULL, \n"
					+ "   `weight` varchar(36) default NULL, \n"
					+ "   `node_order` tinyint(4) default NULL, \n"
					+ "   `graph_id` varchar(20) default NULL, \n"
					// " `target` varchar(20) default NULL, \n" +
					+ "   `action` MEDIUMTEXT \n"
					// " `summary` varchar(50) default NULL \n"
					+ "  ) TYPE=MyISAM; \n\n";

			// stroy time header
			String st_header = "CREATE TABLE `" + document.getDocPrefix()
					+ "rootnodes` ( \n"
					+ "   `id` varchar(30) NOT NULL default '', \n"
					+ "   `startNode` varchar(50) NOT NULL default '', \n"
					+ "   `tblPrefix` varchar(50) NOT NULL default '', \n"
					+ "   `desctiption` varchar(100) NOT NULL default '', \n"
					+ "   PRIMARY KEY `id` (`id`) \n" + "   ) TYPE=MyISAM; \n\n";
			String ac_header = "CREATE TABLE `" + document.getDocPrefix()
					+ "actions` ( \n" 
					+ "`action_id` VARCHAR( 50 ) NOT NULL default '', \n"
					+ "`action_type` VARCHAR( 50 ) NOT NULL default '', \n"
					+ "`action_value` MEDIUMTEXT NOT NULL \n ) TYPE=MyISAM; \n\n";

			writer.write(file_header);
			writer.flush();
			writer.write(drop_header);
			writer.flush();
			writer.write(ga_header);
			writer.flush();
			writer.write(cv_header);
			writer.flush();
			writer.write(st_header);
			writer.flush();
			writer.write(ac_header);
			writer.flush();

			// Write the root nodes
			rootCount = document.graph.getModel().getRootCount();
			for (int i = 0; i < rootCount; i++) {
				root = document.graph.getModel().getRootAt(i);
				if (root instanceof DecisionTreeNode) {
					if (((DecisionTreeNode) root).isRoot()) {
						id = ((DecisionTreeNode) root).getId();
						if (id.equals("") || id == null || id == "")
							((DecisionTreeNode) root).setId(autoId.getNextId());

						writer.write( JustDrawMySQLWriter.srt_replace( "INSERT INTO `" + document.getDocPrefix()
								+ "rootnodes` " + "VALUES (\""
								+ ((DecisionTreeNode) root).getId() + "\", \""
								+ ((DecisionTreeNode) root).getId() + "\", \""
								+ document.getDocPrefix() + "\", \""
								+ "root node\");\n"));
						writer.flush();
					}
				}
			}
			rootCount = document.graph.getModel().getRootCount();
			for (int i = 0; i < rootCount; i++) {
				root = document.graph.getModel().getRootAt(i);
				if (root instanceof DecisionTreeNode) {
					id = ((DecisionTreeNode) root).getId();
					if (id.equals("") || id == null || id == "")
						((DecisionTreeNode) root).setId(autoId.getNextId());
					bounds = document.graph.getGraphLayoutCache().getMapping(
							root, false).getBounds();
					String dtn_str = "";

					dtn_str = "INSERT INTO `" + document.getDocPrefix()
							+ "genericarguments` " + "VALUES (\""
							+ ((DecisionTreeNode) root).getId() + "\", \""
							+ ((DecisionTreeNode) root).getTitle() + "\", \""
							+ ((DecisionTreeNode) root).getPrefix() + "\", \""
							+ ((DecisionTreeNode) root).getSuffix() + "\", \"";

					children = ((DecisionTreeNode) root).getChildren()
							.iterator();
					while (children.hasNext()) {
						child = children.next();
						if (child instanceof Port) {
							edges = ((Port) child).edges();
							TreeMap answerStrings = new TreeMap();
							// String[] answerStrings = new String[]{};

							int xx = 0;
							while (edges.hasNext()) {
								currentEdge = (Edge) edges.next();
								if (currentEdge.getSource().equals(child)) {
									if (currentEdge instanceof DecisionTreeArc) {

										// out put
										String cn_str = "";

										cn_str = "INSERT INTO `"
												+ document.getDocPrefix()
												+ "claimvalues`"
												+ "VALUES (\""
												+ xx
												+ "\", \""
												+ ((DecisionTreeNode) root)
														.getId()
												+ "\", \""
												+ ((DecisionTreeArc) currentEdge)
														.getClaim()
												+ "\", \""
												+ ((DecisionTreeArc) currentEdge)
														.getThreshold()
												+ "\", \"";

										xx++;
										target = ((DefaultPort) currentEdge
												.getTarget()).getParent();
										if (target instanceof DecisionTreeNode) {
											id = ((DecisionTreeNode) target)
													.getId();
											if (id.equals("") || id == null
													|| id == "")
												((DecisionTreeNode) target)
														.setId(autoId
																.getNextId());
											// out put
											cn_str = cn_str
													+ ((DecisionTreeNode) target)
															.getId() + "\", \"";

										}
										if (target instanceof ConclusionNode) {
											id = ((ConclusionNode) target)
													.getId();
											if (id.equals("") || id == null
													|| id == "")
												((ConclusionNode) target)
														.setId(autoId
																.getNextId());
											// out put
											cn_str = cn_str
													+ ((ConclusionNode) target)
															.getId() + "\", \"";

										}

										cn_str = cn_str
												+ ((DecisionTreeArc) currentEdge)
														.getPosition()
												+ "\", \""
												+ ((DecisionTreeNode) root)
														.getGraph_id()
												+ "\", \"";
												//+ ((DecisionTreeArc) currentEdge)
												//		.getSummary()
										

						//*************//				
										String currentString = "";
											
										Iterator actions = ((Vector)((DecisionTreeArc)currentEdge).getActions()).iterator();
										TreeArcAction currentAction;
										
										if(actions.hasNext()){
										   cn_str = cn_str + xx + "_"	+ ((DecisionTreeNode) root).getId() 
										                   + "\" );\n";
										}
										else{
											cn_str = cn_str + " \" );\n";
										}
										
										System.out.print( "writing cn_str:" + cn_str);
										writer.write( JustDrawMySQLWriter.srt_replace( cn_str ));
										System.out.print( " -- done\n" );
										writer.flush();										
										
										while (actions.hasNext()){
											
										      currentAction = (TreeArcAction)actions.next();
										  if (currentAction.getType().toLowerCase().equals("setaction") ||  currentAction.getType().toLowerCase().equals("action") )
										      {
										        currentString = currentString +"INSERT INTO `"
																			  + document.getDocPrefix()
																			  + "actions`"
																			  + "VALUES (\"" + xx + "_"	+ ((DecisionTreeNode) root).getId() +"\" , \""
								                                              + HTMLConverter.escape( currentAction.getVariable()) + "\", \""  
								                                              + HTMLConverter.escape(currentAction.getValue()) +"\" );\n";
										      }
										      else //if (currentAction.getType().toLowerCase().equals("setAction") ||  currentAction.getType().toLowerCase().equals("action") )
										      {
										    	  System.out.print("Bad Action: action not writen to sql\n");
										      }
										    }
										System.out.print("writing currentString: " );
										writer.write( currentString );
										System.out.print(" -- done \n " ) ;
										writer.flush();
										
							/**********
										System.out.println("Connection from "
												+ root
												+ " to "
												+ ((DefaultPort) currentEdge
														.getTarget())
														.getParent()); 
							*/			
									}
								}
								// answerStrings.put(new
								// Integer(((DecisionTreeArc)currentEdge).getPosition()),
								// currentString);
							}
							//System.out.println(answerStrings);
							// Iterator it = answerStrings.values().iterator();
							/*
							 * while (it.hasNext()) {
							 * writer.write((String)it.next()); }
							 */
						}
					}// end while

					if (((DecisionTreeNode) root).hasArgument) {

						// writer.newLine()();
						notsure = ((JustDrawArgument) document.arguments
								.get(root)).getRootNode();
						children = ((ArgumentTreeNode) notsure).getChildren()
								.iterator();
						while (children.hasNext()) {
							child = children.next();
							if (child instanceof Port) {
								edges = ((Port) child).edges();
								while (edges.hasNext()) {
									currentEdge = (Edge) edges.next();
									if (currentEdge.getTarget().equals(child)) {
										if (currentEdge instanceof ArgumentArc) {
											id = ((ArgumentTreeNode) ((DefaultPort) currentEdge
													.getSource()).getParent())
													.getId();
											if (id.equals("") || id == null
													|| id == "")
												((ArgumentTreeNode) ((DefaultPort) currentEdge
														.getSource())
														.getParent())
														.setId(autoId
																.getNextId());
											// writer.write(" <notSureNode
											// refid=\"" +
											// dtn_str +=
											// ((ArgumentTreeNode)((DefaultPort)currentEdge.getSource()).getParent()).getId()
											// + "\", \"";
											// + "\"/>");
											// writer.newLine()();
											// + "Parent" + "\", \""
											// dtn_str += "" + "\", \"";

										}
									} else {
										// dtn_str += "" + "\", \"";

									}
								}
							}
						}
						dtn_str = dtn_str + "" + "\", \""
								+ ((DecisionTreeNode) root).getRelevance()
								+ "\", \"" + "1\", \"" + "D\", \"";

						// not
						// not sure
					} else {
						// no not sures
						dtn_str = dtn_str + "" + "\", \""
								+ ((DecisionTreeNode) root).getRelevance()
								+ "\", \"" + "0\", \"" + "D\", \"";

						// not
						// sure
					}

					dtn_str = dtn_str
							+ ((DecisionTreeNode) root).getDisplay_type()
							+ "\", \""
							+ ((DecisionTreeNode) root).getGraph_id()
							+ "\", \"" + "action" + "\", \"" // + "" + "\",
																// \""
							+ bounds.getX() + "\", \"" + bounds.getY()
							+ "\" );\n";

					System.out.print( "writing dtn_str: " +dtn_str);

					writer.write( JustDrawMySQLWriter.srt_replace( dtn_str ));
					System.out.print( " -- done\n " );
					writer.newLine();
					writer.flush();
				}

				// concluseion
				if (root instanceof ConclusionNode) {
					id = ((ConclusionNode) root).getId();
					if (id.equals("") || id == null || id == "")
						((ConclusionNode) root).setId(autoId.getNextId());

					bounds = document.graph.getGraphLayoutCache().getMapping(
							root, false).getBounds();

					String ctn_str = "";

					ctn_str = "INSERT INTO `" + document.getDocPrefix()
							+ "genericarguments` " + "VALUES (\""
							+ ((ConclusionNode) root).getId() + "\", \""
							+ ((ConclusionNode) root).getTitle() + "\", \""
							+ ((ConclusionNode) root).getPrefix() + "\", \""
							+ ((ConclusionNode) root).getSuffix() + "\", \""
							+ "\", \"" + ((ConclusionNode) root).getRelevance()
							+ "\", \"" + "0\", \"" + "C\", \""
							+ ((ConclusionNode) root).getDisplay_type()
							+ "\", \"" + ((ConclusionNode) root).getGraph_id()
							+ "\", \"" + "action" + "\", \"" + bounds.getX()
							+ "\", \"" + bounds.getY() + "\" ); \n";

					System.out.print(" writing ctn_sre: " + ctn_str);
					writer.write( JustDrawMySQLWriter.srt_replace( ctn_str ));
					System.out.print( " -- done\n " );
					writer.newLine();
					writer.flush();
				}
			}

			Enumeration e = document.arguments.elements();
			Enumeration arguments;
			JGraph currentGraph;
			// int rootCount;
			// Object root;
			while (e.hasMoreElements()) {
				currentGraph = ((JustDrawArgument) e.nextElement()).graph;
				rootCount = currentGraph.getModel().getRootCount();
				String pid = "";
				String ns = "0";

				for (int i = 0; i < rootCount; i++) {

					root = currentGraph.getModel().getRootAt(i);
					if (root instanceof ArgumentTreeNode) {
						if (((ArgumentTreeNode) root).isRoot()) {
							pid = ((ArgumentTreeNode) root).getId();

						} else {
							bounds = currentGraph.getGraphLayoutCache()
									.getMapping(root, false).getBounds();
							String arg_str = "INSERT INTO `"
									+ document.getDocPrefix()
									+ "genericarguments` " + "VALUES (\""
									+ ((ArgumentTreeNode) root).getId()
									+ "\", \""
									+ ((ArgumentTreeNode) root).getTitle()
									+ "\", \""
									+ ((ArgumentTreeNode) root).getPrefix()
									+ "\", \""
									+ ((ArgumentTreeNode) root).getSuffix()
									+ "\", \"";

							// + ((ArgumentTreeNode) root).getParent() + "\",
							// \""
							// +
							// ((ArgumentTreeNode(root)currentEdge.getSource()).getParent()).getId()+
							// "\", \""

							;

							arguments = ((ArgumentTreeNode) root).getClaims()
									.elements();

							int xx = 0;
							ns = "0";
							while (arguments.hasMoreElements()) {
								child = arguments.nextElement();
								String arg_calim_str = "INSERT INTO `"
										+ document.getDocPrefix()
										+ "claimvalues` "
										+ "VALUES (\""
										+ xx
										+ "\", \""
										+ ((ArgumentTreeNode) root).getId()
										+ "\", \""
										+ ((ArgumentClaim) child).getClaim()
										+ "\", \""
										+ ((ArgumentClaim) child)
												.getThreshold() + "\", \""
										+ ((ArgumentClaim) child).getWeight()
										+ "\", \""
										// + "Order" + "\", \""
										+ xx + "\", \""
										// + "" + "\", \""
										+ "" + "\", \"" + "" + "\" );";
								xx++;
								System.out.print("writing arg_calim_str:" + arg_calim_str);
								
								writer.write( JustDrawMySQLWriter.srt_replace( arg_calim_str ));
								System.out.print( " -- done\n " );
								writer.newLine();
								writer.flush();
								
								children = ((ArgumentTreeNode) root)
										.getChildren().iterator();

								while (children.hasNext()) {
									child = children.next();
									if (child instanceof Port) {
										edges = ((Port) child).edges();
										while (edges.hasNext()) {
											currentEdge = (Edge) edges.next();

											if (currentEdge.getSource().equals(
													child)) {
												if (currentEdge instanceof ArgumentArc) {
													id = ((ArgumentTreeNode) ((DefaultPort) currentEdge
															.getSource())
															.getParent())
															.getId();
													if (id.equals("")
															|| id == null
															|| id == "")
														((ArgumentTreeNode) ((DefaultPort) currentEdge
																.getSource())
																.getParent())
																.setId(autoId
																		.getNextId());

													// writer.write("
													// <notSureNode refid=\"" +

													pid = ((ArgumentTreeNode) ((DefaultPort) currentEdge
															.getTarget())
															.getParent())
															.getId()
															+ "\", \"";
													// ///////////// there is
													// something scrued up here
													// );

												}
											}
											if (currentEdge.getTarget().equals(
													child)
													|| ns == "1") {
												// Do whatever you want to do
												ns = "1";
												System.out
														.print("\n --------edge: "
																+ "true" + "");

											} else {
												System.out
														.print("\n --------edge: "
																+ "false" + ""); 
												ns = "0";
											}
										}
									}
								}

							}
							System.out.print("\n --------edge: " + ns + "\n");
							// System.out.print( "\n ns: " + ns + "\n");

							arg_str += pid 
									+ ((ArgumentTreeNode) root).getRelevance()
									+ "\", \"" + ns + "\", \"" + "A\", \""
									// + ((ArgumentTreeNode) root).getGraph_id()
									+ "radio\", \""
									+ ((ArgumentTreeNode) root).getGraph_id()
									+ "\", \"" + "action" + "\", \""
									+ bounds.getX() + "\", \"" + bounds.getY()
									+ "\" ); ";
							// writer.newLine();
							System.out.print("writing: "+arg_str);
							writer.write( JustDrawMySQLWriter.srt_replace( arg_str ));
							System.out.print(" --> done\n" );
							writer.newLine();
							writer.flush();
						}
					}
				}

			}
		System.out.print("->finished<-" );
		writer.flush();
		writer.close();
		System.out.print("->closed<-" );
		} 
		catch (Exception ex) {
			// ** Show dialog here
			ex.printStackTrace();
		}
	}
	
}
