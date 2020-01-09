package ids.employeeat;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences
{
    private static final String PREF_NAME = "EmployeeAt";
    private static final String COPY_DB_FROM_ASSET = "CopyDbFromAsset";
    private static final String SERVER_URL = "serverURL";
    private static final String THEME = "theme";
    private static final String USER_NAME = "userName";
    private static final String PERSON_ID = "personId";
    private static final String PWD = "pwd";


    private SharedPreferences sharedPreferences;

    public AppPreferences(Context context)
    {
        setSharedPreferences(context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE));
    }

    public boolean getCopyDbFromAsset()
    {
        return getSharedPreferences().getBoolean(COPY_DB_FROM_ASSET, true);
    }

    public void setCopyDbFromAsset(boolean isUserDefault)
    {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(COPY_DB_FROM_ASSET, isUserDefault);
        editor.apply();
    }

    public int getTheme()
    {
        return getSharedPreferences().getInt(THEME, 1);
    }

    public void setTheme(int theme)
    {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(THEME, theme);
        editor.apply();
    }

    public String getServerURL()
    {
        return getSharedPreferences().getString(SERVER_URL, "http://192.168.0.106:8080/");
    }

    public void setServerURL(String info)
    {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(SERVER_URL, info);
        editor.apply();
    }

    public String getPwd()
    {
        return getSharedPreferences().getString(PWD, null);
    }

    public void setPwd(String info)
    {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(PWD, info);
        editor.apply();
    }

    public String getUserName()
    {
        return getSharedPreferences().getString(USER_NAME, null);
    }

    public void setUserName(String info)
    {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(USER_NAME, info);
        editor.apply();
    }

    public long getPersonId()
    {
        return getSharedPreferences().getLong(PERSON_ID, 1821L);
    }

    public void setPersonId(long info)
    {
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(PERSON_ID, info);
        editor.apply();
    }

    public SharedPreferences.Editor getEditor()
    {
        return getSharedPreferences().edit();
    }

    private SharedPreferences getSharedPreferences()
    {
        return sharedPreferences;
    }

    private void setSharedPreferences(SharedPreferences sharedPreferences)
    {
        this.sharedPreferences = sharedPreferences;
    }



}
