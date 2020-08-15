package com.leadnsolutions.saloonappointmentscheduling.saloon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.saloon.model.SaloonModel;

import java.util.List;

public class SaloonAdapter extends RecyclerView.Adapter<SaloonAdapter.SaloonViewHolder> {
    private Context mContext;
    private List<SaloonModel> listModel;

    public SaloonAdapter(Context mContext, List<SaloonModel> listModel) {
        this.mContext = mContext;
        this.listModel = listModel;
    }

    @NonNull
    @Override
    public SaloonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.registered_saloon_list,
                parent, false);

        return new SaloonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaloonViewHolder holder, int position) {

        SaloonModel model = listModel.get(position);
        holder.setData(model, mContext);
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public static class SaloonViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtPhone, txtLoc, txtService;
        private ImageView imgSaloon;

        public SaloonViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSaloon = itemView.findViewById(R.id.profile_image);
            txtName = itemView.findViewById(R.id.tv_s_name);
            txtPhone = itemView.findViewById(R.id.tv_s_phone);
            txtLoc = itemView.findViewById(R.id.tv_s_loc);
            txtService = itemView.findViewById(R.id.tv_s_service);
        }

        public void setData(SaloonModel model, Context mContext) {
            txtName.setText(model.getName());
            txtPhone.setText(model.getPhone());
            txtLoc.setText(model.getLocation());
//            txtService.setText(model.getSaloon_service());

            Glide.with(mContext).load(model.getProfile_image()).into(imgSaloon);
        }
    }
}