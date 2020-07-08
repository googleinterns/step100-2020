const BAR_HEIGHT = "55";
const BAR_WIDTH = "690";
const TRANSITION_MILLIS = 600;
let maxVotes;
let topChallenge = null;
const NO_CHALLENGES =
  "No current challenges. Submit a suggestion in the poll and mark checkbox for challenge to be posted. The challenge will be updated weekly based on top voted poll option.";

/**
 * Adds new option to poll.
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
 * Gets poll data, which includes each poll option and the list of options
 * that the current logged in user has voted for, from server and load to DOM. Then get the
 * weekly challenge.
 */
function getPollOptions() {
  fetch("/poll")
    .then(response => response.json())
    .then(pollData => {
      const optionsContainer = document.getElementById("options-container");
      optionsContainer.innerHTML = "";
      if (pollData["options"].length == 0) {
        return;
      }
      let maxVotes = getMaxVotes(pollData);
      topChallenge = pollData["options"][0]["text"];
      pollData["options"].forEach(option => {
        renderOptionElement(option, maxVotes);
      });
      return pollData["votedOptions"];
    })
    .then(votedOptions => {
      if (votedOptions) {
        handleCheck(votedOptions);
      } else {
        return;
      }
    })
    .then(getChallenge);
}

/**
 * Gets the current challenge from server and post to DOM. Check if the challenge needs to be
 * updated.
 */
function getChallenge() {
  //Milliseconds until challenge due date.
  let dueDateMillis = 0;
  fetch("challenge")
    .then(response => response.json())
    .then(challengeData => {
      const weeklyChallenge = document.getElementById("weekly-challenge");
      const dueDateContainer = document.getElementById("due-date");
      const challengeCheckbox = document.getElementsByClassName(
        "challenge-checkbox"
      )[0];
      const challengeLabel = document.getElementById("challenge-label");
      if (challengeData) {
        setChallengeCheckboxVisibility("visible");
        const id = challengeData["challenge"]["id"];
        challengeCheckbox.id = id;
        challengeLabel.setAttribute("for", id);
        challengeCheckbox.checked = challengeData["isCompleted"];
        weeklyChallenge.innerText = challengeData["challenge"]["challengeName"];
        dueDateMillis = challengeData["challenge"]["dueDate"];
        const dueDate = new Date(dueDateMillis).toString();
        dueDateContainer.innerText = `Due: ${dueDate}`;
        checkWeek(dueDateMillis);
      } else {
        noChallengeText();
        updatePoll();
      }
    });
}

function setChallengeCheckboxVisibility(visibility) {
  const challengeCheckbox = document.getElementsByClassName(
    "challenge-checkbox"
  )[0];
  const challengeLabel = document.getElementById("challenge-label");
  const markCompletedText = document.getElementById("mark-completed-text");

  challengeCheckbox.style.visibility = visibility;
  challengeLabel.style.visibility = visibility;
  markCompletedText.style.visibility = visibility;
}

/**
 * Displays text when there are no challenges and hides checkbox.
 */
function noChallengeText() {
  const weeklyChallenge = document.getElementById("weekly-challenge");
  const dueDateContainer = document.getElementById("due-date");
  setChallengeCheckboxVisibility("hidden");
  weeklyChallenge.innerText = NO_CHALLENGES;
  dueDateContainer.innerText = "";
}

/**
 * Checks if the challenge needs to be updated.
 * @param {long} dueDateMillis
 */
function checkWeek(dueDateMillis) {
  let now = new Date();
  let millisTillDueDate = new Date(dueDateMillis) - now;
  if (millisTillDueDate < 0) {
    updatePoll();
  }
}

/**
 * Deletes the top poll option, adding that option as a new challenge to the database.
 */
function updatePoll() {
  fetch("delete-top-option", { method: "POST" }).then(addChallengeToDb);
}

/**
 * Adds challenge to database.
 */
function addChallengeToDb() {
  topChallenge
    ? fetch(`challenge?name=${topChallenge}`, { method: "POST" })
    : noChallengeText();
}

/**
 * Gets the maximum number of votes for the poll. The option with maximum
 * number of votes should be the first option since the options are sorted in
 * order of votes.
 * @param {objct} pollData
 */
function getMaxVotes(pollData) {
  // Gets the list of votes for the first poll option
  let votesArray = pollData["options"][0]["votes"];
  return !!votesArray ? votesArray.length : 0;
}

/**
 * Handles whether checkbox is checked. Takes in list of ids of
 * checkboxes for which current user has checked and checks if id of
 * current checkbox is in that list.
 * @param {array} votedOptions
 */
function handleCheck(votedOptions) {
  const checkboxes = document.querySelectorAll("input[type=checkbox]");
  let votedOptionsSet = convertToMap(votedOptions);
  for (let i = 0; i < checkboxes.length; i++) {
    let checkbox = checkboxes[i];
    markCheckbox(votedOptionsSet, checkbox);
  }
  return;
}

/**
 * Converts list to hashmap.
 * @param {object} votedOptions
 */
function convertToMap(votedOptions) {
  let votedOptionsSet = {};
  votedOptions.forEach(option => (votedOptionsSet[option] = 1));
  return votedOptionsSet;
}

/**
 * Mark whether checkbox is checked by seeing if current checkbox is in
 * votedOptions, which contains the options for which the currently logged in
 * user has voted.
 * @param {object} votedOptionsSet
 * @param {object} checkbox
 */
function markCheckbox(votedOptionsSet, checkbox) {
  if (checkbox.id in votedOptionsSet) {
    checkbox.checked = true;
  } else {
    checkbox.checked = false;
  }
}

/**
 * Uses template in group HTML file and changes text depending on data
 * being loaded.
 * @param {object} option
 */
function renderOptionElement(option, maxVotes) {
  let numVotes = 0;
  if (option["votes"]) {
    numVotes = option["votes"].length;
  }
  //Calculate length of inner bar for poll
  let innerBarLength;
  if (maxVotes == 0) {
    innerBarLength = 0;
  } else {
    innerBarLength = (numVotes / maxVotes) * BAR_WIDTH;
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
 * Handles changing the number of votes when the checkbox is either
 * checked or unchecked.
 * @param {String} id
 * @param {String} checked
 */
function handleCheckboxCount(id, checked) {
  fetch(`mark-checkbox?id=${id}&checked=${checked}&type=Option`, {
    method: "POST"
  }).then(setTimeout(getPollOptions, TRANSITION_MILLIS));
}

/**
 * Handles challenge checkbox change.
 * @param {String} id
 * @param {String} checked
 */
function markChallenge(id, checked) {
  fetch(`mark-checkbox?id=${id}&checked=${checked}&type=Challenge`, {
    method: "POST"
  });
}
