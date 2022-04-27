package org.legendofdragoon.fonteditor;

public final class MathHelper {
  private MathHelper() { }

  public static int clamp(final int value, final int min, final int max) {
    return Math.max(min, Math.min(value, max));
  }

  public static int get(final byte[] data, final int offset, final int size) {
    int value = 0;

    for(int i = 0; i < size; i++) {
      value |= (long)(data[offset + i] & 0xff) << i * 8;
    }

    return value;
  }

  public static void set(final byte[] data, final int offset, final int size, final long value) {
    for(int i = 0; i < size; i++) {
      data[offset + i] = (byte)(value >>> i * 8 & 0xff);
    }
  }
}
