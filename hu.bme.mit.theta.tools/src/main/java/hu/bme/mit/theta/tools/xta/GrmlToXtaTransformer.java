package hu.bme.mit.theta.tools.xta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import hu.bme.mit.theta.common.Tuple2;
import hu.bme.mit.theta.common.Tuple5;

public class GrmlToXtaTransformer {
	
	public static class XtaProcessStringRepresentation {
		private String name;
		private Set<String> clocks=new HashSet<>();
		private Set<String> params=new HashSet<>();
		private Set<String> discVars=new HashSet<>();
		private Set<String> chans=new HashSet<>();
		private Map<String,Tuple2<String,Optional<String>>> locs=new HashMap<>(); //id,name,invariant
		private Set<Tuple5<String,String,Optional<String>,List<String>,Optional<String>>> edges=new HashSet<>(); 
		private String initLocName;
		
		public void setName(String name) {
			this.name=name;
		}
		
		public void addSync(String label) {
			chans.add(label);
		}
		
		public void addClock(String clockName) {
			clocks.add(clockName);
		}
		
		public void addParameter(String paramName) {
			params.add(paramName);
		}
		
		public void addLoc(String id, String name, Optional<String> invar) {
			locs.put(id, Tuple2.of(name, invar));
		}
		
		public void setInitLoc(String initLocName) {
			this.initLocName=initLocName;
		}
		
		public void addEdge(String src, String trg,Optional<String> label,List<String> updates,Optional<String> guard) {
			edges.add(Tuple5.of(src, trg, label, updates, guard));
		}
		
		public String getName() {
			return name;
		}
		
		public Set<String> getClocks() {
			return clocks;
		}
		
		public Set<String> getChans() {
			return chans;
		}
		
		public Set<String> getParams() {
			return params;
		}
		
		public Map<String, Tuple2<String, Optional<String>>> getLocs() {
			return locs;
		}
		
		public String getInitLocName() {
			return initLocName;
		}
		
		public Set<Tuple5<String, String, Optional<String>, List<String>, Optional<String>>> getEdges() {
			return edges;
		}

		public void addDiscVar(String name) {
			discVars.add(name);
		}

		public Set<String> getAllVars() {
			Set<String> result=new HashSet<>(clocks);
			result.addAll(discVars);
			return result;
		}
	}
	
