package edu.hcmuaf.tourrecommendationapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.hcmuaf.tourrecommendationapp.dto.LoginRequest;
import edu.hcmuaf.tourrecommendationapp.service.AuthService;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText txtUsername, txtPassword;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (SDK_INT > 8) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        setContentView(R.layout.activity_login);
        txtUsername = findViewById(R.id.editTextUsername);
        txtPassword = findViewById(R.id.editTextPassword);

        //DI
        authService = AuthService.getInstance();
    }

    public void onLogin(View view) {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

//        try {
//            submit(new LoginRequest(username, password));
//        } catch (Exception e) {
//            Toast.makeText(this, "Network error try again", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }
        submit(new LoginRequest(username, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        if (!aBoolean) {
                            Toast.makeText(getBaseContext(), "Username or password invalid", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(getBaseContext(), "Network error try again", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

//    public void submit(LoginRequest loginRequest) throws Exception {
//        if (!authService.login(loginRequest)) {
//            Toast.makeText(this, "Username or password invalid", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        //redirect to MainActivity
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//    }

    public Observable<Boolean> submit(LoginRequest loginRequest){
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter){
                try {
                    boolean isSuccess = authService.login(loginRequest);
                    emitter.onNext(isSuccess);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }


    public void onRegisterClick(View View) {
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.fade_in);
    }


}





