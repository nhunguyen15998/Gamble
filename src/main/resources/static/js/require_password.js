/* require password modal */
$('#btn-require-password').on('click', () => {
    $('.alert-area').remove()
    $('#require-password-error').text("")
    switch (SECTION) {
        case 1:
            DATA = {
                "require_password": $('input[name="require_password"]').val(),
                "configs": {
                    "change_password" : $('#change-password-switch:checked').val() == null ? 0 : 1,
                    "withdraw_password" : $('#withdraw-password-switch:checked').val() == null ? 0 : 1,
                    "transfer_password" : $('#transfer-password-switch:checked').val() == null ? 0 : 1,
                    "setting_password" : $('#setting-password-switch:checked').val() == null ? 0 : 1
                }
            }
            break
        case 2:
            DATA = {
                "require_password": $('input[name="require_password"]').val(),
                "withdrawBank":{
                    "account_name": $('input[name="account_name"]').val(),
                    "account_number": $('input[name="account_number"]').val(),
                    "bank": $('input[name="bank"]').val(),
                    "bank_amount": $('input[name="bank_amount"]').val(),
                    "notes": $('input[name="notes"]').val()
                }
            }
            break
        case 3:
            DATA = {
                "require_password": $('input[name="require_password"]').val(),
                "withdrawBitcoin":{
                    "bitcoin_amount": $('input[name="bitcoin_amount"]').val(),
                    "transaction_code": transactionCode,
                    "bcaddress": bcaddress
                }
            }
            break
        case 4:
            DATA = {
                "require_password": $('input[name="require_password"]').val(),
                "transfer":{
                    "phone": $('input[name="transfer_phone"]').val(),
                    "amount": $('input[name="transfer_amount"]').val(),
                    "notes": $('input[name="transfer_notes"]').val()
                }
            }
            break
    }

    $.post(PATH, {data:JSON.stringify(DATA)}, (response) => {
        console.log(response)
        if(response.code == 200){
            $('#require-password-modal').modal('hide')
            switch (SECTION) {
                case 1:
                    alertMsg(response.message, alertSuccess, AREA_ID, ALERT_AREA)
                    break
                case 2:
                    alertMsg(response.message, alertSuccess, AREA_ID, ALERT_AREA)
                    $('#span-balance').text("$"+response.wallet)
                    $('input[name="account_name"]').val("")
                    $('input[name="account_number"]').val("")
                    $('input[name="bank"]').val("")
                    $('input[name="bank_amount"]').val("")
                    $('input[name="notes"]').val("")
                    $('#btn-bank-request').prop('disabled', false)
                    break
                case 3:
                    alertMsg(response.message, alertSuccess, AREA_ID, ALERT_AREA)
                    $('#span-balance').text("$"+response.wallet)
                    transactionCode = ""
                    bcaddress = ""
                    $('input[name="bitcoin_address"]').val("")
                    $('input[name="bitcoin_amount"]').val("")
                    $('#btn-bitcoin-request').prop('disabled', false)
                    break
                case 4:
                    alertMsg(response.message+" "+receiverName, alertSuccess, AREA_ID, ALERT_AREA)
                    $('#span-balance').text("$"+response.wallet)
                    $('input[name="transfer_phone"]').val("")
                    $('input[name="transfer_amount"]').val("")
                    $('input[name="transfer_notes"]').val("")
                    receiverName = 'ABC'
                    $('.btn-transfer').closest('.row').children(".div-found").remove()
                    $('#btn-transfer-request').prop('disabled', false)
                    break
            }
        } else {
            $('#require-password-error').text(response.code != 304 ? response.message : "")
        }
    }).fail((data) => {
        console.log(data)
        if(data.responseJSON == null){  
            alertMsg('Whoops! Something went wrong!', alertDanger, AREA_ID, ALERT_AREA)
        }
    })
})
/* end require password modal */