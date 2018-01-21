<?php
	//Inicializar DB
	$conn = new PDO('pgsql:host=horton.elephantsql.com;dbname=omuafwnl', 'omuafwnl', 'xG1ShvrgOX6e4gwVwLa_X35kMNzkqKYv');
	$conn->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
	$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	//$stmt = $conn->prepare('SET SCHEMA \'HydroFox\'');
	//$stmt->execute();
?>