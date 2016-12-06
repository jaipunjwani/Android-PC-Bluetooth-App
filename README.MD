#DESCRIPTION
This application is my exploration of programming using Bluetooth, motivated by the following synopsis:

Let's say that you wish to access a file on your Desktop at home, but you have no internet connectivity (because your WiFi adapter sucks). But your grandmother's phone is at home. Can you use that device to set up a 3-way file transfer system?


Please note that this project is currently in progress. I have broken it down into 3 major stages, and have indicated my progress thus far. 

##Stage 1 (current stage)
The first part of this project is to connect two (Android) devices, one with the user (client), and the other nearby to the user's computer (server), via a simple mobile application. I am using PubNub's free push notification service to send a request from the client device to the server's helper device, which would then handle the file transfer using Bluetooth (see Stage 2). 

##Stage 2
The second part involves the helper Android device connecting to the computer, or the server, to request and complete a file transfer. The computer will have a Java program listening for Bluetooth requests and will then accept the incoming request from the helper device. Java's Bluetooth APIs will assist in actually transferring the file to the helper device from the computer.

##Stage 3
Finally, the helper device must transfer the file to the client. Rather than a P2P transfer, it will use Google Drive's API to upload the file to a storage account of the user's choice. The file will then be accessible by the client, either by manually pulling the file from Google Drive, or setting some sort of file syncing.
