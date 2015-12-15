package com.grizzlynt.demo;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Gsottbauer on 15.12.15.
 */


public class JSONDownloader {

    private final DemoActivity.ResultCallback mCallback;

    public JSONDownloader(DemoActivity.ResultCallback callback) {
        mCallback = callback;
    }

    public void execute(final String url) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    ArrayList<PostAdapter.SimpleListItem> items = new ArrayList<>();


                    OkHttpClient client = new OkHttpClient();


                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Response response = null;
                    response = client.newCall(request).execute();


                    JSONObject object = new JSONObject(response.body().string());
                    JSONArray json_items = object.getJSONArray("items");

                    for (int i = 0; i < json_items.length(); i++) {
                        JSONObject item = (JSONObject) json_items.get(i);

                        PostAdapter.SimpleListItem simpleListItem = new PostAdapter.SimpleListItem(2);
                        simpleListItem.setImageUrl((item.getJSONObject("media")).getString("m"));

                        items.add(simpleListItem);
                    }

                    mCallback.success(items);

                } catch (Exception e) {
                    e.printStackTrace();

                    mCallback.error();
                }
            }
        }).start();
    }
}


