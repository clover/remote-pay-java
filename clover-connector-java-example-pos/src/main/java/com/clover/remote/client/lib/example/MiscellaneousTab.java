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

import com.clover.remote.client.CloverConnector;
import com.clover.remote.client.DefaultCloverConnectorListener;
import com.clover.remote.client.lib.example.messages.ConversationQuestionMessage;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.messages.CloseoutRequest;
import com.clover.remote.client.messages.ConfirmPaymentRequest;
import com.clover.remote.client.messages.CustomActivityRequest;
import com.clover.remote.client.messages.MessageToActivity;
import com.clover.remote.client.messages.OpenCashDrawerRequest;
import com.clover.remote.client.messages.PrintJobStatusRequest;
import com.clover.remote.client.messages.PrintRequest;
import com.clover.remote.client.messages.ReadCardDataRequest;
import com.clover.remote.client.messages.RetrieveDeviceStatusRequest;
import com.clover.remote.client.messages.RetrievePaymentRequest;
import com.clover.remote.client.messages.RetrievePrintersRequest;
import com.clover.remote.client.messages.RetrievePrintersResponse;
import com.clover.remote.client.messages.SaleRequest;
import com.clover.sdk.v3.payments.DataEntryLocation;
import com.clover.sdk.v3.printer.Printer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MiscellaneousTab extends AbstractExampleTab {

  private TextField queryPrintJobTextField;

  private enum CustomActivity {
    BASIC("BasicExample", false),
    BASIC_CONV("BasicConversationalExample", true),
    WEB_VIEW("WebViewExample", false),
    CAROUSEL("CarouselExample", false),
    RATINGS("RatingsExample", false),
    NFC("NFCExample", false);

    private final String name;
    private final boolean conversational;

    CustomActivity(String name, boolean conversational) {
      this.name = name;
      this.conversational = conversational;
    }

    @Override
    public String toString() {
      return name;
    }

    // Package name for example custom activities
    public String getActivityId() {
      return "com.clover.cfp.examples." + name;
    }

    public boolean isConversational() {
      return conversational;
    }
  }

  private String accessToken;
  private String merchantId;
  private boolean nonBlocking = false;

  public MiscellaneousTab(POSStore store, Label statusLabel) {
    super(store, statusLabel, "MISC");
  }

  @Override
  protected Node buildPane() {
    FlowPane fp = new FlowPane();
    fp.orientationProperty().setValue(Orientation.VERTICAL);

    fp.getChildren().addAll(buildTextInputPane(), buildControlPane(), buildCustomActivitiesPane(), buildTransactionSettingsPane(), buildRestPane());

    return fp;
  }

  private Pane buildTextInputPane() {
    GridPane inputPane = new GridPane();
    inputPane.setHgap(2);
    inputPane.setVgap(2);
    inputPane.setPadding(new Insets(10, 10, 0, 10));

    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(60);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(40);
    inputPane.getColumnConstraints().addAll(column1, column2);

    // Show Message
    TextArea displayTextArea = new TextArea("Hello Message!");
    displayTextArea.setPrefHeight(10);
    displayTextArea.setPrefWidth(160);
    displayTextArea.setWrapText(true);
    inputPane.add(displayTextArea, 0, 0);

    Button displayTextButton = new Button("SHOW MESSAGE");
    displayTextButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.showMessage(displayTextArea.getText()); //"This is a custom message");
    });
    inputPane.add(displayTextButton, 1, 0);
    fillJavaFxGrid(displayTextButton);

    // Print Message
    TextArea printTextArea = new TextArea("Print This!!!");
    printTextArea.setPrefHeight(10);
    printTextArea.setPrefWidth(80);
    printTextArea.setWrapText(true);
    inputPane.add(printTextArea, 0, 1);

    // Print Text
    {
        MenuItem defaultPrinter = new MenuItem("Default Printer");
        defaultPrinter.setOnAction(event -> {
          clearLabel();

          List<String> printLines;
          String[] lines = printTextArea.getText().split("\n");
          if (lines.length == 1 && lines[0].isEmpty()) {
            printLines = Collections.singletonList("<<Empty Print Text>>");
          } else {
            printLines = Arrays.asList(lines);
          }

          printTextToPrinter(printLines, null);
        });
        MenuItem loadingMenuItem = new MenuItem("Loading Printers...");

        SplitMenuButton printTextButton = new SplitMenuButton(defaultPrinter, loadingMenuItem);
        printTextButton.showingProperty().addListener(new ChangeListener<Boolean>() {
          @Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            System.out.println("it opened...");
            if(!oldValue && newValue) {

              cloverConnector.addCloverConnectorListener(new DefaultCloverConnectorListener(cloverConnector) {
                @Override public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
                  // do nothing here...
                }

                @Override public void onRetrievePrintersResponse(RetrievePrintersResponse response) {
                  cloverConnector.removeCloverConnectorListener(this);

                  Platform.runLater(new Runnable(){
                    @Override public void run() {
                      printTextButton.getItems().remove(loadingMenuItem);
                      for(Printer p : response.getPrinters()) {
                        MenuItem mi = new MenuItem(p.getName() + ":" + p.getId());
                        mi.setOnAction(event -> {
                          clearLabel();

                          List<String> printLines;
                          String[] lines = printTextArea.getText().split("\n");
                          if (lines.length == 1 && lines[0].isEmpty()) {
                            printLines = Collections.singletonList("<<Empty Print Text>>");
                          } else {
                            printLines = Arrays.asList(lines);
                          }

                          printTextToPrinter(printLines, p.getId());
                        });
                        printTextButton.getItems().add(mi);
                      }
                    }
                  });
                }
              });
              cloverConnector.retrievePrinters(new RetrievePrintersRequest(null));
            } else {
              Platform.runLater(new Runnable(){
                @Override public void run() {
                  printTextButton.getItems().clear();
                  printTextButton.getItems().add(defaultPrinter);
                  printTextButton.getItems().add(loadingMenuItem);
                }
              });
            }
          }
        });
        printTextButton.setText("PRINT TEXT");
        printTextButton.setOnAction(event -> {
          clearLabel();
          List<String> printLines;
          String[] lines = printTextArea.getText().split("\n");
          if (lines.length == 1 && lines[0].isEmpty()) {
            printLines = Collections.singletonList("<<Empty Print Text>>");
          } else {
            printLines = Arrays.asList(lines);
          }
          cloverConnector.printText(printLines);
        });

        inputPane.add(printTextButton, 1, 1);
        fillJavaFxGrid(printTextButton);
      }

    // Query Payment
    TextField queryPaymentTextField = new TextField("JANRZXDFTF3JF");
    queryPaymentTextField.setPrefHeight(10);
    queryPaymentTextField.setPrefWidth(60);
    inputPane.add(queryPaymentTextField, 0, 2);

    Button queryPaymentButton = new Button("QUERY PAYMENT");
    queryPaymentButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.retrievePayment(new RetrievePaymentRequest(queryPaymentTextField.getText()));
    });
    inputPane.add(queryPaymentButton, 1, 2);
    fillJavaFxGrid(queryPaymentButton);


    // Query Print Job
    queryPrintJobTextField = new TextField("");
    queryPrintJobTextField.setPrefHeight(10);
    queryPrintJobTextField.setPrefWidth(60);
    inputPane.add(queryPrintJobTextField, 0, 3);

    Button queryPrintJobButton = new Button("QUERY PRINT JOB");
    queryPrintJobButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.retrievePrintJobStatus(new PrintJobStatusRequest(queryPrintJobTextField.getText()));
    });
    inputPane.add(queryPrintJobButton, 1, 3);
    fillJavaFxGrid(queryPrintJobButton);

    return inputPane;
  }

  private void printTextToPrinter(List<String> printLines, String printerId) {
    PrintRequest pr = new PrintRequest(printLines);
    pr.setPrintRequestId(UUID.randomUUID().toString());
    pr.setPrintDeviceId(printerId);
    queryPrintJobTextField.setText(pr.getPrintRequestId());
    cloverConnector.print(pr);
  }

  private void printImageURLToPrinter( String url, String printerId) {
    PrintRequest pr = new PrintRequest(url);
    pr.setPrintDeviceId(null);
    pr.setPrintRequestId(UUID.randomUUID().toString());
    queryPrintJobTextField.setText(pr.getPrintRequestId());
    cloverConnector.print(pr);
  }

  private Pane buildControlPane() {
    GridPane controlPane = new GridPane();
    controlPane.setHgap(2);
    controlPane.setVgap(2);
    controlPane.setPadding(new Insets(10, 10, 0, 10));

    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(50);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(50);
    controlPane.getColumnConstraints().addAll(column1, column2);

    Button welcomeButton = new Button("SHOW WELCOME MESSAGE");
    welcomeButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.showWelcomeScreen();
    });
    controlPane.add(welcomeButton, 0, 0);
    fillJavaFxGrid(welcomeButton);

    Button thankYouButton = new Button("SHOW THANK YOU");
    thankYouButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.showThankYouScreen();
    });
    controlPane.add(thankYouButton, 1, 0);
    fillJavaFxGrid(thankYouButton);

    Button cancelButton = new Button("CANCEL");
    cancelButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.cancel();
    });
    controlPane.add(cancelButton, 0, 1);
    fillJavaFxGrid(cancelButton);

    // PrintImage
    {
      MenuItem defaultPrinter = new MenuItem("Default Printer");
      defaultPrinter.setOnAction(event -> {
        OpenCashDrawerRequest ocdr = new OpenCashDrawerRequest("Testing");
        ocdr.setDeviceId(null);
        cloverConnector.openCashDrawer(ocdr);
      });
      MenuItem loadingMenuItem = new MenuItem("Loading Printers...");

      SplitMenuButton openCashDrawerButton = new SplitMenuButton(defaultPrinter, loadingMenuItem);
      openCashDrawerButton.showingProperty().addListener(new ChangeListener<Boolean>() {
        @Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
          System.out.println("it opened...");
          if(!oldValue && newValue) {

            cloverConnector.addCloverConnectorListener(new DefaultCloverConnectorListener(cloverConnector) {
              @Override public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
                // do nothing here...
              }

              @Override public void onRetrievePrintersResponse(RetrievePrintersResponse response) {
                cloverConnector.removeCloverConnectorListener(this);

                Platform.runLater(new Runnable(){
                  @Override public void run() {
                    openCashDrawerButton.getItems().remove(loadingMenuItem);
                    for(Printer p : response.getPrinters()) {
                      MenuItem mi = new MenuItem(p.getName() + ":" + p.getId());
                      mi.setOnAction(event -> {
                        OpenCashDrawerRequest ocdr = new OpenCashDrawerRequest("Testing");
                        ocdr.setDeviceId(p.getId());
                        cloverConnector.openCashDrawer(ocdr);
                      });
                      openCashDrawerButton.getItems().add(mi);
                    }
                  }
                });
              }
            });
            cloverConnector.retrievePrinters(new RetrievePrintersRequest(null));
          } else {
            Platform.runLater(new Runnable(){
              @Override public void run() {
                openCashDrawerButton.getItems().clear();
                openCashDrawerButton.getItems().add(defaultPrinter);
                openCashDrawerButton.getItems().add(loadingMenuItem);

              }
            });
          }
        }
      });
      openCashDrawerButton.setText("OPEN CASH DRAWER");
      openCashDrawerButton.setOnAction(event -> {
        clearLabel();
        cloverConnector.openCashDrawer("Testing");
      });

      controlPane.add(openCashDrawerButton, 1, 1);
      fillJavaFxGrid(openCashDrawerButton);
    }

    Button closeoutButton = new Button("CLOSEOUT ORDERS");
    closeoutButton.setOnAction(event -> {
      clearLabel();
      CloseoutRequest request = new CloseoutRequest();
      request.setAllowOpenTabs(false);
      request.setBatchId(null);
      cloverConnector.closeout(request);
    });
    controlPane.add(closeoutButton, 0, 2);
    fillJavaFxGrid(closeoutButton);

    // PrintImage
    {
      MenuItem defaultPrinter = new MenuItem("Default Printer");
      defaultPrinter.setOnAction(event -> {
        printImageURLToPrinter("https://www.clover.com/assets/images/public-site/press/clover_primary_gray_rgb.png", null);
      });
      MenuItem loadingMenuItem = new MenuItem("Loading Printers...");

      SplitMenuButton printImageButton = new SplitMenuButton(defaultPrinter, loadingMenuItem);
      printImageButton.showingProperty().addListener(new ChangeListener<Boolean>() {
        @Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
          System.out.println("it opened...");
          if(!oldValue && newValue) {

            cloverConnector.addCloverConnectorListener(new DefaultCloverConnectorListener(cloverConnector) {
              @Override public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
                // do nothing here...
              }

              @Override public void onRetrievePrintersResponse(RetrievePrintersResponse response) {
                cloverConnector.removeCloverConnectorListener(this);

                Platform.runLater(new Runnable(){
                  @Override public void run() {
                    printImageButton.getItems().remove(loadingMenuItem);
                    for(Printer p : response.getPrinters()) {
                      MenuItem mi = new MenuItem(p.getName() + ":" + p.getId());
                      mi.setOnAction(event -> {
                        printImageURLToPrinter("https://www.clover.com/assets/images/public-site/press/clover_primary_gray_rgb.png", p.getId());
                      });
                      printImageButton.getItems().add(mi);
                    }
                  }
                });
              }
            });
            cloverConnector.retrievePrinters(new RetrievePrintersRequest(null));
          } else {
            Platform.runLater(new Runnable(){
              @Override public void run() {
                printImageButton.getItems().clear();
                printImageButton.getItems().add(defaultPrinter);
                printImageButton.getItems().add(loadingMenuItem);

              }
            });
          }
        }
      });
      printImageButton.setText("PRINT IMAGE");
      printImageButton.setOnAction(event -> {
        clearLabel();
        cloverConnector.printImageFromURL("https://www.clover.com/assets/images/public-site/press/clover_primary_gray_rgb.png");
      });

      controlPane.add(printImageButton, 1, 2);
      fillJavaFxGrid(printImageButton);
    }


    VBox vbox = new VBox();
    vbox.setAlignment(Pos.CENTER);
    vbox.getChildren().addAll(new Label("GET DEVICE STATUS"), new Label("(RESEND)"));

    Button deviceStatusResendButton = new Button();
    deviceStatusResendButton.setGraphic(vbox);
    deviceStatusResendButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.retrieveDeviceStatus(new RetrieveDeviceStatusRequest(true));
    });
    controlPane.add(deviceStatusResendButton, 0, 3);
    fillJavaFxGrid(deviceStatusResendButton);

    Button deviceStatusButton = new Button("GET DEVICE STATUS");
    deviceStatusButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.retrieveDeviceStatus(new RetrieveDeviceStatusRequest(false));
    });
    controlPane.add(deviceStatusButton, 1, 3);
    fillJavaFxGrid(deviceStatusButton);

    Button readCardDataButton = new Button("READ CARD DATA");
    readCardDataButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.readCardData(new ReadCardDataRequest(store.getCardEntryMethods()));
    });
    controlPane.add(readCardDataButton, 0, 4);
    fillJavaFxGrid(readCardDataButton);

    Button resetButton = new Button("RESET DEVICE");
    resetButton.setStyle("-fx-background-color: red");
    resetButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.resetDevice();
    });
    controlPane.add(resetButton, 1, 4);
    fillJavaFxGrid(resetButton);

    return controlPane;
  }

  private Node buildCustomActivitiesPane() {
    TitledPane settingsPane = new TitledPane();
    settingsPane.setText("Custom Activities");
    settingsPane.setCollapsible(false);
    settingsPane.setPadding(new Insets(10, 10, 0, 10));

    // Blocking settings
    GridPane gridPane = new GridPane();
    gridPane.setHgap(4);
    gridPane.setVgap(4);
    gridPane.setPadding(new Insets(10, 10, 10, 10));

    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(50);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(50);
    gridPane.getColumnConstraints().addAll(column1, column2);

    addSelection(gridPane, "Non-Blocking", false, this::setNonBlocking, 0);

    // Activity selection and payload
    ChoiceBox<CustomActivity> choice = new ChoiceBox<>(FXCollections.observableArrayList(CustomActivity.values()));
    choice.setMaxWidth(Double.MAX_VALUE);
    choice.getSelectionModel().selectFirst();
    gridPane.add(choice, 0, 1, 2,1);

    TextField textField = new TextField("{\"name\":\"Bob\"}");
    textField.setMaxWidth(Double.MAX_VALUE);
    gridPane.add(textField, 0, 2, 2,1);

    // Start button
    // Forward define message button
    Button messageButton = new Button("Send Message");

    Button startButton = new Button("Start");
    startButton.setOnAction(event -> {
      CustomActivity selection = choice.getSelectionModel().getSelectedItem();

      CustomActivityRequest car = new CustomActivityRequest(selection.getActivityId());
      car.setPayload(textField.getText());
      car.setNonBlocking(nonBlocking);
      cloverConnector.startCustomActivity(car);

      //If the custom activity is conversational, pass in the messageTo and messageFrom action string arrays
      messageButton.setDisable(!selection.isConversational());

    });
    gridPane.add(startButton, 0, 3);
    fillJavaFxGrid(startButton);

    // Message Button
    messageButton.setDisable(true);
    messageButton.setOnAction(event -> {
      CustomActivity selection = choice.getSelectionModel().getSelectedItem();

      ConversationQuestionMessage message = new ConversationQuestionMessage("Why did the Storm Trooper buy an iPhone?");
      String payload = message.toJsonString();
      MessageToActivity messageRequest = new MessageToActivity(selection.getActivityId(), payload);
      cloverConnector.sendMessageToActivity(messageRequest);
      messageButton.setDisable(true);
    });
    gridPane.add(messageButton, 1, 3);
    fillJavaFxGrid(messageButton);

    settingsPane.setContent(gridPane);
    return settingsPane;
  }

  private Node buildTransactionSettingsPane() {
    TitledPane settingsPane = new TitledPane();
    settingsPane.setText("Transaction Settings");
    settingsPane.setCollapsible(false);
    settingsPane.setPadding(new Insets(10, 10, 0, 10));

    // Payment input settings
    GridPane inputConfigPane = new GridPane();
    inputConfigPane.setHgap(2);
    inputConfigPane.setVgap(2);
    inputConfigPane.setPadding(new Insets(0, 10, 0, 10));

    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(50);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(50);
    inputConfigPane.getColumnConstraints().addAll(column1, column2);

    CheckBox manualCB = new CheckBox();
    CheckBox swipeCB = new CheckBox();
    CheckBox chipCB = new CheckBox();
    CheckBox nearFieldCB = new CheckBox();

    EventHandler<ActionEvent> inputHandler = event -> {
      int value = 0;
      value |= manualCB.isSelected() ? CloverConnector.CARD_ENTRY_METHOD_MANUAL : 0;
      value |= swipeCB.isSelected() ? CloverConnector.CARD_ENTRY_METHOD_MAG_STRIPE : 0;
      value |= chipCB.isSelected() ? CloverConnector.CARD_ENTRY_METHOD_ICC_CONTACT : 0;
      value |= nearFieldCB.isSelected() ? CloverConnector.CARD_ENTRY_METHOD_NFC_CONTACTLESS : 0;

      store.setCardEntryMethods(value);
    };

    inputConfigPane.add(new Label("Manual"), 0, 0);

    manualCB.setSelected(false);
    manualCB.setOnAction(inputHandler);
    addRightJustifiedForGrid(inputConfigPane, manualCB, 1, 0);

    inputConfigPane.add(new Label("Swipe"), 0, 1);

    swipeCB.setSelected(true);
    swipeCB.setOnAction(inputHandler);
    addRightJustifiedForGrid(inputConfigPane, swipeCB, 1, 1);

    inputConfigPane.add(new Label("Chip"), 0, 2);

    chipCB.setSelected(true);
    chipCB.setOnAction(inputHandler);
    addRightJustifiedForGrid(inputConfigPane, chipCB, 1, 2);

    inputConfigPane.add(new Label("Contactless"), 0, 3);

    nearFieldCB.setSelected(true);
    nearFieldCB.setOnAction(inputHandler);
    addRightJustifiedForGrid(inputConfigPane, nearFieldCB, 1, 3);


    // Offline Payment Settings
    GridPane offlinePane = new GridPane();
    offlinePane.setHgap(2);
    offlinePane.setVgap(2);
    offlinePane.setPadding(new Insets(10, 10, 0, 10));

    column1 = new ColumnConstraints();
    column1.setPercentWidth(50);
    column2 = new ColumnConstraints();
    column2.setPercentWidth(50);
    offlinePane.getColumnConstraints().addAll(column1, column2);

    addTriStateGroup(offlinePane, "Force Offline Payment", store.getForceOfflinePayment(), store::setForceOfflinePayment, 0);
    addTriStateGroup(offlinePane, "Allow Offline Payment", store.getAllowOfflinePayment(), store::setAllowOfflinePayment, 1);
    addTriStateGroup(offlinePane, "Accept Offline w/o Prompt", store.getApproveOfflinePaymentWithoutPrompt(), store::setApproveOfflinePaymentWithoutPrompt, 2);

    // Tip settings
    Pane tipPane = createChoiceAndTextEntry("Tip Mode", SaleRequest.TipMode.class, SaleRequest.TipMode.values(),
        store.getTipMode(), store::setTipMode, "Sale Tip Amount", store::setTipAmount);

    // Signature Settings
    Pane signaturePane = createChoiceAndTextEntry("Signature Entry Location", DataEntryLocation.class, DataEntryLocation.values(),
        store.getSignatureEntryLocation(), store::setSignatureEntryLocation, "Signature Threshold", store::setSignatureThreshold);

    // Misc boolean settings
    GridPane miscPane = new GridPane();
    miscPane.setHgap(2);
    miscPane.setVgap(2);
    miscPane.setPadding(new Insets(10, 10, 0, 10));

    column1 = new ColumnConstraints();
    column1.setPercentWidth(60);
    column2 = new ColumnConstraints();
    column2.setPercentWidth(40);
    miscPane.getColumnConstraints().addAll(column1, column2);

    addTriStateGroup(miscPane, "Disable Duplicate Payment Checking", store.getDisableDuplicateChecking(), store::setDisableDuplicateChecking, 0);
    addTriStateGroup(miscPane, "Disable Device Receipt Options Screen", store.getDisableReceiptOptions(), store::setDisableReceiptOptions, 1);
    addTriStateGroup(miscPane, "Disable Device Printing", store.getDisablePrinting(), store::setDisablePrinting, 2);
    addTriStateGroup(miscPane, "Auto Confirm Signature", store.getAutomaticSignatureConfirmation(), store::setAutomaticSignatureConfirmation, 3);
    addTriStateGroup(miscPane, "Auto Confirm Payment Challenges", store.getAutomaticPaymentConfirmation(), store::setAutomaticPaymentConfirmation, 4);

    VBox vBox = new VBox();
    vBox.getChildren().addAll(inputConfigPane, offlinePane, tipPane, signaturePane, miscPane);

    settingsPane.setContent(vBox);
    return settingsPane;
  }

  private Node buildRestPane() {
    TitledPane restPane = new TitledPane();
    restPane.setText("REST");
    restPane.setCollapsible(false);
    restPane.setPadding(new Insets(10, 10, 0, 10));

    VBox restPaneContent = new VBox();
    restPaneContent.setSpacing(2);
    restPane.setContent(restPaneContent);

    HBox idBox = new HBox();
    final TextField appIdTF = new TextField();
    Button oAuthButton = new Button("OAuth Token");
    oAuthButton.setOnAction(event -> {
        new Thread() {
          boolean running = true;
          int port = 22102;
          String applicationId = appIdTF.getText();
          String endpoint = "https://dev1.dev.clover.com";

          @Override
          public void run() {

            try {
              if (Desktop.isDesktopSupported()) {
                try {
                  Desktop.getDesktop().browse(new URI(endpoint + "/oauth/authorize?client_id=" + applicationId + "&response_type=token&redirect_uri=http://localhost:" + port));
                } catch (URISyntaxException e) {
                  Runtime.getRuntime().exec("rundll32 " + endpoint + "/oauth/authorize?client_id=" + applicationId + "&response_type=token");
                }
              }
              //                Runtime.getRuntime().exec("xdg-open https://dev1.dev.clover.com/oauth/authorize?client_id=5FTA0E29EM826&response_type=token");
              ServerSocket ss = new ServerSocket(port);
              while (running) {
                if (ss == null) {
                  running = false;
                } else {
                  final Socket socket = ss.accept();
                  handleConnection(ss, socket, port);
                }
              }


            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }.start();
    });
    idBox.getChildren().addAll(oAuthButton, new javafx.scene.control.Label("Application Id"), appIdTF);
    restPaneContent.getChildren().add(idBox);


    final String apiEndpoint = "https://apidev1.dev.clover.com:443";
    Button resetButton = new Button("Get Payments");
    resetButton.setOnAction(event -> {
      BufferedReader reader = null;
      try {

        URL rawUrl = new URL(apiEndpoint + "/v3/merchants/" + merchantId + "/payments?access_token=" + accessToken);
        URLConnection urlConnection = rawUrl.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        InputStream inputStream = urlConnection.getInputStream();
        reader = new BufferedReader(new InputStreamReader(inputStream));
//        String line = reader.readLine();
//        while(line != null) {
//          System.out.println(line);
//          line = reader.readLine();
//        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    restPaneContent.getChildren().add(resetButton);

    return restPane;
  }

  private void addRightJustifiedForGrid(GridPane pane, Node node, int column, int row) {
    HBox hBox = new HBox();
    hBox.getChildren().addAll(node);
    hBox.setAlignment(Pos.CENTER_RIGHT);

    pane.add(hBox, column, row);
  }

  private void setNonBlocking(boolean nonBlocking) {
    this.nonBlocking = nonBlocking;
  }

  private void addTriStateGroup(GridPane gridPane, String label, Boolean defaultValue, Consumer<Boolean> consumer, int row) {
    gridPane.add(new Label(label), 0, row);

    ToggleGroup group = new ToggleGroup();
    group.selectedToggleProperty().addListener((obs, oldValue, newValue) -> {
      if (group.getSelectedToggle() != null) {
        consumer.accept((Boolean) group.getSelectedToggle().getUserData());
      }
    });

    RadioButton defaultButton = new RadioButton("Default");
    defaultButton.setToggleGroup(group);
    defaultButton.setUserData(null);

    RadioButton yesButton = new RadioButton("Yes");
    yesButton.setToggleGroup(group);
    yesButton.setUserData(Boolean.TRUE);

    RadioButton noButton = new RadioButton("No");
    noButton.setToggleGroup(group);
    noButton.setUserData(Boolean.FALSE);

    RadioButton selected = defaultValue == null ? defaultButton : (defaultValue ? yesButton : noButton);
    selected.setSelected(true);

    HBox hBox = new HBox();
    hBox.getChildren().addAll(defaultButton, yesButton, noButton);
    hBox.setAlignment(Pos.CENTER_RIGHT);

    gridPane.add(hBox, 1, row);
  }

  private <T extends Enum<T>> Pane createChoiceAndTextEntry(String choiceLabel, Class<T> clazz, T[] choiceItems, T defaultItem, Consumer<T> choiceConsumer,
                                                         String textLabel, Consumer<Long> textConsumer) {
    GridPane pane = new GridPane();
    pane.setHgap(2);
    pane.setVgap(2);
    pane.setPadding(new Insets(10, 10, 0, 10));

    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(40);
    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(60);
    pane.getColumnConstraints().addAll(column1, column2);

    pane.add(new Label(choiceLabel), 0, 0);

    final String DEFAULT = "DEFAULT";
    List<String> values = new LinkedList<>();
    values.add(DEFAULT);
    Stream.of(choiceItems).forEach(e -> values.add(e.toString()));

    ChoiceBox<String> choice = new ChoiceBox<>(FXCollections.observableArrayList(values));
    choice.setMaxWidth(Double.MAX_VALUE);
    choice.setOnAction(event -> {
      String selection = choice.getSelectionModel().getSelectedItem();
      T item = selection.equals(DEFAULT) ? null : Enum.valueOf(clazz, selection);
      choiceConsumer.accept(item);
    });
    choice.getSelectionModel().select(defaultItem != null ? defaultItem.toString() : DEFAULT);
    pane.add(choice, 1, 0);

    pane.add(new Label(textLabel), 0, 1);

    TextField textField = new TextField();
    textField.setMaxWidth(Double.MAX_VALUE);
    textField.textProperty().addListener((obs, oldValue, newValue) -> {
      Long amount = null;
      try
      {
        amount = Long.valueOf(newValue);
      } catch (Exception ex) {
        // NO-OP
        textField.setText((newValue == null || newValue.trim().isEmpty()) && !oldValue.isEmpty() ? "" : oldValue);
      }
      textConsumer.accept(amount);
    });
    pane.add(textField, 1, 1);

    return pane;
  }

  private void addSelection(GridPane pane, String label, Boolean selected, Consumer<Boolean> consumer, int row) {
    pane.add(new Label(label), 0, row);

    CheckBox checkBox = new CheckBox();
    checkBox.setSelected(selected != null ? selected : false);
    if (consumer != null) {
      checkBox.setOnAction(event -> consumer.accept(checkBox.isSelected()));
    }
    checkBox.setAlignment(Pos.CENTER_RIGHT);
    addRightJustifiedForGrid(pane, checkBox, 1, row);
  }

  private void handleConnection(final ServerSocket ss, final Socket socket, final int port) {
    try {
      final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      new Thread() {
        @Override public void run() {
          try {
            String line = reader.readLine();
            System.out.println("read: " + line);
            if (line != null) {
              if (line.startsWith("GET /?")) {
                out.print("<html><script>var newLoc = document.URL.replace('#', '&'); if(newLoc != document.URL) {document.location.href = document.URL.replace('#', '&').replace(':"+port  + "/',':"+port+"/capture')} else {document.write(document.URL)}</script></html>");
                // break;
              } else if (line.startsWith("GET /capture")) {
                ss.close();
                Pattern pattern = Pattern.compile("access_token=([A-Za-z0-9-]*)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                  accessToken = matcher.group(1);
                }
                Pattern marchantPattern = Pattern.compile("merchant_id=([A-Za-z0-9-]*)");
                Matcher merchantMatcher = marchantPattern.matcher(line);
                if (merchantMatcher.find()) {
                  merchantId = merchantMatcher.group(1);
                }
                out.print("<html><script>document.write(document.URL); window.close();</script><p><p>" + accessToken + "<p><p>" + merchantId + "</html>");

              }
            }
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            try {
              out.close();
              reader.close();
              socket.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }.start();
    }
    catch (Exception e) {

    }
    finally {

    }
  }
}


