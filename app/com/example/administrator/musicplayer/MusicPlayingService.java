package com.example.administrator.musicplayer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.util.zip.Inflater;
import Util.music;
/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class MusicPlayingService extends Service {
    MediaPlayer player;
    private boolean isPouse=true;
    private boolean isNewMusic=true;
    private  int duration=0;
    private int curremt=0;
    private boolean isover=false;
    private int prePosition=0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MybroadcastReceiever extends BroadcastReceiver{

        private boolean isNewMusic;
        private int position;
        @Override
        public void onReceive(Context context, Intent intent) {

            if(player==null) {
                player=new MediaPlayer();
                player.setOnCompletionListener(on);

            }
            music music= (Util.music) intent.getSerializableExtra("music");
            int progress=intent.getIntExtra("progress",-1);
            isPouse=intent.getBooleanExtra("isPouse",true);
            isNewMusic=intent.getBooleanExtra("isNewMusic",true);
            //if(progress!=-1) toseek(progress);
            if(isNewMusic){
                playMusic(music);
            }else{
                if(isPouse==true){
                    player.seekTo(prePosition);
                    player.start();

                }else{
                    prePosition=player.getCurrentPosition();
                    player.pause();

                }
            }
        }
    }
//    private void toseek(int progress){
//    }


    private void getTime(){
        new Thread (){
            @Override
            public void run() {
                curremt=player.getCurrentPosition();
                duration=player.getDuration();
                while(curremt<duration){
                    try {
                        Thread.sleep(1000);
                        curremt=player.getCurrentPosition();
                        Intent intent=new Intent("musicActivity");
                        intent.putExtra("currentTime",curremt);
                        intent.putExtra("duration",duration);
                        sendBroadcast(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    public void playMusic(music music){
        if(isPouse){
            if(player.isPlaying()) player.stop();
            player.reset();
            try {
                player.setDataSource(music.getPath());
                player.prepare();
                player.start();
                getTime();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }

        MediaPlayer.OnCompletionListener on=new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent=new Intent("musicActivity");
                isover=true;
                intent.putExtra("isOver",isover);
                sendBroadcast(intent);
            }
        };

    MybroadcastReceiever mybroadcastReceiever;


    @Override
    public void onCreate() {
        mybroadcastReceiever=new MybroadcastReceiever();
        IntentFilter filter=new IntentFilter("musicService");
        registerReceiver(mybroadcastReceiever,filter);
        super.onCreate();
    }
    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
        unbindService((ServiceConnection)mybroadcastReceiever);
    }
}
