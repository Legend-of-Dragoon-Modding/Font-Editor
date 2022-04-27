package org.legendofdragoon.fonteditor.opengl;

import org.legendofdragoon.fonteditor.LodFont;
import org.legendofdragoon.fonteditor.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.nuklear.Nuklear.NK_EDIT_COMMITED;

public class FontEditorGui extends Gui {
  private final LodFont lodFont;
  private final Texture texture;

  private int glyphIndex;
  private int metricsIndex;

  private LodFont.Entry glyph;
  private LodFont.Metrics metrics;

  private final ByteBuffer glyphIndexText = BufferUtils.createByteBuffer(9);
  private final IntBuffer glyphIndexLength = BufferUtils.createIntBuffer(1);
  private final ByteBuffer metricsIndexText = BufferUtils.createByteBuffer(9);
  private final IntBuffer metricsIndexLength = BufferUtils.createIntBuffer(1);

  private final ByteBuffer unknown0Text = BufferUtils.createByteBuffer(9);
  private final IntBuffer unknown0Length = BufferUtils.createIntBuffer(1);

  private final ByteBuffer uText = BufferUtils.createByteBuffer(9);
  private final IntBuffer uLength = BufferUtils.createIntBuffer(1);
  private final ByteBuffer vText = BufferUtils.createByteBuffer(9);
  private final IntBuffer vLength = BufferUtils.createIntBuffer(1);
  private final ByteBuffer xText = BufferUtils.createByteBuffer(9);
  private final IntBuffer xLength = BufferUtils.createIntBuffer(1);
  private final ByteBuffer yText = BufferUtils.createByteBuffer(9);
  private final IntBuffer yLength = BufferUtils.createIntBuffer(1);
  private final ByteBuffer wText = BufferUtils.createByteBuffer(9);
  private final IntBuffer wLength = BufferUtils.createIntBuffer(1);
  private final ByteBuffer hText = BufferUtils.createByteBuffer(9);
  private final IntBuffer hLength = BufferUtils.createIntBuffer(1);
  private final ByteBuffer clutText = BufferUtils.createByteBuffer(9);
  private final IntBuffer clutLength = BufferUtils.createIntBuffer(1);
  private final ByteBuffer tpageText = BufferUtils.createByteBuffer(9);
  private final IntBuffer tpageLength = BufferUtils.createIntBuffer(1);
  private final ByteBuffer unknown1Text = BufferUtils.createByteBuffer(9);
  private final IntBuffer unknown1Length = BufferUtils.createIntBuffer(1);
  private final ByteBuffer unknown2Text = BufferUtils.createByteBuffer(9);
  private final IntBuffer unknown2Length = BufferUtils.createIntBuffer(1);

  public FontEditorGui(final LodFont font, final Texture texture) {
    this.lodFont = font;
    this.texture = texture;

    this.displayGlyph(this.glyphIndex, this.metricsIndex);
  }

  public void scrollGlyph(final long amount) {
    this.displayGlyph(Math.floorMod(this.glyphIndex + (int)amount, this.lodFont.entryCount()), 0);
  }

  public void scrollMetrics(final long amount) {
    this.displayGlyph(this.glyphIndex, Math.floorMod(this.metricsIndex + (int)amount, this.lodFont.entries[this.glyphIndex].metrics.length));
  }

  private void displayGlyph(final int glyphIndex, final int metricsIndex) {
    this.glyphIndex = glyphIndex;
    this.metricsIndex = metricsIndex;

    this.glyph = this.lodFont.entries[this.glyphIndex];
    this.metrics = this.glyph.metrics[this.metricsIndex];

    this.setBufferText(this.glyphIndexText, this.glyphIndexLength, String.valueOf(glyphIndex));
    this.setBufferText(this.metricsIndexText, this.metricsIndexLength, String.valueOf(metricsIndex));

    this.setBufferText(this.unknown0Text, this.unknown0Length, String.valueOf(this.glyph.unknown()));

    this.setBufferText(this.uText, this.uLength, String.valueOf(this.metrics.u()));
    this.setBufferText(this.vText, this.vLength, String.valueOf(this.metrics.v()));
    this.setBufferText(this.xText, this.xLength, String.valueOf(this.metrics.x()));
    this.setBufferText(this.yText, this.yLength, String.valueOf(this.metrics.y()));
    this.setBufferText(this.wText, this.wLength, String.valueOf(this.metrics.width()));
    this.setBufferText(this.hText, this.hLength, String.valueOf(this.metrics.height()));
    this.setBufferText(this.clutText, this.clutLength, String.valueOf(this.metrics.clut()));
    this.setBufferText(this.tpageText, this.tpageLength, String.valueOf(this.metrics.tpage()));
    this.setBufferText(this.unknown1Text, this.unknown1Length, String.valueOf(this.metrics.unknown1()));
    this.setBufferText(this.unknown2Text, this.unknown2Length, String.valueOf(this.metrics.unknown2()));
  }

