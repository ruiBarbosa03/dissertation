<?php

	//FAKE NEWS
	function insertReadingIntoDB( $date,$reading, $deviceid, $messageid, $weekday){
		global $conn;

		$stmt = $conn->prepare('INSERT INTO leituras(data_hora, wkday, messageid, deviceid, litros)
		                        VALUES (?,?,?,?,?)');

		$stmt->execute(array($date, $weekday, $messageid, $deviceid, $reading));
	}
	//Página com funções de interação com DB ou com Servidor sigfox

	//Funções CHECK - Utilizador + Device - DB
	function checkUtilizador($username, $login, $password){
		global $conn;
		//Prepare query 1 - Check user
		$stmt = $conn->prepare('SELECT * FROM utilizador WHERE username = ? OR userapi_login = ? OR userapi_password = ?');

		//Execute query and check for success
		$stmt->execute(array($username, $login, $password));

		return $stmt->rowCount();
	};
	function checkUserCreated($login, $password){
		global $conn;
		//Prepare query 1 - Check user
		$stmt = $conn->prepare('SELECT *
														FROM utilizador JOIN device USING(username)
														WHERE userapi_login = ? OR userapi_password = ?');

		//Execute query and check for success
		$stmt->execute(array($login, $password));

		return $stmt->fetch();
	};
	function checkDevice($deviceID){
		global $conn;
		//Prepare query 1 - Check user
		$stmt = $conn->prepare('SELECT * FROM device WHERE deviceid = ?');

		//Execute query and check for success
		$stmt->execute(array($deviceID));

		return $stmt->rowCount();
	};

	//Funções CREATE - Utilizador + Device - DB
	function createUtilizador($username, $login, $password, $phone){
		global $conn;

		//Prepare query 1 - Create User
		$stmt = $conn->prepare('INSERT INTO utilizador (username, userapi_login, userapi_password, telefone) VALUES (?,?,?,?)');

		//Execute query and check for success
		if ($stmt->execute(array($username, $login, $password, $phone)))
			return true;

		else
			return false;
	};
	function createDevice($username, $deviceID){
		global $conn;

		//Prepare query - Create Device
		$stmt = $conn->prepare("INSERT INTO device (deviceid, username)
								VALUES (?,?)");

		//Execute query and check for success
		if ($stmt->execute(array($deviceID, $username)))
			return true;
		else
			return false;
	};
	function createParams($deviceID){
		global $conn;

		//Prepare query - Create Device
		$stmt = $conn->prepare("INSERT INTO options (deviceid, ferias, sendtime, fugas, detetafugas, notificacoes)
								VALUES (?,false, 2, 999, true, true)");

		//Execute query and check for success
		$stmt->execute(array($deviceID));
	}
	
	//Funções de interação entre DB e Servidor sigfox
	//getHTTPData - vai buscar info ao Servidor;
	//insertMessages - põe info na DB
	//getAllMessages - vai buscar toda a info à DB
	//populateLeituras - parse da info e registo de leituras
	function getHTTPData($login, $pass, $device, $url){

		if ($url == FALSE)
			$url_Messages = 'https://backend.sigfox.com/api/devices/'.$device.'/messages'; // put your device-id
		else
			$url_Messages = $url;

		$curl = curl_init();
		curl_setopt($curl, CURLOPT_URL, $url_Messages);
		curl_setopt($curl, CURLOPT_HTTPGET, true);

		curl_setopt($curl, CURLOPT_COOKIESESSION, true);
		curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($curl, CURLOPT_HTTPAUTH, CURLAUTH_ANY); // Need SSL
		curl_setopt($curl, CURLOPT_USERPWD,"$login:$pass");
		$data  = curl_exec ($curl);

		if ($data == FALSE){
			curl_close($curl);
			return false;
		}
		else {
			curl_close($curl);
			return $data;
		};
	};
	function getAllMessages($deviceID){
 	 global $conn;

 	 $stmt = $conn->prepare("SELECT * from messages WHERE deviceid = ? ORDER BY messageid;");

 	 $stmt->execute(array($deviceID));

 	 return $stmt->fetchAll();
  };
	function insertMessages($data, $deviceID){
		global $conn;

		foreach($data["data"] AS $dados){

			$stmt = $conn->prepare("INSERT INTO messages (data_hora, wkday, deviceid, mensagem) VALUES (?,?,?,?);");

			$stmt->execute(array(date("Y-m-d H:i:s", $dados["time"]), date("N",$dados["time"]), $dados["device"], $dados["data"].str_repeat('0', max(0, 24 - strlen($dados["data"])))));
		}

		return true;
	};
	function populateLeituras($device){
		global $conn;

		$data = getAllMessages($device);

		if ($data != FALSE){
			foreach ($data as $dados) {
				for ($j = 0; $j < 24; $j+=4){
					$leituras["messageid"] = $dados["messageid"];
					for ($jj = 0; $jj < 4; $jj++)
						$consumption[$jj] = $dados["mensagem"][$j+$jj];
					$leituras["leitura"] = implode($consumption);
					$leituras["litros"] = hexdec(implode($consumption));
					$leituras["data"] =  date("Y-m-d H:i:s",strtotime($dados["data_hora"]) - 60*60*(20-$j));
					$leituras["wkday"] = date("N", strtotime($leituras["data"]));

					$stmt = $conn->prepare("INSERT INTO leituras(data_hora, wkday, messageid, deviceid, leitura, litros) VALUES (?,?,?,?,?,?);");
					$stmt->execute(array($leituras["data"], $leituras["wkday"], $leituras["messageid"], $device, $leituras["leitura"], $leituras["litros"]));

				}
			}
		};
	};

 //Funções STATS - Vão buscar info de leituras à DB
	function getDailyReading($device, $dia){
		global $conn;

		$dia = $dia . " 00:00:00";

		$query = "SELECT sum(litros) AS read from leituras WHERE deviceid = '".$device."' AND date_trunc('day', data_hora) = '".$dia."'";
		//$stmt = $conn->prepare("SELECT sum(litros) AS read from leituras WHERE deviceid = ? AND date_trunc('day', data_hora) = ?");
		$stmt = $conn->prepare($query);
		$stmt->execute();
		//$stmt->execute(array($device, $dia));
		//return $query;
		return $stmt->fetch();

	};
	function getMonthReading($device, $mes){
		global $conn;

		$stmt = $conn->prepare("SELECT sum(litros) AS read from leituras WHERE deviceid = ? AND date_part('month', data_hora) = ?");

		$stmt->execute(array($device, $mes));

		return $stmt->fetch();

	};
	function getMaxMonth($deviceID){
		global $conn;

		$stmt = $conn->prepare("SELECT date_trunc('month', data_hora) as mes, sum(litros) as soma
														FROM leituras
														WHERE deviceid = ?
														GROUP BY date_trunc('month', data_hora)
														ORDER BY soma DESC
														LIMIT 1");
		$stmt->execute(array($deviceID));

		return $stmt->fetch();
	};
	function getMinMonth($deviceID){
		global $conn;

		$stmt = $conn->prepare("SELECT date_trunc('month', data_hora) as mes, sum(litros) as soma
														FROM leituras
														WHERE deviceid = ?
														GROUP BY date_trunc('month', data_hora)
														ORDER BY soma ASC
														LIMIT 1");
		$stmt->execute(array($deviceID));
		return $stmt->fetch();
	};
	function getAvgMonth($deviceID){
		global $conn;

		$stmt = $conn->prepare("SELECT AVG(soma) AS media
														FROM (SELECT sum(litros) as soma
														      FROM leituras
																	WHERE deviceid = ?
														      GROUP BY date_trunc('month', data_hora))
													  as aux");
	  $stmt->execute(array($deviceID));

		return $stmt->fetch();
	};
	function getMaxDay($deviceID){
		global $conn;

		$stmt = $conn->prepare("SELECT date_trunc('day', data_hora) as dia, sum(litros) as soma
														FROM leituras
														WHERE deviceid = ?
														GROUP BY date_trunc('day', data_hora)
														ORDER BY soma DESC
														LIMIT 1");
		$stmt->execute(array($deviceID));

		return $stmt->fetch();
	};
	function getMinDay($deviceID){
		global $conn;

		$stmt = $conn->prepare("SELECT dia, soma
														FROM (
																SELECT date_trunc('day', data_hora) as dia, sum(litros) as soma
																FROM leituras
																WHERE deviceid = ?
																GROUP BY date_trunc('day', data_hora)
																ORDER BY soma ) as aux
														WHERE soma > 0
														LIMIT 1	");

		$stmt->execute(array($deviceID));
		return $stmt->fetch();
	};
	function getAvgDay($deviceID){
		global $conn;

		$stmt = $conn->prepare("SELECT AVG(soma) AS media
														FROM (SELECT sum(litros) as soma
														      FROM leituras
																	WHERE deviceid = ?
														      GROUP BY date_trunc('day', data_hora))
													  as aux");
	  $stmt->execute(array($deviceID));

		return $stmt->fetch();
	};
	function getYearStats($deviceID){
		global $conn;

		$stmt = $conn->prepare("SELECT date_part('year', data_hora) as ano, sum(litros) as soma
														FROM leituras
														WHERE deviceid = ?
														GROUP BY date_part('year', data_hora)	");

		$stmt->execute(array($deviceID));
		return $stmt->fetchAll();

	}
	function getWeekdayStats($deviceID){
		global $conn;

		$stmt = $conn->prepare("SELECT wkday, sum(litros) as soma
														FROM leituras
														WHERE deviceid = ?
														GROUP BY wkday
														ORDER BY wkday");

		$stmt->execute(array($deviceID));
		return $stmt->fetchAll();
	}
	function getAllDayReadings($deviceID, $data){
		global $conn;

		$data = $data." 00:00:00";

		$stmt = $conn->prepare("SELECT data_hora as date, litros
														FROM leituras
														WHERE date_trunc('day',data_hora) = ? AND deviceid = ?
														ORDER BY data_hora");

		$stmt->execute(array($data, $deviceID));
		return $stmt->fetchAll();
	}


	function getAllWeekReadings($deviceID, $data){
		global $conn;

		$date = new DateTime($data);
		$week = $date->format("W");

		$aux = explode('/', $data);
		$year = $aux[0];

		$query = "SELECT date_trunc('day',data_hora) AS date, sum(litros) AS litros
														FROM leituras
														WHERE date_part('year',data_hora) = ".$year." AND deviceid = '".$deviceID."' AND to_char(data_hora, 'WW') = '".$week."'
														GROUP BY date_trunc('day', data_hora)
														ORDER BY date_trunc('day',data_hora)";

		$stmt = $conn->prepare($query);
		$stmt->execute();
		//$stmt->execute(array($year, $deviceID,$week));
		return $stmt->fetchAll();
	}
	function getAllMonthReadings($deviceID, $mes, $ano){
		global $conn;

		$stmt = $conn->prepare("SELECT date_trunc('day',data_hora) as date, sum(litros) as litros
														FROM leituras
														WHERE date_part('month',data_hora) = ? AND date_part('year',data_hora) = ? AND deviceid = ?
														GROUP BY date_trunc('day', data_hora)
														ORDER BY date_trunc('day', data_hora)");

		$stmt->execute(array($mes,$ano, $deviceID));
		return $stmt->fetchAll();
	}
	function getAllYearReadings($deviceID, $ano){
		global $conn;

		$stmt = $conn->prepare("SELECT date_trunc('month', data_hora) as date, sum(litros) as litros
														FROM leituras
														WHERE date_part('year',data_hora) = ? AND deviceid = ?
														GROUP BY date_trunc('month', data_hora)
														ORDER BY date_trunc('month', data_hora)");

		$stmt->execute(array($ano, $deviceID));
		return $stmt->fetchAll();
	}

	//Função para enviar notificações
	function sendNotification ($token, $message){
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array(
			 'registration_ids' => $token,
			 'data' => $message
			);
		$headers = array(
			'Authorization:key = AAAAXYcSCGM:APA91bGorqKE-c0UGC1djnistdMuvRkYL0fxcvgd2gIFR5k5IqbOWd2NOFUnHr-acT0rHsn8j6doli6m85yglRwcfmGrMQ-Z73cLMOqgaSvBpvob0pGTjj4_VoRRK3lawklmrYDlH--q',
			'Content-Type: application/json'
			);
	   $ch = curl_init();
       curl_setopt($ch, CURLOPT_URL, $url);
       curl_setopt($ch, CURLOPT_POST, true);
       curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
       curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
       curl_setopt ($ch, CURLOPT_SSL_VERIFYHOST, 0);  
       curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
       curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
       $result = curl_exec($ch);           
       if ($result === FALSE) {
           die('Curl failed: ' . curl_error($ch));
       }
       curl_close($ch);
       return $result;
	}
	function getToken(){
		global $conn;

		$stmt = $conn->prepare("SELECT token
								FROM token
								ORDER BY id DESC
								LIMIT 1");

		$stmt->execute();
		return $stmt->fetch();
	}
?>
