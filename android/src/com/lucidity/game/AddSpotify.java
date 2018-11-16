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
import android.widget.Button;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/*import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import com.wrapper.spotify.model_objects.specification.Track;*/


/* TODO:
https://developer.spotify.com/documentation/web-api/libraries/
https://stackoverflow.com/questions/37995704/getting-and-playing-30-second-previews-from-spotify
https://stackoverflow.com/questions/12698428/starting-a-song-from-spotify-intent
https://spotify.github.io/android-sdk/app-remote-lib/docs/

https://developer.spotify.com/documentation/general/guides/content-linking-guide/
https://stackoverflow.com/questions/28524063/spotify-android-intent-play-on-launch
https://github.com/spotify/android-auth/issues/41
play spotify song from custom android application stack overflow
https://stackoverflow.com/questions/25633343/how-to-know-the-login-status-in-spotify
*/

/*TODO:
THE SHA fingerprint needs to be updated on Feb. 9th 2019
https://stackoverflow.com/questions/34908193/spotify-api-invalid-app-id
https://stackoverflow.com/questions/27307265/control-playback-of-the-spotify-app-from-another-android-app
*/
//TODO NOTICE: spotify is registered for non-monetary personal project permission
//https://github.com/thelinmichael/spotify-web-api-java
public class AddSpotify extends AppCompatActivity {
    private static final String CLIENT_ID = "ab466596aaa041d89f062bc0845a16f0";
    private static final String REDIRECT_URI = "testschema://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;
    private static final int REQUEST_CODE = 1337;
    private ContentApi mContentApi;
    private Button search, play;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spotify);
        search = findViewById(R.id.search);
        play = findViewById(R.id.play);
    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.d("Spotify is installed: ", Boolean.toString(detectSpotify()));
        if(!detectSpotify()){
            installSpotify();
        }
        //authorize user
        //Set the connection parameters
        //user has to log in
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        System.out.println("Connected spotify successfully!");

                        // Now you can start interacting with App Remote

                        connected();
                        //playSearchSong("skyfall");
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


    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:6NerOPkqXvkD6pbe9P3WVe");
        //mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX7K31D69s4M1");
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {

                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                            Log.d("AddSpotify", track.name + " by " + track.artist.name);
                        }
                    }
                });
        //playSearchSong("skyfall");
        //getTrack_Async();
    }

    private void callback(){
        mSpotifyAppRemote.getPlayerApi().getPlayerState()
                .setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                    @Override
                    public void onResult(PlayerState playerState) {
                        // have fun with playerState
                    }
                })
                .setErrorCallback(new ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        // =(
                    }
                });
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
            sendOrderedBroadcast(intent, null);
        }catch (ActivityNotFoundException e) {
            /*Toast.makeText(this, "You must first install Spotify", Toast.LENGTH_LONG).show();
            Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.spotify.mobile.android.ui"));
            startActivity(i);*/
            Log.e("The error is: ", e.getMessage());
            System.out.println("The error is: "+ e.getMessage());
        }
    }

    /*private static final String accessToken = "taHZ2SdB-bPA3FsK3D7ZN5npZS47cMy-IEySVEGttOhXmqaVAIo0ESvTCLjLBifhHOHOIuhFUKPW1WMDP7w6dj3MAZdWT8CLI2MkZaXbYLTeoDvXesf2eeiLYPBGdx8tIwQJKgV8XdnzH_DONk";
    private static final String id = "01iyCAUm8EvOFqVWYJ3dVX";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setAccessToken(accessToken)
            .build();
    private static final GetTrackRequest getTrackRequest = spotifyApi.getTrack(id)
            .market(CountryCode.SE)
            .build();

    public static void getTrack_Sync() {
        try {
            final Track track = getTrackRequest.execute();

            System.out.println("Name: " + track.getName());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void getTrack_Async() {
        try {
            final Future<Track> trackFuture = getTrackRequest.executeAsync();

            // ...

            final Track track = trackFuture.get();

            System.out.println("Name: " + track.getName());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        }
    }*/
}
