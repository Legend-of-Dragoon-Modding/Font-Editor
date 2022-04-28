package org.legendofdragoon.fonteditor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LodFont {
  private final Path file;
  private final int offset;

  public final Entry[] entries;
  private final byte[] data;

  public LodFont(final Path file, final int offset) throws IOException {
    this.file = file;
    this.offset = offset;

    this.data = Files.readAllBytes(file);

    final int count = this.entryCount();
    this.entries = new Entry[count];

    for(int entryIndex = 0; entryIndex < count; entryIndex++) {
      this.entries[entryIndex] = new Entry(offset + 8 + entryIndex * 8);
    }
  }

  public void save() throws IOException {
    Files.write(this.file, this.data, StandardOpenOption.TRUNCATE_EXISTING);
  }

  public int entryCount() {
    return MathHelper.get(this.data, this.offset + 6, 2);
  }

  public class Entry {
    private final int offset;

    public final Metrics[] metrics;

    public Entry(final int offset) {
      this.offset = offset;

      final int dataIndex = MathHelper.get(LodFont.this.data, offset, 2);
      final int metricsOffset = LodFont.this.offset + MathHelper.get(LodFont.this.data, LodFont.this.offset + 8 + LodFont.this.entryCount() * 8 + dataIndex * 4, 4);
      final int metricsCount = MathHelper.get(LodFont.this.data, metricsOffset, 4);

      this.metrics = new Metrics[metricsCount];

      for(int metricsIndex = 0; metricsIndex < metricsCount; metricsIndex++) {
        this.metrics[metricsIndex] = new Metrics(metricsOffset + 4 + metricsIndex * 0x14);
      }
    }

    public int unknown() {
      return MathHelper.get(LodFont.this.data, this.offset + 2, 1);
    }

    public void unknown(final int value) {
      MathHelper.set(LodFont.this.data, this.offset + 2, 1, value);
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

    public void u(final int value) {
      MathHelper.set(LodFont.this.data, this.offset, 1, value);
    }

    public int v() {
      return MathHelper.get(LodFont.this.data, this.offset + 1, 1);
    }

    public void v(final int value) {
      MathHelper.set(LodFont.this.data, this.offset + 1, 1, value);
    }

    public int x() {
      return MathHelper.get(LodFont.this.data, this.offset + 2, 1);
    }

    public void x(final int value) {
      MathHelper.set(LodFont.this.data, this.offset + 2, 1, value);
    }

    public int y() {
      return MathHelper.get(LodFont.this.data, this.offset + 3, 1);
    }

    public void y(final int value) {
      MathHelper.set(LodFont.this.data, this.offset + 3, 1, value);
    }

    public int clut() {
      return MathHelper.get(LodFont.this.data, this.offset + 4, 2);
    }

    public void clut(final int value) {
      MathHelper.set(LodFont.this.data, this.offset + 4, 2, value);
    }

    public int tpage() {
      return MathHelper.get(LodFont.this.data, this.offset + 6, 2);
    }

    public void tpage(final int value) {
      MathHelper.set(LodFont.this.data, this.offset + 6, 2, value);
    }

    public int width() {
      return MathHelper.get(LodFont.this.data, this.offset + 8, 2);
    }

    public void width(final int value) {
      MathHelper.set(LodFont.this.data, this.offset + 8, 2, value);
    }

    public int height() {
      return MathHelper.get(LodFont.this.data, this.offset + 0xa, 2);
    }

    public void height(final int value) {
      MathHelper.set(LodFont.this.data, this.offset + 0xa, 2, value);
    }

    public int unknown1() {
      return MathHelper.get(LodFont.this.data, this.offset + 0x10, 2);
    }

    public void unknown1(final int value) {
      MathHelper.set(LodFont.this.data, this.offset + 0x10, 2, value);
    }

    public int unknown2() {
      return MathHelper.get(LodFont.this.data, this.offset + 0x12, 2);
    }

    public void unknown2(final int value) {
      MathHelper.set(LodFont.this.data, this.offset + 0x12, 2, value);
    }
  }
}
