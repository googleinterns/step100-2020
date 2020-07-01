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
  displayGroups(user.groups);
  displayChallenges(user.groups);
  displayBadges(user.badges);
  displayProfilePicture(user.profilePic);
}

/** Display user's interests. */
function displayInterests(interests) {
  // for each interest
}

/** Display groups current user is a part of. */
function displayGroups(groups) {
  // for each group, use the group template
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