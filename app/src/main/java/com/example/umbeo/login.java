package com.example.umbeo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.umbeo.Storage.SharedprefManager;
import com.example.umbeo.api.RetrofitClient;
import com.example.umbeo.response_data.LoginResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class login extends AppCompatActivity {

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Button  login;
    TextView signu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        signu=(TextView)findViewById(R.id.signin);

        signu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, com.example.umbeo.signup.class));
            }
        });
        login = (Button) findViewById(R.id.button);

        final TextView forgotpassword = (TextView) findViewById(R.id.forget);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, forget_password.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userlogin();
            }
        });
    }

    private void userlogin() {
        EditText uname = (EditText) findViewById(R.id.username);
        EditText pass = (EditText) findViewById(R.id.pass);
        TextView err = (TextView) findViewById(R.id.status);

        String email = uname.getText().toString();
        String password = pass.getText().toString();

        login.setEnabled(false);

        if (email.isEmpty() && password.isEmpty()) {
            err.setBackgroundColor(Color.parseColor("#f0f8ff"));
            err.setText("Please Enter Email and password");
            login.setEnabled(true);
            return;
        }

        if (email.isEmpty()) {
            err.setBackgroundColor(Color.parseColor("#f0f8ff"));
            err.setText("Please Enter Email");
            login.setEnabled(true);
            return;
        }
        if (!(email.matches(emailPattern))) {
            err.setBackgroundColor(Color.parseColor("#f0f8ff"));
            err.setText("Please Enter valid Email");
            login.setEnabled(true);
            return;
        }
        if (password.isEmpty()) {
            err.setBackgroundColor(Color.parseColor("#f0f8ff"));
            login.setEnabled(true);
            err.setText("Please Enter a Password");
            return;
        }

        Call<LoginResponse> call = RetrofitClient
                .getmInstance()
                .getApi()
                .userLogin(email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if (response.code() == 200) {

                                LoginResponse loginResponse = response.body();
                                if (loginResponse.getStatus().toString().matches("success")) {


                                    String id = loginResponse.getToken();
                                    SharedprefManager.getInstance(com.example.umbeo.login.this)
                                            .saveToken(loginResponse.getToken());
                                   // Toast.makeText(getApplicationContext(),"ID: "+ id,Toast.LENGTH_LONG).show();
                                    Toast.makeText(com.example.umbeo.login.this,"Login Successful",Toast.LENGTH_LONG).show();
                                    login.setEnabled(true);
                                    Toast.makeText(com.example.umbeo.login.this,"Go to Cart to complete Payment",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(),Example.class));
                                    // startActivity(new Intent(login.this, shopscreen.class));
                                }
                                else{
                                    login.setEnabled(true);
                                }

                            } else {
                                String s = response.errorBody().string();
                                JSONObject temp = new JSONObject(s);
                                Toast.makeText(getApplicationContext(), "Error: " + temp.get("message"), Toast.LENGTH_LONG).show();
                                login.setEnabled(true);

                            }
                        } catch (IOException | JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                            login.setEnabled(true);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                login.setEnabled(true);
            }
        });
    }
}
