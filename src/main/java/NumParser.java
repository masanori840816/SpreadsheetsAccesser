import com.sun.istack.internal.NotNull;

/**
 * Created by masanori on 2016/12/28.
 */
public class NumParser {
    public static float TryParseFloat(@NotNull String originalString){
        float result = -1;
        try{
            result = Float.parseFloat(originalString);
        }
        catch(NumberFormatException ex){
            result = -1;
        }
        finally {
            return result;
        }
    }
}
