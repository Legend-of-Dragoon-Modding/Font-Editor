package org.legendofdragoon.fonteditor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LodFont {
  public final Entry[] entries;

  private final byte[] data;

  public LodFont(final Path file) throws IOException {
    this.data = Files.readAllBytes(file);

    final int count = MathHelper.get(this.data, 6, 2);
    this.entries = new Entry[count];

    for(int entryIndex = 0; entryIndex < count; entryIndex++) {
      this.entries[entryIndex] = new Entry(8 + entryIndex * 8);
    }
  }

  public int entryCount() {
    return MathHelper.get(this.data, 6, 2);
  }

  public class Entry {
    private final int offset;

    public final Metrics[] metrics;

    public Entry(final int offset) {
      this.offset = offset;

      final int dataIndex = MathHelper.get(LodFont.this.data, offset, 2);
      final int metricsOffset = MathHelper.get(LodFont.this.data, 8 + LodFont.this.entryCount() * 8 + dataIndex * 4, 4);
      final int metricsCount = MathHelper.get(LodFont.this.data, metricsOffset, 4);

      this.metrics = new Metrics[metricsCount];

      for(int metricsIndex = 0; metricsIndex < metricsCount; metricsIndex++) {
        this.metrics[metricsIndex] = new Metrics(metricsOffset + 4 + metricsIndex * 0x14);
      }
    }

    public int unknown() {
      return MathHelper.get(LodFont.this.data, this.offset + 2, 1);
    }
  }

  public class Metrics {
    private final int offset;

    public Metrics(final int offset) {
      this.offset = offset;
    }

    public int u() {
      return MathHelper.get(LodFont.this.data, this.offset, 1);
    }

    public int v() {
      return MathHelper.get(LodFont.this.data, this.offset + 1, 1);
    }

    public int x() {
      return MathHelper.get(LodFont.this.data, this.offset + 2, 1);
    }

    public int y() {
      return MathHelper.get(LodFont.this.data, this.offset + 3, 1);
    }

    public int clut() {
      return MathHelper.get(LodFont.this.data, this.offset + 4, 2);
    }

    public int tpage() {
      return MathHelper.get(LodFont.this.data, this.offset + 6, 2);
    }

    public int width() {
      return MathHelper.get(LodFont.this.data, this.offset + 8, 2);
    }

    public int height() {
      return MathHelper.get(LodFont.this.data, this.offset + 0xa, 2);
    }

    public int unknown1() {
      return MathHelper.get(LodFont.this.data, this.offset + 0x10, 2);
    }

    public int unknown2() {
      return MathHelper.get(LodFont.this.data, this.offset + 0x12, 2);
    }
  }
}
