package ids.employeeat.ui.list.attendance.manualEntry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import helper.logger.LogUtil;
import helper.utils.android.ResourceUtil;
import helper.utils.java.DateUtil;
import ids.employeeat.R;
import ids.employeeat.server.ServerConstant;

public class ManualEntryAdapter extends RecyclerView.Adapter<ManualEntryAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private JSONArray dataArray = new JSONArray();
    private Context context;

    public ManualEntryAdapter(Context context)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.adapter_manual_log, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  final MyViewHolder holder, int position)
    {
        try
        {
            JSONObject data = this.dataArray.getJSONObject(position);
            long logStatusId = data.getLong("logStatusId");

            int logStatusColor = R.color.light_gray;
            if (logStatusId == ServerConstant.CON_ACCEPTED)
            {
                logStatusColor = R.color.green;
            }
            else if (logStatusId == ServerConstant.CON_REJECTED)
            {
                logStatusColor = R.color.red;
            }
            holder.tvLogStatus.setTextColor(ResourceUtil.getColor(context, logStatusColor));
            holder.tvLogStatus.setText(data.getString("logStatusName"));

            String date = " ";
            date = date.concat(DateUtil.getDate(data.getLong("logDate"), DateUtil.DATE_FORMAT_FULL_NS));
            holder.tvDate.setText(date);
            holder.tvRemarks.setText(data.getString("remarks"));
        } catch (Exception e)
        {
            LogUtil.logException("ManualEntryAdapter-->onBindViewHolder", e);
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
        TextView tvLogStatus, tvDate, tvRemarks;

        MyViewHolder(View itemView)
        {
            super(itemView);
            tvLogStatus = itemView.findViewById(R.id.tv_log_status);
            tvDate = itemView.findViewById(R.id.tv_time);
            tvRemarks = itemView.findViewById(R.id.tv_remarks);
        }


    }
}
