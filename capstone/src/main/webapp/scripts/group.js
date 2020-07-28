let groupId;

function init() {
  getGroupId();
  checkMembership();
  createLogoutUrl();
  loadPosts();
  getPollOptions();
  fetchBlobstoreUrlAndShowForm();
  loadMembers();
  loadTags();
}

function getGroupId() {
  groupId = window.location.search.substring(1).split("=")[1];
}

function checkMembership() {
  fetch(`/join-group?groupId=${groupId}`)
    .then(response => response.json())
    .then(isMemberData =>
      handleJoinGroupModal(isMemberData["groupName"], isMemberData["isMember"])
    );
}

function handleJoinGroupModal(groupName, isMember) {
  setGroupBannerText(groupName);
  if (!isMember) {
    showJoinGroupModal(groupName);
  } else {
    hideJoinGroupModal();
  }
}

function showJoinGroupModal(groupName) {
  document.getElementById("join-group-modal").style.display = "block";
  let paragraph = document.getElementById("join-group-text");
  paragraph.style.display = "block";
  let text = document.createTextNode(`Join ${groupName} to view its content`);
  paragraph.appendChild(text);

  let button = document.getElementById("join-group-btn");
  button.style.display = "block";
  button.addEventListener("click", joinGroup);

  let aTag = document.getElementById("go-to-profile");
  aTag.style.display = "block";
  aTag.setAttribute("href", "profile.html");
}

function hideJoinGroupModal() {
  document.getElementById("join-group-modal").style.display = "none";
  document.getElementById("join-group-text").style.display = "none";
  document.getElementById("join-group-btn").style.display = "none";
  document.getElementById("go-to-profile").style.display = "none";
}

function joinGroup() {
  fetch(`/join-group?groupId=${groupId}`, { method: "POST" }).then(
    hideJoinGroupModal
  );
}

function setGroupBannerText(groupName) {
  document.getElementById("group-name").textContent = groupName;
}
