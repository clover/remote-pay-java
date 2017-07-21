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

import com.clover.remote.client.lib.example.model.OrderObserver;
import com.clover.remote.client.lib.example.model.POSDiscount;
import com.clover.remote.client.lib.example.model.POSExchange;
import com.clover.remote.client.lib.example.model.POSItem;
import com.clover.remote.client.lib.example.model.POSLineItem;
import com.clover.remote.client.lib.example.model.POSOrder;
import com.clover.remote.client.lib.example.model.POSPayment;
import com.clover.remote.client.lib.example.model.POSRefund;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.lib.example.utils.CurrencyUtils;
import com.clover.remote.client.lib.example.utils.IdUtils;
import com.clover.remote.client.messages.AuthRequest;
import com.clover.remote.client.messages.SaleRequest;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;

import java.util.Locale;

public class RegisterTab extends AbstractExampleTab {

  public RegisterTab(POSStore store, Label statusLabel) {
    super(store, statusLabel, "REGISTER");
  }

  @Override
  protected Node buildPane() {
    SplitPane splitPane = new SplitPane();

    {
      final TableView<POSLineItem> tableView = new TableView<>();

      TableColumn<POSLineItem, String> qColumn = new TableColumn<>("Q");
      qColumn.setCellValueFactory(e -> new SimpleStringProperty(Integer.toString(e.getValue().getQuantity())));
      qColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
      qColumn.setPrefWidth(30);

      TableColumn<POSLineItem, String> nColumn = new TableColumn<>("Name");
      nColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getItem().getName()));
      nColumn.setPrefWidth(160);

      TableColumn<POSLineItem, String> pColumn = new TableColumn<>("Price");
      pColumn.setCellValueFactory(e -> new SimpleStringProperty(CurrencyUtils.format(e.getValue().getItem().getPrice(), Locale.getDefault())));
      pColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
      pColumn.setPrefWidth(70);

      tableView.getColumns().add(qColumn);// quantity
      tableView.getColumns().add(nColumn);// item
      tableView.getColumns().add(pColumn);// price

