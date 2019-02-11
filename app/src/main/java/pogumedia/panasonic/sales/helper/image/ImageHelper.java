package pogumedia.panasonic.sales.helper.image;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.widget.Toast;

import pogumedia.panasonic.sales.R;
import pogumedia.panasonic.sales.helper.util.Constants;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Ridwan Akbar on 24/12/17.
 */

public class ImageHelper {


    Activity activity;
    Context context;
    Fragment fragment;
    String directoryName;
    boolean isActivity = true;

    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int CAMERA_RESULT_CODE = -1;
    public static final int GALLERY_REQUEST_CODE = 2;


    public ImageHelper() {
    }

    public ImageHelper(Activity activity, String directoryName) {
        this.activity = activity;
        this.directoryName = directoryName;
        isActivity = true;
    }

    public ImageHelper(Fragment fragment, String directoryName) {
        this.activity = fragment.getActivity();
        this.fragment = fragment;
        this.directoryName = directoryName;
        isActivity = false;
    }

    public ImageHelper(Context context, String directoryName) {
        this.context = context;
        this.directoryName = directoryName;
        isActivity = false;
    }

    private String[] methodTakePhotos = new String[]{"Gallery", "Camera"};

    public void showChoosePhotoMethod() {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);

        builder.setItems(methodTakePhotos, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        pickPhoto();
                        break;
                    case 1:
                        pickFromCamera();
                        break;
                    default:
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (isActivity)
            activity.startActivityForResult(Intent.createChooser(intent, "Select"), GALLERY_REQUEST_CODE);
        else
            fragment.startActivityForResult(Intent.createChooser(intent, "Select"), GALLERY_REQUEST_CODE);
    }


    public void pickFromCamera() {
        removeCacheDir();

        File tempFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= 24) {
            Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".BaseActivity", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (isActivity)
                activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
            else
                fragment.startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (isActivity)
                activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
            else
                fragment.startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }


    public File onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = activity.managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        int rotasi = getCameraPhotoOrientation(activity, selectedImageUri, selectedImagePath);


        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);

        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Bitmap scaledBitmap = scaleDown(bitmap, 1000, false, rotasi);


        return saveBitmap(scaledBitmap);

    }

    public File onSelectFromCamera() {
        String nameFile;
        try {
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            Bitmap bitmap;
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);

            int rotasi = getCameraPhotoOrientation(activity, Uri.fromFile(f), f.getAbsolutePath());

            Bitmap scaledBitmap = scaleDown(bitmap, 1000, false, rotasi);


            return saveBitmap(scaledBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);


            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return rotate;
    }

    private static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                    boolean filter, float rotasi) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Matrix matrix = new Matrix();
        matrix.postRotate(rotasi);

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        Bitmap fixBitmap = Bitmap.createBitmap(newBitmap, 0, 0, width, height, matrix, true);

        return fixBitmap;
    }

    private File saveBitmap(Bitmap image) {
        File pictureFile = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        pictureFile = new File(activity.getCacheDir() + File.separator + "IMG_" + timeStamp + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            File file = copyFileToDownloads(Uri.fromFile(pictureFile));
            fos.close();

            return file;
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private File copyFileToDownloads(Uri croppedFileUri) throws Exception {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File newMediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(newMediaFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();


        return newMediaFile;

    }

    public void cropImageActivity(Uri source) {

        String filename = source.toString().substring(source.toString().lastIndexOf("/") + 1);

        Uri destination = Uri.fromFile(new File(activity.getCacheDir(), filename));
        UCrop uCrop = UCrop.of(source, destination);
        uCrop = advancedConfig(uCrop);
        uCrop.start(activity);
    }

    public void cropImageFragment(Uri source, Fragment fragment) {

        String filename = source.toString().substring(source.toString().lastIndexOf("/") + 1);

        Uri destination = Uri.fromFile(new File(activity.getCacheDir(), filename));
        UCrop uCrop = UCrop.of(source, destination);
        uCrop = advancedConfig(uCrop);
        uCrop.start(activity, fragment);
    }

    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(60);

        options.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        options.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        options.setActiveWidgetColor(ContextCompat.getColor(activity, R.color.colorAccent));
        options.setMaxBitmapSize(650);


        return uCrop.withOptions(options);
    }

    public File handleCrop(@NonNull Intent result) {

        final Uri resultUri = UCrop.getOutput(result);
        File file = null;
        if (resultUri != null) {
            try {
                file = saveCroppedImage(resultUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "Can't retrieve image", Toast.LENGTH_SHORT).show();
        }

        return file;
    }

    private File saveCroppedImage(Uri source) throws IOException {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName);
        String filename = source.toString().substring(source.toString().lastIndexOf("/") + 1);

        File newMediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);


        FileInputStream inStream = new FileInputStream(new File(source.getPath()));
        FileOutputStream outStream = new FileOutputStream(newMediaFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

        return newMediaFile;

    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(activity, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, "Unexpected error", Toast.LENGTH_SHORT).show();
        }
    }

    public String encodeImage(String uriFile) {
        String fileName = uriFile.substring(uriFile.lastIndexOf("/") + 1);

        if (uriFile.isEmpty()) {
            return "";
        }


        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName);
        File file_before_encode = new File(mediaStorageDir.getPath() + File.separator + fileName);
        String encodedBase64 = null;
        String base64 = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file_before_encode);
            byte[] bytes = new byte[(int) file_before_encode.length()];
            fileInputStreamReader.read(bytes);


            encodedBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
            // base64 = URLEncoder.encode(encodedBase64, "UTF-8");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return encodedBase64;

    }

    public void removeCacheDir() {
        File[] directory = activity.getCacheDir().listFiles();
        if (directory != null) {
            for (File file : directory) {
                if (file.getName().contains("temp"))
                    file.delete();
            }
        }
    }
}