package com.capgemini.pccc.manager;

import com.capgemini.pccc.util.Constants;
import com.capgemini.pccc.util.IA_DateUtils;
import com.capgemini.pccc.util.IaUtils;
import org.apache.commons.configuration2.Configuration;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Collection;

/**
 * @author sflacher
 */
public class AbstractHttpServlet extends HttpServlet {
    public final static String CFG_FILENAME = "pccc.properties";
    // =========================================== constants
    private final static Logger LOG = LoggerFactory.getLogger(AbstractHttpServlet.class);
    private final static String BR = "<BR/>\n";
    public final static String userDir = System.getProperty("user.dir");


    // =========================================== class variables
    public static Configuration _config = IaUtils.loadConfig(CFG_FILENAME);

    static {
        LOG.info("==== AbstractHttpServlet LOADED");

    }

    // =========================================== instance variables

    // =========================================== class methods

    // =========================================== constructors

    // =========================================== public methods
    private String message;

    /**
     *
     */
    public AbstractHttpServlet() {
        Calendar now_CAL = IA_DateUtils.buildCalendarInstance();
        int year = now_CAL.get(Calendar.YEAR);
        String monthStr = Integer.toString(now_CAL.get(Calendar.MONTH) + 1);
        monthStr = IA_DateUtils.addFrontZeroIfNeeded(monthStr);
    }

    protected void respondWithHtml(HttpServletResponse response, String htmlTitle, String htmlContent,
                                   boolean isError) {
        String method = "respondWithHtml(exchange, htmlTitle [" + htmlTitle + "], content )" + Constants.DASHES;

        /*
         * nice layout for logging and debugging => intended to match
         * <table>,<tr>, <td>...
         */
        if (htmlContent == null) {
            htmlContent = "<H1>NO html content provided</H1>";
            isError = true;
        }

        htmlContent = htmlContent.replaceAll("<t", "\n<t");

        response.setHeader("content-type", "text/html; charset=UTF-8");
        // response.setContentLength(htmlOutput.toString().getBytes().length);

        StringBuffer htmlOutput = new StringBuffer();
        htmlOutput.append("<HTML>");
        htmlOutput.append("<HEAD>");
        htmlOutput.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        htmlOutput.append("<link rel=\"stylesheet\" href=\"PCCC_server.css\"/>");
        htmlOutput.append("<link rel=\"stylesheet\" href=\"bootstrap.css\"/>");

        // htmlOutput.append("<style>");
        // htmlOutput.append("body {background-color: #272B27;color:
        // #B3B4B7;font-family:'Arial'}");
        // // bg #3A3C40 et texte #B3B4B7 ou grafana #272B27
        //
        // // htmlOutput.append("body {background-color: powderblue;}");
        // // htmlOutput.append("h2 {color: blue;}");
        // htmlOutput.append("table, th, td {border: 1px green;border-collapse:
        // collapse;} ");
        // htmlOutput.append("tr, td {padding: 5px;}");
        // htmlOutput.append("p {color: red;}");
        // htmlOutput.append("</style>");
        htmlOutput.append("</HEAD><BODY>");
        //		htmlOutput.append("<img src=\"NO_PIC.JPG\" style=\"width: 100%;>");
        htmlOutput.append("<DIV class=\"panel-body issuepanel\">");
        if (isError) {
            htmlOutput.append("<img src=\"dashes.png\" style=\"width: 10%;>");
            htmlOutput.append("<H2>PAGE TECHNIQUE - Une erreur s'est produite</H2>");
            response.setStatus(HttpStatus.BAD_REQUEST_400);
        } else {
            response.setStatus(HttpStatus.OK_200);
        }

        htmlOutput.append("<DIV class=\"panel\" id=\"content\">");
        htmlOutput.append("<H3>" + htmlTitle + "</H3><hr/>");
        htmlOutput.append("" + htmlContent + "");
        htmlOutput.append("</DIV>");

        htmlOutput.append("</DIV></BODY></HTML>");

        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println(htmlOutput);
        } catch (IOException e) {
            LOG.error(Constants.X_LINE);
            LOG.error(e.getClass().getSimpleName() + Constants.DASHES + e.getMessage(), e);
            LOG.error(Constants.X_LINE);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        LOG.info(Constants.C_LINE);
        LOG.info(method + "Response sent");
        // LOG.info(method + htmlOutput);
        LOG.info(Constants.C_LINE);
    }

    /**
     * @param reqURI
     * @param resp
     * @param jsonObject      can be an object or a json object
     * @param isSuccess
     * @throws IOException
     */
    protected void respondWithJson(String reqURI, HttpServletResponse resp, JSONObject jsonObject, boolean isSuccess) {
        String method =
                "respondWithJson(req, resp, text [" + jsonObject.length() + "]chars, " + isSuccess + ")" + Constants.DASHES;
        LOG.info(method + " -- BEGIN");
        displayResponseHeaders(resp);

        /**
         * faire un vrai test unitaire
         */
        if (resp == null ) {
            LOG.warn(method + "JSONObject :" + jsonObject);
            LOG.warn(method + "HttpServletResponse resp is NULL => RETURNING NOW");
            return;
        }

        /*
         * {
         * "http_code" : "200",
         * "title" : "PAGE TECHNIQUE",
         * etc...
         * }
         */
        JSONObject jsoResponse = new JSONObject();
        jsoResponse.put("uri", reqURI);
        jsoResponse.put("success", isSuccess);
        jsoResponse.put("result", jsonObject);

        LOG.info(jsoResponse.toString());
        LOG.info(Constants.X_LINE);


        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentLength(jsoResponse.toString().getBytes().length);
        resp.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = null;
        try {
            out = resp.getWriter();
            out.println(jsoResponse);
            out.close();
            LOG.info(method + "ResponseHeaders and ResponseBody sent OK");
        } catch (IOException e) {
            LOG.error(e.getClass().getSimpleName() + " while getting httpServeletResponse.getWriter()", e);
        }

        LOG.info(Constants.C_LINE);
        LOG.info(method + "ResponseHeaders and ResponseBody sent");
        LOG.info(Constants.C_LINE);
        LOG.info(method + " -- END");
    }

    /**
     * @param resp
     */
    protected void displayResponseHeaders(HttpServletResponse resp) {
        if (resp == null)
            return;

        Collection<String> respHeaderSet = resp.getHeaderNames();
        String respHeaderVal = null;
        LOG.info("==== ResponseHeaders : ");
        for (String key : respHeaderSet) {
            LOG.info("\\t" + resp.getHeader(key));
        }
    }


    // =========================================== protected methods

}
