package com.example.coordinaresponder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FillDetailsFragment extends Fragment {

    private String emergencyType;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_details, container, false);

        TextView title = view.findViewById(R.id.selected_type_title);
        if (emergencyType != null) {
            title.setText(emergencyType + " Emergency");
        }

        view.findViewById(R.id.btn_back_details).setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.btn_send_alert).setOnClickListener(v -> {
            // Handle send alert
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}