let global_myUUID = '';
let global_senderUUID = '';
let global_maxLength = 30;
let global_isGroup = '';
let dividerInserted = false;
let overlay = null;

const inputText = document.getElementById('messageInput');
const submitButton = document.querySelector('.send-icon');
const charCount = document.getElementById('charCount');
const dropDownButton = document.getElementById('dropDownButton');

// Funzione chiamata da Kotlin
function receiveJsonUserChat(jsonString, newMessage="false") {
    var jsonData = JSON.parse(jsonString);
    hideMessages();

    let needDivider = true;

    jsonData.forEach((message, index) => {
        insertMessagesIntoContainer(
            message.id,
            message.myUUID,
            message.senderUUID,
            message.receiverUUID,
            message.content,
            message.timestamp,
            message.favourite,
            `./../assets/media/avatar/${message.senderUUID}.png`,
            index === 0 ? needDivider : false
        );
    });
    if(newMessage != "false"){
        riduciOpacitaUltimoMessaggio();
    }
    scrollToChatEnd();
    bindMessagesCopyButtons();
    bindMessagesDeleteButtons();
}

// Funzione chiamata da Kotlin
function receiveGroupJsonUserChat(jsonString,newMessage = "false"){
    var jsonData = JSON.parse(jsonString);
    hideMessages();

    let needDivider = true;

    jsonData.forEach((message, index) => {
        insertMessagesIntoContainer(
            message.id,
            message.myUUID,
            message.senderUUID,
            message.receiverUUID,
            message.content,
            message.timestamp,
            message.favourite,
            `./../assets/media/avatar/${message.senderUUID}.png`,
            index === 0 ? needDivider : false,
            true
        );
    });
    if(newMessage != "false"){
        riduciOpacitaUltimoMessaggio();
    }
    scrollToChatEnd();
    bindMessagesCopyButtons();
    bindMessagesDeleteButtons();
}

// Funzione chiamata da Kotlin
function receiveContactsListJson(jsonString) {
    var jsonData = JSON.parse(jsonString);

    // Chiama la funzione per creare la lista dei contatti
    createContactList(jsonData);
}

// Aggiunge il divider
function addMessageDivider() {
    const divider = document.createElement('div');
    divider.classList.add('message-divider', 'sticky-top', 'pb-2');
    divider.setAttribute('data-label', 'Yesterday');

    const chatContent = document.getElementById('messageBody');
    const container = chatContent.querySelector('.container');

    const messageDay = document.createElement('div');
    messageDay.classList.add('message-day');
    messageDay.appendChild(divider);

    container.appendChild(messageDay);
}

// Genera il contenuto della chat
function insertMessagesIntoContainer(messageID, myUUID, senderUUID, receiverUUID, messageText, messageDate, favourite, avatarSrc, needDivider, isGroup=false) {
    const isSelf = myUUID === senderUUID;
    const messageClass = isSelf ? 'message self' : 'message';
    let messageContent = '';

/*    if (needDivider) {
        messageContent += `
            <div class="message-divider sticky-top pb-2" data-label="Yesterday">&nbsp;</div>
        `;
    }*/

    messageContent += `
        <!-- Received Message Start -->
        <div class="${messageClass}" data-id="${messageID}">
            <div class="message-wrapper">
                <div class="message-content">
                    ${isGroup && senderUUID !== myUUID ? `<h6 class="text-dark">${senderUUID}</h6>` : ''}
                    <span>${messageText}</span>
                </div>
            </div>
            <div class="message-options">
                <div class="avatar avatar-sm"><img alt="" src="./../assets/media/avatar/user.png"></div>
                <div class="contacts-info">
                </div>
                <span class="message-date">${messageDate}</span>
                <div class="dropdown">
                    <a class="text-muted" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <svg class="hw-18" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z"/>
                        </svg>
                    </a>
                    <div class="dropdown-menu">
                        <a class="dropdown-item d-flex align-items-center copy-button" href="#" data-message="${messageText}">
                            <!-- Default :: Inline SVG -->
                            <svg class="hw-18 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
                            </svg>

                            <!-- Alternate :: External File link -->
                            <!-- <img class="injectable hw-18 mr-2 copy-icon" src="./../assets/media/heroicons/outline/duplicate.svg" alt="message options"> -->
                            <span>Copy</span>
                        </a>
                        <a class="dropdown-item d-flex align-items-center favourite-button" href="#">
                            <!-- Default :: Inline SVG -->
                            <svg class="hw-18 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"/>
                            </svg>

                            <!-- Alternate :: External File link -->
                            <!-- <img class="injectable hw-18 mr-2" src="./../assets/media/heroicons/outline/star.svg" alt="message favourite"> -->
                            <span>Favourite</span>
                        </a>
                        <a class="dropdown-item d-flex align-items-center text-danger" href="#" data-id="${messageID}">
                            <!-- Default :: Inline SVG -->
                            <svg class="hw-18 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                            </svg>

                            <!-- Alternate :: External File link -->
                            <!-- <img class="injectable hw-18 mr-2" src="./../assets/media/heroicons/outline/trash.svg" alt="message delete"> -->
                            <span>Delete</span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <!-- Received Message End -->
    `;

    const container = document.getElementById('messageBody').querySelector('.container');
    const messageDayContainer = document.createElement('div');
    messageDayContainer.classList.add('message-day');
    messageDayContainer.innerHTML = messageContent;

    container.appendChild(messageDayContainer);

}

