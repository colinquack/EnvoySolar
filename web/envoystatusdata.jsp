<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/tlds/envoytags.tld" prefix="EnvoyTags"%>
<!DOCTYPE html>

<EnvoyTags:EnvoyStatusData envoyHost='<%=application.getInitParameter("EnvoyHost")%>'>
    <li>
        <div class="container">
        <div class="leftdata"><%=envoystatusdataname%></div><div class="rightdata"><%=envoystatusdatavalue%></div>
        </div>
    </li>
</EnvoyTags:EnvoyStatusData>