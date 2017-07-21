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

import com.clover.remote.client.lib.example.model.POSCard;
import com.clover.remote.client.lib.example.model.POSOrder;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.lib.example.utils.IdUtils;
import com.clover.remote.client.messages.AuthRequest;
import com.clover.remote.client.messages.SaleRequest;
import com.clover.sdk.v3.payments.VaultedCard;
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

public class CardsTab extends AbstractExampleTab {

  private TableView<POSCard> table;

  public CardsTab(POSStore store, Label statusLabel) {
    super(store, statusLabel, "CARDS");
    store.addStoreObserver(new DefaultStoreObserver() {
      @Override
      public void cardAdded(POSCard card) {
        table.getItems().add(card);
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

    Button vaultButton = new Button("VAULT CARD");
    vaultButton.setOnAction(event -> {
      clearLabel();
      cloverConnector.vaultCard(store.getCardEntryMethods());
    });

    Button saleButton = new Button("SALE");
    saleButton.setOnAction(event -> {
      clearLabel();

      VaultedCard vaultedCard = getVaultedCard();
      POSOrder order = store.getCurrentOrder();
      if (order != null && vaultedCard != null) {
        SaleRequest saleRequest = new SaleRequest(order.getTotal(), IdUtils.getNextId());
        saleRequest.setTippableAmount(order.getTippableAmount());
        saleRequest.setVaultedCard(vaultedCard);
        saleRequest.setTipMode(store.getTipMode());
        saleRequest.setSignatureEntryLocation(store.getSignatureEntryLocation());
        saleRequest.setSignatureThreshold(store.getSignatureThreshold());
        saleRequest.setDisableReceiptSelection(store.getDisableReceiptOptions());
        cloverConnector.sale(saleRequest);
      }
    });

    Button authButton = new Button("AUTH");
    saleButton.setOnAction(event -> {
      clearLabel();

      VaultedCard vaultedCard = getVaultedCard();
      POSOrder order = store.getCurrentOrder();
      if (order != null && vaultedCard != null) {
        AuthRequest authRequest = new AuthRequest(order.getTotal(), IdUtils.getNextId());
        authRequest.setVaultedCard(vaultedCard);
        authRequest.setSignatureEntryLocation(store.getSignatureEntryLocation());
        authRequest.setSignatureThreshold(store.getSignatureThreshold());
        authRequest.setDisableReceiptSelection(store.getDisableReceiptOptions());
        cloverConnector.auth(authRequest);
      }
    });

    inputPane.add(vaultButton, 0, 0);
    inputPane.add(saleButton, 1, 0);
    inputPane.add(authButton, 2, 0);

    // Build the vaulted card display pane
    TitledPane displayPane = new TitledPane();
    displayPane.setText("Vaulted Cards");
    displayPane.setCollapsible(false);
    displayPane.setPadding(new Insets(10, 10, 0, 10));

    table = new TableView<>();
    table.setItems(FXCollections.observableArrayList());
    table.setEditable(false);
    table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
      saleButton.setDisable(newValue == null);
      authButton.setDisable(newValue == null);
    });

    TableColumn<POSCard, String> refundColumn = new TableColumn<>("Name");
    refundColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
    table.getColumns().add(refundColumn);

    TableColumn<POSCard, String> idColumn = new TableColumn<>("First 6");
    idColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFirst6()));
    table.getColumns().add(idColumn);

    TableColumn<POSCard, String> dateColumn = new TableColumn<>("Last 4");
    dateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLast4()));
    table.getColumns().add(dateColumn);

    TableColumn<POSCard, String> monthColumn = new TableColumn<>("Month");
    monthColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMonth()));
    table.getColumns().add(monthColumn);

    TableColumn<POSCard, String> yearColumn = new TableColumn<>("Year");
    yearColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getYear()));
    table.getColumns().add(yearColumn);

    TableColumn<POSCard, String> tokenColumn = new TableColumn<>("Token");
    tokenColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getToken()));
    table.getColumns().add(tokenColumn);

    displayPane.setContent(table);

    fp.getChildren().addAll(displayPane, inputPane);

    return fp;
  }

  private VaultedCard getVaultedCard() {
    POSCard posCard = table.getSelectionModel().getSelectedItem();
    if (posCard != null) {
      VaultedCard vaultedCard = new VaultedCard();
      vaultedCard.setCardholderName(posCard.getName());
      vaultedCard.setFirst6(posCard.getFirst6());
      vaultedCard.setLast4(posCard.getLast4());
      vaultedCard.setExpirationDate(posCard.getMonth() + posCard.getYear());
      vaultedCard.setToken(posCard.getToken());

      return vaultedCard;
    }
    return null;
  }
}
