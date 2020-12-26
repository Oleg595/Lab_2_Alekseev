package Coding;



import ru.spbstu.pipeline.*;

import java.io.*;
import java.util.logging.Logger;

public class Coding implements IExecutor {
    private Logger logger;
    private CodingGrammar grammar;
    private CodingGrammar.MODE mode;
    private IExecutable consumer;
    private IExecutable producer;
    private Double code;
    private int num;
    private Symbols symb;
    private BufferedWriter encoded;
    private BufferedReader decoded;

    public Coding(Logger log){
        grammar = new CodingGrammar();
        logger = log;
    };

    public RC setConsumer(IExecutable c){
        consumer = c;
        return RC.CODE_SUCCESS;
    }
    public RC setProducer(IExecutable p){
        if(p == null){
            System.out.println("Executor haven't producer");
            return RC.CODE_INVALID_ARGUMENT;
        }
        producer = p;
        return RC.CODE_SUCCESS;
    }

    private byte[] DoubleToByte(double d){
        byte[] ans = new byte[Double.SIZE / Byte.SIZE];
        long num = Double.doubleToLongBits(d);
        for(int i = 0; i < ans.length; i++){
            ans[i] = (byte)(num & 0xFF);
            num >>>= Byte.SIZE;
        }
        return ans;
    }

    private double ByteToDouble(byte[] data){
        long ans = 0;
        for(int i = 0; i < Double.SIZE / Byte.SIZE; i++){
            if((data[i] >= 0)){
                ans += (((long)data[i]) << (Byte.SIZE * (i)));
            }
            else{
                ans += (((long)(256 + data[i])) << (Byte.SIZE * (i)));
            }
        }
        double d = Double.longBitsToDouble(ans);
        return d;
    }

    public RC execute(byte [] data){
        try {
            if (mode == CodingGrammar.MODE.ENCODING) {
                byte[] answer;
                int Size_Elem = data.length;
                symb = new Symbols(data);
                Init(symb, data, Size_Elem);
                answer = new byte[Double.SIZE / Byte.SIZE];
                encoded.write(((Integer) symb.length()).toString() + "\n");
                for (int i = 0; i < symb.length(); i++) {
                    encoded.write(symb.Key(i).toString() + " " + symb.Value(i).toString() + "\n");
                }
                encoded.write(((Integer) data.length).toString() + "\n");
                byte[] d = new byte[Double.SIZE / Byte.SIZE];
                d = DoubleToByte(code);
                for (int i = 0; i < Double.SIZE / Byte.SIZE; i++) {
                    answer[i] = d[i];
                }
                encoded.flush();
                return consumer.execute(answer);
            } else {
                symb = new Symbols();
                String str = decoded.readLine();
                int n = Integer.parseInt(str);
                for (int i = 0; i < n; i++) {
                    str = decoded.readLine();
                    double doub = Double.parseDouble(str.substring(str.indexOf(" ") + 1));
                    Byte b = Byte.parseByte(str.substring(0, str.indexOf(" ")));
                    symb.Add(b, doub);
                }
                str = decoded.readLine();
                num = Integer.parseInt(str);
                code = ByteToDouble(data);
                byte[] ans = Word(code, num);
                return consumer.execute(ans);
            }
        }
        catch(IOException e){
            logger.warning("Can't read line");
            return RC.CODE_INVALID_ARGUMENT;
        }
    }

    public RC setConfig(String configFileName){
        CodingGrammar grammar = new CodingGrammar();
        try{
            Syntaksis synt = new Syntaksis();
            RC rc = synt.Synt(new BufferedReader(new FileReader(configFileName)), logger, grammar.delimiter());
            if(rc != RC.CODE_SUCCESS){
                return rc;
            }
            Coding_Semantic seman = new Coding_Semantic();
            rc = seman.Semantic(synt.Result(), logger);
            if(rc != RC.CODE_SUCCESS){
                return rc;
            }
            mode = seman.Mode();
            if(mode == CodingGrammar.MODE.ENCODING){
                encoded = new BufferedWriter(new FileWriter(seman.FileName()));
            }
            if(mode == CodingGrammar.MODE.DECODING){
                decoded = new BufferedReader(new FileReader(seman.FileName()));
            }
        }
        catch(FileNotFoundException e){
            logger.warning("Can't make reader");
            return RC.CODE_INVALID_ARGUMENT;
        }
        catch(IOException e){
            logger.warning("Can't make writer");
            return RC.CODE_INVALID_ARGUMENT;
        }
        return RC.CODE_SUCCESS;
    }

    private byte[] Word(Double word, Integer len){
        byte[] str = new byte[len];
        byte ch;
        for(int i = 0; i < len; i++){
            ch = symb.Symbol(word);
            str[i] = ch;
            word = (word - symb.Prev_Val_Key(ch)) / (symb.Val_Key(ch) - symb.Prev_Val_Key(ch));
        }
        return str;
    }

    private void Init(Symbols elements, byte[] elem, int size){
        byte[] arr = new byte[size];
        for(int j = 0; j < size; j++){
            arr[j] = elem[j];
        }
        code = Code(elements, arr);
    }

    private double Code(Symbols elements, byte[] arr){
        double a = 0.;
        double b = 1.;
        for(int i = 0; i < arr.length; i++){
            double a1 = a + (b - a) * elements.Prev_Val_Key(arr[i]);
            b = a + (b - a) * elements.Val_Key(arr[i]);
            a = a1;
        }
        return (a + b) / 2;
    }
}