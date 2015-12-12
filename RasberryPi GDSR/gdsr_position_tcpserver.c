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

#define trigPin 5
#define echoPin 4

void sendData(int sockfd, int x)
{
 int n;
 
 char buffer[32];
 sprintf(buffer, "%d\n", x);
 if((n=write(sockfd, buffer, strlen(buffer)))<0)
printf("Error socket was not writed.\n");
buffer[n]='\0';
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
int main(void)
{
 int sockfd, newsockfd, portno = 51717,clilen;
char buffer[256];
struct sockaddr_in serv_addr, cli_addr;
int n;
int data;
int sensor;
if(wiringPiSetup()==-1)
return 1;

pinMode(trigPin, OUTPUT);
pinMode(echoPin, INPUT);

sockfd=socket(AF_INET, SOCK_STREAM, 0);
if(sockfd < 0)
printf("Error socket not init.\n");
bzero((char *)&serv_addr, sizeof(serv_addr));

serv_addr.sin_family = AF_INET;
serv_addr.sin_addr.s_addr = INADDR_ANY;
serv_addr.sin_port=htons(portno);

if(bind(sockfd, (struct sockaddr *) &serv_addr,
sizeof(serv_addr))<0)
printf("Error soket does not bind.\n");
listen(sockfd,5);
clilen=sizeof(cli_addr);
close(newsockfd);
	while(1){
	printf("Position detecter waiting...\n");
		if((newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, (socklen_t*) &clilen))<0)
		printf("Error dose not connected.\n");
		printf("========Detecter activate=======\n");
			while(1){
				data=getData(newsockfd);
				printf("data %d\n",data);
				if(data<0)
					break;

				digitalWrite(trigPin, LOW);
				usleep(2);
				digitalWrite(trigPin, HIGH);
				usleep(20);
				digitalWrite(trigPin, LOW);

				while(digitalRead(echoPin)==LOW);
				long startTime=micros();
				while(digitalRead(echoPin)==HIGH);
				long travelTime=micros()-startTime;

				int distance=(travelTime/58);

				sendData(newsockfd, distance);
				delay(1000);
			}
			close(newsockfd);
	}
return 0;
}

