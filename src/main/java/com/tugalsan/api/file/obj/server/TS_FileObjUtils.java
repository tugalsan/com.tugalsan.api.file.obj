package com.tugalsan.api.file.obj.server;

import com.tugalsan.api.string.server.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.unsafe.client.*;
import java.io.*;

public class TS_FileObjUtils {

    public static TGS_UnionExcuse<byte[]> toBytes(Object obj) {
        return TGS_UnSafe.call(() -> {
            if (obj == null) {
                return TGS_UnionExcuse.ofExcuse(TS_FileObjUtils.class.getSimpleName(), "toBytes", "obj == null");
            }
            if (obj instanceof byte[] val) {
                return TGS_UnionExcuse.of(val);
            }
            if (obj instanceof CharSequence val) {
                return TGS_UnionExcuse.of(TS_StringUtils.toByte(val.toString()));
            }
            try (var baos = new ByteArrayOutputStream()) {
                try (var oos = new ObjectOutputStream(baos)) {// DO NOT CLOSE OOS before getting byte array!
                    oos.writeObject(obj);
                    oos.flush();
                    return TGS_UnionExcuse.of(baos.toByteArray());
                }
            } catch (IOException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        });
    }

    public static <T> TGS_UnionExcuse<T> toObject(byte[] bytes, Class<T> outputType) {//java.io.StreamCorruptedException: invalid stream header: 312D2041'
        return TGS_UnSafe.call(() -> {
            if (bytes == null) {
                return TGS_UnionExcuse.ofExcuse(TS_FileObjUtils.class.getSimpleName(), "toObject", "bytes == null");
            }
            if (outputType == CharSequence.class || outputType == String.class) {
                var str = TS_StringUtils.toString(bytes);
                return TGS_UnionExcuse.of((T) str);
            }
            try (var bais = new ByteArrayInputStream(bytes)) {
                return toObject(bais, outputType);
            }
        });
    }

    public static <T> TGS_UnionExcuse<T> toObject(InputStream is, Class<T> outputType) {
        return TGS_UnSafe.call(() -> {
            if (is == null) {
                return TGS_UnionExcuse.ofExcuse(TS_FileObjUtils.class.getSimpleName(), "toObject", "is == null");
            }
            if (outputType == CharSequence.class || outputType == String.class) {
                var str = TS_StringUtils.toString(is);
                return TGS_UnionExcuse.of((T) str);
            }
            Object obj;
            try (var input = new ObjectInputStream(is)) {
                obj = input.readObject();
                if (obj == null && !outputType.isInstance(obj)) {
                    return TGS_UnionExcuse.ofExcuse(TS_FileObjUtils.class.getSimpleName(), "toObject", "obj == null && !outputType.isInstance(obj)");
                }
                return TGS_UnionExcuse.of((T) obj);
            } catch (EOFException e) {
                return TGS_UnionExcuse.ofExcuse(e);
            }
        });
    }
}
