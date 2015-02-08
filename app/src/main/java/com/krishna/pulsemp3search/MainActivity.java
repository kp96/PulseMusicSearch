package com.krishna.pulsemp3search;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Vector;

public class MainActivity extends Activity {
	String url = "http://emp3world.so/search/";
	String query = new String();
	ProgressDialog pgd;
	TextView requestAnother,tv,downloadSize,tvReference,tvListen;
	LinearLayout current;
	ImageView icon,icListen;
	Button onRequestAnother;
	Vector<String> titles;
	Vector<String> urls;
	Vector<String> sizes;
	Uri uri;
	String title;
	int againFlag;
	static int countLinks;
	GetSong currentSong;
	@Override
	protected void onCreate(Bundle savedInstanceState) {   
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button onSearch = (Button) findViewById(R.id.onSearch);
        final EditText search = (EditText) findViewById(R.id.search);
        requestAnother = (TextView) findViewById(R.id.requestAnother);
        onRequestAnother = (Button) findViewById(R.id.onRequestAnother);
        tv = (TextView) findViewById(R.id.downloadLink);
        tvReference = (TextView) findViewById(R.id.tvReference);
        downloadSize = (TextView) findViewById(R.id.downloadSize);
        tvListen = (TextView)findViewById(R.id.tvListen);
        icListen = (ImageView) findViewById(R.id.icListen);
        current = (LinearLayout) findViewById(R.id.layout);
        currentSong = new GetSong();
        icon = (ImageView) findViewById(R.id.icon);
        onSearch.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				countLinks = 0;
				titles = new Vector<String>();
				urls = new Vector<String>();
				sizes = new Vector<String>();
				query = search.getText().toString() + " mp3 download.html";
				query = query.replaceAll(" ", "_");
				query = url + query;
				new GetSong().execute();
				
			}
		});
        onRequestAnother.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myPostExecute();
			}
		});
	}
	void myPostExecute()
	{
		try{
		title = titles.get(countLinks);
		tv.setText(title);
		downloadSize.setText("Size : --- " + sizes.get(countLinks));
		try{
		String s = urls.get(countLinks);
		s = s.replaceAll(" ", "%20");
		uri = Uri.parse(s);
		}catch(Exception e)
		{
			Log.d("Caught inside uri", e.toString());
		}
		countLinks++;
		}catch(ArrayIndexOutOfBoundsException e){
			tv.setText("...Over...");
		}
		
		icon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Download Started", Toast.LENGTH_SHORT).show();;
				DownloadManager.Request songDownload = new DownloadManager.Request(uri);
				Log.d("Download link:" , uri.toString());
				songDownload.setTitle(title);
				songDownload.setDescription("Simple Mp3 Downloader...");
				songDownload.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				songDownload.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, (title+".mp3") );
				songDownload.allowScanningByMediaScanner();
				DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				dm.enqueue(songDownload);
			}
		});
		icListen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
		if(tv.getText().toString().equals("...Over..."))
		{
			if(countLinks != 0)
				tv.setText("End of results..try new query or buy pro version for more..");
			else
				tv.setText("Song not found in database, check your connectivity or try new query");
			icon.setVisibility(View.INVISIBLE);
			requestAnother.setVisibility(View.INVISIBLE);
			onRequestAnother.setVisibility(View.INVISIBLE);
			tvReference.setVisibility(View.INVISIBLE);
			downloadSize.setVisibility(View.INVISIBLE);
			tvListen.setVisibility(View.INVISIBLE);
			icListen.setVisibility(View.INVISIBLE);
		}
		else
		{
		icon.setVisibility(View.VISIBLE);
		requestAnother.setVisibility(View.VISIBLE);
		onRequestAnother.setVisibility(View.VISIBLE);
		tvReference.setVisibility(View.VISIBLE);
		tvListen.setVisibility(View.VISIBLE);
		icListen.setVisibility(View.VISIBLE);
		downloadSize.setVisibility(View.VISIBLE);
		}
	}
	private class GetSong extends AsyncTask<Void, Void, Void>
	{
		String url;
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if(againFlag == 0)
			{
			try{
				Document document = Jsoup.connect(query).get();
				Elements songNames = document.getElementsByClass("song_item");
				Elements songSizes = document.getElementsByClass("song_size");
				int j = 0;
				for(Element songSize: songSizes)
				{
					sizes.add(songSize.ownText());
				}
				for(Element song: songNames)
				{
					
					title = (song.getElementById("song_title").text());
					titles.add(title);					
					Elements links = song.getElementsByTag("a");
					int i = 0;
					for (Element link : links) {
						  ++i;
						  String linkHref = link.attr("href");
						  if(i == 2){
							  url =  linkHref;
							  urls.add(url);
						  }
						}
				}
				
			}catch(IOException e){
				e.printStackTrace();
			}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			pgd.dismiss();
			myPostExecute();
			}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			icon.setVisibility(View.INVISIBLE);
			requestAnother.setVisibility(View.INVISIBLE);
			onRequestAnother.setVisibility(View.INVISIBLE);
			pgd = new ProgressDialog(MainActivity.this);
			pgd.setTitle("Music Downloader");
			pgd.setMessage("Searching..");
			pgd.setIndeterminate(false);
			pgd.show();
		}
		
	}
    
}
