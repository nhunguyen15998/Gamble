$(function ($) {
    "use strict";

    jQuery(document).ready(function () {


        //   magnific popup activation
        $('.play-video').magnificPopup({
            type: 'video'
        });

        $('.img-popup').magnificPopup({
            type: 'image'
        });

        // $('#faqModal').modal('show');

        // Product deal countdown
        $('[data-countdown]').each(function () {
            var $this = $(this),
                finalDate = $(this).data('countdown');
            $this.countdown(finalDate, function (event) {
                $this.html(event.strftime('<span>%D:</span><span>%H:</span><span>%M:</span><span>%S</span>'));
            });
        });
        $('[data-countdown2]').each(function () {
            var $this = $(this),
                finalDate = $(this).data('countdown2');
            $this.countdown(finalDate, function (event) {
                $this.html(event.strftime('<p>%D : <span>Days</span></p> <p>%H : <span>Hours</span></p> <p>%M : <span>Minutes</span></p> <p>%S <span>Seconds</span></p>'));
            });
        });


        // Latest Winner Slider
        var $l_winner_slider = $('.l-winner-slider2');
        $l_winner_slider.owlCarousel({
            loop: true,
            navText: ['<i class="fa fa-angle-left"></i>', '<i class="fa fa-angle-right"></i>'],
            nav: true,
            dots: false,
            autoplay: false,
            margin: 30,
            autoplayTimeout: 6000,
            smartSpeed: 1000,
            responsive: {
                0: {
                    items: 2
                },
                415: {
                    items: 4
                },
                576: {
                    items: 4
                },
                768: {
                    items: 5
                },
                992: {
                    items: 7
                },
                1200: {
                    items: 9
                },
                1920: {
                    items: 9
                }
            }
        });

        // Latest Winner Slider
        var $l_winner_slider2 = $('.l-winner-slider');
        $l_winner_slider2.owlCarousel({
            loop: true,
            navText: ['<i class="fa fa-angle-left"></i>', '<i class="fa fa-angle-right"></i>'],
            nav: true,
            dots: false,
            autoplay: false,
            margin: 30,
            autoplayTimeout: 6000,
            smartSpeed: 1000,
            responsive: {
                0: {
                    items: 3
                },
                576: {
                    items: 4
                },
                768: {
                    items: 4
                },
                992: {
                    items: 3
                },
                1200: {
                    items: 3
                },
                1920: {
                    items: 3
                }
            }
        });

        // Exclusive Lottery Slider
        var $ex_lottery_slider = $('.ex-lottery-slider');
        $ex_lottery_slider.owlCarousel({
            loop: true,
            navText: ['<i class="fa fa-angle-left"></i>', '<i class="fa fa-angle-right"></i>'],
            nav: true,
            dots: false,
            autoplay: false,
            margin: 30,
            autoplayTimeout: 6000,
            smartSpeed: 1000,
            responsive: {
                0: {
                    items: 1
                },
                500: {
                    items: 1
                },
                768: {
                    items: 2
                },
                992: {
                    items: 3
                },
                1200: {
                    items: 3
                },
                1920: {
                    items: 3
                }
            }
        });

        /** Product Details  carousel **/
        var $product_slider = $('.all-slider');
        $product_slider.owlCarousel({
            loop: false,
            dots: false,
            nav: true,
            navText: ["<i class='fa fa-angle-left'></i>", "<i class='fa fa-angle-right'></i>"],
            margin: 0,
            autoplay: false,
            items: 4,
            margin: 30,
            autoplayTimeout: 6000,
            smartSpeed: 1000,
            responsive: {
                0: {
                    items: 3
                },
                768: {
                    items: 4
                }
            }
        });

        // payment Slider
        var $method_slider = $('.method-slider');
        $method_slider.owlCarousel({
            loop: true,
            navText: ['<i class="fa fa-angle-left"></i>', '<i class="fa fa-angle-right"></i>'],
            nav: true,
            dots: false,
            autoplay: false,
            margin: 0,
            autoplayTimeout: 6000,
            smartSpeed: 1000,
            responsive: {
                0: {
                    items: 2
                },
                500: {
                    items: 3
                },
                768: {
                    items: 5
                },
                992: {
                    items: 6
                },
                1200: {
                    items: 7
                },
                1920: {
                    items: 7
                }
            }
        });

        // testimonial-slider
        var $testimonial_slider = $('.testimonial-slider');
        $testimonial_slider.owlCarousel({
            loop: true,
            navText: ['<i class="fa fa-angle-left"></i>', '<i class="fa fa-angle-right"></i>'],
            nav: true,
            dots: false,
            autoplay: false,
            margin: 30,
            autoplayTimeout: 6000,
            smartSpeed: 1000,
            responsive: {
                0: {
                    items: 1
                },
                768: {
                    items: 1
                },
                960: {
                    items: 1
                },
                1200: {
                    items: 1
                }
            }
        });


    });


    /*-------------------------------
        back to top
    ------------------------------*/
    $(document).on('click', '.bottomtotop', function () {
        $("html,body").animate({
            scrollTop: 0
        }, 2000);
    });

    //define variable for store last scrolltop
    var lastScrollTop = '';
    $(window).on('scroll', function () {
        var $window = $(window);
        if ($window.scrollTop() > 0) {
            $(".header").addClass('nav-fixed');
        } else {
            $(".header").removeClass('nav-fixed');
        }

        /*---------------------------
            back to top show / hide
        ---------------------------*/
        var st = $(this).scrollTop();
        var ScrollTop = $('.bottomtotop');
        if ($(window).scrollTop() > 1000) {
            ScrollTop.fadeIn(1000);
        } else {
            ScrollTop.fadeOut(1000);
        }
        lastScrollTop = st;

    });

    $(window).on('load', function () {

        /*---------------------
            Preloader
        -----------------------*/
        var preLoder = $("#preloader");
        preLoder.addClass('hide');
        var backtoTop = $('.back-to-top');
        /*-----------------------------
            back to top
        -----------------------------*/
        var backtoTop = $('.bottomtotop');
        backtoTop.fadeOut(100);

    });

});

