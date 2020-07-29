let map;

function createMap(groupLocations) {
  map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: groupLocations[0].latitude, lng: groupLocations[0].longitude}, zoom: 4.0}); 
}

function createLocationMarker(location) {
  
}