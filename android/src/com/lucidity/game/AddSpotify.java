package com.lucidity.game;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.List;


/* TODO:
https://developer.spotify.com/documentation/android/guides/android-authentication/
https://teamtreehouse.com/community/what-is-redirect-uri
https://developer.spotify.com/documentation/general/guides/content-linking-guide/
https://developer.android.com/studio/build/application-id
https://developer.spotify.com/dashboard/applications/ab466596aaa041d89f062bc0845a16f0
https://developer.spotify.com/documentation/general/guides/app-settings/
https://developer.spotify.com/documentation/android/quick-start/

https://developer.spotify.com/documentation/web-api/libraries/
https://stackoverflow.com/questions/37995704/getting-and-playing-30-second-previews-from-spotify
https://stackoverflow.com/questions/12698428/starting-a-song-from-spotify-intent

https://developer.spotify.com/documentation/general/guides/content-linking-guide/
https://stackoverflow.com/questions/29059466/how-to-automatically-play-playlist-in-spotify-android-app
https://stackoverflow.com/questions/28524063/spotify-android-intent-play-on-launch
https://github.com/spotify/android-auth/issues/41
play spotify song from custom android application stack overflow
*/
//TODO NOTICE: spotify is registered for non-monetary personal project permission
public class AddSpotify extends AppCompatActivity {
    private static final String CLIENT_ID = "ab466596aaa041d89f062bc0845a16f0";
    //private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final String REDIRECT_URI = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;
    private static final int REQUEST_CODE = 1337;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spotify);
    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.d("Spotify is installed: ", Boolean.toString(detectSpotify()));
        if(!detectSpotify()){
            installSpotify();
        }

        playSong();

        //autorizing user
        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        System.out.println("authorized, theoretically");

        //
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        System.out.println("Connected spotify successfully!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("Error error error", throwable.getMessage(), throwable);
                        System.out.println("Error not authenticated " + throwable.getMessage());
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

        //Opens the spotify app
        /*if(isSpotifyInstalled) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("spotify:album:0sNOF9WDwhWunNAHPD3Baj"));
            intent.putExtra(Intent.EXTRA_REFERRER,
                    Uri.parse("android-app://" + this.getPackageName()));
            startActivity(intent);
        }*/
        //playMusic();
        //playSearchSong("skyfall");


    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX7K31D69s4M1");
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void playMusic() {
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
        sendOrderedBroadcast(i, null);

        i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
        sendOrderedBroadcast(i, null);
    }

    public void playSearchSong(String title) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.MainActivity"));
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        //intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist);
        //intent.putExtra(SearchManager.QUERY, artist);
        intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, title);
        intent.putExtra(SearchManager.QUERY, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public boolean detectSpotify(){
        PackageManager pm = getPackageManager();
        boolean isSpotifyInstalled;
        try {
            pm.getPackageInfo("com.spotify.music", 0);
            isSpotifyInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isSpotifyInstalled = false;
        }
        return isSpotifyInstalled;
    }

    public void installSpotify(){
        final String appPackageName = "com.spotify.music";
        final String referrer = "adjust_campaign=PACKAGE_NAME&adjust_tracker=ndjczk&utm_source=adjust_preinstall";

        try {
            Uri uri = Uri.parse("market://details")
                    .buildUpon()
                    .appendQueryParameter("id", appPackageName)
                    .appendQueryParameter("referrer", referrer)
                    .build();
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (android.content.ActivityNotFoundException ignored) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details")
                    .buildUpon()
                    .appendQueryParameter("id", appPackageName)
                    .appendQueryParameter("referrer", referrer)
                    .build();
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    public void playSong(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.setComponent(new ComponentName("com.spotify.mobile.android.ui", "com.spotify.mobile.android.ui.Launcher"));
        intent.putExtra(SearchManager.QUERY, "michael jackson smooth criminal");
        try {
            startActivity(intent);
        }catch (ActivityNotFoundException e) {
            /*Toast.makeText(this, "You must first install Spotify", Toast.LENGTH_LONG).show();
            Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.spotify.mobile.android.ui"));
            startActivity(i);*/
            Log.e("The error is: ", e.getMessage());
            System.out.println("The error is: "+ e.getMessage());
        }
    }
}
