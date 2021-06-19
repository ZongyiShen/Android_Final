package ru.embersoft.parsesitedata;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleTExtView, detailTextView;
    private String detailString="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = findViewById(R.id.imageView);
        titleTExtView = findViewById(R.id.textView);
        detailTextView = findViewById(R.id.detailTextView);

        titleTExtView.setText(getIntent().getStringExtra("title"));
        Picasso.get().load(getIntent().getStringExtra("image")).into(imageView);
        Content content = new Content();
        content.execute();
    }

    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            detailTextView.setText(detailString);

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String detailUrl = getIntent().getStringExtra("detailUrl");

                String url = detailUrl;

                Document doc = Jsoup.connect(url).get();

                Elements data = doc.select("table").select("tr");
                for(int i=0;i<data.size(); i++){
                    Element data2=data.get(i);
                    Elements data3 = data2.select("td");
                    for(int j=0;j<data3.size();j++){
                        Element data4=data3.get(j);
                        if(i == 2 && j == 1){
                            if(data4.text().length() == 0){
                                detailString+="未分類\n";
                            }
                            else
                                detailString += data4.text() + "\n";
                        }
                        else {
                            detailString += data4.text() + "\n";
                        }
                    }
                    //detailString+=data2.toString();
                    Log.d("tr", "tr: "+data.size());
                }
                detailString+="劇情簡介 / ABOUT THE STORY:";
                Elements article = doc.select("div.bbsArticle").select("p");
                Log.d("article", "article: "+article);
                String sArticle = article.toString();
                sArticle = sArticle.replaceAll("<br>", "\n");
                sArticle = sArticle.replaceAll("<p>", "");
                sArticle = sArticle.replaceAll("</p>", "");
                detailString+=sArticle;
                //detailString = data.toString();


            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
