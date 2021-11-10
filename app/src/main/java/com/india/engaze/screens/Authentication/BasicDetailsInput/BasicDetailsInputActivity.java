package com.india.engaze.screens.Authentication.BasicDetailsInput;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.india.engaze.R;
import com.india.engaze.screens.Splash.SplashActivity;
import com.india.engaze.screens.base.BaseActivity;
import com.india.engaze.utils.Functions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BasicDetailsInputActivity extends BaseActivity implements BasicDetailsInputContract.View, FABProgressListener, AdapterView.OnItemSelectedListener {

    @BindView(R.id.fabProgress)
    FABProgressCircle fabProgressCircle;


    @BindView(R.id.fab)
    FloatingActionButton fab;


    @BindView(R.id.email)
    TextInputEditText email;
    @BindView(R.id.name)
    TextInputEditText name;

    @BindView(R.id.emailFrame)
    TextInputLayout emailFrame;

    @BindView(R.id.nameFrame)
    TextInputLayout nameFrame;

    @BindView(R.id.gender_spinner)
    Spinner genderSpinner;

    @Inject
    BasicDetailsInputContract.Presenter<BasicDetailsInputContract.View> presenter;


    private String[] gender = {"Select Gender", "Female", "Male"};
    private int selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_details);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        presenter.onAttach(this);
        setup();
    }

    private void setup() {
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(aa);
        genderSpinner.setOnItemSelectedListener(this);

//        UserData user = AppController.getInstance().getmSessionManager().getUser();
//
//        if (user.getName() != null) {
//            name.setText(Functions.capitalize(user.getName()));
//        }
//
//        if (user.getEmail() != null) {
//            email.setText(user.getEmail());
//        }
//
//        if (user.getGender() != null) {
//            if (user.getGender().equals("1")) {
//                genderSpinner.setSelection(2);
//            } else {
//                genderSpinner.setSelection(1);
//            }
//        }

//        fab.setOnClickListener(v -> {
//            if (!getIsLoading()) {
//                presenter.updateDetails(name.getText().toString(), email.getText().toString(), selectedGender, user);
//            }
//        });

        fabProgressCircle.attachListener(this);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String emailAddress = charSequence.toString().trim();
                if (Functions.isValidEmail(emailAddress)) emailFrame.setErrorEnabled(false);
                else emailFrame.setError(getString(R.string.email_not_valid));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    @Override
    public void onError(String message) {
        super.onError(message);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        fabProgressCircle.show();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        fabProgressCircle.hide();
    }

    @Override
    public void onFABProgressAnimationEnd() {
        Intent intent = new Intent(BasicDetailsInputActivity.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedGender = position - 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void nameError() {
        Functions.shakeView(nameFrame, this);
    }

    @Override
    public void genderError() {
        Functions.shakeView(genderSpinner, this);
    }

    @Override
    public void profileUpdatedSuccessfully() {
        fabProgressCircle.beginFinalAnimation();
    }

    @Override
    public void invalidEmail() {
        Functions.shakeView(emailFrame, this);
    }
}
