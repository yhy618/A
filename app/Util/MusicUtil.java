package Util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/5/6 0006.
 * 音乐获取
 */

public class MusicUtil {

    private static ContentResolver resolve;
    private static List<music>
            musicList=new ArrayList<music>();
    private static long MinTime=40000;//默认MP3文件的最小长度为40000毫秒


    public static void setMinTime(long minTime){
        MinTime=minTime;
    }

    public static List<music> getMusicList(Context context){

        resolve=context.getContentResolver();
        Cursor cursor=resolve.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while(cursor.moveToNext()){
            String title= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String auther=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long duration=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            if(auther.equals("<unknown>")){
                auther="未知的艺术家";
            }
            //筛选符合条件的音乐文件
            if(duration>MinTime){
                music music =new music();
                music.setAuther(auther);
                music.setDuration(duration);
                music.setPath(path);
                music.setTitle(title);
                musicList.add(music);
            }

        }

        return musicList;
    }
}
