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

package com.clover.remote.client.lib.example;


import com.clover.common2.Signature2;
import com.clover.remote.CardData;
import com.clover.remote.Challenge;
import com.clover.remote.InputOption;
import com.clover.remote.client.DefaultCloverConnectorListener;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.lib.example.messages.ConversationResponseMessage;
import com.clover.remote.client.lib.example.messages.CustomerInfo;
import com.clover.remote.client.lib.example.messages.CustomerInfoMessage;
import com.clover.remote.client.lib.example.messages.PayloadMessage;
import com.clover.remote.client.lib.example.messages.PhoneNumberMessage;
import com.clover.remote.client.lib.example.messages.Rating;
import com.clover.remote.client.lib.example.messages.RatingsMessage;
import com.clover.remote.client.lib.example.model.POSCard;
import com.clover.remote.client.lib.example.model.POSExchange;
import com.clover.remote.client.lib.example.model.POSNakedRefund;
import com.clover.remote.client.lib.example.model.POSOrder;
import com.clover.remote.client.lib.example.model.POSPayment;
import com.clover.remote.client.lib.example.model.POSRefund;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.lib.example.utils.CurrencyUtils;
import com.clover.remote.client.messages.AuthResponse;
import com.clover.remote.client.messages.CapturePreAuthResponse;
import com.clover.remote.client.messages.CloseoutResponse;
import com.clover.remote.client.messages.CloverDeviceErrorEvent;
import com.clover.remote.client.messages.CloverDeviceEvent;
import com.clover.remote.client.messages.ConfirmPaymentRequest;
import com.clover.remote.client.messages.CustomActivityResponse;
import com.clover.remote.client.messages.ManualRefundResponse;
import com.clover.remote.client.messages.MessageFromActivity;
import com.clover.remote.client.messages.MessageToActivity;
import com.clover.remote.client.messages.PaymentResponse;
import com.clover.remote.client.messages.PreAuthResponse;
import com.clover.remote.client.messages.PrintJobStatusResponse;
import com.clover.remote.client.messages.PrintManualRefundDeclineReceiptMessage;
import com.clover.remote.client.messages.PrintManualRefundReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentDeclineReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentMerchantCopyReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentReceiptMessage;
import com.clover.remote.client.messages.PrintRefundPaymentReceiptMessage;
import com.clover.remote.client.messages.ReadCardDataResponse;
import com.clover.remote.client.messages.RefundPaymentRequest;
import com.clover.remote.client.messages.RefundPaymentResponse;
import com.clover.remote.client.messages.ResetDeviceResponse;
import com.clover.remote.client.messages.ResultCode;
import com.clover.remote.client.messages.RetrieveDeviceStatusResponse;
import com.clover.remote.client.messages.RetrievePaymentResponse;
import com.clover.remote.client.messages.RetrievePendingPaymentsResponse;
import com.clover.remote.client.messages.RetrievePrintersResponse;
import com.clover.remote.client.messages.SaleResponse;
import com.clover.remote.client.messages.TipAdjustAuthResponse;
import com.clover.remote.client.messages.VaultCardResponse;
import com.clover.remote.client.messages.VerifySignatureRequest;
import com.clover.remote.client.messages.VoidPaymentRequest;
import com.clover.remote.client.messages.VoidPaymentResponse;
import com.clover.remote.message.TipAddedMessage;
import com.clover.sdk.v3.order.VoidReason;
import com.clover.sdk.v3.payments.Credit;
import com.clover.sdk.v3.payments.Payment;
import com.google.gson.Gson;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.Locale;
import java.util.Optional;

public class ExamplePOSCloverConnectorListener extends DefaultCloverConnectorListener {

  private static class Toast {
    // Stub class
    private static final int LENGTH_SHORT = 1;
    private static final int LENGTH_LONG = 2;
  }

  private final Label connectionStatusLabel;
  private final Label responseLabel;
  private final Label deviceActivityLabel;
  private final ExamplePOS.GlassPane glassPane;
  private final ExamplePOS.GlassPane sigGlassPane;
  private CloverDeviceEvent.DeviceEventState lastEvent;
  private final POSStore store;

  private static final String DEFAULT_EID = "DFLTEMPLYEE";

  public ExamplePOSCloverConnectorListener(ICloverConnector cloverConnector, POSStore store, Label connectionStatusLabel, Label statusLabel, Label deviceActivityLabel, ExamplePOS.GlassPane glassPane, ExamplePOS.GlassPane sigGlassPane) {
    super(cloverConnector);
    this.connectionStatusLabel = connectionStatusLabel;
    this.responseLabel = statusLabel;
    this.deviceActivityLabel = deviceActivityLabel;
    this.glassPane = glassPane;
    this.sigGlassPane = sigGlassPane;
    this.store = store;
  }

