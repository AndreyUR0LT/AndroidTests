package com.higtek.testBle

import android.os.AsyncTask
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.Base64
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

//class SoapHelper {
//}


data class MHHTUser(
    @JsonSetter(nulls = Nulls.SKIP)
    val Login: String = "",

    @JsonSetter(nulls = Nulls.SKIP)
    val FirstName: String = "",

    @JsonSetter(nulls = Nulls.SKIP)
    val LastName: String  = "",

    @JsonSetter(nulls = Nulls.SKIP)
    val PasswordHash: String = "",

    @JsonSetter(nulls = Nulls.SKIP)
    val IDTemplate: Int = 0,

    @JsonSetter(nulls = Nulls.SKIP)
    val PowerUser: Boolean = false,

    @JsonSetter(nulls = Nulls.SKIP)
    val IDLocation: Long = 0,

    @JsonSetter(nulls = Nulls.SKIP)
    val LocName: String = "",

    @JsonSetter(nulls = Nulls.SKIP)
    val ID: Long = 0,

    @JsonSetter(nulls = Nulls.SKIP)
    val IDCompany: Long = 0,

    @JsonSetter(nulls = Nulls.SKIP)
    val CompanyName: String = "",
)


//val ESP_SOAP_ADDRESS: String = "https://esp.higtek.com/TransportWebService.asmx"
val ESP_SOAP_ADDRESS: String = "http://192.168.1.120:83/TransportWebService.asmx"

val ESP_GET_MHHT_USERS_FOR_WORKSTATION_OPERATION_NAME: String = "GetMHHTUsersForWorkStation"
val ESP_GET_MHHT_USERS_FOR_WORKSTATION_ACTION: String = "/GetMHHTUsersForWorkStation"
val WSDL_TARGET_NAMESPACE: String = ""


public fun getMHHTUsersForWorkStation(url :String) : List<MHHTUser>?{

    val soapObject = SoapObject(WSDL_TARGET_NAMESPACE, ESP_GET_MHHT_USERS_FOR_WORKSTATION_OPERATION_NAME)

    soapObject.addProperty("erpCode", "")

    val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
    envelope.setOutputSoapObject(soapObject)
    envelope.dotNet = true

    val httpTransportSE = HttpTransportSE(url)

    var soapResponse = ""

    try {
        httpTransportSE.call(ESP_GET_MHHT_USERS_FOR_WORKSTATION_ACTION, envelope)
        val soapPrimitive = envelope.response
        soapResponse = soapPrimitive.toString()

        val decoded: ByteArray = Base64.getDecoder().decode(soapResponse)
        val decompressedXml : String = decompressBuffer(decoded)

        val xmlDeserializer = XmlMapper(JacksonXmlModule().apply {
            setDefaultUseWrapper(false)
        }).registerKotlinModule()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val genres = xmlDeserializer.readValue<List<MHHTUser>>(decompressedXml)

        return genres

    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

/*
    var res = result;



    //val encodedString: String = Base64.getEncoder().encodeToString(originalString.toByteArray())
    //val decodedString: String = String(Base64.getDecoder().decode(result))
    val decoded: ByteArray = Base64.getDecoder().decode(result)

    res = decompressBuffer(decoded)


    val xmlDeserializer = XmlMapper(JacksonXmlModule().apply {
        setDefaultUseWrapper(false)
    }).registerKotlinModule()
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    //var desObj = xmlDeserializer.readValue(res, MHHTUser[]::class.java)

    val genres = xmlDeserializer.readValue<List<MHHTUser>>(res)

    var hash0 = genres[0].PasswordHash

    var hash1 = calculateMd5Hash("123")

    if(hash0 == hash1){
        var i = 0
    }

    return result

 */
}



fun compressBuffer(data:ByteArray)
{
    var str0 = data.toString(Charsets.UTF_8)
    //var outputArray : ByteArray
    var byteArrayOutputStream = ByteArrayOutputStream()

    ZipOutputStream(BufferedOutputStream(byteArrayOutputStream)).use { output ->
        ByteArrayInputStream(data).use { input ->
            BufferedInputStream(input).use { origin ->
                val entry = ZipEntry("HIG")
                output.putNextEntry(entry)
                origin.copyTo(output, 1024 * 10)
            }
        }
    }

    var arr = byteArrayOutputStream.toByteArray()
    var arrS = byteArrayOutputStream.toString()
    var str = arr.toString(Charsets.UTF_8)

    var str2 = decompressBuffer(arr)
}

fun decompressBuffer(data: ByteArray) : String
{
    var zipInputStream = ZipInputStream(ByteArrayInputStream(data))
    var zipEntry = zipInputStream.nextEntry

    val buffer = ByteArray(8192)
    var len = zipInputStream.read(buffer)

    if(len <= 0)
        return ""

    //val bufferToOut = buffer.copyOfRange(0, len)

    var str = buffer.toString(Charsets.UTF_8).substring(0, len)

    return str
}


@OptIn(ExperimentalStdlibApi::class)
fun calculateMd5Hash(input:String) : String {

    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(input.toByteArray())
    return digest.toHexString().uppercase()
}
