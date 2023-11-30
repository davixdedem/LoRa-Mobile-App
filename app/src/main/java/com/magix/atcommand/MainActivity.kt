package com.magix.atcommand

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.concurrent.Executors
import android.Manifest
import android.app.Activity
import android.app.Application
import android.database.SQLException
import android.hardware.usb.UsbDeviceConnection
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.webkit.WebViewClient
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.hoho.android.usbserial.driver.UsbSerialDriver
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    /***************************** DEVICE CONFIG *****************************************/
    private lateinit var loraFreq: String
    private lateinit var loraPower: String
    private lateinit var loraSF: String
    private lateinit var loraBW: String
    private lateinit var loraCodeRate: String
    private lateinit var loraPreambleLength: String
    private lateinit var loraCRC: String
    private lateinit var loraIQInvert: String
    private lateinit var loraSaveToFlash: String
    private lateinit var loraReceiveTimeout: String
    private lateinit var atPrefix: String
    private lateinit var question: String
    /***************************** DEVICE CONFIG *****************************************/

    /***************************** BOOLEAN PARAMS *****************************************/
    private val defaultOne = 1
    private val defaultZero = 0
    private val loraModeOn = defaultOne.toString()
    private val loraModeOff = defaultZero.toString()
    private val sleepModeOn = defaultOne.toString()
    private val sleepModeOff = defaultZero.toString()
    private val reset = defaultOne.toString()
    private val copyrightOn = defaultOne.toString()
    private val copyrightOff = defaultZero.toString()
    private val defaultSet = defaultOne.toString()
    private val printModeOn = defaultOne.toString()
    private val printModeOff = defaultZero.toString()
    /***************************** BOOLEAN PARAMS *****************************************/

    /***************************** AT COMMANDS *****************************************/
    private lateinit var atCmdWakeup: ByteArray
    private lateinit var atCmdSetSleepModeOn: ByteArray
    private lateinit var atCmdSetSleepModeOff: ByteArray
    private lateinit var atCmdReset: ByteArray
    private lateinit var atCmdSetCopyrightOn: ByteArray
    private lateinit var atCmdSetCopyrightOff: ByteArray
    private lateinit var atCmdSetDefault: ByteArray
    private lateinit var atCmdGetChipId: ByteArray
    private lateinit var atCmdGetLoraMode: ByteArray
    private lateinit var atCmdSetLoraModeOn: ByteArray
    private lateinit var atCmdSetLoraModeOff: ByteArray
    private lateinit var atCmdSetLoraParams: ByteArray
    private lateinit var atCmdSetLoraReceiveTimeout: ByteArray
    private lateinit var atCmdGetPrintMode: ByteArray
    private lateinit var atCmdSetPrintModeOn: ByteArray
    private lateinit var atCmdSetPrintModeOff: ByteArray
    private lateinit var atCmdSendString: ByteArray
    private lateinit var commandNewLinesMap: Map<ByteArray, Int>
    /***************************** AT COMMANDS *****************************************/

    /***************************** OTHER VARS *****************************************/
    private lateinit var port: UsbSerialPort
    private val actionUsbPermission = "com.magix.atcommand.USB_PERMISSION"
    private var accumulatedData = ""
    private val channelID = "Notifiche Principali"
    private lateinit var userUUID : String
    private lateinit var myUUID: String
    private lateinit var lastTxMessage: String
    /***************************** OTHER VARS *****************************************/

    /*
     Variabili da utilizzare per conoscere lo status del dispositivo
     */
    private var needThreadReading = true
    private var isDeviceConfigured = false
    private var isThreadReadingRunning = false
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var webView: WebView
    private lateinit var currentPage: String
    private lateinit var driver: UsbSerialDriver
    private lateinit var connection: UsbDeviceConnection

    /*
     GPS
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var timeoutTimer: Timer? = null
    private var isAppInForeground = false

    /***************************** DATABASE *****************************************/
    private lateinit var db: SQLiteDatabase
    private val friendlyName = "LoraWan"
    private val expectedUUID = "LORAWAN0"
    /***************************** DATABASE *****************************************/

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        Classe per conoscere la situazione dell'app(aperta/chiusa ecc..)
         */
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                isAppInForeground = true
            }

            override fun onActivityPaused(activity: Activity) {
                isAppInForeground = false
            }

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })

        setContentView(R.layout.activity_main)

        /*
         GPS
         */
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /*
         WebView
         */
        runOnUiThread {
            webView = findViewById(R.id.webView)
            val settings = webView.settings
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.javaScriptEnabled = true
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            webView.isVerticalScrollBarEnabled = true
            val webAppInterface = WebAppInterface(this)
            webView.addJavascriptInterface(webAppInterface, "Android")
            webView.loadUrl("file:///android_asset/dark-skin/index.html")

        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                currentPage = url?.let { checkCurrentPage(it) }.toString()
                Log.d("Webview","L'utente si trova in $currentPage")
            }
        }

        /*
         Creo le tabelle se non esistono già
         */
        val dbHelper = DatabaseHelper(this)
        db = dbHelper.writableDatabase
        createTableConfigurations(db)
        createTableUsers(db)
        createTableMessage(db)
        createTableGroups(db)

        /*
         Creo il gruppo Multicast se non esiste
         */
        insertIntoGroupsTable(db, friendlyName, expectedUUID)

        /*
         Inserisco l'utente di utilizzo se non esiste già
         */
        userUUID = getDeviceUUID(this)
        myUUID = userUUID
        inserisciConfigurazione(db,"userUUID",userUUID,"UUID dell'utente")

        /*
         Assegnazione dei valori delle stringhe
         */
        loraFreq = getString(R.string.lora_freq)
        loraPower = getString(R.string.lora_power)
        loraSF = getString(R.string.lora_sf)
        loraBW = getString(R.string.lora_bw)
        loraCodeRate = getString(R.string.lora_code_rate)
        loraPreambleLength = getString(R.string.lora_preamble_length)
        loraCRC = getString(R.string.lora_crc)
        loraIQInvert = getString(R.string.lora_iq_invert)
        loraSaveToFlash = getString(R.string.lora_save_to_flash)
        loraReceiveTimeout = getString(R.string.lora_receive_timeout)
        atPrefix = getString(R.string.lora_prefix)
        question = getString(R.string.lora_question)
        atCmdWakeup = (atPrefix + "XXX").toByteArray()
        atCmdSetSleepModeOff = (atPrefix + "LPM=" + sleepModeOff).toByteArray()
        atCmdSetSleepModeOn = (atPrefix + "LPM=" + sleepModeOn).toByteArray()
        atCmdReset = (atPrefix + "RESET=" + reset).toByteArray()
        atCmdSetCopyrightOn = (atPrefix + "Copyright=" + copyrightOn).toByteArray()
        atCmdSetCopyrightOff = (atPrefix + "Copyright=" + copyrightOff).toByteArray()
        atCmdSetDefault = (atPrefix + "DefaultSet=" + defaultSet).toByteArray()
        atCmdGetChipId = (atPrefix + "ChipID=" + question).toByteArray()
        atCmdGetLoraMode = (atPrefix + "LORAWAN=" + question).toByteArray()
        atCmdSetLoraModeOn = (atPrefix + "LORAWAN=" + loraModeOn).toByteArray()
        atCmdSetLoraModeOff = (atPrefix + "LORAWAN=" + loraModeOff).toByteArray()
        atCmdSetLoraParams = (atPrefix +
                "LoraSet=$loraFreq,$loraPower,$loraSF,$loraBW,$loraCodeRate," +
                "$loraPreambleLength,$loraCRC,$loraIQInvert,$loraSaveToFlash").toByteArray()
        atCmdSetLoraReceiveTimeout = (atPrefix + "RX=" + loraReceiveTimeout).toByteArray()

        /*
         Query the current received data output mode:
         - Return value 0: string output;
         - Return value 1:hex output
         */
        atCmdGetPrintMode = (atPrefix + "PrintMode=" + question).toByteArray()

        /*
         * Set the device to hexadecimal output format, the data
         * received in RX mode will be printed in hexadecimal form
         */
        atCmdSetPrintModeOn = (atPrefix + "PrintMode=" + printModeOn).toByteArray()
        atCmdSetPrintModeOff = (atPrefix + "PrintMode=" + printModeOff).toByteArray()

        /*
         * Send the string".
         */
        atCmdSendString = (atPrefix + "SendStr=").toByteArray()

        /*
         * Popolo la mappa delle new lines di lettura
         */
        commandNewLinesMap = mapOf(
            atCmdWakeup to 1,
            atCmdSetSleepModeOn to 2,
            atCmdReset to 25,
            atCmdSetCopyrightOn to 1,
            atCmdSetCopyrightOff to 1,
            atCmdSetDefault to 25,
            atCmdGetChipId to 2,
            atCmdGetLoraMode to 1,
            atCmdSetLoraModeOff to 2,
            atCmdSetLoraModeOn to 25,
            atCmdSetLoraParams to 2,
            atCmdSetLoraReceiveTimeout to 1,
            atCmdGetPrintMode to 2,
            atCmdSetPrintModeOff to 2,
            atCmdSetPrintModeOn to 2
        )

        /*
         Rimuovo le notifiche ad applicazione aperta
         */
        removeNotifications()

    }

    /*
    Controlla in quale pagina si trova l'utente
     */
    private fun checkCurrentPage(url: String): String {
        return when {
            url.contains("index.html") -> "Homepage"
            url.contains("chat-1.html") -> "Chat"
            else -> "Unknown"
        }
    }

    override fun onResume() {
        super.onResume()
        removeNotifications()
    }

    /*
     Controlla i permessi di Geolocalizzazione
     */
     fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Richiede l'autorizzazione all'utente
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
    }

    /*
     Configurazione del dispositivo
     */
    private fun setDeviceConfig(port: UsbSerialPort): Boolean {
        val commands = listOf(
            atCmdWakeup,
            atCmdSetLoraParams,
            atCmdSetLoraModeOff,
            atCmdSetPrintModeOff,
            atCmdSetLoraReceiveTimeout
        )
        return sendListOfCommands(port, commands)
    }

    /*
     Configurazione del dispositivo in RxMode
     */
    private fun setRxMode(port: UsbSerialPort): Boolean {
        val commands = listOf(atCmdWakeup, atCmdSetLoraModeOff, atCmdSetLoraReceiveTimeout)
        return sendListOfCommands(port, commands)
    }

    /*
     Invia una lista di comandi AT
     */
    private fun sendListOfCommands(port: UsbSerialPort, commands: List<ByteArray>): Boolean {
        for (command in commands) {
            val newLines = commandNewLinesMap[command]
            if (newLines != null) {
                sendATCommand(port, command, newLines)
            } else {
                Log.d("USBManager", "Chiave $command non trovata nella mappa")
            }
        }
        return true
    }

    /*
     Invia un messaggio
     */
     fun sendMessage(message: String){
        sendATCommand(port,atCmdSendString,2,message)
    }

    /*
     Invio di un comando AT
     */
    private fun sendATCommand(
        port: UsbSerialPort,
        at: ByteArray,
        newLines: Int,
        message: String = ""
    ): Boolean {
        val responseBuilder = StringBuilder()
        val readThread = Thread {
            val buffer = ByteArray(1024)
            if (message == "") {
                port.write(at, 3000)
            } else {
                needThreadReading = false
                port.write((at + (message.toByteArray())), 1000)
            }
            port.read(buffer, 1000)
            var newLinesCount = 0
            while (newLinesCount < newLines) {
                val numBytesRead = port.read(buffer, 1000)
                if (numBytesRead > 0) {
                    val data = String(buffer.copyOf(numBytesRead), Charsets.UTF_8)
                    responseBuilder.append(data)
                    if (data.contains("\n")) {
                        newLinesCount++
                    }
                } else {
                    break
                }
            }
        }
        readThread.start()
        readThread.join()
        val response = responseBuilder.toString().trim()
        val lines = response.split("\n")
        val atString = String(at, Charsets.UTF_8)

        Log.d("USBManager", "Risposta al comando $atString:")
        Log.d("BUFFERING","lines: $lines")
        if (lines.size > 1) {
            for (i in lines.indices) {
                Log.d("USBManager", "Linea ${i + 1}: ${lines[i]}")
                if ("done" in lines[i]){
                    Log.d("BUFFERING","Messaggio inviato correttamente...")
                }
            }
        } else {
            Log.d("USBManager", "Linea 1: $response")
        }
        needThreadReading = true
        return true
    }

    /*
     OnStart
     */
    override fun onStart() {
        super.onStart()

        /*
         USBManager
         */
        if (!isDeviceConfigured) {
            val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
            val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
            for (entry in deviceList.entries) {
                val device: UsbDevice = entry.value
                val permissionIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent(actionUsbPermission),
                    PendingIntent.FLAG_IMMUTABLE
                )
                usbManager.requestPermission(device, permissionIntent)
            }

            /*
             Find all available drivers from attached devices.
             */
            val manager = getSystemService(USB_SERVICE) as UsbManager
            val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
            Log.d("Driver", "$availableDrivers")
            if (availableDrivers.isEmpty()) {
                Log.d("Driver", "$availableDrivers")
            }

            try {
                /*
                 Open a connection to the first available driver.
                 */
                driver = availableDrivers[0]
                connection = manager.openDevice(driver.device)
                    ?: // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
                    return
                // Rest of your code to handle the USB connection
                port = driver.ports[0]
                port.open(connection)
                port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            } catch (e: Exception) {
                // Handle exceptions here
                e.printStackTrace() // Or perform other error handling tasks
            }




            /*
             Configurazione del dispositivo
             */
            isDeviceConfigured = setDeviceConfig(port)
            if (isDeviceConfigured) {
                Log.d("USBManager", "Device configurato correttamente,popolo i dati sul db.")
                val mName = port.device.manufacturerName
                val mVendorId = port.device.vendorId
                val mDeviceId = port.device.deviceId
                val mProductId = port.device.productId
                val mProductName = port.device.productName
                val mSerialNumber = port.device.serialNumber
                val mVersion = port.device.version
                if (mName != null) {
                    inserisciConfigurazione(db, "mName", mName, "Device Manufacture Name")
                }
                inserisciConfigurazione(db, "mVendorId", mVendorId.toString(), "Device Vendor ID")
                inserisciConfigurazione(db, "mDeviceId", mDeviceId.toString(), "Device ID")
                inserisciConfigurazione(db, "mProductId", mProductId.toString(), "Device Product ID")
                if (mProductName != null) {
                    inserisciConfigurazione(db, "mProductName", mProductName, "Device Manufacture Name")
                }
                if (mSerialNumber != null) {
                    inserisciConfigurazione(db, "mSerialNumber", mSerialNumber, "Device Serial Number")
                }
                inserisciConfigurazione(db, "mVersion", mVersion, "Device Version name")
            }
        }

        executor.execute {
            /*
             Thread di lettura porta seriale
             */
            readSerial(port)
        }
    }

    /*
     Legge dalla porta seriale
     */
    private fun readSerial(port: UsbSerialPort) {
        val readThread = Thread {
            isThreadReadingRunning = true
            val buffer = ByteArray(4096)
            while (!Thread.interrupted()) {
                if (needThreadReading) {
                    try {
                        removeOverlayDeviceDisconnected()
                        val bytesRead = port.read(buffer, 1000)
                        if (bytesRead > 0) {
                            val data = buffer.copyOf(bytesRead)
                            accumulatedData += String(data, Charsets.UTF_8)
                            Log.d("Buffering", accumulatedData)
                            if ("Received String" in accumulatedData) {
                                if (countNewlines(accumulatedData) >= 2) {
                                    Log.d(
                                        "Buffering",
                                        "Messaggio ricevuto, accumulo i dati fino alla prossima new line..."
                                    )
                                    processCompleteData(false)
                                }
                            } else {
                                processCompleteData(false)
                            }
                        }
                    } catch (e: Exception) {
                        // Gestione dell'eccezione
                        isThreadReadingRunning = false
                        e.printStackTrace()
                        Thread.currentThread().interrupt() // Interrompe il thread in caso di eccezione
                        addOverlayDeviceDisconnected()
                    }
                } else {
                    Log.d("Buffering", "Thread fermato in questo momento...")
                }
            }
        }

        if (!isThreadReadingRunning) {
            Log.d("Buffering", "Avvio il Thread di lettura.")
            readThread.start()
        } else {
            Log.d("Buffering", "Thread di lettura già avviato.")
        }
    }

    /*
     Conteggio delle new lines
     */
    private fun countNewlines(text: String): Int {
        return text.count { it == '\n' }
    }

    /*
     Legge i dati completi fino alla newline
     */
    private fun processCompleteData(isMessage: Boolean) {
        val newlineIndex = accumulatedData.indexOf("\n")
        if (newlineIndex != -1) {
            val completeData = accumulatedData.substring(0, newlineIndex + 1)
            accumulatedData = accumulatedData.substring(newlineIndex + 1)
            if ("Received String" in completeData) {
                val lastColonIndex = completeData.lastIndexOf(":")
                if (lastColonIndex != -1 && lastColonIndex < completeData.length - 1) {
                    val secondPart = completeData.substring(lastColonIndex + 1).trim()
                    val contentMessage = verificaContentMessage(secondPart)
                    Log.d("USBManager", "Ricevuto un messaggio: $contentMessage")
                    val senderUUID = verificaUserUUID(secondPart)
                    val expectedReceiverUUID = estraiUltimoUUID(secondPart)
                    Log.d("BUFFERING", "Il mittente è: $senderUUID")
                    Log.d("BUFFERING", "Il destinatario è: $expectedReceiverUUID")
                    val isSenderExist = checkIfUserUUIDExists(db,senderUUID)
                    if (!isSenderExist && senderUUID != "false"){
                        Log.d("DBHandler","L'utente $senderUUID non esiste, lo aggiungo al db.")
                        inserisciUtente(db,senderUUID,senderUUID,contentMessage)
                    }
                    val messageMap = extractRxMessageDetails(accumulatedData)
                    if (messageMap != null && senderUUID != "false" && (expectedReceiverUUID == myUUID)) {
                        if (messageMap.isNotEmpty()) {
                            val rssi = messageMap["RSSI"] as? Int
                            val snr = messageMap["SNR"] as? Int
                            val size = messageMap["SIZE"] as? Int
                            if (rssi != null) {
                                if (snr != null) {
                                    if (size != null) {
                                        inserisciMessaggio(db,senderUUID,userUUID,contentMessage,rssi,snr,size)
                                        getLastMessagesMain()
                                        getLastMessagesMainUserChat(senderUUID)
                                        showNotification(title = senderUUID, content = contentMessage)
                                    }
                                }
                            }
                        }
                    }
                    else if (messageMap != null && senderUUID != "false" && (expectedReceiverUUID == "LORAWAN0")) {
                        if (messageMap.isNotEmpty()) {
                            val rssi = messageMap["RSSI"] as? Int
                            val snr = messageMap["SNR"] as? Int
                            val size = messageMap["SIZE"] as? Int
                            if (rssi != null) {
                                if (snr != null) {
                                    if (size != null) {
                                        Log.d("MessageHandler", "Ricevuto messaggio multicast: $senderUUID,$contentMessage,$rssi,$snr,$size")
                                        inserisciMessaggio(db,senderUUID,"LORAWAN0",contentMessage,rssi,snr,size, hasGroup = 1, idGroup = 1)
                                        getLastMessagesMain()
                                        getLastMessagesMainGroupChat()
                                        showNotification(title = senderUUID, content = contentMessage)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    completeData.trim()
                }
                accumulatedData = ""
                setRxMode(port)
            } else {
                Log.d("USBManager", "Dati completi letti dalla porta seriale: $completeData")
                if ("+TX done" in completeData){
                    //Log.d("DBHandler","Inserisco il messaggio $lastTxMessage inviato, sul db.")
                    //inserisciMessaggio(db,userUUID,"",lastTxMessage, seen = 1)
                    Log.d("Buffering","don't need it")
                }
                accumulatedData = ""
                setRxMode(port)
            }
        }
    }

    /*
     Estrai i valori RSSI,SNR e SIZE
     */
    private fun extractRxMessageDetails(stringa: String): Map<String, Int>? {
        val regex = Regex("""RSSI:(-?\d+), SNR:(-?\d+), Size:(\d+)""")
        val matchResult = regex.find(stringa)

        return if (matchResult != null) {
            val (rssi, snr, size) = matchResult.destructured
            mapOf("RSSI" to rssi.toInt(), "SNR" to snr.toInt(), "SIZE" to size.toInt())
        } else {
            null
        }
    }

    /*
     Creazione del canale di notifica (necessario solo per Android Oreo e versioni successive)
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelID,
            "Nome del Canale",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Descrizione del Canale"
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    /*
     Metodo per mostrare la notifica
     */
    private fun showNotification(title: String, content: String) {
        if (!isAppInForeground) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val notificationBuilder = NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // Imposta l'intenzione quando si preme la notifica
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(this)
            createNotificationChannel()

            val notificationId = 1
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    /*
     Creo la tabella 'message'
     */
    private fun createTableMessage(db: SQLiteDatabase) {
        val query = """
            CREATE TABLE IF NOT EXISTS messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                rssi INTEGER DEFAULT NULL,
                snr INTEGER DEFAULT NULL,
                size INTEGER DEFAULT NULL,
                senderUUID TEXT,
                receiverUUID TEXT,
                content TEXT,
                type TEXT DEFAULT "text",
                seen INT DEFAULT 0,
                sent INT DEFAULT 1,
                favourite INT DEFAULT 0,
                chatID INT,
                hasGroup INT DEFAULT 0,
                idGroup INT DEFAULT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (senderUUID) REFERENCES users(userUUID)
            );
        """.trimIndent()
        db.execSQL(query)
    }

    /*
    Creo la tabella 'groups'
     */
    private fun createTableGroups(db: SQLiteDatabase) {
        val query = """
            CREATE TABLE IF NOT EXISTS groups (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                friendlyName TEXT UNIQUE,
                expectedUUID TEXT UNIQUE,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()
        db.execSQL(query)
    }

    /*
    Funzione per inserire dati nella tabella 'groups' con parametri
     */
    private fun insertIntoGroupsTable(db: SQLiteDatabase, friendlyName: String, expectedUUID: String) {
        val values = ContentValues().apply {
            put("friendlyName", friendlyName)
            put("expectedUUID", expectedUUID)
        }

        val rowId = db.insert("groups", null, values)

        if (rowId != -1L) {
            println("Dati inseriti correttamente nella tabella 'groups'")
        } else {
            println("Errore nell'inserimento dei dati nella tabella 'groups'")
        }
    }

    /*
      Funzione per ottenere l'ultimo messaggio per ogni gruppo
      [
        {
          "id": 1,
          "groupName": "LoraWan",
          "expectedUUID": "LORAWAN0",
          "unseenCount": 2,
          "senderUUID": "hdj76458",
          "content": "Ciao come stai?",
          "timestamp": "2023-11-23 15:30:00",
          "seen": 1
        },
        {
          "id": 2,
          "groupName": "Friends",
          "expectedUUID": "LORAWAN1",
          "unseenCount": 0,
          "senderUUID": "hdj76455",
          "content": "Ciao come stai?",
          "timestamp": "2023-11-23 15:30:00",
          "seen": 0
        }
      ]
     */
    @SuppressLint("Range")
    fun getLastGroupMessages(db: SQLiteDatabase): String {
        val resultArray = JSONArray()

        // Controlla se esistono gruppi nella tabella "groups"
        val groupsQuery = "SELECT id, friendlyName, expectedUUID FROM groups"
        val groupsCursor = db.rawQuery(groupsQuery, null)

        while (groupsCursor.moveToNext()) {
            val groupId = groupsCursor.getInt(groupsCursor.getColumnIndex("id"))
            val groupName = groupsCursor.getString(groupsCursor.getColumnIndex("friendlyName"))
            val expectedUUID = groupsCursor.getString(groupsCursor.getColumnIndex("expectedUUID"))

            // Conta i messaggi non visti per ciascun gruppo
            val unseenCountQuery = "SELECT COUNT(*) AS unseenCount FROM messages WHERE hasGroup = 1 AND idGroup = $groupId AND seen = 0"
            val unseenCountCursor = db.rawQuery(unseenCountQuery, null)
            var unseenCount = 0

            if (unseenCountCursor.moveToFirst()) {
                unseenCount = unseenCountCursor.getInt(unseenCountCursor.getColumnIndex("unseenCount"))
            }

            unseenCountCursor.close()

            // Ottieni l'ultimo messaggio per ciascun gruppo
            val lastMessageQuery = "SELECT id, senderUUID, content, timestamp, seen FROM messages WHERE hasGroup = 1 AND idGroup = $groupId ORDER BY timestamp DESC LIMIT 1"
            val messagesCursor = db.rawQuery(lastMessageQuery, null)

            if (messagesCursor.moveToFirst()) {
                val messageId = messagesCursor.getInt(messagesCursor.getColumnIndex("id"))
                val senderUUID = messagesCursor.getString(messagesCursor.getColumnIndex("senderUUID"))
                val content = messagesCursor.getString(messagesCursor.getColumnIndex("content"))
                val timestamp = messagesCursor.getString(messagesCursor.getColumnIndex("timestamp"))
                val seen = messagesCursor.getInt(messagesCursor.getColumnIndex("seen"))

                // Costruisci l'oggetto JSON per ogni messaggio
                val messageObject = JSONObject().apply {
                    put("id", messageId)
                    put("groupName", groupName)
                    put("expectedUUID", expectedUUID)
                    put("unseenCount", unseenCount)
                    put("senderUUID", senderUUID)
                    put("content", content)
                    put("timestamp", timestamp)
                    put("seen", seen)
                    put("myUUID", myUUID)
                }
                resultArray.put(messageObject)
            }

            messagesCursor.close()
        }

        groupsCursor.close()
        Log.d("FINALLY",resultArray.toString())
        return resultArray.toString()
    }

    /*
    Controlla se i permessi delle notifiche sono abilitati
     */
    fun checkNotificationPermission(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }


    /*
    Richiedi i permessi delle notifiche
     */
    fun requestNotificationPermission(context: Context) {
        val notificationIntent = Intent()

        // Apri le impostazioni dell'applicazione per le notifiche
        when {
            true -> {
                notificationIntent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                notificationIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            true -> {
                notificationIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                notificationIntent.putExtra("app_package", context.packageName)
                notificationIntent.putExtra("app_uid", context.applicationInfo.uid)
            }
            else -> {
                // Versioni precedenti a Lollipop
                notificationIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                notificationIntent.addCategory(Intent.CATEGORY_DEFAULT)
                notificationIntent.data = Uri.parse("package:" + context.packageName)
            }
        }

        // Avvia l'activity delle impostazioni dell'applicazione per le notifiche
        context.startActivity(notificationIntent)
    }



    /*
     Creo la tabella 'users'
     */
    private fun createTableUsers(db: SQLiteDatabase) {
        val query = """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            userUUID TEXT UNIQUE,
            friendlyName TEXT DEFAULT NULL,
            profileImage TEXT DEFAULT NULL,
            lastMessage TIMESTAMP DEFAULT NULL,
            isAccepted INTEGER DEFAULT 0,
            isBlocked INTEGER DEFAULT 0,
            timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """.trimIndent()
        db.execSQL(query)
    }

    /*
     Creo la tabella 'configurations'
     */
    private fun createTableConfigurations(db: SQLiteDatabase) {
        val query = """
        CREATE TABLE IF NOT EXISTS configurations (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            configName TEXT,
            configValue TEXT,
            configDescription TEXT,
            timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """.trimIndent()
        db.execSQL(query)
    }

    /*
     Controlla se uno userUUID esiste
     */
    private fun checkIfUserUUIDExists(db: SQLiteDatabase, userUUID: String): Boolean {
        val query = "SELECT COUNT(*) FROM users WHERE userUUID = ?"
        val selectionArgs = arrayOf(userUUID)

        val cursor = db.rawQuery(query, selectionArgs)
        cursor.use {
            it.moveToFirst()
            val count = it.getInt(0)
            return count > 0
        }
    }

    /*
     Verifica l'esistenza dello userUUID all'interno del messaggio
     */
    private fun verificaUserUUID(stringa: String): String {
        val parts = stringa.split("-")
        if (parts.size >= 3 && isValidUserUUID(parts[0])) {
            return parts[0]
        }
        return "false"
    }

    /*
     Estrae l'UUID finale dal messaggio
     */
    private fun estraiUltimoUUID(stringa: String): String {
        val parts = stringa.split("-")
        if (parts.size >= 3 && isValidUserUUID(parts[2])) {
            return parts[2]
        }
        return "false"
    }

    /*
    Verifica il contenuto del messaggio
     */
    private fun verificaContentMessage(stringa: String): String {
        val parts = stringa.split("-")
        return parts[1]
    }

    /*
     Verifica la length dello userUUID
     */
    private fun isValidUserUUID(userUUID: String): Boolean {
        return userUUID.toByteArray().size == 8
    }

    /*
     Flagga il messaggio come visto
     */
     fun flagMessagesFromSenderAsSeen(db: SQLiteDatabase, senderUUID: String) {
        val contentValues = ContentValues().apply {
            put("seen", 1)
        }
        val whereClause = "senderUUID = ?"
        val whereArgs = arrayOf(senderUUID)
        val numRowsUpdated = db.update("messages", contentValues, whereClause, whereArgs)
        if (numRowsUpdated > 0) {
            Log.d("DBHandler", "Messaggi dal senderUUID $senderUUID segnati come visti con successo!")
        } else {
            Log.d("DBHandler", "Nessun messaggio trovato con il senderUUID specificato $senderUUID.")
        }
    }

    /*
     Inserire l'utente
     */
    private fun inserisciUtente(
        db: SQLiteDatabase,
        userUUID: String,
        friendlyName: String,
        lastMessage: String,
    ): Long {
        val contentValues = ContentValues().apply {
            put("userUUID", userUUID)
            put("friendlyName", friendlyName)
            put("lastMessage", lastMessage)
        }
        return db.insert("users", null, contentValues)
    }

    /*
     Inserire una configurazione
     */
    private fun inserisciConfigurazione(
        db: SQLiteDatabase,
        configName: String,
        configValue: String,
        configDescription: String,
    ): Long {
        val contentValues = ContentValues().apply {
            put("configName", configName)
            put("configValue", configValue)
            put("configDescription", configDescription)
        }
        Log.d("DbHandler", "Ho inserito la configurazione: $configName,$configValue,$configDescription")

        val existingConfigCursor = db.query(
            "configurations",
            arrayOf("configName"),
            "configName = ?",
            arrayOf(configName),
            null,
            null,
            null
        )
        val exists = existingConfigCursor.count > 0
        existingConfigCursor.close()
        return if (exists) {
            db.update("configurations", contentValues, "configName = ?", arrayOf(configName)).toLong()
        } else {
            db.insert("configurations", null, contentValues)
        }
    }

    /*
     Inserire un messaggio
     */
    fun inserisciMessaggio(
        db: SQLiteDatabase,
        senderUUID: String,
        receiverUUID: String,
        content: String,
        rssi: Int? = null,
        snr: Int? = null,
        size: Int? = null,
        sent: Int? = null,
        seen: Int? = null,
        hasGroup: Int? = 0,
        idGroup: Int? = null
    ): Long {
        val chatID = getChatID(db, senderUUID, receiverUUID)
        val contentValues = ContentValues().apply {
            rssi?.let { put("rssi", it) }
            snr?.let { put("snr", it) }
            size?.let { put("size", it) }
            put("senderUUID", senderUUID)
            put("receiverUUID", receiverUUID)
            put("content", content)
            put("chatID", chatID) // Inserisce il chatID ottenuto dalla funzione getChatID
            sent?.let { put("sent", it) }
            seen?.let { put("seen", it) } // Imposta il valore 'seen' direttamente
            hasGroup?.let{put("hasGroup", it)}
            idGroup?.let{put("idGroup",it)}
        }
        Log.d("DBHandler", "Sto inserendo il messaggio: $content sul db con rssi: $rssi, chatID: $chatID, senderUUID: $senderUUID, receiverUUID: $receiverUUID")
        return db.insert("messages", null, contentValues)
    }

    /*
     Ricava il chatID in base agli UUID
     */
    @SuppressLint("Range")
    fun getChatID(db: SQLiteDatabase, senderUUID: String, receiverUUID: String): Int {
        val query = "SELECT chatID FROM messages WHERE (senderUUID = ? AND receiverUUID = ?) OR (senderUUID = ? AND receiverUUID = ?)"
        val selectionArgs = arrayOf(senderUUID, receiverUUID, receiverUUID, senderUUID)
        val cursor = db.rawQuery(query, selectionArgs)
        var chatID = -1 // Valore di default se non viene trovato nessun chatID
        if (cursor != null && cursor.moveToFirst()) {
            chatID = cursor.getInt(cursor.getColumnIndex("chatID"))
        }
        cursor?.close()
        if (chatID == -1) {
            // Se non è stato trovato alcun chatID, genera un nuovo chatID + 1 rispetto al massimo valore attuale
            val maxQuery = "SELECT MAX(chatID) AS maxChatID FROM messages"
            val maxCursor = db.rawQuery(maxQuery, null)
            var maxChatID = 0

            if (maxCursor != null && maxCursor.moveToFirst()) {
                maxChatID = maxCursor.getInt(maxCursor.getColumnIndex("maxChatID"))
            }
            maxCursor?.close()
            chatID = maxChatID + 1
        }
        return chatID
    }

    /*
     Ricava l'UUID del dispositivo
     */
    @SuppressLint("HardwareIds")
    private fun getDeviceUUID(context: Context): String {
        val contentResolver: ContentResolver = context.contentResolver
        val androidId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        return shortHash(androidId)
    }

    /*
     Accorcia l'hash
     */
    private fun shortHash(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        val hexString = StringBuffer()

        for (i in 0 until 8) {
            val hex = Integer.toHexString(0xff and hashBytes[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex[0])
        }
        return hexString.toString()
    }

    /*
     Helper class per gestire il database SQLite
     */
    private class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "ChatDB", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        }
    }

    /*
      Torna un json del tipo:
    [
        {
            "myUUID": "12345",
            "chatID": 1,
            "senderUUID": "LORA",
            "content": "Ciao, come stai?",
            "timestamp": "2023-11-23 15:30:00",
            "seen": 1,
            "unseenCount": 2
        },
        {
            "myUUID": "12345",
            "chatID": 2,
            "senderUUID": "67890",
            "content": "Ehi! Tutto bene!",
            "timestamp": "2023-11-22 09:45:00",
            "seen": 0,
            "unseenCount": 0
        }
    ]
     */
    data class LastMessage(val chatID: Int, val remoteUUID: String, val content: String, val timestamp: String, val seen: Int, val unseencount: Int)
    @SuppressLint("Range")
    fun getLastMessages(db: SQLiteDatabase): String {
        val query = (
                "SELECT messages.chatID, messages.senderUUID, messages.receiverUUID, messages.content, messages.timestamp, messages.seen " +
                        "FROM messages " +
                        "INNER JOIN ( " +
                        "   SELECT chatID, MAX(timestamp) as max_timestamp " +
                        "   FROM messages " +
                        "   GROUP BY chatID " +
                        ") latest ON messages.chatID = latest.chatID AND messages.timestamp = latest.max_timestamp " +
                        "WHERE messages.hasGroup != 1 " +
                        "ORDER BY messages.timestamp DESC"
                )

        val cursor: Cursor = db.rawQuery(query, null)
        val jsonArray = mutableListOf<JSONObject>()

        while (cursor.moveToNext()) {
            val chatID = cursor.getInt(cursor.getColumnIndex("chatID"))
            val senderUUID = cursor.getString(cursor.getColumnIndex("senderUUID"))
            var remoteUUID = cursor.getString(cursor.getColumnIndex("receiverUUID"))
            val content = cursor.getString(cursor.getColumnIndex("content"))
            val timestamp = cursor.getString(cursor.getColumnIndex("timestamp"))
            val seen = cursor.getInt(cursor.getColumnIndex("seen"))

            if (remoteUUID == myUUID) {
                remoteUUID = senderUUID // Se receiverUUID è uguale a senderUUID, remoteUUID assume il valore di senderUUID
            }

            val jsonObject = JSONObject()
            jsonObject.put("chatID", chatID)
            jsonObject.put("remoteUUID", remoteUUID)
            jsonObject.put("content", content)
            jsonObject.put("timestamp", timestamp)
            jsonObject.put("seen", seen)
            jsonObject.put("myUUID", myUUID)

            val unseencountQuery = (
                    "SELECT COUNT(*) as unseencount FROM messages " +
                            "WHERE chatID = $chatID AND seen = 0"
                    )
            val unseencountCursor: Cursor = db.rawQuery(unseencountQuery, null)
            if (unseencountCursor.moveToFirst()) {
                val unseencount = unseencountCursor.getInt(unseencountCursor.getColumnIndex("unseencount"))
                jsonObject.put("unseenCount", unseencount)
            }
            unseencountCursor.close()

            jsonArray.add(jsonObject)
        }

        cursor.close()
        return JSONArray(jsonArray).toString()
    }

    /*
     * Torna un json del tipo:
     * [
     *   {
     *     "senderUUID": "user1",
     *     "receiverUUID": "user2",
     *     "content": "Good to see you",
     *     "timestamp": "2023-11-15 08:45:00",
     *     "favourite": 1
     *   },
     *   {
     *     "senderUUID": "user2",
     *     "receiverUUID": "user1",
     *     "content": "I'm fine, thank you!",
     *     "timestamp": "2023-11-15 08:40:00",
     *     "favourite": 1
     *   },
     *   {
     *     "senderUUID": "user1",
     *     "receiverUUID": "user2",
     *     "content": "How are you?",
     *     "timestamp": "2023-11-15 08:35:00",
     *     "favourite": 0
     *   }
     * ]
     */
    fun getMessagesForUser(db: SQLiteDatabase, userUUID: String): String {
        val jsonArray = JSONArray()

        val query = (
                "SELECT id, senderUUID, receiverUUID, content, timestamp, favourite FROM messages " +
                        "WHERE senderUUID = '$userUUID' OR receiverUUID = '$userUUID' " +
                        "ORDER BY timestamp DESC LIMIT 50"
                )

        val cursor: Cursor = db.rawQuery(query, null)

        cursor.moveToLast() // Spostati all'ultimo risultato del cursore

        while (!cursor.isBeforeFirst) {
            val messageId = cursor.getInt(0)
            val senderUUID = cursor.getString(1)
            val receiverUUID = cursor.getString(2)
            val content = cursor.getString(3)
            val timestamp = cursor.getString(4)
            val favourite = cursor.getInt(5)

            val jsonObject = JSONObject()
            jsonObject.put("id", messageId)
            jsonObject.put("senderUUID", senderUUID)
            jsonObject.put("receiverUUID", receiverUUID)
            jsonObject.put("content", content)
            jsonObject.put("timestamp", timestamp)
            jsonObject.put("favourite", favourite)
            jsonObject.put("myUUID", myUUID)
            jsonArray.put(jsonObject)
            cursor.moveToPrevious()
        }
        cursor.close()

        return jsonArray.toString()
    }

    /*
     Torna un json del tipo:
      [
        {
          "senderUUID": "user1",
          "receiverUUID": "user2",
          "content": "Good to see you",
          "timestamp": "2023-11-15 08:45:00",
          "favourite": 1
        },
        {
          "senderUUID": "user2",
          "receiverUUID": "user1",
          "content": "I'm fine, thank you!",
          "timestamp": "2023-11-15 08:40:00",
          "favourite": 1
        },
        {
          "senderUUID": "user1",
          "receiverUUID": "user2",
          "content": "How are you?",
         "timestamp": "2023-11-15 08:35:00",
          "favourite": 0
        }
     ]
     */
    fun getMessagesForGroup(db: SQLiteDatabase, groupID: Int): String {
        val jsonArray = JSONArray()

        val query = (
                "SELECT id, senderUUID, receiverUUID, content, timestamp, favourite FROM messages " +
                        "WHERE hasGroup = 1 AND IDGroup = $groupID " +
                        "ORDER BY timestamp DESC LIMIT 50"
                )

        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToLast()) {
            do {
                val messageId = cursor.getInt(0)
                val senderUUID = cursor.getString(1)
                val receiverUUID = cursor.getString(2)
                val content = cursor.getString(3)
                val timestamp = cursor.getString(4)
                val favourite = cursor.getInt(5)

                val jsonObject = JSONObject()
                jsonObject.put("id", messageId)
                jsonObject.put("senderUUID", senderUUID)
                jsonObject.put("receiverUUID", receiverUUID)
                jsonObject.put("content", content)
                jsonObject.put("timestamp", timestamp)
                jsonObject.put("favourite", favourite)
                jsonObject.put("myUUID", myUUID)
                jsonArray.put(jsonObject)
            } while (cursor.moveToPrevious())
        }
        cursor.close()
        return jsonArray.toString()
    }

    /*
     Flagga un messaggio nei preferiti
     */
    fun flagMessageAsFavourite(db: SQLiteDatabase, messageId: Int) {
        val query = "UPDATE messages SET favourite = 1 WHERE id = $messageId"
        db.execSQL(query)
    }

    /*
     Chiama la funzione JavaScript 'receiveBothJsonList'
     */
    fun sendBothJsonToJS(jsonString: String, jsonGroupString: String) {
        runOnUiThread {
            webView.evaluateJavascript("receiveBothJsonList('$jsonString','$jsonGroupString', '$myUUID');") {
            }
        }
    }

    /*
     Chiama la funzione JavaScript 'addOverlay'
     */
    fun addOverlayDeviceDisconnected() {
        runOnUiThread {
            webView.evaluateJavascript("addOverlayDeviceDisconnected();") {
            }
        }
    }

    /*
     Chiama la funzione JavaScript 'addOverlay'
     */
    fun removeOverlayDeviceDisconnected() {
        runOnUiThread {
            webView.evaluateJavascript("removeOverlayDeviceDisconnected();") {
            }
        }
    }

    /*
     Chiama la funzione JavaScript 'sendJsonToJSUserChat'
     */
    fun sendJsonToJSUserChat(jsonString: String, newMessage: Boolean = false) {
        if (currentPage == "Chat"){
            Log.d("DBhandler","Flaggo come visti i messaggi: $jsonString")
            val jsonArray = JSONArray(jsonString)
            if (jsonArray.length() > 0) {
                val firstObject = jsonArray.getJSONObject(0)
                val firstSenderUUID = firstObject.getString("senderUUID")
                flagMessagesFromSenderAsSeen(db, firstSenderUUID)
            } else {
                println("Array JSON vuoto")
            }
        }
        runOnUiThread {
            webView.evaluateJavascript("receiveJsonUserChat('$jsonString','$newMessage');") {
            }
        }
    }

    /*
     Chiama la funzione JavaScript 'sendJsonToJSUserChat'
     */
    fun sendJsonToJSGroupUserChat(jsonString: String,newMessage: Boolean = false) {
        if (currentPage == "Chat"){
            Log.d("DBhandler","Flaggo come visti i messaggi del gruppo: $jsonString")
            flagAsSeenAllGroupMessage(db)
        }
        runOnUiThread {
            webView.evaluateJavascript("receiveGroupJsonUserChat('$jsonString','$newMessage');") {
            }
        }
    }

    /*
    Flagga tutti i messaggi del gruppo come visti
     */
    private fun flagAsSeenAllGroupMessage(db: SQLiteDatabase) {
        val whereClause = "hasGroup = ?"
        val whereArgs = arrayOf("1")

        try {
            val numRowsUpdated = db.update(
                "messages",
                ContentValues().apply { put("seen", 1) },
                whereClause,
                whereArgs
            )

            if (numRowsUpdated > 0) {
                Log.d("DBHandler", "Messaggi con hasGroup = 1 segnati come visti con successo!")
            } else {
                Log.d("DBHandler", "Nessun messaggio trovato con hasGroup = 1.")
            }
        } catch (e: SQLException) {
            Log.d("DBHandler", "Errore durante l'aggiornamento dei messaggi: ${e.message}")
        }
    }

    /*
     Torna un dizionario con l'ultimo messaggio di ogni utente
     */
    fun getLastMessagesMain() {
        val messages = getLastMessages(db)
        val messagesGroup = getLastGroupMessages(db)
        Log.d("KotlinScript", messagesGroup)
        val jsonArray = JSONArray(messages)
        val jsonArrayGroup = JSONArray(messagesGroup)
        val jsonString = jsonArray.toString()
        val jsonStringGroup = jsonArrayGroup.toString()
        sendBothJsonToJS(jsonString,jsonStringGroup)
    }

    /*
     Torna un dizionario con la conversazione con uno specifico utente
     */
    fun getLastMessagesMainUserChat(userUUID: String,newMessage: Boolean = false) {
        val messages = getMessagesForUser(db,userUUID)
        Log.d("KotlinScript", messages)
        val jsonArray = JSONArray(messages)
        val jsonString = jsonArray.toString()
        sendJsonToJSUserChat(jsonString,newMessage)
    }

    /*
     Torna un dizionario con la conversazione con uno specifico utente
     */
    fun getLastMessagesMainGroupChat() {
        val messages = getMessagesForGroup(db,1)
        Log.d("KotlinScript", messages)
        val jsonArray = JSONArray(messages)
        val jsonString = jsonArray.toString()
        sendJsonToJSGroupUserChat(jsonString)
    }

    /*
     Binding del back button
     */
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    /*
    Inizializza il GPS
     */
    fun startGPS(timeoutInSeconds: Long = 30) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("GPS","Necessari i permessi GPS...")
            // Permission not granted, handle accordingly
            runOnUiThread {
                webView.evaluateJavascript("hideLoading();"){}
            }
            return
        }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Called when the location has changed
                // Handle the new location data here
                val latitude = String.format("%.4f", location.latitude)
                val longitude = String.format("%.4f", location.longitude)
                Log.d("GPS","$latitude,$longitude")
                val coordinates = "$latitude,$longitude"
                runOnUiThread {
                    webView.evaluateJavascript("hideLoading();"){}
                    webView.evaluateJavascript("populeInputText('$coordinates');"){}
                }
                resetTimeout()
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                // Called when the provider status changes (e.g., GPS enabled or disabled)
            }

            override fun onProviderEnabled(provider: String) {
                // Called when the provider (e.g., GPS) is enabled by the user
            }

            override fun onProviderDisabled(provider: String) {
                // Called when the provider (e.g., GPS) is disabled by the user
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000, // Minimum time interval between location updates in milliseconds
            10f, // Minimum distance between location updates in meters
            locationListener as LocationListener
        )
        startTimeoutTimer(timeoutInSeconds)
    }

    /*

     */
    private fun startTimeoutTimer(timeoutInSeconds: Long) {
        timeoutTimer = Timer()
        timeoutTimer?.schedule(object : TimerTask() {
            override fun run() {
                stopGPS()
            }
        }, timeoutInSeconds * 1000) // Convert seconds to milliseconds
    }

    private fun resetTimeout() {
        timeoutTimer?.cancel()
    }

    fun stopGPS() {
        resetTimeout()
        locationManager?.removeUpdates(locationListener as LocationListener)
    }

    private fun removeNotifications() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll() // Questo cancella tutte le notifiche emesse dall'app
        Log.d("Notifications","Sto cancellando le notifiche...")
    }
}

/*
  Classe utilizzata per il debug con console.log()
 */
class MyWebChromeClient : WebChromeClient() {
    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        consoleMessage?.let {
            Log.d("WebViewConsole", "${it.message()} -- From line ${it.lineNumber()} of ${it.sourceId()}")
        }
        return true
    }
}

/*
 Classe utilizzata per comunicare con JavaScript
 */
class WebAppInterface(private val mainActivity: MainActivity) {
    private val db: SQLiteDatabase = mainActivity.openOrCreateDatabase("ChatDB", Context.MODE_PRIVATE, null)

    @JavascriptInterface
    fun getBothLastMessagesJS() {
        val messages = mainActivity.getLastMessages(db)
        val messagesGroup = mainActivity.getLastGroupMessages(db)
        val jsonArray = JSONArray(messages)
        val jsonArrayGroup = JSONArray(messagesGroup)
        val jsonString = jsonArray.toString()
        val jsonStringGroup = jsonArrayGroup.toString()
        mainActivity.sendBothJsonToJS(jsonString,jsonStringGroup)
    }

    @JavascriptInterface
    fun getMessagesForUserJS(userUUID: String,newMessage: Boolean = false) {
        val messages = mainActivity.getMessagesForUser(db, userUUID)
        Log.d("KotlinScript", messages)
        val jsonArray = JSONArray(messages)
        val jsonString = jsonArray.toString()
        mainActivity.sendJsonToJSUserChat(jsonString,newMessage)
    }

    @JavascriptInterface
    fun getMessagesGroupForUserJS(newMessage: Boolean = false) {
        val messages = mainActivity.getMessagesForGroup(db,1)
        Log.d("KotlinScript", messages)
        val jsonArray = JSONArray(messages)
        val jsonString = jsonArray.toString()
        mainActivity.sendJsonToJSGroupUserChat(jsonString,newMessage)
    }

    @JavascriptInterface
    fun sendMessageJS(senderUUID: String, receiverUUID: String, message: String, isGroup: Boolean = false) {
        Log.d("KotlinScript", "$senderUUID, $receiverUUID, $message")
        if (!isGroup) {
            mainActivity.inserisciMessaggio(db, senderUUID, receiverUUID, message, seen = 1)
            getMessagesForUserJS(receiverUUID,true)
        }
        else{
            mainActivity.inserisciMessaggio(db, senderUUID, receiverUUID, message, seen = 1, idGroup = 1, hasGroup = 1)
            getMessagesGroupForUserJS(true)
        }

        CoroutineScope(Dispatchers.IO).launch {
            // Esegui mainActivity.sendMessage(message) in un thread non bloccante
            mainActivity.sendMessage("$senderUUID-$message-$receiverUUID")
        }
    }

    @JavascriptInterface
    fun flagMessageAsSeenJS(senderUUID: String){
        Log.d("KotlinScript","$senderUUID")
        mainActivity.flagMessagesFromSenderAsSeen(db,senderUUID)
    }

    @JavascriptInterface
    fun flagMessageAsFavouriteJS(messageID: String){
        Log.d("KotlinScript","$messageID")
    }

    @JavascriptInterface
    fun DeleteMessageJS(messageID: String){
        Log.d("KotlinScript","$messageID")
    }

    @JavascriptInterface
    fun getGPSCoordinateJS(){
        Log.d("KotlinScript","Cerco le coordinate GPS attuali...")
        mainActivity.checkLocationPermission()
        mainActivity.startGPS()
    }

}

/**
 Fast settings:
 AT+LoraSet=868000000,18,12,0,1,8,1,0,0
 */