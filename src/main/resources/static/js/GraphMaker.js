class GraphMaker {
    constructor() {
        this.backgroundColors = ['RGBA(225,95,150, 0.3)', 'RGBA(115,255,25, 0.3)', "rgba(255,183,76,0.3)", "rgba(145,70,219,0.6)", "rgba(53,85,212,0.6)"];
        this.borderColors = ['RGBA(225,95,150, 1)', 'RGBA(115,255,25, 1)', "rgba(255,183,76,1)", "rgba(145,70,219,1)", "rgba(53,85,212,1)"];
        this.pointBackgroundColors = ['RGB(46,106,177)', 'RGB(46,106,177)', 'RGB(46,106,177)'];
        this.barBackgroundColors = ["rgba(219, 39, 91, 0.5)", "rgba(130,201,169,0.5)", "rgba(255,183,76,0.5)", "rgba(145,70,219,0.5)", "rgba(53,85,212,0.5)"];

        this.raderGraph;
        this.barGraph;
        this.lineGraph;
    }

    _createRaderDataSets(label, data) {
        const createDataSize = label.length;
        let createData = [];
        for (let i = 0; i < createDataSize; i++) {
            createData.push({
                label: label[i],
                data: data[i],
                backgroundColor: this.backgroundColors[i],
                borderColor: this.borderColors[i],
                borderWidth: 1,
                pointBackgroundColor: this.pointBackgroundColors[i]
            });
        }
        return createData;
    }

    _createBarDataSets(label, data) {
        const createDataSize = label.length;
        let createData = [];
        for (let i = 0; i < createDataSize; i++) {
            createData.push({
                label: label[i],
                data: data[i],
                backgroundColor: this.barBackgroundColors[i],
                borderColor: this.borderColors[i],
                borderWidth: 1,
            });
        }
        return createData;
    }

    createRadarChart(target, labels, label, data, title) {
        let dataset = this._createRaderDataSets(label, data);
        if (this.radarGraph) {
            this.radarGraph.destroy();
        }
        this.radarGraph = new Chart(target, {
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

    createBarGraph(target, labels, label, data, title) {
        let dataset = this._createBarDataSets(label, data);
        if (this.barGraph) {
            this.barGraph.destroy();
        }
        let stepSize = 20;
        for (let i = 0; i < data.length; i++) {
            // console.log(data)
            // console.log(data[i])
            data[i].forEach(element => {
                if (element >= 100) {
                    stepSize = 50;

                }
            });
        }
        this.barGraph = new Chart(target, {
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
                                stepSize: stepSize,
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

    _createLineDataSets(label, data) {
        if (Array.isArray(label)) {
            const createDataSize = label.length;
            let createData = [];
            for (let i = 0; i < createDataSize; i++) {
                createData.push({
                    label: label[i],
                    data: data[i],
                    backgroundColor: this.barBackgroundColors[i],
                    borderColor: this.borderColors[i],
                    borderWidth: 1,
                    fill: 'origin',
                    showLine: true,
                });
            }
            return createData;
        } else {
            let createData = {
                datasets: [{
                label: label,
                data: data,
                backgroundColor: this.barBackgroundColors[0],
                borderColor: this.borderColors[0],
                borderWidth: 1,
                fill: 'origin',
                showLine: true,
                }]
            };
            return createData;
        }
    }

    createLineGraph(target, label, data) {
        let dataset = this._createLineDataSets(label, data);
        if (this.lineGraph) {
            this.lineGraph.destroy();
        }

        this.lineGraph = new Chart(target, {
            type: 'line',
            data: dataset,
            options: {
                plugins: {
                    filler: {
                        propagate: true
                    }
                },
                scales: {
                    yAxes: [{
                        scaleLabel: {
                            display: true,
                            labelString: '人数'
                        },
                        ticks: {
                            // max: 150,
                            // min: 0,
                            stepSize: 5,
                            callback: function (value, index, values) {
                                return value + '人';
                            }
                        }
                    }],
                    xAxes: [{
                        scaleLabel: {
                            display: true,
                            labelString: '点数'
                        },
                        type: 'linear',
                        position: 'bottom',
                        ticks: {
                            min: 0,
                            max: 100,
                            stepSize: 10,
                            callback: function (value, index, values) {
                                return value + '点';
                            }
                        }
                    }]
                },
            }
        });
    }

}