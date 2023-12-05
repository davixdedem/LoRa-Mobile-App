# LoRa Mobile Chat

**LoRa**

![Logo](logo.png) 

LoRa (Long Range) è una tecnologia basata sulla modulazione di frequenza a spettro espans derivata dalla tecnologia Chirp Spread Spectrum (CSS). Rappresenta la prima implementazione a basso costo dello spettro di diffusione chirp per uso commerciale.

## **Descrizione**
Questa è un'applicazione di **chat real-time** basata su LoRa che consente agli utenti di inviare e ricevere messaggi istantanei tra loro o in un'unica chat room. <br>

Nel contesto di questo progetto, utilizziamo la scheda di sviluppo ***CubeCell – AB01 Dev-Board*** in modalità ***AT Command*** per consentire l'interazione con uno Smartphone e avviare lo scambio di dati tramite frequenze radio. L'applicazione permette di avviare semplici conversazioni testuali per comunicare con altri utenti nel raggio LoRa.<br>
Uno degli obiettivi principali di questo progetto è semplificare l'utilizzo della tecnologia LoRa, rendendola accessibile a tutti e agevolando una comunicazione bidirezionale punto-punto. Per garantire un corretto funzionamento, è essenziale che la scheda contenga il codice sorgente disponibile dagli esempi ufficiali forniti da Heltec Cube Cell. Per questa operazione, è possibile utilizzare software come Arduino IDE o il tool ufficiale rilasciato da Heltec Cubel Cell.

- ## Requisiti di Sistema

**Android Versione**: 13 o successiva<br>
**Spazio di archiviazione**: Minimo 16.6 MB<br>
**Memoria RAM**: Consigliato 2 GB o superiore<br>

- ## Dove acquistare *CubeCell – AB01 Dev-Board*
![HTTCAB01](httcab01.png)

[Heltec](https://heltec.org/project/htcc-ab01-v2/)<br>

[Amazon](https://www.amazon.it/LoRaWAN-sviluppo-ASR6501-energetico-Intelligent/dp/B07ZH7NL38/ref=sr_1_1?__mk_it_IT=%C3%85M%C3%85%C5%BD%C3%95%C3%91&crid=2E73JV8F1KPLV&keywords=heltec+cubecell&qid=1701754977&sprefix=heltec+cubecel%2Caps%2C148&sr=8-1)<br>

[Aliexpress](https://it.aliexpress.com/item/1005005444339915.html?spm=a2g0o.productlist.main.3.1d7150b2TFr0YZ&algo_pvid=b9b676a0-1f19-4aaf-807d-e712d7758b64&algo_exp_id=b9b676a0-1f19-4aaf-807d-e712d7758b64-1&pdp_npi=4%40dis%21EUR%2116.48%2116.48%21%21%2117.45%21%21%402103209b17017550135711815e8815%2112000033106113757%21sea%21IT%210%21AB&curPageLogUid=SzqEk2lL0gTd)<br>

- ## Cavi USB compatibili(in base al vostro Smartphone)
![TypeC/MicroUSB](cable.png) *USB Type-C/Micro USB*
![TypeC/MicroUSB](cable2.png) *USB Type-C/Micro USB*

- ## Preparazione del dispositivo<br>

[Istruzioni via Arduino Board Manager](https://docs.heltec.org/en/node/asr650x/htcc_ab01/quick_start.html#use-arduino-board-manager)<br>

[Istruzioni via Git](https://docs.heltec.org/en/node/asr650x/htcc_ab01/quick_start.html#via-git)<br>

[Istruzioni via File Locale](https://docs.heltec.org/en/node/asr650x/htcc_ab01/quick_start.html#via-local-file)<br>


## Configurazione dell'applicazione<br>
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


## **Curiosità**

- LoRa sfrutta bande di radiofrequenza sub-gigahertz come 433 MHz, 868 MHz (Europa) e 915 MHz (Nord America).
- Garantisce trasmissioni a lungo raggio (oltre 10 km in zone rurali, 3–5 km in zone fortemente urbanizzate) con basso consumo energetico.
- Si compone di due parti: LoRa, lo strato fisico, e LoRaWAN (Long Range Wide Area Network), gli strati superiori.
- I nuovi chipset LoRa presentano consumi energetici ridotti, maggiore potenza di trasmissione e dimensioni più compatte rispetto alle generazioni precedenti.
- Dispone di funzionalità di geolocalizzazione per triangolare le posizioni dei dispositivi tramite timestamp dai gateway.
- Consente la connettività a lungo raggio per dispositivi Internet of Things (IoT) in diversi settori.
- Riferimento al PDF dei comandi AT: [Manuale Utente Comandi AT](https://resource.heltec.cn/download/CubeCell/AT_Command_list/CubeCell_Series_AT_Command_User_Manual_V0.4.pdf)

## **Supporto**

Per eventuali domande, segnalazioni di bug o richieste di nuove funzionalità, ti invitiamo ad aprire una nuova issue nel nostro repository su GitHub. Cercheremo di rispondere nel più breve tempo possibile.<br>
Per domande più urgenti o altri problemi, puoi contattarci via email all'indirizzo davide.polli@dedem.it. Ti preghiamo di includere dettagli completi sul problema riscontrato per una risoluzione più rapida.<br>
Siamo impegnati a migliorare continuamente l'applicazione e apprezziamo ogni contributo e feedback dalla nostra community.<br>

