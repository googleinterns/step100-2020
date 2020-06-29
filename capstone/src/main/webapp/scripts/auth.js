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
  createLoginUrl();
}

/*
 * Create the login url for the Users API.
 * Fetch the authentication status of the user from the server.
 */
function createLoginUrl() {
  fetch('/login')
 .then(response => response.json())
 .then((login) => {
    // Need to set button's url to login url.
    const loginUrl = login.loginUrl;
    const loginButton = document.getElementById("login-btn");
    loginButton.setAttribute('href', loginUrl);
    if (login.loggedIn) {
      afterUserSignsIn();
    }
 });
}

/*
 * Sign up the user for the app if they have not already signed up.
 * Redirect logged in users to the correct page.
 */
function afterUserSignsIn() {
  // Send a POST request to the servlet which checks if a user is newly registered.
  fetch('/checkNewUser', {method: 'POST'})
  .then(() => {
    // TODO: Redirect newly registered users to a separate place.
    // Redirect to group page once logged in.
    window.location.href = 'group.html';
  });
}
