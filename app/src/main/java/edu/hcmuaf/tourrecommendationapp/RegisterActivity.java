package edu.hcmuaf.tourrecommendationapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import edu.hcmuaf.tourrecommendationapp.dto.LoginRequest;
import edu.hcmuaf.tourrecommendationapp.dto.LoginResponse;
import edu.hcmuaf.tourrecommendationapp.dto.RegisterRequest;
import edu.hcmuaf.tourrecommendationapp.service.AuthService;
import edu.hcmuaf.tourrecommendationapp.util.ApiClient;
import edu.hcmuaf.tourrecommendationapp.util.SharedPrefs;
import edu.hcmuaf.tourrecommendationapp.util.Utils;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {
    private EditText txtName, txtUsername, txtPassword;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();

        txtName = findViewById(R.id.editTextName);
        txtUsername = findViewById(R.id.editTextUsername);
        txtPassword = findViewById(R.id.editTextPassword);

        //DI
        authService = AuthService.getInstance();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void onRegister(View v) {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String name = txtName.getText().toString();


        try {
            submit(new RegisterRequest(name, username, password));
        } catch (Exception e) {
            Toast.makeText(this, "Network error try again", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private void submit(RegisterRequest registerRequest) throws Exception {
        if (!authService.register(registerRequest)) {
            Toast.makeText(this, "Username or password at least 6 character", Toast.LENGTH_LONG).show();
            return;
        }

        //redirect to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
