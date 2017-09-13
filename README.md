![alt text](https://www.clover.com/assets/images/public-site/press/clover_primary_gray_rgb.png)


# Clover Connector for Java POS integration

## Version
Current version: 1.3.2

## Overview

This SDK allows your Java-based point-of-sale (POS) system to communicate with a [Clover® payment device](https://www.clover.com/pos-hardware/) and process payment transactions. Learn more about [Clover integrations](https://www.clover.com/integrations).

The Java project includes a class library (clover-connector-java) and several examples. One example uses the command line, and another is a PoS application that uses JavaFX. There are also several "getting started" examples. 

To work with the project effectively, you will need:
- [Gradle](https://gradle.org) (suggested version: 3.4).
- The [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/) (Java 8 or greater required).
- To experience transactions end-to-end from the merchant and customer perspectives, we also recommend ordering a [Clover DevKit](http://cloverdevkit.com/collections/devkits/products/clover-mini-dev-kit).

## Getting started
* clover-connector-java and clover-connector-java-example-cli require Java 7 or higher.
* clover-connector-java-example-pos requires Java 8 or higher (uses JavaFX).
* Use `gradle assemble` to build the project from the command line. (Comment out `clover-connector-java-example-pos` in settings.gradle if using Java 7.) You may also import the project into IntelliJ IDEA using the root build.gradle file.

## Getting connected
1. Make sure your Clover DevKit and Java Example App are on the same network submask and have ports unblocked.
2. Install the Secure Network Pay Display app on the Clover device.
3. Start the Secure Network Pay Display app. This will also install the Pay Display app if it's not already present on the device.
4. Start clover-connector-java-example-pos (`com.clover.remote.client.lib.example.ExamplePOS` main class) or clover-connector-java-example-cli and enter the address as displayed on the Clover device.
5. To start the command-line example, you will need to build it with the command `java -jar clover-connector-java-example-cli-0.1.1-SNAPSHOT.jar` first.

## Working with the Secure Network Pay Display app
1. Install the Secure Network Pay Display app.
2. On the command line, type `adb install com.clover.remote.protocol.lan-75.apk`.

### Uninstalling the Secure Network Pay Display app

To uninstall the Secure Network Pay Display app, enter the following on the command line:

`adb uninstall com.clover.remote.protocol.lan`

### Killing the Secure Network Pay Display app

To stop the app:

1. Type `adb shell ps | grep com.clover.remote.protocol.lan` on the command line.
2. Next, type `u0_a73    13964 149   731256 46040 ffffffff 400ff6fc S com.clover.remote.protocol.lan`.      
3. Finally, type `adb shell kill 13964`.

## Working with the SDK

If you've successfully built and synced the project libaries using Gradle, your IDE shouldn't display any errors when importing or opening the project. Transactions between the device and a Java test app will work through an instance of a CloverConnector object. Instantiating the object will require a configuration scheme, which is usually a webSocket device configuration. The next step is to set up a connection listener. Here is an example:

```
URI uri = null;
try {
    if (cloverConnector != null) {
        cloverConnector.dispose();
    }
    uri = new URI(urlToMini); // e.g. "wss://YOUR_DEVICE_IP:YOUR_DEVICE_PORT/remote_pay"

    KeyStore trustStore  = KeyStore.getInstance("PKCS12");
    InputStream trustStoreStream = CloverDeviceConfiguration.class.getResourceAsStream("/certs/clover_cacerts.p12");
    String TRUST_STORE_PASSWORD = "clover";
    trustStore.load(trustStoreStream, TRUST_STORE_PASSWORD.toCharArray());
    
    cloverConnector = new CloverConnector(new WebSocketCloverDeviceConfiguration(uri, "com.yourcompany.appid", trustStore, POS_NAME, DEVICE_NAME, null) {
       @Override public void onPairingCode(String pairingCode) {
          // need to display pairingCode to the POS system
          System.out.println("    > Entering Pairing Code on Device: " + pairingCode);
       }
      
       @Override public void onPairingSuccess(String authToken) {
          // can store this authToken for future connections, so pairing isn't required
          Preferences.userNodeForPackage(cls).put("AUTH_TOKEN", authToken);
       }
    };

    cloverConnector.addCloverConnectorListener(new DefaultCloverConnectorListener() {
       @Override
       public void onSaleResponse(SaleResponse response) {
          ...
       }
       @Override
       public void onAuthResponse(AuthResponse response) {
          ...
       }
       @Override
       public void confirmPaymentRequest(ConfirmPaymentRequest request) {
          // prompts for approval for duplicate or offline payments
          // call cloverConnector.acceptPayment(request.getPayment()) or
          // cloverConnector.rejectPayment(request.getPayment(), request.getChallenges().get(n))
          ...
       }
       @Override
       public void onDeviceDisconnected() {
          // device disconnected
       }
       public void onDeviceConnected() {
          // device connected, but not ready
       }
       @Override
       public void onDeviceReady(MerchantInfo merchantInfo) {
          // once the device is ready, other methods on CloverConnector
          // can be called. i.e.
          // SaleRequest saleRequest = new SaleRequest(2500, "GEX6GS3f5FDSEGS");
          // cloverConnector.sale(saleRequest);
       }
       // override other methods as needed
       ...
    }
    cloverConnector.initializeConnection();
} catch (URISyntaxException e) {
    e.printStackTrace();
}
```

## Additional resources

* [Release Notes](https://github.com/clover/remote-pay-java/releases)
* [API Documentation](http://clover.github.io/remote-pay-java/1.3.1/docs/index.html)
* [Clover Developer Community](https://community.clover.com/index.html)


