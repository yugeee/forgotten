//  年・月を取得して遷移
$('#fare_list').on('click', function(){

  var uri = $('option:selected').val();
  location.href = uri;
    
});