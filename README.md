Sigi
====================

Description
---------------------
Sigi is an Android application framework for building native Android applications whilst using web technologies.


Coming soon:  how to build a full Sigi app!  Use the following to get started or check out these demos:

[http://github.com/voltron/whoopingkof/tree/master/clients/android/](http://github.com/voltron/whoopingkof/tree/master/clients/android/)


Creating a Sigi Application
---------------------

*NOTE*: The following assumes you know how to build a standard Android application using Eclipse or your favorite IDE or text editor.

Create a new <code>Activity</code> and change the class definition so it extends <code>com.sigi.Sigi</code>, not <code>Activity</code>.

Now, you need to add your extensions.


Creating a Sigi Extension
---------------------

### Create Interface ( IFooJS.java ) ###
Add methods that will only be exposed to window object in WebView.

<pre><code>
public interface IFooJS { 
	public String say(String word);
}
</code></pre>

### Create Class Implementation ( Foo.java ). ###  
Implement methods of interface and extend WebExtension abstract fbase class.

<pre><code>public class Foo extends WebViewExtension implements IFooJS{}</code></pre>

### Pass Activity's Context and WebView's Instance ###
Constructor of class must pass in the activity's context and the webview instance.

<pre><code>	
public Foo(Context context, WebView webView) {
	super(context, webView);
				
	// Add JS interface
	// Cast to IFooJS to only expose methods from the interface.
	webView.addJavascriptInterface((IFooJS)this, "Foo");
}
</code></pre>

### Implement Methods ###
Implement interface methods in class definition.

<pre><code>
public String say(String word){
	return word+ " from Java";
}
</code></pre>

### Add Extensions ###
In App.java (main app file), import your extension and create an arraylist of extensions and add your extension to the list inside the activity.

<pre><code>

import com.myapp.foo;

/* In activity */
private ArrayList<IWebViewExtension> extensions = new ArrayList<IWebViewExtension>();

/* ... deeper in the Activity ... */

extensions.add(new Foo(this, webView));

</code></pre>

### Call it in JavaScript ###
In your JavaScript file, call method like so:

<pre><code>
var text = window.Foo.say('Text from JavaScript as well as ');
alert(text); // alerts "Text from JavaScript as well as from Java"
</code></pre>

Authors
---------------------
- Joe McCann [http://github.com/joemccann](http://github.com/joemccann)
- David Wood [http://github.com/davidwood/](http://github.com/joemccann)