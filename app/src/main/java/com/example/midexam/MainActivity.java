package com.example.midexam;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etID;
    private SeekBar sbAge;
    private TextView tvAgeLabel;
    private Button btnAdd, btnEdit, btnDelete, btnView;

    private DatabaseHelper dbHelper;
    private int selectedAge = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etStudentName);
        etID = findViewById(R.id.etStudentID);
        sbAge = findViewById(R.id.sbStudentAge);
        tvAgeLabel = findViewById(R.id.tvAgeLabel);

        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnView = findViewById(R.id.btnView);

        sbAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedAge = progress;
                tvAgeLabel.setText("Student Age: " + selectedAge);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String id = etID.getText().toString().trim();

            if (name.isEmpty() || id.isEmpty()) {
                Toast.makeText(this, "Fill in all fields first", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = dbHelper.insertStudent(id, name, selectedAge);
            if (isInserted) {
                Toast.makeText(this, "Data successfully added!", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(this, "Failed to add data (ID may already exist)", Toast.LENGTH_SHORT).show();
            }
        });

        btnEdit.setOnClickListener(v -> {
            String id = etID.getText().toString().trim();
            String name = etName.getText().toString().trim();

            if (id.isEmpty()) {
                Toast.makeText(this, "Enter the Student ID you want to change", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isUpdated = dbHelper.updateStudent(id, name, selectedAge);
            if (isUpdated) {
                Toast.makeText(this, "Data successfully updated!", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(this, "ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            String id = etID.getText().toString().trim();
            String name = etName.getText().toString().trim();

            if (id.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Enter both Student ID and Name to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete student " + name + " (ID: " + id + ")?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        boolean isDeleted = dbHelper.deleteStudent(id, name);
                        if (isDeleted) {
                            Toast.makeText(this, "Data successfully deleted!", Toast.LENGTH_SHORT).show();
                            clearForm();
                        } else {
                            Toast.makeText(this, "Data not found or ID & Name do not match", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        btnView.setOnClickListener(v -> {
            Cursor cursor = dbHelper.getAllStudents();
            if (cursor.getCount() == 0) {
                showDialog("Student Data", "No data stored yet.");
                return;
            }

            StringBuilder builder = new StringBuilder();
            while (cursor.moveToNext()) {
                builder.append("ID: ").append(cursor.getString(0)).append("\n");
                builder.append("Name: ").append(cursor.getString(1)).append("\n");
                builder.append("Age: ").append(cursor.getInt(2)).append("\n\n");
            }

            showDialog("Student List", builder.toString());
        });
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void clearForm() {
        etName.setText("");
        etID.setText("");
        sbAge.setProgress(17);
    }
}