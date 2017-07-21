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

import com.clover.common2.Signature2;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SignaturePane extends Canvas {
  private SimpleObjectProperty<Signature2> signatureProperty = new SimpleObjectProperty<Signature2>();

  public SignaturePane() {
    super(500, 500);
    final GraphicsContext g = getGraphicsContext2D();
    g.setFill(Color.GREEN);
    g.setStroke(Color.BLUE);
    g.setLineWidth(2);

//    g.fillRect(20,20,200,200);

//    g.strokeLine(0,0,500,500);


    signatureProperty.addListener(new ChangeListener<Signature2>() {
      @Override public void changed(ObservableValue<? extends Signature2> observable, Signature2 oldValue, Signature2 newValue) {

        for(Signature2.Stroke stroke : newValue.strokes) {

          for(int i=1; i<stroke.points.size(); i++) {
            Signature2.Point pt = stroke.points.get(i-1);
            Signature2.Point pt2 = stroke.points.get(i);
            g.strokeLine(2*pt.x/3, 2*pt.y/3, 2*pt2.x/3, 2*pt2.y/3);
          }

        }
      }
    });
  }


  public void setSignature(Signature2 sig) {
    signatureProperty.setValue(sig);
  }
}
