package com.leadnsolutions.saloonappointmentscheduling.saloon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.saloon.model.SaloonModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SaloonAdapter extends RecyclerView.Adapter<SaloonAdapter.SaloonViewHolder> {
    private Context mContext;
    private List<SaloonModel> listModel;
    private OnSaloonClickListener mOnSaloonClickListener;

    public SaloonAdapter(Context mContext, List<SaloonModel> listModel, OnSaloonClickListener mOnSaloonClickListener) {
        this.mContext = mContext;
        this.listModel = listModel;
        this.mOnSaloonClickListener = mOnSaloonClickListener;
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

    public class SaloonViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtRating, txtLoc, txtService;
        private CircleImageView imgSaloon;

        public SaloonViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSaloon = itemView.findViewById(R.id.image_saloon_staff);
            txtName = itemView.findViewById(R.id.tv_s_name);
            txtRating = itemView.findViewById(R.id.tv_rating);

        }

        public void setData(SaloonModel model, Context mContext) {
            txtName.setText(model.getName());
            double totalRating = 0.0;
            for (SaloonModel.SaloonRating saloonRating : model.getSaloonRating()) {
                totalRating += Double.parseDouble(saloonRating.getRating());
            }
            totalRating = totalRating / model.getSaloonRating().size();
            txtRating.setText(String.format("%.1f", totalRating));

//            txtPhone.setText(model.getPhone());
//            txtLoc.setText(model.getAddress());
//            txtService.setText(model.getSaloon_service());

            Glide.with(mContext).load(model.getProfile_image())
                    .dontAnimate().into(imgSaloon);
            itemView.setOnClickListener(view -> {
                mOnSaloonClickListener.onSaloonClick(model);
            });

        }
    }

    public interface OnSaloonClickListener {
        void onSaloonClick(SaloonModel saloonModel);
    }
}