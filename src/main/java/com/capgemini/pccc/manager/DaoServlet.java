package com.capgemini.pccc.manager;

import com.capgemini.pccc.util.Constants;
import com.capgemini.pccc.util.Enumeration.IMgr_Action;
import com.capgemini.pccc.util.IaUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.EnumUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author sflacher
 */
@SuppressWarnings("serial")
public class DaoServlet extends AbstractHttpServlet {

    // =========================================== constants
    private final static Logger LOG = LoggerFactory.getLogger(DaoServlet.class);
    private final static String CALL_ADMIN = "Contacter l'administrateur si besoin.";
    private static final String CR = "\n";

    // public final static Integer TRACKER_META_ALERTE = 5;
    public static String VERBOSE_DATE_FORMAT = "yyyy-MM-dd HH'h'mm'min'";
    public static DateFormat yMd_hms_verboseDF = new SimpleDateFormat(VERBOSE_DATE_FORMAT);
    private static String IM_HOST = null;
    private static String IM_PORT = null;

    protected static String SELECT_PROP = "pccc.sql.select.";
    protected static String INSERT_PROP = "pccc.sql.insert.";

    protected static String UPDATE_PROP = "pccc.sql.update.";
    protected static String DELETE_PROP = "pccc.sql.delete.";

    protected static Connection dbConn = null;

    // =========================================== class variables
    static {
        LOG.info("==== DaoServlet LOADED");
    }

    DaoManager daoManager = DaoManager.getInstance(_config);

    // =========================================== instance variables

    // =========================================== class methods

    // =========================================== constructors

    /**
     *
     */
    public DaoServlet() {
    }

