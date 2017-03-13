package com.example.allexey991.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by allexey991 on 17/05/2016.
 */
public class yandAuth extends Activity {
    WebView mWebview;
    public static final String APP_PREFERENCES = "authTOKENS";//Файл настроек
    public static final String APP_PREFERENCES_NAME = "TOKEN"; // token

    private void saveToken(String token) {
        SharedPreferences mSettings;
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_NAME, token);
        editor.apply();
        /*
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("yandTOKEN", token);
        editor.commit();*/
    }
    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            Pattern pattern = Pattern.compile("access_token=(.*?)(&|$)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find())
            {
                saveToken(matcher.group(1));
                finish();
            }
//            if (url.contains("access_token"))
//            {
//                startActivity(new Intent(yandAuth.this, MainActivity.class));
            return true;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        mWebview = (WebView) findViewById(R.id.webView1);
        mWebview.setWebViewClient(new MyWebViewClient());
        String url = "https://oauth.yandex.ru/authorize?response_type=token&client_id=f595d2a56efe4be18a83cf76265410ec";
//        new yandAuthProc().execute(url);
        mWebview.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        mWebview.loadUrl(url);
    }

    /*public class yandAuthProc extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String token = "";
            try {
                webview.loadUrl(params[0]);
                StartActivitynew Intent(Intent.ACTION_VIEW, address);
                doc = Jsoup.connect("https://forumsite.com/" + params[0]).get();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return token;
        }
        @Override
        protected void onPostExecute(String result, WebView webview) {
            super.onPostExecute(result);

            if (webview.getUrl().contains("acces_token"))
                startActivity(new Intent(yandAuth.this, MainActivity.class));

            return ;
        }
    }*/
}
