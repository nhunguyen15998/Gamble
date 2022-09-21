$('#btn-bank-request').prop('disabled', false)
$('#btn-bank-request').on('click', () => {
    $('#btn-bank-request').prop('disabled', true)
    let div = document.createElement('div')
    let target = document.querySelector('#withdraw-bank div')
    let data = {
        "account_name": $('input[name="account_name"]').val(),
        "account_number": $('input[name="account_number"]').val(),
        "bank": $('input[name="bank"]').val(),
        "bank_amount": $('input[name="bank_amount"]').val(),
        "notes": $('input[name="notes"]').val()
    }
    $('#withdraw_account_name_err').text("")
    $('#withdraw_account_number_err').text("")
    $('#withdraw_bank_err').text("")
    $('#withdraw_bank_amount_err').text("")
    $.ajax({
        url: '/withdrawBankProccess',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            console.log(response)
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
                $('#withdraw_bank_amount_err').text(response.amount ?? "")
            }
            $('#btn-bank-request').prop('disabled', false)
        }, 
        error: function(data){
            console.log(data)
            data.responseJSON.errors.forEach(err => {
                $(`#withdraw_${err.field}_err`).text(err.defaultMessage ?? "")
            })
            if(data.responseJSON == null){  
                let alert = `<div class="alert alert-danger mb-5" role="alert">
                                Whoops! Something went wrong!
                            </div>`
                div.innerHTML += alert
                target.parentNode.insertBefore(div, target)
            }
            $('#btn-bank-request').prop('disabled', false)
        }
    })
})

//tab
//amount input
$('#span-wallet').text($('#span-balance').text())
$('input[name="bank_amount"]').on('input', () => {
    let amount = $('input[name="bank_amount"]').val()
    let exrate = $('#span-exchange-rate').text()
    if(amount.trim().match("^[+]?[0-9]*([.][0-9]*)?$")){
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
    if(amount.trim().match("^[+]?[0-9]*([.][0-9]*)?$")){
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
    $.get('/generateBCAddress', (response) => {
        let rsp = JSON.parse(response)
        transactionCode = rsp.transactionCode
        bcaddress = rsp.bcaddress
        $('input[name="bitcoin_address"]').val(rsp.bcaddress)
    })
})
$('#btn-bitcoin-request').prop('disabled', false)
$('#btn-bitcoin-request').on('click', () => {
    $('#btn-bitcoin-request').prop('disabled', true)
    let div = document.createElement('div')
    let target = document.querySelector('#withdraw-bitcoin div')
    let data = {
        "bitcoin_amount": $('input[name="bitcoin_amount"]').val(),
        "transaction_code": transactionCode,
        "bcaddress": bcaddress
    }
    $('#withdraw_bcaddress_err').text("")
    $('#withdraw_bitcoin_amount_err').text("")
    $.ajax({
        url: '/withdrawBitcoinProccess',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
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
                $('#withdraw_bitcoin_amount_err').text(response.bitcoin_amount ?? "")
            }
            $('#btn-bitcoin-request').prop('disabled', false)
        }, 
        error: function(data){
            console.log(data)
            data.responseJSON.errors.forEach(err => {
                $(`#withdraw_${err.field}_err`).text(err.defaultMessage ?? "")
            })
            if(data.responseJSON == null){  
                let alert = `<div class="alert alert-danger mb-5" role="alert">
                                Whoops! Something went wrong!
                            </div>`
                div.innerHTML += alert
                target.parentNode.insertBefore(div, target)
            }
            $('#btn-bitcoin-request').prop('disabled', false)
        }
    })
})