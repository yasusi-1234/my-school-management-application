const targetForm1 = document.querySelector('#graph-form');

const grade = targetForm1.grade;
// const clazz = targetForm1.clazz;
const season = targetForm1.season;
const subject = targetForm1.subject;
const graphType = targetForm1.graphType;
const button = targetForm1.button;

button.disabled = true;

grade.forEach(element => element.addEventListener('click', () => checkForm()));
// clazz.forEach(element => element.addEventListener('click', () => checkForm()));
season.forEach(element => element.addEventListener('click', () => checkForm()));

/* 教科が選択された際のリスナー追加、
全てが選択された場合は、グラフオプション側のラジオボタンが全て選べるようになり、
単体の教科が選ばれた場合グラフオプション側のレーダーは選べなくなる仕様
*/
for(let i = 0; i < subject.length; i++){
    if(subject[i].value != 'null'){
        subject[i].addEventListener('click',() => {
            graphType[0].disabled = true;
            graphType[1].disabled = false;
            checkForm();
        })
    }else if(subject[i].value == 'null'){
        subject[i].addEventListener('click', () =>{
            graphType[0].disabled = false;
            checkForm();
        })
    }
}

/*グラフオプションのラジオボタンが選択された際のリスナー追加、
レーダーのボタンがクリックされると、教科オプションの単体オプションが全てクリック不可になる。
棒グラフのボタンがクリックされると、教科オプション全て選択可能になる
*/
for(let i = 0; i < graphType.length; i++){
    if(graphType[i].value == 'RADAR'){
        graphType[i].addEventListener('click', () => {
            for(let j = 0; j < subject.length; j++){
                if(subject[j].value != 'null'){
                    subject[j].disabled = true;
                }else{
                    subject[j].disabled = false;
                }
            }
            checkForm();
        })
    }else if(graphType[i].value == 'BAR'){
        graphType[i].addEventListener('click', () => {
            for(let j = 0; j < subject.length; j++){
                if(subject[j].value != 'null'){
                    subject[j].disabled = false;
                }
            }
            checkForm();
        })
    }
}

function checkForm(){
    gradeCheck = check(grade);
    // clazzCheck = check(clazz);
    seasonCheck = [];
    subjectCheck = check(subject);
    graphTypeCheck = check(graphType);

    for(let i = 0; i < season.length; i++){
        if(season[i].checked){
            seasonCheck.push(season[i].value);
        }
    }

    if(gradeCheck && seasonCheck.length > 0 && subjectCheck && graphTypeCheck){
        button.disabled = false;
    }else{
        button.disabled = true;
    }
}

button.addEventListener('click', () =>{
    checkForm();
})

/**渡されたHTML要素のラジオボタンがチェックされているかを精査する */
function check(obj){
    for(let i = 0; i < obj.length; i++){
        objCheck = obj[i].checked;
        if(objCheck){
            return objCheck;
        }
    }
    return false;
}

function graphTypeCheckVal(){
    let checkVal;
    graphType.forEach(el => {
        if(el.checked){
            checkVal = el.value;
        }
    })
    return checkVal;
}
