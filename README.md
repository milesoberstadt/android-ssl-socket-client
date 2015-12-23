# android-ssl-socket-client
Pretty much the simplest SSL socket client I can make for Android. Plays nice with Twisted for Python.

It was a bit of a pain to make an SSL socket client for Android, so I thought I'd share my success with the world. Most of the problem was making the keystore (I kept getting the "No peer certificate" error). There's probably a better way of doing this, but after 3 days of struggling, this method works. 

# TODO:
Add network discovery for finding the host on your network

Make UI a little more responsive

# Usage
To start the server:
    ```
    java -jar server/echoserv_ssl.py
    ```

# Requirements
Portecle (http://portecle.sourceforge.net/)
Java (for keytool)
openssl
Command line experience

I tested all this in Linux Mint, your mileage may very by OS.

# Building the certificates
I wanted to use the Twisted python library for my server, which uses a .pem file with both the certificate and the private key, and Android requires a BKS keystore. We'll make both of those below.

1. Create the client and server private keys (I used the same password for both, we'll be removing the password from the server key at the end of this):
    ```
    openssl genrsa -des3 -out client_key.pem 2048
    openssl genrsa -des3 -out server_key.pem 2048
    ```

2. Create self-signed certificates for both private keys (answer all the prompts)
    ```
    openssl req -new -x509 -key client_key.pem -out client.pem -days 1000
    openssl req -new -x509 -key server_key.pem -out server.pem -days 1000
    ```

3. Import these certificates into Java keystores
    ```
    keytool -importcert -trustcacerts -keystore clienttruststore.jks -storetype jks -file server.pem 
    keytool -importcert -trustcacerts -keystore  servertruststore.jks -storetype jks -file client.pem
    ```

4. Combine the keys and certificates into PKCS12 keystores
    ```
    openssl pkcs12 -export -inkey client_key.pem -in client.pem -out client.p12
    openssl pkcs12 -export -inkey server_key.pem -in server.pem -out server.p12
    ```

    From what I've gathered, PKCS12 is the most widely used keystore format, so from here we'll make our target formats.

5. Convert the client.p12 file to BKS.
Android favors a BKS format, and the easiest way to do that is to use Portecle (http://portecle.sourceforge.net/). Once you have that downloaded and unzipped, run it in your terminal of choice with 
    ```
    java -jar portecle.jar 
    ```

    Next, go to File > Open Keystore File... and open the client.p12 keystore you just made. Enter the password you used when creating it, then go to Tools > Change Keystore Type > BKS, and enter your password again. 

    We also need to add the server's certificate at this point, so go to Tools > Import Trusted Certificate... and select server.pem. Click "Okay" twice, then Yes to accept the certificate, give it any name you like. 

    Then go to File > Save Keystore as... and save to something like client_finished.bks

6. Convert the server.p12 file to PEM
Since the Twisted python example expects an unencryped .pem file, convert to a PEM file with the -nodes flag
    ```
    openssl pkcs12 -in server.p12 -nodes -out server_finished.pem
    ```


Your certificate and keystore are now done!

Replace the included keystore file at app/src/main/res/raw with the one you just generated, and place the server_finished.pem somewhere the server script can find it.
