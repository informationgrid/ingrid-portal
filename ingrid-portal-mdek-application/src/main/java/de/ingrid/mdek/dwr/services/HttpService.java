/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.mdek.dwr.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;


public class HttpService {

	private final static Logger log = Logger.getLogger(HttpService.class);	

    public String getHtmlTitle(String url) {
    	try {
    		Parser parser = new Parser (url);
    		// Find a node that is of type 'TextNode' and is child of a tag named 'title'
    		NodeList nodeList = parser.parse(new AndFilter(new NodeClassFilter(TextNode.class), new HasParentFilter(new TagNameFilter("title"))));
    		SimpleNodeIterator it = nodeList.elements();
    		while (it.hasMoreNodes()) {
    			Node n = it.nextNode();
    			return StringEscapeUtils.unescapeXml(n.getText());
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
