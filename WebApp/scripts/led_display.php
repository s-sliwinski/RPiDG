<?php


$post_data = $_POST['data'];
if (!empty($post_data)) {
    file_put_contents('led_display.json', $post_data);
    echo json_encode($post_data);
}
else {
	echo 'fail';
}

echo"ACK1";

exec ("sudo ./led_display.py");

echo " ACK2";

?>
