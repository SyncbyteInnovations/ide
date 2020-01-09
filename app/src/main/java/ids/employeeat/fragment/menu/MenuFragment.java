package ids.employeeat.fragment.menu;

import org.json.JSONObject;

import helper.logger.LogUtil;
import ids.employeeat.AppConstant;
import ids.employeeat.fragment.AppFragment;
import ids.employeeat.server.ServerComm;


public class MenuFragment extends AppFragment
{
    protected void getServerDataWithPersonId(ServerComm.ServerResponseListener listener, int processType, JSONObject filterData)
    {
        try
        {
            if (filterData == null)
            {
                filterData = new JSONObject();
            }
            filterData.put(AppConstant.TAG_PERSON_ID, getPersonId());
            ServerComm.requestPost(getContext(), processType, listener, filterData);
        }
        catch (Exception e)
        {
            LogUtil.logException(TAG + "-->getServerDataWithPersonId", e);
        }
    }


    protected void getServerData(ServerComm.ServerResponseListener listener, int processType, JSONObject filterData)
    {
        try
        {
            ServerComm.requestPost(getContext(), processType, listener, filterData);
        }
        catch (Exception e)
        {
            LogUtil.logException(TAG + "-->getServerData", e);
        }
    }
}
