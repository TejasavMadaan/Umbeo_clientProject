package com.example.umbeo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    ImageView lichi,strawbe,address;
    TextView editName,editAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.activity_person,container , false);

        editName= v.findViewById(R.id.editName);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditNameFragment frag= new EditNameFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
            }
        });

        editAddress = (TextView) v.findViewById(R.id.editAddress);
        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAddressFragment frag = new EditAddressFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
            }
        });
        return v;
    }
}
