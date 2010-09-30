package com.sigi;

import android.content.Intent;
import android.os.Handler;
import android.webkit.WebView;

public abstract class AbstractWebViewExtension implements WebViewExtension {
	
	// --------------------------------------------------
	// Protected Fields
	// --------------------------------------------------

	/**
	 * Sigi instance
	 */
	protected Sigi sigi;
	
	/**
	 * WebView instance
	 */
	protected WebView webView;
	
	/**
	 * Handler for calling JS
	 */
	protected Handler handler;
	
	// --------------------------------------------------
	// Constructor
	// --------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param context	Context instance
	 * @param webView	WebView instance to extend
	 */
	public AbstractWebViewExtension() {
	}
	
	// --------------------------------------------------
	// Public Methods
	// --------------------------------------------------

	/**
	 * Register the Sigi instance and WebView with the extension
	 * 
	 * @param sigi		Sigi instance
	 * @param webView	WebView instance	
	 */
	public void register(Sigi sigi, WebView webView)
	{
		// Store the Sigi activity and WebView
		this.sigi = sigi;
		this.webView = webView;
		// Initialize fields
		this.handler = new Handler();
		// Enable JavaScript in WebView
		webView.getSettings().setJavaScriptEnabled(true);
	}
	
	/**
	 * Execute a JavaScript command
	 * 
	 * @param command	JavaScript command to execute
	 */
	public void executeJS(String command) {
		final WebView webView = this.webView;
		final String url = "javascript:" + command.trim();
		this.handler.post(new Runnable() {
			public void run() {
				webView.loadUrl(url);
			}
		});
	}

	/**
	 * Intent complete callback
	 * 
	 * @param requestCode	Intent request code
	 * @param data			Intent instance
	 */
	public void intentComplete(int requestCode, Intent intent)
	{
	}
	
}
