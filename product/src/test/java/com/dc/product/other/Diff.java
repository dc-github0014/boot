package com.dc.product.other;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Diff {

    public static void main(String[] files) throws IOException {
        long millis = System.currentTimeMillis();

        String proPath = "C://Users/Dc/Desktop/diff/" + "17T10151920H00012019070351000.txt";
        String newPath = "C://Users/Dc/Desktop/diff/" + "17N8.23.txt";
        String outPath = "C://Users/Dc/Desktop/diff/diff17-2.txt";
        List<String> listPro = new ArrayList<>();
        List<String> listNew = new ArrayList<>();
        Map<String,List<String>> diffMap = new HashMap<>();

        readFile(listPro,listNew,proPath,newPath);
        listPro.removeAll(listNew);
        toDiffMap(listPro,listNew,diffMap);
        writeFile(diffMap,outPath);

        log.info("耗时={}",System.currentTimeMillis() - millis);
    }

    private static void toDiffMap(List<String> listPro, List<String> listNew,Map<String,List<String>> diffMap) {
        List<String> diffList;
        String accountNo;
        List<String> noPro = new ArrayList<>();
        List<String> noNew = new ArrayList<>();
        for (String linePro:listPro) {
            diffList = new ArrayList<>(2);
            accountNo = linePro.substring(22,48);
            noPro.add(accountNo);
            boolean flag = false;
            for (String lineNew:listNew) {
                String accountNoNew = lineNew.substring(22,48);
                if (accountNo.equals(accountNoNew)) {
                    noNew.add(accountNoNew);
                    // 五级分类 生产有误 以新的为准
                    if (!lineNew.substring(229,230).equals(linePro.substring(229,230)))
                        break;
//                    if (lineNew.contains("G")||linePro.contains("G")) {
//                        diffList.add(linePro);
//                        diffList.add(lineNew);
//                        break;
//                    }
                    // 不比较客户信息
                    if (lineNew.contains("B")||linePro.contains("B")){
                        if (lineNew.substring(10,265).equals(linePro.substring(10,265)))
                            break;
                    }
                    // 最后一位为数字
//                    if (!lineNew.substring(254, 255).matches("^\\d+$"))
//                        break;
                    // 特殊交易 安硕系统bug
//                    if (lineNew.contains("G"))
//                        break;
                    diffList.add(linePro);
                    diffList.add(lineNew);
                    flag = true;
                    break;
                }
            }
            diffMap.put(accountNo,diffList);
        }
        noPro.removeAll(noNew);
        log.error(noPro.toString());
    }

    private static void writeFile(Map<String,List<String>> diffMap,String outPath) throws IOException {

        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outPath));
        String flag;
        for (String accountNo:diffMap.keySet()) {
            List<String> accList = diffMap.get(accountNo);
            flag = "P ";
            for (String line:accList) {
                outputStream.write((flag+line+"\n").getBytes());
                flag = "C ";
            }
            outputStream.write("\n".getBytes());
        }
        outputStream.close();
    }

    private static void readFile(List<String> listPro,List<String> listNew,String proPath,String newPath) throws IOException {

        BufferedReader readerPro = new BufferedReader(new FileReader(proPath));
        BufferedReader readerNew = new BufferedReader(new FileReader(newPath));
        String line;
        while ((line = readerPro.readLine()) != null)
            listPro.add(line);
        while ((line = readerNew.readLine()) != null)
            listNew.add(line);
        readerPro.close();
        readerNew.close();
    }


    private static void writeFile(List<String> listDiff,String outPath) throws IOException {

        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outPath));
        for (String line:listDiff) {
            outputStream.write((line.substring(22,48)+"\n").getBytes());
        }
        outputStream.close();
    }

    private static List<String> diff(List<String> listPro,List<String> listNew) {
        List<String> listDiff = new ArrayList<>();
        for (String linePro:listPro) {
            boolean flag = true;
            for (String lineNew:listNew) {
                if (linePro.equals(lineNew)){
                    flag = false;
                    break;
                }
            }
            if (flag)
                listDiff.add(linePro);
        }
        return listDiff;
    }


//        CountDownLatch countDownLatch = new CountDownLatch(6);
//        new ReadFile(listPro, proPath, countDownLatch).start();
//        new ReadFile(listNew, newPath, countDownLatch).start();
//        countDownLatch.await();
}