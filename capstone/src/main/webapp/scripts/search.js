const searchInput = document.getElementById("name-search");
const suggestionsPanel = document.getElementById("suggestions");

let names = [
  { fullname: "Lucy Qu" },
  { fullname: "Lucy Liu" },
  { fullname: "Himani Yadav" },
  { fullname: "Anika Bagga" },
  { fullname: "Lucille Ball" }
];
let currentFocus = -1;

function autocomplete() {
  searchInput.addEventListener("keyup", filter);
  searchInput.addEventListener("keyup", checkKey);
}

function filter() {
  const input = searchInput.value.toLowerCase();
  suggestionsPanel.innerHTML = "";
  const suggestions = names.filter(function(name) {
    return name.fullname.toLowerCase().startsWith(input);
  });
  suggestions.forEach(suggested => appendSuggestion(suggested));
  if (input === "") {
    suggestionsPanel.innerHTML = "";
  }
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
  name.innerHTML = suggested.fullname;
  name.addEventListener("click", () => completeName(suggested.fullname));
  suggestionsPanel.appendChild(name);
}

function completeName(fullname) {
  searchInput.value = fullname;
}

autocomplete();
