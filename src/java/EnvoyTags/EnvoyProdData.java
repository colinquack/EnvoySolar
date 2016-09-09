/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EnvoyTags;

import Envoy.EnvoyData;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author cmarshal
 */
public class EnvoyProdData extends BodyTagSupport {
    private String envoyHost;
    private float feedInTariff;
    private float exportTariff;
    private ArrayList envoyProdData;
    private float weekProd = 0;
    private float totalProd = 0;
    private String totalUnit;
    private float totalDays = 0;
    private int index = 0;

    /**
     * Creates new instance of tag handler
     */
    public EnvoyProdData() {
        super();
    }

    ////////////////////////////////////////////////////////////////
    ///                                                          ///
    ///   User methods.                                          ///
    ///                                                          ///
    ///   Modify these methods to customize your tag handler.    ///
    ///                                                          ///
    ////////////////////////////////////////////////////////////////
    /**
     * Method called from doStartTag(). Fill in this method to perform other
     * operations from doStartTag().
     */
    private void otherDoStartTagOperations() {
        envoyProdData = new ArrayList();
        
        try {
            org.jsoup.nodes.Document doc = Jsoup.connect("http://" + envoyHost + "/production").get();

            Element h1 = doc.getElementsByTag("h1").first();
            Element table = h1.nextElementSibling();
            Elements alltr = table.getElementsByTag("tbody").first().getElementsByTag("tr");
            for (Element tr : alltr) {
                Elements alltd = tr.getElementsByTag("td");

                if (alltd.size() == 2) {
                    String name = alltd.first().text();
                    String value = alltd.last().text();
                    envoyProdData.add(new EnvoyData(name, addMoney(value)));
                    if (name.equals("Since Installation")) {
                        totalProd = Float.parseFloat(value.substring(0, value.indexOf(" ")));
                        totalUnit = value.substring(value.indexOf(" ") + 1);
                    } else if (name.equals("Past Week")) {
                        weekProd = Float.parseFloat(value.substring(0, value.indexOf(" ")));
                    }
                } else {
                    Elements alldiv = alltd.first().getElementsByTag("div");
                    String timeString = alldiv.first().text();
                    //Thu Mar 26, 2015 03:32 PM GMT
                    SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm aa zzz");
                    try {
                        Date dt = df.parse(timeString);
                        Date today = new Date();
                        long diff = today.getTime() - dt.getTime();
                        totalDays = (float)(diff / 1000 / 60 / 60 / 24);
                    } catch(Exception dtex) {

                    }
                }
            }
        } catch(Exception ex) {
        }
        
        addDailyOutput();
        
        index = 0;
        setOutput();
    }
    
    private void addDailyOutput() {
        DecimalFormat df = new DecimalFormat("0.0");
        String prodPerDayTotal = "0";
        
        switch (totalUnit) {
        case "kWh":
            prodPerDayTotal = df.format(totalProd / totalDays);
            break;
        case "MWh":
            prodPerDayTotal = df.format(totalProd * 1000.0 / totalDays);
            break;
        case "GWh":
            prodPerDayTotal = df.format(totalProd * 1000000.0 / totalDays);
            break;
        case "TWh":
            prodPerDayTotal = df.format(totalProd * 1000000000.0 / totalDays);
            break;
        }
        
        String prodPerDayWeek = df.format(weekProd / 7.0);
        envoyProdData.add(new EnvoyData("Daily", prodPerDayWeek + " kWh / " + prodPerDayTotal + " kWh"));
    }
    
    private void setOutput() {
        if (envoyProdData.size() > 0) {
            pageContext.setAttribute("envoyproddataname", ((EnvoyData)envoyProdData.get(index)).getName());
            pageContext.setAttribute("envoyproddatavalue", ((EnvoyData)envoyProdData.get(index)).getValue());
        }
    }
    
    private String addMoney(String rawValue) {
        float value = Float.parseFloat(rawValue.substring(0, rawValue.lastIndexOf(' ')));
        String unit = rawValue.substring(rawValue.lastIndexOf(' ') + 1);
        DecimalFormat df = new DecimalFormat("0.00");
        double rawMoney = value * (feedInTariff + (exportTariff * 0.5));
        
        switch (unit) {
        case "kWh":
            return rawValue + " (£" + String.valueOf(df.format(rawMoney)) + ")";
        case "MWh":
            return rawValue + " (£" + String.valueOf(df.format(rawMoney * 1000.0)) + ")";
        case "GWh":
            return rawValue + " (£" + String.valueOf(df.format(rawMoney * 1000000.0)) + ")";
        case "TWh":
            return rawValue + " (£" + String.valueOf(df.format(rawMoney * 1000000000.0)) + ")";
        case "Wh":
            return rawValue + " (£" + String.valueOf(df.format(rawMoney / 1000.0)) + ")";
        default:
            // W
            return rawValue;
        }
    }

    /**
     * Method called from doEndTag() Fill in this method to perform other
     * operations from doEndTag().
     */
    private void otherDoEndTagOperations() {
    }

