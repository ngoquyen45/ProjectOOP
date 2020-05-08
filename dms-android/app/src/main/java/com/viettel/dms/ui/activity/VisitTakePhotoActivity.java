package com.viettel.dms.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.StringUtils;
//import com.viettel.dms.fileprovider;
import com.viettel.dmsplus.sdk.MainEndpoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;

public class VisitTakePhotoActivity extends BaseActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 10002;
    public static final String PARAM_URL = "PARAM_URL";
    public static final String PARAM_IS_VISITED = "PARAM_IS_VISITED";
    public static final String PARAM_VISITED_PHOTO_ID = "PARAM_VISITED_PHOTO_ID";

    private String mOriginalPhotoPath;
    private String mCurrentPhotoPath;
    private String mPreviousPhotoPath;
    private boolean isVisited = false;
    private String visitedPhotoId;

    private Toolbar toolbar;
    private ImageView imageView;
    private PhotoViewAttacher mAttacher;
    private Boolean isTakingPicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_take_picture);
        toolbar = (Toolbar) findViewById(R.id.app_bar);

        imageView = (ImageView) findViewById(R.id.imageView);
        isVisited = getIntent().getBooleanExtra(PARAM_IS_VISITED, false);

        visitedPhotoId = getIntent().getStringExtra(PARAM_VISITED_PHOTO_ID);

        mOriginalPhotoPath = getIntent().getStringExtra(PARAM_URL);
        mCurrentPhotoPath = mOriginalPhotoPath;

        setSupportActionBar(toolbar);
        setTitle(isVisited ? R.string.visit_view_photo_title : R.string.visit_take_photo_title);
        mAttacher = new PhotoViewAttacher(imageView);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.isNullOrEmpty(mCurrentPhotoPath) && !mCurrentPhotoPath.equalsIgnoreCase(mOriginalPhotoPath)) {
                    new File(mCurrentPhotoPath).delete();
                }
                VisitTakePhotoActivity.this.setResult(RESULT_CANCELED);
                VisitTakePhotoActivity.this.finish();
            }
        });
        if (isVisited) {
            Picasso.with(this).load(MainEndpoint.get().getImageURL(visitedPhotoId)).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    mAttacher.update();
                }

                @Override
                public void onError() {

                }
            });

            return;
        }
        if (StringUtils.isNullOrEmpty(mCurrentPhotoPath) || !(new File(mCurrentPhotoPath).exists())) {
            synchronized (this) {
                if (!isTakingPicture) {
                    mPreviousPhotoPath = mCurrentPhotoPath;
                    dispatchTakePictureIntent();
                    isTakingPicture = true;
                }
            }
        } else {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    Bitmap image = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    imageView.setImageBitmap(image);
                    mAttacher.update();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isVisited) {
            getMenuInflater().inflate(R.menu.menu_visit_take_picture, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_take_picture) {
            mPreviousPhotoPath = mCurrentPhotoPath;
            mCurrentPhotoPath = null;
            synchronized (this) {
                if (!isTakingPicture) {
                    dispatchTakePictureIntent();
                    isTakingPicture = true;
                }
            }
        }
        if (item.getItemId() == R.id.action_done) {
            if (!mCurrentPhotoPath.equalsIgnoreCase(mOriginalPhotoPath) && !StringUtils.isNullOrEmpty(mOriginalPhotoPath)) {
                new File(mOriginalPhotoPath).delete();
            }
            Intent intent = new Intent();
            intent.putExtra(PARAM_URL, mCurrentPhotoPath);
            VisitTakePhotoActivity.this.setResult(RESULT_OK, intent);
            VisitTakePhotoActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            isTakingPicture = false;
            // Delete previous image except original image
            if (!StringUtils.isNullOrEmpty(mPreviousPhotoPath) && !mPreviousPhotoPath.equalsIgnoreCase(mOriginalPhotoPath) && new File(mPreviousPhotoPath).exists()) {
                new File(mPreviousPhotoPath).delete();
            }
            mPreviousPhotoPath = null;
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    setPicAndCompress();
                    mAttacher.update();
                }
            });
        } else {
            finish();
        }
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;

            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File
                DialogUtils.showMessageDialog(this,
                        R.string.error, R.string.error_unknown,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

               /* Uri photoURI = FileProvider.getUriForFile(this,
                        "com.viettel.dms.fileprovider",
                        photoFile);
*/

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


                //Uri contentUri = getUriForFile(getContext(), "com.mydomain.fileprovider", newFile);
                //Uri uriImage = FileProvider.getUriForFile(getBaseContext(), getApplicationContext().getPackageName()+".fileprovider", photoFile );
           //     takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
           //     startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

              //  takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);

                //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            DialogUtils.showMessageDialog(this,
                    R.string.error,
                    R.string.error_not_have_cammera_app,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
        }
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPicAndCompress() {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            bmOptions.inJustDecodeBounds = false;

            Bitmap scaleBm = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            Bitmap newPhoto = Bitmap.createScaledBitmap(scaleBm, scaleBm.getWidth() / 2, scaleBm.getHeight() / 2, false);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Rotate if need
            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    newPhoto = rotateImage(newPhoto, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    newPhoto = rotateImage(newPhoto, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    newPhoto = rotateImage(newPhoto, 270);
                    break;
            }
            // Compress
            newPhoto.compress(Bitmap.CompressFormat.JPEG, HardCodeUtil.IMAGE_QUALITY, byteArrayOutputStream);

            File f = new File(mCurrentPhotoPath);
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(byteArrayOutputStream.toByteArray());
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(newPhoto);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }
}
