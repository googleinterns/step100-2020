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
  let suggestions = document.getElementById("suggestions");
  let list;
  if (suggestions) {
    list = suggestions.getElementsByTagName("div");
    if (e.keyCode == KeyCodes.UP) {
      currentFocus--;
      addActive(list);
    } else if (e.keyCode == KeyCodes.DOWN) {
      currentFocus++;
      addActive(list);
    } else if (e.keyCode == KeyCodes.ENTER) {
      console.log("pressing enter");
    }
  }
  getSuggestions();
}

function getSuggestions() {
  const input = document.getElementById("name-search").value;
  if (input.trim() != "") {
    fetch(`name-data?input=${input}`)
      .then(response => response.json())
      .then(suggestions => suggest(suggestions));
  } else {
    suggestionsPanel.innerHTML = "";
  }
}

function suggest(suggestions) {
  suggestionsPanel.innerHTML = "";
  suggestions.forEach(name => appendSuggestion(name));
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

function appendSuggestion(suggested) {
  const name = document.createElement("div");
  name.innerHTML = suggested;
  name.addEventListener("click", () => completeName(suggested));
  suggestionsPanel.appendChild(name);
}

function completeName(fullname) {
  searchInput.value = fullname;
}
