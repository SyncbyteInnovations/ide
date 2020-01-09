package ids.employeeat.fragment.menu.modules.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import helper.utils.android.FragmentUtil;
import ids.employeeat.AppConstant;
import ids.employeeat.R;
import ids.employeeat.Theme;
import ids.employeeat.fragment.AppFragment;
import ids.employeeat.fragment.menu.modules.dashboard.summary.attendance.AttendanceSummary;
import ids.employeeat.fragment.menu.modules.dashboard.summary.leaves.LeavesSummary;
import ids.employeeat.fragment.menu.modules.punch.PunchFragment;

@SuppressWarnings("unused")
public class DashboardFragment extends AppFragment
{
    private TextView tvAttendanceSummary, tvLeavesSummary, tvOnDutySummary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int layoutId = Theme.getLayout(AppConstant.CON_MENU_DASHBOARD_ID, getPreferences().getTheme());
        View view = inflater.inflate(layoutId, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        initTextView(view);
    }

    private void initTextView(View view)
    {
        tvAttendanceSummary = view.findViewById(R.id.tv_attendance_summary);
        tvAttendanceSummary.setOnClickListener(summaryClickListener);
        tvLeavesSummary = view.findViewById(R.id.tv_leaves_summary);
        tvLeavesSummary.setOnClickListener(summaryClickListener);
        FragmentUtil.loadFragment(getFragmentManager(), AttendanceSummary.class, null, false, R.id.menu_summary);

        TextView tvClockIn = view.findViewById(R.id.tv_clock_in);
        TextView tvClockOut = view.findViewById(R.id.tv_clock_out);

        tvClockIn.setOnClickListener(checkIn);
        tvClockOut.setOnClickListener(checkOut);
    }

    private View.OnClickListener checkIn= new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstant.KEY_PUNCH_STATUS, AppConstant.CON_PUNCH_STATUS_CHECK_IN);
            FragmentUtil.loadFragment(getFragmentManager(), PunchFragment.class, bundle, false, R.id.menu_summary);
        }
    };


    private View.OnClickListener checkOut= new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstant.KEY_PUNCH_STATUS, AppConstant.CON_PUNCH_STATUS_CHECK_OUT);
            FragmentUtil.loadFragment(getFragmentManager(), PunchFragment.class, bundle, false, R.id.menu_summary);
        }
    };


    private View.OnClickListener summaryClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            tvAttendanceSummary.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_line_white);
            tvLeavesSummary.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_line_white);
            TextView tv = ((TextView) v);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_line);
            Class<?> classObject = null;
            switch (v.getId())
            {
                case R.id.tv_attendance_summary:
                    classObject = AttendanceSummary.class;
                    break;
                case R.id.tv_leaves_summary:
                    classObject = LeavesSummary.class;
                    break;
            }
            FragmentUtil.loadFragment(getFragmentManager(), classObject, null, false, R.id.menu_summary);
        }
    };


    @Override
    public void onResume()
    {
        super.onResume();
        setHeaderTitle(R.string.dashboard);
    }
}
