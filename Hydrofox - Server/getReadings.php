<?php
	error_reporting(~0);
	ini_set('display_errors', 1);

	include 'init.php';
	include 'useful.php';

	//Get data from APP
	if (isset($_POST["deviceID"]))
		$deviceID = $_POST["deviceID"];
	else {
		echo "Erro deviceID";
		exit;
	};

	if (!isset($_POST["data"]) && !isset($_POST["month"])) {
		echo "Erro dados";
		exit;
	};

	if (isset($_POST["data"]))
		$dia = $_POST["data"];
	else
		$dia = null;

	if (isset($_POST["month"]))
		$month = $_POST["month"];
	else
		$month = null;

	if ($dia != null){
		$readingDia = getDailyReading($deviceID, $dia);
		if ($readingDia["read"] == NULL)
			$readingDia["read"] = 0;
	};

	if ($month != null){
		$readingMonth = getMonthReading($deviceID, $month);
		if ($readingMonth["read"] == NULL)
			$readingMonth["read"] = 0;
	};

	$results = array();
	$results["dia"] = $readingDia["read"].'';
	$results["mes"] = $readingMonth["read"].'';
	$results["good"] = true;

	echo json_encode($results);

//	echo json_encode($readingDia);

?>
