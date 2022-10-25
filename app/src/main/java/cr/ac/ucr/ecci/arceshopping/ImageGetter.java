package cr.ac.ucr.ecci.arceshopping;

import static android.app.Activity.RESULT_OK;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageGetter extends Fragment {
    public static final int GALLERY_RESULT = 0;
    public static final int CAMERA_RESULT = 1;
    private AlertDialog.Builder picOptions;
    private ImageView iView;
    private Uri pathToUserPic;
    private Context context;
    private Bitmap picFromCamera;
    private Boolean userTookPicture;

    public ImageGetter(Context context, ImageView iView, Uri path){
        this.context = context;
        picOptions = new AlertDialog.Builder(context);
        this.iView = iView;
        pathToUserPic = path;
        setPicOptions();
    }

    public String getUriPath() {
        return pathToUserPic.toString();
    }

    private void setPicOptions()
    {
        //Set click listeners, title and default message to dialog.
        picOptions.setTitle("Actualizar imagen");
        picOptions.setMessage("¿Cómo desea actualizar su imagen de perfil?");

        picOptions.setPositiveButton("Galería", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                launchGallery();
            }
        });
        picOptions.setNegativeButton("Cámara", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                launchCamera();
            }
        });
    }


    public void startPicUpdate() {
        picOptions.show();
    }

    private void launchCamera(){
        //Intent that launches camera
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            //Generate a new file.
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }

        if (photoFile != null) {
            //Create path for incoming image file
            Uri photoURI = FileProvider.getUriForFile(getActivity(),
                    "com.arceshopping.android.fileprovider",
                    photoFile);
            //Save path
            pathToUserPic = photoURI;
            //Add new file to intent, so intent returns the full sized image
            //and saves it into provided path.
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePicture, CAMERA_RESULT);
        }else {displayMessage("Ocurrió un error");}

    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_RESULT);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_RESULT) {

            if (resultCode == RESULT_OK) {
                pathToUserPic = data.getData();
                setProfilePic();
                userTookPicture = false;

            }else {
                displayMessage("No se eligió imagen");
            }
        }

        if(requestCode == CAMERA_RESULT) {
            if(resultCode == RESULT_OK )
            {
              //  picFromCamera = (Bitmap) data.getExtras().get("data");
                setProfilePic();
            }else {
                displayMessage("No se tomó foto");
            }


        }
    }

    public void retrieveUserPic() {
        //If user had previously saved an image as their profile pic, retrieve it
        if(!pathToUserPic.toString().equals("")){
            setProfilePic();
        }
    }

    private Bitmap fromUriToBitmap() {
        Bitmap resultPicture = null;

        try {
            //Get content resolver so we can open image as stream.
            ContentResolver cr = this.context.getContentResolver();
            //cr.takePersistableUriPermission(pathToUserPic, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //Open stream from picture's URI
            final InputStream imageStream = cr.openInputStream(pathToUserPic);
            //Generate bitmap from img specified in Uri
            resultPicture = BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            displayMessage("Ocurrió un error");
        }
        return  resultPicture;
    }

    private void setProfilePic() {
        Bitmap selectedImage = fromUriToBitmap();
        if(selectedImage != null) {
            iView.setImageBitmap(selectedImage);
        }
    }

    public void displayMessage(String message)
    {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    //Code taken from android official documentation. Available at:
    //https://developer.android.com/training/camera-deprecated/photobasics
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        return image;
    }
}

