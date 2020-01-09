package ids.employeeat.ui.list.leaves.leaveBalance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import helper.logger.LogUtil;
import helper.utils.java.MathUtil;
import ids.employeeat.R;

public class LeaveBalanceAdapter extends RecyclerView.Adapter<LeaveBalanceAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private JSONArray dataArray = new JSONArray();

    public LeaveBalanceAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.adapter_leave_balance, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        try
        {
            JSONObject data = this.dataArray.getJSONObject(position);
            holder.tvLeaveType.setText(data.getString("leaveTypeName"));

            double credited = data.getDouble("debit") + data.getDouble("debitPending") + data.getDouble("credit");
            holder.tvCredited.setText(MathUtil.getString(credited));

            double applied = data.getDouble("debit") + data.getDouble("debitPending");
            holder.tvApplied.setText(MathUtil.getString(applied));

            holder.tvAvailed.setText(data.getString("opBalance"));

            double lapsed = 0.0;
            holder.tvLapsed.setText(MathUtil.getString(lapsed));

        } catch (Exception e)
        {
            LogUtil.logException("AttendanceInfoAdapter", e);
        }
    }

    @Override
    public int getItemCount()
    {
        return dataArray.length();
    }

    public void setAdapterList(JSONArray dataArray)
    {
        this.dataArray = dataArray;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLeaveType, tvCredited, tvApplied, tvAvailed, tvLapsed;

        MyViewHolder(View itemView)
        {
            super(itemView);
            tvLeaveType = itemView.findViewById(R.id.tv_leave_type);
            tvCredited = itemView.findViewById(R.id.tv_credited);
            tvApplied = itemView.findViewById(R.id.tv_applied);
            tvAvailed = itemView.findViewById(R.id.tv_availed);
            tvLapsed = itemView.findViewById(R.id.tv_lapsed);
        }


    }
}
