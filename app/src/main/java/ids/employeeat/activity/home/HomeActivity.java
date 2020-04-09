package ids.employeeat.activity.home;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import helper.logger.LogUtil;
import helper.utils.android.FragmentUtil;
import helper.utils.android.ResourceUtil;
import ids.employeeat.AppConstant;
import ids.employeeat.AppPreferences;
import ids.employeeat.R;
import ids.employeeat.activity.AppActivity;
import ids.employeeat.data.database.menu.MenuInfo;
import ids.employeeat.helper.MyLocation;
import ids.employeeat.interfaces.home.HomeInterface;
import ids.employeeat.menu.reside.menu.Reside;
import ids.employeeat.ui.detail.menu.MenuViewModel;

@SuppressWarnings("all")
public class HomeActivity extends AppActivity implements HomeInterface, Reside.MenuListener, NavigationView.OnNavigationItemSelectedListener {
    private AppPreferences preferences;
    private Reside reside;
    private TextView tvHeaderTitle;
    private ImageButton btnLeftMenu, btnRightMenu;
    private ImageView ivHeaderBg;
    private TextClock tcTime;
    private MenuViewModel menuViewModel;

    private MyLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    private void init()
    {
        initView();
        initMenu();
    }

    public void initLocation()
    {
        location = new MyLocation(this);
        location.onResume();
    }


    public void unInitLocationClient()
    {
        if (location != null)
        {
            location.onPause();
            location = null;
        }
    }


    private void initView()
    {
        this.tvHeaderTitle = findViewById(R.id.tv_header_title);
        this.btnLeftMenu = findViewById(R.id.btn_left_menu);
        this.btnRightMenu = findViewById(R.id.btn_right_menu);
        this.ivHeaderBg = findViewById(R.id.iv_home_header_bg);
        this.tcTime = findViewById(R.id.tc_time);
    }


    /*
     * initialize menu
     * */
    private void initMenu()
    {
        menuViewModel = ViewModelProviders.of(this).get(MenuViewModel.class);
        this.reside = new Reside(this, this);
        getReside().setupMenu(R.layout.drawer_home_left_menu, R.layout.drawer_home_right_menu);

        new Notification(this, getReside().getResideMenu().getRightMenuView(),getPreferences().getPersonId());
        menuViewModel.getMenuList(AppConstant.CON_MENU_NONE_ID).observe(this, new Observer<List<MenuInfo>>() {
            @Override
            public void onChanged(@Nullable final List<MenuInfo> menuInfoList)
            {
                getReside().initRecycleView(menuInfoList);
            }
        });
        // getReside().initPersonName(getPreferences().getPersonName());
    }


