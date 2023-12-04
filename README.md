# LoRa Mobile Chat

**LoRa**

![Logo](logo.png)

LoRa (Long Range) è una tecnologia basata sulla modulazione di frequenza a spettro espans derivata dalla tecnologia Chirp Spread Spectrum (CSS). Rappresenta la prima implementazione a basso costo dello spettro di diffusione chirp per uso commerciale.

**Descrizione**

Nel contesto di questo progetto, utilizziamo la scheda di sviluppo **CubeCell – AB01 Dev-Board** in modalità **AT Command** per consentire l'interazione con uno smartphone e avviare lo scambio di dati tramite frequenze radio. L'applicazione permette di avviare semplici conversazioni testuali per comunicare con altri utenti nel raggio LoRa. È importante prestare attenzione alle frequenze disponibili nel paese d'uso e configurare di conseguenza il dispositivo tramite l'apposito pannello di configurazione.

Uno degli obiettivi principali di questo progetto è semplificare l'utilizzo della tecnologia LoRa, rendendola accessibile a tutti e agevolando una comunicazione bidirezionale punto-punto. Per garantire un corretto funzionamento, è essenziale che la scheda contenga il seguente codice sorgente, scaricabile dal LINK fornito. Per questa operazione, è possibile utilizzare software come Arduino IDE o ESPTOOL.

Fino all'ultima versione ufficiale, le chat non godono di alcun livello di crittografia. Tuttavia, in futuro, si potrebbe valutare l'implementazione di una crittografia a chiave simmetrica, sempre nel rispetto dei limiti relativi alla lunghezza massima dei pacchetti dati LoRa.

La scheda è disponibile per l'acquisto da: [LINK].

Nella versione attuale, la scheda è collegata allo smartphone tramite USB: [FOTO].

**Pro**
- Distanza di utilizzo
- Basso costo

**Contro**
- Lunghezza massima dei dati di trasferimento

Riferimento al PDF dei comandi AT: [Manuale Utente Comandi AT](https://resource.heltec.cn/download/CubeCell/AT_Command_list/CubeCell_Series_AT_Command_User_Manual_V0.4.pdf)

![Logo del Progetto](hw1.png)

**Caratteristiche Principali**

- LoRa sfrutta bande di radiofrequenza sub-gigahertz come 433 MHz, 868 MHz (Europa) e 915 MHz (Nord America).
- Garantisce trasmissioni a lungo raggio (oltre 10 km in zone rurali, 3–5 km in zone fortemente urbanizzate) con basso consumo energetico.
- Si compone di due parti: LoRa, lo strato fisico, e LoRaWAN (Long Range Wide Area Network), gli strati superiori.
- I nuovi chipset LoRa presentano consumi energetici ridotti, maggiore potenza di trasmissione e dimensioni più compatte rispetto alle generazioni precedenti.
- Dispone di funzionalità di geolocalizzazione per triangolare le posizioni dei dispositivi tramite timestamp dai gateway.
- Consente la connettività a lungo raggio per dispositivi Internet of Things (IoT) in diversi settori.
