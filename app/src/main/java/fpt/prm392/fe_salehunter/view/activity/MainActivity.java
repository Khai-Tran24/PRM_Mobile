package fpt.prm392.fe_salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.FacebookSdk;

import java.util.Locale;

import fpt.prm392.fe_salehunter.BuildConfig;
import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.databinding.ActivityMainBinding;
import fpt.prm392.fe_salehunter.model.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.UserModel;
import fpt.prm392.fe_salehunter.util.AppSettingsManager;
import fpt.prm392.fe_salehunter.util.NetworkBroadcastReceiver;
import fpt.prm392.fe_salehunter.util.SharedPrefManager;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import fpt.prm392.fe_salehunter.view.UnderlayNavigationDrawer;
import fpt.prm392.fe_salehunter.viewmodel.activity.MainActivityViewModel;


public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private ActivityMainBinding vb;
    private MainActivityViewModel viewModel;
    private UnderlayNavigationDrawer underlayNavigationDrawer;
    private NetworkBroadcastReceiver networkBroadcastReceiver;

    public static final String JUST_SIGNED_IN = "justSignedIn";

    private boolean rememberMe;
    private boolean firstLaunch;
    private boolean signedIn;
    private boolean justSignedIn;
    private String token;
    private UserModel user;

    @Override
    protected void attachBaseContext(Context newBase) {
        String language;
        if (AppSettingsManager.isLanguageSystemDefault(newBase))
            language = Locale.getDefault().getLanguage();
        else language = AppSettingsManager.getLanguageKey(newBase);

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        newBase.getResources().getConfiguration().setLocale(locale);
        newBase.getResources().getConfiguration().setLayoutDirection(locale);

        super.attachBaseContext(newBase);
    }

    void changeLocale() {
        String language;
        if (AppSettingsManager.isLanguageSystemDefault(this))
            language = Locale.getDefault().getLanguage();
        else language = AppSettingsManager.getLanguageKey(this);

        Locale locale = new Locale(language);
        Configuration config = getResources().getConfiguration();
        Locale.setDefault(locale);
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set App Settings
        switch (AppSettingsManager.getTheme(this)) {
            case AppSettingsManager.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case AppSettingsManager.THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) changeLocale();

        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);

        vb = ActivityMainBinding.inflate(getLayoutInflater());
        View view = vb.getRoot();
        setContentView(view);

        overridePendingTransition(R.anim.lay_on, R.anim.lay_off);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        navController = Navigation.findNavController(this, R.id.main_FragmentContainer);

        firstLaunch = SharedPrefManager.get(this).isFirstLaunch();
        rememberMe = SharedPrefManager.get(this).isRememberMeChecked();
        signedIn = SharedPrefManager.get(this).isSignedIn(); // Fixed: Use actual sign-in state
        token = UserAccountManager.getToken(this, UserAccountManager.TOKEN_TYPE_BEARER);
        user = UserAccountManager.getUser(this);

        justSignedIn = getIntent().getBooleanExtra(JUST_SIGNED_IN, false);

        FacebookSdk.setClientToken(getApplication().getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplication());

        if (firstLaunch) {
            startActivity(new Intent(this, AppIntro.class));
            finish();
        } else if (!signedIn || token == null || token.isEmpty()) {
            // Fixed: Check for null/empty token as well
            startActivity(new Intent(this, AccountSign.class));
            finish();
        } else if (!(rememberMe || justSignedIn)) {
            UserAccountManager.signOut(this, true);
        } else {
            // Ensure user object is not null before proceeding
            if (user == null) {
                // If user is null but we're supposedly signed in, sign out and redirect to login
                UserAccountManager.signOut(this, true);
                return;
            }

            // Comment these two lines to skip sign in
            loadUserData(null); //From Local Storage
            if (!justSignedIn) syncUserData(); //From Server

            //Side Menu
            underlayNavigationDrawer = new UnderlayNavigationDrawer(this, vb.menuFrontView, findViewById(R.id.main_FragmentContainer), vb.menuBackView, vb.menuButton);
            vb.menu.setOnCheckedChangeListener((radioGroup, i) -> {

                vb.currentFragmentTitle.setText(((RadioButton) findViewById(i)).getText().toString());
                if (i == R.id.menu_home)
                    navigateToFragment(R.id.homeFragment);
                else if (i == R.id.menu_profile)
                    navigateToFragment(R.id.profileFragment);
                else if (i == R.id.menu_dashboard) {
                    if (user != null) {
                        underlayNavigationDrawer.closeMenu();
                        new Handler().postDelayed(() -> {
                            navController.popBackStack(R.id.dashboardFragment, true);

                            Bundle bundle = new Bundle();
                            bundle.putLong("storeId", user.getStoreId());
                            navController.navigate(R.id.dashboardFragment, bundle, new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
                        }, underlayNavigationDrawer.getAnimationDuration());
                    }
                } else if (i == R.id.menu_mystore) {
                    if (user != null && user.hasStore()) {
                        underlayNavigationDrawer.closeMenu();
                        new Handler().postDelayed(() -> {
                            navController.popBackStack(R.id.storePageFragment, true);

                            Bundle bundle = new Bundle();
                            bundle.putLong("storeId", user.getStoreId());
                            navController.navigate(R.id.storePageFragment, bundle, new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());

                        }, underlayNavigationDrawer.getAnimationDuration());
                    } else {
                        navigateToFragment(R.id.createStoreFragment);
                    }
                } else if (i == R.id.menu_settings)
                    navigateToFragment(R.id.settingsFragment);
                else if (i == R.id.menu_about)
                    navigateToFragment(R.id.aboutFragment);
                else if (i == R.id.menu_signout) {
                    UserAccountManager.signOut(MainActivity.this, false);
                } else
                    navigateToFragment(R.id.underConstructionFragment2);
            });

            //Network Checker
            networkBroadcastReceiver = new NetworkBroadcastReceiver(this);
            registerReceiver(networkBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            //Sale Hunter links detection
            vb.appVersion.setText(BuildConfig.VERSION_NAME);
            Uri appLinkData = getIntent().getData();

            if (appLinkData != null) {
                String url = appLinkData.getPath();
                if (url.lastIndexOf("/") == url.length() - 1)
                    url = url.substring(0, url.lastIndexOf("/"));

                if (appLinkData.getPath().contains("pid=")) {
                    String productId = url.substring(url.indexOf("=") + 1);

                    Bundle bundle = new Bundle();
                    bundle.putLong("productId", Long.parseLong(productId));
                    navController.navigate(R.id.productPageFragment, bundle);
                } else if (appLinkData.getPath().contains("store-profile=")) {
                    String storeId = url.substring(url.indexOf("=") + 1);

                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId", Long.parseLong(storeId));
                    navController.navigate(R.id.storePageFragment, bundle);
                } else if (appLinkData.getPath().equals("/profile")) vb.menuProfile.performClick();
                else if (appLinkData.getPath().equals("/about-us")) vb.menuAbout.performClick();
            }

        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        underlayNavigationDrawer.detectTouch(event);
        return super.onTouchEvent(event);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onBackPressed() {
        if (underlayNavigationDrawer.isOpened()) {
            underlayNavigationDrawer.closeMenu();
        } else if (vb.menu.getCheckedRadioButtonId() != R.id.menu_home && navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == 3) {
            vb.menuHome.setChecked(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            if (networkBroadcastReceiver != null) {
                unregisterReceiver(networkBroadcastReceiver);
            }
        } catch (Exception e) {
            // Log the error but don't crash the app
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
        } catch (Exception e) {
            // Protect against system-level crashes
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
        } catch (Exception e) {
            // Protect against system-level crashes
            e.printStackTrace();
        }
    }

    void navigateToFragment(int id) {
        underlayNavigationDrawer.closeMenu();
        new Handler().postDelayed(() -> {
            navController.popBackStack(id, true);
            navController.navigate(id, null, new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());

        }, underlayNavigationDrawer.getAnimationDuration());
    }

    public void syncUserData() {
        try {
            UserModel currentUser = UserAccountManager.getUser(this);
            if (currentUser == null || currentUser.getSignedInWith() != UserModel.SIGNED_IN_WITH_EMAIL) {
                return;
            }

            if (token == null || token.isEmpty()) {
                UserAccountManager.signOut(MainActivity.this, true);
                return;
            }

            viewModel.getUser(token).observe(this, response -> {
                try {
                    if (response == null) {
                        return;
                    }

                    switch (response.code()) {
                        case BaseResponseModel.SUCCESSFUL_OPERATION:
                            if (response.body() != null && response.body().getUser() != null) {
                                UserModel user = response.body().getUser();
                                UserAccountManager.updateUser(getApplicationContext(), user);
                                loadUserData(user);
                            }
                            break;

                        case BaseResponseModel.FAILED_AUTH:
                            UserAccountManager.signOut(MainActivity.this, true);
                            break;

                        case BaseResponseModel.FAILED_REQUEST_FAILURE:
                            //Toast.makeText(this, "Data Sync Failed !", Toast.LENGTH_SHORT).show();
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

    public void loadUserData(UserModel userModel) {
        try {
            if (userModel != null) {
                user = userModel;
            } else {
                user = UserAccountManager.getUser(this);
            }

            // Additional null safety check
            if (user == null) {
                // If user is still null, sign out and redirect to login
                UserAccountManager.signOut(this, true);
                return;
            }

            // Safe loading with null checks
            String username = user.getFullName();
            String accountType = user.getAccountType();
            String imageLink = user.getImageLink();

            if (vb != null) {
                if (vb.menuUsername != null && username != null) {
                    vb.menuUsername.setText(username);
                }
                
                if (vb.menuAccountType != null && accountType != null) {
                    vb.menuAccountType.setText(accountType);
                }

                if (vb.menuProfilePic != null) {
                    Glide.with(this)
                        .load(imageLink != null ? imageLink : "")
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(50))
                        .placeholder(R.drawable.profile_placeholder)
                        .circleCrop()
                        .into(vb.menuProfilePic);
                }

                if (vb.menuDashboard != null) {
                    if (user.hasStore()) {
                        vb.menuDashboard.setVisibility(View.VISIBLE);
                    } else {
                        vb.menuDashboard.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // If anything goes wrong, sign out safely
            UserAccountManager.signOut(this, true);
        }
    }

    public NavController getAppNavController() {
        return navController;
    }

    public void setTitle(String title) {
        try {
            if (vb != null && vb.currentFragmentTitle != null && title != null) {
                vb.currentFragmentTitle.post(() -> {
                    vb.currentFragmentTitle.setText(title);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}