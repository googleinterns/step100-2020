const BAR_WIDTH = "690";
const BAR_HEIGHT = "55";

function getPollOptions() {
  fetch("/poll")
    .then(response => response.json())
    .then(options => {
      const optionsContainer = document.getElementById("options-container");
      optionsContainer.innerHTML = "";
      options.forEach(option => {
        renderOptionElement(option);
      });
    });
}

function renderOptionElement(option) {
  let innerBarLength = "200";
  let numVotes = 0;
  if (option["votes"]) {
    numVotes = option["votes"].length;
  }
  let text = option["text"];

  const optionsContainer = document.getElementById("options-container");
  const optionElement = document.getElementById("option-template");
  const optionElementNode = document.importNode(optionElement.content, true);
  //Set name of challenge
  const challengeName = optionElementNode.querySelector("p");
  const challengeText = document.createTextNode(text);
  challengeName.appendChild(challengeText);
  const innerBar = optionElementNode.getElementById("inner-bar");
  innerBar.setAttribute("width", innerBarLength);
  const checkbox = optionElementNode.querySelector("input");
  checkbox.id = text;
  const label = optionElementNode.querySelector("label");
  label.htmlFor = text;
  //Set number of votes per challenge option
  const votesLabel = optionElementNode.getElementById("num-votes");
  const votesString = numVotes === 1 ? "vote" : "votes";
  votesLabel.innerText = `${numVotes} ${votesString}`;
  optionsContainer.appendChild(optionElementNode);
}

function addPollOption() {
  const text = document.getElementById("input-box").value;
  if (text.trim() === "") return;
  document.getElementById("input-box").value = "";
  fetch(`poll?text=${text}`, { method: "POST" });
  setTimeout(getPollOptions, 500);
}
