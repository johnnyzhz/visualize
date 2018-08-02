<?php 
function deldir($dir) {  
   	
    $dh = opendir($dir);  
    while ($file = readdir($dh)) {  
        if($file != "." && $file!="..") {  
        $fullpath = $dir."/".$file;  
        
        if(!is_dir($fullpath)) {
        	#echo $fullpath." is file";  
            unlink($fullpath);  
        } else {  
        	#echo $fullpath." is directory";  
   //      	if (is_writable($filename))
			// {
			// 	echo "yes";
			// }
			// else
			// {
			// 	echo "no";
			// }
   //      	if(chmod($fullpath, 0x1FF))
			// {
			// 	echo "changed!";
			// }
			// else
			// {
			// 	echo "unchange!";
			// }
            deldir($fullpath);  
        }  
      }  
    }  
    closedir($dh);  
       
      
    if(rmdir($dir)) {  
        return "true";  
    } else {  
        return "false";  
    }  
}
// function test($dir)
// {
// 	return false;
// }
#echo shell_exec("id -a");
$filename = $_POST['location'];
$filename= substr($filename,strrpos($filename, '/') -21,13);
$filename = "data/".$filename;
#echo $filename;
$dh = opendir($filename);
$data = array();
$idx = 0;
// if (is_writable($filename))
// {
// 	echo "yes";
// }
// else{
// 	if(chmod($filename, 0x1FF))
// 	{
// 		echo "changed!";
// 	}
// 	else{
// 		$perms = substr(sprintf('%o', fileperms($filename)), -4);  
// 		echo $perms; 
// 	}
// }
$res = deldir($filename);
ob_clean();
header("Location:index.html"); 
ob_flush();
flush();
exit();
?>
