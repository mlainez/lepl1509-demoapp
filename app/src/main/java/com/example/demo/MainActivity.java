package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getBaseContext();
        setContentView(R.layout.activity_main);
        setupButtonListeners();
        updateCounter();
    }

    private void setupButtonListeners(){
        MaterialButton press_button = (MaterialButton) findViewById(R.id.press_button);
        press_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> event = createEventFromCurrentTimestamp();
                saveEventToDB(event);
            }
        });

        MaterialButton about_button = (MaterialButton) findViewById(R.id.about_button);
        about_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AboutActivity.class));
            }
        });
    }

    private Map<String, Object> createEventFromCurrentTimestamp(){
        Map<String, Object> event = new HashMap<>();
        Long timestamp = System.currentTimeMillis()/1000;
        String timestampString = Long.toString(timestamp);
        event.put("timestamp", timestampString);
        event.put("type", "button_press");
        return event;
    }

    private void saveEventToDB(Map<String, Object> event){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").add(event).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, R.string.toast_message, Toast.LENGTH_LONG).show();
                        updateCounter();
                    }
                });
    }

    private void updateCounter(){
        MaterialTextView counterField= (MaterialTextView) findViewById(R.id.count);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int count = queryDocumentSnapshots.getDocuments().size();
                counterField.setText(Integer.toString(count));
            }
        });
    }
}