package com.higtek.testBle

import android.os.AsyncTask
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE

//class SoapHelper {
//}


/**
 * Created by samarin on 22-Nov-16.
 */
class SoapHelper {
    val GET_USERS_ACTION: String = "GetMhhtUsers"

    val OPERATION_NAME: String = "GetMhhtUsers"

    //public  final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
    val WSDL_TARGET_NAMESPACE: String = ""

    val SOAP_ADDRESS: String = "http://192.168.0.7:81/communication.asmx"

    fun GetUsers() {
    }

    fun Test() {
        //CommunitcationTask().execute()
    }

    fun Call(): String {
        val request = SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME)

        var pi = PropertyInfo()
        pi.setName("xml")
        pi.value = "test"
        pi.setType(Int::class.java)
        request.addProperty(pi)
        pi = PropertyInfo()
        pi.setName("login")
        pi.value = "hig"
        pi.setType(Int::class.java)
        request.addProperty(pi)
        pi = PropertyInfo()
        pi.setName("password")
        pi.value = "hig"
        pi.setType(Int::class.java)
        request.addProperty(pi)


        val envelope = SoapSerializationEnvelope(
            SoapEnvelope.VER11
        )
        envelope.dotNet = true

        envelope.setOutputSoapObject(request)

        val httpTransport = HttpTransportSE(SOAP_ADDRESS)
        var response: Any? = null

        try {
            httpTransport.call(GET_USERS_ACTION, envelope)
            response = envelope.response
        } catch (exception: Exception) {
            response = exception.toString()
        }
        return response.toString()
    }

/*
    private inner class CommunitcationTask : AsyncTask<Void?, Void?, Void?>() {
        protected override fun doInBackground(vararg params: Void): Void? {
            val s = Call()

            return null
        }
    }

 */
}
