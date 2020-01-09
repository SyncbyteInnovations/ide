package ids.employeeat.fragment.menu.modules.attendance.attendanceInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import ids.employeeat.AppConstant;
import ids.employeeat.R;
import ids.employeeat.Theme;
import ids.employeeat.filter.DateFilter;
import ids.employeeat.fragment.menu.MenuFragment;
import ids.employeeat.menu.reside.filter.FilterMenu;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;
import ids.employeeat.ui.list.attendance.attendanceInfo.AttendanceInfoAdapter;


@SuppressWarnings("unused")
public class AttendanceInfoFragment extends MenuFragment implements ServerComm.ServerResponseListener, DateFilter.DateFilterListener, FilterMenu.FilterMenuListener {
    private AttendanceInfoAdapter adapter;
    private ImageButton btnDateFilter;
    private ImageView ivFilterMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int layoutId = Theme.getLayout(AppConstant.CON_MENU_ATTENDANCE_ID, getPreferences().getTheme());
        View view = inflater.inflate(layoutId, container, false);
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
                    FilterMenu filterMenu = new FilterMenu(getActivity(), getReside(), AttendanceInfoFragment.this);
                    filterMenu.showDateFilter();
                }
            });
            this.btnDateFilter = getActivity().findViewById(R.id.btn_filter_date);
            this.btnDateFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    DateFilter.selectYMDRange(getActivity(), AttendanceInfoFragment.this);
                }
            });
        }

    }

    private void initRecycleView(View view)
    {
        RecyclerView rvList = view.findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = new AttendanceInfoAdapter(getContext());
        rvList.setAdapter(getAdapter());
        getAttendanceInfo(FilterMenu.getDefaultYMDArray());
    }


    @Override
    public void filterData(JSONObject filterArray)
    {
        getAttendanceInfo(filterArray);
        getReside().closeMenu();
    }

    private void getAttendanceInfo(JSONObject filterData)
    {
        getServerDataWithPersonId(this, ServerConstant.GET_ATTENDANCE_INFO_LIST, filterData);
    }



    private AttendanceInfoAdapter getAdapter()
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
        setHeaderTitle(R.string.attendance);
        this.btnDateFilter.setVisibility(View.VISIBLE);
        this.ivFilterMenu.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        this.btnDateFilter.setVisibility(View.INVISIBLE);
        this.ivFilterMenu.setVisibility(View.INVISIBLE);
    }

}
