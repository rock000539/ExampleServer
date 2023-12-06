$(function(){

  'use strict'
 
  const asideBody = new PerfectScrollbar('.aside-body', {
    suppressScrollX: true
  });

  if($('.aside-backdrop').length === 0) {
    $('body').append('<div class="aside-backdrop"></div>');
  }
 
  $(document).on('click', '.aside-menu-link', function(e){
    e.preventDefault()
 
    if(window.matchMedia('(max-width: 1199.9999px)').matches) {
        $('body').toggleClass('show-aside'); 
      } else {
        $('body').removeClass('show-aside');  
    }

    if(window.matchMedia('(min-width: 1200px)').matches) {
        $('body').toggleClass('hide-aside'); 
      } else {
        $('body').removeClass('hide-aside');  
    }

    asideBody.update()
  })

  $(document).on('click', '.nav-aside .with-sub .nav-link', function(e){
    e.preventDefault();

    $(this).parent().siblings().removeClass('show');
    $(this).parent().toggleClass('show');

    asideBody.update()
  })
 

  $(document).on('click', '.aside-backdrop', function(e){
    $('body').removeClass('show-aside');
  })
})
