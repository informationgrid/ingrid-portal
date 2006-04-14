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
    
    
    public static String replaceTerm(String queryStr, String term, String newTerm) {
        int termStartPos = 0;
        int termStopPos = 0;
        String myTerm;
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
    
    public static String addTerm(String queryStr, String str, String operator) {
        StringBuffer result;
        
        if (str == null || operator == null || str.length() == 0) {
            return queryStr;
        }
        
        if (queryStr == null) {
            queryStr = "";
            result = new StringBuffer(str.length());
        } else {
            result = new StringBuffer(queryStr.length() + str.length());
            result.append(queryStr);
        }
        
        if (operator.equals(OP_OR)) {
            String[] terms = str.split(" ");
            if (queryStr.length() > 0 && terms.length > 0) {
                result.insert(0, "(");
                result.append(") OR ");
            }
            for (int i=0; i<terms.length; i++) {
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
            } else if (queryStr.indexOf(" ") == -1) {
                result.append(" ");
            }
            for (int i=0; i<terms.length; i++) {
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
            } else if (queryStr.indexOf(" ") == -1) {
                result.append(" ");
            }
            for (int i=0; i<terms.length; i++) {
                result.append("-").append(terms[i]);
            }
        } else if (operator.equals(OP_PHRASE)) {
            if (queryStr.indexOf(" ") != -1) {
                result.insert(0, "(");
                result.append(") ");
            } else if (queryStr.indexOf(" ") == -1) {
                result.append(" ");
            }
            result.append("\"").append(str).append("\"");
        }
        
        String resultStr = result.toString();
        // strip empty brackets with operators
        return resultStr;
    }
    

    public static String stripQueryWhitespace(String q) {
        // strip empty brackets with operators
        String returnStr = q.replaceAll("\\s*\\(([\\s()]|OR|AND|NOT)*\\)\\s*", "");
        returnStr = returnStr.replaceAll("\\s\\s+", "");
        return returnStr;
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
                } else if(" ()".indexOf(c) > -1) {
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

}
