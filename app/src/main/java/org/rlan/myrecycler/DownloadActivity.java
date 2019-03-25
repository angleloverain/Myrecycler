package org.rlan.myrecycler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.rlan.myrecycler.dbase.DownloadUtil;
import org.rlan.myrecycler.dbase.FileInfo;
import org.rlan.myrecycler.dbase.FileRepo;

import java.util.ArrayList;

public class DownloadActivity extends Activity implements View.OnClickListener {

    private String url = "https://vd.yinyuetai.com/hc.yinyuetai.com/uploads/videos/common/0B71012EFC88052E7599E9CEAEC7A606.flv";
    private String path = Environment.getExternalStorageDirectory().getPath();
    private String fileName=path+"/给我一首歌的时间.mp4";
    private FileRepo fileRepo;
    private ProgressBar progressBar;
    private DownloadUtil downloadUtil;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        fileRepo = new FileRepo(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        downloadUtil = new DownloadUtil(DownloadActivity.this);
//        deleteData();
    }


    // 清空数据
    private void deleteData(){
        ArrayList<FileInfo> list = fileRepo.getFileList(FileInfo.TABLE);
        if (list == null || list.size()==0){
            Log.i("dddd","list is null");
            return;
        }
        for (int i = 0;i<list.size();i++) {
            fileRepo.delete(list.get(i));
        }
    }

//    private void testThread() throws InterruptedException {
//        ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
//
//        service.shutdown();
//        while (!service.awaitTermination(1, TimeUnit.SECONDS)) {
//            System.out.println("线程池没有关闭");
//        }
//
//        System.out.println("线程池已经关闭");
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.start:
                downloadUtil.start(fileName,url);
                break;
            case R.id.pause:
                downloadUtil.pauseDownload();
                break;
            case R.id.resume:
                downloadUtil.resumeDownload();
                break;
            case R.id.over:
                downloadUtil.finishDownload();
                break;
        }
    }

}
