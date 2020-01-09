package ids.employeeat.menu.reside.filter;


import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import helper.logger.LogUtil;
import helper.utils.java.DateUtil;
import ids.employeeat.JsonTag;
import ids.employeeat.R;
import ids.employeeat.filter.DateFilter;
import ids.employeeat.menu.reside.menu.Reside;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;

public class FilterMenu implements DateFilter.DateFilterListener, ServerComm.ServerResponseListener {

    private Activity activity;
    private View view;
    private TextView tvFromDate, tvToDate;
    private FilterMenuListener listener;
    private JSONObject filterObject = null;
    private Spinner sYearAssignment;

    public FilterMenu(Activity activity, Reside reside, FilterMenuListener listener)
    {
        reside.updateRightView(R.layout.filter_right_menu, R.id.ll_filter_right);
        this.activity = activity;
        this.view = reside.getRightView();
        this.listener = listener;
        initView();
    }

    public void showDateFilter()
    {
        View dateFilterView = getView().findViewById(R.id.inc_date_filter);
        if (dateFilterView != null)
        {
            dateFilterView.setVisibility(View.VISIBLE);
        }
    }

    public void showYearAssignmentFilter(long personId)
    {
        try
        {
            sYearAssignment = getView().findViewById(R.id.s_year_assignment);
            if (sYearAssignment != null)
            {
                ServerConstant.getServerDataWithPersonId(getActivity(), personId, this, ServerConstant.GET_LEAVE_ASSIGNMENT_LIST, null);
                sYearAssignment.setVisibility(View.VISIBLE);
            }
        } catch (Exception e)
        {
            LogUtil.logException(getClass(), "showYearAssignmentFilter", e);
        }
    }

    public void showDateYMFilter()
    {

        View dateYMFilterView = getView().findViewById(R.id.inc_date_ym_filter);
        if (dateYMFilterView != null)
        {
            dateYMFilterView.setVisibility(View.VISIBLE);
        }
    }

    public void showDateYFilter()
    {
        View dateYMFilterView = getView().findViewById(R.id.inc_date_ym_filter);
        //hide month
        Spinner sMonth = getView().findViewById(R.id.s_month);
        getView().findViewById(R.id.tv_month_label).setVisibility(View.GONE);

        sMonth.setVisibility(View.GONE);
        if (dateYMFilterView != null)
        {
            dateYMFilterView.setVisibility(View.VISIBLE);
        }
    }

    private void initView()
    {
        initDateFilter();
        initYMFilter();
        initSyncData();
    }

