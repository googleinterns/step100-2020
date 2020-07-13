// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/*
 * Initializes the page.
 */
 window.addEventListener('load', loadPage);

/*
 * Handles all functions to trigger when page loads.
 */
function loadPage() {
  getUserData();
}

/** Fetch current user's data from the server */
function getUserData() {
  fetch('/user')
  .then(response => response.json())
  .then((user) => {
    displayUserInfo(user);
    populateEditForm(user);
  });
}

/** Display current user's information on the page */
function displayUserInfo(user) {
  nameContainer = document.getElementById('name-container');
  nameContainer.innerHTML = user.name;

  emailContainer = document.getElementById('email-container');
  emailContainer.innerHTML = user.email;

  phoneContainer = document.getElementById('phone-container');
  phoneContainer.innerHTML = user.phoneNumber;

  displayInterests(user.interests);
  displayProfilePicture(user.profilePic);

  getGroupData(user.groups);
  
  displayBadges(user.badges);
}

/** Display user's interests. */
function displayInterests(interests) {
  const interestContainer = document.getElementById('interests-container');
  for (interest of interests) {
    const interestElement = document.createElement('span');
    interestElement.id = 'interest';
    interestElement.innerText = interest;
    interestContainer.appendChild(interestElement);
  }
}

/** Fetch user's group membership data. */
function getGroupData() {
  fetch('/user-groups')
  .then(response => response.json())
  .then((groups) => {
    displayGroups(groups);
    displayChallenges(groups);
  });
}

/** Display groups current user is a part of. */
function displayGroups(groups) {
  const groupsContainer = document.getElementById("groups-container");
  const groupElement = document.getElementById("group-template");

  // Hard coded group for now, will remove once we support users joining different groups
  let groupElementNode = document.importNode(groupElement.content, true);
  let groupLink = groupElementNode.getElementById('group-page-link');
  groupLink.href = "group.html";
  let groupName = groupElementNode.getElementById('group-name');
  groupName.innerText = "Group Name";
  groupsContainer.appendChild(groupElementNode);

  for (group of groups) {
    let groupElementNode = document.importNode(groupElement.content, true);

    let groupContainer = groupElementNode.querySelector('.group-container');
    // TODO: Set group-container div's id to be the groupId.

    let groupName = groupElementNode.getElementById('group-name');
    groupName.innerText = group.groupName;

    let groupImage = groupElementNode.getElementById('header-img');
    // TODO: Set image header based off of group.headerImg url.

    groupsContainer.appendChild(groupElementNode);
  }
}

/** Display the user's ongoing and past challenges  */
function displayChallenges(groups) {

}

/** Display the user's earned badges  */
function displayBadges(badges) {
  const badgeContainer = document.getElementById('badge-grid');
  for (badge of badges) {
    badgeContainer.appendChild(createBadgeElement(badge));
  }
}

/** Create a badge element to display */
function createBadgeElement(badge) {
  let badgeElement = document.createElement('div');
  badgeElement.setAttribute('id', badge.badgeId);
  badgeElement.setAttribute('class', 'badge');
  badgeElement.setAttribute('title', badge.challengeName);
  // TODO: Set image based off of badge.iconUrl
}

/** Display the user's profile picture */
function displayProfilePicture(picUrl) {

}

/** Open a modal form for users to edit their profile information */
function editProfile() {
  let modal = document.getElementById('edit-modal');
  modal.classList.toggle('show-modal');
}

/** Close modal form */
function closeModal() {
  let modal = document.getElementById('edit-modal');
  modal.classList.toggle('show-modal');
}

/** Populate form with user values */
function populateEditForm(user) {
  document.getElementById('modal-name-container').innerText = user.name;
  document.getElementById('first').value = user.firstName;
  document.getElementById('last').value = user.lastName;
  document.getElementById('email').value = user.email;
  document.getElementById('phone').value = user.phoneNumber;
  document.getElementById('interests').value = user.interests.join(', ');
}

/** Save updated profile information */
function saveEdits() {
  const editForm = document.getElementById('edit-profile');
  if (editForm.reportValidity()) {
    const firstName = document.getElementById('first').value;
    const lastName = document.getElementById('last').value;
    const email = document.getElementById('email').value;
    const phoneNumber = document.getElementById('phone').value;
    const interests = document.getElementById('interests').value;

    const params = new URLSearchParams();
    params.append('first', firstName);
    params.append('last', lastName);
    params.append('email', email);
    params.append('phone', phoneNumber);
    params.append('interests', interests);

    // Send a POST request to the servlet which registers a new user.
    fetch('/editProfile', {method: 'POST', body: params});
  }
}

/** Code to handle User joining a group */
function joinGroup() {

}

/** Code to handle User creating a group */
function createGroup() {

}