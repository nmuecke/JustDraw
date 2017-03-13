/*
 * Created on Sep 17, 2004
 */
package com.uob.utility;

import java.util.regex.*;

/**
 * @author camfoale
 */
public class HTMLConverter {

  static Pattern newLinePattern = Pattern.compile("[\r\n]+");

  public static String convertToHTML(String text) {

    String html;
    Matcher m = newLinePattern.matcher(text);
    text = m.replaceAll("<br>");
    text = text.replace('[', '<');
    text = text.replace(']', '>');
    html = text;

    return html;
  }

  public static String escape(String s) {
    if( s == null )
      return "";
    int len = s.length();
    StringBuffer sb = new StringBuffer(len * 5 / 4);

    for (int i = 0; i < len; i++)
    {
      char c = s.charAt(i);
      if( (c > 0 && c < 9) || (c > 10 && c < 13) || (c > 13 && c < 32) )
      {
        c = ' ';
        System.err.println("Invalid character " + c + " (0x" + Integer.toHexString(c)
            + ") found in string " + s);
      }
      String elem = htmlchars[c & 0xff];

      sb.append(elem == null ? "" + c : elem);
    }
    return sb.toString();
  }
  
  private static String sentenceCase(String s)
  {
    String ret = "";
    if(s.length() > 0) ret += s.substring(0,1);
    if(s.length() > 1) ret += s.substring(1);
    return ret;
  }

  private static String htmlchars[] = new String[256];

  static
  {
    String entry[] = { "nbsp", "iexcl", "cent", "pound", "curren", "yen", "brvbar", "sect", "uml",
        "copy", "ordf", "laquo", "not", "shy", "reg", "macr", "deg", "plusmn", "sup2", "sup3",
        "acute", "micro", "para", "middot", "cedil", "sup1", "ordm", "raquo", "frac14", "frac12",
        "frac34", "iquest", "Agrave", "Aacute", "Acirc", "Atilde", "Auml", "Aring", "AElig",
        "CCedil", "Egrave", "Eacute", "Ecirc", "Euml", "Igrave", "Iacute", "Icirc", "Iuml", "ETH",
        "Ntilde", "Ograve", "Oacute", "Ocirc", "Otilde", "Ouml", "times", "Oslash", "Ugrave",
        "Uacute", "Ucirc", "Uuml", "Yacute", "THORN", "szlig", "agrave", "aacute", "acirc",
        "atilde", "auml", "aring", "aelig", "ccedil", "egrave", "eacute", "ecirc", "euml",
        "igrave", "iacute", "icirc", "iuml", "eth", "ntilde", "ograve", "oacute", "ocirc",
        "otilde", "ouml", "divid", "oslash", "ugrave", "uacute", "ucirc", "uuml", "yacute",
        "thorn", "yuml" };

    htmlchars['&'] = "&amp;";
    htmlchars['<'] = "&lt;";
    htmlchars['>'] = "&gt;";

    for (int c = '\u00A0', i = 0; c <= '\u00FF'; c++, i++)
    {
      htmlchars[c] = "&" + entry[i] + ";";
    }

    for (int c = '\u0083', i = 131; c <= '\u009f'; c++, i++)
    {
      htmlchars[c] = "&#" + i + ";";
    }

    for (int i = 0; i < 256; i++)
    {
      if( htmlchars[i] != null && htmlchars[i].equalsIgnoreCase("&nbsp;") )
        htmlchars[i] = " ";
    }

    htmlchars['\u0088'] = htmlchars['\u008D'] = htmlchars['\u008E'] = null;
    htmlchars['\u008F'] = htmlchars['\u0090'] = htmlchars['\u0098'] = null;
    htmlchars['\u009D'] = null;
  }

}
