package Writer;

import ru.spbstu.pipeline.RC;

import java.util.TreeMap;

public class Writer_Semantic {
    private WriterGrammar.MODE mode;
    public RC Semantic(TreeMap<String, String> map){
        for (String str : map.keySet()) {
            if(str.equals(WriterGrammar.TokenWriter.MODE.toString())){
                if(map.get(str).equals(WriterGrammar.MODE.DECODING.toString())){
                    mode = WriterGrammar.MODE.DECODING;
                }
                if(map.get(str).equals(WriterGrammar.MODE.ENCODING.toString())){
                    mode = WriterGrammar.MODE.ENCODING;
                }
            }
        }
        return RC.CODE_SUCCESS;
    }



    public WriterGrammar.MODE Mode(){
        return mode;
    }
}
