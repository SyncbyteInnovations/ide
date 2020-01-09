package ids.employeeat.fragment.menu.modules.attendance.manualEntry;


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

import java.util.TimeZone;

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
public class ManualEntryFragment extends MenuFragment implements View.OnClickListener, DateFilter.DateFilterListener, TimeFilter.TimeFilterListener, ServerComm.ServerResponseListener, FilterMenu.FilterMenuListener {
    private TextView tvDate;
    private TextView tvTime;
    private EditText etRemarks;
    private ManualEntryAdapter adapter;
    private ImageView ivFilterMenu;

    private long date = DateUtil.getStartOfDay(System.currentTimeMillis());
    private long time = System.currentTimeMillis();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_manual_entry, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        initRecycleView(view);
        initActivityView();

        Button btnApply = view.findViewById(R.id.btn_apply);
        etRemarks = view.findViewById(R.id.et_remarks);

        this.tvDate = view.findViewById(R.id.tv_date);
        this.tvDate.setOnClickListener(this);
        this.tvDate.setText(DateUtil.getDate(date, DateUtil.DATE_FORMAT_DATE));

        this.tvTime = view.findViewById(R.id.tv_time);
        this.tvTime.setOnClickListener(this);
        this.tvTime.setText(DateUtil.getDate(time, DateUtil.DATE_FORMAT_HOUR_MIN));

        btnApply.setOnClickListener(this);
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
                    FilterMenu filterMenu = new FilterMenu(getActivity(), getReside(), ManualEntryFragment.this);
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
            filterData.put(JsonTag.LOG_TYPE, ServerConstant.CON_MANUAL_LOG);
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
            case R.id.tv_date:
                date();
                break;
            case R.id.tv_time:
                time();
                break;
            case R.id.btn_apply:
                apply();
                break;
        }
    }

    private void apply()
    {
        try
        {
            String onDutyReason = etRemarks.getText().toString().trim();
            long fromDate = DateUtil.getTimeInMilli(this.date, this.time);
            if (onDutyReason.length() <= 0)
            {
                printMsg(R.string.fields_mandatory);
            }

            else
            {
                JSONObject requestData = new JSONObject();

                JSONArray deviceLogList = new JSONArray();

                deviceLogList.put(getLogObject(onDutyReason, AppConstant.CON_PUNCH_STATUS_CHECK_IN, fromDate, AppConstant.CON_TRUE));

                requestData.put(JsonTag.DEVICE_LOG_INFO_LIST, deviceLogList);

                ServerComm.requestPost(getContext(), ServerConstant.APPLY_MANUAL_LOG, this, requestData);

            }
        } catch (Exception e)
        {
            LogUtil.logException(getClass(), "apply", e);
        }
    }

    private void time()
    {
        TimeFilter.selectTime(getActivity(), ManualEntryFragment.this);
    }


    private void date()
    {
        DateFilter.selectYMD(getActivity(), ManualEntryFragment.this);
    }

    @Override
    public void dateFilterInfo(long fromDate, long toDate)
    {
        this.date = fromDate;
        tvDate.setText(DateUtil.getDate(fromDate, DateUtil.DATE_FORMAT_DATE));
    }

    @Override
    public void timeFilterInfo(long fromTime, long toTime)
    {
        this.time = fromTime;
        tvTime.setText(DateUtil.getDate(fromTime, DateUtil.DATE_FORMAT_HOUR_MIN));
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
        setHeaderTitle(R.string.manual_entry);
        this.ivFilterMenu.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        this.ivFilterMenu.setVisibility(View.INVISIBLE);
    }
}
