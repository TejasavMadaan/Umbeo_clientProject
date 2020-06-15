package com.example.umbeo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umbeo.Storage.SharedprefManager;
import com.example.umbeo.api.RetrofitClient;
import com.example.umbeo.response_data.forgetpassword_response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAddressFragment extends Fragment {

    View v;
   EditText add1,add2;
   Button send;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v= inflater.inflate(R.layout.activity_editaddress,container , false);

        send=(Button) v.findViewById(R.id.button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
        return v;
    }

    private void upload(){

        add1 = (EditText) v.findViewById(R.id.address);
        add2 = (EditText) v.findViewById(R.id.address2);

        String address1 = add1.getText().toString();
        String address2 = add2.getText().toString();

       

        String [] address = {address1, address2};

        String shop = "5ec8b35d94e1c83f430781a2";
        String id= SharedprefManager.getInstance(getContext()).getToken();
        String userid="Bearer "+id;

        Call<forgetpassword_response> call = RetrofitClient
                .getmInstance()
                .getApi()
                .updateAddress(userid,shop, address);

       call.enqueue(new Callback<forgetpassword_response>() {
           @Override
           public void onResponse(Call<forgetpassword_response> call, final Response<forgetpassword_response> response) {
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       try {
                           if (response.code() == 200){
                               Toast.makeText(getContext(),"User Address Changed Successfully",Toast.LENGTH_LONG).show();
                               ProfileFragment frag = new ProfileFragment();
                               getFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
                           }
                           else {
                               String s=response.errorBody().string();
                               JSONObject temp=new JSONObject(s);
                               Toast.makeText(getContext(),"Error: "+temp.get("message"),Toast.LENGTH_LONG).show();
                           }
                       }  catch (IOException | JSONException e) {
                           Toast.makeText(getContext(), "Error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();                                    }
                   }
               });
           }

           @Override
           public void onFailure(Call<forgetpassword_response> call, Throwable t) {

           }
       });

    }
}
