package pogumedia.panasonic.sales.helper.util

/**
 * Created by crocodicstudio on 4/9/18.
 */
open class Constants {
    companion object {


        var PARAM_HEADER_TOKEN = "X-Authorization-Token"
        var PARAM_HEADER_TIME = "X-Authorization-Time"
        var PARAM_HEADER = "Referer"
        val PARAM_HEADER_USER_AGENT = "User-Agent"
        val API_STATUS = "api_status"
        val API_MESSAGE = "api_message"
        val INT_API_STATUS = 1
        val ITEMS = "data"
        var TIME_OUT = "SSL handshake timed out"


        val KEY_REQUEST_ACTIVITY = 1
        val KEY_RESULT_ACTIVITY = 2
        val LIMIT = 20


        //Constant key json

        val KEY_JSON_ITEM = "item"


        //Constant WEB VIEW
        val WEB_VIEW_MIME_TYPE = "text/html"
        val WEB_VIEW_ENCODING = "UTF-8"


        //Key status Record
        var KEY_STATUS_SERVER = 0
        var KEY_STATUS_NEW = 1
        var KEY_STATUS_EDIT = 2
        var KEY_STATUS_DELETE = 3
        val DIRECTORY_NAME = "PanasonicSales"
        var KEY_SYNC = "SYNC"
        val KEY_SYNC_ORDER =  "Survey"
        val KEY_SYNC_CUSTOMER = "Store"
        val ANOTHER_OPTION = "Another"


    }

}