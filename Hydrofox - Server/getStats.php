<?php
	//Recebe deviceID do modem Sigfox e devolve estatísticas

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

	//String para meses
	$meses = array("Janeiro", "Fevereiro", "Março", "Abril",
								 "Maio", "Junho", "Julho", "Agosto",
								 "Setembro", "Outubro", "Novembro", "Dezembro");

  $maxMonth = getMaxMonth($deviceID);
  $minMonth = getMinMonth($deviceID);
  $avgMonth = getAvgMonth($deviceID);

  $maxDay = getMaxDay($deviceID);
  $minDay = getMinDay($deviceID);
  $avgDay = getAvgDay($deviceID);

  $yearStats = getYearStats($deviceID);
	$wkdayStats = getWeekdayStats($deviceID);

	$timeMax = strtotime($maxMonth["mes"]);
	$timeMin = strtotime($minMonth["mes"]);

	$results = array();
	$results["maxMonth"] = $meses[date('n',$timeMax)-1]." de 20".date('y', $timeMax);
	$results["maxMonthLiters"] = $maxMonth["soma"];

	$results["minMonth"] = $meses[date('n', $timeMin)-1]." de 20".date('y', $timeMin);
	$results["minMonthLiters"] = $minMonth["soma"];

	$results["avgMonthLiters"] = round($avgMonth["media"]);

	$datetimearray = explode(" ", $maxDay["dia"]);
	$results["maxDay"] = $datetimearray[0];
	$results["maxDayLiters"] = $maxDay["soma"];

	$datetimearray = explode(" ", $minDay["dia"]);
	$results["minDay"] = $datetimearray[0];
	$results["minDayLiters"] = $minDay["soma"];

	$results["avgDayLiters"] = round($avgDay["media"]);

	$results["yearStats"] = $yearStats;
	$results["wkdayStats"] = $wkdayStats;

	echo json_encode($results, JSON_PRETTY_PRINT);

?>
