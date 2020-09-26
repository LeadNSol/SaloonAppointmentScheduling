package com.leadnsolutions.saloonappointmentscheduling.fragments.customer.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.adapter.SaloonRatingAdapter;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaloonProfileFragment extends Fragment {

    public SaloonProfileFragment() {

    }

    private SaloonModel mSaloonModel;

    public SaloonProfileFragment(SaloonModel saloonModel) {
        this.mSaloonModel = saloonModel;
    }

    private NotificationApi mNotificationApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_saloon_profile, container, false);
    }

    private ImageView imgSaloon;
    private TextView txtSaloonName, txtSaloonPhone, txtAddress;
    private AppCompatActivity mActivity;
    private RecyclerView mRvSaloonRating;
    private Button btnBookNow;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();
        if (mActivity != null && mActivity.getSupportActionBar() != null)
            mActivity.getSupportActionBar().hide();

        imgSaloon = view.findViewById(R.id.saloon_profile_image);
        txtSaloonName = view.findViewById(R.id.profile_saloon_name);
        txtSaloonPhone = view.findViewById(R.id.profile_saloon_phone);
        txtAddress = view.findViewById(R.id.profile_saloon_address);
        llVertical = view.findViewById(R.id.ll_vertical);

        btnBookNow = view.findViewById(R.id.btn_book_now);
        mRvSaloonRating = view.findViewById(R.id.rv_saloon_rating);
        mRvSaloonRating.setHasFixedSize(true);
        mRvSaloonRating.setLayoutManager(new LinearLayoutManager(mActivity));


        mNotificationApi = RetrofitHelper.getInstance().getNotificationApiClient();
        loadProfileData();

        btnBookNow.setOnClickListener(view1 -> {
            showBookingDialog();
        });


    }

    private Dialog mBookingDialog;
    private String[] daySaloon = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private String[] timeSaloon = {"01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00",
            "09:00", "10:00", "11:00", "12:00"};

    private String selectedDay, selectedTime;
    private boolean notify = false;

    private void showBookingDialog() {
        if (mBookingDialog == null) {
            mBookingDialog = new Dialog(mActivity);
            mBookingDialog.setCancelable(false);

            mBookingDialog.setContentView(R.layout.dialog_booking_saloon);
            mBookingDialog.show();

            assert mBookingDialog.getWindow() != null;
            mBookingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            MaterialSpinner spDays = mBookingDialog.findViewById(R.id.spinner_day);
            MaterialSpinner spTime = mBookingDialog.findViewById(R.id.spinner_time);
            spTime.setItems(timeSaloon);
            spDays.setItems(daySaloon);

            spTime.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>)
                    (view, position, id, item) -> selectedTime = item);
            spDays.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {
                selectedDay = item;
            });

            Button btnSendRequest = mBookingDialog.findViewById(R.id.btn_send_request);
            btnSendRequest.setOnClickListener(view -> {
                if (selectedDay != null && selectedTime != null) {
                    notify = true;
                    sendBookingNotification();
                } else {
                    spDays.setError("Day isn't Selected!");
                    spTime.setError("Time isn't Selected!");
                }
            });

            TextView txtClose = mBookingDialog.findViewById(R.id.txt_close);
            txtClose.setOnClickListener(v -> {
                mBookingDialog.dismiss();
                mBookingDialog = null;
            });


        } else {
            mBookingDialog.dismiss();
            mBookingDialog = null;
        }
    }

    private void sendBookingNotification() {
        if (SharedPrefHelper.getmHelper().getCustomerModel() != null && notify) {
            CustomerModel customerModel = new Gson().fromJson(SharedPrefHelper.getmHelper().getCustomerModel(), CustomerModel.class);
            if (mSaloonModel != null && customerModel != null) {
                String body = customerModel.getName() + " is sent request for an appointment at " + selectedTime + "and " + selectedDay;
                DatabaseReference dbAllTokenRef = FirebaseDatabase.getInstance().getReference(AppConstant.TOKENS);

                Query query = dbAllTokenRef.orderByKey().equalTo(mSaloonModel.getId());
                dbAllTokenRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren())
                                if (child.getKey().equalsIgnoreCase(mSaloonModel.getId())) {

                                    Token token = child.getValue(Token.class);
                                    Data data = new Data(customerModel.getId(), body,
                                            "New Appointment Request", mSaloonModel.getId(), R.drawable.saloon_icon);
                                    Sender sender = new Sender(data, token.getToken());


                                    mNotificationApi.sendNotification(sender)
                                            .enqueue(new Callback<StatusResponse>() {
                                                @Override
                                                public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                                                    if (response.isSuccessful()) {
                                                        StatusResponse statusResponse = response.body();
                                                        if (statusResponse != null) {
                                                            Toast.makeText(mActivity, "Request is send", Toast.LENGTH_SHORT).show();
                                                            mBookingDialog.dismiss();
                                                            mBookingDialog = null;

                                                            //saving data as appointments in db
                                                            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                                                                    .getReference().child(AppConstant.APPOINTMENTS).push();
                                                            String timeStamp = String.valueOf(System.currentTimeMillis());
                                                            /*HashMap<String, Object> hashMap = new HashMap<>();
                                                            hashMap.put("sender", customerModel.getId());
                                                            hashMap.put("receiver", mSaloonModel.getId());
                                                            hashMap.put("time", selectedTime);
                                                            hashMap.put("day", selectedDay);
                                                            hashMap.put("timeStamp", timeStamp);*/

                                                            AppointmentModel appointmentModel =
                                                                    new AppointmentModel(
                                                                            dbRef.getKey(),
                                                                            customerModel.getId(),
                                                                            mSaloonModel.getId(),
                                                                            selectedTime,
                                                                            selectedDay,
                                                                            timeStamp,
                                                                            "Pending");

                                                            dbRef.setValue(appointmentModel);

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
            notify = false;
        }

    }

    private LinearLayout llVertical;

    private void loadProfileData() {
        if (mSaloonModel != null) {
            txtSaloonName.setText(mSaloonModel.getName());
            txtSaloonPhone.setText(mSaloonModel.getPhone());
            txtAddress.setText(mSaloonModel.getAddress());
            Glide.with(mActivity).load(mSaloonModel.getProfile_image()).into(imgSaloon);


            for (SaloonModel.SaloonService saloonService : mSaloonModel.getSaloonService()) {
                TextView txtSaloonService, txtServicePrice;
                txtSaloonService = new TextView(mActivity);
                txtServicePrice = new TextView(mActivity);
                LinearLayout llHorizontal = new LinearLayout(mActivity);

                txtSaloonService.setText(saloonService.getName());
                txtServicePrice.setText(saloonService.getPrice());

                llHorizontal.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                llHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1);

                layoutParams.setMargins(2, 2, 2, 2);
                txtSaloonService.setLayoutParams(layoutParams);
                txtServicePrice.setLayoutParams(layoutParams);
                txtSaloonService.setGravity(Gravity.CENTER);
                txtSaloonService.setTextSize(18);
                txtServicePrice.setTextSize(18);
                txtServicePrice.setGravity(Gravity.CENTER);

                txtSaloonService.setPadding(3, 3, 3, 3);
                txtServicePrice.setPadding(3, 3, 3, 3);
                llHorizontal.addView(txtSaloonService);
                llHorizontal.addView(txtServicePrice);
                llVertical.addView(llHorizontal);
            }
            SaloonRatingAdapter saloonRatingAdapter = new SaloonRatingAdapter(mActivity, mSaloonModel.getSaloonRating());
            mRvSaloonRating.setAdapter(saloonRatingAdapter);
        }
    }
}