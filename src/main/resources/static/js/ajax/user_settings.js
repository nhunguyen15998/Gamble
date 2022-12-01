$("#btn-general").on('click', () => {
    getClientConfig()
})

$('#btn-settings-save').on('click', () => {
    $('#require-password-error').text("")
    $('input[name="require_password"]').val("")
    $(`.${ALERT_AREA}`).remove()
    let configs = {
        "withdraw_password" : $('#withdraw-password-switch:checked').val() == null ? 0 : 1,
        "transfer_password" : $('#transfer-password-switch:checked').val() == null ? 0 : 1,
        "setting_password" : $('#setting-password-switch:checked').val() == null ? 0 : 1
    }
    console.log(configs)

    $.post('/user/configs/update', {"configs": JSON.stringify(configs)}, (response) => {
        console.log(response)
        switch (response.code) {
            case 200:
                alertMsg(response.message, alertSuccess, AREA_ID, ALERT_AREA)
                $('#require-password-modal').modal('hide')
                break
            case 406:
                $('#require-password-modal').modal('show')
                break
            case 304:
                break
        }
    }).fail((data) => {
        console.log(data)
    })
})

