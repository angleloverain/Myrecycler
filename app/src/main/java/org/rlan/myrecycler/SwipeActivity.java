package org.rlan.myrecycler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.rlan.myrecycler.recyclerlib.MrecyclerAdpater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/1.
 */

public class SwipeActivity extends Activity {

    private RecyclerView myrecylerView;
    private LinearLayoutManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sweip_layout);
        myrecylerView = (RecyclerView) findViewById(R.id.recycler);
        MrecyclerAdpater adpater = new MrecyclerAdpater(this,initData());
        lm = new LinearLayoutManager(this);
        myrecylerView.setLayoutManager(lm);
        myrecylerView.setAdapter(adpater);
    }


    private List<String> initData() {
        List<String> mDatas = new ArrayList<String>();
        for (int i = 1; i <= 15; i++)
        {
            mDatas.add("" + i);
        }
        return mDatas;
    }



     class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.MviewHolder> {

        private Context context;
        private List<String> carousleList;
        private String removedate;
        private int itmeHeight;

        public CarouselAdapter(Context context,List<String> carousleList)
        {
            this.context = context;
            this.carousleList = carousleList;
        }

        public void insetionView(int index,String data ){
            if (data.equals("")) {
                carousleList.add(index, removedate);
            }else {
                carousleList.add(index, data);
            }
            notifyItemInserted(index);
            notifyDataSetChanged();
        }

        public void removeView(int index)
        {
            removedate = carousleList.get(index);
            carousleList.remove(index);
            notifyItemRemoved(index);
            notifyDataSetChanged();
        }


        public int getItmeHeight()
        {
            return itmeHeight;
        }

        @Override
        public CarouselAdapter.MviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new CarouselAdapter.MviewHolder(LayoutInflater.from(context).inflate(R.layout.oneframent_carousel_itme,null));
        }

        @Override
        public void onBindViewHolder(CarouselAdapter.MviewHolder holder, int position) {
            holder.t.setText(carousleList.get(position));
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return carousleList.size();
        }

        class MviewHolder extends RecyclerView.ViewHolder {
            TextView t;
            public MviewHolder(View itemView) {
                super(itemView);
                t = (TextView) itemView.findViewById(R.id.carousel_item);
                int width = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                int height = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                itemView.measure(width, height);
                itmeHeight = itemView.getMeasuredHeight();
            }
        }
    }


}
