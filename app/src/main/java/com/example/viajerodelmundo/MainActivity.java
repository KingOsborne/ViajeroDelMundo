package com.example.viajerodelmundo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    TextInputEditText txtTitle;
    TextInputEditText txtPrice;
    TextInputEditText txtLocation;
    TextInputEditText txtDesc;
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

        Intent intent = getIntent();
        passedDeal = (GreatDeals) intent.getSerializableExtra("Deal");

        if (passedDeal == null){
            passedDeal = new GreatDeals();
        }
        this.passedDeal = passedDeal;

        txtTitle.setText(passedDeal.getTitle());
        txtPrice.setText(passedDeal.getPrice());
        txtLocation.setText(passedDeal.getLocation());
        txtDesc.setText(passedDeal.getDesc());

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
}