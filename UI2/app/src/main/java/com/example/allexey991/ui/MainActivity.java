package com.example.allexey991.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.ResourcesHandler;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private DropboxAPI<AndroidAuthSession> mDBApi;
    final static private String APP_KEY = "6sx73506sktylog";
    final static private String APP_SECRET = "axo766yap345238";

    SharedPreferences mSettings;
    MailCloudClient mailCloudClient = new MailCloudClient();

/*------------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RequestQueue queue = Volley.newRequestQueue(this);
        final ListView listView = (ListView) findViewById(R.id.listView);
//      Разобраться с загрузкой файлов на Облако.mail
        assert listView != null;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mailCloudClient.downloadFile("Test.jpg",
                        mailCloudClient.toCloudRedirectRequest(
                                mailCloudClient.authRequest("allexey991","fktrctq687","list.ru")));
                final Dialog dialog = new Dialog(MainActivity.this,R.style.optionsStyleDialog);
                dialog.setContentView(R.layout.options);
                dialog.setTitle("Properties");
                TextView text = (TextView) dialog.findViewById(R.id.element_name);
                text.setText(String.valueOf(parent.getAdapter().getItem(position)));
                dialog.show();
            }
        });
        final ArrayList<String> filelist = new ArrayList<String>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.a, filelist);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LayoutInflater inflater = getLayoutInflater();

        final AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        final AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);


        final Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_dropBox).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_yandexDisk).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_Mega).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_cloudMail).withIdentifier(4),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_downloads).withIdentifier(5),
                        new SectionDrawerItem().withName(R.string.action_settings).withIdentifier(5),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        final String itemName;
                        switch (position) {
                            case 0:
                                itemName = "DropBox";
                                mSettings = getSharedPreferences("authTOKENS", Context.MODE_PRIVATE);
                                filelist.clear();
                                if(mSettings.contains("DropBoxToken"))
                                {
                                    session.setOAuth2AccessToken(mSettings.getString("DropBoxToken",""));
                                    mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                                    DB_GetList DB_get = new DB_GetList();
                                    DB_get.execute(session);
                                    try {
                                        filelist.addAll(DB_get.get());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }else
                                {
                                    mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
                                }
                                break;
                            case 1:
                                itemName = "Яндекс Диск";
                                filelist.clear();
                                mSettings = getSharedPreferences("authTOKENS", Context.MODE_PRIVATE);
                                if (mSettings.contains("TOKEN")) {
                                    Log.d("test", mSettings.getString("TOKEN", ""));
                                    Credentials cred = new Credentials("f595d2a56efe4be18a83cf76265410ec", "ARXiWssAAwBJK5XLUdtQQC6v3XPVQG3A9Q");
                                    Ynd_GetList Ynd_GetList = new Ynd_GetList();
                                    Ynd_GetList.execute(cred);
                                    try {
                                        List<mail_item> yandItemList = Ynd_GetList.get();
                                        for (int i=0 ; i<yandItemList.size();i++){
                                            filelist.add(yandItemList.get(i).name);
                                        }
                                    } catch (InterruptedException e) {e.printStackTrace();

                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                    startActivity(new Intent(MainActivity.this, yandAuth.class));
                                break;
                            case 3:
                                itemName = "Облако.Mail";
                                filelist.clear();

                                List<mail_item> mailItemList = mailCloudClient.CreateList(mailCloudClient.ListOfFiles(
                                            mailCloudClient.toCloudRedirectRequest(
                                                mailCloudClient.authRequest("allexey991","fktrctq687","list.ru"))));
                                for (int i=0 ; i<mailItemList.size();i++){
                                    filelist.add(mailItemList.get(i).name);
                                }

                                break;

                            case 2:
                                itemName = "Chrome";
                                filelist.clear();
                                break;
                            case 4:
                                itemName = "Settings";
                                filelist.clear();
                                break;
                            case 5:
                                itemName = "Help";
                                filelist.clear();
                                break;
                            default:
                                itemName = "Home";
                                break;
                        }
                        toolbar.setTitle(itemName);
                        adapter.notifyDataSetChanged();
                        assert listView != null;
                        listView.setAdapter(adapter);
                        return false;
                    }

                })
                .build();
    }

    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                mDBApi.getSession().finishAuthentication();
                Log.d("dd","dd");
                String tokendrop = mDBApi.getSession().getOAuth2AccessToken();
                Log.d("dd",tokendrop);

                SharedPreferences mSettings;
                mSettings = getSharedPreferences("authTOKENS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("DropBoxToken", tokendrop);
                editor.apply();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    public void ResultListCloud(Toolbar toolbar1,String name,ArrayAdapter<String> adapterlist,ListView listView1){
        toolbar1.setTitle(name);
        adapterlist.notifyDataSetChanged();
        assert listView1 != null;
        listView1.setAdapter(adapterlist);
    }
    class DB_GetList extends AsyncTask<AndroidAuthSession, Void, ArrayList<String>>{
        @Override
        protected ArrayList<String> doInBackground(AndroidAuthSession... params) {
            AndroidAuthSession sessionThread = params[0];
            DropboxAPI<AndroidAuthSession> mDBApiThread = new DropboxAPI<AndroidAuthSession>(sessionThread);
            DropboxAPI.Entry dirent = null;
            final ArrayList<String> filelistThread = new ArrayList<String>();
            try {
                dirent = mDBApiThread.metadata("/", 1000, null, true, null);
                filelistThread.clear();
                for (DropboxAPI.Entry ent : dirent.contents) {
                    filelistThread.add(ent.fileName());
                }
            } catch (DropboxException e) {
                System.out.println("Error Detail " + e.getMessage());
            }
            System.out.println(filelistThread.size());
            return filelistThread;
        }
    }
    class Ynd_GetList extends AsyncTask<Credentials, Void, List<mail_item>>{

        @Override
        protected List<mail_item> doInBackground(Credentials... params) {
            final List<mail_item> yandItemList = new ArrayList<mail_item>();
            RestClient restclient = new RestClient(params[0]);
            ResourceList reslist = new ResourceList();
            ResourcesArgs args = new ResourcesArgs.Builder()
                    .setPath("/")
                    .setSort(ResourcesArgs.Sort.name)
                    .setLimit(20)
                    .setOffset(0)
                    .setParsingHandler(new ResourcesHandler() {
                        @Override
                        public void handleItem(Resource item) {
                            mail_item yandItem = new mail_item(item.getName().toString(), (int) item.getSize());
                            yandItemList.add(yandItem);
                        }
                    })
                    .build();
            try {
                reslist = restclient.getFlatResourceList(args);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServerIOException e) {
                e.printStackTrace();
            }
            return yandItemList;
        }
    }

}


