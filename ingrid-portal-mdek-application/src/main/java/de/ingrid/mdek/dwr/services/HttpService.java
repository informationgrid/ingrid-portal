package de.ingrid.mdek.dwr.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;


public class HttpService {

	private final static Logger log = Logger.getLogger(HttpService.class);	

    @Deprecated
	public String getHtmlBody(String urlStr) {
    	try {
	    	URL url = new URL(urlStr);
        	BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        	char[] cbuf = new char[65536];
        	StringBuffer stringbuf = new StringBuffer();

        	int read_this_time = 0;
        	while (read_this_time != -1) {
        		read_this_time = in.read(cbuf, 0, 65536);
        		if (read_this_time != -1)
        			stringbuf.append(cbuf, 0, read_this_time);
        	}

        	String result = stringbuf.toString();
        	int beginIndex = result.toLowerCase().indexOf("<body");
        	beginIndex = result.indexOf('>', beginIndex)+1;
        	int endIndex = result.toLowerCase().indexOf("</body>");
        	if (beginIndex < 0 || beginIndex >= result.length()) {
        		beginIndex = 0;
        	}
        	if (endIndex < 0 || endIndex >= result.length()) {
        		endIndex = result.length() - 1;
        	}

        	result = result.substring(beginIndex, endIndex);
        	return result;
        	
    	} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
    	}
	}

    public String getHtmlTitle(String url) {
    	try {
    		Parser parser = new Parser (url);
    		// Find a node that is of type 'TextNode' and is child of a tag named 'title'
    		NodeList nodeList = parser.parse(new AndFilter(new NodeClassFilter(TextNode.class), new HasParentFilter(new TagNameFilter("title"))));
    		SimpleNodeIterator it = nodeList.elements();
    		while (it.hasMoreNodes()) {
    			Node n = it.nextNode();
    			return n.getText();
    		}

    	} catch (ParserException e) {
    		return "";
    	}

    	return "";
    }

    public String parseHtml(String url, int maxWords) {
    	// Parse the given url and return a string with maxWords
    	StringBean sb = new StringBean();
    	sb.setLinks(false);
    	sb.setReplaceNonBreakingSpaces(true);
    	sb.setCollapse(true);
    	sb.setURL(url);
    	String str = sb.getStrings();	// fetch url contents to str

/*
		// NOTE: First approach was trying to use a regexp to find the first n words in a string.
		// Surprisingly, this is way too slow for n > 15.
		// We now use a more simple regexp and just match it n times.

    	// Extract the first n words from str
    	// Pattern consists of '(word+word_boundary...)*maxWords'
    	// The flag DOTALL is required so newlines are also matched by '.'
    	Pattern expression = Pattern.compile("^(\\w+\\b.*?){"+maxWords+"}", Pattern.DOTALL);
    	Pattern expression = Pattern.compile("^(\\w+\\b\\s*?){"+maxWords+"}");
    	Matcher matcher = expression.matcher(str);
    	// If the first n words are found, return the match. Otherwise just return str.
    	if (matcher.lookingAt()) {
    		return matcher.group();
    	} else {
    		return str;
    	}
*/
		// regexp for 'word+word_boundary+{whitespace}'
    	Pattern expression = Pattern.compile("\\w+\\b\\s*?");
		Matcher matcher = expression.matcher(str);

		// Try to find the expression 'maxWords' times.
		// If the expression is not found (most likely due to the input string containing less than maxWords words),
		// the flag noMatch will be set.
		boolean noMatch = false;
		for (int i = 0; i < maxWords; ++i) {
			if (!matcher.find()) {
				noMatch = true;
				break;		
			}
		}

		if (noMatch || maxWords < 1) {
			// str contains less than maxWords words. Just return it.
			return str;
		} else {
			// str contains more than maxWords words. Return a substring till the end of the last match.
			return str.substring(0, matcher.end());
		}
    }
}
