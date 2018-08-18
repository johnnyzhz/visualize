<?php
	
    
    $fp = fopen('public.txt','r');
    $data = array();
    while(!feof($fp)){
         $line = fgets($fp);
         $line_arr = explode("	",$line);
         $dataLine = array();
         $dataLine['graph'] = $line_arr[0];
         $dataLine['author'] = $line_arr[1];
         $dataLine['path'] = $line_arr[2];
         if($line_arr[2]!=null)
         {
         	$data[] = $dataLine;
     	 }
    }
    echo json_encode($data);
    fclose($fp);

?>