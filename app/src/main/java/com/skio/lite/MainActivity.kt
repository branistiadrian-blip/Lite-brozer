package com.skio.lite

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar

class MainActivity : Activity() {

    private lateinit var web: WebView
    private lateinit var bar: EditText
    private lateinit var progressBar: ProgressBar
    private var imagesOn = false

    private val paper = Color.parseColor("#F4EBD0")
    private val ink = Color.parseColor("#2B2B2B")
    private val accent = Color.parseColor("#8B3A1E")

    private val home = """
        <html><head><meta name="viewport" content="width=device-width,initial-scale=1">
        <style>
        body{font-family:monospace;background:#F4EBD0;color:#2B2B2B;padding:20px;max-width:600px;margin:auto}
        h1{border-bottom:3px double #8B3A1E;padding-bottom:8px}
        a{color:#8B3A1E;display:block;padding:8px 0;font-size:18px}
        </style></head><body>
        <h1>Skio Lite</h1>
        <p>Browser ușor pentru internet slab.</p>
        <a href="https://html.duckduckgo.com/html/">&gt; Căutare (DuckDuckGo)</a>
        <a href="https://ro.wikipedia.org">&gt; Wikipedia</a>
        <a href="https://text.npr.org">&gt; Știri text (NPR)</a>
        <a href="https://lite.cnn.com">&gt; CNN Lite</a>
        <a href="https://news.ycombinator.com">&gt; Hacker News</a>
        </body></html>
    """.trimIndent()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(paper)

        // Rândul 1: Înapoi | adresă | GO
        val row1 = LinearLayout(this)
        row1.orientation = LinearLayout.HORIZONTAL
        row1.gravity = Gravity.CENTER_VERTICAL
        row1.setPadding(8, 8, 8, 4)

        val backBtn = Button(this)
        backBtn.text = "<"
        backBtn.typeface = Typeface.MONOSPACE
        backBtn.setOnClickListener { if (web.canGoBack()) web.goBack() }

        bar = EditText(this)
        bar.hint = "adresă sau căutare"
        bar.typeface = Typeface.MONOSPACE
        bar.setTextColor(ink)
        bar.setHintTextColor(Color.GRAY)
        bar.setSingleLine()
        bar.imeOptions = EditorInfo.IME_ACTION_GO
        bar.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        bar.setOnEditorActionListener { _, _, _ ->
            go(bar.text.toString())
            true
        }

        val goBtn = Button(this)
        goBtn.text = "GO"
        goBtn.typeface = Typeface.MONOSPACE
        goBtn.setOnClickListener { go(bar.text.toString()) }

        row1.addView(backBtn)
        row1.addView(bar)
        row1.addView(goBtn)

        // Rândul 2: IMG | JS | Acasă
        val row2 = LinearLayout(this)
        row2.orientation = LinearLayout.HORIZONTAL
        row2.gravity = Gravity.CENTER_VERTICAL
        row2.setPadding(8, 0, 8, 4)

        val imgBtn = Button(this)
        imgBtn.text = "IMG: OFF"
        imgBtn.typeface = Typeface.MONOSPACE
        imgBtn.setOnClickListener {
            imagesOn = !imagesOn
            web.settings.blockNetworkImages = !imagesOn
            imgBtn.text = if (imagesOn) "IMG: ON" else "IMG: OFF"
            web.reload()
        }

        val jsBox = CheckBox(this)
        jsBox.text = "JS"
        jsBox.typeface = Typeface.MONOSPACE
        jsBox.setTextColor(ink)
        jsBox.isChecked = false
        jsBox.setOnCheckedChangeListener { _, on ->
            web.settings.javaScriptEnabled = on
            web.reload()
        }

        val homeBtn = Button(this)
        homeBtn.text = "Acasă"
        homeBtn.typeface = Typeface.MONOSPACE
        homeBtn.setOnClickListener { loadHome() }

        row2.addView(imgBtn)
        row2.addView(jsBox)
        row2.addView(homeBtn)

        // Bara de progres
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        progressBar.max = 100
        progressBar.progressTintList = android.content.res.ColorStateList.valueOf(accent)
        progressBar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 8
        )

        // WebView
        web = WebView(this)
        web.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        )

        val s = web.settings
        s.javaScriptEnabled = false
        s.blockNetworkImages = true
        s.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        s.setSupportZoom(true)
        s.builtInZoomControls = true
        s.displayZoomControls = false
        s.useWideViewPort = true
        s.loadWithOverviewMode = true
        s.mediaPlaybackRequiresUserGesture = true

        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (!url.startsWith("data:") && !url.startsWith("https://skio.lite")) {
                    bar.setText(url)
                }
            }
        }

        web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility =
                    if (newProgress in 1..99) View.VISIBLE else View.INVISIBLE
            }
        }

        root.addView(row1)
        root.addView(row2)
        root.addView(progressBar)
        root.addView(web)
        setContentView(root)

        loadHome()
    }

    private fun loadHome() {
        web.loadDataWithBaseURL("https://skio.lite/", home, "text/html", "utf-8", null)
        bar.setText("")
    }

    private fun go(input: String) {
        val t = input.trim()
        if (t.isEmpty()) return
        val url = when {
            t.startsWith("http://") || t.startsWith("https://") -> t
            t.contains(" ") || !t.contains(".") ->
                "https://html.duckduckgo.com/html/?q=" + java.net.URLEncoder.encode(t, "UTF-8")
            else -> "https://$t"
        }
        web.loadUrl(url)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
