package ids.employeeat.server;

import android.content.Context;

import org.json.JSONObject;

import ids.employeeat.AppConstant;
import ids.employeeat.AppPreferences;

public class ServerConstant {
    public static final int REQUEST_DATA_SIZE = 9 * 1024;

    public static final int AUTHENTICATE_USER = 1;
    public static final int GET_ATTENDANCE_INFO_LIST = 2;
    public static final int GET_SWIPE_INFO_LIST = 3;
    public static final int GET_SHIFT_INFO_LIST = 4;
    public static final int GET_LEAVE_TYPE_LIST = 5;
    public static final int GET_LEAVE_DURATION_LIST = 6;
    public static final int GET_LEAVE_BALANCE_LIST = 7;
    public static final int GET_LEAVE_INFO_LIST = 8;
    public static final int GET_HOLIDAY_INFO_LIST = 9;
    public static final int APPLY_COMP_OFF = 10;
    public static final int APPLY_MANUAL_LOG = 11;
    public static final int APPLY_LEAVE = 12;
    public static final int GET_LEAVE_ASSIGNMENT_LIST = 13;
    public static final int APPLY_ON_DUTY = 14;
    public static final int GET_MANUAL_SWIPE_INFO_LIST = 15;
    public static final int APPLY_LIVE_LOG = 16;
    public static final int CURRENT_SWIPE_DETAILS = 17;
    public static final int AUTHORISE_MANUAL_LOG = 18;
    public static final int GET_SANCTIONER_LIST = 19;
    public static final int GET_AUTHORISER_LIST = 20;

    public static final int CON_DEVICE_LOG = 11;
    public static final int CON_ON_DUTY_LOG = 12;
    public static final int CON_MANUAL_LOG = 13;

    public static final int CON_PENDING = 11;
    public static final int CON_ACCEPTED = 12;
    public static final int CON_REJECTED = 13;


    //API
    private static final String REQUEST_USER_AUTHENTICATION = "login";
    private static final String REQUEST_GET_ATTENDANCE_INFO_LIST = "AttendanceInfoList";
    private static final String REQUEST_SWIPE_DETAILS = "SwipeDetails";
    private static final String REQUEST_SHIFT_INFO_LIST = "ShiftInfoList";
    private static final String REQUEST_GET_LEAVE_TYPE = "LeaveTypeList";
    private static final String REQUEST_GET_LEAVE_DURATION = "LeaveDurationList";
    private static final String REQUEST_GET_LEAVE_BALANCE = "LeaveBalanceList";
    private static final String REQUEST_GET_LEAVE_INFO_LIST = "LeaveInfoList";
    private static final String REQUEST_GET_HOLIDAY_INFO_LIST = "HolidayList";
    private static final String REQUEST_APPLY_COMP_OFF = "ApplyCompOff";
    private static final String REQUEST_APPLY_MANUAL_LOG = "ApplyManualLog";
    private static final String REQUEST_APPLY_LEAVE = "ApplyLeave";
    private static final String REQUEST_LEAVE_ASSIGNMENT_LIST = "LeaveAssignmentList";
    private static final String REQUEST_APPLY_ON_DUTY = "ApplyOnDuty";
    private static final String REQUEST_MANUAL_SWIPE_DETAILS = "ManualSwipeDetails";
    private static final String REQUEST_APPLY_LIVE_LOG = "ApplyLiveLog";
    private static final String REQUEST_CURRENT_SWIPE_DETAILS = "CurrentSwipeDetails";
    private static final String REQUEST_AUTHORISE_MANUAL_LOG = "AuthoriseManualLog";
    private static final String REQUEST_SANCTIONER_LIST = "SanctionerList";
    private static final String REQUEST_APPROVER_LIST = "ApproverList";

    static String getUrl(Context context, int processType)
    {
        String requestType = null;
        switch (processType)
        {
            case ServerConstant.AUTHENTICATE_USER:
                requestType = REQUEST_USER_AUTHENTICATION;
                break;
            case ServerConstant.GET_ATTENDANCE_INFO_LIST:
                requestType = REQUEST_GET_ATTENDANCE_INFO_LIST;
                break;
            case ServerConstant.GET_SWIPE_INFO_LIST:
                requestType = REQUEST_SWIPE_DETAILS;
                break;
            case ServerConstant.GET_SHIFT_INFO_LIST:
                requestType = REQUEST_SHIFT_INFO_LIST;
                break;
            case ServerConstant.GET_LEAVE_TYPE_LIST:
                requestType = REQUEST_GET_LEAVE_TYPE;
                break;
            case ServerConstant.GET_LEAVE_DURATION_LIST:
                requestType = REQUEST_GET_LEAVE_DURATION;
                break;
            case ServerConstant.GET_LEAVE_BALANCE_LIST:
                requestType = REQUEST_GET_LEAVE_BALANCE;
                break;
            case ServerConstant.GET_LEAVE_INFO_LIST:
                requestType = REQUEST_GET_LEAVE_INFO_LIST;
                break;
            case ServerConstant.GET_HOLIDAY_INFO_LIST:
                requestType = REQUEST_GET_HOLIDAY_INFO_LIST;
                break;
            case ServerConstant.APPLY_COMP_OFF:
                requestType = REQUEST_APPLY_COMP_OFF;
                break;
            case ServerConstant.APPLY_MANUAL_LOG:
                requestType = REQUEST_APPLY_MANUAL_LOG;
                break;
            case ServerConstant.APPLY_LEAVE:
                requestType = REQUEST_APPLY_LEAVE;
                break;
            case ServerConstant.GET_LEAVE_ASSIGNMENT_LIST:
                requestType = REQUEST_LEAVE_ASSIGNMENT_LIST;
                break;
            case ServerConstant.APPLY_ON_DUTY:
                requestType = REQUEST_APPLY_ON_DUTY;
                break;
            case ServerConstant.GET_MANUAL_SWIPE_INFO_LIST:
                requestType = REQUEST_MANUAL_SWIPE_DETAILS;
                break;
            case ServerConstant.APPLY_LIVE_LOG:
                requestType = REQUEST_APPLY_LIVE_LOG;
                break;
            case ServerConstant.CURRENT_SWIPE_DETAILS:
                requestType = REQUEST_CURRENT_SWIPE_DETAILS;
                break;
            case ServerConstant.AUTHORISE_MANUAL_LOG:
                requestType = REQUEST_AUTHORISE_MANUAL_LOG;
                break;
            case ServerConstant.GET_SANCTIONER_LIST:
                requestType = REQUEST_SANCTIONER_LIST;
                break;
            case ServerConstant.GET_AUTHORISER_LIST:
                requestType = REQUEST_APPROVER_LIST;
                break;
        }
        return (new AppPreferences(context)).getServerURL() + requestType;
    }

    public static void getServerDataWithPersonId(Context context, long personId, ServerComm.ServerResponseListener listener, int processType, JSONObject filterData) throws Exception
    {
        if (filterData == null)
        {
            filterData = new JSONObject();
        }
        filterData.put(AppConstant.TAG_PERSON_ID, personId);
        ServerComm.requestPost(context, processType, listener, filterData);
    }
}
