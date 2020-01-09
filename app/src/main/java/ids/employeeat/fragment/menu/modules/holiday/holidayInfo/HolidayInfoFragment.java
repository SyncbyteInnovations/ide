package ids.employeeat.fragment.menu.modules.holiday.holidayInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import helper.logger.LogUtil;
import ids.employeeat.R;
import ids.employeeat.filter.DateFilter;
import ids.employeeat.fragment.menu.MenuFragment;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;
import ids.employeeat.ui.list.holiday.holidayInfo.HolidayInfoAdapter;

@SuppressWarnings("unused")
public class HolidayInfoFragment extends MenuFragment implements ServerComm.ServerResponseListener, DateFilter.DateFilterListener {
    private HolidayInfoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_holiday_info, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        initRecycleView(view);
    }

    private void initRecycleView(View view)
    {
        RecyclerView rvList = view.findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = new HolidayInfoAdapter(getContext());
        rvList.setAdapter(getAdapter());
        getHolidayInfo();
    }

    @Override
    public void dateFilterInfo(long fromDate, long toDate)
    {
    }

    private void getHolidayInfo()
    {
        try
        {
            getServerDataWithPersonId(this, ServerConstant.GET_HOLIDAY_INFO_LIST, null);
        } catch (Exception e)
        {
            LogUtil.logException("filterData", e);
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        setHeaderTitle(R.string.public_holiday);

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void responseSuccess(int processType, JSONArray requestData, JSONArray responseData)
    {
        getAdapter().setAdapterList(responseData);
    }

    @Override
    public void responseFail(int processType, JSONArray requestData, int statusCode)
    {

    }

    private HolidayInfoAdapter getAdapter()
    {
        return adapter;
    }

}
