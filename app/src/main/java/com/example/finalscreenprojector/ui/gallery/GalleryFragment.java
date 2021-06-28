package com.example.finalscreenprojector.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalscreenprojector.R;
import com.example.finalscreenprojector.logic.NetworkThread;

import static android.app.Activity.RESULT_OK;

public class GalleryFragment extends Fragment {

    Button selectImage;
    ImageView imageView;
    ImageButton send;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        selectImage = (Button) root.findViewById(R.id.image_selection);
        selectImage.setBackgroundColor(Color.argb(255, 205, 206, 206));
        imageView = (ImageView) root.findViewById(R.id.image_view);
        send = (ImageButton) root.findViewById(R.id.btn_save) ;
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE));
                launchGalleryIntent();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageView.getDrawable()!= null) {
                    BitmapDrawable bmpDrawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bmp = bmpDrawable.getBitmap();
                    System.out.println(bmp.getWidth()+" "+bmp.getHeight());
                    System.out.println("#####################");
                    NetworkThread networkThread = new NetworkThread(bmp, "image");
                    networkThread.start();
                    System.out.println("out of thread...");
                }
                else{
                    System.out.println("imageView is empty");
                }
            }
        });
        return root;
    }

    private void launchGalleryIntent() {
        System.out.println("in launchGalleryIntent...");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select Picture"), 200);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        System.out.println("in onActivityResult...");

        if(resultCode == RESULT_OK){
            if(requestCode == 200){
                Uri selectedImage = intent.getData();
                if(null!=selectedImage){
                    imageView.setImageURI(selectedImage);
                }
            }
        }
    }
}