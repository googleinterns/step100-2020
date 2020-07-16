function init() {
  createLogoutUrl();
  getGroupId();
  loadPosts();
  getPollOptions();
  fetchBlobstoreUrlAndShowForm();
  loadMembers();
}
