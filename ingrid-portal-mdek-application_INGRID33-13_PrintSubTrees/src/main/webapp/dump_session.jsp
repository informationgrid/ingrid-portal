<%@ page import="java.util.*" %>
<%
  // add parameter to session
  String name = request.getParameter("name");
  String value = request.getParameter("value");
  if (name!=null && value!=null && name.length()>0) {
    session.setAttribute(name,value);
  }
  Date lastVisit = (Date)session.getAttribute("lastVisit");
  Date thisVisit = new Date();
%>
<HTML>
  <HEAD>
    <TITLE>Session List</TITLE>
  </HEAD>
  <BODY>
    <H1>Session List</H1>
    Last visit: <%= lastVisit %><BR>
    This visit: <%= thisVisit %><BR>
    Session ID: <%= session.getId() %><BR>
    Session max interval: <%= session.getMaxInactiveInterval() %><BR>

    <H2>Session parameters</H2>
    <%
      Enumeration<String> e = session.getAttributeNames();
      while (e.hasMoreElements()) {
        String attribute = (String) e.nextElement();
        out.println(""+attribute+"="+
          session.getAttribute(attribute)+"<BR>");
      }
        session.setAttribute("lastVisit",thisVisit);
    %>

    <H2>New session parameter</H2>
<% String url = response.encodeURL("session-rewrite"); %>
    <P>Form URL '<%= url %>'</P>
    <FORM ACTION='<%= url %>'>
        <P>Name: <INPUT TYPE='TEXT' NAME='name'></P>
        <P>Value: <INPUT TYPE='TEXT' NAME='value'></P>
      <INPUT TYPE='SUBMIT' VALUE='Add new value'>
    </FORM>
  </BODY>
</HTML>