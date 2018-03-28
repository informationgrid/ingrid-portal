/*
 * **************************************************-
 * ingrid-portal-utils
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.security.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;



/** Utility class for XSS security operations !
 * NO singleton, use instance to avoid problems with multithreading ! 
 */
public class XSSUtil {

	private static final Logger LOG = Logger.getLogger(XSSUtil.class);

	/** Name of the parameter from filter config containing the value the
	 * matching regular expressions will be replaced with ! */
	public static final String REPLACE_VALUE_PARAM_NAME = "replaceValue";

	/** The value the matching regular expressions will be replaced with ! */
    private String regexReplaceValue = "";

	/** The regular expressions from filter configuration. */
	List<String> regexpFromConfig = new ArrayList<String>();

	/** Our patterns (regexps) to be matched and replaced ! */
	List<Pattern> patterns = new ArrayList<Pattern>();

	/** The default patterns (regexps) which will be used if no configuration from filter ! */
    private Pattern[] defaultPatterns = new Pattern[]{
        // Script fragments
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        // src='...'
        Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // lonely script tags
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // eval(...)
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // expression(...)
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // javascript:...
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        // vbscript:...
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        // onload(...)=...
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };
 
	/** Default configuration -> matching internal regexps will be replaced by ""  ! */
	public XSSUtil() {
		clear();
	}

	/** Return to default state clearing all encapsulated values, states ... */
	public void clear() {
		this.regexReplaceValue = "";
		this.regexpFromConfig = new ArrayList<String>();
		this.patterns = Arrays.asList(defaultPatterns);
	}

