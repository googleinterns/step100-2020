function loadPosts() {
  fetch('/post').then(response => response.json()).then((posts) => {
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

  const profileImgDiv = document.createElement('div');
  profileImgDiv.className = "post-img align-vertical";
  postDiv.appendChild(profileImgDiv);

  const postAuthor = document.createElement('h3');
  postAuthor.className = "post-author align-vertical";
  postAuthor.innerText = post.authorId;
  postDiv.append(postAuthor);

  const postContent = document.createElement('p');
  postContent.className = "post-content";
  postContent.innerText = post.postText;
  postDiv.append(postContent);

  return postDiv;
}