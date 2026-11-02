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
        imgBtn.type
