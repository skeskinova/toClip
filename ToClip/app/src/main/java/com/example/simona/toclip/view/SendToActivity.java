package com.example.simona.toclip.view;

import android.support.v4.app.Fragment;

/**
 * Created by simona on 19.5.2016 г..
 */
public class SendToActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new SendToFragment();
    }
}
