const targetForm = document.querySelector('#form');

// const barGraphTarget = document.querySelector('#bar-graph');
// const radarGraphTarget = document.querySelector('#radar-graph');

const season = targetForm.season;
const grade = targetForm.grade.value;
const clazz = targetForm.clazz.value;
const year = targetForm.year.value;
const subject = targetForm.subject.value == "" ? null : targetForm.subject.value;
let seasons = [];

for (let s = 0; s < season.length; s++) {
    if (season[s].checked) {
        seasons.push(season[s].value);
    }
}

const createRader =  clazz !== 'ALL' && seasons.length === 1 && subject === 'ALL';

const tokenName = document.head.children._csrf_header.content;
const token = document.head.children._csrf.content;

graphConfig = {
    clazzOption: clazz,
    gradeOption: grade,
    seasonOption: seasons,
    subjectOption: subject,
    year: year
};

const graphMaker = new GraphMaker();
const createGraphHtml = new CreateGraphHtml('.graph');

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

            if (createRader) {
                // ここでHTML要素を作成する
                let bar = createGraphHtml.appendHTML('bar-graph');
                let rader = createGraphHtml.appendHTML('rader-graph');
                graphMaker.createRadarChart(rader, labels, label, data, title);
                graphMaker.createBarGraph(bar, labels, label, data, title);
            } else {
                if (graphMaker.radarGraph) {
                    graphMaker.radarGraph.destroy();
                }
                // ここでHTML要素を作成する
                let bar = createGraphHtml.appendHTML('bar-graph');
                graphMaker.createBarGraph(bar, labels, label, data, title);
            }
        } else {
            console.log('通信失敗')
        }
    } else {
        console.log('通信中...')
    }
}

ajax.open('POST', '/rest/school/graph', true);
ajax.setRequestHeader('content-type', 'application/json; charset=UTF-8');
ajax.setRequestHeader(tokenName,token);
ajax.send(JSON.stringify(graphConfig));
