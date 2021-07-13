### How To Compile    

1. j2se version 8 or above        
2. gradle 2.7 or above            
   gradle jar => generate jar file    
       

### StartUp     
1. Modify client.properties     
"WALLET":your wallet address        
"WALLET_PUBLIC_KEY":wallet public key    
"EXPLORER_NAME": your web browser "firefox" , "explorer" , or "chrome"    
"AIRDROP_TRANSFER_COIN_NUMBER": minimum transfer coin    

 
2. Execute
java -Xmx768M -cp .;./ToolManCoinAirDropClient-1.0.jar tmc.server.AirDropClient
  
