package com.leadnsolutions.saloonappointmentscheduling.fragments.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.model.AppointmentModel;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.model.CustomerModel;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.model.SaloonModel;
import com.leadnsolutions.saloonappointmentscheduling.notification.NotificationApi;
import com.leadnsolutions.saloonappointmentscheduling.notification.models.Data;
import com.leadnsolutions.saloonappointmentscheduling.notification.models.Sender;
import com.leadnsolutions.saloonappointmentscheduling.notification.models.StatusResponse;
import com.leadnsolutions.saloonappointmentscheduling.notification.models.Token;
import com.leadnsolutions.saloonappointmentscheduling.notification.retrofit.RetrofitHelper;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;
import com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref.SharedPrefHelper;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentsViewHolder> {
    private Context mContext;
    private List<AppointmentModel> mAppointmentList;
    private String type;

    public AppointmentsAdapter(Context mContext, String type, List<AppointmentModel> mAppointmentList) {
        this.mContext = mContext;
        this.mAppointmentList = mAppointmentList;
        this.type = type;
    }

    @NonNull
    @Override
    public AppointmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.single_appointment_list_items, parent, false);

        return new AppointmentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsViewHolder holder, int position) {
        AppointmentModel model = mAppointmentList.get(position);
        holder.setData(model);
    }

    @Override
    public int getItemCount() {
        return mAppointmentList.size();
    }

    public class AppointmentsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgUser;
        private LinearLayout saloonActionLayout;
        private TextView txtName, txtPhone, txtStatus, txtDayTime, txtTimeStamp;
        private Button btnCancel, btnAccept, btnReject;
        private NotificationApi mNotificationApi;

        public AppointmentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mNotificationApi = RetrofitHelper.getInstance().getNotificationApiClient();
            imgUser = itemView.findViewById(R.id.img_user);
            saloonActionLayout = itemView.findViewById(R.id.saloon_action_layout);

            txtName = itemView.findViewById(R.id.txt_name);
            txtPhone = itemView.findViewById(R.id.txt_phone);
            txtStatus = itemView.findViewById(R.id.txt_status);
            txtDayTime = itemView.findViewById(R.id.txt_time_and_day);

            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnCancel = itemView.findViewById(R.id.btn_action_cancel);
            btnReject = itemView.findViewById(R.id.btn_reject);
            if (SharedPrefHelper.getmHelper().getUserLoginType() != null) {
                if (SharedPrefHelper.getmHelper().getUserLoginType().equalsIgnoreCase("Saloon")) {
                    saloonActionLayout.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.GONE);
                } else {
                    saloonActionLayout.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                }
            }


        }

        private CustomerModel customerModel;

        public void setData(AppointmentModel model) {
            if (SharedPrefHelper.getmHelper().getUserLoginType() != null) {
                String loginType = SharedPrefHelper.getmHelper().getUserLoginType();
                if (loginType.equalsIgnoreCase("Saloon")) {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance()
                            .getReference(AppConstant.CUSTOMER)
                            .child(model.getSenderId());

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                customerModel = snapshot.getValue(CustomerModel.class);
                                if (customerModel != null) {
                                    Glide.with(mContext).load(customerModel.getProfile_image()).dontAnimate().into(imgUser);
                                    txtName.setText(customerModel.getName());
                                    txtPhone.setText(customerModel.getPhone());

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                } else {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance()
                            .getReference(AppConstant.SALOON)
                            .child(model.getReceiverId());

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                SaloonModel saloonModel = snapshot.getValue(SaloonModel.class);
                                if (saloonModel != null) {
                                    Glide.with(mContext).load(saloonModel.getProfile_image()).dontAnimate().into(imgUser);
                                    txtName.setText(saloonModel.getName());
                                    txtPhone.setText(saloonModel.getPhone());

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }


            txtDayTime.setText(model.getDay().concat("--").concat(model.getTime()));
            if (model.getStatus().equalsIgnoreCase("Pending")) {
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else if (model.getStatus().equalsIgnoreCase("Rejected")) {
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));
            } else {
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
            }
            txtStatus.setText(model.getStatus());

            if (!type.equalsIgnoreCase("Pending")) {
                btnCancel.setVisibility(View.GONE);
                saloonActionLayout.setVisibility(View.GONE);
            }

            btnCancel.setOnClickListener(v -> {
                // delete option should be performed!
                FirebaseDatabase.getInstance().getReference(AppConstant.APPOINTMENTS).child(model.getUid()).removeValue();
             /* Query query = dbRef.orderByChild("senderId").equalTo(model.getSenderId());
              query.addValueEventListener(new )*/
                Toast.makeText(mContext, "Appointment is cancelled", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            });

            btnReject.setOnClickListener(v -> {
                model.setStatus("Rejected");
                sendBookingNotification(model);
            });

            btnAccept.setOnClickListener(v -> {
                model.setStatus("Accepted");
                sendBookingNotification(model);
            });
        }

        private void sendBookingNotification(AppointmentModel model) {
            if (SharedPrefHelper.getmHelper().getSaloonModel() != null) {
                SaloonModel saloonModel = new Gson().fromJson(SharedPrefHelper.getmHelper().getSaloonModel(), SaloonModel.class);
                if (saloonModel != null && customerModel != null) {
                    String body = saloonModel.getName() + " is " + model.getStatus() + " your appointment";
                    DatabaseReference dbAllTokenRef = FirebaseDatabase.getInstance().getReference(AppConstant.TOKENS);

                    // Query query = dbAllTokenRef.orderByKey().equalTo(mSaloonModel.getId());
                    dbAllTokenRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot child : snapshot.getChildren())
                                    if (child.getKey().equalsIgnoreCase(customerModel.getId())) {

                                        Token token = child.getValue(Token.class);
                                        Data data = new Data(saloonModel.getId(), body,
                                                "New Appointment Request", customerModel.getId(), R.drawable.saloon_icon);
                                        Sender sender = new Sender(data, token.getToken());


                                        mNotificationApi.sendNotification(sender)
                                                .enqueue(new Callback<StatusResponse>() {
                                                    @Override
                                                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                                                        if (response.isSuccessful()) {
                                                            StatusResponse statusResponse = response.body();
                                                            if (statusResponse != null) {
                                                                Toast.makeText(mContext, "Request is send", Toast.LENGTH_SHORT).show();

                                                                //saving data as appointments in d
                                                                String timeStamp = String.valueOf(System.currentTimeMillis());
                                                                model.setTime(timeStamp);
                                                                FirebaseDatabase.getInstance().getReference(AppConstant.APPOINTMENTS)
                                                                        .child(model.getUid())
                                                                        .setValue(model);

                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                                                        t.printStackTrace();
                                                    }
                                                });
                                    }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

        }
    }
}
