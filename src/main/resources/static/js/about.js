// change pw
$('#change-password-div').addClass('d-none')
$('#change-password-div').removeClass('d-flex')

if($('#change-password').is(':checked')){
    $('#change-password-div').removeClass('d-none')
    $('#change-password-div').addClass('d-flex')
} else {
    $('#change-password-div').addClass('d-none')
    $('#change-password-div').removeClass('d-flex')
}

$('#change-password').on('change', () => {
    if($('#change-password').is(':checked')){
        $('#change-password-div').removeClass('d-none')
        $('#change-password-div').addClass('d-flex')
    } else {
        $('#change-password-div').addClass('d-none')
        $('#change-password-div').removeClass('d-flex')
    }
})

// upload
$('#upload-avatar').on('change', function(event){
    console.log($('#upload-avatar').val().replace("C:\\fakepath\\",""))
    readFile(event)
})

function readFile(event){
    var files = event.target.files
    if(files.length != 0){
        console.log(files[0]) //files[0] == $('#formFile')[0].files[0]
        var reader = new FileReader()
        reader.onload = (e) => {
            document.getElementById('img-avatar').src = e.target.result
        }
        reader.readAsDataURL(files[0])
    }
}

//information
$('#a-settings').on('click', () => {
    if(!$('.settings-pills').hasClass('show')){
        $('.nav-link').removeClass('active')
        $('#a-settings').addClass('active')
        $('#settings-menu').children('a:first-child').addClass('active')
    } else {
        $('#a-settings').css('background-color', 'transparent')
        $('#settings-menu').children('a:first-child').removeClass('active')
    }
    //active show
    $('#about-tab').removeClass("active show")
    $('#settings-tab').addClass("active show")
    getClientConfig()
    //require password
    SECTION = 1
    PATH = '/user/configs/updateWithPassword'
    AREA_ID = '#setting-area'
})

function getClientConfig(){
    $.get('/user/configs', (response) => {
        if(response.code == 200){
            let config = JSON.parse(response.configs)
            $('#change-password-switch').prop('checked', config.change_password == 1 ? true : false)
            $('#withdraw-password-switch').prop('checked', config.withdraw_password == 1 ? true : false)
            $('#transfer-password-switch').prop('checked', config.transfer_password == 1 ? true : false)
            $('#setting-password-switch').prop('checked', config.setting_password == 1 ? true : false)
        } else {
            console.log(response.message)
        }
    }).fail((data) => {
        console.log(data)
    })
}