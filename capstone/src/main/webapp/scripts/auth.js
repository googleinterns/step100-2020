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
    const loginButtons = document.getElementsByClassName("login-btn");
    for(let i = 0; i < loginButtons.length; i++) {
      loginButtons[i].setAttribute('href', loginUrl);
    }
 });
}

/*
 * Sign up the user. Placeholder code.
 */
function signUpUser() {
  // Get the user's first name and last name value through the form.
  const firstName = document.getElementById('first').value;
  const lastName = document.getElementById('last').value;

  const params = new URLSearchParams();
  params.append('first', firstName);
  params.append('last', lastName);
  // Send a POST request to the signup servlet with the user's name as params.
  fetch('/signup', {method: 'POST', body: params})
  .then(response => response.json())
  .then((ignore) => {
    // clear form text
    document.getElementById('register').reset();
  });
}

/* 
 * Toggle login form.
 */
function login() {
  let loginForm = document.getElementById("login");
  let loginToggle = document.getElementById("login-toggle");
  let registerForm = document.getElementById("register");
  let registerToggle = document.getElementById("reg-toggle");
  loginForm.style.display = "flex";
  registerForm.style.display = "none";
  loginToggle.classList.add("selected");
  registerToggle.classList.remove("selected");
}

/* 
 * Toggle register form.
 */
function register() {
  let loginForm = document.getElementById("login");
  let loginToggle = document.getElementById("login-toggle");
  let registerForm = document.getElementById("register"); 
  let registerToggle = document.getElementById("reg-toggle");
  loginForm.style.display = "none";
  registerForm.style.display = "flex";
  loginToggle.classList.remove("selected");
  registerToggle.classList.add("selected");
}