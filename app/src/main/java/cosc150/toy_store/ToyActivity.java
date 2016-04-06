package cosc150.toy_store;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ToyActivity extends AppCompatActivity {

    public static int numItemsInCart = 0;
    public static int priceOfItems = 0;
    public static ToyList selectedToys = new ToyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toy);
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);

        Log.d("print", "In ToyActivity: oncreate");

//        Read toy info
        //readToyInfo();

        for (int i = 0; i < MainActivity.bitmapList.size(); i++) {
            ImageView pic = new ImageView(this);
            pic.setImageBitmap(MainActivity.bitmapList.get(i));
            pic.setOnLongClickListener(longListen);
            pic.setTag(i);

            TextView name = new TextView(this);
            name.setText(MainActivity.toyNameList.get(i));

            TextView price = new TextView(this);
            price.setText("$" + Integer.toString(MainActivity.toyPriceList.get(i)));

            linearLayout1.addView(name);
            linearLayout1.addView(price);
            linearLayout1.addView(pic);
        }


        findViewById(R.id.shoppingCartView).setOnDragListener(DropListener);

        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            numItemsInCart=0;
            priceOfItems=0;
            selectedToys.removeAllToys();

            TextView resetItems = (TextView) findViewById(R.id.numItems);
            resetItems.setText("Number of items: "+Integer.toString(numItemsInCart));
            TextView resetPrice = (TextView) findViewById(R.id.shoppingCartPrice);
            resetPrice.setText("Price: $" + Integer.toString(priceOfItems));
            }
        });

        findViewById(R.id.checkoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToyActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }

        });
    }

    View.OnLongClickListener longListen = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ImageView pic = (ImageView) v;
            Log.d("print", "Image clicked - " + pic.getId());
            Toast.makeText(ToyActivity.this, "Image clicked - " + pic.getTag(), Toast.LENGTH_SHORT).show();
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder myShadowBuilder = new MyShadowBuilder(v);

            v.startDrag(data, myShadowBuilder, v, 0);
            return true;
        }
    };

    private class MyShadowBuilder extends View.DragShadowBuilder {
        private ColorDrawable greyShadow;

        public MyShadowBuilder(View view) {
            super(view);
            greyShadow = new ColorDrawable(Color.LTGRAY);

        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
            int height, width;
            height = (int) getView().getHeight();
            width = (int) getView().getWidth();

            greyShadow.setBounds(0, 0, width, height);
            shadowSize.set(width, height);
            shadowTouchPoint.set(width, height);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            greyShadow.draw(canvas);
        }
    }

    View.OnDragListener DropListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            View dragView = (View) event.getLocalState();

            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_ENTERED:
                    //Log.d("Drag event", "Entered shopping cart");