  @Override
  public void onDeviceDisconnected() {
    super.onDeviceDisconnected();
    Platform.runLater(() -> connectionStatusLabel.setText("Disconnected"));
  }

  @Override
  public void onDeviceConnected() {
    super.onDeviceConnected();
    Platform.runLater(() -> connectionStatusLabel.setText("Connected..."));
  }

  @Override
  public void onDeviceReady(final MerchantInfo merchantInfo) {
    super.onDeviceReady(merchantInfo);
    Platform.runLater(() -> {
      glassPane.getChildren().clear();
      glassPane.setVisible(false);
      connectionStatusLabel.setText("Ready (" + merchantInfo.getDeviceInfo().getName() + "-" + merchantInfo.getDeviceInfo().getSerial() + ") " + merchantInfo.getMerchantName());
    });
  }

  @Override
  public void onDeviceActivityStart(final CloverDeviceEvent deviceEvent) {
    Platform.runLater(() -> {
      deviceActivityLabel.setText("ENTER: " + deviceEvent.getMessage());

      glassPane.getChildren().clear();
      glassPane.setVisible(true);
      final ActivityBox activityBox = new ActivityBox(deviceEvent);
      glassPane.setOnKeyPressed(event -> {
        if(event.getCode() == KeyCode.ESCAPE) {
          glassPane.getChildren().clear();
          glassPane.setVisible(false);
          glassPane.setOnKeyPressed(null);
        }
      });

      glassPane.getChildren().add(activityBox);
      glassPane.requestFocus();
      lastEvent = deviceEvent.getEventState();
    });
  }

