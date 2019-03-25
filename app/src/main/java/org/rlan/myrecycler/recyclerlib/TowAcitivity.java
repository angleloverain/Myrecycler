package org.rlan.myrecycler.recyclerlib;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewGroup;

import org.rlan.myrecycler.R;
import org.rlan.myrecycler.SlideActivity;

import java.lang.reflect.Field;

public class TowAcitivity extends SlideActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heard_layout);
    }


}
