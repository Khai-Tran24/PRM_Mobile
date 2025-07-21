package fpt.prm392.fe_salehunter.util;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.databinding.ActivityMainBinding;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.user.UserModel;
import fpt.prm392.fe_salehunter.viewmodel.activity.MainActivityViewModel;

/**
 * Helper class for managing user data operations in MainActivity
 */
public class UserDataManager {
    
    /**
     * Sync user data from server
     */
    public static void syncUserData(Context context, MainActivityViewModel viewModel, 
                                   LifecycleOwner lifecycleOwner, UserDataCallback callback) {
        try {
            UserModel currentUser = UserAccountManager.getUser(context);
            if (currentUser == null || currentUser.getSignedInWith() != UserModel.SIGNED_IN_WITH_EMAIL) {
                return;
            }

            String token = UserAccountManager.getToken(context, UserAccountManager.TOKEN_TYPE_BEARER);
            if (token == null || token.isEmpty()) {
                if (context instanceof android.app.Activity) {
                    UserAccountManager.signOut((android.app.Activity) context, true);
                }
                return;
            }

            viewModel.getUser(token).observe(lifecycleOwner, response -> {
                try {
                    if (response == null) {
                        return;
                    }

                    switch (response.code()) {
                        case BaseResponseModel.SUCCESSFUL_OPERATION:
                            if (response.body() != null && response.body().getData() != null) {
                                UserModel user = response.body().getData();
                                UserAccountManager.updateUser(context, user);
                                if (callback != null) {
                                    callback.onUserDataSynced(user);
                                }
                            }
                            break;

                        case BaseResponseModel.FAILED_AUTH:
                            if (context instanceof android.app.Activity) {
                                UserAccountManager.signOut((android.app.Activity) context, true);
                            }
                            break;

                        case BaseResponseModel.FAILED_REQUEST_FAILURE:
                            // Handle sync failure silently
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Load user data into UI
     */
    public static void loadUserDataToUI(Context context, ActivityMainBinding binding, UserModel userModel) {
        try {
            UserModel user = userModel != null ? userModel : UserAccountManager.getUser(context);
            
            if (user == null) {
                if (context instanceof android.app.Activity) {
                    UserAccountManager.signOut((android.app.Activity) context, true);
                }
                return;
            }

            // Safe loading with null checks
            String username = user.getFullName();
            String accountType = user.getAccountType();
            String imageLink = user.getImageLink();

            if (binding != null) {
                if (binding.menuUsername != null && username != null) {
                    binding.menuUsername.setText(username);
                }
                
                if (binding.menuAccountType != null && accountType != null) {
                    binding.menuAccountType.setText(accountType);
                }

                if (binding.menuProfilePic != null) {
                    Glide.with(context)
                        .load(imageLink != null ? imageLink : "")
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(50))
                        .placeholder(R.drawable.profile_placeholder)
                        .circleCrop()
                        .into(binding.menuProfilePic);
                }

                if (binding.menuDashboard != null) {
                    if (user.hasStore()) {
                        binding.menuDashboard.setVisibility(View.VISIBLE);
                    } else {
                        binding.menuDashboard.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (context instanceof android.app.Activity) {
                UserAccountManager.signOut((android.app.Activity) context, true);
            }
        }
    }
    
    /**
     * Interface for user data sync callback
     */
    public interface UserDataCallback {
        void onUserDataSynced(UserModel user);
    }
}
