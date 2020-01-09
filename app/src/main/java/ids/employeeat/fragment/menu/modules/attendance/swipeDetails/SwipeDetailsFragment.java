package ids.employeeat.fragment.menu.modules.attendance.swipeDetails;

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
import ids.employeeat.ui.list.attendance.swipeDetails.SwipeDetailsAdapter;
import ids.employeeat.filter.DateFilter;
import ids.employeeat.fragment.menu.MenuFragment;
import ids.employeeat.menu.reside.filter.FilterMenu;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;


public class SwipeDetailsFragment extends MenuFragment implements ServerComm.ServerResponseListener, DateFilter.DateFilterListener, FilterMenu.FilterMenuListener
{
    private SwipeDetailsAdapter adapter;
    private ImageView ivFilterMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_swipe_details, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        initActivityView();
        initRecycleView(view);
    }

    private void initActivityView()
    {
        this.ivFilterMenu = getActivity().findViewById(R.id.btn_filter_menu);
        this.ivFilterMenu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FilterMenu filterMenu = new FilterMenu(getActivity(), getReside(), SwipeDetailsFragment.this);
                filterMenu.showDateFilter();
            }
        });
    }

    private void initRecycleView(View view)
    {
        RecyclerView rvList = view.findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = new SwipeDetailsAdapter(getContext());
        rvList.setAdapter(getAdapter());
        getSwipeInfo(FilterMenu.getDefaultYMDArray());
    }

    @Override
    public void dateFilterInfo(long fromDate, long toDate)
    {
    }

    @Override
    public void filterData(JSONObject filterData)
    {
        getSwipeInfo(filterData);
        getReside().closeMenu();
    }

    private void getSwipeInfo(JSONObject filterData)
    {
        try
        {
            filterData.put("logType", ServerConstant.CON_DEVICE_LOG);
            getServerDataWithPersonId(this, ServerConstant.GET_SWIPE_INFO_LIST, filterData);
        }
        catch (Exception e)
        {
            LogUtil.logException(TAG + "-->getSwipeInfo", e);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setHeaderTitle(R.string.swipe_details);
        this.ivFilterMenu.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        this.ivFilterMenu.setVisibility(View.GONE);
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

    private SwipeDetailsAdapter getAdapter()
    {
        return adapter;
    }

}
