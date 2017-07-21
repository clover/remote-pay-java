# Clover Connector for Java

Current version: 1.3.1

## Overview

This SDK provides an API with which to allow your Java-based Point-of-Sale (POS) system to interface with a [Clover® Mini device](https://www.clover.com/pos-hardware/mini). From the Mini, merchants can accept payments using: credit, debit, EMV contact and contactless (including Apple Pay), gift cards, EBT (electronic benefit transfer), and more. Learn more about integrations at [clover.com/integrations](https://www.clover.com/integrations).

The Java project includes both a class library (clover-connector-java) and several examples: one using command line, the other an example PoS application using JavaFX, and several simple "getting started" examples. To effectively work with the project you'll need:
- [Gradle](https://gradle.org) (suggested version 3.4).
- [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/) (Java 8 or greater required).

To complete a transaction end to end, we recommend getting a [Clover Mini Dev Kit](http://cloverdevkit.com/collections/devkits/products/clover-mini-dev-kit).

For more developer documentation and information about the Semi-Integration program, please visit our [semi-integration developer documents](https://docs.clover.com/build/integration-overview-requirements/).

## Release Notes
# Version 1.3.1
* Initial capability

* Added support for Custom Activities
* Device status queries to determine the state of the device and payments processed by the device
  * ICloverConnector
    * Added
      * startCustomActivity - start a custom activity on the Clover device and receive a callback when it is done (onCustumActivityResponse)
      * sendMessageToActivity - send and receive messages to a custom activity running on the Clover device (onMessageFromActivity)
      * retrievePayment - query and receive the status of a payment on the device by its external id (onRetrievePaymentResponse)
      * retrieveDeviceStatus - query and receive the status of the device (onRetrieveDeviceStatusResponse)
  * ICloverConnectorListener
    * Added
      * onCustomActivityResponse
      * onMessageFromActivity
      * onRetrievePaymentResponse
      * onRetrieveDeviceStatusResponse

  * CustomActivity
    * apk must be approved and then installed via the Clover App Market
    * clover-cfp-sdk library
      * Added CloverCFPActivity that can be extended
      * Added constants for getting/retrieving activity payload CloverCFP interface
      * Working with Custom Activities...
        * The action of the activity, as defined in the AndroidManifest, should be passed in as part of the request
        * A single text payload can be passed in to the request and retrieved in the intent via com.clover.remote.cfp.CFPActivity.EXTRA_PAYLOAD constant. e.g. "com.clover.remote.terminal.remotecontrol.extra.EXTRA_PAYLOAD"
        * The CustomActivityResponse (onCustomActivityResponse) contains a single text payload, populated from the com.clover.remote.cfp.EXTRA_PAYLOAD extra in the result Intent
        * Block vs Non-Blocking Activities
            * A blocking CustomActivity (CustomActivityRequest.setNonBlocking(boolean)) will either need finish itself, or can be exited via ICloverConnector.resetDevice()
               * For example: Don't want a Sale request to interrupt Collect Customer Information Custom Activity
            * A non-blocking CustomActivity will finish when a new request is made
               * For example: Want a Sale request to interrupt showing Ads Custom Activity

  * ResetDevice now calls back to onResetDeviceResponse with the current status

* Renamed/Added/Removed a number of API operations and request/response objects to establish
  better consistency across platforms

  * ICloverConnector (Operations)
    * Added
      * printImageFromURL
      * initializeConnection (REQUIRED)
      * addCloverConnectorListener
      * removeCloverConnectorListener
      * acceptPayment - (REQUIRED) Takes a payment object - possible response to a ConfirmPaymentRequest
      * rejectPayment - (REQUIRED) Takes a payment object and the challenge that was associated with
                        the rejection - possible response to a ConfirmPaymentRequest
      * retrievePendingPayments - retrieves a list of payments that were taken offline and are pending
                                  server submission/processing.
    * Renamed
      * capturePreAuth - formerly captureAuth
      * showDisplayOrder - formerly displayOrder - this is now the only operation needed
        to display/change order information displayed on the mini
      * removeDisplayOrder - formerly displayOrderDelete
    * Removed
      * displayOrderLineItemAdded - showDisplayOrder now handles this
      * displayOrderLineItemRemoved - showDisplayOrder now handles this
      * displayOrderDiscountAdded - showDisplayOrder now handles this
      * displayOrderDiscountRemoved - showDisplayOrder now handles this
    * Modified
      * SaleRequest, AuthRequest, PreAuthRequest and ManualRefund require ExternalId to be set. (REQUIRED)
        * ExternalId should be unique per transaction allowing the Clover device to detect, and potentially reject, if the same externalID is reused for subsequent transaction requests
      * changed all device action API calls to return void
      * CloverConnecter now requires ApplicationId to be set via configuration/installation of the third party application. This is provided as part of the device configuration that is passed in during the creation of the CloverConnector.
      * Behavior change for RefundPaymentRequest. In the prior versions, a value of zero for the amount field would trigger a refund of the full payment amount. With the 1.0 version, passing zero in the amount field will trigger a validation failure. Use FullRefund:boolean to specify a full refund amount. NOTE: This will attempt to refund the original (full) payment amount, not the remaining amount, in a partial refund scenario.
  * ICloverConnectorListener (Notifications)
    * Added
      * onPaymentConfirmation - (REQUIRED) consists of a Payment and a list of challenges/void reasons
      * onDeviceError - general callback when there is an error communicating with the device
      * onPrintManualRefundReceipt - if disablePrinting=true on the request, this will get called to indicate the POS can print this receipt
      * onPrintManualRefundDeclineReceipt - if disablePrinting=true on the request, this will get called to indicate the POS can print this receipt
      * onPrintPaymentReceipt - if disablePrinting=true on the request, this will get called to indicate the POS can print this receipt
      * onPrintPaymentDeclineReceipt - if disablePrinting=true on the request, this will get called to indicate the POS can print this receipt
      * onPrintPaymentMerchantCopyReceipt - if disablePrinting=true on the request, this will get called to indicate the POS can print this receipt
      * onPrintRefundPaymentReceipt - if disablePrinting=true on the request, this will get called to indicate the POS can print this receipt
      * onRetrievePendingPaymentsResponse - called with the list of payments taken on the device that aren't processed on the server yet
    * Renamed
      * onDeviceDisconnected - formerly onDisconnected
      * onDeviceConnected - formerly on onConnected
      * onDeviceReady - formerly onReady
      * onTipAdjustAuthResponse - formerly onAuthTipAdjustResponse
      * onCapturePreAuthResponse - formerly onPreAuthCaptureResponse
      * onVerifySignatureRequest - formerly onSignatureVerifyRequest
    * Removed
      * onTransactionState
      * onConfigErrorResponse - These are now processed as normal operation responses
      * onError - now handled by onDeviceError or through normal operation responses
      * onDebug
  * Request/Response Objects
    * Added
      * ConfirmPaymentRequest - Contains a Payment and a list of "challenges" from the
        Clover device during payment operations, if there are questions for the merchant
        on their willingness to accept whatever risk is associated with that payment's
        challenge.
      * RetrievePendingPaymentsResponse - Contains a list of PendingPaymentEntry objects,
                                          which have the paymentId and amount for each
                                          payment that has yet to be sent to the server
                                          for processing.
      * PrintManualRefundReceiptMessage - Contains the Credit object to be printed
      * PrintManualRefundDeclineReceiptMessage - Contains the declined Credit object to be printed
      * PrintPaymentReceiptMessage - Contains the Order and Payment to be printed
      * PrintPaymentDeclineReceiptMessage - Contains the declined Payment and reason to be printed
      * PrintPaymentMerchantCopyReceiptMessage - Contains the payment to be printed
      * PrintRefundPaymentReceiptMessage - Contains Payment, Refund and Order
    * Renamed
      * VerifySignatureRequest - formerly SignatureVerifyRequest
      * CapturePreAuthRequest - formerly CaptureAuthRequest
      * VoidPaymentRequest - formerly VoidTransactionRequest
      * CloseoutRequest - formerly separate field-level parameters
      * TipAdjustAuthResponse - formerly AuthTipAdjustResponse
    * Removed
      * ConfigErrorResponse - These are now processed as normal operation
    * Modified
      * All Response Messages now return the following:​
        * Success:boolean
        * Result:enum [SUCCESS|FAIL|CANCEL|ERROR|UNSUPPORTED] FAIL - failed to process with values/properties as-is CANCEL - canceled, retry could work ERROR - un expected exception occurred UNSUPPORTED - merchant config won't allow the request
        * Reason:String optional information about result value, if not SUCCESS
        * Message:String optional detail information about the result value, if not success
      * SaleResponse, AuthResponse and PreAuthResponse have 3 new flags (e.g. The payment gateway may force an AuthRequest to a SaleRequest)
      * IsSale:boolean - true if the payment is closed
      * IsAuth:boolean - true if the payment can be tip adjusted before closeout
      * IsPreAuth:boolean - true if the payment needs to be "captured" before closeout will close it
* voidPayment operation fix to verify connection status and check for void request
  acknowledgement from the Clover device prior to issuing a successful response
* Added DefaultCloverConnectorListener, which automatically accepts signature if a verify
  signature request is received
* Behavior change for RefundPaymentRequest - In the prior versions, a value of zero for
  the amount field would trigger a refund of the full payment amount. With the 1.1 version,
  passing zero in the amount field will trigger a validation failure.
  Set fullRefund:boolean to `true` to specify a full refund. NOTE: This will attempt to refund
  the original (full) payment amount, not the remaining amount, in a partial refund scenario.
* CloverConnecter now requires ApplicationId to be set via configuration of the
  third party application. This is provided as part of the device configuration
  that is passed in during the creation of the CloverConnector.  The String input parameter of
  "applicationId", which is passed in when instantiating the DefaultCloverDevice, should be
  set using the format of <company specific package>:<version> e.g. com.clover.ExamplePOS:1.2
* Modified remote pay so prompts to take orders offline and flagging duplicate orders appear only in merchant facing mode.
* Added ability to query pending payments.
* PreAuthRequest - no longer prompts for signature, signature verification or receipt options on the customer facing device.
* Changes to support certain transaction level overrides have been included in this version. To facilitate the addition of the new override capabilities, some new options were added to the SaleRequest & TransactionRequest classes.
  * TransactionRequest - extended by SaleRequest, AuthRequest, PreAuthRequest & ManualRefundRequest
    * (Long) signatureThreshold was added to enable the override of the signature threshold in the Merchant settings for payments.
    * (DataEntryLocation) signatureEntryLocation was added to enable the override of the Signature Entry Location in the Merchant Signature Settings for Payments.  Value of NONE will cause the device to skip requesting a signature for the specified transaction.
    Possible values:
      * ON_SCREEN
      * ON_PAPER
      * NONE
    * (Boolean) disableReceiptSelection was added to enable bypassing the customer-facing receipt selection screen.
    * (Boolean) disableDuplicateChecking was added to enable bypassing any duplicate transaction logic and associated requests for confirmation.
    * (Boolean) autoAcceptPaymentConfirmations was added to enable the automatic acceptance of any payment confirmations that might be applicable for the given transaction (e.g. offline payment confirmation).  This override prevents any payment confirmation requests from being transmitted back to the calling program and continues processing as if a confirmPayment() was initiated by the caller.
    * (Boolean) autoAcceptSignature was added to enable the automatic acceptance of a signature (on screen or on paper) if applicable for the given transaction.  This override prevents signature confirmation requests from being transmitted back to the calling program and continues processing as if a acceptSignature() was initiated by the caller.
  * SaleRequest (extends TransactionRequest)
    * (TipMode) tipMode was added to specify the location from which to accept the tip.  You can now provide a tip up front or specify no tip to override the merchant configured (on screen/on paper) settings. **NOTE** If you desire to take the tip on paper, populate the signatureEntryLocation with ON_PAPER
    Possible values:
      * TIP_PROVIDED - tip is included in the request
      * ON_SCREEN_BEFORE_PAYMENT - valid when requested via Mini or Mobile
      * NO_TIP - tip will not be requested for this payment

## Getting Started
* clover-connector-java and clover-connector-java-example-cli require Java 7 or higher
* clover-connector-java-example-pos requires Java 8 or higher (uses JavaFX)
* Use `gradle assemble` to build the project from command line (comment out `clover-connector-java-example-pos` in settings.gradle if using Java 7); you may also import the project into IntelliJ IDEA using the root build.gradle file

## Getting Connected
* Make sure your Clover Dev Kit and Java examples app are on the same network submask and have ports unblocked.
* Install Network Pay Display on the Clover device
* Start Network Pay Display; this will also install Pay Display app if not already present on the device
* Start clover-connector-java-example-pos (`com.clover.remote.client.lib.example.ExamplePOS` main class) or clover-connector-java-example-cli and enter the address as displayed on the Clover device
* The command line example can be started via command line after building with: `java -jar clover-connector-java-example-cli-0.1.1-SNAPSHOT.jar`

### Working with Network Pay Display
* Install Network Pay Display  
`adb install com.clover.remote.protocol.lan-75.apk`

* Uninstall Network Pay Display  
`adb uninstall com.clover.remote.protocol.lan`

* Kill Network Pay Display  
`adb shell ps | grep com.clover.remote.protocol.lan`   
`u0_a73    13964 149   731256 46040 ffffffff 400ff6fc S com.clover.remote.protocol.lan`      
`adb shell kill 13964`


## Working with the SDK

If the project libaries are successfully built and synced using Gradle you should see no errors from your IDE when importing or opening the project. Transactions between the device and a Java test apps will work through an instance of a CloverConnector object. Instantiating the object will require a configuration scheme which is usually a web socket device configuration. The next step is to setup a connection listener. Here is an example:
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
