package com.example.dineshyalla.ibuy.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dineshyalla.ibuy.Database.DatabaseHelper;
import com.example.dineshyalla.ibuy.R;
import com.example.dineshyalla.ibuy.model.ProductClientClass;
import com.example.dineshyalla.ibuy.service.ProductClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BarcodeFetch extends AppCompatActivity {

    public int price = 0;
    public int tempPrice = 0;
    public String barcode = "";
    public String fName="";
    public String lName="";
    public String fFood="";
    public int quan;
    public int price_new;
    public int price_final;
    TextView etFirstName,etLastName,etFavFood, btnFetch, btnScan;
    ImageView img;
    TextView txtAdd;
    DatabaseHelper myDB;
    public ArrayList<String> listBarcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_fetch);

        Intent intent = getIntent();
        listBarcode = intent.getStringArrayListExtra("test");
        TextView textView = (TextView) findViewById(R.id.barcodeView);
        if(listBarcode.size()>0){
            for (String str : listBarcode) {
                textView.setText(str);
                Log.d("msg", str);
                barcode = str;
            }
        }


        etFavFood = (TextView) findViewById(R.id.etFavFood);
        etFirstName = (TextView) findViewById(R.id.etFirstName);
        etLastName = (TextView) findViewById(R.id.etLastName);
        txtAdd = (TextView) findViewById(R.id.txtAdd);
        img = (ImageView) findViewById(R.id.cartView);
        btnFetch = (TextView)findViewById(R.id.btnFetch);
        btnScan = (TextView)findViewById(R.id.btnScan);

        myDB = new DatabaseHelper(this);

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNetworkRequest();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String priceString = Integer.toString(price);
                Intent intent = new Intent(BarcodeFetch.this,ViewListContents.class);
                intent.putExtra("price", priceString);
                startActivity(intent);
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BarcodeFetch.this,Dashboard.class);
                startActivity(intent);
            }
        });




        txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                price = price + tempPrice;
                //quan = Integer.parseInt(etLastName.toString());
                quan = Integer.parseInt(etLastName.getText().toString());
                price_new = Integer.parseInt(etFavFood.getText().toString().substring(1));
                price_final = quan*price_new;
                String fPrice = Integer.toString(price_final);
               // String fPrice = Integer.toString(price);
                etFavFood.setText(fPrice);
                if(fName.length() != 0 && lName.length() != 0 && fFood.length() != 0){
                    Toast.makeText(BarcodeFetch.this,"Price: " + fPrice,Toast.LENGTH_LONG).show();

                    AddData(fName,lName, fFood, fPrice);
                    etFavFood.setText("");
                    etLastName.setText("");
                    etFirstName.setText("");
                }else{
                    Toast.makeText(BarcodeFetch.this,"You must put something in the text field!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setNetworkRequest() {

        //create retrofit instance
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://ibuyapp-env.us-east-2.elasticbeanstalk.com/webapi/myresource/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        ProductClient client = retrofit.create(ProductClient.class);
        Call<ProductClientClass> call = client.getProduct(barcode);
        call.enqueue(new Callback<ProductClientClass>() {
            @Override
            public void onResponse(Call<ProductClientClass> call, Response<ProductClientClass> response) {
                Toast.makeText(getApplicationContext(), "Success " + " " + response.body().getProductName(), Toast.LENGTH_SHORT).show();
                fName = fName + response.body().getProductName();
                lName = lName + response.body().getWeight();
                fFood = fFood + "$"+response.body().getProductprice();
                tempPrice = tempPrice + response.body().getProductprice();
                etFirstName.setText(fName);
                etLastName.setText(lName);
                etFavFood.setText(fFood);

            }

            @Override
            public void onFailure(Call<ProductClientClass> call, Throwable t) {

            }
        });

    }


    public void AddData(String firstName,String lastName, String favFood, String fPrice){
        boolean insertData = myDB.addData(firstName,lastName,favFood,fPrice);

        if(insertData==true){
            Toast.makeText(BarcodeFetch.this,"Item Added To Cart!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(BarcodeFetch.this,"Something went wrong",Toast.LENGTH_LONG).show();
        }
    }
}
