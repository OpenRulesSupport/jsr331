<%@ page import="com.openrules.forms.gui.jsp.*" %>
<%@ page import="com.openrules.forms.*" %>

<%
   String xlsMain = "file:./webapps/Est01/rules/main/HelloWeb.xls";
   String s_attr = "openrules_session";	
   OpenRulesSession openrules_session = (OpenRulesSession) session.getAttribute(s_attr);
   if (openrules_session == null )
   {
     openrules_session = new OpenRulesSession(xlsMain);
     session.setAttribute( s_attr, openrules_session);
     System.out.println("NEW SESSION");
   }
%>   

<HTML>
	<HEAD><TITLE>OpenRules</TITLE>
	</HEAD>
	<body>
	  <%
	    System.out.println("PROCESS REQUEST");
   		openrules_session.processRequest(session, request, out);
	  %>
	</body>
</HTML>

