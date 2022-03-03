package com.avanade.b2cloginsocial.utils

import java.util.*

class B2CUtils {
    companion object{
        private val azureAdB2CHostName : String = "nextdevpoc.b2clogin.com"
        private val tenantName : String = "nextdevpoc"
        fun getAuthorityFromPolicyName(policyName : String) : String{
            //return "https://" + azureAdB2CHostName + "/" + tenantName + ".onmicrosoft.com/oauth2/v2.0/authorize?p=" + policyName
            return "https://nextdevpoc.b2clogin.com/tfp/nextdevpoc.onmicrosoft.com/${policyName}/"
        }

        fun getScopes() : List<String>{
            return Arrays.asList("https://nextdevpoc.onmicrosoft.com/nextapi/demo.read")
        }
    }
}