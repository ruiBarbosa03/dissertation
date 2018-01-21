/*
 * C�digo para MSP430G2553 para Disserta��o:
 *
 * "Contador de �gua com tecnologia Sigfox"
 * Autor: Rui Barbosa
 * Orientador: Professor Doutor Paulo Portugal
 *
 * Detalhes:
 * MCU recebe impulsos, conta-os.
 * A cada x minutos guarda num vetor.
 * A cada 6*x minutos enviam info via UART.
 */

#include <msp430g2553.h>

//Tempor�rio - s� para testes
#define TXLED BIT0
#define RXLED BIT6

//Pinos de comunica��o UART
#define RXD BIT1
#define TXD BIT2

//Liga��o do sensor
#define BUTTON BIT3 //Input
#define PULSE_LED BIT5 //Liga quando � detetado

//N�mero de segundos numa hora. Simplifica atribui��es e contas no c�digo
#define HORA 3600;

//Para fazer convers�o int -> HEX
#define TO_HEX(x) (x <= 9 ? '0' + x : 'a' - 10 + x)

//Guardar contagem = SAVE (segundos) = __*HORA
const unsigned int LITRO = 168;
//129600 = tempo de grava��o em segundos para send = 6h
unsigned long SAVE= 30;
unsigned int MODE = 0;
unsigned int LEAK = 1;
unsigned int LEAK_VALUE = 999;
unsigned int alertSent = 1;

volatile unsigned int count = 0; //2bytes - 65535
volatile unsigned int LiterCount = 0; //Guarda quantidade de �gua gasta em litros
unsigned char message[33] = "AT$SF=000000000000000000000000,1\n";     //Guardar mensagem para envio
unsigned char alert[8] = "AT$SB=0\n";     //Guardar mensagem para envio
unsigned char leak[8] = "AT$SB=1\n";     //Guardar mensagem para envio
unsigned char downlink[28] = "";
unsigned char sleep[7] = "AT$P=1\n";
unsigned char wakeup[7] = "AT$P=0\n";

volatile unsigned int i = 0;      //Auxiliar
volatile unsigned int ii = 0;     //Auxiliar - �ndice do vetor de rece��o de mensagem
volatile unsigned int timerCount; //= SAVE, usada para registo (quando clk � demasiado r�pido para fazer contagem)

void UARTSendArray(unsigned char *TxArray, unsigned char ArrayLength);
void MakeHexMsg(unsigned int count);

int main(void)
{
    // Parar WDT
        WDTCTL = WDTPW + WDTHOLD;

    // Configurar Source do CLK do CPU
        BCSCTL1 = CALBC1_1MHZ; // Set DCO to 1MHz
        DCOCTL = CALDCO_1MHZ;  // Set DCO to 1MHz

    // Configurar timer
        TACTL = TASSEL_1 +  MC_1 + ID_3; //ACLK  + UP Mode + /8 -> fACLK = 4096Hz
        //Interrupt no fundo
        //Como Timer � de 16bit, CCR0 < 65535;
        //Mudar este valor para obter frequ�ncias de comuta��o diferentes
        //fINT = fACLK/CCR0;
        CCR0 = 4096; // 1Hz;

    // Configurar IO
        P1DIR |= TXLED + RXLED + TXD + PULSE_LED;
        P1OUT = 0x00;

        //Para poupar energia, p�r todos os pinos da porta2 como output e por tudo a 0.
        P2DIR = 0xFF;
        P2OUT = 0x00;
        P3DIR = 0xFF;
        P3OUT = 0x00;

        P1OUT |= BUTTON; // Define Pull-Up Resistor
        P1REN |= BUTTON; // Resistor Enabled
        P1IE |= BUTTON; //  interrupt enabled
        P1IES |= BUTTON; // H2L
        P1IFG &= ~BUTTON; // P1.3 IFG cleared

    // Configurar UART - IMPORTANTE!
        /*
         * Para j� UART funciona com SMCLK.
         * � preciso instalar o Xtal de 32kHz para o p�r a trabalhar com ACLK
         */
        P1SEL = RXD + TXD ; // P1.1 = RXD, P1.2=TXD
        P1SEL2 = RXD + TXD ; // P1.1 = RXD, P1.2=TXD

        UCA0CTL1 |= UCSSEL_2; // Use SMCLK
        UCA0BR0 = 104; // Set baud rate to 9600 with 1MHz clock (Data Sheet 15.3.13)
        UCA0BR1 = 0; // Set baud rate to 9600 with 1MHz clock
        UCA0MCTL = UCBRS0; // Modulation UCBRSx = 1
        UCA0CTL1 &= ~UCSWRST; // Initialize USCI state machine
        IE2 |= UCA0RXIE; // Enable USCI_A0 RX interrupt

        timerCount = SAVE;
     CCTL0 |= CCIE + OUTMOD_2;  //Interrupt do comparador CCR0

    __enable_interrupt(); // enable all interrupts
    UARTSendArray(sleep, 7);
    _BIS_SR(LPM3_bits + GIE);
}