    // =========================================== public methods
    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        // Do required initialization
        LOG.info("init() called");

    }

    /*
     * (non-Javadoc)
     *
     */
    public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String method = "doPost()" + " -- ";
        LOG.info(Constants.C_LINE + method);
        doGet(req, response);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {
        try {
            if (dbConn != null) {
                dbConn.close();
            }
        } catch (SQLException se) {
            LOG.error("SILENT exception closing dbConnection" + se.getMessage(), se);
        }//end finally try
    }

    /*
     * (non-Javadoc)
     *
     */
    public void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String method = "doGet()" + " -- ";
        LOG.info(Constants.C_LINE);
        String queryString = req.getQueryString();

        /*
         * Processing HTTP request parameters
         */
        LOG.info(method + "Parameters");
        Map<String, String> httpValMap = IaUtils.convertToUniqueValueMap(req.getParameterMap());

        /*
         * Checking parameter 'action'
         */
        String actionName = "action";
        if (!httpValMap.containsKey(actionName)) {
            respondWithHtml(response, "param�tre '" + actionName + "' manquant", queryString, false);
            return;
        }

        String actionStr = httpValMap.get(actionName);
        LOG.info("actionStr is : '" + actionStr + "'");
        IMgr_Action action = IMgr_Action.N_A;
        if (EnumUtils.isValidEnum(IMgr_Action.class, actionStr))
            action = IMgr_Action.valueOf(actionStr);
        LOG.info("IMgr_Action is : '" + action + "'");

        /*
         * Checking other parameters -- isParamMissing() includes htmlResponse()
         */
        //		if (isParamMissing(response, "pk_id", httpValMap))
        //			return;
        //
        //		String pk_idStr = httpValMap.get("pk_id");
        //		Long pk_id = Long.parseLong(pk_idStr);

        String reqURI = req.getRequestURI();

        /*
         * Dispatching request based on HTTP action requested
         */
        try {

            switch (action) {
                case select:
                    if (isParamMissing(reqURI, response, "queryName", httpValMap))
                        return;

                    doSelect(reqURI, response, httpValMap);
                    break;

                case insert:
                    if (isParamMissing(reqURI, response, "status_id", httpValMap))
                        return;

                    doInsert(reqURI, response, httpValMap);
                    break;

                case update:
                    doUpdate(reqURI, response, httpValMap);
                    break;

                case delete:
                    doDelete(reqURI, response, httpValMap);
                    break;

                default:
                    respondWithHtml(response, "action [" + action + "] inconnue", CALL_ADMIN, false);
                    break;
            }
        } catch (Exception e) {
            String err = "une " + e.getClass().getSimpleName() + " s'est produite lors de l'appel � " + action
                    .toString() + "()" + Constants.DASHES + e.getMessage();
            LOG.error(err, e);
            respondWithHtml(response, err, CALL_ADMIN, true);
        }

    }

    /**
     * @param response
     * @param paramName
     * @param httpValMap
     * @return
     */
    private Boolean isParamMissing(
            String reqURI, HttpServletResponse response, String paramName, Map<String, String> httpValMap) {
        String paramVal = httpValMap.get(paramName);
        if (paramVal == null) {
            JSONObject jso = new JSONObject();
            jso.put("error", "parametre '" + paramName + "' manquant");
            respondWithJson(reqURI, response, jso, false);
            return Boolean.TRUE;
        }
        LOG.info(paramName + " '" + paramVal + "'");
        return Boolean.FALSE;
    }

    // =========================================== protected methods

    // protected Configuration setConfigPropertiesFromJsonDoc(JSONObject jsoHit,
    // Long inDoc_Id) throws IOException {
    //
    // JSONObject sourceObj = jsoHit.getJSONObject("_source");
    // JSONObject metaObj = sourceObj.getJSONObject("meta");
    //
    // _config.setProperty("pk_ID", inDoc_Id.longValue());
    //
    // /*
    // * find trackerType and statusType
    // */
    // Long inDocTrackerId =
    // metaObj.getLong(flow_red_bdd_E.red_i_tracker_id_long.name());
    // IMgr_Tracker trackerType = getIMgr_TrackerFromMap(inDocTrackerId);
    // LOG.info("==== " + trackerType.name());
    //
    // Long inDocStatusId =
    // metaObj.getLong(flow_red_bdd_E.red_i_pk_status_id_long.name());
    // IMgr_Status statusType = getIMgr_StatusFromMap(inDocStatusId);
    // LOG.info("==== " + inDocStatusId);
    // LOG.info("==== " + statusType.name());
    //
    // String createOn = strOf(metaObj, flow_red_bdd_E.red_i_created_on_date);
    // String updatedOn = strOf(metaObj, flow_red_bdd_E.red_i_updated_on_date);
    // String sinceCreation_Min = null, sinceUpdated_Min = null;
    // String dateParam = createOn;
    // try {
    // sinceCreation_Min =
    // IA_DateUtils.convertUTC_To_UserFriendlyFormat(dateParam,
    // IaUtils.quotedTZ_df, yMd_hms_verboseDF);
    // dateParam = createOn;
    // sinceUpdated_Min =
    // IA_DateUtils.convertUTC_To_UserFriendlyFormat(dateParam,
    // IaUtils.quotedTZ_df, yMd_hms_verboseDF);
    // } catch (ParseException e) {
    // String text = "ParseException on IA_DateUtils.extractUTC_MinFromNow(" +
    // dateParam + ", " + IaUtils.quotedTZ_df_STR + ")";
    // LOG.error(text, e);
    // }
    //

    // =========================================== private methods

    /**
     * @param httpValMap
     * @return
     */
    private String buildCommentHtmlForm(Map<String, String> httpValMap) {
        StringBuffer sb = new StringBuffer(1000);
        sb.append("<form id=\"form1\">");
        // sb.append("<form action=\"/red?action=cloture\">");
        sb.append("<textarea name=\"user_comment\" rows=\"10\" cols=\"30\">");
        sb.append("</textarea><br/><br/><br/>");

        // Copy httpVal params as hidden <input>
        String value = null;
        for (String key : httpValMap.keySet()) {
            value = httpValMap.get(key);
            sb.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + value + "\"><br/>");
        }

        sb.append("</form><br/>");
        sb.append("<button type=\"submit\" form=\"form1\" value=\"Submit\">Continuer</button>");
        return sb.toString();
    }

    /**
     * @param reqURI
     * @param response
     * @param httpValMap
     */
    protected void doSelect(String reqURI, HttpServletResponse response, Map<String, String> httpValMap) {
        String queryObjName = httpValMap.get("queryName");
        String method = "doSelect(HttpServletResponse, Map<String, String>)(response, " + queryObjName + ")" +
                Constants.DASHES;

        LOG.info(AbstractHttpServlet.userDir);
        LOG.info(method + "==== BEGIN");

        // pccc.sql.select.generic = SELECT * FROM ${TBL_NAME};
        //_config.setProperty("QUERY_NAME", queryObjName);
        String sql = _config.getString("pccc.sql.select." + queryObjName);
        int aggColumnId = _config.getInt("pccc.sql.select." + queryObjName + ".aggColumnId") - 1;
        JSONObject jsonObject = null;

        try {
            jsonObject = daoManager.executeSQL(sql, aggColumnId, queryObjName);
            if (jsonObject != null) {
                respondWithJson(reqURI, response, jsonObject, false);
                return;
            }
        } catch (SQLException e) {
            LOG.error("Exception [" + e.getClass().getSimpleName() + "] occured -- " + e.getMessage(), e);
        }

        respondWithHtml(response, "SHOULD NOT COME HERE", method, false);
    }


    /**
     * @param response
     * @param httpValMap
     */
    private void doInsert(String reqURI, HttpServletResponse response, Map<String, String> httpValMap) {
        String pkId = httpValMap.get("pk_id");
        String method = "doInsert(response, " + pkId + ")" + Constants.DASHES;

        respondWithHtml(response, "NOT IMPLEMENTED", method, false);
    }

    /**
     * @param response
     * @param httpValMap
     */
    private void doUpdate(String reqURI, HttpServletResponse response, Map<String, String> httpValMap) {
        String pkId = httpValMap.get("pk_id");
        String method = "doUpdate(response, " + pkId + ")" + Constants.DASHES;

        respondWithHtml(response, "NOT IMPLEMENTED", method, false);
    }

    /**
     * @param response
     * @param httpValMap
     */
    private void doDelete(String reqURI, HttpServletResponse response, Map<String, String> httpValMap) {
        String pkId = httpValMap.get("pk_id");
        String method = "doDelete(response, " + pkId + ")" + Constants.DASHES;

        respondWithHtml(response, "NOT IMPLEMENTED", method, false);
    }

    /**
     * @param response
     * @param httpValMap
     */
    private void addComment(HttpServletResponse response, Map<String, String> httpValMap) {
        String pkId = httpValMap.get("pk_id");
        String method = "addComment(response, " + pkId + ")" + Constants.DASHES;

        respondWithHtml(response, "NOT IMPLEMENTED", method, false);
    }

}
