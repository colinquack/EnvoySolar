<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/tlds/envoytags.tld" prefix="EnvoyTags"%>
<!DOCTYPE html>

<EnvoyTags:EnvoySysData envoyHost='<%=application.getInitParameter("EnvoyHost")%>'>
    <li>
<%if (envoybadsysdata.equals("true")) {%>
        <div class="badsysdata">NO <%=envoysysdata%></div>
<%} else {%>
        <div class="sysdata"><%=envoysysdata%></div>        
<%}%>
    </li>
</EnvoyTags:EnvoySysData>
