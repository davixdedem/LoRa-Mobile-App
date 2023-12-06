let overlay = null;
let global_myuuid = "";

// Viene chiamata da Kotlin
function receiveBothJsonList(jsonString, jsonStringGroup, myUUID) {
    flushChatList();
    global_myuuid = myUUID;
    document.getElementById("myUUID").textContent = global_myuuid;
    var jsonData = JSON.parse(jsonString);
    var jsonDataGroup = JSON.parse(jsonStringGroup);
    console.log(jsonString);
    for (var i = 0; i < jsonData.length; i++) {
        myUUID = jsonData[i].myUUID;
        var remoteUUID = jsonData[i].remoteUUID;
        var content = jsonData[i].content;
        var timestamp = jsonData[i].timestamp;
        var seen = jsonData[i].seen;
        var unseenCount = jsonData[i].unseenCount;
        createChatItem(myUUID, remoteUUID, remoteUUID, content, timestamp, seen, unseenCount);
    }
    if (jsonDataGroup.length === 0) {
        createGroupChatItem("LoRa Chat-Room", myUUID, "LORAWAN0", "LORAWAN0", "Send a message to everyone..", "", 0, 0, true);
    } else {
        for (var i = 0; i < jsonDataGroup.length; i++) {
            myUUID = jsonDataGroup[i].myUUID;
            var groupName = jsonDataGroup[i].groupName;
            var expectedUUID = jsonDataGroup[i].expectedUUID;
            var content = jsonDataGroup[i].content;
            var timestamp = jsonDataGroup[i].timestamp;
            var seen = jsonDataGroup[i].seen;
            var unseenCount = jsonDataGroup[i].unseenCount;
            var remoteUUID = jsonDataGroup[i].senderUUID;

            createGroupChatItem(groupName, myUUID, remoteUUID, remoteUUID, content, timestamp, seen, unseenCount);
        }
    }
}

// Funzione chiamata da Kotlin
function receiveContactsListJson(jsonString) {
    var jsonData = JSON.parse(jsonString);

    // Chiama la funzione per creare la lista dei contatti
    createContactList(jsonData);
}

// Funzione chiamata da Kotlin
function receiveConfigurationsListJson(jsonString) {
    // Chiama la funzione per creare la lista dei contatti
    populeConfigurationsList(jsonString);
}

