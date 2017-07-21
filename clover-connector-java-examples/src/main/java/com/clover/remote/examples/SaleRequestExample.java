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

import com.clover.remote.Challenge;
import com.clover.remote.client.CloverConnector;
import com.clover.remote.client.DefaultCloverConnectorListener;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.messages.ConfirmPaymentRequest;
import com.clover.remote.client.messages.SaleRequest;
import com.clover.remote.client.messages.SaleResponse;
import com.clover.remote.client.messages.VerifySignatureRequest;
import com.clover.sdk.v3.payments.Payment;

/**
 * This example class illustrates how to connect to a clover device and processes a sale transaction.  The dialog
 * which occurs is:
 *    1) Create CloverConnector based upon the specified configuration
 *    2) Register the CloverConnectorListener with the CloverConnector
 *    3) Initialize the CloverConnector via initializeConnection() method
 *      a) If network connection configured, input the pairing code provided by the device callback
 *    4) Handle onDeviceReady() callback from device indicating connection was made
 *    5) Create a SaleRequest consisting of an amount and a UNIQUE ID and call the sale() API method to request the
 *    device to begin processing the sale.  Depending on merchant settings, card used in the transaction, device
 *    configuration, etc, the device may prompt for
 *      a) Payment (including possible Credit/Debit selection)
 *      b) Signature
 *      c) Notify merchant signature verification (NOTE:  This is handled by the onVerifySignatureRequest() callback
 *      which is configured in this handler to automatically accept signature verification
 *      d) Receipt choice
 *      NOTE:  During sale processing, the device may challenge the transaction (double charge, offline payment, etc).
 *      The registered listener MUST handle the onConfirmPaymentRequest() callback.
 *    6) After all device prompts have completed, the onSaleResponse() callback is made by the device which
 *    provide the status of the sale request
 *    7) Close the underlying connection via dispose() method
 */
public class SaleRequestExample {

  private static ICloverConnector cloverConnector;

  private static SaleRequest pendingSale;

  private static void exitSampleApp() {
    synchronized (cloverConnector) {
      cloverConnector.notifyAll();
    }
  }

  public static void main(String[] args) {
    // NOTE:  Replace the hard-coded IP address with the correct address from your device
    cloverConnector = new CloverConnector(SampleUtils.getNetworkConfiguration("192.168.0.126"));

    cloverConnector.addCloverConnectorListener(new DefaultCloverConnectorListener(cloverConnector) {
      @Override
      public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
        System.out.println("Confirm Payment Request");

        Challenge[] challenges = request.getChallenges();
        if (challenges != null && challenges.length > 0)
        {
          for (Challenge challenge : challenges) {
            System.out.println("Received a challenge: " + challenge.type);
          }
        }

        System.out.println("Automatically processing challenges");
        cloverConnector.acceptPayment(request.getPayment());
      }

      @Override
      public void onDeviceReady(MerchantInfo merchantInfo) {
        super.onDeviceReady(merchantInfo);
        try {
          pendingSale = new SaleRequest(1000, SampleUtils.getNextId());
          System.out.println("Making sale request:");
          System.out.println("  External ID: " + pendingSale.getExternalId());
          System.out.println("  Amount: " + pendingSale.getAmount());
          System.out.println("  Tip Amount: " + pendingSale.getTipAmount());
          System.out.println("  Tax Amount: " + pendingSale.getTaxAmount());
          cloverConnector.sale(pendingSale);
        } catch (Exception ex) {
          System.err.println("Error submitting sale request");
          ex.printStackTrace();
          exitSampleApp();
        }
      }

      @Override
      public void onSaleResponse(SaleResponse response) {
        try {
          if (response.isSuccess()) {
            Payment payment = response.getPayment();
            if (payment.getExternalPaymentId().equals(pendingSale.getExternalId())) {
              System.out.println("Sale Request Successful");
              System.out.println("  ID: " + payment.getId());
              System.out.println("  External ID: " + payment.getExternalPaymentId());
              System.out.println("  Order ID: " + payment.getOrder().getId());
              System.out.println("  Amount: " + payment.getAmount());
              System.out.println("  Tip Amount: " + payment.getTipAmount());
              System.out.println("  Tax Amount: " + payment.getTaxAmount());
              System.out.println("  Offline: " + payment.getOffline());
              System.out.println("  Authorization Code: " + payment.getCardTransaction().getAuthCode());
              System.out.println("  Card Type: " + payment.getCardTransaction().getCardType());
              System.out.println("  Last 4: " + payment.getCardTransaction().getLast4());
            } else {
              System.err.println("Sale Request/Response mismatch - " + pendingSale.getExternalId() + " vs " + payment.getExternalPaymentId());
            }
          } else {
            System.err.println("Sale Request Failed - " + response.getReason());
          }
        } catch (Exception ex) {
          System.err.println("Error handling sale response");
          ex.printStackTrace();
        }
        exitSampleApp();
      }

      @Override
      public void onVerifySignatureRequest(VerifySignatureRequest request) {
        super.onVerifySignatureRequest(request);
        System.out.println("Verify Signature Request - Signature automatically accepted by default");
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
