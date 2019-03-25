package org.rlan.myrecycler.recyclerlib;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.rlan.myrecycler.R;

/**
 * Created by Administrator on 2017/10/20.
 */

public class MviewHolder extends RecyclerView.ViewHolder {

    public TextView tv;

    public MviewHolder(View itemView,boolean isInsertion) {
        super(itemView);
        if (isInsertion)
            return;
        tv = (TextView) itemView.findViewById(R.id.name);
    }

}
