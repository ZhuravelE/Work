package com.z.mobis.znotesconverter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class Converter extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        initFragment();
    }

    private void initFragment(){
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        FileFragment fileFragment = FileFragment.newInstance(FileFragment.PICK_DB_FOR_OPEN);

        ft.add(R.id.fragment_converter, fileFragment);
        ft.commit();
    }
}
