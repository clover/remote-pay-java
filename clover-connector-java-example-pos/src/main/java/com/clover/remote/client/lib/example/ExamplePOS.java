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

import android.os.AsyncTask;
import com.clover.remote.client.CloverConnectorFactory;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.WebSocketCloverDeviceConfiguration;
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
import com.clover.remote.client.lib.example.utils.SecurityUtils;
import com.clover.remote.client.messages.CloverDeviceEvent;
import com.clover.remote.order.DisplayLineItem;
import com.clover.remote.order.DisplayOrder;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.straylightlabs.hola.dns.Domain;
import net.straylightlabs.hola.sd.Instance;
import net.straylightlabs.hola.sd.Query;
import net.straylightlabs.hola.sd.Service;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.prefs.Preferences;

public class ExamplePOS extends Application {

  private ICloverConnector cloverConnector;
  private CloverDeviceEvent.DeviceEventState lastEvent;
  private final Button disconnectButton = new Button("Reconnect");
  private final Label connectionStatusLabel = new Label("Not Connected");
  private final Label responseLabel = new Label("Waiting...");
  private final Label deviceActivityLabel = new Label("...");
  private final HBox activityButtonBox = new HBox();
  private final TabPane tabPane = new TabPane();

  private GlassPane glassPane = new GlassPane();
  private GlassPane sigGlassPane = new GlassPane();

  private final POSStore store = new POSStore();

  private ExamplePOSCloverConnectorListener baseListener = null;

