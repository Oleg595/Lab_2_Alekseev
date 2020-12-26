package Writer;


import ru.spbstu.pipeline.*;

import java.io.*;
import java.util.logging.Logger;

public class Writer implements IWriter {

    private FileOutputStream write;
    private String config;
    private Logger logger;
    private IExecutable producer;
    private IExecutable consumer;
    private WriterGrammar grammar;
    private WriterGrammar.MODE mode;


    public Writer(Logger log){
        logger = log;
        grammar = new WriterGrammar();
    }
    public RC setOutputStream(FileOutputStream fos){
        write = fos;
        return RC.CODE_SUCCESS;
    }
    public RC setConsumer(IExecutable c){
        consumer = c;
        return RC.CODE_SUCCESS;
    }
    public RC setProducer(IExecutable p){
        producer = p;
        return RC.CODE_SUCCESS;
    }
    public RC setConfig(String configFileName){
        return RC.CODE_SUCCESS;
    }
    public RC execute(byte [] data){
        try {
            write.write(data);
        }
        catch(IOException e) {
            logger.warning(RC.CODE_FAILED_TO_READ.toString());
            return RC.CODE_FAILED_TO_WRITE;
        }
        return RC.CODE_SUCCESS;
    }
}
