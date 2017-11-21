package flytube.music.player;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.util.HashMap;
import java.util.List;

import flytube.music.player.R;


public class TabActivity extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener{

    private ViewPager tabsviewPager;
    private SessionManager session;
    private ActionBar mActionBar;
    private Tabsadapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Intent intent = getIntent();
//        String value = intent.getStringExtra("key");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);
        session = new SessionManager(getApplicationContext());
        tabsviewPager = (ViewPager) findViewById(R.id.tabspager);

        new ListVideoInPlaylist().execute();
        new ChannelVideoInPlaylist().execute();
        new MyLikedVideos().execute();
        mTabsAdapter = new Tabsadapter(getSupportFragmentManager());

        tabsviewPager.setAdapter(mTabsAdapter);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab hometab = getSupportActionBar().newTab().setText("Home").setTabListener(this);
        Tab favtab = getSupportActionBar().newTab().setText("Favourites").setTabListener(this);
        //Tab communitytab = getSupportActionBar().newTab()`.setText("Community").setTabListener(this);

        getSupportActionBar().addTab(hometab);
        getSupportActionBar().addTab(favtab);
        //getSupportActionBar().addTab(communitytab);


        //This helps in providing swiping effect for v7 compat library
        tabsviewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                getSupportActionBar().setSelectedNavigationItem(position);
                //fm .loadVideos();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            Log.d("MYTUBE", "Before logout"+session.isLoggedIn());
            session.logoutUser();
            finish();
            Log.d("MYTUBE", "After logout" + session.isLoggedIn());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(Tab selectedtab, FragmentTransaction arg1) {
        // TODO Auto-generated method stub
        tabsviewPager.setCurrentItem(selectedtab.getPosition()); //update tab position on tap
    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }



    private class MyLikedVideos extends AsyncTask<String, Void, String> {

