package com.tugalsan.api.file.obj.server;

import com.tugalsan.api.string.server.*;
import com.tugalsan.api.unsafe.client.*;
import java.io.*;
import java.util.Optional;

public class TS_FileObjUtils {

    public static Optional<byte[]> toBytes(Object obj) {
        return TGS_UnSafe.call(() -> {
            if (obj == null) {
                return Optional.empty();
            }
            if (obj instanceof byte[] val) {
                return Optional.of(val);
            }
            if (obj instanceof CharSequence val) {
                return Optional.of(TS_StringUtils.toByte(val.toString()));
            }
            try (var baos = new ByteArrayOutputStream()) {
                try (var oos = new ObjectOutputStream(baos)) {// DO NOT CLOSE OOS before getting byte array!
                    oos.writeObject(obj);
                    oos.flush();
                    return Optional.of(baos.toByteArray());
                }
            } catch (IOException ex) {
                return Optional.empty();
            }
        });
    }

    public static <T> Optional<T> toObject(byte[] bytes, Class<T> outputType) {//java.io.StreamCorruptedException: invalid stream header: 312D2041'
        return TGS_UnSafe.call(() -> {
            if (bytes == null) {
                return Optional.empty();
            }
            if (outputType == CharSequence.class || outputType == String.class) {
                var str = TS_StringUtils.toString(bytes);
                return Optional.of((T) str);
            }
            try (var bais = new ByteArrayInputStream(bytes)) {
                return toObject(bais, outputType);
            }
        });
    }

    public static <T> Optional<T> toObject(InputStream is, Class<T> outputType) {
        return TGS_UnSafe.call(() -> {
            if (is == null) {
                return Optional.empty();
            }
            if (outputType == CharSequence.class || outputType == String.class) {
                var str = TS_StringUtils.toString(is);
                return Optional.of((T) str);
            }
            Object obj;
            try (var input = new ObjectInputStream(is)) {
                obj = input.readObject();
                if (obj == null && !outputType.isInstance(obj)) {
                    return Optional.empty();
                }
                return Optional.of((T) obj);
            } catch (EOFException e) {
                return Optional.empty();
            }
        });
    }
}
