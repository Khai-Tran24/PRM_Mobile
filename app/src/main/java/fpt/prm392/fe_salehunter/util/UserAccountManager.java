package fpt.prm392.fe_salehunter.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.model.user.UserModel;
import fpt.prm392.fe_salehunter.view.activity.AccountSign;

public final class UserAccountManager {
    private static UserModel user;

    //Token Type Parameters
    public static final int TOKEN_TYPE_NORMAL = 0;
    public static final int TOKEN_TYPE_BEARER = 1;
    public static final String FORCED_SIGN_OUT = "forcedSignOut";

    public static void signIn(Activity activity, Intent intent, String token, UserModel userModel){
        signInWithTokens(activity, intent, token, null, userModel);
    }

    public static void signInWithTokens(Activity activity, Intent intent, String accessToken, String refreshToken, UserModel userModel){
        user = userModel;
        Context context = activity.getApplicationContext();
        //Save User Data
        SharedPrefManager.get(context).setSignedIn(true);
        SharedPrefManager.get(context).setToken(accessToken);
        if (refreshToken != null) {
            SharedPrefManager.get(context).setRefreshToken(refreshToken);
        }
        SharedPrefManager.get(context).setUser(userModel);

        //Launch Main Activity
        activity.startActivity(intent);
        activity.onBackPressed();
    }

    public static UserModel getUser(Context context){
        if(user == null) user = SharedPrefManager.get(context).getUser();
        return user;
    }

    public static void updateUser(Context context, UserModel userModel){
        user = userModel;
        SharedPrefManager.get(context).setUser(user);
    }

    public static String getToken(Context context, int type){
        String output ="";
        switch (type){
            case TOKEN_TYPE_NORMAL:
                output = SharedPrefManager.get(context).getToken();
                break;

            case TOKEN_TYPE_BEARER:
                output = "Bearer " + SharedPrefManager.get(context).getToken();
                break;
        }

        return output;
    }

    public static String getRefreshToken(Context context){
        return SharedPrefManager.get(context).getRefreshToken();
    }

    public static void signOut(Activity activity, boolean forced){
        DialogsProvider.get(activity).setLoading(true);

        Context context = activity.getApplicationContext();

        //clear user account data
        SharedPrefManager.get(context).setSignedIn(false);
        SharedPrefManager.get(context).setToken("");
        SharedPrefManager.get(context).setRefreshToken("");
        SharedPrefManager.get(context).setUser(null);

        //signout from facebook
        if(AccessToken.getCurrentAccessToken()!=null) LoginManager.getInstance().logOut();

        //signout from google
        if(GoogleSignIn.getLastSignedInAccount(context) != null){

            GoogleSignIn.getClient(context, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
            ).signOut();

        }

        DialogsProvider.get(activity).setLoading(false);

        //Launch Account Sign Activity
        Intent intent = new Intent(context, AccountSign.class);
        intent.putExtra(FORCED_SIGN_OUT,forced);
        activity.startActivity(intent);
        activity.finish();
    }


}
