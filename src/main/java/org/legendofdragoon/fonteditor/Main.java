package org.legendofdragoon.fonteditor;

import java.io.IOException;
import java.nio.file.Path;

public final class Main {
  private Main() { }

  public static void main(final String[] args) throws IOException {
    if(args.length < 2 || args.length > 3) {
      System.err.println("Expected args: <metrics file> <font png> [<metrics offset (hex)>]");
      return;
    }

    final Path metrics = Path.of(args[0]);
    final Path png = Path.of(args[1]);

    final int offset;
    if(args.length == 3) {
      offset = Integer.parseInt(args[2], 16);
    } else {
      offset = 0;
    }

    new FontEditor(metrics, png, offset);
  }
}
