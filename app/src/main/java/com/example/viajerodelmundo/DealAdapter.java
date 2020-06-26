package com.example.viajerodelmundo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {

    ArrayList<GreatDeals> savedDeals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ImageView imageDeal;

    public DealAdapter(){
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        savedDeals = FirebaseUtil.mDeals;
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GreatDeals displayDeal = dataSnapshot.getValue(GreatDeals.class);
                displayDeal.setId(dataSnapshot.getKey());
                savedDeals.add(displayDeal);
                notifyItemChanged(savedDeals.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                GreatDeals displayDeal = dataSnapshot.getValue(GreatDeals.class);
                savedDeals.remove(displayDeal);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_row,parent,false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        GreatDeals deal = savedDeals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return savedDeals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dealTitle;
        TextView dealLocation;
        TextView dealDesc;
        TextView dealPrice;
        ImageButton editDeal;
        ImageButton deletDeal;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            dealTitle = (TextView) itemView.findViewById(R.id.dealTitle);
            dealLocation = (TextView) itemView.findViewById(R.id.dealLocation);
            dealDesc = (TextView) itemView.findViewById(R.id.dealDesc);
            dealPrice = (TextView) itemView.findViewById(R.id.dealPrice);
            editDeal = (ImageButton) itemView.findViewById(R.id.edit_deal);
            deletDeal = (ImageButton) itemView.findViewById(R.id.delete_deal);
            imageDeal =(ImageView) itemView.findViewById(R.id.dealImage);
            itemView.setOnClickListener(this);
        }

        public void bind(GreatDeals deal) {
            dealTitle.setText(deal.getTitle());
            dealLocation.setText(deal.getLocation());
            dealDesc.setText(deal.getDesc());
            dealPrice.setText(deal.getPrice());
            showImage(deal.getImageurl());

            if(FirebaseUtil.isAdmin){
                editDeal.setVisibility(View.VISIBLE);
                deletDeal.setVisibility(View.VISIBLE);
            }else{
                editDeal.setVisibility(View.INVISIBLE);
                deletDeal.setVisibility(View.INVISIBLE);
            }

            editDeal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    GreatDeals pickedDeal = savedDeals.get(position);
                    Intent intent = new Intent(itemView.getContext(),MainActivity.class);
                    intent.putExtra("Deal",pickedDeal);
                    v.getContext().startActivity(intent);
                }
            });

            deletDeal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    GreatDeals pickedDeal = savedDeals.get(position);
                    deleteDeal(pickedDeal,v);
                    Snackbar snackbar = Snackbar.make(v, "Deal has been deleted", Snackbar.LENGTH_LONG);
                    snackbar.setDuration(3000);
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
                    snackbar.show();
                    Intent refresh = new Intent(itemView.getContext(), ListActivity.class);
                    v.getContext().startActivity(refresh);
                }
            });

        }

        @Override
        public void onClick(View v) {
        }

    }

    private void deleteDeal(GreatDeals passedDeal,View v){

        if (passedDeal == null){
            Snackbar snackbar = Snackbar.make(v, "Cannot delete deal that has not been saved", Snackbar.LENGTH_LONG);
            snackbar.setDuration(3000);
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
            snackbar.show();
        }

        mDatabaseReference.child(passedDeal.getId()).removeValue();

        if((passedDeal.getImageName() != null && passedDeal.getImageName().isEmpty()) == false){
            StorageReference picRef = FirebaseUtil.mStorage.getReference().child(passedDeal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

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
                    .centerCrop()
                    .into(imageDeal);
        }
    }

}
