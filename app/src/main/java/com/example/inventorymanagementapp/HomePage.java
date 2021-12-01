package com.example.inventorymanagementapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.inventorymanagementapp.R.id.tb1;
import static com.example.inventorymanagementapp.R.id.txtdescription;

public class HomePage extends AppCompatActivity {

    private ArrayList<String> data = new ArrayList<String>();
    private ArrayList<String> data1 = new ArrayList<String>();
    private ArrayList<String> data2 = new ArrayList<String>();
    private ArrayList<String> data3 = new ArrayList<String>();

    private TableLayout table;
    EditText ed1, ed2, ed3, ed4, ed5, ed6, ed7, ed8, edCname, edCphone, edCemail;
//    TextView tv1;
    Button b1, b_discard, b_share, b_generate;
    Spinner sp1;
    int myPageWidth = 1200;
    Date dateobj;
    DateFormat dateFormat;
    String file = "receipt";
    private String fileContent;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        ed1 = findViewById(R.id.ed1);
        ed2 = findViewById(R.id.ed2);
        ed3 = findViewById(R.id.ed3);

        ed4 = findViewById(R.id.txtsub);
        ed6 = findViewById(R.id.txtdiscount);
        ed7 = findViewById(R.id.txtdescription);
        ed8 = findViewById(R.id.txttotalcost);
        edCname= findViewById(R.id.txtname);
        edCphone =findViewById(R.id.txtphone);
        edCemail = findViewById(R.id.txtemail);
//        tv1 = findViewById(R.id.add_details);
        b1 = findViewById(R.id.btn1);
        b_discard = findViewById(R.id.btnclear);
        b_generate = findViewById(R.id.btngeneratefile);

        sp1 = findViewById(R.id.spinnerdoctype);

        table = findViewById(R.id.tb1);
        linearLayout = findViewById(R.id.linearLayout_for_table_layout);


         //calling method create txt
        b_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createTxt();
                share();
            }

//            private void createTxt() {
//
//                fileContent = table.toString();
//                try {
//                    FileOutputStream fileOutputStream = openFileOutput(file, MODE_PRIVATE);
//                    fileOutputStream.write(fileContent.getBytes());
//                    fileOutputStream.close();
//
//                    File fileDirectory = new File(getFilesDir(), file);
//                    Toast.makeText(getBaseContext(), "FILE SAVED"+fileDirectory, Toast.LENGTH_LONG).show();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        });

        ed6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(ed4.getText().toString().trim()) ){
                    Toast errorToast = Toast.makeText(HomePage.this, "Make at least one entry in the form above", Toast.LENGTH_SHORT);
                    errorToast.show();
                }
                else {
                    int subtotal = Integer.parseInt(ed4.getText().toString());
                    int discountable = 0;
                    discountable= Integer.parseInt(ed6.getText().toString());
                    int finalcost = (subtotal-discountable);
                    ed8.setText(String.valueOf(finalcost));
                }

            }
        });


        b_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDiscardPressed();
            }
        });



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                if(TextUtils.isEmpty(ed1.getText().toString().trim()) || TextUtils.isEmpty(ed2.getText().toString().trim())  || TextUtils.isEmpty(ed3.getText().toString().trim()) ){
                    Toast errorToast = Toast.makeText(HomePage.this, "Name , price and quantity can't be null!", Toast.LENGTH_LONG);
                    errorToast.show();
                }
                else{
                    add();

                }
            }
        });
    }

    private void share() {
        Bitmap bitmap = getBitmapFromView(linearLayout);
        try {
            File fileTb = new File(this.getExternalCacheDir(), "background.png");
            FileOutputStream fOut = new FileOutputStream(fileTb);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            fileTb.setReadable(true, false);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileTb));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "share by"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("ResourceAsColor")
    private Bitmap getBitmapFromView(View view){
        Bitmap returnedBitmap = Bitmap. createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas= new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if(bgDrawable != null){
            bgDrawable.draw(canvas);
        }
        else{
            canvas.drawColor(android.R.color.white);
        }
        view.draw(canvas);


        return returnedBitmap;

    }

    //Creating method to add date to table layout
    public void add() {

        int tot;
        String name = ed1.getText().toString();
        int price = 0;
        try {
            price = Integer.parseInt(ed2.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast errorToast = Toast.makeText(HomePage.this, "Price can only take numeric values only!", Toast.LENGTH_SHORT);
            errorToast.show();
        }
        int qtt = 0;
        try {
            qtt = Integer.parseInt(ed3.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast errorToast = Toast.makeText(HomePage.this, "Quantity can only take numeric values only!", Toast.LENGTH_SHORT);
            errorToast.show();
        }

        tot = price * qtt;
        data.add(name);
        data1.add(String.valueOf(price));
        data2.add(String.valueOf(qtt));
        data3.add(String.valueOf(tot));

        TableLayout table = (TableLayout) findViewById(R.id.tb1);
        TableRow row = new TableRow(this);
        TextView t1 = new TextView(this);
        TextView t2 = new TextView(this);
        TextView t3 = new TextView(this);
        TextView t4 = new TextView(this);

        String total;
        int sum = 0;
        for (int i = 0; i < data.size(); i++) {
            String name1 = data.get(i);
            String prc = data1.get(i);
            String qt = data2.get(i);
            total = data3.get(i);

            t1.setText(name1);
            t2.setText(prc);
            t3.setText(qt);
            t4.setText(total);

            sum = sum + Integer.parseInt(data3.get(i).toString());
        }
//   adding data to table rows.
        row.addView(t1);
        row.addView(t2);
        row.addView(t3);
        row.addView(t4);




        table.addView(row);

        ed4.setText(String.valueOf(sum));

//  clearing the textviews.
        ed1.setText("");
        ed2.setText("");
        ed3.setText("");
        ed7.setText("");


        ed1.requestFocus();

    }



//    method for alert message box when discard button is pressed

    public void onDiscardPressed(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Discard all your entries");
        alertDialogBuilder.setMessage("All your entries in this form will be deleted!\nDo you want to continue?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(getIntent());
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder. create();
        alertDialog.show();
    }

}