/* alert */
const alertSuccess = 'success'
const alertDanger = 'danger'

let alertMsg = (msg, type, target, area) => {
    let settingAlertDiv = document.createElement('div')
    settingAlertDiv.classList.add(area)
    let settingTarget = document.querySelector(target)
    let alert = `<div class="alert alert-${type}" role="alert">
                    ${msg}
                </div>`
    settingAlertDiv.innerHTML += alert
    settingTarget.parentNode.insertBefore(settingAlertDiv, settingTarget)
}
/* end alert */
/* require password modal */
let DATA 
let PATH 
let AREA_ID 
let SECTION
const ALERT_AREA = 'alert-area'
/* end require password modal */
/* pagination */
//create pagination
function createPagination(ulClass, size){
    let targetLi = document.querySelector(ulClass)
    for(let i = size; i >= 1; i--){
        let li = document.createElement('li')
        li.classList.add('page-item')
        let a = document.createElement('a')
        a.classList.add('page-link')
        if(i == 1){
            a.classList.add('active')
        }
        a.innerText = i
        li.appendChild(a)
        targetLi.parentNode.insertBefore(li, targetLi.nextSibling)
    }
    $('.page-link').on('click', (e) => {
        onPageBtnClick(e)
    })
}
/* end pagination */

/* profile nav */
// $('.left-area-item').click(function(){
//     localStorage.setItem('active-item', $(this).attr('class').split(' ')[1]);
// })
// $(window).on('load', function () {
//     var ele = localStorage.getItem('active-item');
//     if(ele==null)
//         $('.left-area-item').first().addClass('active'); 
//     else 
//         $('.'+ele).addClass("active");
       
//     localStorage.removeItem('active-item');
  
// })
/* end profile nav */