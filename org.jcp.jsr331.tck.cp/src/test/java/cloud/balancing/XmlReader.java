package cloud.balancing;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlReader {


	public static CloudComputer[] readComputers(String fileName) {

		try {

			File xmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = dbFactory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			System.out.println("Read Cloud Computers from <" + fileName + "> (root "
					+ doc.getDocumentElement().getNodeName() + ")");
			NodeList nodeList = doc.getElementsByTagName("CloudComputer");
			System.out.println("-----------------------");
			CloudComputer[] computers = new CloudComputer[nodeList.getLength()];
			for (int i = 0; i < computers.length; i++) {
				computers[i] = new CloudComputer();
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					computers[i].setId(getTagValue("id", element));
					computers[i].setCpuPower(getIntValue("cpuPower", element));
					computers[i].setMemory(getIntValue("memory", element));
					computers[i].setNetworkBandwidth(getIntValue(
							"networkBandwidth", element));
					computers[i].setCost(getIntValue("cost", element));
//					System.out.println(computers[i]);
				}
			}
			return computers;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static CloudProcess[] readProcesses(String fileName) {

		try {

			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			System.out.println("Read Cloud Processes from <" + fileName + "> (root "
					+ doc.getDocumentElement().getNodeName() + ")");
			NodeList nList = doc.getElementsByTagName("CloudProcess");
			System.out.println("-----------------------");
			CloudProcess[] processes = new CloudProcess[nList.getLength()];
			for (int i = 0; i < processes.length; i++) {
				processes[i] = new CloudProcess();
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					processes[i].setId(getTagValue("id", eElement));
					processes[i].setRequiredCpuPower(getIntValue("requiredCpuPower", eElement));
					processes[i].setRequiredMemory(getIntValue("requiredMemory", eElement));
					processes[i].setRequiredNetworkBandwidth(getIntValue(
							"requiredNetworkBandwidth", eElement));
//					System.out.println(processes[i]);
				}
			}
			return processes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getTagValue(String tag, Element element) {
		NodeList nlList = element.getElementsByTagName(tag).item(0)
				.getChildNodes();

		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}

	private static int getIntValue(String tag, Element element) {

		return Integer.parseInt(getTagValue(tag, element));
	}

	public static void main(String argv[]) {
		XmlReader.readComputers("./data/cb-0800comp-2400proc.xml");
		XmlReader.readProcesses("./data/cb-0800comp-2400proc.xml");
	}

}
