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

import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.lib.example.model.POSStore;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

public abstract class AbstractExampleTab extends Tab {

  ICloverConnector cloverConnector;

  final POSStore store;

  private final String tabName;

  private final Label statusLabel;

  AbstractExampleTab(POSStore store, Label statusLabel, String tabName) {
    super(tabName);
    this.store = store;
    this.tabName = tabName;
    this.statusLabel = statusLabel;

    setContent(buildPane());
  }

  public String getTabName() {
    return tabName;
  }

  public void setCloverConnector(ICloverConnector connector) {
    this.cloverConnector = connector;
  }

  abstract Node buildPane();

  protected void clearLabel() {
    Platform.runLater(() -> statusLabel.setText(""));
  }

  protected void fillJavaFxGrid(Control control) {
    control.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    GridPane.setFillWidth(control, true);
    GridPane.setFillHeight(control, true);
  }

}
