package com.irmadevelop.authentication;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.net.Uri;

public class MainActivity extends AppCompatActivity {

    private static final int CHOOSER_IMAGES = 1;
    private Button btnDownload;
    private Button btnUpload;
    private ImageView imvImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        imvImage = (ImageView) findViewById(R.id.imvImage);

        imvImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT); // Hace que se abra el chooser
                startActivityForResult(Intent.createChooser(i, "Selecciona una imagen"), CHOOSER_IMAGES);
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSER_IMAGES){
            Uri imageUri = data.getData();
            if (imageUri != null){
                imvImage.setImageURI(imageUri);
            }
        }
    }
}
