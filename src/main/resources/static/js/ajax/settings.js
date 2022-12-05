$('#btn-save-settings').on('click', () => {
    // let count = parseInt($('#settings-form').attr('data-items'))
    // for (let index = 1; index <= count; index++) {
    //     console.log($(`input[name="currency${index}"]`).val())
    // }
    let settings = {
        "vndID" : $('input[name="currency1"]').attr('data-id'),
        "vnd" : $(`input[name="currency1"]`).val(),
        "btcID" : $('input[name="currency2"]').attr('data-id'),
        "btc" : $(`input[name="currency2"]`).val()
    }

    let div = document.createElement('div')
    let target = document.querySelector('#settings-form')

    $.post("/admin/settings/update", {"settings" : JSON.stringify(settings)}, (response) => {
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
        setTimeout(() => {
            $(target.parentNode.children[0]).fadeOut(1000)
            target.parentNode.children[0].remove()
        }, 3000)
    }).fail((data) => {
        console.log(data)
    })
})