import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a generic matrix.
 * @param <A> fist component of the matrix, it must be unique because is the index.
 * @param <B> second component of the matrix
 */
public class Matrix<A,B> {
    private Map<A,B> content;

    public Matrix() {
        this.content = new HashMap<>();
    }

    public void addRow(A a, B b){
        content.put(a, b);
    }
    public B getB(A a){
        return content.get(a);
    }
}