    private void initSyncData()
    {
        Button btnSync = getView().findViewById(R.id.btn_sync);
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (getFilterObject() != null)
                    {
                        if (getFilterObject().has("year") && getFilterObject().has("month"))
                        {
                            int year = getFilterObject().getInt("year");
                            int month = getFilterObject().getInt("month");
                            JSONObject filterData = new JSONObject();
                            filterData.put("fromDate", DateUtil.getDateFromMYMonthStart(month, year));
                            filterData.put("toDate", DateUtil.getDateFromMYMonthEnd(month, year));
                            listener.filterData(filterData);
                        }
                        else
                        {
                            listener.filterData(getFilterObject());
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(), R.string.data_already_sync, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e)
                {
                    LogUtil.logException(getClass(), e);
                }
            }
        });
    }

    private void initDateFilter()
    {
        tvFromDate = getView().findViewById(R.id.from_date);
        tvToDate = getView().findViewById(R.id.to_date);

        String date = DateUtil.getDate(System.currentTimeMillis(), DateUtil.DATE_FORMAT_DATE);
        tvFromDate.setText(date);
        tvToDate.setText(date);

        if (tvFromDate != null)
        {
            tvFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    DateFilter.selectYMDRange(getActivity(), FilterMenu.this);
                }
            });
        }
    }

    private void initYMFilter()
    {
        Spinner sMonth = getView().findViewById(R.id.s_month);
        if (sMonth != null)
        {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.month_array, android.R.layout.simple_spinner_dropdown_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sMonth.setAdapter(adapter);
            sMonth.setSelection(DateUtil.getMonth());
            sMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    try
                    {
                        putJsonData("month", position);
                    } catch (Exception e)
                    {
                        LogUtil.logException("initYMFilter", e);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });
        }
        Spinner sYear = getView().findViewById(R.id.s_year);
        if (sYear != null)
        {
            List<Integer> yearList = new ArrayList<>();
            yearList.add(DateUtil.getYear() - 2);
            yearList.add(DateUtil.getYear() - 1);
            yearList.add(DateUtil.getYear());
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, yearList);
            sYear.setAdapter(adapter);
            sYear.setSelection(2);
            sYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    try
                    {
                        String selectedItem = parent.getItemAtPosition(position).toString();
                        putJsonData("year", selectedItem);

                    } catch (Exception e)
                    {
                        LogUtil.logException("initYMFilter", e);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });
        }
    }

    public static JSONObject getDefaultYMDArray()
    {
        JSONObject filterData = null;
        try
        {
            filterData = new JSONObject();
            filterData.put("fromDate", DateUtil.getUTCStartOfDay(System.currentTimeMillis()));
            filterData.put("toDate", DateUtil.getUTCEndOfDay(System.currentTimeMillis()));
        } catch (Exception e)
        {
            LogUtil.logException("getDefaultYMDArray", e);
        }
        return filterData;
    }

    public static JSONObject getDefaultYMArray()
    {
        JSONObject filterData = null;
        try
        {
            filterData = new JSONObject();
            filterData.put("fromDate", DateUtil.getDateFromMYMonthStart(DateUtil.getMonth(), DateUtil.getYear()));
            filterData.put("toDate", DateUtil.getDateFromMYMonthEnd(DateUtil.getMonth(), DateUtil.getYear()));
        } catch (Exception e)
        {
            LogUtil.logException("getDefaultYMArray", e);
        }
        return filterData;
    }

    @Override
    public void dateFilterInfo(long fromDate, long toDate)
    {
        tvFromDate.setText(DateUtil.getDate(fromDate, DateUtil.DATE_FORMAT_DATE));
        tvToDate.setText(DateUtil.getDate(toDate, DateUtil.DATE_FORMAT_DATE));
        TimeZone timeZone = TimeZone.getDefault();
        putJsonData("fromDate", DateUtil.getUTCStartOfDay(fromDate + timeZone.getRawOffset()));
        putJsonData("toDate", DateUtil.getUTCEndOfDay(toDate + timeZone.getRawOffset()));
    }

    private void putJsonData(String key, Object value)
    {
        try
        {
            if (getFilterObject() == null)
            {
                filterObject = new JSONObject();
            }
            getFilterObject().put(key, value);
        } catch (Exception e)
        {
            LogUtil.logException("putJsonData", e);
        }
    }

    private View getView()
    {
        return this.view;
    }

    private Activity getActivity()
    {
        return activity;
    }

    private JSONObject getFilterObject()
    {
        return filterObject;
    }

    public void showLeaveTypeFilter()
    {
        RelativeLayout rlLeaveType = getView().findViewById(R.id.rl_leave_type);
        rlLeaveType.setVisibility(View.VISIBLE);
        Spinner sLeaveType = getView().findViewById(R.id.s_leave_type);
    }

    public interface FilterMenuListener {
        void filterData(JSONObject filterArray);
    }

    @Override
    public void responseSuccess(int processType, JSONArray requestData, JSONArray responseData)
    {
        switch (processType)
        {
            case ServerConstant.GET_LEAVE_ASSIGNMENT_LIST:
                processLeaveAssignmentList(responseData);
                break;
        }
    }

    private void processLeaveAssignmentList(final JSONArray responseData)
    {
        try
        {
            if (sYearAssignment != null && getActivity() != null && responseData != null && responseData.length() > 0)
            {
                int length = responseData.length();
                List<String> list = new ArrayList<>();
                for (int index = 0; index < length; index++)
                {
                    String fromDate = DateUtil.getUTCDate(responseData.getJSONObject(index).getLong(JsonTag.FROM_DATE), DateUtil.DATE_FORMAT_DATE);
                    String toDate = DateUtil.getUTCDate(responseData.getJSONObject(index).getLong(JsonTag.TO_DATE), DateUtil.DATE_FORMAT_DATE);
                    list.add(fromDate + " - " + toDate);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sYearAssignment.setSelection(0);
                sYearAssignment.setAdapter(adapter);
                sYearAssignment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        try
                        {
                            filterObject = new JSONObject();
                            filterObject.put(JsonTag.YEAR_ASSIGNMENT, responseData);

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });
            }
        } catch (Exception e)
        {
            LogUtil.logException(getClass(), "responseSuccess", e);
        }
    }

    @Override
    public void responseFail(int processType, JSONArray requestData, int statusCode)
    {

    }
}