// Nasconde i messaggi attuali
function hideMessages() {
    const messages = document.querySelectorAll('.message-day');
    messages.forEach(message => {
        message.style.display = 'none';
    });
}

// Scroll fino alla fine della chat
function scrollToChatEnd() {
    document.querySelector('.chat-finished').scrollIntoView({
        block: 'end',
        behavior: 'auto'
    });
}

// Recupero myUUID a senderUUID
function set_myUUID_and_senderUUID() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);

    if (urlParams.has('myUUID')) {
        global_myUUID = urlParams.get('myUUID');
        console.log('myUUID recuperato dalla URL:', global_myUUID);

        if (urlParams.has('senderUUID')) {
            global_senderUUID = urlParams.get('senderUUID');
            console.log('SenderUUID recuperato dalla URL:', global_senderUUID);
        } else {
            console.log('Parametro senderUUID non presente nell\'URL');
        }
    } else {
        console.log('Parametro myUUID non presente nell\'URL');
    }
}

// Recupero isGroup
function set_isGroup() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);

    if (urlParams.has('isGroup')) {
        global_isGroup = urlParams.get('isGroup');
        console.log('isGroup recuperato dalla URL:', global_isGroup);
    } else {
        console.log('Parametro isGroup non presente nell\'URL');
    }
}

// Binding del pulsante di invio
submitButton.addEventListener('click', function(event) {
    const textValue = inputText.value.trim();
    if (textValue === '') {
        console.log('Il valore dell\'input è vuoto. Impossibile inviare il messaggio.');
        event.preventDefault();
    } else {
        console.log('Valore dell\'input:', textValue);
        console.log(global_isGroup)
        if (global_isGroup === "false"){
            simulateSending();
            Android.sendMessageJS(global_myUUID, global_senderUUID, textValue, false);
        }
        else{
            simulateSending();
            Android.sendMessageJS(global_myUUID, "LORAWAN0", textValue, true);
        }
        inputText.value = "";
        charCount.textContent = `0/${global_maxLength}`;
    }
});

// Gestisce il counter dell'input text e l'opacità del pulsante in base al conteggio dei caratteri
function bindingInputText() {
    const charCount = document.getElementById('charCount');

    inputText.addEventListener('input', function(event) {
        const pastedText = (event.inputType === 'insertFromPaste');
        const currentLength = inputText.value.length;

        charCount.textContent = `${currentLength}/${global_maxLength}`;
        if ((currentLength > 0 && currentLength <= global_maxLength || pastedText)) {
            submitButton.style.opacity = '1.0';
        } else {
            submitButton.style.opacity = '0.5';
        }
    });
}

// Popola l'InputText
function populeInputText(text) {
    inputText.value = text
    messageInput.style.background = "transparent";
    charCount.textContent = `${text.length}/${global_maxLength}`;
}

// Mostra il caricamento GPS
function showLoading(text) {
    const messageInput = document.getElementById('messageInput');

    // Imposta il testo e la classe per l'effetto di lampeggio
    messageInput.value = "Looking for GPS...";
    messageInput.style.color = "black";
    messageInput.style.background = "#3498db"; // Imposta lo sfondo blu
    messageInput.classList.add('blink-text');
    messageInput.disabled = true;
    submitButton.style.opacity = "0";
    dropDownButton.style.opacity = "0";
}

