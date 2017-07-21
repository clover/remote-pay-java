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

import com.clover.remote.client.lib.example.model.POSExchange;
import com.clover.remote.client.lib.example.model.POSLineItem;
import com.clover.remote.client.lib.example.model.POSOrder;
import com.clover.remote.client.lib.example.model.POSPayment;
import com.clover.remote.client.lib.example.model.POSRefund;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.lib.example.utils.CurrencyUtils;
import com.clover.remote.client.messages.RefundPaymentRequest;
import com.clover.remote.client.messages.TipAdjustAuthRequest;
import com.clover.remote.client.messages.VoidPaymentRequest;
import com.clover.sdk.v3.order.VoidReason;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrdersTab extends AbstractExampleTab {

  private TableView<POSOrder> ordersTable;

  public OrdersTab(POSStore store, Label statusLabel) {
    super(store, statusLabel, "ORDERS");
  }

  public void refresh() {
    ordersTable.getItems().clear();
    ordersTable.getItems().addAll(store.getOrders().stream().filter(o -> o.getStatus() != POSOrder.OrderStatus.INITIAL).collect(Collectors.toList()));
  }

  @Override
  protected Node buildPane() {
    SplitPane splitPane = new SplitPane();
    splitPane.setOrientation(Orientation.VERTICAL);
    SplitPane bottomSplitPane = new SplitPane();

    final TableView<POSLineItem> orderItemsTable = new TableView<>();
    {
      TableColumn col = new TableColumn("Q");
      col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
        @Override public ObservableValue call(TableColumn.CellDataFeatures param) {
          return new SimpleIntegerProperty(((POSLineItem)param.getValue()).getQuantity());
        }
      });
      col.setPrefWidth(30);
      col.setStyle("-fx-alignment: CENTER-RIGHT;");
      orderItemsTable.getColumns().add(col);

      TableColumn<POSLineItem, String> col1 = new TableColumn<>("Item");
      col1.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getItem().getName()));
      col1.setPrefWidth(160);
      orderItemsTable.getColumns().add(col1);

      TableColumn<POSLineItem, String> col2 = new TableColumn<>("Amount");
      col2.setCellValueFactory(e -> new SimpleStringProperty(CurrencyUtils.format(e.getValue().getPrice(), Locale.getDefault())));
      col2.setPrefWidth(70);
      col2.setStyle("-fx-alignment: CENTER-RIGHT;");
      orderItemsTable.getColumns().add(col2);
    }

    final TableView<POSExchange> orderPaymentTable = new TableView<>();
    {
      TableColumn<POSExchange, String> col = new TableColumn<>("Status");
      col.setCellValueFactory(e -> {
        if (e.getValue() instanceof POSPayment) {
          POSPayment.Status status = ((POSPayment) e.getValue()).getPaymentStatus();
          if (status != null) {
            return new SimpleStringProperty(status.toString());
          }
        } else if (e.getValue() instanceof POSRefund) {
          return new SimpleStringProperty("REFUND");
        }
        return new SimpleStringProperty("");
      });
      col.setPrefWidth(100);
      orderPaymentTable.getColumns().add(col);

      TableColumn<POSExchange, String> col1 = new TableColumn<>("Amount");
      col1.setCellValueFactory(e -> new SimpleStringProperty(CurrencyUtils.format(e.getValue().getAmount(), Locale.getDefault())));
      col1.setStyle("-fx-alignment: CENTER-RIGHT;");
      orderPaymentTable.getColumns().add(col1);

      TableColumn<POSExchange, String> col2 = new TableColumn<>("Tip Amount");
      col2.setCellValueFactory(e -> {
        if (e.getValue() instanceof POSPayment) {
          return new SimpleStringProperty(CurrencyUtils.format(((POSPayment) e.getValue()).getTipAmount(), Locale.getDefault()));
        }
        return new SimpleStringProperty("");
      });
      col2.setStyle("-fx-alignment: CENTER-RIGHT;");
      orderPaymentTable.getColumns().add(col2);

      TableColumn<POSExchange, String> col3 = new TableColumn<>("External ID");
      col3.setCellValueFactory(e -> {
        if (e.getValue() instanceof POSPayment) {
          return new SimpleStringProperty(((POSPayment) e.getValue()).getExternalPaymentId());
        }
        return new SimpleStringProperty("");
      });
      col3.setPrefWidth(120);
      orderPaymentTable.getColumns().add(col3);
    }

    GridPane paymentControlPane = new GridPane();
    {
      Button voidButton = new Button("Void");
      voidButton.setDisable(true);
      voidButton.setOnAction(event -> {
        POSExchange posExchange = orderPaymentTable.getSelectionModel().getSelectedItem();
        if (posExchange instanceof POSPayment) {
          VoidPaymentRequest vpr = new VoidPaymentRequest();
          vpr.setPaymentId(posExchange.getPaymentID());
          vpr.setOrderId(posExchange.getOrderId());
          vpr.setVoidReason(VoidReason.USER_CANCEL.name());
          cloverConnector.voidPayment(vpr);
        }
      });
      paymentControlPane.add(voidButton, 0, 0);
      fillJavaFxGrid(voidButton);

      Button refundButton = new Button("Refund");
      refundButton.setDisable(true);
      refundButton.setOnAction(event -> {
        POSExchange posExchange = orderPaymentTable.getSelectionModel().getSelectedItem();
        if (posExchange instanceof POSPayment) {
          RefundPaymentRequest rpr = new RefundPaymentRequest();
          rpr.setPaymentId(posExchange.getPaymentID());
          rpr.setOrderId(posExchange.orderID);
          rpr.setFullRefund(true);
          cloverConnector.refundPayment(rpr);
        }
      });
      paymentControlPane.add(refundButton, 1, 0);
      fillJavaFxGrid(refundButton);

      Button tipAdjustButton = new Button("Tip Adjust");
      tipAdjustButton.setDisable(true);
      tipAdjustButton.setOnAction(event -> {
        POSExchange posExchange = orderPaymentTable.getSelectionModel().getSelectedItem();
        if (posExchange instanceof POSPayment) {

          TextInputDialog dialog = new TextInputDialog("");
          dialog.setTitle("Tip Adjustment");
          dialog.setContentText("Tip Adjustment");
          dialog.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            // Verify input is numerical
            try
            {
              Long.valueOf(newValue);
              dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
            } catch (Exception ex) {
              // NO-OP
              dialog.getEditor().setText((newValue == null || newValue.trim().isEmpty()) && !oldValue.isEmpty() ? "" : oldValue);
              dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            }
          });

          Optional<String> result = dialog.showAndWait();
          if (result.isPresent() && !result.get().isEmpty()) {
            long value = Long.valueOf(result.get());
            TipAdjustAuthRequest taar = new TipAdjustAuthRequest();
            taar.setPaymentId(posExchange.getPaymentID());
            taar.setOrderId(posExchange.getOrderId());
            taar.setTipAmount(value);
            cloverConnector.tipAdjustAuth(taar);
          }
        }
      });
      paymentControlPane.add(tipAdjustButton, 2, 0);
      fillJavaFxGrid(tipAdjustButton);

      Button receiptButton = new Button("Receipt");
      receiptButton.setDisable(true);
      receiptButton.setOnAction(event -> {
        POSExchange posExchange = orderPaymentTable.getSelectionModel().getSelectedItem();
        if (posExchange instanceof POSPayment) {
          cloverConnector.displayPaymentReceiptOptions(posExchange.orderID, posExchange.getPaymentID());
        }
      });
      paymentControlPane.add(receiptButton, 3, 0);
      fillJavaFxGrid(receiptButton);

      orderPaymentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
        boolean voidDisabled = true;
        boolean refundDisabled = true;
        boolean tipAdjustDisabled = true;
        boolean receiptDisabled = true;
        if (newValue instanceof POSPayment) {
          POSPayment payment = (POSPayment) newValue;
          if (payment.getPaymentStatus() == POSPayment.Status.AUTHORIZED) {
            voidDisabled = false;
            refundDisabled = false;
            tipAdjustDisabled = false;
            receiptDisabled = false;
          } else if (payment.getPaymentStatus() == POSPayment.Status.PAID) {
            voidDisabled = false;
            refundDisabled = false;
            receiptDisabled = false;
          }
        }
        voidButton.setDisable(voidDisabled);
        refundButton.setDisable(refundDisabled);
        tipAdjustButton.setDisable(tipAdjustDisabled);
        receiptButton.setDisable(receiptDisabled);
      });
    }

    ordersTable = new TableView<>();
    {
      TableColumn<POSOrder, String> col = new TableColumn<>("Order");
      col.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().id));
      ordersTable.getColumns().add(col);

      TableColumn<POSOrder, String> col1 = new TableColumn<>("Status");
      col1.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getStatus().toString()));
      ordersTable.getColumns().add(col1);

      TableColumn<POSOrder, String> col2 = new TableColumn<>("Date");
      col2.setCellValueFactory(e -> new SimpleStringProperty(new SimpleDateFormat().format(e.getValue().date)));
      col2.setPrefWidth(120);
      ordersTable.getColumns().add(col2);

      TableColumn<POSOrder, String> col3 = new TableColumn<>("Subtotal");
      col3.setCellValueFactory(e -> new SimpleStringProperty(CurrencyUtils.format(e.getValue().getPreTaxSubTotal(), Locale.getDefault())));
      col3.setStyle("-fx-alignment: CENTER-RIGHT;");
      ordersTable.getColumns().add(col3);

      TableColumn<POSOrder, String> col4 = new TableColumn<>("Tax");
      col4.setCellValueFactory(e -> new SimpleStringProperty(CurrencyUtils.format(e.getValue().getTaxAmount(), Locale.getDefault())));
      col4.setStyle("-fx-alignment: CENTER-RIGHT;");
      ordersTable.getColumns().add(col4);

      TableColumn<POSOrder, String> col5 = new TableColumn<>("Total");
      col5.setCellValueFactory(e -> new SimpleStringProperty(CurrencyUtils.format(e.getValue().getTotal(), Locale.getDefault())));
      col5.setStyle("-fx-alignment: CENTER-RIGHT;");
      ordersTable.getColumns().add(col5);
    }

    ordersTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
      orderPaymentTable.getItems().clear();
      orderItemsTable.getItems().clear();
      if (newValue != null) {
        for(POSExchange payment : newValue.getPayments()) {
          orderPaymentTable.getItems().add(payment);
        }

        for(POSLineItem li : newValue.getItems()) {
          orderItemsTable.getItems().add(li);
        }
      }
    });

    splitPane.getItems().add(ordersTable);
    splitPane.getItems().add(bottomSplitPane);

    VBox vBox = new VBox();
    vBox.getChildren().addAll(orderPaymentTable, paymentControlPane);

    bottomSplitPane.getItems().add(orderItemsTable);
    bottomSplitPane.getItems().add(vBox);

    return splitPane;
  }
}
