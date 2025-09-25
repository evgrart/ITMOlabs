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

    r.addEventListener('change', () => {
        drawShape(r.value);
    });

    fire.addEventListener('click', (event) => {
        event.preventDefault();
        if (validateForm()) {
            const xv = x.value.trim().replace(',', '.');
            const yv = y.value.trim().replace(',', '.');
            const rv = r.value;
            sendReq(xv, yv, rv);
        }
    });

    if (canvas) {
        canvas.addEventListener('click', (event) => {
            if (!r.value) {
                saveError("Не выбрано значение R");
                return;
            }

            const rect = canvas.getBoundingClientRect();
            const canvasX = event.clientX - rect.left;
            const canvasY = event.clientY - rect.top;

            const cX = canvas.width / 2;
            const cY = canvas.height / 2;

            const mX = (canvasX - cX) / scale;
            const mY = (cY - canvasY) / scale;

            sendReq(Number(mX), Number(mY), r.value);
        });
    }

    function drawShape(rValue) {
        const xC = canvas.width;
        const yC = canvas.height;
        ctx.clearRect(0, 0, xC, yC);
        
        const gomot = rValue * scale;
        ctx.fillStyle = 'rgba(20, 48, 46, 0.7)';

        ctx.beginPath();
        ctx.rect(xC / 2 - gomot, yC / 2 - gomot, gomot, gomot);
        ctx.fill();
        
        ctx.beginPath();
        ctx.moveTo(xC / 2, yC / 2);
        ctx.lineTo(xC / 2, yC / 2 + gomot);
        ctx.lineTo(xC / 2 - gomot, yC / 2);
        ctx.closePath();
        ctx.fill();

        ctx.beginPath();
        ctx.moveTo(xC / 2, yC / 2);
        ctx.arc(xC / 2, yC / 2, gomot / 2, 0, Math.PI / 2);
        ctx.closePath();
        ctx.fill();

        ctx.beginPath();
        ctx.moveTo(0, yC / 2);
        ctx.lineTo(xC, yC / 2);
        ctx.lineTo(xC - 10, yC / 2 - 5);
        ctx.moveTo(xC, yC / 2);
        ctx.lineTo(xC - 10, yC / 2 + 5);

        ctx.moveTo(xC / 2, yC);
        ctx.lineTo(xC / 2, 0);
        ctx.lineTo(xC / 2 - 5, 10);
        ctx.moveTo(xC / 2, 0);
        ctx.lineTo(xC / 2 + 5, 10);
        ctx.stroke();

        ctx.fillStyle = 'black';
        ctx.font = '16px Arial';

        const ticks = [-1, -0.5, 0.5, 1];

        for (let i = 0; i < ticks.length; i++) {
            const tickValue = ticks[i];

            const position = tickValue * gomot;
            const labelText = (tickValue * rValue).toString();
            const xPos = xC / 2 + position;
            ctx.beginPath();
            ctx.moveTo(xPos, yC / 2 - 5);
            ctx.lineTo(xPos, yC / 2 + 5);
            ctx.stroke();
            ctx.textAlign = 'center';
            ctx.fillText(labelText, xPos, yC / 2 + 20);

            const yPos = yC / 2 - position;
            ctx.beginPath();
            ctx.moveTo(xC / 2 - 5, yPos);
            ctx.lineTo(xC / 2 + 5, yPos);
            ctx.stroke();
            ctx.textAlign = 'left';
            ctx.fillText(labelText, xC / 2 + 10, yPos + 5);
        }

        resultsHistory.forEach(shot => {
            const shotX = xC / 2 + shot.x * scale;
            const shotY = yC / 2 - shot.y * scale;
            drawPoint(shotX, shotY, shot.hit);
        });
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

    function sendReq(x, y, r) {
        console.log(`Отправка запроса: x=${x}, y=${y}, r=${r}`);
        const url = `/fcgi-bin/lab-server-1.0.jar?x=${x}&y=${y}&r=${r}`;

        fetch(url, {method:"GET"})
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Сетевая ошибка: ${response.status} ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                console.log('Получен ответ от сервера:', data);
                if (data.error) {
                    saveError(`Ошибка от сервера: ${data.error}`);
                } else {
                    updateUI(data);
                }
            })
            .catch(error => {
                console.error('Ошибка при отправке запроса:', error);
                saveError("Не удалось связаться с сервером");
            });
    }

    function updateUI(data) {
        const centerX = canvas.width / 2;
        const centerY = canvas.height / 2;
        
        const canvasX = centerX + data.x * scale;
        const canvasY = centerY - data.y * scale;

        drawPoint(canvasX, canvasY, data.hit);
        addResultToTable(data);

        saveToStorage(data);
    }

    function drawPoint(x, y, isHit) {
        ctx.beginPath();
        ctx.arc(x, y, 5, 0, 2 * Math.PI);
        ctx.fillStyle = isHit ? 'lime' : 'red';
        ctx.fill();
        ctx.strokeStyle = 'black';
        ctx.lineWidth = 1;
        ctx.stroke();
    }

    function addResultToTable(data) {
        if (!results) return;

        const row = results.insertRow(0);
        const hitText = data.hit ? 'Попадание' : 'Промах';

        row.innerHTML = `
            <td>${data.x}</td>
            <td>${data.y}</td>
            <td>${data.r}</td>
            <td>${hitText}</td>
            <td>${data.currentTime}</td>
        `;
    }

    function addErrorToTable(message, time) {
        if (!errors) return;
        const row = errors.insertRow(-1); 
        row.innerHTML = `
            <td>${message}</td>
            <td>${time}</td>
        `;
    }

    function saveError(message) {
        const time = new Date().toLocaleTimeString();
        const errorData = { message, time };

        errorsHistory.push(errorData); 
        if (errorsHistory.length > maxh) {
            errorsHistory.shift(); 
        }

        localStorage.setItem('errors', JSON.stringify(errorsHistory));
        updateErrorsFromHistory(); 
    }

    function updateErrorsFromHistory() {
        if (!errors) return;
        errors.innerHTML = '';

        [...errorsHistory].reverse().forEach(err => {
            addErrorToTable(err.message, err.time);
        });
    }

    function saveToStorage(data) {
        resultsHistory.unshift(data);

        if (resultsHistory.length > maxh) {
            resultsHistory.pop();
        }

        localStorage.setItem('results', JSON.stringify(resultsHistory));
    }

    function loadFromStorage() {
        resultsHistory = JSON.parse(localStorage.getItem('results') || '[]');
        errorsHistory = JSON.parse(localStorage.getItem('errors') || '[]');

        updateTableFromHistory();
        updateErrorsFromHistory();

        if (r.value) {
            drawShape(r.value);
        } else if (resultsHistory.length > 0) {
            r.value = resultsHistory[0].r;
            drawShape(resultsHistory[0].r);
        } else {
            drawShape(0);
        }
    }

    function updateTableFromHistory() {
        if (!results) return;
        results.innerHTML = '';

        resultsHistory.forEach(data => {
            addResultToTable(data);
        });
        const rows = Array.from(results.rows);
        results.innerHTML = '';
        rows.reverse().forEach(row => results.appendChild(row));
    }

    loadFromStorage();
});
