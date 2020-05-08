package com.viettel.dms.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.IdDto;
import com.viettel.dmsplus.sdk.models.Location;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;

public class CloseVisitActivity extends BaseActivity {

    public static final String PARAM_CUSTOMER_INFO = "PARAM_CUSTOMER_INFO";
    public static final String PARAM_LOCATION = "PARAM_LOCATION";
    public static final String PARAM_PHOTO_ID = "PARAM_PHOTO_ID";
    public static final int REQUEST_IMAGE_CAPTURE = 10001;

    private CustomerForVisit customerInfo;
    private Location location;

    private String mCurrentPhotoPath;

    private Toolbar toolbar;
    private ImageView imageView;
    private PhotoViewAttacher mAttacher;

    private Dialog progressDialog;

    private String photoId;
    private Boolean isTakingPicture = false;

    private SdkAsyncTask<?> mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_visit);

        Bundle args = getIntent().getExtras();
        customerInfo = args.getParcelable(PARAM_CUSTOMER_INFO);
        location = args.getParcelable(PARAM_LOCATION);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        imageView = (ImageView) findViewById(R.id.imageView);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CloseVisitActivity.this.finish();
            }
        });
        setTitle(R.string.close_visit_title);
        mAttacher = new PhotoViewAttacher(imageView);
        synchronized (this) {
            if (!isTakingPicture) {
                dispatchTakePictureIntent();
                isTakingPicture = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mb_menu_close_visit, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_close_visit) {
            if (mCurrentPhotoPath == null) {
                return true;
            }
            progressDialog = DialogUtils.showProgressDialog(this, R.string.notify, R.string.close_visit_uploading_image, true);
            postImage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    private void postImage() {
        mTask = MainEndpoint.get().requestUploadImage(mCurrentPhotoPath).executeAsync(
                new RequestCompleteCallback<IdDto>() {

                    @Override
                    public void onSuccess(IdDto data) {
                        photoId = data.getId();
                        closeVisit();
                    }

                    @Override
                    public void onError(SdkException info) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        NetworkErrorDialog.processError(CloseVisitActivity.this, info);
                    }

                    @Override
                    public void onFinish(boolean canceled) {
                        mTask = null;
                    }
                }
        );
    }

    private void closeVisit() {
        mTask = MainEndpoint.get().requestCloseVisit(customerInfo.getId(), photoId, location).executeAsync(
                new RequestCompleteCallback<IdDto>() {
                    @Override
                    public void onSuccess(IdDto data) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        new File(mCurrentPhotoPath).delete();
                        Intent i = new Intent();
                        i.putExtra(PARAM_PHOTO_ID,photoId);
                        CloseVisitActivity.this.setResult(RESULT_OK,i);
                        CloseVisitActivity.this.finish();
                    }

                    @Override
                    public void onError(SdkException info) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        NetworkErrorDialog.processError(CloseVisitActivity.this, info);
                    }

                    @Override
                    public void onFinish(boolean canceled) {
                        mTask = null;
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {

    }

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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            isTakingPicture = false;
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

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

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
