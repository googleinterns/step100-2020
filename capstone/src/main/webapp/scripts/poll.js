function createOptionElement(text) {
  console.log(text);
  const optionsContainer = document.getElementById("options-container");
  const optionElement = document.createElement("div");
  optionElement.innerText = text;
  optionsContainer.appendChild(optionElement);
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
