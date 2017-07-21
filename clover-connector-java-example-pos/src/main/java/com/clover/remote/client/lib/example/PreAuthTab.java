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

import com.clover.remote.client.lib.example.model.POSPayment;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.lib.example.utils.IdUtils;
import com.clover.remote.client.messages.CapturePreAuthRequest;
import com.clover.remote.client.messages.PreAuthRequest;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import java.text.NumberFormat;

public class PreAuthTab extends AbstractExampleTab {

  private TableView<POSPayment> table;

  public PreAuthTab(POSStore store, Label statusLabel) {
    super(store, statusLabel, "PREAUTH");
    store.addStoreObserver(new DefaultStoreObserver() {
      @Override
      public void preAuthAdded(POSPayment payment) {
        table.getItems().add(payment);
      }

      @Override
      public void preAuthRemoved(POSPayment payment) {
        if (table.getItems().remove(payment)) {
          cloverConnector.showWelcomeScreen();
        }
      }
    });
  }

  @Override
  protected Node buildPane() {
    FlowPane fp = new FlowPane();
    fp.orientationProperty().setValue(Orientation.VERTICAL);

    // Build the vault card control pane
    GridPane inputPane = new GridPane();
    inputPane.setHgap(2);
    inputPane.setVgap(2);
    inputPane.setPadding(new Insets(10, 10, 0, 10));

    Button preAuthButton = new Button("PREAUTH CARD");
    preAuthButton.setDisable(true);

    TextField input = new TextField();
    input.setPrefWidth(80);

    preAuthButton.setOnAction(event -> {
      clearLabel();
      long amount = Long.valueOf(input.getText());
      PreAuthRequest request = new PreAuthRequest(amount, IdUtils.getNextId());
      request.setCardEntryMethods(store.getCardEntryMethods());
      request.setDisablePrinting(store.getDisablePrinting());
      request.setSignatureEntryLocation(store.getSignatureEntryLocation());
      request.setSignatureThreshold(store.getSignatureThreshold());
      request.setDisableReceiptSelection(store.getDisableReceiptOptions());
      request.setDisableDuplicateChecking(store.getDisableDuplicateChecking());
      cloverConnector.preAuth(request);
    });

    input.textProperty().addListener((obs, oldValue, newValue) -> {
      // Verify input is numerical
      try
      {
        Long.valueOf(newValue);
        preAuthButton.setDisable(false);
      } catch (Exception ex) {
        // NO-OP
        input.setText((newValue == null || newValue.trim().isEmpty()) && !oldValue.isEmpty() ? "" : oldValue);
        preAuthButton.setDisable(true);
      }
    });

    inputPane.add(new Label("Pre-Auth Amount:  "), 0, 0);
    inputPane.add(input, 1, 0);
    inputPane.add(preAuthButton, 2, 0);

    // Build the vaulted card display pane
    TitledPane displayPane = new TitledPane();
    displayPane.setText("Pre-Authorized Cards");
    displayPane.setCollapsible(false);
    displayPane.setPadding(new Insets(10, 10, 0, 10));

    table = new TableView<>();
    table.setItems(FXCollections.observableArrayList());
    table.setEditable(false);
    table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    TableColumn<POSPayment, String> refundColumn = new TableColumn<>("Amount");
    refundColumn.setCellValueFactory(param -> new SimpleStringProperty(NumberFormat.getCurrencyInstance().format((double) param.getValue().getAmount() / 100)));
    table.getColumns().add(refundColumn);

    TableColumn<POSPayment, String> idColumn = new TableColumn<>("External Payment ID");
    idColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getExternalPaymentId()));
    table.getColumns().add(idColumn);

    TableColumn<POSPayment, String> dateColumn = new TableColumn<>("Order ID");
    dateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getOrderId()));
    table.getColumns().add(dateColumn);

    TableColumn<POSPayment, String> monthColumn = new TableColumn<>("Payment ID");
    monthColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPaymentID()));
    table.getColumns().add(monthColumn);

    displayPane.setContent(table);

    GridPane paymentPane = new GridPane();
    paymentPane.setHgap(2);
    paymentPane.setVgap(2);
    paymentPane.setPadding(new Insets(10, 10, 0, 10));

    Button payPreAuthButton = new Button("PAY WITH PRE-AUTH");
    payPreAuthButton.setDisable(true);
    payPreAuthButton.setOnAction(event -> {
      clearLabel();
      CapturePreAuthRequest car = new CapturePreAuthRequest();
      car.setPaymentID(table.getSelectionModel().getSelectedItem().getPaymentID());
      car.setAmount(store.getCurrentOrder().getTotal());
      car.setTipAmount(store.getCurrentOrder().getTips());
      cloverConnector.capturePreAuth(car);
    });

    table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> payPreAuthButton.setDisable(newValue == null));
    paymentPane.add(payPreAuthButton, 2, 0);

    fp.getChildren().addAll(inputPane, displayPane, paymentPane);

    return fp;
  }
}
