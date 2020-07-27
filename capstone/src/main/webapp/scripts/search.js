const searchInput = document.getElementById("name-search");
const suggestionsPanel = document.getElementById("suggestions");
const KeyCodes = {
  UP: "38",
  DOWN: "40",
  ENTER: "13"
};

let currentFocus = -1;

function autocomplete() {
  searchInput.addEventListener("keyup", checkKey);
}

function checkKey(e) {
  let suggestionsContainer = document.getElementById("suggestions");
  let list = suggestionsContainer.getElementsByTagName("div");
  if (e.keyCode == KeyCodes.UP) {
    currentFocus--;
    addActive(list);
  } else if (e.keyCode == KeyCodes.DOWN) {
    currentFocus++;
    addActive(list);
  } else if (e.keyCode == KeyCodes.ENTER) {
    // e.preventDefault();
    getSearchResults(list);
  } else {
    getSuggestions();
  }
}

function getSearchResults(list) {
  let names = "";
  if (list.length >= 5) {
    //Gets top 5 suggested names
    names = `${list[0].innerHTML},${list[1].innerHTML},${list[2].innerHTML},${list[3].innerHTML},${list[4].innerHTML}`;
  } else {
    for (let i = 0; i < list.length; i++) {
      names = names.concat(`${list[i].innerHTML},`);
    }
  }
  fetch(`/search-results?names=${names}`).then(response =>
    response.json().then(results => displayResults(results))
  );
}

function displayResults(results) {
  const suggestionsContainer = document.getElementById("suggestions");
  suggestionsContainer.innerHTML = "";
  results.forEach(user => appendUser(user, suggestionsContainer));
}

function appendUser(user, suggestionsContainer) {
  let userId = user.userId;
  let name = `${user.firstName} ${user.lastName}`;

  const userDiv = document.createElement("div");
  userDiv.innerHTML = name;
  userDiv.setAttribute("id", userId);
  userDiv.setAttribute("class", "user-suggestion");

  const profile = document.createElement("img");
  profile.setAttribute("src", "https://i.imgur.com/JMUNgVq.jpeg");
  profile.setAttribute("class", "profile-icon");

  const plusIcon = document.createElement("img");
  plusIcon.setAttribute("src", "images/plus_icon.png");
  plusIcon.setAttribute("class", "add-user-icon");
  plusIcon.addEventListener("click", function() {
    fetch(`group-member?userId=${userId}&groupId=${groupId}`, {
      method: "POST"
    });
  });

  userDiv.appendChild(profile);
  userDiv.appendChild(plusIcon);
  suggestionsContainer.appendChild(userDiv);
}

function getSuggestions() {
  const input = document.getElementById("name-search").value;
  if (input.trim() != "") {
    fetch(`name-data?input=${input}`)
      .then(response => response.json())
      .then(suggestions => addSuggestions(suggestions));
  } else {
    suggestionsPanel.innerHTML = "";
  }
}

function addSuggestions(suggestions) {
  suggestionsPanel.innerHTML = "";
  suggestions.forEach(name => appendSuggestion(name));
}

function appendSuggestion(suggested) {
  const name = document.createElement("div");
  name.innerHTML = suggested;
  name.addEventListener("click", () => completeName(suggested));
  suggestionsPanel.appendChild(name);
}

function addActive(list) {
  removeActive();
  if (currentFocus >= list.length) {
    currentFocus = 0;
  }
  if (currentFocus < 0) {
    currentFocus = list.length - 1;
  }
  list[currentFocus].classList.add("autocomplete-active");
}

function removeActive() {
  const active = document.getElementsByClassName("autocomplete-active")[0];
  if (active) {
    active.classList.remove("autocomplete-active");
  }
}

function completeName(fullname) {
  searchInput.value = fullname;
}
