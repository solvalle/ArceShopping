package cr.ac.ucr.ecci.arceshopping;

import static android.app.Activity.RESULT_OK;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ImageGetter extends Fragment {
    public static final int GALLERY_RESULT = 0;
    public static final int CAMERA_RESULT = 1;
    private AlertDialog.Builder picOptions;
    private ImageView iView;
    private Uri pathToUserPic;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public ImageGetter(Context context, ImageView iView, Uri path){
        //Activity that invoked ImageGetter
        this.context = context;
        //Dialog that allows user to choose where to get their user pic from.
        picOptions = new AlertDialog.Builder(context);
        //Image View reference where ImageGetter will place the retrieved image on.
        this.iView = iView;
        //Path to where the user's pic is stored.
        pathToUserPic = path;

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        setPicOptions();
    }

    public String getUriPath() {
        return pathToUserPic.toString();
    }

    /**
     *  Set click listeners, title and default message to dialog.
     **/
    private void setPicOptions() {
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

    /**
     * Display options to user
     * */
    public void startPicUpdate() {
        picOptions.show();
    }

    /**
     * Start camera intent with a newly generated file.
     * Intent will store the taken picture on the provided
     * file.
     * */
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
            Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.arceshopping.android.fileprovider", photoFile);

            //Save path
            pathToUserPic = photoURI;
            //Add new file to intent, so intent returns the full sized image
            //and saves it into provided path.
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePicture, CAMERA_RESULT);
        } else {
            displayMessage("Ocurrió un error");
        }

    }

    /**
     * Start gallery intent that shows images only.
     * */
    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // Intent.ACTION_OPEN_DOCUMENT
        //Display only images
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_RESULT);
    }

    /**
     * Check result from either camera launch or gallery launch.
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If the result comes from gallery intent, then...
        if(requestCode == GALLERY_RESULT && data != null && data.getData() != null) {
            if (resultCode == RESULT_OK) {
                pathToUserPic = data.getData();
                iView.setImageURI(pathToUserPic);
                uploadImage();
            }else {
                displayMessage("No se eligió imagen");
            }
        }

        //If the result comes from camera intent, then...
        if(requestCode == CAMERA_RESULT) {
            if(resultCode == RESULT_OK ) {
                iView.setImageURI(pathToUserPic);
                uploadImage();
            }else {
                displayMessage("No se tomó foto");
            }
        }
    }

    /**
     * This method is used to upload an image to Firebase Storage
     */
    public void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Guardando imagen...");
        progressDialog.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("images/" + randomKey);
        imageRef.putFile(pathToUserPic)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        pathToUserPic = Uri.parse(uri.toString());
                                        progressDialog.dismiss();
                                        displayMessage("Imagen cargada con éxito. Recuerde actualizar los cambios.");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        displayMessage("Failed to upload");
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
                    }
               });
    }


    /**
     * This method is to be used by whatever class creates an instance of
     * ImageGetter. It checks if user had previously saved an image as their
     * user profile pic and if so, retrieves it.
     */
    public void retrieveUserPic() {
        //If user had previously saved an image as their profile pic, retrieve it
        if(!pathToUserPic .toString().equals("")) {
            System.out.println("IMAGE URL: " + pathToUserPic.toString());
            Picasso.get().load(pathToUserPic.toString()).into(iView);
        }
    }

    public void displayMessage(String message) {
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

