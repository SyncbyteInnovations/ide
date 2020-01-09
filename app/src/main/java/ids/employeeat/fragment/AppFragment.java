package ids.employeeat.fragment;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.TimeZone;

import ids.employeeat.AppConstant;
import ids.employeeat.AppPreferences;
import ids.employeeat.JsonTag;
import ids.employeeat.helper.MyLocation;
import ids.employeeat.interfaces.home.HomeInterface;
import ids.employeeat.menu.reside.menu.Reside;

public class AppFragment extends Fragment
{
    protected final String TAG = getClass().getName();
    private HomeInterface homeInterface;
    private AppPreferences appPreferences;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.homeInterface = (HomeInterface) context;
        this.appPreferences = new AppPreferences(getContext());
    }

    protected void setHeaderTitle(int titleId)
    {
        getHomeInterface().headerTitle(getString(titleId));
    }

    protected MyLocation getLocation()
    {
        return getHomeInterface().getLocation();
    }

    protected Reside getReside()
    {
        return getHomeInterface().getReside();
    }

    protected long getPersonId()
    {
        return getPreferences().getPersonId();
    }

    protected AppPreferences getPreferences()
    {
        return this.appPreferences;
    }

    private HomeInterface getHomeInterface()
    {
        return this.homeInterface;
    }

    protected void printMsg(int msg)
    {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void printMsg(String msg)
    {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected   JSONObject getLogObject(String description, int punchStatusId, long logDate, int isManual) throws Exception
    {
        JSONObject deviceLogInfo = new JSONObject();

        JSONObject person = new JSONObject();
        person.put(JsonTag.ID, getPersonId());

        JSONObject verificationMode = new JSONObject();
        verificationMode.put(JsonTag.ID, AppConstant.CON_VERIFICATION_MODE_MANUAL_ENTRY);

        JSONObject punchStatus = new JSONObject();
        punchStatus.put(JsonTag.ID, punchStatusId);

        deviceLogInfo.put(JsonTag.DESCRIPTION, description);
        deviceLogInfo.put(JsonTag.IS_MANUAL, isManual);
        deviceLogInfo.put(JsonTag.VERIFICATION_MODE, verificationMode);
        deviceLogInfo.put(JsonTag.LAT, getLocation().getLat());
        deviceLogInfo.put(JsonTag.LON, getLocation().getLon());
        deviceLogInfo.put(JsonTag.PUNCH_STATUS, punchStatus);
        deviceLogInfo.put(JsonTag.TIME_ZONE_OFFSET, TimeZone.getDefault().getRawOffset());
        deviceLogInfo.put(JsonTag.TIME_ZONE_NAME, TimeZone.getDefault().getID());
        deviceLogInfo.put(JsonTag.LOG_DATE, logDate);
        deviceLogInfo.put(JsonTag.PERSON, person);

        return deviceLogInfo;
    }
}
