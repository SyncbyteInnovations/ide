package ids.employeeat.fragment.menu.modules.leaves.leaveBalance;

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
import ids.employeeat.JsonTag;
import ids.employeeat.R;
import ids.employeeat.fragment.menu.MenuFragment;
import ids.employeeat.menu.reside.filter.FilterMenu;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;
import ids.employeeat.ui.list.leaves.leaveBalance.LeaveBalanceAdapter;

@SuppressWarnings("unused")
public class LeaveBalanceFragment extends MenuFragment implements ServerComm.ServerResponseListener, FilterMenu.FilterMenuListener {

    private LeaveBalanceAdapter adapter;
    private ImageView ivFilterMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_leave_balance, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        setHeaderTitle(R.string.leave_balance);
        initActivityView();
        initRecycleView(view);
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
                    FilterMenu filterMenu = new FilterMenu(getActivity(), getReside(), LeaveBalanceFragment.this);
                    filterMenu.showYearAssignmentFilter(getPersonId());
                }
            });
        }
    }

    private void initRecycleView(View view)
    {
        RecyclerView rvList = view.findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = new LeaveBalanceAdapter(getContext());
        rvList.setAdapter(getAdapter());
    }

    @Override
    public void filterData(JSONObject filterArray)
    {
        try
        {
            if (filterArray.has(JsonTag.YEAR_ASSIGNMENT))
            {
                JSONObject yearData = filterArray.getJSONArray(JsonTag.YEAR_ASSIGNMENT).getJSONObject(0);
                yearData.put(JsonTag.YEAR_ASSIGNMENT_ID, yearData.get(JsonTag.ID));
                yearData.remove(JsonTag.ID);
                getServerDataWithPersonId(this, ServerConstant.GET_LEAVE_BALANCE_LIST, yearData);
            }
        } catch (Exception e)
        {
            LogUtil.logException(getClass(), "filterData", e);
        }
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

    private LeaveBalanceAdapter getAdapter()
    {
        return adapter;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setHeaderTitle(R.string.attendance);
        this.ivFilterMenu.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        this.ivFilterMenu.setVisibility(View.INVISIBLE);
    }

}