function populeConfigurationsList(jsonData) {
    const data = JSON.parse(jsonData);

    const manufactureName = data.find(config => config.configName === "mName");
    const manufactureVendorID = data.find(config => config.configName === "mVendorId");
    const manufactureDeviceID = data.find(config => config.configName === "mDeviceId");
    const manufactureProductID = data.find(config => config.configName === "mProductId");
    const manufactureProductName = data.find(config => config.configName === "mProductName");
    const manufactureSerialNumber = data.find(config => config.configName === "mSerialNumber");
    const manufactureVersion = data.find(config => config.configName === "mVersion");

    const loraFreq = data.find(config => config.configName === "loraFreq");
    const loraPower = data.find(config => config.configName === "loraPower");
    const loraSF = data.find(config => config.configName === "loraSF");
    const loraBW = data.find(config => config.configName === "loraBW");
    const loraCodeRate = data.find(config => config.configName === "loraCodeRate");
    const loraPreambleLength = data.find(config => config.configName === "loraPreambleLength");
    const loraCRC = data.find(config => config.configName === "loraCRC");
    const loraSaveToFlash = data.find(config => config.configName === "loraSaveToFlash");
    const loraReceiveTimeout = data.find(config => config.configName === "loraReceiveTimeout");

    if (manufactureName) {
        const mNameElement = document.getElementById("mName");
        if (mNameElement) {
            mNameElement.textContent = manufactureName.configValue;
        }
    }

    if (manufactureVendorID) {
        const manufactureVendorIDElement = document.getElementById("mVendorId");
        if (manufactureVendorIDElement) {
            manufactureVendorIDElement.textContent = manufactureVendorID.configValue;
        }
    }

    if (manufactureDeviceID) {
        const manufactureDeviceIDElement = document.getElementById("mDeviceId");
        if (manufactureDeviceIDElement) {
            manufactureDeviceIDElement.textContent = manufactureDeviceID.configValue;
        }
    }

    if (manufactureProductID) {
        const manufactureProductIDElement = document.getElementById("mProductId");
        if (manufactureProductIDElement) {
            manufactureProductIDElement.textContent = manufactureProductID.configValue;
        }
    }

    if (manufactureProductName) {
        const manufactureProductNameElement = document.getElementById("mProductName");
        if (manufactureProductNameElement) {
            manufactureProductNameElement.textContent = manufactureProductName.configValue;
        }
    }

    if (manufactureSerialNumber) {
        const manufactureSerialNumberElement = document.getElementById("mSerialNumber");
        if (manufactureSerialNumberElement) {
            manufactureSerialNumberElement.textContent = manufactureSerialNumber.configValue;
        }
    }

    if (manufactureVersion) {
        const manufactureVersionElement = document.getElementById("mVersion");
        if (manufactureVersionElement) {
            manufactureVersionElement.textContent = manufactureVersion.configValue;
        }
    }

    if (loraFreq) {
        const loraFreqElement = document.getElementById("loraFreq");
        if (loraFreqElement) {
            loraFreqElement.textContent = loraFreq.configValue;
        }
    }

    if (loraPower) {
        const loraPowerElement = document.getElementById("loraPower");
        if (loraPowerElement) {
            loraPowerElement.textContent = loraPower.configValue;
        }
    }

    if (loraSF) {
        const loraSFElement = document.getElementById("loraSF");
        if (loraSFElement) {
            loraSFElement.textContent = loraSF.configValue;
        }
    }

    if (loraBW) {
        const loraBWElement = document.getElementById("loraBW");
        if (loraBWElement) {
            loraBWElement.textContent = loraBW.configValue;
        }
    }

    if (loraCodeRate) {
        const loraCodeRateElement = document.getElementById("loraCodeRate");
        if (loraCodeRateElement) {
            loraCodeRateElement.textContent = loraCodeRate.configValue;
        }
    }

    if (loraPreambleLength) {
        const loraPreambleLengthElement = document.getElementById("loraPreambleLength");
        if (loraPreambleLengthElement) {
            loraPreambleLengthElement.textContent = loraPreambleLength.configValue;
        }
    }

    if (loraCRC) {
        const loraCRCElement = document.getElementById("loraCRC");
        if (loraCRCElement) {
            loraCRCElement.textContent = loraCRC.configValue;
        }
    }

    if (loraSaveToFlash) {
        const loraSaveToFlashElement = document.getElementById("loraSaveToFlash");
        if (loraSaveToFlashElement) {
            loraSaveToFlashElement.textContent = loraSaveToFlash.configValue;
        }
    }

    if (loraReceiveTimeout) {
        const loraReceiveTimeoutElement = document.getElementById("loraReceiveTimeout");
        if (loraReceiveTimeoutElement) {
            loraReceiveTimeoutElement.textContent = loraReceiveTimeout.configValue;
        }
    }

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

            const link = document.createElement('a');
            link.classList.add('contacts-link');
            link.href = `./chat-1.html?senderUUID=${encodeURIComponent(user.friendlyName)}&myUUID=${encodeURIComponent(global_myuuid)}&isGroup=false`;

            const avatar = document.createElement('div');
            avatar.classList.add('avatar');
            const img = document.createElement('img');
            img.src = `./../assets/media/avatar/user.png`;
            img.alt = '';
            avatar.appendChild(img);

            const contactsContent = document.createElement('div');
            contactsContent.classList.add('contacts-content');

            const contactsInfo = document.createElement('div');
            contactsInfo.classList.add('contacts-info');

            const chatName = document.createElement('h6');
            chatName.classList.add('chat-name', 'text-truncate');
            chatName.textContent = user.friendlyName;

            const contactsTexts = document.createElement('div');
            contactsTexts.classList.add('contacts-texts');

            const svg = document.createElement('svg');
            svg.classList.add('hw-16', 'text-muted', 'mr-1');
            svg.setAttribute('viewBox', '0 0 20 20');
            svg.setAttribute('fill', 'currentColor');

            const path = document.createElement('path');
            path.setAttribute('fill-rule', 'evenodd');
            path.setAttribute('d', 'M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z');
            path.setAttribute('clip-rule', 'evenodd');

            svg.appendChild(path);

            const text = document.createElement('p');
            text.classList.add('text-muted', 'mb-0');
            text.textContent = user.lastMessage;

            //contactsTexts.appendChild(svg);
            //contactsTexts.appendChild(text);

            contactsInfo.appendChild(chatName);
            contactsContent.appendChild(contactsInfo);
            contactsContent.appendChild(contactsTexts);

            link.appendChild(avatar);
            link.appendChild(contactsContent);

            listItem.appendChild(link);

            // Aggiunge l'elemento alla sezione corrispondente
            section.appendChild(listItem);
        });
    });
}


