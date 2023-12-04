# LoRa Mobile Chat

**LoRa**

![Logo](logo.png) 

LoRa (Long Range) è una tecnologia basata sulla modulazione di frequenza a spettro espans derivata dalla tecnologia Chirp Spread Spectrum (CSS). Rappresenta la prima implementazione a basso costo dello spettro di diffusione chirp per uso commerciale.

## **Descrizione**
Questa è un'applicazione di **chat real-time** basata su LoRa che consente agli utenti di inviare e ricevere messaggi istantanei.

Nel contesto di questo progetto, utilizziamo la scheda di sviluppo **CubeCell – AB01 Dev-Board** in modalità **AT Command** per consentire l'interazione con uno smartphone e avviare lo scambio di dati tramite frequenze radio. L'applicazione permette di avviare semplici conversazioni testuali per comunicare con altri utenti nel raggio LoRa.<br>
Uno degli obiettivi principali di questo progetto è semplificare l'utilizzo della tecnologia LoRa, rendendola accessibile a tutti e agevolando una comunicazione bidirezionale punto-punto. Per garantire un corretto funzionamento, è essenziale che la scheda contenga il codice sorgente disponibile dagli esempi ufficiali forniti da Heltec Cube Cell. Per questa operazione, è possibile utilizzare software come Arduino IDE o ESPTOOL.
La scheda è disponibile per l'acquisto da: [LINK].
Nella versione attuale, la scheda è collegata allo smartphone tramite USB: [FOTO].

## **Funzionalità**
**Messaggi in tempo reale**: La chat supporta la comunicazione istantanea tra gli utenti con l'utilizzo di Socket.io per la trasmissione dei messaggi in tempo reale.<br>
**Connessione multipla**: Gli utenti possono connettersi contemporaneamente e scambiare messaggi tra loro in un'unica chat room.

## PREPARAZIONE DEL DISPOSITIVO ⚙️<bt>
**Tramite ARDUINO-IDE**<br>
**1 - Preparazione dell'ambiente Arduino**<br>
Il processo di flash del codice sulla scheda è abbastanza semplice e richiede pochi passaggi utilizzando l'IDE di Arduino, per supporto visitare: [Guida Ufficiale](https://docs.heltec.org/en/node/asr650x/htcc_am02/quick_start.html#use-arduino-board-manager) <br>
**2 - Impostare la modalità AT Command**<br>
Il nostro intento è quello di installare il codice sorgente dedicato alla predisposizione dei comandi AT,disponibile negli esempi. 
***Examples --> Lora --> AT_Command***

## PREPARAZIONE DEL DISPOSITIVO  ⚙️<br>
**Tramite ESPTOOL**<br>
<span style="color: red;">Testo colorato in blu</span>
**1 - Preparazione dell'ambiente Arduino**<br>
Il processo di flash del codice sulla scheda è abbastanza semplice e richiede pochi passaggi utilizzando l'IDE di Arduino, per supporto visitare: [Guida Ufficiale](https://docs.heltec.org/en/node/asr650x/htcc_am02/quick_start.html#use-arduino-board-manager) <br>
**2 - Impostare la modalità AT Command**<br>
Il nostro intento è quello di installare il codice sorgente dedicato alla predisposizione dei comandi AT,disponibile negli esempi. 
***Examples --> Lora --> AT_Command***
## **Pro & Contro**
| **Pro**                                       | **Contro**                                              |
|-----------------------------------------------|----------------------------------------------------------|
| Ampia copertura                               | Banda limitata                                           |
| Basso consumo energetico                       | Velocità di trasmissione limitata                         |
| Penetrazione di ostacoli                       | Interferenze                                             |
| Costi ridotti                                  | Sicurezza                                                |
| Versatilità delle applicazioni                 | Limitazioni dei pacchetti dati                            |

**Pro**
- Ampia copertura: LoRa offre una copertura estesa, con trasmissioni che possono raggiungere diverse decine di chilometri in aree rurali e diverse miglia in ambienti urbani densamente popolati.
- Basso consumo energetico: I dispositivi LoRa sono progettati per consumare poca energia, il che li rende ideali per applicazioni a batteria o alimentate da energia solare.
- Penetrazione di ostacoli: La tecnologia LoRa ha la capacità di penetrare attraverso ostacoli come pareti e edifici, migliorando la copertura in ambienti urbani e indoor.
- Costi ridotti: La tecnologia LoRa offre un'infrastruttura a basso costo sia per la creazione che per il mantenimento di reti IoT a lungo raggio.
- Versatilità delle applicazioni: È adatta per una vasta gamma di applicazioni IoT come monitoraggio ambientale, smart city, agricoltura intelligente, gestione delle risorse, e altro ancora.

**Contro**
- Banda limitata: Le bande di frequenza utilizzate da LoRa potrebbero essere limitate o regolamentate in alcune regioni, rendendo difficile l'implementazione su scala globale.
- Velocità di trasmissione limitata: Rispetto ad altre tecnologie wireless, LoRa offre velocità di trasmissione dati relativamente basse, adatte per messaggi brevi ma non per trasferimenti di dati ad alta velocità.
- Interferenze: In aree congestionate o con molti dispositivi LoRa operanti contemporaneamente, potrebbero verificarsi interferenze che compromettono la qualità della trasmissione.
- Sicurezza: Fino all'ultima versione ufficiale, LoRa non ha implementato di default livelli avanzati di crittografia, richiedendo ulteriori misure di sicurezza per proteggere i dati sensibili.
- Limitazioni dei pacchetti dati: La lunghezza massima dei pacchetti dati LoRa può essere limitata, richiedendo la segmentazione dei dati o limitando il tipo di informazioni trasmesse.

## **Curiosità**
- LoRa sfrutta bande di radiofrequenza sub-gigahertz come 433 MHz, 868 MHz (Europa) e 915 MHz (Nord America).
- Garantisce trasmissioni a lungo raggio (oltre 10 km in zone rurali, 3–5 km in zone fortemente urbanizzate) con basso consumo energetico.
- Si compone di due parti: LoRa, lo strato fisico, e LoRaWAN (Long Range Wide Area Network), gli strati superiori.
- I nuovi chipset LoRa presentano consumi energetici ridotti, maggiore potenza di trasmissione e dimensioni più compatte rispetto alle generazioni precedenti.
- Dispone di funzionalità di geolocalizzazione per triangolare le posizioni dei dispositivi tramite timestamp dai gateway.
- Consente la connettività a lungo raggio per dispositivi Internet of Things (IoT) in diversi settori.
- Riferimento al PDF dei comandi AT: [Manuale Utente Comandi AT](https://resource.heltec.cn/download/CubeCell/AT_Command_list/CubeCell_Series_AT_Command_User_Manual_V0.4.pdf)
