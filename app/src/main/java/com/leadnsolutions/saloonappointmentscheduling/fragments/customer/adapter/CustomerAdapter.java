package com.leadnsolutions.saloonappointmentscheduling.fragments.customer.adapter;

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
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.model.CustomerModel;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Context mContext;
    private List<CustomerModel> listModel;


    public CustomerAdapter(Context mContext, List<CustomerModel> listModel) {
        this.mContext = mContext;
        this.listModel = listModel;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.registered_customers_list,
                parent, false);

        return new CustomerAdapter.CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {

        CustomerModel customerModel = listModel.get(position);
        holder.setData(customerModel, mContext);


    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtPhone, txtLoc, txtGender;

        private ImageView imgCustomer;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCustomer = itemView.findViewById(R.id.customer_profile_image);
            txtName = itemView.findViewById(R.id.tv_customer_name);
            txtPhone = itemView.findViewById(R.id.tv_customer_phone);
            txtLoc = itemView.findViewById(R.id.tv_customer_loc);
            txtGender = itemView.findViewById(R.id.tv_customer_gender);
        }

        public void setData(CustomerModel customerModel, Context mContext) {

            txtName.setText(customerModel.getName());
            txtPhone.setText(customerModel.getPhone());
            txtLoc.setText(customerModel.getLocation());
            txtGender.setText(customerModel.getGender());

            Glide.with(mContext).load(customerModel.getProfile_image()).into(imgCustomer);

        }
    }
}
