const searchInput = document.getElementById("name-search");
const suggestionsPanel = document.getElementById("suggestions");
const KeyCodes = {
  UP: "38",
  DOWN: "40",
  ENTER: "13"
};

let currentFocus = -1;
let nameSuggestions;

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
    findSelected(list);
  } else {
    getSuggestions();
  }
}

/**
 * Handles pulling up results if user pressing enter after cycling through suggestions using arrow
 * keys.
 * @param {list} list
 */
function findSelected(list) {
  const active = document.getElementsByClassName("autocomplete-active")[0];
  if (active) {
    const name = active.innerHTML;
    completeNameAndSearch(name);
  } else {
    getSearchResults("");
  }
}

/**
 * Gets user results from server based on user input.
 * @param {string} selectedName
 */
function getSearchResults(selectedName) {
  let names = "";

  if (selectedName) {
    names = sort(selectedName).join(",");
  } else {
    nameSuggestions.forEach(name => (names = names.concat(`${name},`)));
  }
  fetch(`/search-results?names=${names}`).then(response =>
    response.json().then(results => displayResults(results))
  );
}

/**
 * Sorts list of names based off of name that user selected from suggestions. The suggested name
 * now goes to the front of the list.
 * @param {string} selectedName
 */
function sort(selectedName) {
  for (let i = 0; i < nameSuggestions.length; i++) {
    if (nameSuggestions[i] === selectedName) {
      let currName = nameSuggestions[i];
      nameSuggestions.splice(i, 1);
      nameSuggestions.unshift(currName);
    }
  }
  return nameSuggestions;
}

/**
 * Adds user results to DOM.
 * @param {list} results
 */
function displayResults(results) {
  const suggestionsContainer = document.getElementById("suggestions");
  suggestionsContainer.innerHTML = "";
  results.forEach(user => appendUser(user, suggestionsContainer));
}

/**
 * Adds each user result to DOM.
 * @param {json} user
 * @param {html} suggestionsContainer
 */
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
    }).then(() => {
      userDiv.classList.add("add-user-active");
      plusIcon.setAttribute("src", "images/tick.png");
    });
  });

  userDiv.appendChild(profile);
  userDiv.appendChild(plusIcon);
  suggestionsContainer.appendChild(userDiv);
}

/**
 * Gets name suggestions from server based on user input.
 */
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

/**
 * Adds name suggestions to DOM.
 * @param {list} suggestions
 */
function addSuggestions(suggestions) {
  nameSuggestions = suggestions;
  suggestionsPanel.innerHTML = "";
  if (suggestions.length >= 5) {
    for (let i = 0; i < 5; i++) {
      appendSuggestion(suggestions[i]);
    }
  } else {
    suggestions.forEach(name => appendSuggestion(name));
  }
}

/**
 * Adds each suggestion to DOM.
 * @param {string} suggested
 */
function appendSuggestion(suggested) {
  const name = document.createElement("div");
  name.innerHTML = suggested;
  name.addEventListener("click", () => completeNameAndSearch(suggested));
  suggestionsPanel.appendChild(name);
}

/**
 * Highlights the current suggestion based on user arrow clicks.
 * @param {array} list
 */
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

/**
 * Removes highlight on name suggestion.
 */
function removeActive() {
  const active = document.getElementsByClassName("autocomplete-active")[0];
  if (active) {
    active.classList.remove("autocomplete-active");
  }
}

/**
 * Fills in the search bar with the name of the suggested name that the user pressed on.
 * @param {string} fullname
 */
function completeNameAndSearch(fullname) {
  searchInput.value = fullname;
  getSearchResults(fullname);
}
