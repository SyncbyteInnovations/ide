package ids.employeeat.fragment.menu.modules.leaves.compOffEntry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import helper.logger.LogUtil;
import helper.utils.java.DateUtil;
import ids.employeeat.AppConstant;
import ids.employeeat.JsonTag;
import ids.employeeat.R;
import ids.employeeat.filter.DateFilter;
import ids.employeeat.fragment.menu.MenuFragment;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;

@SuppressWarnings("unused")
public class CompOffEntryFragment extends MenuFragment implements View.OnClickListener, ServerComm.ServerResponseListener, DateFilter.DateFilterListener
{
    private Spinner sLeaveType, sLeaveDuration, sLeaveAssignment, sSanctioner, sAuthoriser;
    private EditText etRemarks;
    private TextView tvFromDate, tvToDate;

    //Default Current Date
    private long fromDate = DateUtil.getUTCStartOfDay(System.currentTimeMillis());
    private long toDate = DateUtil.getUTCEndOfDay(System.currentTimeMillis());

    private JSONObject leaveType;
    private JSONObject leaveDuration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_leave_or_comp_off_entry, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        initView(view);
        setHeaderTitle(R.string.apply_comp_off);
        serverRequest();
    }

    private void serverRequest()
    {
        try
        {
            JSONObject leaveTypeFilter = new JSONObject();
            leaveTypeFilter.put(AppConstant.TAG_COMP_OFF, true);
            getServerDataWithPersonId(this, ServerConstant.GET_LEAVE_TYPE_LIST, leaveTypeFilter);
            getServerData(this, ServerConstant.GET_LEAVE_DURATION_LIST, null);
        }
        catch (Exception e)
        {
            LogUtil.logException(TAG + "-->serverRequest", e);
        }

    }

    private void initView(View view)
    {
        this.sLeaveDuration = view.findViewById(R.id.s_leave_duration);
        this.sLeaveType = view.findViewById(R.id.s_leave_type);
        this.etRemarks = view.findViewById(R.id.et_remarks);

        this.tvFromDate = view.findViewById(R.id.tv_from_date);
        this.tvFromDate.setOnClickListener(this);
        this.tvFromDate.setText(DateUtil.getUTCDate(fromDate, DateUtil.DATE_FORMAT_DATE));

        this.tvToDate = view.findViewById(R.id.tv_to_date);
        this.tvToDate.setText(DateUtil.getUTCDate(this.toDate, DateUtil.DATE_FORMAT_DATE));
        this.tvToDate.setOnClickListener(this);

        Button btnApply = view.findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_from_date:
                fromDate();
                break;
            case R.id.tv_to_date:
                toDate();
                break;
            case R.id.btn_apply:
                apply();
                break;
        }
    }

    private void fromDate()
    {
        this.tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DateFilter.selectYMD(getActivity(), CompOffEntryFragment.this, false);
            }
        });
    }

    private void toDate()
    {
        this.tvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DateFilter.selectYMD(getActivity(), CompOffEntryFragment.this, true);
            }
        });
    }

    private void apply()
    {
        try
        {
            String leaveReason = etRemarks.getText().toString().trim();
            if (leaveReason.length() <= 0)
            {
                printMsg(R.string.fields_mandatory);
            }
            else
            {
                JSONObject person = new JSONObject();
                person.put("id", getPersonId());

                JSONObject leaveTransactionInfo = new JSONObject();
                leaveTransactionInfo.put(JsonTag.PERSON, person);
                leaveTransactionInfo.put("leaveType", leaveType);
                leaveTransactionInfo.put("leaveDuration", leaveDuration);
                leaveTransactionInfo.put("leaveReason", leaveReason);
                leaveTransactionInfo.put("fromDate", fromDate);
                leaveTransactionInfo.put("toDate", toDate);

                ServerComm.requestPost(getContext(), ServerConstant.APPLY_COMP_OFF, CompOffEntryFragment.this, leaveTransactionInfo);
            }
        }
        catch (Exception e)
        {
            LogUtil.logException(TAG + "apply", e);
        }
    }

    @Override
    public void dateFilterInfo(long fromDate, long toDate)
    {
        if (fromDate > 0)
        {
            this.fromDate = fromDate;
            tvFromDate.setText(DateUtil.getUTCDate(fromDate, DateUtil.DATE_FORMAT_DATE));
        }
        if (toDate > 0)
        {
            this.toDate = toDate;
            tvToDate.setText(DateUtil.getUTCDate(toDate, DateUtil.DATE_FORMAT_DATE));
        }
    }

    @Override
    public void responseSuccess(int processType, JSONArray requestData, JSONArray responseData)
    {
        switch (processType)
        {
            case ServerConstant.GET_LEAVE_TYPE_LIST:
                processLeaveTypeInfo(responseData);
                break;
            case ServerConstant.GET_LEAVE_DURATION_LIST:
                processLeaveDurationInfo(responseData);
                break;
            case ServerConstant.GET_AUTHORISER_LIST:
                processAuthoriserInfo(responseData);
                break;
            case ServerConstant.GET_SANCTIONER_LIST:
                processSanctionerInfo(responseData);
                break;
        }

    }

    private void processSanctionerInfo(final JSONArray responseData)
    {
        try
        {
            if (getsSanctioner() != null && getActivity() != null && responseData != null && responseData.length() > 0)
            {
                int length = responseData.length();
                List<String> list = new ArrayList<>();
                for (int index = 0; index < length; index++)
                {
                    list.add(responseData.getJSONObject(index).getString(JsonTag.NAME));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getsSanctioner().setAdapter(adapter);
                getsSanctioner().setSelection(0);
                getsSanctioner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        try
                        {
                            JSONObject sanctionBy = new JSONObject();
                            sanctionBy.put(JsonTag.ID, responseData.getJSONObject(position).get(JsonTag.ID));
                          //  leaveInfo.put(JsonTag.SANCTION_BY, sanctionBy);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });
            }
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "responseSuccess", e);
        }
    }

    private void processAuthoriserInfo(final JSONArray responseData)
    {
        try
        {
            if (getAuthoriser() != null && getActivity() != null && responseData != null && responseData.length() > 0)
            {
                LogUtil.logInfo(responseData.toString());
                int length = responseData.length();
                List<String> list = new ArrayList<>();
                for (int index = 0; index < length; index++)
                {
                    list.add(responseData.getJSONObject(index).getString(JsonTag.NAME));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getAuthoriser().setAdapter(adapter);
                getAuthoriser().setSelection(0);
                getAuthoriser().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        try
                        {
                            JSONObject authoriseBy = new JSONObject();
                            authoriseBy.put(JsonTag.ID, responseData.getJSONObject(position).get(JsonTag.ID));
                           // leaveInfo.put(JsonTag.AUTHORISE_BY, authoriseBy);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });
            }
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "responseSuccess", e);
        }
    }

    @Override
    public void responseFail(int processType, JSONArray requestData, int statusCode)
    {

    }

    private void processLeaveDurationInfo(final JSONArray responseData)
    {
        try
        {
            if (getLeaveDurationSpinner() != null && getActivity() != null && responseData != null && responseData.length() > 0)
            {
                leaveDuration = responseData.getJSONObject(0);
                int length = responseData.length();
                List<String> list = new ArrayList<>();
                for (int index = 0; index < length; index++)
                {
                    list.add(responseData.getJSONObject(index).getString(JsonTag.NAME));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getLeaveDurationSpinner().setAdapter(adapter);
                getLeaveDurationSpinner().setSelection(0);
                getLeaveDurationSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        try
                        {
                            leaveDuration = responseData.getJSONObject(position);
                        }
                        catch (Exception e)
                        {
                            LogUtil.logException(TAG + "-->processLeaveDurationInfo", e);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });
            }
        }
        catch (Exception e)
        {
            LogUtil.logException(TAG + "responseSuccess", e);
        }
    }

    private void processLeaveTypeInfo(final JSONArray responseData)
    {
        try
        {
            if (getLeaveTypeSpinner() != null && getActivity() != null && responseData != null && responseData.length() > 0)
            {
                leaveType = responseData.getJSONObject(0);
                int length = responseData.length();
                List<String> list = new ArrayList<>();
                for (int index = 0; index < length; index++)
                {
                    list.add(responseData.getJSONObject(index).getString(JsonTag.NAME));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getLeaveTypeSpinner().setSelection(0);
                getLeaveTypeSpinner().setEnabled(false);
                getLeaveTypeSpinner().setAdapter(adapter);
                getLeaveTypeSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        try
                        {
                            leaveType = responseData.getJSONObject(position);
                        }
                        catch (Exception e)
                        {
                            LogUtil.logException(TAG + "-->processLeaveTypeInfo", e);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });
            }
        }
        catch (Exception e)
        {
            LogUtil.logException(TAG + "responseSuccess", e);
        }
    }

    private Spinner getsSanctioner()
    {
        return sSanctioner;
    }

    private Spinner getAuthoriser()
    {
        return sAuthoriser;
    }


    private Spinner getLeaveTypeSpinner()
    {
        return this.sLeaveType;
    }

    private Spinner getLeaveDurationSpinner()
    {
        return this.sLeaveDuration;
    }

}
