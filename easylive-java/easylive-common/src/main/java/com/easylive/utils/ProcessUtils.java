package com.easylive.utils;

import com.easylive.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class ProcessUtils {
    private static final Logger logger= LoggerFactory.getLogger(ProcessUtils.class);
    private static final String osName = System.getProperty("os.name").toLowerCase();
    public static String executeCommand(String cmd,Boolean showLog)throws BusinessException{
        if (StringTools.isEmpty(cmd)){
            return null;
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try{
            if(osName.contains("win")){
                process = Runtime.getRuntime().exec(cmd);
            }else{
                process = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",cmd});
            }
            PrintStream errorStream = new PrintStream(process.getErrorStream());
            PrintStream inputStream = new PrintStream(process.getInputStream());
            errorStream.start();
            inputStream.start();
            process.waitFor();
            String result=errorStream.stringBuffer.append(inputStream.stringBuffer+"\n").toString();
            if(showLog){
                logger.info("执行命令{}结果{}",cmd,result);
            }
            return result;
        }catch (Exception e){
            logger.error("执行命令失败cmd{}失败:{}",cmd,e.getMessage());
            throw new BusinessException("视频转换失败");
        }finally{
            if (null != process){
                ProcessKiller ffmpegKiller = new ProcessKiller(process);
                runtime.addShutdownHook(ffmpegKiller);
            }
        }
    }
    private static class ProcessKiller extends Thread{
        private Process process;
        public ProcessKiller(Process process){ this.process=process; }
        @Override
        public void run() {this.process.destroy();}
    }

    static class PrintStream extends Thread{
        InputStream inputStream=null;
        BufferedReader bufferedReader=null;
        StringBuffer stringBuffer=new StringBuffer();
        public PrintStream(InputStream inputStream){this.inputStream=inputStream;}

        @Override
        public void run() {
            try{
                if(null==inputStream){
                    return;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line=null;
                while((line=bufferedReader.readLine())!=null){
                    stringBuffer.append(line);
                }
            }catch (Exception e){
                logger.error("读取输入流出错了！错误信息："+e.getMessage());
            }finally{
                try{
                    if(null!=bufferedReader){
                        bufferedReader.close();
                    }
                    if(null!=inputStream){
                        inputStream.close();
                    }
                }catch (IOException e){
                    logger.error("调用printstream读取输出流后，关闭流时出错");
                }
            }
        }
    }
}
