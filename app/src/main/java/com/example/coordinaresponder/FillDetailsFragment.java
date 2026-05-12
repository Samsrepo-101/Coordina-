package com.example.coordinaresponder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coordinaresponder.models.IncidentRequest;
import com.example.coordinaresponder.network.SupabaseClient;
import com.example.coordinaresponder.network.SupabaseService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FillDetailsFragment extends Fragment {

    private String emergencyType;
    private EditText etLocation, etDetails;
    private SupabaseService supabaseService;

    public static FillDetailsFragment newInstance(String type) {
        FillDetailsFragment fragment = new FillDetailsFragment();
        Bundle args = new Bundle();
        args.putString("emergency_type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            emergencyType = getArguments().getString("emergency_type");
        }
        supabaseService = SupabaseClient.getService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_details, container, false);

        etLocation = view.findViewById(R.id.et_location);
        etDetails = view.findViewById(R.id.et_details);
        
        TextView title = view.findViewById(R.id.selected_type_title);
        if (emergencyType != null) {
            title.setText(emergencyType + " Emergency");
        }

        view.findViewById(R.id.btn_back_details).setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Photo attachment is skipped in this Supabase REST implementation for simplicity, 
        // as it requires multi-part upload to Supabase Storage.
        view.findViewById(R.id.btn_attach_photo).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Photo upload coming soon", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btn_send_alert).setOnClickListener(v -> sendEmergencyReport());

        return view;
    }

    private void sendEmergencyReport() {
        String location = etLocation.getText().toString().trim();
        String details = etDetails.getText().toString().trim();
        
        SharedPreferences prefs = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String userId = prefs.getString("user_id", null);

        if (token == null || userId == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mapping UI fields to public.incidents schema
        // address = location, type = emergencyType, reported_by = userId
        IncidentRequest incident = new IncidentRequest(
                emergencyType,
                "open",
                0.0, // Latitude placeholder
                0.0, // Longitude placeholder
                location,
                userId
        );

        supabaseService.createIncident(Config.SUPABASE_ANON_KEY, token, incident).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Alert Sent Successfully!", Toast.LENGTH_LONG).show();
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                } else {
                    Toast.makeText(getContext(), "Error sending alert: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
