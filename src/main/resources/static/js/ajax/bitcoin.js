let interval = setInterval(function(){
    $.get('/getTransaction/'+transactionCode, (response) => {
        let rsp = JSON.parse(response)
        console.log(rsp.status)
        if(rsp.status == 1){
            clearInterval(interval)
            window.location.href = "/results/success"
        }
    })
}, 3000);

function startTimer(duration, display) {
    var timer = duration, minutes, seconds;
    var counting = setInterval(function () {
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        display.textContent = minutes + ":" + seconds;

        if (--timer < 0) {
            timer = duration;
            clearInterval(interval)
            clearInterval(counting)
            //update stt 
            $.post('/updateTransaction/'+transactionCode, (response) => {
                let rsp = JSON.parse(response)
                console.log(rsp.code)
            })
        }
    }, 1000);
}

window.onload = function () {
    var fiveMinutes = 60 * 5
    var display = document.querySelector('#bitcoin-timer')
    startTimer(fiveMinutes, display)
};
