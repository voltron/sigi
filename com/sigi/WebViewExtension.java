package com.sigi;

import android.content.Intent;
import android.webkit.WebView;

public interface WebViewExtension {

	/**
	 * Handle application pause
	 */
	public void onPause();
	
	/**
	 * Handle application resume
	 */
	public void onResume();
	
	/**
	 * Register the Sigi instance and WebView with the extension
	 * 
	 * @param sigi		Sigi instance
	 * @param webView	WebView instance	
	 */
	public void register(Sigi sigi, WebView webView);
	
	/**
	 * Initialize the extension
	 */
	public void initialize();

	/**
	 * Intent complete callback
	 * 
	 * @param requestCode	Intent request code
	 * @param data			Intent instance
	 */
	public void intentComplete(int requestCode, Intent intent);
	
}
