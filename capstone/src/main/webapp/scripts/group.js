let groupId;
let locationsLimit = 1;
let groupUser;

function init() {
  getUserData();
  getGroupId();
  checkMembership();
  createLogoutUrl();
  loadPosts();
  getPollOptions();
  fetchBlobstoreUrlAndShowForm();
  loadMembers();
  loadClosestGroupLocations();
  autocomplete();
  loadTags();
}

/** Fetch current user's data from the server */
function getUserData() {
  fetch("/user")
    .then(response => response.json())
    .then(user => {
      groupUser = user;
      const formImg = document.getElementById("post-form-img");
      formImg.className = "user-img-blank align-vertical";
      const navImg = document.getElementById("profile-btn");
      navImg.className = "profile-btn-blank";
      if (user.profilePic != null && user.profilePic != "") {
        const authorProfileImg = document.createElement("img");
        authorProfileImg.className = "user-img align-vertical";
        authorProfileImg.src = "serve?blob-key=" + user.profilePic;
        formImg.append(authorProfileImg);

        const navProfileImg = document.createElement("img");
        navProfileImg.className = "profile-btn-img";
        navProfileImg.src = "serve?blob-key=" + user.profilePic;
        navImg.append(navProfileImg);
      }  
    });
}

var xmlhttp = new XMLHttpRequest();
xmlhttp.onreadystatechange = function() {
  if (this.readyState == 4 && this.status == 200) {
    var myArr = JSON.parse(this.responseText);
    let script = document.createElement('script');
    script.src = "https://maps.googleapis.com/maps/api/js?key=" + myArr[0].GOOGLE_MAPS_API_KEY;
    script.innerHTML = "async defer";
    document.getElementsByTagName('head')[0].appendChild(script);
  }
};
xmlhttp.open("GET", "scripts/api_key.json", true);
xmlhttp.send();

function getGroupId() {
  groupId = window.location.search.substring(1).split("groupId=")[1];
}

function findClosestGroupLocations() {
  fetch(`/create-quadtree?groupId=${groupId}`, { method: "POST" }).then(() => {
    loadClosestGroupLocations();
  });
}

function loadClosestGroupLocations() {
  fetch(`/central-group-locations?groupId=${groupId}`).then(response => response.json()).then((allCentralGroupLocations) => {
    const groupLocations = document.getElementById("locations-container");
    if (allCentralGroupLocations.length == 0) {
      groupLocations.innerHTML = "No locations available. Please add more members or members are too far away.";
      let mapElement = document.getElementById('map');
      mapElement.style.display = "none";
    } else {
      groupLocations.innerHTML = "";
      for (let i = 1; i < locationsLimit + 1; i++) {
        groupLocations.appendChild(createLocationComponent(allCentralGroupLocations[i]));
      }
      let mapElement = document.getElementById('map');
      mapElement.style.display = "block";
      if (map == null) {
        createMap(allCentralGroupLocations, locationsLimit);
      } else {
        addMarkers(allCentralGroupLocations, locationsLimit);
      }
    }
  });
}

function locationAmount() {
  const amount = document.getElementById("number");
  locationsLimit = parseInt(amount.value);
  loadClosestGroupLocations();
}

function createLocationComponent(location){
  const locationDiv = document.createElement("div");

  const locationName = document.createElement("p");
  locationName.className = "location-name";
  locationName.innerText = location.locationName;
  locationDiv.append(locationName);

  const locationAddress = document.createElement("p");
  locationAddress.className = "location-detail";
  locationAddress.innerText = location.address;
  locationDiv.append(locationAddress);

  const locationLatLon = document.createElement("p");
  locationLatLon.className = "location-detail";
  locationLatLon.innerText = location.coordinate.latitude + ", " + location.coordinate.longitude;
  locationDiv.append(locationLatLon);

  const locationDistance = document.createElement("p");
  locationDistance.className = "location-detail";
  locationDistance.innerText = location.distance + "mi";
  locationDiv.append(locationDistance);

  return locationDiv;
}

function checkMembership() {
  fetch(`/join-group?groupId=${groupId}`)
    .then(response => response.json())
    .then(isMemberData =>
      handleJoinGroupModal(isMemberData["groupName"], isMemberData["isMember"])
    );
}

function handleJoinGroupModal(groupName, isMember) {
  setGroupBannerText(groupName);
  if (!isMember) {
    showJoinGroupModal(groupName);
  } else {
    hideJoinGroupModal();
  }
}

function showJoinGroupModal(groupName) {
  document.getElementById("join-group-modal").style.display = "block";
  let paragraph = document.getElementById("join-group-text");
  paragraph.style.display = "block";
  let text = document.createTextNode(`Join ${groupName} to view its content`);
  paragraph.appendChild(text);

  let button = document.getElementById("join-group-btn");
  button.style.display = "block";
  button.addEventListener("click", joinGroup);

  let aTag = document.getElementById("go-to-profile");
  aTag.style.display = "block";
  aTag.setAttribute("href", "profile.html");
}

function hideJoinGroupModal() {
  document.getElementById("join-group-modal").style.display = "none";
  document.getElementById("join-group-text").style.display = "none";
  document.getElementById("join-group-btn").style.display = "none";
  document.getElementById("go-to-profile").style.display = "none";
}

function joinGroup() {
  fetch(`/join-group?groupId=${groupId}`, { method: "POST" }).then(
    hideJoinGroupModal
  );
}

function setGroupBannerText(groupName) {
  document.getElementById("group-name").textContent = groupName;
}
