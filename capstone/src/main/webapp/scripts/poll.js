const BAR_WIDTH = "690";
const BAR_HEIGHT = "55";

function createOptionElement(text) {
  let innerBar = "200";
  let numVotes = "4";

  const optionsContainer = document.getElementById("options-container");
  const optionContainer = document.createElement("option-container");
  optionContainer.setAttribute("class", "option-container");
  //Add challenge name
  const challengeName = document.createElement("p");
  const challengeText = document.createTextNode(text);
  challengeName.appendChild(challengeText);
  optionContainer.appendChild(challengeName);
  //Poll bar
  const svgElement = document.createElementNS(
    "http://www.w3.org/2000/svg",
    "svg"
  );
  svgElement.setAttribute("width", BAR_WIDTH);
  svgElement.setAttribute("height", BAR_HEIGHT);
  const outerRect = document.createElementNS(
    "http://www.w3.org/2000/svg",
    "rect"
  );
  outerRect.setAttribute("width", BAR_WIDTH);
  outerRect.setAttribute("height", BAR_HEIGHT);
  outerRect.setAttribute("class", "poll-rect");
  const innerRect = document.createElementNS(
    "http://www.w3.org/2000/svg",
    "rect"
  );
  innerRect.setAttribute("width", innerBar);
  innerRect.setAttribute("height", BAR_HEIGHT);
  innerRect.setAttribute("class", "poll-inner-rect");
  svgElement.appendChild(outerRect);
  svgElement.appendChild(innerRect);
  optionContainer.appendChild(svgElement);
  //Create checkbox
  const checkContainer = document.createElement("span");
  checkContainer.setAttribute("class", "check-container");
  const inputElement = document.createElement("input");
  inputElement.setAttribute("type", "checkbox");
  inputElement.setAttribute("id", text);
  const labelElement = document.createElement("label");
  labelElement.htmlFor = text;
  checkContainer.appendChild(inputElement);
  checkContainer.appendChild(labelElement);
  optionContainer.appendChild(checkContainer);
  //Create votes label
  const votesLabel = document.createElement("div");
  votesLabel.setAttribute("class", "votes-label");
  votesLabel.innerText = `${numVotes} votes`;
  optionContainer.appendChild(votesLabel);
  //Append each option to outer options container
  optionsContainer.appendChild(optionContainer);
}

/**
 * Adds user's challenge suggestion.
 */
function addOption() {
  let text = document.getElementById("input-box").value;
  //if input is empty, can't submit
  if (text.trim() === "") return;
  document.getElementById("input-box").value = "";
  createOptionElement(text);
}
