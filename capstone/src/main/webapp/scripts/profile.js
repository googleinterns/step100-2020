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
  
  displayBadges(user.badges); // maybe change this to getBadgeData?
                              // or just add to Badge and User class
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

}

/** Display the user's profile picture */
function displayProfilePicture(picUrl) {

}

/** Open a modal form for users to edit their profile information */
function editProfile() {

}