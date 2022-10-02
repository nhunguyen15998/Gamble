const lis = [...document.querySelectorAll('.pagination-article > li')]
const lisChanged = [...document.querySelectorAll('.pagination-article > li:not(:first-child):not(:last-child)')]
let currentPage = 1
const size = lisChanged.length

$('.page-link').on('click', (e) => {
    onPageBtnClick(e)
})

function onPageBtnClick(e){
    var clicked = e.target.innerHTML
    var previous = $(lis[0]).children().html()
    var next = $(lis[lis.length-1]).children().html()
    if(clicked != previous && clicked != next){
        document.querySelector('.pagination-article .active').classList.remove('active')
        e.target.classList.add('active')
        currentPage = parseInt(clicked)
    }
}

function onPrevious(){
    if(currentPage <= 1){
        return
    }
    document.querySelector('.pagination-article .active').classList.remove('active')
    if((currentPage-1) % size == 0){
        for(let i = size; i >= 1; i--){
            let a = `<a class="page-link">${currentPage-i}</a>`
            lisChanged[size-i].innerHTML = a
            $('.page-link').on('click', (e) => {
                onPageBtnClick(e)
            })
        }
        lisChanged[size-1].childNodes[0].classList.add('active')
        currentPage -= 1
    } else {
        let num = parseInt(lisChanged[0].innerText)
        lisChanged[currentPage - 1 - num].childNodes[0].classList.add('active')
        currentPage -= 1
    }
}

function onNext(){
    if(currentPage >= totalPages){
        return
    }
    document.querySelector('.pagination-article .active').classList.remove('active')
    if(currentPage % size == 0){
        let remainPages = totalPages - currentPage
        for(let i = 1; i <= size; i++){
            let a =  `<a class="page-link">${currentPage+i}</a>`
            if(remainPages < size && i > remainPages){
                a = `<a class="page-link d-none">${currentPage+i}</a>`
            }
            lisChanged[i-1].innerHTML = a
            $('.page-link').on('click', (e) => {
                onPageBtnClick(e)
            })
        }
        lisChanged[0].childNodes[0].classList.add('active')
        currentPage += 1
    } else {
        let num = parseInt(lisChanged[0].innerText)
        lisChanged[currentPage + 1 - num].childNodes[0].classList.add('active')
        currentPage += 1
    }
}   
        