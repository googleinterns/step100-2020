let groupId;

function init() {
  getGroupId();
  checkMembership(groupId);
  createLogoutUrl();
  loadPosts();
  getPollOptions(groupId);
  fetchBlobstoreUrlAndShowForm();
  loadMembers();
}

function getGroupId() {
  groupId = window.location.search.substring(1).split("=")[1];
}

function checkMembership(groupId) {
  fetch(`/join-group?groupId=${groupId}`)
    .then(response => response.json())
    .then(isMemberData => handleJoinGroupModal(isMemberData["isMember"]));
}

function handleJoinGroupModal(isMember) {
  if (isMember == "false") {
    showJoinGroupModal();
  } else {
    hideJoinGroupModal();
  }
}

function showJoinGroupModal() {
  document.getElementById("join-group-modal").style.display = "block";
  document.getElementById("join-group-text").style.display = "block";
  let button = document.getElementById("join-group-btn");
  button.style.display = "block";
  button.addEventListener("click", joinGroup);
}

function hideJoinGroupModal() {
  document.getElementById("join-group-modal").style.display = "none";
  document.getElementById("join-group-text").style.display = "none";
  document.getElementById("join-group-btn").style.display = "none";
}

function joinGroup() {
  fetch(`/join-group?groupId=${groupId}`, { method: "POST" }).then(
    hideJoinGroupModal
  );
}
