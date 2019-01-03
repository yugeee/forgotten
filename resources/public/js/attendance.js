//  休日チェック
$('.holiday').change(function(){
  
  if($(this).closest('.holiday').prop('checked')){
    // チェックされた場合は休日
    $(this).closest('.attendance').addClass('table-secondary');
    $(this).closest('.attendance').find('.holiday-value').val('1');

    // input を空にしてreadonlyにする
    $(this).closest('.attendance').find('.start-time').val('00:00').attr('readonly',true);
    $(this).closest('.attendance').find('.end-time').val('00:00').attr('readonly',true);
    $(this).closest('.attendance').find('.rest-time').val('00:00').attr('readonly',true);
    $(this).closest('.attendance').find('.work-time').val('00:00').attr('readonly',true);
   }else{
    // チェックが外れた場合は平日
    $(this).closest('.attendance').removeClass('table-secondary');
    $(this).closest('.attendance').find('.holiday-value').val('0');

    // input を入力可能にする
    $(this).closest('.attendance').find('.start-time').val('00:00').attr('readonly',false);
    $(this).closest('.attendance').find('.end-time').val('00:00').attr('readonly',false);
    $(this).closest('.attendance').find('.rest-time').val('00:00').attr('readonly',false);
    $(this).closest('.attendance').find('.work-time').val('00:00').attr('readonly',false);
   }

   // 勤務時間の算出
  var work_time_sum = sumWorkTime();
  $('#work_time_sum').html(work_time_sum);
  
});


//   勤務時間計算
$('.time').change(function(){
  
  // DOMから値を取得
  var start_time_var = $(this).closest('.attendance').find('.start-time').val();
  var end_time_var   = $(this).closest('.attendance').find('.end-time').val();
  var rest_time_var  = $(this).closest('.attendance').find('.rest-time').val();

  // :区切りで配列にする ex "9:00" -> ["09", "00"]
  var start_time_arr = start_time_var.split(':');
  var end_time_arr   = end_time_var.split(':');
  var rest_time_arr  = rest_time_var.split(':');

  // 秒数に変換
  var start_time_sec = getSecond(start_time_arr);
  var end_time_sec   = getSecond(end_time_arr);
  var rest_time_sec  = getSecond(rest_time_arr);

  // 秒数で計算
  var work_time_sec = end_time_sec - start_time_sec - rest_time_sec;

  // 時間は小数点以下で切り捨てて除算
  var work_time_hour_tmp = Math.floor(work_time_sec / 3600);
  // 分は時間で割り切れなかった分を除算
  var work_time_minuit_tmp = (work_time_sec - (work_time_hour_tmp * 3600)) / 60;

  // 0埋め
  var work_time_hour = pudding00(work_time_hour_tmp);
  var work_time_minuit = pudding00(work_time_minuit_tmp);

  // 最終的にこれが入る
  var work_time = work_time_hour + ':' + work_time_minuit;

  $(this).closest('.attendance').find('.work-time').val(work_time);

  // 勤務時間の算出
  var work_time_sum = sumWorkTime();
  $('#work_time_sum').html(work_time_sum);
});


/**
 * 時間の配列を受け取って秒を返す
 * ex [09,00] -> 32400 
 */
function getSecond(arr){

  if(arr){
    // 時間・分を秒に変換
    var hour = parseInt(arr[0], 10) * 60 * 60;
    var minuit = parseInt(arr[1], 10) * 60;
    return hour + minuit;
  }
}


/**
 * 数字を受け取って2桁の0埋めを行う
 * ex 9 -> 09
 */
function pudding00(num){
  return ('00' + num).slice(-2);
}

//   勤務時間計算
function sumWorkTime(){
  // DOMから値を取得
  var work_time_sum_sec = 0;

  $("#attendances .work-time").each(function () {
    var work_time_val = $(this).val();
    var work_time = work_time_val.split(':');
    work_time_sum_sec += getSecond(work_time);
  });

  // 時間は小数点以下で切り捨てて除算
  var work_time_hour = Math.floor(work_time_sum_sec / 3600);
  // 分は時間で割り切れなかった分を除算
  var work_time_minuit_tmp = (work_time_sum_sec - (work_time_hour * 3600)) / 60;
  
  // 0埋め
  var work_time_minuit = pudding00(work_time_minuit_tmp);
  
  // 最終的にこれが入る
  var work_time_sum = work_time_hour + '：' + work_time_minuit;

  return work_time_sum;
}

/**
 * 時間の配列を受け取って秒を返す
 * ex [09,00] -> 32400 
 */
function getSecond(arr){

  if(arr){
    // 時間・分を秒に変換
    var hour = parseInt(arr[0], 10) * 60 * 60;
    var minuit = parseInt(arr[1], 10) * 60;
    return hour + minuit;
  }
}