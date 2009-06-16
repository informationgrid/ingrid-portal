package de.ingrid.mdek.mapping;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import de.ingrid.utils.xml.XMLUtils;

public class ScriptImportDataMapper implements ImportDataMapper {
	
	final protected static Log log = LogFactory.getLog(ScriptImportDataMapper.class);
	
	private ScriptEngine engine = null;

	// Injected by Spring
	private InputStream mapperScript;
	
	// Injected by Spring
	private InputStream template;
	
	public InputStream convert(InputStream data) {
		Map<String, Object> parameters = new Hashtable<String, Object>();
		InputStream targetStream = null;
		
		try {
			// get DOM-tree from XML-file
			Document doc = getDomFromSourceData(data);
			
			// get DOM-tree from template-file
			Document docTarget = getDomFromSourceData(template);
			
					//*****************
					/*
					XPath xpath = XPathFactory.newInstance().newXPath();
					Node node = null;
					try {
						node = (Node) xpath.evaluate("//igc/data-sources/data-source/general/title", doc, XPathConstants.NODE);
					} catch (XPathExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					//*****************
			
		    parameters.put("source", doc);
		    parameters.put("target", docTarget);
			doMap(parameters);
	
			String targetString = toString(docTarget);
			if (log.isDebugEnabled()) {
				log.debug("Resulting XML:" + targetString);
			}
			targetStream = new ByteArrayInputStream(targetString.getBytes("UTF-8"));
		} catch (TransformerException e) {
			log.error("Error while transforming Document to String!");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			log.error("Error while transforming Document to String!");
			e.printStackTrace();
		}
		
		return targetStream;
	}
	
	private void doMap(Map<String, Object> parameters) {
        try {
	        ScriptEngine engine = this.getScriptEngine();
			
	        // pass all parameters
	        for(String param : parameters.keySet())
	        	engine.put(param, parameters.get(param));
	        engine.put("log", log);
	
			// execute the mapping
	    	log.debug("Mapping with script: " + mapperScript);
	        engine.eval(new InputStreamReader(mapperScript));
	        
		} catch (ScriptException e) {
			log.error("Error while evaluating the script!");
			e.printStackTrace();
		}

	}

	private Document getDomFromSourceData(InputStream data) {
		Document doc = null;
		try { 
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			//dbf.setNamespaceAware(true);
			//dbf.setValidating(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
	
			doc = db.parse(data);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * Get the script engine (JavaScript). It returns always the same instance once initialized.
	 * 
	 * @return script engine.
	 */
	protected ScriptEngine getScriptEngine()  {
		if (engine == null) {
			ScriptEngineManager manager = new ScriptEngineManager();
	        engine = manager.getEngineByName("JavaScript");
		}
		return engine;
	}
	
	public void setMapperScript(InputStream script) {
		this.mapperScript = script;
	}
	
	public void setTemplate(InputStream tpl) {
		this.template = tpl;
	}
	
	 public static String toString(Document document) throws TransformerException { 
	        StringWriter stringWriter = new StringWriter(); 
	        StreamResult streamResult = new StreamResult(stringWriter); 
	        TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
	        Transformer transformer = transformerFactory.newTransformer(); 
	        //transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
	        //transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); 
	        //transformer.setOutputProperty(OutputKeys.METHOD, "xml"); 
	        transformer.transform(new DOMSource(document.getDocumentElement()), streamResult); 
	        return stringWriter.toString(); 
	     } 

}