  @Override
  public void onReadCardDataResponse(final ReadCardDataResponse response) {
    Platform.runLater(() -> {
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.setTitle("Card Data");
      dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

      if(response.isSuccess()) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(2);
        gridPane.setVgap(2);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(30);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(70);
        gridPane.getColumnConstraints().addAll(column1, column2);

        CardData cd = response.getCardData();

        gridPane.add(new Label("Encrypted: "), 0, 0);
        gridPane.add(new Label(Boolean.toString(cd.encrypted)), 1, 0);

        gridPane.add(new Label("Cardholder Name: "), 0, 1);
        gridPane.add(new Label(cd.cardholderName), 1, 1);

        gridPane.add(new Label("First Name: "), 0, 2);
        gridPane.add(new Label(cd.firstName), 1, 2);

        gridPane.add(new Label("Last Name: "), 0, 3);
        gridPane.add(new Label(cd.lastName), 1, 3);

        gridPane.add(new Label("Expiration: "), 0, 4);
        gridPane.add(new Label(cd.exp), 1, 4);

        gridPane.add(new Label("First 6: "), 0, 5);
        gridPane.add(new Label(cd.first6), 1, 5);

        gridPane.add(new Label("Last 4: "), 0, 6);
        gridPane.add(new Label(cd.last4), 1, 6);

        gridPane.add(createTrackLabel("Track 1: "), 0, 7);
        gridPane.add(createTrackData(cd.track1), 1, 7);

        gridPane.add(createTrackLabel("Track 2: "), 0, 8);
        gridPane.add(createTrackData(cd.track2), 1, 8);

        gridPane.add(createTrackLabel("Track 3: "), 0, 9);
        gridPane.add(createTrackData(cd.track3), 1, 9);

        gridPane.add(createTrackLabel("Masked Track 1: "), 0, 10);
        gridPane.add(createTrackData(cd.maskedTrack1), 1, 10);

        gridPane.add(createTrackLabel("Masked Track 2: "), 0, 11);
        gridPane.add(createTrackData(cd.maskedTrack2), 1, 11);

        gridPane.add(createTrackLabel("Masked Track 3: "), 0, 12);
        gridPane.add(createTrackData(cd.maskedTrack3), 1, 12);

        gridPane.add(new Label("Pan: "), 0, 13);
        gridPane.add(new Label(cd.pan), 1, 13);

        dialog.getDialogPane().setContent(gridPane);
      } else {
        Label error = new Label("ReadCardData failed: " + response.getResult() + " -> " + response.getReason());
        error.setWrapText(true);
        error.setMaxWidth(400);

        dialog.getDialogPane().setContent(error);
      }

      dialog.showAndWait();
    });
  }

  private Label createTrackLabel(String text) {
    Label label = new Label(text);
    label.setStyle("-fx-alignment: TOP-LEFT;");
    label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    GridPane.setFillWidth(label, true);
    GridPane.setFillHeight(label, true);
    return label;
  }

  private Label createTrackData(String text) {
    Label label = new Label(text);
    label.setWrapText(true);
    label.setMaxWidth(250);
    return label;
  }

  @Override
  public synchronized void onDeviceActivityEnd(CloverDeviceEvent deviceEvent) {
    Platform.runLater(() -> {
      deviceActivityLabel.setText("EXIT: " + deviceEvent.getMessage());

      glassPane.setVisible(false);
    });
  }

  @Override
  public void onDeviceError(CloverDeviceErrorEvent deviceErrorEvent) {
    showMessage("DeviceError: " + deviceErrorEvent.getMessage(), Toast.LENGTH_LONG);
  }

  @Override
  public void onAuthResponse(AuthResponse response) {
    if (response.isSuccess()) {
      Payment _payment = response.getPayment();
      long cashback = _payment.getCashbackAmount() == null ? 0 : _payment.getCashbackAmount();
      long tip = _payment.getTipAmount() == null ? 0 : _payment.getTipAmount();
      POSPayment payment = new POSPayment(_payment.getId(), _payment.getExternalPaymentId(), _payment.getOrder().getId(), DEFAULT_EID, _payment.getAmount(), tip, cashback);
      setPaymentStatus(payment, response);
      store.addPaymentToOrder(payment, store.getCurrentOrder());
      showMessage("Auth successfully processed.", Toast.LENGTH_SHORT);

      store.createOrder(false);
      cloverConnector.showWelcomeScreen();
    } else {
      showMessage("Auth error:" + response.getResult(), Toast.LENGTH_LONG);
      cloverConnector.showMessage("There was a problem processing the transaction");
    }
  }

  @Override
  public void onPreAuthResponse(PreAuthResponse response) {
    if (response.isSuccess()) {
      Payment _payment = response.getPayment();
      long cashback = _payment.getCashbackAmount() == null ? 0 : _payment.getCashbackAmount();
      long tip = _payment.getTipAmount() == null ? 0 : _payment.getTipAmount();
      POSPayment payment = new POSPayment(_payment.getId(), _payment.getExternalPaymentId(), _payment.getOrder().getId(), DEFAULT_EID, _payment.getAmount(), tip, cashback);
      setPaymentStatus(payment, response);
      store.addPreAuth(payment);
      showMessage("PreAuth successfully processed.", Toast.LENGTH_SHORT);
    } else {
      showMessage("PreAuth: " + response.getResult(), Toast.LENGTH_LONG);
    }
  }

  @Override
  public void onRetrievePendingPaymentsResponse(final RetrievePendingPaymentsResponse response) {
    if (!response.isSuccess()) {
      store.setPendingPayments(null);
      showMessage("Retrieve Pending Payments: " + response.getResult(), Toast.LENGTH_LONG);
    } else {
      store.setPendingPayments(response.getPendingPayments());
    }
  }

  @Override
  public void onTipAdjustAuthResponse(final TipAdjustAuthResponse response) {
    if (response.isSuccess()) {

      boolean updatedTip = false;
      for (POSOrder order : store.getOrders()) {
        for (POSExchange exchange : order.getPayments()) {
          if (exchange instanceof POSPayment) {
            POSPayment posPayment = (POSPayment) exchange;
            if (exchange.getPaymentID().equals(response.getPaymentId())) {
              posPayment.setTipAmount(response.getTipAmount());
              // TODO: should the stats be updated?
              updatedTip = true;
              break;
            }
          }
        }
        if (updatedTip) {
          showMessage("Tip successfully adjusted", Toast.LENGTH_LONG);
          break;
        }
      }
    } else {
      showMessage("Tip adjust failed", Toast.LENGTH_LONG);
    }
  }

  @Override
  public void onCapturePreAuthResponse(final CapturePreAuthResponse response) {
    if (response.isSuccess()) {
      for (final POSPayment payment : store.getPreAuths()) {
        if (payment.getPaymentID().equals(response.getPaymentID())) {
          final long paymentAmount = response.getAmount();
          Platform.runLater(() -> {
            store.removePreAuth(payment);
            store.addPaymentToOrder(payment, store.getCurrentOrder());
            payment.setPaymentStatus(POSPayment.Status.AUTHORIZED);
            payment.amount = paymentAmount;
            showMessage("Sale successfully processing using Pre Authorization", Toast.LENGTH_LONG);

            //TODO: if order isn't fully paid, don't create a new order...
            store.createOrder(false);
          });
          break;
        } else {
          showMessage("PreAuth Capture: Payment received does not match any of the stored PreAuth records", Toast.LENGTH_LONG);
        }
      }
    } else {
      showMessage("PreAuth Capture Error: Payment failed with response code = " + response.getResult() + " and reason: " + response.getReason(), Toast.LENGTH_LONG);
    }
  }

  @Override
  public synchronized void onVerifySignatureRequest(final VerifySignatureRequest request) {
    Platform.runLater(() -> {
      sigGlassPane.getChildren().clear();
      sigGlassPane.getChildren().add(new SignatureConfirmationPane(request));
      sigGlassPane.setVisible(true);
    });
  }

  @Override
  public void onMessageFromActivity(MessageFromActivity message) {
    //showMessage("Custom Activity Message Received for actionId: " + message.actionId + " with payload: " + message.payload, Toast.LENGTH_LONG);
    PayloadMessage payloadMessage = new Gson().fromJson(message.getPayload(), PayloadMessage.class);
    switch (payloadMessage.messageType) {
      case REQUEST_RATINGS:
        handleRequestRatings();
        break;
      case RATINGS:
        handleRatings(message.getPayload());
        break;
      case PHONE_NUMBER:
        handleCustomerLookup(message.getPayload());
        break;
      case CONVERSATION_RESPONSE:
        handleJokeResponse(message.getPayload());
        break;
      default:
        showMessage("Unknown Payload: " + payloadMessage.messageType.name(), Toast.LENGTH_LONG);
    }
  }

  @Override
  public void onConfirmPaymentRequest(final ConfirmPaymentRequest request) {
    Platform.runLater(() -> {
      Challenge failedChallenge = null;
      for (Challenge challenge : request.getChallenges()) {
        AlertPanel alert = new AlertPanel();//(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Confirm");
//            alert.setHeaderText("");
        alert.setContentText(challenge.message);
        alert.setButtonTypes(ButtonType.NO, ButtonType.YES);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.YES) {
          //ok
        } else {
          //cancel
          failedChallenge = challenge;
        }
      }
      if (failedChallenge == null) {
        cloverConnector.acceptPayment(request.getPayment());
      } else {
        cloverConnector.rejectPayment(request.getPayment(), failedChallenge);
      }
    });
  }

  @Override
  public void onCloseoutResponse(final CloseoutResponse response) {
    if (response.isSuccess()) {
      showMessage("Closeout is scheduled.", Toast.LENGTH_SHORT);
    } else {
      showMessage("Error scheduling closeout: " + response.getResult(), Toast.LENGTH_LONG);
    }
  }

  @Override
  public void onSaleResponse(SaleResponse response) {
    if (response != null) {
      if (response.isSuccess()) { // Handle cancel response
        if (response.getPayment() != null) {
          Payment _payment = response.getPayment();
          long cashback = _payment.getCashbackAmount() == null ? 0 : _payment.getCashbackAmount();
          long tip = _payment.getTipAmount() == null ? 0 : _payment.getTipAmount();
          POSPayment payment = new POSPayment(_payment.getId(), _payment.getExternalPaymentId(), _payment.getOrder().getId(), DEFAULT_EID, _payment.getAmount(), tip, cashback);
          setPaymentStatus(payment, response);

          store.addPaymentToOrder(payment, store.getCurrentOrder());
          showMessage("Sale successfully processed", Toast.LENGTH_SHORT);
          store.createOrder(false);
        } else { // Handle null payment
          showMessage("Error: Sale response was missing the payment", Toast.LENGTH_LONG);
        }
      } else {
        showMessage(response.getResult().toString() + ":" + response.getReason() + "  " + response.getMessage(), Toast.LENGTH_LONG);
      }
    } else { //Handle null payment response
      showMessage("Error: Null SaleResponse", Toast.LENGTH_LONG);
    }
    cloverConnector.showWelcomeScreen();
  }

  @Override
  public void onManualRefundResponse(final ManualRefundResponse response) {
    if (response.isSuccess()) {
      Credit credit = response.getCredit();
      final POSNakedRefund nakedRefund = new POSNakedRefund(null, credit.getAmount());
      Platform.runLater(() -> {
        store.addRefund(nakedRefund);
        showMessage("Manual Refund successfully processed", Toast.LENGTH_SHORT);
      });
    } else if (response.getResult() == ResultCode.CANCEL) {
      showMessage("User canceled the Manual Refund", Toast.LENGTH_SHORT);
    } else {
      showMessage("Manual Refund Failed with code: " + response.getResult() + " - " + response.getMessage(), Toast.LENGTH_LONG);
    }
  }

  @Override
  public void onRefundPaymentResponse(final RefundPaymentResponse response) {
    if (response.isSuccess()) {
      POSRefund refund = new POSRefund(response.getRefund().getId(), response.getPaymentId(), response.getOrderId(), "DEFAULT", response.getRefund().getAmount());
      boolean done = false;
      for (POSOrder order : store.getOrders()) {
        for (POSExchange payment : order.getPayments()) {
          if (payment instanceof POSPayment) {
            if (payment.getPaymentID().equals(response.getRefund().getPayment().getId())) {
              ((POSPayment) payment).setPaymentStatus(POSPayment.Status.REFUNDED);
              store.addRefundToOrder(refund, order);
              showMessage("Payment successfully refunded", Toast.LENGTH_SHORT);
              done = true;
              break;
            }
          }
        }
        if (done) {
          break;
        }
      }
    } else {
      showMessage("Refund Error: " + response.getReason(), Toast.LENGTH_LONG);
    }
  }

  @Override
  public void onTipAdded(TipAddedMessage message) {
    if (message.tipAmount > 0) {
      showMessage("Tip successfully added: " + CurrencyUtils.format(message.tipAmount, Locale.getDefault()), Toast.LENGTH_SHORT);
    }
  }

  @Override
  public void onVoidPaymentResponse(final VoidPaymentResponse response) {
    if (response.isSuccess()) {
      boolean done = false;
      for (POSOrder order : store.getOrders()) {
        for (POSExchange payment : order.getPayments()) {
          if (payment instanceof POSPayment) {
            if (payment.getPaymentID().equals(response.getPaymentId())) {
              ((POSPayment) payment).setPaymentStatus(POSPayment.Status.VOIDED);
              showMessage("Payment was voided", Toast.LENGTH_SHORT);
              done = true;
              break;
            }
          }
        }
        if (done) {
          break;
        }
      }
    } else {
      showMessage(getClass().getName() + ":Got VoidPaymentResponse of " + response.getResult(), Toast.LENGTH_LONG);
    }
  }

  @Override
  public void onVaultCardResponse(final VaultCardResponse response) {
    if (response.isSuccess()) {
      POSCard card = new POSCard();
      card.setFirst6(response.getCard().getFirst6());
      card.setLast4(response.getCard().getLast4());
      card.setName(response.getCard().getCardholderName());
      card.setMonth(response.getCard().getExpirationDate().substring(0, 2));
      card.setYear(response.getCard().getExpirationDate().substring(2, 4));
      card.setToken(response.getCard().getToken());
      store.addCard(card);
      showMessage("Card successfully vaulted", Toast.LENGTH_SHORT);
    } else {
      if (response.getResult() == ResultCode.CANCEL) {
        showMessage("User canceled the operation", Toast.LENGTH_SHORT);
        cloverConnector.showWelcomeScreen();
      } else {
        showMessage("Error capturing card: " + response.getResult(), Toast.LENGTH_LONG);
        cloverConnector.showMessage("Card was not saved");
        try {
          Thread.sleep(4000);
        } catch (Exception ex) {
          // NO-OP
        }
        cloverConnector.showWelcomeScreen();
      }
    }
  }

  @Override
  public void onPrintManualRefundReceipt(PrintManualRefundReceiptMessage pcm) {
    showMessage("Print Request for ManualRefund", Toast.LENGTH_SHORT);
  }

  @Override
  public void onPrintManualRefundDeclineReceipt(PrintManualRefundDeclineReceiptMessage pcdrm) {
    showMessage("Print Request for Declined ManualRefund", Toast.LENGTH_SHORT);
  }

  @Override
  public void onPrintPaymentReceipt(PrintPaymentReceiptMessage pprm) {
    showMessage("Print Request for Payment Receipt", Toast.LENGTH_SHORT);
  }

  @Override
  public void onPrintPaymentDeclineReceipt(PrintPaymentDeclineReceiptMessage ppdrm) {
    showMessage("Print Request for DeclinedPayment Receipt", Toast.LENGTH_SHORT);
  }

  @Override
  public void onPrintPaymentMerchantCopyReceipt(PrintPaymentMerchantCopyReceiptMessage ppmcrm) {
    showMessage("Print Request for MerchantCopy of a Payment Receipt", Toast.LENGTH_SHORT);
  }

  @Override
  public void onPrintRefundPaymentReceipt(PrintRefundPaymentReceiptMessage pprrm) {
    showMessage("Print Request for RefundPayment Receipt", Toast.LENGTH_SHORT);
  }

  @Override
  public void onCustomActivityResponse(CustomActivityResponse response) {
    boolean success = response.isSuccess();
    if (success) {
      showMessage("Success! Got: " + response.getPayload() + " from CustomActivity: " + response.getAction(), 5000);
    } else {
      if (response.getResult().equals(ResultCode.CANCEL)) {
        showMessage("Custom activity: " + response.getAction() + " was canceled.  Reason: " + response.getReason(), 5000);
      } else {
        showMessage("Failure! Custom activity: " + response.getAction() + " failed.  Reason: " + response.getReason(), 5000);
      }
    }
  }

  @Override
  public void onRetrieveDeviceStatusResponse(RetrieveDeviceStatusResponse response) {
    showMessage((response.isSuccess() ? "Success!" : "Failed!") + " State: " + response.getState()
        + " ExternalActivityId: " + response.getData().toString()
        + " reason: " + response.getReason(), Toast.LENGTH_LONG);
  }

  @Override
  public void onResetDeviceResponse(ResetDeviceResponse response) {
    showMessage((response.isSuccess() ? "Success!" : "Failed!") + " State: " + response.getState()
        + " reason: " + response.getReason(), Toast.LENGTH_LONG);
  }

  @Override
  public void onRetrievePaymentResponse(RetrievePaymentResponse response) {
    showMessage("RetrievePayment: " + (response.isSuccess() ? "Success!" : "Failed!")
        + " QueryStatus: " + response.getQueryStatus() + " for id " + response.getExternalPaymentId()
        + " Payment: " + response.getPayment()
        + " reason: " + response.getReason(), Toast.LENGTH_LONG);
  }

  @Override public void onPrintJobStatusResponse(PrintJobStatusResponse response) {
    showMessage(
        "RetrievePrintersResponse: " + (response.isSuccess()) + " State: " + response.getStatus() + " for print job id: " + response.getPrintRequestId(), 2000);
  }


  private void promptForRefundAndVoid(PaymentResponse response) {
    // refund payment?
    {
      AlertPanel alert = new AlertPanel();//(Alert.AlertType.CONFIRMATION);
//      alert.setTitle("Refund Payment?");
//      alert.setHeaderText("");
      alert.setContentText("Would you like to refund the payment?");
      alert.setButtonTypes(ButtonType.NO, ButtonType.YES);
      Optional<ButtonType> result = alert.showAndWait();

      if (result.get() == ButtonType.YES) {
        //ok
        RefundPaymentRequest rpr = new RefundPaymentRequest();

        // full amount?
        {
          AlertPanel faAlert = new AlertPanel();//(Alert.AlertType.CONFIRMATION);
//          faAlert.setTitle("Refund Full Amount?");
//          faAlert.setHeaderText("");
          faAlert.setContentText("Would you like to refund the full Amount?");
          faAlert.setButtonTypes(ButtonType.NO, ButtonType.YES);
          Optional<ButtonType> faResult = faAlert.showAndWait();

          if(result.get() == ButtonType.YES) {
            rpr.setAmount(0);
            rpr.setFullRefund(true);
          }
          else {
            rpr.setAmount(response.getPayment().getAmount() / 2);
            rpr.setFullRefund(false);
          }
        }

        rpr.setOrderId(response.getPayment().getOrder().getId());
        rpr.setPaymentId(response.getPayment().getId());

        cloverConnector.refundPayment(rpr);
      } else {
        // void
        promptForVoid(response);

      }
      alert.close();
    }
  }

  private void promptForVoid(PaymentResponse response) {
    AlertPanel alert = new AlertPanel();//(Alert.AlertType.CONFIRMATION);
//    alert.setTitle("Void Payment?");
//    alert.setHeaderText("");
    alert.setContentText("Would you like to void the payment?");
    alert.setButtonTypes(ButtonType.NO, ButtonType.YES);
    Optional<ButtonType> result = alert.showAndWait();

    if (result.get() == ButtonType.YES) {
      VoidPaymentRequest vpr = new VoidPaymentRequest();
      vpr.setOrderId(response.getPayment().getOrder().getId());
      vpr.setPaymentId(response.getPayment().getId());
      vpr.setVoidReason(VoidReason.USER_CANCEL.toString());

      cloverConnector.voidPayment(vpr);
    } else {
      promptForReceipt(response.getPayment().getOrder().getId(), response.getPayment().getId());
    }
  }

  private void promptForReceipt(String orderId, String paymentId) {
    AlertPanel alert = new AlertPanel();//(Alert.AlertType.CONFIRMATION);
//    alert.setTitle("Show Receipt Screen?");
//    alert.setHeaderText("");
    alert.setContentText("Would you like to re-issue receipt?");
    alert.setButtonTypes(ButtonType.NO, ButtonType.YES);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.YES) {
      cloverConnector.displayPaymentReceiptOptions(orderId, paymentId);
    }
  }

  private void handleRequestRatings() {
    Rating rating1 = new Rating();
    rating1.id = "Quality";
    rating1.question = "How would you rate the overall quality of your entree?";
    rating1.value = 0;
    Rating rating2 = new Rating();
    rating2.id = "Server";
    rating2.question = "How would you rate the overall performance of your server?";
    rating2.value = 0;
    Rating rating3 = new Rating();
    rating3.id = "Value";
    rating3.question = "How would you rate the overall value of your dining experience?";
    rating3.value = 0;
    Rating rating4 = new Rating();
    rating4.id = "RepeatBusiness";
    rating4.question = "How likely are you to dine at this establishment again in the near future?";
    rating4.value = 0;
    Rating[] ratings = new Rating[]{rating1, rating2, rating3, rating4};
    RatingsMessage ratingsMessage = new RatingsMessage(ratings);
    String ratingsListJson = ratingsMessage.toJsonString();
    sendMessageToActivity("com.clover.cfp.examples.RatingsExample", ratingsListJson);
  }

  private void handleRatings(String payload) {
    //showMessage(payload, Toast.LENGTH_SHORT);
//    RatingsMessage ratingsMessage = (RatingsMessage) PayloadMessage.fromJsonString(payload);
//    Rating[] ratingsPayload = ratingsMessage.ratings;
//    showRatingsDialog(ratingsPayload);
    //for (Rating rating:ratingsPayload
    //     ) {
    //  String ratingString = "Rating ID: " + rating.id + " - " + rating.question + " Rating value: " + Integer.toString(rating.value);
    //  showMessage(ratingString, Toast.LENGTH_SHORT);
    //}
  }

  private void handleCustomerLookup(String payload) {
    PhoneNumberMessage phoneNumberMessage = new Gson().fromJson(payload, PhoneNumberMessage.class);
    String phoneNumber = phoneNumberMessage.phoneNumber;
    showMessage("Just received phone number " + phoneNumber + " from the Ratings remote application.", 3000);
    showMessage("Sending customer name Ron Burgundy to the Ratings remote application for phone number " + phoneNumber, 3000);
    CustomerInfo customerInfo = new CustomerInfo();
    customerInfo.customerName = "Ron Burgundy";
    customerInfo.phoneNumber = phoneNumber;
    CustomerInfoMessage customerInfoMessage = new CustomerInfoMessage(customerInfo);
    String customerInfoJson = customerInfoMessage.toJsonString();
    sendMessageToActivity("com.clover.cfp.examples.RatingsExample", customerInfoJson);
  }

  private void handleJokeResponse(String payload) {
    ConversationResponseMessage jokeResponseMessage = (ConversationResponseMessage) PayloadMessage.fromJsonString(payload);
    showMessage("Received JokeResponse of: " + jokeResponseMessage.message, Toast.LENGTH_SHORT);
  }

  public void sendMessageToActivity(String activityId, String payload) {
    MessageToActivity messageRequest = new MessageToActivity(activityId, payload);
    cloverConnector.sendMessageToActivity(messageRequest);
  }

  private void showMessage(String msg, int duration) {
    Platform.runLater(() -> responseLabel.setText(msg));
  }

  private void setPaymentStatus(POSPayment payment, PaymentResponse response) {
    if(response.isSale()) {
      payment.setPaymentStatus(POSPayment.Status.PAID);
    } else if(response.isAuth()) {
      payment.setPaymentStatus(POSPayment.Status.AUTHORIZED);
    } else if(response.isPreAuth()) {
      payment.setPaymentStatus(POSPayment.Status.PREAUTHORIZED);
    }
  }

  class ActivityBox extends BorderPane {
    HBox buttonBox = new HBox();
    Label lbl = new Label();
    public ActivityBox(CloverDeviceEvent evt) {
      this.setTop(lbl);
      this.setBottom(buttonBox);

      lbl.setAlignment(Pos.CENTER);
      buttonBox.setAlignment(Pos.CENTER);
      buttonBox.setSpacing(10);

      setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(5), new Insets(0,0,0,0))));
      setPadding(new Insets(20,20,20,20));

      lbl.setText(evt.getMessage() + " - [Press ESC to exit]");
      lbl.setPadding(new Insets(10,0,15,0));
      for(final InputOption io : evt.getInputOptions())
      {
        Button b = new Button(io.description);
        b.setOnAction(event -> cloverConnector.invokeInputOption(io));
        buttonBox.getChildren().add(b);
      }
    }

    public void clear() {
      buttonBox.getChildren().clear();
      lbl.setText("");
    }

  }

  class SignatureConfirmationPane extends BorderPane {
    VerifySignatureRequest request;
    Signature2 sig = null;

    private SignatureConfirmationPane(final VerifySignatureRequest request, final Signature2 sig) {
      SignaturePane sigPane = new SignaturePane();
      sigPane.setWidth(600);
      sigPane.setHeight(500);
      sigPane.setSignature(sig);
      BorderPane bp = this;
      bp.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));
      bp.setCenter(sigPane);



      HBox hbox = new HBox();
      Button rejectButton = new Button("Reject");
      rejectButton.setOnAction(event -> {
        sigGlassPane.getChildren().clear();
        sigGlassPane.setVisible(false);
        if(request != null) {
          cloverConnector.rejectSignature(request);
        }
      });

      Button acceptButton = new Button(request == null ? "OK" : "Accept");
      acceptButton.setOnAction(event -> {
        sigGlassPane.getChildren().clear();
        sigGlassPane.setVisible(false);
        if(request != null) {
          cloverConnector.acceptSignature(request);
        }
      });
      hbox.setAlignment(Pos.CENTER_RIGHT);
      if(request == null) {
        hbox.getChildren().addAll(acceptButton);
      } else {
        hbox.getChildren().addAll(rejectButton, acceptButton);
      }
      bp.setBottom(hbox);
      this.setCenter(sigPane);
      this.setBottom(hbox);

      setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(5), new Insets(0,0,0,0))));
      setPadding(new Insets(20,20,20,20));
    }

    public SignatureConfirmationPane (final Signature2 sig) {
      this(null, sig);
    }
    public SignatureConfirmationPane (final VerifySignatureRequest request) {
      this(request, request.getSignature());
    }
  }

  class AlertPanel extends VBox {
    Label contentText = new Label();
    HBox buttonBox = new HBox();
    ButtonType selectedType = null;
    ExamplePOS.GlassPane gp = sigGlassPane;

    public AlertPanel(ExamplePOS.GlassPane glassPane) {
      gp = glassPane;
    }
    public AlertPanel() {
      getChildren().add(contentText);
      getChildren().add(buttonBox);

      buttonBox.setAlignment(Pos.CENTER_RIGHT);
      buttonBox.setSpacing(10);

      setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(5), new Insets(0,0,0,0))));
      setSpacing(15);
      setPadding(new Insets(20,20,20,20));
    }

    public void setButtonTypes(ButtonType ... type) {
      for(final ButtonType tp : type) {
        Button b = new Button(tp.getButtonData().toString());
        b.setUserData(type);
        b.setOnAction(event -> {
          selectedType = tp;
          close();
          Toolkit.getToolkit().exitNestedEventLoop(AlertPanel.this, null);
        });
        buttonBox.getChildren().add(b);
      }
    }

    public Optional<ButtonType> showAndWait() {

      Toolkit.getToolkit().checkFxUserThread();

      gp.getChildren().clear();
      gp.getChildren().add(this);
      gp.setVisible(true);

      Toolkit.getToolkit().enterNestedEventLoop(AlertPanel.this);

      return Optional.of(selectedType);
    }

    public void setContentText(String s) {
      contentText.setText(s);
    }

    public void close() {
      gp.getChildren().clear();
      gp.setVisible(false);
    }
  }

  static class PromptPanel extends VBox {
    Label contentText = new Label();
    TextField textField = new TextField();
    HBox buttonBox = new HBox();
    ButtonType selectedType = null;
    ExamplePOS.GlassPane gp;// = sigGlassPane;

    public PromptPanel(ExamplePOS.GlassPane glassPane, String message) {
      this.gp = glassPane;
      getChildren().add(contentText);
      getChildren().add(textField);
      getChildren().add(buttonBox);

      addButtons(new ButtonType[]{ButtonType.CANCEL, ButtonType.OK});

      buttonBox.setAlignment(Pos.CENTER_RIGHT);
      buttonBox.setSpacing(10);

      setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(5), new Insets(0,0,0,0))));
      setSpacing(15);
      setPadding(new Insets(20,20,20,20));
    }

    private void addButtons(ButtonType ... type) {
      for(final ButtonType tp : type) {
        Button b = new Button(tp.getButtonData().toString());
        b.setUserData(type);
        b.setOnAction(event -> {
          selectedType = tp;
          close();
          Toolkit.getToolkit().exitNestedEventLoop(PromptPanel.this, null);
        });
        buttonBox.getChildren().add(b);
      }
    }

    public Optional<String> showAndWait() {

      Toolkit.getToolkit().checkFxUserThread();

      gp.getChildren().clear();
      gp.getChildren().add(this);
      gp.setVisible(true);

      Toolkit.getToolkit().enterNestedEventLoop(PromptPanel.this);

      if (selectedType == ButtonType.OK) {
        return Optional.of(textField.getText());
      } else {
        return Optional.of(null);
      }
    }

    public void setContentText(String s) {
      contentText.setText(s);
    }

    public void close() {
      gp.getChildren().clear();
      gp.setVisible(false);
    }
  }

  static class PairPanel extends VBox {
    Label headingText = new Label();
    Label contentText = new Label();
    public PairPanel(String pairingCode) {
      getChildren().add(headingText);
      getChildren().add(contentText);

      headingText.setText("Enter Pairing Code on Device");
      contentText.setTextAlignment(TextAlignment.CENTER);

      StringBuilder sb = new StringBuilder();
      for(int i=0; i<pairingCode.length(); i++) {
        sb.append(pairingCode.charAt(i));
        sb.append("  ");
      }

      contentText.setFont(new Font(22));

      contentText.setText(sb.toString().trim());

      setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(5), new Insets(0,0,0,0))));
      setSpacing(15);
      setPadding(new Insets(20,20,20,20));

      this.setAlignment(Pos.CENTER);
    }
  }
}
