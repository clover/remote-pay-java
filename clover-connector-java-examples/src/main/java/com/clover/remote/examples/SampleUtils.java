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
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class SampleUtils {
  private static final SecureRandom random = new SecureRandom();
  private static final char[] vals = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'}; // Crockford's base 32 chars

  private static final String APP_ID = "com.cloverconnector.java.simple.sample:1.4.1";
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
      KeyStore trustStore  = createTrustStore();

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

  private static KeyStore createTrustStore() {
    try {
      String storeType = KeyStore.getDefaultType();
      KeyStore trustStore = KeyStore.getInstance(storeType);
      char[] TRUST_STORE_PASSWORD = "clover".toCharArray();
      trustStore.load(null, TRUST_STORE_PASSWORD);

      // Load the old "dev" cert.  This should be valid for all target environments (dev, stg, sandbox, prod).
      Certificate cert = loadCertificateFromResource("/certs/device_ca_certificate.crt");
      trustStore.setCertificateEntry("dev", cert);

      // Now load the environment specific cert (e.g. prod).  Always retrieving this cert from prod as it is really
      // only valid in prod at this point, and we also don't have a mechanism within the SDK of specifying the target
      // environment.
      cert = loadCertificateFromResource("/certs/env_device_ca_certificate.crt");
      trustStore.setCertificateEntry("prod", cert);

      return trustStore;
    } catch(Throwable t) {
      t.printStackTrace();
    }
    return null;
  }

  private static Certificate loadCertificateFromResource(String name) {
    System.out.println("Loading cert:  " + name);

    InputStream is = null;
    try {
      is = SampleUtils.class.getResourceAsStream(name);

      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      return cf.generateCertificate(is);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception ex) {
          // NO-OP
        }
      }
    }
  }
}
