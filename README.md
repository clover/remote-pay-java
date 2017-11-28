![alt text](https://www.clover.com/assets/images/public-site/press/clover_primary_gray_rgb.png)


# Clover Connector for Java POS integration

## Version
Current version: 1.4.0

## Overview

This SDK allows your Java-based point-of-sale (POS) system to communicate with a [Clover® payment device](https://www.clover.com/pos-hardware/) and process payment transactions. Learn more about [Clover integrations](https://www.clover.com/integrations).

The Java project includes a class library (clover-connector-java) and several examples. One example uses the command line, and another is a PoS application that uses JavaFX. There are also several "getting started" examples. 

To work with the project effectively, you will need:
- [Gradle](https://gradle.org) (suggested version: 3.4).
- The [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/) (Java 8 or greater required).
- To experience transactions end-to-end from the merchant and customer perspectives, we also recommend ordering a [Clover DevKit](http://cloverdevkit.com/collections/devkits/products/clover-mini-dev-kit).

## Getting started
* clover-connector-java requires Java 7 or higher.
* clover-connector-java-example-pos and clover-connector-java-examples require Java 8 or higher (uses JavaFX).
* Use `gradle assemble` to build the project from the command line. (Comment out `clover-connector-java-example-pos` in settings.gradle if using Java 7.) You may also import the project into IntelliJ IDEA using the root build.gradle file.

## Getting connected
1. Make sure your Clover DevKit and Java Example App are on the same network submask and have ports unblocked.
2. Install the Secure Network Pay Display app on the Clover device. **NOTE:** The Secure Network Pay Display app is currently in Beta. If you're interested in using a local network connection, please contact Clover's Developer Relations team at [semi-integrations@clover.com](mailto:semi-integrations@clover.com). 
3. Start the Secure Network Pay Display app. This will also install the Pay Display app if it's not already present on the device.
4. Start clover-connector-java-example-pos (`com.clover.remote.client.lib.example.ExamplePOS` main class) and enter the address as displayed on the Clover device.

## Additional resources

* [Release Notes](https://github.com/clover/remote-pay-java/releases)
* [API Documentation](http://clover.github.io/remote-pay-java/1.4.0/docs/index.html)
* [Clover Developer Community](https://community.clover.com/index.html)


