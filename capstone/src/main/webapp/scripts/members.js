function loadMembers() {
  fetch(`/all-group-members`).then(response => response.json()).then((allGroupMembers) => {
    const allMembers = allGroupMembers;
    const memberGrid = document.getElementsByClassName("member-grid-container")[0];
    memberGrid.innerHTML = '';
    for (let i = 0; i < allMembers.length; i++) {
      memberGrid.appendChild(createMemberComponents(allMembers[i]));
    }
  }).then(() => {
    addMemberProfileListener();
  });
}

function createMemberComponents(memberInfo) {
  const memberImgDiv = document.createElement('div');
  memberImgDiv.id = memberInfo.userId;
  memberImgDiv.title = memberInfo.firstName + " " + memberInfo.lastName;
  // If user doesn't have image, replace with blank small yellow profile circle 
  if (memberInfo.profilePic == null || memberInfo.profilePic == "") {
    memberImgDiv.className = "member-grid-item member-img-blank small-member-img";
  } else {
    memberImgDiv.className = "member-grid-item member-img-div small-member-img";
    let memberImg = document.createElement('img');
    memberImg.className = "member-img small-member-img";
    memberImg.src = "serve?blob-key=" + memberInfo.profilePic;
    memberImgDiv.append(memberImg);
  }
  return memberImgDiv;
}

// When group member clicked, show detailed member profile modal
function addMemberProfileListener() {
  let elements = document.getElementsByClassName('member-grid-item');
    for (let i = 0; i < elements.length; i++) {
      elements[i].addEventListener("click", function() {
        showMemberProfile(this.id);
      });
    }
}

// Show group member profile (profile pic, name, and badges)
function showMemberProfile(userId){
  fetch(`/group-member?id=${userId}`).then(response => response.json()).then((memResponse) => {
    const modalContent = document.getElementsByClassName("modal-content")[0];
    modalContent.innerHTML = "<span class='close'>&times;</span>";
    addModalListeners();
    modalContent.appendChild(createMemberModal(memResponse));
  }).then(() => {
    const modal = document.getElementById("memberProfile");
    modal.style.display = "block";
  });
}

// Close modal if (x) button clicked or user clicks anywhere outside modal
function addModalListeners() {
  const modal = document.getElementById("memberProfile");
  const spanClose = document.getElementsByClassName("close")[0];
  spanClose.addEventListener("click", function () {
    modal.style.display = "none";
  });
  window.addEventListener("click", function () {
    if (event.target == modal) {
      modal.style.display = "none";
    }
  });
}

function createMemberModal(memResponse) {
  const modalDiv = document.createElement('div');
  modalDiv.className = "modal-div";
  modalDiv.append(createMemberProfileImg(memResponse));
  modalDiv.append(createMemberName(memResponse));
  modalDiv.append(createMemberBadges());
  return modalDiv;
}

function createMemberProfileImg(memResponse){
  const memberProfileImgDiv = document.createElement('div');
  // If user doesn't have image, replace with blank large yellow profile circle 
  if (memResponse.profilePic == null || memResponse.profilePic == "") {
    memberProfileImgDiv.className = "member-img-blank large-member-img";
  } else {
    memberProfileImgDiv.className = "member-img-div large-member-img";
    let memberProfileImg = document.createElement('img');
    memberProfileImg.className = "member-img large-member-img";
    memberProfileImg.src = "serve?blob-key=" + memResponse.profilePic;
    memberProfileImgDiv.append(memberProfileImg);
  }
  return memberProfileImgDiv;
}

function createMemberName(memResponse){
  const memberName = document.createElement('h3');
  memberName.className = "member-name";
  memberName.innerText = memResponse.firstName + " " + memResponse.lastName;
  return memberName;
}

function createMemberBadges() {
  const memberBadges = document.createElement('div');
  memberBadges.className = "member-badges";
  return memberBadges;
}