package org.legendofdragoon.fonteditor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legendofdragoon.fonteditor.opengl.Camera;
import org.legendofdragoon.fonteditor.opengl.Context;
import org.legendofdragoon.fonteditor.opengl.Font;
import org.legendofdragoon.fonteditor.opengl.GuiManager;
import org.legendofdragoon.fonteditor.opengl.Texture;
import org.legendofdragoon.fonteditor.opengl.Window;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11C.GL_RGBA;

public class FontEditor {
  private static final Logger LOGGER = LogManager.getFormatterLogger(FontEditor.class);

  private Camera camera;
  private Window window;
  private Context ctx;
  private final GuiManager guiManager;

  private float scrollY;
  private boolean shift;

  public FontEditor(final Path metrics, final Path png, final int offset) throws IOException {
    this.camera = new Camera(0.0f, 0.0f);
    this.window = new Window("Font Editor", 1280, 720);
    this.ctx = new Context(this.window, this.camera);

    this.guiManager = new GuiManager(this.window);
    this.window.setEventPoller(this.guiManager::captureInput);

    final Font font = new Font("gfx/fonts/Consolas.ttf");
    this.guiManager.setFont(font);

    final LodFont lodFont = new LodFont(metrics, offset);
    final Texture fontTexture = Texture.create(texture -> {
      texture.png(png);
      texture.dataFormat(GL_RGBA);
      texture.internalFormat(GL_RGBA);
    });
    final FontEditorGui gui = new FontEditorGui(lodFont, fontTexture);
    this.guiManager.pushGui(gui);

    this.window.events.onKeyPress((window1, key, scancode, mods) -> {
      if(key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
        this.shift = true;
      }
    });

    this.window.events.onKeyRelease((window1, key, scancode, mods) -> {
      if(key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
        this.shift = false;
      }
    });

    this.window.events.onMouseScroll((window, deltaX, deltaY) -> {
      if(Math.signum(this.scrollY) != -Math.signum(deltaY)) {
        this.scrollY = 0;
      }

      this.scrollY -= deltaY;

      if(Math.abs(this.scrollY) >= 1.0f) {
        final int scrollAmount = (int)Math.signum(this.scrollY);
        this.scrollY -= scrollAmount;

        if(!this.shift) {
          gui.scrollGlyph(scrollAmount);
        } else {
          gui.scrollMetrics(scrollAmount);
        }
      }
    });


    this.ctx.onDraw(() -> this.guiManager.draw(this.ctx.getWidth(), this.ctx.getHeight(), this.ctx.getWidth() / this.window.getScale(), this.ctx.getHeight() / this.window.getScale()));

    this.window.show();

    try {
      this.window.run();
    } catch(final Throwable t) {
      LOGGER.error("Shutting down due to exception:", t);
      this.window.close();
    } finally {
      this.guiManager.free();
      font.free();
      Window.free();
    }
  }
}
