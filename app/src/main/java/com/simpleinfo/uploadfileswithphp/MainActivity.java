package com.simpleinfo.uploadfileswithphp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int GET_IMAGE_REQUEST_CODE = 1;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ItemRecyclerViewAdapter mAdapter;

    List<Data> mDatas = new ArrayList<>();
    PopupOfProgress mPopupOfProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView  = this.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this,1);
        mRecyclerView.setLayoutManager(mLayoutManager);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.upload_file) {

            if(mDatas.size() == 0){

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Message");
                alertDialog.setMessage("Please select file.");
                alertDialog.show();
                return false;
            }

            mPopupOfProgress = new PopupOfProgress(this);
            mPopupOfProgress.show();

            new UploaderAsyncTask(mDatas, new CustomProgressListener()).execute();

            return true;

        }else if(id == R.id.select_file){

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_IMAGE_REQUEST_CODE);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GET_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //TODO: action


            ClipData clipData = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

                clipData = data.getClipData();
            }

            if(clipData == null){

                    String path = "";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        path = this.getPathForKitKatAbove(this,data.getData());

                    }else{

                        path = this.getRealPathFromURI(data.getData());
                    }


                    Data imageData = new Data();
                    imageData.setImagePath(path);
                    imageData.setImageName(this.getImageName(path));

                    mDatas.add(imageData);

                    mAdapter =  new ItemRecyclerViewAdapter(mDatas,this);

                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setItemViewCacheSize(1);

                    return;
                }




                int itemCount = clipData.getItemCount();

                for(int x = 0 ; x < itemCount ; x++){

                    ClipData.Item item = clipData.getItemAt(x);
                    Uri uri = item.getUri();
                    String path = "";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                        path = this.getPathForKitKatAbove(this,uri);

                    }else{

                        path= this.getRealPathFromURI(uri);


                    }


                    Data imageData = new Data();
                    imageData.setImagePath(path);
                    imageData.setImageName(this.getImageName(path));
                    mDatas.add(imageData);

                }




            mAdapter =  new ItemRecyclerViewAdapter(mDatas,this);

            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemViewCacheSize(1);



        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected String getPathForKitKatAbove(Context context, Uri uri){

        String filePath = "";

        if(DocumentsContract.isDocumentUri(context, uri)) {

            String id = DocumentsContract.getDocumentId(uri);
            String splitedId = id.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};


            Cursor cursor = getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column,  MediaStore.Images.Media._ID + "=?", new String[]{splitedId}, null);


            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return filePath;

    }
    protected String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private String getImageName(String path){
        String splitedString[] = path.split("/");


        return splitedString[splitedString.length -1];
    }

    private void updateStatusInView(){

        /**
         * dismiss upload dialog
         */
        mPopupOfProgress.mAlertDialog.dismiss();

        int count = mLayoutManager.getChildCount();

        for(int x = 0 ; x < count ; x++){

            CardView cardView = (CardView) mLayoutManager.getChildAt(x);

            Data data = (Data) cardView.getTag();
            TextView statusTextView = cardView.findViewById(R.id.status_textview);

            if(data.isUploaded()){

                statusTextView.setText("Status : Uploaded");

            }else{

                statusTextView.setText("Status : Not uploaded");
            }

        }


    }

    class CustomProgressListener implements CustomRequestBody.ProgressListener{

        int totalFileToUpload = 0 ;
        int totalFileAttempToUploaded = 0;

        float overAllDataUploaded = 0;


        public void setTotalFileToUpload(int totalFileToUpload) {
            this.totalFileToUpload = totalFileToUpload;


        }



        public void resetOverallUpload(){
            overAllDataUploaded = 0;
        }


        @Override
        public void progress(long uploaded, final long size, final Data data) {



            float sizeFloat = size;

            overAllDataUploaded += uploaded;



            final float percentageUpload = (overAllDataUploaded /  sizeFloat) * 100;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mPopupOfProgress.mPerFileProgress.setProgress((int) overAllDataUploaded);
                    mPopupOfProgress.mPerFileProgress.setMax((int) size);

                    mPopupOfProgress.mOverallProgress.setProgress(totalFileAttempToUploaded);
                    mPopupOfProgress.mOverallProgress.setMax(totalFileToUpload);
                    mPopupOfProgress.mFileNameTextView.setText(data.getImageName());
                    mPopupOfProgress.mPerFileProgressTextview.setText(String.format("%.2f",percentageUpload)+"");
                    mPopupOfProgress.mOverallProgressTextView.setText(totalFileAttempToUploaded +"/"+totalFileToUpload);
                }
            });
        }

        @Override
        public void onFileUploadDone(Data data) {

            totalFileAttempToUploaded++;
            data.setUploaded(true);

            if(totalFileToUpload <= totalFileAttempToUploaded){

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateStatusInView();
                    }
                });

            }

        }

        @Override
        public void onError(String errorMessage, final Data data) {

            data.setUploaded(false);

            totalFileAttempToUploaded++;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mPopupOfProgress.mFileNameTextView.setText("Failed :"+data.getImageName());
                        mPopupOfProgress.mOverallProgressTextView.setText(totalFileAttempToUploaded +"/"+totalFileToUpload);

                        if(totalFileToUpload <= totalFileAttempToUploaded){
                        updateStatusInView();
                        }
                    }
                });


        }


    }

}
