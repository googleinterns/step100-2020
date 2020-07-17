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
    .then(isMemberData =>
      handleJoinGroupModal(isMemberData["groupName"], isMemberData["isMember"])
    );
}

function handleJoinGroupModal(groupName, isMember) {
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
