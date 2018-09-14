package com.simpleinfo.uploadfileswithphp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.CustomViewHolder>  {

    List<Data> mDatas;
    Context mContext;

    public ItemRecyclerViewAdapter(List<Data> datas,Context context){

        mDatas = datas;
        mContext = context;

    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        CardView cardView = (CardView) inflater.inflate(R.layout.image_item,viewGroup,false);
        CustomViewHolder customViewHolder = new CustomViewHolder(cardView);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
        Data data = mDatas.get(i);

        File file = new File(data.getImagePath());
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
        customViewHolder.mImageView.setImageBitmap(bmp);


        customViewHolder.mCardView.setTag(data);
        customViewHolder.mImageNameTextView.setText(data.getImageName());



        if(data.isUploaded()){
            customViewHolder.mStatusTextView.setText("Status : Uploaded");


        }else{

            customViewHolder.mStatusTextView.setText("");

        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        ImageView mImageView;
        TextView mImageNameTextView;
        TextView mStatusTextView;


        CustomViewHolder(@NonNull CardView cardView) {
            super(cardView);
            mCardView = cardView;
            mImageView = mCardView.findViewById(R.id.image_view);
            mImageNameTextView = mCardView.findViewById(R.id.image_name_textview);
            mStatusTextView = mCardView.findViewById(R.id.status_textview);

        }
    }
}
