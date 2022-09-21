let interval = setInterval(function(){
    $.get('/getTransaction/'+transactionCode, (response) => {
        let rsp = JSON.parse(response)
        console.log(rsp.status)
        if(rsp.status == 1){
            clearInterval(interval)
            window.location.href = "/results/success"
        }
    })
}, 1000);
