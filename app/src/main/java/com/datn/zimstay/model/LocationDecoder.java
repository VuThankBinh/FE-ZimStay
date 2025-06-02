package com.datn.zimstay.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LocationDecoder {
    public static double[] decodeLocation(String hex) {
        byte[] bytes = hexStringToByteArray(hex);

        // Bỏ qua 4 byte đầu (endianness + type), đọc theo LITTLE_ENDIAN
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 5, 16).order(ByteOrder.LITTLE_ENDIAN);

        double lon = buffer.getDouble(); // longitude
        double lat = buffer.getDouble(); // latitude

        return new double[]{lat, lon};
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)
                    ((Character.digit(s.charAt(i), 16) << 4)
                            + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    // Test
    public static void main(String[] args) {
        String locationHex = "E6100000010CF697DD9387A534402506819543835A40";

        double[] latLng = decodeLocation(locationHex);
        System.out.println("Latitude: " + latLng[0]);
        System.out.println("Longitude: " + latLng[1]);
    }
}
