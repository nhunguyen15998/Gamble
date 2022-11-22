$('#btn-change-password-save').on('click', () => {
    $('#old_password_err').text("")
    $('#new_password_err').text("")
    $('#confirm_new_password_err').text("")
    let div = document.createElement('div')
    let target = document.querySelector('#changePassword')

    let data = {
        "old_password": $('input[name="old_password"]').val(),
        "new_password": $('input[name="new_password"]').val(),
        "confirm_new_password": $('input[name="confirm_new_password"]').val()
    }
    console.log(data)
    $.ajax({
        url: '/user/change-password',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            $('#old_password_err').text("")
            $('#new_password_err').text("")
            $('#confirm_new_password_err').text("")
            let alert = `<div class="alert alert-success my-3" role="alert">
                            Successfully changed password!
                        </div>`
            if(response.code != 200){
                alert = `<div class="alert alert-danger my-3" role="alert">
                            ${response.message}
                        </div>`
            }
            div.innerHTML += alert
            target.parentNode.insertBefore(div, target)
            $('input[name="old_password"]').val(""),
            $('input[name="new_password"]').val(""),
            $('input[name="confirm_new_password"]').val("")
            setTimeout(() => {
                $(target.parentNode.children[1]).fadeOut(1000)
                target.parentNode.children[1].remove()
            }, 3000)
            if(response.code == 200){
                setTimeout(() => {
                    window.location.href = response.page
                }, 2000)
            }
        }, 
        error: function(data){
            console.log(data)
            data.responseJSON.errors.forEach(err => {
                $(`#${err.field}_err`).text(err.defaultMessage ?? "")
            })
            if(data.responseJSON == null){  
                let alert = `<div class="alert alert-danger mb-5" role="alert">
                                Whoops! Something went wrong!
                            </div>`
                div.innerHTML += alert
                target.parentNode.insertBefore(div, target)
            }
        }
    })
})

$('#btn-change-password-cancel').on('click', () => {
    $('#old_password_err').text("")
    $('#new_password_err').text("")
    $('#confirm_new_password_err').text("")
})