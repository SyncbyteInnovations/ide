package ids.employeeat.fragment.menu.modules.dashboard.summary.attendance;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ids.employeeat.R;
import ids.employeeat.fragment.menu.modules.dashboard.summary.SummaryFragment;

public class AttendanceSummary extends SummaryFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dashboard_summary_attendance, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {

    }
}
