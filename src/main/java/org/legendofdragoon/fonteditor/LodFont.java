package org.legendofdragoon.fonteditor;

import java.io.FileInputStream;
import java.io.IOException;

public class LodFont {
  public final Entry[] entries;

  public LodFont(final FileInputStream input) throws IOException {
    input.skip(6);

    final int count = (int)MathHelper.get(input.readNBytes(2), 0, 2);
    this.entries = new Entry[count];

    final byte[] entryData = input.readNBytes(count * 8);

    for(int entryIndex = 0; entryIndex < count; entryIndex++) {
      final long index = MathHelper.get(entryData, entryIndex * 8, 2);
      input.getChannel().position(8 + count * 8 + index * 4);

      final int metricsOffset = (int)MathHelper.get(input.readNBytes(4), 0, 4);
      input.getChannel().position(metricsOffset);

      final int metricsCount = (int)MathHelper.get(input.readNBytes(4), 0, 4);
      final byte[] metricsData = input.readNBytes(metricsCount * 0x14);
      final Metrics[] metrics = new Metrics[metricsCount];

      for(int metricsIndex = 0; metricsIndex < metricsCount; metricsIndex++) {
        final int u = metricsData[metricsIndex * 0x14] & 0xff;
        final int v = metricsData[metricsIndex * 0x14 + 1] & 0xff;
        final int x = metricsData[metricsIndex * 0x14 + 2] & 0xff;
        final int y = metricsData[metricsIndex * 0x14 + 3] & 0xff;
        final int clut = (int)MathHelper.get(metricsData, metricsIndex * 0x14 + 4, 2);
        final int tpage = (int)MathHelper.get(metricsData, metricsIndex * 0x14 + 6, 2);
        final int width = (int)MathHelper.get(metricsData, metricsIndex * 0x14 + 8, 2);
        final int height = (int)MathHelper.get(metricsData, metricsIndex * 0x14 + 10, 2);
        final int unknown1 = (int)MathHelper.get(metricsData, metricsIndex * 0x14 + 0x10, 2);
        final int unknown2 = (int)MathHelper.get(metricsData, metricsIndex * 0x14 + 0x12, 2);
        metrics[metricsIndex] = new Metrics(u, v, x, y, clut, tpage, width, height, unknown1, unknown2);
      }

      this.entries[entryIndex] = new Entry(metricsOffset, entryData[entryIndex * 8 + 2], metrics);
    }
  }

  public static class Entry {
    public final int index;
    public final int unknown;

    public final Metrics[] metrics;

    public Entry(final int index, final int unknown, final Metrics[] metrics) {
      this.index = index;
      this.unknown = unknown;

      this.metrics = metrics;
    }
  }

  public static class Metrics {
    public final int u;
    public final int v;
    public final int x;
    public final int y;
    public final int clut;
    public final int tpage;
    public final int width;
    public final int height;
    public final int unknown1;
    public final int unknown2;

    public Metrics(final int u, final int v, final int x, final int y, final int clut, final int tpage, final int width, final int height, final int unknown1, final int unknown2) {
      this.u = u;
      this.v = v;
      this.x = x;
      this.y = y;
      this.clut = clut;
      this.tpage = tpage;
      this.width = width;
      this.height = height;
      this.unknown1 = unknown1;
      this.unknown2 = unknown2;
    }
  }
}
