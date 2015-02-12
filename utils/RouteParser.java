package utils;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RouteParser {
	
	public static String getFirstCharacterDataFromElements(NodeList nodes) {
		  
		  //Parse the value in the node	  
		  if (nodes.getLength() > 0){			  
			  
			  return nodes.item(0).getTextContent();
			    
		  }else{
			  
			  return "";
		  }
	}
	
	public static String getNamedCharacterDataFromElements(NodeList nodes, String tag) {
		  
		  //Parse the value in the node	  

		if (nodes.getLength() > 0){
			
			Element node = (Element) nodes.item(0);
			NodeList children = node.getElementsByTagName(tag);
			
			return getFirstCharacterDataFromElements(children);
			
		}
		
		
			  
		return "";
		  
	}
	
	public static String getNamedCharacterDataFromElements(NodeList grandparents, String tag1, String tag2) {
		  
		  //Parse the value in the node	  
		if (grandparents.getLength() > 0){
			
			Element grandparent = (Element) grandparents.item(0);
			NodeList parents = grandparent.getElementsByTagName(tag1);
			
			for (int i=0;i < parents.getLength();i++){			  
				  
				Element parent = (Element) parents.item(i);			    	
		    	NodeList children = parent.getElementsByTagName(tag2);
		    	if (children.getLength() > 0){
					return getFirstCharacterDataFromElements(children);
		    	}
			}			
		}
			  
		return "";
		  
	}
	
	public static String getCharacterDataFromElement(Node node) {
		  
		  //Parse the value in the node
		  Element e = (Element) node;
		  Node child = e.getFirstChild();
		  
		    if (child instanceof CharacterData) {
		       CharacterData cd = (CharacterData) child;
		       return cd.getData().replaceAll("\\s","");
		    }
		    else{return "";}

	}

}
