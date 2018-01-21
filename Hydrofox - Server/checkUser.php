<?php

	//Start PDO connection
	$conn = new PDO('pgsql:host=db.fe.up.pt;dbname=ee12014', 'ee12014', 'rui12014');
	$conn->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
	$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	$stmt = $conn->prepare('SET SCHEMA \'HydroFox\'');
	$stmt->execute();

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
	
	//Prepare query
	$stmt = $conn->prepare("SELECT *
							FROM utilizador
							WHERE userapi_login = ? AND userapi_password = ?");
	
	//Execute query
	$stmt->execute(array($login, $password));
	
	//Get the result
	$result = $stmt->fetch();

	//Check if there's user. If there is returns USERNAME. Else return "0"
	if (count($result) > 1){
		echo $result["username"];
		exit;
	}

	else {
		echo "failed";
		exit;
	}

?>
