package com.sa3ud.createpdf;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    EditText etText;
    Button btnCreate;

    String getpath;



    // declaring width and height
    // for our PDF file.
    int pageHeight = 3508;
    int pageWidth = 2480;

    // creating a bitmap variable
    // for storing our images
    Bitmap bmp, scaledbmp;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_home, container, false);


        checkAndRequestPermissions();


        etText = parentView.findViewById(R.id.et_text);
        btnCreate = parentView.findViewById(R.id.btn_create);

        // initializing our variables.
      //  bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
       // scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);




        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etText.getText().length()==0){

                    etText.startAnimation(shakeError());
                }else {

                    create_pdf();
                }



            }
        });


        return parentView;
    }

    private void create_pdf() {

        //                 This needs to resemble an A4 piece of paper
        //          <--------              W 2480                -------->
        //borderH   |                                                    |   |
        //       ^  |                                                    |   |
        //  59dp |  |                                                    |   |
        //  105px|  |                                                    |   |
        //       v  |                                                    |   |
        //    ^     |    vertical linear layout                          |   |
        //   DHH    |    ______________________________________________  |   |
        //   32dp   |    | Date header                                 | |   |
        //   84px   |    """"""""""""""""""""""""""""""""""""""""""""""  |   |
        //    v     |                                                    |   |
        //    ^     |                                                    |   |
        // padding  |    <Horizontal Linear layout>                      |   |
        //  16dp    |                                                    |   |
        //  42px    |                                                    |   |
        //    v     |     ______      ______      ______      ______     |   |
        //    ^     |    |      |    |      |    |      |    |      |    |    > H 3508px     // overall height
        //   CHH    |<-->| Card |<-->| Card |<-->| Card |<-->| Card |<-->|   |
        //  118dp   |40dp|      |16dp|      |16dp|      |16dp|      |40dp|   |
        //  310px   |105px------ 42px ------ 42px ------42px  ------105px|   |
        //    v     |                                                    |   |
        //borderH   |                                                    |   |
        //       ^  |                                                    |   |
        //  40dp |  |                                                    |   |
        //  105px|  |                                                    |   |
        //       v  |__________________Bottom of page____________________|  _|


        getpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(getpath, etText.getText().toString() + "Test.pdf");


        try {



            // creating an object variable
            // for our PDF document.
            PdfDocument mPdfDocument = new PdfDocument();

            // two variables for paint "paint" is used
            // for drawing shapes and we will use "title"
            // for adding text in our PDF file.
            Paint paint = new Paint();
            Paint title = new Paint();

            // we are adding page info to our PDF file
            // in which we will be passing our pageWidth,
            // pageHeight and number of pages and after that
            // we are calling it to create our PDF.
            PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

            // below line is used for setting
            // start page for our PDF file.
            PdfDocument.Page myPage = mPdfDocument.startPage(mypageInfo);

            // creating a variable for canvas
            // from our page of PDF.
            Canvas canvas = myPage.getCanvas();

            // below line is used to draw our image on our PDF file.
            // the first parameter of our drawbitmap method is
            // our bitmap
            // second parameter is position from left
            // third parameter is position from top and last
            // one is our variable for paint.
            canvas.drawBitmap(scaledbmp, 56, 40, paint);

            // below line is used for adding typeface for
            // our text which we will be adding in our PDF file.
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            // below line is used for setting text size
            // which we will be displaying in our PDF file.
            title.setTextSize(15);

            // below line is sued for setting color
            // of our text inside our PDF file.
            title.setColor(ContextCompat.getColor(getContext(), R.color.purple_200));

            // below line is used to draw text in our PDF file.
            // the first parameter is our text, second parameter
            // is position from start, third parameter is position from top
            // and then we are passing our variable of paint which is title.
            canvas.drawText("A portal for IT professionals.", 209, 100, title);
            canvas.drawText("Geeks for Geeks", 209, 80, title);

            // similarly we are creating another text and in this
            // we are aligning this text to center of our PDF file.
            title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            title.setColor(ContextCompat.getColor(getContext(), R.color.purple_200));
            title.setTextSize(15);

            // below line is used for setting
            // our text to center of PDF.
            title.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("This is sample document which we have created.", 396, 560, title);

            // after adding all attributes to our
            // PDF file we will be finishing our page.
            mPdfDocument.finishPage(myPage);

            // below line is used to set the name of
            // our PDF file and its path.
            // after creating a file name we will
            // write our PDF file to that location.
            mPdfDocument.writeTo(new FileOutputStream(file));

            try {
                Toast.makeText(getContext(), "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
                open_pdf();

            } catch (Exception e) {
                Toast.makeText(getContext(), "File Not Found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.d("tag", "-------------------->" + e);
        }


    }


    public void open_pdf() {

        File file = new File(getpath, etText.getText().toString() + "Test.pdf");
        Uri pdfURI = FileProvider.getUriForFile(getContext(), "com.ak.my_pdf" + ".provider", file);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(pdfURI, "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.d("Tag", "----------------->" + e);
        }
    }

    public TranslateAnimation shakeError() {

        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }


    private boolean checkAndRequestPermissions() {

        int storage = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storage2 = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (storage2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;

    }



}