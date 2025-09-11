document.addEventListener('DOMContentLoaded', () => {
    
    const video = document.getElementById('background');
    const sound = document.getElementById('sound');
    
    if (video && sound) { /* если элементов нет вернет налл*/

        sound.addEventListener('click', () => {
            video.muted = !video.muted; 
            if (video.muted) {
                sound.textContent = 'ВКЛ ЗВУК';
            } else {
                sound.textContent = 'ВЫКЛ ЗВУК';
            }
        });

    } 
    const canvas = document.getElementById('canvas');
    const ctx = canvas.getContext('2d');

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
    const rval = document.getElementById('rvalue');
    if (rval) {
        drawShape(rval.value);
        
        rval.addEventListener('change', () => {
            drawShape(rval.value);
        });
    }
    
});