class CreateGraphHtml{
    constructor(targetClass = '.graph-sample'){
        this.target = document.querySelector(targetClass);
    }

    appendHTML(graphType = '#bar-graph', divWidth){
        let addElementDiv = document.createElement('div');
        addElementDiv.className = "graph-area";
        if(divWidth){
            addElementDiv.style.width = divWidth;
        }

        let addElementSpan = document.createElement('span');
        addElementSpan.className = 'graph-btn';
        addElementSpan.innerHTML = '閉じる';
        addElementDiv.appendChild(addElementSpan);

        let addElementCanvas = document.createElement('canvas');
        addElementCanvas.width = 1;
        addElementCanvas.height = 1;
        addElementCanvas.id = graphType;
        addElementDiv.appendChild(addElementCanvas);

        addElementSpan.addEventListener('click', ()=>{
            addElementCanvas.style.display = addElementCanvas.style.display == "none" ? "" : "none";
            addElementSpan.innerHTML = addElementSpan.innerHTML == "開く" ? addElementSpan.innerHTML = "閉じる" : addElementSpan.innerHTML = "開く";
        })

        this.target.appendChild(addElementDiv);
        return addElementCanvas;
    }
}