// 行追加
// ここで追加された行を削除すると、ぎょその行の要素だけ消える（保存されていない行だけ .deleteRow が付いている）
$(function() {
    $('#add_row_fare_master').click(function() {
      $('#fare_masters').append(`
      <tr class="fare_master_record">
        <td>
          <button class="deleteRow btn btn-danger" type="button">削除</button>
        </td>

        <input name="fare-master[][fare_master_id]" type="hidden">
        <td>
          <input class="form-control form-control-sm input-group" name="fare-master[][purpose]" type="text">
        </td>
        
        <td>
          <input class="form-control form-control-sm input-group" name="fare-master[][transportation]" type="text">
        </td>
        
        <td>
          <input class="form-control form-control-sm input-group" name="fare-master[][departure]" type="text">
        </td>
        
        <td>
          <input class="form-control form-control-sm input-group" name="fare-master[][arrival]" type="text">
        </td>
        
        <td>
          <input class="round_trip" type="checkbox">
        </td>
        <input class="round_trip_value" name="fare-master[][round_trip]" type="hidden" value="0">

        <td>
          <input class="fare_master form-control form-control-sm input-group" name="fare-master[][fare]" type="text">
        </td>
      </tr>  
      `);
    });
  });

  
//   片/往チェック
$('.round_trip').change(function(){
  
    if($(this).closest('.round_trip').prop('checked')){
      // チェックされた場合は往復
      $(this).closest('.fare_master_record').find('.round_trip_value').val('1');
     }else{
      // チェックが外れた場合は片道
      $(this).closest('.fare_master_record').find('.round_trip_value').val('0');
     } 
});