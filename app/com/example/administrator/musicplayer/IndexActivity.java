package com.example.administrator.musicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import Util.MusicUtil;
import Util.music;
import adapter.listview_adapter;

/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class IndexActivity extends Activity implements View.OnClickListener {
    private int progress;
    private String T;
    private TextView playingName_tv;
    private ListView musicMenu;
    private SeekBar sb;
    private TextView time;
    private ImageButton previous,next,circulation,pouse;
    private List<music>musicList;
    private listview_adapter adapter;
    private reciver mybroadcastReceiever;
    private music music;
    private int index;//歌曲的下标
    private  boolean isPouse;//当前歌曲播放状态的标志
    private  boolean isNewMusic;
    private int clt;//播放模式,0,1,2分别表示单曲循环，列表循环，随机播放
     private SharedPreferences sha;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        init();
    }

    /*  控件的初始化*/
    private  void init(){
        sha=getSharedPreferences("data",0);
        editor=sha.edit();
        progress=10;
        index=0;
        clt=1;
        isPouse=true;
        isNewMusic=true;
        musicList= MusicUtil.getMusicList(IndexActivity.this);
        adapter=new listview_adapter(musicList,IndexActivity.this);
        playingName_tv= (TextView) findViewById(R.id.playingName_index_tv);
        musicMenu=(ListView) findViewById(R.id.musicMenu_index_lv);
        sb=(SeekBar) findViewById(R.id.function_index_sb);
        time=(TextView) findViewById(R.id.musicTime_index_tv);
        previous=(ImageButton) findViewById(R.id.previous);
        next=(ImageButton) findViewById(R.id.Next);
        circulation=(ImageButton) findViewById(R.id.circulation);
        pouse=(ImageButton) findViewById(R.id.Pouse);
        pouse.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        circulation.setOnClickListener(this);
        musicMenu.setAdapter(adapter);
        musicMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position!=index) {

                    index = position;
                    isNewMusic=true;
                    isPouse=true;
                    changeIcon();
                    send_to_Service(index);
                    isNewMusic=false;
                }
            }
        });
        changeSeek();








        //注册广播
        mybroadcastReceiever=new reciver();
        IntentFilter filter=new IntentFilter("musicActivity");
        registerReceiver(mybroadcastReceiever,filter);
        Intent intent=new Intent(IndexActivity.this,MusicPlayingService.class);
        startService(intent);
    }









    public void changeTitle(){
        String musicName=music.getTitle();
        String singerName=music.getAuther();
        playingName_tv.setText(musicName+"---->"+singerName);
    }








     public void  changeSeek(){
         sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {

                 Intent intent =new Intent("musicService");
                 intent.putExtra("progress",10);
                // sendBroadcast(intent);
             }
         });
     }






    //向服务端发送需要播放歌曲的信息
    public void send_to_Service(int position){
        music=musicList.get(position);
        if(isNewMusic&&isPouse) changeTitle();

        Intent intent=new Intent("musicService");
        intent.putExtra("isPouse",isPouse);
        intent.putExtra("music",music);
        intent.putExtra("isNewMusic",isNewMusic);
        sendBroadcast(intent);
        isPouse=!isPouse;
    }






    //按钮的单击事件响应
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.previous:
                isPouse=true;
                isNewMusic=true;
                if(clt==1){
                    index--;
                    if(index<0) index=musicList.size()-1;
                }else if(clt==2){
                    index= (int) (Math.random() * musicList.size());
                }
                send_to_Service(index);
                isNewMusic=false;
                break;
            case R.id.Next:
                isPouse=true;
                isNewMusic=true;
                if(clt==1){
                    index++;
                    if(index>musicList.size()) index=musicList.size()-1;
                }else if(clt==0){
                }else if(clt==2){
                    index= (int) (Math.random() * musicList.size());
                }
                changeIcon();
                send_to_Service(index);
                isNewMusic=false;
                break;
            case R.id.Pouse:
                //System.out.println("userActivity用户点击了暂停或者播放键");
                if (music==null){
                    send_to_Service(index);

                }else{
                    changeIcon();
                    send_to_Service(index);

                }
                isNewMusic=false;
                break;
            case R.id.circulation:
                switch(clt){
                    case 0:
                        clt=1;
                        circulation.setImageResource(android.R.drawable.ic_menu_revert);
                        Toast.makeText(this,"列表循环",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        clt=2;
                        circulation.setImageResource(android.R.drawable.ic_menu_sort_by_size);
                        Toast.makeText(this,"随机播放",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        clt=0;
                        circulation.setImageResource(android.R.drawable.ic_lock_lock);
                        Toast.makeText(this,"单曲循环",Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }



    //用于当按下按钮后改变按钮的图标
    public void changeIcon(){

        if(isPouse){
            pouse.setImageResource(android.R.drawable.ic_media_pause);

        }else{
            pouse.setImageResource(android.R.drawable.ic_media_play);

        }
    }


    //设置歌曲的进度以及时长
    public void setTime(int current,int duration){
        int minute1=current/1000/60;
        int second1=current/1000%60;
        int minute2=duration/1000/60;
        int second2=duration/1000%60;
        String Sminute1="";
        String Ssecond1="";
        String Sminute2="";
        String Ssecond2="";
        if(minute1<10){
            Sminute1=("0"+minute1);
        }else {
            Sminute1=(""+minute1);
        }
        if(second1<10){
            Ssecond1=("0"+second1);
        }else {
            Ssecond1=(""+second1);
        }
        if(minute2<10){
            Sminute2=("0"+minute2);
        }else {
            Sminute2=(""+minute2);
        }
        if(second2<10){
            Ssecond2=("0"+second2);
        }else {
            Ssecond2=(""+second2);
        }
        time.setText(Sminute1+":"+Ssecond1+"/"+Sminute2+":"+Ssecond2);
    }


    //用于播放下一曲
    public void Next(){
        isPouse=true;
        isNewMusic=true;
        if(clt==1){
            index++;
            if(index>musicList.size()) index=musicList.size()-1;
        }else if(clt==0){
        }else if(clt==2){
            index= (int) (Math.random() * musicList.size());
        }
        changeIcon();

        send_to_Service(index);
        editor.putInt("index",index);
        editor.putBoolean("isNewMusic",isNewMusic);
        editor.putBoolean("isPouse",isPouse);
        editor.commit();
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {




        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            editor.putInt("index",index);
            editor.putBoolean("isNewMusic",isNewMusic);
            editor.putBoolean("isPouse",isPouse);
            editor.commit();

            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
       return super.onKeyDown(keyCode,event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,1,"退出");
        menu.add(Menu.NONE,1,1,"帮助");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch(item.getItemId()){
            case 0:
                AlertDialog.Builder dialog=new AlertDialog.Builder(IndexActivity.this);
                dialog.setTitle("退出应用");
                dialog.setMessage("亲。_。,确定要退出音乐播放器吗？");
                dialog.setPositiveButton("残忍退出",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(IndexActivity.this,MusicPlayingService.class);
                        stopService(intent);
                        editor.clear();
                        System.exit(0);
                    }
                });
                dialog.setNegativeButton("取消",new  DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                break;
            case 1:
                AlertDialog.Builder dialog1=new AlertDialog.Builder(IndexActivity.this);
                dialog1.setTitle("人生是一场不可抗拒的前进");
                dialog1.setMessage("MusicPlayer-V 1.0\n\n时间：2017-05-15\n\n更多功能敬请期待后续版本\n\n");
                dialog1.show();
        }


        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
        unbindService((ServiceConnection) mybroadcastReceiever);
    }

    public void setSeekBar(int current){
        sb.setProgress(current);
    }

    public class reciver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int current=intent.getIntExtra("currentTime",-1);
            int duration=intent.getIntExtra("duration",-1);
            boolean isOver=intent.getBooleanExtra("isOver",false);
            if(isOver) Next();
            if(current!=-1){
                setSeekBar((int) (((current*1.0)/duration)*100));
                setTime(current,duration);
            }
        }
    }
    }


