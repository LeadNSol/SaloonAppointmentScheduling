package com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.adapter;

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
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.model.AddStaff;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.AddStaffViewHolder> {

    private Context mContext;
    private List<AddStaff> listModel;

    public StaffAdapter(Context mContext, List<AddStaff> listModel) {
        this.mContext = mContext;
        this.listModel = listModel;
    }

    @NonNull
    @Override
    public StaffAdapter.AddStaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.saloon_staff_list,
                parent, false);

        return new AddStaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffAdapter.AddStaffViewHolder holder, int position) {

        AddStaff model = listModel.get(position);
        holder.setData(model, mContext);
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public static class AddStaffViewHolder extends RecyclerView.ViewHolder {

        TextView staffName, staffPhone, staffAddress, staffType, staffFree;
        ImageView staffImage;

        public AddStaffViewHolder(@NonNull View itemView) {
            super(itemView);
            staffImage = itemView.findViewById(R.id.image_saloon_staff);
            staffName = itemView.findViewById(R.id.staff_name);
            staffAddress = itemView.findViewById(R.id.staff_address);
            staffPhone = itemView.findViewById(R.id.staff_phone);
            staffType = itemView.findViewById(R.id.staff_type);
            staffFree = itemView.findViewById(R.id.staff_isFree);
        }

        public void setData(AddStaff model, Context mContext) {
            staffName.setText(model.getsName());
            staffAddress.setText(model.getsAddress());
            staffPhone.setText(model.getsPhone());
            staffType.setText(model.getsDesignation());
            staffFree.setText(model.getsFree());

            Glide.with(mContext).load(model.getsImage()).into(staffImage);
        }

    }
}
