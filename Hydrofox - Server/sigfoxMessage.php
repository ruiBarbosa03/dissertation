<?php
//sigfoxMessage.php
//Recebe callback da cloud do Sigfox (deviceID, dados, hora, acknowledge) - Pedido HTTP POST
//
//Faz inserção na base de dados (em mensagem)
//Parse da informação e insere leituras na Base de Dados (em leituras)
//
//Envia notificação à aplicação móvel
//
//Devolve resposta à cloud Sigfox baseada nas opções do utilizador.

	error_reporting(~0);
	ini_set('display_errors', 1);

	include 'init.php';
	include 'useful.php';

	//Get data from Sigfox
	if (isset($_POST["deviceID"])){
		$deviceID = $_POST["deviceID"];
	}else{
		echo "Erro deviceID";
		exit;
	};
	
	if (isset($_POST["data"])){
		$data = $_POST["data"];
	}else{
		echo "Erro dados";
		exit;
	};
	
	if (isset($_POST["time"])){
		$time = $_POST["time"];
	}else{
		echo "Erro hora";
		exit;
	};
	
	if (isset($_POST["ack"])){
		$ack = $_POST["ack"];
	}else{
		echo "Erro ack";
		exit;
	};

	global $conn;
		//Verificar se é alarme ou não!
		//Código de alarme -> data = '0' & noti = TRUE & ferias = TRUE
		$stmt = $conn->prepare("SELECT * FROM options WHERE deviceid = ?;");
		$stmt->execute(array($deviceID));
		$info = $stmt->fetch();
		
		if (strcmp($data, '0') == 0){
			//Get info about notifications and ferias	
			if ($info["notificacoes"] && $info["ferias"]){
				//Send noti to app
				
				$token[] = array();
				$tokenFromDB = getToken();
				$token[0] = $tokenFromDB["token"];
				
				$message = array("message" => "Alarme! Alguém ligou a água em sua casa.");
				$result = sendNotification($token, $message);
				echo $result;
			};
		}
		
		else if (strcmp($data, '1') == 0){
			//Get info about notifications and ferias
		
			if ($info["notificacoes"] && $info["detetafugas"]){
				
				//Send noti to app
				$token[] = array();
				$tokenFromDB = getToken();
				$token[0] = $tokenFromDB["token"];
				
				$message = array("message" => "Parece que tem uma fuga.");
				$result = sendNotification($token, $message);
				echo $result;
			};
		}
		else{
			//Insert data into database
			//Insert into messages
				$stmt = $conn->prepare("INSERT INTO messages(data_hora, deviceid, mensagem) VALUES (?,?,?);");
				$auxDate = date("Y-m-d H:i:s", $time);
				$stmt->execute(array($auxDate, $deviceID, $data));
			
			//Insert into leituras
			for ($j = 0; $j < 24; $j+=4){
				for ($jj = 0; $jj < 4; $jj++)
					$consumption[$jj] = $data[$j+$jj];
				$leitura = implode($consumption);
				$litros  = hexdec($leitura);
				
				$date =  date("Y-m-d H:i:s",$time - 60*60*(20-$j));
				$wkday = date("N", strtotime($date));

				$stmt = $conn->prepare("INSERT INTO leituras(data_hora, wkday, deviceid, leitura, litros) VALUES (?,?,?,?,?);");
				$stmt->execute(array($date, $wkday, $deviceID, $leitura, $litros));

			}
					
			//Send noti to app
			if ($info["notificacoes"]){
				$token[] = array();
				$tokenFromDB = getToken();
				$token[0] = $tokenFromDB["token"];
			
				$message = array("message" => "Nova leitura! Veja quanta água gastou ontem.");
				$result = sendNotification($token, $message);
			};
		};
		
		//Reply to SIGFOX
		//get ferias + sendtime + overflow
		$stmt = $conn->prepare("SELECT * FROM options WHERE deviceid = ?;");
		$stmt->execute(array($deviceID));
		$result = $stmt->fetch();
		
		$holiday = $result["ferias"];
		if ($holiday)
			$holiday = '1';
		else 
			$holiday = '0';
		
		$sendtime = $result["sendtime"];
		$overflow = $result["fugas"];
		$overflow = strval($overflow);
		$overflowDetection = $result["detetafugas"];
		
		if ($overflowDetection)
			$overflowDetection = '1';
		else 
			$overflowDetection = '0';
		
		http_response_code(200);
		
		header("Content-Type : application/json");
		$message = Array();
		$message["21464A"] = Array();
		$message["21464A"]["downlinkData"] = "0000000000".$overflowDetection.$overflow[0].$overflow[1].$overflow[2].$holiday.$sendtime;
		                                     
		echo json_encode($message);

?>
