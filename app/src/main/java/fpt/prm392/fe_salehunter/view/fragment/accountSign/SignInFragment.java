package fpt.prm392.fe_salehunter.view.fragment.accountSign;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.databinding.FragmentSignInBinding;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.UserModel;
import fpt.prm392.fe_salehunter.util.DialogsProvider;
import fpt.prm392.fe_salehunter.util.SharedPrefManager;
import fpt.prm392.fe_salehunter.util.TextFieldValidator;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import fpt.prm392.fe_salehunter.view.activity.AccountSign;
import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.view.activity.MainActivity;
import fpt.prm392.fe_salehunter.viewmodel.fragment.accountSign.SignInViewModel;

public class SignInFragment extends Fragment {
    private FragmentSignInBinding vb;
    private SignInViewModel viewModel;
    private NavController navController;

    public SignInFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(vb==null) vb = FragmentSignInBinding.inflate(inflater,container,false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(((AccountSign) getActivity()).isBackButtonVisible()) {
            ((AccountSign) getActivity()).setTitle(getString(R.string.Sign_In));
            ((AccountSign) getActivity()).setBackButton(false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(viewModel!=null) return;

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        if(viewModel.googleServicesNotAvailable()) vb.signInSocialAuth.setVisibility(View.GONE);

        vb.signInEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.signInEmail.getEditText().getText().length()>0){

                    if(TextFieldValidator.isValidEmail(editable.toString())) vb.signInEmail.setError(null);
                    else vb.signInEmail.setError(getString(R.string.Email_not_complete_or_not_valid));

                }
                else vb.signInEmail.setError(null);

            }
        });

        vb.signInPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.signInPassword.getEditText().getText().length()>0){
                    if(TextFieldValidator.isValidPassword(editable.toString())) vb.signInPassword.setError(null);
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.PASSWORD_MIN,TextFieldValidator.NO_MAX_VALUE)) vb.signInPassword.setError(getString(R.string.Password_Minimum_length_is) +  " "+ TextFieldValidator.PASSWORD_MIN);
                    else vb.signInPassword.setError(getString(R.string.Password_must_contain));
                }
                else vb.signInPassword.setError(null);

            }
        });

        vb.signInButton.setOnClickListener(button -> {
            if(isDataValid()) signIn();
        });


        vb.signInFacebook.setOnClickListener(button -> {
                facebookAuth();
        });

        vb.signInGoogle.setOnClickListener(button -> {
                googleAuth();
        });

        vb.signInSignUp.setOnClickListener(button -> {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment);
        });

        vb.signInForgotPassword.setOnClickListener(button -> {
            navController.navigate(R.id.action_signInFragment_to_forgotPasswordFragment);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) getGoogleAuthResult(data);
    }

    boolean isDataValid(){
        boolean validData = true;

        if(vb.signInPassword.getError()!=null || vb.signInPassword.getEditText().getText().length()==0){
            vb.signInPassword.requestFocus();
            vb.signInPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        if(vb.signInEmail.getError()!=null || vb.signInEmail.getEditText().getText().length()==0){
            vb.signInEmail.requestFocus();
            vb.signInEmail.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        return validData;
    }

    void signIn(){
        DialogsProvider.get(getActivity()).setLoading(true);

        viewModel.signIn(vb.signInEmail.getEditText().getText().toString(), vb.signInPassword.getEditText().getText().toString())
                .observe(getViewLifecycleOwner(), response -> {

                    DialogsProvider.get(getActivity()).setLoading(false);

                    switch (response.code()){
                        case BaseResponseModel.SUCCESSFUL_OPERATION:
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.putExtra(MainActivity.JUST_SIGNED_IN,true);

                            SharedPrefManager.get(getContext()).setRememberMe(vb.signInRememberMe.isChecked());
                            // Add null safety check for response body and extract accessToken correctly
                            if (response.body() != null && response.body().getData() != null && 
                                response.body().getData().getUser() != null && 
                                response.body().getData().getAccessToken() != null) {
                                UserAccountManager.signInWithTokens(getActivity(), intent, 
                                        response.body().getData().getAccessToken(), 
                                        response.body().getData().getRefreshToken(), 
                                        response.body().getData().getUser());
                            }
                            break;

                        case BaseResponseModel.FAILED_NOT_FOUND:
                            DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Welcome), getString(R.string.you_dont_have_an_account));
                            break;

                        case BaseResponseModel.FAILED_AUTH:
                            DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Wrong_Password), getString(R.string.You_can_reset_password));
                            break;

                        case BaseResponseModel.FAILED_REQUEST_FAILURE:
                            DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Request_Error), getString(R.string.Please_Check_your_connection));
                            break;

                        default:
                            DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
                    }

                });

    }

    void facebookAuth(){
        DialogsProvider.get(getActivity()).setLoading(true);

        viewModel.facebookAuth(SignInFragment.this).observe(getViewLifecycleOwner(),response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                case BaseResponseModel.SUCCESSFUL_CREATION:
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra(MainActivity.JUST_SIGNED_IN,true);

                    UserModel userModel = new UserModel();
                    userModel.setId(Long.parseLong(viewModel.getFacebookSocialAuthModel().getClientId()));
                    userModel.setEmail(viewModel.getFacebookSocialAuthModel().getEmail());
                    userModel.setFullName(viewModel.getFacebookSocialAuthModel().getFullName());
                    userModel.setImageUrl(viewModel.getFacebookSocialAuthModel().getImage());
                    userModel.setSignedInWith(UserModel.SIGNED_IN_WITH_FACEBOOK);

                    UserAccountManager.signIn(getActivity(), intent, response.headers().get(Repository.AUTH_TOKEN_HEADER), userModel);
                    break;

                case BaseResponseModel.FAILED_INVALID_DATA:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Authentication_Failed), getString(R.string.please_try_again_later));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Request_Error), getString(R.string.Please_Check_your_connection));
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }

        });
    }

    void googleAuth(){
        DialogsProvider.get(getActivity()).setLoading(true);
        viewModel.googleAuth(SignInFragment.this);
    }

    void getGoogleAuthResult(Intent data){
        viewModel.googleAuthOnActivityResult(data,SignInFragment.this).observe(getViewLifecycleOwner(), response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                case BaseResponseModel.SUCCESSFUL_CREATION:
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra(MainActivity.JUST_SIGNED_IN,true);

                    UserModel userModel = new UserModel();
                    userModel.setId(Long.parseLong(viewModel.getGoogleSocialAuthModel().getClientId()));
                    userModel.setEmail(viewModel.getGoogleSocialAuthModel().getEmail());
                    userModel.setFullName(viewModel.getGoogleSocialAuthModel().getFullName());
                    userModel.setImageUrl(viewModel.getGoogleSocialAuthModel().getImage());
                    userModel.setSignedInWith(UserModel.SIGNED_IN_WITH_GOOGLE);

                    UserAccountManager.signIn(getActivity(), intent, response.headers().get(Repository.AUTH_TOKEN_HEADER), userModel);
                    break;

                case BaseResponseModel.FAILED_INVALID_DATA:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Authentication_Failed), getString(R.string.Please_try_again));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Request_Error), getString(R.string.Please_Check_your_connection));
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }

        });
    }


}