function createOptionElement(text) {
  // const optionsContainer = document.getElementById("options-container");
  // const checkbox = document.createElement("input");
  // checkbox.type = "checkbox";
  // //replace with some id
  // checkbox.id = "id";
  // //replace with suggestion name
  // checkbox.name = "name";
  // //replace with value
  // checkbox.value = "value";
  // const label = document.createElement("label");
  // //replace with id
  // label.htmlFor = "id";
  // label.appendChild(document.createTextNode(text));
  // const br = document.createElement("br");
  // optionsContainer.appendChild(checkbox);
  // optionsContainer.appendChild(label);
  // optionsContainer.appendChild(br);
  console.log(text);
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
