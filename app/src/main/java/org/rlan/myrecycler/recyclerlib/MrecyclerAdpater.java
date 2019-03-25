package org.rlan.myrecycler.recyclerlib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.rlan.myrecycler.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/20.
 */

public class MrecyclerAdpater extends RecyclerView.Adapter<MviewHolder> {

    private List<String> date;  // 数据都原始数据为标准

    private Context context;

    private List<IViewinfo> insertViews;


    public static final int TYPE_HEADER = 0;  //说明是带有Header的
    public static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

    public MrecyclerAdpater(Context context,List<String> date)
    {
        insertViews = new ArrayList<>();
        this.date = date;
        this.context = context;
    }

    public void insetionView(View view ,int code){
       insertViews.add(new IViewinfo(view,code));
    }

    public void insetionView(View view ){
        insertViews.add(new IViewinfo(view,insertViews.size()));
    }


    @Override
    public MviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
   //     Log.i("dddd","onCreate  " + viewType);
        if (insertViews.size()!=0)
        {
            for (int i=0;i<insertViews.size();i++)
            {
                if (viewType==insertViews.get(i).code)
                {
                    return new MviewHolder(insertViews.get(i).view,true);
                }
            }
        }
        MviewHolder holderView = new MviewHolder(LayoutInflater.from(context).inflate(R.layout.list_itme,null),false);
        return holderView;
    }

    @Override
    public void onBindViewHolder(MviewHolder holder, int position) {
        if (insertViews.size()!=0)
        {
            for (int i=0;i<insertViews.size();i++)
            {
                if (position==insertViews.get(i).code)
                {
                    return ;
                }
            }
        }
        int count = 0;
        for (int i=0;i<insertViews.size();i++)
        {
            if(insertViews.get(i).code<position)
            {
                count++;
            }
        }
        holder.tv.setText(date.get(position-count));
    }

    @Override
    public int getItemViewType(int position) {
   //     Log.i("dddd","getItem");
        return position;
    }

    @Override
    public int getItemCount() {
     //   Log.i("dddd","getItemCount");
        return date.size()+insertViews.size();
    }


    private class IViewinfo{
        View view;
        int code;

        public IViewinfo(View view, int code) {
            this.view = view;
            this.code = code;
        }
    }
}
