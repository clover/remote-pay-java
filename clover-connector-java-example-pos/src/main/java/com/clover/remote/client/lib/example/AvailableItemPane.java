/*
 * Copyright (C) 2016 Clover Network, Inc.
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

import com.clover.remote.client.lib.example.model.POSItem;
import com.clover.remote.client.lib.example.utils.CurrencyUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Locale;

public class AvailableItemPane extends BorderPane {
  POSItem item;
  Label itemLabel = new Label();
  Label priceLabel = new Label();

  public AvailableItemPane() {
    setCenter(itemLabel);
    setBottom(priceLabel);

    priceLabel.setBackground(new Background(new BackgroundFill(Color.NAVY, null, null)));
    priceLabel.setAlignment(Pos.CENTER_RIGHT);
    priceLabel.setTextFill(Color.LIGHTGREY);
    setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
    this.setStyle("-fx-border-width: 1px; -fx-border-color: darkblue; -fx-border-style: solid");
    itemLabel.setStyle("-fx-padding: 15px");
  }

  public POSItem getItem() {
    return item;
  }

  public void setItem(POSItem item) {
    this.item = item;
    itemLabel.setText(item.getName());
    priceLabel.setText(CurrencyUtils.format(item.getPrice(), Locale.getDefault()));
  }
}
