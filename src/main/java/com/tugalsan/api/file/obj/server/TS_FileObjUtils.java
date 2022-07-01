package com.tugalsan.api.file.obj.server;

import com.tugalsan.api.string.server.*;
import com.tugalsan.api.unsafe.client.*;
import java.io.*;
import java.nio.charset.*;

public class TS_FileObjUtils {

    public static byte[] toBytes(Object obj) {
        return TGS_UnSafe.compile(() -> {
            if (obj == null) {
                return null;
            }
            if (obj instanceof byte[] val) {
                return val;
            }
            if (obj instanceof CharSequence val) {
                return TS_StringUtils.toByte(val.toString());
            }
            try ( var baos = new ByteArrayOutputStream()) {
                toStream(obj, baos);
                return baos.toByteArray();
            }
        });
    }

    public static Object toObject(byte[] bytes) {
        return TGS_UnSafe.compile(() -> {
            if (bytes == null) {
                return null;
            }
            Object obj;
            try ( var bais = new ByteArrayInputStream(bytes)) {
                obj = toObject(bais);
            }
            if (obj instanceof CharSequence) {
                return TS_StringUtils.toString(bytes);
            }
            return obj;
        });
    }

    public static Object toObject(InputStream is) {
        return TGS_UnSafe.compile(() -> {
            Object obj;
            try ( var input = new ObjectInputStream(is)) {
                obj = input.readObject();
            }
            if (obj instanceof CharSequence) {
                try ( var input = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    var sb = new StringBuilder();
                    String readLine;
                    while ((readLine = input.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    return sb.substring(0, sb.length() - 1);
                }
            }
            return obj;
        });
    }

    public static void toStream(Object sourceObject, OutputStream os) {
        TGS_UnSafe.execute(() -> {
            try ( var output = new ObjectOutputStream(os)) {
                output.writeObject(sourceObject);
                output.flush();
            }
        });
    }
}
