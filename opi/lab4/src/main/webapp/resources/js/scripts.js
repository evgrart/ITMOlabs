(function() {
    let canvas, ctx, scale, rValue;
    let resultsHistory = [];
    let errorsHistory = []; 

    function addErrorToTable(message, time) {
        const errorsBody = document.getElementById('errors-body');
        if (!errorsBody) return;
        const row = errorsBody.insertRow(0);
        const cell1 = row.insertCell(0);
        const cell2 = row.insertCell(1);
        cell1.innerHTML = message;
        cell2.innerHTML = time;
    }

    function saveError(message) {
        const time = new Date().toLocaleTimeString();
        const errorData = { message, time };
        errorsHistory.unshift(errorData);
        
        if (errorsHistory.length > 30) {
            errorsHistory.pop();
        }
        
        localStorage.setItem('errors', JSON.stringify(errorsHistory));
        updateErrorsFromHistory();
    }

    function updateErrorsFromHistory() {
        const errorsBody = document.getElementById('errors-body');
        if (!errorsBody) return;
        errorsBody.innerHTML = '';
        errorsHistory.forEach(err => addErrorToTable(err.message, err.time));
    }

    function drawPoint(x, y, isHit) {
        if (!ctx) return;
        const canvasX = canvas.width / 2 + x * scale;
        const canvasY = canvas.height / 2 - y * scale;

        ctx.beginPath();
        ctx.arc(canvasX, canvasY, 5, 0, 2 * Math.PI);
        ctx.fillStyle = isHit ? 'lime' : 'red';
        ctx.fill();
        ctx.strokeStyle = 'black';
        ctx.lineWidth = 1;
        ctx.stroke();
    }

    function drawPoints() {
        resultsHistory.forEach(shot => {
            if (Number(shot.r) === Number(rValue)) {
                drawPoint(shot.x, shot.y, shot.hit);
            }
        });
    }

    function drawShape() {
        if (!canvas || !ctx || !rValue) return;
        
        const width = canvas.width;
        const height = canvas.height;
        const cx = width / 2;
        const cy = height / 2;
        ctx.clearRect(0, 0, width, height);
        
        const r_scaled = rValue * scale;
        ctx.fillStyle = 'rgba(78, 111, 230, 0.5)';

        // Прямоугольник
        ctx.beginPath();
        ctx.rect(cx, cy, r_scaled, r_scaled / 2);
        ctx.fill();

        // Четверть круга
        ctx.beginPath();
        ctx.moveTo(cx, cy);
        ctx.arc(cx, cy, r_scaled / 2, Math.PI, 1.5 * Math.PI, false);
        ctx.closePath();
        ctx.fill();

        // Треугольник
        ctx.beginPath();
        ctx.moveTo(cx, cy);              
        ctx.lineTo(cx - r_scaled, cy);     
        ctx.lineTo(cx, cy + r_scaled/2);  
        ctx.closePath();
        ctx.fill();

        ctx.strokeStyle = 'black';
        ctx.fillStyle = 'black';
        ctx.lineWidth = 1;

        ctx.beginPath();
        ctx.moveTo(0, cy); ctx.lineTo(width, cy);
        ctx.moveTo(cx, height); ctx.lineTo(cx, 0);  
        ctx.stroke();

        ctx.font = '14px Arial';
        
        const labels = [
            { val: rValue, text: 'R' },
            { val: rValue / 2, text: 'R/2' },
            { val: -rValue / 2, text: '-R/2' },
            { val: -rValue, text: '-R' }
        ];

        labels.forEach(label => {
            const xPos = cx + label.val * scale;
            ctx.textAlign = 'center';
            ctx.textBaseline = 'top';
            ctx.beginPath();
            ctx.moveTo(xPos, cy - 5);
            ctx.lineTo(xPos, cy + 5);
            ctx.stroke();
            ctx.fillText(label.text, xPos, cy + 8);

            const yPos = cy - label.val * scale;
            ctx.textAlign = 'left';
            ctx.textBaseline = 'middle';
            ctx.beginPath();
            ctx.moveTo(cx - 5, yPos);
            ctx.lineTo(cx + 5, yPos);
            ctx.stroke();
            ctx.fillText(label.text, cx + 8, yPos);
        });
    }

    window.updateCanvas = function() {
        if (typeof resultsJsonString !== 'undefined' && resultsJsonString) {
            try {
                resultsHistory = JSON.parse(resultsJsonString);
            } catch (e) {
                console.error("Failed to parse JSON results:", e);
                resultsHistory = [];
            }
        } else {
            resultsHistory = [];
        }
            
        const rElement = document.getElementById('mainForm:r-menu');
        if (rElement) {
            rValue = rElement.value;
        }

        drawShape();
        drawPoints();
    };

    window.onRChange = function(newR) {
        rValue = newR;
        drawShape();
        drawPoints();
    };

    function drawClock() {
        const clockCanvas = document.getElementById("clock");
        if (!clockCanvas) return; 
        
        const clockCtx = clockCanvas.getContext("2d");
        const radius = clockCanvas.height / 2;
        
        clockCtx.setTransform(1, 0, 0, 1, 0, 0);
        clockCtx.clearRect(0, 0, clockCanvas.width, clockCanvas.height);
        
        clockCtx.translate(radius, radius);
        const reducedRadius = radius * 0.90;

        function drawFace(ctx, radius) {
            ctx.beginPath();
            ctx.arc(0, 0, radius, 0, 2 * Math.PI);
            ctx.fillStyle = 'rgba(255, 255, 255, 0.1)';
            ctx.fill();
            
            ctx.strokeStyle = "white";
            ctx.lineWidth = radius * 0.05;
            ctx.stroke();
            
            ctx.beginPath();
            ctx.arc(0, 0, radius * 0.1, 0, 2 * Math.PI);
            ctx.fillStyle = 'white';
            ctx.fill();
        }

        function drawNumbers(ctx, radius) {
            ctx.font = radius * 0.15 + "px arial";
            ctx.textBaseline = "middle";
            ctx.textAlign = "center";
            ctx.fillStyle = "white";
            for(let num = 1; num <= 12; num++){
                let ang = num * Math.PI / 6;
                ctx.rotate(ang);
                ctx.translate(0, -radius * 0.85);
                ctx.rotate(-ang);
                ctx.fillText(num.toString(), 0, 0);
                ctx.rotate(ang);
                ctx.translate(0, radius * 0.85);
                ctx.rotate(-ang);
            }
        }

        function drawTime(ctx, radius) {
            const now = new Date();
            let hour = now.getHours();
            let minute = now.getMinutes();
            let second = now.getSeconds();
            hour = hour % 12;
            hour = (hour*Math.PI/6) + (minute*Math.PI/(6*60)) + (second*Math.PI/(360*60));
            drawHand(ctx, hour, radius*0.5, radius*0.07, "white");
            minute = (minute*Math.PI/30)+(second*Math.PI/(30*60));
            drawHand(ctx, minute, radius*0.8, radius*0.05, "white");
            second = (second*Math.PI/30);
            drawHand(ctx, second, radius*0.9, radius*0.02, "red");
        }

        function drawHand(ctx, pos, length, width, color) {
            ctx.beginPath();
            ctx.lineWidth = width;
            ctx.lineCap = "round";
            ctx.strokeStyle = color;
            ctx.moveTo(0,0);
            ctx.rotate(pos);
            ctx.lineTo(0, -length);
            ctx.stroke();
            ctx.rotate(-pos);
        }
        
        drawFace(clockCtx, reducedRadius);
        drawNumbers(clockCtx, reducedRadius);
        drawTime(clockCtx, reducedRadius);
    }

    document.addEventListener('DOMContentLoaded', () => {
        canvas = document.getElementById('canvas');
        if (canvas) {
            ctx = canvas.getContext('2d');
            scale = 55; 
            
            errorsHistory = JSON.parse(localStorage.getItem('errors') || '[]');
            updateErrorsFromHistory();

            canvas.addEventListener('click', (event) => {
                const currentR = document.getElementById('mainForm:r-menu').value;
                if (!currentR) {
                    saveError("Не выбрано значение R");
                    return;
                }

                const rect = canvas.getBoundingClientRect();
                const canvasX = (event.clientX - rect.left - canvas.width / 2) / scale;
                const canvasY = (canvas.height / 2 - (event.clientY - rect.top)) / scale;

                sendCanvasClick([{name: 'x', value: canvasX.toFixed(4)}, {name: 'y', value: canvasY.toFixed(4)}]);
            });
            
            updateCanvas();
        }

        const clockElement = document.getElementById("clock");
        if (clockElement) {
            setInterval(drawClock, 9000); 
            drawClock();
        }
    });
})();