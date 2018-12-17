package com.lucidity.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoundGallery extends AppCompatActivity {
    private ArrayList<String> audioName, audioPath;
    //private ArrayAdapter<String> adapter;
    private ListItemAdapter adapter;
    private String username;
    private static MediaPlayer mMediaPlayer;
    private ArrayList<Boolean> isPlaying;
    ImageButton playing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_gallery);

        Login login = new Login(getApplicationContext());
        username = login.getUsername();

        final ListView gallery = (ListView) findViewById(R.id.sound_gallery);
        getLocalAudio();
        displayAudio(gallery);

        /*gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if(isPlaying[position]){
                    mMediaPlayer.release();
                    isPlaying[position] = false;
                } else {
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(audioPath.get(position)));
                    mMediaPlayer.start();
                    isPlaying[position] = true;
                }
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mMediaPlayer) {
                        mMediaPlayer.release();
                    }
                });
            }

        });*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isPlaying.isEmpty()){
            if(isPlaying.contains(true)){
                mMediaPlayer.release();
            }
        }
    }

    private void getLocalAudio(){
        audioName = new ArrayList<String>();
        audioPath = new ArrayList<String>();
        isPlaying = new ArrayList<>();
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/audioDir
        File directory = cw.getDir("audioDir", Context.MODE_PRIVATE);
        File subfolder = new File(directory, username);
        File[] files = new File(subfolder.getAbsolutePath()).listFiles();
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                if ("mp3".equals(files[i].getName().substring(files[i].getName().length() - 3, files[i].getName().length()))) {
                    audioPath.add(files[i].getAbsolutePath());
                    audioName.add(files[i].getName());
                    isPlaying.add(false);
                }
            }
        }
    }

    private void displayAudio(ListView gallery){
        if(audioName.size() > 0) {
            /*adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, android.R.id.text1, audioName);
            gallery.setAdapter(adapter); //Set all the file in the list.*/
            adapter = new ListItemAdapter(this, audioName, audioPath);
            gallery.setAdapter(adapter);

        }
    }

    public class ListItemAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> audioName = new ArrayList<>();
        private ArrayList<String> audioPath = new ArrayList<>();
        private Context context;

        ListItemAdapter(Context context, ArrayList<String> names, ArrayList<String> paths){
            this.audioName = names;
            this.audioPath = paths;
            this.context = context;
        }

        @Override
        public int getCount() {
            return audioName.size();
        }

        @Override
        public Object getItem(int pos) {
            return audioName.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return audioName.indexOf(audioName.get(pos));
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.audio_listitem, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.audio_name);
            listItemText.setText(audioName.get(position));

            //Handle buttons and add onClickListeners
            ImageButton deleteBtn = view.findViewById(R.id.audio_del);
            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //TODO: call dialoge
                    File file = new File(audioPath.get(position));
                    file.delete();
                    if (isPlaying.get(position)) {
                        mMediaPlayer.release();
                    }
                    audioName.remove(position);
                    audioPath.remove(position);
                    isPlaying.remove(position);

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(SoundGallery.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(SoundGallery.this);
                    }
                    builder.setTitle("Success")
                            .setMessage("The song is deleted from the app")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    startActivity(getIntent());
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            });

            ImageButton playBtn = view.findViewById(R.id.audio_play);
            playBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(isPlaying.get(position)){
                        mMediaPlayer.release();
                        isPlaying.set(position, false);
                        ((ImageButton) v).setImageResource(R.drawable.play);
                    } else {
                        for (int i = 0; i < audioPath.size(); i++) {
                            if(isPlaying.get(i)) {
                                isPlaying.set(i, false);
                                mMediaPlayer.release();
                                playing.setImageResource(R.drawable.play);
                            }
                        }
                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(audioPath.get(position)));
                        mMediaPlayer.start();
                        isPlaying.set(position, true);
                        playing = (ImageButton) v;
                        ((ImageButton) v).setImageResource(R.drawable.stop);
                    }
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mMediaPlayer) {
                            mMediaPlayer.release();
                        }
                    });
                }
            });

            return view;
        }

        public void addItem(String item){
            //audioName.add(item);
            //notifyDataSetChanged();
        }

    }
}
