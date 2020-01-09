package ids.employeeat.ui.list.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ids.employeeat.R;
import ids.employeeat.data.database.menu.MenuInfo;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;

        private MenuViewHolder(View itemView)
        {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    private List<MenuInfo> dataList;
    private LayoutInflater inflater;

    public MenuAdapter(Context context, List<MenuInfo> dataList)
    {
        this.inflater = LayoutInflater.from(context);
        this.dataList = dataList;
    }

    @Override
    @NonNull
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.adapter_home_left_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position)
    {
        if (dataList != null)
        {
            MenuInfo menuInfo = dataList.get(position);
            holder.tvTitle.setText(menuInfo.getName());
            holder.ivIcon.setImageResource(menuInfo.getIconId());
        }
    }

    @Override
    public int getItemCount()
    {
        if (dataList != null)
        {
            return dataList.size();
        }
        else
        {
            return 0;
        }
    }

    public void updateMenuList(List<MenuInfo> dataList)
    {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public MenuInfo getData(int position)
    {
        if (dataList != null && dataList.size() > 0)
        {
            return dataList.get(position);
        }
        else
        {
            return null;
        }
    }
}