// Port 1 interrupt service routine
#pragma vector=PORT1_VECTOR
__interrupt void Port_1(void)
{
    count++;
    //P1OUT ^= PULSE_LED;
    P1IFG &= ~BUTTON; // P1.3 IFG cleared

    if (MODE == 1 && alertSent == 0){
        //UARTSendArray(wakeup, 7);
        UARTSendArray(alert, 8);
        alertSent = 1;
    }

   if (count == LITRO){
        // Por fun��o do contador aqui!
        LiterCount++;
        //H� fuga
        if (LiterCount > LEAK_VALUE && LEAK == 1 && alertSent == 0){
            //UARTSendArray(wakeup, 7);
            UARTSendArray(leak, 8);
            alertSent = 1;
        }
        count = 0;
    }

   UARTSendArray(sleep, 7);
   __bis_SR_register_on_exit(LPM3_bits);

}

#pragma vector=TIMER0_A0_VECTOR
__interrupt void Timer_A (void)
{
    //Conta tempo
    timerCount--;

    if (timerCount == 0){
        //P1OUT ^= TXLED;
        MakeHexMsg(LiterCount);
        //MakeHexMsg(count);

        count = 0;
        LiterCount = 0;
        i++;
        timerCount = SAVE;

        if (i == 24){
            i = 0;
            //P1OUT ^= RXLED;
            //UARTSendArray(wakeup, 7);
            UARTSendArray(message, 33);
        }
     }
    __bis_SR_register_on_exit(LPM3_bits);
}

#pragma vector=USCIAB0RX_VECTOR
__interrupt void USCI0RX_ISR(void)
{
   downlink[ii++] = UCA0RXBUF;

   if (UCA0RXBUF == '\n'){
       //SAVE TIME
       switch(downlink[25]){
           case '0': SAVE = 129600; break; //Medida - 90min; Envio 6h
           case '1': SAVE = 129600*2; break; //Medida - 3h; Envio 12h
           case '2': SAVE = 129600*4; break; //Medida - 6h; Envio 24h
           default: SAVE = 30; break;
       }

       //MODO F�RIAS
       switch (downlink[24]){
           case '0': MODE = 0; alertSent = 0; break;
           case '1': MODE = 1; alertSent = 0; break;
           default: MODE = 0; alertSent = 0; break;
       }

       //LEAKS
       LEAK_VALUE = downlink[23]+downlink[22]*10+downlink[21]*100;
       LEAK = downlink[20];

       ii = 0;
       i=0;
       downlink[0] = '\0';
       downlink[24] = '\0';
       downlink[25] = '\0';
   }
   UARTSendArray(sleep, 7);
   __bis_SR_register_on_exit(LPM3_bits);
}


void UARTSendArray(unsigned char *TxArray, unsigned char ArrayLength){

    while(ArrayLength--){ // Loop until StringLength == 0 and post decrement
         while(!(IFG2 & UCA0TXIFG)); // Wait for TX buffer to be ready for new data
         UCA0TXBUF = *TxArray; //Write the character at the location specified py the pointer
         TxArray++; //Increment the TxString pointer to point to the next character
    }
}

void MakeHexMsg(unsigned int count){

    message[i+6] = TO_HEX(((count & 0xF000) >> 12));
    i++;
    message[i+6] = TO_HEX(((count & 0x0F00) >> 8));
    i++;
    message[i+6] = TO_HEX(((count & 0x00F0) >> 4));
    i++;
    message[i+6] = TO_HEX((count & 0x000F));

}




