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

/**
 *
 * @author cmarshal
 */
public class EnvoyStatusData extends BodyTagSupport {
    private String envoyHost;
    private ArrayList envoyStatusData;
    private int index = 0;

    /**
     * Creates new instance of tag handler
     */
    public EnvoyStatusData() {
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
        ArrayList allEnvoyData = new ArrayList();
        envoyStatusData = new ArrayList();
        
        try {
            org.jsoup.nodes.Document doc = Jsoup.connect("http://" + envoyHost).get();

            Elements allh2 = doc.getElementsByTag("h2");
            for (Element h2 : allh2) {
                if (h2.text().equals("System Statistics")) {
                    Elements tables = h2.parent().getElementsByTag("table");
                    Elements alltr = tables.first().getElementsByTag("tbody").first().getElementsByTag("tr");
                    for (Element tr : alltr) {
                        Elements alltd = tr.getElementsByTag("td");
                        allEnvoyData.add(new EnvoyData(alltd.first().text(), alltd.last().text()));
                    }
                }
            }
        } catch(Exception ex) {
        }
        
        int numMicroInverters = 0;
        int numMicroInvertersOnline = 0;
        
        for (int i = 0; i < allEnvoyData.size(); i++) {
            EnvoyData ed = (EnvoyData)allEnvoyData.get(i);
            if (ed.getName().equals("Last connection to website")) {
                envoyStatusData.add(new EnvoyData(ed.getName(), ed.getValue()));
            } else if (ed.getName().equals("Number of Microinverters")) {
                numMicroInverters = Integer.parseInt(ed.getValue());
            } else if (ed.getName().equals("Number of Microinverters Online")) {
                numMicroInvertersOnline = Integer.parseInt(ed.getValue());
            }
        }
        
        envoyStatusData.add(new EnvoyData("Microinverters Online", 
                Integer.toString(numMicroInvertersOnline) + "/" + Integer.toString(numMicroInverters)));
        
        index = 0;
        setOutput();
    }
    
    private void setOutput() {
        if (envoyStatusData.size() > 0) {
            EnvoyData ed = (EnvoyData)envoyStatusData.get(index);
            pageContext.setAttribute("envoystatusdataname", ed.getName());
            pageContext.setAttribute("envoystatusdatavalue", ed.getValue());
        }
    }

    /**
     * Method called from doEndTag() Fill in this method to perform other
     * operations from doEndTag().
     */
    private void otherDoEndTagOperations() {
        // TODO: code that performs other operations in doEndTag
        //       should be placed here.
        //       It will be called after initializing variables,
        //       finding the parent, setting IDREFs, etc, and
        //       before calling shouldEvaluateRestOfPageAfterEndTag().
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
        throw new JspException("Error in envoyData tag", ex);
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
        if (++index < envoyStatusData.size()) {
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
}
