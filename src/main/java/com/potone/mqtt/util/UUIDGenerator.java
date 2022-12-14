package com.potone.mqtt.util;

import java.net.InetAddress;

/**
 * @Author fan'zi'ang
 * @create 2022/10/29
 */
public class UUIDGenerator {

    private static final int IP;

    private UUIDGenerator() {

    }

    static {
        int ipadd;
        try {
            ipadd = toInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
    }

    /**
     * generate uuid
     *
     * @return
     */
    public static String generate() {
        return new StringBuilder(32).append(format(getIP())).append(
                format(getJVM())).append(format(getHiTime())).append(
                format(getLoTime())).append(format(getCount())).toString();
    }

    private static short counter = (short) 0;

    private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

    private static final String format(int intval) {
        String formatted = Integer.toHexString(intval);
        StringBuilder buf = new StringBuilder("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    private static final String format(short shortval) {
        String formatted = Integer.toHexString(shortval);
        StringBuilder buf = new StringBuilder("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }

    private static final int getJVM() {
        return JVM;
    }

    private static final short getCount() {
        synchronized (UUIDGenerator.class) {
            if (counter < 0) {
                counter = 0;
            }
            return counter++;
        }
    }

    /**
     * Unique in a local network
     */
    private static final int getIP() {
        return IP;
    }

    /**
     * Unique down to millisecond
     */
    private static final short getHiTime() {
        return (short) (System.currentTimeMillis() >>> 32);
    }

    private static final int getLoTime() {
        return (int) System.currentTimeMillis();
    }

    private static final int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
        }
        return result;
    }

}
