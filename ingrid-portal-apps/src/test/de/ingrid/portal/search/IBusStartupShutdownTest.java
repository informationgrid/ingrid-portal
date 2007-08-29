package de.ingrid.portal.search;

import junit.framework.TestCase;

public class IBusStartupShutdownTest extends TestCase {

    protected void setUp() throws Exception {

        super.setUp();
    }
/*
    public void testStartupShutdown() {
         String iBusUrl = "wetag:///torwald-ibus:ibus-torwald";
         
         try {

         // set it up
         String jxtaConfigFilename = getResourceAsStream("/jxta.conf.xml");
         
         PeerService communication = (PeerService) startJxtaCommunication(jxtaConfigFilename);
         communication.subscribeGroup(iBusUrl);

         ProxyService proxy = new ProxyService();
         
         proxy.setCommunication(((ICommunication)communication));
         proxy.startup();
         
         RemoteInvocationController ric = proxy.createRemoteInvocationController(iBusUrl);

         Bus bus = (Bus) ric.invoke(Bus.class, Bus.class.getMethod("getInstance", null), null);
         
         assertNotNull(bus);
         
         // shut it down
         
         proxy.setCommunication(null);
         proxy.shutdown();
         
         proxy = null;
         
         communication.unsubscribeGroup(iBusUrl);
         communication.shutdown();
         
         communication = null;

         
         } catch (Throwable e) {
         assertNotNull(null);
         e.printStackTrace();
         }
    }

    private static String getResourceAsStream(String resource) throws Exception {
        String stripped = resource.startsWith("/") ? resource.substring(1) : resource;

        String stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            URL url = classLoader.getResource(stripped);
            if (url != null) {
                stream = url.toString();
            }
        }
        if (stream == null) {
            Environment.class.getResourceAsStream(resource);
        }
        if (stream == null) {
            URL url = Environment.class.getClassLoader().getResource(stripped);
            if (url != null) {
                stream = url.toString();
            }
        }
        if (stream == null) {
            throw new Exception(resource + " not found");
        }
        return stream;
    }

    private ICommunication startJxtaCommunication(String fileName) throws Exception {
        PeerService communication = new PeerService();

        URL url = ConfigurationUtils.locate(fileName);

        InputSource inputSource = new InputSource(url.openStream());
        DocumentBuilderFactory buildFactory = DocumentBuilderFactory.newInstance();
        Document descriptionsDocument = buildFactory.newDocumentBuilder().parse(inputSource);
        Element descriptionElement = descriptionsDocument.getDocumentElement();
        NodeList callNodes = descriptionElement.getElementsByTagName("call");

        if (callNodes.getLength() < 1) {
            throw new ParseException("No call tags in the descriptor.xml file.");
        }

        for (int i = 0; i < callNodes.getLength(); i++) {
            Element element = (Element) callNodes.item(i);
            final String methodName = "set" + element.getAttribute("attribute");

            NodeList argNodes = element.getElementsByTagName("arg");
            if (argNodes.getLength() < 1) {
                throw new ParseException("No arg tags under the a call tag in the descriptor.xml file.");
            }

            Element argElement = (Element) argNodes.item(0);
            final String type = argElement.getAttribute("type");
            final String value = ((Text) argElement.getChildNodes().item(0)).getNodeValue();

            Class argType = null;
            Object argValue = null;
            if (type.endsWith("String")) {
                argType = String.class;
                argValue = value;
            } else if (type.endsWith("boolean")) {
                argType = boolean.class;
                argValue = new Boolean(value);
            } else if (type.endsWith("int")) {
                argType = int.class;
                argValue = new Integer(value);
            } else {
                throw new ParseException("Unknown argument type: " + type);
            }

            Method method = communication.getClass().getMethod(methodName, new Class[] { argType });
            method.invoke(communication, new Object[] { argValue });
        }

        communication.boot();

        return communication;
    }
*/
}
