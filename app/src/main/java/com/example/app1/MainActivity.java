package com.example.app1;

import com.example.app1.AndroidMultiPartEntity.ProgressListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LocationListener{
    public static final int REQUEST_IMAGE = 1;
    public static final int REQUEST_TAKE_PHOTO = 2;
    public static final int PICK_IMAGE_REQUEST = 3;
    private ProgressBar progressBar;
    private String filePath = null;
//    private TextView txtPercentage;
    private ImageView imgPreview;
    private Button btnUpload;
    private LinearLayout layoutFabCamera,layoutFabGallery;
    private CardView cvPredAcc,cvDecease,cvRemedy;
    private FloatingActionButton fabGetImageMode,fabCamera,fabGallery;
    private boolean fabExpanded = false;
    public long totalSize = 0;
    public Uri fileUri;
    public Uri fileUri_scaled;
    public File photoFile = null;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private String[] permissionList = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int MyVersion = Build.VERSION.SDK_INT;
        Log.e("VERSION","Version --"+MyVersion);

        super.onCreate(savedInstanceState);
        if(MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1){
            requestAppPermissions();
        }
        else{
            loadMainPage();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onLocationChanged(Location location) {
        Config.lattitude = String.valueOf(location.getLatitude());
        Config.longitude = String.valueOf(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

//        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    // Check for permission on latest android devices
    private void requestAppPermissions(){
        if (!hasReadPermissions() || !hasWritePermissions() || !hasLocationPermissions()) {
            ActivityCompat.requestPermissions(this, permissionList
                    , 1);
        }else{
            loadMainPage();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && checkAllPermissionGranted(grantResults)) {
                    loadMainPage();
                }else{
                    Toast.makeText(MainActivity.this, "Permission denied exiting the app", Toast.LENGTH_SHORT).show();
                    this.finishAffinity();
                }
            }
        }
    }
    private  boolean checkAllPermissionGranted(int[] grantResults){
        for (int per :grantResults){
            if(per == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasLocationPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }
    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutFabCamera.setVisibility(View.INVISIBLE);
        layoutFabGallery.setVisibility(View.INVISIBLE);
        fabGetImageMode.setImageResource(R.drawable.ic_add_black_24dp );
//        fabGetImageMode.setC(R.drawable.ic_input_add);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabCamera.setVisibility(View.VISIBLE);
        layoutFabGallery.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        fabGetImageMode.setImageResource(R.drawable.ic_close_black_24dp);
        fabExpanded = true;
    }
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                Config.camPhotoFile = createImageFile("");

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", Config.camPhotoFile);
            fileUri = photoUri;
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE);

        }
    }

    private void createResizeFile(Bitmap bitmap,String scale_uri){
        // BitmapFactory.decodeFile(org_uri)
        Bitmap photo = bitmap;
        photo = Bitmap.createScaledBitmap(photo, 256, 256, false);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(scale_uri);
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(Config.camPhotoFile != null){
            String org_uri = Config.camPhotoFile.getAbsolutePath();
            File file =  new File(org_uri);
            file.delete();
            Log.e("FILECOMPRESS","deleted file"+org_uri);
            Log.e("FILECOMPRESS","new file"+scale_uri);
        }

        Config.imageFilePath = scale_uri;
    }
    private File createImageFile(String type) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_"+type + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//        Toast.makeText(this, "File :"+image.toString(), Toast.LENGTH_SHORT).show();
        Config.imageFilePath = image.getAbsolutePath();
        //Toast.makeText(this, "File___ :"+Config.imageFilePath, Toast.LENGTH_SHORT).show();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if(Config.imageFilePath == null){
                    Toast.makeText(this,"Invalid Path for image",Toast.LENGTH_SHORT).show();
                }else{
//
                    // bimatp factory
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    // down sizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 6;


                    Bitmap bitmap = BitmapFactory.decodeFile(Config.imageFilePath, options);
                    bitmap = Bitmap.createScaledBitmap(bitmap,300,400,false);
                    imgPreview.setVisibility(View.VISIBLE);
                    imgPreview.setImageBitmap(bitmap);
//                    Toast.makeText(this,"Image height :"+ bitmap.getHeight() +"  Image width "+bitmap.getWidth(), Toast.LENGTH_SHORT).show();

                    // making upload button visible
                    btnUpload.setVisibility(View.VISIBLE);

                    // Dali phone changes
                    cvPredAcc.setVisibility(View.GONE);
                    cvDecease.setVisibility(View.GONE);
                    cvRemedy.setVisibility(View.GONE);

                    File photoFile_scaled = null;
                    try {
                        photoFile_scaled = createImageFile("scaled");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri photoUri_scaled = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile_scaled);
                    fileUri_scaled = photoUri_scaled;
                    createResizeFile(bitmap,photoFile_scaled.getAbsolutePath());

                }


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Please take a leaf photo", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = Bitmap.createScaledBitmap(bitmap,300,400,false);
                // Log.d(TAG, String.valueOf(bitmap));

                imgPreview.setVisibility(View.VISIBLE);
                imgPreview.setImageBitmap(bitmap);
                imgPreview.setImageBitmap(bitmap);
                btnUpload.setVisibility(View.VISIBLE);
                // Dali phone changes
                cvPredAcc.setVisibility(View.GONE);
                cvDecease.setVisibility(View.GONE);
                cvRemedy.setVisibility(View.GONE);
                File photoFile_scaled = null;
                try {
                    photoFile_scaled = createImageFile("scaled");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri photoUri_scaled = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile_scaled);
                fileUri_scaled = photoUri_scaled;
                createResizeFile(bitmap,photoFile_scaled.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void loadMainPage(){
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getGPSCoords();
        fabGetImageMode = (FloatingActionButton) findViewById(R.id.fabSetting);
        fabCamera = (FloatingActionButton) findViewById(R.id.fabCamera);
        fabGallery = (FloatingActionButton) findViewById(R.id.fabGallery);

        layoutFabCamera = (LinearLayout) this.findViewById(R.id.layoutFabCamera);
        layoutFabGallery = (LinearLayout) this.findViewById(R.id.layoutFabGallery);


        cvPredAcc = findViewById(R.id.cvPredAcc);
        cvDecease = findViewById(R.id.cvDecease);
        cvRemedy = findViewById(R.id.cvRemedy);

        imgPreview = (ImageView) findViewById(R.id.imageView1);
        btnUpload = (Button) findViewById(R.id.btnUpload);
//        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        fabGetImageMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Here app can start", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Here app can start", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
                openCameraIntent();
            }
        });
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Here app can start", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
                openGallery();
            }
        });
    }
    @SuppressLint("MissingPermission")
    private void getGPSCoords() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    // Button onClick method
    public void uploadData(View view){
        //Toast.makeText(this, "on click upload", Toast.LENGTH_SHORT).show();
//        Config.imageFilePath = fileUri.();
        // uploading the file to server
        progressBar.setVisibility(View.VISIBLE);
        new UploadFileToServer().execute();

    }
    // On image click
    private void openGallery(){
//        Toast.makeText(getApplicationContext(),"Go to gallery",Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

/**
 * Uploading the file to server
 * */
private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
    @Override
    protected void onPreExecute() {
        // setting progress bar to zero
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // Making progress bar visible
        progressBar.setVisibility(View.VISIBLE);

        // updating progress bar value
        progressBar.setProgress(progress[0]);

        // updating percentage value
//        txtPercentage.setText(String.valueOf(progress[0]) + "%");
    }

    @Override
    protected String doInBackground(Void... params) {
        return uploadFile();
    }

    @SuppressWarnings("deprecation")
    private String uploadFile() {
        String responseString = null;

        HttpClient httpclient = new DefaultHttpClient();
        Log.e("HTTPERRR","Http Client --"+httpclient);
        HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);
        Log.e("HTTPERRR","Http Post Client --"+httppost);
        //HttpGet httpget = new HttpGet(Config.FILE_UPLOAD_URL);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            File sourceFile = new File(Config.imageFilePath);

            Log.e("REQLOC",Config.lattitude+","+Config.longitude);
            // Adding file data to http body
            entity.addPart("file", new FileBody(sourceFile));

            // Extra parameters if you want to pass to server
            entity.addPart("lat",
                    new StringBody(Config.lattitude));
            entity.addPart("lng",
                    new StringBody(Config.longitude));
            String str = String.valueOf(entity.getContentLength());

            totalSize = entity.getContentLength();
            Log.e("REQUEST","Size of file"+str);


            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
//            HttpResponse response1 = httpclient.execute(httpget);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);

            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

        } catch (ClientProtocolException e) {
            responseString = e.toString();
        }catch (IOException e) {
            responseString = e.toString();
        }

        return responseString;

    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("RESPONSE", "Response from server: " + result);
        if(result.contains("Error occurred! Http Status Code: ") || result.contains("UnknownHostException")){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Unable to connect to server,Kindly check your internet connection !!",Toast.LENGTH_SHORT).show();
        }else {

            progressBar.setVisibility(View.GONE);
            btnUpload.setVisibility(View.GONE);


            TextView tvPredAccHeader = findViewById(R.id.tvPredAccHeader);
//            TextView tvPredAccDetails = findViewById(R.id.tvPredAccDetails);
            TextView tvRemedyHeader = findViewById(R.id.tvRemedyHeader);
            TextView tvRemedyDetils = findViewById(R.id.tvRemedyDetails);
            TextView tvDeceasesHeader = findViewById(R.id.tvDeceasesHeader);
            TextView tvDeceasesDetails = findViewById(R.id.tvDeceasesDetails);

            // showing the server response in an alert dialog
            //showAlert(result);
            String pred_accuracy = "";
            String disease_desc = "";
            String disease_name = "";
            String disease_remedy = "";
            String remedy_desc = "";
            String remedy_dose = "";
            String remedy_medicine = "";
            String remedy_name = "";
            String remedy_remarks = "";
            String message = "";
            String disease_desc_tv = "";
            String remedy_desc_tv = "";
            Boolean status = true;
            String status_msg = "Image Detected";

            JSONObject jObj;
            try {
                jObj = new JSONObject(result);
                JSONObject jResult = jObj.optJSONObject("result");
                Log.e("JSON", jResult.toString());
                if (jResult != null) {
                    JSONObject jDesease = jResult.optJSONObject("disease");
                    Log.e("JSON", jDesease.toString());
                    if (jDesease != null) {

                        cvDecease.setVisibility(View.VISIBLE);
//                        disease_desc = jDesease.optString("description");
//                        if (disease_desc != null && disease_desc != "") {
//                            disease_desc_tv = disease_desc_tv + "<b>"+"Description: " + "</b>"+ disease_desc;
//                        }

                        disease_name = jDesease.optString("name");
                        tvDeceasesHeader.setText(disease_name);

//                        disease_remedy = jDesease.optString("remedyName");
//                        if (disease_remedy != null && disease_remedy != "") {
//                            disease_desc_tv = disease_desc_tv + "<br>" + "<b>"+ "Remedy Name: " + "</b>"+ disease_remedy;
//                        }
//                        tvDeceasesDetails.setText(Html.fromHtml(disease_desc_tv));
                    }
                    JSONObject jRemedy = jResult.optJSONObject("remedy");
                    if (jRemedy != null) {
                        cvRemedy.setVisibility(View.VISIBLE);
                        tvRemedyHeader.setText("Remedy");

                        remedy_desc = jRemedy.optString("description");
//                        if (remedy_desc != null && remedy_desc != "") {
//                            remedy_desc_tv = remedy_desc_tv + "<b>"+ "Description: "+ "</b>" + remedy_desc;
//                        }

//                        remedy_dose = jRemedy.optString("dose");
//                        if (remedy_dose != null && remedy_dose != "") {
//                            remedy_desc_tv = remedy_desc_tv + "<br>" + "<b>"+ "Dose: " + "</b>"+ remedy_dose;
//                        }

//                        remedy_medicine = jRemedy.optString("medicine");
//                        if (remedy_medicine != null && remedy_medicine != "") {
//                            remedy_desc_tv = remedy_desc_tv + "<br>" + "<b>"+ "Medicine: " + "</b>"+ remedy_medicine;
//                        }
//
//                        remedy_name = jRemedy.optString("name");
//
//                        remedy_remarks = jRemedy.optString("remarks");
//                        if (remedy_remarks != null && remedy_remarks != "") {
//                            remedy_desc_tv = remedy_desc_tv + "<br>" + "<b>"+ "Remarks: " + "</b>"+ remedy_remarks;
//                        }

                        tvRemedyDetils.setText(Html.fromHtml(remedy_desc));
                    }
                    message = jResult.optString("message");
                    status = jResult.optBoolean("status");
                    if (status == false) {
                        status_msg = "Unable to detect image";
                        Toast.makeText(getApplicationContext(), status_msg, Toast.LENGTH_LONG).show();
                    }

                    pred_accuracy = jResult.optString("confidence");
                    if (pred_accuracy != null && pred_accuracy != "") {
                        cvPredAcc.setVisibility(View.VISIBLE);
                        tvPredAccHeader.setText("Prediction Certainty : " + pred_accuracy);
//                        tvPredAccDetails.setText(pred_accuracy);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
                super.onPostExecute(result);
    }

}

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}

