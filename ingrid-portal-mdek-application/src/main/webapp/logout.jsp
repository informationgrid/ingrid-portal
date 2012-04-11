<%
    request.getSession(true).setAttribute("userName", null);
    String destination ="login.jsp";
    response.sendRedirect(response.encodeRedirectURL(destination));
%>