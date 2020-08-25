package com.example.solenteatout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AddARestaurantActivity extends AppCompatActivity implements OnClickListener {

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        Button b = (Button) findViewById(R.id.btn1);
        b.setOnClickListener(this);
    }

    public void onClick(View view) {
        //getting the EditTexts using R.id
        EditText et1 = (EditText) findViewById(R.id.et1);
        EditText et2 = (EditText) findViewById(R.id.et2);
        EditText et3 = (EditText) findViewById(R.id.et3);
        EditText et4 = (EditText) findViewById(R.id.et4);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        if (view.getId() == R.id.btn1)
        //done when button is clicked (adding a restaurant)
        {
            String restaurantName = et1.getText().toString();
            String restaurantAddress = et2.getText().toString();
            String cuisine = et3.getText().toString();
            String rating = et4.getText().toString();
            bundle.putString("com.example.solenteatout.restaurantname", restaurantName);
            bundle.putString("com.example.solenteatout.restaurantaddress", restaurantAddress);
            bundle.putString("com.example.solenteatout.cuisine", cuisine);
            bundle.putString("com.example.solenteatout.rating", rating);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

}