// Flushing della chat-view
function flushChatList(){
    var contactsList = document.getElementById("chatContactTab");
    contactsList.innerHTML = '';
    }

// Crea la chat per singoli utenti
function createChatItem(myUUID, senderUUID, name, lastMessage, timestamp, seen, unseenCount) {
    var contactsList = document.getElementById("chatContactTab");

    var listItem = document.createElement("li");
    if (seen == 0){
        listItem.classList.add("contacts-item", "friends", "unread");
    }
    else{
        listItem.classList.add("contacts-item", "friends");
    }

    // Imposta l'attributo data-id con il valore di senderUUID
    listItem.setAttribute("data-id", senderUUID);

    var link = document.createElement("a");
    link.classList.add("contacts-link");
    link.href = `./chat-1.html?senderUUID=${encodeURIComponent(senderUUID)}&myUUID=${encodeURIComponent(myUUID)}&isGroup=false`;


    var avatarDiv = document.createElement("div");
    avatarDiv.classList.add("avatar", "avatar");

    var img = document.createElement("img");

    img.src = "./../assets/media/avatar/user.png";
    img.alt = "";

    avatarDiv.appendChild(img);

    var contactsContent = document.createElement("div");
    contactsContent.classList.add("contacts-content");

    var contactsInfo = document.createElement("div");
    contactsInfo.classList.add("contacts-info");

    var chatName = document.createElement("h6");
    chatName.classList.add("chat-name", "text-truncate");
    chatName.textContent = name;

    var chatTime = document.createElement("div");
    chatTime.classList.add("chat-time");
    chatTime.textContent = getTimeAgo(timestamp);

    contactsInfo.appendChild(chatName);
    contactsInfo.appendChild(chatTime);

    var contactsTexts = document.createElement("div");
    contactsTexts.classList.add("contacts-texts");

    var messageParagraph = document.createElement("p");
    messageParagraph.classList.add("text-truncate");
    messageParagraph.textContent = lastMessage;

    var unseenCountMessage = document.createElement("div");
    unseenCountMessage.classList.add("badge","badge-rounded","badge-primary","ml-1")
    unseenCountMessage.textContent = unseenCount;

    contactsTexts.appendChild(messageParagraph);
    if(unseenCount > 0){
    contactsTexts.appendChild(unseenCountMessage);
    }

    contactsContent.appendChild(contactsInfo);
    contactsContent.appendChild(contactsTexts);

    link.appendChild(avatarDiv);
    link.appendChild(contactsContent);

    listItem.appendChild(link);

    // Associazione dell'evento click all'elemento appena creato
    listItem.addEventListener('click', function(event) {
        const dataId = listItem.getAttribute('data-id');
        if (dataId) {
            console.log('Data ID:', dataId);
        } else {
            console.log('Data ID non presente');
        }
    });

    contactsList.appendChild(listItem);
}

