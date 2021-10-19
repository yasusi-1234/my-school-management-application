window.addEventListener('DOMContentLoaded',(event) =>{
    const button = document.getElementById('submit');
    const back = document.getElementsByClassName('back')[0];
    button.addEventListener('click',(e) =>{
        console.log('hello')
        back.style.visibility = 'visible';
        back.style.opacity = 1;
    })
})
