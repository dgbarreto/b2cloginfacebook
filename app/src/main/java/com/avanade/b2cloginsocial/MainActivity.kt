package com.avanade.b2cloginsocial

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.CookieSyncManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.avanade.b2cloginsocial.utils.B2CUtils
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalException

class MainActivity : AppCompatActivity() {
    private lateinit var bLoginFacebook: Button
    private lateinit var bLogout: Button
    private lateinit var bLogin: Button
    private lateinit var tvToken: TextView
    private lateinit var b2capp: IMultipleAccountPublicClientApplication
    private lateinit var account: IAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bLoginFacebook = findViewById(R.id.bLoginFacebook)
        bLogout = findViewById(R.id.bLogoutFacebook)
        bLogin = findViewById(R.id.bLogin)
        tvToken = findViewById(R.id.tvToken)

        bLoginFacebook.setOnClickListener { loginFacebook() }
        bLogout.setOnClickListener { logout() }
        bLogin.setOnClickListener { login() }

        tvToken.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                val clipManager: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("Token", tvToken.text)
                clipManager.setPrimaryClip(clipData)
                Toast.makeText(baseContext, "Token copiado para área de transferência!", Toast.LENGTH_LONG).show()
            }
        })

        PublicClientApplication.createMultipleAccountPublicClientApplication(baseContext, R.raw.auth_config_b2c,
            object: IPublicClientApplication.IMultipleAccountApplicationCreatedListener{
                override fun onCreated(application: IMultipleAccountPublicClientApplication?) {
                    b2capp = application!!
                }

                override fun onError(exception: MsalException?) {
                    println("Error")
                }
            })
    }

    private fun login(){
        val parameters = AcquireTokenParameters.Builder()
            .startAuthorizationFromActivity(this)
            .fromAuthority(B2CUtils.getAuthorityFromPolicyName("B2C_1_Main_login"))
            .withScopes(B2CUtils.getScopes())
            .withPrompt(Prompt.LOGIN)
            .withCallback(authInterativeCallback)
            .build()

        b2capp!!.acquireToken(parameters)
    }

    private fun loginFacebook(){
        val parameters = AcquireTokenParameters.Builder()
            .startAuthorizationFromActivity(this)
            .fromAuthority(B2CUtils.getAuthorityFromPolicyName("B2C_1_facebook_signup"))
            .withScopes(B2CUtils.getScopes())
            .withPrompt(Prompt.LOGIN)
            .withCallback(authInterativeCallback)
            .build()

        b2capp!!.acquireToken(parameters)
    }

    private fun logout(){
        if(account != null){
            b2capp!!.removeAccount(account, removeInterativeCallback)
        }
    }

    private val removeInterativeCallback: IMultipleAccountPublicClientApplication.RemoveAccountCallback
        private get() = object : IMultipleAccountPublicClientApplication.RemoveAccountCallback{
            override fun onRemoved() {
                Toast.makeText(baseContext, "Removed", Toast.LENGTH_LONG).show()
                val cookieSyncManager = CookieSyncManager.createInstance(baseContext)
                cookieSyncManager.startSync()
                val cookieManager = android.webkit.CookieManager.getInstance()
                cookieManager.removeAllCookies(null)
                cookieManager.removeSessionCookies(null)
                cookieManager.flush()
                cookieSyncManager.stopSync()
                cookieSyncManager.sync()
            }

            override fun onError(exception: MsalException) {
                Toast.makeText(baseContext, "Error to remove", Toast.LENGTH_LONG).show()
            }

        }

    private val authInterativeCallback: AuthenticationCallback
        private get() = object: AuthenticationCallback{
            override fun onSuccess(authenticationResult: IAuthenticationResult?) {
                Toast.makeText(baseContext, "Sucesso - ${authenticationResult!!.accessToken}", Toast.LENGTH_LONG).show()
                account = authenticationResult!!.account
                tvToken.setText(authenticationResult.accessToken)
            }

            override fun onError(exception: MsalException?) {
                Toast.makeText(baseContext, "Problema ao logar: ${exception.toString()}", Toast.LENGTH_LONG).show()
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

        }
}