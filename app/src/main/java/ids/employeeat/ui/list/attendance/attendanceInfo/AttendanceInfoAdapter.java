package ids.employeeat.ui.list.attendance.attendanceInfo;

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
import helper.utils.java.DateUtil;
import ids.employeeat.R;

public class AttendanceInfoAdapter extends RecyclerView.Adapter<AttendanceInfoAdapter.MyViewHolder>
{
    private LayoutInflater inflater;
    private JSONArray dataArray = new JSONArray();

    public AttendanceInfoAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.adapter_attendance_info, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        try
        {
            JSONObject data = this.dataArray.getJSONObject(position);
            holder.tvAttStatus.setText(data.getString("attendanceStatus"));
            holder.tvDate.setText(DateUtil.getDate(data.getLong("date"), "dd/MM/yyyy"));

            long inTime = data.getLong("inTime");
            long outTime = data.getLong("outTime");

            String date = " ";
            date = date.concat(inTime > 0 ? DateUtil.getDate(inTime, "dd/MM/yyyy HH:mm") : "-");
            date = date.concat(" - ");
            date = date.concat(outTime > 0 ? DateUtil.getDate(outTime, "dd/MM/yyyy HH:mm") : "-");
            holder.tv_time.setText(date);

            String comingBy = " ";
            comingBy = comingBy.concat(data.getString("comingBy"));
            comingBy = comingBy.concat(" min");
            holder.tvComingBy.setText(comingBy);

            String goingBy = " ";
            goingBy = goingBy.concat(data.getString("goingBy"));
            goingBy = goingBy.concat(" min");
            holder.tvGoingBy.setText(goingBy);

            String duration = " ";
            duration = duration.concat(data.getString("duration"));
            duration = duration.concat(" min");
            holder.tvDuration.setText(duration);

        }
        catch (Exception e)
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

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvDate, tvAttStatus, tv_time, tvComingBy, tvGoingBy, tvDuration;

        MyViewHolder(View itemView)
        {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAttStatus = itemView.findViewById(R.id.tv_att_status);
            tv_time = itemView.findViewById(R.id.tv_time);
            tvComingBy = itemView.findViewById(R.id.tv_coming_by);
            tvGoingBy = itemView.findViewById(R.id.tv_going_by);
            tvDuration = itemView.findViewById(R.id.tv_duration);
        }


    }
}
