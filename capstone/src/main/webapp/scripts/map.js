let map;
let markerBounds;
let markers = [];
function createMap(allCentralGroupLocations, locationsLimit) {
  map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: allCentralGroupLocations[0].coordinate.latitude, lng: allCentralGroupLocations[0].coordinate.longitude}, zoom: 10.0}); 
  
  addMarkers(allCentralGroupLocations, locationsLimit);
}

function removeMarkers(){
  for (let i = 0; i < markers.length; i++) {
    markers[i].setMap(null);
  }
}

function addMarkers(allCentralGroupLocations, locationsLimit) {
  removeMarkers();
  markers = [];
  markerBounds = new google.maps.LatLngBounds();

  for (let i = 1; i < locationsLimit + 1; i++) {
    addLocationMarker(allCentralGroupLocations[i]);
  }
  if (locationsLimit > 2) {
    map.fitBounds(markerBounds);
  }
}

function toggleLocationVisibility() {
  let locations_content = document.getElementById('group-locations-content');
  if(locations_content.style.display == 'block') {
    locations_content.style.display = 'none';
  } else {
    locations_content.style.display = 'block';
  }
}

function addLocationMarker(location) {
  locationPoint = new google.maps.LatLng(location.coordinate.latitude, location.coordinate.longitude);
  const marker = new google.maps.Marker({
    position: locationPoint,
    map: map,
    title: `${location.locationName}`
  });
  markerBounds.extend(locationPoint);

  let contentString = "<p class='marker-text'>" + location.locationName + "</p> <p class='marker-text'>" + location.address + "</p>";
  const infoWindow = new google.maps.InfoWindow({content: contentString});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
  markers.push(marker);
}