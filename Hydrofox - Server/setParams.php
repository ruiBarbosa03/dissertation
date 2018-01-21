<?php
//setParams.php
//Recebe dados da aplicaçãp móvel relacionados com tempo de envio e modo de funcionamento e guarda-os em BD.
	include 'init.php';
	
	//Get data from APP
	if (isset($_POST["deviceID"]))
		$deviceID = $_POST["deviceID"];
	if (isset($_POST["modo"]))
		$ferias = $_POST["modo"];
	if (isset($_POST["sendTime"]))
		$envio = $_POST["sendTime"];
	if (isset($_POST["fugas"]))
		$fugas = $_POST["fugas"];
	if (isset($_POST["detetafugas"]))
		$detetafugas = $_POST["detetafugas"];
	if (isset($_POST["notificacoes"]))
		$notificacoes = $_POST["notificacoes"];
	
	global $conn;
	//SAVE THIS IN DB
	echo $fugas;
	$stmt = $conn->prepare("UPDATE options 
							SET ferias = ?, sendtime = ?, fugas = ?, detetafugas = ?, notificacoes = ?
							WHERE deviceid = ?;");
	$stmt->execute(array($ferias, $envio, $fugas, $detetafugas, $notificacoes, $deviceID));

?>