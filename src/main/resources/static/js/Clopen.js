const clopenTarget = document.querySelectorAll('.graph-area');
clopenTarget.forEach((value, key) => {
    let targetchild;
    let targetbtn;
    for(let child of value.children){
        if(child.className == 'graph-entity'){
            targetchild = child;
        }
        if(child.className == 'graph-btn'){
            targetbtn = child;
        }
    }
    console.log(targetchild);
    console.log(targetbtn);

    targetbtn.addEventListener('click', ()=>{
        targetchild.style.display = targetchild.style.display == "none" ? "" : "none";
        targetbtn.innerHTML = targetbtn.innerHTML == "開く" ? targetbtn.innerHTML = "閉じる" : targetbtn.innerHTML = "開く";
    })
});
