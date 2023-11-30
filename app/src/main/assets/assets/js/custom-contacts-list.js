let overlay = null;

// Viene chiamata da Kotlin
function receiveBothJsonList(jsonString, jsonStringGroup, myUUID) {
    flushChatList();
    var jsonData = JSON.parse(jsonString);
    var jsonDataGroup = JSON.parse(jsonStringGroup);
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
        createGroupChatItem("LoRa Group", myUUID, "LORAWAN0", "LORAWAN0", "Manda un messaggio a tutti quanti..", "", 0, 0, true);
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

// Avviene quando la pagina è stata caricata
window.onload = function() {
    flushChatList();
    Android.getBothLastMessagesJS();
}
