<?php
	//Recebe deviceID + tempo + data e devolve resultado
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
  if (isset($_POST["intervalo"]))
		$intervalo = $_POST["intervalo"];
	else {
		echo "Erro - Deve escolher intervalo";
		exit;
	};
  if (isset($_POST["data"]))
		$data = $_POST["data"];
	else {
		echo "Erro na data";
		exit;
	};

  $aux = explode('/', $data);
  $ano = $aux[0];
  $mes = $aux[1];
  $dia = $aux[2];

  switch ($intervalo){
    case "Dia":
      $dados = getAllDayReadings($deviceID, $data);
      break;
    case "Semana":
      $dados = getAllWeekReadings($deviceID, $data);
      break;
    case "Mês":
      $dados = getAllMonthReadings($deviceID, $mes, $ano);
      break;
    case "Ano":
      $dados = getAllYearReadings($deviceID, $ano);
      break;
    default:
      $dados = "Erro";
  }

  echo json_encode($dados);

?>
