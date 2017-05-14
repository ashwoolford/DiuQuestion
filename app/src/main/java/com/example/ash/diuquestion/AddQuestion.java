package com.example.ash.diuquestion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddQuestion extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private ImageView uploadImageView;
    private ImageView imageView;
    private Uri imageUri;
    private ProgressDialog mProgressDialog;
    private LoadingButton lb;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    @InjectView(R.id.name)
    EditText mName;
    @InjectView(R.id.subject_name)
    EditText mSubName;
    @InjectView(R.id.code)
    EditText mCode;
    @InjectView(R.id.exam_type)
    EditText mExamType;
    @InjectView(R.id.description)
    EditText mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        ButterKnife.inject(this);

        mProgressDialog = new ProgressDialog(this);
        uploadImageView = (ImageView) findViewById(R.id.uploadImage);
        imageView = (ImageView) findViewById(R.id.uploadView);
        toolbar = (Toolbar) findViewById(R.id.addquestion_toobar);
        toolbar.setTitle("Add a question");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //initializing firebase instances

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        lb = (LoadingButton) findViewById(R.id.button);


        uploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalary();
            }
        });
    }

    private void openGalary() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name, subName, code, examType, description;
                    name = mName.getText().toString(); subName = mSubName.getText().toString(); code = mCode.getText().toString();
                    examType = mExamType.getText().toString(); description = mDescription.getText().toString();

                    if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(subName) && !TextUtils.isEmpty(code)
                            && !TextUtils.isEmpty(examType) && !TextUtils.isEmpty(description)){

                        saveOnDatabase(name, subName, code, examType, description);
                        lb.startLoading();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please fill all fields",Toast.LENGTH_SHORT).show();
                        lb.loadingFailed();
                    }

                }
            });
        }
    }

    private void saveOnDatabase(final String name, final String subName, final String code, final String examType, final String description) {

        StorageReference filepath = storageReference.child("images").child(imageUri.getLastPathSegment());
        UploadTask uploadTask = filepath.putFile(imageUri);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                mProgressDialog.setMessage("Uploading "+Math.round(progress)+"%");
                mProgressDialog.show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                DatabaseReference Ref = databaseReference.child("posts").push();
                Ref.child("post_title").setValue(name);
                Ref.child("subject_name").setValue(subName);
                Ref.child("code").setValue(code);
                Ref.child("exam_type").setValue(examType);
                Ref.child("description").setValue(description);
                Ref.child("url").setValue(downloadUri.toString());


                lb.loadingSuccessful();
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Upload completed",Toast.LENGTH_SHORT).show();
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                lb.loadingFailed();
                Toast.makeText(getApplicationContext(),"Upload failed",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
