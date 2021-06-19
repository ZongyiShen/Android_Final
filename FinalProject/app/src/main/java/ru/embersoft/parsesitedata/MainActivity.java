package ru.embersoft.parsesitedata;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParseAdapter(parseItems, this);
        recyclerView.setAdapter(adapter);

        Content content = new Content();
        content.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_item, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        // Get the search view and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); //Do not iconfy the widget; expand it by default

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase();
                ArrayList<ParseItem> newList = new ArrayList<>();
                for (ParseItem parseItem : parseItems) {
                    String title = parseItem.getTitle().toLowerCase();

                    // you can specify as many conditions as you like
                    if (title.contains(newText)) {
                        newList.add(parseItem);
                    }
                }
                // create method in adapter
                adapter.setFilter(newList);

                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        MenuItem ToMain2 = menu.findItem(R.id.main2);
        ToMain2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MainActivity2.class);
                MainActivity.this.startActivity(intent);
                return true;
            }
        });



        return true;

    }

    private class Content extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                List <String>img = new ArrayList<String>();
                List <String>til = new ArrayList<String>();
                List <String>url_lst = new ArrayList<String>();
                String url = "https://www.vscinemas.com.tw/vsweb/film/index.aspx";

                Document doc = Jsoup.connect(url).get();

                Elements data = doc.select("h2");
                Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
                for(int i =0;i<images.size();i++){
                    String ig=images.eq(i).attr("src");
                    if(ig.contains("upload/film")) {
                        Log.d("ig", "ig: " + "https://www.vscinemas.com.tw/vsweb" + ig.substring(2));
                        img.add("https://www.vscinemas.com.tw/vsweb" + ig.substring(2));
                    }
                }
                for(int i =0;i<data.size();i++){
                    String h=data.eq(i).select("a[href]").attr("href");
                    String t=data.eq(i).text();
                    Log.d("datailUrl", "datailUrl: "+"https://www.vscinemas.com.tw/vsweb/film/"+h);
                    Log.d("title", "title: "+t);
                    til.add(t);
                    url_lst.add("https://www.vscinemas.com.tw/vsweb/film/"+h);
                }
                for(int i=0;i<img.size();i++){
                    parseItems.add(new ParseItem(img.get(i),til.get(i),url_lst.get(i)));
                    Log.d("items", "img: " + img.get(i) + " . title: " + til.get(i)+" . detailurl:"+url_lst.get(i));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
