/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
package de.ingrid.portal.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UtilsString {

    private static final Logger log = LoggerFactory.getLogger(UtilsString.class);

    // see http://hotwired.lycos.com/webmonkey/reference/special_characters/
    static Object[][] entities = {
    // {"#39", new Integer(39)}, // ' - apostrophe
            { "quot", 34 }, // " - double-quote
            { "amp", 38 }, // & - ampersand
            { "lt", 60 }, // < - less-than
            { "gt", 62 }, // > - greater-than
            { "nbsp", 160 }, // non-breaking space
            { "copy", 169 }, // © - copyright
            { "reg", 174 }, // ® - registered trademark
            { "Agrave", 192 }, // À - uppercase A, grave accent
            { "Aacute", 193 }, // �? - uppercase A, acute accent
            { "Acirc", 194 }, // Â - uppercase A, circumflex
            // accent
            { "Atilde", 195 }, // Ã - uppercase A, tilde
            { "Auml", 196 }, // Ä - uppercase A, umlaut
            { "Aring", 197 }, // Å - uppercase A, ring
            { "AElig", 198 }, // Æ - uppercase AE
            { "Ccedil", 199 }, // Ç - uppercase C, cedilla
            { "Egrave", 200 }, // È - uppercase E, grave accent
            { "Eacute", 201 }, // É - uppercase E, acute accent
            { "Ecirc", 202 }, // Ê - uppercase E, circumflex
            // accent
            { "Euml", 203 }, // Ë - uppercase E, umlaut
            { "Igrave", 204 }, // Ì - uppercase I, grave accent
            { "Iacute", 205 }, // �? - uppercase I, acute accent
            { "Icirc", 206 }, // Î - uppercase I, circumflex
            // accent
            { "Iuml", 207 }, // �? - uppercase I, umlaut
            { "ETH", 208 }, // �? - uppercase Eth, Icelandic
            { "Ntilde", 209 }, // Ñ - uppercase N, tilde
            { "Ograve", 210 }, // Ò - uppercase O, grave accent
            { "Oacute", 211 }, // Ó - uppercase O, acute accent
            { "Ocirc", 212 }, // Ô - uppercase O, circumflex
            // accent
            { "Otilde", 213 }, // Õ - uppercase O, tilde
            { "Ouml", 214 }, // Ö - uppercase O, umlaut
            { "Oslash", 216 }, // Ø - uppercase O, slash
            { "Ugrave", 217 }, // Ù - uppercase U, grave accent
            { "Uacute", 218 }, // Ú - uppercase U, acute accent
            { "Ucirc", 219 }, // Û - uppercase U, circumflex
            // accent
            { "Uuml", 220 }, // Ü - uppercase U, umlaut
            { "Yacute", 221 }, // �? - uppercase Y, acute accent
            { "THORN", 222 }, // Þ - uppercase THORN, Icelandic
            { "szlig", 223 }, // ß - lowercase sharps, German
            { "agrave", 224 }, // à - lowercase a, grave accent
            { "aacute", 225 }, // á - lowercase a, acute accent
            { "acirc", 226 }, // â - lowercase a, circumflex
            // accent
            { "atilde", 227 }, // ã - lowercase a, tilde
            { "auml", 228 }, // ä - lowercase a, umlaut
            { "aring", 229 }, // å - lowercase a, ring
            { "aelig", 230 }, // æ - lowercase ae
            { "ccedil", 231 }, // ç - lowercase c, cedilla
            { "egrave", 232 }, // è - lowercase e, grave accent
            { "eacute", 233 }, // é - lowercase e, acute accent
            { "ecirc", 234 }, // ê - lowercase e, circumflex
            // accent
            { "euml", 235 }, // ë - lowercase e, umlaut
            { "igrave", 236 }, // ì - lowercase i, grave accent
            { "iacute", 237 }, // í - lowercase i, acute accent
            { "icirc", 238 }, // î - lowercase i, circumflex
            // accent
            { "iuml", 239 }, // ï - lowercase i, umlaut
            { "igrave", 236 }, // ì - lowercase i, grave accent
            { "iacute", 237 }, // í - lowercase i, acute accent
            { "icirc", 238 }, // î - lowercase i, circumflex
            // accent
            { "iuml", 239 }, // ï - lowercase i, umlaut
            { "eth", 240 }, // ð - lowercase eth, Icelandic
            { "ntilde", 241 }, // ñ - lowercase n, tilde
            { "ograve", 242 }, // ò - lowercase o, grave accent
            { "oacute", 243 }, // ó - lowercase o, acute accent
            { "ocirc", 244 }, // ô - lowercase o, circumflex
            // accent
            { "otilde", 245 }, // õ - lowercase o, tilde
            { "ouml", 246 }, // ö - lowercase o, umlaut
            { "oslash", 248 }, // ø - lowercase o, slash
            { "ugrave", 249 }, // ù - lowercase u, grave accent
            { "uacute", 250 }, // ú - lowercase u, acute accent
            { "ucirc", 251 }, // û - lowercase u, circumflex
            // accent
            { "uuml", 252 }, // ü - lowercase u, umlaut
            { "yacute", 253 }, // ý - lowercase y, acute accent
            { "thorn", 254 }, // þ - lowercase thorn, Icelandic
            { "yuml", 255 }, // ÿ - lowercase y, umlaut
            { "euro", 8364 }, // Euro symbol
    };

    static Map e2i = new HashMap();

    static Map i2e = new HashMap();
    static {
        for (int i = 0; i < entities.length; ++i) {
            e2i.put(entities[i][0], entities[i][1]);
            i2e.put(entities[i][1], entities[i][0]);
        }
    }

    /**
     * Turns funky characters into HTML entity equivalents
     * <p>
     * e.g. <tt>"bread" & "butter"</tt> =>
     * <tt>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</tt>.
     * Update: supports nearly all HTML entities, including funky accents. See
     * the source code for more detail.
     * 
     * @see #htmlunescape(String)
     */
    public static String htmlescape(String s1) {
        if (s1 == null)
            return null;
        StringBuilder buf = new StringBuilder();
        int i;
        for (i = 0; i < s1.length(); ++i) {
            char ch = s1.charAt(i);
            String entity = (String) i2e.get((int) ch);
            if (entity == null) {
                if (((int) ch) > 128) {
                    buf.append("&#" + ((int) ch) + ";");
                } else {
                    buf.append(ch);
                }
            } else {
                buf.append("&" + entity + ";");
            }
        }
        return buf.toString();
    }

    /**
     * Given a string containing entity escapes, returns a string containing the
     * actual Unicode characters corresponding to the escapes.
     * 
     * Note: nasty bug fixed by Helge Tesgaard (and, in parallel, by Alex, but
     * Helge deserves major props for emailing me the fix). 15-Feb-2002 Another
     * bug fixed by Sean Brown <sean@boohai.com>
     * 
     * @see #htmlescape(String)
     */
    public static String htmlunescape(String s1) {
        StringBuilder buf = new StringBuilder();
        int i;
        for (i = 0; i < s1.length(); ++i) {
            char ch = s1.charAt(i);
            if (ch == '&') {
                int semi = s1.indexOf(';', i + 1);
                if (semi == -1) {
                    buf.append(ch);
                    continue;
                }
                String entity = s1.substring(i + 1, semi);
                Integer iso;
                if (entity.charAt(0) == '#') {
                    iso = new Integer(entity.substring(1));
                } else {
                    iso = (Integer) e2i.get(entity);
                }
                if (iso == null) {
                    // entity not found in entity list
                    // presume non entity character
                    buf.append(ch);
                    continue;
                } else {
                    buf.append((char) (iso.intValue()));
                }
                i = semi;
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Extracts a search term (single word) from a sentence (normally the
     * longest word)
     * 
     * @param sentence
     * @param separator
     *            separator between words in sentence (regex for split method)
     * @return
     */
    public static String getSearchTerm(String sentence, String separator) {
        char[] charsToRemove = new char[] { '.', ':', ',', '!', '?', ';', '"' };
        String ret = "";

        try {
            String[] terms = sentence.split(separator);

            // fetch longest term
            for (int i = 0; i < terms.length; i++) {
                if (terms[i].trim().length() > ret.length()) {
                    ret = terms[i].trim();
                }
            }

            // remove not supported chars from beginning
            int i = 0;
            char myChar;
            while (i < charsToRemove.length && ret.length() > 0) {
                myChar = ret.charAt(0);
                if (myChar == charsToRemove[i]) {
                    ret = ret.substring(1, ret.length()).trim();
                    i = -1;
                }
                i++;
            }

            // remove not supported chars from end
            i = 0;
            while (i < charsToRemove.length && ret.length() > 0) {
                myChar = ret.charAt(ret.length() - 1);
                if (myChar == charsToRemove[i]) {
                    ret = ret.substring(0, ret.length() - 1).trim();
                    i = -1;
                }
                i++;
            }
        } catch (Exception ex) {
            if (log.isWarnEnabled()) {
                log.warn("error fetching \"search\" term from string '" + sentence + "'", ex);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Search Term from teaser: " + ret);
        }

        return ret;
    }

    /**
     * Cut a string at a given position. Returns a substring with max. maxLength
     * or shorter. searches for the last occurence of ' '.
     * 
     * @param str
     * @param maxLength
     * @return
     */
    public static String cutString(String str, int maxLength) {
        return cutString(str, maxLength, Settings.SEARCH_RANKED_MAX_ROW_LENGTH);
    }

    /**
     * Cut a string at a given position. Also takes the maximumLength of the
     * first Row into Account, which may differ from the Length of the second
     * row ! Returns a substring with max. maxLength or shorter. searches for
     * the last occurence of ' '.
     * 
     * @param str
     * @param maxLength
     * @return
     */
    public static String cutString(String str, int maxLength, int maxLengthFirstRow) {
        if (str == null || str.length() == 0) {
            return str;
        }

        // check for lines without white space which are longer than the max row
        // length
        int startIndex = 0;
        int endIndex = startIndex + maxLengthFirstRow - 1;
        while (endIndex < str.length()) {
            int nextWhitespace = str.lastIndexOf(' ', endIndex);
            if (nextWhitespace == -1) {
                str = str.substring(0, endIndex - 3).concat("...");
                break;
            } else if (nextWhitespace <= startIndex) {
                str = str.substring(0, str.length() - 3).concat("...");
                break;
            }
            startIndex = endIndex + 1;
            endIndex = startIndex + Settings.SEARCH_RANKED_MAX_ROW_LENGTH - 1;
        }

        if (str.length() <= maxLength) {
            return str;
        }

        int lastWhitespace = str.lastIndexOf(' ', maxLength);
        if (lastWhitespace > 1) {
            return str.substring(0, lastWhitespace).concat("...");
        } else {
            return str.substring(0, maxLength - 3).concat("...");
        }
    }

    /**
     * Escape characters in a string for use in regular expressions.
     * 
     * @param str
     *            The String.
     * @return The escaped String.
     */
    public static String regExEscape(String str) {
        final String escapeChars = "\\[({^$.?*+|";
        StringBuilder buf = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            String c = str.substring(i, i + 1);
            if (escapeChars.indexOf(c) != -1) {
                buf.append('\\');
            }
            buf.append(c);
        }
        return buf.toString();
    }

    public static String getShortURLStr(String urlStr, int maxLength) {

        if (urlStr.length() <= maxLength)
            return urlStr;

        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            return "invalid url syntax";
        }

        String path = url.getPath();
        String host = url.getHost();
        String protocoll = url.getProtocol();
        String query = url.getQuery();
        int port = url.getPort();

        StringBuilder resultB = new StringBuilder();
        resultB.append(protocoll).append("://").append(host);
        if (port > -1) {
            resultB.append(":").append(port);
        }

        int maxPathLength = maxLength - resultB.length();
        if (maxPathLength <= 0) {
            return resultB.substring(0, maxLength - 3).concat("...");
        }
        if (path.length() <= maxPathLength && (query == null || query.length() == 0)) {
            resultB.append(path);
        } else {
            String[] pathElements = path.split("/");
            // first path element is empty string !
            if (pathElements != null && pathElements.length >= 2) {
                StringBuilder resultPath = new StringBuilder();
                // start from end !
                // don't take first path element into account, will be processed
                // afterwards (index 1)
                boolean pathElementsProcessed = true;
                for (int i = pathElements.length - 1; i > 1; i--) {
                    // 5 because of "/" which must be added and "/..." which
                    // should be addable !
                    int checkLenth = resultPath.length() + pathElements[i].length() + 5;
                    if (query != null) {
                        // if we have a query str, take "?..." into account as
                        // well
                        checkLenth = checkLenth + 4;
                    }
                    if (checkLenth <= maxPathLength) {
                        resultPath.insert(0, "/").insert(1, pathElements[i]);
                    } else {
                        resultPath.insert(0, "/...");
                        pathElementsProcessed = false;
                        break;
                    }
                }
                if (pathElementsProcessed) {
                    // try to add missing one !
                    if (resultPath.length() + pathElements[1].length() + 1 <= maxPathLength) {
                        resultPath.insert(0, "/").insert(1, pathElements[1]);
                    } else {
                        resultPath.insert(0, "/...");
                    }
                }
                resultB.append(resultPath);
            }
        }

        if (query != null && query.length() > 0) {
            if (resultB.length() < maxLength - 4) {
                if (query.length() + 1 < maxLength - resultB.length()) {
                    resultB.append("?").append(query);
                } else {
                    resultB.append("?").append(query.substring(0, maxLength - resultB.length() - 4)).append("...");
                }
                return resultB.toString();
            } else {
                return resultB.append("?...").toString();
            }
        }

        return resultB.toString();
    }

    public static String getURLDomain(String urlStr) {

        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            return "invalid url syntax";
        }

        return url.getHost();
    }

    /**
     * Strip HTML tags from a string.
     * 
     * @param s
     *            The String with HTML tags.
     * @return The String without HTML tags.
     */
    public static String stripHTMLTags(String s) {
        return s.replaceAll("\\<(.|\n)*?\\>", "");
    }

    /**
     * Strips HTML tags but conserves AND encodes HTML entities.
     * 
     * "&quot;<b>ingrid®</b>&quot;" will result in "&quot;ingrid&reg;&quot;"
     * 
     * @param s
     *            The input String.
     * @return The transformed output.
     */
    public static String stripHTMLTagsAndHTMLEncode(String s) {
        return UtilsString.htmlescape(UtilsString.stripHTMLTags(UtilsString.htmlunescape(s)));
    }

    public static String concatStringsIfNotNull(String[] strings, String separator)  {
    	StringBuilder result = new StringBuilder();
    	if (strings != null) {
	    	for (int i=0; i < strings.length; i++) {
	    		if (strings[i] != null) {
	    			if (result.length() > 0) {
	    				result.append(separator);
	    			}
					result.append(strings[i]);
	    		}
	    	}
    	}
    	
    	return result.toString();
    }

    /**
     * Escape chars in a string. The chars to escape are in the second passed string.  
     * @param str the string to escape
     * @param charsToEscape all chars in this string will be escaped in the string above
     * @return the escaped string
     */
    public static String escapeChars(String str, String charsToEscape) {
        StringBuilder buf = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            String c = str.substring(i, i + 1);
            if (charsToEscape.indexOf(c) != -1) {
                buf.append('\\');
            }
            buf.append(c);
        }
        return buf.toString();
    }
    
    /**
     * Turns all characters into HTML entity equivalents
     * 
     */
    public static String htmlescapeAll(String s1) {
        if (s1 == null)
            return null;
        StringBuilder buf = new StringBuilder();
        int i;
        for (i = 0; i < s1.length(); ++i) {
            char ch = s1.charAt(i);
            buf.append("&#" + ((int) ch) + ";");
        }
        return buf.toString();
    }

    /** replaceAll String method (Java) for velocity :) */
    public static String replaceAll(String stringToProcess, String stringToReplace, String stringToReplaceWith) {
    	return stringToProcess.replaceAll(stringToReplace, stringToReplaceWith);
    }
}
