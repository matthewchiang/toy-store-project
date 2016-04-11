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

    int numItemsInCart = 0;
    int priceOfItems = 0;
    ToyList selectedToys = new ToyList();
    ToyList toyList;
    long searchItemID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toy);
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);

        Intent intent = getIntent();
        toyList = intent.getExtras().getParcelable("toylist");
        searchItemID = intent.getLongExtra("itemID", -1);

        for (int i = 0; i < toyList.getNumOfToys(); i++) {
            Toy currToy = toyList.getToy(i);
            ImageView pic = new ImageView(this);
            pic.setImageBitmap(currToy.getImage());
            pic.setOnLongClickListener(longListen);
            pic.setTag(i);

            TextView name = new TextView(this);
            name.setText(currToy.getToyName());
            name.setTextSize(20);

            TextView price = new TextView(this);

            price.setText("$" + Integer.toString(currToy.getPrice()));
            price.setTextSize(20);


            linearLayout1.addView(name);
            linearLayout1.addView(price);
            linearLayout1.addView(pic);
        }

        if (searchItemID != -1) {
            try {
                addSearchToCart();
            } catch (Exception e) {
                System.out.println("Cannot add item from search");
            }
        }


        findViewById(R.id.shoppingCartView).setOnDragListener(DropListener);

        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.checkoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CheckoutActivity.class);
                intent.putExtra("selectedToyList", selectedToys);
                intent.putExtra("totalPrice", priceOfItems);
                startActivity(intent);
            }

        });
    }

    View.OnLongClickListener longListen = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ImageView pic = (ImageView) v;
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

                case DragEvent.ACTION_DROP:
                    updateCart(dragView);
                    break;
            }
            return true;
        }
    };

    private void updateCart(View v) {
        // v is an image with a tag
        String tag = v.getTag().toString();
        tag = tag.replace("image", "");
        int toyNum = Integer.parseInt(tag);
        Toast.makeText(getApplicationContext(), toyList.getToy(toyNum).getToyName() + " costs " + toyList.getToy(toyNum).getPrice(), Toast.LENGTH_SHORT).show();

        Toy newToy = toyList.getToy(toyNum);
        selectedToys.addToy(newToy);

        // Update price
        TextView priceView = (TextView) findViewById(R.id.shoppingCartPrice);
        priceOfItems += toyList.getToy(toyNum).getPrice();
        priceView.setText("Price: $" + Integer.toString(priceOfItems));

        //Update number of items
        TextView numItems = (TextView) findViewById(R.id.numItems);
        numItemsInCart += 1;
        numItems.setText("Number of items: " + Integer.toString(numItemsInCart));

    }

    private void addSearchToCart() {
        Toy searchToy = toyList.getToy((int) searchItemID);
        numItemsInCart++;
        priceOfItems += searchToy.getPrice();
        selectedToys.addToy(searchToy);

        TextView priceView = (TextView) findViewById(R.id.shoppingCartPrice);
        priceView.setText("Price: $" + Integer.toString(priceOfItems));

        //Update number of items
        TextView numItems = (TextView) findViewById(R.id.numItems);
        numItems.setText("Number of items: " + Integer.toString(numItemsInCart));

        Toast.makeText(getApplicationContext(), searchToy.getToyName() + " costs " + searchToy.getPrice(), Toast.LENGTH_SHORT).show();

    }
}