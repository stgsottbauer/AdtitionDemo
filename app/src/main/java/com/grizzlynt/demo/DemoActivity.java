package com.grizzlynt.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.adition.android.sdk.AditionView;
import com.adition.android.sdk.Constants;
import com.adition.android.sdk.browser.AditionBrowser;
import com.adition.android.sdk.util.Log;
import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class DemoActivity extends Activity
{
  private AditionView aditionView;
  private PostAdapter mAdapter;
  private RecyclerView mRecyclerView;
  private GridLayoutManager mGridLayoutManager;
  private PullRefreshLayout mRefreshLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.setLogLevel(Log.LEVEL_DEBUG);
    //setUpOldAdView();

    setContentView(R.layout.recylver_view_activity);

    mAdapter = new PostAdapter(this);

    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    mGridLayoutManager = new GridLayoutManager(this, 2);
    mRefreshLayout = (PullRefreshLayout) findViewById(R.id.refresh_layout);
    mRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {

        getItems();

      }
    });

    //get full width based on position
    mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {

        switch (position) {
          case 2:
          case 7:
          case 12:
            return 2;

          default:
            return 1;
        }
      }
    });

    //set manager and adapter
    mRecyclerView.setLayoutManager(mGridLayoutManager);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setAdapter(mAdapter);

    getItems();

  }


  private void getItems()
  {

    JSONDownloader downloader = new JSONDownloader(mCallback);
    downloader.execute("https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1");
  }


  private ResultCallback mCallback = new ResultCallback() {
    @Override
    public void success(ArrayList<PostAdapter.SimpleListItem> items) {
      mAdapter.removeAll();
      mAdapter.addAll(items);

      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          mRefreshLayout.setRefreshing(false);
        }
      });
    }

    @Override
    public void error() {

    }
  };


  public interface ResultCallback{

    void success(ArrayList<PostAdapter.SimpleListItem> items);
    void error();


  }


  private void setUpOldAdView()
  {

    setContentView(R.layout.demo_activity);

    /* configuration variables */
    String networkID     = "2046";
    String contentunitID = "3371530";
    boolean isInline     = true;

    /* creating AditionView */
    //this.aditionView = new AditionView(this, contentunitID, networkID, isInline);
    aditionView.setBackgroundColor(Color.WHITE);
    aditionView.addProfileTargetingKey("ad", "pics");

    /* registering event handler */
    aditionView.addJSObserver(this.onEvent);
    aditionView.addSDKObserver(this.onEvent);

    /* use custom Browser */
    aditionView.setBrowser(this.demoBrowser);

    /* start loading content */
    aditionView.execute();

    /* not inserting aditionView until it really has a banner */
  }


  /**
   * event observer for ad events
   */
  private Observer onEvent = new Observer() {
    /**
     * @param observable event caller
     * @param o event payload
     */
    @Override
    public void update(Observable observable, Object o) {
      int adWidth = aditionView.getAdWidth();
      int adHeight = aditionView.getAdHeight();
      String eventName = ((Bundle) o).getString("eventName");

      // simple event toast
      String text = ((Bundle) o).getString("eventName") + "\n" + Integer.toString(adWidth) + " x " + Integer.toString(adHeight);
      Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();


      RelativeLayout l = (RelativeLayout) findViewById(R.id.container);

      if (eventName.equals(Constants.EVENT_DOM_LOADED)) {
        // a banner was received. resize ad container to full width and proportional height

        int adContainerWidth = l.getWidth();
        double ratio = (double)adHeight / (double)adWidth;
        int adContainerHeight = (int)(ratio * adContainerWidth);

        FrameLayout.LayoutParams newAdContainerDimensions = new FrameLayout.LayoutParams(adContainerWidth, adContainerHeight);
        l.setLayoutParams(newAdContainerDimensions);

        /* insert AditionView into layout */
        l.addView(aditionView);

      } else if (eventName.equals(Constants.EVENT_NO_BANNER)) {
        // remove view if no banner was received
        l.removeView(aditionView);
      }
    }
  };


  /**
   * custom browser implementation
   */
  private AditionBrowser demoBrowser = new AditionBrowser() {
    /**
     * @param url Click URL to process
     */
    @Override
    public void openBrowserForAd(String url) {
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      startActivity(browserIntent);
//      finish();
    }
  };
}
