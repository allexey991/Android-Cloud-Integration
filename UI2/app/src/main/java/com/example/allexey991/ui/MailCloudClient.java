package com.example.allexey991.ui;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by allexey991 on 30/05/2016.
 */
public class MailCloudClient{


    final OkHttpClient client = new OkHttpClient();
    CookieManager cookieManager = new CookieManager();
    String MailToken = "7TQxhH8ASV4CDkQsnKiKDWEYszCsVh2V";
    String DownLoadLink = "https://cloclo38.datacloudmail.ru/get/";
    String SdcToken = "7DAcjbVnLOtYSya0";
    String Path = "";

    public OkHttpClient authRequest (final String login, final String password, final String domain){
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                RequestBody formBody = new FormEncodingBuilder()
                        .add("Login", login)
                        .add("Password", password)
                        .add("Domain", domain)
                        .build();
                Request request = new Request.Builder()
                        .url("https://auth.mail.ru/cgi-bin/auth")
                        .post(formBody)
                        .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    System.out.println(Integer.toString(response.code()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        return client;
    }
    class MyTask extends AsyncTask<OkHttpClient, Void, String>{
        @Override
        protected String doInBackground(OkHttpClient... params) {
            OkHttpClient client1 = params[0];
            Request request = new Request.Builder()
                    .url("https://cloud.mail.ru/api/v2/folder?token="+MailToken+"&sdc="+SdcToken+"&home=/")
                    .build();

            Response response = null;
            try {
                response = client1.newCall(request).execute();
                System.out.println(Integer.toString(response.code()));
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
    public OkHttpClient toCloudRedirectRequest(final OkHttpClient client1){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://cloud.mail.ru")
                        .build();

                Response response = null;
                try {
                    response = client1.newCall(request).execute();
                    System.out.println(Integer.toString(response.code()));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return client1;

    }
    public String ListOfFiles(final OkHttpClient client2){
        final String jsonString;
        MyTask mt = new MyTask();
        mt.execute(client2);
        try {
            return jsonString = mt.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();

        }
        return null;

    }
    public List<mail_item> CreateList(String json){
        Gson gson = new Gson();
        json = json.substring(json.indexOf("["),json.lastIndexOf("]")+1);
        System.out.println(json);
        Type itemsListType = new TypeToken<List<mail_item>>() {}.getType();
        List<mail_item> listItemsDes = new Gson().fromJson(json,itemsListType);
        return listItemsDes;
    }
    public void downloadFile (String fileName, OkHttpClient client){
        Path = fileName;
        DownloadProccess dwnP = new DownloadProccess();
        dwnP.execute(client);

    }
    class DownloadProccess extends AsyncTask<OkHttpClient, Void, Void> {
        @Override 
        protected Void doInBackground(OkHttpClient... params) {
            OkHttpClient client1 = params[0];
            Request request = new Request.Builder()
                    .url(DownLoadLink+"Test.jpg")
                    .build();

            Response response = null;
            try {
                response = client1.newCall(request).execute();
                System.out.println(Integer.toString(response.code()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
