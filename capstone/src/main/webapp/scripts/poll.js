const BAR_WIDTH = "690";
const BAR_HEIGHT = "55";
const TRANSITION_MILLIS = 300;

/**
 * Get poll options from server and load to DOM.
 */
function getPollOptions() {
  fetch("/poll")
    .then(response => response.json())
    .then(options => {
      const optionsContainer = document.getElementById("options-container");
      optionsContainer.innerHTML = "";
      options["options"].forEach(option => {
        renderOptionElement(option);
      });
      return options["votedOptions"];
    })
    .then(votedOptions => {
      handleCheck(votedOptions);
    });
}

/**
 * Handles whether checkbox is checked. Takes in list of
 * options for which current user has voted checks if id of
 * current checkbox is in that list.
 * @param {array} votedOptions
 */
function handleCheck(votedOptions) {
  const checkboxes = document.querySelectorAll("input[type=checkbox]");
  for (let i = 0; i < checkboxes.length; i++) {
    let checkbox = checkboxes[i];
    if (
      votedOptions.find(function(checkboxId) {
        return checkbox.id == checkboxId;
      })
    ) {
      checkbox.checked = true;
    } else {
      checkbox.checked = false;
    }
  }
  return;
}

/**
 * Uses template in group HTML file and changes text depending on data
 * being loaded.
 * @param {object} option
 */
function renderOptionElement(option) {
  let innerBarLength = "200";
  let numVotes = 0;
  if (option["votes"]) {
    numVotes = option["votes"].length;
  }
  const text = option["text"];

  const optionsContainer = document.getElementById("options-container");
  const optionElement = document.getElementById("option-template");
  const optionElementNode = document.importNode(optionElement.content, true);
  //Set name of challenge
  const challengeName = optionElementNode.querySelector("p");
  const challengeText = document.createTextNode(text);
  challengeName.appendChild(challengeText);
  //Set length of inner bar for poll
  const innerBar = optionElementNode.getElementById("inner-bar");
  innerBar.setAttribute("width", innerBarLength);
  //Set id for checkbox
  const checkbox = optionElementNode.querySelector("input");
  const id = option["id"];
  checkbox.id = id;
  //Set for field for label
  const label = optionElementNode.querySelector("label");
  label.htmlFor = id;
  //Set number of votes per challenge option
  const votesLabel = optionElementNode.getElementById("num-votes");
  const votesString = numVotes === 1 ? "vote" : "votes";
  votesLabel.innerText = `${numVotes} ${votesString}`;
  optionsContainer.appendChild(optionElementNode);
}

/**
 * Add new option to poll.
 */
function addPollOption() {
  const text = document.getElementById("input-box").value;
  if (text.trim() === "") return;
  document.getElementById("input-box").value = "";
  fetch(`poll?text=${text}`, { method: "POST" }).then(
    setTimeout(getPollOptions, 500)
  );
}

/**
 * Handles changing the number of votes when the checkbox is either
 * checked or unchecked.
 * @param {String} id
 * @param {String} checked
 */
function handleCheckboxCount(id, checked) {
  fetch(`update-votes?id=${id}&checked=${checked}`, { method: "POST" }).then(
    setTimeout(getPollOptions, TRANSITION_MILLIS)
  );
}
