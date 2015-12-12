//gcc -o tcpemtest1 tcpemergency.c -lpthread -lwiringPi
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <wiringPi.h>
#include <pthread.h>

#define buzzer 12
#define ledred1 13
#define ledblu1 14
#define ledyel1 0
#define ledred2 9
#define ledblu2 7
#define ledyel2 8

void cleanbuz();
void cleanled1();
void cleanled2();

pthread_t p_thread[2];
int thr_id;

void *t_led1(void *fl){
 pthread_cleanup_push(cleanled1,NULL);
 while(1){
	  digitalWrite(ledred1,1);
	  delay(150);
	  digitalWrite(ledred1,0);
	  delay(150);
	  digitalWrite(ledblu1,1);
	  delay(150);
	  digitalWrite(ledblu1,0);
	  delay(150);
	  digitalWrite(ledyel1,1);
	  delay(150);
	  digitalWrite(ledyel1,0);
	  delay(150);
 }
 pthread_cleanup_pop(1);
}

void *t_led2(void *fl){
 pthread_cleanup_push(cleanled2,NULL);
 while(1){
	  digitalWrite(ledred2,1);
	  delay(150);
	  digitalWrite(ledred2,0);
	  delay(150);
	  digitalWrite(ledblu2,1);
	  delay(150);
	  digitalWrite(ledblu2,0);
	  delay(150);
	  digitalWrite(ledyel2,1);
	  delay(150);
	  digitalWrite(ledyel2,0);
	  delay(150);
 }
 pthread_cleanup_pop(1);
}

void *t_buz(void *unused){
 pthread_cleanup_push(cleanbuz,NULL);
 while(1){
  digitalWrite(buzzer,HIGH);
  delay(100);
  digitalWrite(buzzer,LOW);
  delay(100);
 }
 pthread_cleanup_pop(1);
}

int getData(int sockfd)
{
 char buffer[32];
 int n;
 if((n=read(sockfd,buffer,31))<0)
printf("Error socket was not read.\n");
buffer[n]='\0';
return atoi(buffer);
}


int main(void){

/*
system("gpio readall");
sleep(3);

system("gpio mode 12 OUTPUT");
system("gpio mode 13 OUTPUT");
system("gpio mode 14 OUTPUT");

pinMode(buzzer, OUTPUT);
pinMode(ledred, OUTPUT);
pinMode(ledblu, OUTPUT);

system("gpio readall");
sleep(3);
*/
if(wiringPiSetup()==-1)
return 1;

pinMode(buzzer, OUTPUT);
pinMode(ledred1, OUTPUT);
pinMode(ledblu1, OUTPUT);
pinMode(ledyel1, OUTPUT);
pinMode(ledred2, OUTPUT);
pinMode(ledblu2, OUTPUT);
pinMode(ledyel2, OUTPUT);

digitalWrite(ledred1,0);
digitalWrite(ledblu1,0);
digitalWrite(ledyel1,0);
digitalWrite(ledred2,0);
digitalWrite(ledblu2,0);
digitalWrite(ledyel2,0);
digitalWrite(buzzer,0);

int flag=0;

int sockfd, newsockfd, portno=51720,clilen;
char buffer[256];
struct sockaddr_in serv_addr, cli_addr;
int n;
int data;

sockfd=socket(AF_INET, SOCK_STREAM,0);
if(sockfd<0)
printf("Error\n");
bzero((char*)&serv_addr, sizeof(serv_addr));

serv_addr.sin_family=AF_INET;
serv_addr.sin_addr.s_addr=INADDR_ANY;
serv_addr.sin_port=htons(portno);

if(bind(sockfd,(struct sockaddr *) &serv_addr, sizeof(serv_addr))<0)
printf("Error\n");
listen(sockfd,5);
clilen=sizeof(cli_addr);


while(1){
printf("Emergency server waiting...\n");
if((newsockfd=accept(sockfd,(struct sockaddr *) &cli_addr, (socklen_t*) &clilen))<0)
printf("Error\n");
printf("Emergency situation!!!\n");
while(1){
data=getData(newsockfd);
printf("getdata!%d\n",data);
	if(data<=0)
		break;
	if(flag==0){
		flag=1;
		thr_id=pthread_create(&p_thread[0],NULL,t_led1,NULL);
		if(thr_id<0)
			exit(0);
		thr_id=pthread_create(&p_thread[1],NULL,t_buz,NULL);
		if(thr_id<0)
			exit(0);
		thr_id=pthread_create(&p_thread[2],NULL,t_led2,NULL);
		if(thr_id<0)
			exit(0);
	}
delay(1000);
}
pthread_cancel(p_thread[0]);
pthread_join(p_thread[0],NULL);
pthread_cancel(p_thread[1]);
pthread_join(p_thread[1],NULL);
pthread_cancel(p_thread[2]);
pthread_join(p_thread[2],NULL);
flag=0;
close(newsockfd);
}
return 0;
}

void cleanled1(){
digitalWrite(ledred1,0);
digitalWrite(ledblu1,0);
digitalWrite(ledyel1,0);
}

void cleanled2(){
digitalWrite(ledred2,0);
digitalWrite(ledblu2,0);
digitalWrite(ledyel2,0);
}

void cleanbuz(){
digitalWrite(buzzer,0);
}
