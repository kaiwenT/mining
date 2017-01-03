package com.hust.mining.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static List<String[]> read(String...filenames) {
        if (CommonUtil.hasEmptyArray(filenames)) {
            return null;
        }
        int taskSize = filenames.length;
        ExecutorService pool = Executors.newFixedThreadPool(taskSize);
        List<String[]> content = new ArrayList<String[]>();
        FileUtil util = new FileUtil();
        for (int i = 0; i < taskSize; i++) {
            Callable<List<String[]>> thread = util.new ReadThread(filenames[i]);
            Future<List<String[]>> f = pool.submit(thread);
            try {
                content.addAll(f.get());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("read from {} failed", filenames[i]);
            }
        }
        pool.shutdown();
        return content;
    }

    public static boolean write(String filename, List<String[]> content) {
        if (StringUtils.isEmpty(filename)) {
            return false;
        }
        if (content == null || content.size() == 0) {
            return false;
        }
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Callable<Boolean> thread = new FileUtil().new WriteThread(filename, content);
        Future<Boolean> f = pool.submit(thread);
        try {
            return f.get();
        } catch (Exception e) {
            logger.error("写文件失败:{}", e.toString());
        }
        return false;
    }

    public static boolean delete(String filename) {
        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            file.delete();
            return true;
        }
        return false;
    }

    class ReadThread implements Callable<List<String[]>> {

        private String filename;

        protected ReadThread(String filename) {
            super();
            this.filename = filename;
        }

        @Override
        public List<String[]> call() throws Exception {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            List<String[]> content = new ArrayList<String[]>();
            synchronized (br) {
                String line;
                while (true) {
                    line = br.readLine();
                    if (StringUtils.isEmpty(line)) {
                        break;
                    }
                    String[] row = line.split("\t");
                    content.add(row);
                }
            }
            br.close();
            return content;
        }
    }

    class WriteThread implements Callable<Boolean> {
        private String filename;
        private List<String[]> content;

        protected WriteThread(String filename, List<String[]> content) {
            super();
            this.filename = filename;
            this.content = content;
        }

        @Override
        public Boolean call() throws Exception {
            // TODO Auto-generated method stub
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
                for (String[] row : content) {
                    String line = StringUtils.join(row, "\t");
                    bw.write(line + "\r\n");
                }
                bw.close();
            } catch (Exception e) {
                logger.error("write {} failed, because:{}", filename, e.toString());
                return false;
            }
            return true;
        }

    }
}
