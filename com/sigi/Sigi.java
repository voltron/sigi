package com.sigi;

import java.util.ArrayList;
import java.util.HashMap;

import com.whoopingkof.demo.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;

public class Sigi extends Activity {

	// --------------------------------------------------
	// Static Constants
	// --------------------------------------------------
	
	/**
	 * URL path separator
	 */
	private static final String PATH_SEPARATOR = "/";
	
	/**
	 * Prefix for local assets
	 */
	private static final String LOCAL_ROOT = "file:///android_asset/";
	
	// --------------------------------------------------
	// Private Fields
	// --------------------------------------------------
	
	/**
	 * List of IWebView extensions being used
	 */
	private ArrayList<WebViewExtension> extensions = new ArrayList<WebViewExtension>();
	
	/**
	 * Extension that is acting as menu delegate
	 */
	private MenuDelegate menuDelegate = null;
	
	private HashMap<Integer, WebViewExtension> intentCallbacks;
	
	// --------------------------------------------------
	// Protected Fields
	// --------------------------------------------------
	
	/**
	 * WebView instance
	 */
	protected WebView webView;

	/**
	 * Root file URL
	 */
	protected String rootFile;
	
	// --------------------------------------------------
	// Public Properties
	// --------------------------------------------------

	/**
	 * Expose the WebView instance
	 */
	public WebView getWebView()
	{
		return this.webView;
	}
	
	// --------------------------------------------------
	// Overridden Methods
	// --------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the intent callback dictionary
		intentCallbacks = new HashMap<Integer, WebViewExtension>();
		
		// Set the window properties
		Window window = this.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		
		// Add the WebView and set layout
		webView = new WebView(this);
		webView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		// Remove native scroll bars
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		
		// Configure WebView settings
		WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(false);
		// Enable geolocation
		// TODO: since we're doing this in Java, should this be false?
		webSettings.setGeolocationEnabled(true);

		// Enable DOM storage
		webSettings.setDatabasePath("/data/data/" + this.getClass().getPackage().getName() + "/databases");
		webSettings.setDatabaseEnabled(true);

		// Set the WebViewClient and WebChromeClient
		webView.setWebViewClient(new SigiWebViewClient());
		webView.setWebChromeClient(new SigiWebChromeClient(this));
		
		// Set the content view
		setContentView(webView);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPause() {
		for (WebViewExtension extension : extensions)
		{
			extension.onPause();
		}
		super.onPause();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume() {
		for (WebViewExtension extension : extensions)
		{
			extension.onResume();
		}
		super.onResume();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if (this.menuDelegate instanceof MenuDelegate)
		{
			return this.menuDelegate.willShowMenu();
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Overrides the default "back" button, which would exit the app, and
		// instead uses the browser history.
		if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
			this.webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.menu_home, menu);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.goto_main) {
			this.loadRoot();
		}
		return true;
	}
	
	// --------------------------------------------------
	// Public Methods
	// --------------------------------------------------
	
	/**
	 * Load a URL in the WebView
	 * 
	 * @param url	URL to load
	 */
	public void loadUrl(String url)
	{
		this.webView.loadUrl(url);
	}
	
	/**
	 * Load a local asset (from assets folder) in the WebView
	 * 
	 * @param url	Root relative URL to load
	 */
	public void loadFile(String url)
	{
		url = url.trim();
		if (url.startsWith(Sigi.PATH_SEPARATOR))
		{
			url = url.substring(Sigi.PATH_SEPARATOR.length());
		}
		if (!url.startsWith(LOCAL_ROOT))
		{
			url = LOCAL_ROOT + url;
		}
		this.loadUrl(url);
	}
	
	/**
	 * Load a local file and set as the root file
	 * 
	 * @param url	Root relative URL to load
	 */
	public void loadRoot(String url)
	{
		this.rootFile = url.trim();
		this.loadFile(url);
	}
	
	/**
	 * Load the defined root file
	 * 
	 */
	public void loadRoot()
	{
		if (!this.rootFile.equals(""))
		{
			this.loadFile(this.rootFile);
		}
	}
	
	/**
	 * Execute an intent
	 * 
	 * @param intent	Intent instance
	 */
	public void executeIntent(Intent intent)
	{
		this.startActivity(intent);
	}
	
	/**
	 * Execute an intent with a result
	 * 
	 * @param intent	Intent instance
	 * @param extension	Extension to be notified of Intent result
	 * @return	True if intent was started, false if not
	 */
	public Boolean executeIntent(WebViewExtension extension, Intent intent, int requestCode)
	{
		if (!this.intentCallbacks.containsKey(requestCode))
		{
			this.intentCallbacks.put(requestCode, extension);
			this.startActivityForResult(intent, requestCode);
			return true;
		}
		return false;
	}
	
	/**
	 * Get a path from a content URI
	 * 
	 * @param uri	URI
	 * @return	Path
	 */
	public String getPath(Uri uri) 
	{
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}	
	
	// --------------------------------------------------
	// Protected Methods
	// --------------------------------------------------
	
	/**
	 * Add an extension
	 * 
	 * @param extension	WebViewExtension instance
	 */
	protected void addExtension(WebViewExtension extension)
	{
		try
		{
			extension.register(this, this.webView);
			extension.initialize();
			this.extensions.add(extension);
			if (!(this.menuDelegate instanceof MenuDelegate) && extension instanceof MenuDelegate)
			{
				this.menuDelegate = (MenuDelegate)extension;
			}

		}
		catch (Exception ex)
		{
			Log.d("Sigi", "addExtension: " + ex.getMessage());
		}
	}
	
	/**
	 * Complete callback for startActivityForResult
	 * 
	 * @param requestCode	Intent request code
	 * @param resultCode	Intent result code
	 * @param data			Intent instance
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (this.intentCallbacks.containsKey(requestCode))
		{
			if (resultCode == RESULT_OK)
			{
				WebViewExtension extension = this.intentCallbacks.get(requestCode);
				extension.intentComplete(requestCode, data);
			}
			this.intentCallbacks.remove(requestCode);
		}
	}
	
	// --------------------------------------------------
	// Internal Classes
	// --------------------------------------------------
	
	/**
	 * Custom WebChromeClient that adds JavaScript alert and console.log
	 */
	private class SigiWebChromeClient extends WebChromeClient {
		
		// --------------------------------------------------
		// Private Fields
		// --------------------------------------------------
		
		/**
		 * Context instance
		 */
		private Context context;

		// --------------------------------------------------
		// Constructor
		// --------------------------------------------------
		
		/**
		 * Constructor
		 * 
		 * @param context	Context instance
		 */
		public SigiWebChromeClient(Context context) {
			this.context = context;
		}
		
		// --------------------------------------------------
		// Overridden Methods
		// --------------------------------------------------

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
			result.confirm();
			return true;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
			quotaUpdater.updateQuota(204801); // TODO: make quote value a constant
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			// http://developer.android.com/guide/developing/debug-tasks.html
			Log.d("WEB_CONSOLE", message + " -- From line " + lineNumber + " of " + sourceID);
		}
		
	}
	
	/**
	 * Custom WebViewClient that forces links to be loaded in WebView
	 */
	private class SigiWebViewClient extends WebViewClient {
	
		// --------------------------------------------------
		// Overridden Methods
		// --------------------------------------------------

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			view.loadUrl(url);
			return true;
		}
	}
	
}
