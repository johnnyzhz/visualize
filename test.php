<?php

	$nodeFile = $_POST['nodeFile'];
	$edgeFile = $_POST['edgeFile'];
	$nodeSize = $_POST['ns'];
	$nodeSizeValue = $_POST['nodeSizeValue'];
	$nodeSizeRankingMethod = $_POST['nodeSizeRankingMethod'];
	$nodeMinSize = $_POST['nodeMinSize'];
	$nodeMaxSize = $_POST['nodeMaxSize'];
	$nodeColor = $_POST['nc'];
	$nodeColorValue = $_POST['nodeColorValue'];
	$nodeColorRankingMethod = $_POST['nodeColorRankingMethod'];
	$nodeMinColor = $_POST['nodeMinColor'];
	$nodeMaxColor = $_POST['nodeMaxColor'];
	$nodeColorPartitionMethod = $_POST['nodeColorPartitionMethod'];
	$edgeColor = $_POST['ec'];
	$edgeColorValue = $_POST['edgeColorValue'];
	$edgeColorRankingMethod = $_POST['edgeColorRankingMethod'];
	$edgeMinColor = $_POST['edgeMinColor'];
	$edgeMaxColor = $_POST['edgeMaxColor'];
	$edgeColorPartitionMethod = $_POST['edgeColorPartitionMethod'];
	$layoutType = $_POST['lo'];
	//ForceAtlas2
	$f2gravity = $_POST['f2gravity'];
	$scalingRatio = $_POST['scalingRatio'];
	$strongGravityMode = $_POST['strongGravityMode'];
	$edgeWeightInfluence = $_POST['edgeWeightInfluence'];
	$f2barnesHutTheta = $_POST['f2barnesHutTheta'];
	$barnesHutOptimize = $_POST['barnesHutOptimize'];
	$adjustSizes = $_POST['adjustSizes'];
	//ForceAtlas
	$cooling = $_POST['cooling'];
	
	$freezeBalance = $_POST['freezeBalance'];
	$freezeInertia = $_POST['freezeInertia'];
	$freezeStrength = $_POST['freezeStrength'];
	$f1gravity = $_POST['f1gravity'];
	$inertia = $_POST['inertia'];
	$maxDisplacement = $_POST['maxDisplacement'];
	$repulsionStrength = $_POST['repulsionStrength'];
	$f1speed = $_POST['f1speed'];
	//random
	$size = $_POST['size'];
	//noverlap
	$margin = $_POST['margin'];
	$ratio = $_POST['ratio'];
	$lapspeed = $_POST['lapspeed'];
	//rotate
	$angle = $_POST['angle'];
	//contract
	$contract = $_POST['contract'];
	//expand
	$expand = $_POST['expand'];
	//label
	$labelspeed = $_POST['labelspeed'];
	$adjustBySize = $_POST['adjustBySize'];
	//fr
	$frspeed = $_POST['frspeed'];
	$frgravity = $_POST['frgravity'];
	$area = $_POST['area'];
	//YifanHu
	$stepRatio = $_POST['stepRatio'];
	$adaptiveCooling = $_POST['adaptiveCooling'];
	$step = $_POST['step'];
	$relativeStrength = $_POST['relativeStrength'];
	$optimalDistance = $_POST['optimalDistance'];
	$convergenceThreshold = $_POST['convergenceThreshold'];
	$initialStep = $_POST['initialStep'];
	$yfhbarnesHutTheta = $_POST['yfhbarnesHutTheta'];
	$quadTreeMaxLevel = $_POST['quadTreeMaxLevel'];


	

	require_once("/var/www/webnetvis/java/Java.inc");
	java_require("/home/ubuntu/java/jdk1.8.0_181/jre/lib/ext/test_sigma.jar");
	java_require("/home/ubuntu/java/jdk1.8.0_181/jre/lib/ext/gephi-toolkit-0.9.2-all.jar");
	java_require("/home/ubuntu/java/jdk1.8.0_181/jre/lib/ext/gson.jar");
	java_require("/home/ubuntu/java/jdk1.8.0_181/jre/lib/ext/uk-ac-ox-oii-sigmaexporter.jar");

	$layout = new Java("java.util.HashMap");
	$node = new Java("java.util.HashMap");
	$input = new Java("java.util.HashMap");
	$edge = new Java("java.util.HashMap");
	$layout->put("type", "ForceAtlas2");

	// // $layout->put("gravity", 2.0);
	// // $layout->put("scalingRatio", 5.0); 
	
	$node->put("size", $nodeSize);
	$node->put("sizeValue", $nodeSizeValue);
	$node->put("sizeRankingMethod", $nodeSizeRankingMethod);
	$node->put("minSize",$nodeMinSize);
	$node->put("maxSize", $nodeMaxSize);
	$node->put("color", $nodeColor);
	$node->put("colorValue", $nodeColorValue);
	$node->put("colorRankingMethod", $nodeColorRankingMethod);
	$node->put("minColor", $nodeMinColor);
	$node->put("maxColor", $nodeMaxColor);
	$node->put("colorPartitionMethod", $nodeColorPartitionMethod);

	$edge->put("color", $edgeColor);
	$edge->put("colorValue", $edgeColorValue);
	$edge->put("colorPartitionMethod", $edgeColorPartitionMethod);
	$edge->put("minColor", $edgeMinColor);
	$edge->put("maxColor", $edgeMaxColor);
	$edge->put("colorRankingMethod", $edgeColorRankingMethod);
	


	$layout->put("type", $layoutType);
	$layout->put("scalingRatio", $scalingRatio);
	$layout->put("f2gravity",  $f2gravity);
	$layout->put("strongGravityMode", $strongGravityMode);
	$layout->put("edgeWeightInfluence", $edgeWeightInfluence);
	$layout->put("f2barnesHutTheta", $f2barnesHutTheta);
	$layout->put("barnesHutOptimize", $barnesHutOptimize);
	$layout->put("adjustSizes", $adjustSizes);

	// //ForceAtlas
	$layout->put("cooling", $cooling);
	$layout->put("freezeBalance", $freezeBalance);
	$layout->put("freezeInertia", $freezeInertia);
	$layout->put("freezeStrength", $freezeStrength);
	$layout->put("f1gravity", $f1gravity);
	$layout->put("inertia", $inertia);
	$layout->put("maxDisplacement", $maxDisplacement);
	$layout->put("repulsionStrength", $repulsionStrength);
	$layout->put("f1speed", $f1speed);
	//random
	$layout->put("size", $size);
	//noverlap
	$layout->put("margin", $margin);
	$layout->put("ratio", $ratio);
	$layout->put("speed", $speed);
	//rotate
	$layout->put("angle", $angle);
	//contract
	$layout->put("contract", $contract);
	//expand
	$layout->put("expand", $expand);
	//label
	$layout->put("labelspeed", $labelspeed);
	$layout->put("adjustBySize", $adjustBySize);
	//fr
	$layout->put("frspeed", $frspeed);
	$layout->put("frgravity", $frgravity);
	$layout->put("area", $area);
	//YifanHu
	$layout->put("stepRatio", $stepRatio);
	$layout->put("adaptiveCooling", $adaptiveCooling);
	$layout->put("step", $step);
	$layout->put("relativeStrength", $relativeStrength);
	$layout->put("optimalDistance", $optimalDistance);
	$layout->put("convergenceThreshold", $convergenceThreshold);
	$layout->put("initialStep", $initialStep);
	$layout->put("yfhbarnesHutTheta", $yfhbarnesHutTheta);
	$layout->put("quadTreeMaxLevel", $quadTreeMaxLevel);


	$input->put("nodesFile", $nodeFile);
	$input->put("edgesFile", $edgeFile);
	
	$cfg = new Java("test_sigma.Config", $layout, $node, $input, $edge);              
	$rankingGraph = new Java("test_sigma.RankingGraph", $cfg);
	//echo gettype($cooling);
	
	$result = $rankingGraph->script();
	$info = (string)$result;
	
	if ($info!= null)
	{
		
		header("Location:".$info); 
		
	}
	else
	{
		echo "An error occurs when building the directory, please try again!";
	}




?>