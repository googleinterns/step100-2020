function init() {
  getGroupId();
  loadPosts();
  getPollOptions();
  fetchBlobstoreUrlAndShowForm();
  loadMembers();
}
