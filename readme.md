### How To Compile    

1. j2se version 8 or above        
2. gradle 2.7 or above            
   gradle jar => generate jar file    
       
### StartUp     
1. Modify server.properties in ToolManCoinAirDropServer-xxx.jar file      
"WALLET":your wallet address        
"WALLET_PUBLIC_KEY":wallet public key
"AIRDROP_SERVER":air drop server name or ip     
"AIRDROP_SERVER_PORT": air drop server port     

 
2. Execute
java -Xmx768M -cp .;./ToolManCoinAirDropClient-1.0b1.jar tmc.server.AirDropClient
  
