/**
 * MainActivity.java
 * A Yang
 * Ajay Vijayakumaran Nair
 * Nachiket Doke
 * 
 */
package com.example.inclass05;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String LOGGING_KEY = "inclass";
	private String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=65b290dd809944d086a040c8f3fe7f67&extras=url_m&per_page=20&format=rest&text=";
	private int currentPhotoIndex = 0;
	private List<String> urlList = new ArrayList<String>();
	private ProgressDialog progressDialogue;
	private boolean isChecked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(!isNetworkConnected()){
			Toast.makeText(this, "No internet connectivity", Toast.LENGTH_LONG).show();
			return;
		}
	}

	public void prevPhotoClicked(View view) {
		if(urlList.size() == 0){return;}
		if (currentPhotoIndex == 0) {
			currentPhotoIndex = urlList.size() - 1;
		} else {
			currentPhotoIndex--;
		}
		Log.d(LOGGING_KEY, currentPhotoIndex + "");
		new LoadPhotoTask().execute(urlList.get(currentPhotoIndex));
	}

	public void nextPhotoClicked(View view) {
		if(urlList.size() == 0){return;}
		if (currentPhotoIndex == urlList.size() - 1) {
			currentPhotoIndex = 0;
		} else {
			currentPhotoIndex++;
		}
		Log.d(LOGGING_KEY, currentPhotoIndex + "");
		new LoadPhotoTask().execute(urlList.get(currentPhotoIndex));
	}

	public void goBtnClicked(View view) {
		currentPhotoIndex = 0;
		String searchText = ((EditText) findViewById(R.id.editTextSearchQ)).getText().toString();
		this.isChecked = ((Switch) findViewById(R.id.switchParser)).isChecked();
		String searchUrl;
		try {
			searchUrl = this.url + URLEncoder.encode(searchText, "UTF-8");
			new FetchXMLTask().execute(searchUrl);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	private class FetchXMLTask extends AsyncTask<String, Integer, List<String>> {

		@Override
		protected void onPreExecute() {
			if(!isNetworkConnected()){
				Toast.makeText(MainActivity.this, "No internet connectivity", Toast.LENGTH_LONG).show();
				this.cancel(true);
				return;
			}
			progressDialogue = new ProgressDialog(MainActivity.this);
			progressDialogue.setTitle("Loading Photo");
			progressDialogue.setCancelable(false);
			progressDialogue.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(List<String> result) {
			Log.d(LOGGING_KEY, result.toString());
			// progressDialogue.dismiss();
			MainActivity.this.urlList = result;
			new LoadPhotoTask().execute(result.get(0));
			super.onPostExecute(result);
		}

		@Override
		protected List<String> doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.connect();
				int statusCode = connection.getResponseCode();
				if (statusCode == HttpURLConnection.HTTP_OK) {
					InputStream inStream = connection.getInputStream();
					if (isChecked) {
						Log.d(LOGGING_KEY, "Using SAX");
						urlList = new SAXPaserHelper().fetchUrlList(inStream);
					} else {
						Log.d(LOGGING_KEY, "Using Pull");
						urlList = new PullParserHelper().getUrlList(inStream);
					}
					return urlList;
				} else {
					Log.d(LOGGING_KEY, "Http status is not OK");
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}

	private class LoadPhotoTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			if(!isNetworkConnected()){
				Toast.makeText(MainActivity.this, "No internet connectivity", Toast.LENGTH_LONG).show();
				this.cancel(true);
				return;
			}
			if (!progressDialogue.isShowing()) {
				progressDialogue.show();
			}
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			ImageView imageView = (ImageView) findViewById(R.id.imageViewPhoto);
			imageView.setImageBitmap(result);
			progressDialogue.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String photoUrl = params[0];
			Bitmap bmp = null;
			URL url;
			try {
				url = new URL(photoUrl);
				bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return bmp;
		}

	}
}
