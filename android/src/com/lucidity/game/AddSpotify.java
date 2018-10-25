package com.lucidity.game;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;


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
*/
//TODO NOTICE: spotify is registered for non-monetary personal project permission
public class AddSpotify extends AppCompatActivity {
    private static final String CLIENT_ID = "ab466596aaa041d89f062bc0845a16f0";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spotify);

    }

    @Override
    protected void onStart(){
        super.onStart();

        //autorizing user
        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        //
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        //Log.d("MainActivity", "Connected! Yay!");
                        System.out.println("Add spotify successfully!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        //Log.e("MainActivity", throwable.getMessage(), throwable);
                        System.out.println("Error error error");
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

        //Opens the spotify app
        /*PackageManager pm = getPackageManager();
        boolean isSpotifyInstalled;
        try {
            pm.getPackageInfo("com.spotify.music", 0);
            isSpotifyInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isSpotifyInstalled = false;
        }
        if(isSpotifyInstalled) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("spotify:album:0sNOF9WDwhWunNAHPD3Baj"));
            intent.putExtra(Intent.EXTRA_REFERRER,
                    Uri.parse("android-app://" + this.getPackageName()));
            startActivity(intent);
        }*/
    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX7K31D69s4M1");
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
