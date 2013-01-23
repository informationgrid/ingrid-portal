<%
    request.getSession(true).setAttribute("userName", null);
    request.getSession().invalidate();
    String destination ="login.jsp";
    response.sendRedirect(response.encodeRedirectURL(destination));
%>