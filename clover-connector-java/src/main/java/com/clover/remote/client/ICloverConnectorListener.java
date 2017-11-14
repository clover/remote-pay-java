/*
 * Copyright (C) 2016 Clover Network, Inc.
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

package com.clover.remote.client;

import com.clover.remote.client.messages.AuthResponse;
import com.clover.remote.client.messages.CapturePreAuthResponse;
import com.clover.remote.client.messages.CloseoutResponse;
import com.clover.remote.client.messages.CloverDeviceErrorEvent;
import com.clover.remote.client.messages.CloverDeviceEvent;
import com.clover.remote.client.messages.CustomActivityResponse;
import com.clover.remote.client.messages.PrintJobStatusResponse;
import com.clover.remote.client.messages.RetrievePaymentResponse;
import com.clover.remote.client.messages.MessageFromActivity;
import com.clover.remote.client.messages.ConfirmPaymentRequest;
import com.clover.remote.client.messages.ManualRefundResponse;
import com.clover.remote.client.messages.PreAuthResponse;
import com.clover.remote.client.messages.PrintManualRefundDeclineReceiptMessage;
import com.clover.remote.client.messages.PrintManualRefundReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentDeclineReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentMerchantCopyReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentReceiptMessage;
import com.clover.remote.client.messages.PrintRefundPaymentReceiptMessage;
import com.clover.remote.client.messages.ReadCardDataResponse;
import com.clover.remote.client.messages.RefundPaymentResponse;
import com.clover.remote.client.messages.ResetDeviceResponse;
import com.clover.remote.client.messages.RetrieveDeviceStatusResponse;
import com.clover.remote.client.messages.RetrievePendingPaymentsResponse;
import com.clover.remote.client.messages.RetrievePrintersResponse;
import com.clover.remote.client.messages.SaleResponse;
import com.clover.remote.client.messages.TipAdjustAuthResponse;
import com.clover.remote.client.messages.VaultCardResponse;
import com.clover.remote.client.messages.VerifySignatureRequest;
import com.clover.remote.client.messages.VoidPaymentResponse;
import com.clover.remote.message.TipAddedMessage;

/**
 * These are the methods to implement for intercepting messages that are sent from a Clover device.
 */
@SuppressWarnings("unused")
public interface ICloverConnectorListener {

  /**
   * Called when the Clover device transitions to a new screen or activity. The 
   * CloverDeviceEvent passed in will contain an event type, a description, and a list of 
   * available InputOptions.
   *
   * @param deviceEvent The device event.
   */
  void onDeviceActivityStart(CloverDeviceEvent deviceEvent);

  /**
   * Called when the Clover device transitions away from a screen or activity. The 
   * CloverDeviceEvent passed in will contain an event type and description. 
   *
   * <p>
   * <b>Note:</b> The start and end events are not guaranteed to process in order. The 
   * event type should be used to make sure these events are paired.
   *
   * @param deviceEvent The device event.
   */
  void onDeviceActivityEnd(CloverDeviceEvent deviceEvent);

  /**
   * Called when an error occurs while trying to send messages to the Clover device.
   *
   * @param deviceErrorEvent The device error event.
   */
  void onDeviceError(CloverDeviceErrorEvent deviceErrorEvent);

  /**
   * Called in response to a PreAuth() request. 
   *
   * <p>
   * <b>Note:</b> The boolean isPreAuth flag in the PreAuthResponse indicates whether 
   * CapturePreAuth() can be called for the returned Payment. If the isPreAuth flag is 
   * false and the isAuth flag is true, then the payment gateway coerced the PreAuth() 
   * request to an Auth. 
   * The payment will need to be voided or it will be automatically captured at closeout.
   *
   * @param response The response to the transaction request.
   */
  void onPreAuthResponse(PreAuthResponse response);

  /**
   * Called in response to an Auth() request. 
   *
   * <p>
   * <b>Note:</b> An Auth transaction may come back as a final Sale, depending on the 
   * payment gateway. 
   * The AuthResponse has a boolean isAuth flag that indicates whether the Payment can 
   * still be tip-adjusted.
   *
   * @param response The response to the transaction request.
   */
  void onAuthResponse(AuthResponse response);

  /**
   * Called in response to a tip adjustment for an Auth transaction. 
   * Contains the tipAmount if successful.
   *
   * @param response The response to the transaction request.
   */
  void onTipAdjustAuthResponse(TipAdjustAuthResponse response);

  /**
   * Called in response to a CapturePreAuth() request. 
   * Contains the new Amount and TipAmount if successful.
   *
   * @param response The response to the transaction request.
   */
  void onCapturePreAuthResponse(CapturePreAuthResponse response);

  /**
   * Called when the Clover device requests verification for a user's on-screen signature. 
   * The Payment and Signature will be passed in.
   *
   * @param request The verification request.
   */
  void onVerifySignatureRequest(VerifySignatureRequest request);

  /**
   * Called when the Clover device encounters a Challenge at the payment gateway 
   * and requires confirmation. A Challenge is triggered by a potential duplicate Payment 
   * (DUPLICATE_CHALLENGE) or an offline Payment (OFFLINE_CHALLENGE). 
   * The device sends a ConfirmPaymentRequest() asking the merchant 
   * to either AcceptPayment() or RejectPayment().
   *
   * <p>
   * <b>Note:</b> Duplicate Payment Challenges are raised when multiple Payments are made 
   * with the same card type and last four digits within the same hour. For this reason, 
   * we recommend that you do not programmatically call CloverConnector.RejectPayment() 
   * on all instances of DUPLICATE_CHALLENGE. For more information, see 
   * <a href="https://docs.clover.com/build/working-with-challenges/">Working with 
   * Challenges</a>. 
   *
   * @param request The request for confirmation.
   */
  void onConfirmPaymentRequest(ConfirmPaymentRequest request);

