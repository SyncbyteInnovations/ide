package ids.employeeat.menu.reside.menu;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import helper.reside.ResideMenu;
import helper.utils.android.RecycleViewUtil;
import helper.utils.android.ResourceUtil;
import ids.employeeat.AppPreferences;
import ids.employeeat.R;
import ids.employeeat.activity.login.LoginActivity;
import ids.employeeat.data.database.menu.MenuInfo;
import ids.employeeat.ui.list.menu.MenuAdapter;

@SuppressWarnings("all")
public class Reside {
    private Activity activity;
    private ResideMenu resideMenu;
    private MenuListener menuListener;
    private MenuAdapter adapter;
    private TextView tvMenuModuleHeader;

    public Reside(Activity activity, MenuListener menuListener)
    {
        this.activity = activity;
        this.menuListener = menuListener;

    }

    public void setupMenu(int leftLayoutId, int rightLayoutId)
    {
        initResideMenu(leftLayoutId, rightLayoutId);
        initMenuModuleHeader();
    }

    public void updateRightView(int viewId)
    {
        getResideMenu().updateRightMenuView(viewId);
        openRight();
    }


    public void updateRightView(int viewId, int innerViewId)
    {
        getResideMenu().updateRightMenuView(viewId, innerViewId);
        openRight();
    }


    private void initMenuModuleHeader()
    {
        this.tvMenuModuleHeader = getResideMenu().getLeftMenuView().findViewById(R.id.tv_menu_module_header);
        setModuleHeader(getActivity().getString(R.string.home), 0);
        tvMenuModuleHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getMenuListener().onModuleHeaderClick();
            }
        });
    }

    private void initResideMenu(int leftLayoutId, int rightLayoutId)
    {
        resideMenu = new ResideMenu(getActivity(), leftLayoutId, rightLayoutId);
                initLogout();
        getResideMenu().attachToActivity();
        getResideMenu().setBackgroundColor(ResourceUtil.getColor(activity, R.color.resideMenuBg));
        getResideMenu().openMenu(ResideMenu.DIRECTION_LEFT);
        getResideMenu().setShadowVisible(false);
    }


    public void initPersonName(String personName)
    {
        TextView tvPersonName = getResideMenu().getLeftMenuView().findViewById(R.id.tv_person_name);
        tvPersonName.setText(personName);
    }

    private void initLogout()
    {
        ImageButton btnLogout = getResideMenu().getLeftMenuView().findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AppPreferences preferences = new AppPreferences(getActivity());
                preferences.setUserName(null);
                preferences.setPwd(null);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }

    public void initRecycleView(List<MenuInfo> menuInfoList)
    {

        RecyclerView recyclerView = getResideMenu().getLeftMenuView().findViewById(R.id.rv_left_menu);
        this.adapter = new MenuAdapter(getActivity(), menuInfoList);
        recyclerView.setAdapter(getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecycleViewUtil(getActivity(), recyclerView, new RecycleViewUtil.ClickListener() {
            @Override
            public void onClick(View v, int position)
            {
                getMenuListener().onClick(position, adapter.getData(position));
            }

            @Override
            public void onLongClick(View v, final int position)
            {
            }
        }));
        getMenuListener().onClick(0, adapter.getData(0));
    }

    public void setModuleHeader(String masterName, int backIconId)
    {
        getMenuModuleHeader().setCompoundDrawablesWithIntrinsicBounds(backIconId, 0, 0, 0);
        getMenuModuleHeader().setEnabled(backIconId != 0);
        getMenuModuleHeader().setText(masterName);
    }

    public void openLeft()
    {
        getResideMenu().openMenu(ResideMenu.DIRECTION_LEFT);
    }

    public void openRight()
    {
        getResideMenu().openMenu(ResideMenu.DIRECTION_RIGHT);
    }

    public void openRight(int ViewId)
    {
        getResideMenu().openMenu(ResideMenu.DIRECTION_RIGHT);
    }

    public void closeMenu()
    {
        getResideMenu().closeMenu();
    }

    private Activity getActivity()
    {
        return this.activity;
    }

    public ResideMenu getResideMenu()
    {
        return this.resideMenu;
    }

    private MenuListener getMenuListener()
    {
        return this.menuListener;
    }

    public View getRightView()
    {
        return getResideMenu().getRightMenuView();
    }

    private TextView getMenuModuleHeader()
    {
        return this.tvMenuModuleHeader;
    }

    public MenuAdapter getAdapter()
    {
        return this.adapter;
    }

    public interface MenuListener {
        boolean onClick(int position, MenuInfo menuInfo);

        void onModuleHeaderClick();
    }



}
