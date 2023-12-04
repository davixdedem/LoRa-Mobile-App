# LoRa Mobile Chat

**LoRa**

![Logo](logo.png) 

LoRa (Long Range) è una tecnologia basata sulla modulazione di frequenza a spettro espans derivata dalla tecnologia Chirp Spread Spectrum (CSS). Rappresenta la prima implementazione a basso costo dello spettro di diffusione chirp per uso commerciale.

## **Descrizione**
Questa è un'applicazione di **chat real-time** basata su LoRa che consente agli utenti di inviare e ricevere messaggi istantanei tra loro o in un'unica chat room. <br>

Nel contesto di questo progetto, utilizziamo la scheda di sviluppo **CubeCell – AB01 Dev-Board** in modalità **AT Command** per consentire l'interazione con uno smartphone e avviare lo scambio di dati tramite frequenze radio. L'applicazione permette di avviare semplici conversazioni testuali per comunicare con altri utenti nel raggio LoRa.<br>
Uno degli obiettivi principali di questo progetto è semplificare l'utilizzo della tecnologia LoRa, rendendola accessibile a tutti e agevolando una comunicazione bidirezionale punto-punto. Per garantire un corretto funzionamento, è essenziale che la scheda contenga il codice sorgente disponibile dagli esempi ufficiali forniti da Heltec Cube Cell. Per questa operazione, è possibile utilizzare software come Arduino IDE o il tool ufficiale rilasciato da Heltec Cubel Cell.
Nella versione attuale, la scheda è collegata allo smartphone tramite USB: [FOTO].

- ## Requisiti di Sistema

- **Android Versione**: 13 o successiva
- **Spazio di archiviazione**: Minimo 100 MB
- **Memoria RAM**: Consigliato 2 GB o superiore

- ## Preparazione del dispositivo<br>

**1 - Clonare il repository**<br>
``git clone https://github.com/davixdedem/LoRa-Mobile-App``<br>
**2 - Entrare nella directory *tools* del progetto**<br>
``cd Lora-Mobile-App/tools``<br>
**3 - Rendere eseguibile il tools *CubeCellflash***<br>
``chmod +x CubeCellflash``<br>
**4 - Flashing del file *at_command.hex***<br>
``./CubeCellflash -serial /dev/ttyUSB0 at_command.hex``<br>

- ## Configurazione dell'applicazione<br>

**1 - Scaricare l'applicazione dall'ultima release disponibile.**<br>
**2 - Collegare la Dev-Board allo Smartphone tramite cavo USB.**<br>
**3 - Configurare la frequenza disponibile nel proprio paese tramite il pannello di configurazione.**<br>
**3 - Inizia a chattare!**<br>


## **Pro & Contro**
| **Pro**                                       | **Contro**                                              |
|-----------------------------------------------|----------------------------------------------------------|
| Ampia copertura                               | Banda limitata                                           |
| Basso consumo energetico                       | Velocità di trasmissione limitata                         |
| Penetrazione di ostacoli                       | Interferenze                                             |
| Costi ridotti                                  | Sicurezza                                                |
| Versatilità delle applicazioni                 | Limitazioni dei pacchetti dati                            |


-## **Curiosità**
- LoRa sfrutta bande di radiofrequenza sub-gigahertz come 433 MHz, 868 MHz (Europa) e 915 MHz (Nord America).
- Garantisce trasmissioni a lungo raggio (oltre 10 km in zone rurali, 3–5 km in zone fortemente urbanizzate) con basso consumo energetico.
- Si compone di due parti: LoRa, lo strato fisico, e LoRaWAN (Long Range Wide Area Network), gli strati superiori.
- I nuovi chipset LoRa presentano consumi energetici ridotti, maggiore potenza di trasmissione e dimensioni più compatte rispetto alle generazioni precedenti.
- Dispone di funzionalità di geolocalizzazione per triangolare le posizioni dei dispositivi tramite timestamp dai gateway.
- Consente la connettività a lungo raggio per dispositivi Internet of Things (IoT) in diversi settori.
- Riferimento al PDF dei comandi AT: [Manuale Utente Comandi AT](https://resource.heltec.cn/download/CubeCell/AT_Command_list/CubeCell_Series_AT_Command_User_Manual_V0.4.pdf)

-## **Supporto**
Per eventuali domande, segnalazioni di bug o richieste di nuove funzionalità, ti invitiamo ad aprire una nuova issue nel nostro repository su GitHub. Cercheremo di rispondere nel più breve tempo possibile.<br>
Per domande più urgenti o altri problemi, puoi contattarci via email all'indirizzo davide.polli@dedem.it. Ti preghiamo di includere dettagli completi sul problema riscontrato per una risoluzione più rapida.<br>
Siamo impegnati a migliorare continuamente l'applicazione e apprezziamo ogni contributo e feedback dalla nostra comunità.<br>

