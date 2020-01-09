package ids.employeeat.fragment.menu.modules.leaves.leaveInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import helper.logger.LogUtil;
import ids.employeeat.R;
import ids.employeeat.filter.DateFilter;
import ids.employeeat.fragment.menu.MenuFragment;
import ids.employeeat.menu.reside.filter.FilterMenu;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;
import ids.employeeat.ui.list.leaves.leaveInfo.LeaveInfoAdapter;


@SuppressWarnings("unused")
public class LeaveInfoFragment extends MenuFragment implements ServerComm.ServerResponseListener, DateFilter.DateFilterListener, FilterMenu.FilterMenuListener {
    private LeaveInfoAdapter adapter;
    private ImageView ivFilterMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_leave_info, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        initRecycleView(view);
        initActivityView();
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
                    FilterMenu filterMenu = new FilterMenu(getActivity(), getReside(), LeaveInfoFragment.this);
                    filterMenu.showDateFilter();
                }
            });
        }
    }

    private void initRecycleView(View view)
    {
        RecyclerView rvList = view.findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = new LeaveInfoAdapter(getContext());
        rvList.setAdapter(getAdapter());
        try
        {
            JSONObject filterData = FilterMenu.getDefaultYMDArray();
            filterData.put("leaveTypeId", "2");
            getLeaveInfo(filterData);
        } catch (Exception e)
        {
            LogUtil.logException(TAG, e);
        }
    }


    @Override
    public void filterData(JSONObject filterData)
    {
        try
        {
            filterData.put("leaveTypeId", "2");
            getLeaveInfo(filterData);
            getReside().closeMenu();
        } catch (Exception e)
        {
            LogUtil.logException(TAG, e);
        }
    }

    private void getLeaveInfo(JSONObject filterData)
    {
        getServerDataWithPersonId(this, ServerConstant.GET_LEAVE_INFO_LIST, filterData);
    }


    private LeaveInfoAdapter getAdapter()
    {
        return adapter;
    }

    @Override
    public void dateFilterInfo(long fromDate, long toDate)
    {

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


    @Override
    public void onResume()
    {
        super.onResume();
        setHeaderTitle(R.string.leave_info);
        this.ivFilterMenu.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        this.ivFilterMenu.setVisibility(View.INVISIBLE);
    }


}
