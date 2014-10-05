
import com.wiseaux.setCreator.SetCreatorEnhanced;



/**
 * This is where the SelfOrganizingMap is going to be run.
 * 
 * @author Daniel Swain
 */
public class TestSOM {
    
    public static void main(String[] args){
        SetCreatorEnhanced som = new SetCreatorEnhanced();
        som.run();
        som.train(10);
    }
}
