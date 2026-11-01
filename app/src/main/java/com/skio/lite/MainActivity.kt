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
    private lateinit var progress: ProgressBar
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

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(paper)
        }

        val row1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 8, 8, 4)
        }
        val back = Button(this).apply {
            text = "<"
            typeface = Typeface.MONOSPACE
            setOnClickListener { if (web.canGoBack()) web.goBack() }
        }
        bar = EditText(this).apply {
            hint = "adresă sau căutare"
            typeface = Typeface.MONOSPACE
            setTextColor(ink)
            setHintTextColor(Color.GRAY)
            setSingleLine()
            imeOptions = EditorInfo.IME_ACTION_GO
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
            setOnEditorActionListener { _, _, _ -> go(text.toString()); true }
        }
        val goBtn = Button(this).apply {
            text = "GO"
            typeface = Typeface.MONOSPACE
            setOnClickListener { go(bar.text.toString()) }
        }
        row1.addView(back)
        row1.addView(bar)
        row1.addView(goBtn)

        val row2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(8, 0, 8, 4)
        }
        val imgBtn = Button(this).apply {
            text = "IMG: OFF"
            typeface = Typeface.MONOSPACE
            setOnClickListener {
                imagesOn = !imagesOn
                web.settings.blockNetworkImages = !imagesOn
                text = if (imagesOn) "IMG: ON" else "IMG: OFF"
                web.reload()
            }
        }
        val jsBox = CheckBox(this).apply {
            text = "JS"
            typeface = Typeface.MONOSPACE
            setTextColor(ink)
            isChecked = false
            setOnCheckedChangeListener { _, on ->
                web.settings.javaScriptEnabled = on
                web.reload()
            }
        }
        val homeBtn = Button(this).apply {
            text = "Acasă"
            typeface = Typeface.MONOSPACE
            setOnClickListener { loadHome() }
        }
        row2.addView(imgBtn)
        row2.addView(jsBox)
        row2.addView(homeBtn)

        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progressTintList = android.content.res.ColorStateList.valueOf(accent)
            layoutParams = LinearLayout.LayoutParams(-1, 8)
        }

        web = WebView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, 0, 1f)
            settings.apply {
                javaScriptEnabled = false
                blockNetworkImages = true
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                loadsImagesAutomatically = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                useWideViewPort = true
                loadWithOverviewMode = true
                mediaPlaybackRequiresUserGesture = true
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    if (!url.startsWith("data:") && !url.startsWith("https://skio.lite")) {
                        bar.setText(url)
                    }
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, p: Int) {
                    progress.progress = p
                    progress.visibility = if (p in 1..99) View.VISIBLE else View.INVISIBLE
                }
            }
        }

        root.addView(row1)
        root.addView(row2)
        root.addView(progress)
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
