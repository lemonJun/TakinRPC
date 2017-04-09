//package com.raft;
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
//
//import java.io.File;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * Created by Administrator on 2016/11/9.
// */
//public class SearchMain {
//    public static void main(String[] args) throws Exception {
//        SAXReader reader = new SAXReader();
//        Document document = reader.read(new File("D:\\micro\\micro-parent\\product-service\\src\\main\\resources\\mapper\\AdInfoMapper.xml"));
//        Element root = document.getRootElement();
//
//        List list = document.selectNodes("mapper/select");
//        System.out.println(list.size());
//
//
//    }
//}
