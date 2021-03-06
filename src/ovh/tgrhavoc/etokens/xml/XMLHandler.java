package ovh.tgrhavoc.etokens.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ovh.tgrhavoc.etokens.eTokens;

public class XMLHandler {
	eTokens plugin;
	File xmlFile;
	
	List<Token> tokens = new ArrayList<Token>();

	public XMLHandler(eTokens main) {
		plugin = main;
		xmlFile = new File(main.getDataFolder(), "configuration.xml");
		if (!xmlFile.exists())
			createDefaultXML();

		readXML();
	}

	private void createDefaultXML() {
		try {
			xmlFile.createNewFile();
			saveToXML(xmlFile.getPath()); // Default XML Config
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readXML() {
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentFactory
					.newDocumentBuilder();
			Document doc = documentBuilder.parse(xmlFile);

			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("token");

			System.out.println("Root element :"
					+ doc.getDocumentElement().getNodeName());

			for (int temp = 0; temp < nodeList.getLength(); temp++) {
				Node node = nodeList.item(temp);
				Token token = new Token();
				
				if (node.hasAttributes()) {
					for (int i = 0; i < node.getAttributes().getLength(); i++) {
						Node atrr = node.getAttributes().item(i);
						
						if (atrr.getNodeName().equalsIgnoreCase("token-amount")) {
							token.setTokenAmount(Integer.parseInt(atrr.getNodeValue()));
						}
						
						if (atrr.getNodeName().equalsIgnoreCase("money")) {
							Bukkit.broadcastMessage("Found money atrr: " + atrr.getNodeValue());
							if (plugin.canUseVault()){
								token.setEcoAmount(Double.parseDouble(atrr.getNodeValue()));
							}else{
								Bukkit.getLogger().severe("[eTokens]: It seems that you're trying to use economy money when you don't have vault installed!\n"
										+ "Please install vault then reload this plugin!");
							}
						}
						if (atrr.getNodeName().equalsIgnoreCase("use-tokens")) {
							Bukkit.broadcastMessage("Found use-tokens atrr");
							token.setUseToken(Boolean.parseBoolean(atrr.getNodeValue()));
						}
						
						
						if (atrr.getNodeName().equalsIgnoreCase("block-amount")) {
							token.setBlockAmount(Integer.parseInt(atrr.getNodeValue()));
						}
						if (atrr.getNodeName().equalsIgnoreCase("block-type")) {
							token.setBlockType(atrr.getNodeValue());
						}
						if (atrr.getNodeName().equalsIgnoreCase("objective")) {
							token.setObjective(atrr.getNodeValue());
						}
						if (atrr.getNodeName().equalsIgnoreCase("distance")) {
							token.setDistance(Integer.parseInt(atrr.getNodeValue()));
						}
						if (atrr.getNodeName().equalsIgnoreCase("achievement")) {
							token.setAchievement(atrr.getNodeValue());
						}
						if (atrr.getNodeName().equalsIgnoreCase("kills")){
							token.setKills(Integer.parseInt(atrr.getNodeValue()));
						}
						if (atrr.getNodeName().equalsIgnoreCase("entity-type")){
							token.setEntityType(atrr.getNodeValue());
						}
						if (atrr.getNodeName().equalsIgnoreCase("repeatable")){
							token.setRepeatable(Boolean.valueOf(atrr.getNodeValue()));
						}
					}
				}
				token.setMessage(node.getTextContent());
				tokens.add(token);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveToXML(String xml) {
		Document dom;
		Element e = null;
		Attr attr = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();
			Element rootEle = dom.createElement("tokens");

			// Default diamond ore break for token
			e = dom.createElement("token");
			e.appendChild(dom
					.createTextNode("Well done {PLAYER}, have received {TOKEN-AMOUNT} token(s) for breaking {BLOCK-AMOUNT} {BLOCK-TYPE} block(s)!"));
			attr = dom.createAttribute("token-amount");
			attr.setValue("10");
			e.setAttributeNode(attr);

			attr = dom.createAttribute("objective");
			attr.setValue("block-break");
			e.setAttributeNode(attr);

			attr = dom.createAttribute("block-type");
			attr.setValue("diamond-ore");
			e.setAttributeNode(attr);

			attr = dom.createAttribute("block-amount");
			attr.setValue("10");
			e.setAttributeNode(attr);

			rootEle.appendChild(e);

			// Walk 10,000 blocks for token
			e = dom.createElement("token");
			e.appendChild(dom
					.createTextNode("You have received {TOKEN-AMOUNT} tokens for walking {DISTANCE} block(s)!"));
			attr = dom.createAttribute("token-amount");
			attr.setValue("1");
			e.setAttributeNode(attr);

			attr = dom.createAttribute("objective");
			attr.setValue("walk");
			e.setAttributeNode(attr);

			attr = dom.createAttribute("distance");
			attr.setValue("10000");
			e.setAttributeNode(attr);

			rootEle.appendChild(e);

			// Get token for achievement
			e = dom.createElement("token");
			e.appendChild(dom
					.createTextNode("You have received {TOKEN-AMOUNT} tokens for getting the achievement {ACHIEVEMENT}!"));
			attr = dom.createAttribute("token-amount");
			attr.setValue("10");
			e.setAttributeNode(attr);

			attr = dom.createAttribute("objective");
			attr.setValue("achivement");
			e.setAttributeNode(attr);

			attr = dom.createAttribute("achievement");
			attr.setValue(Achievement.BAKE_CAKE.name());
			e.setAttributeNode(attr);

			rootEle.appendChild(e);
			
			//Get token for killing 100 zombies (Repeatable)
			e = dom.createElement("token");
			e.appendChild(dom
					.createTextNode("You have received {TOKEN-AMOUNT} tokens for killing {KILLS} {ENTITY-TYPE}!"));
			attr = dom.createAttribute("token-amount");
			attr.setValue("10");
			e.setAttributeNode(attr);

			attr = dom.createAttribute("objective");
			attr.setValue("kill");
			e.setAttributeNode(attr);

			attr = dom.createAttribute("kills");
			attr.setValue("100");
			e.setAttributeNode(attr);
			
			attr = dom.createAttribute("entity-type");
			attr.setValue(EntityType.ZOMBIE.name());
			e.setAttributeNode(attr);
			
			attr = dom.createAttribute("repeatable");
			attr.setValue("true");
			e.setAttributeNode(attr);

			rootEle.appendChild(e);

			dom.appendChild(rootEle);
			try {
				Transformer tr = TransformerFactory.newInstance()
						.newTransformer();
				FileOutputStream fos = new FileOutputStream(xml);
				tr.setOutputProperty(OutputKeys.INDENT, "yes");
				tr.setOutputProperty(OutputKeys.METHOD, "xml");
				tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				tr.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "4");
				tr.transform(new DOMSource(dom), new StreamResult(fos));
				fos.close();
			} catch (TransformerException te) {
				System.out.println(te.getMessage());
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public List<Token> getTokens(){
		return tokens;
	}
}
