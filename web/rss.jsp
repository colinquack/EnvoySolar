<?xml version="1.0" encoding="UTF-8"?>
<%@page contentType="application/xml"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.lang.String" %>
<%@page import="java.io.File" %>
<%@page import="java.util.Date" %>
<%@page import="org.jsoup.Jsoup"%>
<%@page import="org.jsoup.nodes.Element"%>
<%@page import="org.jsoup.select.Elements"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@taglib uri="/WEB-INF/tlds/envoytags.tld" prefix="envoyTags"%>

<%!
String mainURL(HttpServletRequest request) {
        return request.getScheme() + "://" +
        request.getServerName() + ":" +
        request.getServerPort() + 
        request.getContextPath();
}

String buildDate() {
    return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z").format(new Date());
}

String niceDate() {
    return new SimpleDateFormat("HH:mm ' on ' EEEEEEEEE d MMMMMMMMM yyyy").format(new Date());
}

String currentOutput() {
    String totalProd = "0 W";
    try {
        org.jsoup.nodes.Document doc = Jsoup.connect("http://" + getServletContext().getInitParameter("EnvoyHost") + "/production").get();

        Element h1 = doc.getElementsByTag("h1").first();
        Element table = h1.nextElementSibling();
        Elements alltr = table.getElementsByTag("tbody").first().getElementsByTag("tr");
        for (Element tr : alltr) {
            Elements alltd = tr.getElementsByTag("td");

            if (alltd.size() == 2) {
                String name = alltd.first().text();
                if (name.equals("Currently")) {
                    totalProd = alltd.last().text();
                    break;
                }
            }
        }
    } catch(Exception ex) {
    }
    
    return totalProd;
}
%>
<rss version="2.0" 
  xmlns:atom="http://www.w3.org/2005/Atom"
  xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:taxo="http://purl.org/rss/1.0/modules/taxonomy/"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
>
<channel>
<title>Enphase Envoy</title>
<description>Solar Panels</description>
<link><%=mainURL(request)%></link>
<lastBuildDate><%=buildDate()%></lastBuildDate>
        <item>
            <title>Generating <%=currentOutput()%> at <%=niceDate()%></title>
            <description>
<envoyTags:EnvoySysData envoyHost='<%=application.getInitParameter("EnvoyHost")%>'>
<%if (envoybadsysdata.equals("true")) {%>NO <%}%><%=envoysysdata%>.  <![CDATA[<br/>]]>
</envoyTags:EnvoySysData>
<envoyTags:EnvoyProdData
    envoyHost='<%=application.getInitParameter("EnvoyHost")%>'
    feedInTariff='<%=application.getInitParameter("FeedInTariff")%>'
    exportTariff='<%=application.getInitParameter("ExportTariff")%>'>
<%=envoyproddataname%> <%=envoyproddatavalue%>.  <![CDATA[<br/>]]>
</envoyTags:EnvoyProdData>
<envoyTags:EnvoyStatusData envoyHost='<%=application.getInitParameter("EnvoyHost")%>'>
<%=envoystatusdataname%> <%=envoystatusdatavalue%>.  <![CDATA[<br/>]]>
</envoyTags:EnvoyStatusData>
            </description>
            <pubDate><%=buildDate()%></pubDate>
            <guid isPermaLink="false"><%=niceDate()%></guid>
        </item>
</channel>
</rss>