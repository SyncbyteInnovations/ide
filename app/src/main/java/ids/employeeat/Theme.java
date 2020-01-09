package ids.employeeat;

public class Theme
{
    public static int getLayout(int menuId, int themeId)
    {
        int layoutId = R.layout.fragment_dashboard;
        switch (themeId)
        {
            default:
                switch (menuId)
                {
                    case AppConstant.CON_MENU_DASHBOARD_ID:
                        layoutId = R.layout.fragment_dashboard;
                        break;
                    case AppConstant.CON_MENU_LEAVES_ID:
                        layoutId = R.layout.fragment_leaves;
                        break;
                    case AppConstant.CON_MENU_HOLIDAY_ID:
                        layoutId = R.layout.fragment_holiday;
                        break;
                    case AppConstant.CON_MENU_ATTENDANCE_ID:
                        layoutId = R.layout.fragment_attendance_info;
                        break;
                    case AppConstant.CON_MENU_PUNCH_ID:
                        layoutId = R.layout.fragment_punch;
                }
        }
        return layoutId;
    }
}
