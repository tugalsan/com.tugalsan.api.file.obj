package com.tugalsan.api.file.obj.server;

import com.tugalsan.api.string.server.*;
import java.io.*;
import java.nio.charset.*;

public class TS_FileObjUtils {

    public static byte[] toBytes(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return (byte[]) obj;
        }
        if (obj instanceof CharSequence) {
            var cs = (CharSequence) obj;
            return TS_StringUtils.toByte(cs.toString());
        }
        try ( var baos = new ByteArrayOutputStream()) {
            toStream(obj, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object toObject(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        Object obj;
        try ( var bais = new ByteArrayInputStream(bytes)) {
            obj = toObject(bais);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (obj instanceof CharSequence) {
            return TS_StringUtils.toString(bytes);
        }
        return obj;
    }

    public static Object toObject(InputStream is) {
        Object obj;
        try ( var input = new ObjectInputStream(is)) {
            obj = input.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (obj instanceof CharSequence) {
            try ( var input = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                var sb = new StringBuilder();
                String readLine;
                while ((readLine = input.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                return sb.substring(0, sb.length() - 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return obj;
    }

    public static void toStream(Object sourceObject, OutputStream os) {
        try ( var output = new ObjectOutputStream(os)) {
            output.writeObject(sourceObject);
            output.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
