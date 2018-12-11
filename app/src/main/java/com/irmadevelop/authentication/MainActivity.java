package com.irmadevelop.authentication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int CHOOSER_IMAGES = 1;
    private static final String TAG = "MainActivity";
    private Button btnDownload;
    private Button btnUpload;
    private ImageView imvImage;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageReference = FirebaseStorage.getInstance().getReference();

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

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final StorageReference imagenCeluRef = storageReference.child("imagen_del_celu.jpg");//solo es referencia, no es el el nodo del arbol que contiene al archivo
                imvImage.setDrawingCacheEnabled(true);//para que la imagen no desaparezca
                imvImage.buildDrawingCache();//para que la imagen permanezca aunque en algun momento pueda ser sustraida

                //se requiere bitmap para el trabajo
                Bitmap bitmap = imvImage.getDrawingCache();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                byte[] imagenCeluByte = baos.toByteArray();
                UploadTask uploadTask = imagenCeluRef.putBytes(imagenCeluByte);
                //se tiene que ejecutar en un segundo plano por eso es un task

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Ocurrió un herror en la subida");
                        e.printStackTrace(); //para ver el error completo.
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this,"Subida exitosa", Toast.LENGTH_SHORT).show();
                        imagenCeluRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri uriPhoto = uri;
                                String photoURL = uriPhoto.toString();
                                Log.w(TAG, "URLPhoto > " + photoURL);
                            }
                        });
                        
                    }
                });

            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final File file;
                try{
                    file = File.createTempFile("irma","jpg");//creamos un archivo temporal
                    storageReference.child("irma.jpg").getFile(file)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    imvImage.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //si falla
                                    Log.e(TAG,"Ocurrió un error al mostrar la imagen");
                                    e.printStackTrace();
                                }
                            });
                }catch(Exception e){
                    Log.e(TAG,"Ocurrió un error en la descarga de la imagen");
                    e.printStackTrace();
                }
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
