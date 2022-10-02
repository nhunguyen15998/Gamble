//auth
$('#wheel-play').on('click', () => {
    $.post('/games/wheels/authentication', (response) => {
        console.log(response)
        if(response.code == 200){
            window.location.href = response.message
        } else {
            document.querySelector('#warning-modal p').innerHTML = response.message
            $('#warning-modal').modal('show')
        }
    }).fail((data) => {
        console.log(data)
    })
})