$('#btn-settings-save').on('click', () => {
    let configs = {
        "change_password" : $('#change-password-switch:checked').val(),
        "withdraw_password" : $('#withdraw-password-switch:checked').val(),
        "transfer_password" : $('#transfer-password-switch:checked').val()
    }
    console.log(configs)

    $.post('/configs/update', {"configs": JSON.stringify(configs)}, (response) => {
        console.log(response)
    }).fail((data) => {
        console.log(data)
    })
})