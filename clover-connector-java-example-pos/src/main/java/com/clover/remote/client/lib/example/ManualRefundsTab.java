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

import com.clover.remote.client.lib.example.model.POSNakedRefund;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.lib.example.utils.IdUtils;
import com.clover.remote.client.messages.ManualRefundRequest;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class ManualRefundsTab extends AbstractExampleTab {

  private TableView<POSNakedRefund> table;

  public ManualRefundsTab(POSStore store, Label statusLabel) {
    super(store, statusLabel, "REFUND");
    store.addStoreObserver(new DefaultStoreObserver() {
      @Override
      public void refundAdded(POSNakedRefund refund) {
        table.getItems().add(refund);
      }
    });
  }

  @Override
  protected Node buildPane() {
    FlowPane fp = new FlowPane();
    fp.orientationProperty().setValue(Orientation.VERTICAL);

    // Build the manual refund input pane
    GridPane inputPane = new GridPane();
    inputPane.setHgap(2);
    inputPane.setVgap(2);
    inputPane.setPadding(new Insets(10, 10, 0, 10));

    Button refundButton = new Button("REFUND");
    refundButton.setDisable(true);

    TextField input = new TextField();
    input.setPrefWidth(80);

    refundButton.setOnAction(event -> {
      clearLabel();
      long refundAmount = Long.valueOf(input.getText());
      ManualRefundRequest request = new ManualRefundRequest(refundAmount, IdUtils.getNextId());
      request.setCardEntryMethods(store.getCardEntryMethods());
      request.setDisablePrinting(store.getDisablePrinting());
      request.setDisableReceiptSelection(store.getDisableReceiptOptions());
      cloverConnector.manualRefund(request);
    });

    input.textProperty().addListener((obs, oldValue, newValue) -> {
      // Verify input is numerical
      try
      {
        Long.valueOf(newValue);
        refundButton.setDisable(false);
      } catch (Exception ex) {
        // NO-OP
        input.setText((newValue == null || newValue.trim().isEmpty()) && !oldValue.isEmpty() ? "" : oldValue);
        refundButton.setDisable(input.getText().isEmpty());
      }
    });

    inputPane.add(new Label("Refund Amount:  "), 0, 0);
    inputPane.add(input, 1, 0);
    inputPane.add(refundButton, 2, 0);

    // Build the manual refund display pane
    TitledPane displayPane = new TitledPane();
    displayPane.setText("Processed Refunds");
    displayPane.setCollapsible(false);
    displayPane.setPadding(new Insets(10, 10, 0, 10));

    table = new TableView<>();
    table.setItems(FXCollections.observableArrayList());
    table.setEditable(false);

    TableColumn<POSNakedRefund, String> refundColumn = new TableColumn<>("Refund");
    refundColumn.setCellValueFactory(param -> new SimpleStringProperty(NumberFormat.getCurrencyInstance().format((double) param.getValue().Amount / 100)));
    table.getColumns().add(refundColumn);

    TableColumn<POSNakedRefund, String> idColumn = new TableColumn<>("ID");
    idColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().EmployeeID));
    table.getColumns().add(idColumn);

    TableColumn<POSNakedRefund, String> dateColumn = new TableColumn<>("Date");
    dateColumn.setCellValueFactory(param -> new SimpleStringProperty(new SimpleDateFormat().format(param.getValue().date)));
    table.getColumns().add(dateColumn);

    displayPane.setContent(table);

    fp.getChildren().addAll(inputPane, displayPane);

    return fp;
  }
}
