package com.grizzlynt.demo;

/**
 * Knips App 1.0 Copyright Grizzly GmbH © 2015. all rights reserved
 *
 * @author: Stefan Gsottbauer
 * @since: 19.1.2015
 * <p/>
 * Project: Knips Package: cc.grizzly.knips.adapter
 * <p/>
 * Contributors: - Grizzly GmbH. < office@grizzly.cc >
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adition.android.sdk.AditionView;
import com.adition.android.sdk.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> { // implements Filterable {

    private int VIEW_TYPE_POST = 0;
    private int VIEW_TYPE_AD = 1;


    private final DisplayMetrics mMetrics;
    private final int mWidth;
    private final int mHeight;

    private AditionView mAdFirst;
    private AditionView mAdSecond;
    private AditionView mAdThird;


    private ArrayList<SimpleListItem> mSimpleListItems;

    private Activity mCtx;
    private OnItemClickListener mItemClickListener;

    private int mTopMargin;

    public PostAdapter(Activity context) {

        mSimpleListItems = new ArrayList<>();
        mCtx = context;

        mMetrics = new DisplayMetrics();
        mCtx.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        mWidth = mMetrics.widthPixels / 2;
        mHeight = mWidth;

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == VIEW_TYPE_AD) {
            return new AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_ad_item, parent, false));
        } else {
            return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false));
        }
    }


    @Override
    public int getItemViewType(int position) {

        SimpleListItem item = mSimpleListItems.get(position);


        if (item.getType() == -1 || item.getType() == -2) {
            return VIEW_TYPE_AD;
        }

        return VIEW_TYPE_POST;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final SimpleListItem item = mSimpleListItems.get(position);
        holder.onBindViewHolder(item, position);
    }

    public void add(SimpleListItem item) {

        addAdPost();


        mSimpleListItems.add(mSimpleListItems.size(), item);
        notifyItemInserted(mSimpleListItems.size());

    }


    public void addAdPost() {
        if (mSimpleListItems.size() == 2) {
            mSimpleListItems.add(new SimpleListItem(-1));
            return;
        }

        if (mSimpleListItems.size() == 7) {
            mSimpleListItems.add(new SimpleListItem(-1));
            return;
        }

        if (mSimpleListItems.size() == 12) {
            mSimpleListItems.add(new SimpleListItem(-1));
            return;
        }
    }

    public void addAll(ArrayList<SimpleListItem> items) {

        if (items != null) {
            for (SimpleListItem item : items) {
                add(item);
            }

            notifyDataSetChanged();
        }
    }

    public void removeAll() {

        int i = (mSimpleListItems.size() > 0) ? mSimpleListItems.size() - 1 : 0;

        for (int j = i; j >= 0; j--) {
            remove(j);
        }
    }

    public void remove(int position) {
        try {
            mSimpleListItems.remove(position);
            notifyItemRemoved(position);
        } catch (Exception e) {

        }
    }

    public SimpleListItem getItem(int position) {
        return mSimpleListItems.get(position);
    }


    public ArrayList<SimpleListItem> getSimpleListItems() {
        return mSimpleListItems;
    }

    public boolean contains(SimpleListItem item) {
        return mSimpleListItems.contains(item);
    }

    @Override
    public int getItemCount() {
        return mSimpleListItems.size();
    }

    public interface OnItemClickListener {
        /**
         * Called when the view is clicked.
         *
         * @param v        view that is clicked
         * @param position position that is clicked;
         */
        public void onItemClick(View v, int position);
    }


    public int getTopMargin() {
        return mTopMargin;
    }

    public void setTopMargin(int mTopMargin) {
        this.mTopMargin = mTopMargin;
    }


    public class ImageViewHolder extends ViewHolder {

        private ImageView mMissionItemImage;

        public ImageViewHolder(View itemView) {
            super(itemView);

            mMissionItemImage = (ImageView) itemView.findViewById(R.id.post_image);

        }

        @Override
        public void onBindViewHolder(final Object object, final int position) {


            final SimpleListItem item = (SimpleListItem) object;


            Picasso.with(mCtx).load(item.getImageUrl()).centerCrop().resize(mWidth, mHeight).into(mMissionItemImage, new Callback() {
                @Override
                public void onSuccess() {

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mMissionItemImage.getLayoutParams();
                    params.height = mHeight - 10;
                    params.width = mWidth - 10;

                    mMissionItemImage.setLayoutParams(params);
                }

                @Override
                public void onError() {

                }
            });


            RecyclerView.LayoutParams params1 = (RecyclerView.LayoutParams) itemView.getLayoutParams();


            if (position == 0 || position == 1) {
                params1.topMargin = mTopMargin;
                itemView.setLayoutParams(params1);
            } else {
                params1.topMargin = 0;
                itemView.setLayoutParams(params1);
            }


            mMissionItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(view, position);
                    }
                }
            });

            itemView.setTag(item);
        }
    }


    public class AdViewHolder extends ViewHolder {

        private ImageView mAdImage;
        private RelativeLayout mAdView;

        public AdViewHolder(View itemView) {
            super(itemView);

            mAdImage = (ImageView) itemView.findViewById(R.id.ad_image);
            mAdView = (RelativeLayout) itemView.findViewById(R.id.ad_view);

        }

        @Override
        public void onBindViewHolder(Object object, final int position) {

            final SimpleListItem item = (SimpleListItem) object;


            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mAdImage.getLayoutParams();

            switch (position) {
                case 2:
                    if (mAdFirst == null) {
                        mAdFirst = createAdView("3371529", 2);
                        mAdFirst.execute();
                    }

                    if (mAdFirst.getParent() != null) {
                        ((ViewGroup) mAdFirst.getParent()).removeView(mAdFirst);
                    }
                    mAdView.addView(mAdFirst);
                    break;

                case 7:
                    if (mAdSecond == null) {
                        mAdSecond = createAdView("3371530", 7);
                        mAdSecond.execute();
                    }

                    if (mAdSecond.getParent() != null) {
                        ((ViewGroup) mAdSecond.getParent()).removeView(mAdSecond);
                    }

                    mAdView.addView(mAdSecond);
                    break;

                case 12:
                    if (mAdThird == null) {
                        mAdThird = createAdView("3371531", 12);
                        mAdThird.execute();
                    }

                    if (mAdThird.getParent() != null) {
                        ((ViewGroup) mAdThird.getParent()).removeView(mAdThird);
                    }

                    mAdView.addView(mAdThird);
                    break;
            }

                /*if(post.getContent() != null) {

                    Picasso.with(mCtx).load(post.getContent()).into(mAdImage);

                    params1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params1.width =  ViewGroup.LayoutParams.MATCH_PARENT;
                    mAdImage.setLayoutParams(params1);


                    if(post.getDescription() != null) {

                        try{

                            String tmp = post.getDescription();

                            if (!tmp.startsWith("http://") && !tmp.startsWith("https://")){
                                tmp = "http://" + tmp;
                            }

                            final Uri uri = Uri.parse(tmp);

                            mAdImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent myIntent = new Intent(Intent.ACTION_VIEW,uri);
                                    mCtx.startActivity(myIntent);
                                }
                            });

                        }catch (Exception e)
                        {

                        }
                    }
                }
                else
                {
                    params1.height = 0;
                    params1.width =  ViewGroup.LayoutParams.MATCH_PARENT;
                    mAdImage.setLayoutParams(params1);
                }*/

            if (item.getType() == -2 || item.getImageUrl() == null) {

                params1.height = 0;
                params1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mAdImage.setLayoutParams(params1);
                itemView.setPadding(0, 0, 0, 0);
            } else {

                itemView.setPadding(4, 4, 4, 4);
            }

            itemView.setTag(item);

        }


        private AditionView createAdView(final String content_id, final int position) {


            final AditionView view = new AditionView(mCtx, content_id, "2046", true);


            /*view.addProfileTargetingKey("user", "1");
            view.addProfileTargetingKey("userid", "1");
            view.addProfileTargetingKey("birthday_year", "1987");

            String userAgent = new WebView(mCtx).getSettings().getUserAgentString();
            if (userAgent.contains("Mobile")) {
                view.addProfileTargetingKey("devicetype", "phone");
            } else {
                view.addProfileTargetingKey("devicetype", "tablet");
            }

            view.addProfileTargetingKey("pagetype", "test");
            view.addProfileTargetingKey("title", "Fotochallenge");
            view.addProfileTargetingKey("docid", "51");*/


            view.addSDKObserver(new Observer() {
                @Override
                public void update(Observable observable, Object o) {

                    try {

                        String eventName = ((Bundle) o).getString("eventName");

                        /*if (eventName.equals("no_banner")) {
                            mSimpleListItems.get(position).setId(-2);
                            notifyItemChanged(position);
                            view.deleteSDKObservers();
                        }*/

                        if (eventName.equals(Constants.EVENT_BANNER_RECEIVED)) {
                            updateAdViews(position, view.toJSON());
                            //view.deleteSDKObservers();
                        }

                    } catch (Throwable e) {

                    }
                }
            });

            return view;
        }

        private void updateAdViews(int position, JSONObject adJson) {
            try {

                JSONObject data = adJson.getJSONObject("adJson");
                JSONArray files = data.getJSONArray("files");

                String adImage = ((JSONObject) files.get(0)).getString("url");

                mSimpleListItems.get(position).setImageUrl(adImage);

                notifyItemChanged(position);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(final Object object, int position);
    }


    public static class SimpleListItem {

        int mType;
        String mImageUrl;


        public SimpleListItem(int type) {
            mType = type;
        }

        public int getType() {
            return mType;
        }

        public void setType(int mType) {
            this.mType = mType;
        }

        public String getImageUrl() {
            return mImageUrl;
        }

        public void setImageUrl(String mImageUrl) {
            this.mImageUrl = mImageUrl;
        }

        @Override
        public String toString() {
            return "SimpleListItem{" +
                    "mType=" + mType +
                    ", mImageUrl='" + mImageUrl + '\'' +
                    '}';
        }
    }

}
