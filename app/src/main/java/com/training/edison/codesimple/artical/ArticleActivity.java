package com.training.edison.codesimple.artical;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.training.edison.codesimple.R;
import com.training.edison.codesimple.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ArticleActivity extends AppCompatActivity {

    private static final String TAG = "ArticleActivity";
    private String mTitle = null;
    private String mLink = null;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mTitle = getIntent().getStringExtra(ArticleBean.TITLE);
        mLink = getIntent().getStringExtra(ArticleBean.LINK);
        CollapsingToolbarLayout collapsingToolbarLayout
                = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(mTitle);
        Log.i(TAG, "onCreate: link " + mLink);
        MyAsyncTask mAsyncTask = new MyAsyncTask();
        mAsyncTask.execute();
    }

    public static void startActivity(Context context, String link, String title){
        Intent articlePost = new Intent(context, ArticleActivity.class);
        articlePost.putExtra(ArticleBean.LINK, link);
        articlePost.putExtra(ArticleBean.TITLE,title);
        context.startActivity(articlePost);
    }

    private class MyAsyncTask extends AsyncTask<Object, Object, String> {

        protected String doInBackground(Object... urls) {
            Document doc;
            String postBody = null;
            try {
                doc = Jsoup.connect(mLink).get();
                postBody = doc.select("div.p_part").toString();
                postBody = postBody.replaceAll("/_image", Utils.BLOG_URL + "/_image");
//                Log.i(TAG, "doInBackground: elements.toString\n" + postBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return postBody;
        }

        protected void onPostExecute(String result) {
            mWebView.loadDataWithBaseURL("x-data://base", result, "text/html", "utf-8", null);
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished: ");
            imgReset();
        }
    }

    private void imgReset() {
        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{" +
                "var img = objs[i];   " +
                "img.style.maxWidth = '100%'; img.style.height = 'auto';  " +
                "}" +
                "})()");
    }
}
