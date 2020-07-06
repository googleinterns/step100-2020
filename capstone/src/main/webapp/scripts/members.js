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
  modalDiv.className = "modal-div";
  modalDiv.append(createMemberProfileImg());
  modalDiv.append(createMemberName(memResponse));
  modalDiv.append(createMemberBadges());
  return modalDiv;
}

function createMemberProfileImg(){
  const memberProfileImgDiv = document.createElement('div');
  memberProfileImgDiv.className = "member-img";
  return memberProfileImgDiv;
}

function createMemberName(memResponse){
  const memberName = document.createElement('h3');
  memberName.className = "member-name";
  memberName.innerText = memResponse.name;
  return memberName;
}

function createMemberBadges() {
  const memberBadges = document.createElement('div');
  memberBadges.className = "member-badges";
  return memberBadges;
}