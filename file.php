<?php
if(!empty($_FILES['myfile'])){
    $fileinfo = $_FILES['myfile'];
    $filename = time();
    $destination = "/tmp/".$filename.'.csv';
    $success = move_uploaded_file($fileinfo['tmp_name'],$destination);
    $data = array();
    $data['status'] = $success;
    $data['filename'] = $destination;
    echo json_encode($data);
}
else{
	$data = array();
	$data['status'] = "true";
	echo json_encode($data);
}
?>