package ids.employeeat.fragment.menu.modules.dashboard.summary.leaves;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ids.employeeat.AppConstant;
import ids.employeeat.R;
import ids.employeeat.Theme;
import ids.employeeat.fragment.menu.modules.dashboard.summary.SummaryFragment;

public class LeavesSummary extends SummaryFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dashboard_summary_leaves, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {

    }
}