        private YouTube youtube;
        private String playlistId;
        @Override
        protected String doInBackground(String... params) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 1); // 0 - for private mode
            String oauth_token = pref.getString("oauth_token", "");
            Log.d("inserting in playlist", oauth_token);
            try {

                Log.d("one","one");
                // Authorize the request.
                GoogleCredential credential = new GoogleCredential().setAccessToken(oauth_token);

                // This object is used to make YouTube Data API requests.
                youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(
                        "Flytube").build();


                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("part", "snippet,contentDetails,statistics");
                parameters.put("myRating", "like");

                YouTube.Videos.List videosListMyRatedVideosRequest = youtube.videos().list(parameters.get("part").toString());
                if (parameters.containsKey("myRating") && parameters.get("myRating") != "") {
                    videosListMyRatedVideosRequest.setMyRating(parameters.get("myRating").toString());
                }

                VideoListResponse response = videosListMyRatedVideosRequest.execute();
                List<Video> videos = response.getItems();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }
    }
    private class ListVideoInPlaylist extends AsyncTask<String, Void, String> {

        private YouTube youtube;
        private String playlistId;

        @Override
        protected String doInBackground(String... params) {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 1); // 0 - for private mode
            String oauth_token = pref.getString("oauth_token", "");
            Log.d("inserting in playlist", oauth_token);
            try {

                Log.d("one","one");
                // Authorize the request.
                GoogleCredential credential = new GoogleCredential().setAccessToken(oauth_token);

                // This object is used to make YouTube Data API requests.
                youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(
                        "Flytube").build();



                // list that contains playlists of current user.
                //  String uploadPlaylistId = "PL8_B7e8MFom3V9ktKZ9JaNppL2-Y6gjt0";

//                YouTube.Playlists.List p1 = youtube.playlists().list("snippet").setMine(true);
////                Log.d("Playlist List", p1.execute().getItems().toString());
//                PlaylistListResponse p = p1.execute();
//
//                // Retrieve the playlist ID of of SSJU-CMPE-277
//                for (Playlist item:p.getItems()) {
//                    playlistId = item.getId().toString();
//
//                    YouTube.PlaylistItems.List list =  youtube.playlistItems().list("snippet,contentDetails");
//
//
//                }


                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("part", "snippet");
                parameters.put("mine", "true");
                parameters.put("maxResults", "25");
                parameters.put("onBehalfOfContentOwner", "");
                parameters.put("onBehalfOfContentOwnerChannel", "");

                YouTube.Playlists.List playlistsListMineRequest = youtube.playlists().list(parameters.get("part").toString());
                if (parameters.containsKey("mine") && parameters.get("mine") != "") {
                    boolean mine = (parameters.get("mine") == "true") ? true : false;
                    playlistsListMineRequest.setMine(mine);
                }

                if (parameters.containsKey("maxResults")) {
                    playlistsListMineRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
                }

                if (parameters.containsKey("onBehalfOfContentOwner") && parameters.get("onBehalfOfContentOwner") != "") {
                    playlistsListMineRequest.setOnBehalfOfContentOwner(parameters.get("onBehalfOfContentOwner").toString());
                }

                if (parameters.containsKey("onBehalfOfContentOwnerChannel") && parameters.get("onBehalfOfContentOwnerChannel") != "") {
                    playlistsListMineRequest.setOnBehalfOfContentOwnerChannel(parameters.get("onBehalfOfContentOwnerChannel").toString());
                }

                PlaylistListResponse response = playlistsListMineRequest.execute();

                // Print data from the API response and return the new playlist

                return null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }

    }



    private class ChannelVideoInPlaylist extends AsyncTask<String, Void, String> {

        private YouTube youtube;
        private String playlistId;

        @Override
        protected String doInBackground(String... params) {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 1); // 0 - for private mode
            String oauth_token = pref.getString("oauth_token", "");
            Log.d("inserting in playlist", oauth_token);
            try {

                Log.d("one", "one");
                // Authorize the request.
                GoogleCredential credential = new GoogleCredential().setAccessToken(oauth_token);

                // This object is used to make YouTube Data API requests.
                youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(
                        "Flytube").build();

                HashMap<String, String> parameters1 = new HashMap<>();
                parameters1.put("part", "snippet,contentDetails,statistics");
                parameters1.put("mine", "true");

                YouTube.Channels.List channelsListMineRequest = youtube.channels().list(parameters1.get("part").toString());
                if (parameters1.containsKey("mine") && parameters1.get("mine") != "") {
                    boolean mine = (parameters1.get("mine") == "true") ? true : false;
                    channelsListMineRequest.setMine(mine);
                }


                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("part", "snippet");
//                parameters.put("mine", "true");
                parameters.put("maxResults", "25");
//                parameters.put("onBehalfOfContentOwner", "");
//                parameters.put("onBehalfOfContentOwnerChannel", "");

                YouTube.Playlists.List playlistsListMineRequest = youtube.playlists().list(parameters.get("part").toString());
                if (parameters.containsKey("mine") && parameters.get("mine") != "") {
                    boolean mine = (parameters.get("mine") == "true") ? true : false;
                    playlistsListMineRequest.setMine(mine);
                }

                if (parameters.containsKey("maxResults")) {
                    playlistsListMineRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
                }

//                if (parameters.containsKey("onBehalfOfContentOwner") && parameters.get("onBehalfOfContentOwner") != "") {
//                    playlistsListMineRequest.setOnBehalfOfContentOwner(parameters.get("onBehalfOfContentOwner").toString());
//                }
//
//                if (parameters.containsKey("onBehalfOfContentOwnerChannel") && parameters.get("onBehalfOfContentOwnerChannel") != "") {
//                    playlistsListMineRequest.setOnBehalfOfContentOwnerChannel(parameters.get("onBehalfOfContentOwnerChannel").toString());
//                }



                ChannelListResponse response = channelsListMineRequest.execute();

                for (int i = 0; i < response.getItems().size(); i++) {
                    String channelID = response.getItems().get(i).getId();
                    playlistsListMineRequest.setChannelId(channelID);
                    PlaylistListResponse videosResponse = playlistsListMineRequest.execute();

                    for (int j = 0; j < videosResponse.getItems().size(); j++) {
                        Log.println(Log.ASSERT, "Playlist",videosResponse.getItems().get(j).getId());
                    }
                }

                // Print data from the API response and return the new playlist

                return null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }

    }

}