const lis = [...document.querySelectorAll('.pagination-article > li')]
let lisChanged = [...document.querySelectorAll('.pagination-article > li:not(:first-child):not(:last-child)')]
let size = lisChanged.length

function onPageBtnClick(e){
    var clicked = e.target.innerHTML
    var previous = $(lis[0]).children().html()
    var next = $(lis[lis.length-1]).children().html()
    if(clicked != previous && clicked != next){
        document.querySelector('.pagination-article .active').classList.remove('active')
        e.target.classList.add('active')
        currentPage = parseInt(clicked)
        updatePageByNumber()
    }
}

function updatePageByNumber(){
    let url = window.location.href
    url = url.substring(0, url.lastIndexOf("?"))
    window.history.pushState({}, null, url + `?page=${currentPage}`)
    let data = getBlogData()
    $.get('/articles', {"data":JSON.stringify(data)}, (response) => {
        console.log(response)
        $('#blog-section').empty()
        response.forEach(blog => {
            if(blog.total != null){
                $('.pagination-article').attr('total-pages', blog.total)
            } else {
                cateSlug = blog.cate_slug
                let blogs = `<div class="col-xl-4 col-lg-4 col-md-6">
                                <div>
                                    <a class="card single-bonus" href="/articles/detail/${cateSlug}/${blog.blog_slug}-${blog.blog_id}">
                                        <div class="card-body">
                                            <h4 class="card-title">${blog.title}</h4>
                                        </div>
                                    </a>
                                </div>
                            </div>`
                $('#blog-section').append(blogs)
            }
        });
    }).fail((data) => {
        console.log(data)
    })
}

function getOffset(){
    return currentPage*itemPerPage - itemPerPage
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
            $('.page-link').off("click")
            $('.page-link').on('click', (e) => {
                onPageBtnClick(e)
            })
        }
        lisChanged[size-1].childNodes[0].classList.add('active')
    } else {
        let num = parseInt(lisChanged[0].innerText)
        lisChanged[currentPage - 1 - num].childNodes[0].classList.add('active')
    }
    currentPage -= 1
    updatePageByNumber()
}

function onNext(){
    console.log("currentPage: "+currentPage)
    if(currentPage >= totalPages){
        return
    }
    document.querySelector('.pagination-article .active').classList.remove('active')
    console.log("currentPage % size: "+size)
    if(currentPage % size == 0){
        let remainPages = totalPages - currentPage
        for(let i = 1; i <= size; i++){
            let a =  `<a class="page-link">${currentPage+i}</a>`
            if(remainPages < size && i > remainPages){
                a = `<a class="page-link d-none">${currentPage+i}</a>`
            }
            lisChanged[i-1].innerHTML = a
            $('.page-link').off("click")
            $('.page-link').on('click', (e) => {
                onPageBtnClick(e)
            })
        }
        lisChanged[0].childNodes[0].classList.add('active')
    } else {
        let num = parseInt(lisChanged[0].innerText)
        lisChanged[currentPage + 1 - num].childNodes[0].classList.add('active')
        console.log("num: "+num)
    }
    currentPage += 1
    updatePageByNumber()
}   
        
//clear pagination
function clearPagination(){
    lisChanged = [...document.querySelectorAll('.pagination-article > li:not(:first-child):not(:last-child)')]
    size = lisChanged.length
    for (let i = 0; i < lisChanged.length; i++) {
        lisChanged[i].remove()
    }
    $('.pagination-article').parent().addClass('d-none')
}

//redraw pagination
function redrawPagination(totalPages){
    if($('.pagination-article').parent().hasClass('d-none')){
        $('.pagination-article').parent().removeClass('d-none')
    }
    size = totalPages <= 1 ? totalPages : 2
    
    createPagination('.pagination-article .page-item:first-child', size)
    lisChanged = [...document.querySelectorAll('.pagination-article > li:not(:first-child):not(:last-child)')]
}

//on reload page update current selected page
function onReloadUpdateSelectedPage(){
    let arrayPages = []
    for(let i = 0; i < lisChanged.length; i++){
        let txt = $(lisChanged[i]).children().context.innerText
        arrayPages.push(txt)
    }
    let indx = arrayPages.findIndex(e => e == `${currentPage}`)
    if(indx >= 0){
        document.querySelector('.pagination-article .active').classList.remove('active')
        $(lisChanged[indx]).children().addClass('active')
    } else {
        let index = currentPage
        while(index > size && index > 0){
            index = index - size
        }
        for(let k = size; k >= 1; k--){
            let txt = $(lisChanged[k - 1]).children().context
            txt.innerHTML = `<a class="page-link">${currentPage+k-index}</a>`
            if(k == index){
                $(lisChanged[k - 1]).children().addClass('active')
            }
            $('.page-link').off("click")
            $('.page-link').on('click', (e) => {
                onPageBtnClick(e)
            })
        }
    }
}
if(totalPages > 0){
    onReloadUpdateSelectedPage()
}