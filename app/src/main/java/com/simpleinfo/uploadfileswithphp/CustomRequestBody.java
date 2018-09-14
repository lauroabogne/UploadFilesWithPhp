package com.simpleinfo.uploadfileswithphp;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class CustomRequestBody extends RequestBody {

    RequestBody mDelegateRequestBody;
    ProgressListener mProgressListener;
    CountingSink mCountingSink;
    Data mData;

    long mContentLength = 0;
    CustomRequestBody(RequestBody delegateRequestBody,ProgressListener progressListener,Data data){

        mDelegateRequestBody = delegateRequestBody;

        try {
            mContentLength = mDelegateRequestBody.contentLength();
            Log.e("size",mDelegateRequestBody.contentLength()+"");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mProgressListener = progressListener;
        mData = data;
    }

    @Override
    public long contentLength() {


        return mContentLength;
    }

    @Override
    public MediaType contentType() {
        return mDelegateRequestBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        if(mCountingSink == null){

            mCountingSink = new CountingSink(sink);


        }


        //CountingSink countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(mCountingSink);
        mDelegateRequestBody.writeTo(bufferedSink);
        bufferedSink.flush();

    }

    protected final class CountingSink extends ForwardingSink {

        long mUploaded = 0;
        long mContentLenght = 0;
        public CountingSink(Sink delegate) {
            super(delegate);

            mContentLenght = contentLength();
        }
        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            mUploaded += byteCount;

            if(mProgressListener !=null){
                mProgressListener.progress(byteCount,mContentLenght,mData);

                if( mUploaded >= CustomRequestBody.this.mContentLength ){

                    mProgressListener.onFileUploadDone(mData);
                }
            }


        }
    }
    public interface ProgressListener {
        void progress(long uploaded, long size, Data data);
        void onFileUploadDone(Data data);
        void onError(String errorMessage , Data data);
    }
}
