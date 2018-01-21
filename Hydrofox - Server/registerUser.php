<?php
//Recebe info da aplicação móvel via pedido HTTP Post
//Recebe login, passowrd, username, deviceid, phonenumber
//Regista user na BD
	error_reporting(~0);
	ini_set('display_errors', 1);

	include 'init.php';
	include 'useful.php';

	//Get data from APP
	if (isset($_POST["login"]))
		$login = $_POST["login"];
	else {
		echo "failed";
		exit;
	}

	if (isset($_POST["password"]))
		$password = $_POST["password"];
	else {
		echo "failed";
		exit;
	}

	if (isset($_POST["username"]))
		$username = $_POST["username"];
	else {
		echo "failed";
		exit;
	}

	if (isset($_POST["deviceID"]))
		$deviceID = $_POST["deviceID"];
	else {
		echo "failed";
		exit;
	}

	if (isset($_POST["phone"]))
		$phone = $_POST["phone"];
	else
		$phone = null;

//Verifica se deviceID está correto
	$HTTPdata = getHTTPData($login, $password, $deviceID, false);
	if (!$HTTPdata){ //ERRO AO TIRAR MENSAGENS POR API
		echo "Impossível de recolher leituras";
		exit;
	};
	//Verifica se user existe. Se não existir tenta criar.
	if (checkUtilizador($username, $login, $password) == 0){
		if (!createUtilizador($username, $login, $password, $phone)){
			echo "Erro a criar utilizador.";
			exit;
		};
	}
	else {
		echo "Nome de utilizador ou conjunto login/password já existe.";
		exit;
	};

	//Cria dispositivo
	if (checkDevice($deviceID) == 0){
		if (!createDevice($username, $deviceID)){
			echo "Erro a criar dispositivo.";
			exit;
		};
	}
	else{
		echo "Código do dispositivo já existente.";
		exit;
	};
	
	setParams($deviceID);

	//Data in Database
	$messages = getAllMessages($deviceID);

	if (count($messages) > 1) {
		echo "good";
		exit;
	}
	else{
		//Submeter leituras na BD
		$jsonHTTPData = json_decode($HTTPdata, true); //Ir buscar leituras via API
			insertMessages($jsonHTTPData, $deviceID);

			while ($jsonHTTPData["paging"] != NULL){
				$properURL = str_replace("limit=100&", "", $jsonHTTPData["paging"]["next"]); //URL they give is ridiculous and does not work
				$HTTPdata = getHTTPData($login, $password, $deviceID, $properURL);
				$jsonHTTPData = json_decode($HTTPdata, true);
				insertMessages($jsonHTTPData, $deviceID);
			};

			populateLeituras($deviceID);
	};
	
	

	echo "good";
	exit;

?>
