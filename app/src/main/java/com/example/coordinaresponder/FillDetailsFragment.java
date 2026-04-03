package com.example.coordinaresponder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FillDetailsFragment extends Fragment {

    private String emergencyType;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    
    private EditText etLocation, etDetails;
    private ImageView ivPreview;
    private LinearLayout layoutAttachHint;
    private Uri imageUri;
    
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    ivPreview.setImageURI(imageUri);
                    ivPreview.setVisibility(View.VISIBLE);
                    layoutAttachHint.setVisibility(View.GONE);
                }
            }
    );

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
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_details, container, false);

        etLocation = view.findViewById(R.id.et_location);
        etDetails = view.findViewById(R.id.et_details);
        ivPreview = view.findViewById(R.id.iv_preview);
        layoutAttachHint = view.findViewById(R.id.layout_attach_hint);
        
        TextView title = view.findViewById(R.id.selected_type_title);
        if (emergencyType != null) {
            title.setText(emergencyType + " Emergency");
        }

        view.findViewById(R.id.btn_back_details).setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.btn_attach_photo).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        view.findViewById(R.id.btn_send_alert).setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageAndSendReport();
            } else {
                sendEmergencyReport("");
            }
        });

        return view;
    }

    private void uploadImageAndSendReport() {
        String fileName = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("emergency_photos/" + fileName);

        Toast.makeText(getContext(), "Uploading photo...", Toast.LENGTH_SHORT).show();
        
        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    sendEmergencyReport(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    sendEmergencyReport(""); // Send without image if upload fails
                });
    }

    private void sendEmergencyReport(String photoUrl) {
        String location = etLocation.getText().toString().trim();
        String details = etDetails.getText().toString().trim();
        
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> report = new HashMap<>();
        report.put("user_id", mAuth.getCurrentUser().getUid());
        report.put("emergency_type", emergencyType);
        report.put("location", location);
        report.put("details", details);
        report.put("photo_url", photoUrl);
        report.put("report_time", FieldValue.serverTimestamp());
        report.put("status", "Reported");

        db.collection("reports")
                .add(report)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Alert Sent Successfully!", Toast.LENGTH_LONG).show();
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error saving report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
