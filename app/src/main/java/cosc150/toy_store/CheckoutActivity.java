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
import android.widget.TextView;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        TextView listOfSelectedToys = (TextView) findViewById(R.id.selectedToysView);
        String toyNamesString = "";
        ToyList selectedToys = ToyActivity.selectedToys;

        Log.d("print", "size of selectedToys " + selectedToys.getNumOfToys());

        for (int i = 0; i<selectedToys.getNumOfToys(); i++)
        {
            String toyName = selectedToys.getToy(i).getToyName();
            Log.d("print", "Toy name" + selectedToys.getToy(i).getToyName());
            toyNamesString+=toyName+"\n";
        }

        Log.d("print", "toyNamesString "+toyNamesString);
        toyNamesString += "\nTotal Price of "+ ToyActivity.numItemsInCart +" Items: $"+ToyActivity.priceOfItems;
        listOfSelectedToys.setText(toyNamesString);
    }

    public void openURL(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.toysrus.com/storeLocator/index.jsp"));
        startActivity(browserIntent);
    }
}
