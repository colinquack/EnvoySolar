<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/tlds/envoytags.tld" prefix="envoyTags"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Enphase Envoy</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
        <link rel="alternate" type="application/rss+xml" title="Envoy Feed" href="rss.jsp">
        <link rel="stylesheet" type="text/css" href="envoy.css" title="mystyle">
        <link rel="shortcut icon" href="icons/favicon.ico">
    </head>
    <body>
        <div class="bodybox">
            <table>
<envoyTags:EnvoySysData envoyHost='<%=application.getInitParameter("EnvoyHost")%>'>
                <tr>
                    <td colspan="3">
<%if (envoybadsysdata.equals("true")) {%>
                        <div class="badsysdata">NO <%=envoysysdata%></div>
<%} else {%>
                        <div class="sysdata"><%=envoysysdata%></div>        
<%}%>
                    </td>
                </tr>
</envoyTags:EnvoySysData>
<envoyTags:EnvoyProdData
    envoyHost='<%=application.getInitParameter("EnvoyHost")%>'
    feedInTariff='<%=application.getInitParameter("FeedInTariff")%>'
    exportTariff='<%=application.getInitParameter("ExportTariff")%>'>
    <tr><td><div class="dataname"><%=envoyproddataname%></div></td>
        <td><div>&nbsp;&nbsp;</div></td>
        <td><div class="datavalue"><%=envoyproddatavalue%></div></td></tr>
</envoyTags:EnvoyProdData>
<envoyTags:EnvoyStatusData envoyHost='<%=application.getInitParameter("EnvoyHost")%>'>
    <tr><td><div class="dataname"><%=envoystatusdataname%></div></td>
        <td><div>&nbsp;&nbsp;</div></td>
        <td><div class="datavalue"><%=envoystatusdatavalue%></div></td></tr>
</envoyTags:EnvoyStatusData>
            </table>
            <br/>
            <div>
                <button type="button" onclick="window.location.href='mobile.jsp'">Mobile</button>
                <button type="button" onclick="window.location.href='dashboard.html'">Dashboard</button>
            </div>
        </div>
    </body>
</html>
