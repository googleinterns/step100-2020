let groupId;
let locationsLimit = 1;

function init() {
  createAPIKey();
  getGroupId();
  checkMembership();
  createLogoutUrl();
  loadPosts();
  getPollOptions();
  fetchBlobstoreUrlAndShowForm();
  loadMembers();
  findClosestGroupLocations();
  autocomplete();
  loadTags();
}

function createAPIKey() {
  let script = document.createElement('script');
  let key = JSON.parse('/api_key.json').GOOGLE_MAPS_API_KEY;
  script.src = "https://maps.googleapis.com/maps/api/js?key=" + key;
  document.getElementsByTagName('head')[0].appendChild(script);
}

function getGroupId() {
  groupId = window.location.search.substring(1).split("groupId=")[1];
}

function findClosestGroupLocations() {
  fetch(`/create-quadtree?groupId=${groupId}`, { method: "POST" });
  fetch(`/central-group-locations?groupId=${groupId}`).then(response => response.json()).then((allCentralGroupLocations) => {
    const groupLocations = document.getElementById("locations-container");
    groupLocations.innerHTML = "";
    for (let i = 1; i < locationsLimit + 1; i++) {
      groupLocations.appendChild(createLocationComponent(allCentralGroupLocations[i]));
    }
    createMap(allCentralGroupLocations, locationsLimit);
  });
}

function locationAmount() {
  const amount = document.getElementById("number");
  const value = amount.value;
  if (value === "1") {
    locationsLimit = 1;
  } else if (value === "5") {
    locationsLimit = 5;
  } else if (value === "10") {
    locationsLimit = 10;
  } else if(value === "15") {
    locationsLimit = 15;
  } else if(value === "20") {
    locationsLimit = 20;
  } 
  findClosestGroupLocations();
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
