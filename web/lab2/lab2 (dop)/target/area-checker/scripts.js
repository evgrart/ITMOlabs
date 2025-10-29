document.addEventListener('DOMContentLoaded', () => {
    const video = document.getElementById('background');
    const sound = document.getElementById('sound');
    const canvas = document.getElementById('canvas');
    const ctx = canvas.getContext('2d');
    const r = document.getElementById('rvalue');
    const x = document.getElementById('xvalue');
    const y = document.getElementById('yvalue');
    const fire = document.getElementById('bam');
    const results = document.getElementById('results-body');
    const errors = document.getElementById('errors-body');
    const clusterColors = ['#00BFFF', '#FFD700', '#FF4500'];

    const maxh = 30;
    const scale = 80;

    let resultsHistory = [];
    let errorsHistory = [];

    if (video && sound) {
        sound.addEventListener('click', () => {
            video.muted = !video.muted;
            sound.textContent = video.muted ? 'ВКЛ ЗВУК' : 'ВЫКЛ ЗВУК';
        });
    }

    if (r) {
        r.addEventListener('change', () => {
            localStorage.setItem('lastRValue', r.value);
            drawShape(r.value);
        });
    }

    if (fire) {
        fire.addEventListener('click', (event) => {
            if (!validateForm()) {
                event.preventDefault();
            }
        });
    }

    if (canvas) {
        canvas.addEventListener('click', (event) => {
            const currentR = r.value;
            if (!currentR) {
                saveError("Не выбрано значение R");
                return;
            }

            const rect = canvas.getBoundingClientRect();
            const cX = canvas.width / 2;
            const cY = canvas.height / 2;

            const mX = (event.clientX - rect.left - cX) / scale;
            const mY = (cY - (event.clientY - rect.top)) / scale;

            const xValues = [-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2];
            const closestX = xValues.reduce((prev, curr) => {
                return Math.abs(curr - mX) < Math.abs(prev - mX) ? curr : prev;
            });
            x.value = closestX;
            y.value = mY.toFixed(4);
            
            document.querySelector(`input[name="x-choice"][value="${closestX}"]`).checked = true;
            
            document.querySelector('form').submit();
        });
    }

    function drawShape(rValue) {
        if (!canvas || !ctx || !rValue) return;
        
        const xC = canvas.width;
        const yC = canvas.height;
        const cx = xC / 2;
        const cy = yC / 2;
        ctx.clearRect(0, 0, xC, yC);
        
        const r_scaled = rValue * scale;
        ctx.fillStyle = 'rgba(20, 48, 46, 0.7)';

        //Прямоуг
        ctx.beginPath();
        ctx.rect(cx, cy, r_scaled, r_scaled / 2);
        ctx.fill();

        //Треугольник 
        ctx.beginPath();
        ctx.moveTo(cx, cy);
        ctx.lineTo(cx, cy - r_scaled / 2);
        ctx.lineTo(cx - r_scaled, cy);
        ctx.closePath();
        ctx.fill();

        //Треуг
        ctx.beginPath();
        ctx.moveTo(cx, cy);
        ctx.arc(cx, cy, r_scaled, Math.PI, 0.5 * Math.PI, true);
        ctx.closePath();
        ctx.fill();

        ctx.strokeStyle = 'black'; ctx.fillStyle = 'black';
        ctx.beginPath();
        ctx.moveTo(0, yC / 2); ctx.lineTo(xC, yC / 2);
        ctx.lineTo(xC - 10, yC / 2 - 5);
        ctx.moveTo(xC, yC / 2); ctx.lineTo(xC - 10, yC / 2 + 5);
        ctx.moveTo(xC / 2, yC); ctx.lineTo(xC / 2, 0);
        ctx.lineTo(xC / 2 - 5, 10);
        ctx.moveTo(xC / 2, 0); ctx.lineTo(xC / 2 + 5, 10);
        ctx.stroke();

        ctx.font = '16px Arial';
        const labels = [{v: rValue, l: 'R'}, {v: rValue/2, l: 'R/2'}, {v: -rValue/2, l: '-R/2'}, {v: -rValue, l: '-R'}];
        labels.forEach(item => {
            const xPos = cx + (item.v/rValue)*r_scaled, yPos = cy - (item.v/rValue)*r_scaled;
            ctx.beginPath(); ctx.moveTo(xPos, cy - 5); ctx.lineTo(xPos, cy + 5); ctx.stroke();
            ctx.textAlign = 'center'; ctx.fillText(item.v, xPos, cy + 20);
            ctx.beginPath(); ctx.moveTo(cx - 5, yPos); ctx.lineTo(cx + 5, yPos); ctx.stroke();
            ctx.textAlign = 'left'; ctx.fillText(item.v, cx + 10, yPos + 5);
        });

        resultsHistory.forEach(shot => {
            if (Number(shot.r) === Number(rValue)) {
                const shotX = cx + shot.x * scale;
                const shotY = cy - shot.y * scale;
                drawPoint(shotX, shotY, shot.hit, shot.cluster);
            }
        });
    }
    

    function drawPoint(x, y, isHit, clusterId) {
        ctx.beginPath();
        ctx.arc(x, y, 5, 0, 2 * Math.PI);

        if (clusterId !== undefined && clusterId !== -1 && clusterColors[clusterId]) {
            ctx.fillStyle = clusterColors[clusterId];
        } else {
            ctx.fillStyle = isHit ? 'lime' : 'red';
        }
        
        ctx.fill();
        ctx.strokeStyle = 'black';
        ctx.lineWidth = 1;
        ctx.stroke();
    }

    function validateForm() {
        const xStr = x.value.trim().replace(',', '.');
        const yStr = y.value.trim().replace(',', '.');

        if (isNaN(parseFloat(xStr)) || isNaN(parseFloat(yStr))) {
            saveError("X и Y должны быть числами");
            return false;
        }

        return true;
    }

    function addErrorToTable(message, time) {
        if (!errors) return;
        const row = errors.insertRow(-1);
        row.innerHTML = `<td>${message}</td><td>${time}</td>`;
    }

    function saveError(message) {
        const time = new Date().toLocaleTimeString();
        const errorData = { message, time };
        errorsHistory.unshift(errorData);
        if (errorsHistory.length > maxh) {
            errorsHistory.pop();
        }
        localStorage.setItem('errors', JSON.stringify(errorsHistory));
        updateErrorsFromHistory();
    }

    function updateErrorsFromHistory() {
        if (!errors) return;
        errors.innerHTML = '';
        errorsHistory.forEach(err => addErrorToTable(err.message, err.time));
    }
    
    function loadFromStorage() {
        errorsHistory = JSON.parse(localStorage.getItem('errors') || '[]');
        updateErrorsFromHistory();
    }

    
    loadFromStorage();
    
    if (typeof serverResults !== 'undefined') {
        resultsHistory = serverResults;
    }
    
    const savedR = localStorage.getItem('lastRValue');
    if (savedR && r) {
        r.value = savedR;
    }

    const arrow = document.getElementById('cursor-arrow');
    let isArrowVisible = false;

    document.addEventListener('keydown', (event) => {
        if (event.ctrlKey && event.code === 'Space') {
            event.preventDefault();

            isArrowVisible = !isArrowVisible;
            arrow.style.display = isArrowVisible ? 'block' : 'none';
        }
    });

    document.addEventListener('mousemove', (event) => {
        if (isArrowVisible) {
            arrow.style.left = (event.clientX - 5) + 'px';
            arrow.style.top = (event.clientY - 5) + 'px';
        }
    });

        
    drawShape(r.value || 3);
});