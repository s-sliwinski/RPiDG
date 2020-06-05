<?php
//if ($_SERVER["REQUEST_METHOD"] == "POST"){

$json = file_get_contents("php://input");
$data = json_decode($json);

if(!empty($data))
{
$arrSize = sizeof($data);

for($i=0; $i < $arrSize; $i++) {
$x = $data[$i]->x;
$y = $data[$i]->y;
$r = $data[$i]->r;
$g = $data[$i]->g;
$b = $data[$i]->b;

$color = "-r$r -g$g -b$b";

exec("sudo ./zad3.py -x$y -y$x $color");
}
}
//}
?>

