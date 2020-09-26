package com.leadnsolutions.saloonappointmentscheduling.fragments.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.model.CustomerModel;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.model.SaloonModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SaloonRatingAdapter extends RecyclerView.Adapter<SaloonRatingAdapter.SaloonRatingViewHolder> {
    private Context mContext;
    private List<SaloonModel.SaloonRating> mSaloonRatingList;

    public SaloonRatingAdapter(Context mContext, List<SaloonModel.SaloonRating> mSaloonRatingList) {
        this.mContext = mContext;
        this.mSaloonRatingList = mSaloonRatingList;
    }

    @NonNull
    @Override
    public SaloonRatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.single_saloon_rating_items, parent, false);
        return new SaloonRatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaloonRatingViewHolder holder, int position) {
        SaloonModel.SaloonRating rating = mSaloonRatingList.get(position);
        holder.setData(rating);
    }

    @Override
    public int getItemCount() {
        return mSaloonRatingList.size();
    }

    public class SaloonRatingViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgCustomer;
        private TextView txtName, txtRating, txtComment;

        public SaloonRatingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCustomer = itemView.findViewById(R.id.img_customer);
            txtName = itemView.findViewById(R.id.txt_customer_name);
            txtRating = itemView.findViewById(R.id.txt_rating);
            txtComment = itemView.findViewById(R.id.txt_comments);
        }

        public void setData(SaloonModel.SaloonRating rating) {
            txtComment.setText(rating.getComment());
            txtRating.setText(rating.getRating());
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.CUSTOMER).child(rating.getCustomerId());
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        CustomerModel customerModel = snapshot.getValue(CustomerModel.class);
                        if (customerModel != null) {
                            txtName.setText(customerModel.getName());
                            Glide.with(mContext).load(customerModel.getProfile_image()).dontAnimate().into(imgCustomer);
                        } else {
                            Toast.makeText(mContext, "No Customer is Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //Glide.with(mContext).load()
        }

    }
}
