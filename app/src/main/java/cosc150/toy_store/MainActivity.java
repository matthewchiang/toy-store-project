package cosc150.toy_store;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //SearchView sv;
    //ListView lv;
    //    String[] toyNames = {"Lego Technic Crawler Crane costs $150","Lego Technic Volvo L350F costs $250","Lego Star Wars Millennium Falcon costs $150"};
    //ArrayAdapter<String> adapter;
    public static ToyList toyList;
    public static ArrayList<String> toyNameList = new ArrayList<String>();
    public static ArrayList<Integer> toyPriceList = new ArrayList<Integer>();
    //public static ArrayList<ImageView> imageList = new ArrayList<ImageView>();
    public static ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Read toy info, stores in toyNameList, toyPriceList, and text boxes
        readToyInfo();


//        Set up the actionlisteners
//        lv = (ListView) findViewById(R.id.toyListView);
//        sv =  (SearchView) findViewById(R.id.toySearchView);
//
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toyNames);
//        lv.setAdapter(adapter);
//
//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String text){
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String text) {
//                adapter.getFilter().filter(text);
//                return false;
//            }
//        });
//
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Object o = lv.getItemAtPosition(position);
//                String toy = o.toString();
//                Toast.makeText(getApplicationContext(), "You have chosen the item " + toy, Toast.LENGTH_LONG).show();
//
//                Intent intent = new Intent(MainActivity.this, ToyActivity.class);
//                startActivity(intent);
//            }
//        });

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
        Log.d("print", "onclick");
        Intent intent = new Intent(MainActivity.this, ToyActivity.class);
        startActivity(intent);
    }


    //reads info in, saves in ArrayLists
    private void readToyInfo() {
        TextView t = (TextView) findViewById(R.id.allToys);
        String tToPrint = "";
        InputStream is = null;

        try {
            is = getAssets().open("toy.data");
            int size = is.available();
            byte[] buffer = new byte[size]; //declare the size of the byte array with size of the file
            is.read(buffer); //read file
            is.close(); //close file
            toyList = new ToyList(buffer, size);
            System.out.println("There are " + toyList.getNumOfToys() + " toys.");

            for (int i = 0; i < toyList.getNumOfToys(); i++) {

                //String viewToFind = "photo" + String.valueOf(i + 1) + "View";
                //Log.d("print", "viewtofind: " + viewToFind);
                //int idToFind = this.getResources().getIdentifier(viewToFind, "id", this.getPackageName());
                //ImageView iv = (ImageView) findViewById(idToFind);

                Bitmap bmp = toyList.getToy(i).getImage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapData = stream.toByteArray();

                // To convert byte array to Bitmap
                Bitmap bmpCopy = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
                bitmapList.add(bmpCopy);
                //ImageView iv = new ImageView(this);
                //iv.setImageBitmap(bmpCopy);

                //imageList.add(iv);
                toyNameList.add(toyList.getToy(i).getToyName());
                toyPriceList.add(toyList.getToy(i).getPrice());

                tToPrint += toyNameList.get(i) + " costs $" + toyPriceList.get(i) + "\n";
            }
            t.setText(tToPrint);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private ToyList readToyInfo() {
//
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                try {
//                    Log.d("print", "About to read data in readToyInfo");
//
//                    URL url = new URL("http://people.cs.georgetown.edu/~wzhou/toy.data");
//                    Log.d("print", "URL: "+url);
//                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//
//                    Log.d("print", "Created bufferedReader");
//
////            Read data into string first
//            String inputLine;
//            String allData="";
//            while ((inputLine = in.readLine()) != null)
//                allData.concat(inputLine);
//            Log.d("print", "Read file" +allData);
//            in.close();
//
////            Convert string to byte array
//            Log.d("print", "Converting to byte array");
//
//            byte[] temp = allData.getBytes();
//            Log.d("print", "Byte array "+temp);
//
//            ToyList toylist = new ToyList(temp, temp.length);
//                    Log.d("print", "Created toylist! "+toylist);
//
//                    } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        }).start();
//
//        Log.d("print", "Done with thread. About to return toylist");
//
//        return toylist;
//    }
}