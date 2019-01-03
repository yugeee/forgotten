// 行追加
// ここで追加された行を削除すると、その行の要素だけ消える（保存されていない行だけ .deleteRow が付いている）
$(function() {
    $('#add_row_fare').click(function() {

      var options = '';
      $("#fare_masters .fare_master").each(function (index, element) {
        var fare_master_id = $(element).find('.fare_master_id').val();
        var purpose = $(element).find('.purpose').val();
        options += `<option class="fare_master" value="${fare_master_id}">${purpose}</option>`;
      });

      $("#fare_masters .fare_master").each(function () {
        
      });

      $('#fares').append(`
      <tr class="fare_record">

        <input name="fare[][fare_id]" type="hidden">

        <td>
          <button class="deleteRow btn btn-danger" type="button">削除</button>
        </td>

        <td>
          <select name="fare_master_select">
            <option class="fare_master" selected="" value="">勤怠マスタを選択</option>
            ${options}
          </select>
        </td>

        <td>
          <input class="date form-control form-control-sm input-group" name="fare[][date]" type="date">
        </td>
        
        <td>
          <input class="purpose form-control form-control-sm input-group" name="fare[][purpose]" type="text">
        </td>
        
        <td>
          <input class="transportation form-control form-control-sm input-group" name="fare[][transportation]" type="text">
        </td>
        
        <td>
          <input class="departure form-control form-control-sm input-group" name="fare[][departure]" type="text">
        </td>
        
        <td>
          <input class="arrival form-control form-control-sm input-group" name="fare[][arrival]" type="text">
        </td>
        
        <td>
          <input class="round_trip" type="checkbox">
        </td>
        <input class="round_trip-value" name="fare[][round_trip]" type="hidden" value="0">

        <td>
          <input class="fare form-control form-control-sm input-group" name="fare[][fare]" type="text">
        </td>
      </tr>  
      `);
    });
  });
    
  // 行削除
  $(document).on('click', '.deleteRow', function () {
    
    $(this).closest('tr').remove();

    var fare_sum = 0;
   
    $("#fares .fare").each(function () {
        var fare = $(this).val();
        if ($.isNumeric(fare)) {
           fare_sum += parseFloat(fare);
        }                  
    });
    $("#sum").html(fare_sum);

  });

//   片/往チェック
$(document).on('change', '.round_trip', function(){
  
    if($(this).closest('.round_trip').prop('checked')){
      // チェックされた場合は往復
      $(this).closest('.fare_record').find('.round_trip_value').val('1');
     }else{
      // チェックが外れた場合は片道
      $(this).closest('.fare_record').find('.round_trip_value').val('0');
     } 
});

//  総額の計算
$("#fares").on('input', '.fare', function () {
    var fare_sum = sumFare;
    $("#sum").html(fare_sum);
});


$(document).on('change', '[name=fare_master_select]', function(){
  // 交通費マスタID
  var selected_fare_master_id = $(this).val();

  var fare_master = $(`input[value="${selected_fare_master_id}"]`).closest('.fare_master');

  // 交通費マスタ取得
  var purpose        = fare_master.find('.purpose').val();
  var transportation = fare_master.find('.transportation').val();
  var departure      = fare_master.find('.departure').val();
  var arrival        = fare_master.find('.arrival').val();
  var round_trip     = fare_master.find('.round_trip').val();
  var fare           = fare_master.find('.fare').val();

  var fare_input = $(this).closest('.fare_record');
  // 交通費マスタ書き込み
  fare_input.find('.purpose').val(purpose);
  fare_input.find('.transportation').val(transportation);
  fare_input.find('.departure').val(departure);
  fare_input.find('.arrival').val(arrival);
  if(round_trip == 1){
    fare_input.find('.round_trip').prop('checked', true);
  }else{
    fare_input.find('.round_trip').prop('checked', false);
  }
  fare_input.find('.round_trip_value').val(round_trip);
  fare_input.find('.fare').val(fare);

  // 交通費計算
  var fare_sum = sumFare();
  $("#sum").html(fare_sum);
});


/**
 * 交通費計算
 */
function sumFare(arr){

  var fare_sum = 0;

  $("#fares .fare").each(function () {
    var fare = $(this).val();
    if ($.isNumeric(fare)) {
       fare_sum += parseFloat(fare);
    }                  
  });

  return fare_sum;
}