package com.lemon.takinmq.remoting.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class SerializeUtil {

    //序列化
    public static byte[] jdkSerialize(Object object) {
        if (null == object) {
            return null;
        }
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //解序列化
    @SuppressWarnings("unchecked")
    public static <T> T jdkDeserialize(byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // fastjson序列化
    public static String jsonSerialize(Object object) {
        try {
            String bytes = JSON.toJSONString(object, SerializerFeature.WriteClassName);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // fastjson反序列化
    @SuppressWarnings("unchecked")
    public static <T> T jsonDeserialize(String bytes) {
        if (bytes == null || ("").equals(bytes))
            return null;
        try {
            T ObjT = (T) JSON.parse(bytes);
            return (T) ObjT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
