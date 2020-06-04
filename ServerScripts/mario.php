<?php

function ledIndexToTag($x, $y) {
	
	return "LED" .$x .$y;
}

$ledDisplay = array();
$ledDisplayTestFile = 'mario.json';

$n = 0;

for ($i =0; $i < 8; $i++) {
	for($j=0; $j < 8; $j++){
		$ledTag = ledIndexToTag($i, $j);
		if(isset($_POST[$ledTag])){
			$ledDisplay[$n] = json_decode($_POST[$ledTag]);
			$n=$n+1;
		}
	}
}

/*DEBUG print_r($ledDisplay);*/

$ledDisplayJson = json_encode($ledDisplay);

file_put_contents($ledDisplayTestFile, $ledDisplayJson);

echo"ACK1";

exec ("sudo ./mario.py");

echo " ACK2";

?> 
