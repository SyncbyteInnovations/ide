package ids.employeeat.fragment.menu.modules.attendance.outdoorEntry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import helper.logger.LogUtil;
import helper.utils.java.DateUtil;
import ids.employeeat.AppConstant;
import ids.employeeat.JsonTag;
import ids.employeeat.R;
import ids.employeeat.filter.DateFilter;
import ids.employeeat.filter.TimeFilter;
import ids.employeeat.fragment.menu.MenuFragment;
import ids.employeeat.menu.reside.filter.FilterMenu;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;
import ids.employeeat.ui.list.attendance.manualEntry.ManualEntryAdapter;

@SuppressWarnings("all")
public class OutdoorEntryFragment extends MenuFragment implements View.OnClickListener, DateFilter.DateFilterListener, TimeFilter.TimeFilterListener, ServerComm.ServerResponseListener, FilterMenu.FilterMenuListener {

    private TextView tvFromDate, tvFromTime, tvToTime, tvToDate;
    private EditText etRemarks;
    private Button btnApply;

    private long fromDate = DateUtil.getStartOfDay(System.currentTimeMillis());
    private long toDate = DateUtil.getEndOfDay(System.currentTimeMillis());
    private long fromTime = System.currentTimeMillis();
    private long toTime = System.currentTimeMillis();

    private ManualEntryAdapter adapter;
    private ImageView ivFilterMenu;

    private long date = DateUtil.getStartOfDay(System.currentTimeMillis());
    private long time = System.currentTimeMillis();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_outdoor_entry, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        initRecycleView(view);
        initActivityView();


        this.etRemarks = view.findViewById(R.id.et_remarks);

        this.tvFromDate = view.findViewById(R.id.tv_from_date);
        this.tvFromDate.setOnClickListener(this);
        this.tvFromDate.setText(DateUtil.getDate(fromDate, DateUtil.DATE_FORMAT_DATE));

        this.tvToDate = view.findViewById(R.id.tv_to_date);
        this.tvToDate.setText(DateUtil.getDate(this.toDate, DateUtil.DATE_FORMAT_DATE));
        this.tvToDate.setOnClickListener(this);

        this.tvFromTime = view.findViewById(R.id.tv_from_time);
        this.tvFromTime.setOnClickListener(this);
        this.tvFromTime.setText(DateUtil.getDate(fromTime, DateUtil.DATE_FORMAT_HOUR_MIN));

        this.tvToTime = view.findViewById(R.id.tv_to_time);
        this.tvToTime.setText(DateUtil.getDate(toTime, DateUtil.DATE_FORMAT_HOUR_MIN));
        this.tvToTime.setOnClickListener(this);

        this.btnApply = view.findViewById(R.id.btn_apply);
        this.btnApply.setOnClickListener(this);
    }


    private void initActivityView()
    {
        if (getActivity() != null)
        {
            this.ivFilterMenu = getActivity().findViewById(R.id.btn_filter_menu);
            this.ivFilterMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    FilterMenu filterMenu = new FilterMenu(getActivity(), getReside(), OutdoorEntryFragment.this);
                    filterMenu.showDateFilter();
                }
            });
        }
    }

    private void initRecycleView(View view)
    {
        RecyclerView rvList = view.findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = new ManualEntryAdapter(getContext());
        rvList.setAdapter(getAdapter());
        getManualLog(FilterMenu.getDefaultYMDArray());
    }

    private void getManualLog(JSONObject filterData)
    {
        try
        {
            filterData.put(JsonTag.LOG_TYPE, ServerConstant.CON_ON_DUTY_LOG);
            getServerDataWithPersonId(this, ServerConstant.GET_MANUAL_SWIPE_INFO_LIST, filterData);
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "-->getManualLog", e);
        }
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_from_date:
                fromDate();
                break;
            case R.id.tv_to_date:
                toDate();
                break;
            case R.id.tv_from_time:
                fromTime();
                break;
            case R.id.tv_to_time:
                toTime();
                break;
            case R.id.btn_apply:
                apply();
                break;
        }
    }

    private void fromDate()
    {
        DateFilter.selectYMD(getActivity(), OutdoorEntryFragment.this, false);
    }

    private void fromTime()
    {
        TimeFilter.selectTime(getActivity(), OutdoorEntryFragment.this, false);
    }

    private void toDate()
    {
        DateFilter.selectYMD(getActivity(), OutdoorEntryFragment.this, true);
    }

    private void toTime()
    {
        TimeFilter.selectTime(getActivity(), OutdoorEntryFragment.this, true);
    }

    private void apply()
    {
        try
        {
            String onDutyReason = etRemarks.getText().toString().trim();
            long fromDate = DateUtil.getTimeInMilli(this.fromDate, this.fromTime);
            long toDate = DateUtil.getTimeInMilli(this.toDate, this.toTime);
            if (onDutyReason.length() <= 0)
            {
                printMsg(R.string.fields_mandatory);
            }
            else if (fromDate > toDate)
            {
                printMsg(R.string.from_date_cannot_be_greater_than_to_date);
            }
            else
            {
                JSONObject requestData = new JSONObject();

                JSONArray deviceLogList = new JSONArray();
                deviceLogList.put(getLogObject(onDutyReason, AppConstant.CON_PUNCH_STATUS_CHECK_IN, fromDate, AppConstant.CON_TRUE));
                deviceLogList.put(getLogObject(onDutyReason, AppConstant.CON_PUNCH_STATUS_CHECK_OUT, toDate, AppConstant.CON_TRUE));

                requestData.put(JsonTag.DEVICE_LOG_INFO_LIST, deviceLogList);

                ServerComm.requestPost(getContext(), ServerConstant.APPLY_ON_DUTY, this, requestData);

            }
        } catch (Exception e)
        {
            LogUtil.logException(getClass(), "apply", e);
        }
    }

    @Override
    public void dateFilterInfo(long fromDate, long toDate)
    {
        if (fromDate > 0)
        {
            this.fromDate = fromDate;
            tvFromDate.setText(DateUtil.getDate(fromDate, DateUtil.DATE_FORMAT_DATE));
        }
        if (toDate > 0)
        {
            this.toDate = toDate;
            tvToDate.setText(DateUtil.getDate(toDate, DateUtil.DATE_FORMAT_DATE));
        }
    }

    @Override
    public void timeFilterInfo(long fromTime, long toTime)
    {
        if (fromTime > 0)
        {
            this.fromTime = fromTime;
            tvFromTime.setText(DateUtil.getDate(fromTime, DateUtil.DATE_FORMAT_HOUR_MIN));
        }
        if (toTime > 0)
        {
            this.toTime = toTime;
            tvToTime.setText(DateUtil.getDate(toTime, DateUtil.DATE_FORMAT_HOUR_MIN));
        }
    }

    @Override
    public void responseSuccess(int processType, JSONArray requestData, JSONArray responseArray)
    {
        try
        {
            switch (processType)
            {
                case ServerConstant.GET_MANUAL_SWIPE_INFO_LIST:
                    getAdapter().setAdapterList(responseArray);
                    break;
            }
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "-->", e);
        }
    }

    @Override
    public void responseFail(int processType, JSONArray requestData, int statusCode)
    {

    }

    private ManualEntryAdapter getAdapter()
    {
        return adapter;
    }

    @Override
    public void filterData(JSONObject filterArray)
    {
        getManualLog(filterArray);
        getReside().closeMenu();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        setHeaderTitle(R.string.apply_on_duty);
        this.ivFilterMenu.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        this.ivFilterMenu.setVisibility(View.INVISIBLE);
    }
}
