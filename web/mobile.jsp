<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/tlds/envoytags.tld" prefix="envoyTags"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Solar Panels</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
        <meta name="apple-mobile-web-app-capable" content="yes"/>
        <link rel="apple-touch-icon" sizes="57x57" href="icons/apple-touch-icon-57x57.png">
        <link rel="apple-touch-icon" sizes="60x60" href="icons/apple-touch-icon-60x60.png">
        <link rel="apple-touch-icon" sizes="72x72" href="icons/apple-touch-icon-72x72.png">
        <link rel="apple-touch-icon" sizes="76x76" href="icons/apple-touch-icon-76x76.png">
        <link rel="apple-touch-icon" sizes="114x114" href="icons/apple-touch-icon-114x114.png">
        <link rel="apple-touch-icon" sizes="120x120" href="icons/apple-touch-icon-120x120.png">
        <link rel="apple-touch-icon" sizes="144x144" href="icons/apple-touch-icon-144x144.png">
        <link rel="apple-touch-icon" sizes="152x152" href="icons/apple-touch-icon-152x152.png">
        <link rel="apple-touch-icon" sizes="180x180" href="icons/apple-touch-icon-180x180.png">
        <link rel="icon" type="image/png" href="icons/favicon-32x32.png" sizes="32x32">
        <link rel="icon" type="image/png" href="icons/android-chrome-192x192.png" sizes="192x192">
        <link rel="icon" type="image/png" href="icons/favicon-96x96.png" sizes="96x96">
        <link rel="icon" type="image/png" href="icons/favicon-16x16.png" sizes="16x16">
        <link rel="manifest" href="icons/manifest.json">
        <link rel="mask-icon" href="icons/safari-pinned-tab.svg" color="#5bbad5">
        <meta name="msapplication-TileColor" content="#2d89ef">
        <meta name="msapplication-TileImage" content="/mstile-144x144.png">
        <meta name="theme-color" content="#ffffff">
        <link rel="shortcut icon" href="icons/favicon.ico">
        <link rel="stylesheet" type="text/css" href="envoymobile.css" title="mystyle">
        <link rel="stylesheet" href="themes/colin.min.css" />
        <link rel="stylesheet" href="themes/jquery.mobile.icons.min.css" />
        <link rel="stylesheet" href="jquery.mobile-1.4.5/jquery.mobile.structure-1.4.5.min.css"/>    
        <script type="text/javascript" src="jquery-2.1.3/jquery-2.1.3.min.js"></script>
        <script>
            var timeoutID;
            function setStyles() {
                $("li").addClass("ui-li-static ui-body-inherit");
                $("ul li:first-child").addClass("ui-first-child");
                $("ul li:last-child").addClass("ui-last-child");
                $("li").bind("click", function() {
                    updateData();
                });
            };
          
            function updateData() {
                clearTimeout(timeoutID);
                $("<li>Loading...</li>").replaceAll("li");
                setStyles();
                $("li").fadeTo(<%=application.getInitParameter("FadeOutTime")%>, <%=application.getInitParameter("FadeOpacity")%>);
                $("#envoysysdata").load("envoysysdata.jsp", function() {setStyles();});
                $("#envoyproddata").load("envoyproddata.jsp", function() {setStyles();});
                $("#envoystatusdata").load("envoystatusdata.jsp", function() {setStyles();});
                timeoutID = setTimeout('updateData()', <%=application.getInitParameter("RefreshTime")%>);
            };
            
            $(document).ready(function() {
                $("h1").bind("click", function() {
                    window.open("dashboard.html", "_self");
                });
                
                //$("div[data-role='content']").addClass("wallpaper");
                
                $.ajaxSetup({
                    timeout: <%=application.getInitParameter("AjaxTimeout")%>,
                    cache: false,
                    error: function(jqXHR, textStatus, errorThrown) {
                        clearTimeout(timeoutID);
                        $("#envoysysdata").html("<li><div class='errortext'>" + errorThrown + "<div></li>");
                        $("#envoyproddata").html("<li><div class='errortext'>" + errorThrown + "<div></li>");
                        $("#envoystatusdata").html("<li><div class='errortext'>" + errorThrown + "<div></li>");
                        timeoutID = setTimeout('updateData()', <%=application.getInitParameter("RetryTime")%>);
                    }
                });
                updateData();
            });
      </script>
        <script type="text/javascript" src="jquery.mobile-1.4.5/jquery.mobile-1.4.5.min.js"></script>
    </head>
    <body>
        <div data-role="page">
            <div data-role="header" data-position="fixed">
                <h1>Enphase Envoy</h1>
            </div>
            <div data-role="content">
                <div class="legend">System</div>
                <ul data-role="listview" data-inset="true" data-filter="false" id="envoysysdata"><li/></ul>
                <div class="legend">Production</div>
                <ul data-role="listview" data-inset="true" data-filter="false" id="envoyproddata"><li/></ul>
                <div class="legend">Status</div>
                <ul data-role="listview" data-inset="true" data-filter="false" id="envoystatusdata"><li/></ul>
            </div>
        </div>
    </body>
</html>
