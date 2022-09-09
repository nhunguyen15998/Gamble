$('#btn-transfer-request').on('click', () => {
    let div = document.createElement('div')
    let target = document.querySelector('#div-transfer')
    let data = {
        "phone": $('input[name="transfer_phone"]').val(),
        "amount": $('input[name="transfer_amount"]').val(),
        "notes": $('input[name="transfer_notes"]').val(),
    }
    console.log(data)
    $.ajax({
        url: 'http://localhost:9090/api/transferProccess',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            $('#transfer-phone-err').text("")
            $('#transfer-amount-err').text("")
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
                $('#transfer-amount-err').text(response.amount ?? "")
                $('#transfer-phone-err').text(response.phone ?? "")
            }
        },
        error: function(data){
                console.log(data)
                if(data.responseJSON != null){
                    $('#transfer-phone-err').text(data.responseJSON.phone ?? "")
                    $('#transfer-amount-err').text(data.responseJSON.amount ?? "")
                } else {

                }
        }
    })
})

//search by phone
let receiverName = 'ABC'
$('input[name="transfer_phone"]').on('input', () => {
    $('#transfer-phone-err').text("")
    $('.btn-transfer').closest('.row').children(".div-found").remove()
    let phone = $('input[name="transfer_phone"]').val()
    if(phone == "") return
    if(phone.match("^[0][3|5|7|8|9][0-9]{8}$")){
        $.get('/api/getUserByPhone', {"phone":phone}, (response) => {
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
            $('#transfer-phone-err').text("Not found")
        })
    }
})
