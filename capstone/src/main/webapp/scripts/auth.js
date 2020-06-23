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
  authenticateUser();
}

/*
 * Fetch the authentication status of the user from the server.
 * Create the login url for the Users API.
 */
function authenticateUser() {
  fetch('/login')
  .then(response => response.json())
  .then((login) => {
    // need to set button's url to login url.
    const loginUrl = login.url;
    const loginButton = document.getElementById('login-btn');
    loginButton.setAttribute('href', loginUrl);
  });
}

/*
 * Sign up the user.
 */
function signUpUser() {
  // get the user's first name and last name value through the form

  // send a POST request to the sign up servlet with the name as params
  fetch('/signup')
  .then(response => response.json())
  .then((login) => {
    // 
  });
}


let loginToggle = document.getElementById("login");
let registerToggle = document.getElementById("register");
/* 
 * Toggle login form.
 */
function login() {
  loginToggle.style.left = "50px";
  registerToggle.style.left = "450px";
}

/* 
 * Toggle register form.
 */
function register() {
  loginToggle.style.left = "-400px";
  registerToggle.style.left = "50px";
}