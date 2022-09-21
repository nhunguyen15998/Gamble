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
    //active show
    $('#about-tab').removeClass("active show")
    $('#settings-tab').addClass("active show")
})
