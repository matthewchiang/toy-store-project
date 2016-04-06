package cosc150.toy_store;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        LinearLayout linearLayoutCheckout = (LinearLayout) findViewById(R.id.linearLayoutCheckout);

        for (int i = 0; i< ToyActivity.numItemsInCart; i++)
        {
            TextView thisToy = new TextView(this);
            String toyName = ToyActivity.selectedToys.getToy(i).getToyName();
            thisToy.setText(toyName);
            linearLayoutCheckout.addView(thisToy);
        }

        TextView totalPrice = new TextView(this);
        totalPrice.setText("\nTotal price of " + ToyActivity.numItemsInCart + " item(s): $" + ToyActivity.priceOfItems);
        linearLayoutCheckout.addView(totalPrice);
    }

    public void openURL(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.toysrus.com/storeLocator/index.jsp"));
        startActivity(browserIntent);
    }

    public void checkout(View view) {
        Toast.makeText(getApplicationContext(), "Now charging " + ToyActivity.priceOfItems + " to your " +
                "credit card. Thank you!", Toast.LENGTH_LONG).show();

    }
}
