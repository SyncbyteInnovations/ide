package ids.employeeat.fragment.menu.modules.leaves.leaveEntry;

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
import ids.employeeat.JsonTag;
import ids.employeeat.R;
import ids.employeeat.filter.DateFilter;
import ids.employeeat.fragment.menu.modules.leaves.LeavesFragment;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;

@SuppressWarnings("unused")
public class LeaveEntryFragment extends LeavesFragment implements View.OnClickListener, ServerComm.ServerResponseListener, DateFilter.DateFilterListener {
    private Spinner sLeaveType, sLeaveDuration, sLeaveAssignment, sSanctioner, sAuthoriser;
    private TextView tvFromDate, tvToDate;
    private EditText etRemarks;
    private JSONObject leaveInfo = new JSONObject();
    private JSONObject leaveAssignment;
    //Default Current Date
    private long fromDate = DateUtil.getUTCStartOfDay(System.currentTimeMillis());
    private long toDate = DateUtil.getUTCEndOfDay(System.currentTimeMillis());

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
        setHeaderTitle(R.string.apply_leave);
        serverRequest();
    }

    private void serverRequest()
    {
        try
        {
            //getServerDataWithPersonId(this, ServerConstant.GET_LEAVE_TYPE_LIST, null);
            getServerDataWithPersonId(this, ServerConstant.GET_LEAVE_ASSIGNMENT_LIST, null);
            getServerDataWithPersonId(this, ServerConstant.GET_SANCTIONER_LIST, null);
            getServerDataWithPersonId(this, ServerConstant.GET_AUTHORISER_LIST, null);
            getServerData(this, ServerConstant.GET_LEAVE_DURATION_LIST, null);
        } catch (Exception e)
        {
            LogUtil.logException(getClass(), e);
        }
    }

    private void initView(View view)
    {
        Button btnApply = view.findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(this);
        this.sLeaveDuration = view.findViewById(R.id.s_leave_duration);
        this.sLeaveAssignment = view.findViewById(R.id.s_leave_assignment);
        this.sAuthoriser = view.findViewById(R.id.s_sanction);
        this.sSanctioner = view.findViewById(R.id.s_authorise);
        this.sLeaveType = view.findViewById(R.id.s_leave_type);
        this.etRemarks = view.findViewById(R.id.et_remarks);

        this.tvFromDate = view.findViewById(R.id.tv_from_date);
        this.tvFromDate.setOnClickListener(this);
        this.tvFromDate.setText(DateUtil.getUTCDate(fromDate, DateUtil.DATE_FORMAT_DATE));

        this.tvToDate = view.findViewById(R.id.tv_to_date);
        this.tvToDate.setText(DateUtil.getUTCDate(this.toDate, DateUtil.DATE_FORMAT_DATE));
        this.tvToDate.setOnClickListener(this);
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

    private void apply()
    {
        try
        {
            String leaveReason = etRemarks.getText().toString().trim();
            if (leaveReason.length() <= 0)
            {
                printMsg(R.string.fields_mandatory);
            }
            else if (fromDate < leaveAssignment.getLong(JsonTag.FROM_DATE) || fromDate > leaveAssignment.getLong(JsonTag.TO_DATE))
            {
                printMsg(R.string.invalid_from_date);
            }
            else if (toDate < leaveAssignment.getLong(JsonTag.FROM_DATE) || toDate > leaveAssignment.getLong(JsonTag.TO_DATE))
            {
                printMsg(R.string.invalid_to_date);
            }
            else
            {
                JSONObject person = new JSONObject();
                person.put(JsonTag.ID, getPersonId());
                leaveInfo.put(JsonTag.LEAVE_REASON, leaveReason);
                leaveInfo.put(JsonTag.FROM_DATE, this.fromDate);
                leaveInfo.put(JsonTag.TO_DATE, this.toDate);
                leaveInfo.put(JsonTag.PERSON, person);

                LogUtil.logInfo(leaveInfo.toString());
                ServerComm.requestPost(getContext(), ServerConstant.APPLY_LEAVE, this, leaveInfo);

            }
        } catch (Exception e)
        {
            LogUtil.logException(getClass(), "apply", e);
        }
    }

    private void fromDate()
    {
        this.tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DateFilter.selectUTCYMD(getActivity(), LeaveEntryFragment.this, false);
            }
        });
    }

    private void toDate()
    {
        this.tvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DateFilter.selectUTCYMD(getActivity(), LeaveEntryFragment.this, true);
            }
        });
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
            case ServerConstant.GET_LEAVE_ASSIGNMENT_LIST:
                processLeaveAssignmentList(responseData);
                break;
        }

    }

    @Override
    public void responseFail(int processType, JSONArray requestData, int statusCode)
    {

    }

    private void processLeaveAssignmentList(final JSONArray responseData)
    {
        try
        {
            if (getLeaveAssignmentSpinner() != null && getActivity() != null && responseData != null && responseData.length() > 0)
            {
                int length = responseData.length();
                List<String> list = new ArrayList<>();
                for (int index = 0; index < length; index++)
                {
                    String fromDate = DateUtil.getUTCDate(responseData.getJSONObject(index).getLong(JsonTag.FROM_DATE), DateUtil.DATE_FORMAT_DATE);
                    String toDate = DateUtil.getUTCDate(responseData.getJSONObject(index).getLong(JsonTag.TO_DATE), DateUtil.DATE_FORMAT_DATE);
                    list.add(fromDate + " - " + toDate);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getLeaveAssignmentSpinner().setSelection(0);
                getLeaveAssignmentSpinner().setAdapter(adapter);
                getLeaveAssignmentSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        try
                        {
                            leaveAssignment = responseData.getJSONObject(position);
                            leaveInfo.put(JsonTag.YEAR_ASSIGNMENT, leaveAssignment);

                            JSONObject requestData = new JSONObject();
                            requestData.put(JsonTag.FROM_DATE, leaveAssignment.get(JsonTag.FROM_DATE));
                            requestData.put(JsonTag.TO_DATE, leaveAssignment.get(JsonTag.TO_DATE));
                            getServerDataWithPersonId(LeaveEntryFragment.this, ServerConstant.GET_LEAVE_TYPE_LIST, requestData);
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

    private void processLeaveDurationInfo(final JSONArray responseData)
    {
        try
        {
            if (getLeaveDurationSpinner() != null && getActivity() != null && responseData != null && responseData.length() > 0)
            {
                final int length = responseData.length();
                List<String> list = new ArrayList<>();
                for (int index = 0; index < length; index++)
                {
                    list.add(responseData.getJSONObject(index).getString(JsonTag.NAME));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getLeaveDurationSpinner().setSelection(0);
                getLeaveDurationSpinner().setAdapter(adapter);
                getLeaveDurationSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        try
                        {
                            JSONObject leaveDuration = new JSONObject();
                            leaveDuration.put(JsonTag.ID, responseData.getJSONObject(position).get(JsonTag.ID));
                            leaveInfo.put(JsonTag.LEAVE_DURATION, leaveDuration);
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

    private void processLeaveTypeInfo(final JSONArray responseData)
    {
        try
        {
            if (getLeaveTypeSpinner() != null && getActivity() != null && responseData != null && responseData.length() > 0)
            {
                int length = responseData.length();
                List<String> list = new ArrayList<>();
                for (int index = 0; index < length; index++)
                {
                    list.add(responseData.getJSONObject(index).getString(JsonTag.NAME));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getLeaveTypeSpinner().setAdapter(adapter);
                getLeaveTypeSpinner().setSelection(0);
                getLeaveTypeSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        try
                        {
                            JSONObject leaveType = new JSONObject();
                            leaveType.put(JsonTag.ID, responseData.getJSONObject(position).get(JsonTag.ID));
                            leaveInfo.put(JsonTag.LEAVE_TYPE, leaveType);
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
                            leaveInfo.put(JsonTag.SANCTION_BY, sanctionBy);
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
                            leaveInfo.put(JsonTag.AUTHORISE_BY, authoriseBy);
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


    private Spinner getLeaveTypeSpinner()
    {
        return this.sLeaveType;
    }

    public Spinner getsSanctioner()
    {
        return sSanctioner;
    }

    public Spinner getAuthoriser()
    {
        return sAuthoriser;
    }

    private Spinner getLeaveAssignmentSpinner()
    {
        return this.sLeaveAssignment;
    }

    private Spinner getLeaveDurationSpinner()
    {
        return this.sLeaveDuration;
    }
}
