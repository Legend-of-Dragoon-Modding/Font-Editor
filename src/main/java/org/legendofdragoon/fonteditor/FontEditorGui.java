package org.legendofdragoon.fonteditor;

import org.legendofdragoon.fonteditor.opengl.Gui;
import org.legendofdragoon.fonteditor.opengl.GuiManager;
import org.legendofdragoon.fonteditor.opengl.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
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

  private final ByteBuffer ticksPerFrameText = BufferUtils.createByteBuffer(9);
  private final IntBuffer ticksPerFrameLength = BufferUtils.createIntBuffer(1);

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
  private final ByteBuffer widthScaleText = BufferUtils.createByteBuffer(9);
  private final IntBuffer widthScaleLength = BufferUtils.createIntBuffer(1);
  private final ByteBuffer heightScaleText = BufferUtils.createByteBuffer(9);
  private final IntBuffer heightScaleLength = BufferUtils.createIntBuffer(1);

  public FontEditorGui(final LodFont font, final Texture texture) {
    this.lodFont = font;
    this.texture = texture;

    this.displayGlyph(this.glyphIndex, this.metricsIndex);
  }

  public void scrollGlyph(final int amount) {
    this.displayGlyph(Math.floorMod(this.glyphIndex + amount, this.lodFont.entryCount()), 0);
  }

  public void scrollMetrics(final int amount) {
    this.displayGlyph(this.glyphIndex, Math.floorMod(this.metricsIndex + amount, this.lodFont.entries[this.glyphIndex].metrics.length));
  }

  private void displayGlyph(final int glyphIndex, final int metricsIndex) {
    this.glyphIndex = glyphIndex;
    this.metricsIndex = metricsIndex;

    this.glyph = this.lodFont.entries[this.glyphIndex];
    this.metrics = this.glyph.metrics[this.metricsIndex];

    this.setBufferText(this.glyphIndexText, this.glyphIndexLength, String.valueOf(glyphIndex));
    this.setBufferText(this.metricsIndexText, this.metricsIndexLength, String.valueOf(metricsIndex));

    this.setBufferText(this.ticksPerFrameText, this.ticksPerFrameLength, String.valueOf(this.glyph.ticksPerFrame()));

    this.setBufferText(this.uText, this.uLength, String.valueOf(this.metrics.u()));
    this.setBufferText(this.vText, this.vLength, String.valueOf(this.metrics.v()));
    this.setBufferText(this.xText, this.xLength, String.valueOf(this.metrics.x()));
    this.setBufferText(this.yText, this.yLength, String.valueOf(this.metrics.y()));
    this.setBufferText(this.wText, this.wLength, String.valueOf(this.metrics.width()));
    this.setBufferText(this.hText, this.hLength, String.valueOf(this.metrics.height()));
    this.setBufferText(this.clutText, this.clutLength, "0x" + Integer.toHexString(this.metrics.clut()));
    this.setBufferText(this.tpageText, this.tpageLength, "0x" + Integer.toHexString(this.metrics.tpage()));
    this.setBufferText(this.widthScaleText, this.widthScaleLength, "0x" + Integer.toHexString(this.metrics.widthScale()));
    this.setBufferText(this.heightScaleText, this.heightScaleLength, "0x" + Integer.toHexString(this.metrics.heightScale()));
  }

  private void save() {
    try {
      this.lodFont.save();
    } catch(final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void setBufferText(final ByteBuffer text, final IntBuffer length, final String value) {
    text.clear();
    text.put(value.getBytes());

    for(int i = 0; i < text.limit() - value.length(); i++) {
      text.put((byte)0);
    }

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

  private int getInt(final String str) {
    try {
      return Integer.parseInt(str);
    } catch(final NumberFormatException e) {
      return 0;
    }
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
        final int glyphRes = this.numberbox(manager, this.glyphIndexText, this.glyphIndexLength, 8);

        if((glyphRes & NK_EDIT_COMMITED) != 0) {
          this.displayGlyph(MathHelper.clamp(this.getInt(this.getBufferText(this.glyphIndexText, this.glyphIndexLength)), 0, this.lodFont.entries.length - 1), 0);
        }

        row.nextColumn(0.20f);
        this.label(manager, "Part:", TextAlign.RIGHT);

        row.nextColumn(0.25f);
        final int metricsRes = this.numberbox(manager, this.metricsIndexText, this.metricsIndexLength, 8);

        if((metricsRes & NK_EDIT_COMMITED) != 0) {
          this.displayGlyph(this.glyphIndex, MathHelper.clamp(this.getInt(this.getBufferText(this.metricsIndexText, this.metricsIndexLength)), 0, this.glyph.metrics.length - 1));
        }

        row.nextColumn(0.10f);
        this.label(manager, "/" + (this.glyph.metrics.length - 1));
      });

      this.row(manager, 5.0f, 1);

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "Ticks:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int res = this.numberbox(manager, this.ticksPerFrameText, this.ticksPerFrameLength, 8);

        if((res & NK_EDIT_COMMITED) != 0) {
          this.glyph.ticksPerFrame(this.getInt(this.getBufferText(this.ticksPerFrameText, this.ticksPerFrameLength)));
        }

        row.nextColumn(0.5f);
      });

      this.row(manager, 5.0f, 1);

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "U:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int uRes = this.numberbox(manager, this.uText, this.uLength, 8);

        if((uRes & NK_EDIT_COMMITED) != 0) {
          this.metrics.u(this.getInt(this.getBufferText(this.uText, this.uLength)));
        }

        row.nextColumn(0.05f);
        this.label(manager, "V:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int vRes = this.numberbox(manager, this.vText, this.vLength, 8);

        if((vRes & NK_EDIT_COMMITED) != 0) {
          this.metrics.v(this.getInt(this.getBufferText(this.vText, this.vLength)));
        }
      });

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "X:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int xRes = this.numberbox(manager, this.xText, this.xLength, 8);

        if((xRes & NK_EDIT_COMMITED) != 0) {
          this.metrics.x(this.getInt(this.getBufferText(this.xText, this.xLength)));
        }

        row.nextColumn(0.05f);
        this.label(manager, "Y:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int yRes = this.numberbox(manager, this.yText, this.yLength, 8);

        if((yRes & NK_EDIT_COMMITED) != 0) {
          this.metrics.y(this.getInt(this.getBufferText(this.yText, this.yLength)));
        }
      });

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "W:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int wRes = this.numberbox(manager, this.wText, this.wLength, 8);

        if((wRes & NK_EDIT_COMMITED) != 0) {
          this.metrics.width(this.getInt(this.getBufferText(this.wText, this.wLength)));
        }

        row.nextColumn(0.05f);
        this.label(manager, "H:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int hRes = this.numberbox(manager, this.hText, this.hLength, 8);

        if((hRes & NK_EDIT_COMMITED) != 0) {
          this.metrics.height(this.getInt(this.getBufferText(this.hText, this.hLength)));
        }
      });

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "CLUT:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int clutRes = this.numberbox(manager, this.clutText, this.clutLength, 8);

        if((clutRes & NK_EDIT_COMMITED) != 0) {
          this.metrics.clut(this.getInt(this.getBufferText(this.clutText, this.clutLength)));
        }

        row.nextColumn(0.05f);
        this.label(manager, "TPAGE:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int tpageRes = this.numberbox(manager, this.tpageText, this.tpageLength, 8);

        if((tpageRes & NK_EDIT_COMMITED) != 0) {
          this.metrics.tpage(this.getInt(this.getBufferText(this.tpageText, this.tpageLength)));
        }
      });

      this.row(manager, 30.0f, 4, row -> {
        row.nextColumn(0.05f);
        this.label(manager, "WScale:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int u1Res = this.numberbox(manager, this.widthScaleText, this.widthScaleLength, 8);

        if((u1Res & NK_EDIT_COMMITED) != 0) {
          this.metrics.widthScale(this.getInt(this.getBufferText(this.widthScaleText, this.widthScaleLength)));
        }

        row.nextColumn(0.05f);
        this.label(manager, "HScale:", TextAlign.RIGHT);

        row.nextColumn(0.45f);
        final int u2Res = this.numberbox(manager, this.heightScaleText, this.heightScaleLength, 8);

        if((u2Res & NK_EDIT_COMMITED) != 0) {
          this.metrics.heightScale(this.getInt(this.getBufferText(this.heightScaleText, this.heightScaleLength)));
        }
      });

      this.row(manager, 5.0f, 1);

      this.row(manager, 30.0f, 1, row -> {
        row.nextColumn(1.0f);
        this.button(manager, "Save", this::save);
      });
    });
  }
}
