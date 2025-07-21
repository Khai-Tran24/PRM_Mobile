package fpt.prm392.fe_salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;

import com.facebook.FacebookSdk;

import fpt.prm392.fe_salehunter.BuildConfig;
import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.databinding.ActivityMainBinding;
import fpt.prm392.fe_salehunter.model.user.UserModel;
import fpt.prm392.fe_salehunter.util.DeepLinkHelper;
import fpt.prm392.fe_salehunter.util.LocaleHelper;
import fpt.prm392.fe_salehunter.util.NetworkBroadcastReceiver;
import fpt.prm392.fe_salehunter.util.SharedPrefManager;
import fpt.prm392.fe_salehunter.util.ThemeHelper;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import fpt.prm392.fe_salehunter.util.UserDataManager;
import fpt.prm392.fe_salehunter.view.UnderlayNavigationDrawer;
import fpt.prm392.fe_salehunter.view.fragment.dialogs.ChatOverlayFragment;
import fpt.prm392.fe_salehunter.viewmodel.activity.MainActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private ActivityMainBinding vb;
    private MainActivityViewModel viewModel;
    private UnderlayNavigationDrawer underlayNavigationDrawer;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private FloatingActionButton fabChat;
    private ChatOverlayFragment chatOverlayFragment;

    public static final String JUST_SIGNED_IN = "justSignedIn";

    private UserModel user;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocaleToContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme settings
        ThemeHelper.applyTheme(this);

        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);

        vb = ActivityMainBinding.inflate(getLayoutInflater());
        View view = vb.getRoot();
        setContentView(view);

        overridePendingTransition(R.anim.lay_on, R.anim.lay_off);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        navController = Navigation.findNavController(this, R.id.main_FragmentContainer);

        boolean firstLaunch = SharedPrefManager.get(this).isFirstLaunch();
        boolean rememberMe = SharedPrefManager.get(this).isRememberMeChecked();
        boolean signedIn = SharedPrefManager.get(this).isSignedIn(); // Fixed: Use actual sign-in state
        String token = UserAccountManager.getToken(this, UserAccountManager.TOKEN_TYPE_BEARER);
        user = UserAccountManager.getUser(this);

        boolean justSignedIn = getIntent().getBooleanExtra(JUST_SIGNED_IN, false);

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

            //Initialize chat functionality
            setupChatButton();

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
            // Handle deep links
            DeepLinkHelper.handleDeepLink(this, getIntent(), navController);

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
        DeepLinkHelper.navigateToFragment(navController, id, underlayNavigationDrawer.getAnimationDuration());
    }

    public void syncUserData() {
        UserDataManager.syncUserData(this, viewModel, this, user -> loadUserData(user));
    }

    public void loadUserData(UserModel userModel) {
        UserDataManager.loadUserDataToUI(this, vb, userModel);
        if (userModel != null) {
            this.user = userModel;
        }
    }

    public void changeLocale() {
        LocaleHelper.updateLocaleConfiguration(this);
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

    private void setupChatButton() {
        fabChat = vb.fabChat;
        
        // Set up chat button click listener
        fabChat.setOnClickListener(v -> {
            openChatOverlay();
        });

        // Handle swipe to hide functionality
        setupChatButtonGestures();
    }

    private void setupChatButtonGestures() {
        fabChat.setOnTouchListener(new View.OnTouchListener() {
            private float initialX;
            private float initialTouchX;
            private float initialTouchY;
            private final int SWIPE_THRESHOLD = 100;
            private final int SWIPE_VELOCITY_THRESHOLD = 100;
            private boolean isMoving = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = v.getX();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        isMoving = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - initialTouchX;
                        float deltaY = event.getRawY() - initialTouchY;
                        
                        // Check if user is moving the FAB
                        if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10) {
                            isMoving = true;
                        }
                        
                        // Move the FAB horizontally only if swiping right
                        if (deltaX > 0) {
                            v.setX(initialX + deltaX);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        float finalDeltaX = event.getRawX() - initialTouchX;
                        
                        if (isMoving && finalDeltaX > SWIPE_THRESHOLD) {
                            // Hide the FAB by moving it off screen
                            v.animate()
                                .translationX(v.getWidth() + 100)
                                .alpha(0.3f)
                                .setDuration(300)
                                .start();
                        } else {
                            // Return to original position
                            v.animate()
                                .x(initialX)
                                .alpha(1.0f)
                                .setDuration(200)
                                .start();
                            
                            // If it wasn't moved much, treat as click
                            if (!isMoving) {
                                v.performClick();
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        // Double tap to show if hidden
        fabChat.setOnClickListener(v -> {
            if (v.getAlpha() < 1.0f) {
                // Show the FAB
                v.animate()
                    .translationX(0)
                    .alpha(1.0f)
                    .setDuration(300)
                    .start();
            } else {
                openChatOverlay();
            }
        });
    }

    private void openChatOverlay() {
        if (chatOverlayFragment == null) {
            chatOverlayFragment = new ChatOverlayFragment();
            chatOverlayFragment.setChatOverlayListener(new ChatOverlayFragment.ChatOverlayListener() {
                @Override
                public void onChatClosed() {
                    closeChatOverlay();
                }
            });
        }

        // Show the overlay
        vb.chatOverlayContainer.setVisibility(View.VISIBLE);
        
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.chat_overlay_container, chatOverlayFragment)
                .commit();
    }

    private void closeChatOverlay() {
        vb.chatOverlayContainer.setVisibility(View.GONE);
        
        if (chatOverlayFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(chatOverlayFragment)
                    .commit();
        }
    }

}