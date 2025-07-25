package fpt.prm392.fe_salehunter.view.fragment.dialogs;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.databinding.FragmentEmailVerificationDialogBinding;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.util.DialogsProvider;
import fpt.prm392.fe_salehunter.viewmodel.fragment.dialogs.EmailVerificationDialogViewModel;

public class EmailVerificationDialog extends BottomSheetDialogFragment {
    private FragmentEmailVerificationDialogBinding vb;
    private EmailVerificationDialogViewModel viewModel;

    private EditText[] code;
    private String email, titleText, subTitleText;

    DialogResultListener dialogResultListener;

    public EmailVerificationDialog() {

    }

    public interface DialogResultListener{
        void onSuccess();
        void onCancel();
    }

    public void setDialogResultListener(DialogResultListener dialogResultListener){
        this.dialogResultListener = dialogResultListener;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMessage(String titleText, String subTitleText) {
       this.titleText = titleText;
       this.subTitleText = subTitleText;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentEmailVerificationDialogBinding.inflate(inflater,container,false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EmailVerificationDialogViewModel.class);

        vb.emailVerificationDialogTitle.setText(titleText);
        vb.emailVerificationDialogSubtitle.setText(subTitleText);

        sendEmailVerification();

        code = new EditText[6];
        code[0]=vb.verificationDialogCodeNum;
        code[1]=vb.verificationDialogCodeNum1;
        code[2]=vb.verificationDialogCodeNum2;
        code[3]=vb.verificationDialogCodeNum3;
        code[4]=vb.verificationDialogCodeNum4;
        code[5]=vb.verificationDialogCodeNum5;

        code[0].requestFocus();
        code[0].clearFocus();

        for(int i=0; i<code.length; i++){

            final int index = i;

            if(index < code.length-1){

                code[index].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!editable.toString().isEmpty()){
                            code[index+1].requestFocus();
                            code[index].setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.written_code,getActivity().getTheme()));
                        }
                        else code[index].setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.empty_code,getActivity().getTheme()));

                    }
                });

            }
            else{

                code[index].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!editable.toString().isEmpty()){
                            code[index].clearFocus();
                            code[index].setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.written_code,getActivity().getTheme()));
                        }
                        else code[index].setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.empty_code,getActivity().getTheme()));
                    }
                });

            }

            code[index].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        code[index].selectAll();
                        code[index].animate().scaleX(1.2f).scaleY(1.2f).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                    } else code[index].animate().scaleX(1.0f).scaleY(1.0f).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();

                }
            });

        }

        vb.verificationDialogCodeResend.setOnClickListener(button -> resendPinToEmail());

        vb.verificationDialogCodeButton.setOnClickListener(button -> {
            if(isDataValid()) verifyPin();
        });

    }

    boolean isDataValid(){
        boolean validData = true;

        for(EditText block:code){
            if(block.getText().toString().isEmpty()){
                block.requestFocus();
                block.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
                validData=false;
                break;
            }
        }

        return validData;
    }

    public void sendEmailVerification(){

        viewModel.sendEmailVerification(email).observe(getViewLifecycleOwner(), response ->{
            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    dismiss();
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Email_Verification_Request_Failed), getString(R.string.Please_Check_your_connection));
                    break;

                default:
                    dismiss();
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }
        });

    }

    public void resendPinToEmail(){

        viewModel.sendEmailVerification(email).observe(getViewLifecycleOwner(), response ->{
            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:

                    Snackbar snackbar = Snackbar.make(vb.verificationDialogCodeSnackbarColayout, R.string.New_PIN_is_on_the_way,15000);
                    snackbar.getView().setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.snackbar,getActivity().getTheme()));
                    snackbar.setBackgroundTint(getResources().getColor(R.color.lightModesecondary,getActivity().getTheme()));
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.show();
                    break;

                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Email_isnt_registered), getString(R.string.you_dont_have_an_account));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Request_Failed), getString(R.string.please_check_your_internet_connection));
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }
        });

    }

    void verifyPin(){
        DialogsProvider.get(getActivity()).setLoading(true);

        StringBuilder pin = new StringBuilder();
        for(EditText digit : code) pin.append(digit.getText().toString());

        viewModel.verifyToken(pin.toString()).observe(getViewLifecycleOwner(), response ->{
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                //valid
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if(dialogResultListener!=null) dialogResultListener.onSuccess();
                    dismiss();
                    break;

                //expired
                case BaseResponseModel.FAILED_INVALID_DATA:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Pin_is_expired), getString(R.string.Please_resend_pin_code_again));
                    break;

                //invalid
                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Pin_is_invalid), getString(R.string.Please_check_it_and_try_again));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Request_Error), getString(R.string.please_check_your_internet_connection));
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }

        });
    }

}