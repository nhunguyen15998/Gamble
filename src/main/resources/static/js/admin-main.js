$(function ($) {
    $('.user-type').click(function(){
        localStorage.setItem('active-type', $(this).attr('class'));
    });

    $(window).on('load', function () {
        var type = localStorage.getItem('active-type');
        
        $('.user-type-1').removeClass('active');
        $('.user-type-1').removeClass('text-dark');
        $('.user-type-2').removeClass('active');
        $('.user-type-2').removeClass('text-dark');
        if(type==null) $('.user-type-1').addClass('active')
        else{
            $(type.includes('user-type-1')==true?'.user-type-1':'.user-type-2').addClass('active');
            $(type.includes('user-type-1')==false?'.user-type-1':'.user-type-2').addClass('text-dark');
            localStorage.removeItem('active-type');

        }
    });
});