      BorderPane currentOrderPane = new BorderPane();
     {
        BorderPane orderStats = new BorderPane();

        GridPane gPane = new GridPane();
        Label totalLabel = new Label("Total:");
        totalLabel.setPrefWidth(200);
        Label total = new Label();

        Font partialFont = new Font(totalLabel.getFont().getName(), totalLabel.getFont().getSize() * .8);

        Label subTotalLabel = new Label("Subtotal:");
        subTotalLabel.setFont(partialFont);
        gPane.add(subTotalLabel, 0, 0);

        Label subTotal = new Label();
        subTotal.setFont(partialFont);
        addRightJustifiedForGrid(gPane, subTotal, 1, 0);

        Label discountsLabel = new Label("Discounts:");
        discountsLabel.setFont(partialFont);
        gPane.add(discountsLabel, 0, 1);

        Label discounts = new Label();
        discounts.setFont(partialFont);
        addRightJustifiedForGrid(gPane, discounts, 1, 1);

        Label taxLabel = new Label("Tax:");
        taxLabel.setFont(partialFont);
        gPane.add(taxLabel, 0, 2);

        Label tax = new Label();
        tax.setFont(partialFont);
        gPane.add(tax, 1, 2);
        addRightJustifiedForGrid(gPane, tax, 1, 2);

        gPane.add(totalLabel, 0, 3);
        addRightJustifiedForGrid(gPane, total, 1, 3);

        HBox orderButtons = new HBox();

        Button newButton = new Button("New");
        newButton.setOnAction(event -> store.createOrder(true));

        Button saleButton = new Button("Sale");
        saleButton.setOnAction(event -> {
          SaleRequest request = new SaleRequest(store.getCurrentOrder().getTotal(), IdUtils.getNextId());
          request.setCardEntryMethods(store.getCardEntryMethods());
          request.setAllowOfflinePayment(store.getAllowOfflinePayment());
          request.setForceOfflinePayment(store.getForceOfflinePayment());
          request.setApproveOfflinePaymentWithoutPrompt(store.getApproveOfflinePaymentWithoutPrompt());
          request.setTippableAmount(store.getCurrentOrder().getTippableAmount());
          request.setTaxAmount(store.getCurrentOrder().getTaxAmount());
          request.setDisablePrinting(store.getDisablePrinting());
          request.setTipMode(store.getTipMode());
          request.setSignatureEntryLocation(store.getSignatureEntryLocation());
          request.setSignatureThreshold(store.getSignatureThreshold());
          request.setDisableReceiptSelection(store.getDisableReceiptOptions());
          request.setDisableDuplicateChecking(store.getDisableDuplicateChecking());
          request.setTipAmount(store.getTipAmount());
          request.setAutoAcceptPaymentConfirmations(store.getAutomaticPaymentConfirmation());
          request.setAutoAcceptSignature(store.getAutomaticSignatureConfirmation());

          cloverConnector.sale(request);
        });

        Button authButton = new Button("Auth");
        authButton.setOnAction(event -> {
          AuthRequest request = new AuthRequest(store.getCurrentOrder().getTotal(), IdUtils.getNextId());
          request.setCardEntryMethods(store.getCardEntryMethods());
          request.setAllowOfflinePayment(store.getAllowOfflinePayment());
          request.setForceOfflinePayment(store.getForceOfflinePayment());
          request.setApproveOfflinePaymentWithoutPrompt(store.getApproveOfflinePaymentWithoutPrompt());
          request.setTippableAmount(store.getCurrentOrder().getTippableAmount());
          request.setTaxAmount(store.getCurrentOrder().getTaxAmount());
          request.setDisablePrinting(store.getDisablePrinting());
          request.setSignatureEntryLocation(store.getSignatureEntryLocation());
          request.setSignatureThreshold(store.getSignatureThreshold());
          request.setDisableReceiptSelection(store.getDisableReceiptOptions());
          request.setDisableDuplicateChecking(store.getDisableDuplicateChecking());
          request.setAutoAcceptPaymentConfirmations(store.getAutomaticPaymentConfirmation());
          request.setAutoAcceptSignature(store.getAutomaticSignatureConfirmation());
          cloverConnector.auth(request);
        });

        saleButton.setStyle("-fx-background-color: green");
        authButton.setStyle("-fx-background-color: green");

        orderButtons.setSpacing(10);
        orderButtons.setAlignment(Pos.BASELINE_RIGHT);

        orderButtons.getChildren().add(newButton);
        orderButtons.getChildren().add(saleButton);
        orderButtons.getChildren().add(authButton);


        orderStats.setTop(gPane);
        orderStats.setBottom(orderButtons);

        store.addCurrentOrderObserver(new OrderObserver() {
          @Override public void lineItemAdded(POSOrder posOrder, POSLineItem lineItem) {
            tableView.getItems().add(lineItem);
            subTotal.setText(CurrencyUtils.format(store.getCurrentOrder().getPreTaxSubTotal(), Locale.getDefault()));
            tax.setText(CurrencyUtils.format(store.getCurrentOrder().getTaxAmount(), Locale.getDefault()));
            total.setText(CurrencyUtils.format(store.getCurrentOrder().getTotal(), Locale.getDefault()));
          }

          @Override public void lineItemRemoved(POSOrder posOrder, POSLineItem lineItem) {
            tableView.getItems().remove(lineItem);
            subTotal.setText(CurrencyUtils.format(store.getCurrentOrder().getPreTaxSubTotal(), Locale.getDefault()));
            tax.setText(CurrencyUtils.format(store.getCurrentOrder().getTaxAmount(), Locale.getDefault()));
            total.setText(CurrencyUtils.format(store.getCurrentOrder().getTotal(), Locale.getDefault()));
          }
          @Override public void lineItemChanged(POSOrder posOrder, POSLineItem lineItem) {
            tableView.getItems().clear();
            tableView.getItems().addAll(posOrder.getItems());
            subTotal.setText(CurrencyUtils.format(store.getCurrentOrder().getPreTaxSubTotal(), Locale.getDefault()));
            tax.setText(CurrencyUtils.format(store.getCurrentOrder().getTaxAmount(), Locale.getDefault()));
            total.setText(CurrencyUtils.format(store.getCurrentOrder().getTotal(), Locale.getDefault()));
          }
          @Override public void paymentAdded(POSOrder posOrder, POSPayment payment) {}
          @Override public void refundAdded(POSOrder posOrder, POSRefund refund) {}
          @Override public void paymentChanged(POSOrder posOrder, POSExchange pay) {}
          @Override public void discountAdded(POSOrder posOrder, POSDiscount discount) {}
          @Override public void discountChanged(POSOrder posOrder, POSDiscount discount) {}
        });

        store.addStoreObserver(new DefaultStoreObserver() {
          @Override
          public void newOrderCreated(POSOrder order, boolean userInitiated) {
            tableView.getItems().clear();
            Platform.runLater(() -> {
              subTotal.setText("");
              tax.setText("");
              total.setText("");
            });
          }
        });

        currentOrderPane.setTop(new Label("Current Order:"));
        currentOrderPane.setCenter(tableView);
        currentOrderPane.setBottom(orderStats);
      }


      splitPane.getItems().add(currentOrderPane);
      SplitPane.setResizableWithParent(currentOrderPane, false);
    }
    {
      TilePane availableItemsPane = new TilePane();
      availableItemsPane.setHgap(5);
      availableItemsPane.setVgap(5);

      for(final POSItem item : store.getAvailableItems()) {
        AvailableItemPane availItem = new AvailableItemPane();
        availItem.setItem(item);
        availableItemsPane.getChildren().add(availItem);

        availItem.setOnMouseClicked(event -> store.getCurrentOrder().addItem(item, 1));
      }
      splitPane.getItems().add(availableItemsPane);
    }
    splitPane.setDividerPosition(0, .3);
    return splitPane;
  }

  private void addRightJustifiedForGrid(GridPane pane, Node node, int column, int row) {
    HBox hBox = new HBox();
    hBox.getChildren().addAll(node);
    hBox.setAlignment(Pos.CENTER_RIGHT);

    pane.add(hBox, column, row);
  }


}
