package breakpoints;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BreakpointsDeserializer {
	public static List<IBreakpoint> deserialize(String bpFilePath) {
		List<IBreakpoint> result = new ArrayList<IBreakpoint>();
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(new FileInputStream(bpFilePath));
			
			Element root = document.getDocumentElement();
			NodeList breakpointNodes = root.getElementsByTagName("breakpoint");
			BreakpointFactory bpFactory = new BreakpointFactory();
			
			for (int i = 0; i < breakpointNodes.getLength(); i++) {
				Element breakpointElement = (Element) breakpointNodes.item(i);
				String type = breakpointElement.getAttribute("type");
				result.add(bpFactory.getBreakpoint(type, breakpointElement));
			}
		} catch(Exception e) {
			System.err.println(("Unable to load breakpoints from file (" + bpFilePath + ")\n" + e.getMessage()));
			return new ArrayList<IBreakpoint>(); 
		}
		
		return result;
	}
}
