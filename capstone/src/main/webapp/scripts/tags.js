/** Fetch group tags from servlet. */
function loadTags() {
  fetch(`group-tags?groupId=${groupId}`)
  .then(response => response.json())
  .then((tags) => {
    displayTags(tags);
  });
}

/** Display group tags. */
function displayTags(tags) {
  const tagContainer = document.getElementById('group-tags');
  if (Object.keys(tags).length === 0) {
      const text = document.createElement('p');
      text.innerText = 'Your group doesn\'t have any tags yet. Try adding some content!';
      tagContainer.appendChild(text);
  } else {
    for (tag of tags) {
      const tagElement = document.createElement('span');
      tagElement.id = 'tag';
      tagElement.innerText = tag.text;
      tagContainer.appendChild(tagElement);
    }
  }
}

/** 
 * Opens an informational modal to explain 
 * what group tags are and how they are generated. 
 */
function openAboutTags() {
  let modal = document.getElementById('about-tags-modal');
  modal.style.display = "block"
  addModalListeners('about-tags-modal', 'about-close');
}
