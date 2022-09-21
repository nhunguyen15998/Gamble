$('#btn-transfer-request').prop('disabled', false)
$('#btn-transfer-request').on('click', () => {
    $('#btn-transfer-request').prop('disabled', true)
    let div = document.createElement('div')
    let target = document.querySelector('#div-transfer')
    let data = {
        "phone": $('input[name="transfer_phone"]').val(),
        "amount": $('input[name="transfer_amount"]').val(),
        "notes": $('input[name="transfer_notes"]').val(),
    }
    $('#transfer_phone_err').text("")
    $('#transfer_amount_err').text("")
    $.ajax({
        url: '/transferProccess',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            if(response.code == 200){
                console.log(response.msg)
                let alert = `<div class="alert alert-success mb-4" role="alert">
                            ${response.msg} ${receiverName}
                        </div>`
                div.innerHTML += alert
                target.parentNode.insertBefore(div, target)
                $('#span-balance').text("$"+response.wallet)

                $('input[name="transfer_phone"]').val("")
                $('input[name="transfer_amount"]').val("")
                $('input[name="transfer_notes"]').val("")
                receiverName = 'ABC'
                $('.btn-transfer').closest('.row').children(".div-found").remove()
            } else {
                $('#transfer_amount_err').text(response.amount ?? "")
                $('#transfer_amount_err').text(response.amount ?? "")
                $('#transfer_phone_err').text(response.phone ?? "")
            }
            $('#btn-transfer-request').prop('disabled', false)
        },
        error: function(data){
            console.log(data)
            data.responseJSON.errors.forEach(err => {
                $(`#transfer_${err.field}_err`).text(err.defaultMessage ?? "")
            })
            if(data.responseJSON == null){  
                let alert = `<div class="alert alert-danger mb-5" role="alert">
                                Whoops! Something went wrong!
                            </div>`
                div.innerHTML += alert
                target.parentNode.insertBefore(div, target)
            }
            $('#btn-transfer-request').prop('disabled', false)
        }
    })
})

//search by phone
let receiverName = 'ABC'
$('input[name="transfer_phone"]').on('input', () => {
    $('#transfer_phone_err').text("")
    $('.btn-transfer').closest('.row').children(".div-found").remove()
    let phone = $('input[name="transfer_phone"]').val()
    if(phone == "") return
    if(phone.match("^[0][3|5|7|8|9][0-9]{8}$")){
        $.get('/getUserByPhone', {"phone":phone}, (response) => {
            receiverName = `${response.first_name} ${response.last_name}`
            let found = `<div class="align-items-center col-6 d-flex div-found">
                            <small>Found:</small>
                            <img src="${response.thumbnail}" width="50" style="border-radius: 50%;" class="mx-3">
                            <div class="d-flex flex-column">
                                <small>${receiverName}</small>
                                <small>${response.phone}</small>
                            </div>        
                        </div>`
            $(found).insertBefore('.btn-transfer')
        }).fail(() => {
            $('#transfer_phone_err').text("Not found")
        })
    }
})
