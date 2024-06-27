<%@ page import="com.openrules.forms.gui.jsp.*" %>
<%@ page import="com.openrules.forms.*" %>
<%@ page import="com.openrules.ruleengine.*" %>
<%@ page import="cp.openrules.gui.*" %>

<%
   String xlsMain = "file:./webapps/cpinside.openrules.gui/rules/main/CP.xls";
   String sessionAttribute = "openrulesSession";	
   OpenRulesSession openrulesSession = (OpenRulesSession) session.getAttribute(sessionAttribute);
   if (openrulesSession == null )
   {
     openrulesSession = new OpenRulesSession(xlsMain);
     session.setAttribute( sessionAttribute, openrulesSession);
     System.out.println("NEW OPENRULES SESSION");
 
	 ProblemManager manager = new ProblemManager();	 
	 Dialog dialog = openrulesSession.getDialog();
	 dialog.put("manager",manager);
	 
   }
%>   

<HTML>
	<HEAD><TITLE>OpenRules</TITLE></HEAD>
	<body>
	  <%
	    System.out.println("PROCESS REQUEST");
   		openrulesSession.processRequest(session, request, out);
	  %>
	</body>
</HTML>

