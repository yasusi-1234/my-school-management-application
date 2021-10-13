let barrierTargetArray = [];
barrierTargetArray.push(document.querySelector(".btn-logout"));
barrierTargetArray.push(document.querySelector(".btn-login"));
barrierTargetArray.push(document.querySelector("#submit"));
barrierTargetArray.push(document.querySelector("#output"));

function barrierBtn(target){
   target.addEventListener('click', ()=>{
       console.log(target);
       target.classList.add('btn-barrier');
        setTimeout(() => {
            if(target.classList.contains('btn-barrier')){
               target.classList.remove('btn-barrier');
            }
        }, 3000);
    })
}

barrierTargetArray.filter(value => value).forEach(value => barrierBtn(value));