//                    Toast.makeText(getApplicationContext(), "Entering", Toast.LENGTH_SHORT).show();
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //Log.d("Drag event", "Exited");
//                    Toast.makeText(getApplicationContext(), "Exiting", Toast.LENGTH_SHORT).show();
                    break;
                case DragEvent.ACTION_DROP:
                    //Log.d("Drag event", "Dropped");
                    Toast.makeText(getApplicationContext(), "You want to purchase a toy.", Toast.LENGTH_SHORT).show();
                    updateCart(dragView);
                    break;
            }
            return true;
        }
    };

    private void updateCart(View v) {
        // v is an image with a tag
        String tag = v.getTag().toString();
        Log.d("print", "tag is " + tag);
        tag = tag.replace("image", "");
        Log.d("print", "tag is now " + tag);
        int toyNum = Integer.parseInt(tag);
        Log.d("print", "Toy number is " + toyNum);
        Log.d("print", MainActivity.toyNameList.get(toyNum) + " costs " + MainActivity.toyPriceList.get(toyNum));
        Toast.makeText(getApplicationContext(), MainActivity.toyNameList.get(toyNum) + " costs $" + MainActivity.toyPriceList.get(toyNum), Toast.LENGTH_SHORT).show();

        Toy newToy = MainActivity.toyList.getToy(toyNum);
        selectedToys.addToy(newToy);

        // Update price
        TextView priceView = (TextView) findViewById(R.id.shoppingCartPrice);
        priceOfItems += MainActivity.toyPriceList.get(toyNum);
        priceView.setText("Price: $" + Integer.toString(priceOfItems));

        //Update number of items
        TextView numItems = (TextView) findViewById(R.id.numItems);
        numItemsInCart+=1;
        numItems.setText("Number of items: " + Integer.toString(numItemsInCart));

    }

    //Log.d("print", "tag is " + tag);
    //tag = tag.replace("image", "");
    //Log.d("print", "tag is now " + tag);
    //Log.d("print", "Toy number is " + toyNum + 1);
    //Log.d("print", toyNameList.get(toyNum) + " costs " + toyPriceList.get(toyNum));

    // Update price
       /* TextView price = (TextView) findViewById(R.id.shoppingCartPrice);
        String priceText = (String) price.getText();
        //priceText = priceText.replace("Price: $", "");
        priceText = priceText.substring(priceText.indexOf('$')+1);
        int newPrice = Integer.parseInt(priceText);
        Log.d("print", "priceText:" + priceText);
        if (priceText != "") {
            //If there is already a toy in the basket
            Log.d("print", "There's a toy in the basket");
            //newPrice = Integer.parseInt(priceText);
            //newPrice += toyPriceList.get(toyNum);
        } else {
            Log.d("print", "No toys yet");
            //If no toys in the basket
            //newPrice = toyPriceList.get(toyNum);
        }
        newPrice += toyPriceList.get(toyNum);
        Log.d("print", "New price is " + newPrice);
        price.setText("Price: $" + Integer.toString(newPrice));*/

    //Update number of items
        /*int newNumItems;
        TextView numItems = (TextView) findViewById(R.id.numItems);
        String numItemsText = (String) numItems.getText();
        numItemsText = numItemsText.replace("Number of items: ", "");
        if (numItemsText != "") {
            newNumItems = Integer.parseInt(numItemsText);
            newNumItems += 1;
        } else {
            newNumItems = 1;
        }
        Log.d("print", "Num items: " + newNumItems);
        numItems.setText("Number of items: " + Integer.toString(newNumItems));*/
    //}


    /*private void readToyInfo() {

        InputStream is = null;

        try {
            is = getAssets().open("toy.data");
            int size = is.available();
            byte[] buffer = new byte[size]; //declare the size of the byte array with size of the file
            is.read(buffer); //read file
            is.close(); //close file
            ToyList toyList = new ToyList(buffer, size);
            System.out.println("There are " + toyList.getNumOfToys() + " toys.");

            for (int i = 0; i < toyList.getNumOfToys(); i++) {

                String viewToFind = "photo" + String.valueOf(i + 1) + "View";
                Log.d("print", "viewtofind: " + viewToFind);
                int idToFind = this.getResources().getIdentifier(viewToFind, "id", this.getPackageName());


                Bitmap bmp = toyList.getToy(i).getImage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapData = stream.toByteArray();

                // To convert byte array to Bitmap
                Bitmap bmpCopy = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);

                ImageView iv = (ImageView) findViewById(idToFind);
                iv.setImageBitmap(bmpCopy);

                MainActivity.imageList.add(iv);
                MainActivity.toyNameList.add(toyList.getToy(i).getToyName());
                MainActivity.toyPriceList.add(toyList.getToy(i).getPrice());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

//    private void readToyInfo() {
//        try {
//            Log.d("print", "About to read data");
//
//            URL url = new URL("http://people.cs.georgetown.edu/~wzhou/toy.data");
//            Log.d("print", "URL:: "+url);
//            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//
////            Read data into string first
//            String inputLine;
//            String allData="";
//            while ((inputLine = in.readLine()) != null)
//                allData.concat(inputLine);
//                Log.d("print", "Reading file");
//            in.close();
//
////            Convert string to byte array
//            Log.d("print", "Converting to byte array");
//
//            byte[] temp = allData.getBytes();
////            int length = (int) file.length();
////            byte[] temp = new byte[length];
////            file.readFully(temp);
////            file.close();
//            Log.d("print", "Byte array "+temp);
//
//            toylist = new ToyList(temp, temp.length);
//
//        } catch (MalformedURLException e1) {
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }

}