package org.rlan.myrecycler.dbase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class FileRepo {
    
    private DBHelper dbHelper;
    
    public FileRepo(Context context){
        dbHelper=new DBHelper(context);
    }


    // 插入一个文件列表
    public int insert(FileInfo fileInfo){
        //打开连接，写入数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FileInfo.KEY_name,fileInfo.name);
        values.put(FileInfo.KEY_data_label,fileInfo.data_label);
        // 往数据库里插入数据
        long file_Id = db.insert(FileInfo.TABLE,null,values);
        //创建对应的表
        if (tabbleIsExist(fileInfo.data_label)) {
            String CREATE_TABLE_STUDENT = "CREATE TABLE " + fileInfo.data_label + "("
                    + DownloadSize.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + DownloadSize.KEY_STARTSIZE + " INTEGER, "
                    + DownloadSize.KEY_ENDSIZE + " INTEGER)";
            db.execSQL(CREATE_TABLE_STUDENT);
        }
        db.close();
        return (int)file_Id;
    }

    // 插入一个文件分段大小
    public int dataSizeInsert(String tabelname,DownloadSize downloadSize){
        //打开连接，写入数据
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DownloadSize.KEY_STARTSIZE,downloadSize.startSize);
        values.put(DownloadSize.KEY_ENDSIZE,downloadSize.endSize);
        // 往数据库里插入数据
        long file_Id = db.insert(tabelname,null,values);
        db.close();
        return (int)file_Id;
    }


    public void delete(FileInfo fileInfo){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(FileInfo.TABLE,FileInfo.KEY_name+"=?", new String[]{String.valueOf(fileInfo.name)});
        db.execSQL("DROP TABLE IF EXISTS "+ fileInfo.data_label);
        db.close();
    }

    // 更新对应的下载文件的分段Size表
    public void update(String tabelname,DownloadSize downloadSize){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DownloadSize.KEY_STARTSIZE,downloadSize.startSize);
        values.put(DownloadSize.KEY_ENDSIZE,downloadSize.endSize);
        db.update(tabelname,values,DownloadSize.KEY_ID+"=?",new String[] { String.valueOf(downloadSize.Size_ID) });
        db.close();
    }

    // 获取所以文件列表
    public ArrayList<FileInfo> getFileList(String tabelname){
        ArrayList<FileInfo> sizelist = new ArrayList<>();
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cs= db.query( tabelname, null, null, null, null, null, null);
        while (cs.moveToNext()){
           FileInfo fileInfo = new FileInfo();
           fileInfo.data_label = cs.getString(cs.getColumnIndex(FileInfo.KEY_data_label));
           fileInfo.name = cs.getString(cs.getColumnIndex(FileInfo.KEY_name));
           sizelist.add(fileInfo);
        }
        db.close();
        return sizelist;
    }

    // 获取某个文件的记录的Size列表
    public ArrayList<DownloadSize> getFileInfoList(String tabelname){
        ArrayList<DownloadSize> sizelist = new ArrayList<>();
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cs= db.query( tabelname, null, null, null, null, null, null);
        while (cs.moveToNext()){
            DownloadSize downloadSize = new DownloadSize();
            downloadSize.startSize = cs.getInt(cs.getColumnIndex(DownloadSize.KEY_STARTSIZE));
            downloadSize.endSize = cs.getInt(cs.getColumnIndex(DownloadSize.KEY_ENDSIZE));
            sizelist.add(downloadSize);
        }
        db.close();
        return sizelist;
    }


    // 获取某个文件列表
    public FileInfo getFileInfoById(String filename){
        FileInfo fileInfo = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery( "select * from "+FileInfo.TABLE+" where "+FileInfo.KEY_name+"=?", new String[]{filename});
        if(c.moveToNext()){
            fileInfo = new FileInfo();
            fileInfo.data_label = c.getString(c.getColumnIndex(FileInfo.KEY_data_label));
            fileInfo.file_ID = c.getInt(c.getColumnIndex(FileInfo.KEY_ID));
            fileInfo.name = c.getString(c.getColumnIndex(FileInfo.KEY_name));
        }
        db.close();
        return fileInfo;
    }

    public boolean tabbleIsExist(String tableName){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean result = false;
        if(tableName == null){
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        db.close();
        return result;
    }

}
