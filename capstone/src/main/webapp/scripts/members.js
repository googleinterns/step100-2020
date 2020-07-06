// When a member clicks anywhere outside modal, close it 
window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}

let modal = document.getElementById("myModal");

function showMemberProfile(){
  fetch('/group-member').then(response => response.json()).then((memResponse) => {
    createMemberModal(memResponse);
  }).then(() => {
    modal.style.display = "block";
  });
}

function createMemberModal(memResponse) {
  const modalDiv = document.createElement('div');


  return modalDiv;
}
