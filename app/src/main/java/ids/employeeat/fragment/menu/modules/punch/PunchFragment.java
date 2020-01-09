package ids.employeeat.fragment.menu.modules.punch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import helper.logger.LogUtil;
import helper.utils.java.DateUtil;
import helper.utils.java.MathUtil;
import ids.employeeat.AppConstant;
import ids.employeeat.JsonTag;
import ids.employeeat.R;
import ids.employeeat.Theme;
import ids.employeeat.fragment.menu.MenuFragment;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;

public class PunchFragment extends MenuFragment implements ServerComm.ServerResponseListener, View.OnClickListener {

    private TextView tvAddress;
    private TextView tvPunchCount;
    private int punchStatusId = AppConstant.CON_PUNCH_STATUS_CHECK_IN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int layoutId = Theme.getLayout(AppConstant.CON_MENU_PUNCH_ID, getPreferences().getTheme());
        View view = inflater.inflate(layoutId, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        tvAddress = view.findViewById(R.id.tv_address);
        tvPunchCount = view.findViewById(R.id.tv_punch_count);
        initPunch(view);
        getCurrentSwipeDetails();
    }

    private void getCurrentSwipeDetails()
    {
        try
        {
            JSONObject requestData = new JSONObject();
            requestData.put(JsonTag.PUNCH_STATUS_ID, punchStatusId);
            getServerDataWithPersonId(this, ServerConstant.CURRENT_SWIPE_DETAILS, requestData);
        } catch (Exception e)
        {
            LogUtil.logException(getClass(), "getCurrentSwipeDetails", e);
        }
    }

    private void initPunch(View view)
    {
        TextView tvPunch = view.findViewById(R.id.tv_punch);
        tvPunch.setOnClickListener(this);
        tvPunch.setText(R.string.check_in);
        if (getArguments() != null)
        {
            punchStatusId = getArguments().getInt(AppConstant.KEY_PUNCH_STATUS);
            switch (punchStatusId)
            {
                case AppConstant.CON_PUNCH_STATUS_CHECK_OUT:
                    tvPunch.setText(R.string.check_out);
                    break;
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            String address = getString(R.string.address);
            address = address.concat(": ");
            address = address.concat(getLocation().getAddress());
            tvAddress.setText(address);
        } catch (Exception e)
        {
            LogUtil.logException(getClass(), e);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_punch:
                punchAttendance();
                break;
        }
    }

    public void punchAttendance()
    {
        try
        {
            JSONObject requestData = new JSONObject();
            JSONArray deviceLogList = new JSONArray();

            String description = "Regular Attendance";
            deviceLogList.put(getLogObject(description, punchStatusId, DateUtil.getCurrentTime(), AppConstant.CON_FALSE));


            requestData.put(JsonTag.DEVICE_LOG_INFO_LIST, deviceLogList);
            ServerComm.requestPost(getContext(), ServerConstant.APPLY_LIVE_LOG, this, requestData);
        } catch (Exception e)
        {
            LogUtil.logException(getClass(), "checkIn", e);
        }
    }

    @Override
    public void responseSuccess(int processType, JSONArray requestData, JSONArray responseData)
    {
        try
        {
            switch (processType)
            {
                case ServerConstant.APPLY_LIVE_LOG:
                    getCurrentSwipeDetails();
                    break;
                case ServerConstant.CURRENT_SWIPE_DETAILS:
                    setPunchCount(responseData.getJSONObject(0).getLong(JsonTag.LOG_COUNT));
                    break;
            }
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "-->", e);
        }
    }

    private void setPunchCount(long count)
    {
        String info = getString(R.string.you_have_checked_in_today);
        if(punchStatusId == AppConstant.CON_PUNCH_STATUS_CHECK_OUT)
        {
            info = getString(R.string.you_have_checked_out_today);
        }
        info  = info.concat(" ");
        info  = info.concat(MathUtil.getString(count));
        info  = info.concat(" ");
        info  = info.concat(getString(R.string.times));
        tvPunchCount.setText(info);
    }

    @Override
    public void responseFail(int processType, JSONArray requestData, int statusCode)
    {

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }
}
