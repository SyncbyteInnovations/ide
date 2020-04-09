package ids.employeeat.fragment.menu.modules.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ids.employeeat.AppConstant;
import ids.employeeat.R;
import ids.employeeat.Theme;
import ids.employeeat.fragment.AppFragment;

public class DeviceFragment extends AppFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int layoutId = Theme.getLayout(AppConstant.CON_MENU_DEVICE_ID, getPreferences().getTheme());
        View view = inflater.inflate(layoutId, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        setHeaderTitle(R.string.holiday);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setHeaderTitle(R.string.holiday);
    }
}
