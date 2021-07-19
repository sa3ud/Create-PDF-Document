package com.sa3ud.createpdf;

import android.content.ClipData;
import android.content.Context;
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

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {


    EditText etText;
    Button btnCreate;

    String getpath;

    File file;
Uri mArrayUri;


    private int image_rec_code = 1;
    static final int REQUEST_IMAGE_CAPTURE = 0;


    // declaring width and height
    // for our PDF file.
    int pageHeight = 600;
    int pageWidth = 500;

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

   //      initializing our variables.
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img_random);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 400, 300, false);




        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etText.getText().length()==0){

                    etText.startAnimation(shakeError());
                }else {

                    createPdf();
                }



            }
        });


        return parentView;
    }

    private void createPdf() {

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




        file = getOutputFile(etText.getText().toString());

        try {
            PdfDocument myPdfDocument = new PdfDocument();
            // two variables for paint "paint" is used
            // for drawing shapes and we will use "title"
            // for adding text in our PDF file.
            Paint myPaint = new Paint();
            // we are adding page info to our PDF file
            // in which we will be passing our pageWidth,
            // pageHeight and number of pages and after that
            // we are calling it to create our PDF.
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            // below line is used for setting
            // start page for our PDF file.
            PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
            // creating a variable for canvas
            // from our page of PDF.
            Canvas canvas = myPage.getCanvas();


            canvas.drawBitmap(scaledbmp,50,50,myPaint);


            // similarly we are creating another text and in this
            // we are aligning this text to center of our PDF file.
            myPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            myPaint.setColor(ContextCompat.getColor(getContext(), R.color.purple_200));
            myPaint.setTextSize(30);

            // below line is used for setting
            // our text to center of PDF.
            myPaint.setTextAlign(Paint.Align.CENTER);



            canvas.drawText(etText.getText().toString(), 100, 400, myPaint);

            // after adding all attributes to our
            // PDF file we will be finishing our page.
            myPdfDocument.finishPage(myPage);



            // below line is used to set the name of
            // our PDF file and its path.
            myPdfDocument.writeTo(new FileOutputStream(file));


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

     //   File file = new File(getpath, "Test.pdf");

        Uri pdfURI = FileProvider.getUriForFile(getContext(), getActivity().getPackageName()+ ".provider", file);
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



    public void share(File file) {


        Uri sharingUri = Uri.parse(file.getPath());

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, sharingUri);
        startActivity(share);
    }




    private File getOutputFile(String filename) {

        getpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
      //  File file = new File(getpath,  "Test.pdf");

        File root = new File(getpath, "Create PDF");
        boolean isFolderCreated = true;

        if (!root.exists()) {

            isFolderCreated = root.mkdir();
        } else {

        }

        if (isFolderCreated) {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = filename + "_" + timeStamp;

            return new File(root, imageFileName + ".pdf");
        } else {
            Toast.makeText(getContext(), "Folder is not created", Toast.LENGTH_SHORT).show();
            return null;
        }
    }



}