package com.dfirago.simplecrud.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dfirago.simplecrud.R;
import com.dfirago.simplecrud.dao.ProductItemDAO;
import com.dfirago.simplecrud.dao.impl.ProductItemDAOImpl;
import com.dfirago.simplecrud.domain.ProductItem;
import com.dfirago.simplecrud.enums.PhotoTaskType;
import com.dfirago.simplecrud.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by dmfi on 18/02/2017.
 */
public class ProductDetailsActivity extends Activity {

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_SELECT_FILE = 1;

    public static final String LABEL_CHOOSE_FROM_GALLERY = "Choose from Gallery";
    public static final String LABEL_CANCEL = "Cancel";
    public static final String LABEL_TAKE_PHOTO = "Take Photo";

    public static final String TMP_FILE_PATH = "/sdcard/tmp";

    private PhotoTaskType userChosenTask;

    public static final String PRODUCT_ITEM_ID = "product_item_id";

    private ProductItem productItem;
    private String imageUri;

    private ImageView productImageView;
    private TextView productTitleView;
    private TextView productDescriptionView;

    private ProductItemDAO productItemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productItemDAO = new ProductItemDAOImpl(getApplicationContext());

        productImageView = (ImageView) findViewById(R.id.product_details_image);
        productTitleView = (TextView) findViewById(R.id.product_details_title);
        productDescriptionView = (TextView) findViewById(R.id.product_details_description);

        Intent intent = getIntent();
        Long id = intent.getLongExtra(PRODUCT_ITEM_ID, -1L);

        if (id != -1L) {
            // existing item should be loaded
            populateFromDatabase(id);
        } else {
            // create new item
            productItem = new ProductItem();
            setDefaultImage();
        }

        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Button saveButton = (Button) findViewById(R.id.product_details_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productItem.setImageUri(imageUri);
                productItem.setTitle(productTitleView.getText().toString());
                productItem.setDescription(productDescriptionView.getText().toString());
                productItemDAO.save(productItem);
                ProductDetailsActivity.this.finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.product_details_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetailsActivity.this.finish();
            }
        });
    }

    private void populateFromDatabase(Long id) {
        productItem = productItemDAO.find(id);
        try {
            imageUri = productItem.getImageUri();
            Bitmap productImageBitmap = Utils.getBitmap(
                    getApplicationContext(), Uri.parse(imageUri));
            productImageView.setImageBitmap(productImageBitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_LONG).show();
        }
        productTitleView.setText(productItem.getTitle());
        productDescriptionView.setText(productItem.getDescription());
    }

    private void setDefaultImage() {
        imageUri = Utils.getUriFromDrawable(
                getApplicationContext(), R.drawable.no_image_found).toString();
        Bitmap productImageBitmap = BitmapFactory
                .decodeResource(getResources(), R.drawable.no_image_found);
        productImageView.setImageBitmap(productImageBitmap);
    }

    private void selectImage() {
        final CharSequence[] items = {LABEL_TAKE_PHOTO, LABEL_CHOOSE_FROM_GALLERY,
                LABEL_CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(LABEL_TAKE_PHOTO)) {
                    userChosenTask = PhotoTaskType.CAMERA;
                    if (Utils.checkPermission(ProductDetailsActivity.this, Manifest.permission.CAMERA))
                        cameraIntent();
                } else if (items[item].equals(LABEL_CHOOSE_FROM_GALLERY)) {
                    userChosenTask = PhotoTaskType.GALLERY;
                    if (Utils.checkPermission(ProductDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                        galleryIntent();
                } else if (items[item].equals(LABEL_CANCEL)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(TMP_FILE_PATH)));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select file"), REQUEST_SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utils.REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChosenTask.equals(LABEL_TAKE_PHOTO))
                        cameraIntent();
                    else if (userChosenTask.equals(LABEL_CHOOSE_FROM_GALLERY))
                        galleryIntent();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult();
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            addImage(data.getData());
        }
    }

    private void onCaptureImageResult() {
        File file = new File(TMP_FILE_PATH);
        try {
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), null, null));
            addImage(uri);
            if (!file.delete()) {
                Log.i("logImage", "Failed to delete " + file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addImage(Uri uri) {
        try {
            Bitmap scaledBitmap = Utils.getBitmap(getApplicationContext(), uri);
            productImageView.setImageBitmap(scaledBitmap);
            imageUri = uri.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
