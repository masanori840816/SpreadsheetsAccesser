import com.sun.istack.internal.NotNull;

/**
 * Created by masanori on 2016/12/28.
 * parse text to number.
 */
class NumParser {
    static float TryParseFloat(@NotNull String originalString){
        float result;
        try{
            result = Float.parseFloat(originalString);
        }
        catch(NumberFormatException ex){
            result = -1;
        }
        return result;
    }
}
