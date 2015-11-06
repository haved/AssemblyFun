package me.havard.assemblyfun;

/** An exception thrown when something went wrong in the Assembly parsing/running
 * Created by Havard on 06.11.2015.
 */
public class AssemblyException extends RuntimeException {
    private int mTextId;
    public AssemblyException(String reason) {
        super(reason);
    }
    public AssemblyException(String reason, int textId) {
        super(reason);
        mTextId = textId;
    }

    public int getTextId(){
        return mTextId;
    }
}
