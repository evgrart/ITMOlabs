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
    
    if (video && sound) {
        sound.addEventListener('click', () => {
            video.muted = !video.muted;
            sound.textContent = video.muted ? 'ВКЛ ЗВУК' : 'ВЫКЛ ЗВУК';
        });
    }

    drawShape(r.value); 
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
                alert('Выбери значение R');
                return;
            }

            const rect = canvas.getBoundingClientRect();
            const canvasX = event.clientX - rect.left;
            const canvasY = event.clientY - rect.top;

            const scale = 80;
            const cX = canvas.width / 2;
            const cY = canvas.height / 2;
            
            const mX = (canvasX - cX) / scale;
            const mY = (cY - canvasY) / scale; 

            sendReq(mX.toFixed(2), mY.toFixed(2), r.value);
        });
    }

    function drawShape(rValue) {
        const x = canvas.width;
        const y = canvas.height;
        ctx.clearRect(0, 0, x, y);
        const scale = 80; 
        const gomot = rValue * scale; 
        ctx.fillStyle = 'rgb(20, 48, 46, 0.7)'; 

        ctx.rect(x / 2 - gomot, y / 2 - gomot, gomot, gomot);
        ctx.fill();
        ctx.stroke();

        ctx.beginPath(); 
        ctx.moveTo(x / 2, y / 2); 
        ctx.lineTo(x / 2, y / 2 + gomot); 
        ctx.lineTo(x / 2 - gomot, y / 2); 
        ctx.closePath(); 
        ctx.fill(); 

        
        ctx.beginPath();
        ctx.moveTo(x / 2, y / 2); 
        ctx.arc(x / 2, y / 2, gomot / 2, 0, Math.PI / 2); 
        ctx.closePath(); 
        ctx.fill(); 

        ctx.beginPath();
        ctx.moveTo(0, y / 2);
        ctx.lineTo(x, y / 2);
        ctx.lineTo(x - 10, y / 2 - 5);
        ctx.moveTo(x, y / 2);
        ctx.lineTo(x - 10, y / 2 + 5);

        ctx.moveTo(x / 2, y);
        ctx.lineTo(x / 2, 0);
        ctx.lineTo(x / 2 - 5, 10);
        ctx.moveTo(x / 2, 0);
        ctx.lineTo(x / 2 + 5, 10);
        ctx.stroke();

        ctx.fillStyle = 'black';
        ctx.font = '16px Arial';

        const ticks = [-1, -0.5, 0.5, 1]; 
        
    for (let i = 0; i < ticks.length; i++) {
        const tickValue = ticks[i]; 
        
        const position = tickValue * gomot; 
        const labelText = (tickValue * rValue).toString();
        const xPos = x / 2 + position;
        ctx.beginPath();
        ctx.moveTo(xPos, y / 2 - 5);
        ctx.lineTo(xPos, y / 2 + 5);
        ctx.stroke();
        ctx.textAlign = 'center';
        ctx.fillText(labelText, xPos, y / 2 + 20);

        const yPos = y / 2 - position;
        ctx.beginPath();
        ctx.moveTo(x / 2 - 5, yPos);
        ctx.lineTo(x / 2 + 5, yPos);
        ctx.stroke();
        ctx.textAlign = 'left';
        ctx.fillText(labelText, x / 2 + 10, yPos + 5);
    }
    }

    function validateForm() {
        const xStr = x.value.trim().replace(',', '.');
        const yStr = y.value.trim().replace(',', '.');
        
        const xNum = parseFloat(xStr);
        const yNum = parseFloat(yStr);
        
        if (isNaN(xNum) || isNaN(yNum)) {
            alert('X и Y - числа');
            return false;
        }

        if (yNum <= -5 || yNum >= 3) {
            alert('Y должен быть в диапазоне (-5; 3)');
            return false;
        }
        
        return true; 
    }

    function sendReq(x, y, r) {
        console.log(`Отправка запроса: x=${x}, y=${y}, r=${r}`);
        const url = `/fcgi-bin/lab-server-1.0.jar?x=${x}&y=${y}&r=${r}`;

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Сетевая ошибка: ${response.status} ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                console.log('Получен ответ от сервера:', data);
                if (data.error) {
                    alert(`Ошибка от сервера: ${data.error}`);
                } else {
                    updateUI(data);
                }
            })
            .catch(error => {
                console.error('Ошибка при отправке запроса:', error);
                alert('Не удалось связаться с сервером. Проверьте консоль (F12).');
            });
    }

    function updateUI(data) {
        const scale = 80;
        const centerX = canvas.width / 2;
        const centerY = canvas.height / 2;
        const canvasX = centerX + data.x * scale;
        const canvasY = centerY - data.y * scale;

        drawPoint(canvasX, canvasY, data.hit);
        addResultToTable(data);
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
            <td>${data.currentTime}</td>
            <td>${hitText}</td>
        `;
    }
});sendRequestToServer