package cosc150.toy_store;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SearchView sv;
    ListView lv;
    ArrayAdapter<String> adapter;
    ToyList toyList = null;
    ArrayList<String> toyNameList = new ArrayList<String>();
    ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    MediaPlayer mySound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySound = MediaPlayer.create(this, R.raw.jahzzartakemehigher);
        mySound.start();
        Log.d("print", "playing " + mySound.isPlaying());
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

//        Read toy info, stores in toyNameList, toyPriceList, and text boxes
        readToyInfo();

        String[] toyNamesArray = new String[toyNameList.size()];
        toyNamesArray = toyNameList.toArray(toyNamesArray);

//        Set up the actionlisteners
        lv = (ListView) findViewById(R.id.toyListView);
        sv = (SearchView) findViewById(R.id.toySearchView);


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toyNamesArray);
        lv.setAdapter(adapter);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                adapter.getFilter().filter(text);
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getBaseContext(), ToyActivity.class);
                intent.putExtra("toylist", toyList);
                intent.putExtra("itemID", id);
                startActivity(intent);


            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewToysOnClick(View view) {
        Intent intent = new Intent(getBaseContext(), ToyActivity.class);
        intent.putExtra("toylist", toyList);
        startActivity(intent);

    }


    //reads info in, saves in ArrayLists
    private void readToyInfo() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;

                try {
                    URL url = new URL("http://people.cs.georgetown.edu/~wzhou/toy.data");
                    URLConnection urlconn = url.openConnection();
                    urlconn.connect();
                    int fileLength = urlconn.getContentLength();

                    is = urlconn.getInputStream();

                    //Make a new file and save it
                    String filename = "/ToyDataFromURL.data";

                    File file = new File(getFilesDir(), filename);

                    FileOutputStream fileOutputStream = new FileOutputStream(file);

                    int count;
                    byte buffer[] = new byte[fileLength];

                    while ((count = is.read(buffer, 0, buffer.length)) != -1)
                        fileOutputStream.write(buffer, 0, count);


                    InputStream fileInputStream;
                    try {
                        //Open the file just created
                        fileInputStream = new FileInputStream(file);

                        int fileInputLength = fileInputStream.available();
                        byte[] fileBuffer = new byte[fileInputLength]; //declare the size of the byte array with size of the file
                        fileInputStream.read(fileBuffer); //read file
                        fileInputStream.close(); //close
                        toyList = new ToyList(fileBuffer, fileInputLength);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!thread.isAlive()){
            for (int i = 0; i < toyList.getNumOfToys(); i++) {

                Bitmap bmp = toyList.getToy(i).getImage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapData = stream.toByteArray();

                // To convert byte array to Bitmap
                Bitmap bmpCopy = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);

                toyNameList.add(toyList.getToy(i).getToyName());
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://cosc150.toy_store/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://cosc150.toy_store/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

