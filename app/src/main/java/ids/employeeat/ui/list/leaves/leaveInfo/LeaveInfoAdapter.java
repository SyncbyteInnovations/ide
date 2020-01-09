package ids.employeeat.ui.list.leaves.leaveInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import helper.logger.LogUtil;
import helper.utils.java.DateUtil;
import ids.employeeat.AppConstant;
import ids.employeeat.JsonTag;
import ids.employeeat.R;

public class LeaveInfoAdapter extends RecyclerView.Adapter<LeaveInfoAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private JSONArray dataArray = new JSONArray();

    public LeaveInfoAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.adapter_leave_info, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        try
        {
            JSONObject data = this.dataArray.getJSONObject(position);
            holder.tvLeaveType.setText(data.getString("leaveTypeName"));


            long fromDate = data.getLong("fromDate");
            long toDate = data.getLong("toDate");

            String date = " ";
            date = date.concat(DateUtil.getDate(fromDate, DateUtil.DATE_FORMAT_DATE));
            date = date.concat(" - ");
            date = date.concat(DateUtil.getDate(toDate, DateUtil.DATE_FORMAT_DATE));
            holder.tvDate.setText(date);

            processLeaveStatus(data, holder.tvLeaveStatus);
            processApprover(data, holder);

            holder.tvLeaveDuration.setText(data.getString("leaveDurationName"));
            holder.tvSanctionBy.setText(data.getString("sanctionBy"));
            holder.tvAuthorisedBy.setText(data.getString("authoriseBy"));
        } catch (Exception e)
        {
            LogUtil.logException("AttendanceInfoAdapter", e);
        }
    }

    private void processApprover(JSONObject data, MyViewHolder holder) throws Exception
    {
        int isSanctioned = data.getInt(JsonTag.IS_SANCTIONED);
        int isAuthorised = data.getInt(JsonTag.IS_AUTHORISED);

        if (isSanctioned == AppConstant.CON_TRUE)
        {
            holder.sanctionByLabel.setText(R.string.sanctioned_by);
        }

        if (isAuthorised == AppConstant.CON_TRUE)
        {
            holder.authoriseByLabel.setText(R.string.authorised_by);
        }
    }

    private void processLeaveStatus(JSONObject data, TextView tv) throws Exception
    {
        long leaveStatusId = data.getLong(JsonTag.LEAVE_STATUS_ID);
        tv.setText(data.getString(JsonTag.LEAVE_STATUS_NAME));
        if (leaveStatusId == AppConstant.CON_APPROVE_STATUS_REJECTED)
        {
            tv.setTextColor(Color.RED);
        }
        else if (leaveStatusId == AppConstant.CON_APPROVE_STATUS_ACCEPTED)
        {
            tv.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount()
    {
        if (dataArray == null)
        {
            return 0;
        }
        return dataArray.length();
    }

    public void setAdapterList(JSONArray dataArray)
    {
        this.dataArray = dataArray;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLeaveType, tvLeaveDuration, tvLeaveStatus, tvSanctionBy, tvAuthorisedBy, tvDate;
        TextView sanctionByLabel, authoriseByLabel;

        MyViewHolder(View itemView)
        {
            super(itemView);
            tvLeaveType = itemView.findViewById(R.id.tv_leave_type);
            tvDate = itemView.findViewById(R.id.tv_time);
            tvLeaveDuration = itemView.findViewById(R.id.tv_leave_duration);
            tvLeaveStatus = itemView.findViewById(R.id.tv_leave_status);
            tvSanctionBy = itemView.findViewById(R.id.tv_sanctioned_by);
            tvAuthorisedBy = itemView.findViewById(R.id.tv_authorised_by);
            sanctionByLabel = itemView.findViewById(R.id.sanction_by_label);
            authoriseByLabel = itemView.findViewById(R.id.authorise_by_label);
        }


    }
}