  public ExamplePOS() {
    initStore();

    store.addStoreObserver(new DefaultStoreObserver() {
      @Override
      public void newOrderCreated(POSOrder order, boolean userInitiated) {
        cloverConnector.showWelcomeScreen();
      }
    });

    store.addCurrentOrderObserver(new OrderObserver() {
      @Override public void lineItemAdded(POSOrder posOrder, POSLineItem lineItem) {
        updateOrder();
      }
      @Override public void lineItemRemoved(POSOrder posOrder, POSLineItem lineItem) {
        updateOrder();
      }
      @Override public void lineItemChanged(POSOrder posOrder, POSLineItem lineItem) {
        updateOrder();
      }
      @Override public void paymentAdded(POSOrder posOrder, POSPayment payment) {}
      @Override public void refundAdded(POSOrder posOrder, POSRefund refund) {}
      @Override public void paymentChanged(POSOrder posOrder, POSExchange pay) {}
      @Override public void discountAdded(POSOrder posOrder, POSDiscount discount) {}
      @Override public void discountChanged(POSOrder posOrder, POSDiscount discount) {}

      private void updateOrder() {
        DisplayOrder displayOrder = new DisplayOrder();

        List<DisplayLineItem> lineItems = new ArrayList<DisplayLineItem>();
        for(POSLineItem pli : store.getCurrentOrder().getItems()) {
          DisplayLineItem dli = new DisplayLineItem();
          dli.setId(Math.random()+"");
          dli.setName(pli.getItem().getName());
          dli.setQuantity(pli.getQuantity()+"");
          dli.setPrice(CurrencyUtils.format(pli.getPrice(), Locale.getDefault()));

          lineItems.add(dli);
        }

        displayOrder.setLineItems(lineItems);
        displayOrder.setTotal(CurrencyUtils.format(store.getCurrentOrder().getTotal(), Locale.getDefault()));
        displayOrder.setTax(CurrencyUtils.format(store.getCurrentOrder().getTaxAmount(), Locale.getDefault()));

        cloverConnector.showDisplayOrder(displayOrder);
      }
    });
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage primaryStage) {

    primaryStage.setOnCloseRequest(event -> {
      if(cloverConnector != null) {
        cloverConnector.dispose();
      }
      System.exit(0);
    });

    activityButtonBox.setAlignment(Pos.CENTER);

    primaryStage.setTitle("Clover Example POS");

    BorderPane pane = new BorderPane();

    activityButtonBox.setAlignment(Pos.CENTER_LEFT);

    BorderPane topPane = new BorderPane();

    topPane.setLeft(disconnectButton);
    topPane.setCenter(connectionStatusLabel);
    connectionStatusLabel.setAlignment(Pos.CENTER_LEFT);

    pane.setTop(topPane);

    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    {
      OrdersTab ordersTab = new OrdersTab(store, responseLabel);

      tabPane.getTabs().add(new RegisterTab(store, responseLabel));
      tabPane.getTabs().add(ordersTab);
      tabPane.getTabs().add(new ManualRefundsTab(store, responseLabel));
      tabPane.getTabs().add(new CardsTab(store, responseLabel));
      tabPane.getTabs().add(new PreAuthTab(store, responseLabel));
      tabPane.getTabs().add(new PendingPayementsTab(store, responseLabel));
      tabPane.getTabs().add(new MiscellaneousTab(store, responseLabel));

      tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
        if (newValue == ordersTab) {
          ordersTab.refresh();
        }
      });
    }

    disconnectButton.setOnAction(event -> {
      if(cloverConnector != null) {
        cloverConnector.dispose();
        cloverConnector = null;

        tabPane.getSelectionModel().select(tabPane.getTabs().get(0));
      }
      showLaunchPanel();
    });

    pane.setCenter(tabPane);

    BorderPane bottomPane = new BorderPane();
    VBox bottomLabelsPanel = new VBox();
    bottomLabelsPanel.getChildren().addAll(responseLabel, deviceActivityLabel);
    bottomPane.setBottom(bottomLabelsPanel);

    pane.setBottom(bottomPane);


    StackPane stackPane = new StackPane();
    stackPane.getChildren().add(pane);
    stackPane.getChildren().add(glassPane);
    stackPane.getChildren().add(sigGlassPane);
    sigGlassPane.setVisible(false);
    Scene scene = new Scene(stackPane, 900, 680);
    primaryStage.setScene(scene);



    primaryStage.show();

    showLaunchPanel();

  }

  private void showLaunchPanel() {
    Preferences preferences = Preferences.userNodeForPackage(ExamplePOS.class);
    final String lastURL = preferences.get("LAST_DEVICE", "{}");
    final ComboBox<Device> devices = new ComboBox<>();
    devices.setEditable(false);

    Platform.runLater(new Runnable(){
      public void run() {
        glassPane.getChildren().clear();

        ImageView imageView = new ImageView(new Image(getClass().getResource("/clover_logo.png").toExternalForm()));
        imageView.setStyle("-fx-padding: 10px");
        VBox vBox = new VBox();
        Label label = new Label("Enter Device Address:");

        class DevicesListCell extends ListCell<Device>{

          protected void updateItem(Device item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
              setGraphic(null);
            } else {
              setText(item.name);
            }
          }
        }

        //        devices.setEditable(true);
        Callback cellFactory = new Callback<javafx.scene.control.ListView<Device>,ListCell<Device>>(){
          @Override
          public ListCell<Device> call(javafx.scene.control.ListView l){
            return new DevicesListCell();
          }
        };
        devices.setButtonCell(new DevicesListCell());

        // Just set the button cell here:
        //devices.setButtonCell((ListCell) cellFactory.call(null));
        devices.setCellFactory(cellFactory);
        Device lastDev = new Gson().fromJson(lastURL, Device.class);
        //        final TextField addressField = new TextField(lastURL);
        if(lastDev != null) {
          devices.getItems().add(lastDev);
        }

        vBox.setPadding(new Insets(20,15,20,15));
        vBox.setSpacing(8);
        vBox.getChildren().add(label);
        HBox comboPlusAddBox = new HBox();
        comboPlusAddBox.setSpacing(5);
        Button addDeviceButton = new Button("+");
        addDeviceButton.setOnAction(event -> {
          // prompt for host
          ExamplePOSCloverConnectorListener.PromptPanel promptPanel = new ExamplePOSCloverConnectorListener.PromptPanel(sigGlassPane, "Enter Endpoint:");
          Optional<String> s = promptPanel.showAndWait();
          if(s.isPresent()) {
            Device d = new Device();
            d.name = s.get();
            d.endpoint = s.get();

            if (!devices.getItems().contains(d)) {
              devices.getItems().add(d);
            }
            devices.getSelectionModel().select(d);
          }
        });
        comboPlusAddBox.getChildren().addAll(devices, addDeviceButton);
        vBox.getChildren().add(comboPlusAddBox);
        CheckBox pairCheckbox = new CheckBox("Force Pairing");
        Button connectButton = new Button("Connect");
        BorderPane buttonPane = new BorderPane();
        buttonPane.setRight(connectButton);
        buttonPane.setLeft(pairCheckbox);

        VBox vbox = new VBox();
        vbox.setStyle("-fx-padding: 20px");
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(5), new Insets(0,0,0,0))));
        vbox.setSpacing(8);
        vbox.getChildren().add(imageView);
        vbox.getChildren().add(vBox);
        vbox.getChildren().add(buttonPane);

        glassPane.getChildren().add(vbox);

        connectButton.setOnAction(new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent event) {
            try {

              Object dev = devices.getSelectionModel().getSelectedItem();
              Device device = (Device) dev;
              URI endpoint = new URI(device.endpoint);
              Preferences preferences = Preferences.userNodeForPackage(ExamplePOS.class);
              preferences.put("LAST_DEVICE", new Gson().toJson(device).toString());

              KeyStore trustStore = SecurityUtils.createTrustStore(false);
              String authToken = pairCheckbox.isSelected() ? null : Preferences.userNodeForPackage(ExamplePOS.class).get("AUTH_TOKEN", null);
              WebSocketCloverDeviceConfiguration deviceConfiguration = new WebSocketCloverDeviceConfiguration(endpoint, "com.cloverconnector.java.pos:1.1.0", trustStore, "Clover Example POS Java", "Lane4", authToken){
                @Override public void onPairingCode(final String pairingCode) {
                  displayPairingCode(pairingCode);
                }

                @Override public void onPairingSuccess(String authToken) {
                  Preferences.userNodeForPackage(ExamplePOS.class).put("AUTH_TOKEN", authToken);
                }
              };
              cloverConnector = CloverConnectorFactory.createICloverConnector(deviceConfiguration);
              baseListener = new ExamplePOSCloverConnectorListener(cloverConnector, store, connectionStatusLabel, responseLabel, deviceActivityLabel, glassPane, sigGlassPane);
              cloverConnector.addCloverConnectorListener(baseListener);
              cloverConnector.initializeConnection();
              glassPane.getChildren().clear();
              glassPane.setVisible(false);

              tabPane.getTabs().forEach(tab -> ((AbstractExampleTab) tab).setCloverConnector(cloverConnector));
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });

        // load local devices...
        {

          final Device loadingDevice = new Device();
          loadingDevice.name = "Loading...";
          loadingDevice.endpoint = "0.0.0.0";

          devices.getItems().add(loadingDevice);
          //
          //      try {
          Service service = Service.fromName("_http._tcp");
          final Query query = Query.createWithTimeout(service, Domain.LOCAL, 3000);

          new AsyncTask<Void, Void, Long>() {
            @Override protected Long doInBackground(Void... args) {
              try {
                Set<Instance> instances = query.runOnce();
                for (Instance ins : instances) {
                  final Device device = new Device();
                  device.name = ins.getName();
                  System.out.println(ins);
                  Set<InetAddress> addresses = ins.getAddresses();
                  for (InetAddress inetAddr : addresses) {
                    if (inetAddr instanceof Inet4Address) {
                      System.out.println(inetAddr.getHostName());
                      device.endpoint = "wss://" + inetAddr.getHostName() + ":" + ins.getPort() + "/remote_pay";
                      System.out.println("endpoint: " + device.endpoint);
                    }
                  }
                  Platform.runLater(new Runnable() {
                    @Override public void run() {
                      if (devices.getItems().contains(device)) {
                        devices.getItems().remove(device);
                      }
                      devices.getItems().add(device); // because the name might be different, this will remove the old by IP and add the new with name
                    }
                  });
                }
              } catch (UnknownHostException e) {

              } catch (IOException e) {

              }
              return 0L;
            }

            @Override protected void onPostExecute(Long val) {
              Platform.runLater(() -> devices.getItems().remove(loadingDevice));
            }
          }.execute();
        }
      }
    });

    glassPane.setVisible(true);


  }

  private void initStore() {
    store.addAvailableItem(new POSItem("0", "Chicken Nuggets", 539, true, true));
    store.addAvailableItem(new POSItem("1", "Hamburger", 699, true, true));
    store.addAvailableItem(new POSItem("2", "Cheeseburger", 759, true, true));
    store.addAvailableItem(new POSItem("3", "Double Hamburger", 819, true, true));
    store.addAvailableItem(new POSItem("4", "Double Cheeseburger", 899, true, true));
    store.addAvailableItem(new POSItem("5", "Bacon Cheeseburger", 999, true, true));
    store.addAvailableItem(new POSItem("6", "Small French Fries", 239, true, true));
    store.addAvailableItem(new POSItem("7", "Medium French Fries", 259, true, true));
    store.addAvailableItem(new POSItem("8", "Large French Fries", 279, true, true));
    store.addAvailableItem(new POSItem("9", "Small Fountain Drink", 169, true, true));
    store.addAvailableItem(new POSItem("10", "Medium Fountain Drink", 189, true, true));
    store.addAvailableItem(new POSItem("11", "Large Fountain Drink", 229, true, true));
    store.addAvailableItem(new POSItem("12", "Chocolate Milkshake", 449, true, true));
    store.addAvailableItem(new POSItem("13", "Vanilla Milkshake", 419, true, true));
    store.addAvailableItem(new POSItem("14", "Strawberry Milkshake", 439, true, true));
    store.addAvailableItem(new POSItem("15", "Ice Cream Cone", 189, true, true));
    store.addAvailableItem(new POSItem("16", "$25 Gift Card", 2500, false, false));
    store.addAvailableItem(new POSItem("17", "$50 Gift Card", 5000, false, false));

    store.addAvailableDiscount(new POSDiscount("10% Off", 0.1f));
    store.addAvailableDiscount(new POSDiscount("$5 Off", 500));
    store.addAvailableDiscount(new POSDiscount("None", 0));

    store.createOrder(false);

    // Per Transaction Settings defaults
//    store.setTipMode(SaleRequest.TipMode.ON_SCREEN_BEFORE_PAYMENT);
//    store.setSignatureEntryLocation(DataEntryLocation.ON_PAPER);
//    store.setDisablePrinting(false);
//    store.setDisableReceiptOptions(false);
//    store.setDisableDuplicateChecking(false);
//    store.setAllowOfflinePayment(false);
//    store.setForceOfflinePayment(false);
//    store.setApproveOfflinePaymentWithoutPrompt(true);
//    store.setAutomaticSignatureConfirmation(true);
//    store.setAutomaticPaymentConfirmation(true);
  }

  private void displayPairingCode(final String pairingCode) {
    Platform.runLater(() -> {
      glassPane.getChildren().clear();
      glassPane.setVisible(true);
      ExamplePOSCloverConnectorListener.PairPanel alertPanel = new ExamplePOSCloverConnectorListener.PairPanel(pairingCode);
      glassPane.getChildren().add(alertPanel);
      glassPane.setOnKeyPressed(event -> {
        if(event.getCode() == KeyCode.ESCAPE) {
          glassPane.getChildren().clear();
          glassPane.setVisible(false);
          glassPane.setOnKeyPressed(null);
        }
      });
    });
  }

  class GlassPane extends HBox {
    public GlassPane() {
      setStyle("-fx-background-color: rgba(50, 50, 50, 0.5);");
      setAlignment(Pos.CENTER);
      setFillHeight(false);
//      setCenterShape(true);
    }
  }

  class Device {
    String name;
    String endpoint;

    @Override public int hashCode() {
      return endpoint.hashCode();
    }

    @Override public boolean equals(Object obj) {
      try {
        return ((Device) obj).endpoint.equals(endpoint);
      } catch(NullPointerException npe) {
        return false;
      }
    }
  }
}
