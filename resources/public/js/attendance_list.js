//  年・月を取得して遷移
$('#attendance_list').on('click', function(){

  var uri = $('option:selected').val();
  location.href = uri;
    
});