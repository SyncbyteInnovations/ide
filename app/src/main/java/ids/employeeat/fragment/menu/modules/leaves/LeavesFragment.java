package ids.employeeat.fragment.menu.modules.leaves;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ids.employeeat.AppConstant;
import ids.employeeat.R;
import ids.employeeat.Theme;
import ids.employeeat.fragment.AppFragment;
import ids.employeeat.fragment.menu.MenuFragment;


public class LeavesFragment extends MenuFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int layoutId = Theme.getLayout(AppConstant.CON_MENU_LEAVES_ID, getPreferences().getTheme());
        View view = inflater.inflate(layoutId, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        setHeaderTitle(R.string.leaves);
    }

}
