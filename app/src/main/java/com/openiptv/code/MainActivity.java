package com.openiptv.code;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.tv.TvContract;
import android.media.tv.TvInputManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.openiptv.code.input.TVInputService;

import java.util.Locale;

import static com.openiptv.code.epg.EPGService.isSetupComplete;

public class MainActivity extends Activity {

    private TextView textView;
    private TextView tv_info;
    private RadioGroup rg_language;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        tv_info = findViewById(R.id.tv_info);
        rg_language = findViewById(R.id.rg_language);
        rg_language.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_chinese) {
                    switchLanguage(Locale.CHINESE);
                } else {
                    switchLanguage(Locale.ENGLISH);
                }
            }
        });

//        if (isSetupComplete(this)) {
//            DatabaseActions databaseActions = new DatabaseActions(getApplicationContext());
//            String accountId = databaseActions.getActiveAccount();
//            databaseActions.setActiveAccount(accountId);
//            databaseActions.close();
//        }
//
//        Intent intent = new Intent(this, TVInputService.class);
//        startService(intent);
//
//        Intent i;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            i = new Intent(TvInputManager.ACTION_SETUP_INPUTS);
//        } else {
//            i = new Intent(Intent.ACTION_VIEW, TvContract.Channels.CONTENT_URI);
//            i.setData(TvContract.buildChannelsUriForInput(TvContract.buildInputId(new ComponentName(Constants.COMPONENT_PACKAGE, Constants.COMPONENT_CLASS))));
//        }
//
//        startActivity(i);
//
//        finish();
    }

    public void switchLanguage(Locale locale) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = locale;
        resources.updateConfiguration(config, dm);

        //update view
        textView.setText(getString(R.string.success));
        tv_info.setText(getString(R.string.select_language));
    }
}
