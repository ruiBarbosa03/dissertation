<?php
	//Recebe deviceID do modem Sigfox e devolve estatísticas

	error_reporting(~0);
	ini_set('display_errors', 1);

	include 'init.php';
	include 'useful.php';

	//Get data from APP
	if (isset($_POST["login"]))
		$login = $_POST["login"];
	else {
		echo "Erro falta de login.";
		exit;
	};
	if (isset($_POST["password"]))
		$password = $_POST["password"];
	else {
		echo "Erro falta de password.";
		exit;
	};

	//Check login
	$results = checkUserCreated($login, $password);
	$reply = Array();

	if (!$results){
		echo "Erro";
		exit;
	} else{
		$reply["username"] = $results['username'];
		$reply['deviceid'] = $results['deviceid'];
		$reply['phonenumber'] = $results['telefone'];

		echo json_encode($reply, JSON_PRETTY_PRINT);
		exit;
	}

?>
