package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.musicplayer.R;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import Util.music;

/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class listview_adapter extends BaseAdapter {
    private List<music> musicList;
    private Context context;
    private LayoutInflater inflater;

    public listview_adapter(List<music> musicList, Context context){
        this.musicList=musicList;
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Myhoder myhoder=null;
        if(convertView==null){
            Context context=parent.getContext();

            convertView=LayoutInflater.from(context).inflate(R.layout.music_view,parent,false);

            myhoder=new Myhoder(convertView);
            convertView.setTag(myhoder);

        }else{

            myhoder= (Myhoder) convertView.getTag();
        }

        myhoder.imageView.setImageResource(R.drawable.icon);

        myhoder.title.setText(musicList.get(position).getTitle());

        myhoder.getSinger().setText(musicList.get(position).getAuther());

        return convertView;
    }

    class Myhoder{
        ImageView imageView;
        TextView title,singer;
        Myhoder(View view){

            title= (TextView) view.findViewById(R.id.music_name_tv);

            singer= (TextView) view.findViewById(R.id.singer_name_tv);

            imageView=(ImageView)view.findViewById(R.id.menu_img);

        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getSinger() {
            return singer;
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public void setSinger(TextView singer) {
            this.singer = singer;
        }
    }
}
