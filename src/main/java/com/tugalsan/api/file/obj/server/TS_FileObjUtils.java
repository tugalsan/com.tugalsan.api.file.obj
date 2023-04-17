package com.tugalsan.api.file.obj.server;

import com.tugalsan.api.string.server.*;
import com.tugalsan.api.unsafe.client.*;
import java.io.*;

public class TS_FileObjUtils {

    public static byte[] toBytes(Object obj) {
        return TGS_UnSafe.call(() -> {
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

    public static Object toString(byte[] bytes) {
        return TS_StringUtils.toString(bytes);
    }

    public static Object toObject(byte[] bytes) {
        return TGS_UnSafe.call(() -> {
            if (bytes == null) {
                return null;
            }
            Object obj;
            try ( var bais = new ByteArrayInputStream(bytes)) {
                obj = toObject(bais);
            }
            if (obj == null) {
                return null;
            }
            if (obj instanceof CharSequence) {
                return TS_StringUtils.toString(bytes);
            }
            return obj;
        });
    }

    @Deprecated// for not Strings
    public static Object toObject(InputStream is) {
        return TGS_UnSafe.call(() -> {
            Object obj;
            try ( var input = new ObjectInputStream(is)) {
                obj = input.readObject();
            } catch (EOFException e) {
                return null;
            }
            return obj;
        });
    }

    public static void toStream(Object sourceObject, OutputStream os) {
        TGS_UnSafe.run(() -> {
            try ( var output = new ObjectOutputStream(os)) {
                output.writeObject(sourceObject);
                output.flush();
            }
        });
    }
}
