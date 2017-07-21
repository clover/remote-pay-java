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

package com.clover.remote.examples;

import com.clover.remote.client.CloverDeviceConfiguration;
import com.clover.remote.client.WebSocketCloverDeviceConfiguration;

import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;

public class SampleUtils {
  private static final SecureRandom random = new SecureRandom();
  private static final char[] vals = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'}; // Crockford's base 32 chars

  private static final String APP_ID = "com.cloverconnector.java.simple.sample:1.3.1";
  private static final String POS_NAME = "Clover Simple Sample Java";
  private static final String DEVICE_NAME = "Clover Device";


  private SampleUtils() {
    // Private constructor for utility class
  }

  public static String getNextId() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 13; i++) {
      int idx = random.nextInt(vals.length);
      sb.append(vals[idx]);
    }
    return sb.toString();
  }

  public static CloverDeviceConfiguration getNetworkConfiguration(String ip) {
    return getNetworkConfiguration(ip, null);
  }

  public static CloverDeviceConfiguration getNetworkConfiguration(String ip, Integer port) {
    Integer dvcPort = port != null ? port : Integer.valueOf(12345);
    try {
      URI endpoint = new URI("wss://" + ip + ":" + dvcPort + "/remote_pay");
      KeyStore trustStore  = KeyStore.getInstance("PKCS12");
      InputStream trustStoreStream = CloverDeviceConfiguration.class.getResourceAsStream("/certs/clover_cacerts.p12");
      String TRUST_STORE_PASSWORD = "clover";
      trustStore.load(trustStoreStream, TRUST_STORE_PASSWORD.toCharArray());

      // For WebSocket configuration, we must handle the device pairing via callback
      return new WebSocketCloverDeviceConfiguration(endpoint, APP_ID, trustStore, POS_NAME, DEVICE_NAME, null) {
        @Override
        public void onPairingCode(final String pairingCode) {
          System.out.println("Enter Pairing Code on Device: " + pairingCode);
        }

        @Override
        public void onPairingSuccess(String authToken) {
          System.out.println("Pairing successful");
        }
      };
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    System.err.println("Error creating CloverDeviceConfiguration");
    return null;
  }
}
