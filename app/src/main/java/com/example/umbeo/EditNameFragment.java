package com.example.umbeo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.umbeo.Storage.SharedprefManager;
import com.example.umbeo.api.RetrofitClient;
import com.example.umbeo.response_data.forgetpassword_response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class EditNameFragment extends Fragment {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    String encodedImage = "Empty";
    private Button editName;
    private ImageView dp;
     EditText nam,phon;

    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v= inflater.inflate(R.layout.activity_editname,container , false);

        editName= (Button) v.findViewById(R.id.button);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
                // Toast.makeText(getContext(),"User Name Updated Successfully",Toast.LENGTH_LONG).show();
            }
        });

        dp = (ImageView) v.findViewById(R.id.dp);
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                } else
                    selectImage();
            }
        });
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUrl = data.getData();
                if (selectedImageUrl != null) {
                    try {
                        // set the image to display...
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImageUrl);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        dp.setImageBitmap(bitmap);

                        //call function to go for base64 conversion...
                        // Following is the selected image file
                        File selectedImageFile = new File(getPathFromUri(selectedImageUrl));
                        // do the conversion...

                        encodedImage = encodeImage(selectedImageFile);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getActivity().getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;

    }

    private String encodeImage(File selectedImageFile) throws Exception {

        InputStream inputStream = new FileInputStream(selectedImageFile); // You can get an inputStream using any I/O API
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        bytes = output.toByteArray();
        String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);

        return encoded;
    }

    private void upload(){

        nam = (EditText) v.findViewById(R.id.username);
        phon = (EditText) v.findViewById(R.id.number);

        String name = nam.getText().toString();
        String number = phon.getText().toString();

        String shop = "5ec8b35d94e1c83f430781a2";
        String id= SharedprefManager.getInstance(getContext()).getToken();
        String userid="Bearer "+id;

        if (userid.isEmpty()){
            Toast.makeText(getContext(),"Please Login First",Toast.LENGTH_LONG).show();
            return;
        }

        if(name.isEmpty()){
            if (number.isEmpty()){
                if (encodedImage.matches("Empty")){
                    Toast.makeText(getContext(),"Please Fill Any one of details",Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    // send only image...
                    Call<forgetpassword_response> call = RetrofitClient
                            .getmInstance()
                            .getApi()
                            .updateImage(userid,shop,encodedImage);
                    call.enqueue(new Callback<forgetpassword_response>() {
                        @Override
                        public void onResponse(Call<forgetpassword_response> call, final Response<forgetpassword_response> response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.code() == 200){
                                            Toast.makeText(getContext(),"User Profile Image Changed Successfully",Toast.LENGTH_LONG).show();
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
            else{
                if (encodedImage.matches("Empty")){
                    // send only number
                    Call<forgetpassword_response> call = RetrofitClient
                            .getmInstance()
                            .getApi()
                            .updateNumber(userid,shop,number);

                    call.enqueue(new Callback<forgetpassword_response>() {
                        @Override
                        public void onResponse(Call<forgetpassword_response> call, final Response<forgetpassword_response> response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.code() == 200){
                                            Toast.makeText(getContext(),"User Phone Number Changed Successfully",Toast.LENGTH_LONG).show();
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
                else{
                    // send number and image...
                    Call<forgetpassword_response> call = RetrofitClient
                            .getmInstance()
                            .getApi()
                            .updateNumbandImage(userid,shop,number,encodedImage);

                    call.enqueue(new Callback<forgetpassword_response>() {
                        @Override
                        public void onResponse(Call<forgetpassword_response> call, final Response<forgetpassword_response> response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.code() == 200){
                                            Toast.makeText(getContext(),"User Phone Number and Image Changed Successfully",Toast.LENGTH_LONG).show();
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
        }
        else{
            if (number.isEmpty()){
                if (encodedImage.matches("Empty")){
                    //send only name...

                    Call<forgetpassword_response> call = RetrofitClient
                            .getmInstance()
                            .getApi()
                            .updateName(userid,shop,name);

                    call.enqueue(new Callback<forgetpassword_response>() {
                        @Override
                        public void onResponse(Call<forgetpassword_response> call, final Response<forgetpassword_response> response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.code() == 200){
                                            Toast.makeText(getContext(),"User Name Changed Successfully",Toast.LENGTH_LONG).show();
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
                else{
                    // send name and image

                    Call<forgetpassword_response> call = RetrofitClient
                            .getmInstance()
                            .getApi()
                            .updateNameandImage(userid,shop,name,encodedImage);

                    call.enqueue(new Callback<forgetpassword_response>() {
                        @Override
                        public void onResponse(Call<forgetpassword_response> call, final Response<forgetpassword_response> response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.code() == 200){
                                            Toast.makeText(getContext(),"User Name and Image Changed Successfully",Toast.LENGTH_LONG).show();
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
            else{
                if (encodedImage.matches("Empty")){
                    // send name and number...

                    Call<forgetpassword_response> call = RetrofitClient
                            .getmInstance()
                            .getApi()
                            .updateNameandNumber(userid,shop,name,encodedImage);

                    call.enqueue(new Callback<forgetpassword_response>() {
                        @Override
                        public void onResponse(Call<forgetpassword_response> call, final Response<forgetpassword_response> response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.code() == 200){
                                            Toast.makeText(getContext(),"User Name and Number Changed Successfully",Toast.LENGTH_LONG).show();
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
                else{
                    // send all name, number and image...

                    Call<forgetpassword_response> call = RetrofitClient
                            .getmInstance()
                            .getApi()
                            .updateProfile(userid ,shop, name , encodedImage,number);

                    call.enqueue(new Callback<forgetpassword_response>() {
                        @Override
                        public void onResponse(Call<forgetpassword_response> call, final Response<forgetpassword_response> response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        if (response.code() == 200) {
                                            forgetpassword_response rep=response.body();
                                            if (rep.getStatus().matches("success")){
                                                Toast.makeText(getContext(),"Record Successfully changed",Toast.LENGTH_LONG).show();
                                                EditNameFragment frag= new EditNameFragment();
                                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
                                            }
                                        }
                                        else {
                                            String s=response.errorBody().string();
                                            JSONObject temp=new JSONObject(s);
                                            Toast.makeText(getContext(),"Error: "+temp.get("message"),Toast.LENGTH_LONG).show();
                                        }

                                    }
                                    catch (IOException | JSONException e) {
                                        Toast.makeText(getContext(), "Error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<forgetpassword_response> call, Throwable t) {

                        }
                    });
                }
            }

        }

    }


}
