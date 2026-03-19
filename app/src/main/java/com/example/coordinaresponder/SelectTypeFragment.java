package com.example.coordinaresponder;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SelectTypeFragment extends Fragment {

    public void setOnTypeSelectedListener(EmergencyFormActivity emergencyFormActivity) {
    }

    public interface OnTypeSelectedListener {
        void onTypeSelected(String type);
    }

    private OnTypeSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTypeSelectedListener) {
            listener = (OnTypeSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnTypeSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_type, container, false);

        view.findViewById(R.id.type_fire).setOnClickListener(v -> selectType("Fire"));
        view.findViewById(R.id.type_medical).setOnClickListener(v -> selectType("Medical"));
        view.findViewById(R.id.type_crime).setOnClickListener(v -> selectType("Crime"));
        view.findViewById(R.id.type_disaster).setOnClickListener(v -> selectType("Disaster"));
        view.findViewById(R.id.type_accident).setOnClickListener(v -> selectType("Accident"));

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void selectType(String type) {
        if (listener != null) {
            listener.onTypeSelected(type);
        }
    }
}