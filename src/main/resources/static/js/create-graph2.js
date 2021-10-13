const targetForm = document.querySelector('#graph-form');
const targetButton = targetForm.button;

const barGraphTarget = document.querySelector('#bar-graph');
const radarGraphTarget = document.querySelector('#radar-graph');

targetButton.addEventListener('click', () => {
    let season = targetForm.season;

    let grade = targetForm.grade.value;
    // let clazz = targetForm.clazz.value;
    let subject = targetForm.subject.value == "null" ? null : targetForm.subject.value;
    let graph = targetForm.graphType.value;
    let seasons = [];

    let username = targetForm.username.value;

    for (let s = 0; s < season.length; s++) {
        if (season[s].checked) {
            seasons.push(season[s].value);
        }
    }

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
                if(graphTypeCheckVal() == 'RADAR'){
                    createRadarChart(radarGraphTarget, labels, label, data, title);
                    createBarGraph(barGraphTarget, labels, label, data, title);
                }else{
                    if(radarGraph){
                        radarGraph.destroy();
                    }
                    createBarGraph(barGraphTarget, labels, label, data, title);
                }
            } else {
                console.log('通信失敗')
            }
        } else {
            console.log('通信中...')
        }
    }
    
    graphConfig = {
        // clazzOption: clazz,
        gradeOption: grade,
        seasonOption: seasons,
        subjectOption: subject,
        graphOption: graph
    };

    ajax.open('POST', '/rest/school/graph/' + username, true);
    ajax.setRequestHeader('content-type', 'application/json; charset=UTF-8');
    ajax.send(JSON.stringify(graphConfig));

});


const backgroundColors = ['RGBA(225,95,150, 0.3)', 'RGBA(115,255,25, 0.3)', "rgba(255,183,76,0.3)"];
const borderColors = ['RGBA(225,95,150, 1)', 'RGBA(115,255,25, 1)', "rgba(255,183,76,1)"];
const pointBackgroundColors = ['RGB(46,106,177)', 'RGB(46,106,177)', 'RGB(46,106,177)'];

const barBackgroundColors = ["rgba(219, 39, 91, 0.5)", "rgba(130,201,169,0.5)", "rgba(255,183,76,0.5)"];

let radarGraph;
let barGraph;

function createRaderDataSets(label, data) {
    const createDataSize = label.length;
    let createdata = [];
    for (let i = 0; i < createDataSize; i++) {
        createdata.push({
            label: label[i],
            data: data[i],
            backgroundColor: backgroundColors[i],
            borderColor: borderColors[i],
            borderWidth: 1,
            pointBackgroundColor: pointBackgroundColors[i]
        });
    }
    return createdata;
}

function createBarDataSets(label, data) {
    const createDataSize = label.length;
    let createdata = [];
    for (let i = 0; i < createDataSize; i++) {
        createdata.push({
            label: label[i],
            data: data[i],
            backgroundColor: barBackgroundColors[i],
            borderColor: borderColors[i],
            borderWidth: 1,
        });
    }
    return createdata;
}


function createRadarChart(target, labels, label, data, title) {
    let dataset = createRaderDataSets(label,data);
    if(radarGraph){
        radarGraph.destroy();
    }
    radarGraph = new Chart(target, {
        type: 'radar',
        data: {
            labels: labels,
            datasets: [...dataset]
        },
        options: {
            title: {
                display: true,
                text: title
            },
            scale: {

                ticks: {
                    suggestedMin: 0,
                    suggestedMax: 100,
                    min: 0,
                    max: 100,
                    stepSize: 20,
                    callback: function (value, index, values) {
                        return value + '点';
                    }
                }

            }
        }
    });
}

function createBarGraph(target, labels, label, data, title) {
    let dataset = createBarDataSets(label,data);
    if(barGraph){
        barGraph.destroy();
    }
    barGraph = new Chart(target, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [...dataset]
        },
        options: {
            title: {
                display: true,
                text: title
            },
            scales: {
                yAxes: [{
                        ticks: {
                            suggestedMax: 100,
                            suggestedMin: 0,
                            stepSize: 20,
                            callback: function (value, index, values) {
                                return value + '点';
                            }
                        }
                    }

                ]
            },
        }
    });
}