	public static XtaProcessStringRepresentation read(String name) {
		XtaProcessStringRepresentation result=new XtaProcessStringRepresentation();
		
		File fXmlFile = new File("src/test/resources/grml/"+name+".imi.grml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			//doc.getDocumentElement().normalize();//TODO: see what this does
			
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			//System.out.println(doc.getDocumentElement().getFirstChild());
			
			NodeList childNodes=doc.getChildNodes();
			
			Element root=doc.getDocumentElement();
			/*System.out.println(root.getNodeName());
			System.out.println(root.getNodeValue());
			System.out.println(root.getNodeType());*/
			//NamedNodeMap attribs=root.getAttributes();//formalismURL,xmlns
			NodeList children=root.getChildNodes();
			for (int child_indx=0; child_indx<children.getLength();child_indx++) {
				Node inode=children.item(child_indx);
				if (inode.getNodeName().contains("comment")) {
					String comment=inode.getNodeValue();
					String[] spacesplit=comment.split(" ");
					String processname=spacesplit[3].substring(0,spacesplit[3].length()-1);
					result.setName(processname);
				}
				if (inode.getNodeType() == Node.ELEMENT_NODE) {
					String iname=inode.getNodeName();
					if (iname.equals("attribute")) {
						if (inode.getAttributes().getLength()!=1) throw new IOException("Weird attributes in attribute node");
						Node attrnameattr= inode.getAttributes().item(0);
						if (!attrnameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in attribute node");
						String attrname=attrnameattr.getNodeValue();
						if (attrname.equals("variables")) {
							NodeList vartypes=inode.getChildNodes();
							for (int vartype_indx=0;vartype_indx<vartypes.getLength();vartype_indx++) {
								Node vartype=vartypes.item(vartype_indx);
								if (vartype.getNodeType()==Node.ELEMENT_NODE) {
									if (vartype.getAttributes().getLength()!=1) throw new IOException("Weird attributes in vartype");
									Node vartypenameattr= vartype.getAttributes().item(0);
									if (!vartypenameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in vartype");
									String vartypename=vartypenameattr.getNodeValue();
									if (vartypename.equals("clocks")) {
										NodeList vars=vartype.getChildNodes();
										for (int var_indx=0;var_indx<vars.getLength();var_indx++) {
											Node var=vars.item(var_indx);
											if (var.getNodeType()==Node.ELEMENT_NODE){
												if (var.getAttributes().getLength()!=1) throw new IOException("Weird attributes in clock");
												Node clockattr= var.getAttributes().item(0);
												if (!clockattr.getNodeName().equals("name")) throw new IOException("Not name attribute in clock");
												if (!clockattr.getNodeValue().equals("clock")) throw new IOException("Not clock");
												NodeList clocklist=var.getChildNodes();
												for (int clk_indx=0;clk_indx<clocklist.getLength();clk_indx++) {
													Node clock=clocklist.item(clk_indx);
													if (clock.getNodeType()==Node.ELEMENT_NODE){
														if (clock.getAttributes().getLength()!=1) throw new IOException("Weird attributes in clockname");
														Node clocknameattr= clock.getAttributes().item(0);
														if (!clocknameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in clockname");
														if (!clocknameattr.getNodeValue().equals("name")) throw new IOException("Clockname name attribute not name");
														result.addClock(clock.getTextContent());
													}
												}
											}
										}
									} else if (vartypename.equals("discretes")) {
										NodeList vars=vartype.getChildNodes();
										for (int var_indx=0;var_indx<vars.getLength();var_indx++) {
											Node var=vars.item(var_indx);
											if (var.getNodeType()==Node.ELEMENT_NODE){
												if (var.getAttributes().getLength()!=1) throw new IOException("Weird attributes in discrete");
												if (!((Element)var).getAttribute("name").equals("discrete"))
													throw new IOException("Not discrete");
												NodeList varlist=var.getChildNodes();
												for (int disc_indx=0;disc_indx<varlist.getLength();disc_indx++) {
													Node varnode=varlist.item(disc_indx);
													if (varnode.getNodeType()==Node.ELEMENT_NODE){
														if (varnode.getAttributes().getLength()!=1) throw new IOException("Weird attributes in varname");
														if (!((Element)varnode).getAttribute("name").equals("name"))
															throw new IOException("Varname name attribute not name");
														result.addDiscVar(varnode.getTextContent());
													}
												}
											}
										}
									} else throw new IOException("Variable type "+vartypename+" not yet supported.");
								}
							}
						} else if (attrname.equals("constants")) {
							NodeList consttypes=inode.getChildNodes();
							for (int consttype_indx=0;consttype_indx<consttypes.getLength();consttype_indx++) {
								Node consttype=consttypes.item(consttype_indx);
								if (consttype.getNodeType()==Node.ELEMENT_NODE) {
									if (consttype.getAttributes().getLength()!=1) throw new IOException("Weird attributes in consttype");
									Node consttypenameattr= consttype.getAttributes().item(0);
									if (!consttypenameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in consttype");
									String consttypename=consttypenameattr.getNodeValue();
									if (consttypename.equals("parameters")) {
										NodeList params=consttype.getChildNodes();
										for (int param_indx=0;param_indx<params.getLength();param_indx++) {
											Node param=params.item(param_indx);
											if (param.getNodeType()==Node.ELEMENT_NODE){
												if (param.getAttributes().getLength()!=1) throw new IOException("Weird attributes in param");
												Node paramattr= param.getAttributes().item(0);
												if (!paramattr.getNodeName().equals("name")) throw new IOException("Not name attribute in param");
												if (!paramattr.getNodeValue().equals("parameter")) throw new IOException("Not parameter");
												NodeList paramlist=param.getChildNodes();
												for (int prm_indx=0;prm_indx<paramlist.getLength();prm_indx++) {
													Node prm=paramlist.item(prm_indx);
													if (prm.getNodeType()==Node.ELEMENT_NODE){
														if (prm.getAttributes().getLength()!=1) throw new IOException("Weird attributes in paramname");
														Node prmnameattr= prm.getAttributes().item(0);
														if (!prmnameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in paramname");
														if (!prmnameattr.getNodeValue().equals("name")) throw new IOException("Paramname name attribute not name");
														result.addParameter(prm.getTextContent());
													}
												}
											}
										}
									} else throw new IOException("Non clock variable");
								}
							}
						} else throw new IOException("Attribute not variable or constant");
					} else if (iname.equals("node")) {
						String node_id;
						String node_name="";
						Optional<String> node_inv=Optional.empty();
						boolean init=false;
						if (inode.getAttributes().getLength()!=2) throw new IOException("Weird attributes in node element");
						Node nodeidattr= inode.getAttributes().item(0);
						if (!nodeidattr.getNodeName().equals("id")) throw new IOException("Not id attribute in node");
						node_id=nodeidattr.getNodeValue();
						Node nodetypeattr= inode.getAttributes().item(1);
						if (!nodetypeattr.getNodeName().equals("nodeType")) throw new IOException("Not type attribute in node");
						if (!nodetypeattr.getNodeValue().equals("state")) throw new IOException("Node type not state");
						
						NodeList nodeattributes=inode.getChildNodes();
						for (int nodeattr_indx=0;nodeattr_indx<nodeattributes.getLength();nodeattr_indx++) {
							Node nodeattr=nodeattributes.item(nodeattr_indx);
							if (nodeattr.getNodeType()==Node.ELEMENT_NODE){
								if (nodeattr.getAttributes().getLength()!=1) throw new IOException("Weird attributes in node attribute element");
								Node nodeattrnameattr= nodeattr.getAttributes().item(0);
								if (!nodeattrnameattr.getNodeName().equals("name")) throw new IOException("Node attribute element attribute not name");
								String nodeattrname=nodeattrnameattr.getNodeValue();
								if (nodeattrname.equals("name")) {
									node_name=nodeattr.getTextContent();
								} else if (nodeattrname.equals("type")) {
									NodeList nodetypelist=nodeattr.getChildNodes();
									for (int nodetype_indx=0;nodetype_indx<nodetypelist.getLength();nodetype_indx++) {
										Node nodetype=nodetypelist.item(nodetype_indx);
										if (nodetype.getNodeType()==Node.ELEMENT_NODE){
											if (nodetype.getAttributes().getLength()!=1) throw new IOException("Weird attributes in node type element.");
											Node typenameattr=nodetype.getAttributes().item(0);
											if (!typenameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in node type element");
											if (!typenameattr.getNodeValue().equals("initialState")) throw new IOException("Node type element name not initial state");
											init=true;
										}
									}
								} else if (nodeattrname.equals("invariant")) {
									NodeList nodeinvlist=nodeattr.getChildNodes();
									for (int inv_indx=0;inv_indx<nodeinvlist.getLength();inv_indx++) {
										Node nodeinv=nodeinvlist.item(inv_indx);
										if (nodeinv.getNodeType()==Node.ELEMENT_NODE){
											if (nodeinv.getAttributes().getLength()!=1) throw new IOException("Weird attributes in node invariant element.");
											Node invnameattr=nodeinv.getAttributes().item(0);
											if (!invnameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in node invariant element");
											if (!invnameattr.getNodeValue().equals("boolExpr")) throw new IOException("Node invariant "+invnameattr.getNodeValue()+" not yet supported.");
											node_inv=Optional.of(parseBoolExpr(nodeinv,result.getAllVars()));
										}
									}
								} else throw new IOException("Node attribute type "+nodeattrname+" not yet supported.");
							}
						}
						result.addLoc(node_id, node_name, node_inv);
						if (init) result.setInitLoc(node_name);
					} else if (iname.equals("arc")) {
						String arc_id;
						String arc_src;
						String arc_trg;
						Optional<String> arc_label=Optional.empty();
						List<String> arc_updates=new ArrayList<>();
						Optional<String> arc_guard=Optional.empty();
						
						Element arcelem=(Element) inode;
						if (inode.getAttributes().getLength()!=4) throw new IOException("Weird attributes in arc element");
						arc_id=arcelem.getAttribute("id");
						if (!arcelem.getAttribute("arcType").equals("transition")) throw new IOException("Arc type not transition");
						arc_src=arcelem.getAttribute("source");
						arc_trg=arcelem.getAttribute("target");
						
						NodeList arcattributes=inode.getChildNodes();
						for (int arcattr_indx=0;arcattr_indx<arcattributes.getLength();arcattr_indx++) {
							Node arcattr=arcattributes.item(arcattr_indx);
							if (arcattr.getNodeName().contains("comment")) {
								String comment=arcattr.getNodeValue();
								String[] spacesplit=comment.split(" ");
								String sync=spacesplit[2];
								arc_label=Optional.of(sync);
								result.addSync(sync);
							}
							if (arcattr.getNodeType()==Node.ELEMENT_NODE){
								String arcattrname=((Element) arcattr).getAttribute("name");								
								if (arcattrname.equals("label")) {
									String syncname=arcattr.getTextContent();
									if (!syncname.contains("nosync")) throw new IOException("Sync not yet supported.");
								} else if (arcattrname.equals("updates")) {
									NodeList updateelements=arcattr.getChildNodes();
									for (int update_indx=0;update_indx<updateelements.getLength();update_indx++) {
										Node updatenode=updateelements.item(update_indx);
										if (updatenode.getNodeType()==Node.ELEMENT_NODE){
											if (!((Element) updatenode).getAttribute("name").equals("update")) throw new IOException("Update element name not update");
											arc_updates.add(parseUpdate(updatenode));
										}
									}
								} else if (arcattrname.equals("guard")) {
									NodeList guards=arcattr.getChildNodes();
									for (int guard_indx=0;guard_indx<guards.getLength();guard_indx++) {
										Node guard=guards.item(guard_indx);
										if (guard.getNodeType()==Node.ELEMENT_NODE){
											if (!((Element) guard).getAttribute("name").equals("boolExpr")) throw new IOException("Guard not bool expr");
											arc_guard=Optional.of(parseBoolExpr(guard,result.clocks));
										}
									}
									
								} else throw new IOException("Arc attribute type "+arcattrname+" not yet supported.");
							}
						}
						result.addEdge(arc_src, arc_trg, arc_label, arc_updates, arc_guard);
					}  else throw new IOException("Node type "+iname+" not yet supported.");
					//System.out.println(inode.getTextContent());
				}
			}
			
			return result;
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	
	private static void printSys(List<XtaProcessStringRepresentation> results) {
		System.out.println();
		System.out.print("system ");
		int cntr=0;
		for (XtaProcessStringRepresentation x:results) {
			cntr++;
			System.out.print(x.name);
			if (cntr<results.size()) {
				System.out.print(", ");
			}
		}
		System.out.println(";");
	}

	private static void printVars(Set<String> clocks, Set<String> vars, Set<String> params, Set<String> validChans) {
		for (String p:params) {
			System.out.println("const int "+p+";");
		}
		System.out.println();
		
		for (String v: vars) {
			System.out.println("int "+v+";");
		}
		System.out.println();
		
		for (String c: clocks) {
			System.out.println("clock "+c+";");
		}
		System.out.println();
		
		for (String c:validChans) {
			System.out.println("chan "+c+";");
		}
		System.out.println();
	}

	private static void printXta(XtaProcessStringRepresentation result) {
		Map<String, Tuple2<String, Optional<String>>> locs=result.locs;
		Set<Tuple5<String, String, Optional<String>, List<String>, Optional<String>>> edges=result.edges;
		System.out.println("process "+result.getName()+"() {");
		/*for (String s:result.clocks) {
			System.out.println("\t"+"clock "+s+";");
		}
		System.out.println();*/
		System.out.println("\t"+"state");
		int cntr=0;
		for (String locid:locs.keySet()){
			cntr++;
			Tuple2<String, Optional<String>> loc=locs.get(locid);
			System.out.print("\t"+"\t"+loc.get1());
			
			if (loc.get2().isPresent()) 
				System.out.print(" { "+loc.get2().get()+" }");
			
			if (cntr<locs.size()) 
				System.out.println(",");
			else
				System.out.println(";");
		}
		System.out.println();
		System.out.println("\t"+"init "+result.getInitLocName()+";");
		System.out.println();
		System.out.println("\t"+"trans");
		cntr=0;
		for (Tuple5<String, String, Optional<String>, List<String>, Optional<String>> edge:edges) {
			cntr++;
			System.out.print("\t"+"\t"+locs.get(edge.get1()).get1()+" -> "+locs.get(edge.get2()).get1());
			
			boolean haslabel=edge.get3().isPresent() && result.chans.contains(edge.get3().get());
			boolean hasupdate=!edge.get4().isEmpty();
			boolean hasguard=edge.get5().isPresent();
			if (hasguard || haslabel || hasupdate) {
				System.out.print(" {");
				if (hasguard) {
					System.out.print(" guard "+ edge.get5().get()+";");
				}
				if (haslabel) {
					String type="?";
					if (hasguard) type="!";
					System.out.print(" sync "+ edge.get3().get()+type+";");
				}
				if (hasupdate) {
					System.out.print(" assign");
					int cntr2=0;
					for (String update:edge.get4()) {
						cntr2++;
						System.out.print(" "+update);
						if (cntr2<edge.get4().size())
							System.out.print(",");
						else
							System.out.print(";");
					}
				}
				
				System.out.print(" }");
			} else System.out.print(" { }");
			
			if (cntr<edges.size()) 
				System.out.println(",");
			else
				System.out.println(";");
		}
		System.out.println("}");
	}

	private static String parseUpdate(Node updatenode) throws IOException {
		Node varnode = null;
		Node exprnode = null;
		
		NodeList updateattribs=updatenode.getChildNodes();
		for (int i=0; i<updateattribs.getLength();i++) {
			Node updateattrib=updateattribs.item(i);
			if (updateattrib.getNodeType()==Node.ELEMENT_NODE){
				if (updateattrib.getAttributes().getLength()!=1) throw new IOException("Weird attributes in update.");
				String attrtype=((Element) updateattrib).getAttribute("name");
				if (attrtype.equals("name")) {
					varnode=updateattrib; 
				} else if (attrtype.equals("expr")) {
					exprnode=updateattrib;
				} else throw new IOException("Update attrib type "+ attrtype +" not yet supported.");
			}
		}
		
		if (varnode==null || exprnode==null) throw new IOException("Not enough attributes for update");
		String var=varnode.getTextContent();
		String expr=parseExpr(exprnode);
		return var+" = "+expr;
	}

	private static String parseBoolExpr(Node exprnode,Set<String> clocks) throws IOException {
		//at this point we have an element of type attribute 
		//with an attribute element name=boolExpr
		NodeList exprtypelist=exprnode.getChildNodes();
		for (int i=0; i<exprtypelist.getLength();i++) {
			Node exprtypenode=exprtypelist.item(i);
			if (exprtypenode.getNodeType()==Node.ELEMENT_NODE){
				if (exprtypenode.getAttributes().getLength()!=1) throw new IOException("Weird attributes in boolExpr type element.");
				Node typenameattr=exprtypenode.getAttributes().item(0);
				if (!typenameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in boolExpr type element");
				String exprType=typenameattr.getNodeValue();
				if (exprType.equals("greaterEqual")) {
					return parseGreaterEqual(exprtypenode,clocks);
				} else if (exprType.equals("greater")) {
					return parseGreater(exprtypenode,clocks);
				} else if (exprType.equals("and")) {
					return parseAnd(exprtypenode,clocks);
				} else if (exprType.equals("equal")) {
					return parseEqual(exprtypenode,clocks);
				} else if (exprType.equals("*")) {
					return parseMulExpr(exprtypenode);
				} else throw new IOException("Expression type "+exprType+" not yet supported.");
			}
		}
		return null;
	}

	private static String parseEqual(Node exprnode, Set<String> clocks) throws IOException {
		Node leftExprNode=null;
		Node rightExprNode=null;
		NodeList exprs=exprnode.getChildNodes();
		for (int i=0; i<exprs.getLength();i++) {
			Node exprtypenode=exprs.item(i);
			if (exprtypenode.getNodeType()==Node.ELEMENT_NODE){
				if (exprtypenode.getAttributes().getLength()!=1) throw new IOException("Weird attributes in expr element.");
				Node exprnameattr=exprtypenode.getAttributes().item(0);
				if (!exprnameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in expr element");
				if (!exprnameattr.getNodeValue().equals("expr")) throw new IOException("Name attribute not expr in element");
				if (leftExprNode==null) leftExprNode=exprtypenode;
				else if (rightExprNode==null) rightExprNode=exprtypenode;
				else throw new IOException("Too many subexprs");
			}
		}
		if (leftExprNode==null || rightExprNode==null) throw new IOException("Too few subexprs");
		String leftexpr=parseExpr(leftExprNode);
		String rightexpr=parseExpr(rightExprNode);
		
		if (clocks.contains(rightexpr)) {
			return rightexpr+" == "+leftexpr;
		} else return leftexpr+" == "+rightexpr;
	}

	private static String parseAnd(Node exprnode, Set<String> clocks) throws IOException {
		String leftExpr=null;
		String rightExpr=null;
		NodeList exprs=exprnode.getChildNodes();
		for (int i=0; i<exprs.getLength();i++) {
			Node exprtypenode=exprs.item(i);
			if (exprtypenode.getNodeType()==Node.ELEMENT_NODE){
				if (exprtypenode.getAttributes().getLength()!=1) throw new IOException("Weird attributes in and element.");
				String exprType=((Element) exprtypenode).getAttribute("name");
				String expr;
				if (exprType.equals("greaterEqual")) {
					expr=parseGreaterEqual(exprtypenode,clocks);
				} else if (exprType.equals("greater")) {
					expr=parseGreater(exprtypenode,clocks);
				} else if (exprType.equals("equal")) {
					expr=parseEqual(exprtypenode,clocks);
				} else if (exprType.equals("and")) {
					expr=parseAnd(exprtypenode,clocks);
				} else if (exprType.equals("*")) {
					expr=parseMulExpr(exprtypenode);
				} else throw new IOException("Expression type "+exprType+" not yet supported.");
			
				if (leftExpr==null) leftExpr=expr;
				else if (rightExpr==null) rightExpr=expr;
				else throw new IOException("Too many subexprs");
			}
		}
		if (leftExpr==null || rightExpr==null) throw new IOException("Too few subexprs");
		return leftExpr+" && "+rightExpr;
	}

	private static String parseGreater(Node exprnode,Set<String> clocks) throws IOException {
		Node leftExprNode=null;
		Node rightExprNode=null;
		NodeList exprs=exprnode.getChildNodes();
		for (int i=0; i<exprs.getLength();i++) {
			Node exprtypenode=exprs.item(i);
			if (exprtypenode.getNodeType()==Node.ELEMENT_NODE){
				if (exprtypenode.getAttributes().getLength()!=1) throw new IOException("Weird attributes in expr element.");
				Node exprnameattr=exprtypenode.getAttributes().item(0);
				if (!exprnameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in expr element");
				if (!exprnameattr.getNodeValue().equals("expr")) throw new IOException("Name attribute not expr in element");
				if (leftExprNode==null) leftExprNode=exprtypenode;
				else if (rightExprNode==null) rightExprNode=exprtypenode;
				else throw new IOException("Too many subexprs");
			}
		}
		if (leftExprNode==null || rightExprNode==null) throw new IOException("Too few subexprs");
		String leftexpr=parseExpr(leftExprNode);
		String rightexpr=parseExpr(rightExprNode);
		if (clocks.contains(leftexpr)){
			return leftexpr+" > "+rightexpr;
		} else if (clocks.contains(rightexpr)) {
			return rightexpr+" < "+leftexpr;
		} else return leftexpr+" > "+rightexpr;
			//throw new IOException("Expr type not yet supported "+leftexpr+" ? "+rightexpr);
	}

	private static String parseGreaterEqual(Node exprnode,Set<String> clocks) throws IOException {
		Node leftExprNode=null;
		Node rightExprNode=null;
		NodeList exprs=exprnode.getChildNodes();
		for (int i=0; i<exprs.getLength();i++) {
			Node exprtypenode=exprs.item(i);
			if (exprtypenode.getNodeType()==Node.ELEMENT_NODE){
				if (exprtypenode.getAttributes().getLength()!=1) throw new IOException("Weird attributes in expr element.");
				Node exprnameattr=exprtypenode.getAttributes().item(0);
				if (!exprnameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in expr element");
				if (!exprnameattr.getNodeValue().equals("expr")) throw new IOException("Name attribute not expr in element");
				if (leftExprNode==null) leftExprNode=exprtypenode;
				else if (rightExprNode==null) rightExprNode=exprtypenode;
				else throw new IOException("Too many subexprs");
			}
		}
		if (leftExprNode==null || rightExprNode==null) throw new IOException("Too few subexprs");
		String leftexpr=parseExpr(leftExprNode);
		String rightexpr=parseExpr(rightExprNode);
		if (clocks.contains(leftexpr)){
			return leftexpr+" >= "+rightexpr;
		} else //if (clocks.contains(rightexpr)) {
			return rightexpr+" <= "+leftexpr;
		//} else throw new IOException("Expr type not yet supported "+leftexpr+" ? "+rightexpr);
	}

	private static String parseExpr(Node exprnode) throws IOException {
		NodeList exprlist=exprnode.getChildNodes();
		for (int i=0; i<exprlist.getLength();i++) {
			Node exprtypenode=exprlist.item(i);
			if (exprtypenode.getNodeType()==Node.ELEMENT_NODE){
				if (exprtypenode.getAttributes().getLength()!=1) throw new IOException("Weird attributes in expr element.");
				Node exprnameattr=exprtypenode.getAttributes().item(0);
				if (!exprnameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in expr element");
				String exprtype=exprnameattr.getNodeValue();
				if (exprtype.equals("const")) {
					return exprtypenode.getTextContent();
				} else if (exprtype.equals("name")) {
					return exprtypenode.getTextContent();
				} else if (exprtype.equals("*")) {
					return parseMulExpr(exprtypenode);
				} else if (exprtype.equals("+")) {
					return parseAddExpr(exprtypenode);
				}else  throw new IOException("Expression type "+exprtype+" not yet supported.");
			}
		}
		return null;
	}

	private static String parseAddExpr(Node exprnode) throws IOException {
		String leftExpr=null;
		String rightExpr=null;
		NodeList exprs=exprnode.getChildNodes();
		for (int i=0; i<exprs.getLength();i++) {
			Node exprtypenode=exprs.item(i);
			if (exprtypenode.getNodeType()==Node.ELEMENT_NODE){
				if (exprtypenode.getAttributes().getLength()!=1) throw new IOException("Weird attributes in expr element.");
				Node exprnameattr=exprtypenode.getAttributes().item(0);
				if (!exprnameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in expr element");
				String exprtype=exprnameattr.getNodeValue();
				String expr;
				if (exprtype.equals("const")) {
					expr= exprtypenode.getTextContent();
				} else if (exprtype.equals("name")) {
					expr= exprtypenode.getTextContent();
				} else if (exprtype.equals("*")) {
					expr= parseMulExpr(exprtypenode);
				} else  throw new IOException("Expression type "+exprtype+" not yet supported.");
			
				if (leftExpr==null) leftExpr=expr;
				else if (rightExpr==null) rightExpr=expr;
				else throw new IOException("Too many subexprs");
			}
		}
		if (leftExpr==null || rightExpr==null) throw new IOException("Too few subexprs");
		return leftExpr+" + "+rightExpr;
	}

	private static String parseMulExpr(Node exprnode) throws IOException {
		String leftExpr=null;
		String rightExpr=null;
		NodeList exprs=exprnode.getChildNodes();
		for (int i=0; i<exprs.getLength();i++) {
			Node exprtypenode=exprs.item(i);
			if (exprtypenode.getNodeType()==Node.ELEMENT_NODE){
				if (exprtypenode.getAttributes().getLength()!=1) throw new IOException("Weird attributes in expr element.");
				Node exprnameattr=exprtypenode.getAttributes().item(0);
				if (!exprnameattr.getNodeName().equals("name")) throw new IOException("Not name attribute in expr element");
				String exprtype=exprnameattr.getNodeValue();
				String expr;
				if (exprtype.equals("const")) {
					expr= exprtypenode.getTextContent();
				} else if (exprtype.equals("name")) {
					expr= exprtypenode.getTextContent();
				} else if (exprtype.equals("*")) {
					expr= parseMulExpr(exprtypenode);
				} else  throw new IOException("Expression type "+exprtype+" not yet supported.");
			
				if (leftExpr==null) leftExpr=expr;
				else if (rightExpr==null) rightExpr=expr;
				else throw new IOException("Too many subexprs");
			}
		}
		if (leftExpr==null || rightExpr==null) throw new IOException("Too few subexprs");
		return leftExpr+" * "+rightExpr;
	}

	public static void transform(List<String> inputs) {
		List<XtaProcessStringRepresentation> results=new ArrayList<>();
		for (String proc:inputs) {
			XtaProcessStringRepresentation result=read(proc);
			results.add(result);
		}
		Set<String> validChans=handleChans(results);
		printVars(results.get(0).clocks,results.get(0).discVars,results.get(0).params,validChans);
		for (XtaProcessStringRepresentation result:results) {
			printXta(result);
		}
		printSys(results);
	}

	private static Set<String> handleChans(List<XtaProcessStringRepresentation> procs) {
		Set<String> result=new HashSet<>();
		Map<String,Integer> chanCnt=new HashMap<>();
		//count occurences of channels
		for (XtaProcessStringRepresentation proc:procs) {
			Set<String> chans=proc.getChans();
			for (String chan:chans) {
				int cnt=chanCnt.getOrDefault(chan, 0)+1;
				chanCnt.put(chan, cnt);
			}
		}
		//only keep channels of at least two occurences
		for (XtaProcessStringRepresentation proc:procs) {
			Set<String> chans=proc.getChans();
			Set<String> chansToRemove=new HashSet<>();
			for (String chan:chans) {
				int cnt=chanCnt.get(chan);
				if (cnt<2) chansToRemove.add(chan);
			}
			chans.removeAll(chansToRemove);
			result.addAll(chans);
		}
		return result;
		
	}
}
