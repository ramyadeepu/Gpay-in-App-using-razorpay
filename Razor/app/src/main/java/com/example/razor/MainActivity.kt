package com.example.razor

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.PaymentMethodsCallback
import com.razorpay.PaymentResultListener
import com.razorpay.Razorpay
import com.razorpay.ValidationListener
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    var razorpay: Razorpay? = null
    var webView: WebView? = null
    var Btn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Btn = findViewById(R.id.btn)
        webView = findViewById(R.id.webview)

        Btn?.setOnClickListener {
            Upi()
        }
        razorpay = Razorpay(this)
        razorpay?.setWebView(webView)
        razorpay?.getPaymentMethods(object : PaymentMethodsCallback {
            override fun onPaymentMethodsReceived(result: String?) {

                Log.d("Result", "" + result)
            }

            override fun onError(error: String?) {
                if (error != null) {
                    Log.e("Get Payment error", error)
                }
            }
        })
    }


    fun Upi() {
        var payload = JSONObject("{currency:'INR'}")
        try {

            payload.put("amount", "111")
            payload.put("contact", "9999999999")
            payload.put("email", "customer@name.com")
            payload.put("method", "upi")
            payload.put("_[flow]", "intent")
            payload.put("upi_app_package_name", "com.google.android.apps.nbu.paisa.user")
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
        razorpay!!.validateFields(payload, object : ValidationListener {
            override fun onValidationSuccess() {
                webView?.visibility = View.VISIBLE
                Btn?.visibility = View.GONE
                try {
                     razorpay!!.submit(payload, object : PaymentResultListener
                     {
                        override fun onPaymentSuccess(razorpayPaymentId: String)
                        {
                            println("PaymentSuccess")
                            Btn?.visibility = View.VISIBLE
                            webView?.visibility = View.GONE
                            Toast.makeText(this@MainActivity, "payment succees", Toast.LENGTH_LONG).show()
                        }

                        override fun onPaymentError(code: Int, description: String)
                        {
                            println(code)
                            println("Description  " + description)
                            println("PaymentError")
                            Btn?.visibility = View.VISIBLE
                            webView?.visibility = View.GONE
                            Toast.makeText(this@MainActivity, "Payment error", Toast.LENGTH_LONG).show()

                        }
                    });
                } catch (e: Exception)
                {
                }
            }
            override fun onValidationError(error: Map<String, String>) {
                println("validation Error")

            }
        })

    }


}