package fpt.prm392.fe_salehunter.view.fragment.main;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.adapter.SettingsListAdapter;
import fpt.prm392.fe_salehunter.databinding.FragmentAboutBinding;
import fpt.prm392.fe_salehunter.databinding.FragmentSettingsBinding;
import fpt.prm392.fe_salehunter.util.AppSettingsManager;
import fpt.prm392.fe_salehunter.view.activity.MainActivity;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding vb;
    private SettingsListAdapter adapter;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentSettingsBinding.inflate(inflater,container,false);
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
        ((MainActivity) getActivity()).setTitle(getString(R.string.Settings));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Theme Settings
        switch (AppSettingsManager.getTheme(getContext())){
            case AppSettingsManager.THEME_LIGHT:
               vb.lightThemeButton.setChecked(true);
                break;
            case AppSettingsManager.THEME_DARK:
                vb.darkThemeButton.setChecked(true);
                break;
            default:
                vb.autoThemeButton.setChecked(true);
        }

        vb.settingsThemeRadioButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.autoThemeButton) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    AppSettingsManager.setThemeAuto(getContext());
                } else if (checkedId == R.id.lightThemeButton) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    AppSettingsManager.setThemeLight(getContext());
                } else if (checkedId == R.id.darkThemeButton) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    AppSettingsManager.setThemeDark(getContext());
                }
            }
        });

        vb.autoThemeImage.setOnClickListener((image)->{
            vb.autoThemeButton.setChecked(true);
        });

        vb.lightThemeImage.setOnClickListener((image)->{
            vb.lightThemeButton.setChecked(true);
        });

        vb.darkThemeImage.setOnClickListener((image)->{
            vb.darkThemeButton.setChecked(true);
        });

        //Main Settings
        adapter = new SettingsListAdapter(getContext(), AppSettingsManager.getSettingsList(getContext()),vb.settingsList);

        vb.settingsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        vb.settingsList.setAdapter(adapter);

        adapter.setItemClickListener(new SettingsListAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                switch (position){
                    case AppSettingsManager.SETTINGS_LIST_LANGUAGE:
                        showLanguagesListDialog();
                        break;
                }
            }
        });

    }

    void showLanguagesListDialog(){
        AlertDialog languagesDialog = new AlertDialog.Builder(getContext()).setSingleChoiceItems(R.array.languages, AppSettingsManager.getLanguageIndex(getContext()), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppSettingsManager.setLanguage(getContext(),which);
                dialog.dismiss();
                getActivity().recreate();
                adapter.notifyItemChanged(AppSettingsManager.SETTINGS_LIST_LANGUAGE);
            }
        }).create();

        languagesDialog.setTitle(getString(R.string.language));
        languagesDialog.show();
    }
}