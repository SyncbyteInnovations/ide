package ids.employeeat.fragment.menu.modules.attendance.shiftRoaster;

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
import helper.utils.java.DateUtil;
import ids.employeeat.AppConstant;
import ids.employeeat.R;
import ids.employeeat.Theme;
import ids.employeeat.fragment.menu.MenuFragment;
import ids.employeeat.menu.reside.filter.FilterMenu;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;
import ids.employeeat.ui.list.attendance.shiftRoaster.ShiftRoasterAdapter;


@SuppressWarnings("unused")
public class ShiftRoasterFragment extends MenuFragment implements ServerComm.ServerResponseListener, FilterMenu.FilterMenuListener {
    private ShiftRoasterAdapter adapter;
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
                    FilterMenu filterMenu = new FilterMenu(getActivity(), getReside(), ShiftRoasterFragment.this);
                    filterMenu.showDateYMFilter();
                }
            });
        }
    }

    private void initRecycleView(View view)
    {
        RecyclerView rvList = view.findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = new ShiftRoasterAdapter(getContext());
        rvList.setAdapter(getAdapter());
        getShiftInfo(FilterMenu.getDefaultYMArray());
    }

    @Override
    public void filterData(JSONObject filterArray)
    {
        getShiftInfo(filterArray);
        getReside().closeMenu();
    }

    private void getShiftInfo(JSONObject filterData)
    {
        getServerDataWithPersonId(this, ServerConstant.GET_SHIFT_INFO_LIST, filterData);
    }


    private ShiftRoasterAdapter getAdapter()
    {
        return adapter;
    }

    @Override
    public void responseSuccess(int processType, JSONArray requestData, JSONArray responseData)
    {
        JSONArray adapterList = new JSONArray();
        try
        {
            JSONObject filterData = requestData.getJSONObject(0);
            long filterFromDate = filterData.getLong("fromDate");
            long filterToDate = filterData.getLong("toDate");

            int length = responseData.length();
            while (filterFromDate <= filterToDate)
            {
                JSONObject adapterData = new JSONObject();
                for (int index = 0; index < length; index++)
                {

                    JSONObject jData = responseData.getJSONObject(index);
                    String weekDay = jData.getString("weekDay");
                    long fromDate = jData.getLong("fromDate");
                    long toDate = jData.getLong("toDate");
                    if (toDate <= 0 || (toDate >= filterFromDate && toDate <= filterToDate))
                    {
                        String currentWeekDay = DateUtil.getDate(filterFromDate, DateUtil.DATE_FORMAT_DAY_STRING).toUpperCase();
                        if (weekDay.contains(currentWeekDay))
                        {
                            adapterData.put("date", filterFromDate);
                            adapterData.put("shift", jData.getString("shiftName"));
                            adapterData.put("weekDay", jData.getString("weekDay"));
                            break;
                        }
                    }
                }
                filterFromDate = filterFromDate + DateUtil.ONE_DAY;
                adapterList.put(adapterData);
            }
            getAdapter().setAdapterList(adapterList);
        } catch (Exception e)
        {
            LogUtil.logException("responseSuccess", e);
        }
    }

    @Override
    public void responseFail(int processType, JSONArray requestData, int statusCode)
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        setHeaderTitle(R.string.shift_roaster);
        this.ivFilterMenu.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        this.ivFilterMenu.setVisibility(View.INVISIBLE);
    }

}
