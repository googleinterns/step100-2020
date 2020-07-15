/*
 * Create the logout url for the Users API.
 * Fetch the authentication status of the user from the server.
 */
function createLogoutUrl() {
  fetch('/login')
 .then(response => response.json())
 .then((login) => {
    // Need to set button's url to logout url.
    const logoutUrl = login.logoutUrl;
    const logoutButton = document.getElementById("logout-btn");
    logoutButton.setAttribute('href', logoutUrl);
    if (!login.loggedIn) {
      window.location.href = 'index.html';
    }
  });
}