function init() {
  fetchBlobstoreUrlAndShowForm();
  loadPosts();
}

function loadPosts() {
  fetch('/group-post').then(response => response.json()).then((posts) => {
    const allPostsList = document.getElementById('posts-container');
    allPostsList.innerHTML = '';
    for (var i = 0; i < posts.length; i++) {
      allPostsList.appendChild(createSinglePost(posts[i]));
    }
  });
}

function createSinglePost(post) {
  const postDiv = document.createElement('div');
  postDiv.className = "post-div";
  postDiv.appendChild(createProfileImg(post));
  postDiv.append(createAuthor(post));
  postDiv.append(createPostText(post));
  postDiv.append(createCommentBox());
  return postDiv;
}

// Create HTML element for post profile img
function createProfileImg() {
  const profileImgDiv = document.createElement('div');
  profileImgDiv.className = "post-img align-vertical";
  return profileImgDiv;
}

// Create HTML element for post author name 
function createAuthor(post) {
  const postAuthor = document.createElement('h3');
  postAuthor.className = "post-author align-vertical";
  postAuthor.innerText = post.authorId;
  return postAuthor;
}

// Create HTML element for post text
function createPostText(post) {
  const postContent = document.createElement('p');
  postContent.className = "post-content";
  postContent.innerText = post.postText;
  return postContent;
}

// Create comment input HTML element
function createCommentBox() {
  const commentBox = document.createElement('input');
  commentBox.type = "text";
  commentBox.name = "comment-input";
  commentBox.value = "Write a comment";
  commentBox.id= "comment-input";
  return commentBox;
}

// Gets URL for uploaded image
function fetchBlobstoreUrlAndShowForm() {
  fetch('/post-image-handler').then((response) => {
  	return response.text();
  }).then((imageUploadUrl) => {
    const messageForm = document.getElementById('comments-form');
    messageForm.action = imageUploadUrl;
  });
}