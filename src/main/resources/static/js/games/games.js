//auth
function openGame(url){
    $.post(url, (response) => {
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
}

$('#wheel-play').on('click', () => {
    openGame('/games/wheels/authentication')
})
$('#baucua-play').on('click', () => {
    openGame('/games/baucua/authentication')
})