package com.simpleinfo.uploadfileswithphp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.CacheControl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class UploaderAsyncTask extends AsyncTask<String,String,String> {

    private final static String TAG = "UploaderAsyncTask";
    private final static String URL = "http://192.168.43.202/upload_receiver.php";

    OkHttpClient mClient;
    List<Data> mDatas;
    MainActivity.CustomProgressListener mCustomProgressListener;


    public  UploaderAsyncTask(List<Data> mData,MainActivity.CustomProgressListener customProgressListener){

        mClient = new OkHttpClient();
        mDatas = mData;
        mCustomProgressListener = customProgressListener;

    }

    @Override
    protected String doInBackground(String... strings) {

        /**
         * for overall progress
         */
        mCustomProgressListener.setTotalFileToUpload(mDatas.size());


        for(Data  data : mDatas){

            MultipartBody.Builder mBuilder = new MultipartBody.Builder();
            mBuilder.setType(MultipartBody.FORM);
            File file = new File(data.getImagePath());


            mBuilder.addFormDataPart("image", data.getImageName(), RequestBody.create(MediaType.parse("image/jpeg"),file));
            mBuilder.addFormDataPart("image_description", "This is the description");

            MultipartBody requestBody = mBuilder.build();

            CustomRequestBody customRequestBody = new CustomRequestBody(requestBody,mCustomProgressListener,data);

            Request request = new Request.Builder()
                    .url(URL)
                    .post(customRequestBody)
                    .cacheControl(new CacheControl.Builder().noCache().build())
                    .build();


            try {


                Response response = mClient.newCall(request).execute();

                if(!response.isSuccessful()){

                    mCustomProgressListener.onError("Failed to upload with error code "+response.code(),data);
                }


                ResponseBody responseBodyCopy = response.peekBody(Long.MAX_VALUE);

                /**
                 * server response
                 */
                Log.e(TAG,responseBodyCopy.string());


            } catch (IOException e) {

                e.printStackTrace();



                mCustomProgressListener.onError(e.getMessage(),data);

            }finally {
                /**
                 *
                 */
                mCustomProgressListener.resetOverallUpload();
            }


        }



        return null;
    }







}