// Nasconde il caricamento GPS
function hideLoading() {
    const messageInput = document.getElementById('messageInput');

    // Rimuovi la classe di lampeggio e ripristina l'opacità normale
    messageInput.classList.remove('blink-text');
    messageInput.value = ""; // Rimuovi il testo
    messageInput.style.color = "white";
    messageInput.disabled = false; // Riattiva l'input dopo aver ottenuto le coordinate
    submitButton.style.opacity = "1";
    dropDownButton.style.opacity = "1";
}

// Aggiunge l'overlay che indica il dispositivo scollegato, viene chiamato da Kotlin
function addOverlayDeviceDisconnected() {
      // Crea un div per l'overlay
      const overlay = document.createElement('div');
      overlay.style.position = 'fixed';
      overlay.style.top = '0';
      overlay.style.left = '0';
      overlay.style.width = '100%';
      overlay.style.height = '100%';
      overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.5)'; // Imposta un colore di sfondo semi-trasparente
      overlay.style.zIndex = '9999'; // Assicura che l'overlay sia in primo piano
      overlay.style.display = 'flex';
      overlay.style.justifyContent = 'center';
      overlay.style.alignItems = 'center';

      // Aggiunge testo per indicare la mancanza di connessione
      const testo = document.createElement('p');
      testo.textContent = 'Dispositivo non connesso';
      testo.style.color = '#fff'; // Colore del testo
      testo.style.fontSize = '24px'; // Dimensione del testo
      overlay.appendChild(testo);

      // Aggiungi l'overlay al body del documento
      document.body.appendChild(overlay);
}

// Rimuove l'overlay che indica il dispositivo scollegato, viene chiamato da Kotlin
function removeOverlayDeviceDisconnected() {
    const overlay = document.querySelector('.overlay-dispositivo-non-connesso');
    if (overlay) {
        overlay.remove();
    }
}

// Mette in attesa l'input text fino a nuova disponibilità
function simulateSending() {
    var messageInput = document.getElementById('messageInput');
    var duration = 1500; // Durata desiderata in millisecondi (qui impostata a 1000ms in più rispetto al valore precedente)
    var refreshRate = 10; // Intervallo di aggiornamento della barra di caricamento in millisecondi
    var totalIterations = duration / refreshRate; // Calcolo del numero totale di iterazioni
    var increment = 100 / totalIterations; // Calcolo dell'incremento in base al numero di iterazioni

    var loadingProgress = 0;
    var loadingInterval = setInterval(function() {
        messageInput.style.backgroundImage = `linear-gradient(to right, #3498db ${loadingProgress}%, transparent ${loadingProgress}%)`;
        loadingProgress += increment; // Incremento basato sul numero di iterazioni
        messageInput.placeholder = 'Sending message...';

        if (loadingProgress >= 100) {
            clearInterval(loadingInterval);
            messageInput.style.backgroundImage = 'none';
            messageInput.placeholder = 'Type your message here...';
            ripristinaOpacitaUltimoMessaggio();
        }
    }, refreshRate); // Intervallo di aggiornamento della barra di caricamento
}


// Riduce l'opacità dell'ultimo messaggio
function riduciOpacitaUltimoMessaggio() {
  const messaggi = document.querySelectorAll('.message');
  charCount.style.opacity = "0";
  submitButton.style.opacity = "0";
  dropDownButton.style.opacity = "0";
  if (messaggi.length > 0) {
    const ultimoMessaggio = messaggi[messaggi.length - 1];
    ultimoMessaggio.style.opacity = '0.5';
  }
}

// Resetta l'opacità dell'ultimo messaggio
function ripristinaOpacitaUltimoMessaggio() {
  const messaggi = document.querySelectorAll('.message');
  charCount.style.opacity = "1.0";
  submitButton.style.opacity = "1.0";
  dropDownButton.style.opacity = "1.0";
  if (messaggi.length > 0) {
    const ultimoMessaggio = messaggi[messaggi.length - 1];
    ultimoMessaggio.style.opacity = '1';
    submitButton.style.opacity = '0.5';
  }
}

// Copia nella clipboard
function copyToClipboard(text) {
    const tempInput = document.createElement('input');
    tempInput.value = text;
    document.body.appendChild(tempInput);
    tempInput.select();
    document.execCommand('copy');
    document.body.removeChild(tempInput);
}