// Crea la chat per gruppi
function createGroupChatItem(groupName, myUUID, senderUUID, name, lastMessage, timestamp, seen, unseenCount,emptyGroup=false) {
    var contactsList = document.getElementById("chatContactTab");

    var listItem = document.createElement("li");
    if (seen == 0){
        listItem.classList.add("contacts-item", "groups", "unread");
    }
    else{
        listItem.classList.add("contacts-item", "groups");
    }

    // Imposta l'attributo data-id con il valore di senderUUID
    listItem.setAttribute("data-id", senderUUID);

    var link = document.createElement("a");
    link.classList.add("contacts-link");
    link.href = `./chat-1.html?senderUUID=${encodeURIComponent(senderUUID)}&myUUID=${encodeURIComponent(myUUID)}&isGroup=true`;

    var avatarDiv = document.createElement("div");
    avatarDiv.classList.add("avatar", "avatar");

    var img = document.createElement("img");

    img.src = "./../assets/media/avatar/group.png";
    img.alt = "";

    avatarDiv.appendChild(img);

    var contactsContent = document.createElement("div");
    contactsContent.classList.add("contacts-content");

    var contactsInfo = document.createElement("div");
    contactsInfo.classList.add("contacts-info");

    var chatName = document.createElement("h6");
    chatName.classList.add("chat-name", "text-truncate");
    chatName.textContent = groupName;

    var chatTime = document.createElement("div");
    chatTime.classList.add("chat-time");
    if(!emptyGroup){
    chatTime.textContent = getTimeAgo(timestamp);
    }

    contactsInfo.appendChild(chatName);
    contactsInfo.appendChild(chatTime);

    var contactsTexts = document.createElement("div");
    contactsTexts.classList.add("contacts-texts");

    var messageParagraph = document.createElement("p");
    messageParagraph.classList.add("text-truncate");
    if(!emptyGroup){
    messageParagraph.innerHTML = `<span>${senderUUID}: </span>${lastMessage}`;
    }
    else{
    messageParagraph.innerHTML = `${lastMessage}`;
    messageParagraph.style.color = "gray";
    }

    var unseenCountMessage = document.createElement("div");
    unseenCountMessage.classList.add("badge","badge-rounded","badge-primary","ml-1")
    unseenCountMessage.textContent = unseenCount;

    contactsTexts.appendChild(messageParagraph);
    if(unseenCount > 0){
    contactsTexts.appendChild(unseenCountMessage);
    }

    contactsContent.appendChild(contactsInfo);
    contactsContent.appendChild(contactsTexts);

    link.appendChild(avatarDiv);
    link.appendChild(contactsContent);

    listItem.appendChild(link);

    // Associazione dell'evento click all'elemento appena creato
    listItem.addEventListener('click', function(event) {
        const dataId = listItem.getAttribute('data-id');
        if (dataId) {
            console.log('Data ID:', dataId);
        } else {
            console.log('Data ID non presente');
        }
    });

    contactsList.appendChild(listItem);
}

// Converte il tempo in quanti secondi,minuti o ore fa
function getTimeAgo(timestamp) {
    var now = new Date().getTime();
    var secondsPast = (now - timestamp) / 1000;

    if (secondsPast < 60) {
        return parseInt(secondsPast) + ' secondi fa';
    } else if (secondsPast < 3600) {
        var minutes = parseInt(secondsPast / 60);
        return minutes + (minutes === 1 ? ' minuto fa' : ' minuti fa');
    } else if (secondsPast < 21600) { // 6 ore = 21600 secondi
        var hours = parseInt(secondsPast / 3600);
        return hours + (hours === 1 ? ' ora fa' : ' ore fa');
    } else {
        var date = new Date(timestamp);
        var day = ('0' + date.getDate()).slice(-2);
        var month = ('0' + (date.getMonth() + 1)).slice(-2);
        var year = date.getFullYear().toString().slice(-2);
        return `${day}/${month}/${year}`;
    }
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

// Nasconde gli elementi della rubrica
function hideContacts() {
    const elementsToHide = document.querySelectorAll('.contacts-item, .contacts-item.active, small.font-weight-medium.text-uppercase.text-muted');
    elementsToHide.forEach(element => {
        element.style.display = 'none';
    });
}

function bindingContactListButton(){
    const friendsTab = document.getElementById('friends-tab');
    friendsTab.addEventListener('click', function(event) {
        event.preventDefault();
        Android.getContactsListJS();
    });
}

// Avviene quando la pagina è stata caricata
window.onload = function() {
    flushChatList();
    hideContacts();
    bindingContactListButton();

    Android.getConfigurations();
    Android.getBothLastMessagesJS();
    Android.getContactsListJS();
}
