package org.rlan.myrecycler.dbase;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static android.util.Log.i;

public class DownloadUtil {

    private static final int LIMTSIZE = 6;
    private int section_size = 3;
    private String url;
    private String fileName;
    private FileRepo fileRepo;
    private Context context;
    private ScheduledExecutorService service;

    public DownloadUtil(Context context) {
        this.context = context;
        service = Executors.newScheduledThreadPool(LIMTSIZE);
        fileRepo = new FileRepo(context);
    }
    // 初始化下载信息
    public void start(String fileName,String url)  {
        this.url = url;
        this.fileName = fileName;
        new Thread(new InitDate()).start();
    }

    // 开始下载文件
     private void startDownload(){
      FileInfo fileInfo = fileRepo.getFileInfoById(fileName);
        ArrayList<DownloadSize> list = fileRepo.getFileInfoList(fileInfo.data_label);
        for (int i = 0;i < list.size();i++){
            service.submit(new MasterDownload(list.get(i)));
        }
    }

    //恢复下载文件
    public void resumeDownload(){

    }

    // 暂停下载文件
    public void pauseDownload(){

    }

    // 删除或完成下载文件信息
    public void finishDownload(){

    }


    class InitDate implements Runnable{
        @Override
        public void run() {
            try {
                RandomAccessFile raf = new RandomAccessFile(fileName,"rw");
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.connect();
                int fileLength = connection.getContentLength();
                String type = connection.getContentType();
                // 在处理分段下载
                raf.setLength(fileLength);
                int each = 0;
                int end_ecah = 0;
                if (fileLength>section_size){
                    each = fileLength / section_size;
                    end_ecah = fileLength % section_size;
                }
                FileInfo fileInfo = new FileInfo();
                fileInfo.name = fileName;
                String label = new File(fileName).getName();
                int index = label.indexOf(".");
                label = label.substring(0,index);
                fileInfo.data_label = label;
                if (fileRepo.getFileInfoById(fileName) == null){
                    fileRepo.insert(fileInfo);
                    for (int i = 0;i<section_size;i++){
                        DownloadSize downloadSize = new DownloadSize();
                        downloadSize.startSize = i*each + 1;
                        downloadSize.endSize = (i+1)*each;
                        if (i+1==section_size){
                            downloadSize.endSize = (i+1)*each + end_ecah;
                        }
                        fileRepo.dataSizeInsert(fileInfo.data_label,downloadSize);
                    }
                }else {
                    Log.i("dddd","该文件正在下载当中。。。");
                }
                raf.close();
                startDownload();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class MasterDownload implements Runnable {
        private DownloadSize downloadSize;
        public MasterDownload(DownloadSize downloadSize){
            this.downloadSize = downloadSize;
        }
        @Override
        public void run() {
            i("dddd","thread name : "+Thread.currentThread().getName());
            HttpURLConnection connection = null;
            try {
                RandomAccessFile raf = new RandomAccessFile(fileName,"rw");
                connection = (HttpURLConnection) new URL(url).openConnection();
                // 设置连接超时时间
                connection.setConnectTimeout(15000);
                // 设置读取超时时间
                connection.setReadTimeout(15000);
                // 设置请求参数，即具体的 HTTP 方法
                connection.setRequestMethod("GET");
                // 添加 HTTP HEAD 中的一些参数
                connection.setRequestProperty("Range","bytes="+downloadSize.startSize+"-"+downloadSize.endSize);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.connect();
                // 这里就要开始下载了
                InputStream in = connection.getInputStream();
                byte [] fulsh = new byte[4096];
                int len = -1;
                raf.seek(downloadSize.startSize);
                while ((len = in.read(fulsh)) != -1){
                    raf.write(fulsh,0,len);
                }
                raf.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
