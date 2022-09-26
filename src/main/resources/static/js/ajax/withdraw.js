$('#pills-withdraw-tab').on('click', () => {
    SECTION = 2
    PATH = '/withdrawBankProccessWithPassword'
    AREA_ID = '#withdraw-bank div'
})
$('#withdraw-bank-tab').on('click', () => {
    SECTION = 2
    PATH = '/withdrawBankProccessWithPassword'
    AREA_ID = '#withdraw-bank div'
})

$('#btn-bank-request').prop('disabled', false)

$('#btn-bank-request').on('click', () => {
    $('#btn-bank-request').prop('disabled', true)
    $('#require-password-error').text("")
    $('input[name="require_password"]').val("")
    $(`.${ALERT_AREA}`).remove()

    $('#withdraw_account_name_err').text("")
    $('#withdraw_account_number_err').text("")
    $('#withdraw_bank_err').text("")
    $('#withdraw_bank_amount_err').text("")

    let data = {
        "account_name": $('input[name="account_name"]').val(),
        "account_number": $('input[name="account_number"]').val(),
        "bank": $('input[name="bank"]').val(),
        "bank_amount": $('input[name="bank_amount"]').val(),
        "notes": $('input[name="notes"]').val()
    }

    $.ajax({
        url: '/withdrawBankProccess',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            console.log(response)
            switch (response.code) {
                case 200:
                    alertMsg(response.message, alertSuccess, AREA_ID, ALERT_AREA)
                    $('#span-balance').text("$"+response.wallet)
                    $('input[name="account_name"]').val("")
                    $('input[name="account_number"]').val("")
                    $('input[name="bank"]').val("")
                    $('input[name="bank_amount"]').val("")
                    $('input[name="notes"]').val("")
                    break
                case 406:
                    $('#require-password-modal').modal('show')
                    break
                case 400:
                    $('#withdraw_bank_amount_err').text(response.amount ?? "")
                    break
            }
            $('#btn-bank-request').prop('disabled', false)
        }, 
        error: function(data){
            console.log(data)
            data.responseJSON.errors.forEach(err => {
                $(`#withdraw_${err.field}_err`).text(err.defaultMessage ?? "")
            })
            if(data.responseJSON == null){  
                alertMsg('Whoops! Something went wrong!', alertDanger, AREA_ID, ALERT_AREA)
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
    SECTION = 3
    PATH = '/withdrawBitcoinProccessWithPassword'
    AREA_ID = '#withdraw-bitcoin div'
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
    $('#require-password-error').text("")
    $('input[name="require_password"]').val("")
    $(`.${ALERT_AREA}`).remove()
    $('#withdraw_bcaddress_err').text("")
    $('#withdraw_bitcoin_amount_err').text("")
    
    let data = {
        "bitcoin_amount": $('input[name="bitcoin_amount"]').val(),
        "transaction_code": transactionCode,
        "bcaddress": bcaddress
    }
    $.ajax({
        url: '/withdrawBitcoinProccess',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            switch (response.code) {
                case 200:
                    alertMsg(response.message, alertSuccess, AREA_ID, ALERT_AREA)
                    $('#span-balance').text("$"+response.wallet)
                    transactionCode = ""
                    bcaddress = ""
                    $('input[name="bitcoin_address"]').val("")
                    $('input[name="bitcoin_amount"]').val("")
                    break
                case 406:
                    $('#require-password-modal').modal('show')
                    break
                case 400:
                    $('#withdraw_bitcoin_amount_err').text(response.amount ?? "")
                    break
            }
            $('#btn-bitcoin-request').prop('disabled', false)
        }, 
        error: function(data){
            console.log(data)
            data.responseJSON.errors.forEach(err => {
                $(`#withdraw_${err.field}_err`).text(err.defaultMessage ?? "")
            })
            if(data.responseJSON == null){  
                alertMsg('Whoops! Something went wrong!', alertDanger, AREA_ID, ALERT_AREA)
            }
            $('#btn-bitcoin-request').prop('disabled', false)
        }
    })
})