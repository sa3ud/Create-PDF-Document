package com.sa3ud.createpdf;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    EditText etText;
    Button btnCreate;

    String getpath;

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
        File file2 = new File(getpath, etText.getText().toString() + "Test.pdf");

        try {


            try {

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