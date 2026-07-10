package com.mdau.ushirika.module.report.util;

import java.util.ArrayList;
import java.util.List;

/** Builds a UTF-8 CSV byte array with BOM so Excel opens it correctly. */
public final class CsvBuilder {

    private static final byte[] BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

    private final StringBuilder sb = new StringBuilder();
    private boolean firstCol = true;

    private CsvBuilder() {}

    public static CsvBuilder create() { return new CsvBuilder(); }

    /** Write header row and move to next row. */
    public CsvBuilder header(String... cols) {
        for (String c : cols) col(c);
        newRow();
        return this;
    }

    /** Append a single cell value (null becomes empty string). */
    public CsvBuilder col(Object value) {
        if (!firstCol) sb.append(',');
        firstCol = false;
        String v = value == null ? "" : value.toString();
        // Escape: wrap in quotes if the value contains comma, quote, or newline.
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            sb.append('"').append(v.replace("\"", "\"\"")).append('"');
        } else {
            sb.append(v);
        }
        return this;
    }

    /** End the current row. */
    public CsvBuilder newRow() {
        sb.append("\r\n");
        firstCol = true;
        return this;
    }

    public byte[] toBytes() {
        byte[] body = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] out = new byte[BOM.length + body.length];
        System.arraycopy(BOM, 0, out, 0, BOM.length);
        System.arraycopy(body, 0, out, BOM.length, body.length);
        return out;
    }
}
