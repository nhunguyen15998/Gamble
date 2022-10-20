$('#blog-cate-select').on('change', () => {
    currentPage = 1
    $.get('/articles', {"data":JSON.stringify(getBlogData())}, (response) => {
        console.log(response)
        let cateSlug= ''
        $('#blog-section').empty()
        //clear pagination
        clearPagination()
        if(response[0].code == 404 || $('#blog-cate-select').val()==-1){
            let div = '<div class="col-12 text-center mb-5"><p>Not found</p></div>'
            $('#blog-section').append(div)
            cateSlug = response[0].cateSlug
        } else {
            response.forEach(blog => {
                if(blog.total != null){
                    $('.pagination-article').attr('total-pages', blog.total)
                    totalPages = parseInt(blog.total)
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
            })
            //redraw pagination
            redrawPagination(totalPages)
        }
        let url = window.location.href
        url = url.substring(0, url.lastIndexOf("/"))
        window.history.pushState({}, null, url + `/${cateSlug}?page=1`)
    }).fail((data) => {
        console.log(data)
    })
})