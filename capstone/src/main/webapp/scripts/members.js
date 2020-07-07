function loadMembers() {

}

function addModalListeners() {
  const modal = document.getElementById("memberProfile");
  const spanClose = document.getElementsByClassName("close")[0];
  spanClose.addEventListener("click", function () {
    if (event.target == modal) {
      modal.style.display = "none";
    }
  });
  const modalContent = document.getElementsByClassName("modal-content")[0];
  modalContent.innerHTML += '';
  modalContent.appendChild(createMemberModal());
  window.addEventListener("click", function () {
    if (event.target == modal) {
      modal.style.display = "none";
    }
  });
}

function addMemberProfileListener() {
  let elements = document.getElementsByClassName('member-grid-item');
    for (let i = 0; i < elements.length; i++) {
      elements[i].addEventListener("click", function() {
        showMemberProfile(this.id);
      });
    }
}

function showMemberProfile(userId){
  fetch(`/group-member?id=${userId}`).then(response => response.json()).then((memResponse) => {
    modal.append(createMemberModal(memResponse));
  }).then(() => {
    modal.style.display = "block";
  });
}

function createMemberModal() {
  const modalDiv = document.createElement('div');
  modalDiv.className = "modal-div";
  modalDiv.append(createMemberProfileImg());
  modalDiv.append(createMemberName());
  modalDiv.append(createMemberBadges());
  return modalDiv;
}

function createMemberProfileImg(){
  const memberProfileImgDiv = document.createElement('div');
  memberProfileImgDiv.className = "member-img";
  return memberProfileImgDiv;
}

function createMemberName(){
  const memberName = document.createElement('h3');
  memberName.className = "member-name";
  memberName.innerText = "Jane Doe";
  return memberName;
}

function createMemberBadges() {
  const memberBadges = document.createElement('div');
  memberBadges.className = "member-badges";
  return memberBadges;
}