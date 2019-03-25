package org.rlan.myrecycler;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.rlan.myrecycler.recyclerlib.MrecyclerAdpater;
import org.rlan.myrecycler.recyclerlib.MyrecylerView;
import org.rlan.myrecycler.recyclerlib.TowAcitivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends SlideActivity implements View.OnClickListener{

    private MyrecylerView myrecylerView;
    LinearLayoutManager lm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myrecylerView = (MyrecylerView) findViewById(R.id.recycler);
        MrecyclerAdpater adpater = new MrecyclerAdpater(this,initData());
        lm = new LinearLayoutManager(this);
        myrecylerView.setLayoutManager(lm);
        adpater.insetionView(getView("insertion1"),1);
        adpater.insetionView(getView("insertion2"),2);
        adpater.insetionView(getView("insertion3"),3);
        adpater.insetionView(getView("用力下拉有惊喜"),7);
        myrecylerView.setAdapter(adpater);
    }

    private View getView(String content) {
        View view = LayoutInflater.from(this).inflate(R.layout.heard_layout,null);
        TextView textView = (TextView) view.findViewById(R.id.dfsdfs);
        textView.setText(content);
        return  view;
    }

    private List<String> initData() {
        List<String> mDatas = new ArrayList<String>();
        for (int i = 1; i <= 145; i++)
        {
            mDatas.add("" + i);
        }
        return mDatas;
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, TowAcitivity.class));
    }
}
