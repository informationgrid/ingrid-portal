package de.ingrid.portal.global;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UtilsString {

    private final static Log log = LogFactory.getLog(UtilsString.class);

    //  see http://hotwired.lycos.com/webmonkey/reference/special_characters/
    static Object[][] entities = {
    // {"#39", new Integer(39)},       // ' - apostrophe
            { "quot", new Integer(34) }, // " - double-quote
            { "amp", new Integer(38) }, // & - ampersand
            { "lt", new Integer(60) }, // < - less-than
            { "gt", new Integer(62) }, // > - greater-than
            { "nbsp", new Integer(160) }, // non-breaking space
            { "copy", new Integer(169) }, // © - copyright
            { "reg", new Integer(174) }, // ® - registered trademark
            { "Agrave", new Integer(192) }, // À - uppercase A, grave accent
            { "Aacute", new Integer(193) }, // Á - uppercase A, acute accent
            { "Acirc", new Integer(194) }, // Â - uppercase A, circumflex accent
            { "Atilde", new Integer(195) }, // Ã - uppercase A, tilde
            { "Auml", new Integer(196) }, // Ä - uppercase A, umlaut
            { "Aring", new Integer(197) }, // Å - uppercase A, ring
            { "AElig", new Integer(198) }, // Æ - uppercase AE
            { "Ccedil", new Integer(199) }, // Ç - uppercase C, cedilla
            { "Egrave", new Integer(200) }, // È - uppercase E, grave accent
            { "Eacute", new Integer(201) }, // É - uppercase E, acute accent
            { "Ecirc", new Integer(202) }, // Ê - uppercase E, circumflex accent
            { "Euml", new Integer(203) }, // Ë - uppercase E, umlaut
            { "Igrave", new Integer(204) }, // Ì - uppercase I, grave accent
            { "Iacute", new Integer(205) }, // Í - uppercase I, acute accent
            { "Icirc", new Integer(206) }, // Î - uppercase I, circumflex accent
            { "Iuml", new Integer(207) }, // Ï - uppercase I, umlaut
            { "ETH", new Integer(208) }, // Ð - uppercase Eth, Icelandic
            { "Ntilde", new Integer(209) }, // Ñ - uppercase N, tilde
            { "Ograve", new Integer(210) }, // Ò - uppercase O, grave accent
            { "Oacute", new Integer(211) }, // Ó - uppercase O, acute accent
            { "Ocirc", new Integer(212) }, // Ô - uppercase O, circumflex accent
            { "Otilde", new Integer(213) }, // Õ - uppercase O, tilde
            { "Ouml", new Integer(214) }, // Ö - uppercase O, umlaut
            { "Oslash", new Integer(216) }, // Ø - uppercase O, slash
            { "Ugrave", new Integer(217) }, // Ù - uppercase U, grave accent
            { "Uacute", new Integer(218) }, // Ú - uppercase U, acute accent
            { "Ucirc", new Integer(219) }, // Û - uppercase U, circumflex accent
            { "Uuml", new Integer(220) }, // Ü - uppercase U, umlaut
            { "Yacute", new Integer(221) }, // Ý - uppercase Y, acute accent
            { "THORN", new Integer(222) }, // Þ - uppercase THORN, Icelandic
            { "szlig", new Integer(223) }, // ß - lowercase sharps, German
            { "agrave", new Integer(224) }, // à - lowercase a, grave accent
            { "aacute", new Integer(225) }, // á - lowercase a, acute accent
            { "acirc", new Integer(226) }, // â - lowercase a, circumflex accent
            { "atilde", new Integer(227) }, // ã - lowercase a, tilde
            { "auml", new Integer(228) }, // ä - lowercase a, umlaut
            { "aring", new Integer(229) }, // å - lowercase a, ring
            { "aelig", new Integer(230) }, // æ - lowercase ae
            { "ccedil", new Integer(231) }, // ç - lowercase c, cedilla
            { "egrave", new Integer(232) }, // è - lowercase e, grave accent
            { "eacute", new Integer(233) }, // é - lowercase e, acute accent
            { "ecirc", new Integer(234) }, // ê - lowercase e, circumflex accent
            { "euml", new Integer(235) }, // ë - lowercase e, umlaut
            { "igrave", new Integer(236) }, // ì - lowercase i, grave accent
            { "iacute", new Integer(237) }, // í - lowercase i, acute accent
            { "icirc", new Integer(238) }, // î - lowercase i, circumflex accent
            { "iuml", new Integer(239) }, // ï - lowercase i, umlaut
            { "igrave", new Integer(236) }, // ì - lowercase i, grave accent
            { "iacute", new Integer(237) }, // í - lowercase i, acute accent
            { "icirc", new Integer(238) }, // î - lowercase i, circumflex accent
            { "iuml", new Integer(239) }, // ï - lowercase i, umlaut
            { "eth", new Integer(240) }, // ð - lowercase eth, Icelandic
            { "ntilde", new Integer(241) }, // ñ - lowercase n, tilde
            { "ograve", new Integer(242) }, // ò - lowercase o, grave accent
            { "oacute", new Integer(243) }, // ó - lowercase o, acute accent
            { "ocirc", new Integer(244) }, // ô - lowercase o, circumflex accent
            { "otilde", new Integer(245) }, // õ - lowercase o, tilde
            { "ouml", new Integer(246) }, // ö - lowercase o, umlaut
            { "oslash", new Integer(248) }, // ø - lowercase o, slash
            { "ugrave", new Integer(249) }, // ù - lowercase u, grave accent
            { "uacute", new Integer(250) }, // ú - lowercase u, acute accent
            { "ucirc", new Integer(251) }, // û - lowercase u, circumflex accent
            { "uuml", new Integer(252) }, // ü - lowercase u, umlaut
            { "yacute", new Integer(253) }, // ý - lowercase y, acute accent
            { "thorn", new Integer(254) }, // þ - lowercase thorn, Icelandic
            { "yuml", new Integer(255) }, // ÿ - lowercase y, umlaut
            { "euro", new Integer(8364) }, // Euro symbol
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
     * Turns funky characters into HTML entity equivalents<p>
     * e.g. <tt>"bread" & "butter"</tt> => <tt>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</tt>.
     * Update: supports nearly all HTML entities, including funky accents. See the source code for more detail.
     * @see #htmlunescape(String)
     **/
    public static String htmlescape(String s1) {
        if (s1 == null) 
            return null;
        StringBuffer buf = new StringBuffer();
        int i;
        for (i = 0; i < s1.length(); ++i) {
            char ch = s1.charAt(i);
            String entity = (String) i2e.get(new Integer((int) ch));
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
     * Given a string containing entity escapes, returns a string
     * containing the actual Unicode characters corresponding to the
     * escapes.
     *
     * Note: nasty bug fixed by Helge Tesgaard (and, in parallel, by
     * Alex, but Helge deserves major props for emailing me the fix).
     * 15-Feb-2002 Another bug fixed by Sean Brown <sean@boohai.com>
     *
     * @see #htmlescape(String)
     **/
    public static String htmlunescape(String s1) {
        StringBuffer buf = new StringBuffer();
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
                    buf.append("&" + entity + ";");
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
     * Extracts a search term (single word) from a sentence (normally the longest word)
     * @param sentence
     * @param separator separator between words in sentence (regex for split method)
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
     * Cut a string at a given position. Returns a substring 
     * with max. maxLength or shorter. searches for the last 
     * occurence of ' '.
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
     * first Row into Account, which may differ from the Length of the second row ! 
     * Returns a substring with max. maxLength or shorter. searches for the last 
     * occurence of ' '.
     * 
     * @param str
     * @param maxLength
     * @return
     */
    public static String cutString(String str, int maxLength, int maxLengthFirstRow) {
        if (str == null || str.length() == 0) {
            return str;
        }

        // check for lines without white space which are longer than the max row length
        int startIndex = 0;
        int endIndex = startIndex + maxLengthFirstRow - 1;
        while (endIndex < str.length()) {
            int nextWhitespace = str.lastIndexOf(' ', endIndex);
            if (nextWhitespace == -1) {
                str = str.substring(0, endIndex - 3).concat("...");
                break;
            } else if (nextWhitespace <= startIndex) {
                str = str.substring(0, startIndex - 3).concat("...");
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
     * @param str The String.
     * @return The escaped String.
     */
    public static String regExEscape(String str) {
        final String escapeChars = "\\[({^$.?*+|";
        StringBuffer buf = new StringBuffer(str.length());
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
    
            StringBuffer resultB = new StringBuffer();
            resultB.append(protocoll).append("://").append(host);
            if (port > -1) {
                resultB.append(":").append(port);
            }
    
            int maxPathLength = maxLength - resultB.length();
            if (maxPathLength <= 0) {
                return resultB.substring(0, maxLength - 3).concat("...");
            }
            if (path.length() <= maxPathLength) {
                resultB.append(path);
            } else {
                String[] pathElements = path.split("/");
                // first path element is empty string !
                if (pathElements != null && pathElements.length >= 2) {
                    StringBuffer resultPath = new StringBuffer();
                    // start from end !
                    // don't take first path element into account, will be processed afterwards (index 1)
                    boolean pathElementsProcessed = true;
                    for (int i = pathElements.length - 1; i > 1; i--) {
                        // 5 because of "/" which must be added and "/..." which should be addable !
                        if (resultPath.length() + pathElements[i].length() + 5 <= maxPathLength) {
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
    
            if (query != null) {
                if (resultB.length() < maxLength) {
                    if (query.length() + 1 < maxLength - resultB.length()) {
                        resultB.append("?").append(query);
                    } else {
                        resultB.append("?").append(query.substring(0, maxLength - resultB.length() - 4)).append("...");
                    }
                } else {
    //                resultB.append("?...");
                }
            }
    
            if (resultB.length() > maxLength) {
                return resultB.substring(0, maxLength - 3).concat("...");
            }
    
            return resultB.toString();
        }

}
