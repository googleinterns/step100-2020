let groupId;

function init() {
  getGroupId();
  console.log(groupId);
  //   checkMembership(groupId);
  createLogoutUrl();
  loadPosts();
  getPollOptions(groupId);
  fetchBlobstoreUrlAndShowForm();
  loadMembers();
}

function getGroupId() {
  console.log("getting group id");
  console.log(window.location.search.substring(1).split("=")[1]);
  groupId = window.location.search.substring(1).split("=")[1];
}

function checkMembership() {
  fetch(`/join-group?groupId=${groupId}`).then(response => response.json());
}
