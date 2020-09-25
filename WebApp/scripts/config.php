<?php

$post_data = $_POST['data'];
if (!empty($post_data)) {
    file_put_contents('config.json', $post_data);
    echo json_encode($post_data);
}
else {
	echo 'fail';
}
?>