  /**
   * Called in response to a Closeout() request.
   *
   * @param response The response to the transaction request.
   */
  void onCloseoutResponse(CloseoutResponse response);

  /**
   * Called at the completion of a Sale() request. The SaleResponse contains a ResultCode 
   * and a Success boolean. A successful Sale transaction will also have the payment 
   * object, which can be for the full or partial amount of the Sale request. 
   *
   * <p>
   * <b>Note:</b> A Sale transaction my come back as a tip-adjustable Auth, depending 
   * on the payment gateway. The SaleResponse has a boolean isSale flag that indicates 
   * whether the sale is final, or will be finalized during closeout.
   *
   * @param SaleResponse The response to the transaction request.
   */
  void onSaleResponse(SaleResponse response);

  /**
   * Called in response to a manual refund request
   *
   * @param response The response
   */
  void onManualRefundResponse(ManualRefundResponse response);

  /**
   * Called in response to a refund payment request
   *
   * @param response The response
   */
  void onRefundPaymentResponse(RefundPaymentResponse response);

  /**
   * Called when a customer selects a tip amount on the Clover device screen
   *
   * @param message The message
   */
  void onTipAdded(TipAddedMessage message);

  /**
   * Called in response to a void payment request
   *
   * @param response The response
   */
  void onVoidPaymentResponse(VoidPaymentResponse response);

  /**
   * Called when the Clover device is disconnected
   */
  void onDeviceDisconnected();

  /**
   * Called when the Clover device is connected, but not ready to communicate
   */
  void onDeviceConnected();

  /**
   * Called when the Clover device is ready to communicate
   *
   * @param merchantInfo The merchant info for the device
   */
  void onDeviceReady(MerchantInfo merchantInfo);

  /**
   * Called in response to a vault card request
   *
   * @param response The response
   */
  void onVaultCardResponse(VaultCardResponse response);

  /**
   * Called to update the status of a print job
   *
   * @param response The response contains the print job identifier and that job's status
   */
    void onPrintJobStatusResponse(PrintJobStatusResponse response);

  /**
   * Called in response to a retrievePrinters() request
   *
   * @param response Response object containing an array of the printers being passed back
   */
  void onRetrievePrintersResponse(RetrievePrintersResponse response);

  /**
   * Will only be called if disablePrinting = true on the Sale, Auth, PreAuth or ManualRefund Request
   * Called when a user requests to print a receipt for a ManualRefund
   *
   * @param message The message
   */
  void onPrintManualRefundReceipt(PrintManualRefundReceiptMessage message);

  /**
   * Will only be called if disablePrinting = true on the Sale, Auth, PreAuth or ManualRefund Request
   * Called when a user requests to print a receipt for a declined ManualRefund
   *
   * @param message The message
   */
  void onPrintManualRefundDeclineReceipt(PrintManualRefundDeclineReceiptMessage message);

  /**
   * Will only be called if disablePrinting = true on the Sale, Auth, PreAuth or ManualRefund Request
   * Called when a user requests to print a receipt for a payment
   *
   * @param message The message
   */
  void onPrintPaymentReceipt(PrintPaymentReceiptMessage message);

  /**
   * Will only be called if disablePrinting = true on the Sale, Auth, PreAuth or ManualRefund Request
   * Called when a user requests to print a receipt for a declined payment
   *
   * @param message The message
   */
  void onPrintPaymentDeclineReceipt(PrintPaymentDeclineReceiptMessage message);

  /**
   * Will only be called if disablePrinting = true on the Sale, Auth, PreAuth or ManualRefund Request
   * Called when a user requests to print a merchant copy of a payment receipt
   *
   * @param message The message
   */
  void onPrintPaymentMerchantCopyReceipt(PrintPaymentMerchantCopyReceiptMessage message);

  /**
   * Will only be called if disablePrinting = true on the Sale, Auth, PreAuth or ManualRefund Request
   * Called when a user requests to print a receipt for a payment refund
   *
   * @param message The message
   */
  void onPrintRefundPaymentReceipt(PrintRefundPaymentReceiptMessage message);

  /**
   * Called in response to a retrievePendingPayment(...) request.
   *
   * @param response The response
   */
  void onRetrievePendingPaymentsResponse(RetrievePendingPaymentsResponse response);

  /**
   * Called in response to a readCardData(...) request.
   *
   * @param response The response
   */
  void onReadCardDataResponse(ReadCardDataResponse response);

  /**
   * Called when a message is sent from a custom activity
   * @param message The message
   */
  void onMessageFromActivity(MessageFromActivity message);

  /**
   * Called when a custom activity finishes
   *
   * @param response The response
   */
  void onCustomActivityResponse(CustomActivityResponse response);

  /**
   * Called in response to a RetrieveDeviceState request
   *
   * @param response The response
   */
  void onRetrieveDeviceStatusResponse(RetrieveDeviceStatusResponse response);

  /**
   * Called in response to a ResetDevice request
   *
   * @param response The response
   */
  void onResetDeviceResponse(ResetDeviceResponse response);

  /**
   * Called in response to a RetrievePaymentRequest
   *
   * @param response The response
   */
  void onRetrievePaymentResponse(RetrievePaymentResponse response);

}
