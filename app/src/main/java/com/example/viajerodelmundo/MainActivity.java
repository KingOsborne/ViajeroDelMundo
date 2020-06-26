package com.example.viajerodelmundo;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final int PICTURE_RESULT = 42;
    TextInputEditText txtTitle;
    TextInputEditText txtPrice;
    TextInputEditText txtLocation;
    TextInputEditText txtDesc;
    ImageView imageView;
    GreatDeals passedDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        txtTitle = (TextInputEditText) findViewById(R.id.txtTitle);
        txtPrice = (TextInputEditText) findViewById(R.id.txtPrice);
        txtLocation = (TextInputEditText) findViewById(R.id.txtLocation);
        txtDesc = (TextInputEditText) findViewById(R.id.txtDesc);
        imageView = (ImageView) findViewById(R.id.imageView);

        final Intent intent = getIntent();
        passedDeal = (GreatDeals) intent.getSerializableExtra("Deal");

        if (passedDeal == null){
            passedDeal = new GreatDeals();
        }
        this.passedDeal = passedDeal;

        txtTitle.setText(passedDeal.getTitle());
        txtPrice.setText(passedDeal.getPrice());
        txtLocation.setText(passedDeal.getLocation());
        txtDesc.setText(passedDeal.getDesc());
        showImage(passedDeal.getImageurl());

        Button btnVal = findViewById(R.id.containedButton);

        btnVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/jpeg");
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent1.createChooser(intent1,"Insert New Picture"),PICTURE_RESULT);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        View v = findViewById(R.id.save_menu);
        if (item.getItemId() == R.id.save_menu) {
            saveDeal();
            Snackbar snackbar = Snackbar.make(v, "The Deal has been saved", Snackbar.LENGTH_LONG);
            snackbar.setDuration(3000);
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
            snackbar.show();
            clean();
            backToList();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void clean() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtLocation.setText("");
        txtDesc.setText("");

        txtTitle.requestFocus();
    }

    private void saveDeal() {
        passedDeal.setTitle(txtTitle.getText().toString());
        passedDeal.setPrice(txtPrice.getText().toString());
        passedDeal.setLocation(txtLocation.getText().toString());
        passedDeal.setDesc(txtDesc.getText().toString());

        if (passedDeal.getId() == null){
            mDatabaseReference.push().setValue(passedDeal);
        }else{
            mDatabaseReference.child(passedDeal.getId()).setValue(passedDeal);
        }

    }

    private void deleteDeal(View v){

        if (passedDeal == null){
            Snackbar snackbar = Snackbar.make(v, "Cannot delete deal that has not been saved", Snackbar.LENGTH_LONG);
            snackbar.setDuration(3000);
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
            snackbar.show();
        }

        mDatabaseReference.child(passedDeal.getId()).removeValue();
    }

    private void backToList(){
        Intent intent = new Intent(this,ListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 42 && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    String picName = taskSnapshot.getStorage().getPath();
                    passedDeal.setImageurl(url);
                    passedDeal.setImageName(picName);
                    showImage(url);
                }
            });
        }
    }

    private void showImage(String url){
        if (url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            int height = Resources.getSystem().getDisplayMetrics().heightPixels;

            Picasso.get()
                    .load(url)
                    .resize(width,height*2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }
}