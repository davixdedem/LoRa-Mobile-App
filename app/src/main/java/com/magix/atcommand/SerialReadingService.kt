package com.magix.atcommand

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SerialReadingService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Esegui azioni specifiche in base all'intent ricevuto, se necessario
        // ...

        // Avvia la lettura seriale chiamando startReading
        startReading {
            // Logica per la lettura seriale
            // Assicurati di eseguire queste operazioni in un thread separato per non bloccare il thread principale
        }

        return START_STICKY // Questo fa sì che il servizio venga riavviato se termina inaspettatamente
    }

    // Rinominiamo uno dei metodi onBind per risolvere il conflitto
    override fun onBind(intent: Intent?): IBinder? {
        // Implementazione onBind con Intent opzionale
        return null
    }

    fun startReading(readSerialFunction: () -> Unit) {
        // Esegui altre operazioni del servizio se necessario

        // Chiamata alla funzione readSerial dal main
        readSerialFunction.invoke()
    }

}