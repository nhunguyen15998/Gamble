//proceed payment
$('#btn-proceed-payment').prop('disabled', false)
$('#btn-proceed-payment').on('click', () => {
    $('#btn-proceed-payment').prop('disabled', true)
    let method = $('input[name="payments"]:checked').val()
    let data = {
        "amount": $('input[name="amount"]').val(),
        "method": method,
    }
    $.ajax({
        url: '/depositProcess',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            console.log(response)
            if(response.code == 200){
                switch(parseInt(method)){
                    case 0: //vnpay
                        window.location.href = response.data
                        break
                    case 1: //momo
                        window.location.href = response.payUrl
                        break
                    case 2: //bitcoin
                        window.location.href = response.data+`/${response.transactionCode}/`+`${response.bcaddress}`
                        break
                }
            } else {
                $('#amount-err').text(response.amount ?? "")
                $('#btn-proceed-payment').prop('disabled', false)
            }
        },
        error: function(data){
            console.log(data)
            data.responseJSON.errors.forEach(err => {
                $(`#${err.field}-err`).text(err.defaultMessage ?? "")
            })
            if(data.responseJSON == null){  
                let alert = `<div class="alert alert-danger mb-5" role="alert">
                                Whoops! Something went wrong!
                            </div>`
                div.innerHTML += alert
                target.parentNode.insertBefore(div, target)
            }
            $('#btn-proceed-payment').prop('disabled', false)
        }
    })
})

//regenerate bitcoin address
$('#btn-regenerate-code').on('click', () => {
    var transactionCode = window.location.href.split("/")[7]
    $.ajax({
        url: '/regenerateBitcoin',
        type: 'post',
        data: JSON.stringify({
            transactionCode: transactionCode
        }),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            if(response.bcaddress != null && response.data != null && response.transactionCode != null){
                window.location.href = response.data+`/${response.transactionCode}/`+`${response.bcaddress}`   
            }
        },
        error: function(data){
            console.log(data)
        }
    })
})




//ui
let exchangeRate = ''
function paymentWithVNDExRate(){
    $.get('/getExRate', {"name": "VND"}, (response) => {
        let rsp = JSON.parse(response)
        document.querySelector('#deposit-note small:first-child span').innerHTML = `1 VND = ${rsp.exchangeRate} USD`
        $('#span-exchange-rate').text(rsp.exchangeRate)
        exchangeRate = parseFloat(rsp.exchangeRate)
    })
    $('#deposit-currency').text('VND')
    $('#deposit-amount').text('0')
}

function paymentWithBTCExRate(){
    $.get('/getExRate', {"name": "BITCOIN"}, (response) => {
        let rsp = JSON.parse(response)
        document.querySelector('#deposit-note small:first-child span')
                .innerHTML = `1 BTC = ${(parseFloat(rsp.exchangeRate)).toLocaleString('en-US', {style: 'currency', currency: 'USD'})} USD`
        exchangeRate = parseFloat(rsp.exchangeRate)
        $('#span-bitcoin-exchange-rate').text(rsp.exchangeRate)
    })
}

if($('#vnpay').prop('checked', true)){   ///DEFAULT
    paymentWithVNDExRate()
    document.querySelector('#deposit-note small:last-child span').innerHTML = `10,000 - 10,000,000 VND`
}
$('#vnpay').on('click', () => {   ///VNPAY
    $('input[name="amount"]').val("")
    $('#amount-err').text("") 
    paymentWithVNDExRate()
    document.querySelector('#deposit-note small:last-child span').innerHTML = `10,000 - 10,000,000 VND`
})
$('#bitcoin').on('click', () => {   ///BITCOIN
    $('input[name="amount"]').val("")
    $('#amount-err').text("") 
    paymentWithBTCExRate()
    $('#deposit-currency').text('BTC')
    $('#deposit-amount').text('0')
    document.querySelector('#deposit-note small:last-child span').innerHTML = `0,0001 - 10 BTC`
})
$('#momo').on('click', () => {   ///MOMO
    $('input[name="amount"]').val("")
    $('#amount-err').text("") 
    paymentWithVNDExRate()
    document.querySelector('#deposit-note small:last-child span').innerHTML = `1,000 - 50,000,000 VND`
})

$('input[name="amount"]').on('input', () => {
    //alert(exchangeRate)
    let amount = $('input[name="amount"]').val()
    if(amount != "" && amount.trim().match("^[+]?[0-9]*([.][0-9]*)?$")){
        $('#amount-err').text("") 
        $('#deposit-amount').text((parseFloat(amount)*exchangeRate).toLocaleString('en-US', {style: 'currency', currency: 'USD', minimumFractionDigits:6}))
    } else {
        $('#amount-err').text("Invalid format")   
    }
})
$('.deposit-type').click(function(){
        $('.deposit-type').each(function(index, item) {
            $(item).removeClass('type-active');
        });
         $(this).addClass('type-active');
     
})