    public List<MenuInfo> getMenuList(final int masterId, Observer<List<MenuInfo>> menuInfoObserver)
    {
        try
        {
            menuViewModel.getMenuList(masterId).observe(this, menuInfoObserver);
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "-->getMenuList", e);
        }
        return null;

    }

    public void reloadMenuInfo(int masterId, List<MenuInfo> menuInfoList)
    {
        TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.menu_titles_icons);
        switch (masterId)
        {
            case AppConstant.CON_MENU_ATTENDANCE_ID:
                navMenuIcons = getResources().obtainTypedArray(R.array.menu_attendance_titles_icons);
                break;
            case AppConstant.CON_MENU_HOLIDAY_ID:
                navMenuIcons = getResources().obtainTypedArray(R.array.menu_holiday_titles_icons);
                break;
        }

        if (menuInfoList != null && menuInfoList.size() > 0)
        {
            int length = menuInfoList.size();
            for (int i = 0; i < length; i++)
            {
                MenuInfo menuInfo = menuInfoList.get(i);
                menuInfo.setIconId(navMenuIcons.getResourceId(i, -1));
            }
        }
        navMenuIcons.recycle();
    }

    /*
     * Menu Listener
     * */
    @Override
    public boolean onClick(int position, final MenuInfo menuInfo)
    {
        boolean valid = false;
        try
        {
            if (menuInfo != null)
            {
                if (menuInfo.getMasterId() == AppConstant.CON_MENU_NONE_ID)
                {
                    getMenuList(menuInfo.getId(), new Observer<List<MenuInfo>>() {
                        @Override
                        public void onChanged(List<MenuInfo> menuInfoList)
                        {
                            reloadMenuInfo(menuInfo.getMasterId(), menuInfoList);
                            processMenu(menuInfo, menuInfoList);
                        }
                    });
                }
                else
                {
                    processMenu(menuInfo, null);
                }
                valid = true;
            }
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "-->openMenu", e);
        }
        return valid;
    }

    private void processMenu(MenuInfo masterMenu, List<MenuInfo> menuInfoList)
    {
        try
        {
            if (menuInfoList != null && menuInfoList.size() > 0)
            {
                getReside().setModuleHeader(masterMenu.getName(), R.drawable.ic_menu_back);
                getReside().getAdapter().updateMenuList(menuInfoList);
            }
            else
            {
                String modulePath = AppConstant.PACKAGE_NAME + ".fragment.menu.modules.";
                String className = masterMenu.getClassName();

                String packageName = modulePath;
                if (masterMenu.getMasterId() == AppConstant.CON_MENU_ATTENDANCE_ID)
                {
                    packageName = packageName.concat("attendance").concat(".");
                }
                else if (masterMenu.getMasterId() == AppConstant.CON_MENU_LEAVES_ID)
                {
                    packageName = packageName.concat("leaves").concat(".");
                }
                else if (masterMenu.getMasterId() == AppConstant.CON_MENU_HOLIDAY_ID)
                {
                    packageName = packageName.concat("holiday").concat(".");
                }
                else if (masterMenu.getMasterId() == AppConstant.CON_MENU_DEVICE_ID)
                {
                    packageName = packageName.concat("device").concat(".");
                }
                else if (masterMenu.getMasterId() == AppConstant.CON_MENU_USER_MANAGE_ID)
                {
                    packageName = packageName.concat("usermanage").concat(".");
                }
                packageName = packageName.concat(className.substring(0, 1).toLowerCase().concat(className.substring(1, className.length())));

                String classPath = packageName.concat(".").concat(className + "Fragment");
                FragmentUtil.loadFragment(getSupportFragmentManager(), Class.forName(classPath), null, false, R.id.menu_view_container);
                if (getReside() != null)
                {
                    getReside().closeMenu();
                }
            }
            if (masterMenu.getId() == AppConstant.CON_MENU_DASHBOARD_ID || masterMenu.getId() == AppConstant.CON_MENU_ATTENDANCE_MANUAL_ENTRY
                    || masterMenu.getId() == AppConstant.CON_MENU_OUTDOOR_ENTRY || masterMenu.getId() == AppConstant.CON_MENU_LEAVE_ENTRY
                    || masterMenu.getId() == AppConstant.CON_MENU_COMP_OFF_ENTRY || masterMenu.getId() == AppConstant.CON_MENU_USER_MANAGE_ID
                    || masterMenu.getId() == AppConstant.CON_MENU_DEVICE_BLUETOOTH_ID  || masterMenu.getId() == AppConstant.CON_MENU_DEVICE_USER_BIND_ID || masterMenu.getId() == AppConstant.CON_MENU_DEVICE_WIFI_ID
                    || masterMenu.getId() == AppConstant.CON_MENU_DEVICE_ID
            )
            {
                setViewDashboard();
            }
            else
            {
                setViewWhiteBg();
            }
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "-->processMenu", e);
        }
    }


    private void setViewWhiteBg()
    {
        this.btnLeftMenu.setImageResource(R.drawable.ic_menu_left_drawer_gray);
        this.btnRightMenu.setImageResource(R.drawable.ic_menu_notification_gray);
        this.tcTime.setTextColor(ResourceUtil.getColor(this, R.color.dark_gray));
        this.tvHeaderTitle.setTextColor(ResourceUtil.getColor(this, R.color.dark_gray));
        this.ivHeaderBg.setVisibility(View.GONE);
    }

    private void setViewDashboard()
    {
        this.btnLeftMenu.setImageResource(R.drawable.ic_menu_left_drawer);
        this.btnRightMenu.setImageResource(R.drawable.ic_menu_notification);
        this.tcTime.setTextColor(ResourceUtil.getColor(this, R.color.white));
        this.tvHeaderTitle.setTextColor(ResourceUtil.getColor(this, R.color.white));
        this.ivHeaderBg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onModuleHeaderClick()
    {
        getMenuList(AppConstant.CON_MENU_NONE_ID, new Observer<List<MenuInfo>>() {
            @Override
            public void onChanged(List<MenuInfo> menuInfoList)
            {
                reloadMenuInfo(AppConstant.CON_MENU_NONE_ID, menuInfoList);
                getReside().getAdapter().updateMenuList(menuInfoList);
                getReside().setModuleHeader(HomeActivity.this.getString(R.string.home), 0);
            }
        });
    }

    /*
     * Menu Listener
     * */


    /*
     * Home Interface
     * */
    @Override
    public void headerTitle(String title)
    {
        this.tvHeaderTitle.setText(title);
    }


    @Override
    public Reside getReside()
    {
        return this.reside;
    }

    @Override
    public MyLocation getLocation()
    {
        return location;
    }


    /*
     * Home Interface
     * */

    /*
     * Click
     * */

    public void rightClick(View view)
    {
        getReside().openRight();
    }

    public void leftMenu(View view)
    {
        getReside().openLeft();
    }

    private AppPreferences getPreferences()
    {
        if (this.preferences == null)
        {
            this.preferences = new AppPreferences(this);
        }
        return this.preferences;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initLocation();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unInitLocationClient();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        return false;
    }
}
