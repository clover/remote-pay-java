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

import com.clover.remote.PendingPaymentEntry;
import com.clover.remote.client.lib.example.model.POSStore;
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
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import java.text.NumberFormat;
import java.util.List;

public class PendingPayementsTab extends AbstractExampleTab {

  private TableView<PendingPaymentEntry> table;

  public PendingPayementsTab(POSStore store, Label statusLabel) {
    super(store, statusLabel, "PENDING");
    store.addStoreObserver(new DefaultStoreObserver() {
      @Override
      public void pendingPaymentsRetrieved(List<PendingPaymentEntry> pendingPayments) {
        table.getItems().setAll(pendingPayments);
      }
    });
  }

  @Override
  protected Node buildPane() {
    FlowPane fp = new FlowPane();
    fp.orientationProperty().setValue(Orientation.VERTICAL);

    // Build the vaulted card display pane
    TitledPane displayPane = new TitledPane();
    displayPane.setText("Pending Payments");
    displayPane.setCollapsible(false);
    displayPane.setPadding(new Insets(10, 10, 0, 10));

    table = new TableView<>();
    table.setItems(FXCollections.observableArrayList());
    table.setEditable(false);
    table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    TableColumn<PendingPaymentEntry, String> idColumn = new TableColumn<>("Payment ID");
    idColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().paymentId));
    idColumn.setPrefWidth(150);
    table.getColumns().add(idColumn);

    TableColumn<PendingPaymentEntry, String> amountColumn = new TableColumn<>("Amount");
    amountColumn.setCellValueFactory(param -> new SimpleStringProperty(NumberFormat.getCurrencyInstance().format((double) param.getValue().amount / 100)));
    table.getColumns().add(amountColumn);

    displayPane.setContent(table);

    GridPane refreshPane = new GridPane();
    refreshPane.setHgap(2);
    refreshPane.setVgap(2);
    refreshPane.setPadding(new Insets(10, 10, 0, 10));

    Button refreshButton = new Button("REFRESH");
    refreshButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.retrievePendingPayments();
    });

    refreshPane.add(refreshButton, 2, 0);

    fp.getChildren().addAll(displayPane, refreshPane);

    return fp;
  }
}
