function getChallenge() {
  console.log("here");
  const weeklyChallenge = document.getElementById("weekly-challenge");
  const topChallenge = document.getElementsByClassName("option-container");
  console.log(topChallenge);
  console.log(topChallenge.firstChild);
  console.log(topChallenge.firstElementChild);
  // console.log(topChallenge.firstChild.getElementsByTagName("p"));
  // const topChallengeText = topChallenge[0].getElementsByTagName("p").innerHTML;
  // weeklyChallenge.innerText = topChallengeText;
}

// window.addEventListener('load', function () {
//   console.log("window has loaded");
//   const weeklyChallenge = document.getElementById("weekly-challenge");
//   const topChallenge = document.getElementsByClassName("option-container");
//   console.log(topChallenge);
//   console.log(topChallenge[0]);
// })