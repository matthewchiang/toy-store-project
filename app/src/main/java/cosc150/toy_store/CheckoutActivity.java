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

    private static ToyList selectedToys = null;
    private int totalPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        LinearLayout linearLayoutCheckout = (LinearLayout) findViewById(R.id.linearLayoutCheckout);

        Intent intent = getIntent();
        selectedToys = intent.getExtras().getParcelable("selectedToyList");
        totalPrice = intent.getIntExtra("totalPrice", 0);

        for (int i = 0; i< selectedToys.getNumOfToys(); i++)
        {
            TextView thisToy = new TextView(this);
            String toyName = selectedToys.getToy(i).getToyName();
            thisToy.setText(toyName);
            linearLayoutCheckout.addView(thisToy);
        }

        TextView totalPriceView = new TextView(this);

        totalPriceView.setText("\nTotal price of " + selectedToys.getNumOfToys() + " item(s): $" + Integer.toString(totalPrice));
        linearLayoutCheckout.addView(totalPriceView);
    }

    public void openURL(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.toysrus.com/storeLocator/index.jsp"));
        startActivity(browserIntent);
    }

    public void checkout(View view) {
        Toast.makeText(getApplicationContext(), "Now charging " + totalPrice + " to your " +
                "credit card. Thank you!", Toast.LENGTH_LONG).show();

    }
}
