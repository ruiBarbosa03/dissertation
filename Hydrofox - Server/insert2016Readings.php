<?php
  //Recebe deviceID + tempo + data e devolve resultado
  error_reporting(~0);
  ini_set('display_errors', 1);

  include 'init.php';
  include 'useful.php';

  $deviceid = '21464A';
  $Date = "2016-09-27";
  echo date('Y-m-d', strtotime($Date. ' + 1 days'));
  echo date('Y-m-d', strtotime($Date. ' + 2 days'));
  //Pôr 1000 dias de leituras
  $i = 0;
  for ($i=0; $i<302; $i++){
    //1. Make date
    $string = ' + '.$i.' days';
    $newDate = date('Y-m-d H:i:s', strtotime($Date.$string));

    for ($ii = 0; $ii < 4; $ii++){
      $hours = $ii*6;
      $newDateOut = date('Y-m-d H:i:s', strtotime($newDate." + ".$hours." hours"));
      $weekday = $dayofweek = date('w', strtotime($newDateOut));
      $newReading =rand(0,300);

      //Insert in DB
      insertReadingIntoDB($newDateOut, $newReading, $deviceid, 673, $weekday);

    };

    //2. Generate reading (most likely 0 can go as high as 500)




    //3. Insert
  };

?>
