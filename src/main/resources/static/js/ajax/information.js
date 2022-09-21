//click about tab
$('#btn-about').on('click', () => {
    $.ajax({
        url: '/getUser',
        type: 'get',
        success: function(response){
            $('input[name="id"]').val(response.id)
            $('input[name="user_id"]').val(response.user_profile_id)
            $('input[name="first_name"]').val(response.first_name)
            $('input[name="last_name"]').val(response.last_name)
            $('input[name="phone"]').val(response.phone)
            $('input[name="thumbnail"]').val(response.thumbnail)
            $('input[name="email"]').val(response.email)
            $('input[name="birth"]').val(response.birth.substring(0, 10))
            switch(response.gender){
                case 0:
                    $('input[id="male"]').prop('checked', true)
                    break
                case 1:
                    $('input[id="female"]').prop('checked', true)
                    break
                case 2: 
                    $('input[id="other"]').prop('checked', true)
                    break
            }
            if(response.thumbnail != null){
                $('input[name="thumbnail"]').val(response.thumbnail)
            }
        },
        error: function(data){
            
        }
    })
})

let div = document.createElement('div')
let target = document.querySelector('#about-form')
//update profile
$('#btn-profile-save').on('click', () => {
    let data = {
        "first_name": $('input[name="first_name"]').val(),
        "last_name": $('input[name="last_name"]').val(),
        "email": $('input[name="email"]').val(),
        "birth": $('input[name="birth"]').val(),
        "gender": $('input[name="gender"]:checked').val()
    }
    $.ajax({
        url: '/updateUser',
        type: 'post',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(response){
            $('#first_name_err').text("")
            $('#last_name_err').text("")
            $('#email_err').text("")
            $('.user-name').text(response.name)
            let alert = `<div class="alert alert-success mb-5" role="alert">
                            Successfully updated!
                        </div>`
            div.innerHTML += alert
            target.parentNode.insertBefore(div, target)
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

//update profile ava
$('#upload-avatar').on('change', () => {
    $('#upload-modal').modal('show')
})

function closeUploadModal(){
    $('#upload-modal').modal('hide')
    document.getElementsByClassName('modal-backdrop')[0].remove()
    document.body.classList.remove('modal-open')
}

function uploadModal(){
    var file = $('#upload-avatar')[0].files[0]
    var formData = new FormData()
    formData.append('photo', file)
    $.ajax({
        url: '/changeProfilePhoto',
        type: 'post',
        data: formData,
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        success: function(response){
            console.log(response)
            $('.thumbnails').attr('src', $('#img-avatar').attr('src'))
            let alert = `<div class="alert alert-success mb-5" role="alert">
                            Successfully updated profile photo!
                        </div>`
            div.innerHTML += alert
            target.parentNode.insertBefore(div, target)
        },
        error: function(data){
            console.log(data)
        }
    })
    closeUploadModal()
}