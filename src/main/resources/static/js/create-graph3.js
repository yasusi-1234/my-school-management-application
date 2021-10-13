const targetForm = document.querySelector('#form');

// const barGraphTarget = document.querySelector('#bar-graph');
// const radarGraphTarget = document.querySelector('#radar-graph');

const season = targetForm.season;
const grade = targetForm.grade.value;
const subject = targetForm.subject.value == "" ? null : targetForm.subject.value;
const username = targetForm.username.value;
let seasons = [];

for (let s = 0; s < season.length; s++) {
    if (season[s].checked) {
        seasons.push(season[s].value);
    }
}

const makeRader = subject == 'ALL' && seasons.length == 1;

const tokenName = document.head.children._csrf_header.content;
const token = document.head.children._csrf.content;

const graphMaker = new GraphMaker();
const createGraphHtml = new CreateGraphHtml('.graph');

graphConfig = {
    gradeOption: grade,
    seasonOption: seasons,
    subjectOption: subject
};

const ajax = new XMLHttpRequest();
ajax.onreadystatechange = () => {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            console.log('通信成功!!')
            let result = ajax.response;
            let {
                title,
                labels,
                label,
                data
            } = JSON.parse(result);
            console.log(result);

            if (makeRader) {
                let bar = createGraphHtml.appendHTML('bar-graph');
                let rader = createGraphHtml.appendHTML('rader-graph');
                graphMaker.createRadarChart(rader, labels, label, data, title);
                graphMaker.createBarGraph(bar, labels, label, data, title);
            } else {
                if (graphMaker.radarGraph) {
                    graphMaker.radarGraph.destroy();
                }
                let bar = createGraphHtml.appendHTML('bar-graph');
                graphMaker.createBarGraph(bar, labels, label, data, title);
                requestLineGraph();
            }
        } else {
            console.log('通信失敗')
        }
    } else {
        console.log('通信中...')
    }
}

ajax.open('POST', '/rest/school/graph/' + username, true);
ajax.setRequestHeader('content-type', 'application/json; charset=UTF-8');
ajax.setRequestHeader(tokenName, token);
ajax.send(JSON.stringify(graphConfig));

const createLineGraph = seasons.length === 1 && subject !== 'ALL';

// test
function requestLineGraph(){
    if(createLineGraph){
        const ajax2 = new XMLHttpRequest();
        ajax2.onreadystatechange = () => {
            if (ajax2.readyState == 4) {
                if (ajax2.status == 200) {
                    console.log('通信成功!!')
                    let result = ajax2.response;
                    console.log(result);
                    console.log(JSON.parse(result));
                    let {label, data} = JSON.parse(result);
                    let line = createGraphHtml.appendHTML('line');
                    graphMaker.createLineGraph(line, label, data);
                } else {
                    console.log('通信失敗')
                }
            } else {
                console.log('通信中...')
            }
        }
        
        studentDeviationGraphConfig = {
            gradeOption: grade,
            seasonOption: seasons[0],
            subjectOption: subject,
            // year: year
            // graphOption: graph
        };
        // 全教科の場合は実行しないようにする
        ajax2.open('POST', '/rest/school/graph/sample/' + username, true);
        ajax2.setRequestHeader('content-type', 'application/json; charset=UTF-8');
        ajax2.setRequestHeader(tokenName,token);
        ajax2.send(JSON.stringify(studentDeviationGraphConfig));
    
    }
}
