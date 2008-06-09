package de.ingrid.mdek.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

public class HelpServlet extends HttpServlet {

	private final static Logger log = Logger.getLogger(HelpServlet.class);	

	// base filename for the xml helpfile. Additional chars are added depending on the request locale before '.xml'
	// e.g. if the base filename is 'mdek-application-help.xml' and the locale is 'en', the resulting String will be:
	// mdek-application-help_en.xml
	private String baseHelpFileName = null;
	private String xslFileName = null;


	public void init() throws ServletException {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("help");
		baseHelpFileName = "/WEB-INF/classes/"+resourceBundle.getString("help.baseFileName");
		xslFileName = "/WEB-INF/classes/"+resourceBundle.getString("help.xslFileName");
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		HttpSession session = request.getSession();
		Enumeration<String> enumeration = request.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String parameterName = enumeration.nextElement();
			String parameter = (String) request.getParameter(parameterName);
			log.debug("parameter name: "+parameterName);
			log.debug("parameter value: "+parameter);
		}

		String fileName = baseHelpFileName.substring(0, baseHelpFileName.lastIndexOf(".xml"))
						+ "_" + request.getLocale().getLanguage() + ".xml";

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF8");
		
		PrintWriter out = null;		
		try {
			out = response.getWriter();

		} catch (IOException e) {
			log.error("Could not open PrintWriter.", e);
			throw new ServletException(e);
		}

        // read help resource
        SAXReader xmlReader = new SAXReader();
        Document doc = null;
        try {
            doc = xmlReader.read(getServletContext().getResourceAsStream(fileName));

        } catch (Exception e) {
//            log.error("Error reading help source file!", e);
        }

        // If the doc could not be found, use the base help filename
        if (doc == null) {
            try {
                doc = xmlReader.read(getServletContext().getResourceAsStream(baseHelpFileName));

            } catch (Exception e) {
                log.error("Error reading help source file!", e);
                throw new ServletException(e);
            }
        }

        // get the help chapter
        String helpKey = request.getParameter("hkey");
        if (helpKey == null) {
            helpKey = "index";
        }
        Object chapterObj = doc.selectSingleNode( "//section[@help-key='" + helpKey + "']/ancestor::chapter" );

        // transform the xml content to valid html using xslt
        if (chapterObj == null) {
//        	context.put("help_content", "<p>help key (" + helpKey + ") not found!</p>");
        	out.print("<p>help key (" + helpKey + ") not found!</p>");

        } else {
            TransformerFactory factory;
            try {
                factory = TransformerFactory.newInstance();
            } catch (Throwable t) {
                System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
                factory = TransformerFactory.newInstance();
            }

            try {
                StreamSource stylesheet = new StreamSource(getServletContext().getResourceAsStream(xslFileName));
                Transformer transformer = factory.newTransformer(stylesheet);
                DocumentResult result = new DocumentResult();
                String subtree = ((DefaultElement)chapterObj).asXML();
                DocumentSource source = new DocumentSource( DocumentHelper.parseText(subtree) );
                
                transformer.transform(source, result);
                String helpContent = result.getDocument().asXML();
//                context.put("help_content", helpContent);
                out.print(helpContent);

            } catch (Exception e) {
                log.error("Error processing help entry!", e);
            }
        }
	}
}
