import java.net.*;
import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WebsitePath {
    public static void main(String[] args) throws Exception {
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Enter Your URL :");
    	String getPath = sc.nextLine();
    	String websitePath = null;
    	if(getPath.startsWith("www.") || getPath.startsWith("http") && getPath.endsWith(".com/")) {
    		websitePath = getPath+"sitemap.xml";
    	}else if (getPath.startsWith("www.") || getPath.startsWith("http") && getPath.endsWith(".com")) {
    		websitePath = getPath+"/sitemap.xml";
		}else {
			System.out.println("Enter Correct Syn ");
		}
		String xmlFilePath = websitePath;
		String outPutPath = "/home/software/Desktop/GlenwoodsTask/"+get+".txt";
		
		StringBuilder AllUrl = new StringBuilder();
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFilePath);
			doc.getDocumentElement().normalize();

			// Get All Account Node As a list from DBFArm.xml
			NodeList urlList = doc.getElementsByTagName("url");
			System.out.println("Total " + urlList.getLength() + " URL's in Given JavaTPointSitemap.xml From FilePath = "
					+ xmlFilePath + "\n");
			for (int i = 0; i < urlList.getLength(); i++) {
				Node urlNode = urlList.item(i);
				if (urlNode.getNodeType() == Node.ELEMENT_NODE) {
					Element urlElement = (Element) urlNode;
					String url = urlElement.getElementsByTagName("loc").item(0).getTextContent();
					AllUrl.append(url+"\n");
				}
			}
			FileWriter onlyUrl = new FileWriter(outPutPath);
			onlyUrl.write(AllUrl.toString());
			onlyUrl.close();
			System.out.print("onlyUrlJavaTpoint.txt Created Successfully in FilePath = " + outPutPath);

    }catch (IOException e) {
        e.printStackTrace();
    }
    }
}

