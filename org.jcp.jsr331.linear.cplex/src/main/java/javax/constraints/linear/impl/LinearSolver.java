package javax.constraints.linear.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.constraints.Objective;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LinearSolver extends javax.constraints.linear.LinearSolver {

	static public final String JSR331_LINEAR_SOLVER_VERSION = "CPLEX";

	public LinearSolver() {
	}
	
	@Override
	public String getOutputFilename() {
		//return OUTPUT_FOLDER + "/" + getProblem().getName() + ".xml";
	    return uniqueName(".xml");
	}
	
	@Override
	protected String preProcess() {
		// create Cplex cmd-file
		//String name = OUTPUT_FOLDER + "/" + getProblem().getName() + ".cmd";
	    String name = uniqueName(".cmd");
		try {
			PrintStream out = new PrintStream(name);
//			System.out.println("Cplex commands:");
			out.println("read " + getInputFilename());
			out.println("optimize");
			out.println("write " + getOutputFilename() + " SOL");
			out.println("quit");
//			System.out.println("read " + getInputFilename());
//			System.out.println("optimize");
//			System.out.println("write " + getOutputFilename() + " SOL");
//			System.out.println("quit");
			out.close();
		} catch (Exception e) {
			System.out.println("Cannot create command file " + name);
		}
		
		File oldFile = new File(getOutputFilename());
		oldFile.delete();
		
		return name;
	}

	public String getCommanLine() {
		String exe = System.getProperty(LP_SOLVER_EXE);
		if (exe == null) {
			exe = "cplex";
		}
		String options = System.getProperty(LP_SOLVER_OPTIONS);
		if (options == null) {
			options = "-maximize -dualsimplex";
		}
		String name = getProblem().getName();
		return exe;
	}

	public String getVersion() {
		return JSR331_LINEAR_SOLVER_VERSION;
	}

	/**
	 * GLPK minimizes by default
	 */
	public Objective getDefaultOptimizationObjective() {
		return Objective.MINIMIZE;
	}
	
	public HashMap<String, String> readResults() {
		
		String fileName = getOutputFilename();
		try {
			System.out.println("Read CPLEX results from file: " + fileName);
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			System.out.println("Read variables from <" + fileName + "> (root "
					+ doc.getDocumentElement().getNodeName() + ")");
			Node nodeVariables = doc.getElementsByTagName("variables").item(0);
			NodeList nList = nodeVariables.getChildNodes();
//			System.out.println("-----------------------");
			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
			HashMap<String, String> results = new HashMap<String, String>();
			for (int i = 0; i < nList.getLength(); i++) {
				Node varNode = nList.item(i);
				if ("variable".equals(varNode.getNodeName())) {
					XmlVar xmlVar = new XmlVar();
					Element eElement = (Element) varNode;
					String name = getTagValue("name", eElement);
					String value = getTagValue("value", eElement);
					xmlVar.setName(name);
					xmlVar.setIndex(getIntValue("index", eElement));
					xmlVar.setValue(getDoubleValue("value", eElement));
//					System.out.println(xmlVar);
					results.put(name,value);
				}
			}
			return results;
		}
		catch (IOException e) {
			log("I/O Error reading " + fileName);
			return null;
		}
		catch (SAXException e) {
			log("Error parsing file " + fileName);
			return null;
		}
		catch (IllegalArgumentException e) {
			log("Cannot find file " + fileName);
			return null;
		}
		catch (Exception e) {
			String msg = "*** Infeasible problem.";
			log(msg);
			return null;
		}
	}

	/*
	 * Reads an output file as xml and produces an array that is parallel to the array
	 * of all variables
	 * 
	 */
//	public int[] readResultValues() {
//		try {
//			String fileName = getOutputFilename();
//			File fXmlFile = new File(fileName);
//			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
//					.newInstance();
//			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//			Document doc = dBuilder.parse(fXmlFile);
//			doc.getDocumentElement().normalize();
//
//			System.out.println("Read variable from <" + fileName + "> (root "
//					+ doc.getDocumentElement().getNodeName() + ")");
//			Node nodeVariables = doc.getElementsByTagName("variables").item(0);
//			NodeList nList = nodeVariables.getChildNodes();
//			System.out.println("-----------------------");
//			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
//			int[] values = new int[problem.getVars().length];
//			int n = 0;
//			for (int i = 0; i < nList.getLength(); i++) {
//				Node varNode = nList.item(i);
//				//if (varNode.getNodeType() == Node.ELEMENT_NODE) {
//				if ("variable".equals(varNode.getNodeName())) {
//					XmlVar xmlVar = new XmlVar();
//					Element eElement = (Element) varNode;
//					xmlVar.setName(getTagValue("name", eElement));
//					xmlVar.setIndex(getIntValue("index", eElement));
//					xmlVar.setValue((int)getDoubleValue("value", eElement));
//					System.out.println(xmlVar);
//					values[n] = xmlVar.getValue();
//					n++;
//				}
//			}
//			
//			return values;
//		} catch (Exception e) {
//			String msg = "Infeasible problem.";
//			log(msg);
//			return null;
//		}
//	}
	
	public static String getTagValue(String tag, Element element) {
		Attr attr = element.getAttributeNode(tag);
		return attr.getValue();
	}

	public static int getIntValue(String tag, Element element) {

		return Integer.parseInt(getTagValue(tag, element));
	}
	
	public static double getDoubleValue(String tag, Element element) {

		return Double.parseDouble(getTagValue(tag, element));
	}

}