  private void setBufferText(final ByteBuffer text, final IntBuffer length, final String value) {
    text.clear();
    text.put(value.getBytes());
    text.put((byte)0);
    text.flip();

    length.clear();
    length.put(value.length());
    length.flip();
  }

  private String getBufferText(final ByteBuffer text, final IntBuffer length) {
    final byte[] b = new byte[length.get(0)];

    text.mark();
    text.get(b, 0, b.length);
    text.reset();

    return new String(b);
  }

  @Override
  protected void draw(final GuiManager manager, final MemoryStack stack) {
    this.simpleWindow(manager, stack, "Font Editor", 0, 0, manager.window.getWidth(), manager.window.getHeight(), () -> {
      this.rowStatic(manager, this.texture.height, 1, row -> {
        row.nextColumn(this.texture.width);
        this.image(manager, stack, this.texture, 0, 0);

        this.rect(manager, stack, this.metrics.u(), this.metrics.v(), this.metrics.width(), this.metrics.height(), 255, 0, 255);
      });

      this.row(manager, 5.0f, 1);

      this.row(manager, 30.0f, 5, row -> {
        row.nextColumn(0.20f);
        this.label(manager, "Glyph:", TextAlign.RIGHT);

        row.nextColumn(0.25f);
        final int glyphRes = this.textbox(manager, this.glyphIndexText, this.glyphIndexLength, 8);

        if((glyphRes & NK_EDIT_COMMITED) != 0) {
          this.displayGlyph(MathHelper.clamp(Integer.parseInt(this.getBufferText(this.glyphIndexText, this.glyphIndexLength)), 0, this.lodFont.entries.length - 1), 0);
        }

        row.nextColumn(0.20f);
        this.label(manager, "Part:", TextAlign.RIGHT);

        row.nextColumn(0.25f);
        final int metricsRes = this.textbox(manager, this.metricsIndexText, this.metricsIndexLength, 8);

        if((metricsRes & NK_EDIT_COMMITED) != 0) {
          this.displayGlyph(this.glyphIndex, MathHelper.clamp(Integer.parseInt(this.getBufferText(this.metricsIndexText, this.metricsIndexLength)), 0, this.glyph.metrics.length - 1));
        }

        row.nextColumn(0.10f);
        this.label(manager, "/" + (this.glyph.metrics.length - 1));
      });

      this.row(manager, 5.0f, 1);

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "?:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.unknown0Text, this.unknown0Length, 8);

        row.nextColumn(0.5f);
      });

      this.row(manager, 5.0f, 1);

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "U:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.uText, this.uLength, 8);

        row.nextColumn(0.05f);
        this.label(manager, "V:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.vText, this.vLength, 8);
      });

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "X:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.xText, this.xLength, 8);

        row.nextColumn(0.05f);
        this.label(manager, "Y:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.yText, this.yLength, 8);
      });

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "W:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.wText, this.wLength, 8);

        row.nextColumn(0.05f);
        this.label(manager, "H:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.hText, this.hLength, 8);
      });

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "CLUT:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.clutText, this.clutLength, 8);

        row.nextColumn(0.05f);
        this.label(manager, "TPAGE:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.tpageText, this.tpageLength, 8);
      });

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "?1:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.unknown1Text, this.unknown1Length, 8);

        row.nextColumn(0.05f);
        this.label(manager, "?2:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        this.numberbox(manager, this.unknown2Text, this.unknown2Length, 8);
      });
    });
  }
}
