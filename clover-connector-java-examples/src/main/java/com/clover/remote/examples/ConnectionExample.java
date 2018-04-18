/*
 * Copyright (C) 2017 Clover Network, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clover.remote.examples;

import com.clover.remote.client.CloverConnectorFactory;
import com.clover.remote.client.DefaultCloverConnectorListener;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.messages.ConfirmPaymentRequest;

/**
 * This example class illustrates how to create and terminate a connection to a clover device.  The dialog
 * which occurs is:
 *    1) Create CloverConnector based upon the specified configuration
 *    2) Register the CloverConnectorListener with the CloverConnector
 *    3) Initialize the CloverConnector via initializeConnection() method
 *      a) If network connection configured, input the pairing code provided by the device callback
 *    4) Handle onDeviceReady() callback from device indicating connection was made
 *    5) Close the underlying connection via dispose() method
 */
public class ConnectionExample {

  private static ICloverConnector cloverConnector;

  private static void exitSampleApp() {
    synchronized (cloverConnector) {
      cloverConnector.notifyAll();
    }
  }

  public static void main(String[] args) {
    // NOTE:  Replace the hard-coded IP address with the correct address from your device
    cloverConnector = CloverConnectorFactory.createICloverConnector(SampleUtils.getNetworkConfiguration("10.249.254.159"));

    cloverConnector.addCloverConnectorListener(new DefaultCloverConnectorListener(cloverConnector) {
      @Override
      public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
      }

      @Override
      public void onDeviceReady(MerchantInfo merchantInfo) {
        super.onDeviceReady(merchantInfo);
        System.out.println("Connection Successful!!!");
        exitSampleApp();
      }
    });

    cloverConnector.initializeConnection();

    synchronized (cloverConnector) {
      try {
        cloverConnector.wait();
      } catch (Exception ex) {
        System.out.println("Exit signaled");
      }
    }

    cloverConnector.dispose();
    System.exit(0);
  }
}
