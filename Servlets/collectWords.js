var accordian = $('#accordion');
var sub = $('#accordion>div');
var subHead = $('#accordion h3');
var subDetail = $('#accordion .detail');
var speed = 300;

subHead.click(function(){
  var active = $('#accordion .detail:visible');
  var activeIndex = active.parent().index();  
  var index = $(this).parent().index();
 
  sub.each(function(){
    if (activeIndex !== index) {
      if ($(this).index() === index){
        $(this).find('.detail').stop().slideDown(speed).parent().addClass('active');
      }
      else {
        $(this).find('.detail').stop().slideUp(speed, function(){
          $(this).parent().removeClass('active');
        });   
      }
    } else {
      $(this).find('.detail').stop().slideUp(speed, function(){
        $(this).parent().removeClass('active');
      });
    }
  });
});