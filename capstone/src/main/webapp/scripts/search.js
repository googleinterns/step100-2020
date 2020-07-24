const searchInput = document.getElementById("name-search");
const suggestionsPanel = document.getElementById("suggestions");

let currentFocus = -1;

function autocomplete() {
  searchInput.addEventListener("keyup", checkKey);
}

function checkKey(e) {
  let list = document.getElementById("suggestions");
  if (list) {
    list = list.getElementsByTagName("div");
  }
  if (e.keyCode == "38") {
    //if up arrow is pressed, highlight name
    currentFocus--;
    addActive(list);
  } else if (e.keyCode == "40") {
    //down arrow
    currentFocus++;
    addActive(list);
  } else if (e.keyCode == 13) {
    //press enter
  } else {
    getSuggestions();
  }
}

function getSuggestions() {
  const input = document.getElementById("name-search").value;
  if (input.trim() != "") {
    fetch(`name-data?input=${input}`)
      .then(response => response.json())
      .then(suggestions => suggest(suggestions));
  }
}

function suggest(suggestions) {
  const input = searchInput.value.toLowerCase();
  suggestionsPanel.innerHTML = "";
  suggestions.forEach(name => appendSuggestion(name));
  if (input === "") {
    suggestionsPanel.innerHTML = "";
  }
}

function addActive(list) {
  removeActive(list);
  if (currentFocus >= list.length) {
    currentFocus = 0;
  }
  if (currentFocus < 0) {
    currentFocus = list.length - 1;
  }
  list[currentFocus].classList.add("autocomplete-active");
}

function removeActive(list) {
  for (let i = 0; i < list.length; i++) {
    list[i].classList.remove("autocomplete-active");
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
