package com.ajou.helpt.home.view.fragment

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentKakaoPayWebViewBinding
import com.ajou.helpt.home.view.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URISyntaxException

class KakaoPayWebViewFragment : Fragment() {
    private var _binding: FragmentKakaoPayWebViewBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private val dataStore = UserDataStore()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentKakaoPayWebViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: KakaoPayWebViewFragmentArgs by navArgs()
        val url = args.url
        binding.webview.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = false
            allowFileAccess = true

            setRenderPriority(WebSettings.RenderPriority.HIGH)
            cacheMode = WebSettings.LOAD_DEFAULT

            allowContentAccess = true
            loadsImagesAutomatically = true
            loadWithOverviewMode = true

            useWideViewPort = true

        }
        WebView.setWebContentsDebuggingEnabled(true)
        binding.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        binding.webview.setNetworkAvailable(true)
        binding.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.d("tokenbody url", url.toString())
                if (url!!.contains("success")) {
                    Log.d("url contains", "")
                    binding.webview.visibility = View.GONE
                    // TODO UI 꾸미기
                    val builder = AlertDialog.Builder(mContext)
                    builder.setTitle("결제 성공")
                    builder.setMessage("결제 성공하셨습니다.\n등록해주셔서 감사합니다.")
                    builder.setPositiveButton(
                        "확인",
                        DialogInterface.OnClickListener { dialog, which ->
                            val intent = Intent(mContext, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        })
                    builder.create()
                    builder.show()

                } else {
                    var intent: Intent? = null
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        Log.d("url", intent.toString())
                        val uri = Uri.parse(intent!!.dataString)
                        Log.d("url", uri.toString())
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                        return true
                    } catch (e: ActivityNotFoundException) {
                        Log.d("url e", e.message.toString())
                        val existPackage = intent?.`package`
                        val marketIntent = Intent(Intent.ACTION_VIEW);
                        marketIntent.data = Uri.parse("market://details?id=" + existPackage);
                        if (existPackage != null) {
                            startActivity(marketIntent)
                            return true
                        }
                    } catch (e: URISyntaxException) {
                        Log.d("url error", e.message.toString())
                        return false
                    }
                }
                return false
            }

        }
        binding.webview.webChromeClient = WebChromeClient()
//
        binding.webview.loadUrl(args.url!!)
    }
}