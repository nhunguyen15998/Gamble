$('#btn-bank-request').on('click', () => {
    let div = document.createElement('div')
    let target = document.querySelector('#withdraw-bank div')
    let data = {
        "account_name": $('input[name="account_name"]').val(),
        "account_number": $('input[name="account_number"]').val(),
        "bank": $('input[name="bank"]').val(),
        "bank_amount": $('input[name="bank_amount"]').val(),
        "notes": $('input[name="notes"]').val()
    }
    console.log(data)
    $.ajax({
        url: 'http://localhost:9090/api/withdrawBankProccess',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            console.log(response)
            $('#withdraw-account-name-err').text("")
            $('#withdraw-account-number-err').text("")
            $('#withdraw-bank-err').text("")
            $('#withdraw-bank-amount-err').text("")
            if(response.code == 200){
                console.log(response.msg)
                let alert = `<div class="alert alert-success mb-4" role="alert">
                            ${response.msg}
                        </div>`
                div.innerHTML += alert
                target.parentNode.insertBefore(div, target)
                $('#span-balance').text("$"+response.wallet)

                $('input[name="account_name"]').val("")
                $('input[name="account_number"]').val("")
                $('input[name="bank"]').val("")
                $('input[name="bank_amount"]').val("")
                $('input[name="notes"]').val("")
            } else {
                $('#withdraw-bank-amount-err').text(response.amount ?? "")
            }
        }, 
        error: function(data){
            console.log(data)
            if(data.responseJSON != null){
                $('#withdraw-account-name-err').text(data.responseJSON.account_name ?? "")
                $('#withdraw-account-number-err').text(data.responseJSON.account_number ?? "")
                $('#withdraw-bank-err').text(data.responseJSON.bank ?? "")
                $('#withdraw-bank-amount-err').text(data.responseJSON.bank_amount ?? "")
            } else {

            }
        }
    })
})

//tab
//amount input
$('#span-wallet').text($('#span-balance').text())
$('input[name="bank_amount"]').on('input', () => {
    let amount = $('input[name="bank_amount"]').val()
    let exrate = $('#span-exchange-rate').text()
    if(amount.match("^[+ -]?[0-9]*([.][0-9]*)?$")){
        let wallet = parseFloat($('#span-balance').text().replace("$", ""))
        let substract = parseFloat(amount)*parseFloat(exrate)
        $('#span-wallet').text(`$${wallet-substract}`)
    } 
    if(amount.trim() == ""){
        $('#span-wallet').text($('#span-balance').text())
    }
})

$('input[name="bitcoin_amount"]').on('input', () => {
    let amount = $('input[name="bitcoin_amount"]').val()
    let exrate = $('#span-bitcoin-exchange-rate').text()
    if(amount.match("^[+ -]?[0-9]*([.][0-9]*)?$")){
        let wallet = parseFloat($('#span-balance').text().replace("$", ""))
        let substract = parseFloat(amount)*parseFloat(exrate)
        $('#span-bitcoin-wallet').text(`$${wallet-substract}`)
    } 
    if(amount.trim() == ""){
        $('#span-bitcoin-wallet').text($('#span-balance').text())
    }
})

//bitcoin
$('#withdraw-bitcoin-tab').on('click', () => {
    paymentWithBTCExRate()
})
let transactionCode = ""
let bcaddress = ""
$('#btn-bitcoin-address').on('click', () => {
    $.get('/api/generateBCAddress', (response) => {
        let rsp = JSON.parse(response)
        transactionCode = rsp.transactionCode
        bcaddress = rsp.bcaddress
        $('input[name="bitcoin_address"]').val(rsp.bcaddress)
    })
})
$('#btn-bitcoin-request').on('click', () => {
    let div = document.createElement('div')
    let target = document.querySelector('#withdraw-bitcoin div')
    let data = {
        "bitcoin_amount": $('input[name="bitcoin_amount"]').val(),
        "transaction_code": transactionCode,
        "bcaddress": bcaddress
    }
    $.ajax({
        url: 'http://localhost:9090/api/withdrawBitcoinProccess',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            $('#withdraw-bitcoin-address-err').text("")
            $('#withdraw-bitcoin-amount-err').text("")
            if(response.code == 200){
                let alert = `<div class="alert alert-success mb-4" role="alert">
                            ${response.msg}
                        </div>`
                div.innerHTML += alert
                target.parentNode.insertBefore(div, target)
                $('#span-balance').text("$"+response.wallet)
                transactionCode = ""
                bcaddress = ""
                $('input[name="bitcoin_address"]').val("")
                $('input[name="bitcoin_amount"]').val("")
            } else {
                $('#withdraw-bitcoin-amount-err').text(response.bitcoin_amount ?? "")
            }
        }, 
        error: function(data){
            console.log(data)
            if(data.responseJSON != null){
                $('#withdraw-bitcoin-address-err').text(data.responseJSON.bcaddress ?? "")
                $('#withdraw-bitcoin-amount-err').text(data.responseJSON.bitcoin_amount ?? "")
            } else {

            }
        }
    })
})