    /**
     * Fill in this method to process the body content of the tag. You only need
     * to do this if the tag's BodyContent property is set to "JSP" or
     * "tagdependent." If the tag's bodyContent is set to "empty," then this
     * method will not be called.
     */
    private void writeTagBodyContent(JspWriter out, BodyContent bodyContent) throws IOException {
        // TODO: insert code to write html before writing the body content.
        // e.g.:
        //
        // out.println("<strong>" + attribute_1 + "</strong>");
        // out.println("   <blockquote>");

        // write the body content (after processing by the JSP engine) on the output Writer
        bodyContent.writeOut(out);

        // Or else get the body content as a string and process it, e.g.:
        //     String bodyStr = bodyContent.getString();
        //     String result = yourProcessingMethod(bodyStr);
        //     out.println(result);
        // TODO: insert code to write html after writing the body content.
        // e.g.:
        //
        // out.println("   </blockquote>");
        // clear the body content for the next time through.
        bodyContent.clearBody();
    }

    ////////////////////////////////////////////////////////////////
    ///                                                          ///
    ///   Tag Handler interface methods.                         ///
    ///                                                          ///
    ///   Do not modify these methods; instead, modify the       ///
    ///   methods that they call.                                ///
    ///                                                          ///
    ////////////////////////////////////////////////////////////////
    /**
     * This method is called when the JSP engine encounters the start tag, after
     * the attributes are processed. Scripting variables (if any) have their
     * values set here.
     *
     * @return EVAL_BODY_BUFFERED if the JSP engine should evaluate the tag
     * body, otherwise return SKIP_BODY. This method is automatically generated.
     * Do not modify this method. Instead, modify the methods that this method
     * calls.
     */
    @Override
    public int doStartTag() throws JspException {
        otherDoStartTagOperations();
        
        if (theBodyShouldBeEvaluated()) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

    /**
     * This method is called after the JSP engine finished processing the tag.
     *
     * @return EVAL_PAGE if the JSP engine should continue evaluating the JSP
     * page, otherwise return SKIP_PAGE. This method is automatically generated.
     * Do not modify this method. Instead, modify the methods that this method
     * calls.
     */
    @Override
    public int doEndTag() throws JspException {
        otherDoEndTagOperations();
        
        if (shouldEvaluateRestOfPageAfterEndTag()) {
            return EVAL_PAGE;
        } else {
            return SKIP_PAGE;
        }
    }

    /**
     * This method is called after the JSP engine processes the body content of
     * the tag.
     *
     * @return EVAL_BODY_AGAIN if the JSP engine should evaluate the tag body
     * again, otherwise return SKIP_BODY. This method is automatically
     * generated. Do not modify this method. Instead, modify the methods that
     * this method calls.
     */
    @Override
    public int doAfterBody() throws JspException {
        try {
            // This code is generated for tags whose bodyContent is "JSP"
            BodyContent bodyCont = getBodyContent();
            JspWriter out = bodyCont.getEnclosingWriter();
            
            writeTagBodyContent(out, bodyCont);
        } catch (Exception ex) {
            handleBodyContentException(ex);
        }
        
        if (theBodyShouldBeEvaluatedAgain()) {
            return EVAL_BODY_AGAIN;
        } else {
            return SKIP_BODY;
        }
    }

    /**
     * Handles exception from processing the body content.
     */
    private void handleBodyContentException(Exception ex) throws JspException {
        // Since the doAfterBody method is guarded, place exception handing code here.
        throw new JspException("Error in envoyProdData tag", ex);
    }

    /**
     * Fill in this method to determine if the rest of the JSP page should be
     * generated after this tag is finished. Called from doEndTag().
     */
    private boolean shouldEvaluateRestOfPageAfterEndTag() {
        // TODO: code that determines whether the rest of the page
        //       should be evaluated after the tag is processed
        //       should be placed here.
        //       Called from the doEndTag() method.
        //
        return true;
    }

    /**
     * Fill in this method to determine if the tag body should be evaluated
     * again after evaluating the body. Use this method to create an iterating
     * tag. Called from doAfterBody().
     */
    private boolean theBodyShouldBeEvaluatedAgain() {
        if (++index < envoyProdData.size()) {
            setOutput();
            return true;
        } else {
            return false;
        }
    }

    private boolean theBodyShouldBeEvaluated() {
        // TODO: code that determines whether the body should be
        //       evaluated should be placed here.
        //       Called from the doStartTag() method.
        return true;
    }
    
    /**
     * Setter for the envoyHost attribute.
     * @param value
     */
    public void setenvoyHost(java.lang.String value) {
        this.envoyHost = value;
    }   
    
    /**
     * Setter for the envoyHost attribute.
     * @param value
     */
    public void setfeedInTariff(java.lang.String value) {
        this.feedInTariff = Float.parseFloat(value);
    } 
    
    /**
     * Setter for the envoyHost attribute.
     * @param value
     */
    public void setexportTariff(java.lang.String value) {
        this.exportTariff = Float.parseFloat(value);
    }      
}
