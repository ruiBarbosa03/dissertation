<?php
//Regista o token para fazer notificações usando Google Firebase API
	include 'init.php';
	
	global $conn; 
	
	if (isset($_POST["Token"])){
		$token = $_POST["Token"];
		$stmt = $conn->prepare("INSERT INTO token (token) 
								VALUES (?);");
		$stmt->execute(array($token));
		
		echo "Good";
		exit;
		
	}
	else 
		echo "Erro de registo de Token.";
	
	exit;
?>