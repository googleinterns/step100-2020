function init() {
  loadPosts();
  uploadImage();
  //fetchBlobstoreUrlAndShowForm();
}

function uploadImage() {
  document.getElementById('camera-btn').addEventListener('click', openDialog);
    function openDialog() {
        document.getElementById('fileid').click();
        document.getElementById('fileid').addEventListener('change', fetchBlobstoreUrlAndShowForm);

        function fetchBlobstoreUrlAndShowForm() {
          fetch('/post-image-handler')
          .then((response) => {
            return response.text();
          })
          .then((imageUploadUrl) => {
            const messageForm = document.getElementById('post-form');
            messageForm.action = imageUploadUrl;
          });
        }
    }
}

function loadPosts() {
  fetch('/group-post').then(response => response.json()).then((postsResp) => {
    const posts = postsResp.posts;
    const allPostsList = document.getElementById('posts-container');
    allPostsList.innerHTML = '';
    for (let i = 0; i < posts.length; i++) {
      allPostsList.appendChild(createSinglePost(posts[i]));
    }
  }).then(() => {
    let elements = document.getElementsByClassName('post-btn align-vertical comment-btn');
    for (let i = 0; i < elements.length; i++) {
      elements[i].addEventListener("click", function() {
        postComment(this.id, this.id + "comment-input")
      });
    }

    let likeBtns = document.getElementsByClassName('like-icon vertical-align');
    for (let i = 0; i < likeBtns.length; i++) {
      likeBtns[i].addEventListener("click", function() {
        //this.classList.toggle("active");
        likeToggled(this.id, true);
      });
    }
  });
}

function likeToggled(likeId, liked) {
  const postId = likeId.substring(0, likeId.length - 4);
  const request = new Request(`/update-likes?id=${postId}&liked=${liked}`, { method: "POST" });
  fetch(request).then(() => {
    loadPosts();
  }); 
}

// Performs POST request to add comment to post 
function postComment(buttonId, commentBoxId) {
    const commentVal = document.getElementById(commentBoxId).value;
    const request = new Request(`/post-comment?id=${buttonId}&comment-text=${commentVal}`, { method: "POST" });
    fetch(request).then(() => {
      loadPosts();
    });
}

function createSinglePost(post) {
  const postDiv = document.createElement('div');
  postDiv.className = "post-div";
  postDiv.appendChild(createProfileImg(post));
  postDiv.append(createAuthor(post));
  postDiv.append(createPostText(post));
  postDiv.append(createLikesContainer(post));
  postDiv.append(createCommentsContainer(post));
  postDiv.append(createCommentBox(post));
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

function createLikesContainer(post) {
  const likesDiv = document.createElement('div');
  likesDiv.className = "likes-div";

  const likesLabel = document.createElement('p');
  likesLabel.className = "likes-label vertical-align";
  likesLabel.id = post.postId + "likes-label";
  likesLabel.innerText = "0 likes";
  likesDiv.appendChild(likesLabel);

  const likeIcon = document.createElement('ion-icon');
  likeIcon.className = "like-icon vertical-align";
  likeIcon.name = "heart";
  likeIcon.id = post.postId + "like";
  likesDiv.appendChild(likeIcon);

  return likesDiv;
}

// Create container for all post comments
function createCommentsContainer(post) {
  const commentsContainer = document.createElement('div');
  commentsContainer.className = "comments-content";
  const allComments = document.createElement('ul');
  for(comment of post.comments) {
    allComments.appendChild(createSingleComment(comment));
  }
  commentsContainer.appendChild(allComments);
  return commentsContainer;
}

function createSingleComment(comment) {
  const commentContainer = document.createElement('li');
  commentContainer.className = "comment-content";

  const commentUserImg = document.createElement('span');
  commentUserImg.className = "comment-user align-vertical";
  commentContainer.appendChild(commentUserImg);

  const commentTextDiv = document.createElement('div');
  commentTextDiv.className = "comment-text-div align-vertical";
  commentContainer.appendChild(commentTextDiv);

  const commentText = document.createElement('p');
  commentText.className = "comment-text align-vertical";
  commentText.innerText = comment.commentText;
  commentTextDiv.appendChild(commentText);

  return commentContainer;
}

// Create comment input HTML element
function createCommentBox(post) {
  const commentBoxDiv = document.createElement('div');
  commentBoxDiv.className = "comment-box-div";

  const commentBox = document.createElement('input');
  commentBox.type = "text";
  commentBox.name = "comment-input";
  commentBox.placeholder = "Write a comment";
  commentBox.className = "comment-input";
  commentBox.id = post.postId + "comment-input";
  commentBoxDiv.appendChild(commentBox);

  const commentBtn = document.createElement('button');
  commentBtn.className = "post-btn align-vertical comment-btn";
  commentBtn.type = "submit";
  commentBtn.id = post.postId;
  commentBtn.innerHTML = "<img class='small-icon' src='images/send_plane.png' alt/>";
  commentBoxDiv.appendChild(commentBtn);
  return commentBoxDiv;
}

// Gets URL for uploaded image
function fetchBlobstoreUrlAndShowForm() {
  fetch('/post-image-handler').then((response) => {
  	return response.text();
  }).then((imageUploadUrl) => {
    const messageForm = document.getElementById('post-form');
    messageForm.action = imageUploadUrl;
  });
}