	/** Extract parameters from filter configuration (initial parameters passed from web.xml).
	 * NOTICE: Keeps current configuration if no regexps passed from filterconfig ! */
	public void parseFilterConfig(FilterConfig filterConfig) {
        // extract regexp from configuration
        if (filterConfig != null) {
        	List<String> myRegexps = new ArrayList<String>();

        	Enumeration paramNames = filterConfig.getInitParameterNames();
        	while (paramNames.hasMoreElements()) {
        		String paramName = paramNames.nextElement().toString();
        		String paramValue = filterConfig.getInitParameter(paramName);
        		
        		if (REPLACE_VALUE_PARAM_NAME.equals(paramName)) {
        			if (LOG.isInfoEnabled()) {
                    	LOG.info("Passed replaceValue from web.xml: \"" + paramValue + "\"");
                    }

        			setRegexReplaceValue(paramValue);

        		} else {
                    if (LOG.isInfoEnabled()) {
                    	LOG.info("Passed regex from web.xml: \"" + paramValue + "\"");
                    }

                    myRegexps.add(paramValue);
        		}
        	}
        	
            // set up patterns from configuration
        	if (myRegexps.size() > 0) {
        		regexpFromConfig = myRegexps;

        		this.patterns = new ArrayList<Pattern>();
            	for (String regexp : myRegexps) {
            		this.patterns.add(
                		Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));        		
            	}
        	} else {
                if (LOG.isInfoEnabled()) {
                	LOG.info("No regex passed from web.xml, we use our DEFAULT regexps for XSS filtering");
                }        		
        	}
        }
	}

    /** HTML form decoding. Decode a String from the application/x-www-form-urlencoded MIME format.
     * Use this method for decoding of requests Query Strings etc. before checking whether malicious content. 
     * @param encodedValue the encoded value, e.g. "%3C" instead of "<"
     * @return decoded value, e.g. "<" instead of "%3C" 
     */
    public String urlDecode(String encodedValue) {
    	if (encodedValue == null) {
    		return encodedValue;
    	}

    	try {
    		return URLDecoder.decode(encodedValue, "UTF-8");
    	}
    	catch (UnsupportedEncodingException e) {
    		throw new RuntimeException("Error in urlDecode.", e);
    	}
    }

    /** Clear value from malicious code.
     * @param value "infected" value
     * @param name name of parameter for debugging purposes. Pass null if no name wanted.
     * @return cleared value
     */
    public String stripParameter(String value, String name) {
        return stripXSS(value, name);
    }

    /** Clear values from malicious code.
     * @param values "infected" values
     * @param name name of parameter for debugging purposes. Pass null if no name wanted.
     * @return cleared values
     */
    public String[] stripParameterValues(String[] values, String name) {
        if (values == null) {
            return null;
        }
 
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripParameter(values[i], name);
        }
 
        return encodedValues;
    }

    /** Clear values in map from malicious code.
     * @param origMap parameter map from request with "infected" values
     * @return cleared parameter map
     */
    public Map stripParameterMap(Map origMap) {
        Map retMap = new HashMap();

        Iterator it = origMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
            	// value is always String[]
            	value = stripParameterValues((String[])value, key.toString());
/*
            	if (String.class.isAssignableFrom(value.getClass())) {
                	value = stripParameter(value.toString(), key.toString());
            	} else if (String[].class.isAssignableFrom(value.getClass())) {
                	value = stripParameterValues((String[])value, key.toString());
            	}
*/
            }

            retMap.put(key, value);
        }

        return retMap;
    }

    /** Clear value from malicious code.
     * @param value "infected" value
     * @param name name of header for debugging purposes. Pass null if no name wanted.
     * @return cleared value
     */
    public String stripHeader(String value, String name) {
        return stripXSS(value, name);
    }

    /** Clear the given "infected" value.
     * The matching regexps will be replaced with our "replace value" (can be set via FilterConfig).
     * @param value "infected" value ?
     * @param name name of parameter containing the value for debugging purposes. Pass null if not needed.
     * @return stripped value
     */
    private String stripXSS(String value, String name) {
    	if (value == null) {
            return null;    		
    	}

    	String origValue = value;

        // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
        // avoid encoded attacks.
    	// Needs huge ESAPI.properties file ! We skip this !
        //value = ESAPI.encoder().canonicalize(value);

        // Avoid null characters
        value = value.replaceAll("\0", "");

        // Remove all sections that match a pattern
        for (Pattern scriptPattern : patterns){
            value = scriptPattern.matcher(value).replaceAll(regexReplaceValue);
        }

        if (!origValue.equals(value)) {
        	LOG.warn("!!! Stripped request header/parameter \"" + name + "\"");
        	LOG.warn("from \"" + origValue + "\"");
        	LOG.warn("to   \"" + value + "\"");
        }

        return value;
    }

    /** Check whether the passed value has malicious content.
     * Call urlDecode() before calling this method if value from URL etc.
     * Is checked against all our regexps (after null characters are removed) !
     * If one matches then value is considered malicious !
     * @param value "infected" content ?
     * @return true=malicious, false=is ok
     */
    public boolean containsXSS(String value) {
    	if (value == null) {
            return false;    		
    	}

        // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
        // avoid encoded attacks.
    	// Needs huge ESAPI.properties file ! We skip this !
        //value = ESAPI.encoder().canonicalize(value);

        // Avoid null characters
        value = value.replaceAll("\0", "");

        // If pattern found we have malicious content.
        for (Pattern scriptPattern : patterns){
            if (scriptPattern.matcher(value).find()) {
            	LOG.warn("MALICIOUS content found (matches regexp of XSSFilter) ! -> \"" + value + "\"");
            	return true;
            }
        }

        return false;
    }

	/** Get the value the matching regular expressions will be replaced with ! */
	public String getRegexReplaceValue() {
		return regexReplaceValue;
	}

	/** Set the value the matching regular expressions will be replaced with ! */
	public void setRegexReplaceValue(String regexReplaceValue) {
		this.regexReplaceValue = regexReplaceValue;
	}

	/** Just for Unit tests to check read regexps ! */
	public List<String> getRegexpFromConfig() {
		return regexpFromConfig;
	}

	
    /** DEBUG level: output of parameters, attributes */
    public void debugRequest(HttpServletRequest hreq) {
    	if (hreq == null)
    		return;
        if (!LOG.isDebugEnabled())
        	return;

		LOG.debug("--------------------");
		LOG.debug("New Request: " + hreq.getClass());
		debugParameterMap(hreq.getParameterMap());
		debugAttributeNames(hreq.getAttributeNames());
		LOG.debug("--------------------");
    }

    /** DEBUG level: output of parameter key/value pairs in map ! */
    public void debugParameterMap(Map parameterMap) {
    	if (parameterMap == null)
    		return;
        if (!LOG.isDebugEnabled())
        	return;

        String output = null;
        Iterator it = parameterMap.entrySet().iterator();
        while (it.hasNext()) {
        	if (output == null) {
        		output = "";
        	}

            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            String outputEntry = null;
            String[] values = (String[]) entry.getValue();
            if (values != null) {
            	outputEntry = "[";
            	for (String value : values) {
            		outputEntry = outputEntry + "\"" + value + "\","; 
            	}
            	outputEntry = outputEntry + "]";
            }
            outputEntry = "\"" + key + "\" / " + outputEntry;
            output = output + "\n" + outputEntry;
        }
        if (output != null) {
            LOG.debug("ParameterMap: " + output);        	
        }
    }

    /** DEBUG level: output of attribute names in request ! */
    public void debugAttributeNames(Enumeration attributeNames) {
    	if (attributeNames == null)
    		return;
        if (!LOG.isDebugEnabled())
        	return;

        String output = null;
        while (attributeNames.hasMoreElements()) {
        	if (output == null) {
        		output = "[";
        	}
        	output = output + "\"" + attributeNames.nextElement() + "\",";
        	output = output + "]";
        }
        if (output != null) {
            LOG.debug("AttributeNames: " + output);
        }
    }
}
