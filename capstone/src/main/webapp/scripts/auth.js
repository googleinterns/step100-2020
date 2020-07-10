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
 * Check if a user is new.
 * Redirect logged in users to the correct page.
 */
function afterUserSignsIn() {
  // Send a request to the servlet which checks if a user is new to the webpage.
  fetch('/checkNewUser')
 .then(response => response.json())
 .then((user) => {
    if (user.isUserNew) {
      openRegisterModal();
    } else {
      // Redirect to profile page once logged in.
      window.location.href = 'profile.html';
    }
  });
}

/*
 * Opens up a modal for new users to register.
 */
function openRegisterModal() {
  let modal = document.getElementById('register-modal');
  modal.classList.toggle('show-modal');
}

/*
 * Sign up new user for the app.
 */
function createNewUser() {
  const registerForm = document.getElementById('register');
  if (registerForm.reportValidity()) {
    const firstName = document.getElementById('first').value;
    const lastName = document.getElementById('last').value;
    const phoneNumber = document.getElementById('phone').value;
    const interests = document.getElementById('interests').value;

    const params = new URLSearchParams();
    params.append('first', firstName);
    params.append('last', lastName);
    params.append('phone', phoneNumber);
    params.append('interests', interests);

    // Send a POST request to the servlet which registers a new user.
    fetch('/createNewUser', {method: 'POST', body: params})
    .then(response => response.json())
    .then(() => {
      // Clear form text.
      document.getElementById('register').reset();
      // Redirect to group page once registered.
      window.location.href = 'group.html';
    });
  }
}