// Binding del pulsante per copiare il messaggio
function bindMessagesCopyButtons(){
    const copyIcons = document.querySelectorAll('.copy-button');

    copyIcons.forEach(function(copyIcon) {
        copyIcon.addEventListener('click', function(event) {
            const parentMessage = event.target.closest('.message');
            if (parentMessage) {
                const messageContent = parentMessage.querySelector('.message-content');
                if (messageContent) {
                    let contentToCopy = messageContent.textContent;
                    contentToCopy = contentToCopy.trim(); // Rimuovi gli spazi vuoti
                    copyToClipboard(contentToCopy, parentMessage);
                }
            }
        });
    });
}

// Binding del pulsante per eliminare un messaggio
function bindMessagesDeleteButtons(){
    const dropdownItems = document.querySelectorAll('.text-danger');
    dropdownItems.forEach(function(item) {
        item.addEventListener('click', function(event) {
            event.preventDefault(); // Evita l'azione predefinita del link

            const messageID = item.getAttribute('data-id');
            if (messageID) {
                console.log(messageID);
                if (global_isGroup == "false"){
                    Android.DeleteMessageJS(messageID,global_myUUID,false);
                }
                else{
                    Android.DeleteMessageJS(messageID,global_myUUID,true);
                }
            }
        });
    });
}

// Crea la lista dei contatti
function createContactList(jsonData) {
    const contactsList = document.getElementById('friendsTab');
    contactsList.innerHTML = ''; // Pulisce il contenuto precedente

    // Raggruppa gli utenti per la prima lettera del nome
    const groupedUsers = {};

    jsonData.forEach(user => {
        const firstLetter = user.friendlyName.charAt(0).toUpperCase();

        if (!groupedUsers[firstLetter]) {
            groupedUsers[firstLetter] = [];
        }

        groupedUsers[firstLetter].push(user);
    });

    // Ordina le chiavi in ordine alfabetico
    const sortedKeys = Object.keys(groupedUsers).sort();

    sortedKeys.forEach(letter => {
        const usersInSection = groupedUsers[letter];

        // Crea la sezione per la lettera
        const section = document.createElement('li');
        section.innerHTML = `<small class="font-weight-medium text-uppercase text-muted" data-letter="${letter}">${letter}</small>`;
        contactsList.appendChild(section);

        // Itera attraverso gli utenti nella sezione corrente
        usersInSection.forEach(user => {
            const listItem = document.createElement('li');
            listItem.classList.add('contacts-item', 'active');
            listItem.innerHTML = `
                <a class="contacts-link" href="#">
                    <div class="avatar">
                        <img src="./../assets/media/avatar/user.png" alt="">
                    </div>
                    <div class="contacts-content">
                        <div class="contacts-info">
                            <h6 class="chat-name text-truncate">${user.friendlyName}</h6>
                        </div>
                        <div class="contacts-texts">
                            <svg class="hw-16 text-muted mr-1" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd"/>
                            </svg>
                            <p class="text-muted mb-0">${user.lastMessage}</p>
                        </div>
                    </div>
                </a>
            `;

            // Aggiunge l'elemento alla sezione corrispondente
            section.appendChild(listItem);
        });
    });
}

// Nasconde gli elementi della rubrica
function hideContacts() {
    const elementsToHide = document.querySelectorAll('.contacts-item, .contacts-item.active, small.font-weight-medium.text-uppercase.text-muted');

    elementsToHide.forEach(element => {
        element.style.display = 'none';
    });
}


// Avviene quando la pagina è stata caricata
window.onload = function() {
    hideMessages();
    hideContacts();
    set_myUUID_and_senderUUID();
    set_isGroup();
    if (global_isGroup === "false"){
        Android.getMessagesForUserJS(global_senderUUID,false)
    }
    else{
        Android.getMessagesGroupForUserJS(false)
    }
    Android.flagMessageAsSeenJS(global_senderUUID)
    scrollToChatEnd();

    submitButton.style.opacity = '0.5';

    inputText.style.color = 'white';
    inputText.setAttribute('maxlength', global_maxLength);
    charCount.textContent = `0/${global_maxLength}`;

    const locationLink = document.getElementById('buttonLocation');
    locationLink.addEventListener('click', function(event) {
        event.preventDefault();
        console.log('Location link clicked!');
        showLoading();
        Android.getGPSCoordinateJS();
    });

}




