<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/tlds/envoytags.tld" prefix="EnvoyTags"%>
<!DOCTYPE html>

<EnvoyTags:EnvoyProdData 
    envoyHost='<%=application.getInitParameter("EnvoyHost")%>'
    feedInTariff='<%=application.getInitParameter("FeedInTariff")%>'
    exportTariff='<%=application.getInitParameter("ExportTariff")%>'>
    <li>
        <div class="container">
            <div class="leftdata"><%=envoyproddataname%></div><div class="rightdata"><%=envoyproddatavalue%></div>
        </div>
    </li>
</EnvoyTags:EnvoyProdData>
