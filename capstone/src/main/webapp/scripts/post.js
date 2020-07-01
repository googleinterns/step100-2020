function init() {
  loadPosts();
  getPollOptions();
  fetchBlobstoreUrlAndShowForm();
}

let postResponse;

function loadPosts() {
  fetch('/group-post').then(response => response.json()).then((postsResp) => {
    postResponse = postsResp;
    const posts = postResponse["posts"];
    const allPostsList = document.getElementById('posts-container');
    allPostsList.innerHTML = '';
    for (let i = 0; i < posts.length; i++) {
      allPostsList.appendChild(createSinglePost(posts[i], postResponse["likedPosts"]));
    }
  }).then(() => {
    addCommentInputListener();
    addLikeButtonListener();
  });
}

function addCommentInputListener() {
  let elements = document.getElementsByClassName('post-btn align-vertical comment-btn');
    for (let i = 0; i < elements.length; i++) {
      elements[i].addEventListener("click", function() {
        postComment(this.id, this.id + "comment-input");
      });
    }
}

function addLikeButtonListener() {
  let likeBtns = document.getElementsByClassName('like-icon vertical-align');
    for (let i = 0; i < likeBtns.length; i++) {
      likeBtns[i].addEventListener("click", function() {
        let postId = parseInt(getPostIdFromsLikesId(this.id));
        let userLikedPosts = postResponse["likedPosts"];
        if(userLikedPosts.includes(postId)) {
          likeToggled(this.id, false);
        } else {
          likeToggled(this.id, true);
        }
      });
    }
}

function likeToggled(likeId, liked) {
  let postId = getPostIdFromsLikesId(likeId);
  let request = new Request(`/update-likes?id=${postId}&liked=${liked}`, { method: "POST" });
  fetch(request).then(() => {
    loadPosts();
  }); 
}

/* likeId = postId + "like"
  use substring to just get the postId. */
function getPostIdFromsLikesId(likeId) {
  return likeId.substring(0, likeId.length - 4);
}

// Performs POST request to add comment to post 
function postComment(buttonId, commentBoxId) {
  const commentVal = document.getElementById(commentBoxId).value;
  let request = new Request(`/post-comment?id=${buttonId}&comment-text=${commentVal}`, { method: "POST" });
  fetch(request).then(() => {
    loadPosts();
  });
}

function createSinglePost(post, likedPosts) {
  const postDiv = document.createElement('div');
  postDiv.className = "post-div";
  postDiv.appendChild(createProfileImg(post));
  postDiv.append(createAuthor(post));
  postDiv.append(createPostText(post));
  if (post.img != null && post.img != "") {
    postDiv.append(createPostImage(post));
  }
  postDiv.append(createLikesContainer(post, likedPosts));
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

// Create HTML element for post img
function createPostImage(post) {
  const imageDiv = document.createElement('div');
  imageDiv.className = "img-div";
  let imageContent = document.createElement('img');
  imageContent.className = "uploadedImage";
  imageContent.src = "serve?blob-key=" + post.img;
  imageDiv.appendChild(imageContent);
  return imageDiv;
}

// Create HTML elements for like icon and string
function createLikesContainer(post, likedPosts) {
  const likesDiv = document.createElement('div');
  likesDiv.className = "likes-div";

  // Set number of likes label
  const likesLabel = document.createElement('p');
  likesLabel.className = "likes-label vertical-align";
  likesLabel.id = post.postId + "likes-label";
  const numLikes = post["likes"].length;
  const likesString = numLikes > 1 ? `${numLikes} likes` : `${numLikes} like`;
  const labelString = numLikes == 0 ? "" : likesString;
  likesLabel.innerText = `${labelString}`;
  likesDiv.appendChild(likesLabel);

  // Set like icon state (filled or only outline)
  const likeIcon = document.createElement('ion-icon');
  if (likedPosts.includes(post.postId)) {
    likeIcon.className = "like-icon vertical-align liked";
  } else {
    likeIcon.className = "like-icon vertical-align unliked";
  }
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
  for (comment of post.comments) {
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
  fetch('/post-image-blobstore').then((response) => {
  	return response.text();
  }).then((imageUploadUrl) => {
    const messageForm = document.getElementById('post-form');
    messageForm.action = imageUploadUrl;
  });
}