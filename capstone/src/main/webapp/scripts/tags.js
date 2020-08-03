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
      let tagName = tag.text;
      const tagElement = document.createElement('span');
      tagElement.id = 'tag';
      tagElement.innerText = tagName;
      tagElement.addEventListener("click", function () {
        openTagsContext(tagName);
      });
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


/** 
 * Opens a modal that shows the usage of a tag in the context of 
 * the group's posts, comments, etc. 
 */
function openTagsContext(tag) {
  const tagNameElement = document.getElementById('tag-name');
  tagNameElement.innerHTML = 'Usage of tag: ' + tag;

  fetch(`/tags-context?groupId=${groupId}&tag=${tag}`)
  .then(response => response.json())
  .then((contextText) => {  
    let modal = document.getElementById('tags-context-modal');
    displayTagContext(contextText);
    modal.style.display = "block"
    addModalListeners('tags-context-modal', 'context-close');
  });
}

/** Display the context of a given tag. */
function displayTagContext(contextText) {
  const container = document.getElementById('context-container');
  container.innerHTML = "";

  for (context of contextText) {
    const textElement = document.createElement('p');
    textElement.id = 'context-text';
    const boldedText = boldTag(context.text, context.tagText);
    textElement.innerHTML = `${context.type}:	${boldedText}`;
    container.appendChild(textElement);
  }
}

/** Bold the given part of a text. */
function boldTag(text, tag) {
  return text.replace(new RegExp('(^|\\W)(' + tag + ')(\\W|$)','ig'), '$1<strong>$2</strong>$3');
}
