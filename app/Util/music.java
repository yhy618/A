package Util;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class music implements Serializable{
    private static  long serialVersionID=1L;
    private String title,path,auther;
    long duration;

    public static long getSerialVersionID() {
        return serialVersionID;
    }

    public static void setSerialVersionID(long serialVersionID) {
        music.serialVersionID = serialVersionID;
    }

    public String getAuther() {
        return auther;
    }

    public long getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }



    public void setTitle(String title) {
        this.title = title;
    }
}
