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
  document.getElementById("join-group-btn").style.display = "block";
}

function hideJoinGroupModal() {
  console.log("hide");
  document.getElementById("join-group-modal").style.display = "none";
  document.getElementById("join-group-text").style.display = "none";
  document.getElementById("join-group-btn").style.display = "none";
}
