/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class UtilsQueryString {

    public final static String OP_AND = "AND";

    public final static String OP_OR = "OR";

    public final static String OP_NOT = "NOT";

    public final static String OP_PHRASE = "PHRASE";

    public final static String OP_SIMPLE = "SIMPLE";

    /**
     * This method replaces a term in the query string with the new term. It also
     * takes into account that terms can be surrounded by '"'.
     * 
     * @param queryStr The query string.
     * @param term The term to search for.
     * @param newTerm The term to replace with.
     * @return The resulting query string.
     */
    public static String replaceTerm(String queryStr, String term, String newTerm) {
        int termStartPos = 0;
        int termStopPos = 0;
        String myTerm;
        if (queryStr == null) {
            queryStr = "";
        }
        StringBuffer result = new StringBuffer(queryStr);

        while (true) {
            termStartPos = getTermStartPos(result.toString(), termStopPos);
            if (termStartPos == result.length()) {
                break;
            }
            termStopPos = getTermStopPos(result.toString(), termStartPos);
            myTerm = result.substring(termStartPos, termStopPos);
            if (myTerm.equalsIgnoreCase(term)) {
                result.replace(termStartPos, termStopPos, newTerm);
                termStopPos = termStopPos + (newTerm.length() - term.length());
            } else if (myTerm.equalsIgnoreCase("\"" + term + "\"")) {
                result.replace(termStartPos, termStopPos, newTerm);
                termStopPos = termStopPos + (newTerm.length() - term.length()) - 2;
            }
            if (termStopPos == result.length()) {
                break;
            }
        }
        return result.toString();
    }

    /**
     * Add a new Term to the query string. This method tries to figure out
     * how to extend the existing query (i.e. put brackets around it), before
     * adding the term. Terms can be added with a operator.
     * 
     * operators: OP_OR, OP_AND, OP_NOT, OP_PHRASE
     * 
     * 
     * @param queryStr The query string.
     * @param str The term to add.
     * @param operator The operator to use.
     * @return The query string with the term added using operator.
     */
    public static String addTerm(String queryStr, String str, String operator) {
        StringBuffer result;

        if (str == null || operator == null || str.length() == 0) {
            return queryStr;
        }

        if (queryStr == null) {
            queryStr = "";
            result = new StringBuffer(str.length());
        } else {
            result = new StringBuffer(queryStr.length() + str.length() + 1);
            result.append(queryStr.trim());
        }

        if (operator.equals(OP_SIMPLE)) {
            if (queryStr.trim().length() > 0) {
                result.append(" ");
            }
            result.append(str);
        } else if (operator.equals(OP_OR)) {
            String[] terms = str.split(" ");
            if (queryStr.length() > 0 && terms.length > 0) {
                result.insert(0, "(");
                result.append(") OR ");
            }
            for (int i = 0; i < terms.length; i++) {
                result.append(terms[i]);
                if (i != terms.length - 1) {
                    result.append(" OR ");
                }
            }
        } else if (operator.equals(OP_AND)) {
            String[] terms = str.split(" ");
            if (queryStr.indexOf(" ") != -1 && terms.length > 0) {
                result.insert(0, "(");
                result.append(") ");
            } else if (queryStr.length() > 0 && queryStr.indexOf(" ") == -1) {
                result.append(" ");
            }
            for (int i = 0; i < terms.length; i++) {
                result.append(terms[i]);
                if (i != terms.length - 1) {
                    result.append(" ");
                }
            }
        } else if (operator.equals(OP_NOT)) {
            String[] terms = str.split(" ");
            if (queryStr.indexOf(" ") != -1 && terms.length > 0) {
                result.insert(0, "(");
                result.append(") ");
            } else if (queryStr.length() > 0 && queryStr.indexOf(" ") == -1) {
                result.append(" ");
            }
            for (int i = 0; i < terms.length; i++) {
                result.append("-").append(terms[i]);
            }
        } else if (operator.equals(OP_PHRASE)) {
            if (queryStr.indexOf(" ") != -1) {
                result.insert(0, "(");
                result.append(") ");
            } else if (queryStr.length() > 0 && queryStr.indexOf(" ") == -1) {
                result.append(" ");
            }
            result.append("\"").append(str).append("\"");
        }

        String resultStr = result.toString();
        // strip empty brackets with operators
        return resultStr;
    }

    /**
     * Strips white space from query string. This includes also empty brackets.
     * 
     * @param q The queryString
     * @return The "cleaned up" query string. 
     */
    public static String stripQueryWhitespace(String q) {
        // strip empty brackets with operators
        String returnStr = q.replaceAll("\\s*\\(([\\s()]|OR|AND|NOT)*\\)\\s*", "");
        returnStr = returnStr.replaceAll("\\s\\s+", " ");
        returnStr = returnStr.replaceAll("\\s*\\)", ")");
        returnStr = returnStr.replaceAll("\\(\\s*", "(");
        // strip '((', '))' like "((hallo welt))"
        returnStr = returnStr.replaceAll("\\((\\([^\\)]*\\))\\)", "$1");
        // strip '(', ')' like "(name)"
        returnStr = returnStr.replaceAll("\\(([^\\s]*)\\)", "$1");
        return returnStr.trim();
    }

    private static int getTermStartPos(String query, int start) {
        char c;
        if (query == null || query.length() == 0) {
            return 0;
        }
        String str = new String(query).toLowerCase();
        int cursor = start;
        while (true) {
            c = str.charAt(cursor);
            // ignore all dividing and white space chars
            if (" ()".indexOf(c) > -1) {
                cursor++;
            } else if (str.indexOf("or", cursor) == 0) {
                cursor = cursor + 2;
            } else if (str.indexOf("and", cursor) == 0) {
                cursor = cursor + 3;
            } else {
                break;
            }
            if (cursor == str.length()) {
                break;
            }
        }
        return cursor;
    }

    private static int getTermStopPos(String query, int start) {
        char c;
        boolean isInPhrase = false;
        String str = new String(query).toLowerCase();
        int cursor = start;
        while (true) {
            c = str.charAt(cursor);
            if (isInPhrase) {
                if (c == '"') {
                    cursor++;
                    break;
                } else {
                    cursor++;
                }
            } else {
                if (c == '"') {
                    isInPhrase = true;
                    cursor++;
                } else if (" ()".indexOf(c) > -1) {
                    break;
                } else {
                    cursor++;
                }
            }
            if (cursor == str.length()) {
                break;
            }
        }
        return cursor;
    }

    /**
     * Returns a "phrased" String (surrounded by '"') if the string contains a ' '.
     * 
     * @param term A String, representing a term.
     * @return The "phrased" term.
     */
    public static String getPhrasedTerm(String term) {
        if (term != null && term.indexOf(" ") != -1) {
            return "\"".concat(term).concat("\"");
        } else {
            return term;
        }
    }

    /**
     * Remove a field from query string (key:["]value["]).
     * 
     * @param query The query string.
     * @param term The field name to remove.
     * @return The resulting query string.
     */
    public static String removeField(String query, String fieldName) {
        int termStartPos = 0;
        int termStopPos = 0;
        String myTerm;
        StringBuffer result = new StringBuffer(query);

        while (true) {
            termStartPos = getTermStartPos(result.toString(), termStopPos);
            if (termStartPos == result.length()) {
                break;
            }
            termStopPos = getTermStopPos(result.toString(), termStartPos);
            myTerm = result.substring(termStartPos, termStopPos);
            if (myTerm.startsWith(fieldName.concat(":"))) {
                result.replace(termStartPos, termStopPos, "");
                termStopPos = termStartPos;
            }
            if (termStopPos == result.length()) {
                break;
            }
        }
        return result.toString();
    }
    
    public static String normalizeUuid(String token) {
    	return token.replaceAll("^\\{(.+)\\}$", "$1");
    }

}
